package jp.loioz.app.user.depositRecvList.service;

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
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.user.depositRecvList.dto.DepositRecvListDto;
import jp.loioz.app.user.depositRecvList.form.DepositRecvListSearchForm;
import jp.loioz.app.user.depositRecvList.form.DepositRecvListViewForm;
import jp.loioz.bean.AnkenTantoLawyerJimuBean;
import jp.loioz.bean.DepositRecvListBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TDepositRecvDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.MAccountEntity;

/**
 * 預り金管理一覧覧画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepositRecvListService extends DefaultService {

	/** アカウントマスタDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 預り金Daoクラス */
	@Autowired
	private TDepositRecvDao tDepositRecvDao;

	/** 案件担当Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 画面表示情報を作成する
	 * 
	 * @return
	 */
	public DepositRecvListViewForm createViewForm() {
		DepositRecvListViewForm viewForm = new DepositRecvListViewForm();
		return viewForm;
	}

	/**
	 * 検索条件に該当する情報を検索します。
	 * 
	 * @param viewForm
	 * @param searchForm 検索条件フォーム
	 */
	public DepositRecvListViewForm searchDepositRecvList(DepositRecvListViewForm viewForm, DepositRecvListSearchForm searchForm) {

		// 検索条件に該当する報酬一覧情報を取得
		viewForm = this.searchDepositRecvListOnePage(viewForm, searchForm);

		// 一覧表示のソートキーとソート順をviewFormにセット
		viewForm.setDepositRecvListSortItem(searchForm.getDepositRecvListSortItem());
		viewForm.setDepositRecvListSortOrder(searchForm.getDepositRecvListSortOrder());

		return viewForm;
	}

	/**
	 * 預り金管理検索条件フラグメント表示用項目を設定する
	 * 
	 * @param searchForm
	 */
	public void setDispProperties(DepositRecvListSearchForm searchForm) {

		// 分野のプルダウン情報（利用停止を含む）を検索条件Formにセット
		List<BunyaDto> bunyaList = commonBunyaService.getBunyaList();
		searchForm.setBunyaList(bunyaList);

		// 売上計上先、担当弁護士、担当事務のプルダウン情報を検索条件Formにセット
		List<MAccountEntity> accountList = mAccountDao.selectAll();
		searchForm.setSalesOwnerList(getLawyerDtoList(accountList));
		searchForm.setTantoLawyerList(getLawyerDtoList(accountList));
		searchForm.setTantoJimuList(getJimuDtoList(accountList));

		// 検索条件の分野がマスターテーブルに存在しない、もしくは削除済みの分野の場合、分野を初期化（null）にする
		searchForm.setBunyaId(commonBunyaService.replaceIfBunyaNotExist(searchForm.getBunyaId(), bunyaList));
	}


	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * アカウントEntityのリストから弁護士リストを取得します
	 * 
	 * @param accountEntityList
	 * @return
	 */
	private List<SelectOptionForm> getLawyerDtoList(List<MAccountEntity> accountEntityList) {
		List<SelectOptionForm> lawyerDtoList = new ArrayList<SelectOptionForm>();
		if (LoiozCollectionUtils.isEmpty(accountEntityList)) {
			return lawyerDtoList;
		}
		List<MAccountEntity> lawyerList = accountEntityList.stream()
				.filter(entity -> AccountType.LAWYER.equalsByCode(entity.getAccountType()))
				.collect(Collectors.toList());
		for (MAccountEntity entity : lawyerList) {
			SelectOptionForm dto = new SelectOptionForm(entity.getAccountSeq().toString(), PersonName.fromEntity(entity).getName());
			lawyerDtoList.add(dto);
		}
		return lawyerDtoList;
	}

	/**
	 * アカウントEntityのリストから事務員リストを取得します
	 * 
	 * @param accountEntityList
	 * @return
	 */
	private List<SelectOptionForm> getJimuDtoList(List<MAccountEntity> accountEntityList) {
		List<SelectOptionForm> jimuDtoList = new ArrayList<SelectOptionForm>();
		if (LoiozCollectionUtils.isEmpty(accountEntityList)) {
			return jimuDtoList;
		}
		List<MAccountEntity> jimuList = accountEntityList.stream()
				.filter(entity -> AccountType.JIMU.equalsByCode(entity.getAccountType())).collect(Collectors.toList());
		for (MAccountEntity entity : jimuList) {
			SelectOptionForm dto = new SelectOptionForm(entity.getAccountSeq().toString(), PersonName.fromEntity(entity).getName());
			jimuDtoList.add(dto);
		}
		return jimuDtoList;
	}

	/**
	 * 預り金情報を取得する
	 * 
	 * @param viewForm
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	private DepositRecvListViewForm searchDepositRecvListOnePage(DepositRecvListViewForm viewForm, DepositRecvListSearchForm searchForm) {
		// 預り金情報を取得
		Page<DepositRecvListDto> page = getOnePage(searchForm);
		// 1ページ分の案件情報をセット
		viewForm.setPage(page);
		// 案件情報をセット
		viewForm.setDepositRecvList(page.getContent());
		return viewForm;
	}

	/**
	 * 預り金一覧で表示する1ページ分を取得する<br>
	 * 
	 * @param searchForm
	 * @return
	 */
	private Page<DepositRecvListDto> getOnePage(DepositRecvListSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索処理
		List<DepositRecvListBean> depositRecvListBeanList = tDepositRecvDao.selectByListSearchConditions(
				searchForm.toDepositRecvListSearchCondition(), searchForm.toDepositRecvListSortCondition(), options);

		// 預り金情報に担当弁護士、担当事務をセット
		this.setDepositRecvListTantoLaywerJimu(depositRecvListBeanList);

		// Dto変換
		List<DepositRecvListDto> depositRecvList = this.convertBean2Dto(depositRecvListBeanList);

		Page<DepositRecvListDto> page = new PageImpl<>(depositRecvList, pageable, options.getCount());
		return page;
	}

	/**
	 * 預り金情報に担当弁護士、担当事務をセットします。<br>
	 * 
	 * @param depositRecvList
	 */
	private void setDepositRecvListTantoLaywerJimu(List<DepositRecvListBean> depositRecvListBean) {

		if (LoiozCollectionUtils.isEmpty(depositRecvListBean)) {
			return;
		}

		List<Long> ankenIdList = depositRecvListBean.stream().map(bean -> bean.getAnkenId()).collect(Collectors.toList());
		List<AnkenTantoLawyerJimuBean> tantoLaywerJimuBeanList = tAnkenTantoDao.selectAnkenTantoLaywerJimuById(ankenIdList);
		for (DepositRecvListBean bean : depositRecvListBean) {
			// 担当弁護士の担当者名のセット
			Optional<AnkenTantoLawyerJimuBean> lawyerOptBean = tantoLaywerJimuBeanList.stream()
					.filter(tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(bean.getAnkenId())
							&& TantoType.LAWYER.equalsByCode(tantoLaywerJimuBean.getTantoType()))
					.findFirst();
			if (lawyerOptBean.isPresent()) {
				bean.setTantoLaywerName(lawyerOptBean.get().getTantoName());
			}
			// 担当事務の担当者名のセット
			Optional<AnkenTantoLawyerJimuBean> jimuOptBean = tantoLaywerJimuBeanList.stream()
					.filter(tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(bean.getAnkenId())
							&& TantoType.JIMU.equalsByCode(tantoLaywerJimuBean.getTantoType()))
					.findFirst();
			if (jimuOptBean.isPresent()) {
				bean.setTantoJimuName(jimuOptBean.get().getTantoName());
			}
		}
	}

	/**
	 * DepositRecvListBean<Bean>型からDepositRecvListDto<Dto>型に変換します
	 * 
	 * @param depositRecvListBean
	 * @return
	 */
	private List<DepositRecvListDto> convertBean2Dto(List<DepositRecvListBean> depositRecvListBean) {

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// Bean型をDto型に変換する
		List<DepositRecvListDto> dtoList = new ArrayList<DepositRecvListDto>();
		depositRecvListBean.forEach(bean -> {
			dtoList.add(convertDepositRecvBean2Dto(bean, bunyaMap));
		});
		return dtoList;
	}

	/**
	 * DepositRecvListBean型からDepositRecvListDto型に変換します<br>
	 * 
	 * @param depositRecvListBean
	 * @param bunyaMap
	 * @return
	 */
	private DepositRecvListDto convertDepositRecvBean2Dto(DepositRecvListBean depositRecvListBean, Map<Long, BunyaDto> bunyaMap) {

		DepositRecvListDto depositRecvListDto = new DepositRecvListDto();

		depositRecvListDto.setPersonId(depositRecvListBean.getPersonId());
		depositRecvListDto.setName(depositRecvListBean.getPersonNameSei() + CommonConstant.SPACE + depositRecvListBean.getPersonNameMei());
		depositRecvListDto.setAnkenId(depositRecvListBean.getAnkenId());
		depositRecvListDto.setAnkenName(depositRecvListBean.getAnkenName());
		depositRecvListDto.setBunyaName(bunyaMap.get(depositRecvListBean.getBunyaId()).getBunyaName());
		depositRecvListDto.setAnkenStatus(depositRecvListBean.getAnkenStatus());
		depositRecvListDto.setAnkenStatusName(CommonConstant.AnkenStatus.of(depositRecvListBean.getAnkenStatus()).getVal());
		depositRecvListDto.setTantoLaywerName(depositRecvListBean.getTantoLaywerName());
		depositRecvListDto.setTantoJimuName(depositRecvListBean.getTantoJimuName());
		depositRecvListDto.setNyukinTotal(depositRecvListBean.getNyukinTotal());
		depositRecvListDto.setShukkinTotal(depositRecvListBean.getShukkinTotal());
		depositRecvListDto.setZandakaTotal(depositRecvListBean.getZandakaTotal());
		depositRecvListDto.setLastEditAt(depositRecvListBean.getLastEditAt());

		return depositRecvListDto;
	}

}