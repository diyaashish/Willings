package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 会議室一覧用のDto
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class RoomListDto {

	/** 会議室ID */
	private Long roomId;

	/** 会議室名 */
	private String roomName;

	/** 表示順 */
	private Long dispOrder;

	/** バージョンNo */
	private Long versionNo;
}