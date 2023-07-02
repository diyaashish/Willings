package jp.loioz.bean;

import java.time.LocalDateTime;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 案件相手方情報Bean
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenKanyoshaBean {

	/** 案件ID */
	private Long ankenId;

	/** 名簿ID */
	private Long personId;

	/** 関与者Seq */
	private Long kanyoshaSeq;

	/** 代理人関与者Seq */
	private Long relatedKanyoshaSeq;

	/** 関与者種別 */
	private String kanyoshaType;

	/** 表示順 */
	private Long dispOrder;

	/** 代理フラグ */
	private String dairiFlg;

	/** 関与者名 */
	private String kanyoshaName;

	/** 登録日時 */
	private LocalDateTime createdAt;

}
