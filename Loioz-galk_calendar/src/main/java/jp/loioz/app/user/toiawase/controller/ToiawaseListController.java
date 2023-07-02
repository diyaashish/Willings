package jp.loioz.app.user.toiawase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.controller.PageableController;
import jp.loioz.app.user.toiawase.form.ToiawaseListSearchForm;
import jp.loioz.app.user.toiawase.form.ToiawaseListViewForm;
import jp.loioz.app.user.toiawase.service.ToiawaseListService;

/**
 * 問い合わせ一覧Controller
 */
@Controller
@SessionAttributes(types = ToiawaseListSearchForm.class)
@RequestMapping("user/toiawase")
public class ToiawaseListController extends DefaultController implements PageableController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/toiawase/toiawaseList";
	/** 画面描写で使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";
	/** 検索処理で使用するフォームオブジェクト名 */
	private static final String SEARCH_FORM_NAME = "toiawaseListSearchForm";

	/** 問い合わせ一覧サービス */
	@Autowired
	private ToiawaseListService service;

	/** 検索用オブジェクトの作成 */
	@ModelAttribute(SEARCH_FORM_NAME)
	ToiawaseListSearchForm setUpSearchForm() {
		return new ToiawaseListSearchForm();
	}

	/**
	 * 問い合わせ一覧
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) ToiawaseListSearchForm searchForm) {

		// 検索値の初期化
		searchForm.initForm();

		// 問い合わせ一覧画面オブジェクトの作成
		ToiawaseListViewForm viewForm = service.createViewForm();

		// 問い合わせ一覧画面のデータ設定
		service.search(viewForm, searchForm);

		// 画面返却
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 問い合わせ一覧の検索処理
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView search(@ModelAttribute(SEARCH_FORM_NAME) ToiawaseListSearchForm searchForm) {

		// ページングの初期化
		searchForm.setDefaultPage();

		// 問い合わせ一覧画面オブジェクトの作成
		ToiawaseListViewForm viewForm = service.createViewForm();

		// 問い合わせ一覧画面のデータ設定
		service.search(viewForm, searchForm);

		// 画面返却
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * ページャ
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/pageList", method = RequestMethod.GET)
	public ModelAndView page(@ModelAttribute(SEARCH_FORM_NAME) ToiawaseListSearchForm searchForm) {

		// 問い合わせ一覧画面オブジェクトの作成
		ToiawaseListViewForm viewForm = service.createViewForm();

		// 問い合わせ一覧画面のデータ設定
		service.search(viewForm, searchForm);

		// 画面返却
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

}
