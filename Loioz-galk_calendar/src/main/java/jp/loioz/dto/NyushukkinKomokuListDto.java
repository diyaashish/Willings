package jp.loioz.dto;

import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

@Data
public class NyushukkinKomokuListDto {

	/** 入出金SEQ */
	private Long nyushukkinKomokuId;

	/** 入出金タイプ */
	@Required
	private String nyushukkinType;

	/** 名称 */
	private String komokuName;

	/** 課税区分 */
	private String taxFlg;

	/** 表示順 */
	private Long dispOrder;

	/** バージョンNo */
	private Long versionNo;

	/** 削除済みかどうか */
	private boolean isDeleted;

	/** 無効かどうか */
	private boolean isDisabled;
}
