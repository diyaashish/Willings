package jp.loioz.app.user.sosakikanManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.controller.PageableController;
import jp.loioz.app.user.sosakikanManagement.form.SosakikanListForm;
import jp.loioz.app.user.sosakikanManagement.form.SosakikanSearchForm;
import jp.loioz.app.user.sosakikanManagement.service.SosakikanListService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;

@Controller
@RequestMapping("user/sosakikanManagement")
@SessionAttributes(SosakikanListController.SEARCH_FORM_NAME)
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class SosakikanListController extends DefaultController implements PageableController {
	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/sosakikanManagement/sosakikanList";

	/** 検索情報を保持するフォームオブジェクト名 **/
	public static final String SEARCH_FORM_NAME = "sosakikanSearchForm";

	/** viewのformで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 捜査機関のサービスクラス **/
	@Autowired
	private SosakikanListService service;

	// フォームクラスの初期化
	@ModelAttribute(SEARCH_FORM_NAME)
	SosakikanSearchForm createSearchForm() {
		return new SosakikanSearchForm();
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
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) SosakikanSearchForm sosakikanSearchForm) {
		// 初期処理
		sosakikanSearchForm.initForm();

		/* 画面表示情報を取得 */
		SosakikanListForm viewForm = service.search(new SosakikanListForm(), this.createSearchForm());

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 捜査機関一覧検索
	 *
	 * @param sosakikanSearchForm
	 * @return
	 */
	@RequestMapping(value = "search", method = RequestMethod.GET)
	public ModelAndView search(@ModelAttribute(SEARCH_FORM_NAME) SosakikanSearchForm sosakikanSearchForm) {
		// ページ数を初期化
		sosakikanSearchForm.setDefaultPage();
		// 検索処理
		SosakikanListForm viewForm = service.search(new SosakikanListForm(), sosakikanSearchForm);

		// 画面情報を返却
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 捜査機関一覧ページング
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "pageList", method = RequestMethod.GET)
	public ModelAndView page(@ModelAttribute(SEARCH_FORM_NAME) SosakikanSearchForm sosakikanSearchForm) {
		// ページング
		SosakikanListForm viewForm = service.search(new SosakikanListForm(), sosakikanSearchForm);

		// 画面情報を返却
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}
}
