package jp.loioz.app.user.ankenList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.controller.PageableController;
import jp.loioz.app.common.form.PagerForm;
import jp.loioz.app.user.ankenList.form.AnkenListSearchForm;
import jp.loioz.app.user.ankenList.form.AnkenListViewForm;
import jp.loioz.app.user.ankenList.service.AnkenListService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.AnkenListMenu;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.SessionAttrKeyEnum;
import jp.loioz.common.constant.SortConstant.AnkenListSortItem;
import jp.loioz.common.constant.SortConstant.SaibanListSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.groups.AdvisorAnkenListSearchDate;
import jp.loioz.common.validation.groups.AllAnkenListSearchDate;
import jp.loioz.common.validation.groups.AnkenListInitSearch;
import jp.loioz.common.validation.groups.KeijiSaibanListSearchDate;
import jp.loioz.common.validation.groups.MinjiSaibanListSearchDate;
import jp.loioz.common.validation.groups.MyAnkenListSearchDate;

/**
 * 案件一覧画面のコントローラークラス
 */
@Controller
@RequestMapping(value = "user/ankenList")
@SessionAttributes(AnkenListController.SEARCH_FORM_NAME)
public class AnkenListController extends DefaultController implements PageableController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/ankenList/ankenList";

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "ankenListSearchForm";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** すべての案件検索条件 AjaxViewのパス */
	public static final String ALL_ANKEN_SEARCH_FORM_AJAX_PATH = "user/ankenList/ankenListFragment::allAnkenListSearchFormFragment";

	/** 自分の担当案件検索条件 AjaxViewのパス */
	public static final String MY_ANKEN_SEARCH_FORM_AJAX_PATH = "user/ankenList/ankenListFragment::myAnkenListSearchFormFragment";

	/** 顧問案件検索条件 AjaxViewのパス */
	public static final String ADVISOR_ANKEN_SEARCH_FORM_AJAX_PATH = "user/ankenList/ankenListFragment::advisorAnkenListSearchFormFragment";

	/** 民事裁判検索条件 AjaxViewのパス */
	public static final String MINJI_SAIBAN_SEARCH_FORM_AJAX_PATH = "user/ankenList/ankenListFragment::minjiSaibanListSearchFormFragment";

	/** 刑事裁判検索条件 AjaxViewのパス */
	public static final String KEIJI_SAIBAN_SEARCH_FORM_AJAX_PATH = "user/ankenList/ankenListFragment::keijiSaibanListSearchFormFragment";

	/** 案件一覧 AjaxViewのパス */
	public static final String ANKEN_LIST_AJAX_PATH = "user/ankenList/ankenListFragment::ankenListAndPagerWraperFragment";

	/** 民事裁判一覧 AjaxViewのパス */
	public static final String MINJI_SAIBAN_LIST_AJAX_PATH = "user/ankenList/ankenListFragment::minjiSaibanListAndPagerWraperFragment";

	/** 刑事裁判一覧 AjaxViewのパス */
	public static final String KEIJI_SAIBAN_LIST_AJAX_PATH = "user/ankenList/ankenListFragment::keijiSaibanListAndPagerWraperFragment";

	/** 案件一覧ページャのページ情報（定義） */
	public static final String ANKEN_LIST_PAGER_LIST_PAGE = "page";

	/** 案件一覧ページャフラグメントID（定義） */
	public static final String ANKEN_LIST_PAGER_LIST_FRAGMENT_ID = "listFragmentId";

	/** 案件一覧ページャフラグメントID（値） */
	public static final String ANKEN_LIST_PAGER_LIST_FRAGMENT_ID_VALUE = "ankenList";

	/** 案件一覧ページャAjaxファンクション名（定義） */
	public static final String ANKEN_LIST_PAGER_AJAX_FUNC_NAME = "pagingAjaxFuncName";

	/** 案件一覧ページャAjaxファンクション名（値） */
	public static final String ANKEN_LIST_PAGER_AJAX_FUNC_NAME_VALUE = "ankenListPaging";

	/** 案件一覧のサービスクラス */
	@Autowired
	private AnkenListService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	// =========================================================================
	// 案件一覧 メソッド
	// =========================================================================

	/**
	 * 案件一覧画面初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@Validated({AnkenListInitSearch.class}) AnkenListSearchForm searchForm, BindingResult result) {

		// 検索条件を初期化する。
		searchForm.clearSearchForm();
		searchForm.initForm();
		// クイック検索ワードを初期化
		this.resetQuickSearchCondition(searchForm);
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.list(null, null));
	}

	/**
	 * 案件一覧画面クイック検索
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/quick", method = RequestMethod.GET)
	public ModelAndView quick(AnkenListSearchForm searchForm) {

		searchForm.initQuickSearchForm();
		// 「検索条件：顧客ステータス」は「すべて」を設定
		searchForm.getAllAnkenListSearchForm().setAnkenStatus(CommonConstant.ANKEN_STATUS_ALL_CD);
		
		// クイック検索ワードをSessionに保持（ヘッダーに表示する用）
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.QUICK_SEARCH_WORD, searchForm.getQuickSearch());
		
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.list(null, null));
	}
	
	/**
	 * 案件一覧画面表示
	 * 
	 * @param searchForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@Validated({AnkenListInitSearch.class}) AnkenListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		// セッションに検索条件フォームが無い場合は検索条件フォームを初期化する
		if (StringUtils.isEmpty(searchForm.getSelectedAnkenListMenu())) {
			// 検索条件を初期化する。
			searchForm.clearSearchForm();
			searchForm.initForm();
		} else if (result != null && result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			// 選択中のメニューで返すフォームを設定
			if (searchForm.isAnkenListMenu()) {
				AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
				mv = getMyModelAndViewWithErrors(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME, result);
			} else {
				AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
				mv = getMyModelAndViewWithErrors(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME, result);
			}
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// ログインユーザのアカウントタイプに応じて「自分の担当案件」検索条件フォームに「担当弁護士」、「担当事務」をセットする。セッションにあった値も上書きする。
		if (AccountType.LAWYER.equals(SessionUtils.getAccountType())) {
			searchForm.getMyAnkenListSearchForm().setTantoLaywer(SessionUtils.getLoginAccountSeq());
		} else if (AccountType.JIMU.equals(SessionUtils.getAccountType())) {
			searchForm.getMyAnkenListSearchForm().setTantoJimu(SessionUtils.getLoginAccountSeq());
		}

		// 一覧情報を取得
		if (searchForm.isAnkenListMenu()) {
			AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
			viewForm = service.searchAnken(viewForm, searchForm);
			mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		} else {
			AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
			viewForm = service.searchSaiban(viewForm, searchForm);
			mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		}
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件検索条件フォーム表示
	 * 
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	@RequestMapping(value = "/displayAnkenListSearchForm", method = RequestMethod.GET)
	public ModelAndView displayAnkenListSearchForm(AnkenListSearchForm searchForm) {

		ModelAndView mv = null;

		// 選択中のメニューで返す検索条件フォームを設定
		if (AnkenListMenu.All_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			mv = getMyModelAndView(searchForm, ALL_ANKEN_SEARCH_FORM_AJAX_PATH, SEARCH_FORM_NAME);
		} else if (AnkenListMenu.MY_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			mv = getMyModelAndView(searchForm, MY_ANKEN_SEARCH_FORM_AJAX_PATH, SEARCH_FORM_NAME);
		} else if (AnkenListMenu.ADVISOR_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			mv = getMyModelAndView(searchForm, ADVISOR_ANKEN_SEARCH_FORM_AJAX_PATH, SEARCH_FORM_NAME);
		} else if (AnkenListMenu.MINJI_SAIBAN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			mv = getMyModelAndView(searchForm, MINJI_SAIBAN_SEARCH_FORM_AJAX_PATH, SEARCH_FORM_NAME);
		} else if (AnkenListMenu.KEIJI_SAIBAN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			mv = getMyModelAndView(searchForm, KEIJI_SAIBAN_SEARCH_FORM_AJAX_PATH, SEARCH_FORM_NAME);
		}
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * すべての案件一覧検索処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/searchAllAnkenList", method = RequestMethod.GET)
	public ModelAndView searchAllAnkenList(@Validated({AllAnkenListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		// クイック検索ワードを初期化
		this.resetQuickSearchCondition(searchForm);
		
		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
			mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// 検索時は最初のページを表示
		searchForm.getAllAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する案件一覧情報を取得
		AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
		viewForm = service.searchAnken(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * すべての案件メニューを選択
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/selectAllAnkenListMenu", method = RequestMethod.GET)
	public ModelAndView selectAllAnkenListMenu(@Validated({AllAnkenListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		// クイック検索ワードを初期化
		this.resetQuickSearchCondition(searchForm);
		
		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
			mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// 検索条件に該当する案件一覧情報を取得
		AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
		viewForm = service.searchAnken(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getAnkenList())) {
			searchForm.getAllAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの案件一覧情報を取得
			viewForm = service.searchAnken(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * すべての案件検索条件初期化
	 * 
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	@RequestMapping(value = "/initAllAnkenSearchCondition", method = RequestMethod.GET)
	public ModelAndView initAllAnkenSearchCondition(AnkenListSearchForm searchForm) {

		ModelAndView mv = null;

		// すべての案件の検索条件のみ初期化
		searchForm.initAllAnkenListSearchForm();
		mv = getMyModelAndView(searchForm, ALL_ANKEN_SEARCH_FORM_AJAX_PATH, SEARCH_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * すべての案件一覧ページング処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @param pageNumber ページング番号
	 * @return
	 */
	@RequestMapping(value = "/allAnkenPager", method = RequestMethod.GET)
	public ModelAndView allAnkenPager(@Validated({AllAnkenListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result,
			@RequestParam(name = "page", required = true) int pageNumber) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
			mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// ページ番号を検索フォームにセット
		searchForm.getAllAnkenListSearchForm().setPage(pageNumber);

		// 検索条件に該当する案件一覧情報を取得
		AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
		viewForm = service.searchAnken(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getAnkenList())) {
			searchForm.getAllAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの案件一覧情報を取得
			viewForm = service.searchAnken(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 自分の担当案件一覧検索処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/searchMyAnkenList", method = RequestMethod.GET)
	public ModelAndView searchMyAnkenList(@Validated({MyAnkenListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;
		
		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
			mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// 検索時は最初のページを表示
		searchForm.getMyAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する案件一覧情報を取得
		AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
		viewForm = service.searchAnken(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 自分の担当案件メニューを選択
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/selectMyAnkenListMenu", method = RequestMethod.GET)
	public ModelAndView selectMyAnkenListMenu(@Validated({MyAnkenListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		// クイック検索ワードを初期化
		this.resetQuickSearchCondition(searchForm);
		
		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
			mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// 検索条件に該当する案件一覧情報を取得
		AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
		viewForm = service.searchAnken(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getAnkenList())) {
			searchForm.getMyAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの案件一覧情報を取得
			viewForm = service.searchAnken(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 自分の担当案件検索条件初期化
	 * 
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	@RequestMapping(value = "/initMyAnkenSearchCondition", method = RequestMethod.GET)
	public ModelAndView initMyAnkenSearchCondition(AnkenListSearchForm searchForm) {

		ModelAndView mv = null;

		// 自分の担当案件の検索条件のみ初期化
		searchForm.initMyAnkenListSearchForm();
		mv = getMyModelAndView(searchForm, MY_ANKEN_SEARCH_FORM_AJAX_PATH, SEARCH_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 自分の担当案件一覧ページング処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @param pageNumber ページング番号
	 * @return
	 */
	@RequestMapping(value = "/myAnkenPager", method = RequestMethod.GET)
	public ModelAndView myAnkenPager(@Validated({MyAnkenListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result,
			@RequestParam(name = "page", required = true) int pageNumber) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
			mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// ページ番号を検索フォームにセット
		searchForm.getMyAnkenListSearchForm().setPage(pageNumber);

		// 検索条件に該当する案件一覧情報を取得
		AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
		viewForm = service.searchAnken(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getAnkenList())) {
			searchForm.getMyAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの案件一覧情報を取得
			viewForm = service.searchAnken(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 顧問案件一覧検索処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/searchAdvisorAnkenList", method = RequestMethod.GET)
	public ModelAndView searchAdvisorAnkenList(@Validated({AdvisorAnkenListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;
		
		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
			mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// 検索時は最初のページを表示
		searchForm.getAdvisorAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する案件一覧情報を取得
		AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
		viewForm = service.searchAnken(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 顧問案件メニュー選択
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/selectAdvisorAnkenListMenu", method = RequestMethod.GET)
	public ModelAndView selectAdvisorAnkenListMenu(@Validated({AdvisorAnkenListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		// クイック検索ワードを初期化
		this.resetQuickSearchCondition(searchForm);
		
		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
			mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// 検索条件に該当する案件一覧情報を取得
		AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
		viewForm = service.searchAnken(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getAnkenList())) {
			searchForm.getAdvisorAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの案件一覧情報を取得
			viewForm = service.searchAnken(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 顧問案件検索条件初期化
	 * 
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	@RequestMapping(value = "/initAdvisorAnkenSearchCondition", method = RequestMethod.GET)
	public ModelAndView initAdvisorAnkenSearchCondition(AnkenListSearchForm searchForm) {

		ModelAndView mv = null;

		// 顧問案件の検索条件のみ初期化
		searchForm.initAdvisorAnkenListSearchForm();
		mv = getMyModelAndView(searchForm, ADVISOR_ANKEN_SEARCH_FORM_AJAX_PATH, SEARCH_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 顧問案件一覧ページング処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @param pageNumber ページング番号
	 * @return
	 */
	@RequestMapping(value = "/advisorAnkenPager", method = RequestMethod.GET)
	public ModelAndView advisorAnkenPager(@Validated({MyAnkenListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result,
			@RequestParam(name = "page", required = true) int pageNumber) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
			mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// ページ番号を検索フォームにセット
		searchForm.getAdvisorAnkenListSearchForm().setPage(pageNumber);

		// 検索条件に該当する案件一覧情報を取得
		AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
		viewForm = service.searchAnken(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getAnkenList())) {
			searchForm.getAdvisorAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの案件一覧情報を取得
			viewForm = service.searchAnken(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件一覧並び替え処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param pageNumber ページング番号（処理上不要だがPageableControllerではpageが無いとエラーになるため）
	 * @param sortKey 一覧のソートキー
	 * @param sortOrder ソート順
	 * @return
	 */
	@RequestMapping(value = "/sortAnkenList", method = RequestMethod.GET)
	public ModelAndView sortAnkenList(AnkenListSearchForm searchForm,
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "sortKey", required = true) AnkenListSortItem sortKey,
			@RequestParam(name = "sortOrder", required = true) SortOrder sortOrder) {

		ModelAndView mv = null;

		// ページ番号、一覧の並び順を検索条件フォームにセット
		if (AnkenListMenu.All_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 最初のページを表示
			searchForm.getAllAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
			// ソートキー設定
			searchForm.getAllAnkenListSearchForm().setAllAnkenListSortKey(sortKey);
			searchForm.getAllAnkenListSearchForm().setAllAnkenListSortOrder(sortOrder);
		} else if (AnkenListMenu.MY_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 最初のページを表示
			searchForm.getMyAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
			// ソートキー設定
			searchForm.getMyAnkenListSearchForm().setMyAnkenListSortKey(sortKey);
			searchForm.getMyAnkenListSearchForm().setMyAnkenListSortOrder(sortOrder);
		} else {
			// 最初のページを表示
			searchForm.getAdvisorAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
			// ソートキー設定
			searchForm.getAdvisorAnkenListSearchForm().setAdvisorAnkenListSortKey(sortKey);
			searchForm.getAdvisorAnkenListSearchForm().setAdvisorAnkenListSortOrder(sortOrder);
		}
		// 検索条件に該当する案件一覧情報を取得
		AnkenListViewForm.AnkenKeiListViewForm viewForm = service.createAnkenListViewForm();
		viewForm = service.searchAnken(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, ANKEN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	// =========================================================================
	// 裁判一覧 メソッド
	// =========================================================================

	/**
	 * 民事裁判一覧検索処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/searchMinjiSaibanList", method = RequestMethod.GET)
	public ModelAndView searchMinjiSaibanList(@Validated({MinjiSaibanListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
			mv = getMyModelAndView(viewForm, MINJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// 検索時は最初のページを表示
		searchForm.getMinjiSaibanListSearchForm().setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する民事裁判一覧情報を取得
		AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
		viewForm = service.searchSaiban(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, MINJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 民事裁判メニュー選択
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/selectMinjiSaibanListMenu", method = RequestMethod.GET)
	public ModelAndView selectMinjiSaibanListMenu(@Validated({MinjiSaibanListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		// クイック検索ワードを初期化
		this.resetQuickSearchCondition(searchForm);
		
		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
			mv = getMyModelAndView(viewForm, MINJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// 検索条件に該当する裁判一覧情報を取得
		AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
		viewForm = service.searchSaiban(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getSaibanList())) {
			searchForm.getAdvisorAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの案件一覧情報を取得
			viewForm = service.searchSaiban(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, MINJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 民事裁判検索条件初期化
	 * 
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	@RequestMapping(value = "/initMinjiSaibanSearchCondition", method = RequestMethod.GET)
	public ModelAndView initMinjiSaibanSearchCondition(AnkenListSearchForm searchForm) {

		ModelAndView mv = null;

		// 民事裁判の検索条件のみ初期化
		searchForm.initMinjiSaibanListSearchForm();
		mv = getMyModelAndView(searchForm, MINJI_SAIBAN_SEARCH_FORM_AJAX_PATH, SEARCH_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 民事裁判一覧ページング処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @param pageNumber ページング番号
	 * @return
	 */
	@RequestMapping(value = "/minjiSaibanPager", method = RequestMethod.GET)
	public ModelAndView minjiSaibanPager(@Validated({MinjiSaibanListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result,
			@RequestParam(name = "page", required = true) int pageNumber) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
			mv = getMyModelAndView(viewForm, MINJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// ページ番号を検索フォームにセット
		searchForm.getMinjiSaibanListSearchForm().setPage(pageNumber);

		// 検索条件に該当する裁判一覧情報を取得
		AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
		viewForm = service.searchSaiban(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getSaibanList())) {
			searchForm.getMinjiSaibanListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの裁判一覧情報を取得
			viewForm = service.searchSaiban(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, MINJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 民事裁判一覧並び替え処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param pageNumber ページング番号（処理上不要だがPageableControllerではpageが無いとエラーになるため）
	 * @param sortKey 一覧のソートキー
	 * @param sortOrder ソート順
	 * @return
	 */
	@RequestMapping(value = "/sortMinjiSaibanList", method = RequestMethod.GET)
	public ModelAndView sortMinjiSaibanList(AnkenListSearchForm searchForm,
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "sortKey", required = true) SaibanListSortItem sortKey,
			@RequestParam(name = "sortOrder", required = true) SortOrder sortOrder) {

		ModelAndView mv = null;

		// 最初のページを表示
		searchForm.getMinjiSaibanListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
		// ソートキー設定
		searchForm.getMinjiSaibanListSearchForm().setMinjiSaibanListSortKey(sortKey);
		searchForm.getMinjiSaibanListSearchForm().setMinjiSaibanListSortOrder(sortOrder);

		// 検索条件に該当する案件一覧情報を取得
		AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
		viewForm = service.searchSaiban(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, MINJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 刑事裁判一覧検索処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/searchKeijiSaibanList", method = RequestMethod.GET)
	public ModelAndView searchKeijiSaibanList(@Validated({KeijiSaibanListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
			mv = getMyModelAndView(viewForm, KEIJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// 検索時は最初のページを表示
		searchForm.getKeijiSaibanListSearchForm().setPage(PagerForm.DEFAULT_PAGE);

		// 検索条件に該当する刑事裁判一覧情報を取得
		AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
		viewForm = service.searchSaiban(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, KEIJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 刑事裁判メニュー選択
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/selectKeijiSaibanListMenu", method = RequestMethod.GET)
	public ModelAndView selectKeijiSaibanListMenu(@Validated({KeijiSaibanListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		// クイック検索ワードを初期化
		this.resetQuickSearchCondition(searchForm);
		
		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
			mv = getMyModelAndView(viewForm, KEIJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// 検索条件に該当する裁判一覧情報を取得
		AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
		viewForm = service.searchSaiban(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getSaibanList())) {
			searchForm.getAdvisorAnkenListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの案件一覧情報を取得
			viewForm = service.searchSaiban(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, KEIJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 刑事裁判検索条件初期化
	 * 
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	@RequestMapping(value = "/initKeijiSaibanSearchCondition", method = RequestMethod.GET)
	public ModelAndView initKeijiSaibanSearchCondition(AnkenListSearchForm searchForm) {

		ModelAndView mv = null;

		// 刑事裁判の検索条件のみ初期化
		searchForm.initKeijiSaibanListSearchForm();
		mv = getMyModelAndView(searchForm, KEIJI_SAIBAN_SEARCH_FORM_AJAX_PATH, SEARCH_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 刑事裁判一覧ページング処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param result
	 * @param pageNumber ページング番号
	 * @return
	 */
	@RequestMapping(value = "/keijiSaibanPager", method = RequestMethod.GET)
	public ModelAndView keijiSaibanPager(@Validated({KeijiSaibanListSearchDate.class}) AnkenListSearchForm searchForm, BindingResult result,
			@RequestParam(name = "page", required = true) int pageNumber) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
			mv = getMyModelAndView(viewForm, KEIJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// ページ番号を検索フォームにセット
		searchForm.getKeijiSaibanListSearchForm().setPage(pageNumber);

		// 検索条件に該当する裁判一覧情報を取得
		AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
		viewForm = service.searchSaiban(viewForm, searchForm);

		// 検索条件に該当するデータが少なくなっていて該当ページが開けない場合は初期ページで開く
		if (LoiozCollectionUtils.isEmpty(viewForm.getSaibanList())) {
			searchForm.getKeijiSaibanListSearchForm().setPage(PagerForm.DEFAULT_PAGE);
			// 検索条件に該当する初期ページの裁判一覧情報を取得
			viewForm = service.searchSaiban(viewForm, searchForm);
		}

		mv = getMyModelAndView(viewForm, KEIJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 刑事裁判一覧並び替え処理
	 * 
	 * @param searchForm 検索条件フォーム
	 * @param pageNumber ページング番号（処理上不要だがPageableControllerではpageが無いとエラーになるため）
	 * @param sortKey 一覧のソートキー
	 * @param sortOrder ソート順
	 * @return
	 */
	@RequestMapping(value = "/sortKeijiSaibanList", method = RequestMethod.GET)
	public ModelAndView sortKeijiSaibanList(AnkenListSearchForm searchForm,
			@RequestParam(name = "page", required = false) int pageNumber,
			@RequestParam(name = "sortKey", required = true) SaibanListSortItem sortKey,
			@RequestParam(name = "sortOrder", required = true) SortOrder sortOrder) {

		ModelAndView mv = null;

		// 最初のページを表示
		searchForm.getKeijiSaibanListSearchForm().setPage(PagerForm.DEFAULT_PAGE);

		// ソートキー設定
		searchForm.getKeijiSaibanListSearchForm().setKeijiSaibanListSortKey(sortKey);
		searchForm.getKeijiSaibanListSearchForm().setKeijiSaibanListSortOrder(sortOrder);

		// 検索条件に該当する案件一覧情報を取得
		AnkenListViewForm.SaibanKeiListViewForm viewForm = service.createSaibanListViewForm();
		viewForm = service.searchSaiban(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, KEIJI_SAIBAN_LIST_AJAX_PATH, VIEW_FORM_NAME);
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
	 * クイック検索の検索条件を初期化する
	 * 
	 * @param searchForm
	 */
	private void resetQuickSearchCondition(AnkenListSearchForm searchForm) {
		
		// クイック検索ワードを初期化
		searchForm.removeQuickSearchParam();
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.QUICK_SEARCH_WORD, "");
	}
}
