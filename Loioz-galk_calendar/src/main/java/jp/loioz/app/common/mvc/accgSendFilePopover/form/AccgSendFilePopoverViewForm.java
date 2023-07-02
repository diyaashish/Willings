package jp.loioz.app.common.mvc.accgSendFilePopover.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.common.mvc.accgSendFilePopover.dto.SendFileItemDto;
import jp.loioz.common.constant.CommonConstant.AccgDocSendType;
import lombok.Data;

/**
 * 会計送付ファイルポップオーバーの画面表示用オブジェクト
 */
@Data
public class AccgSendFilePopoverViewForm {

	/** 会計書類送付SEQ */
	private Long accgDocActSendSeq;

	/** 会計書類送付種別 */
	private AccgDocSendType accgDocSendType;

	/** 件名 */
	private String sendSubject;

	/** To */
	private String sendTo;

	/** CC */
	private List<String> sendCc = Collections.emptyList();

	/** BCC */
	private List<String> sendBcc = Collections.emptyList();

	/** ファイル情報 */
	private List<SendFileItemDto> fileItemList = Collections.emptyList();

	/** WEB共有によって作成されたデータの場合 */
	public boolean isWebSend() {
		return AccgDocSendType.WEB == this.accgDocSendType;
	}

	/** 送付済みに変更によって作成されたデータの場合 */
	public boolean isChangeToSend() {
		return AccgDocSendType.CHANGE_TO_SEND == this.accgDocSendType;
	}

}
