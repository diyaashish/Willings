package jp.loioz.app.user.officeAccountSetting.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.officeAccountSetting.form.OfficeAccountInviteForm;
import jp.loioz.app.user.officeAccountSetting.service.OfficeAccountInviteService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.entity.TInvitedAccountVerificationEntity;

/**
 * loiozにメンバーを招待する画面のコントローラークラス
 */
@Controller
@RequestMapping("user/officeAccountSetting")
public class OfficeAccountInviteController extends DefaultController {
	/** コントローラーに対するviewパス */
	private static final String MY_VIEW_PATH = "user/officeAccountSetting/officeAccountInviteModal";

	/** コントローラーに対するviewパス */
	private static final String AJAX_MODAL_PATH = MY_VIEW_PATH + "::select-modal";

	/** viewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME = "accountMailForm";

	/** アカウントサービス **/
	@Autowired
	private OfficeAccountInviteService service;

	@ModelAttribute(VIEW_FORM_NAME)
	OfficeAccountInviteForm setUpEditForm() {
		return new OfficeAccountInviteForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 招待メールモーダルの表示
	 *
	 * @return
	 */
	@RequestMapping(value = "/invite", method = RequestMethod.GET)
	public ModelAndView mail() {
		// viewFormの作成
		OfficeAccountInviteForm viewForm = service.createViewForm();
		return getMyModelAndView(viewForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 招待の取り消し
	 *
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/inviteCancel", method = RequestMethod.POST)
	public Map<String, Object> mailCancel(OfficeAccountInviteForm form) {

		Map<String, Object> response = new HashMap<>();

		// DBアクセスチェック
		if (this.checkaccessDBValidatedForAccountMailCancel(form, response)) {
			// 整合性エラーが発生した場合
			return response;
		}

		try {
			// 招待アカウント認証_削除処理
			service.deleteMailAddressisData(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00041, "アカウント招待"));
			return response;

		} catch (AppException e) {
			// 送信に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 送信
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sendInviteMail", method = RequestMethod.POST)
	public Map<String, Object> sendMailAccount(@Validated @ModelAttribute(VIEW_FORM_NAME) OfficeAccountInviteForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			response.put("mailModalRefresh", false);
			return response;
		}

		// 画面独自のバリデーションチェック
		if (this.inputFormValidatedForAccountMailSave(form, response)) {
			// エラーが発生した場合
			return response;
		}

		// DBアクセスチェック
		if (this.accessDBValidatedForAccountMailSave(form, response)) {
			// エラーが発生した場合
			return response;
		}

		try {
			// 送信処理
			ArrayList<TInvitedAccountVerificationEntity> tInvitedAccountVerificationEntity = service.send(form);

			// メール送信作成
			service.sendAccountForgetRequestMail(tInvitedAccountVerificationEntity);

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00131));
			return response;

		} catch (AppException e) {
			// 送信に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			response.put("mailModalRefresh", false);
			return response;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 画面独自のバリデーションチェック（招待メール送信（保存）用）
	 * 
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean inputFormValidatedForAccountMailSave(OfficeAccountInviteForm viewForm, Map<String, Object> response) {
		boolean error = false;

		// 全て未入力の場合はエラーにする
		if (service.checkMailAddressIsEmpty(viewForm)) {
			// ライセンスを全て利用している場合
			response.put("succeeded", false);
			String message = getMessage(MessageEnum.MSG_E00150, "送信先メールアドレス", "1件以上");
			response.put("message", message);
			response.put("mailModalRefresh", false);
			return true;
		}

		// フォーム上の同一のメールアドレスが入力されているか？
		if (service.checkFormMailAddressIsDuplicat(viewForm)) {
			// ライセンスを全て利用している場合
			response.put("succeeded", false);
			String message = getMessage(MessageEnum.MSG_E00151);
			response.put("message", message);
			response.put("mailModalRefresh", false);
			return true;
		}

		return error;
	}

	/**
	 * DBアクセスを伴うチェック（招待メール送信（保存）用）
	 *
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean accessDBValidatedForAccountMailSave(OfficeAccountInviteForm viewForm, Map<String, Object> response) {
		boolean error = false;

		// DB上の同一のメールアドレスが入力されているか？
		if (service.checkDbMailAddressIsDuplicat(viewForm)) {
			// ライセンスを全て利用している場合
			response.put("succeeded", false);
			String message = getMessage(MessageEnum.MSG_E00151);
			response.put("message", message);
			response.put("mailModalRefresh", true);
			return true;
		}

		// ライセンス数制限チェック
		if (service.checkLicenseLimitForAccountMailSave(viewForm)) {
			// ライセンスを全て利用している場合
			response.put("succeeded", false);
			String message = getMessage(MessageEnum.MSG_E00149);
			response.put("message", message);
			response.put("mailModalRefresh", true);
			return true;
		}

		return error;
	}

	/**
	 * DBアクセスを伴うチェック（招待アカウントの取り消し）
	 *
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean checkaccessDBValidatedForAccountMailCancel(OfficeAccountInviteForm viewForm, Map<String, Object> response) {

		// 認証キーの存在チェックをする
		MessageEnum errorMessageEnum = service.checkExistInvitedAccountVerification(viewForm.getVerificationkey());
		if (errorMessageEnum == null) {
			return false;
		}

		response.put("succeeded", false);
		String message = getMessage(errorMessageEnum, "このメールアドレスへの招待");
		response.put("message", message);

		return true;
	}

}