package jp.loioz.app.user.caseInvoiceStatementList.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.accg.AccgCaseForm;
import jp.loioz.app.common.form.accg.AccgCasePersonForm;
import jp.loioz.app.common.form.accg.AccgCaseSummaryForm;
import jp.loioz.app.common.service.CommonAccgSummaryService;
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.app.user.caseInvoiceStatementList.form.CaseInvoiceStatementListViewForm;
import jp.loioz.dto.AccgInvoiceStatementListItemDto;

/**
 * 請求書／精算書一覧画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CaseInvoiceStatementListService extends DefaultService {

	/** 会計管理：預り金詳細、報酬詳細の共通サービス */
	@Autowired
	private CommonAccgSummaryService commonAccgSummaryService;

	/** 会計管理共通サービスクラス */
	@Autowired
	private CommonAccgService commonAccgService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 請求書／精算書画面の表示情報を作成する
	 * 
	 * @return
	 */
	public CaseInvoiceStatementListViewForm createViewForm() {
		CaseInvoiceStatementListViewForm viewForm = new CaseInvoiceStatementListViewForm();
		return viewForm;
	}

	/**
	 * 会計管理の案件情報、報酬・預り金サマリを取得、設定します
	 * 
	 * @param viewForm
	 * @param ankenId
	 * @param personId
	 */
	public void getAccgData(CaseInvoiceStatementListViewForm viewForm, Long ankenId, Long personId) {

		// 案件「会計管理」の案件情報
		AccgCaseForm accgCaseForm = commonAccgSummaryService.getAccgCase(ankenId, personId);
		viewForm.setAccgCaseForm(accgCaseForm);

		// 案件「会計管理」の報酬、預り金合計
		AccgCaseSummaryForm accgCaseSummaryForm = commonAccgSummaryService.getAccgCaseSummary(ankenId, personId);
		viewForm.setAccgCaseSummaryForm(accgCaseSummaryForm);

		// 案件「会計管理」の顧客一覧
		AccgCasePersonForm pccgCasePersonForm = commonAccgSummaryService.getAccgCasePerson(ankenId, personId);
		viewForm.setAccgCasePersonForm(pccgCasePersonForm);

	}

	/**
	 * 請求書・精算書情報を取得、設定します
	 * 
	 * @param viewForm
	 * @param ankenId
	 * @param personId
	 */
	public void searchCaseInvoiceStatementList(CaseInvoiceStatementListViewForm viewForm, Long ankenId, Long personId) {

		// 請求書／精算書取得
		List<AccgInvoiceStatementListItemDto> invoiceStatementList = commonAccgService.getAccgDocInvoiceStatementList(ankenId, personId, true);
		viewForm.setInvoiceStatementList(invoiceStatementList);

	}

}