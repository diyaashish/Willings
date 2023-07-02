package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

/**
 * ファイル管理画面用アップロードファイル情報
 *
 */

@Entity
@Data
public class FileDownloadFileInfoDto {

	/** ファイル詳細情報管理ID */
	@Column(name = "file_detail_info_management_id")
	private Long fileDetailInfoManagementId;

	/** ファイル名 */
	@Column(name = "file_name")
	private String fileName;

	/** ファイル拡張子 */
	@Column(name = "file_extension")
	private String fileExtension;

	/** フォルダパス */
	@Column(name = "folder_path")
	private String folderPath;

	/** S3オブジェクトキー */
	@Column(name = "s3_object_key")
	private String s3ObjectKey;

	/** ファイル区分 */
	@Column(name = "file_kubun")
	private String fileKubun;

	/** ZIPフォルダパス（ダウンロードでzip圧縮するときのフォルダ名+ファイル名） */
	private String zipFolderPath;
}
