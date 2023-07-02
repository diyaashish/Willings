package jp.loioz.app.user.personManagement.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import jp.loioz.common.utility.DateUtils;
import lombok.Data;

/**
 * 名簿画面：予定一覧用オブジェクト
 */
@Data
public class PersonScheduleListDto {

	/** スケジュールSEQ */
	private Long scheduleSeq;

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

	/** 件名 */
	private String subject;

	/** 場所 */
	private String place;

	/** 開始日 */
	private LocalDate dateFrom;

	/** 開始時間 */
	private LocalTime timeFrom;

	/** 終日フラグ */
	private boolean isAllDay;

	/** 終了日（終日予定の場合のみ値が存在する） */
	private LocalDate dateTo;
	
	/** 終了時間 */
	private LocalTime timeTo;
	
	/** アカウント名 */
	private List<String> accountNameList;

	/** 初回面談予定かどうか */
	private boolean isShokaiMendan;

	/** 表示用の開始日時（終日指定されていない予定用） */
	public String getDispDateTimeFrom() {
		return DateUtils.parseToString(this.dateFrom, DateUtils.DATE_M + "月" + DateUtils.DATE_D + "日") + " (" + this.dateFrom.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE) + ") " + DateUtils.parseToString(this.timeFrom, DateUtils.TIME_FORMAT_HMM);
	}
	
	/** 表示用の終了時間（終日指定されていない予定用） */
	public String getDispDateTimeTo() {
		return DateUtils.parseToString(LocalDateTime.of(this.dateTo, this.timeTo), DateUtils.TIME_FORMAT_HMM);
	}

	/** 表示用の日付（終日指定されている予定用） */
	public String getDispAllDayDate() {
		if (this.allDayPeriodIsOneDay()) {
			// 終日が1日間
			return DateUtils.parseToString(this.dateFrom, DateUtils.DATE_M + "月" + DateUtils.DATE_D + "日") + " (" + this.dateFrom.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE) + ") ";
		} else {
			// 終日が複数日間
			return getFormatDate(this.dateFrom, this.dateTo)
					+ " (" + this.dateFrom.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE) + ") "
					+ " ～ "
					+ getFormatDate(this.dateTo, this.dateFrom)
					+ " (" + this.dateTo.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE) + ") ";
		}
	}
	
	/** 終日指定の期間が1日間かどうか（終日指定が複数日間にまたがる場合はfalse） */
	public boolean allDayPeriodIsOneDay() {
		if (this.dateTo == null | this.dateTo.isEqual(this.dateFrom)) {
			// 終日がFromの日、1日の場合
			return true;
		} else {
			// 終日がFromの日から、複数日間の場合
			return false;
		}
	}

	/** 現在日時より過去の予定かどうか */
	public boolean isKakoYotei() {
		if (isAllDay) {
			if (dateTo.isBefore(LocalDate.now())) {
				return true;
			}
			return false;
		} else {
			LocalDateTime yoteiDateTime = this.dateFrom.atTime(this.timeTo);
			if (yoteiDateTime.isBefore(LocalDateTime.now())) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 日付が年跨ぎになっているか確認し、年跨ぎの場合は　dateを yyyy/M/d フォーマットで返します。<br>
	 * 年跨ぎ出ない場合は、dateを M/d フォーマットで返します。
	 * @param date
	 * @param comparisonDate
	 * @return
	 */
	private String getFormatDate(LocalDate date, LocalDate comparisonDate) {
		
		if(date.getYear() != comparisonDate.getYear()) {
			return DateUtils.parseToString(date, DateUtils.DATE_JP_YYYY_MM_DD_ZERO_FILL);
		} else {
			return DateUtils.parseToString(date, DateUtils.DATE_M + "月" + DateUtils.DATE_D + "日");
		}
		
	}
}
