package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.SeisanId;
import lombok.Data;

@Data
public class SeisanshoDto {

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
	private String hasseiDate;

	/** 報酬項目ID */
	private String hoshuKomokuId;

	/** 入出金項目ID */
	private Long nyushukkinKomokuId;

	/** 入出金タイプ */
	private String nyushukkinType;

	/** 入金額 */
	private BigDecimal nyukinGaku;

	/** 出金額 */
	private BigDecimal shukkinGaku;

	/** 消費税率 */
	private String taxRate;

	/** 源泉徴収フラグ */
	private String gensenchoshuFlg;

	/** 源泉徴収額 */
	private BigDecimal gensenchoshuGaku;

	/** 摘要 */
	private String tekiyo;

	/** 精算SEQ */
	private Long seisanSeq;

	/** 精算ID */
	private Long seisanId;

	/** 登録アカウントID */
	private Long createdBy;

	// m_nyushukkin_komoku
	/** 項目名 */
	private String komokuName;

	/** 課税フラグ名 */
	private String taxFlg;

	// t_anken
	/** 案件名 */
	private String ankenName;

	/** 分野 */
	private String bunya;

	// t_person
	/** 顧客名 */
	private String customerName;

	// m_account
	/** アカウント名 */
	private String accountName;

	// ********************************
	// 画面表示用
	// ********************************
	/** 項目 */
	private String item;

	/** 税区分 */
	private String taxType;

	/** 案件ID(表示用) */
	private AnkenId displayAnkenId;

	/** 顧客ID(表示用) */
	private CustomerId displayCustomerId;

	/** 精算ID(表示用) */
	private SeisanId displaySeisanId;

	/** 入金額(表示用) */
	private String displayNyukinGaku;

	/** 出金額(表示用) */
	private String displayShukkinGaku;

	/** 金額(表示用) */
	private String kingaku;

	/** 消費税額 */
	private BigDecimal tax;

	/** 消費税額(表示用) */
	private String displayTax;

	/** 源泉徴収額 */
	private String dispGensenchoshuGaku;

	/** 入金項目ID */
	private Long nyukinKomokuId;

	/** 出金項目ID */
	private Long shukkinKomokuId;

	/** 課税/非課税(表示用) */
	private String taxText;

	/** 入出金タイプ */
	private String displaNyushukkinType;

	/** 税込み入金額 */
	private BigDecimal taxIncludedNyukingaku;

	/** 税込み出金額 */
	private BigDecimal taxIncludedShukkingaku;

	/** 画面表示のチェック状態 */
	private Boolean defaultSeisanCheck;

	/** チェック可否 */
	private Boolean disabledSeisanCheck;
	
    /** ▼ ソート用データ（発生日、開始日時でデータをソートするために利用） */
	
	/** タイムチャージ時間指定方法 */
	private String timeChargeTimeShitei;
	
	/** 発生日 */
	private LocalDate hasseiDateDate;
	
	/** タイムチャージ開始日時 */
	private LocalDateTime timeChargeStartTime;
}