package jp.loioz.app.user.kaikeiManagement.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jp.loioz.common.constant.CommonConstant.LawyerHoshu;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 * 報酬明細一覧Excel出力データ
 */
@Data
public class ExcelHoshuMeisaiData {

	public static final int COLUMN_NUM = 12;

	/** 発生日 */
	private String hasseiDate;

	/** 項目 */
	private LawyerHoshu hoshuKomoku;

	/** 報酬額 */
	private BigDecimal hoshuGaku;

	/** 表示用合計額 */
	private String dispHoshuGaku;

	/** 税額 */
	private BigDecimal taxGaku;

	/** 出力用税額Excelではカンマはなし */
	private String dispTaxGaku;

	/** 源泉徴収 */
	private BigDecimal gensenGaku;

	/** 出力用源泉徴収Excelではカンマはなし */
	private String dispGensenGaku;

	/** 合計 */
	private BigDecimal total;

	/** 表示用合計 */
	private String dispTotal;

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 摘要 */
	private String tekiyo;

	/** 精算処理日 */
	private String seisanDate;

	/** 精算ID */
	private String seisanId;

	/** ▼ ソート用データ */

	/** 発生日 */
	private LocalDate hasseiDateDate;

	/** タイムチャージ開始日時 */
	private LocalDateTime timeChargeStartTime;
	
}
