package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 部署所属ユーザー管理用のDto
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class BushoShozokuDto {

	/** アカウント連番 */
	private Long accountSeq;

	/** 部署ID */
	private Long bushoId;

	/** アカウント名 */
	private String accountName;

	/** アカウント種別 */
	private String accountType;

	/** 表示順 */
	private Long dispOrder;

	/** 所属フラグ */
	private boolean flgRegist;
}