package jp.loioz.app.global.download.dto;

import jp.loioz.common.constant.CommonConstant.AccgDocFileType;
import lombok.Data;

/**
 * ダウンロードファイル
 */
@Data
public class DownloadFileDto {

	/** 発行日 */
	private String issueDate;

	/** 会計書類-送付ファイルSEQ */
	private Long accgDocActSendFileSeq;

	/** 会計書類-送付種別 */
	private AccgDocFileType accgDocFileType;

	/** ファイル名 */
	private String fileName;

	/** ダウンロード済フラグ */
	private boolean isDownloaded;

	/** ダウンロード日時 */
	private String downloadDate;

	/** ファイル種別のソート番号 */
	public int fileTypeSortOrder() {
		return this.accgDocFileType.getDispOrder();
	}

}
