package jp.loioz.dto;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 分野の編集用のDtoクラス
 */
@Data
public class BunyaEditDto {

	/** 分野ID */
	private Long bunyaId;

	/** 分野区分 */
	@Required
	private String bunyaType;

	/** 分野名 */
	@Required
	@MaxDigit(max = 30)
	private String bunyaName;

	/** 表示順 */
	private Long dispOrder;

	/** 無効フラグ */
	private boolean disabledFlg;

}