package jp.loioz.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ChohyoConstatnt {

	// ディレクト名
	private static final String NAIBU_DIR_PATH = "naibu/";
	private static final String PDF_FONTS_DIR_PATH = "fonts/";

	// 帳票
	public static final String SEISANSHO = "精算書";
	public static final String SEIKYUSHO = "請求書";
	public static final String SHUKKINMEISAISHO = "出金明細書";
	public static final String SHIHARAIKEIKAKUHYO = "支払計画表";
	public static final String TIMECHARGEKEISANSHO = "タイムチャージ計算書";

	// 精算書
	public static final String JP_EN = "円";

	// 敬称
	public static final String KEISHO_SAMA = "様";
	public static final String KEISHO_ONCHU = "御中";
	public static final String ATE = "宛";

	// -----------------------------------------------------------------------------
	// Excelテンプレート(帳票)
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum OutputWordTemplate implements DefaultEnum {
		// ■ルール
		// 内部帳票：W + N + 連番(0埋め4桁)
		// 預かり品一覧画面
		WORD_NAIBU_1("WN0001", NAIBU_DIR_PATH, "受領証", ".docx"),
		WORD_NAIBU_2("WN0002", NAIBU_DIR_PATH, "預り証", ".docx"),

		// 送付書（一般）
		WORD_NAIBU_3("WN0003", NAIBU_DIR_PATH, "送付書(一般)", ".docx"),
		// 送付書（FAX）
		WORD_NAIBU_4("WN0004", NAIBU_DIR_PATH, "送付書(FAX)", ".docx"),
		// 送付書（委任契約書）
		WORD_NAIBU_5("WN0005", NAIBU_DIR_PATH, "送付書(委任契約書)", ".docx"),
		// 送付書（一般）- 捜査機関情報、公判担当検察官情報
		WORD_NAIBU_6("WN0006", NAIBU_DIR_PATH, "送付書(一般)", ".docx"),
		// 送付書（FAX）- 捜査機関情報、公判担当検察官情報
		WORD_NAIBU_7("WN0007", NAIBU_DIR_PATH, "送付書(FAX)", ".docx"),

		// 裁判管理（民事）
		WORD_NAIBU_8("WN0008", NAIBU_DIR_PATH, "口頭弁論期日請書", ".docx"),
		// 裁判管理（刑事）
		WORD_NAIBU_9("WN0009", NAIBU_DIR_PATH, "公判期日請書", ".docx"),

		// 外部帳票：W + G + 連番(0埋め4桁)

		;

		/** コード */
		private String cd;

		/** ディレクトリ名 */
		private String dirPath;

		/** ファイル名称 */
		private String val;

		/** 拡張子 */
		private String fileType;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static OutputWordTemplate of(String cd) {
			return DefaultEnum.getEnum(OutputWordTemplate.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// Excelテンプレート(帳票)
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum OutputExcelTemplate implements DefaultEnum {
		// ■ルール
		// 内部帳票：E + N + 連番(0埋め4桁)
		EXCEL_NAIBU_1("EN0001", NAIBU_DIR_PATH, "業務履歴", ".xlsx"),
		EXCEL_NAIBU_2("EN0002", NAIBU_DIR_PATH, "出金予定一覧", ".xlsx"),
		EXCEL_NAIBU_3("EN0003", NAIBU_DIR_PATH, "入金予定一覧", ".xlsx"),
		EXCEL_NAIBU_4("EN0004", NAIBU_DIR_PATH, "未収入金一覧", ".xlsx"),
		EXCEL_NAIBU_5("EN0005", NAIBU_DIR_PATH, "報酬明細", ".xlsx"),
		EXCEL_NAIBU_6("EN0006", NAIBU_DIR_PATH, "入出金明細", ".xlsx"),
		EXCEL_NAIBU_7("EN0007", NAIBU_DIR_PATH, "支払計画表", ".xlsx"),
		EXCEL_NAIBU_8("EN0008", NAIBU_DIR_PATH, "タイムチャージ計算書", ".xlsx"),
		EXCEL_NAIBU_9("EN0009", NAIBU_DIR_PATH, "精算書", ".xlsx"),
		EXCEL_NAIBU_10("EN0010", NAIBU_DIR_PATH, "案件情報", ".xlsx"),
		EXCEL_NAIBU_11("EN0011", NAIBU_DIR_PATH, "タスク一覧", ".xlsx"),
		EXCEL_NAIBU_12("EN0012", NAIBU_DIR_PATH, "名簿（すべて）", ".xlsx"),
		EXCEL_NAIBU_13("EN0013", NAIBU_DIR_PATH, "顧客名簿（個人）", ".xlsx"),
		EXCEL_NAIBU_14("EN0014", NAIBU_DIR_PATH, "顧客名簿（企業・団体）", ".xlsx"),
		EXCEL_NAIBU_15("EN0015", NAIBU_DIR_PATH, "民事裁判", ".xlsx"),
		EXCEL_NAIBU_16("EN0016", NAIBU_DIR_PATH, "刑事裁判", ".xlsx"),
		EXCEL_NAIBU_17("EN0017", NAIBU_DIR_PATH, "弁護士名簿", ".xlsx"),
		EXCEL_NAIBU_18("EN0018", NAIBU_DIR_PATH, "顧客名簿", ".xlsx"),
		EXCEL_NAIBU_20("EN0020", NAIBU_DIR_PATH, "報酬明細", ".xlsx"),
		EXCEL_NAIBU_21("EN0021", NAIBU_DIR_PATH, "預り金／実費明細", ".xlsx"),
		EXCEL_NAIBU_22("EN0022", NAIBU_DIR_PATH, "報酬管理", ".xlsx"),
		EXCEL_NAIBU_23("EN0023", NAIBU_DIR_PATH, "預り金／実費管理", ".xlsx"),

		// 外部帳票：E + G + 連番(0埋め4桁)
		;

		/** コード */
		private String cd;

		/** ディレクトリ名 */
		private String dirPath;

		/** ファイル名称 */
		private String val;

		/** 拡張子 */
		private String fileType;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static OutputExcelTemplate of(String cd) {
			return DefaultEnum.getEnum(OutputExcelTemplate.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// CSV(帳票)
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum OutputCsvTemplate implements DefaultEnum {
		// ■ルール
		// 内部帳票：C + N + 連番(0埋め4桁)
		CSV_NAIBU_1("CN0001", "案件情報", ".csv"),
		CSV_NAIBU_2("CN0002", "名簿（すべて）", ".csv"),
		CSV_NAIBU_3("CN0003", "顧客名簿（個人）", ".csv"),
		CSV_NAIBU_4("CN0004", "顧客名簿（企業・団体）", ".csv"),
		CSV_NAIBU_5("CN0005", "民事裁判", ".csv"),
		CSV_NAIBU_6("CN0006", "刑事裁判", ".csv"),
		CSV_NAIBU_7("CN0007", "弁護士名簿", ".csv"),
		CSV_NAIBU_8("CN0008", "顧客名簿", ".csv"),
		CSV_NAIBU_9("CN0009", "顧問取引先", ".csv"),
		CSV_NAIBU_10("CN0010", "筆まめ_名簿（すべて）", ".csv"),
		CSV_NAIBU_11("CN0011", "筆まめ_顧客名簿（個人）", ".csv"),
		CSV_NAIBU_12("CN0012", "筆まめ_顧客名簿（企業・団体）", ".csv"),
		CSV_NAIBU_13("CN0013", "筆まめ_弁護士名簿", ".csv"),
		CSV_NAIBU_14("CN0014", "筆まめ_顧客名簿", ".csv"),
		CSV_NAIBU_15("CN0015", "筆まめ_顧問取引先", ".csv"),
		CSV_NAIBU_16("CN0016", "報酬管理", ".csv"),
		CSV_NAIBU_17("CN0017", "預り金／実費管理", ".csv"),

		// 外部帳票：C + G + 連番(0埋め4桁)
		;

		/** コード */
		private String cd;

		/** ファイル名称 */
		private String val;

		/** 拡張子 */
		private String fileType;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static OutputCsvTemplate of(String cd) {
			return DefaultEnum.getEnum(OutputCsvTemplate.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// PDF(帳票)
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum OutputPdfTemplate implements DefaultEnum {
		// ■ルール
		// 帳票：P + 連番(0埋め4桁)
		PDF_1("P0001", "請求書", ".jrxml", ".pdf"),
		PDF_2("P0002", "精算書", ".jrxml", ".pdf"),
		PDF_3("P0003", "実費明細書", ".jrxml", ".pdf"),
		PDF_4("P0004", "タイムチャージ計算書", ".jrxml", ".pdf"),
		PDF_5("P0005", "分割予定表", ".jrxml", ".pdf"),;

		/** コード */
		private String cd;

		/** ファイル名称 */
		private String val;

		/** テンプレート拡張子 */
		private String tempFileType;

		/** 出力拡張子 */
		private String outputFileType;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static OutputPdfTemplate of(String cd) {
			return DefaultEnum.getEnum(OutputPdfTemplate.class, cd);
		}
	}

	// -----------------------------------------------------------------------------
	// PDF(帳票)
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum OutputPdfFonts implements DefaultEnum {
		// ■ルール
		BIZ_UDP_MINCHO("1", PDF_FONTS_DIR_PATH, "BIZUDP明朝", "bizudpm", ".ttf"),
		IPA_EX_MINCHO("2", PDF_FONTS_DIR_PATH, "IPAex明朝", "ipaexm", ".ttf"),;

		/** コード */
		private String cd;

		/** ファイルパス名称 */
		private String dir;

		/** フォントファミリー名 */
		private String val;

		/** ファイル名 */
		private String fileName;

		/** 拡張子 */
		private String fileType;

		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static OutputPdfFonts of(String cd) {
			return DefaultEnum.getEnum(OutputPdfFonts.class, cd);
		}
	}

}
