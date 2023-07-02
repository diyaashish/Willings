package jp.loioz.app.user.officeAccountSetting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.officeAccountSetting.form.OfficeAccountSearchForm;
import jp.loioz.app.user.officeAccountSetting.form.OfficeAccountSettingForm;
import jp.loioz.app.user.officeAccountSetting.service.OfficeAccountListService;

/**
 * アカウント設定画面のコントローラークラス
 */
@Controller
@RequestMapping("user/officeAccountSetting")
public class OfficeAccountListController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/officeAccountSetting/officeAccountList";

	/** viewのsearchformで使用するフォームオブジェクト名 */
	private static final String SEARCH_FORM_NAME = "accountSearchForm";

	/** viewのformで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** アカウント一覧画面のサービスクラス **/
	@Autowired
	private OfficeAccountListService service;

	// フォームクラスの初期化
	@ModelAttribute(SEARCH_FORM_NAME)
	OfficeAccountSearchForm createSearchForm() {
		return new OfficeAccountSearchForm();
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
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) OfficeAccountSearchForm accountSearchForm) {

		// 初期処理
		accountSearchForm.initForm();

		/* 画面表示情報を取得 */
		OfficeAccountSettingForm viewForm = service.search(new OfficeAccountSettingForm(), accountSearchForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * アカウント一覧検索
	 *
	 * @param accountSearchForm
	 * @return
	 */
	@RequestMapping(value = "search", method = RequestMethod.GET)
	public ModelAndView search(@ModelAttribute(SEARCH_FORM_NAME) OfficeAccountSearchForm accountSearchForm) {

		// ページ数を初期化
		accountSearchForm.setDefaultPage();

		// 検索処理
		OfficeAccountSettingForm viewForm = service.search(new OfficeAccountSettingForm(), accountSearchForm);

		// 画面情報を返却
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * アカウント一覧ページング
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "pageList", method = RequestMethod.GET)
	public ModelAndView page(@ModelAttribute(SEARCH_FORM_NAME) OfficeAccountSearchForm accountSearchForm) {

		// ページング
		OfficeAccountSettingForm viewForm = service.search(new OfficeAccountSettingForm(), accountSearchForm);

		// 画面情報を返却
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
