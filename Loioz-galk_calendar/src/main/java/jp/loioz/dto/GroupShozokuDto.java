package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 所属グループアカウント画面のDtoクラス
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class GroupShozokuDto {

	/** グループID */
	private Long groupId;

	/** アカウントSEQ */
	private Long accountSeq;

	/** アカウント名 */
	private String accountName;

	/** アカウント種別 */
	private String accountType;

	/** 表示順 */
	private Long dispOrder;

	/** 所属フラグ */
	private boolean flgRegist;
}