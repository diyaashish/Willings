package jp.loioz.app.user.fractionSetting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.fractionSetting.form.FractionSettingInputForm;
import jp.loioz.app.user.fractionSetting.service.FractionSettingService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

/**
 * 端数処理 画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping("user/fractionSetting")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class FractionSettingController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/fractionSetting/fractionSetting";

	/** 端数処理fragmentのパス */
	private static final String FRACTION_SETTING_INPUT_FRAGMENT_PATH = "user/fractionSetting/fractionSettingFragment::fractionSettingInputFragment";

	/** 端数処理情報の入力用フォームオブジェクト名 */
	private static final String FRACTION_SETTING_INPUT_FORM_NAME = "fractionSettingInputForm";

	/** 端数処理情報の入力用フォームフラグメントオブジェクト名 */
	private static final String FRACTION_SETTING_INPUT_FRAGMENT_FORM_NAME = "fractionSettingInputFragmentForm";

	/** 端数処理画面のサービスクラス */
	@Autowired
	private FractionSettingService service;

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

		// 画面表示情報の設定
		FractionSettingInputForm inputForm = new FractionSettingInputForm();
		FractionSettingInputForm.FractionSettingInputFragmentForm fractionSettingInputFragmentForm = service.getFractionSettingInputFragmentForm();

		// フォームオブジェクトを設定
		ModelAndView mv = getMyModelAndView(inputForm, MY_VIEW_PATH, FRACTION_SETTING_INPUT_FORM_NAME);
		mv.addObject(FRACTION_SETTING_INPUT_FRAGMENT_FORM_NAME, fractionSettingInputFragmentForm);

		return mv;
	}

	/**
	 * 端数処理入力フラグメントの表示
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getFractionSettingInputFragment", method = RequestMethod.GET)
	public ModelAndView getFractionSettingInputFragment() {

		ModelAndView mv = null;

		// 画面表示情報の設定
		FractionSettingInputForm.FractionSettingInputFragmentForm fractionSettingInputFragmentForm = service.getFractionSettingInputFragmentForm();
		mv = getMyModelAndView(fractionSettingInputFragmentForm, FRACTION_SETTING_INPUT_FRAGMENT_PATH, FRACTION_SETTING_INPUT_FRAGMENT_FORM_NAME);

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		// フォームオブジェクトを設定
		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 保存処理（事務所情報）
	 *
	 * @param officeInputForm フォーム情報（事務所情報）
	 * @param result リザルト情報
	 * @return responce
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView save(@Validated FractionSettingInputForm.FractionSettingInputFragmentForm inputForm, BindingResult result) {

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			service.setDispFractionSettingInputFragmentForm(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, FRACTION_SETTING_INPUT_FRAGMENT_PATH, FRACTION_SETTING_INPUT_FRAGMENT_FORM_NAME, result);
		}

		try {
			// 会計データ設定情報を更新
			service.update(inputForm);
			// 画面Viewを取得
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "端数処理の設定"));
			return null;

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得する
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;
		}

	}

}
