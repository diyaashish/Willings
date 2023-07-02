package jp.loioz.app.common.excel.dto;

import java.math.BigDecimal;
import java.util.List;

import jp.loioz.common.constant.AccgConstant;
import jp.loioz.common.constant.CommonConstant.DepositType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import lombok.Data;

/**
 * 預り金明細用のExcelDto
 */
@Data
public class DepositRecvDetailListExcelDto {

	/** 出力日 */
	private String outPutRange;

	/** 名簿名 */
	private String personName;

	/** 案件名 */
	private String ankenName;

	/** Excelに出力する預り金明細データ */
	private List<ExcelDepositRecvDetailListRowData> depositRecvDetailListDtoList;

	/**
	 * 預り金明細の1行分のデータ
	 */
	@Data
	public static class ExcelDepositRecvDetailListRowData {

		/** 入出金タイプコード */
		private String depositTypeCd;

		/** 入出金タイプ */
		private String depositType;

		/** 項目 */
		private String depositItemName;

		/** 発生日 */
		private String depositDate;

		/** 入出金完了フラグ */
		private String depositCompleteFlg;

		/** 入金額 */
		private BigDecimal depositAmount;

		/** 出金額 */
		private BigDecimal withdrawalAmount;

		/** 請求書／精算書 */
		private String InvoiceStatementNo;

		/** 事務所負担フラグ */
		private boolean isTenantBear;

		/** 摘要 */
		private String sumText;

		/** メモ */
		private String depositRecvMemo;

		/** 回収不能フラグ */
		private String uncollectibleFlg;

		/** 回収不能警告メッセージ */
		private String uncollectibleWarningMessage;

		/**
		 * 入出金が完了している場合は発生日を-区切りに編集して取得します。<br>
		 * 入出金が未完了の場合は、入出金タイプに応じたステータス名を取得します<br>
		 * 
		 * @return
		 */
		public String getDateFormat() {
			String depositDate = "";
			
			// 入出金完了フラグがONの場合は発生日を表示
			if (SystemFlg.codeToBoolean(this.depositCompleteFlg)) {
				if (this.depositDate != null) {
					depositDate = this.depositDate;
				}
			} else {
				// 入出金完了フラグがOFFの場合は発生日が無いのでステータス名を表示
				if (DepositType.NYUKIN.equalsByCode(this.depositTypeCd)) {
					depositDate = AccgConstant.STATUS_DEPOSIT_RECV_NOT_YET;
				} else if (DepositType.SHUKKIN.equalsByCode(this.depositTypeCd)) {
					depositDate = AccgConstant.STATUS_DEPOSIT_PAYMENT_NOT_YET;
				} else {
					throw new RuntimeException("想定外の入出金タイプ[" + this.depositTypeCd + "]");
				}
				
				// 回収不能フラグが立っている場合は、不能の旨のメッセージを表示
				if (SystemFlg.FLG_ON.equalsByCode(this.uncollectibleFlg)) {
					depositDate = depositDate + "\r\n*" + this.uncollectibleWarningMessage;
				}
			}
			
			return depositDate;
		}
	}
}