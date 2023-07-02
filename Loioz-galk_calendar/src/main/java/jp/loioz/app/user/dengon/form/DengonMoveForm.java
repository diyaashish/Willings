package jp.loioz.app.user.dengon.form;

import java.util.ArrayList;
import java.util.List;

import jp.loioz.dto.DengonFolderDto;
import lombok.Data;

/**
 * 伝言移動画面のフォームクラス
 */
@Data
public class DengonMoveForm {

	/* 伝言seq */
	private Long dengonSeq;

	/* カスタムフォルダリスト */
	private List<DengonFolderDto> customeFolderList = new ArrayList<DengonFolderDto>();

	/* 送信メールフラグ */
	private Boolean sendMailFlg = false;

}