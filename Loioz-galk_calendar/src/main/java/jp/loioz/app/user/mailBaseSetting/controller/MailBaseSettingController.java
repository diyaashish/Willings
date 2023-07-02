package jp.loioz.app.user.mailBaseSetting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.mailBaseSetting.form.MailBaseSettingInputForm;
import jp.loioz.app.user.mailBaseSetting.form.MailBaseSettingViewForm;
import jp.loioz.app.user.mailBaseSetting.service.MailBaseSettingService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

/**
 * メール設定 基本設定画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/mailBaseSetting")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class MailBaseSettingController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/mailBaseSetting/mailBaseSetting";

	/** ダウンロード設定フラグメントviewのパス */
	private static final String ACCG_DOWNLOAD_PEROPD_SETTING_VIEW_NAME = "user/mailBaseSetting/mailBaseSettingFragment::downloadPeriodSettingFragment";

	/** メールパスワード設定フラグメントviewのパス */
	private static final String ACCG_MAIL_PASSWORD_SETTING_VIEW_NAME = "user/mailBaseSetting/mailBaseSettingFragment::mailPasswordSettingFragment";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** ダウンロード設定フラグメント 入力フォームオブジェクト名 */
	private static final String DOWNLOAD_PEROPD_SETTING_INPUT_FORM_NAME = "downloadPeriodSettingInputForm";

	/** メールパスワード設定フラグメント 入力フォームオブジェクト名 */
	private static final String MAIL_PASSWORD_SETTING_INPUT_FORM_NAME = "mailPasswordSettingInputForm";

	/** その他の設定のサービスクラス */
	@Autowired
	private MailBaseSettingService mailBaseSettingService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * メール設定 基本設定画面初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {

		ModelAndView mv = null;

		var viewForm = new MailBaseSettingViewForm();
		MailBaseSettingInputForm.DownloadPeriodSettingInputForm downloadPeriodSettingInputForm = mailBaseSettingService.createDownloadPeriodSettingInputForm();
		MailBaseSettingInputForm.MailPasswordSettingInputForm mailPasswordSettingInputForm = mailBaseSettingService.createMailPasswordSettingInputForm();

		mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		mv.addObject(DOWNLOAD_PEROPD_SETTING_INPUT_FORM_NAME, downloadPeriodSettingInputForm);
		mv.addObject(MAIL_PASSWORD_SETTING_INPUT_FORM_NAME, mailPasswordSettingInputForm);

		return mv;
	}

	/**
	 * メール設定 ダウンロード期間画面情報の取得
	 * 
	 * @return 画面情報
	 */
	@RequestMapping(value = "/getDownloadPeriodSettingFragment", method = RequestMethod.GET)
	public ModelAndView getDownloadPeriodSettingFragment() {

		ModelAndView mv = null;

		MailBaseSettingInputForm.DownloadPeriodSettingInputForm downloadPeriodSettingInputForm = mailBaseSettingService.createDownloadPeriodSettingInputForm();
		mv = getMyModelAndView(downloadPeriodSettingInputForm, ACCG_DOWNLOAD_PEROPD_SETTING_VIEW_NAME, DOWNLOAD_PEROPD_SETTING_INPUT_FORM_NAME);

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * メール設定 メールパスワード設定画面情報の取得
	 * 
	 * @return 画面情報
	 */
	@RequestMapping(value = "/getMailPasswordSettingFragment", method = RequestMethod.GET)
	public ModelAndView getMailPasswordSettingFragment() {

		ModelAndView mv = null;

		MailBaseSettingInputForm.MailPasswordSettingInputForm mailPasswordSettingInputForm = mailBaseSettingService.createMailPasswordSettingInputForm();
		mv = getMyModelAndView(mailPasswordSettingInputForm, ACCG_MAIL_PASSWORD_SETTING_VIEW_NAME, MAIL_PASSWORD_SETTING_INPUT_FORM_NAME);

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * ダウンロード期間情報の保存処理
	 * 
	 * @param inputForm
	 * @return
	 */
	@RequestMapping(value = "/saveDownloadPeriodSetting", method = RequestMethod.POST)
	public ModelAndView saveDownloadPeriodSetting(@Validated MailBaseSettingInputForm.DownloadPeriodSettingInputForm inputForm, BindingResult result) {

		if (result.hasErrors()) {
			mailBaseSettingService.setDispProperties(inputForm);
			setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, ACCG_DOWNLOAD_PEROPD_SETTING_VIEW_NAME, DOWNLOAD_PEROPD_SETTING_INPUT_FORM_NAME, result);
		}

		try {
			// 更新処理
			mailBaseSettingService.saveDownloadPeriodSetting(inputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "メール設定"));
			return null;

		} catch (AppException ex) {

			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}
	}

	/**
	 * メールパスワード設定情報の保存処理
	 * 
	 * @param inputForm
	 * @return
	 */
	@RequestMapping(value = "/saveMailPasswordSetting", method = RequestMethod.POST)
	public ModelAndView saveMailPasswordSetting(@Validated MailBaseSettingInputForm.MailPasswordSettingInputForm inputForm, BindingResult result) {

		if (result.hasErrors()) {
			mailBaseSettingService.setDispProperties(inputForm);
			setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, ACCG_MAIL_PASSWORD_SETTING_VIEW_NAME, MAIL_PASSWORD_SETTING_INPUT_FORM_NAME, result);
		}

		try {
			// 更新処理
			mailBaseSettingService.saveMailPasswordSetting(inputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "メール設定"));
			return null;

		} catch (AppException ex) {

			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
