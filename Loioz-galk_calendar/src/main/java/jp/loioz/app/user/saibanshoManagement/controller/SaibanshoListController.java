package jp.loioz.app.user.saibanshoManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.controller.PageableController;
import jp.loioz.app.user.saibanshoManagement.form.SaibanshoListForm;
import jp.loioz.app.user.saibanshoManagement.form.SaibanshoSearchForm;
import jp.loioz.app.user.saibanshoManagement.service.SaibanshoListService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;

@Controller
@RequestMapping("user/saibanshoManagement")
@SessionAttributes(types = SaibanshoSearchForm.class)
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class SaibanshoListController extends DefaultController implements PageableController {
	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/saibanshoManagement/saibanshoList";

	/** 検索情報を保持するフォームオブジェクト名 **/
	public static final String SEARCH_FORM_NAME = "saibanshoSearchForm";

	/** viewのformで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 裁判所のサービスクラス **/
	@Autowired
	private SaibanshoListService service;

	// フォームクラスの初期化
	@ModelAttribute(SEARCH_FORM_NAME)
	SaibanshoSearchForm createSearchForm() {
		return new SaibanshoSearchForm();
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
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) SaibanshoSearchForm saibanshoSearchForm) {
		// 初期処理
		saibanshoSearchForm.initForm();

		/* 画面表示情報を取得 */
		SaibanshoListForm viewForm = service.search(new SaibanshoListForm(), this.createSearchForm());

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 裁判所一覧検索
	 *
	 * @param saibanshoSearchForm
	 * @return
	 */
	@RequestMapping(value = "search", method = RequestMethod.GET)
	public ModelAndView search(@ModelAttribute(SEARCH_FORM_NAME) SaibanshoSearchForm saibanshoSearchForm) {
		// ページ数を初期化
		saibanshoSearchForm.setDefaultPage();
		// 検索処理
		SaibanshoListForm viewForm = service.search(new SaibanshoListForm(), saibanshoSearchForm);

		// 画面情報を返却
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 裁判所一覧ページング
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "pageList", method = RequestMethod.GET)
	public ModelAndView page(@ModelAttribute(SEARCH_FORM_NAME) SaibanshoSearchForm saibanshoSearchForm) {
		// ページング
		SaibanshoListForm viewForm = service.search(new SaibanshoListForm(), saibanshoSearchForm);

		// 画面情報を返却
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

}
