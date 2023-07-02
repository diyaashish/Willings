package jp.loioz.app.user.invoiceList.controller;

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
import jp.loioz.app.user.invoiceDetail.controller.InvoiceDetailController;
import jp.loioz.app.user.invoiceList.form.InvoiceListSearchForm;
import jp.loioz.app.user.invoiceList.form.InvoiceListViewForm;
import jp.loioz.app.user.invoiceList.service.InvoiceListService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.StringUtils;

/**
 * 請求書一覧画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/invoiceList")
@SessionAttributes(InvoiceListController.SEARCH_FORM_NAME)
public class InvoiceListController extends DefaultController {

	/** 請求書詳細画面遷移のレスポンスヘッダー設定値 */
	public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_INVOICE_DETAIL_REDIRECT = "100";

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/invoiceList/invoiceList";

	/** 検索条件のフラグメントviewのパス */
	private static final String INVOICE_LIST_SEARCH_FRAGMENT_VIEW_PATH = "user/invoiceList/invoiceListFragment::invoiceListSearchFragment";

	/** 請求書一覧とページングのフラグメントviewのパス */
	private static final String INVOICE_LIST_PAGER_FRAGMENT_VIEW_PATH = "user/invoiceList/invoiceListFragment::invoiceListAndPagerWraperFragment";

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "invoiceListSearchForm";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 請求書一覧のサービスクラス */
	@Autowired
	private InvoiceListService service;

	/** フォームクラスの初期化 */
	@ModelAttribute(SEARCH_FORM_NAME)
	InvoiceListSearchForm setUpForm() {
		return new InvoiceListSearchForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 請求書一覧の画面表示（検索条件初期化）
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) InvoiceListSearchForm searchForm) {

		// 検索条件を初期化する。
		searchForm.initForm();
		// 表示用項目を設定
		service.setDispProperties(searchForm);
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.list(null, null));
	}

	/**
	 * 請求書一覧の画面表示（セッションに検索条件があれば優先して使用する）
	 * 
	 * @param searchForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@ModelAttribute(SEARCH_FORM_NAME) @Validated InvoiceListSearchForm searchForm,
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
				InvoiceListViewForm viewForm = service.createViewForm();
				mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
				super.setAjaxProcResultSuccess();
				return mv;
			}
		}
		// 検索条件に該当する一覧情報を取得
		InvoiceListViewForm viewForm = service.createViewForm();
		viewForm = service.searchInvoiceList(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getInvoiceList())) {
			searchForm.setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの請求書一覧情報を取得
			viewForm = service.searchInvoiceList(viewForm, searchForm);
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
	 * 請求書の削除完了後の請求書一覧表示用
	 * 
	 * @param attributes
	 * @return
	 */
	@RequestMapping(value = "/deleteSuccessRedirect", method = RequestMethod.GET)
	public ModelAndView deleteSuccessRedirect(RedirectAttributes attributes) {
		RedirectViewBuilder builder = new RedirectViewBuilder(attributes, ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.list(null, null)));
		return builder.build(MessageHolder.ofInfo(getMessage(MessageEnum.MSG_I00028, "請求書")));
	}

	/**
	 * 請求書詳細画面表示でエラーになる場合の請求書一覧表示用
	 * 
	 * @param searchForm
	 * @param attributes
	 * @param errMessage
	 * @return
	 */
	@RequestMapping(value = "/displayWhenInvoiceDetailsFails", method = RequestMethod.GET)
	public ModelAndView displayWhenInvoiceDetailsFails(
			@ModelAttribute(SEARCH_FORM_NAME) InvoiceListSearchForm searchForm, RedirectAttributes attributes,
			String errMessage) {
		RedirectViewBuilder builder = new RedirectViewBuilder(attributes,
				ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.list(null, null)));
		return builder.build(MessageHolder.ofError(errMessage));
	}

	/**
	 * 請求書詳細画面遷移
	 * 
	 * @param invoiceSeq
	 * @param accgDocSeq
	 * @param attributes
	 * @return
	 */
	@RequestMapping(value = "/redirectInvoiceDetails", method = RequestMethod.GET)
	public ModelAndView redirectInvoiceDetails(@RequestParam("invoiceSeq") Long invoiceSeq, @RequestParam("accgDocSeq") Long accgDocSeq, RedirectAttributes attributes) {

		String redirectUrl = null;

		// 請求書情報が存在有無で遷移先を指定
		if (service.checkExistsInvoice(invoiceSeq)) {
			// 請求書詳細画面へ
			redirectUrl = ModelAndViewUtils.getRedirectPath(InvoiceDetailController.class, controller -> controller.index(invoiceSeq));
		} else {
			String errMessage;
			// 精算書としてデータが存在するか確認
			if (service.checkExistsStatement(accgDocSeq)) {
				errMessage = StringUtils.lineBreakStr2Code(getMessage(MessageEnum.MSG_E00185, "精算書"));
			} else {
				errMessage = StringUtils.lineBreakStr2Code(getMessage(MessageEnum.MSG_E00184));
			}
			// 請求書一覧画面へ
			redirectUrl = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.displayWhenInvoiceDetailsFails(null, null, errMessage));
		}

		super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_INVOICE_DETAIL_REDIRECT, redirectUrl);
		return null;
	}

	/**
	 * 検索条件フォーム表示
	 * 
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	@RequestMapping(value = "/displayInvoiceListSearchForm", method = RequestMethod.GET)
	public ModelAndView displayInvoiceListSearchForm(@ModelAttribute(SEARCH_FORM_NAME) InvoiceListSearchForm searchForm) {

		ModelAndView mv = null;
		mv = getMyModelAndView(searchForm, INVOICE_LIST_SEARCH_FRAGMENT_VIEW_PATH, SEARCH_FORM_NAME);
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
	@RequestMapping(value = "/initInvoiceSearchCondition", method = RequestMethod.GET)
	public ModelAndView initInvoiceSearchCondition(@ModelAttribute(SEARCH_FORM_NAME) InvoiceListSearchForm searchForm) {

		ModelAndView mv = null;

		// 検索条件初期化
		searchForm.initForm();
		// フィルターは開く
		searchForm.setSearchConditionOpen(true);
		// 表示用項目を設定
		service.setDispProperties(searchForm);
		mv = getMyModelAndView(searchForm, INVOICE_LIST_SEARCH_FRAGMENT_VIEW_PATH, SEARCH_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 請求書一覧検索処理
	 * 
	 * @param searchForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/searchInvoiceList", method = RequestMethod.GET)
	public ModelAndView searchInvoiceList(@ModelAttribute(SEARCH_FORM_NAME) @Validated InvoiceListSearchForm searchForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			InvoiceListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, INVOICE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}

		// 検索時は最初のページを表示
		searchForm.setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する一覧情報を取得
		InvoiceListViewForm viewForm = service.createViewForm();
		viewForm = service.searchInvoiceList(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, INVOICE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 請求書一覧ソート処理
	 * 
	 * @param searchForm
	 * @param result
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/sort", method = RequestMethod.GET)
	public ModelAndView sort(@ModelAttribute(SEARCH_FORM_NAME) @Validated InvoiceListSearchForm searchForm,
			BindingResult result, HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, searchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			InvoiceListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, INVOICE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}

		// ソート時は最初のページを表示
		searchForm.setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する一覧情報を取得
		InvoiceListViewForm viewForm = service.createViewForm();
		viewForm = service.searchInvoiceList(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, INVOICE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 請求書一覧ページング処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/pager", method = RequestMethod.GET)
	public ModelAndView pager(@ModelAttribute(SEARCH_FORM_NAME) @Validated InvoiceListSearchForm searchForm,
			BindingResult result, HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, searchForm)) {
			return null;
		}

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			InvoiceListViewForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, INVOICE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}

		// 検索条件に該当する請求書一覧情報を取得
		InvoiceListViewForm viewForm = service.createViewForm();
		viewForm = service.searchInvoiceList(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getInvoiceList())) {
			searchForm.setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの請求書一覧情報を取得
			viewForm = service.searchInvoiceList(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, INVOICE_LIST_PAGER_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
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
	private boolean existSession(HttpMethod httpMethod, InvoiceListSearchForm searchForm) {

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
