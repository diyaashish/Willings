package jp.loioz.app.common.pdf.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PN0001用の受け渡しデータオブジェクト
 */
@Data
public class P0001Data implements PdfData {

	/** デフォルト挿入文 */
	private static final String DEFAULT_SUB_TITLE = "下記の通り、ご請求申し上げます。";

	/** タイトル */
	private String title;

	/** 日付 */
	private String outputDate;

	/** 請求番号 */
	private String seikyuNo;

	/** 宛先名 */
	private String atesakiName;

	/** 宛先詳細 */
	private String atesakiDetail;

	/** 事務所名 */
	private String jimushoName;

	/** 弁護士名 */
	private String lawyerName;

	/** 印影画像 */
	private byte[] stamp;

	/** 送付元詳細 */
	private String sofumotoDetail;

	/** 挿入文 */
	private String subTitle = DEFAULT_SUB_TITLE;

	/** 案件名 */
	private String ankenName;

	/** 請求金額 */
	private String seikyuKingaku;

	/** 支払期日 */
	private String shiharaiLimitDate;

	/** 振込先 */
	private String paymentDestination;

	/** 預り金額一覧 */
	private List<AzukariKingakuListItem> azukariKingakuList;

	/** 預かり一覧：日付非表示 */
	private boolean hideAzukariDate;

	/** 預かり金額合計 */
	private String azukariKingakuTotal;

	/** 報酬・実費一覧 */
	private List<HoshuJippiKingakuListItem> hoshuJippiKingakuList;

	/** 報酬・実費一覧：日付非表示 */
	private boolean hideHoshuJippiDate;

	/** 報酬・実費：小計 */
	private String hoshuJippiSubTotal;

	/** 報酬・実費：消費税 */
	private String hoshuJippiTaxTotal;

	/** 報酬・実費：源泉徴収 */
	private String hoshuJippiGensenTotal;

	/** 報酬・実費：合計 */
	private String hoshuJippiTotal;

	/** 精算額 */
	private String seisanTotal;

	/** 10%対象 */
	private String tenPercentTotal;

	/** 10%対象（消費税） */
	private String tenPercentTaxTotal;

	/** 10%対象 */
	private boolean hasTenPercentTax;

	/** 8%対象 */
	private String eightPercentTotal;

	/** 8%対象（消費税） */
	private String eightPercentTaxTotal;

	/** 8%対象 */
	private boolean hasEightPercentTax;

	/** 備考欄 */
	private String remarks;

	/**
	 * 預り金一覧
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AzukariKingakuListItem {

		/** 日付 */
		private String azukariDate;

		/** 預り金名 */
		private String azukariKingakuName;

		/** 預り金詳細 */
		private String azukariKingakuDetail;

		/** 金額 */
		private String kingaku;

	}

	/**
	 * 報酬・実費一覧
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class HoshuJippiKingakuListItem {

		/** 日付 */
		private String hoshuJippiDate;

		/** 報酬・実費名 */
		private String hoshuJippiKingakuName;

		/** 報酬・実費詳細 */
		private String hoshuJippiKingakuDetail;

		/** 金額 */
		private String kingaku;

	}

}
