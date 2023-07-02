package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * グループ管理用のDtoクラス
 */
@Entity(naming= NamingType.SNAKE_LOWER_CASE)
@Data
public class GroupListDto {

	/** グループID */
	private Long groupId;

	/** グループ名 */
	private String groupName;

	/** 表示順 */
	private Long dispOrder;

	/** 作成日時 */
	private String createdAt;

	/** 更新日時 */
	private String updatedAt;

	/** バージョンNo */
	private Long versionNo;
}