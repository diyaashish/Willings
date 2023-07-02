package jp.loioz.app.user.officeSetting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.officeSetting.form.OfficeSettingInputForm;
import jp.loioz.app.user.officeSetting.form.OfficeSettingViewForm;
import jp.loioz.app.user.officeSetting.service.OfficeSettingService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.SessionUtils;

/**
 * 事務所設定画面のコントローラークラス
 */
@Controller
@RequestMapping("user/officeSetting")
public class OfficeSettingController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/officeSetting/officeSetting";

	/** コントローラと対応する事務所情報設定のフラグメントビューパス */
	private static final String OFFICE_INFO_SETTING_FRAGMENT_PATH = "user/officeSetting/officeSettingFragment::officeInfoSettingFragment";

	/** コントローラと対応する事務印情報設定のフラグメントビューパス */
	private static final String OFFICE_STAMP_SETTING_FRAGMENT_PATH = "user/officeSetting/officeSettingFragment::officeStampSettingFragment";

	/** viewのformで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 事務所情報入力フォームオブジェクト名 */
	private static final String OFFICE_INFO_SETTING_INPUT_FORM_NAME = "officeInfoSettingInputForm";

	/** 事務印情報入力フォームオブジェクト名 */
	private static final String OFFICE_STAMP_SETTING_INPUT_FORM_NAME = "officeStampSettingInputForm";

	/** 事務所設定画面のサービスクラス */
	@Autowired
	private OfficeSettingService officeSettingService;

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 初期表示
	 *
	 * @param form フォーム情報
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {

		ModelAndView mv = null;
		Long tenantSeq = SessionUtils.getTenantSeq();

		// 画面オブジェクトの作成
		OfficeSettingViewForm viewForm = new OfficeSettingViewForm();
		OfficeSettingInputForm.OfficeInfoSettingInputForm officeInfoSettingInputForm = officeSettingService.createOfficeInfoSettingInputForm(tenantSeq);
		OfficeSettingInputForm.OfficeStampSettingInputForm officeStampSettingInputForm = officeSettingService.createOfficeStampSettingInputForm(tenantSeq);

		// 画面情報の設定
		mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		mv.addObject(OFFICE_INFO_SETTING_INPUT_FORM_NAME, officeInfoSettingInputForm);
		mv.addObject(OFFICE_STAMP_SETTING_INPUT_FORM_NAME, officeStampSettingInputForm);

		return mv;
	}

	/**
	 * 事務所情報設定フラグメントの取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getOfficeInfoSettingFragment", method = RequestMethod.GET)
	public ModelAndView getOfficeInfoSettingFragment() {

		ModelAndView mv = null;
		Long tenantSeq = SessionUtils.getTenantSeq();

		OfficeSettingInputForm.OfficeInfoSettingInputForm officeInfoSettingInputForm = officeSettingService.createOfficeInfoSettingInputForm(tenantSeq);
		mv = getMyModelAndView(officeInfoSettingInputForm, OFFICE_INFO_SETTING_FRAGMENT_PATH, OFFICE_INFO_SETTING_INPUT_FORM_NAME);

		if (mv == null) {
			// 画面情報取得失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 事務印情報設定フラグメントの取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getOfficeStampSettingFragment", method = RequestMethod.GET)
	public ModelAndView getOfficeStampSettingFragment() {

		ModelAndView mv = null;
		Long tenantSeq = SessionUtils.getTenantSeq();

		OfficeSettingInputForm.OfficeStampSettingInputForm officeStampSettingInputForm = officeSettingService.createOfficeStampSettingInputForm(tenantSeq);
		mv = getMyModelAndView(officeStampSettingInputForm, OFFICE_STAMP_SETTING_FRAGMENT_PATH, OFFICE_STAMP_SETTING_INPUT_FORM_NAME);

		if (mv == null) {
			// 画面情報取得失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 事務所情報の保存処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveOfficeInfo", method = RequestMethod.POST)
	public ModelAndView saveOfficeInfo(@Validated OfficeSettingInputForm.OfficeInfoSettingInputForm inputForm, BindingResult result) {

		// セッションからテナントSEQを取得
		Long tenantSeq = SessionUtils.getTenantSeq();

		if (result.hasErrors()) {
			// 表示情報の設定
			officeSettingService.setDispProperties(tenantSeq, inputForm);

			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, OFFICE_INFO_SETTING_FRAGMENT_PATH, OFFICE_INFO_SETTING_INPUT_FORM_NAME, result);
		}

		try {
			// 更新処理
			officeSettingService.saveOfficeInfo(tenantSeq, inputForm);

			// セッション管理の事務所名を更新
			SessionUtils.setTenantName(inputForm.getTenantName());

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "事務所情報"));
			return null;

		} catch (AppException ex) {
			// エラーメッセージを設定
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 事務所情報の保存処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveOfficeStamp", method = RequestMethod.POST)
	public ModelAndView saveOfficeStamp(@Validated OfficeSettingInputForm.OfficeStampSettingInputForm inputForm, BindingResult result) {

		// セッションからテナントSEQを取得
		Long tenantSeq = SessionUtils.getTenantSeq();

		if (result.hasErrors()) {
			// 表示情報の設定
			officeSettingService.setDispProperties(tenantSeq, inputForm);

			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, OFFICE_STAMP_SETTING_FRAGMENT_PATH, OFFICE_STAMP_SETTING_INPUT_FORM_NAME, result);
		}

		try {
			// 更新処理
			officeSettingService.saveOfficeStamp(tenantSeq, inputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00045, "アップロード"));
			return null;

		} catch (AppException ex) {
			// エラーメッセージを設定
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 事務所印情報の削除
	 * 
	 * @return
	 */
	@RequestMapping(value = "/deleteOfficeStamp", method = RequestMethod.POST)
	public ModelAndView deleteOfficeStamp() {

		// セッションからテナントSEQを取得
		Long tenantSeq = SessionUtils.getTenantSeq();

		try {
			// 削除処理
			officeSettingService.deleteOfficeStamp(tenantSeq);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "事務所印情報"));
			return null;

		} catch (AppException ex) {
			// エラーメッセージを設定
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
