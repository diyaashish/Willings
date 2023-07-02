package jp.loioz.app.user.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.loioz.app.common.form.LazyLoadForm;
import jp.loioz.app.user.schedule.annotations.HasSchedule;
import jp.loioz.app.user.schedule.form.ScheduleCommonViewForm;
import jp.loioz.app.user.schedule.service.ScheduleCommonService;

/**
 * 予定を扱う画面の共通設定
 */
@ControllerAdvice(annotations = HasSchedule.class)
public class ScheduleCommonControllerAdvice {

	@Autowired
	private ScheduleCommonService service;

	@ModelAttribute("scheduleCommonViewForm")
	public LazyLoadForm<ScheduleCommonViewForm> viewForm() {
		return new LazyLoadForm<>(() -> service.createCommonViewForm());
	}
}