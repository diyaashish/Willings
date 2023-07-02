package jp.loioz.bean;

import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 案件分野情報Bean
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenBunyaBean {

	/** 案件ID */
	private Long ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野ID */
	private Long bunyaId;

	/** 案件登録日 */
	private LocalDate ankenCreatedDate;

	/** 区分 */
	private String ankenType;

	/** 事案概要・方針 */
	private String jianSummary;

	/** 分野区分 */
	private String bunyaType;

	/** 分野名 */
	private String bunyaName;

}
