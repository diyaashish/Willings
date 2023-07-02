package jp.loioz.bean.userHeader;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * ヘッダー検索：裁判一覧Bean
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class UserHeaderSearchSaibanBean {

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 案件ID */
	private Long ankenId;

	/** 裁判連番No */
	private Long saibanBranchNo;

	/** 分野ID */
	private Long bunyaId;

	/** 分野名 */
	private String bunyaName;

	/** 分野種別 */
	private String bunyaType;

	/** 案件種別 */
	private String ankenType;

}
