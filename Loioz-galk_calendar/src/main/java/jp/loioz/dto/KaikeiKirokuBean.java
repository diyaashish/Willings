package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class KaikeiKirokuBean {

	// ------------------------------------------
	// DBと対となる情報
	// ------------------------------------------
	// t_kaikei_kiroku
	/** 会計記録SEQ */
	private Long kaikeiKirokuSeq;

	/** 顧客ID */
	private Long customerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID */
	private Long ankenId;

	/** 発生日 */
	private LocalDate hasseiDate;

	/** 入出金タイプ */
	private String nyushukkinType;

	/** 入出金項目ID */
	private Long nyushukkinKomokuId;

	/** 入金額 */
	private BigDecimal nyukinGaku;

	/** 出金額 */
	private BigDecimal shukkinGaku;

	/** 消費税金額 */
	private BigDecimal taxGaku;

	/** 摘要 */
	private String tekiyo;

	/** プール種別 */
	private String poolType;

	/** 消費税率 */
	private String taxRate;

	/** 精算処理日 */
	private LocalDate seisanDate;

	/** 精算SEQ */
	private Long seisanSeq;

	/** 精算ID */
	private Long seisanId;

	/** 最終編集日時 */
	private LocalDateTime lastEditAt;

	// t_nyushukkin_yotei
	/** 入出金予定SEQ */
	private Long nyushukkinYoteiSeq;

	/** 入出金予定の精算SEQ */
	private Long yoteiSeisanSeq;

	// m_nyushukkin_komoku
	/** 項目名 */
	private String komokuName;

	/** 課税フラグ */
	private String taxFlg;

	// t_anken
	/** 案件名 */
	private String ankenName;

	/** 分野 */
	private Long bunyaId;

	// t_person
	/** 最終編集アカウントSEQ */
	private Long lastEditBy;

	// t_person
	/** 登録日時 */
	private LocalDateTime createdAt;

	// t_person
	/** 登録アカウントSEQ */
	private Long createdBy;

	// t_anken_customer
	/** 案件ステータス */
	private String ankenStatus;

	// ---------------------------------------------------
	// 会計管理画面では不要だが、
	// 精算書作成画面で使用しているため残す。
	// ---------------------------------------------------
	/** 報酬項目ID */
	private String hoshuKomokuId;

	/** 源泉徴収フラグ */
	private String gensenchoshuFlg;

	/** 源泉徴収額 */
	private BigDecimal gensenchoshuGaku;

	// m_account
	/** アカウント名 */
	private String accountName;

	/** タイムチャージ時間指定方法 */
	private String timeChargeTimeShitei;

	/** タイムチャージ開始日時 */
	private LocalDateTime timeChargeStartTime;

	// ------------------------------------------
	// 画面表示用
	// ------------------------------------------
	/** 税込み入金額 */
	private BigDecimal taxIncludedNyukingaku;

	/** 税込み出金額 */
	private BigDecimal taxIncludedShukkingaku;
}