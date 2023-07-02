package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class NyukinYoteiBean {

	// t_nyushukkin_yotei
	/** 入出金予定SEQ */
	@Column(name = "nyushukkin_yotei_seq")
	private Long nyushukkinYoteiSeq;

	/** 入出金予定タイプ */
	@Column(name = "nyushukkin_type")
	private String nyushukkinType;

	/** 顧客ID */
	@Column(name = "customer_id")
	private Long customerId;

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 分野ID */
	@Column(name = "bunya_id")
	private Long bunyaId;

	/** 入金予定日 */
	@Column(name = "nyushukkin_yotei_date")
	private LocalDate nyushukkinYoteiDate;

	/** 入金予定額 */
	@Column(name = "nyushukkin_yotei_gaku")
	private BigDecimal nyushukkinYoteiGaku;

	/** 入金日 */
	@Column(name = "nyushukkin_date")
	private LocalDate nyushukkinDate;

	/** 入金額 */
	@Column(name = "nyushukkin_gaku")
	private BigDecimal nyushukkinGaku;

	/** 摘要 */
	@Column(name = "tekiyo")
	private String tekiyo;

	/** 精算SEQ */
	@Column(name = "seisan_seq")
	private Long seisanSeq;

	/** 精算ID */
	@Column(name = "seisan_id")
	private Long seisanId;

	// t_anken
	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	// t_person
	/** 顧客名 */
	@Column(name = "customer_name")
	private String customerName;
}