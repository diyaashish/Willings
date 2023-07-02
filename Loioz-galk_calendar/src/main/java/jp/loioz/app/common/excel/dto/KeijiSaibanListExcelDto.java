package jp.loioz.app.common.excel.dto;

import java.util.List;

import lombok.Data;

/**
 * 刑事裁判一覧用のExcelDto
 */
@Data
public class KeijiSaibanListExcelDto {

	/** 選択中の画面名 */
	private String selectedAnkenListMenu;

	/** 出力日 */
	private String outPutRange;

	/** Excelに出力する裁判一覧データ */
	private List<ExcelKeijiSaibanListRowData> saibanListDtoList;

	/**
	 * 刑事裁判一覧の1行分のデータ
	 */
	@Data
	public static class ExcelKeijiSaibanListRowData {

		/** 裁判SEQ */
		private String saibanSeq = "";

		/** 案件ID */
		private String ankenId = "";

		/** 案件名 */
		private String ankenName = "";

		/** 分野名 */
		private String bunyaName = "";

		/** 裁判所 */
		private String saibanshoNameMei = "";

		/** 事件番号 （令和X年（X）第XXX号） */
		private String jikenNumber = "";

		/** 事件名 */
		private String jikenName = "";

		/** 名簿ID */
		private String personId = "";

		/** 顧客ID */
		private String customerId = "";

		/** 顧客姓＋名 */
		private String customerName = "";

		/** 顧客数（外〇名） */
		private String numberOfCustomer = "";

		/** 裁判手続き名 */
		private String saibanStatusName = "";

		/** 検察庁 */
		private String kensatsuchoNameMei = "";

		/** 担当弁護士１ */
		private String tantoLawyerName1 = "";

		/** 担当弁護士２ */
		private String tantoLawyerName2 = "";

		/** 担当弁護士３ */
		private String tantoLawyerName3 = "";

		/** 担当弁護士４ */
		private String tantoLawyerName4 = "";

		/** 担当弁護士５ */
		private String tantoLawyerName5 = "";

		/** 担当弁護士６ */
		private String tantoLawyerName6 = "";

		/** 担当事務１ */
		private String tantoJimuName1 = "";

		/** 担当事務２ */
		private String tantoJimuName2 = "";

		/** 担当事務３ */
		private String tantoJimuName3 = "";

		/** 担当事務４ */
		private String tantoJimuName4 = "";

		/** 担当事務５ */
		private String tantoJimuName5 = "";

		/** 担当事務６ */
		private String tantoJimuName6 = "";

		/** 申立日／起訴日 */
		private String saibanStartDateFormat = "";

		/** 終了日／判決日 */
		private String saibanEndDateFormat = "";

		/** 担当案件フラグ 1:担当 */
		private String tantoAnkenFlg = "";
	}
}