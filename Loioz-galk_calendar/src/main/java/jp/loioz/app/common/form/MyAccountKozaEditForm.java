package jp.loioz.app.common.form;

import javax.validation.Valid;

import jp.loioz.dto.MyAccountKozaDto;
import lombok.Data;

/**
 * 個人口座編集画面用のフォームクラス
 */
@Data
public class MyAccountKozaEditForm {

	@Valid
	/** 口座情報Dto */
	private MyAccountKozaDto myAccountKozaDto = new MyAccountKozaDto();
}
