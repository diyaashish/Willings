package jp.loioz.app.user.saibanshoManagement.form;

import javax.validation.Valid;

import jp.loioz.dto.SaibanshoEditDto;
import lombok.Data;

@Data
public class SaibanshoEditForm {

	/** 裁判所情報 */
	@Valid
	private SaibanshoEditDto saibanshoEditData = new SaibanshoEditDto();

	/**
	 * 新規登録モード判定
	 *
	 * @return
	 */
	public boolean isNew() {
		boolean newFlg = false;
		if (saibanshoEditData.getSaibanshoId() == null) {
			newFlg = true;
		}
		return newFlg;
	}
}