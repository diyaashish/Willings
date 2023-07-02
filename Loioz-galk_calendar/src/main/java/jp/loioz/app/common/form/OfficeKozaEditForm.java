package jp.loioz.app.common.form;

import javax.validation.Valid;

import jp.loioz.dto.OfficeKozaDto;
import lombok.Data;

/**
 * 事務所口座編集画面用のフォームクラス
 */
@Data
public class OfficeKozaEditForm {

	@Valid
	/** 口座情報Dto */
	private OfficeKozaDto officeKozaDto = new OfficeKozaDto();
}
