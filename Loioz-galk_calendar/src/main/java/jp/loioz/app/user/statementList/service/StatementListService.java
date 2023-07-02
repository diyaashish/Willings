package jp.loioz.app.user.statementList.service;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.util.StringUtils;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.user.statementList.dto.StatementListDto;
import jp.loioz.app.user.statementList.form.StatementListSearchForm;
import jp.loioz.app.user.statementList.form.StatementListViewForm;
import jp.loioz.bean.AnkenTantoLawyerJimuBean;
import jp.loioz.bean.StatementListBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.IssueStatus;
import jp.loioz.common.constant.CommonConstant.StatementRefundStatus;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAccgStatementDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.entity.MAccountEntity;

/**
 * 精算書一覧覧画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StatementListService {

	/** アカウントマスタDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 精算書Daoクラス */
	@Autowired
	private TAccgStatementDao tAccgStatementDao;

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
	public StatementListViewForm createViewForm() {
		StatementListViewForm viewForm = new StatementListViewForm();
		return viewForm;
	}

	/**
	 * 検索条件に該当する情報を検索します。
	 * 
	 * @param viewForm
	 * @param searchForm 検索条件フォーム
	 */
	public StatementListViewForm searchStatementList(StatementListViewForm viewForm, StatementListSearchForm searchForm) {

		// 検索条件に該当する精算書情報を取得
		viewForm = this.searchStatementListOnePage(viewForm, searchForm);

		// 一覧表示のソートキーとソート順をviewFormにセット
		viewForm.setStatementListSortItem(searchForm.getStatementListSortItem());
		viewForm.setStatementListSortOrder(searchForm.getStatementListSortOrder());

		return viewForm;
	}

	/**
	 * 精算書検索条件フラグメント表示用項目を設定する
	 * 
	 * @param searchForm
	 */
	public void setDispProperties(StatementListSearchForm searchForm) {

		// 売上計上先、担当弁護士、担当事務のプルダウン情報を検索条件Formにセット
		List<MAccountEntity> accountList = mAccountDao.selectAll();
		searchForm.setSalesOwnerList(getLawyerDtoList(accountList));
		searchForm.setTantoLawyerList(getLawyerDtoList(accountList));
		searchForm.setTantoJimuList(getJimuDtoList(accountList));
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
	 * 精算書情報を取得する
	 * 
	 * @param viewForm
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	private StatementListViewForm searchStatementListOnePage(StatementListViewForm viewForm, StatementListSearchForm searchForm) {
		// 精算書情報を取得
		Page<StatementListDto> page = getOnePage(searchForm);
		// 1ページ分の案件情報をセット
		viewForm.setPage(page);
		// 案件情報をセット
		viewForm.setStatementList(page.getContent());
		return viewForm;
	}

	/**
	 * 一覧で表示する1ページ分を取得する<br>
	 * 
	 * @param searchForm
	 * @return
	 */
	private Page<StatementListDto> getOnePage(StatementListSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索処理
		List<StatementListBean> statementListBeanList = tAccgStatementDao.selectStatementListBySearchConditions(
				searchForm.toStatementListSearchCondition(), searchForm.toStatementListSortCondition(), options);

		// 精算書情報に担当弁護士、担当事務をセット
		this.setStatementListTantoLaywerJimu(statementListBeanList);

		// Dto変換
		List<StatementListDto> statementList = this.convertBean2Dto(statementListBeanList);

		Page<StatementListDto> page = new PageImpl<>(statementList, pageable, options.getCount());
		return page;
	}

	/**
	 * 精算書情報に担当弁護士、担当事務をセットします。<br>
	 * 
	 * @param statementList
	 */
	private void setStatementListTantoLaywerJimu(List<StatementListBean> statementList) {

		if (LoiozCollectionUtils.isEmpty(statementList)) {
			return;
		}

		List<Long> ankenIdList = statementList.stream().map(bean -> bean.getAnkenId()).collect(Collectors.toList());
		List<AnkenTantoLawyerJimuBean> tantoLaywerJimuBeanList = tAnkenTantoDao.selectAnkenTantoLaywerJimuById(ankenIdList);
		for (StatementListBean statementListBean : statementList) {
			// 担当弁護士の担当者名
			Optional<AnkenTantoLawyerJimuBean> lawyerOptBean = tantoLaywerJimuBeanList.stream().filter(
					tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(statementListBean.getAnkenId())
							&& TantoType.LAWYER.equalsByCode(tantoLaywerJimuBean.getTantoType()))
					.findFirst();
			if (lawyerOptBean.isPresent()) {
				statementListBean.setTantoLaywerName(lawyerOptBean.get().getTantoName());
			}
			// 担当事務の担当者名
			Optional<AnkenTantoLawyerJimuBean> jimuOptBean = tantoLaywerJimuBeanList.stream().filter(
					tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(statementListBean.getAnkenId())
							&& TantoType.JIMU.equalsByCode(tantoLaywerJimuBean.getTantoType()))
					.findFirst();
			if (jimuOptBean.isPresent()) {
				statementListBean.setTantoJimuName(jimuOptBean.get().getTantoName());
			}
		}
	}

	/**
	 * StatementListBean<Bean>型からStatementListDto<Dto>型に変換します
	 * 
	 * @param statementListBean
	 * @return
	 */
	private List<StatementListDto> convertBean2Dto(List<StatementListBean> statementListBean) {

		// Bean型をDto型に変換する
		List<StatementListDto> dtoList = new ArrayList<StatementListDto>();
		statementListBean.forEach(bean -> {
			dtoList.add(convertStatementBean2Dto(bean));
		});
		return dtoList;
	}

	/**
	 * StatementListBean型からStatementListDto型に変換します<br>
	 * 
	 * @param bean
	 * @return
	 */
	private StatementListDto convertStatementBean2Dto(StatementListBean bean) {

		StatementListDto statementListDto = new StatementListDto();

		// 会計書類SEQ
		statementListDto.setAccgDocSeq(bean.getAccgDocSeq());
		// 案件ID
		statementListDto.setAnkenId(bean.getAnkenId());
		// 案件名
		statementListDto.setAnkenName(bean.getAnkenName());
		// 期日
		statementListDto.setRefundDate(bean.getRefundDate());
		// 請求金額
		statementListDto.setStatementAmount(AccountingUtils.toDispAmountLabel(bean.getStatementAmount()));
		// 請求日
		statementListDto.setStatementDate(bean.getStatementDate());
		// 発行ステータス
		statementListDto.setStatementIssueStatus(bean.getStatementIssueStatus());
		if (!StringUtils.isEmpty(bean.getStatementIssueStatus())) {
			// 発行ステータス表示名
			statementListDto.setStatementIssueStatusName(IssueStatus.of(bean.getStatementIssueStatus()).getVal());
		}
		// 精算書メモ
		statementListDto.setStatementMemo(bean.getStatementMemo());
		// 請求番号
		statementListDto.setStatementNo(bean.getStatementNo());
		// 精算状況
		statementListDto.setStatementRefundStatus(bean.getStatementRefundStatus());
		if (!StringUtils.isEmpty(bean.getStatementRefundStatus())) {
			// 精算状況表示名
			statementListDto.setStatementRefundStatusName(StatementRefundStatus.of(bean.getStatementRefundStatus()).getVal());
		}
		// 精算書SEQ
		statementListDto.setStatementSeq(bean.getStatementSeq());
		// 名前
		statementListDto.setName(bean.getPersonNameSei() + CommonConstant.SPACE + bean.getPersonNameMei());
		// 名簿ID
		statementListDto.setPersonId(bean.getPersonId());
		// 担当弁護士
		statementListDto.setTantoLaywerName(bean.getTantoLaywerName());
		// 担当事務
		statementListDto.setTantoJimuName(bean.getTantoJimuName());
		// 取引実績SEQ
		statementListDto.setAccgRecordSeq(bean.getAccgRecordSeq());

		return statementListDto;
	}

}