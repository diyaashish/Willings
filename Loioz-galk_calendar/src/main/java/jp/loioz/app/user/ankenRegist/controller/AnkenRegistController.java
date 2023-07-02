package jp.loioz.app.user.ankenRegist.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.user.ankenRegist.form.AnkenRegistInputForm;
import jp.loioz.app.user.ankenRegist.service.AnkenRegistService;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.validation.groups.Hojin;
import jp.loioz.common.validation.groups.Kojin;

/**
 * 案件新規登録画面のcontrollerクラス
 *
 */
@Controller
@RequestMapping(value = "user/ankenRegist")
public class AnkenRegistController extends DefaultController {

	/** 新規登録に対応するviewパス */
	private static final String MY_VIEW_PATH = "user/ankenRegist/ankenRegist";

	/** 案件基本情報入力フォーム売上計上先選択肢fragmentのパス */
	private static final String ANKEN_BASIC_LAWYER_OPTIONS_FRAGMENT_PATH = "user/ankenRegist/ankenRegistFragment::lawyerOptionList";

	/** 顧客情報fragmentのパス */
	private static final String CUSTOMER_BASIC_FRAGMENT_PATH = "user/ankenRegist/ankenRegistFragment::customerRegistBasicInputFragment";

	/** 選択済み顧客情報エリアfragmentのパス */
	private static final String CUSTOMER_LIST_FRAGMENT_PATH = "user/ankenRegist/ankenRegistFragment::selectedCustomerFragment";

	/** 案件基本情報の入力用フォームオブジェクト名 */
	private static final String ANKEN_REGIST_BASIC_INPUT_FORM_NAME = "ankenRegistBasicInputForm";

	/** 顧客情報オブジェクト名 */
	private static final String CUSTOMER_LIST_NAME = "customerList";

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyInputForm";

	/** 新規登録のserviceクラス */
	@Autowired
	private AnkenRegistService service;

	/** 共通案件サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {

		AnkenRegistInputForm inputform = new AnkenRegistInputForm();

		// 基本情報フォームを設定
		AnkenRegistInputForm.AnkenRegistBasicInputForm basicInputForm = service.createNewAnkenBasicInputForm();
		inputform.setAnkenRegistBasicInputForm(basicInputForm);

		ModelAndView mv = ModelAndViewUtils.getModelAndView(MY_VIEW_PATH);
		// フォームオブジェクトを設定
		mv.addObject(ANKEN_REGIST_BASIC_INPUT_FORM_NAME, basicInputForm);

		return mv;
	}

	/**
	 * 売上計上先の自動選択処理
	 *
	 * @param ankenType
	 * @return
	 */
	@RequestMapping(value = "/changeType", method = RequestMethod.GET)
	public ModelAndView setSalesOwner(@RequestParam("ankenType") String ankenType) {

		ModelAndView mv = null;

		try {
			// 画面表示情報を取得
			AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm = new AnkenRegistInputForm.AnkenRegistBasicInputForm();

			// 表示用プロパティの設定
			service.setDisplayData(inputForm, AnkenType.of(ankenType));
			mv = getMyModelAndView(inputForm, ANKEN_BASIC_LAWYER_OPTIONS_FRAGMENT_PATH, ANKEN_REGIST_BASIC_INPUT_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 【個人】顧客追加処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/addKojinCustomer", method = RequestMethod.POST)
	public ModelAndView addKojinCustomer(@Validated({Kojin.class}) AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, CUSTOMER_BASIC_FRAGMENT_PATH, ANKEN_REGIST_BASIC_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		service.setKojinCustomer(inputForm);

		mv = getMyModelAndView(inputForm.getCustomerList(), CUSTOMER_LIST_FRAGMENT_PATH, CUSTOMER_LIST_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 【法人】顧客追加処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/addHojinCustomer", method = RequestMethod.POST)
	public ModelAndView addHojinCustomer(@Validated({Hojin.class}) AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, CUSTOMER_BASIC_FRAGMENT_PATH, ANKEN_REGIST_BASIC_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		service.setHojinCustomer(inputForm);

		mv = getMyModelAndView(inputForm.getCustomerList(), CUSTOMER_LIST_FRAGMENT_PATH, CUSTOMER_LIST_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 選択中の顧客削除処理
	 * 
	 * @param inputForm
	 * @param deleteIndex
	 * @return
	 */
	@RequestMapping(value = "/deleteCustomer", method = RequestMethod.POST)
	public ModelAndView deleteCustomer(AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm,
			@RequestParam(name = "deleteCustomerIndex", required = true) int deleteIndex) {

		ModelAndView mv = null;

		service.deleteCustomer(inputForm, deleteIndex);

		mv = getMyModelAndView(inputForm.getCustomerList(), CUSTOMER_LIST_FRAGMENT_PATH, CUSTOMER_LIST_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/registAnkenCustomerBasic", method = RequestMethod.POST)
	public Map<String, Object> registAnkenCustomerBasic(@Validated AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		commonAnkenService.validateTanto(inputForm.getSalesOwner(), result, "salesOwner", "売上計上先");
		commonAnkenService.validateTanto(inputForm.getTantoLawyer(), result, "tantoLawyer", "担当弁護士");
		commonAnkenService.validateTanto(inputForm.getTantoJimu(), result, "tantoJimu", "担当事務");

		commonAnkenService.validateSalesOwnerCount(inputForm.getSalesOwner(), result, "salesOwner");

		// 入力バリデーション
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 登録処理
		Long ankenId;
		try {
			ankenId = service.registAnkenCustomerBasic(inputForm);
			response.put("succeeded", true);
			response.put("ankenId", ankenId);
			response.put("message", getMessage(MessageEnum.MSG_I00034, "案件"));
			return response;
		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 登録済みの名簿を検索します
	 * 
	 * @param searchWord
	 * @param inputForm
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/searchCustomer", method = RequestMethod.GET)
	public Map<String, Object> searchCustomer(@RequestParam(name = "searchWord", required = true) String searchWord,
			AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm) {

		Map<String, Object> response = new HashMap<>();

		// 検索ワードが名前、名前（かな）に該当する名簿一覧を取得
		response.put("customerList", service.getCustomer(searchWord, inputForm.getCustomerList()));
		response.put("succeeded", true);
		return response;
	}

	/**
	 * 【検索結果】顧客追加処理
	 * 
	 * @param inputForm
	 * @param addCustomerId
	 * @return
	 */
	@RequestMapping(value = "/addCustomer", method = RequestMethod.POST)
	public ModelAndView addCustomer(AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm,
			@RequestParam(name = "addCustomerId", required = true) Long addCustomerId) {

		ModelAndView mv = null;

		try {
			service.setCustomer(inputForm, addCustomerId);
		} catch (AppException ex) {
			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return mv;
		}

		mv = getMyModelAndView(inputForm.getCustomerList(), CUSTOMER_LIST_FRAGMENT_PATH, CUSTOMER_LIST_NAME);
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

}
