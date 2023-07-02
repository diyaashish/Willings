package jp.loioz.app.user.azukariItem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.controller.PageableController;
import jp.loioz.app.user.azukariItem.form.AzukariItemSearchForm;
import jp.loioz.app.user.azukariItem.form.AzukariItemViewForm;
import jp.loioz.app.user.azukariItem.service.AzukariItemListService;
import jp.loioz.app.user.gyomuHistory.annitations.HasGyomuHistoryByAnken;

/**
 * 預り品一覧画面のcontrollerクラス
 */
@Controller
@HasGyomuHistoryByAnken
@RequestMapping("user/azukariItem")
@SessionAttributes(value = {AzukariItemListController.SEARCH_FORM_NAME})
public class AzukariItemListController extends DefaultController implements PageableController {

	/** 預り品一覧に対応するviewパス */
	private static final String MY_VIEW_PATH = "user/azukariItem/azukariItemList";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "azukariItemSearchForm";

	/** 預り品一覧のserviceクラス */
	@Autowired
	private AzukariItemListService service;

	/** フォームクラスの初期化 */
	@ModelAttribute(SEARCH_FORM_NAME)
	AzukariItemSearchForm setUpForm() {
		return new AzukariItemSearchForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 一覧画面
	 *
	 * @param searchForm
	 * @param result
	 * @param msgHolder
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) AzukariItemSearchForm searchForm) {

		// 検索条件の初期化
		searchForm.initForm();

		// viewFormの作成
		AzukariItemViewForm viewForm = service.createViewForm(searchForm);

		// データ設定処理
		service.setData(viewForm, searchForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 預り品一覧検索
	 *
	 * @param searchForm
	 * @param result
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView search(@ModelAttribute(SEARCH_FORM_NAME) AzukariItemSearchForm searchForm) {

		// ページャの初期化
		searchForm.setDefaultPage();

		// viewFormの作成
		AzukariItemViewForm viewForm = service.createViewForm(searchForm);

		// データ設定処理
		service.setData(viewForm, searchForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * ページング処理
	 * 
	 * @param searchForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/pageList", method = RequestMethod.GET)
	public ModelAndView page(@ModelAttribute(SEARCH_FORM_NAME) AzukariItemSearchForm searchForm) {

		// viewFormの作成
		AzukariItemViewForm viewForm = service.createViewForm(searchForm);

		// データ設定処理
		service.setData(viewForm, searchForm);
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}
}