package jp.loioz.app.common.mvc.ankenCustomer.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.groups.Default;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.mvc.ankenCustomer.form.CustomerRegistForm;
import jp.loioz.app.common.mvc.ankenCustomer.service.CustomerRegistCommonService;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.common.service.CommonPersonService;
import jp.loioz.app.common.validation.accessDB.CommonCustomerValidator;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.validation.groups.Hojin;
import jp.loioz.common.validation.groups.Kojin;
import jp.loioz.domain.value.PersonId;
import lombok.NonNull;

/**
 * 案件-顧客共通コントローラー
 */
@Controller
@RequestMapping(value = "common/mvc/ankencustomer")
public class CustomerRegistCommonController extends DefaultController {

	/** 共通顧客登録モーダルのコントローラーパス */
	private static final String CUSTOMER_REGIST_MODAL_PATH = "common/mvc/ankenCustomer/customerRegist";
	/** 共通顧客登録モーダルの顧客登録ビューのフラグメントパス */
	private static final String CUSTOMER_REGIST_FRAGMENT_VIEW_PATH = CUSTOMER_REGIST_MODAL_PATH + "::customerRegistModalFragment";
	/** 共通顧客登録モーダルの顧客登録ビューの入力フォーム名 */
	private static final String CUSTOMER_REGIST＿FRAGMENT_INPUT_FORM_NAME = "registFragmentInputForm";

	/** 共通顧客登録モーダルの顧客検索ビューのフラグメントパス */
	private static final String CUSTOMER_SEARCH_FRAGMENT_VIEW_PATH = CUSTOMER_REGIST_MODAL_PATH + "::customerSearchModalFragment";
	/** 共通顧客登録モーダルの顧客検索ビューの検索フォームオブジェクト */
	private static final String CUSTOMER_SEARCH_FRAGMENT_SEARCH_FORM_NAME = "searchFragmentSearchForm";
	/** 共通顧客登録モーダルの顧客検索ビューの画面表示フォームオブジェクト */
	private static final String CUSTOMER_SEARCH_FRAGMENT_VIEW_FORM_NAME = "searchFragmentViewForm";

	/** 画面サービス */
	@Autowired
	private CustomerRegistCommonService customerRegistCommonService;

	/** 案件共通サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenservice;

	/** 顧客情報関連の共通サービスクラス */
	@Autowired
	private CommonPersonService commonCustomerService;

	/** 顧客 DB整合性チェックサービスクラス */
	@Autowired
	private CommonCustomerValidator commonCustomerValidator;

	// Commonなのでindexメソッドはなし

	// =========================================================================
	// 顧客登録モーダル_顧客登録フラグメント
	// =========================================================================

	/**
	 * 顧客登録モーダルの表示
	 * 
	 * @param ankenId
	 * @param customerType
	 * @return
	 */
	@RequestMapping(value = "/getCustomeRegistModalFragment", method = RequestMethod.GET)
	public ModelAndView getKanyoshaCreateModalFragment(@RequestParam(name = "ankenId") Long ankenId) {

		ModelAndView mv = null;

		CustomerRegistForm.RegistFragmentInputForm inputForm = customerRegistCommonService.createCustomerRegistModalInputForm(ankenId);
		mv = getMyModelAndView(inputForm, CUSTOMER_REGIST_FRAGMENT_VIEW_PATH, CUSTOMER_REGIST＿FRAGMENT_INPUT_FORM_NAME);

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 個人顧客の登録
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registCustomer", params = "kojin", method = RequestMethod.POST)
	public ModelAndView registKojinCustomer(@Validated({Default.class, Kojin.class}) CustomerRegistForm.RegistFragmentInputForm inputForm, BindingResult result) {
		return registCustomer(inputForm, result);
	}

	/**
	 * 法人顧客の登録
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registCustomer", params = "hojin", method = RequestMethod.POST)
	public ModelAndView registHojinCustomer(@Validated({Default.class, Hojin.class}) CustomerRegistForm.RegistFragmentInputForm inputForm, BindingResult result) {
		return registCustomer(inputForm, result);
	}

	/**
	 * 顧客の登録
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	private ModelAndView registCustomer(CustomerRegistForm.RegistFragmentInputForm inputForm, BindingResult result) {
		ModelAndView mv = null;

		// 入力チェックエラー
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, CUSTOMER_REGIST_FRAGMENT_VIEW_PATH, CUSTOMER_REGIST＿FRAGMENT_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		// DBアクセスチェック
		Map<String, String> errorMessageMap = new HashMap<>();
		if (!this.accessDbValidated(inputForm, errorMessageMap)) {
			// 既に対象案件に顧客が紐づいている場合、登録上限を超える場合
			mv = getMyModelAndViewWithErrors(inputForm, CUSTOMER_REGIST_FRAGMENT_VIEW_PATH, CUSTOMER_REGIST＿FRAGMENT_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(errorMessageMap.get("message")));
			return mv;
		}

		try {
			// 登録処理
			customerRegistCommonService.registCustomer(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "顧客"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * DBアクセスを伴うチェック
	 *
	 * @param ankenId
	 * @param response
	 * @return
	 */
	private boolean accessDbValidated(CustomerRegistForm.RegistFragmentInputForm inputForm, Map<String, String> errorMap) {
		boolean valid = true;

		// 登録上限の重複チェック
		if (commonCustomerService.isCustomerAdd(inputForm.getAnkenId())) {
			// 登録上限を超えて登録はエラー
			errorMap.put("message", getMessage(MessageEnum.MSG_E00025));
			valid = false;
		}
		return valid;
	}

	// =========================================================================
	// 顧客検索モーダル_顧客登録フラグメント
	// =========================================================================

	/**
	 * 顧客検索モーダル
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getCustomerSearchModalFragment", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragment(@ModelAttribute(CUSTOMER_SEARCH_FRAGMENT_SEARCH_FORM_NAME) CustomerRegistForm.SearchFragmentSearchForm searchForm) {

		ModelAndView mv = null;

		CustomerRegistForm.SearchFragmentViewForm viewForm = customerRegistCommonService.createSearchFragmentViewForm(searchForm);
		mv = getMyModelAndView(viewForm, CUSTOMER_SEARCH_FRAGMENT_VIEW_PATH, CUSTOMER_SEARCH_FRAGMENT_VIEW_FORM_NAME);

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 顧客検索 -> 登録のプリチェックを行う
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/registSearchResultCustomerPreCheck", method = RequestMethod.GET)
	public Map<String, Object> registSearchResultCustomerPreCheck(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "personId") Long personId) {
		Map<String, Object> response = new HashMap<>();

		// DB整合性チェック
		Map<String, String> errorMsgMap = new HashMap<>();
		if (!accessDbValidated(ankenId, personId, errorMsgMap)) {
			response.put("succeeded", false);
			response.put("message", errorMsgMap.get("message"));
			return response;
		}

		// プリチェックに問題がなければsucceededで返却
		String confirmMsg = "【" + PersonId.of(personId).toString() + "】を登録しますか？";
		response.put("succeeded", true);
		response.put("confirmMsg", confirmMsg);
		return response;
	}

	/**
	 * 顧客検索 -> 顧客の登録処理
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/registSearchResultCustomer", method = RequestMethod.POST)
	public ModelAndView registSearchResultCustomer(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "personId") Long personId) {

		// DB整合性チェック
		Map<String, String> errorMsgMap = new HashMap<>();
		if (!accessDbValidated(ankenId, personId, errorMsgMap)) {
			String errorMsg = errorMsgMap.get("message");
			super.setAjaxProcResultFailure(errorMsg);
			return null;
		}

		try {
			// 顧客検索 -> 案件-顧客登録処理
			customerRegistCommonService.registSearchResultCustomer(ankenId, personId);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "顧客"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * DBの整合性チェック
	 * 
	 * @param ankenId
	 * @param personId
	 * @param errorMsgMap
	 * @return
	 */
	private boolean accessDbValidated(@NonNull Long ankenId, @NonNull Long personId, Map<String, String> errorMsgMap) {
		boolean isValid = true;

		// 名簿の存在チェック
		if (!commonCustomerValidator.existsPerson(personId)) {
			errorMsgMap.put("message", getMessage(MessageEnum.MSG_E00025.getMessageKey()));
			isValid = false;
			return isValid;
		}

		// 名簿の案件紐づけ上限チェック
		if (commonAnkenservice.isAnkenAdd(personId)) {
			errorMsgMap.put("message", getMessage(MessageEnum.MSG_E00025.getMessageKey()));
			isValid = false;
			return isValid;
		}
		// 案件の顧客紐づけ上限チェック
		if (commonCustomerService.isCustomerAdd(ankenId)) {
			errorMsgMap.put("message", getMessage(MessageEnum.MSG_E00025.getMessageKey()));
			isValid = false;
			return isValid;
		}

		return isValid;
	}

}
