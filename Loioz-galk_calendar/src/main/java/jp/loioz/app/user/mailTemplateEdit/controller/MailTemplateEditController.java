package jp.loioz.app.user.mailTemplateEdit.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.mailTemplate.controller.MailTemplateController;
import jp.loioz.app.user.mailTemplateEdit.form.MailTemplateEditInputForm;
import jp.loioz.app.user.mailTemplateEdit.form.MailTemplateEditViewForm;
import jp.loioz.app.user.mailTemplateEdit.service.MailTemplateEditService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.utility.ValidateUtils;

/**
 * メールテンプレートの作成画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/mailTemplateEdit")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class MailTemplateEditController extends DefaultController {

	/** メールテンプレート登録完了のヘッダーメッセージ */
	public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_TEMPLATE_REGIST_SUCCESS_REDIRECT = "regist_success_redirect";

	/** メールテンプレート削除完了のヘッダーメッセージ */
	public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_TEMPLATE_DELETE_SUCCESS_REDIRECT = "delete_success_redirect";

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/mailTemplateEdit/mailTemplateEdit";

	/** メールテンプレートの設定のフラグメントviewのパス */
	private static final String TEMPLATE_EDIT_FRAGMENT_VIEW_PATH = "user/mailTemplateEdit/mailTemplateEditFragment::templateEditFragment";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** メールテンプレート入力フォームオブジェクト名 */
	private static final String TEMPLATE_INPUT_FORM_NAME = "templateInputForm";

	/** メールテンプレートの設定のサービスクラス */
	@Autowired
	private MailTemplateEditService mailTemplateEditService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * メールテンプレートの設定画面初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {

		ModelAndView mv = null;

		MailTemplateEditViewForm viewForm = mailTemplateEditService.createMailTemplateEditViewForm();
		MailTemplateEditInputForm.TemplateInputForm templateInputForm = mailTemplateEditService.createTemplateInputForm();

		mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		mv.addObject(TEMPLATE_INPUT_FORM_NAME, templateInputForm);

		return mv;
	}

	/**
	 * メールテンプレートの設定画面初期表示
	 * 
	 * @param mailTemplateSeq
	 * @return
	 */
	@RequestMapping(value = "/{mailTemplateSeq}/", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable Long mailTemplateSeq) {

		ModelAndView mv = null;

		MailTemplateEditViewForm viewForm = mailTemplateEditService.createMailTemplateEditViewForm(mailTemplateSeq);
		MailTemplateEditInputForm.TemplateInputForm templateInputForm = mailTemplateEditService.createTemplateInputForm(mailTemplateSeq);

		mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		mv.addObject(TEMPLATE_INPUT_FORM_NAME, templateInputForm);

		return mv;
	}

	/**
	 * 登録処理完了後の編集画面遷移用
	 * 
	 * @param mailTemplateSeq
	 * @param attributes
	 * @return
	 */
	@RequestMapping(value = "/{mailTemplateSeq}/registSuccessRedirect", method = RequestMethod.GET)
	public ModelAndView registSuccessRedirect(@PathVariable Long mailTemplateSeq, RedirectAttributes attributes) {
		RedirectViewBuilder builder = new RedirectViewBuilder(attributes, ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.edit(mailTemplateSeq)));
		return builder.build(MessageHolder.ofInfo(getMessage(MessageEnum.MSG_I00020, "メールテンプレート")));
	}

	/**
	 * メールテンプレート編集：画面表示情報の取得
	 * 
	 * @param mailTemplateSeq
	 * @return
	 */
	@RequestMapping(value = "/{mailTemplateSeq}/getEditInputFragment", method = RequestMethod.GET)
	public ModelAndView getEditInputFragment(@PathVariable Long mailTemplateSeq) {

		ModelAndView mv = null;

		MailTemplateEditInputForm.TemplateInputForm templateInputForm = mailTemplateEditService.createTemplateInputForm(mailTemplateSeq);
		mv = getMyModelAndView(templateInputForm, TEMPLATE_EDIT_FRAGMENT_VIEW_PATH, TEMPLATE_INPUT_FORM_NAME);

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * メールテンプレートの登録処理
	 * 
	 * @param mailTemplateSeq
	 * @return
	 */
	@RequestMapping(value = "/registMailTemplate", method = RequestMethod.POST)
	public ModelAndView registMailTemplate(@Validated MailTemplateEditInputForm.TemplateInputForm inputForm, BindingResult result) {

		// 追加バリデーションチェック
		this.inputFormValid(result, inputForm);

		// 入力エラーの場合
		if (result.hasErrors()) {
			mailTemplateEditService.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, TEMPLATE_EDIT_FRAGMENT_VIEW_PATH, TEMPLATE_INPUT_FORM_NAME, result);
		}

		try {
			// 登録処理
			Long mailTemplateSeq = mailTemplateEditService.registMailTemplate(inputForm);

			String redirectUrl = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.registSuccessRedirect(mailTemplateSeq, null));
			super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_TEMPLATE_REGIST_SUCCESS_REDIRECT, redirectUrl);
			return null;

		} catch (AppException ex) {
			// エラーメッセージの表示
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}
	}

	/**
	 * メールテンプレートの更新処理
	 * 
	 * @param mailTemplateSeq
	 * @return
	 */
	@RequestMapping(value = "/updateMailTemplate", method = RequestMethod.POST)
	public ModelAndView updateMailTemplate(@Validated MailTemplateEditInputForm.TemplateInputForm inputForm, BindingResult result) {

		// 追加バリデーションチェック
		this.inputFormValid(result, inputForm);

		// 入力エラーの場合
		if (result.hasErrors()) {
			mailTemplateEditService.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, TEMPLATE_EDIT_FRAGMENT_VIEW_PATH, TEMPLATE_INPUT_FORM_NAME, result);
		}

		try {
			// 更新処理
			mailTemplateEditService.updateMailTemplate(inputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "メールテンプレート"));
			return null;

		} catch (AppException ex) {
			// エラーメッセージの表示
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}
	}

	/**
	 * メールテンプレートの更新処理
	 * 
	 * @param mailTemplateSeq
	 * @return
	 */
	@RequestMapping(value = "/deleteMailTemplate", method = RequestMethod.POST)
	public ModelAndView deleteMailTemplate(@RequestParam Long mailTemplateSeq) {

		try {
			// 削除処理
			mailTemplateEditService.deleteMailTemplate(mailTemplateSeq);

			String redirectUrl = ModelAndViewUtils.getRedirectPath(MailTemplateController.class, controller -> controller.deleteSuccessRedirect(null));
			super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_TEMPLATE_DELETE_SUCCESS_REDIRECT, redirectUrl);
			return null;

		} catch (AppException ex) {
			// エラーメッセージの表示
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 入力値チェック
	 * 
	 * @param result
	 * @param inputForm
	 */
	private void inputFormValid(BindingResult result, MailTemplateEditInputForm.TemplateInputForm inputForm) {

		// mailCcの入力値チェック
		if (StringUtils.isNotEmpty(inputForm.getMailCc())) {
			this.joiningMailAddressValid(result, inputForm.getMailCc(), "mailCc", CommonConstant.MAIL_TEMPLATE_CC_LIMIT);
		}

		// mailBccの入力値チェック
		if (StringUtils.isNotEmpty(inputForm.getMailBcc())) {
			this.joiningMailAddressValid(result, inputForm.getMailBcc(), "mailBcc", CommonConstant.MAIL_TEMPLATE_BCC_LIMIT);
		}

	}

	/**
	 * カンマ文字区切りのメールアドレスチェック
	 * 
	 * @param result
	 * @param joiningMailAddress
	 * @param fieldName
	 * @param splitLimit
	 */
	private void joiningMailAddressValid(BindingResult result, String joiningMailAddress, String fieldName, final int splitLimit) {

		// カンマ文字区切りを配列化
		List<String> splitedMailAddressList = Arrays.asList(joiningMailAddress.split(CommonConstant.COMMA, -1));

		// 区切り件数チェック
		if (splitedMailAddressList.size() >= splitLimit) {
			result.rejectValue(fieldName, MessageEnum.MSG_E00067.getMessageKey(), new Integer[]{splitLimit}, "");
		}

		// Emailアドレス入力形式チェック
		for (String mailAddress : splitedMailAddressList) {
			if (StringUtils.isEmpty(mailAddress) || !ValidateUtils.isEmailPatternValid(mailAddress)) {
				result.rejectValue(fieldName, MessageEnum.VARIDATE_MSG_E00001.getMessageKey());

				// 1件でも入力エラーパターンがあればそこで終了
				break;
			}
		}

	}

}
