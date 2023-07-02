package jp.loioz.common.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ファイルストレージ系の定数クラス
 */
public class FileStorageConstant {

	/** スラッシュ区切り文字 */
	public static final String DELIMITER = "/";

	/** 不等号区切り文字 */
	public static final String GT = "＞";

	/** S3ルートディレクトリ */
	private static final String BUCKET_ROOT_DIRECTORY = "tenant-storage";
	/** 変換元テナントディレクトリ名 */
	public static final String REPLACE_TENANT_DOMAIN_DIRECTORY = "replacement-tenant-domain";

	// 外部帳票用のディレクトリパス
	public static final String GAIBU_CHOHYO_DIR_PATH = "manage-storage/gaibu_chohyo_templates/";

	// -----------------------------------------------------------------------------
	// 外部帳票カテゴリ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum GaibuChohyo {
		CUSTOMER(1, null, "顧客"),
		CU_CATEGORY1(2, CUSTOMER, "顧客カテゴリ①"),
		CU_CA1_CATEGORY1(3, CU_CATEGORY1, "顧客カテゴリ①-1"),
		CU_CA1_CATEGORY2(3, CU_CATEGORY1, "顧客カテゴリ①-2"),
		CU_CATEGORY2(2, CUSTOMER, "顧客カテゴリ②"),
		ANKEN(1, null, "案件"),
		AN_CATEGORY1(2, ANKEN, "案件カテゴリ①"),
		AN_CATEGORY2(2, ANKEN, "案件カテゴリ②"),
		AN_CA2_CATEGORY1(3, AN_CATEGORY2, "案件カテゴリ②-1"),
		AN_CA2_CATEGORY2(3, AN_CATEGORY2, "案件カテゴリ②-2"),
		CATEGORY3(1, null, "カテゴリ③"),
		CA3_CATEGORY1(2, CATEGORY3, "カテゴリ③-1"),
		CA3_CA1_CATEGORY1(3, CA3_CATEGORY1, "カテゴリ③-1-1"),
		CA3_CA1_CATEGORY2(3, CA3_CATEGORY1, "カテゴリ③-1-2"),
		CA3_CATEGORY2(2, CATEGORY3, "カテゴリ③-2"),
		;// TODO 確認用の仮データ

		/** ディレクトリ階層 */
		private int hierarchy;

		/** 親ディレクトリ */
		private GaibuChohyo parentDir;

		/** ディレクトリ名 */
		private String dirName;

		/**
		 * ディレクトリ名からEnumを取得する
		 */
		public static GaibuChohyo of(String dirName) {
			return Arrays.stream(GaibuChohyo.values()).filter(e -> e.dirName.equals(dirName)).findFirst().orElse(null);
		}

		/**
		 * 子ディレクトリを持つか判定する
		 */
		public boolean hasChildDir() {
			return Arrays.stream(GaibuChohyo.values()).filter(e -> this.equals(e.parentDir)).findFirst().orElse(null) != null;
		}

		/**
		 * ディレクトリパスを取得
		 */
		public String getName() {

			List<String> dirNameList = new ArrayList<>();
			dirNameList.add(this.dirName);

			GaibuChohyo parentDir = this.getParentDir();

			while (parentDir != null) {

				// 配列の先頭に追加
				dirNameList.add(0, parentDir.getDirName());

				parentDir = parentDir.getParentDir();
			}

			return String.join(GT, dirNameList);
		}
	}

	// -----------------------------------------------------------------------------
	// 外部帳票SEQと登録カテゴリの対応をとるEnum
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum GaibuChohyoCategory {
		CUSTOMER(25, GaibuChohyo.CUSTOMER),
		CU_CATEGORY1(26, GaibuChohyo.CU_CATEGORY1),
		CU_CA1_CATEGORY1(27, GaibuChohyo.CU_CA1_CATEGORY1),
		CU_CA1_CATEGORY2(999, GaibuChohyo.CU_CA1_CATEGORY2),
		CU_CATEGORY2(999, GaibuChohyo.CU_CATEGORY2),
		ANKEN(30, GaibuChohyo.ANKEN),
		AN_CATEGORY1(29, GaibuChohyo.AN_CATEGORY1),
		AN_CATEGORY2(999, GaibuChohyo.AN_CATEGORY2),
		AN_CA2_CATEGORY1(999, GaibuChohyo.AN_CA2_CATEGORY1),
		AN_CA2_CATEGORY2(28, GaibuChohyo.AN_CA2_CATEGORY2),
		CATEGORY(999, GaibuChohyo.CATEGORY3),
		CA_CATEGORY1(999, GaibuChohyo.CA3_CATEGORY1),
		CA_CA1_CATEGORY1(999, GaibuChohyo.CA3_CA1_CATEGORY1),
		CA_CA1_CATEGORY2(999, GaibuChohyo.CA3_CA1_CATEGORY2),
		CA_CATEGORY2(999, GaibuChohyo.CA3_CATEGORY2),
		;// TODO 確認用の仮データ

		/** 帳票SEQ */
		private int chohyoSeq;

		/** 登録カテゴリのEnum */
		private GaibuChohyo category;

		/**
		 * ディレクトリ物理名リストから帳票SEQリストを取得する
		 */
		public static List<Integer> getChohyoSeqList(List<String> dirPathList) {
			List<Integer> chohyoSeqList = new ArrayList<Integer>();

			for (String dirPath : dirPathList) {
				chohyoSeqList.addAll(Arrays.stream(GaibuChohyoCategory.values()).filter(e -> e.category.equals(GaibuChohyo.of(dirPath)))
						.map(GaibuChohyoCategory::getChohyoSeq).collect(Collectors.toList()));
			}

			return new ArrayList<Integer>(new HashSet<>(chohyoSeqList));
		}
	}

	// -----------------------------------------------------------------------------
	// 掲示板のS3のアップロードファイルディレクトリ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum S3KeijibanDir implements S3FileDirEnum {

		TENANT_STORAGE(1, null, BUCKET_ROOT_DIRECTORY),
		TE_DOMAIN(2, TENANT_STORAGE, REPLACE_TENANT_DOMAIN_DIRECTORY),
		TE_DO_KEIJIBAN(3, TE_DOMAIN, "keijiban");

		/** ディレクトリ階層 */
		private int hierarchy;

		/** 親ディレクトリ */
		private S3KeijibanDir parentDir;

		/** ディレクトリ名 */
		private String dirName;

	}

	// -----------------------------------------------------------------------------
	// ファイル管理画面 S3のアップロードファイルディレクトリ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum S3FileManagementDir implements S3FileDirEnum {

		TENANT_STORAGE(1, null, BUCKET_ROOT_DIRECTORY),
		TE_DOMAIN(2, TENANT_STORAGE, REPLACE_TENANT_DOMAIN_DIRECTORY),
		TE_DO_FILE_MANAGEMENT(3, TE_DOMAIN, "file-management");

		/** ディレクトリ階層 */
		private int hierarchy;

		/** 親ディレクトリ */
		private S3FileManagementDir parentDir;

		/** ディレクトリ名 */
		private String dirName;

	}

	// -----------------------------------------------------------------------------
	// 問い合わせ管理画面 S3のアップロードファイルディレクトリ
	// -----------------------------------------------------------------------------
	@Getter
	@AllArgsConstructor
	public enum S3ToiawaseDir implements S3FileDirEnum {

		TENANT_STORAGE(1, null, BUCKET_ROOT_DIRECTORY),
		TE_DOMAIN(2, TENANT_STORAGE, REPLACE_TENANT_DOMAIN_DIRECTORY),
		TE_DO_TOIAWASE(3, TE_DOMAIN, "toiawase");

		/** ディレクトリ階層 */
		private int hierarchy;

		/** 親ディレクトリ */
		private S3ToiawaseDir parentDir;

		/** ディレクトリ名 */
		private String dirName;

	}

	// -----------------------------------------------------------------------------
	// 会計機能 S3のアップロードファイルディレクトリ
	// -----------------------------------------------------------------------------
	/**
	 * 会計機能 S3のアップロードファイルディレクトリ
	 */
	@Getter
	@AllArgsConstructor
	public enum S3AccountingDir implements S3FileDirEnum {

		TENANT_STORAGE(1, null, BUCKET_ROOT_DIRECTORY),
		TE_DOMAIN(2, TENANT_STORAGE, REPLACE_TENANT_DOMAIN_DIRECTORY),
		TE_DO_ACCOUNTING(3, TE_DOMAIN, "accounting");

		/** ディレクトリ階層 */
		private int hierarchy;

		/** 親ディレクトリ */
		private S3AccountingDir parentDir;

		/** ディレクトリ名 */
		private String dirName;

	}

	/**
	 * S3Fileディレクトの共通Enum
	 *
	 */
	private interface S3FileDirEnum {

		/** 親ディレクトリ */
		S3FileDirEnum getParentDir();

		/** ディレクトリ名 */
		String getDirName();

		/** ディレクトリパスの取得 */
		default String getPath() {

			List<String> dirNameList = new ArrayList<>();
			dirNameList.add(getDirName());

			S3FileDirEnum parentDir = getParentDir();

			while (parentDir != null) {

				// 配列の先頭に追加
				dirNameList.add(0, parentDir.getDirName());

				parentDir = parentDir.getParentDir();
			}

			return String.join(DELIMITER, dirNameList) + DELIMITER;
		}
	}

}