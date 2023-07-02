package jp.loioz.app.common.mvc.accgSendFilePopover.dto;

import jp.loioz.common.constant.CommonConstant.AccgDocFileType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 送付ファイル情報Dto
 */
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SendFileItemDto {

	/** 会計書類ファイルSEQ */
	private Long accgDocFileSeq;

	/** 会計書類ファイル種別 */
	private AccgDocFileType accgDocFileType;

	/** ファイル名 */
	private String fileName;

	/** 最終ダウンロード日時 */
	private String lastDownloadDateTime;

	/** ファイル種別のソート順番号の取得 */
	public int getFileDispOrder() {
		return this.accgDocFileType.getDispOrder();
	}

}
