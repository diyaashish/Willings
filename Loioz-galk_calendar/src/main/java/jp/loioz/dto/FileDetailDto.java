package jp.loioz.dto;

import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

@Entity
@Data
public class FileDetailDto {

	/** ファイル構成管理ID */
	@Column(name = "file_configuration_management_id")
	private Long fileConfigurationManagementId;

	/** ファイル詳細情報管理ID */
	@Column(name = "file_detail_info_management_id")
	private Long fileDetailInfoManagementId;

	/** ルートフォルダ関連情報管理ID */
	@Column(name = "root_folder_related_info_management_id")
	private Long rootFolderRelatedInfoManagementId;

	/** ファイル区分 */
	@Column(name = "file_kubun")
	private String fileKubun;

	/** ファイル名 */
	@Column(name = "file_name")
	private String fileName;

	/** ファイル拡張子 */
	@Column(name = "file_extension")
	private String fileExtension;

	/** ファイルタイプ */
	@Column(name = "file_type")
	private String fileType;

	/** フォルダパス */
	@Column(name = "folder_path")
	private String folderPath;

	/** ファイル作成日時 */
	@Column(name = "file_created_datetime")
	private LocalDateTime fileCreatedDateTime;

	/** アップロード日時 */
	@Column(name = "upload_datetime")
	private LocalDateTime uploadDateTime;

	/** アップロードユーザー */
	@Column(name = "upload_user")
	private String uploadUser;

	/** ファイルサイズ */
	@Column(name = "file_size")
	private Long fileSize;

	/** 閲覧制限 */
	@Column(name = "view_limit")
	private Long viewLimit;

	/** 案件ID */
	@Column(name = "anken_id")
	private AnkenId ankenId;

	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 顧客ID */
	@Column(name = "customer_id")
	private CustomerId customerId;

	/** 顧客名 */
	@Column(name = "customer_name")
	private String customerName;

	/** 表示優先順位 */
	@Column(name = "display_priority")
	private int displayPriority;

	/** 配下のフォルダ表示制限を行うフラグ **/
	private boolean dispLimit = false;

	/** 制限をかけられるかの制限を行うフラグ **/
	private boolean ableLimit = false;

	/**
	 * 画面表示用のファイル作成日時の文字列を取得する
	 * 
	 * @return 指定の形式に変換されたファイル作成日時の文字列
	 */
	public String getFileCreatedDatetimeForDisplay() {
		return DateUtils.parseToString(this.fileCreatedDateTime, DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM);
	}

	/**
	 * 画面表示用のアップロード日時の文字列を取得する
	 * 
	 * @return 指定の形式に変換されたアップロード日時の文字列
	 */
	public String getUploadDatetimeForDisplay() {
		if (this.uploadDateTime == null) {
			return CommonConstant.BLANK;
		}
		return DateUtils.parseToString(this.uploadDateTime, DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM);
	}

	/**
	 * 画面表示用のファイルサイズの文字列を取得する
	 * 
	 * @return 指定の形式に変換されたファイルサイズの文字列
	 */
	public String getFileSizeForDisplay() {
		if (this.fileSize == null) {
			return CommonConstant.BLANK;
		}
		if (this.fileSize / 1024L / 1024L / 1024L > 0) {
			return String.valueOf(this.fileSize / 1024L / 1024L / 1024L) + "GB";
		} else if (this.fileSize / 1024L / 1024L > 0) {
			return String.valueOf(this.fileSize / 1024L / 1024L) + "MB";
		} else if (this.fileSize / 1024L > 0) {
			return String.valueOf(this.fileSize / 1024L) + "KB";
		} else {
			return String.valueOf(this.fileSize) + "byte";
		}
	}

}
