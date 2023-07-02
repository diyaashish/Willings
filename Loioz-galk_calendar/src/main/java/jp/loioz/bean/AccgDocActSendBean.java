package jp.loioz.bean;

import java.time.LocalDateTime;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 会計書類対応-送付情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AccgDocActSendBean {

	/** 会計書類対応送付SEQ */
	private Long accgDocActSendSeq;

	/** 会計書類対応SEQ */
	private Long accgDocActSeq;

	/** 送信種別 */
	private String sendType;

	/** 会計書類SEQ */
	private Long accgDocSeq;

	/** 対応種別 */
	private String actType;

	/** 対応アカウントID */
	private Long actBy;

	/** 対応日時 */
	private LocalDateTime actAt;
}