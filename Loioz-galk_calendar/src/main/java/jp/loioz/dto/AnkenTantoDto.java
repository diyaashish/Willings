package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenTantoDto {

	/** 案件ID */
	private Long ankenId;

	/** アカウントSEQ */
	private Long accountSeq;

	/** 担当種別枝番 */
	private Long tantoTypeBranchNo;

	/** 案件主担当フラグ */
	private String ankenMainTantoFlg;

	/** 担当種別 */
	private String tantoType;

	/** アカウント色 */
	private String accountColor;

	/** アカウント種別 */
	private String accountType;

}