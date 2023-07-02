package jp.loioz.app.user.dengon.form;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import jp.loioz.dto.DengonFolderDto;
import lombok.Data;

/***
 * カスタムフォルダー編集画面のフォームクラス
 */
@Data
public class DengonFolderEditForm {

	/*** 選択しているフォルダSEQ */
	private Long currentDengonFolderSeq;

	/*** カスタムフォルダーDto */
	@Valid
	private DengonFolderDto dto = new DengonFolderDto();

	/** カスタムフォルダの選択リスト */
	private List<DengonFolderDto> folderSelectList = new ArrayList<DengonFolderDto>();

	// 親フォルダの作成上限判定フラグ
	boolean creatableParentFolder = true;

	// 子フォルダの作成上限判定フラグ
	boolean creatableSubFolder = true;

	/** ごみ箱のフォルダ */
	boolean currentTrashedFlg = false;

	/**
	 * 新規登録モード判定
	 *
	 * @return
	 */
	public boolean isNew() {
		boolean newFlg = false;
		if (dto.getDengonFolderSeq() == null) {
			newFlg = true;
		}
		return newFlg;
	}

}