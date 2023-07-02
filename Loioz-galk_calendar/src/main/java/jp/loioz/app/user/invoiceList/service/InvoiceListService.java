package jp.loioz.app.user.invoiceList.service;

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
import jp.loioz.app.user.invoiceList.dto.InvoiceListDto;
import jp.loioz.app.user.invoiceList.form.InvoiceListSearchForm;
import jp.loioz.app.user.invoiceList.form.InvoiceListViewForm;
import jp.loioz.bean.AnkenTantoLawyerJimuBean;
import jp.loioz.bean.InvoiceListBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.InvoicePaymentStatus;
import jp.loioz.common.constant.CommonConstant.InvoiceType;
import jp.loioz.common.constant.CommonConstant.IssueStatus;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAccgInvoiceDao;
import jp.loioz.dao.TAccgStatementDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TAccgInvoiceEntity;
import jp.loioz.entity.TAccgStatementEntity;

/**
 * 請求書一覧覧画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InvoiceListService {

	/** アカウントマスタDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 請求書Daoクラス */
	@Autowired
	private TAccgInvoiceDao tAccgInvoiceDao;

	/** 案件担当Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** 精算書Daoクラス */
	@Autowired
	private TAccgStatementDao tAccgStatementDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 画面表示情報を作成する
	 * 
	 * @return
	 */
	public InvoiceListViewForm createViewForm() {
		InvoiceListViewForm viewForm = new InvoiceListViewForm();
		return viewForm;
	}

	/**
	 * 検索条件に該当する情報を検索します。
	 * 
	 * @param viewForm
	 * @param searchForm 検索条件フォーム
	 */
	public InvoiceListViewForm searchInvoiceList(InvoiceListViewForm viewForm, InvoiceListSearchForm searchForm) {

		// 検索条件に該当する請求書情報を取得
		viewForm = this.searchInvoiceListOnePage(viewForm, searchForm);

		// 一覧表示のソートキーとソート順をviewFormにセット
		viewForm.setInvoiceListSortItem(searchForm.getInvoiceListSortItem());
		viewForm.setInvoiceListSortOrder(searchForm.getInvoiceListSortOrder());

		return viewForm;
	}

	/**
	 * 請求書検索条件フラグメント表示用項目を設定する
	 * 
	 * @param searchForm
	 */
	public void setDispProperties(InvoiceListSearchForm searchForm) {

		// 売上計上先、担当弁護士、担当事務のプルダウン情報を検索条件Formにセット
		List<MAccountEntity> accountList = mAccountDao.selectAll();
		searchForm.setSalesOwnerList(getLawyerDtoList(accountList));
		searchForm.setTantoLawyerList(getLawyerDtoList(accountList));
		searchForm.setTantoJimuList(getJimuDtoList(accountList));
	}

	/**
	 * 請求書情報が有るか確認します<br>
	 * 有る場合は true を返します
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	public boolean checkExistsInvoice(Long invoiceSeq) {
		boolean isExists = false;
		TAccgInvoiceEntity tAccgInvoiceEntity = tAccgInvoiceDao.selectInvoiceByInvoiceSeq(invoiceSeq);
		if (tAccgInvoiceEntity != null) {
			isExists = true;
		}
		return isExists;
	}

	/**
	 * 精算書情報が有るか確認します<br>
	 * 有る場合は true を返します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	public boolean checkExistsStatement(Long accgDocSeq) {
		boolean isExists = false;
		TAccgStatementEntity tAccgStatementEntity = tAccgStatementDao.selectStatementByAccgDocSeq(accgDocSeq);
		if (tAccgStatementEntity != null) {
			isExists = true;
		}
		return isExists;
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
	 * 請求書情報を取得する
	 * 
	 * @param viewForm
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	private InvoiceListViewForm searchInvoiceListOnePage(InvoiceListViewForm viewForm, InvoiceListSearchForm searchForm) {
		// 請求書情報を取得
		Page<InvoiceListDto> page = getOnePage(searchForm);
		// 1ページ分の案件情報をセット
		viewForm.setPage(page);
		// 案件情報をセット
		viewForm.setInvoiceList(page.getContent());
		return viewForm;
	}

	/**
	 * 一覧で表示する1ページ分を取得する<br>
	 * 
	 * @param searchForm
	 * @return
	 */
	private Page<InvoiceListDto> getOnePage(InvoiceListSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索処理
		List<InvoiceListBean> invoiceListBeanList = tAccgInvoiceDao.selectInvoiceListBySearchConditions(
				searchForm.toInvoiceListSearchCondition(), searchForm.toInvoiceListSortCondition(), options);

		// 請求書情報に担当弁護士、担当事務をセット
		this.setInvoiceListTantoLaywerJimu(invoiceListBeanList);

		// Dto変換
		List<InvoiceListDto> invoiceList = this.convertBean2Dto(invoiceListBeanList);

		Page<InvoiceListDto> page = new PageImpl<>(invoiceList, pageable, options.getCount());
		return page;
	}

	/**
	 * 請求書情報に担当弁護士、担当事務をセットします。<br>
	 * 
	 * @param invoiceList
	 */
	private void setInvoiceListTantoLaywerJimu(List<InvoiceListBean> invoiceList) {

		if (LoiozCollectionUtils.isEmpty(invoiceList)) {
			return;
		}

		List<Long> ankenIdList = invoiceList.stream().map(bean -> bean.getAnkenId()).collect(Collectors.toList());
		List<AnkenTantoLawyerJimuBean> tantoLaywerJimuBeanList = tAnkenTantoDao.selectAnkenTantoLaywerJimuById(ankenIdList);
		for (InvoiceListBean invoiceListBean : invoiceList) {
			// 担当弁護士の担当者名
			Optional<AnkenTantoLawyerJimuBean> lawyerOptBean = tantoLaywerJimuBeanList.stream().filter(
					tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(invoiceListBean.getAnkenId())
							&& TantoType.LAWYER.equalsByCode(tantoLaywerJimuBean.getTantoType()))
					.findFirst();
			if (lawyerOptBean.isPresent()) {
				invoiceListBean.setTantoLaywerName(lawyerOptBean.get().getTantoName());
			}
			// 担当事務の担当者名
			Optional<AnkenTantoLawyerJimuBean> jimuOptBean = tantoLaywerJimuBeanList.stream().filter(
					tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(invoiceListBean.getAnkenId())
							&& TantoType.JIMU.equalsByCode(tantoLaywerJimuBean.getTantoType()))
					.findFirst();
			if (jimuOptBean.isPresent()) {
				invoiceListBean.setTantoJimuName(jimuOptBean.get().getTantoName());
			}
		}
	}

	/**
	 * InvoiceListBean<Bean>型からInvoiceListDto<Dto>型に変換します
	 * 
	 * @param invoiceListBean
	 * @return
	 */
	private List<InvoiceListDto> convertBean2Dto(List<InvoiceListBean> invoiceListBean) {

		// Bean型をDto型に変換する
		List<InvoiceListDto> dtoList = new ArrayList<InvoiceListDto>();
		invoiceListBean.forEach(bean -> {
			dtoList.add(convertInvoiceBean2Dto(bean));
		});
		return dtoList;
	}

	/**
	 * InvoiceListBean型からInvoiceListDto型に変換します<br>
	 * 
	 * @param bean
	 * @return
	 */
	private InvoiceListDto convertInvoiceBean2Dto(InvoiceListBean bean) {

		InvoiceListDto invoiceListDto = new InvoiceListDto();

		// 会計書類SEQ
		invoiceListDto.setAccgDocSeq(bean.getAccgDocSeq());
		// 案件ID
		invoiceListDto.setAnkenId(bean.getAnkenId());
		// 案件名
		invoiceListDto.setAnkenName(bean.getAnkenName());
		// 請求方法
		invoiceListDto.setInvoiceType(bean.getInvoiceType());
		if (!StringUtils.isEmpty(bean.getInvoiceType())) {
			// 請求方法表示名
			invoiceListDto.setInvoiceTypeName(InvoiceType.of(bean.getInvoiceType()).getVal());
		}
		// 仕払期日
		invoiceListDto.setDueDate(bean.getDueDate());
		// 請求金額
		invoiceListDto.setInvoiceAmount(AccountingUtils.toDispAmountLabel(bean.getInvoiceAmount()));
		// 請求日
		invoiceListDto.setInvoiceDate(bean.getInvoiceDate());
		// 発行ステータス
		invoiceListDto.setInvoiceIssueStatus(bean.getInvoiceIssueStatus());
		if (!StringUtils.isEmpty(bean.getInvoiceIssueStatus())) {
			// 発行ステータス表示名
			invoiceListDto.setInvoiceIssueStatusName(IssueStatus.of(bean.getInvoiceIssueStatus()).getVal());
		}
		// 請求書メモ
		invoiceListDto.setInvoiceMemo(bean.getInvoiceMemo());
		// 請求番号
		invoiceListDto.setInvoiceNo(bean.getInvoiceNo());
		// 入金状況
		invoiceListDto.setInvoicePaymentStatus(bean.getInvoicePaymentStatus());
		if (!StringUtils.isEmpty(bean.getInvoicePaymentStatus())) {
			// 入金状況表示名
			invoiceListDto.setInvoicePaymentStatusName(InvoicePaymentStatus.of(bean.getInvoicePaymentStatus()).getVal());
		}
		// 請求書SEQ
		invoiceListDto.setInvoiceSeq(bean.getInvoiceSeq());
		// 名前
		invoiceListDto.setName(bean.getPersonNameSei() + CommonConstant.SPACE + bean.getPersonNameMei());
		// 名簿ID
		invoiceListDto.setPersonId(bean.getPersonId());
		// 担当弁護士
		invoiceListDto.setTantoLaywerName(bean.getTantoLaywerName());
		// 担当事務
		invoiceListDto.setTantoJimuName(bean.getTantoJimuName());
		// 取引実績SEQ
		invoiceListDto.setAccgRecordSeq(bean.getAccgRecordSeq());

		return invoiceListDto;
	}

}