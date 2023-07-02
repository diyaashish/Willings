package jp.loioz.app.user.personCase.form;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import jp.loioz.app.common.form.accg.AccgPersonSummaryForm;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenTantoDispDto;
import lombok.Data;

/**
 * 名簿-案件表示フォーム
 */
@Data
public class PersonCaseViewForm {

	/** 名簿ID */
	private Long personId;

	/** 名前 */
	private PersonName personName;

	/** 名簿-案件一覧画面表示用オブジェクト */
	private List<PersonAnkenListViewForm> personAnkenListViewForm;

	/** 連携中ストレージ */
	private ExternalService storageConnectedService;

	/******************************
	 * 報酬
	 ******************************/

	/** 報酬-合計（報酬 + 消費税額 ） */
	private BigDecimal feeTotalAmount;

	/** 報酬-未請求（報酬 + 消費税額 ） */
	private BigDecimal feeUnclaimedTotalAmount;

	/******************************
	 * 預り金
	 ******************************/

	/** 預り金：預り金残高 */
	private BigDecimal totalDepositBalanceAmount;

	/** 預り金：入金合計 */
	private BigDecimal totalDepositAmount;

	/** 預り金：出金合計 */
	private BigDecimal totalWithdrawalAmount;

	/** 預り金：事務所負担金額 */
	private BigDecimal totalTenantBearAmount;

	/******************************
	 * 売上
	 ******************************/

	/** 対象名簿の売上合計（見込） */
	private BigDecimal personTotalSalesAmountExpect;

	/** 対象名簿の売上合計（実績） */
	private BigDecimal personTotalSalesAmountResult;

	/** 対象名簿の売上合計（税込） */
	private BigDecimal personTotalSalesTaxIncludedAmount;

	/** 対象名簿の源泉徴収税合計 */
	private BigDecimal personTotalSalesWithholdingAmount;

	/** 対象名簿の値引き合計 */
	private BigDecimal personTotalSalesDiscountAmount;

	/** 対象名簿の入金待ち合計 */
	private BigDecimal personTotalSalesAwaitingAmount;

	/**
	 * 名簿-案件一覧画面表示用オブジェクト
	 */
	@Data
	public static class PersonAnkenListViewForm {

		/** 名簿ID */
		private Long personId;

		/** 案件ID */
		private Long ankenId;

		/** 案件登録日 */
		private String ankenCreateDate;

		/** 分野名 */
		private String bunyaName;

		/** 分野種別 */
		private String bunyaTypeCd;

		/** 案件種別 */
		private AnkenType ankenType;

		/** 案件名 */
		private String ankenName;

		/** 担当弁護士 */
		private List<AnkenTantoDispDto> tantoLawyer = Collections.emptyList();

		/** 担当事務 */
		private List<AnkenTantoDispDto> tantoJimu = Collections.emptyList();

		/** 案件ステータス */
		private AnkenStatus ankenStatus;

		/** 受任日 */
		private String acceptDate;

		/** 事件処理完了日 */
		private String caseCompleteDate;

		/** 完了日 */
		private String completeDate;

		/** 完了フラグ */
		private boolean isComplete;

		/** 案件相手方 */
		private String aitegataNameDisp;

		/** 裁判枝番 */
		private Long firstSaibanBranchNo;

		/** 会計 売上金額、預り金サマリー */
		private AccgPersonSummaryForm accgPersonSummaryForm;

	}

}
