package jp.loioz.app.common.pdf.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * PN0004用の受け渡しデータオブジェクト
 */
@Data
public class P0004Data implements PdfData {

	/** 日付 */
	private String outputDate;

	/** 案件名 */
	private String ankenName;

	/** タイムチャージ合計 */
	private String timeChargeTotalMinutes;

	/** タイムチャージ合計 */
	private String timeChargeTotalAmount;

	/** タイムチャージ一覧 */
	private List<TimeChargeListItem> timeChargeList;

	/**
	 * タイムチャージ一覧
	 */
	@Data
	@AllArgsConstructor
	public static class TimeChargeListItem {

		/** 日付 */
		private String timeChargeDate;

		/** 活動内容 */
		private String detail;

		/** 時間（分） */
		private String workMinutes;

		/** 時間単価 */
		private String hourlyWage;

		/** 小計 */
		private String subTotal;

	}

}
