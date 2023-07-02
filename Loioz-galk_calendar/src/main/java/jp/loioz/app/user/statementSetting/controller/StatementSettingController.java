package jp.loioz.app.user.statementSetting.controller;

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
import jp.loioz.app.user.statementSetting.form.StatementSettingInputForm;
import jp.loioz.app.user.statementSetting.form.StatementSettingViewForm;
import jp.loioz.app.user.statementSetting.service.StatementSettingService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

/**
 * 精算書の設定画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/statementSetting")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class StatementSettingController extends DefaultController {

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyStatementSettingFragmentInputForm";

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/statementSetting/statementSetting";

	/** 精算書の設定のフラグメントviewのパス */
	private static final String STATEMENT_SETTING_FRAGMENT_VIEW_PATH = "user/statementSetting/statementSettingFragment::statementSettingFragment";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 精算書設定のフラグメント入力オブジェクト名 */
	private static final String STATEMENT_SETTING_FRAGMENT_INPUT_FORM_NAME = "statementSettingFragmentInputForm";

	/** 精算書の設定のサービスクラス */
	@Autowired
	private StatementSettingService statementSettingService;

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 精算書の設定画面初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {

		StatementSettingViewForm viewForm = new StatementSettingViewForm();
		StatementSettingInputForm.StatementSettingFragmentInputForm statementSettingFragmentInputForm = statementSettingService.createStatementSettingFragmentInputForm();

		ModelAndView mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		mv.addObject(STATEMENT_SETTING_FRAGMENT_INPUT_FORM_NAME, statementSettingFragmentInputForm);

		return mv;
	}

	/**
	 * 精算書設定フラグメントの画面表示情報を取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getStatementSettingFragment", method = RequestMethod.GET)
	public ModelAndView getStatementSettingFragment() {

		ModelAndView mv = null;

		StatementSettingInputForm.StatementSettingFragmentInputForm inputForm = statementSettingService.createStatementSettingFragmentInputForm();
		mv = getMyModelAndView(inputForm, STATEMENT_SETTING_FRAGMENT_VIEW_PATH, STATEMENT_SETTING_FRAGMENT_INPUT_FORM_NAME);

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		// フォームオブジェクトを設定
		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 精算書設定の更新処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ModelAndView update(@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) StatementSettingInputForm.StatementSettingFragmentInputForm inputForm,
			BindingResult result) {

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			statementSettingService.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, STATEMENT_SETTING_FRAGMENT_VIEW_PATH, STATEMENT_SETTING_FRAGMENT_INPUT_FORM_NAME, result);
		}

		try {
			// 精算書設定情報を更新
			statementSettingService.update(inputForm);

			// 画面Viewを取得
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "精算書の設定"));
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
