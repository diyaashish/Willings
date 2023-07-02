package jp.loioz.dto;

import lombok.Data;

/**
 * 分野一覧表示用のDtoクラス
 */
@Data
public class BunyaListDto {

	/** 分野ID */
	private Long bunyaId;
	
	/** 分野区分 */
	private String bunyaType;
	
	/** 分野名 */
	private String bunyaName;
	
	/** 表示順 */
	private Long dispOrder;
	
	/** 無効フラグ */
	private boolean disabledFlg;

}