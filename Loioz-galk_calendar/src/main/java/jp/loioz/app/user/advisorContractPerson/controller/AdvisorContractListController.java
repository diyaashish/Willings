package jp.loioz.app.user.advisorContractPerson.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.advisorContractPerson.form.AdvisorContractListSearchForm;
import jp.loioz.app.user.advisorContractPerson.form.AdvisorContractListViewForm;
import jp.loioz.app.user.advisorContractPerson.service.AdvisorContractListService;
import jp.loioz.app.user.personManagement.controller.PersonEditController;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 顧問契約一覧画面（名簿）のコントローラークラス
 */
@Controller
@RequestMapping("user/advisorContractPerson")
@SessionAttributes(AdvisorContractListController.SEARCH_FORM_NAME)
public class AdvisorContractListController extends DefaultController {

	/** コントローラと対応するviewパス **/
	public static final String MY_VIEW_PATH = "user/advisorContractPerson/advisorContractList";
	
	/** 顧問契約一覧fragmentのパス */
	private static final String ADVISOR_CONTRACT_LIST_VIEW_FRAGMENT_PATH = "user/advisorContractPerson/advisorContractListFragment::advisorContractListViewFragment";
	
	/** viewで使用するフォームオブジェクト名 **/
	public static final String VIEW_FORM_NAME = "viewForm";
	
	/** 検索条件のFormクラス */
	public static final String SEARCH_FORM_NAME = "searchForm";

	// 顧客共通ヘッダー
	/** 顧客共通ヘッダーのフラグメントパス */
	private static final String CUSTOMER_ANKENSELECTED_FRAGMENT_PATH = "common/customerAnkenSelected::detailInfo";

	/** 顧問契約一覧画面（名簿）のサービスクラス */
	@Autowired
	private AdvisorContractListService service;
	
	/** 検索条件のModelAttribute */
	@ModelAttribute(SEARCH_FORM_NAME)
	AdvisorContractListSearchForm setUpSearchForm() {
		return new AdvisorContractListSearchForm();
	}
	// =========================================================================
	// public メソッド
	// =========================================================================
	
	/**
	 * 初期表示
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) AdvisorContractListSearchForm searchForm) {
		
		Long personId = searchForm.getPersonId();
		boolean  canAccess = service.canAccess(personId);
		if (!canAccess) {
			// アクセス不可の状態（楽観チェックNGの場合）は、名簿画面にリダイレクト
			return ModelAndViewUtils.getRedirectModelAndView(PersonEditController.class, controller -> controller.index(personId));
		}
		
		// 初期化
		searchForm.initForm();
		
		// viewFormの作成
		AdvisorContractListViewForm viewForm = service.createViewForm(searchForm);
		
		// 画面情報をリターン
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}
	
	/**
	 * ページング
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/pager", method = RequestMethod.GET)
	public ModelAndView page(@ModelAttribute(SEARCH_FORM_NAME) AdvisorContractListSearchForm searchForm) {
		
		// viewFormの作成
		AdvisorContractListViewForm viewForm = service.createViewForm(searchForm);
		
		// 画面情報をリターン
		ModelAndView mv = getMyModelAndView(viewForm, ADVISOR_CONTRACT_LIST_VIEW_FRAGMENT_PATH, VIEW_FORM_NAME);
		super.setAjaxProcResultSuccess();
		
		return mv;
	}

	// ▼ サイドメニュー

	/**
	 * 顧客案件の共通ヘッダー部分を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/getCustomerAnkenSelectedView", method = RequestMethod.GET)
	public ModelAndView getCustomerAnkenSelectedView(@RequestParam(name = "personId") Long personId) {

		ModelAndView mv = null;

		try {

			// サイドメニュー画面の取得
			mv = ModelAndViewUtils.getModelAndView(CUSTOMER_ANKENSELECTED_FRAGMENT_PATH);
			// パラメータ情報を設定
			mv.addObject("wrapHeaderCustomerId", personId);

		} catch (Exception ex) {
			// 上記のメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// メソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 顧問契約一覧フラグメントを取得
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getContractListViewFragment", method = RequestMethod.GET)
	public ModelAndView getContractListViewFragment(@ModelAttribute(SEARCH_FORM_NAME) AdvisorContractListSearchForm searchForm) {
		
		// viewFormの作成
		AdvisorContractListViewForm viewForm = service.createViewForm(searchForm);
		
		// 画面情報をリターン
		ModelAndView mv = getMyModelAndView(viewForm, ADVISOR_CONTRACT_LIST_VIEW_FRAGMENT_PATH, VIEW_FORM_NAME);
		super.setAjaxProcResultSuccess();
	
		return mv;
	}
	
}
