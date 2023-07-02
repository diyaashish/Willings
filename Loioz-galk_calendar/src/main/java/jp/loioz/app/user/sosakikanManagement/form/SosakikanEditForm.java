package jp.loioz.app.user.sosakikanManagement.form;

import javax.validation.Valid;

import jp.loioz.dto.SosakikanEditDto;
import lombok.Data;

@Data
public class SosakikanEditForm {

	@Valid
	private SosakikanEditDto sosakikanEditData = new SosakikanEditDto();

	/**
	 * 新規登録モード判定
	 *
	 * @return
	 */
	public boolean isNew() {
		boolean newFlg = false;
		if (sosakikanEditData.getSosakikanId() == null) {
			newFlg = true;
		}
		return newFlg;
	}

}
