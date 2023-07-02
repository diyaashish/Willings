package jp.loioz.app.common.excel.dto;

import java.util.List;

import lombok.Data;

/**
 * 案件一覧用のExcelDto
 */
@Data
public class AnkenListExcelDto {

	/** 選択中の画面名 */
	private String selectedAnkenListMenu;

	/** 出力日 */
	private String outPutRange;

	/** Excelに出力する案件一覧データ */
	private List<ExcelAnkenListRowData> ankenListDtoList;

	/**
	 * 案件一覧の1行分のデータ
	 */
	@Data
	public static class ExcelAnkenListRowData {

		/** 案件ID */
		private String ankenId = "";

		/** 案件名 */
		private String ankenName = "";

		/** 分野名 */
		private String bunyaName = "";

		/** 案件登録日 */
		private String ankenCreatedDateFormat = "";

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

		/** 担当案件フラグ 1:担当 */
		private String tantoAnkenFlg = "";

		/** 顧客ID */
		private String customerId = "";

		/** 名簿ID */
		private String personId = "";

		/** 顧客姓＋名 */
		private String customerName = "";

		/** 案件ステータス名 */
		private String ankenStatusName = "";

		/** 顧客登録日 */
		private String customerCreatedDateFormat = "";

		/** 受任日 */
		private String juninDateFormat = "";

		/** 相手方名 */
		private String aitegataName = "";

		/** 相手方数 外〇名 */
		private String numberOfAitegata = "";
	}
}