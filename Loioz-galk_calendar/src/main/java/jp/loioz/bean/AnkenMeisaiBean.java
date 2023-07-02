package jp.loioz.bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class AnkenMeisaiBean {

	// t_kaikei_kiroku
	/** 会計記録SEQ */
	@Column(name = "kaikei_kiroku_seq")
	Long kaikeiKirokuSeq;

	/** 顧客ID */
	@Column(name = "customer_id")
	Long customerId;

	/** 案件ID */
	@Column(name = "anken_id")
	Long ankenId;

	/** 発生日 */
	@Column(name = "hassei_date")
	LocalDate hasseiDate;

	/** 報酬項目ID */
	@Column(name = "hoshu_komoku_id")
	String hoshuKomokuId;

	/** 入出金項目ID */
	@Column(name = "nyushukkin_komoku_id")
	Long nyushukkinKomokuId;

	/** 入出金タイプ */
	@Column(name = "nyushukkin_type")
	String nyushukkinType;

	/** 入金額 */
	@Column(name = "nyukin_gaku")
	BigDecimal nyukinGaku;

	/** 出金額 */
	@Column(name = "shukkin_gaku")
	BigDecimal shukkinGaku;

	/** 消費税金額 */
	@Column(name = "tax_gaku")
	BigDecimal taxGaku;

	/** タイムチャージ単価 */
	@Column(name = "time_charge_tanka")
	BigDecimal timeChargeTanka;

	/** タイムチャージ開始時間 */
	@Column(name = "time_charge_start_time")
	LocalTime timeChargeStartTime;

	/** タイムチャージ終了時間 */
	@Column(name = "time_charge_end_time")
	LocalTime timeChargeEndTime;

	/** 消費税率 */
	@Column(name = "tax_rate")
	String taxRate;

	/** 税区分 */
	@Column(name = "tax_flg")
	String taxFlg;

	/** 源泉徴収フラグ */
	@Column(name = "gensenchoshu_flg")
	String gensenchoshuFlg;

	/** 源泉徴収額 */
	@Column(name = "gensenchoshu_gaku")
	BigDecimal gensenchoshuGaku;

	/** 摘要 */
	@Column(name = "tekiyo")
	String tekiyo;

	/** 精算処理日 */
	@Column(name = "seisan_date")
	LocalDate seisanDate;

	// t_anken
	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 案件ID */
	@Column(name = "bunya_id")
	private Long bunyaId;

	// t_anken_customer
	/** 受任日 */
	@Column(name = "junin_date")
	private String juninDate;

	/** 事件処理完了日 */
	@Column(name = "jiken_kanryo_date")
	private String jikenKanryoDate;

	/** 完了フラグ */
	@Column(name = "kanryo_flg")
	private String kanryoFlg;

	/** 顧客ステータス */
	@Column(name = "anken_status")
	private String ankenStatus;

	// t_person
	/** 顧客名 */
	@Column(name = "customer_name")
	private String customerName;
}