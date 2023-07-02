package jp.loioz.app.user.ankenManagement.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.user.ankenManagement.form.AnkenEditMinjiInputForm;
import jp.loioz.app.user.ankenManagement.form.AnkenEditMinjiInputForm.AnkenBasicInputForm;
import jp.loioz.app.user.ankenManagement.form.AnkenEditMinjiViewForm;
import jp.loioz.app.user.ankenManagement.service.AnkenEditMinjiService;
import jp.loioz.app.user.gyomuHistory.annitations.HasGyomuHistoryByAnken;
import jp.loioz.app.user.schedule.annotations.HasSchedule;
import jp.loioz.app.user.schedule.form.ScheduleInputForm;
import jp.loioz.app.user.schedule.service.ScheduleCommonService;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 案件民事コントローラー
 */
@Controller
@HasSchedule
@HasGyomuHistoryByAnken
@RequestMapping(value = "user/ankenMinjiManagement/{ankenId}")
public class AnkenEditMinjiController extends DefaultController {

	/** コントローラと対応するviewパス */
	private static final String MY_VIEW_PATH = "user/ankenManagement/ankenEditMinji";

	/******************************************************************
	 * サイドメニュー
	 ******************************************************************/

	/** 顧客案件メニューfragmentのパス */
	private static final String CUSTOMER_ANKENMENU_FRAGMENT_PATH = "common/customerAnkenMenu::menuList";

	/******************************************************************
	 * 基本情報
	 ******************************************************************/

	/** 案件基本情報表示fragmentのパス */
	private static final String ANKEN_BASIC_VIEW_FRAGMENT_PATH = "user/ankenManagement/ankenEditMinjiFragment::ankenBasicViewFragment";
	/** 案件基本情報入力フォームfragmentのパス */
	private static final String ANKEN_BASIC_INPUT_FRAGMENT_PATH = "user/ankenManagement/ankenEditMinjiFragment::ankenBasicInputFragment";
	/** 案件基本情報入力フォーム売上計上先選択肢fragmentのパス */
	private static final String ANKEN_BASIC_LAWYER_OPTIONS_FRAGMENT_PATH = "user/ankenManagement/ankenEditMinjiFragment::lawyerOptionList";

	/** 案件基本情報の表示用フォームオブジェクト名 */
	private static final String ANKEN_BASIC_VIEW_FORM_NAME = "ankenBasicViewForm";
	/** 案件基本情報の入力用フォームオブジェクト名 */
	private static final String ANKEN_BASIC_INPUT_FORM_NAME = "ankenBasicInputForm";

	/******************************************************************
	 * 案件-顧客登録など
	 ******************************************************************/

	/** 案件-顧客情報表示fragmentのパス */
	private static final String ANKEN_CUSTOMER_LIST_VIEW_FRAGMENT_PATH = "user/ankenManagement/ankenEditMinjiFragment::ankenCustomerListViewFragment";
	/** 案件-顧客情報表示fragmentのパス */
	private static final String ANKEN_CUSTOMER_LIST_INPUT_FRAGMENT_PATH = "user/ankenManagement/ankenEditMinjiFragment::ankenCustomerListInputFragment";

	/** 案件-顧客一覧情報の表示用フォームオブジェクト名 */
	private static final String ANKEN_CUSTOMER_LIST_VIEW_FORM_NAME = "ankenCustomerListViewForm";
	/** 案件-顧客入力情報の表示用フォームオブジェクト名 */
	private static final String ANKEN_CUSTOMER_LIST_INPUT_FORM_NAME = "ankenCustomerListInputForm";

	/******************************************************************
	 * 案件-関与者(相手方、相手方代理人)
	 ******************************************************************/

	/** 案件-関与者情報表示fragmentのパス */
	private static final String ANKEN_KANYOSHA_LIST_FRAGMENT_PATH = "user/ankenManagement/ankenEditMinjiFragment::ankenAitegataListViewFragment";
	/** 案件-関与者一覧情報の表示用フォームオブジェクト名 */
	private static final String ANKEN_KANYOSHA_LIST_VIEW_FORM_NAME = "ankenAitegataListViewForm";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/** サービスクラス */
	@Autowired
	private AnkenEditMinjiService service;

	/** 共通案件サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 共通予定サービスクラス */
	@Autowired
	private ScheduleCommonService scheduleCommonService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 * 
	 * @param ankenId 案件ID
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable Long ankenId) {

		if (!commonAnkenService.isMinji(ankenId)) {
			// 案件が民事以外の場合、共通コントローラーに戻す
			return ModelAndViewUtils.getRedirectModelAndView(AnkenEditController.class, controller -> controller.index(ankenId));
		}

		// 画面表示情報を取得
		AnkenEditMinjiViewForm viewForm = service.createViewForm(ankenId);

		// 案件 基本情報表示用オブジェクトの作成
		AnkenEditMinjiViewForm.AnkenBasicViewForm ankenBasicViewForm = service.createAnkenBasicViewForm(ankenId);
		viewForm.setAnkenBasicViewForm(ankenBasicViewForm);

		// 案件 顧客情報表示用オブジェクトの作成
		AnkenEditMinjiViewForm.AnkenCustomerListViewForm ankenCustomerListViewForm = service.createAnkenCustomerListViewForm(ankenId);
		viewForm.setAnkenCustomerListViewForm(ankenCustomerListViewForm);

		// 案件 関与者情報表示用オブジェクトの作成
		AnkenEditMinjiViewForm.AnkenAitegataListViewForm ankenAitegataListViewForm = service.createAnkenAitegataListViewForm(ankenId);
		viewForm.setAnkenAitegataListViewForm(ankenAitegataListViewForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);

	}

	/**
	 * 案件追加後の案件編集画面初期表示用。<br>
	 * 案件基本情報を取得し入力フォームにセットする。<br>
	 * 経営者が1ユーザーしかいない場合は、そのユーザーを入力フォームの売上計上先にセットする。<br>
	 * 
	 * <pre>
	 * 基本情報部分を編集モードで表示するため、
	 * 基本情報入力フォームのインスタンスを取得し、リダイレクト先に渡すように設定する。
	 * 
	 * ※リダイレクトを行うのはブラウザのURLが、このメソッドへのアクセス時のURLではなく、indexメソッドのURLになるようにするため。
	 * </pre>
	 * 
	 * @param ankenId
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/indexAfterAnkenRegist", method = RequestMethod.GET)
	public ModelAndView indexAfterAnkenRegist(@PathVariable Long ankenId,
			RedirectAttributes redirectAttributes) {

		if (!commonAnkenService.isMinji(ankenId)) {
			// 案件が民事以外の場合、共通コントローラーに戻す
			return ModelAndViewUtils.getRedirectModelAndView(AnkenEditController.class, controller -> controller.indexAfterAnkenRegist(ankenId, redirectAttributes));
		}

		// 基本情報編集画面の表示データを取得
		AnkenEditMinjiInputForm.AnkenBasicInputForm basicInfoInputForm = service.createAnkenBasicInputForm(ankenId);

		// 売上帰属に選択できるユーザーが1ユーザーしかいない場合、売上計上先にユーザーをセットした状態にする
		basicInfoInputForm = service.setDefaultSalesOwner(basicInfoInputForm);

		// リダイレクト先に引き継ぐ
		redirectAttributes.addFlashAttribute(ANKEN_BASIC_INPUT_FORM_NAME, basicInfoInputForm);

		// 自画面のindexメソッドへのリダイレクトオブジェクトを生成
		String redirectPath = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.index(ankenId));
		RedirectViewBuilder redirectViewBuilder = new RedirectViewBuilder(redirectAttributes, redirectPath);

		return redirectViewBuilder.build();
	}

	/**
	 * 顧客案件のサイドメニュー部分を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getCustomerAnkenMenuView", method = RequestMethod.GET)
	public ModelAndView getCustomerAnkenMenuView(@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// サイドメニュー画面の取得
			mv = ModelAndViewUtils.getModelAndView(CUSTOMER_ANKENMENU_FRAGMENT_PATH);
			// パラメータ情報を設定
			mv.addObject("sideMenuAnkenId", ankenId);
			mv.addObject("selectedTabClass", "is_anken");

		} catch (Exception ex) {
			// 上記のメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// メソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件民事の基本情報表示フォーム部分を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenBasicView", method = RequestMethod.GET)
	public ModelAndView getAnkenBasicView(@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 顧客の基本情報表示フォームを設定した画面の取得
			AnkenEditMinjiViewForm.AnkenBasicViewForm basicViewForm = service.createAnkenBasicViewForm(ankenId);
			mv = getMyModelAndView(basicViewForm, ANKEN_BASIC_VIEW_FRAGMENT_PATH, ANKEN_BASIC_VIEW_FORM_NAME);

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件の基本情報入力フォーム部分を取得する
	 * 
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenBasicInputForm", method = RequestMethod.GET)
	public ModelAndView getAnkenBasicInputForm(@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			AnkenEditMinjiInputForm.AnkenBasicInputForm basicInfoInputForm = service.createAnkenBasicInputForm(ankenId);
			mv = getMyModelAndView(basicInfoInputForm, ANKEN_BASIC_INPUT_FRAGMENT_PATH, ANKEN_BASIC_INPUT_FORM_NAME);

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 売上計上先の自動選択処理
	 *
	 * @param ankenId
	 * @param ankenType
	 * @return
	 */
	@RequestMapping(value = "/changeType", method = RequestMethod.GET)
	public ModelAndView setSalesOwner(@PathVariable Long ankenId, @RequestParam("ankenType") String ankenType) {

		ModelAndView mv = null;

		try {
			// 画面表示情報を取得
			AnkenEditMinjiInputForm.AnkenBasicInputForm basicInputForm = new AnkenEditMinjiInputForm.AnkenBasicInputForm();

			// 表示用プロパティの設定
			service.setDisplayData(basicInputForm, ankenId, AnkenType.of(ankenType));
			mv = getMyModelAndView(basicInputForm, ANKEN_BASIC_LAWYER_OPTIONS_FRAGMENT_PATH, ANKEN_BASIC_INPUT_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件基本情報の更新前アラートチェック
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/beforeSaveAnkenBasicCheck", method = RequestMethod.POST)
	public Map<String, Object> beforeSaveAnkenBasicCheck(@PathVariable Long ankenId,
			@Validated AnkenEditMinjiInputForm.AnkenBasicInputForm basicInputForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		if (commonAnkenService.isChangedBunya(ankenId, basicInputForm.getBunya())) {
			// 分野を変更しようとしているケース

			if (commonAnkenService.canChangeBunya(ankenId)) {
				// 分野切替が可能な場合
				response.put("succeed", true);
				response.put("confirm", true);
				response.put("message", "分野に依存する情報を削除します。\nよろしいですか？");
				return response;
			} else {
				// 分野切替が不可能な場合
				response.put("succeed", false);
				response.put("confirm", false);
				response.put("message", getMessage(MessageEnum.MSG_E00046));
				return response;
			}
		}

		// 更新前に確認することがない場合
		response.put("succeed", true);
		response.put("confirm", false);
		return response;
	}

	/**
	 * 顧客の基本情報入力フォーム部分を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenCustomerListView", method = RequestMethod.GET)
	public ModelAndView getAnkenCustomerListView(@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {
			// 入力フォームを設定した画面の取得
			AnkenEditMinjiViewForm.AnkenCustomerListViewForm ankenCustomerListViewForm = service.createAnkenCustomerListViewForm(ankenId);
			mv = getMyModelAndView(ankenCustomerListViewForm, ANKEN_CUSTOMER_LIST_VIEW_FRAGMENT_PATH, ANKEN_CUSTOMER_LIST_VIEW_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 顧客の基本情報入力フォーム部分を取得する
	 *
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenCustomerInputView", method = RequestMethod.GET)
	public ModelAndView getAnkenCustomerInputView(@PathVariable Long ankenId, @RequestParam Long customerId) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			AnkenEditMinjiInputForm.AnkenCustomerInputForm ankenCustomerListInputForm = service.createAnkenCustomerInputForm(ankenId,
					customerId);
			mv = getMyModelAndView(ankenCustomerListInputForm, ANKEN_CUSTOMER_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_LIST_INPUT_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 顧客の基本情報入力フォーム部分を取得する
	 *
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenKanyoshaListView", method = RequestMethod.GET)
	public ModelAndView getAnkenKanyoshaListView(@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			AnkenEditMinjiViewForm.AnkenAitegataListViewForm ankenAitegataListViewForm = service.createAnkenAitegataListViewForm(ankenId);
			mv = getMyModelAndView(ankenAitegataListViewForm, ANKEN_KANYOSHA_LIST_FRAGMENT_PATH, ANKEN_KANYOSHA_LIST_VIEW_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件基本情報の更新処理
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveAnkenBasic", method = RequestMethod.POST)
	public ModelAndView saveAnkenBasic(@PathVariable Long ankenId, @Validated AnkenEditMinjiInputForm.AnkenBasicInputForm basicInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用プロパティの設定
		service.setDisplayData(basicInputForm, ankenId, basicInputForm.getAnkenType());

		// 担当者の相関チェック
		commonAnkenService.validateTanto(basicInputForm.getSalesOwner(), result, "salesOwner", "売上計上先");
		commonAnkenService.validateTanto(basicInputForm.getTantoLawyer(), result, "tantoLawyer", "担当弁護士");
		commonAnkenService.validateTanto(basicInputForm.getTantoJimu(), result, "tantoJimu", "担当事務員");

		commonAnkenService.validateSalesOwnerCount(basicInputForm.getSalesOwner(), result, "salesOwner");

		// 入力バリデーション
		if (result.hasErrors()) {
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(basicInputForm, ANKEN_BASIC_INPUT_FRAGMENT_PATH, ANKEN_BASIC_INPUT_FORM_NAME, result);
		}

		if (commonAnkenService.isChangedBunya(ankenId, basicInputForm.getBunya())) {
			// 分野を変更するケース

			if (this.accessDBValidated(ankenId)) {
				// 分野切替が不可の場合、処理失敗とする
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00046));
				return null;
			}

			try {
				// 分野切替処理を含む、案件情報の更新処理
				service.saveAnkenBasicHasChangeBunya(ankenId, basicInputForm);

				// 画面画面リダイレクトを行う特殊ケース
				super.setAjaxProcResult(AnkenBasicInputForm.HEADER_VALUE_OF_AJAX_PROC_RESULT_REDIRECT, "");
				return null;

			} catch (AppException e) {
				// 処理失敗とする
				super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
				return null;
			}

		} else {
			// 分野を変更しないケース

			try {
				// 案件情報の更新
				service.saveAnkenBasic(ankenId, basicInputForm);

				// 画面表示Viewを返却
				mv = this.getAnkenBasicView(ankenId);

			} catch (AppException e) {
				// 処理失敗とする
				super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
				return null;
			}
		}

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "案件情報"));
		return mv;
	}

	/**
	 * 顧客の基本情報入力フォーム部分を取得する
	 *
	 * @param ankenId
	 * @param customerId
	 * @param rowInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveAnkenCustomer", method = RequestMethod.POST)
	public ModelAndView saveAnkenCustomer(@PathVariable Long ankenId, @RequestParam Long customerId,
			@Validated AnkenEditMinjiInputForm.AnkenCustomerInputForm rowInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		service.setDisplayData(rowInputForm, ankenId, customerId);

		// 案件-顧客情報のバリデーション
		this.validateAnkenCustomer(rowInputForm, result);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合、処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(rowInputForm, ANKEN_CUSTOMER_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_LIST_INPUT_FORM_NAME, result);
		}

		try {
			// 案件顧客情報の保存処理
			service.saveAnkenCustomer(ankenId, customerId, rowInputForm);

			mv = this.getAnkenCustomerListView(ankenId);
		} catch (AppException e) {
			// エラー内容を返却
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "顧客情報"));
		return mv;
	}

	/**
	 * 顧客紐づけ解除
	 * 
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteCustomer", method = RequestMethod.POST)
	public Map<String, Object> deleteCustomer(@PathVariable Long ankenId, @RequestParam Long customerId) {

		Map<String, Object> response = new HashMap<>();

		// 削除可能かどうかを判定します。
		boolean isAbleToDeleteCustomer = commonAnkenService.isAbleToDeleteCustomer(ankenId, customerId);

		if (!isAbleToDeleteCustomer) {
			// 削除不可の場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00045.getMessageKey()));
			return response;
		}

		try {
			// 案件と顧客の紐づけを解除します。
			service.deleteAnkenCustomer(ankenId, customerId);

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00037.getMessageKey(), "案件"));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00101.getMessageKey(), "案件"));
			return response;
		}
	}

	/**
	 * 相手方情報の削除処理
	 * 
	 * @param ankenId
	 * @param aitegataKanyoshaSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteAitegata", method = RequestMethod.POST)
	public Map<String, Object> deleteAitegata(@PathVariable Long ankenId, @RequestParam(name = "aitegataKanyoshaSeq") Long aitegataKanyoshaSeq) {
		Map<String, Object> response = new HashMap<>();

		try {
			// 相手方情報の保存処理
			service.deleteAitegata(ankenId, aitegataKanyoshaSeq);

			response.put("succeed", true);
			response.put("message", getMessage(MessageEnum.MSG_I00037, "相手方"));
			return response;

		} catch (AppException e) {
			// エラー内容を返却
			response.put("succeed", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 関与者を案件から削除する前のチェック
	 * 
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteKanyoshaFromAnkenBeforeCheck", method = RequestMethod.POST)
	public Map<String, Object> deleteKanyoshaFromAnkenBeforeCheck(@RequestParam(name = "kanyoshaSeq") Long kanyoshaSeq) {

		Map<String, Object> response;
		try {
			response = service.deleteCommonKanyoshaFromAnkenBeforeCheck(kanyoshaSeq);
		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}
		return response;

	}

	/**
	 * 相手方情報の削除処理
	 * 
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/deleteKanyoshaFromAnken", method = RequestMethod.POST)
	public ModelAndView deleteKanyoshaFromAnken(@PathVariable Long ankenId, @RequestParam(name = "kanyoshaSeq") Long kanyoshaSeq) {
		ModelAndView mv = null;
		Map<String, String> errorMessage = new HashMap<>();
		// DB整合姓チェック
		if (!this.accessDBValidForDelete(kanyoshaSeq, errorMessage)) {
			super.setAjaxProcResultFailure(errorMessage.get("message"));
			return null;
		}
		try {

			// 削除処理
			service.deleteKanyoshaFromAnken(kanyoshaSeq);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;
		}
		// 正常に削除できたケースは、HTMLがないのでnull
		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00001));
		return mv;

	}

	/**
	 * 相手方代理人の削除処理
	 * 
	 * @param ankenId
	 * @param aitegataKanyoshaSeq
	 * @param dairininKanyoshaSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteDairinin", method = RequestMethod.POST)
	public Map<String, Object> deleteDairinin(@PathVariable Long ankenId,
			@RequestParam(name = "aitegataKanyoshaSeq") Long aitegataKanyoshaSeq,
			@RequestParam(name = "dairininKanyoshaSeq") Long dairininKanyoshaSeq) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 相手方情報の保存処理
			service.deleteAitegataDairinin(ankenId, aitegataKanyoshaSeq, dairininKanyoshaSeq);

			response.put("succeed", true);
			response.put("message", getMessage(MessageEnum.MSG_I00037, "代理人"));
			return response;

		} catch (AppException e) {
			// エラー内容を返却
			response.put("succeed", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 初回面談登録
	 *
	 * @param form フォーム
	 * @param result バリデーション結果
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createShokaiMendan", method = RequestMethod.POST)
	public Map<String, Object> createShokaiMendan(@Validated @ModelAttribute ScheduleInputForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力値の相関バリデートチェック(新規)
		scheduleCommonService.scheduleFormValidatedForRegist(form, result);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 入力値のDB整合性チェック(新規)
		if (scheduleCommonService.acessDBValdateRegistForSchedule(form, response)) {
			return response;
		}

		try {
			commonAnkenService.createShokaiMendanSchedule(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "初回面談予定"));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));
			return response;
		}
	}

	/**
	 * 初回面談更新
	 *
	 * @param form フォーム
	 * @param result バリデーション結果
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateShokaiMendan", method = RequestMethod.POST)
	public Map<String, Object> updateShokaiMendan(
			@Validated @ModelAttribute ScheduleInputForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力値の相関バリデートチェック(更新)
		scheduleCommonService.scheduleFormValidatedForUpdate(form, result);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 入力値のDB整合性チェック(更新)
		if (scheduleCommonService.acessDBValdateUpdateForSchedule(form, response)) {
			return response;
		}

		try {
			commonAnkenService.updateShokaiMendanSchedule(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "初回面談予定"));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));
			return response;
		}
	}

	/**
	 * 初回面談削除
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteShokaiMendan", method = RequestMethod.POST)
	public Map<String, Object> deleteShokaiMendan(@PathVariable Long ankenId,
			@RequestParam("customerId") Long customerId) {

		Map<String, Object> response = new HashMap<>();

		try {
			commonAnkenService.deleteShokaiMendanSchedule(ankenId, customerId);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00001));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00014));
			return response;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 分野切り替えが可能かどうか
	 * 
	 * @param ankenId
	 * @return
	 */
	private boolean accessDBValidated(Long ankenId) {

		boolean error = false;

		// 分野切替可能かどうかのチェック
		if (!commonAnkenService.canChangeBunya(ankenId)) {
			error = true;
		}

		return error;
	}

	/**
	 * 案件顧客情報のバリデーション
	 * 
	 * @param rowInputForm
	 * @param result
	 */
	private void validateAnkenCustomer(AnkenEditMinjiInputForm.AnkenCustomerInputForm rowInputForm, BindingResult result) {

		// 受任日が入力されていない && 事件処理日が入力されている
		if (rowInputForm.getJuninDateLocalDate() == null && rowInputForm.getJikenshoriKanryoDateLocalDate() != null) {
			if (!result.hasFieldErrors("juninDate")) {
				result.rejectValue("juninDate", MessageEnum.VARIDATE_MSG_E00002.getMessageKey());
			}
		}

		// 日付の整合性チェック
		if (!DateUtils.isCorrectDate(rowInputForm.getJuninDateLocalDate(), rowInputForm.getJikenshoriKanryoDateLocalDate())) {
			result.rejectValue("jikenshoriKanryoDate", MessageEnum.MSG_E00041.getMessageKey(), new String[]{"事件処理完了日", "受任日以降"}, "");
		}

	}

	/**
	 * 関与者削除時のDB整合バリデート
	 * 
	 * @param form
	 * @param response
	 * @return
	 */
	private boolean accessDBValidForDelete(Long kanyoshaSeq, Map<String, String> errorMap) {

		// 削除が不可能な場合、ここで返却
		if (!service.canDeleteKanyosha(kanyoshaSeq)) {
			errorMap.put("message", getMessage(MessageEnum.MSG_E00086, "会計情報が登録されている。もしくは預かり元として登録されている"));
			return false;
		}

		return true;
	}
}
