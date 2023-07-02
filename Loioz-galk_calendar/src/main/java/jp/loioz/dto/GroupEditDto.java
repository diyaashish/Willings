package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * グループ管理用のDtoクラス
 */
@Entity(naming= NamingType.SNAKE_LOWER_CASE)
@Data
public class GroupEditDto {

	/** グループID */
	private Long groupId;

	/** グループ名 */
	@Required
	@MaxDigit(max = 128, item = "グループ名")
	private String groupName;

	/** 表示順 */
	@Required
	@Numeric
	@MinNumericValue(min = 1, item = "表示順")
	private String dispOrder;

	/** バージョンNo */
	private Long versionNo;
}