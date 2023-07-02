package jp.loioz.dto;

import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

@Data
@Entity
public class TrashBoxFileListDto {

	/** ファイル区分 */
	@Column(name = "file_kubun")
	private String fileKubun;

	/** ファイル構成管理ID */
	@Column(name = "file_configuration_management_id")
	private Long fileConfigurationManagementId;

	/** フォルダパス */
	@Column(name = "folder_path")
	private String folderPath;

	/** ファイル拡張子 */
	@Column(name = "file_extension")
	private String fileExtension;

	/** ファイル名 */
	@Column(name = "file_name")
	private String fileName;

	/** 削除操作アカウント連番 */
	@Column(name = "deleted_operation_account_seq")
	private Long deletedOperationAccountSeq;

	/** 削除日時(更新日) */
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	/** 削除ユーザー名 */
	@Column(name = "deleted_user_name")
	private String deletedUserName;

	/** ファイルサイズ */
	@Column(name = "file_size")
	private Long fileSize;

	/** 案件ID */
	@Column(name = "anken_id")
	private AnkenId ankenId;

	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 顧客ID */
	@Column(name = "customer_id")
	private CustomerId customerId;

	/** 顧客ID */
	@Column(name = "customer_name")
	private String customerName;

	/**
	 * 画面表示用の削除日時の文字列を取得する
	 * 
	 * @return 指定の形式に変換された削除日時の文字列
	 */
	public String getDeletedAtForDisplay() {
		return DateUtils.parseToString(this.deletedAt,
				DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM);
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

	/**
	 * 拡張子を取得する
	 * 
	 * @return 指定の形式に変換されたファイルサイズの文字列
	 */
	public String getExtension() {
		if (this.fileName == null) {
			return CommonConstant.BLANK;
		}
		int lastDotPosition = this.fileName.lastIndexOf(".");
		if (lastDotPosition != -1) {
			return this.fileName.substring(lastDotPosition + 1);
		} else {
			return null;
		}
	}
}
