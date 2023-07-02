package jp.loioz.app.user.azukarikinManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.controller.PageableController;
import jp.loioz.app.user.azukarikinManagement.form.AzukarikinSearchForm;
import jp.loioz.app.user.azukarikinManagement.form.AzukarikinViewForm;
import jp.loioz.app.user.azukarikinManagement.service.AzukarikinService;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

/**
 * 預り金・未収入金画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@LoiozAdminControlCheck(items = {LoiozAdminControl.OLD_KAIKEI_OPEN})
@Controller
@RequestMapping(UrlConstant.AZUKARIKIN_MANAGEMENT_URL)
@SessionAttributes(AzukarikinListController.SEARCH_FORM_NAME)
public class AzukarikinListController extends DefaultController implements PageableController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/azukarikinManagement/azukarikinList";

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "azukarikinSearchForm";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "azukarikinViewForm";

	/** 預り金・未収入金画面のサービスクラス */
	@Autowired
	private AzukarikinService service;

	/** フォームクラスの初期化 */
	@ModelAttribute(SEARCH_FORM_NAME)
	AzukarikinSearchForm setUpSearchForm() {
		return new AzukarikinSearchForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期遷移
	 *
	 * @param searchForm
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) AzukarikinSearchForm searchForm) {

		// 初期化
		searchForm.initForm();

		/* 画面表示情報を取得 */
		AzukarikinViewForm viewForm = service.createViewForm();

		// 初期遷移時はデータを表示しない
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 預り金・未収入金検索
	 *
	 * @param searchForm
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView search(@ModelAttribute(SEARCH_FORM_NAME) AzukarikinSearchForm searchForm) {

		// ページャー初期化
		searchForm.setDefaultPage();

		/* 画面表示情報を取得 */
		AzukarikinViewForm viewForm = service.createViewForm();
		service.setData(viewForm, searchForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * ページング
	 * 
	 * @param searchForm
	 * @return 検索結果画面
	 */
	@RequestMapping(value = "/pageList", method = RequestMethod.GET)
	public ModelAndView page(@ModelAttribute(SEARCH_FORM_NAME) AzukarikinSearchForm searchForm) {

		/* 画面表示情報を取得 */
		AzukarikinViewForm viewForm = service.createViewForm();
		service.setData(viewForm, searchForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}
}