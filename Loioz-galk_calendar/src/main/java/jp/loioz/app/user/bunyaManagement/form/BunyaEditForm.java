package jp.loioz.app.user.bunyaManagement.form;

import javax.validation.Valid;

import jp.loioz.dto.BunyaEditDto;
import lombok.Data;

/**
 * 分野の設定編集画面のフォームクラス
 */
@Data
public class BunyaEditForm {

	/** 分野の編集用Dto */
	@Valid
	private BunyaEditDto bunyaEditData = new BunyaEditDto();

	/**
	 * 新規登録モード判定
	 *
	 * @return
	 */
	public boolean isNew() {
		boolean newFlg = false;
		if (bunyaEditData.getBunyaId() == null) {
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
		if (!bunyaEditData.isDisabledFlg()) {
			usingFlg = true;
		}
		return usingFlg;
	}
}