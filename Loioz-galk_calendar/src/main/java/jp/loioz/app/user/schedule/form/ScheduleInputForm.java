package jp.loioz.app.user.schedule.form;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import jp.loioz.app.user.schedule.logic.ScheduleDateTimeInput;
import jp.loioz.common.constant.CommonConstant.JsDayOfWeek;
import jp.loioz.common.constant.CommonConstant.SchedulePermission;
import jp.loioz.common.constant.CommonConstant.ScheduleRepeatType;
import jp.loioz.common.constant.CommonConstant.ShutteiType;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.validation.annotation.DigitRange;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.groups.Update;
import lombok.Data;

/**
 * 予定の入力フォームクラス
 */
@Data
public class ScheduleInputForm implements ScheduleDateTimeInput {

	// 予定

	/** 予定SEQ */
	@Required(groups = Update.class)
	private Long scheduleSeq;

	/** 件名 */
	@Required
	@DigitRange(max = 30)
	private String subject;

	/** 日付From */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate dateFrom;

	/** 日付To */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate dateTo;

	/** 時間From */
	@DateTimeFormat(pattern = DateUtils.TIME_FORMAT)
	private LocalTime timeFrom;

	/** 時間To */
	@DateTimeFormat(pattern = DateUtils.TIME_FORMAT)
	private LocalTime timeTo;

	/** 時間：時間From */
	private int hourFrom;

	/** 時間：分From */
	private int minFrom;

	/** 時間：時間To */
	private int hourTo;

	/** 時間：分To */
	private int minTo;

	/** 終日 */
	private boolean allday;

	/** 繰返し */
	private boolean repeat;

	/** 繰返しタイプ */
	private ScheduleRepeatType repeatType;

	/** 繰返し曜日 */
	private Map<JsDayOfWeek, Boolean> repeatYobi = new HashMap<>();

	/** 繰返し日 */
	private Long repeatDayOfMonth;

	/** 繰返し期間指定 */
	private boolean useRepeatDate;

	/** 繰返し日付From */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate repeatDateFrom;

	/** 繰返し日付To */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate repeatDateTo;

	/** 施設選択 */
	private boolean roomSelected;

	/** 施設ID */
	private Long roomId;

	/** その他入力場所 */
	@DigitRange(max = 100)
	private String place;

	/** メモ */
	@DigitRange(max = 3000)
	private String memo;

	/** 公開範囲 */
	@Required // Enum範囲外の値がはnullになるのでRequired
	private SchedulePermission openRange;

	/** 編集許可 */
	@Required // Enum範囲外の値がはnullになるのでRequireds
	private SchedulePermission editRange;

	/** 顧客ID */
	private Long customerId;

	/** 案件ID */
	private Long ankenId;

	/** 初回面談 */
	private boolean shokaiMendan;

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 裁判期日 */
	private boolean saibanLimit;

	/** 裁判期日SEQ */
	private Long saibanLimitSeq;

	/** 出廷種別 */
	private ShutteiType shutteiType;

	// 予定-参加者

	/** 参加者 */
	private List<Long> member = new ArrayList<>();

	/** 参加者 */
	private Long memberOne;

	// Existing properties...

    private List<String> participantStatus;

    // Getter and setter methods for participantStatus...

}