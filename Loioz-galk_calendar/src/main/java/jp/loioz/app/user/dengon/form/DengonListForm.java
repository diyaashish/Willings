package jp.loioz.app.user.dengon.form;

import java.util.ArrayList;
import java.util.List;

import jp.loioz.dto.DengonFolderDto;
import lombok.Data;

/**
 * 伝言リスト画面のフォームクラス
 */
@Data
public class DengonListForm {

	/* カスタムフォルダリスト */
	private List<DengonFolderDto> customeFolderList = new ArrayList<DengonFolderDto>();

	/* 伝言リスト表示用DTOリスト */
	private List<DengonMessageListForm> dengonList = new ArrayList<DengonMessageListForm>();

	// 受信BOX内の伝言の未読件数
	int unreadCount = 0;

	// 下書きの件数
	int draftCount = 0;

	// フォルダが作成可能かの判定フラグ
	boolean creatableFolder;
}