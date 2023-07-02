package jp.loioz.app.user.statementList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.form.PagerForm;
import jp.loioz.app.user.statementList.form.StatementListSearchForm;
import jp.loioz.app.user.statementList.form.StatementListViewForm;
import jp.loioz.app.user.statementList.service.StatementListService;
import jp.loioz.common.constant.CommonConstant.MessageLevel;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 精算書一覧画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/statementList")
@SessionAttributes(StatementListController.SEARCH_FORM_NAME)
public class StatementListController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/statementList/statementList";

	/** 検索条件のフラグメントviewのパス */
	private static final String STATEMENT_LIST_SEARCH_FRAGMENT_VIEW_PATH = "user/statementList/statementListFragment::statementListSearchFragment";

	/** 精算書一覧とページングのフラグメントviewのパス */
	private static final String STATEMENT_LIST_PAGER_FRAGMENT_VIEW_PATH = "user/statementList/statementListFragment::statementListAndPagerWraperFragment";

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "statementListSearchForm";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 精算書一覧のサービスクラス */
	@Autowired
	private StatementListService service;

	/** フォームクラスの初期化 */
	@ModelAttribute(SEARCH_FORM_NAME)
	StatementListSearchForm setUpForm() {
		return new StatementListSearchForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 精算書一覧の画面表示（検索条件初期化）
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) StatementListSearchForm searchForm) {

		// 検索条件を初期化する。
		searchForm.initForm();
		// 表示用項目を設定
		service.setDispProperties(searchForm);
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.list(null, null));
	}

	/**
	 * 精算書一覧の画面表示（セッションに検索条件があれば優先して使用する）
	 * 
	 * @param searchForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@ModelAttribute(SEARCH_FORM_NAME) @Validated StatementListSearchForm searchForm,
			BindingResult result) {

		ModelAndView mv = null;

		// セッションに検索条件が無い場合、検索条件を初期化する
		if (LoiozCollectionUtils.isEmpty(searchForm.getTantoLawyerList())) {
			// 検索条件を初期化
			searchForm.initForm();
			// 表示用項目を設定
			service.setDispProperties(searchForm);
		} else {
			// 入力バリデーション
			if (result != null && result.hasErrors()) {
				// 検索条件にエラーがある場合は、空のviewFormを返し検索結果0件として表示する
				StatementListViewForm viewForm = service.createViewForm();
				mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
				super.setAjaxProcResultSuccess();
				return mv;
			}
		}
		// 検索条件に該当する一覧情報を取得
		StatementListViewForm viewForm = service.createViewForm();
		viewForm = service.searchStatementList(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getStatementList())) {
			searchForm.setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの精算書一覧情報を取得
			viewForm = service.searchStatementList(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 精算書詳細画面表示でエラーになる場合の精算書一覧表示用
	 * 
	 * @param searchForm
	 * @param attributes
	 * @param errMessage
	 * @return
	 */
	@RequestMapping(value = "/displayWhenStatementDetailsFails", method = RequestMethod.GET)
	public ModelAndView displayWhenStatementDetailsFails(
			@ModelAttribute(SEARCH_FORM_NAME) StatementListSearchForm searchForm, RedirectAttributes attributes,
			String errMessage) {
		RedirectViewBuilder builder = new RedirectViewBuilder(attributes,
				ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.list(null, null)));
		return builder.build(MessageHolder.ofError(errMessage));
	}

	/**
	 * メッセージ表示用初期画面表示処理
	 * 
	 * @param levelCd
	 * @param message
	 * @param attributes
	 * @return
	 */
	@RequestMapping(value = "/redirectIndexWithMsg", method = RequestMethod.GET)
	public ModelAndView redirectIndexWithMsg(
			@RequestParam("level") String levelCd,
			@RequestParam("message") String message,
			RedirectAttributes attributes) {
		RedirectViewBuilder builder = new RedirectViewBuilder(attributes, ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.list(null, null)));
		return builder.build(MessageHolder.ofLevel(MessageLevel.of(levelCd), message));
	}

	/**
	 * 検索条件フォーム表示
	 * 
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	@RequestMapping(value = "/displayStatementListSearchForm", method = RequestMethod.GET)
	public ModelAndView displayStatementListSearchForm(@ModelAttribute(SEARCH_FORM_NAME) StatementListSearchForm searchForm) {

		ModelAndView mv = null;
		mv = getMyModelAndView(searchForm, STATEMENT_LIST_SEARCH_FRAGMENT_VIEW_PATH, SEARCH_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 検索条件初期化
	 * 
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	@RequestMapping(value = "/initStatementSearchCondition", method = RequestMethod.GET)
	public ModelAndView initStatementSearchCondition(@ModelAttribute(SEARCH_FORM_NAME) StatementListSearchForm searchForm) {

		ModelAndView mv = null;

		// 検索条件初期化
		searchForm.initForm();
		// フィルターは開く
		searchForm.setSearchConditionOpen(true);
		// 表示用項目を設定
		service.setDispProperties(searchForm);
		mv = getMyModelAndView(searchForm, STATEMENT_LIST_SEARCH_FRAGMENT_VIEW_PATH, SEARCH_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 精算書一覧検索処理
	 * 
	 * @param searchForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/searchStatementList", method = RequestMethod.GET)
	public ModelAndView searchStatementList(@ModelAttribute(SEARCH_FORM_NAME) @Validated StatementListSearchForm searchForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			StatementListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, STATEMENT_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}

		// 検索時は最初のページを表示
		searchForm.setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する一覧情報を取得
		StatementListViewForm viewForm = service.createViewForm();
		viewForm = service.searchStatementList(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, STATEMENT_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 精算書一覧ソート処理
	 * 
	 * @param searchForm
	 * @param result
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/sort", method = RequestMethod.GET)
	public ModelAndView sort(@ModelAttribute(SEARCH_FORM_NAME) @Validated StatementListSearchForm searchForm,
			BindingResult result, HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, searchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			StatementListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, STATEMENT_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}

		// ソート時は最初のページを表示
		searchForm.setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する一覧情報を取得
		StatementListViewForm viewForm = service.createViewForm();
		viewForm = service.searchStatementList(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, STATEMENT_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 精算書一覧ページング処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/pager", method = RequestMethod.GET)
	public ModelAndView pager(@ModelAttribute(SEARCH_FORM_NAME) @Validated StatementListSearchForm searchForm,
			BindingResult result, HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, searchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			StatementListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, STATEMENT_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}

		// 検索条件に該当する精算書一覧情報を取得
		StatementListViewForm viewForm = service.createViewForm();
		viewForm = service.searchStatementList(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getStatementList())) {
			searchForm.setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの精算書一覧情報を取得
			viewForm = service.searchStatementList(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, STATEMENT_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * セッション情報が有るか確認する。<br>
	 * POSTリクエストの場合、上記のSessionが変わった状態でリクエストすると、
	 * フレームワーク側のCSRFトークンチェックでNGとなり、Controllerの処理までこないため、ここではPOSTの処理は考慮しない。
	 * 
	 * @param httpMethod
	 * @param searchForm
	 * @return
	 */
	private boolean existSession(HttpMethod httpMethod, StatementListSearchForm searchForm) {

		// POSTの場合はセッション有無を確認しない
		if (httpMethod == null || httpMethod == HttpMethod.POST) {
			return true;
		}

		// セッションにフォームが有るか確認する
		if (searchForm == null || LoiozCollectionUtils.isEmpty(searchForm.getTantoLawyerList())) {
			super.setAjaxProcResultFailure(MessageEnum.MSG_E00161.getMessageKey());
			return false;
		} else {
			return true;
		}
	}

}
