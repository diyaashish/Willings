package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class SeisanKirokuBean {

	// t_seisan_kiroku
	/** 精算SEQ */
	@Column(name = "seisan_seq")
	Long seisanSeq;

	/** 精算ID */
	@Column(name = "seisan_id")
	Long seisanId;

	/** 精算区分 */
	@Column(name = "seisan_kubun")
	Long seisanKubun;

	/** 請求方法 */
	@Column(name = "seikyu_type")
	String seikyuType;

	/** 案件ID */
	@Column(name = "anken_id")
	Long ankenId;

	/** 顧客ID */
	@Column(name = "customer_id")
	Long customerId;

	/** 精算額 */
	@Column(name = "seisan_gaku")
	BigDecimal seisanGaku;

	/** 摘要 */
	@Column(name = "tekiyo")
	String tekiyo;

	/** 確定Flg */
	@Column(name = "seisan_status")
	String seisanStatus;

	/** 登録日 */
	@Column(name = "created_at")
	LocalDateTime createdAt;

	// t_anken
	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 分野 */
	@Column(name = "bunya_id")
	private Long bunyaId;

	// t_person
	/** 顧客名 */
	@Column(name = "customer_name")
	private String customerName;
}