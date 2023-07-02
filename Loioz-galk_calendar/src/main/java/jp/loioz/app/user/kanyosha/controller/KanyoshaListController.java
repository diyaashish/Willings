package jp.loioz.app.user.kanyosha.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.gyomuHistory.annitations.HasGyomuHistoryByAnken;
import jp.loioz.app.user.kanyosha.form.KanyoshaListForm;
import jp.loioz.app.user.kanyosha.service.KanyoshaListService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;

/**
 * 関与者一覧画面のcontrollerクラス
 */
@Controller
@HasGyomuHistoryByAnken
@RequestMapping("user/kanyosha")
public class KanyoshaListController extends DefaultController {

	/** コントローラに対応するviewパス **/
	private static final String MY_VIEW_PATH = "user/kanyosha/kanyoshaList";

	/** viewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 関与者一覧画面のserviceクラス */
	@Autowired
	private KanyoshaListService service;

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 初期表示
	 *
	 * @param ankenId
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView index(@RequestParam(name = "transitionCustomerId") Long customerId, @RequestParam(name = "transitionAnkenId") Long ankenId) {
		KanyoshaListForm form = service.createViewForm(customerId, ankenId);
		return getMyModelAndView(form, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 表示順変更処理
	 *
	 * @param kanyoshaSeq
	 * @param index
	 * @param ankenId
	 * @return gson.toJson(jsonObject)
	 */
	@RequestMapping(value = "/dispOrder", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> dispOrder(
			@RequestParam(name = "kanyoshaSeq") String kanyoshaSeq,
			@RequestParam(name = "index") String index,
			@RequestParam(name = "ankenId") Long ankenId) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 表示順変更処理
			service.dispOrder(kanyoshaSeq, index, ankenId);

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得する
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getMessageKey()));
			return response;
		}
		response.put("succeeded", true);
		response.put("message", getMessage(MessageEnum.MSG_I00023, "表示順"));
		return response;
	}
}
