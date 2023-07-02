package jp.loioz.bean;

import java.time.LocalDate;
import java.time.LocalTime;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.constant.CommonConstant.EraType;
import lombok.Data;

/**
 * 案件ダッシュボード：予定情報取得用オブジェクト
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenDashScheduleBean {

	/** スケジュールSEQ */
	private Long scheduleSeq;

	/** 件名 */
	private String subject;

	/** 日付From */
	private LocalDate dateFrom;

	/** 日付To */
	private LocalDate dateTo;

	/** 時間From */
	private LocalTime timeFrom;

	/** 時間To */
	private LocalTime timeTo;

	/** 終日フラグ */
	private String allDayFlg;

	/** 場所(会議室) */
	private Long roomId;

	/** 場所(その他) */
	private String place;

	/** 顧客ID */
	private Long customerId;

	/** 案件ID */
	private Long ankenId;

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 裁判枝番 */
	private Long saibanBranchNo;

	/** 裁判期日SEQ */
	private Long saibanLimitSeq;

	/** 出廷種別 */
	private String shutteiType;

	/** アカウントSEQ */
	private Long accountSeq;

	/** 事件名 */
	private String jikenName;

	/** 事件元号 */
	private EraType jiken_gengo;

	/** 事件元号 */
	private String jiken_year;

	/** 事件元号 */
	private String jiken_mark;

	/** 事件元号 */
	private String jiken_no;

}
