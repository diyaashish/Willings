package jp.loioz.app.user.fileManagement.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.dto.FileUploadFileInfoDto;
import lombok.Data;

/**
 * ファイル管理画面ファイルアップロード用Form
 */
@Data
public class FileListUploadForm {

	/** ルートフォルダ関連情報管理ID */
	private Long rootFolderRelatedInfoManagementId;

	/** アップロードファイル情報 */
	private List<FileUploadFileInfoDto> uploadFileInfo;

	/** アップロード時のルートフォルダ以降のフォルダパス */
	private String currentFolderPath;

	/** アップロード時のファイル構成管理ID */
	private Long currentFileConfigurationManagementId;

	/** アップロード時のファイル重複有無 */
	private boolean fileDuplicate;

	/** アップロード時のプリチェック結果 */
	private boolean preCheckResult;

	/** アップロード時の処理を継続するかの有無 */
	private boolean fileUploadContinue;

	/**
	 * 画面側返却用
	 */
	@Data
	public class JsonResponseData {

		/** アップロードの成否 */
		private boolean isSuccess;

		/** エラーコード */
		private String errorCode;

		/** メッセージ */
		private String message;

		/** アップロード時の処理を継続するかの有無 */
		private boolean fileUploadContinue;

		/** ファイル名重複で除外したファイル名リスト */
		private List<String> dupFileList;

		/** 処理に失敗したファイルのUUID */
		private List<String> failerFileUuidList = Collections.emptyList();

		/** アップロードファイルが上書き可能か */
		private boolean canOverWrite;


	}
}
