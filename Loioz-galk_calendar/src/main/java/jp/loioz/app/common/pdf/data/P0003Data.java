package jp.loioz.app.common.pdf.data;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PN0003用の受け渡しデータオブジェクト
 */
@Data
public class P0003Data implements PdfData {

	/** 日付 */
	private String outputDate;

	/** 案件名 */
	private String ankenName;

	/** 実費一覧 */
	private List<JippiListItem> jippiList;

	/** 入金合計 */
	private String nyukinTotal;

	/** 出金合計 */
	private String shukkinTotal;

	/** 入出金合計 */
	private String nyushukkinTotal;

	/**
	 * 実費一覧
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class JippiListItem {

		/** 日付 */
		private String jippiDate;

		/** 項目名 */
		private String komokuName;

		/** 入金額 */
		private String nyukinGaku;

		/** 出金額 */
		private String shukkinGaku;

		/** 摘要 */
		private String tekiyo;

		/** 状況 */
		private String status;

	}

}
