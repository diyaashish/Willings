package jp.loioz.app.user.officeAccountSetting.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.validation.accessDB.CommonAccountValidator;
import jp.loioz.app.user.officeAccountSetting.form.OfficeAccountEditForm;
import jp.loioz.app.user.officeAccountSetting.service.OfficeAccountEditService;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountStatus;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.PasswordDto;

/**
 * アカウント編集画面のコントローラークラス
 */
@Controller
@RequestMapping("user/officeAccountSetting")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class OfficeAccountEditController extends DefaultController {
	/** コントローラーに対するviewパス */
	private static final String MY_VIEW_PATH = "user/officeAccountSetting/officeAccountEditModal";

	/** コントローラーに対するviewパス */
	private static final String AJAX_MODAL_PATH = MY_VIEW_PATH + "::select-modal";

	/** viewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME = "accountEditForm";

	/** バリデーター */
	private static final Validator javaxValidator = Validation.buildDefaultValidatorFactory().getValidator();

	/** Springバリデーター */
	private static final SpringValidatorAdapter validator = new SpringValidatorAdapter(javaxValidator);

	/** アカウントサービス **/
	@Autowired
	private OfficeAccountEditService service;

	/** 共通アカウントDB整合性バリデーター */
	@Autowired
	private CommonAccountValidator commonAccountValidator;

	@ModelAttribute(VIEW_FORM_NAME)
	OfficeAccountEditForm setUpEditForm() {
		return new OfficeAccountEditForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 新規登録モーダルの表示
	 *
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ModelAndView create() {

		// viewFormの作成
		OfficeAccountEditForm viewForm = service.createViewForm();

		return getMyModelAndView(viewForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}

	/**
	 * DBアクセスを伴うチェック
	 *
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean accessDBValidatedForAccountEditSave(OfficeAccountEditForm viewForm, Map<String, Object> response, boolean isRegist) {
		boolean error = false;

		// ライセンス数制限チェック
		if (service.checkLicenseLimitForAccountEditSave(viewForm, isRegist)) {
			// ライセンスを全て利用している場合
			response.put("succeeded", false);
			String message = isRegist ? getMessage(MessageEnum.MSG_E00090) : getMessage(MessageEnum.MSG_E00051);
			response.put("message", message);
			return true;
		}

		// アカウントIDの重複チェック
		if (commonAccountValidator.checkAccountIdExists(
				viewForm.getAccountEditData().getAccountId(), viewForm.getAccountEditData().getAccountSeq())) {
			// アカウントIDがすでに使用されている
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00031, "アカウントID"));
			return true;
		}

		return error;
	}

	/**
	 * 新規登録
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	public Map<String, Object> regist(@Validated @ModelAttribute(VIEW_FORM_NAME) OfficeAccountEditForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// パスワードチェック
		this.passWordValidator(form, result);

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// DBアクセスチェック
		if (this.accessDBValidatedForAccountEditSave(form, response, true)) {
			// 整合性エラーが発生した場合
			return response;
		}

		try {
			// 登録処理
			service.regist(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "アカウント"));
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 更新モーダル表示
	 *
	 * @param viewForm
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public ModelAndView edit(OfficeAccountEditForm viewForm) {

		// 更新するデータをセットする
		viewForm = service.setData(viewForm);
		return getMyModelAndView(viewForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 更新
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Map<String, Object> update(@Validated @ModelAttribute(VIEW_FORM_NAME) OfficeAccountEditForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// パスワードチェックを行う
		this.passWordValidator(form, result);

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// DBアクセスチェック
		if (this.accessDBValidatedForAccountEditSave(form, response, false)) {
			// 整合性エラーが発生した場合
			return response;
		}

		try {
			// 更新
			service.update(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "アカウント情報"));
			return response;

		} catch (AppException e) {
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * パスワードのバリデーションチェックを追加します。
	 *
	 * @param form
	 * @param result
	 */
	private void passWordValidator(OfficeAccountEditForm form, BindingResult result) {

		String addFieldName = "accountEditData.password.";
		PasswordDto passwordDto = form.getAccountEditData().getPassword();

		// 更新時は入力されたときのみ判定を行う
		if (!form.isNew() && StringUtils.isAllEmpty(passwordDto.getPassword(), passwordDto.getConfirm())) {
			return;
		}

		BeanPropertyBindingResult passwordErrors = new BeanPropertyBindingResult(passwordDto, passwordDto.getClass().getName());
		validator.validate(passwordDto, passwordErrors);

		// FieldErrorをbindingRresultにaddする
		List<FieldError> passwordFieldErrors = passwordErrors.getFieldErrors();
		if (!ListUtils.isEmpty(passwordFieldErrors)) {
			for (FieldError error : passwordFieldErrors) {
				FieldError addError = new FieldError(form.getClass().getName(), addFieldName + error.getField(), error.getDefaultMessage());
				result.addError(addError);
			}
		}

		// パスワード相関チェックはFieldErrorとして検知できないので、ObjectErrorの内容をaddする
		passwordErrors.getAllErrors().stream()
				.filter(error -> !(error instanceof FieldError))
				.map(error -> new FieldError(form.getClass().getName(), "accountEditData.password", error.getDefaultMessage()))
				.forEach(result::addError);
	}

	/**
	 * アカウントの有効／停止
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
	public Map<String, Object> changeStatus(@Validated @ModelAttribute(VIEW_FORM_NAME) OfficeAccountEditForm form, BindingResult result) {
		Map<String, Object> response = new HashMap<>();
		
		// DBアクセスチェック
		if (this.accessDBValidatedForChangeStatus(form, response)) {
			// 整合性エラーが発生した場合
			return response;
		}
		
		try {
			// アカウントの有効／停止の切り替え
			service.changeStatus(form);
			response.put("succeeded", true);

			String accountStatus = form.getAccountEditData().getAccountStatus();
			String msg = "";
			if (AccountStatus.ENABLED.equalsByCode(accountStatus)) {
				// 有効 -> 無効
				msg = AccountStatus.DISABLED.getVal();
			} else {
				// 無効 -> 有効
				msg = AccountStatus.ENABLED.getVal();
			}
			msg = String.format("%s%s", msg, "に");
			response.put("message", getMessage(MessageEnum.MSG_I00031, msg));// アカウントを有効/無効にしました

			// ログインユーザーが自分を削除した場合
			if (form.getAccountEditData().getAccountSeq().equals(SessionUtils.getLoginAccountSeq())) {
				response.put("accountDeleted", true);
			}

			return response;

		} catch (AppException e) {
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}

	}
	
	/**
	 * DBアクセスを伴うチェック
	 *
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean accessDBValidatedForChangeStatus(OfficeAccountEditForm viewform, Map<String, Object> response) {
		boolean error = false;

		// ライセンス数制限チェック
		if (service.checkLicenseLimitForChangeStatus(viewform)) {
			// 利用可能なライセンス数を超える場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00051));
			return true;
		}

		return error;
	}
}