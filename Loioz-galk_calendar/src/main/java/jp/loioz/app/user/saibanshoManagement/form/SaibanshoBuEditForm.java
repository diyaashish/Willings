package jp.loioz.app.user.saibanshoManagement.form;

import javax.validation.Valid;

import jp.loioz.dto.SaibanshoBuEditDto;
import lombok.Data;

@Data
public class SaibanshoBuEditForm {

	/** 裁判所部情報 */
	@Valid
	private SaibanshoBuEditDto saibanshoBuEditData = new SaibanshoBuEditDto();

	/**
	 * 新規登録モード判定
	 *
	 * @return
	 */
	public boolean isNew() {
		boolean newFlg = false;
		if (saibanshoBuEditData.getKeizokuBuId() == null) {
			newFlg = true;
		}
		return newFlg;
	}

}