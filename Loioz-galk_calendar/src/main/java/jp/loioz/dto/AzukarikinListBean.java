package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class AzukarikinListBean {

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野ID */
	private Long bunyaId;

	/** 案件ステータス */
	private String ankenStatus;

	/** 受任日 */
	private LocalDate juninDate;

	/** 最終入金日 */
	private LocalDate LastNyukinDate;

	/** 会計記録SEQ */
	private Long kaikeiKirokuSeq;

	/** 報酬項目ID */
	private Long hoshuKomokuId;

	/** 入出金種別 */
	private String nyushukkinType;

	/** 入金額 */
	private BigDecimal nyukinGaku;

	/** 出金額 */
	private BigDecimal shukkinGaku;

	/** 源泉徴収 */
	private BigDecimal taxGaku;

	/** 消費税区分 */
	private String taxRate;

	/** 消費税フラグ(非課税、内税、外税) */
	private String taxFlg;

	/** 源泉徴収フラグ */
	private String gensenchoshuFlg;

	/** 源泉徴収 */
	private BigDecimal gensenchoshuGaku;

}
