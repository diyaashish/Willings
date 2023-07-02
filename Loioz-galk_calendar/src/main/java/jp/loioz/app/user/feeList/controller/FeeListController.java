package jp.loioz.app.user.feeList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.form.PagerForm;
import jp.loioz.app.user.feeList.form.FeeListSearchForm;
import jp.loioz.app.user.feeList.form.FeeListViewForm;
import jp.loioz.app.user.feeList.service.FeeListService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.SortConstant.FeeListSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.StringUtils;

/**
 * 報酬一覧画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/feeList")
@SessionAttributes(FeeListController.SEARCH_FORM_NAME)
public class FeeListController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/feeList/feeList";

	/** 検索条件のフラグメントviewのパス */
	private static final String FEE_LIST_SEARCH_FRAGMENT_VIEW_PATH = "user/feeList/feeListFragment::feeListSearchFragment";

	/** 報酬一覧とページングのフラグメントviewのパス */
	private static final String FEE_LIST_PAGER_FRAGMENT_VIEW_PATH = "user/feeList/feeListFragment::feeListAndPagerWraperFragment";

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "feeListSearchForm";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 報酬一覧のサービスクラス */
	@Autowired
	private FeeListService service;

	/** フォームクラスの初期化 */
	@ModelAttribute(SEARCH_FORM_NAME)
	FeeListSearchForm setUpForm() {
		return new FeeListSearchForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 報酬一覧の画面表示（検索条件初期化）
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) FeeListSearchForm searchForm) {

		// 検索条件を初期化
		searchForm.initForm();
		// 表示用項目を設定
		service.setDispProperties(searchForm);
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.list(null, null));
	}

	/**
	 * 報酬一覧の画面表示（セッションに検索条件があれば優先して使用する）
	 * 
	 * @param searchForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@ModelAttribute(SEARCH_FORM_NAME) @Validated FeeListSearchForm searchForm,
			BindingResult result) {

		ModelAndView mv = null;

		// メニュー選択時、クイック検索をしていた場合はクイック検索ワード、ページ番号を初期化する
		if (!StringUtils.isEmpty(searchForm.getSearchWord())) {
			searchForm.setSearchWord("");
			searchForm.defaultPage();
		}

		// セッションに検索条件が無い場合、検索条件を初期化する
		if (StringUtils.isEmpty(searchForm.getAnkenStatus())) {
			// 検索条件を初期化
			searchForm.initForm();
			// 表示用項目を設定
			service.setDispProperties(searchForm);
		} else {
			// 入力バリデーション
			if (result != null && result.hasErrors()) {
				// 検索条件にエラーがある場合は、空のviewFormを返し検索結果0件として表示する
				FeeListViewForm viewForm = service.createViewForm();
				mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
				super.setAjaxProcResultSuccess();
				return mv;
			}
		}
		// 検索条件に該当する一覧情報を取得
		FeeListViewForm viewForm = service.createViewForm();
		viewForm = service.searchFeeList(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getFeeList())) {
			searchForm.setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの報酬一覧情報を取得
			viewForm = service.searchFeeList(viewForm, searchForm);
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
	 * 検索条件フォーム表示
	 * 
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	@RequestMapping(value = "/displayFeeListSearchForm", method = RequestMethod.GET)
	public ModelAndView displayFeeListSearchForm(@ModelAttribute(SEARCH_FORM_NAME) FeeListSearchForm searchForm) {

		ModelAndView mv = null;
		mv = getMyModelAndView(searchForm, FEE_LIST_SEARCH_FRAGMENT_VIEW_PATH, SEARCH_FORM_NAME);
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
	@RequestMapping(value = "/initFeeSearchCondition", method = RequestMethod.GET)
	public ModelAndView initFeeSearchCondition(@ModelAttribute(SEARCH_FORM_NAME) FeeListSearchForm searchForm) {

		ModelAndView mv = null;

		// 検索条件初期化
		searchForm.initForm();
		// フィルターは開く
		searchForm.setSearchConditionOpen(true);
		// 表示用項目を設定
		service.setDispProperties(searchForm);
		mv = getMyModelAndView(searchForm, FEE_LIST_SEARCH_FRAGMENT_VIEW_PATH, SEARCH_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 報酬一覧検索処理（検索ワードを使用して検索）
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/searchFeeListUsingSearchWord", method = RequestMethod.GET)
	public ModelAndView searchFeeListUsingSearchWord(@ModelAttribute(SEARCH_FORM_NAME) FeeListSearchForm searchForm) {

		ModelAndView mv = null;

		// 検索ワード、フィルタ開閉フラグ、ソート条件以外をクリアする
		initSearchFormForSearchWord(searchForm);

		// 表示用項目を設定
		service.setDispProperties(searchForm);

		// 検索時は最初のページを表示
		searchForm.setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する一覧情報を取得
		FeeListViewForm viewForm = service.createViewForm();
		viewForm = service.searchFeeList(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, FEE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 報酬一覧検索処理（検索条件を使用して検索）
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/searchFeeListUsingSearchCondition", method = RequestMethod.GET)
	public ModelAndView searchFeeListUsingSearchCondition(
			@ModelAttribute(SEARCH_FORM_NAME) @Validated FeeListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は、空のviewFormを返し検索結果0件として表示する
			FeeListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, FEE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}

		// 報酬合計From、報酬合計Toのバリデーション
		if (service.validateFeeListSearchForm(searchForm)) {
			// 検索条件にエラーがある場合は、空のviewFormを返し検索結果0件として表示する
			FeeListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, FEE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}

		// 表示用項目を設定
		service.setDispProperties(searchForm);

		// 検索ワードを空にする
		searchForm.setSearchWord("");

		// 検索時は最初のページを表示
		searchForm.setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する一覧情報を取得
		FeeListViewForm viewForm = service.createViewForm();
		viewForm = service.searchFeeList(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, FEE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 報酬一覧ソート処理
	 * 
	 * @param searchForm
	 * @param result
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/sort", method = RequestMethod.GET)
	public ModelAndView sort(@ModelAttribute(SEARCH_FORM_NAME) @Validated FeeListSearchForm searchForm,
			BindingResult result, HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, searchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は、空のviewFormを返し検索結果0件として表示する
			FeeListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, FEE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}
		
		// ソート時は最初のページを表示
		searchForm.setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する一覧情報を取得
		FeeListViewForm viewForm = service.createViewForm();
		viewForm = service.searchFeeList(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, FEE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 報酬一覧ページング処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/pager", method = RequestMethod.GET)
	public ModelAndView pager(@ModelAttribute(SEARCH_FORM_NAME) @Validated FeeListSearchForm searchForm,
			BindingResult result, HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, searchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			FeeListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, FEE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}

		// 報酬合計From、報酬合計Toのバリデーション
		if (service.validateFeeListSearchForm(searchForm)) {
			// 検索条件にエラーがある場合は、空のviewFormを返し検索結果0件として表示する
			FeeListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, FEE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}

		// 検索条件に該当する報酬一覧情報を取得
		FeeListViewForm viewForm = service.createViewForm();
		viewForm = service.searchFeeList(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getFeeList())) {
			searchForm.setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの報酬一覧情報を取得
			viewForm = service.searchFeeList(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, FEE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
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
	 * 検索条件を検索ワード検索用に変更します<br>
	 * 
	 * @param searchForm
	 */
	private void initSearchFormForSearchWord(FeeListSearchForm searchForm) {
		// 現在の検索ワード、開閉フラグ、ソートを取得
		String searchWord = searchForm.getSearchWord();
		boolean searchConditionOpen = searchForm.isSearchConditionOpen();
		FeeListSortItem sortItem = searchForm.getFeeListSortItem();
		SortOrder sortOrder = searchForm.getFeeListSortOrder();
		
		// 検索条件をクリア
		searchForm.clearForm();
		
		// 検索ワード、開閉フラグ、ソートをセット
		searchForm.setSearchWord(searchWord);
		searchForm.setSearchConditionOpen(searchConditionOpen);
		searchForm.setFeeListSortItem(sortItem);
		searchForm.setFeeListSortOrder(sortOrder);
		
		// 顧客ステータスに「すべて」をセット
		searchForm.setAnkenStatus(CommonConstant.ANKEN_STATUS_ALL_CD);
	}

	/**
	 * セッション情報が有るか確認する。<br>
	 * POSTリクエストの場合、上記のSessionが変わった状態でリクエストすると、
	 * フレームワーク側のCSRFトークンチェックでNGとなり、Controllerの処理までこないため、ここではPOSTの処理は考慮しない。
	 * 
	 * @param httpMethod
	 * @param searchForm
	 * @return
	 */
	private boolean existSession(HttpMethod httpMethod, FeeListSearchForm searchForm) {

		// POSTの場合はセッション有無を確認しない
		if (httpMethod == null || httpMethod == HttpMethod.POST) {
			return true;
		}

		// セッションにフォームが有るか確認する
		if (searchForm == null || StringUtils.isEmpty(searchForm.getAnkenStatus())) {
			super.setAjaxProcResultFailure(MessageEnum.MSG_E00161.getMessageKey());
			return false;
		} else {
			return true;
		}
	}

}
