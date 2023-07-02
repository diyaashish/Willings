package jp.loioz.app.user.ankenManagement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.ankenManagement.form.AnkenDeleteForm;
import jp.loioz.app.user.ankenManagement.service.AnkenDeleteService;
import jp.loioz.app.user.personManagement.controller.PersonEditController;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 案件管理画面-削除モーダルのコントローラークラス
 */
@Controller
@RequestMapping("user/ankenManagement")
public class AnkenDeleteController extends DefaultController {

	/** コントローラに対応するviewパス */
	private static final String MY_VIEW_PATH = "user/ankenManagement/ankenDeleteModal";

	/** コントローラに対応するviewパス */
	private static final String AJAX_MODAL_PATH = MY_VIEW_PATH + "::ankenDeleteModal";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "ankenDeleteForm";

	@Autowired
	private AnkenDeleteService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * モーダル表示
	 *
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/openAnkenDeleteModal", method = RequestMethod.GET)
	public ModelAndView index(@RequestParam(name = "ankenId") Long ankenId) {

		ModelAndView mv = null;

		AnkenDeleteForm ankenDeleteForm = service.createViewForm(ankenId);
		mv = getMyModelAndView(ankenDeleteForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);

		if (mv == null) {
			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件の削除
	 * 
	 * @param ankenId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteAnken", method = RequestMethod.POST)
	public Map<String, Object> deleteAnken(@RequestParam(name = "ankenId") Long ankenId) {

		Map<String, Object> response = new HashMap<String, Object>();

		// 顧客情報を保持しておく (削除処理後は紐づくIDを取得出来ない)
		List<Long> customerId = service.getRelatedCustomerId(ankenId);
		String redirectPath = ModelAndViewUtils.getRedirectPath(PersonEditController.class, controller -> controller.index(customerId.stream().findFirst().get()));

		try {
			// 案件の削除が可能かどうかチェックする。
			service.deleteCheck(ankenId);

			// 案件の削除処理
			service.deleteAnken(ankenId);

		} catch (AppException e) {
			response.put("successed", false);
			response.put("message", getMessage(e.getErrorType(), e.getMessageArgs()));
			return response;
		}

		response.put("successed", true);
		response.put("message", getMessage(MessageEnum.MSG_I00028, "案件"));
		response.put("redirectPath", redirectPath);

		return response;
	}

}
