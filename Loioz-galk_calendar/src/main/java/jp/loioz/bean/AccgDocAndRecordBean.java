package jp.loioz.bean;

import java.math.BigDecimal;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 会計書類＋取引実績Bean
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AccgDocAndRecordBean {

	/** 会計書類SEQ */
	private Long accgDocSeq;

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	/** ドキュメントタイプ */
	private String accgDocType;

	/** 取引実績SEQ */
	private Long accgRecordSeq;

	/** 報酬入金額【見込】 */
	private BigDecimal feeAmountExpect;

	/** 預り金入金額【見込】 */
	private BigDecimal depositRecvAmountExpect;

	/** 預り金出金額【見込】 */
	private BigDecimal depositPaymentAmountExpect;

}
