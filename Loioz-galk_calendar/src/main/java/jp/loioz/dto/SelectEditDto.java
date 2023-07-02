package jp.loioz.dto;

import java.time.LocalDateTime;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 選択肢を管理するDtoクラス
 */
@Data
public class SelectEditDto {

	/** 選択肢SEQ */
	private Long selectSeq;

	/** 選択肢区分 */
	private String selectType;

	/** 選択肢名 */
	@Required
	@MaxDigit(max = 30)
	private String selectVal;

	/** 表示順 */
	private String dispOrder;

	/** 最終停止日時 */
	private LocalDateTime deletedAt;

	/** バージョンNo */
	private Long versionNo;
}