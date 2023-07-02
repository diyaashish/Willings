package jp.loioz.app.user.ankenList.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.seasar.doma.boot.Pageables;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.user.ankenList.form.AnkenListSearchForm;
import jp.loioz.app.user.ankenList.form.AnkenListViewForm;
import jp.loioz.bean.AnkenAitegataCntBean;
import jp.loioz.bean.AnkenListBean;
import jp.loioz.bean.AnkenTantoFlgBean;
import jp.loioz.bean.AnkenTantoLawyerJimuBean;
import jp.loioz.bean.SaibanAitegataBean;
import jp.loioz.bean.SaibanListBean;
import jp.loioz.bean.SaibanSeqCustomerBean;
import jp.loioz.bean.SaibanTantoFlgBean;
import jp.loioz.bean.SaibanTantoLawyerJimuBean;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.AnkenListMenu;
import jp.loioz.common.constant.CommonConstant.BunyaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.AnkenListDao;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TSaibanTantoDao;
import jp.loioz.dto.AnkenListDto;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.SaibanListDto;
import jp.loioz.entity.MAccountEntity;

/**
 * 案件一覧画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AnkenListService extends DefaultService {

	/** アカウントマスタDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 案件一覧共通サービスクラス */
	@Autowired
	private AnkenListCommonService ankenListCommonService;

	/** 案件一覧Daoクラス */
	@Autowired
	private AnkenListDao ankenListDao;

	/** 案件担当Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** 裁判担当Daoクラス */
	@Autowired
	private TSaibanTantoDao tSaibanTantoDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 案件一覧用viewFormを作成します。
	 *
	 * @return
	 */
	public AnkenListViewForm.AnkenKeiListViewForm createAnkenListViewForm() {
		AnkenListViewForm.AnkenKeiListViewForm viewForm = new AnkenListViewForm.AnkenKeiListViewForm();
		return viewForm;
	}

	/**
	 * 裁判一覧用viewFormを作成します。
	 *
	 * @return
	 */
	public AnkenListViewForm.SaibanKeiListViewForm createSaibanListViewForm() {
		AnkenListViewForm.SaibanKeiListViewForm viewForm = new AnkenListViewForm.SaibanKeiListViewForm();
		return viewForm;
	}

	/**
	 * 検索条件に該当する案件一覧情報を検索します。
	 * 
	 * @param viewForm
	 * @param searchForm 検索条件フォーム
	 */
	public AnkenListViewForm.AnkenKeiListViewForm searchAnken(AnkenListViewForm.AnkenKeiListViewForm viewForm, AnkenListSearchForm searchForm) {

		// 分野のプルダウン情報（利用停止を含む）を検索条件Formにセット
		List<BunyaDto> bunyaList = commonBunyaService.getBunyaList();
		searchForm.getAllAnkenListSearchForm().setBunyaList(bunyaList);
		searchForm.getMyAnkenListSearchForm().setBunyaList(bunyaList);
		searchForm.getAdvisorAnkenListSearchForm().setBunyaList(bunyaList);

		// 担当弁護士、担当事務のプルダウン情報を検索条件Formにセット
		List<MAccountEntity> accountList = mAccountDao.selectAll();
		searchForm.setTantoLawyerList(accountList.stream().filter(entity -> AccountType.LAWYER.equalsByCode(entity.getAccountType())).collect(Collectors.toList()));
		searchForm.setTantoJimuList(accountList.stream().filter(entity -> AccountType.JIMU.equalsByCode(entity.getAccountType())).collect(Collectors.toList()));

		// 検索条件の分野がマスターテーブルに存在しない、もしくは削除済みの分野の場合、分野を初期化（null）にする
		replaceIfBunyaNotExist(searchForm, bunyaList);

		// 検索条件に該当する案件一覧情報を取得
		viewForm = searchAnkenList(viewForm, searchForm);

		// 一覧表示のソートキーとソート順をviewFormにセット
		if (AnkenListMenu.All_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			viewForm.setAnkenListSortKey(searchForm.getAllAnkenListSearchForm().getAllAnkenListSortKey());
			viewForm.setAnkenListSortOrder(searchForm.getAllAnkenListSearchForm().getAllAnkenListSortOrder());
		} else if (AnkenListMenu.MY_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			viewForm.setAnkenListSortKey(searchForm.getMyAnkenListSearchForm().getMyAnkenListSortKey());
			viewForm.setAnkenListSortOrder(searchForm.getMyAnkenListSearchForm().getMyAnkenListSortOrder());
		} else if (AnkenListMenu.ADVISOR_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			viewForm.setAnkenListSortKey(searchForm.getAdvisorAnkenListSearchForm().getAdvisorAnkenListSortKey());
			viewForm.setAnkenListSortOrder(searchForm.getAdvisorAnkenListSearchForm().getAdvisorAnkenListSortOrder());
		} else {
			// 対象の案件なし
		}

		return viewForm;
	}

	/**
	 * 検索条件に該当する裁判一覧情報を検索します。
	 * 
	 * @param viewForm
	 * @param searchForm 検索条件フォーム
	 */
	public AnkenListViewForm.SaibanKeiListViewForm searchSaiban(AnkenListViewForm.SaibanKeiListViewForm viewForm, AnkenListSearchForm searchForm) {

		// 分野のプルダウン情報（利用停止を含む）を検索条件Formにセット
		List<BunyaDto> bunyaList = commonBunyaService.getBunyaList();
		searchForm.getMinjiSaibanListSearchForm().setBunyaList(bunyaList.stream().filter(dto -> BunyaType.MINJI.equalsByCode(dto.getBunyaType())).collect(Collectors.toList()));
		searchForm.getKeijiSaibanListSearchForm().setBunyaList(bunyaList.stream().filter(dto -> BunyaType.KEIJI.equalsByCode(dto.getBunyaType())).collect(Collectors.toList()));

		// 担当弁護士、担当事務のプルダウン情報を検索条件Formにセット
		List<MAccountEntity> accountList = mAccountDao.selectAll();
		searchForm.setTantoLawyerList(accountList.stream().filter(entity -> AccountType.LAWYER.equalsByCode(entity.getAccountType())).collect(Collectors.toList()));
		searchForm.setTantoJimuList(accountList.stream().filter(entity -> AccountType.JIMU.equalsByCode(entity.getAccountType())).collect(Collectors.toList()));

		// 検索条件の分野がマスターテーブルに存在しない、もしくは削除済みの分野の場合、分野を初期化（null）にする
		replaceIfBunyaNotExist(searchForm, bunyaList);

		// 検索条件に該当する案件一覧情報を取得
		viewForm = searchSaibanList(viewForm, searchForm);

		// 一覧表示のソートキーとソート順をviewFormにセット
		if (AnkenListMenu.MINJI_SAIBAN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 民事裁判
			viewForm.setSaibanListSortKey(searchForm.getMinjiSaibanListSearchForm().getMinjiSaibanListSortKey());
			viewForm.setSaibanListSortOrder(searchForm.getMinjiSaibanListSearchForm().getMinjiSaibanListSortOrder());
		} else if (AnkenListMenu.KEIJI_SAIBAN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 刑事裁判
			viewForm.setSaibanListSortKey(searchForm.getKeijiSaibanListSearchForm().getKeijiSaibanListSortKey());
			viewForm.setSaibanListSortOrder(searchForm.getKeijiSaibanListSearchForm().getKeijiSaibanListSortOrder());
		} else {
			// それ以外
		}

		return viewForm;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 案件情報を取得する
	 * 
	 * @param viewForm
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	private AnkenListViewForm.AnkenKeiListViewForm searchAnkenList(AnkenListViewForm.AnkenKeiListViewForm viewForm, AnkenListSearchForm searchForm) {
		// 案件情報を取得
		Page<AnkenListDto> page = getOnePageOfAnkenList(searchForm);
		// 1ページ分の案件情報をセット
		viewForm.setPage(page);
		// 案件情報をセット
		viewForm.setAnkenList(page.getContent());
		return viewForm;
	}

	/**
	 * 裁判情報を取得する
	 * 
	 * @param viewForm
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	private AnkenListViewForm.SaibanKeiListViewForm searchSaibanList(AnkenListViewForm.SaibanKeiListViewForm viewForm, AnkenListSearchForm searchForm) {
		// 裁判情報を取得
		Page<SaibanListDto> page = getOnePageOfSaibanList(searchForm);
		// 1ページ分の案件情報をセット
		viewForm.setPage(page);
		// 案件情報をセット
		viewForm.setSaibanList(page.getContent());
		return viewForm;
	}

	/**
	 * 案件一覧で表示する1ページ分の案件情報を取得する<br>
	 * 
	 * @param searchForm
	 * @return
	 */
	private Page<AnkenListDto> getOnePageOfAnkenList(AnkenListSearchForm searchForm) {

		// ページャー
		Pageable pageable = null;
		if (AnkenListMenu.All_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// すべての案件
			pageable = searchForm.getAllAnkenListSearchForm().toPageable();
		} else if (AnkenListMenu.MY_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 自分の担当案件
			pageable = searchForm.getMyAnkenListSearchForm().toPageable();
		} else if (AnkenListMenu.ADVISOR_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 顧問取引先の案件
			pageable = searchForm.getAdvisorAnkenListSearchForm().toPageable();
		} else {
			// 対象のメニューなし
		}
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		List<AnkenListBean> ankenListBeanList = null;
		if (!AnkenListMenu.ADVISOR_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 顧問取引先の案件以外

			// 1ページ分の案件情報を取得
			if (StringUtils.isEmpty(searchForm.getQuickSearch())) {
				// 通常の検索の場合
				ankenListBeanList = ankenListDao.selectAnkenListBySearchConditions(
						ankenListCommonService.createAnkenSearchCondition(searchForm),
						ankenListCommonService.createAnkenSortCondition(searchForm),
						options);
			} else {
				// クイック検索の場合（「すべて」メニューの場合のみこのケースがあり得る）
				ankenListBeanList = ankenListDao.selectAnkenListByQuickSearch(
						StringUtils.removeSpaceCharacter(searchForm.getQuickSearch()),
						ankenListCommonService.createAnkenSearchCondition(searchForm).getAnkenStatusList(),
						ankenListCommonService.createAnkenSortCondition(searchForm),
						options);
			}
		} else {
			// 顧問取引先の案件

			// 1ページ分の案件情報を取得
			ankenListBeanList = ankenListDao.selectAdvisorAnkenListBySearchConditions(
					ankenListCommonService.createAnkenSearchCondition(searchForm),
					ankenListCommonService.createAnkenSortCondition(searchForm),
					options);
		}

		// 案件IDに関する担当弁護士、担当事務を取得
		this.setAnkenListTantoLaywerJimu(ankenListBeanList);

		// 案件IDに関する相手方を取得
		this.setAnkenListAitegata(ankenListBeanList);

		// 案件IDに関する担当フラグを取得（ログインユーザが担当する案件かどうか）
		this.setAnkenListTantoAnkenFlg(ankenListBeanList);

		List<AnkenListDto> ankenList = this.convertAnkenBeanListForDtoList(ankenListBeanList);

		Page<AnkenListDto> page = new PageImpl<>(ankenList, pageable, options.getCount());
		return page;
	}

	/**
	 * 1ページ分の裁判情報を取得する<br>
	 * 
	 * @param searchForm
	 * @return
	 */
	private Page<SaibanListDto> getOnePageOfSaibanList(AnkenListSearchForm searchForm) {

		// ページャー、検索オプション
		Pageable pageable;
		SelectOptions options;
		// 1ページ分の裁判情報を取得
		List<SaibanListBean> saibanListBeanList;
		if (AnkenListMenu.MINJI_SAIBAN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 民事裁判
			pageable = searchForm.getMinjiSaibanListSearchForm().toPageable();
			options = Pageables.toSelectOptions(pageable).count();
			saibanListBeanList = ankenListDao.selectMinjiSaibanListBySearchConditions(
					ankenListCommonService.createSaibanSearchCondition(searchForm),
					ankenListCommonService.createSaibanSortCondition(searchForm),
					options);
		} else {
			// 刑事裁判
			pageable = searchForm.getKeijiSaibanListSearchForm().toPageable();
			options = Pageables.toSelectOptions(pageable).count();
			saibanListBeanList = ankenListDao.selectKeijiSaibanListBySearchConditions(
					ankenListCommonService.createSaibanSearchCondition(searchForm),
					ankenListCommonService.createSaibanSortCondition(searchForm),
					options);
		}

		// 裁判に関する担当弁護士、担当事務を取得
		this.setSaibanListTantoLaywerJimu(saibanListBeanList);

		// 裁判に関する相手方を取得（民事裁判の場合）
		if (AnkenListMenu.MINJI_SAIBAN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			this.setSaibanListAitegata(saibanListBeanList);
		}

		// 裁判に関する担当フラグを取得（ログインユーザが担当する案件かどうか）
		this.setSaibanListTantoSaibanFlg(saibanListBeanList);

		// 裁判に関する顧客情報を取得
		this.setSaibanListCustomer(saibanListBeanList);

		List<SaibanListDto> saibanList = this.convertSaibanBeanListForDtoList(saibanListBeanList);

		Page<SaibanListDto> page = new PageImpl<>(saibanList, pageable, options.getCount());
		return page;
	}

	/**
	 * 検索条件の分野IDがマスターテーブルに存在するかチェックし存在しない、もしくは削除済みの場合は分野をnullにする
	 * 
	 * @param searchForm
	 * @param masterBunyaList
	 * @return
	 */
	private AnkenListSearchForm replaceIfBunyaNotExist(AnkenListSearchForm searchForm, List<BunyaDto> masterBunyaList) {
		String bunyaId = null;
		if (AnkenListMenu.All_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// すべての案件
			bunyaId = searchForm.getAllAnkenListSearchForm().getBunyaId();
		} else if (AnkenListMenu.MY_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 自分の担当案件
			bunyaId = searchForm.getMyAnkenListSearchForm().getBunyaId();
		} else if (AnkenListMenu.ADVISOR_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 顧問取引先の案件
			bunyaId = searchForm.getAdvisorAnkenListSearchForm().getBunyaId();
		} else {
			// 対象の案件なし
		}

		// 分野マスターテーブルに存在しない、もしくは削除済みの場合は検索条件の分野をnullにする
		if (!isExistBunya(bunyaId, masterBunyaList)) {
			if (AnkenListMenu.All_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
				// すべての案件
				searchForm.getAllAnkenListSearchForm().setBunyaId(null);
			} else if (AnkenListMenu.MY_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
				// 自分の担当案件
				searchForm.getMyAnkenListSearchForm().setBunyaId(null);
			} else if (AnkenListMenu.ADVISOR_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
				// 顧問取引先の案件
				searchForm.getAdvisorAnkenListSearchForm().setBunyaId(null);
			} else {
				// 対象の案件なし
			}
		}
		return searchForm;
	}

	/**
	 * 検索条件で設定した分野がマスターテーブルに存在しないか、もしくは削除済みかチェックする
	 *
	 * @param strBunyaId
	 * @param masterBunyaList
	 * @return
	 */
	private boolean isExistBunya(String strBunyaId, List<BunyaDto> masterBunyaList) {

		if (StringUtils.isEmpty(strBunyaId)) {
			return false;
		}

		Long bunyaId = Long.valueOf(strBunyaId);
		for (BunyaDto dto : masterBunyaList) {
			if (dto.getBunyaId().equals(bunyaId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * AnkenList<Bean>型からAnkenList<Dto>型に変換します
	 * 
	 * @param AnkenListBeanList
	 * @return
	 */
	private List<AnkenListDto> convertAnkenBeanListForDtoList(List<AnkenListBean> AnkenListBeanList) {

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// Bean型をDto型に変換する
		List<AnkenListDto> ankenListDtoList = new ArrayList<AnkenListDto>();
		AnkenListBeanList.forEach(bean -> {
			ankenListDtoList.add(convertAnkenBeanForDto(bean, bunyaMap));
		});
		return ankenListDtoList;
	}

	/**
	 * AnkenListBean型からAnkenListDto型に変換します<br>
	 * 
	 * @param ankenListBean
	 * @return
	 */
	private AnkenListDto convertAnkenBeanForDto(AnkenListBean ankenListBean, Map<Long, BunyaDto> bunyaMap) {

		AnkenListDto ankenListDto = new AnkenListDto();

		ankenListDto.setAitegataName(ankenListBean.getAitegataName());
		ankenListDto.setAnkenCreatedDate(ankenListBean.getAnkenCreatedDate());
		ankenListDto.setAnkenId(ankenListBean.getAnkenId());
		ankenListDto.setAnkenName(ankenListBean.getAnkenName());
		ankenListDto.setAnkenStatus(ankenListBean.getAnkenStatus());
		ankenListDto.setBunyaId(ankenListBean.getBunyaId());
		ankenListDto.setBunya(bunyaMap.get(ankenListBean.getBunyaId()));
		ankenListDto.setCustomerCreatedDate(ankenListBean.getCustomerCreatedDate());
		ankenListDto.setPersonId(ankenListBean.getPersonId());
		ankenListDto.setCustomerId(ankenListBean.getCustomerId());
		ankenListDto.setCustomerNameMei(ankenListBean.getCustomerNameMei());
		ankenListDto.setCustomerNameMeiKana(ankenListBean.getCustomerNameMeiKana());
		ankenListDto.setCustomerNameSei(ankenListBean.getCustomerNameSei());
		ankenListDto.setCustomerNameSeiKana(ankenListBean.getCustomerNameSeiKana());
		ankenListDto.setJuninDate(ankenListBean.getJuninDate());
		ankenListDto.setNumberOfAitegata(ankenListBean.getNumberOfAitegata());
		ankenListDto.setTantoAnkenFlg(ankenListBean.getTantoAnkenFlg());
		ankenListDto.setTantoLaywerName(ankenListBean.getTantoLaywerName());
		ankenListDto.setTantoJimuName(ankenListBean.getTantoJimuName());

		return ankenListDto;
	}

	/**
	 * SaibanList<Bean>型からSaibanList<Dto>型に変換します
	 * 
	 * @param AnkenListBeanList
	 * @return
	 */
	private List<SaibanListDto> convertSaibanBeanListForDtoList(List<SaibanListBean> SaibanListBeanList) {

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// Bean型をDto型に変換する
		List<SaibanListDto> saibanListDtoList = new ArrayList<SaibanListDto>();
		SaibanListBeanList.forEach(bean -> {
			saibanListDtoList.add(convertSaibanBeanForDto(bean, bunyaMap));
		});
		return saibanListDtoList;
	}

	/**
	 * SaibanListBean型からSaibanListDto型に変換します<br>
	 * 
	 * @param ankenListBean
	 * @return
	 */
	private SaibanListDto convertSaibanBeanForDto(SaibanListBean saibanListBean, Map<Long, BunyaDto> bunyaMap) {

		SaibanListDto saibanListDto = new SaibanListDto();

		saibanListDto.setSaibanSeq(saibanListBean.getSaibanSeq());
		saibanListDto.setAnkenId(saibanListBean.getAnkenId());
		saibanListDto.setAnkenName(saibanListBean.getAnkenName());
		saibanListDto.setBunyaId(saibanListBean.getBunyaId());
		saibanListDto.setBunya(bunyaMap.get(saibanListBean.getBunyaId()));
		saibanListDto.setSaibanshoNameMei(saibanListBean.getSaibanshoNameMei());
		saibanListDto.setJikenSeq(saibanListBean.getJikenSeq());
		saibanListDto.setJikenGengo(saibanListBean.getJikenGengo());
		saibanListDto.setJikenYear(saibanListBean.getJikenYear());
		saibanListDto.setJikenMark(saibanListBean.getJikenMark());
		saibanListDto.setJikenNo(saibanListBean.getJikenNo());
		saibanListDto.setJikenName(saibanListBean.getJikenName());
		saibanListDto.setPersonId(saibanListBean.getPersonId());
		saibanListDto.setCustomerId(saibanListBean.getCustomerId());
		saibanListDto.setCustomerNameSei(saibanListBean.getCustomerNameSei());
		saibanListDto.setCustomerNameSeiKana(saibanListBean.getCustomerNameSeiKana());
		saibanListDto.setCustomerNameMei(saibanListBean.getCustomerNameMei());
		saibanListDto.setCustomerNameMeiKana(saibanListBean.getCustomerNameMeiKana());
		saibanListDto.setNumberOfCustomer(saibanListBean.getNumberOfCustomer());
		saibanListDto.setAitegataName(saibanListBean.getAitegataName());
		saibanListDto.setNumberOfAitegata(saibanListBean.getNumberOfAitegata());
		saibanListDto.setSaibanStatus(saibanListBean.getSaibanStatus());
		saibanListDto.setKensatsuchoNameMei(saibanListBean.getKensatsuchoNameMei());
		saibanListDto.setTantoLaywerName(saibanListBean.getTantoLaywerName());
		saibanListDto.setTantoJimuName(saibanListBean.getTantoJimuName());
		saibanListDto.setSaibanStartDate(saibanListBean.getSaibanStartDate());
		saibanListDto.setSaibanEndDate(saibanListBean.getSaibanEndDate());
		saibanListDto.setTantoAnkenFlg(saibanListBean.getTantoSaibanFlg());

		return saibanListDto;
	}

	/**
	 * 案件に関する担当弁護士、担当事務をセットします。
	 * 
	 * @param ankenList
	 */
	private void setAnkenListTantoLaywerJimu(List<AnkenListBean> ankenList) {
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			return;
		}

		List<Long> ankenIdList = ankenList.stream().map(bean -> bean.getAnkenId().asLong()).collect(Collectors.toList());
		List<AnkenTantoLawyerJimuBean> tantoLaywerJimuBeanList = tAnkenTantoDao.selectAnkenTantoLaywerJimuById(ankenIdList);
		for (AnkenListBean ankenListBean : ankenList) {
			// 担当弁護士の担当者名
			Optional<AnkenTantoLawyerJimuBean> lawyerOptBean = tantoLaywerJimuBeanList.stream().filter(tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(ankenListBean.getAnkenId().asLong()) && TantoType.LAWYER.equalsByCode(tantoLaywerJimuBean.getTantoType())).findFirst();
			if (lawyerOptBean.isPresent()) {
				ankenListBean.setTantoLaywerName(lawyerOptBean.get().getTantoName());
			}
			// 担当事務の担当者名
			Optional<AnkenTantoLawyerJimuBean> jimuOptBean = tantoLaywerJimuBeanList.stream().filter(tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(ankenListBean.getAnkenId().asLong()) && TantoType.JIMU.equalsByCode(tantoLaywerJimuBean.getTantoType())).findFirst();
			if (jimuOptBean.isPresent()) {
				ankenListBean.setTantoJimuName(jimuOptBean.get().getTantoName());
			}
		}
	}

	/**
	 * 案件に関する相手方情報をセットします。
	 * 
	 * @param ankenList
	 */
	private void setAnkenListAitegata(List<AnkenListBean> ankenList) {
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			return;
		}

		List<Long> ankenIdList = ankenList.stream().map(bean -> bean.getAnkenId().asLong()).collect(Collectors.toList());
		List<AnkenAitegataCntBean> aitegataBeanList = ankenListDao.selectAnkenAitegataByAnkenId(ankenIdList);
		for (AnkenListBean ankenListBean : ankenList) {
			Optional<AnkenAitegataCntBean> optBean = aitegataBeanList.stream().filter(aitegataBean -> aitegataBean.getAnkenId().asLong().equals(ankenListBean.getAnkenId().asLong())).findFirst();
			if (optBean.isPresent()) {
				ankenListBean.setAitegataName(optBean.get().getAitegataName());
				ankenListBean.setNumberOfAitegata(optBean.get().getNumberOfAitegata());
			}
		}
	}

	/**
	 * ログインユーザが案件の担当かフラグをセットします。
	 * 
	 * @param ankenList
	 */
	private void setAnkenListTantoAnkenFlg(List<AnkenListBean> ankenList) {
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			return;
		}

		List<Long> ankenIdList = ankenList.stream().map(bean -> bean.getAnkenId().asLong()).collect(Collectors.toList());
		List<AnkenTantoFlgBean> ankenTantoFlgBeanList = ankenListDao.selectTantoAnkenFlgById(SessionUtils.getLoginAccountSeq(), ankenIdList);
		for (AnkenListBean ankenListBean : ankenList) {
			Optional<AnkenTantoFlgBean> optBean = ankenTantoFlgBeanList.stream().filter(ankenTantoFlgBean -> ankenTantoFlgBean.getAnkenId().asLong().equals(ankenListBean.getAnkenId().asLong())).findFirst();
			if (optBean.isPresent()) {
				ankenListBean.setTantoAnkenFlg(optBean.get().getTantoAnkenFlg());
			}
		}
	}

	/**
	 * 裁判に関する担当弁護士、担当事務をセットします。
	 * 
	 * @param ankenList
	 */
	private void setSaibanListTantoLaywerJimu(List<SaibanListBean> saibanList) {
		if (LoiozCollectionUtils.isEmpty(saibanList)) {
			return;
		}

		List<Long> saibanSeqList = saibanList.stream().map(bean -> bean.getSaibanSeq()).collect(Collectors.toList());
		List<SaibanTantoLawyerJimuBean> tantoLawyerJimuBeanList = tSaibanTantoDao.selectSaibanTantoLaywerJimuBySeq(saibanSeqList);
		for (SaibanListBean saibanListBean : saibanList) {
			Optional<SaibanTantoLawyerJimuBean> lawyerOptBean = tantoLawyerJimuBeanList.stream().filter(tantoLawyerJimuBean -> tantoLawyerJimuBean.getSaibanSeq().equals(saibanListBean.getSaibanSeq()) && TantoType.LAWYER.equalsByCode(tantoLawyerJimuBean.getTantoType())).findFirst();
			if (lawyerOptBean.isPresent()) {
				saibanListBean.setTantoLaywerName(lawyerOptBean.get().getTantoName());
			}
			Optional<SaibanTantoLawyerJimuBean> jimuOptBean = tantoLawyerJimuBeanList.stream().filter(tantoLawyerJimuBean -> tantoLawyerJimuBean.getSaibanSeq().equals(saibanListBean.getSaibanSeq()) && TantoType.JIMU.equalsByCode(tantoLawyerJimuBean.getTantoType())).findFirst();
			if (jimuOptBean.isPresent()) {
				saibanListBean.setTantoJimuName(jimuOptBean.get().getTantoName());
			}
		}
	}

	/**
	 * 裁判に関する相手方情報をセットします。
	 * 
	 * @param ankenList
	 */
	private void setSaibanListAitegata(List<SaibanListBean> saibanList) {
		if (LoiozCollectionUtils.isEmpty(saibanList)) {
			return;
		}

		List<Long> saibanSeqList = saibanList.stream().map(bean -> bean.getSaibanSeq()).collect(Collectors.toList());
		List<SaibanAitegataBean> aitegataBeanList = ankenListDao.selectSaibanAitegataBySaibanSeq(saibanSeqList);
		for (SaibanListBean saibanListBean : saibanList) {
			Optional<SaibanAitegataBean> optBean = aitegataBeanList.stream().filter(aitegataBean -> aitegataBean.getSaibanSeq().equals(saibanListBean.getSaibanSeq())).findFirst();
			if (optBean.isPresent()) {
				saibanListBean.setAitegataName(optBean.get().getAitegataName());
				saibanListBean.setNumberOfAitegata(optBean.get().getNumberOfAitegata());
			}
		}
	}

	/**
	 * ログインユーザが裁判の担当かフラグをセットします。
	 * 
	 * @param ankenList
	 */
	private void setSaibanListTantoSaibanFlg(List<SaibanListBean> saibanList) {
		if (LoiozCollectionUtils.isEmpty(saibanList)) {
			return;
		}

		List<Long> saibanSeqList = saibanList.stream().map(bean -> bean.getSaibanSeq()).collect(Collectors.toList());
		List<SaibanTantoFlgBean> saibanTantoFlgBeanList = ankenListDao.selectTantoSaibanFlgBySeq(SessionUtils.getLoginAccountSeq(), saibanSeqList);
		for (SaibanListBean saibanListBean : saibanList) {
			Optional<SaibanTantoFlgBean> optBean = saibanTantoFlgBeanList.stream().filter(ankenTantoFlgBean -> ankenTantoFlgBean.getSaibanSeq().equals(saibanListBean.getSaibanSeq())).findFirst();
			if (optBean.isPresent()) {
				saibanListBean.setTantoSaibanFlg(optBean.get().getTantoSaibanFlg());
			}
		}
	}

	/**
	 * 裁判に関する顧客情報(1名分）と紐づけられている顧客数をセットします。<br>
	 * 顧客の中で筆頭が設定してあれば筆頭の顧客情報をセットします。<br>
	 * 筆頭が設定していなければ最初に登録された顧客情報をセットします。
	 * 
	 * @param ankenList
	 */
	private void setSaibanListCustomer(List<SaibanListBean> saibanList) {
		if (LoiozCollectionUtils.isEmpty(saibanList)) {
			return;
		}

		List<Long> saibanSeqList = saibanList.stream().map(bean -> bean.getSaibanSeq()).collect(Collectors.toList());
		List<SaibanSeqCustomerBean> customerBeanList = ankenListDao.selectSaibanCustomerBySeq(saibanSeqList);
		for (SaibanListBean saibanListBean : saibanList) {
			Optional<SaibanSeqCustomerBean> optBean = customerBeanList.stream().filter(
					aitegataBean -> aitegataBean.getSaibanSeq().equals(saibanListBean.getSaibanSeq())).findFirst();
			if (optBean.isPresent()) {
				saibanListBean.setPersonId(optBean.get().getPersonId());
				saibanListBean.setCustomerId(optBean.get().getCustomerId());
				saibanListBean.setCustomerNameMei(optBean.get().getCustomerNameMei());
				saibanListBean.setCustomerNameMeiKana(optBean.get().getCustomerNameMeiKana());
				saibanListBean.setCustomerNameSei(optBean.get().getCustomerNameSei());
				saibanListBean.setCustomerNameSeiKana(optBean.get().getCustomerNameSeiKana());
				saibanListBean.setNumberOfCustomer(optBean.get().getNumberOfCustomer());
			}
		}
	}
}