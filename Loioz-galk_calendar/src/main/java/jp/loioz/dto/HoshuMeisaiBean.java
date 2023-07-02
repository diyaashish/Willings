package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class HoshuMeisaiBean {

	// ********************************
	// DBと対となる情報
	// ********************************
	// t_kaikei_kiroku
	/** 会計記録SEQ */
	private Long kaikeiKirokuSeq;

	/** 顧客ID */
	private Long customerId;

	/** 案件ID */
	private Long ankenId;

	/** 発生日 */
	private LocalDate hasseiDate;

	/** 報酬項目ID */
	private String hoshuKomokuId;

	/** 金額 */
	@Column(name = "shukkin_gaku")
	private BigDecimal kingaku;

	/** 消費税金額 */
	@Column(name = "tax_gaku")
	private BigDecimal taxGaku;

	/** タイムチャージ単価 */
	private BigDecimal timeChargeTanka;

	/** タイムチャージ時間指定方法 */
	private String timeChargeTimeShitei;

	/** タイムチャージ開始日時 */
	private LocalDateTime timeChargeStartTime;

	/** タイムチャージ終了日時 */
	private LocalDateTime timeChargeEndTime;

	/** タイムチャージ時間 */
	private Integer timeChargeTime;

	/** 消費税率 */
	private String taxRate;

	/** 税区分 */
	private String taxFlg;

	/** 源泉徴収フラグ */
	private String gensenchoshuFlg;

	/** 源泉徴収額 */
	private BigDecimal gensenchoshuGaku;

	/** 摘要 */
	private String tekiyo;

	/** 精算処理日 */
	private LocalDate seisanDate;

	/** 精算SEQ */
	private Long seisanSeq;

	// t_seisan_kiroku
	/** 精算ID */
	private Long seisanId;

	// t_anken
	/** 案件名 */
	private String ankenName;

	/** 分野 */
	private Long bunyaId;

	// t_person
	/** 顧客名 */
	private String customerName;

	/** 案件ステータス */
	private String ankenStatus;
}