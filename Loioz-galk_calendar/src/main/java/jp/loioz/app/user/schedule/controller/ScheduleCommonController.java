package jp.loioz.app.user.schedule.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.schedule.form.ScheduleDetail;
import jp.loioz.app.user.schedule.form.ScheduleInputForm;
import jp.loioz.app.user.schedule.form.ajax.RoomAvailabilityRequest;
import jp.loioz.app.user.schedule.form.ajax.RoomAvailabilityResponse;
import jp.loioz.app.user.schedule.form.ajax.ScheduleBySeqRequest;
import jp.loioz.app.user.schedule.service.ScheduleCommonService;
import jp.loioz.common.constant.CommonConstant.ShutteiType;
import jp.loioz.common.constant.MessageEnum;

/**
 * 予定の共通コントローラークラス
 */
@Controller
@RequestMapping(value = "user/schedule")
public class ScheduleCommonController extends DefaultController {

	/** サービスクラス */
	@Autowired
	private ScheduleCommonService service;

	// =========================================================================
	// Ajax
	// =========================================================================
	/**
	 * 予定SEQで予定を取得する
	 *
	 * @param request 取得条件
	 * @param result バリデーション結果
	 * @return 予定
	 */
	@ResponseBody
	@RequestMapping(value = "/getScheduleBySeq", method = RequestMethod.POST)
	public Map<Long, ScheduleDetail> getScheduleBySeq(@Validated @ModelAttribute ScheduleBySeqRequest request, BindingResult result) {

		if (result.hasErrors()) {
			return Collections.emptyMap();
		}

		return service.getSchedule(request);
	}

	/**
	 * 予定SEQで予定を取得する ※予定SEQが一意となるデータを返却する
	 *
	 * @param request 取得条件
	 * @param result バリデーション結果
	 * @return 予定
	 */
	@ResponseBody
	@RequestMapping(value = "/getSchedulePKOneBySeq", method = RequestMethod.POST)
	public Map<Long, ScheduleDetail> getSchedulePKOneBySeq(@Validated @ModelAttribute ScheduleBySeqRequest request, BindingResult result) {

		if (result.hasErrors()) {
			return Collections.emptyMap();
		}

		return service.getSchedulePKOne(request);
	}

	/**
	 * 予定SEQで予定を取得する ※予定SEQが一意となるデータを返却する
	 *
	 * @param request 取得条件
	 * @param result バリデーション結果
	 * @return 予定
	 */
	@ResponseBody
	@RequestMapping(value = "/getSchedulePKOneByAnkenId", method = RequestMethod.POST)
	public Map<Long, ScheduleDetail> getSchedulePKOneByAnkenId(@RequestParam(name = "ankenId", required = false) Long ankenId) {

		if (ankenId == null) {
			return Collections.emptyMap();
		}

		ScheduleBySeqRequest request = new ScheduleBySeqRequest();
		List<Long> scheduleSeq = service.getScheduleSeqByAnkenId(ankenId);
		request.setScheduleSeq(scheduleSeq);

		return service.getSchedulePKOne(request);
	}

	/**
	 * 会議室の予約状況を取得する
	 *
	 * @param request 予約条件
	 * @param result バリデーション結果
	 * @return 予約状況
	 */
	@ResponseBody
	@RequestMapping(value = "/getRoomAvailability", method = RequestMethod.POST)
	public RoomAvailabilityResponse getRoomAvailability(@Validated @ModelAttribute RoomAvailabilityRequest request, BindingResult result) {

		if (result.hasErrors()) {
			return new RoomAvailabilityResponse();
		}

		return service.getRoomAvailability(request);
	}

	/**
	 * 登録処理前に同じ時間に参加者の予定がないかを確認する
	 * 
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/needConfirm", method = RequestMethod.POST)
	public Map<String, Object> needConfirm(@Validated ScheduleInputForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		List<String> conflictAccount = service.getConflictAccount(form);
		// 重複するユーザーがいない、もしくは「出廷不要」の場合 -> 確認アラートは不要
		if (ListUtils.isEmpty(conflictAccount) || ShutteiType.NOT_REQUIRED == form.getShutteiType()) {
			response.put("needConfirm", false);
		} else {
			response.put("needConfirm", true);
			response.put("message", getMessage(MessageEnum.MSG_W00011));
		}

		return response;
	}

	/**
	 * 案件担当者プルダウンに表示するアカウントを取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getAnkenTanto", method = RequestMethod.POST)
	public Map<String, Object> getAnkenTanto(@RequestParam(name = "ankenId") Long ankenId) {

		Map<String, Object> response = new HashMap<>();
		response.put("ankenTanto", service.getAnkenTantoUserSeq(ankenId));

		return response;
	}

	/**
	 * 裁判担当者プルダウンに表示するアカウントを取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getSaibanTanto", method = RequestMethod.POST)
	public Map<String, Object> getSaibanTanto(@RequestParam(name = "saibanSeq") Long saibanSeq) {

		Map<String, Object> response = new HashMap<>();
		response.put("saibanTanto", service.getSaibanTantoUserSeq(saibanSeq));

		return response;
	}

	/**
	 * 案件主担当者の取得
	 * 
	 * 一部他画面からもアクセスあり(タスク編集:参加者)
	 * 
	 * @param ankenId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getAnkenMainTanto", method = RequestMethod.POST)
	public Map<String, Object> getAnkenMainTanto(@RequestParam(name = "ankenId") Long ankenId) {

		Map<String, Object> response = new HashMap<>();
		response.put("mainTanto", service.getAnkenMainTantoUserSeq(ankenId));

		return response;
	}

	/**
	 * 裁判主担当者の取得
	 * 
	 * @param ankenId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getSaibanMainTanto", method = RequestMethod.POST)
	public Map<String, Object> getSaibanMainTanto(@RequestParam(name = "saibanSeq") Long saibanSeq) {

		Map<String, Object> response = new HashMap<>();
		response.put("mainTanto", service.getSaibanMainTantoUserSeq(saibanSeq));

		return response;
	}

}