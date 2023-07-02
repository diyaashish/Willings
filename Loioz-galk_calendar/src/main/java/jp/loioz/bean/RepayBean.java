package jp.loioz.bean;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class RepayBean {

	/** 既入金項目SEQ */
	private Long docRepaySeq;

	/** 会計書類SEQ */
	private Long accgDocSeq;

	/** 取引日 */
	private LocalDate repayTransactionDate;

	/** 項目名 */
	private String repayItemName;

	/** 既入金金額 */
	private BigDecimal repayAmount;

	/** 摘要 */
	private String sumText;

	/** 並び順 */
	private Long docRepayOrder;

	/** 預り金SEQ （預り金が複数紐づく場合、カンマ区切りで取得） */
	private String depositRecvSeq;

	/** 作成元会計書類SEQ（預り金が複数紐づく場合、カンマ区切りで取得） */
	private String createdAccgDocSeq;
}
