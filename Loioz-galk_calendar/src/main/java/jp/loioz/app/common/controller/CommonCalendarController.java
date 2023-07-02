package jp.loioz.app.common.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.loioz.app.common.service.CommonCalendarService;
import jp.loioz.common.utility.DateUtils;

@Controller
@RequestMapping(value = "common")
public class CommonCalendarController {

	@Autowired
	private CommonCalendarService commonCalendarService;

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * datepicker、ミニカレンダーで表示する祝日情報を取得する
	 */
	@ResponseBody
	@RequestMapping(value = "/getHolidays", method = RequestMethod.GET)
	public List<String> getHolidays() {

		// 表示する日付を取得
		LocalDate from = DateUtils.getDatePickerHolidaysFrom();
		LocalDate to = DateUtils.getDatePickerHolidaysTo();

		Map<LocalDate, String> holidaysMap = commonCalendarService.getHolidaysMap(from, to);
		return holidaysMap.keySet().stream().map(LocalDate::toString).collect(Collectors.toList());
	}

}
