package jp.loioz.app.user.funcOldKaikeiSetting.dto;

import jp.loioz.common.constant.CommonConstant.DisabledFlg;
import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import lombok.Data;

/**
 * 入出金項目マスタ一覧のDto
 */
@Data
public class FuncOldKaikeiSettingListItemDto {

	/** 入出金項目ID */
	private Long nyushukkinKomokuId;

	/** 入出金種別タイプ */
	private NyushukkinType nyushukkinType;

	/** 入出金項目名 */
	private String komokuName;

	/** 課税フラグ */
	private TaxFlg taxFlg;

	/** 表示順 */
	private Long dispOrder;

	/** 無効フラグ */
	private DisabledFlg disabledFlg;

	/** 削除不可フラグ */
	private String undeletableFlg;

}
