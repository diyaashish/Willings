package jp.loioz.app.user.bushoManagement.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.bushoManagement.form.BushoListForm;
import jp.loioz.app.user.bushoManagement.form.BushoSearchForm;
import jp.loioz.app.user.bushoManagement.service.BushoListService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;

/**
 * 部門データ画面のコントローラークラス
 */
@Controller
@RequestMapping("user/bushoManagement/")
@SessionAttributes(types = BushoSearchForm.class)
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class BushoListController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/bushoManagement/bushoList";

	/** 検索情報を保持するフォームオブジェクト名 **/
	public static final String SEARCH_FORM_NAME = "bushoSearchForm";

	/** viewのformで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 部署のサービスクラス **/
	@Autowired
	private BushoListService service;

	// フォームクラスの初期化
	@ModelAttribute(SEARCH_FORM_NAME)
	BushoSearchForm createSearchForm() {
		return new BushoSearchForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 *
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) BushoSearchForm bushoSearchForm) {
		// 初期処理
		bushoSearchForm.initForm();

		/* 画面表示情報を取得 */
		BushoListForm viewForm = service.search(new BushoListForm(), this.createSearchForm());

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 部署一覧検索
	 *
	 * @param roomSearchForm
	 * @return
	 */
	@RequestMapping(value = "search", method = RequestMethod.GET)
	public ModelAndView search(@ModelAttribute(SEARCH_FORM_NAME) BushoSearchForm bushoSearchForm) {
		// ページ数を初期化
		bushoSearchForm.setDefaultPage();
		// 検索処理
		BushoListForm viewForm = service.search(new BushoListForm(), bushoSearchForm);

		// 画面情報を返却
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 表示順変更処理
	 *
	 * @param targetId
	 * @param index
	 * @return
	 */
	@RequestMapping(value = "updateDispOrder", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateDispOrder(
			@RequestParam(name = "targetId") String targetId,
			@RequestParam(name = "index") String index) {

		Map<String, Object> response = new HashMap<>();
		List<Long> dispOrder = new ArrayList<Long>();

		try {
			/* 表示順変更処理 */
			dispOrder = service.updateDispOrder(targetId, index);

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得する
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getMessageKey()));
			return response;
		}
		response.put("succeeded", true);
		response.put("dispOrder", dispOrder);
		response.put("message", getMessage(MessageEnum.MSG_I00023, "表示順"));
		return response;
	}

}