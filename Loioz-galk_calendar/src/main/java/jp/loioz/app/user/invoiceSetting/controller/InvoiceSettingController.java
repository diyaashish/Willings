package jp.loioz.app.user.invoiceSetting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.user.invoiceSetting.form.InvoiceSettingInputForm;
import jp.loioz.app.user.invoiceSetting.form.InvoiceSettingViewForm;
import jp.loioz.app.user.invoiceSetting.service.InvoiceSettingService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

/**
 * 請求書設定の設定画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/invoiceSetting")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class InvoiceSettingController extends DefaultController {

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyInvoiceSettingFragmentInputForm";

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/invoiceSetting/invoiceSetting";

	/** 請求書の設定のフラグメントviewのパス */
	private static final String INVOICE_SETTING_FRAGMENT_VIEW_PATH = "user/invoiceSetting/invoiceSettingFragment::invoiceSettingFragment";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 請求書設定のフラグメント入力オブジェクト名 */
	private static final String INVOICE_SETTING_FRAGMENT_INPUT_FORM_NAME = "invoiceSettingFragmentInputForm";

	/** 請求書の設定のサービスクラス */
	@Autowired
	private InvoiceSettingService invoiceSettingService;

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 請求書の設定画面初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {

		InvoiceSettingViewForm viewForm = new InvoiceSettingViewForm();
		InvoiceSettingInputForm.InvoiceSettingFragmentInputForm invoiceSettingFragmentInputForm = invoiceSettingService.createInvoiceSettingFragmentInputForm();

		ModelAndView mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		mv.addObject(INVOICE_SETTING_FRAGMENT_INPUT_FORM_NAME, invoiceSettingFragmentInputForm);

		return mv;
	}

	/**
	 * 請求書設定フラグメントの画面表示情報を取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getInvoiceSettingFragment", method = RequestMethod.GET)
	public ModelAndView getInvoiceSettingFragment() {

		ModelAndView mv = null;

		InvoiceSettingInputForm.InvoiceSettingFragmentInputForm inputForm = invoiceSettingService.createInvoiceSettingFragmentInputForm();
		mv = getMyModelAndView(inputForm, INVOICE_SETTING_FRAGMENT_VIEW_PATH, INVOICE_SETTING_FRAGMENT_INPUT_FORM_NAME);

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		// フォームオブジェクトを設定
		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 請求書設定の更新処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ModelAndView update(@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) InvoiceSettingInputForm.InvoiceSettingFragmentInputForm inputForm,
			BindingResult result) {

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			invoiceSettingService.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, INVOICE_SETTING_FRAGMENT_VIEW_PATH, INVOICE_SETTING_FRAGMENT_INPUT_FORM_NAME, result);
		}

		try {
			// 会計データ設定情報を更新
			invoiceSettingService.update(inputForm);

			// 画面Viewを取得
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "請求書の設定"));
			return null;

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得する
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
