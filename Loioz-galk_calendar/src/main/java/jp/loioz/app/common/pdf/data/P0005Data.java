package jp.loioz.app.common.pdf.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PN0005用の受け渡しデータオブジェクト
 */
@Data
public class P0005Data implements PdfData {

	/** 一覧行数 (左右合計 60件 変更する際は、テンプレート側のスタイルも含め修正すること) */
	public static final Integer LIST_ROW_SIZE = 30;

	/** 日付 */
	private String outputDate;

	/** 案件名 */
	private String ankenName;

	/** 請求金額 */
	private String seikyuTotal;

	/** 分割回数 */
	private String installmentsCount;

	/** 振込先 */
	private String paymentDestination;

	/** 支払い計画一覧 1 ~ 30 */
	private List<StatementScheduleListItem> statementScheduleLeftList;

	/** 支払い計画一覧 31 ～ 60 */
	private List<StatementScheduleListItem> statementScheduleRightList;

	/**
	 * 実費一覧
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StatementScheduleListItem {

		/** 回数 */
		private Integer countNo;

		/** 振込期限 */
		private String limitDate;

		/** 金額 */
		private String kingaku;

		/** 残高 */
		private String balance;

	}

}
