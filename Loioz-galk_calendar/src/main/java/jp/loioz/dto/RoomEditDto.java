package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 会議室用のDto
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class RoomEditDto {

	/** 会議室ID */
	private Long roomId;

	/** 会議室名 */
	@Required
	@MaxDigit(max = 64, item = "会議室名")
	private String roomName;

	/** 表示順 */
	private String dispOrder;

	/** バージョンNo */
	private Long versionNo;
}