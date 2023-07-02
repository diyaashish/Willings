package jp.loioz.app.user.depositRecvList.controller;

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
import jp.loioz.app.user.depositRecvList.form.DepositRecvListSearchForm;
import jp.loioz.app.user.depositRecvList.form.DepositRecvListViewForm;
import jp.loioz.app.user.depositRecvList.service.DepositRecvListService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.SortConstant.DepositRecvListSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.StringUtils;

/**
 * 預り金一覧画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/depositRecvList")
@SessionAttributes(DepositRecvListController.SEARCH_FORM_NAME)
public class DepositRecvListController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/depositRecvList/depositRecvList";

	/** 検索条件のフラグメントviewのパス */
	private static final String DEPOSIT_RECV_LIST_SEARCH_FRAGMENT_VIEW_PATH = "user/depositRecvList/depositRecvListFragment::depositRecvListSearchFragment";

	/** 預り金一覧とページングのフラグメントviewのパス */
	private static final String DEPOSIT_RECV_LIST_PAGER_FRAGMENT_VIEW_PATH = "user/depositRecvList/depositRecvListFragment::depositRecvListAndPagerWraperFragment";

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "depositRecvListSearchForm";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_ROW_FORM_NAME = "viewForm";

	/** 預り金一覧のサービスクラス */
	@Autowired
	private DepositRecvListService service;

	/** フォームクラスの初期化 */
	@ModelAttribute(SEARCH_FORM_NAME)
	DepositRecvListSearchForm setUpForm() {
		return new DepositRecvListSearchForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 預り金一覧の画面表示（検索条件初期化）
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) DepositRecvListSearchForm searchForm) {

		// 検索条件を初期化する。
		searchForm.initForm();
		// 表示用項目を設定
		service.setDispProperties(searchForm);
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.list(null));
	}

	/**
	 * 預り金一覧の画面表示（セッションに検索条件があれば優先して使用する）
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@ModelAttribute(SEARCH_FORM_NAME) DepositRecvListSearchForm searchForm) {

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
		}

		// 検索条件に該当する一覧情報を取得
		DepositRecvListViewForm viewForm = service.createViewForm();
		viewForm = service.searchDepositRecvList(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getDepositRecvList())) {
			searchForm.setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの案件一覧情報を取得
			viewForm = service.searchDepositRecvList(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_ROW_FORM_NAME);
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
	@RequestMapping(value = "/displayDepositRecvListSearchForm", method = RequestMethod.GET)
	public ModelAndView displayDepositRecvListSearchForm(
			@ModelAttribute(SEARCH_FORM_NAME) DepositRecvListSearchForm searchForm) {

		ModelAndView mv = null;

		// 表示用項目を設定
		service.setDispProperties(searchForm);

		mv = getMyModelAndView(searchForm, DEPOSIT_RECV_LIST_SEARCH_FRAGMENT_VIEW_PATH, SEARCH_FORM_NAME);
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
	@RequestMapping(value = "/initDepositRecvSearchCondition", method = RequestMethod.GET)
	public ModelAndView initDepositRecvSearchCondition(
			@ModelAttribute(SEARCH_FORM_NAME) DepositRecvListSearchForm searchForm) {

		ModelAndView mv = null;

		// 検索条件初期化
		searchForm.initForm();
		// フィルターは開く
		searchForm.setSearchConditionOpen(true);
		// 表示用項目を設定
		service.setDispProperties(searchForm);
		mv = getMyModelAndView(searchForm, DEPOSIT_RECV_LIST_SEARCH_FRAGMENT_VIEW_PATH, SEARCH_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金一覧検索処理（検索ワードを使用して検索）
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/searchDepositRecvListUsingSearchWord", method = RequestMethod.GET)
	public ModelAndView searchDepositRecvListUsingSearchWord(
			@ModelAttribute(SEARCH_FORM_NAME) DepositRecvListSearchForm searchForm) {

		ModelAndView mv = null;

		// 検索ワード、フィルタ開閉フラグ、ソート条件以外をクリアする
		initSearchFormForSearchWord(searchForm);

		// 表示用項目を設定
		service.setDispProperties(searchForm);

		// 検索時は最初のページを表示
		searchForm.setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する一覧情報を取得
		DepositRecvListViewForm viewForm = service.createViewForm();
		viewForm = service.searchDepositRecvList(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, DEPOSIT_RECV_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_ROW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金一覧検索処理（検索条件を使用して検索）
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/searchDepositRecvListUsingSearchCondition", method = RequestMethod.GET)
	public ModelAndView searchDepositRecvListUsingSearchCondition(
			@ModelAttribute(SEARCH_FORM_NAME) @Validated DepositRecvListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			DepositRecvListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, DEPOSIT_RECV_LIST_SEARCH_FRAGMENT_VIEW_PATH, VIEW_ROW_FORM_NAME);
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
		DepositRecvListViewForm viewForm = service.createViewForm();
		viewForm = service.searchDepositRecvList(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, DEPOSIT_RECV_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_ROW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金一覧ソート処理
	 * 
	 * @param searchForm
	 * @param result
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/sort", method = RequestMethod.GET)
	public ModelAndView sort(@ModelAttribute(SEARCH_FORM_NAME) @Validated DepositRecvListSearchForm searchForm,
			BindingResult result, HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, searchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			DepositRecvListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, DEPOSIT_RECV_LIST_SEARCH_FRAGMENT_VIEW_PATH, VIEW_ROW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}

		// ソート時は最初のページを表示
		searchForm.setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する一覧情報を取得
		DepositRecvListViewForm viewForm = service.createViewForm();
		viewForm = service.searchDepositRecvList(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, DEPOSIT_RECV_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_ROW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金一覧ページング処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/pager", method = RequestMethod.GET)
	public ModelAndView pager(@ModelAttribute(SEARCH_FORM_NAME) @Validated DepositRecvListSearchForm searchForm,
			BindingResult result, HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, searchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			DepositRecvListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, DEPOSIT_RECV_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_ROW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}

		// 検索条件に該当する案件一覧情報を取得
		DepositRecvListViewForm viewForm = service.createViewForm();
		viewForm = service.searchDepositRecvList(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getDepositRecvList())) {
			searchForm.setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの案件一覧情報を取得
			viewForm = service.searchDepositRecvList(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, DEPOSIT_RECV_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_ROW_FORM_NAME);
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
	private void initSearchFormForSearchWord(DepositRecvListSearchForm searchForm) {
		// 現在の検索ワード、開閉フラグ、ソートを取得
		String searchWord = searchForm.getSearchWord();
		boolean searchConditionOpen = searchForm.isSearchConditionOpen();
		DepositRecvListSortItem sortItem = searchForm.getDepositRecvListSortItem();
		SortOrder sortOrder = searchForm.getDepositRecvListSortOrder();
		
		// 検索条件をクリア
		searchForm.clearForm();
		
		// 検索ワード、開閉フラグ、ソートをセット
		searchForm.setSearchWord(searchWord);
		searchForm.setSearchConditionOpen(searchConditionOpen);
		searchForm.setDepositRecvListSortItem(sortItem);
		searchForm.setDepositRecvListSortOrder(sortOrder);
		
		// 顧客ステータスに「すべて」をセット
		searchForm.setAnkenStatus(CommonConstant.ANKEN_STATUS_ALL_CD);
		// 預り金残高合計に「すべて」をセット
		searchForm.setTotalDepositBalance(CommonConstant.TotalDepositBalance.All.getCd());
	}

	/**
	 * セッション情報が有るか確認する。<br>
	 * 
	 * <pre>
	 * 複数タブで操作している場合、いずれかのタブで再ログインをし、Session（ブラウザが保持するCookie）が変わった場合、
	 * 開いていたタスク画面で再操作をしようとすると、Sessionが変わっているため、SearchFormの中の値がNULLになっている状態が発生する。
	 * そのまま後続処理を行うとNullPointerExceptionなどエラーが発生するため、Sessionが変わった（SearchFormの中の値がNULL）などの場合は
	 * 後続処理を行わないようにする。
	 * 
	 * POSTリクエストの場合、上記のSessionが変わった状態でリクエストすると、
	 * フレームワーク側のCSRFトークンチェックでNGとなり、Controllerの処理までこないため、ここではPOSTの処理は考慮しない。
	 * </pre>
	 * 
	 * @param httpMethod
	 * @param searchForm
	 * @return
	 */
	private boolean existSession(HttpMethod httpMethod, DepositRecvListSearchForm searchForm) {

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
