package jp.loioz.app.user.kaikeiManagement.controller;

import java.util.HashMap;
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
import jp.loioz.app.user.gyomuHistory.annitations.HasGyomuHistoryByAnken;
import jp.loioz.app.user.gyomuHistory.annitations.HasGyomuHistoryByCustomer;
import jp.loioz.app.user.kaikeiManagement.form.KaikeiListInputForm;
import jp.loioz.app.user.kaikeiManagement.form.KaikeiListSearchForm;
import jp.loioz.app.user.kaikeiManagement.form.KaikeiListViewForm;
import jp.loioz.app.user.kaikeiManagement.service.KaikeiCommonService;
import jp.loioz.app.user.kaikeiManagement.service.KaikeiListService;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 会計管理画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@LoiozAdminControlCheck(items = {LoiozAdminControl.OLD_KAIKEI_OPEN})
@Controller
@HasGyomuHistoryByCustomer
@HasGyomuHistoryByAnken
@RequestMapping(UrlConstant.KAIKEI_MANAGEMENT_URL)
@SessionAttributes(value = {KaikeiListController.KAIKEI_SEARCH_FORM_NAME})
public class KaikeiListController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/kaikeiManagement/kaikeiList";

	/** 案件明細：再検索した際のviewパス */
	public static final String ANKEN_MEISAI_LIST = MY_VIEW_PATH + "::ankenMeisaiList";

	/** 案件明細：精算完了時の案件ステータス入力フラグメントパス */
	public static final String ANKEN_STATUS_FRAGMENT_VIEW_PATH = "user/kaikeiManagement/kaikeiListFragment::ankenStatusInputFragment";
	/** 案件明細：精算完了時の案件ステータス入力フォーム名 */
	public static final String ANKEN_STATUS_FRAGMENT_INPUT_FORM_NAME = "ankenStatusInputForm";

	/** 会計記録：再検索した際のviewパス */
	public static final String KAIKEI_KIROKU_LIST = MY_VIEW_PATH + "::kaikeiKirokuList";

	/** 入金予定：再検索した際のviewパス */
	public static final String NYUKIN_YOTEI_LIST = MY_VIEW_PATH + "::nyukinYoteiList";

	/** 出金予定：再検索した際のviewパス */
	public static final String SHUKKIN_YOTEI_LIST = MY_VIEW_PATH + "::shukkinYoteiList";

	/** 検索条件のフォームオブジェクト名 */
	public static final String KAIKEI_SEARCH_FORM_NAME = "kaikeiListSearchForm";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 会計管理系画面の共通サービス */
	@Autowired
	private KaikeiCommonService kaikeiCommonService;

	/** サービスクラス */
	@Autowired
	private KaikeiListService service;

	/** フォームクラスの初期化(検索用フォーム) */
	@ModelAttribute(KAIKEI_SEARCH_FORM_NAME)
	KaikeiListSearchForm setUpSeachForm() {
		return new KaikeiListSearchForm();
	}

	/** フォームクラスの初期化(viewフォーム) */
	@ModelAttribute(VIEW_FORM_NAME)
	KaikeiListViewForm setUpViewForm() {
		return new KaikeiListViewForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	// ****************************************************************
	// 会計管理一覧
	// ****************************************************************

	/**
	 * 画面表示データを取得します。
	 * 
	 * @param searchForm 検索フォーム
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(KAIKEI_SEARCH_FORM_NAME) KaikeiListSearchForm searchForm) {

		Long transitionCustomerId = searchForm.getTransitionCustomerId();
		Long transitionAnkenId = searchForm.getTransitionAnkenId();
		if (CommonUtils.allNull(transitionCustomerId, transitionAnkenId)) {
			// 顧客、案件のいずれのIDも指定されていないアクセスの場合は404エラー扱いとする
			throw new DataNotFoundException("顧客ID、案件IDのいずれも未設定のリクエストです。");
		}

		if (transitionCustomerId == null) {
			// 案件軸の会計管理画面を表示する場合
			// 案件IDの検証
			service.validAnkenId(transitionAnkenId);
			// 遷移元判定フラグをOFF(=案件)にする
			searchForm.setTransitionFlg(SystemFlg.FLG_OFF.getCd());
		} else {
			// 顧客軸の会計管理画面を表示する場合
			// 顧客IDの検証
			service.validCustomerId(transitionCustomerId);
			// 遷移元判定フラグをON(=顧客)にする
			searchForm.setTransitionFlg(SystemFlg.FLG_ON.getCd());
		}

		searchForm.setKaikeiKirokuHiddenFlg(SystemFlg.FLG_OFF.getCd());
		searchForm.setYoteiHiddenFlg(SystemFlg.FLG_OFF.getCd());

		// 会計管理画面
		KaikeiListViewForm viewForm = kaikeiCommonService.createViewForm(searchForm);
		// 精算書タブ表示をリセットする。
		searchForm.setIsSeisanshoTab(false);
		return this.getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 会計管理画面(一覧) 初期表示<br>
	 * 
	 * <pre>
	 * 顧客管理から遷移した時用です。
	 * </pre>
	 * 
	 * @param searchForm 検索フォーム
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/list/customer", method = RequestMethod.GET)
	public ModelAndView initCustomer(@ModelAttribute(KAIKEI_SEARCH_FORM_NAME) KaikeiListSearchForm searchForm) {

		// 遷移元判定フラグをON(=顧客)にします。
		searchForm.setTransitionFlg(SystemFlg.FLG_ON.getCd());
		searchForm.setKaikeiKirokuHiddenFlg(SystemFlg.FLG_OFF.getCd());
		searchForm.setYoteiHiddenFlg(SystemFlg.FLG_OFF.getCd());

		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.index(null));
	}

	// ****************************************************************
	// 案件明細
	// ****************************************************************
	/**
	 * 案件明細情報の検索を行います。<br>
	 * 
	 * <pre>
	 * 案件明細一覧の「完了済データ非表示」チェックボックスの
	 * ON／OFFによる再検索を行う時用。
	 * </pre>
	 * 
	 * @param searchForm 検索フォーム
	 * @param result バリデーション結果
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/searchAnkenMeisai", method = RequestMethod.POST)
	public ModelAndView searchAnkenMeisai(@Validated KaikeiListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		// リクエストパラメータが不正な場合
		if (result.hasErrors()) {
			this.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		// 案件明細一覧を取得する
		KaikeiListViewForm viewForm = kaikeiCommonService.createViewFormForAnkenMeisai(searchForm);
		mv = getMyModelAndView(viewForm, ANKEN_MEISAI_LIST, VIEW_FORM_NAME);

		if (mv == null) {
			this.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		this.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件明細_案件ステータス入力エリアの表示
	 * 
	 * @param ankenId
	 * @param customerId
	 * @param transitionFlg
	 * @return
	 */
	@RequestMapping(value = "/getAnkenStatusInputFragment", method = RequestMethod.GET)
	public ModelAndView getAnkenStatusInputFragment(@RequestParam("ankenId") Long ankenId, @RequestParam("customerId") Long customerId, @RequestParam("transitionFlg") String transitionFlg) {

		ModelAndView mv = null;

		try {
			// フォームオブジェクトを作成
			KaikeiListInputForm.AnkenStatusInputForm inputForm = service.createAnkenStatusInputForm(ankenId, customerId, transitionFlg);
			mv = getMyModelAndView(inputForm, ANKEN_STATUS_FRAGMENT_VIEW_PATH, ANKEN_STATUS_FRAGMENT_INPUT_FORM_NAME);

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return mv;
		}

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件ステータスの保存処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveAnkenStatus", method = RequestMethod.POST)
	public ModelAndView saveAnkenStatus(@Validated KaikeiListInputForm.AnkenStatusInputForm inputForm, BindingResult result) {

		try {
			// 画面表示用オブジェクトの作成
			service.setDisplayProperties(inputForm);

			if (result.hasErrors()) {
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				return getMyModelAndViewWithErrors(inputForm, ANKEN_STATUS_FRAGMENT_VIEW_PATH, ANKEN_STATUS_FRAGMENT_INPUT_FORM_NAME, result);
			}

			// 保存処理
			service.saveAnkenStatus(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00054, "顧客ステータス"));
			return null;
		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	// ****************************************************************
	// 会計記録
	// ****************************************************************
	/**
	 * 会計記録情報の検索を行います。<br>
	 * 
	 * <pre>
	 * 会計記録一覧の「精算済データ非表示」チェックボックスの
	 * ON／OFFによる再検索を行う時用。
	 * </pre>
	 * 
	 * @param searchForm 検索フォーム
	 * @param result バリデーション結果
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/searchKaikeiKiroku", method = RequestMethod.POST)
	public ModelAndView searchKaikeiKiroku(@Validated KaikeiListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		if (result.hasErrors()) {
			this.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		// 画面Viewを作成する
		KaikeiListViewForm viewForm = kaikeiCommonService.createViewFormForKaikeiKiroku(searchForm);
		mv = getMyModelAndView(viewForm, KAIKEI_KIROKU_LIST, VIEW_FORM_NAME);

		if (mv == null) {
			this.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		this.setAjaxProcResultSuccess();
		return mv;
	}

	// ****************************************************************
	// 入出金予定
	// ****************************************************************
	/**
	 * 入出金予定情報の検索を行います。<br>
	 * 
	 * <pre>
	 * 入出金予定一覧の「処理済データ非表示」チェックボックスの
	 * ON／OFFによる再検索を行う時用。
	 * </pre>
	 * 
	 * @param searchForm 検索フォーム
	 * @param result バリデーション結果
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/searchNyushukkinYotei", method = RequestMethod.POST)
	public ModelAndView searchNyushukkinYotei(@Validated KaikeiListSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		if (result.hasErrors()) {
			this.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		// 画面Viewを作成する
		KaikeiListViewForm viewForm = kaikeiCommonService.createViewFormForNyushukkinYotei(searchForm);
		mv = getMyModelAndView(viewForm, NYUKIN_YOTEI_LIST, VIEW_FORM_NAME);

		if (mv == null) {
			this.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		this.setAjaxProcResultSuccess();
		return mv;
	}

	// ****************************************************************
	// 精算記録
	// ****************************************************************
	/**
	 * 摘要を更新します。
	 *
	 * @param seisanSeq
	 * @param tekiyo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateSeisanTekiyo", method = RequestMethod.POST)
	public Map<String, Object> updateSeisanTekiyo(@RequestParam(name = "seisanSeq") Long seisanSeq, @RequestParam(name = "tekiyo") String tekiyo) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 更新処理
			service.updateSeisanTekiyo(seisanSeq, tekiyo);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "摘要"));

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));
		}

		return response;
	}
}
