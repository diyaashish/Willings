package jp.loioz.app.user.funcOldKaikeiSetting.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.funcOldKaikeiSetting.form.FuncOldKaikeiSettingInputForm;
import jp.loioz.app.user.funcOldKaikeiSetting.form.FuncOldKaikeiSettingSearchForm;
import jp.loioz.app.user.funcOldKaikeiSetting.form.FuncOldKaikeiSettingViewForm;
import jp.loioz.app.user.funcOldKaikeiSetting.service.FuncOldKaikeiSettingService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

/**
 * 旧会計管理の機能設定画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@LoiozAdminControlCheck(items = {LoiozAdminControl.OLD_KAIKEI_OPEN})
@Controller
@RequestMapping(UrlConstant.FUNC_OLD_KAIKEI_SETTING_URL)
@DenyAccountKengen(value = AccountKengen.GENERAL)
@SessionAttributes(FuncOldKaikeiSettingController.SEARCH_FORM_NAME)
public class FuncOldKaikeiSettingController extends DefaultController {

	/** コントローラと対応するviewパス **/
	public static final String MY_VIEW_PATH = "user/funcOldKaikeiSetting/funcOldKaikeiSetting";

	/** 旧会計管理の機能設定fragmentのパス */
	private static final String FUNC_OLD_KAIKEI_BASIC_SETTING_FRAGMENT_PATH = "user/funcOldKaikeiSetting/funcOldKaikeiSettingFragment::funcOldKaikeiBasicSettingFragment";
	
	/** 入出金項目一覧のラップfragmentのパス */
	private static final String FUNC_OLD_KAIKEI_LIST_VIEW_WRAP_FRAGMENT_PATH = "user/funcOldKaikeiSetting/funcOldKaikeiSettingFragment::funcOldKaikeiSettingViewWrapFragment";
	
	/** 入出金項目一覧fragmentのパス */
	private static final String FUNC_OLD_KAIKEI_LIST_VIEW_FRAGMENT_PATH = "user/funcOldKaikeiSetting/funcOldKaikeiSettingFragment::funcOldKaikeiSettingViewFragment";

	/** viewで使用するフォームオブジェクト名 **/
	public static final String VIEW_FORM_NAME = "viewForm";

	/** 基本設定のFormクラス */
	public static final String BASIC_SETTING_INPUT_FORM_NAME = "funcOldKaikeiBasicSettingInputForm";
	
	/** 検索条件のFormクラス */
	public static final String SEARCH_FORM_NAME = "funcOldKaikeiSettingSearchForm";

	/** 入出金項目一覧fragmentフォームオブジェクト名 */
	private static final String FUNC_OLD_KAIKEI_LIST_FRAGMENT_VIEW_FORM = "funcOldKaikeiSettingListViewForm";

	/** 入出金項目画面のサービスクラス */
	@Autowired
	private FuncOldKaikeiSettingService service;

	/** 検索条件のModelAttribute */
	@ModelAttribute(SEARCH_FORM_NAME)
	FuncOldKaikeiSettingSearchForm setUpSearchForm() {
		return new FuncOldKaikeiSettingSearchForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) FuncOldKaikeiSettingSearchForm searchForm) {

		// 初期化
		searchForm.initForm();
		
		// viewFormの作成
		FuncOldKaikeiSettingViewForm viewForm = new FuncOldKaikeiSettingViewForm();
		
		// 入出金項目一覧フラグメント画面オブジェクトを作成
		FuncOldKaikeiSettingViewForm.FuncOldKaikeiSettingListViewForm funcOldKaikeiSettingListViewForm = service.createFuncOldKaikeiSettingListViewForm(searchForm.getFuncOldKaikeiSettingListSearchForm());
		viewForm.setFuncOldKaikeiSettingListViewForm(funcOldKaikeiSettingListViewForm);
		
		// 基本設定の入力フォームを取得
		FuncOldKaikeiSettingInputForm.FuncOldKaikeiBasicSettingInputForm basicSettingInputForm = service.createFuncOldKaikeiBasicSettingInputForm();
		
		// 画面情報作成
		ModelAndView mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		mv.addObject(BASIC_SETTING_INPUT_FORM_NAME, basicSettingInputForm);
		
		return mv;
	}
	
	/**
	 * 旧会計管理の機能設定フラグメントを取得する
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getFuncOldKaikeiBasicSettingFragment", method = RequestMethod.GET)
	public ModelAndView getFuncOldKaikeiBasicSettingFragment() {
		
		// 機能設定フラグメントのフォームを取得
		FuncOldKaikeiSettingInputForm.FuncOldKaikeiBasicSettingInputForm basicSettingInputForm = service.createFuncOldKaikeiBasicSettingInputForm();
		
		// 画面返却
		super.setAjaxProcResultSuccess();
		return getMyModelAndView(basicSettingInputForm, FUNC_OLD_KAIKEI_BASIC_SETTING_FRAGMENT_PATH, BASIC_SETTING_INPUT_FORM_NAME);
	}
	
	/**
	 * 入出金項目一覧をラップするフラグメント（入出金項目一覧の表示制御フラグメント）を取得する
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getFuncOldKaikeiSettingViewWrapFragment", method = RequestMethod.GET)
	public ModelAndView getFuncOldKaikeiSettingViewWrapFragment(@ModelAttribute(SEARCH_FORM_NAME) FuncOldKaikeiSettingSearchForm searchForm) {
		
		// viewFormの作成
		FuncOldKaikeiSettingViewForm viewForm = new FuncOldKaikeiSettingViewForm();
		
		// 入出金項目一覧フラグメント画面オブジェクトを作成
		FuncOldKaikeiSettingViewForm.FuncOldKaikeiSettingListViewForm funcOldKaikeiSettingListViewForm = service.createFuncOldKaikeiSettingListViewForm(searchForm.getFuncOldKaikeiSettingListSearchForm());
		viewForm.setFuncOldKaikeiSettingListViewForm(funcOldKaikeiSettingListViewForm);
		
		// 画面情報作成
		super.setAjaxProcResultSuccess();
		return getMyModelAndView(viewForm, FUNC_OLD_KAIKEI_LIST_VIEW_WRAP_FRAGMENT_PATH, VIEW_FORM_NAME);
	}
	
	/**
	 * 入出金項目一覧フラグメントの表示
	 *
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getFuncOldKaikeiSettingListSearch", method = RequestMethod.GET)
	public ModelAndView getFuncOldKaikeiSettingListSearch(@ModelAttribute(SEARCH_FORM_NAME) FuncOldKaikeiSettingSearchForm searchForm) {

		// 入出金項目一覧フラグメント画面オブジェクトを作成
		FuncOldKaikeiSettingViewForm.FuncOldKaikeiSettingListViewForm funcOldKaikeiSettingListViewForm = service.createFuncOldKaikeiSettingListViewForm(searchForm.getFuncOldKaikeiSettingListSearchForm());

		// 画面情報を返却
		super.setAjaxProcResultSuccess();
		return getMyModelAndView(funcOldKaikeiSettingListViewForm, FUNC_OLD_KAIKEI_LIST_VIEW_FRAGMENT_PATH, FUNC_OLD_KAIKEI_LIST_FRAGMENT_VIEW_FORM);
	}
	
	/**
	 * 旧会計管理の機能設定の保存
	 * 
	 * @param basicSettingInputForm
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/saveFuncOldKaikeiBasicSetting", method = RequestMethod.POST)
	public Map<String, Object> saveFuncOldKaikeiBasicSetting(@Validated FuncOldKaikeiSettingInputForm.FuncOldKaikeiBasicSettingInputForm basicSettingInputForm, BindingResult result) {
		
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			return response;
		}
		
		try {

			// 保存処理
			service.saveFuncOldKaikeiBasicSetting(basicSettingInputForm);
			
			// 保存成功メッセージパーツ
			String saveMessageElem = "";
			String isOldKaikeiOn = basicSettingInputForm.getIsOldKaikeiOn();
			if (SystemFlg.FLG_ON.equalsByCode(isOldKaikeiOn)) {
				// 旧会計機能をONで保存した場合
				saveMessageElem = "有効";
			} else {
				// 旧会計機能をOFFで保存した場合
				saveMessageElem = "無効";
			}
			
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00055, saveMessageElem));

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得する
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
		
		return response;
	}

	/**
	 * 表示順変更処理
	 * 
	 * @param targetId
	 * @param index
	 * @return
	 */
	@RequestMapping(value = "/updateDispOrder", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateDispOrder(
			@RequestParam(name = "targetId") String targetId,
			@RequestParam(name = "index") String index) {

		Map<String, Object> response = new HashMap<>();
		List<Long> dispOrder = new ArrayList<Long>();

		try {
			/* 表示順変更処理 */
			dispOrder = service.updateDispOrder(targetId, index);

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得する
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getMessageKey()));
			return response;
		}
		response.put("succeeded", true);
		response.put("dispOrder", dispOrder);
		response.put("message", getMessage(MessageEnum.MSG_I00023, "表示順"));
		return response;
	}

}
