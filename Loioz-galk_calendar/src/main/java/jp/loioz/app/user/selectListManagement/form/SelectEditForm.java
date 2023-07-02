package jp.loioz.app.user.selectListManagement.form;

import javax.validation.Valid;

import jp.loioz.dto.SelectEditDto;
import lombok.Data;

/**
 * 選択肢マスタ編集画面のフォームクラス
 */
@Data
public class SelectEditForm {

	/** 選択肢Dto */
	@Valid
	private SelectEditDto selectEditData = new SelectEditDto();

	/**
	 * 新規登録モード判定
	 *
	 * @return
	 */
	public boolean isNew() {
		boolean newFlg = false;
		if (selectEditData.getSelectSeq() == null) {
			newFlg = true;
		}
		return newFlg;
	}

	/**
	 * 利用停止判定
	 *
	 * @return
	 */
	public boolean isUsing() {
		boolean usingFlg = false;
		if (selectEditData.getDeletedAt() == null) {
			usingFlg = true;
		}
		return usingFlg;
	}
}