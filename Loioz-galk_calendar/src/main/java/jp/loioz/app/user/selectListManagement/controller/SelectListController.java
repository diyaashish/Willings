package jp.loioz.app.user.selectListManagement.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.selectListManagement.form.SelectListForm;
import jp.loioz.app.user.selectListManagement.form.SelectSearchForm;
import jp.loioz.app.user.selectListManagement.service.SelectListService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;

/**
 * 選択肢設定画面のコントローラークラス
 */
@Controller
@RequestMapping(value = "user/selectListManagement")
@SessionAttributes(types = SelectSearchForm.class)
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class SelectListController extends DefaultController {
	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/selectListManagement/selectList";

	/** 検索情報を保持するフォームオブジェクト名 **/
	public static final String SEARCH_FORM_NAME = "selectListSearchForm";

	/** viewのformで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 選択肢のサービスクラス **/
	@Autowired
	private SelectListService service;

	// フォームクラスの初期化
	@ModelAttribute(SEARCH_FORM_NAME)
	SelectSearchForm createSearchForm() {
		return new SelectSearchForm();
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
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) SelectSearchForm selectSearchForm) {
		// 初期処理
		selectSearchForm.initForm();

		/* 画面表示情報を取得 */
		SelectListForm viewForm = service.search(new SelectListForm(), this.createSearchForm());

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 選択肢一覧検索
	 *
	 * @param selectListSearchForm
	 * @return
	 */
	@RequestMapping(value = "search", method = RequestMethod.GET)
	public ModelAndView search(@Validated @ModelAttribute(SEARCH_FORM_NAME) SelectSearchForm selectSearchForm, BindingResult result) {
		// ページ数を初期化
		selectSearchForm.setDefaultPage();
		// 検索処理
		SelectListForm viewForm = service.search(new SelectListForm(), selectSearchForm);

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
	@RequestMapping(value = "/updateDispOrder", method = RequestMethod.POST)
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