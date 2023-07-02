package jp.loioz.app.user.schedule.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jp.loioz.app.user.schedule.enums.CalendarDispType;
import jp.loioz.app.user.schedule.enums.UserSelectType;
import lombok.Data;

/**
 * 予定表：カレンダー選択状態
 */
@Data
public class CalendarStatusForm {

	/** カレンダー表示種別 */
	private CalendarDispType dispType;

	/** 選択中の日付 */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate calendarDate;

	/** ユーザー選択タブ種別 */
	private UserSelectType userSelectType;

	/** 選択中のユーザ */
	private List<Long> selectedAccountSeq = new ArrayList<>();

	/** 祝日の表示非表示切り替えステータス */
	private boolean selectedHoliday = true;
}