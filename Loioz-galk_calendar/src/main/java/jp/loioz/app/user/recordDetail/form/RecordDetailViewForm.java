package jp.loioz.app.user.recordDetail.form;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.recordDetail.dto.PaymentPlanListItemDto;
import jp.loioz.app.user.recordDetail.dto.RecordListItemDto;
import jp.loioz.app.user.recordDetail.dto.RecordSummaryDto;
import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.domain.value.PersonAttribute;
import lombok.Data;

/**
 * 取引実績詳細画面の画面表示フォームクラス
 */
@Data
public class RecordDetailViewForm {

	/** 取引明細SEQ */
	private Long accgRecordSeq;

	/** 会計記録SEQ */
	private Long accgDocSeq;

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	/** 請求 / 精算種別 */
	private AccgDocType accgDocType;

	/** 請求書SEQ ※請求書の場合のみ保持 */
	private Long invoiceSeq;

	/** 精算書SEQ ※精算書の場合のみ保持 */
	private Long statementSeq;

	/** 取引実績-会計書類（請求書・精算書）フォーム */
	private RecordDetailAccgDocViewForm recordDetailAccgDocViewForm;

	/** 報酬、預り金／実費の内訳フォームオブジェクト */
	private BreakdownOfFeeAndDepositViewForm breakdownOfFeeAndDepositViewForm;

	/** 取引実績一覧フォームオブジェクト */
	private RecordListViewForm recordListViewForm;

	/** 支払い計画一覧表示オブジェクト */
	private PaymentPlanListViewForm paymentPlanListViewForm;

	/**
	 * 取引実績ヘッダーフォーム（フラグメント）
	 */
	@Data
	public static class RecordDetailAccgDocViewForm {

		/** 会計記録SEQ */
		private Long accgDocSeq;

		/** 取引明細SEQ */
		private Long accgRecordSeq;

		/** 案件ID */
		private Long ankenId;

		/** 名簿ID */
		private Long personId;

		/** 分野名 */
		private String bunyaName;

		/** 案件種別 */
		private AnkenType ankenType;

		/** 案件ステータス（顧客ステータス） */
		private AnkenStatus ankenStatus;

		/** 案件名 */
		private String ankenName;

		/** 名前 */
		private String personName;

		/** 名簿属性 */
		private PersonAttribute personAttribute;

		/** 名簿種別 */
		private CustomerType customerType;

		/** 会計書類タイプ */
		private AccgDocType accgDocType;

		/** 取引実績金額サマリ */
		private RecordSummaryDto recordSummaryDto;

	}

	/**
	 * 報酬、預り金／実費の内訳情報
	 */
	@Data
	public static class BreakdownOfFeeAndDepositViewForm {

		/** 報酬入金額見込 */
		private BigDecimal feeAmountExpect;

		/** 預り金入金額見込 */
		private BigDecimal depositRecvAmountExpect;

		/** 報酬入金額 */
		private BigDecimal recordFeeAmount;

		/** 預り金入金額 */
		private BigDecimal recordDepositRecvAmount;

		/** 過入金額 */
		private BigDecimal overPaymentAmount;

		/** 入金額合計 */
		private BigDecimal recordTotalAmount;

		/** 報酬残金 */
		private BigDecimal feeBalance;

		/** 預り金残金 */
		private BigDecimal depositRecvBalance;

		/** 残金合計 */
		private BigDecimal totalBalance;

		/**
		 * 報酬、預り金／実費の内訳情報を表示するかどうか<br>
		 * ※報酬と預り金／実費が混在している（請求書）の場合にtrueを返します。
		 * 
		 * @return
		 */
		public boolean shouldDisplayBreakdown() {
			boolean dispFlg = true;
			if (this.feeAmountExpect == null || LoiozNumberUtils.equalsZero(this.feeAmountExpect)) {
				dispFlg = false;
				return dispFlg;
			}
			
			if (this.depositRecvAmountExpect == null || LoiozNumberUtils.equalsZero(this.depositRecvAmountExpect)) {
				dispFlg = false;
				return dispFlg;
			}
			
			return dispFlg;
		}
	}

	/**
	 * 取引実績一覧画面表示情報
	 */
	@Data
	public static class RecordListViewForm {

		/** 取引明細SEQ */
		private Long accgRecordSeq;

		/** 会計記録SEQ */
		private Long accgDocSeq;

		/** 回収不能に変更可能かどうか */
		private boolean isChangableToUncollectible;

		/** 回収不能解除が可能かどうか */
		private boolean isUndoableFromUncollectible;

		/** 取引実績一覧 */
		private List<RecordListItemDto> recordList = Collections.emptyList();

		/** 過入金レコードが存在するかどうか */
		private boolean existsOverPaymentRecord;

		/** 返金済みのレコードが存在するかどうか */
		private boolean existsRefundedRecord;

		/** 追加可能 */
		private boolean canAdd;

	}

	/**
	 * 支払計画一覧画面表示用オブジェクト
	 */
	@Data
	public static class PaymentPlanListViewForm {

		/** 会計書類SEQ */
		private Long accgDocSeq;

		/** 会計書類SEQ */
		private Long invoiceSeq;

		/** 支払計画一覧 */
		private List<PaymentPlanListItemDto> paymentPlanList = Collections.emptyList();

	}

}
