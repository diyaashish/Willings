package jp.loioz.dto;

import lombok.Data;

/**
 * 選択肢を管理するDtoクラス
 */
@Data
public class SelectListDto {

	/** 選択肢SEQ */
	private Long selectSeq;

	/** 選択肢区分 */
	private String selectType;

	/** 表示順 */
	private Long dispOrder;

	/** 選択肢名 */
	private String selectVal;

	/** バージョンNo */
	private Long versionNo;

	/** 削除済みかどうか */
	private boolean isDeleted;

}