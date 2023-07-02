package jp.loioz.app.common.form;

import javax.validation.Valid;

import jp.loioz.dto.KozaDto;
import lombok.Data;

/**
 * 共通口座編集画面用のフォームクラス
 */
@Data
public class GinkoKozaEditForm {

	@Valid
	/** 口座情報Dto */
	private KozaDto kozaDto = new KozaDto();

	/** 新規作成フラグ */
	private Boolean isNew = false;

	/** 利用停止フラグ */
	private Boolean isDeleted = false;

}