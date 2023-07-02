package jp.loioz.app.user.nyushukkinYotei.shukkin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.controller.PageableController;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ShukkinListSearchForm;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ShukkinListViewForm;
import jp.loioz.app.user.nyushukkinYotei.shukkin.service.ShukkinListService;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@LoiozAdminControlCheck(items = {LoiozAdminControl.OLD_KAIKEI_OPEN})
@Controller
@RequestMapping(UrlConstant.NYUSHUKKIN_YOTEI_SHUKKIN_URL)
@SessionAttributes(ShukkinListController.SEARCH_FORM_NAME)
public class ShukkinListController extends DefaultController implements PageableController {

	/** コントローラーと対応するviewパス **/
	public static final String MY_VIEW_PATH = "user/nyushukkinYotei/shukkinList";

	/** 検索で使用するFormクラス（セッション管理） **/
	public static final String SEARCH_FORM_NAME = "shukkinListSearchForm";

	/** 画面表示で使用するFormクラス **/
	public static final String VIEW_FORM_NAME = "shukkinListViewForm";

	/** 入出金予定一覧（出金）で使用するserviceクラス **/
	@Autowired
	private ShukkinListService service;

	@ModelAttribute(SEARCH_FORM_NAME)
	ShukkinListSearchForm setUpSearchForm() {
		return new ShukkinListSearchForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 出金予定一覧を表示する
	 * 
	 * @param searchForm
	 * @return 画面
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) ShukkinListSearchForm searchForm) {

		// 検索条件の初期化
		searchForm.initForm();

		// viewの作成
		ShukkinListViewForm viewForm = service.createViewForm(searchForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 出金予定一覧検索
	 * 
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView search(@ModelAttribute(SEARCH_FORM_NAME) ShukkinListSearchForm searchForm) {

		// ページャの初期化
		searchForm.setDefaultPage();

		// 選択中の予定SEQを破棄します。
		searchForm.discardSelected();

		// 日付指定を選択月の最終日に合わせる
		searchForm.settingLastDaysOfMonth();

		// viewの作成
		ShukkinListViewForm viewForm = service.createViewForm(searchForm);

		// データの取得
		service.setData(searchForm, viewForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 入金予定一覧検索
	 *
	 * @param searchForm
	 * @return 画面情報
	 */
	@RequestMapping(value = "/pageList", method = RequestMethod.GET)
	public ModelAndView page(@ModelAttribute(SEARCH_FORM_NAME) ShukkinListSearchForm searchForm) {

		// 選択中の予定SEQを破棄します。
		searchForm.discardSelected();

		// viewの作成
		ShukkinListViewForm viewForm = service.createViewForm(searchForm);

		// データの取得
		service.setData(searchForm, viewForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 入出金予定一覧タブ切り替え(検索条件を保持)
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/tabSelected", method = RequestMethod.GET)
	public ModelAndView tabSelected(@ModelAttribute(SEARCH_FORM_NAME) ShukkinListSearchForm searchForm) {

		// viewの作成
		ShukkinListViewForm viewForm = service.createViewForm(searchForm);

		// データの取得
		service.setData(searchForm, viewForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
