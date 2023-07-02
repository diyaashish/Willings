package jp.loioz.app.common.mvc.ankenCustomer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.mvc.ankenCustomer.form.AnkenRegistForm;
import jp.loioz.app.common.mvc.ankenCustomer.service.AnkenRegistCommonService;
import jp.loioz.app.user.ankenManagement.controller.AnkenEditController;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 案件登録共通コントローラー
 */
@Controller
@RequestMapping(value = "common/mvc/ankencustomer")
public class AnkenRegistCommonController extends DefaultController {

	/** 案件登録処理後のレスポンスヘッダー設定値 */
	public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_ANKEN_REGIST_REDIRECT = "100";

	/** 共通案件登録モーダルのコントローラーパス */
	private static final String ANKEN_REGIST_MODAL_PATH = "common/mvc/ankenCustomer/ankenRegist";
	/** 共通案件登録モーダルの案件登録ビューのフラグメントパス */
	private static final String ANKEN_REGIST_FRAGMENT_VIEW_PATH = ANKEN_REGIST_MODAL_PATH + "::ankenRegistModalFragment";
	/** 共通案件登録モーダルの案件登録ビューの入力フォーム名 */
	private static final String ANKEN_REGIST＿FRAGMENT_INPUT_FORM_NAME = "registFragmentInputForm";

	/** 共通案件登録モーダルの案件検索ビューのフラグメントパス */
	private static final String ANKEN_SEARCH_FRAGMENT_VIEW_PATH = ANKEN_REGIST_MODAL_PATH + "::ankenSearchModalFragment";
	/** 共通案件登録モーダルの案件検索ビューの検索フォームオブジェクト */
	private static final String ANKEN_SEARCH_FRAGMENT_SEARCH_FORM_NAME = "searchFragmentSearchForm";
	/** 共通案件登録モーダルの案件検索ビューの画面表示フォームオブジェクト */
	private static final String ANKEN_SEARCH_FRAGMENT_VIEW_FORM_NAME = "searchFragmentViewForm";

	/** 画面サービス */
	@Autowired
	private AnkenRegistCommonService ankenRegistCommonService;

	// Commonなのでindexメソッドはなし

	// =========================================================================
	// 案件登録モーダル_案件登録フラグメント
	// =========================================================================

	/**
	 * 案件登録モーダルの表示
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenRegistModalFragment", method = RequestMethod.GET)
	public ModelAndView getAnkenRegistModalFragment(@RequestParam(name = "personId") Long personId) {

		ModelAndView mv = null;

		AnkenRegistForm.RegistFragmentInputForm inputForm = ankenRegistCommonService.createAnkenRegistModalInputForm(personId);
		mv = getMyModelAndView(inputForm, ANKEN_REGIST_FRAGMENT_VIEW_PATH, ANKEN_REGIST＿FRAGMENT_INPUT_FORM_NAME);

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件の登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registAnken", method = RequestMethod.POST)
	public ModelAndView registAnken(@Validated AnkenRegistForm.RegistFragmentInputForm inputForm, BindingResult result) {
		ModelAndView mv = null;

		// 入力チェックエラー
		if (result.hasErrors()) {
			// 表示用項目のセットアップ
			ankenRegistCommonService.setUpDispProperties(inputForm);
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_REGIST_FRAGMENT_VIEW_PATH, ANKEN_REGIST＿FRAGMENT_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			Long ankenId = ankenRegistCommonService.registAnken(inputForm);

			// 登録処理後の画面遷移先URLをレスポンスヘッダーに設定する ※完了メッセージはクライアント側で設定
			String redirectUrl = ModelAndViewUtils.getRedirectPath(AnkenEditController.class, controller -> controller.indexAfterAnkenRegist(ankenId, null));
			super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_ANKEN_REGIST_REDIRECT, redirectUrl);
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

	}

	// =========================================================================
	// 案件検索モーダル_案件登録フラグメント
	// =========================================================================

	/**
	 * 案件検索モーダル
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getAnkenSearchModalFragment", method = RequestMethod.GET)
	public ModelAndView getAnkenSearchModalFragment(@ModelAttribute(ANKEN_SEARCH_FRAGMENT_SEARCH_FORM_NAME) AnkenRegistForm.SearchFragmentSearchForm searchForm) {

		ModelAndView mv = null;

		AnkenRegistForm.SearchFragmentViewForm viewForm = ankenRegistCommonService.createSearchFragmentViewForm(searchForm);
		mv = getMyModelAndView(viewForm, ANKEN_SEARCH_FRAGMENT_VIEW_PATH, ANKEN_SEARCH_FRAGMENT_VIEW_FORM_NAME);

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 顧客検索 -> 顧客の登録処理
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/registSearchResultAnken", method = RequestMethod.POST)
	public ModelAndView registSearchResultCustomer(
			@RequestParam(name = "personId") Long personId,
			@RequestParam(name = "ankenId") Long ankenId) {

		try {
			// 顧客検索 -> 案件-顧客登録処理
			ankenRegistCommonService.registSearchResultAnken(personId, ankenId);
			// 登録処理後の画面遷移先URLをレスポンスヘッダーに設定する ※完了メッセージはクライアント側で設定
			String redirectUrl = ModelAndViewUtils.getRedirectPath(AnkenEditController.class, controller -> controller.indexAndCustomerId(ankenId, personId));
			super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_ANKEN_REGIST_REDIRECT, redirectUrl);
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

}
