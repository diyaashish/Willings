package jp.loioz.app.user.calendar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonCalendarService;
import jp.loioz.app.user.calendar.form.CalendarViewForm;
import jp.loioz.app.user.calendar.form.CalendarViewForm.CalendarOptionsViewForm;
import jp.loioz.app.user.calendar.form.CalendarViewForm.CalendarScheduleViewForm;
import jp.loioz.dao.TSaibanLimitDao;
import jp.loioz.dao.TScheduleDao;

/**
 * カレンダー画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CalendarService extends DefaultService {

	/** カレンダー共通サービス */
	@Autowired
	private CommonCalendarService commonCalendarService;

	/** 予定情報用のDaoクラス */
	@Autowired
	private TScheduleDao tScheduleDao;

	/** 裁判期日情報用のDaoクラス */
	@Autowired
	private TSaibanLimitDao tSaibanLimitDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 画面表示情報を作成する
	 *
	 * @return 画面表示情報
	 */
	public CalendarViewForm createViewForm() {
		CalendarViewForm form = new CalendarViewForm();

		// 選択肢フォーム
		CalendarOptionsViewForm calendarOptionsViewForm = new CalendarOptionsViewForm();
		form.setCalendarOptionsViewForm(calendarOptionsViewForm);

		// 予定データ表示フォーム
		CalendarScheduleViewForm calendarScheduleViewForm = new CalendarScheduleViewForm();

		form.setCalendarScheduleViewForm(calendarScheduleViewForm);

		return form;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}