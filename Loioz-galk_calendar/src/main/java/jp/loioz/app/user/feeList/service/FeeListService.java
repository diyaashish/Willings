package jp.loioz.app.user.feeList.service;

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
import jp.loioz.app.user.feeList.dto.FeeListDto;
import jp.loioz.app.user.feeList.form.FeeListSearchForm;
import jp.loioz.app.user.feeList.form.FeeListViewForm;
import jp.loioz.bean.AnkenTantoLawyerJimuBean;
import jp.loioz.bean.FeeListBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TFeeDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.MAccountEntity;

/**
 * 報酬一覧画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FeeListService extends DefaultService {

	/** アカウントマスタDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 売上Daoクラス */
	@Autowired
	private TFeeDao tFeeDao;

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
	public FeeListViewForm createViewForm() {
		FeeListViewForm viewForm = new FeeListViewForm();
		return viewForm;
	}

	/**
	 * 検索条件に該当する報酬一覧情報を検索します。
	 * 
	 * @param viewForm
	 * @param searchForm 検索条件フォーム
	 */
	public FeeListViewForm searchFeeList(FeeListViewForm viewForm, FeeListSearchForm searchForm) {

		// 検索条件に該当する報酬一覧情報を取得
		viewForm = this.searchFeeListOnePage(viewForm, searchForm);

		// 一覧表示のソートキーとソート順をviewFormにセット
		viewForm.setFeeListSortItem(searchForm.getFeeListSortItem());
		viewForm.setFeeListSortOrder(searchForm.getFeeListSortOrder());

		return viewForm;
	}

	/**
	 * 報酬管理検索条件フラグメント表示用項目を設定する
	 * 
	 * @param searchForm
	 */
	public void setDispProperties(FeeListSearchForm searchForm) {

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

	/**
	 * 報酬一覧：検索条件フォームの検証をします。<br>
	 * エラーが有る場合はtrueを返します。<br>
	 * 
	 * @param searchForm
	 * @return
	 */
	public boolean validateFeeListSearchForm(FeeListSearchForm searchForm) {

		// 売上合計Fromの検証
		if (!StringUtils.isEmpty(searchForm.getFeeTotalFrom())) {
			try {
				Long.valueOf(searchForm.getFeeTotalFrom().replaceAll(",", ""));
			} catch (NumberFormatException nfe) {
				return true;
			}
			// 全角なら半角変換
			searchForm.setFeeTotalFrom(StringUtils.convertFullToHalfNum(searchForm.getFeeTotalFrom()));
		}

		// 売上合計Toの検証
		if (!StringUtils.isEmpty(searchForm.getFeeTotalTo())) {
			try {
				Long.valueOf(searchForm.getFeeTotalTo().replaceAll(",", ""));
			} catch (NumberFormatException nfe) {
				return true;
			}
			// 全角なら半角変換
			searchForm.setFeeTotalTo(StringUtils.convertFullToHalfNum(searchForm.getFeeTotalTo()));
		}
		return false;
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
	 * 報酬情報を取得する
	 * 
	 * @param viewForm
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	private FeeListViewForm searchFeeListOnePage(FeeListViewForm viewForm, FeeListSearchForm searchForm) {
		// 報酬情報を取得
		Page<FeeListDto> page = getOnePage(searchForm);
		// 1ページ分の報酬情報をセット
		viewForm.setPage(page);
		// 報酬情報をセット
		viewForm.setFeeList(page.getContent());
		return viewForm;
	}

	/**
	 * 報酬一覧で表示する1ページ分の売上・報酬情報を取得する<br>
	 * 
	 * @param searchForm
	 * @return
	 */
	private Page<FeeListDto> getOnePage(FeeListSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索処理
		List<FeeListBean> feeListBean = tFeeDao.selectByListSearchConditions(
				searchForm.toFeeListSearchCondition(), searchForm.toFeeListSortCondition(), options);

		// 報酬情報に担当弁護士、担当事務をセット
		this.setFeeListTantoLaywerJimu(feeListBean);

		// Dto変換
		List<FeeListDto> feeList = this.convertBean2Dto(feeListBean);

		Page<FeeListDto> page = new PageImpl<>(feeList, pageable, options.getCount());
		return page;
	}

	/**
	 * 報酬情報に担当弁護士、担当事務をセットします。<br>
	 * 
	 * @param feeList
	 */
	private void setFeeListTantoLaywerJimu(List<FeeListBean> feeList) {

		if (LoiozCollectionUtils.isEmpty(feeList)) {
			return;
		}

		List<Long> ankenIdList = feeList.stream().map(bean -> bean.getAnkenId()).collect(Collectors.toList());
		List<AnkenTantoLawyerJimuBean> tantoLaywerJimuBeanList = tAnkenTantoDao.selectAnkenTantoLaywerJimuById(ankenIdList);
		for (FeeListBean feeListBean : feeList) {
			// 担当弁護士の担当者名
			Optional<AnkenTantoLawyerJimuBean> lawyerOptBean = tantoLaywerJimuBeanList.stream().filter(
					tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(feeListBean.getAnkenId())
							&& TantoType.LAWYER.equalsByCode(tantoLaywerJimuBean.getTantoType()))
					.findFirst();
			if (lawyerOptBean.isPresent()) {
				feeListBean.setTantoLaywerName(lawyerOptBean.get().getTantoName());
			}
			// 担当事務の担当者名
			Optional<AnkenTantoLawyerJimuBean> jimuOptBean = tantoLaywerJimuBeanList.stream().filter(
					tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(feeListBean.getAnkenId())
							&& TantoType.JIMU.equalsByCode(tantoLaywerJimuBean.getTantoType()))
					.findFirst();
			if (jimuOptBean.isPresent()) {
				feeListBean.setTantoJimuName(jimuOptBean.get().getTantoName());
			}
		}
	}

	/**
	 * FeeListBean<Bean>型からFeeListDto<Dto>型に変換します
	 * 
	 * @param feeListBean
	 * @return
	 */
	private List<FeeListDto> convertBean2Dto(List<FeeListBean> feeListBean) {

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// Bean型をDto型に変換する
		List<FeeListDto> dtoList = new ArrayList<FeeListDto>();
		feeListBean.forEach(bean -> {
			dtoList.add(convertFeeBean2Dto(bean, bunyaMap));
		});
		return dtoList;
	}

	/**
	 * FeeListBean型からFeeListDto型に変換します<br>
	 * 
	 * @param feeListBean
	 * @param bunyaMap
	 * @return
	 */
	private FeeListDto convertFeeBean2Dto(FeeListBean feeListBean, Map<Long, BunyaDto> bunyaMap) {

		FeeListDto feeListDto = new FeeListDto();

		feeListDto.setPersonId(feeListBean.getPersonId());
		feeListDto.setName(feeListBean.getPersonNameSei() + CommonConstant.SPACE + feeListBean.getPersonNameMei());
		feeListDto.setAnkenId(feeListBean.getAnkenId());
		feeListDto.setAnkenName(feeListBean.getAnkenName());
		feeListDto.setBunyaName(bunyaMap.get(feeListBean.getBunyaId()).getBunyaName());
		feeListDto.setAnkenStatus(feeListBean.getAnkenStatus());
		feeListDto.setAnkenStatusName(CommonConstant.AnkenStatus.of(feeListBean.getAnkenStatus()).getVal());
		feeListDto.setTantoLaywerName(feeListBean.getTantoLaywerName());
		feeListDto.setTantoJimuName(feeListBean.getTantoJimuName());
		feeListDto.setFeeTotalAmount(AccountingUtils.toDispAmountLabel(feeListBean.getFeeTotalAmount()));
		feeListDto.setFeeUnclaimedTotalAmount(AccountingUtils.toDispAmountLabel(feeListBean.getFeeUnclaimedTotalAmount()));
		feeListDto.setLastEditAt(feeListBean.getLastEditAt());

		return feeListDto;
	}

}