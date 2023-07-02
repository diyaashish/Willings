package jp.loioz.app.user.meiboList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.controller.PageableController;
import jp.loioz.app.user.meiboList.form.MeiboListSearchForm;
import jp.loioz.app.user.meiboList.form.MeiboListViewForm;
import jp.loioz.app.user.meiboList.service.MeiboListService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.SessionAttrKeyEnum;
import jp.loioz.common.constant.meiboList.MeiboListConstant.MeiboMenu;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;

/**
 * 名簿一覧画面のコントローラークラス
 */
@Controller
@RequestMapping(value = "user/meiboList")
@SessionAttributes(MeiboListController.SEARCH_FORM_NAME)
public class MeiboListController extends DefaultController implements PageableController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/meiboList/meiboList";

	/** 名簿メニューのフラグメントviewのパス */
	private static final String MEIBO_MENU_FRAGMENT_VIEW_PATH = "user/meiboList/meiboMenu::meiboMenuFragment";

	/** 名簿検索条件のフラグメントviewのパス */
	private static final String ALL_MEIBO_SEARCH_FRAGMENT_VIEW_PATH = "user/meiboList/meiboListFragment::allMeiboSearchFragment";
	private static final String CUSTOMER_ALL_MEIBO_SEARCH_FRAGMENT_VIEW_PATH = "user/meiboList/meiboListFragment::customerAllMeiboSearchFragment";
	private static final String CUSTOMER_KOJIN_MEIBO_SEARCH_FRAGMENT_VIEW_PATH = "user/meiboList/meiboListFragment::customerKojinMeiboSearchFragment";
	private static final String CUSTOMER_HOJIN_MEIBO_SEARCH_FRAGMENT_VIEW_PATH = "user/meiboList/meiboListFragment::customerHojinMeiboSearchFragment";
	private static final String ADVISOR_MEIBO_SEARCH_FRAGMENT_VIEW_PATH = "user/meiboList/meiboListFragment::advisorMeiboSearchFragment";
	private static final String BENGOSHI_MEIBO_SEARCH_FRAGMENT_VIEW_PATH = "user/meiboList/meiboListFragment::bengoshiMeiboSearchFragment";

	/** 名簿一覧とページングのフラグメントviewのパス */
	private static final String ALL_MEIBO_LIST_PAGER_FRAGMENT_VIEW_PATH = "user/meiboList/meiboListFragment::allMeiboListAndPagerWraperFragment";
	private static final String CUSTOMER_ALL_MEIBO_LIST_PAGER_FRAGMENT_VIEW_PATH = "user/meiboList/meiboListFragment::customerAllMeiboListAndPagerWraperFragment";
	private static final String CUSTOMER_KOJIN_MEIBO_LIST_PAGER_FRAGMENT_VIEW_PATH = "user/meiboList/meiboListFragment::customerKojinMeiboListAndPagerWraperFragment";
	private static final String CUSTOMER_HOJIN_MEIBO_LIST_PAGER_FRAGMENT_VIEW_PATH = "user/meiboList/meiboListFragment::customerHojinMeiboListAndPagerWraperFragment";
	private static final String ADVISOR_MEIBO_LIST_PAGER_FRAGMENT_VIEW_PATH = "user/meiboList/meiboListFragment::advisorMeiboListAndPagerWraperFragment";
	private static final String BENGOSHI_MEIBO_LIST_PAGER_FRAGMENT_VIEW_PATH = "user/meiboList/meiboListFragment::bengoshiMeiboListAndPagerWraperFragment";

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "meiboListSearchForm";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 名簿メニューのフラグメントフォームオブジェクト名 */
	private static final String MEIBO_MENU_FRAGMENT_VIEW_FORM_NAME = "meiboMenuViewForm";
	/** 名簿検索のフラグメントフォームオブジェクト名 */
	private static final String MEIBO_SEARCH_FRAGMENT_VIEW_FORM_NAME = "meiboSearchViewForm";
	/** 名簿一覧のフラグメントフォームオブジェクト名 */
	private static final String ALL_MEIBO_LIST_FRAGMENT_VIEW_FORM_NAME = "allMeiboListViewForm";
	private static final String CUSTOMER_ALL_MEIBO_LIST_FRAGMENT_VIEW_FORM_NAME = "customerAllMeiboListViewForm";
	private static final String CUSTOMER_KOJIN_MEIBO_LIST_FRAGMENT_VIEW_FORM_NAME = "customerKojinMeiboListViewForm";
	private static final String CUSTOMER_HOJIN_MEIBO_LIST_FRAGMENT_VIEW_FORM_NAME = "customerHojinMeiboListViewForm";
	private static final String ADVISOR_MEIBO_LIST_FRAGMENT_VIEW_FORM_NAME = "advisorMeiboListViewForm";
	private static final String BENGOSHI_MEIBO_LIST_FRAGMENT_VIEW_FORM_NAME = "bengoshiMeiboListViewForm";

	/** 名簿一覧のサービスクラス */
	@Autowired
	private MeiboListService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 名簿一覧画面初期表示
	 * 
	 * @param searchForm
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(MeiboListSearchForm searchForm) {

		// セッションの検索条件初期化
		searchForm.initForm();
		// クイック検索ワードを初期化
		this.resetQuickSearchCondition(searchForm);
		
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.list(null));
	}

	/**
	 * 顧客名簿画面初期表示
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/customerAll", method = RequestMethod.GET)
	public ModelAndView customerAll(MeiboListSearchForm searchForm) {

		// 顧客名簿画面を開く用
		searchForm.initForm(MeiboMenu.CUSTOMER_ALL.getCd());
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.list(null));
	}

	/**
	 * 名簿一覧画面クイック検索
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/quick", method = RequestMethod.GET)
	public ModelAndView quick(MeiboListSearchForm searchForm) {

		searchForm.initQuickSearchForm();
		// クイック検索ワードをSessionに保持（ヘッダーに表示する用）
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.QUICK_SEARCH_WORD, searchForm.getQuickSearch());
		
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.list(null));
	}

	/**
	 * 名簿一覧の画面表示
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(MeiboListSearchForm searchForm) {

		// セッションに検索条件が無ければ検索条件フォームを初期化する（前回検索条件があれば、その結果を表示する）
		if (searchForm.getAllMeiboListSearchForm() == null) {
			searchForm.initForm();
		}

		// メニューの取得
		MeiboMenu meiboMenu = MeiboMenu.of(searchForm.getMeiboMenuCd());

		ModelAndView mv = null;

		MeiboListViewForm viewForm = service.createViewForm();

		// 名簿メニュ画面表示用オブジェクトを設定
		MeiboListViewForm.MeiboMenuViewForm meiboMenuViewForm = service.createMeiboMenuViewForm();
		viewForm.setMeiboMenuViewForm(meiboMenuViewForm);

		// 名簿検索条件画面表示用オブジェクトを設定
		MeiboListViewForm.MeiboSearchViewForm meiboSearchViewForm = service.createMeiboSearchViewForm(meiboMenu);
		viewForm.setMeiboSearchViewForm(meiboSearchViewForm);

		// 名簿一覧画面表示用オブジェクトの設定
		switch (meiboMenu) {
		case ALL:
			MeiboListViewForm.AllMeiboListViewForm allMeiboListViewForm = service.createAllMeiboListViewForm(searchForm);
			viewForm.setAllMeiboListViewForm(allMeiboListViewForm);
			break;
		case CUSTOMER_ALL:
			MeiboListViewForm.CustomerAllMeiboListViewForm customerAllMeiboListViewForm = service.createCustomerAllMeiboListViewForm(searchForm);
			viewForm.setCustomerAllMeiboListViewForm(customerAllMeiboListViewForm);
			break;
		case CUSTOMER_KOJIN:
			MeiboListViewForm.CustomerKojinMeiboListViewForm customerKojinMeiboListViewForm = service.createCustomerKojinMeiboListViewForm(searchForm);
			viewForm.setCustomerKojinMeiboListViewForm(customerKojinMeiboListViewForm);
			break;
		case CUSTOMER_HOJIN:
			MeiboListViewForm.CustomerHojinMeiboListViewForm customerHojinMeiboListViewForm = service.createCustomerHojinMeiboListViewForm(searchForm);
			viewForm.setCustomerHojinMeiboListViewForm(customerHojinMeiboListViewForm);
			break;
		case ADVISOR:
			MeiboListViewForm.AdvisorMeiboListViewForm advisorMeiboListViewForm = service.createAdvisorMeiboListViewForm(searchForm);
			viewForm.setAdvisorMeiboListViewForm(advisorMeiboListViewForm);
			break;
		case BENGOSHI:
			MeiboListViewForm.BengoshiMeiboListViewForm bengoshiMeiboListViewForm = service.createBengoshiMeiboListViewForm(searchForm);
			viewForm.setBengoshiMeiboListViewForm(bengoshiMeiboListViewForm);
			break;
		default:
			break;
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
	 * 名簿メニューの選択
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/changeMenu", method = RequestMethod.GET)
	public ModelAndView changeMenu(MeiboListSearchForm searchForm) {

		// クイック検索の検索条件を初期化
		this.resetQuickSearchCondition(searchForm);
		
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.getMeiboMenuFragment(null));
	}

	/**
	 * 名簿一覧の検索
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView search(MeiboListSearchForm searchForm) {

		// クイック検索の検索条件を初期化
		this.resetQuickSearchCondition(searchForm);
		
		searchForm.defaultPage();
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.getMeiboListFragment(null));
	}

	/**
	 * 名簿一覧のソート
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/sort", method = RequestMethod.GET)
	public ModelAndView sort(MeiboListSearchForm searchForm) {

		searchForm.defaultPage();
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.getMeiboListFragment(null));
	}

	/**
	 * 名簿メニューの画面情報を取得する。
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getMeiboMenuFragment", method = RequestMethod.GET)
	public ModelAndView getMeiboMenuFragment(MeiboListSearchForm searchForm) {

		ModelAndView mv = null;
		MeiboListViewForm.MeiboMenuViewForm viewForm = service.createMeiboMenuViewForm();

		mv = getMyModelAndView(viewForm, MEIBO_MENU_FRAGMENT_VIEW_PATH, MEIBO_MENU_FRAGMENT_VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 名簿メニューの画面情報を取得する。
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getMeiboSearchFragment", method = RequestMethod.GET)
	public ModelAndView getMeiboSearchFragment(MeiboListSearchForm searchForm) {

		ModelAndView mv = null;
		mv = this.getMeiboSearchFragmentMv(searchForm);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 名簿メニューの画面情報を取得する。
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getMeiboListFragment", method = RequestMethod.GET)
	public ModelAndView getMeiboListFragment(MeiboListSearchForm searchForm) {

		ModelAndView mv = null;
		mv = this.getMeiboListFragmentMv(searchForm);
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
	 * 名簿検索条件
	 * 
	 * @param searchForm
	 * @return
	 */
	private ModelAndView getMeiboSearchFragmentMv(MeiboListSearchForm searchForm) {

		MeiboMenu meiboMenu = MeiboMenu.of(searchForm.getMeiboMenuCd());
		switch (meiboMenu) {
		case ALL:
			MeiboListViewForm.MeiboSearchViewForm allMeiboSearchViewForm = service.createAllMeiboSearchViewForm();
			return getMyModelAndView(allMeiboSearchViewForm, ALL_MEIBO_SEARCH_FRAGMENT_VIEW_PATH, MEIBO_SEARCH_FRAGMENT_VIEW_FORM_NAME);
		case CUSTOMER_ALL:
			MeiboListViewForm.MeiboSearchViewForm customerAllMeiboSearchViewForm = service.createCustomerAllMeiboSearchViewForm();
			return getMyModelAndView(customerAllMeiboSearchViewForm, CUSTOMER_ALL_MEIBO_SEARCH_FRAGMENT_VIEW_PATH, MEIBO_SEARCH_FRAGMENT_VIEW_FORM_NAME);
		case CUSTOMER_KOJIN:
			MeiboListViewForm.MeiboSearchViewForm customerKojinMeiboSearchViewForm = service.createCustomerKojinMeiboSearchViewForm();
			return getMyModelAndView(customerKojinMeiboSearchViewForm, CUSTOMER_KOJIN_MEIBO_SEARCH_FRAGMENT_VIEW_PATH, MEIBO_SEARCH_FRAGMENT_VIEW_FORM_NAME);
		case CUSTOMER_HOJIN:
			MeiboListViewForm.MeiboSearchViewForm customerHojinMeiboSearchViewForm = service.createCustomerHojinMeiboSearchViewForm();
			return getMyModelAndView(customerHojinMeiboSearchViewForm, CUSTOMER_HOJIN_MEIBO_SEARCH_FRAGMENT_VIEW_PATH, MEIBO_SEARCH_FRAGMENT_VIEW_FORM_NAME);
		case ADVISOR:
			MeiboListViewForm.MeiboSearchViewForm advisorMeiboSearchViewForm = service.createAdvisorMeiboSearchViewForm();
			return getMyModelAndView(advisorMeiboSearchViewForm, ADVISOR_MEIBO_SEARCH_FRAGMENT_VIEW_PATH, MEIBO_SEARCH_FRAGMENT_VIEW_FORM_NAME);
		case BENGOSHI:
			MeiboListViewForm.MeiboSearchViewForm bengoshiMeiboSearchViewForm = service.createBengoshiMeiboSearchViewForm();
			return getMyModelAndView(bengoshiMeiboSearchViewForm, BENGOSHI_MEIBO_SEARCH_FRAGMENT_VIEW_PATH, MEIBO_SEARCH_FRAGMENT_VIEW_FORM_NAME);
		default:
			return null;
		}
	}

	/**
	 * 名簿一覧の画面情報を取得する
	 * 
	 * @param searchForm
	 * @return
	 */
	private ModelAndView getMeiboListFragmentMv(MeiboListSearchForm searchForm) {

		MeiboMenu meiboMenu = MeiboMenu.of(searchForm.getMeiboMenuCd());
		switch (meiboMenu) {
		case ALL:
			MeiboListViewForm.AllMeiboListViewForm allMeiboListViewForm = service.createAllMeiboListViewForm(searchForm);
			return getMyModelAndView(allMeiboListViewForm, ALL_MEIBO_LIST_PAGER_FRAGMENT_VIEW_PATH, ALL_MEIBO_LIST_FRAGMENT_VIEW_FORM_NAME);
		case CUSTOMER_ALL:
			MeiboListViewForm.CustomerAllMeiboListViewForm customerAllMeiboListViewForm = service.createCustomerAllMeiboListViewForm(searchForm);
			return getMyModelAndView(customerAllMeiboListViewForm, CUSTOMER_ALL_MEIBO_LIST_PAGER_FRAGMENT_VIEW_PATH, CUSTOMER_ALL_MEIBO_LIST_FRAGMENT_VIEW_FORM_NAME);
		case CUSTOMER_KOJIN:
			MeiboListViewForm.CustomerKojinMeiboListViewForm customerKojinMeiboListViewForm = service.createCustomerKojinMeiboListViewForm(searchForm);
			return getMyModelAndView(customerKojinMeiboListViewForm, CUSTOMER_KOJIN_MEIBO_LIST_PAGER_FRAGMENT_VIEW_PATH, CUSTOMER_KOJIN_MEIBO_LIST_FRAGMENT_VIEW_FORM_NAME);
		case CUSTOMER_HOJIN:
			MeiboListViewForm.CustomerHojinMeiboListViewForm customerHojinMeiboListViewForm = service.createCustomerHojinMeiboListViewForm(searchForm);
			return getMyModelAndView(customerHojinMeiboListViewForm, CUSTOMER_HOJIN_MEIBO_LIST_PAGER_FRAGMENT_VIEW_PATH, CUSTOMER_HOJIN_MEIBO_LIST_FRAGMENT_VIEW_FORM_NAME);
		case ADVISOR:
			MeiboListViewForm.AdvisorMeiboListViewForm advisorMeiboListViewForm = service.createAdvisorMeiboListViewForm(searchForm);
			return getMyModelAndView(advisorMeiboListViewForm, ADVISOR_MEIBO_LIST_PAGER_FRAGMENT_VIEW_PATH, ADVISOR_MEIBO_LIST_FRAGMENT_VIEW_FORM_NAME);
		case BENGOSHI:
			MeiboListViewForm.BengoshiMeiboListViewForm bengoshiMeiboListViewForm = service.createBengoshiMeiboListViewForm(searchForm);
			return getMyModelAndView(bengoshiMeiboListViewForm, BENGOSHI_MEIBO_LIST_PAGER_FRAGMENT_VIEW_PATH, BENGOSHI_MEIBO_LIST_FRAGMENT_VIEW_FORM_NAME);
		default:
			return null;
		}
	}
	
	/**
	 * クイック検索の検索条件を初期化する
	 * 
	 * @param searchForm
	 */
	private void resetQuickSearchCondition(MeiboListSearchForm searchForm) {
		
		// クイック検索ワードを初期化
		searchForm.removeQuickSearchParam();
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.QUICK_SEARCH_WORD, "");
	}

}
