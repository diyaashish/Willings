package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;
import lombok.Data;

@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class AzukarikinAnkenTantoListBean {

	/** 案件ID */
	private Long ankenId;

	/** アカウントSEQ */
	private Long accountSeq;

	/** アカウント名 */
	private String accountName;

	/** 担当種別 */
	private String tantoType;

	/** 担当種別枝番 */
	private Long tantoTypeBranchNo;

	/** 案件主担当フラグ */
	private String ankenMainTantoFlg;

}
