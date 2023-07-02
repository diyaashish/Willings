package jp.loioz.app.user.roomManagement.form;

import javax.validation.Valid;

import jp.loioz.dto.RoomEditDto;
import lombok.Data;

/**
 * 会議室編集画面のフォームクラス
 */
@Data
public class RoomEditForm {

	/** 会議室情報 */
	@Valid
	private RoomEditDto roomEditData = new RoomEditDto();

	/**
	 * 新規登録モード判定
	 *
	 * @return
	 */
	public boolean isNew() {
		boolean newFlg = false;
		if (roomEditData.getRoomId() == null) {
			newFlg = true;
		}
		return newFlg;
	}
}