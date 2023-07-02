package jp.loioz.app.user.calendar.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.calendar.dto.CalendarOptionsBushoAccountDto;
import lombok.Data;

/**
 * カレンダー画面の画面表示フォームクラス
 */
@Data
public class CalendarViewForm {

	/** カレンダー選択肢-表示用オブジェクト */
	private CalendarOptionsViewForm calendarOptionsViewForm;

	/** カレンダー予定-表示用オブジェクト */
	private CalendarScheduleViewForm calendarScheduleViewForm;

	/**
	 * カレンダー選択肢-表示用オブジェクト
	 */
	@Data
	public static class CalendarOptionsViewForm {

		/** カレンダー選択肢-部署+アカウント */
		private List<CalendarOptionsBushoAccountDto> calendarOptionsAccountDtoList = Collections.emptyList();

	}

	/**
	 * 予定エリア表示用オブジェクト
	 */
	@Data
	public static class CalendarScheduleViewForm {

	}

}