package jp.loioz.app.user.caseInvoiceStatementList.form;

import java.util.List;

import jp.loioz.app.common.form.accg.AccgCaseForm;
import jp.loioz.app.common.form.accg.AccgCasePersonForm;
import jp.loioz.app.common.form.accg.AccgCaseSummaryForm;
import jp.loioz.dto.AccgInvoiceStatementListItemDto;
import lombok.Data;

/**
 * 請求書・精算書画面の画面表示フォームクラス
 */
@Data
public class CaseInvoiceStatementListViewForm {

	/** 会計管理-案件共通 */
	private AccgCaseForm accgCaseForm;

	/** 会計管理-案件共通-報酬、預り金 */
	private AccgCaseSummaryForm accgCaseSummaryForm;

	/** 会計管理-共通サイド-対象の顧客 */
	private AccgCasePersonForm accgCasePersonForm;

	/** 請求書／精算書 */
	private List<AccgInvoiceStatementListItemDto> invoiceStatementList;

}
