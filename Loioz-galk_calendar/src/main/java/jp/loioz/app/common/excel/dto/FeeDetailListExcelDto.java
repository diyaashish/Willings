package jp.loioz.app.common.excel.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * 報酬明細用のExcelDto
 */
@Data
public class FeeDetailListExcelDto {

	/** 出力日 */
	private String outPutRange;

	/** 名簿名 */
	private String personName;

	/** 案件名 */
	private String ankenName;

	/** Excelに出力する報酬明細データ */
	private List<ExcelFeeDetailListRowData> feeDetailListDtoList;

	/**
	 * 報酬明細の1行分のデータ
	 */
	@Data
	public static class ExcelFeeDetailListRowData {

		/** 項目 */
		private String feeItemName;

		/** 発生日 */
		private String feeDate;

		/** 報酬ステータス */
		private String feePaymentStatus;

		/** 消費税率 */
		private String taxRateType;

		/** 源泉徴収 */
		private String withholding;

		/** 報酬額（税込） */
		private BigDecimal feeAmountTaxIn;

		/** 請求書番号／精算書番号 */
		private String invoiceStatementNo;

		/** タイムチャージフラグ */
		private String feeTimeChargeFlg;

		/** タイムチャージ 単価（1h） */
		private BigDecimal hourPrice;

		/** タイムチャージ 時間 */
		private String workTimeMinute;

		/** メモ */
		private String feeMemo;

		/** 回収不能フラグ */
		private String uncollectibleFlg;

		/** 摘要 */
		private String sumText;

	}
}