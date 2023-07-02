package jp.loioz.app.user.depositRecvDetail.dto;

import java.time.LocalDate;

import jp.loioz.common.constant.AccgConstant;
import jp.loioz.common.constant.CommonConstant.DepositType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * 預り金明細一覧表示用Dto
 */
@Data
public class DepositRecvDetailListDto {

	/** 預り金SEQ */
	private Long depositRecvSeq;

	/** 会計書類SEQ（作成元） */
	private Long createdAccgDocSeq;

	/** 会計書類SEQ（使用先） */
	private Long usingAccgDocSeq;

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	/** 項目 */
	private String depositItemName;

	/** 発生日 */
	private LocalDate depositDate;

	/** 入出金完了フラグ */
	private String depositCompleteFlg;

	/** 入出金タイプ */
	private String depositType;

	/** 作成タイプ */
	private String createdType;

	/** 実費入金フラグ */
	private String expenseInvoiceFlg;

	/** 入金額 */
	private String depositAmount;

	/** 出金額 */
	private String withdrawalAmount;

	/** 使用先請求書SEQ */
	private Long usingInvoiceSeq; 

	/** 使用先請求書番号 */
	private String usingInvoiceNo;

	/** 使用先請求書入金ステータス */
	private String usingInvoicePaymentStatus;

	/** 使用先精算書SEQ */
	private Long usingStatementSeq;

	/** 使用先精算書番号 */
	private String usingStatementNo;

	/** 使用先精算書返金ステータス */
	private String usingStatementRefundStatus;

	/** 作成元請求書SEQ */
	private Long createdInvoiceSeq; 

	/** 作成元請求書番号 */
	private String createdInvoiceNo;

	/** 作成元請求書入金ステータス */
	private String createdInvoicePaymentStatus;

	/** 作成元精算書SEQ */
	private Long createdStatementSeq;

	/** 作成元精算書番号 */
	private String createdStatementNo;

	/** 作成元精算書返金ステータス */
	private String createdStatementRefundStatus;

	/** 事務所負担フラグ */
	private boolean isTenantBear;

	/** 摘要 */
	private String sumText;

	/** メモ */
	private String depositRecvMemo;

	/** 回収不能フラグ */
	private String uncollectibleFlg;

	/** 選択チェックボックス（チェック済みフラグ） */
	private boolean isChecked;

	/** チェックボックス表示フラグ */
	private boolean checkboxDisplayFlg;

	/**
	 * 入出金タイプが「入金」かどうかを判定する<br>
	 * ※入出金タイプの値が設定されていない場合にこのメソッドを実行した場合はRuntimeExceptionが発生する。
	 * 
	 * @return
	 */
	public boolean isTypeDeposit() {
		
		String depositTypeCd = this.depositType;
		if (StringUtils.isEmpty(depositTypeCd)) {
			throw new RuntimeException("depositTypeが設定されていないためエラー。");
		}
		
		boolean isTypeDeposit = DepositType.NYUKIN.getCd().equals(depositTypeCd);
		
		return isTypeDeposit;
	}
	
	/**
	 * 入出金が完了している場合は発生日を-区切りに編集して取得します。<br>
	 * 入出金が未完了の場合は、入出金タイプに応じたステータス名を取得します<br>
	 * 
	 * @return
	 */
	public String getDateFormat() {
		// 入出金完了フラグがONの場合は発生日を表示
		if (SystemFlg.codeToBoolean(this.depositCompleteFlg)) {
			if (this.depositDate != null) {
				return DateUtils.parseToString(this.depositDate, DateUtils.DATE_FORMAT_HYPHEN_DELIMITED);
			}
			return "";
		} else {
			// 入出金完了フラグがOFFの場合は発生日が無いのでステータス名を表示
			if (DepositType.NYUKIN.equalsByCode(this.depositType)) {
				return AccgConstant.STATUS_DEPOSIT_RECV_NOT_YET;
			} else {
				return AccgConstant.STATUS_DEPOSIT_PAYMENT_NOT_YET;
			}
		}
	}

	/**
	 * 黒字で表示
	 * 
	 * @return
	 */
	public boolean isDispInBlack() {

		// 発生日が有るなら黒字
		if (this.depositDate != null) {
			return true;
		}

		// 入出金完了フラグが無い場合は、請求書や精算書作成時にできたデータでは無いため黒字
		if (StringUtils.isEmpty(this.depositCompleteFlg)) {
			return true;
		}

		return false;
	}
}
