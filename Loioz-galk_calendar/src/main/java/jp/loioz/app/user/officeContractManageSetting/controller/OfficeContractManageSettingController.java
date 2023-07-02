package jp.loioz.app.user.officeContractManageSetting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.officeContractManageSetting.form.OfficeContractManageSettingInputForm;
import jp.loioz.app.user.officeContractManageSetting.form.OfficeContractManageSettingViewForm;
import jp.loioz.app.user.officeContractManageSetting.service.OfficeContractManageSettingService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.SessionUtils;

/**
 * 契約担当者設定画面のコントローラークラス
 */
@Controller
@RequestMapping("user/officeContractManageSetting")
public class OfficeContractManageSettingController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/officeContractManageSetting/officeContractManageSetting";

	/** コントローラと対応する契約担当者情報設定のフラグメントビューパス */
	private static final String CONTACT_MANAGER_SETTING_FRAGMENT_PATH = "user/officeContractManageSetting/officeContractManageSettingFragment::contactManagerSettingFragment";

	/** viewのformで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 契約担当者情報入力フォームオブジェクト名 */
	private static final String CONTACT_MANAGER_SETTING_INPUT_FORM_NAME = "contactManagerSettingInputForm";

	/** 契約担当者設定画面のサービスクラス */
	@Autowired
	private OfficeContractManageSettingService officeContractManageSettingService;

	// =========================================================================
	// public メソッド
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
		OfficeContractManageSettingViewForm viewForm = new OfficeContractManageSettingViewForm();
		OfficeContractManageSettingInputForm.ContactManagerSettingInputForm contactManagerSettingInputForm = officeContractManageSettingService.createContactManagerSettingInputForm(tenantSeq);

		// 画面情報の設定
		mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		mv.addObject(CONTACT_MANAGER_SETTING_INPUT_FORM_NAME, contactManagerSettingInputForm);

		return mv;
	}

	/**
	 * 契約者情報設定フラグメントの取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getContactManagerSettingFragment", method = RequestMethod.GET)
	public ModelAndView getContactManagerSettingFragment() {

		ModelAndView mv = null;
		Long tenantSeq = SessionUtils.getTenantSeq();

		OfficeContractManageSettingInputForm.ContactManagerSettingInputForm contactManagerSettingInputForm = officeContractManageSettingService.createContactManagerSettingInputForm(tenantSeq);
		mv = getMyModelAndView(contactManagerSettingInputForm, CONTACT_MANAGER_SETTING_FRAGMENT_PATH, CONTACT_MANAGER_SETTING_INPUT_FORM_NAME);

		if (mv == null) {
			// 画面情報取得失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 契約担当者情報の保存処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveContactManager", method = RequestMethod.POST)
	public ModelAndView saveContactManager(@Validated OfficeContractManageSettingInputForm.ContactManagerSettingInputForm inputForm, BindingResult result) {

		// セッションからテナントSEQを取得
		Long tenantSeq = SessionUtils.getTenantSeq();

		if (result.hasErrors()) {
			// 表示情報の設定
			officeContractManageSettingService.setDispProperties(tenantSeq, inputForm);

			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, CONTACT_MANAGER_SETTING_FRAGMENT_PATH, CONTACT_MANAGER_SETTING_INPUT_FORM_NAME, result);
		}

		try {
			// 更新処理
			officeContractManageSettingService.saveContactManager(tenantSeq, inputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "契約担当者情報"));
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
