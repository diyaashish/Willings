package jp.loioz.app.user.kaikeiManagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jp.loioz.common.constant.CommonConstant.LawyerHoshu;
import jp.loioz.domain.value.SeisanId;
import lombok.Data;

/**
 * 報酬明細一覧Dto
 */
@Data
public class HoshuMeisaiListDto {

	/** 会計記録SEQ */
	private Long kaikeiKirokuSeq;

	/** 発生日 */
	private String hasseiDate;

	/** 報酬項目 */
	private LawyerHoshu hoshuKomoku;

	/** 報酬額 */
	private BigDecimal hoshuGaku;

	/** 表示用報酬額 */
	private String dispHoshuGaku;

	/** 消費税 */
	private BigDecimal taxGaku;

	/** 表示用消費税 */
	private String dispTaxGaku;

	/** 源泉徴収 */
	private BigDecimal gensenGaku;

	/** 表示用源泉徴収 */
	private String dispGensenGaku;

	/** 合計 */
	private BigDecimal total;

	/** 表示用合計 */
	private String dispTotal;

	/** 顧客ID */
	private Long customerId;

	/** 表示用顧客ID */
	private String dispCustomerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID */
	private Long ankenId;

	/** 案件ID */
	private String dispAnkenId;

	/** 案件名 */
	private String ankenName;

	/** 摘要 */
	private String tekiyou;

	/** 精算処理日 */
	private String seisanDate;

	/** 精算ID */
	private SeisanId seisanId;

	/** 処理済みかどうか ※精算処理日がある, もしくは案件ステータスが精算完了の場合にTRUE */
	private boolean isCompleted;

	/** ▼ ソート用データ */

	/** 発生日 */
	private LocalDate hasseiDateDate;

	/** タイムチャージ開始日時 */
	private LocalDateTime timeChargeStartTime;

}
