package jp.loioz.app.user.saibanManagement.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
import jp.loioz.app.common.service.CommonSaibanService;
import jp.loioz.app.common.service.chohyo.CommonChohyoService;
import jp.loioz.app.user.gyomuHistory.annitations.HasGyomuHistoryByAnken;
import jp.loioz.app.user.saibanManagement.form.SaibanEditKeijiInputForm;
import jp.loioz.app.user.saibanManagement.form.SaibanEditKeijiViewForm;
import jp.loioz.app.user.saibanManagement.form.SaibanScheduleInputForm;
import jp.loioz.app.user.saibanManagement.service.SaibanEditKeijiService;
import jp.loioz.app.user.schedule.annotations.HasSchedule;
import jp.loioz.app.user.schedule.form.ScheduleDetail;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.MessageLevel;
import jp.loioz.common.constant.CommonConstant.TojishaHyoki;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.StringUtils;

/**
 * 裁判管理画面のコントローラークラス
 */
@Controller
@HasSchedule
@HasGyomuHistoryByAnken
@RequestMapping(value = "user/saibanKeijiManagement/{saibanSeq}/{ankenId}")
public class SaibanEditKeijiController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/saibanManagement/saibanEditKeiji";

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyInputForm";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/******************************************************************
	 * サイドメニュー
	 ******************************************************************/

	/** 顧客案件メニューfragmentのパス */
	private static final String CUSTOMER_ANKENMENU_FRAGMENT_PATH = "common/customerAnkenMenu::menuList";

	/******************************************************************
	 * 基本情報
	 ******************************************************************/

	/** 裁判基本情報表示fragmentのパス */
	private static final String SAIBAN_BASIC_VIEW_FRAGMENT_PATH = "user/saibanManagement/saibanEditKeijiFragment::saibanBasicViewFragment";
	/** 裁判基本情報入力フォームfragmentのパス */
	private static final String SAIBAN_BASIC_INPUT_FRAGMENT_PATH = "user/saibanManagement/saibanEditKeijiFragment::saibanBasicInputFragment";

	/** 裁判 基本情報の表示用フォームオブジェクト名 */
	private static final String SAIBAN_BASIC_VIEW_FORM_NAME = "saibanBasicViewForm";
	/** 裁判 基本情報の入力用フォームオブジェクト名 */
	private static final String SAIBAN_BASIC_INPUT_FORM_NAME = "saibanBasicInputForm";

	/******************************************************************
	 * 期日
	 ******************************************************************/

	/** 裁判 期日 情報表示fragmentのパス */
	private static final String SAIBAN_KIJITSU_VIEW_FRAGMENT_PATH = "user/saibanManagement/saibanEditKeijiFragment::saibanKijitsuViewFragment";

	/** 裁判 期日 情報の表示用フォームオブジェクト名 */
	private static final String SAIBAN_KIJITSU_VIEW_FORM_NAME = "saibanKijitsuViewForm";

	/** 裁判 期日結果 入力fragmentのパス */
	private static final String SAIBAN_KIJITSU_RESULT_INPUT_FRAGMENT_PATH = "user/saibanManagement/saibanEditKeijiFragment::saibanKijitsuResultInputFragment";

	/** 裁判 期日結果 入力用フォームオブジェクト名 */
	private static final String SAIBAN_KIJITSU_RESULT_INPUT_VIEW_FORM_NAME = "saibanKijitsuResultInputForm";

	/******************************************************************
	 * 顧客
	 ******************************************************************/

	/** 裁判 顧客 情報表示fragmentのパス */
	private static final String SAIBAN_CUSTOMER_VIEW_FRAGMENT_PATH = "user/saibanManagement/saibanEditKeijiFragment::saibanCustomerViewFragment";

	/** 裁判 顧客 情報の表示用フォームオブジェクト名 */
	private static final String SAIBAN_CUSTOMER_VIEW_FORM_NAME = "saibanCustomerViewForm";

	/** 裁判 顧客 追加入力fragmentのパス */
	private static final String SAIBAN_CUSTOMER_ADD_INPUT_FRAGMENT_PATH = "user/saibanManagement/saibanEditKeijiFragment::saibanCustomerAddInputFragment";

	/** 裁判 顧客 追加入力用フォームオブジェクト名 */
	private static final String SAIBAN_CUSTOMER_ADD_INPUT_VIEW_FORM_NAME = "saibanCustomerAddInputForm";

	/******************************************************************
	 * 共犯者
	 ******************************************************************/

	/** 裁判 共犯者 情報表示fragmentのパス */
	private static final String SAIBAN_OTHER_VIEW_FRAGMENT_PATH = "user/saibanManagement/saibanEditKeijiFragment::saibanOtherViewFragment";

	/** 裁判 共犯者 情報の表示用フォームオブジェクト名 */
	private static final String SAIBAN_OTHER_VIEW_FORM_NAME = "saibanOtherViewForm";

	/******************************************************************
	 * 公判担当検察官
	 ******************************************************************/

	/** 公判担当検察官 情報表示fragmentのパス */
	private static final String SAIBAN_KENSATSUKAN_VIEW_FRAGMENT_PATH = "user/saibanManagement/saibanEditKeijiFragment::saibanKensatsukanViewFragment";
	/** 公判担当検察官 情報入力fragmentのパス */
	private static final String SAIBAN_KENSATSUKAN_INPUT_FRAGMENT_PATH = "user/saibanManagement/saibanEditKeijiFragment::saibanKensatsukanInputFragment";

	/** 公判担当検察官 情報の表示用フォームオブジェクト名 */
	private static final String SAIBAN_KENSATSUKAN_VIEW_FORM_NAME = "saibanKensatsukanViewForm";
	/** 公判担当検察官 入力用 フォームオブジェクト名 */
	private static final String SAIBAN_KENSATSUKAN_INPUT_FORM_NAME = "saibanKensatsukanInputForm";

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/** サービスクラス */
	@Autowired
	private SaibanEditKeijiService service;

	/** 共通帳票サービスクラス */
	@Autowired
	private CommonSaibanService commonSaibanService;

	/** 共通帳票サービスクラス */
	@Autowired
	private CommonChohyoService commonChohyoService;

	/** 共通案件サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable Long saibanSeq, @PathVariable Long ankenId) {

		// 画面表示情報を取得
		SaibanEditKeijiViewForm viewForm = service.createViewForm(saibanSeq, ankenId);

		// 基本情報を取得
		SaibanEditKeijiViewForm.SaibanBasicViewForm basicViewForm = service.createSaibanBasicViewForm(saibanSeq);
		viewForm.setSaibanBasicViewForm(basicViewForm);

		// 期日情報を取得（初期表示では、全期間分のデータは取得しない）
		boolean isAllTimeKijitsuList = false;
		SaibanEditKeijiViewForm.SaibanKijitsuViewForm kijitsuViewForm = service.createSaibanKijitsuViewForm(saibanSeq, isAllTimeKijitsuList);
		viewForm.setSaibanKijitsuViewForm(kijitsuViewForm);

		// 顧客（被告人）情報を取得
		SaibanEditKeijiViewForm.SaibanCustomerViewForm customerViewForm = service.createSaibanCustomerViewForm(saibanSeq);
		viewForm.setSaibanCustomerViewForm(customerViewForm);

		// 共犯者情報を取得
		SaibanEditKeijiViewForm.SaibanOtherViewForm otherViewForm = service.createSaibanOtherViewForm(saibanSeq);
		viewForm.setSaibanOtherViewForm(otherViewForm);

		// 公判担当検察官を取得
		SaibanEditKeijiViewForm.SaibanKensatsukanViewForm kensatsukanViewForm = service.createSaibanKensatsukanViewForm(saibanSeq);
		viewForm.setSaibanKensatsukanViewForm(kensatsukanViewForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * メッセージ表示をともなう初期表示画面へのリダイレクト処理
	 * 
	 * @param saibanSeq
	 * @param levelCd メッセージレベル
	 * @param message メッセージ
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "redirectIndexWithMessage", method = RequestMethod.GET)
	public ModelAndView redirectIndexWithMessage(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam("level") String levelCd, @RequestParam("message") String message,
			RedirectAttributes redirectAttributes) {

		// 自画面のindexメソッドへのリダイレクトオブジェクトを生成
		String redirectPath = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.index(saibanSeq, ankenId));
		RedirectViewBuilder redirectViewBuilder = new RedirectViewBuilder(redirectAttributes, redirectPath);

		MessageLevel level = MessageLevel.of(levelCd);
		return redirectViewBuilder.build(MessageHolder.ofLevel(level, StringUtils.lineBreakStr2Code(message)));
	}

	// ▼ 基本情報部分

	/**
	 * 裁判の基本情報表示フォーム部分を取得する
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getSaibanBasicViewForm", method = RequestMethod.GET)
	public ModelAndView getSaibanBasicView(@PathVariable Long saibanSeq, @PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 裁判の基本情報表示フォームを設定した画面の取得
			SaibanEditKeijiViewForm.SaibanBasicViewForm basicViewForm = service.createSaibanBasicViewForm(saibanSeq);
			mv = getMyModelAndView(basicViewForm, SAIBAN_BASIC_VIEW_FRAGMENT_PATH, SAIBAN_BASIC_VIEW_FORM_NAME);

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
	 * 裁判期日 表示フォーム部分を取得する
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param isAllTimeKijitsuList
	 * @return
	 */
	@RequestMapping(value = "/getSaibanKijitsuViewForm", method = RequestMethod.GET)
	public ModelAndView getSaibanKijitsuView(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "isAllTimeKijitsuList", required = false) boolean isAllTimeKijitsuList) {

		ModelAndView mv = null;

		try {

			// 裁判の期日 表示フォームを設定した画面の取得
			SaibanEditKeijiViewForm.SaibanKijitsuViewForm kijitsuViewForm = service.createSaibanKijitsuViewForm(saibanSeq, isAllTimeKijitsuList);
			mv = getMyModelAndView(kijitsuViewForm, SAIBAN_KIJITSU_VIEW_FRAGMENT_PATH, SAIBAN_KIJITSU_VIEW_FORM_NAME);

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
	 * 裁判刑事画面：期日予定情報の詳細モーダルデータを取得
	 * 
	 * ThymeleafのモデルデータのJson化は予定データの日付フォーマット処理が考慮されていないため、
	 * 名簿画面のスケジュール一覧レンダリング直後にAjaxにより取得する
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param isAllTimeKijitsuList
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getSaibanKijitsuDetails", method = RequestMethod.GET)
	public Map<Long, ScheduleDetail> getSaibanKijitsuDetails(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "isAllTimeKijitsuList", required = false) boolean isAllTimeKijitsuList) {

		try {
			Map<Long, ScheduleDetail> scheduleDetails = service.getSaibanKijitsuDetails(saibanSeq, isAllTimeKijitsuList);

			super.setAjaxProcResultSuccess();
			return scheduleDetails;

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}
	}

	/**
	 * 裁判期日 入力フォーム部分を取得する
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param saibanLimitSeq
	 * @return
	 */
	@RequestMapping(value = "/getSaibanKijitsuResultInputForm", method = RequestMethod.GET)
	public ModelAndView getSaibanKijitsuResultInputForm(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "saibanLimitSeq") Long saibanLimitSeq) {

		ModelAndView mv = null;

		try {

			// 期日結果入力フォームを設定した画面の取得
			SaibanEditKeijiInputForm.SaibanKijitsuResultInputForm saibanKijitsuResultInputForm = service.createSaibanKijitsuResultInputForm(saibanLimitSeq);
			mv = getMyModelAndView(saibanKijitsuResultInputForm, SAIBAN_KIJITSU_RESULT_INPUT_FRAGMENT_PATH, SAIBAN_KIJITSU_RESULT_INPUT_VIEW_FORM_NAME);

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
	 * 顧客情報 表示フォーム部分を取得する
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getSaibanCustomerViewForm", method = RequestMethod.GET)
	public ModelAndView getSaibanCustomerView(@PathVariable Long saibanSeq, @PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 裁判の顧客情報表示フォームを設定した画面の取得
			SaibanEditKeijiViewForm.SaibanCustomerViewForm customerViewForm = service.createSaibanCustomerViewForm(saibanSeq);
			mv = getMyModelAndView(customerViewForm, SAIBAN_CUSTOMER_VIEW_FRAGMENT_PATH, SAIBAN_CUSTOMER_VIEW_FORM_NAME);

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
	 * 共犯者 表示フォーム部分を取得する
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getSaibanOtherViewForm", method = RequestMethod.GET)
	public ModelAndView getSaibanOtherView(@PathVariable Long saibanSeq, @PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 裁判の基本情報表示フォームを設定した画面の取得
			SaibanEditKeijiViewForm.SaibanOtherViewForm otherViewForm = service.createSaibanOtherViewForm(saibanSeq);
			mv = getMyModelAndView(otherViewForm, SAIBAN_OTHER_VIEW_FRAGMENT_PATH, SAIBAN_OTHER_VIEW_FORM_NAME);

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
	 * 検察官 表示フォーム部分を取得する
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getSaibanKensatsukanView", method = RequestMethod.GET)
	public ModelAndView getSaibanKensatsukanView(@PathVariable Long saibanSeq, @PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 裁判の基本情報表示フォームを設定した画面の取得
			SaibanEditKeijiViewForm.SaibanKensatsukanViewForm aitegataViewForm = service.createSaibanKensatsukanViewForm(saibanSeq);
			mv = getMyModelAndView(aitegataViewForm, SAIBAN_KENSATSUKAN_VIEW_FRAGMENT_PATH, SAIBAN_KENSATSUKAN_VIEW_FORM_NAME);

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
	 * 裁判民事の基本情報の保存
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param basicInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveSaibanBasic", method = RequestMethod.POST)
	public ModelAndView saveSaibanBasic(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) SaibanEditKeijiInputForm.SaibanBasicInputForm basicInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		service.setDisplayData(basicInputForm, saibanSeq);

		// 担当者の相関チェック
		commonAnkenService.validateTanto(basicInputForm.getTantoLawyer(), result, "tantoLawyer", "担当弁護士");
		commonAnkenService.validateTanto(basicInputForm.getTantoJimu(), result, "tantoJimu", "担当事務員");

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 入力フォームにerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(basicInputForm, SAIBAN_BASIC_INPUT_FRAGMENT_PATH, SAIBAN_BASIC_INPUT_FORM_NAME, result);
		}

		try {

			// 保存処理
			service.saveSaibanBasic(saibanSeq, basicInputForm);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {
			// 想定しないエラー

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00013));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getSaibanBasicView(saibanSeq, ankenId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "裁判情報"));
		return mv;
	}

	/**
	 * 裁判削除
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteSaiban", method = RequestMethod.POST)
	public Map<String, Object> deleteSaiban(@PathVariable Long saibanSeq, @PathVariable Long ankenId) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 削除処理
			service.deleteSaiban(saibanSeq);
			response.put("succeeded", true);
			response.put("ankenId", ankenId);
			response.put("message", getMessage(MessageEnum.MSG_I00028, "裁判情報"));
			return response;

		} catch (Exception e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013.getMessageKey()));
			return response;
		}

	}

	/**
	 * 裁判の基本情報入力フォーム部分を取得する
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getSaibanBasicInputForm", method = RequestMethod.GET)
	public ModelAndView getSaibanBasicInputForm(@PathVariable Long saibanSeq, @PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			SaibanEditKeijiInputForm.SaibanBasicInputForm basicInfoInputForm = service.createSaibanBasicInputForm(saibanSeq);
			mv = getMyModelAndView(basicInfoInputForm, SAIBAN_BASIC_INPUT_FRAGMENT_PATH, SAIBAN_BASIC_INPUT_FORM_NAME);

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
	 * 顧客追加 入力フォーム部分を取得する
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getSaibanCustomerAddInputForm", method = RequestMethod.GET)
	public ModelAndView getSaibanCustomerAddInputForm(@PathVariable Long saibanSeq, @PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 顧客追加入力フォームを設定した画面の取得
			SaibanEditKeijiInputForm.SaibanCustomerAddInputForm customerAddInputForm = service.createSaibanCustomerAddInputForm(saibanSeq, ankenId);
			mv = getMyModelAndView(customerAddInputForm, SAIBAN_CUSTOMER_ADD_INPUT_FRAGMENT_PATH, SAIBAN_CUSTOMER_ADD_INPUT_VIEW_FORM_NAME);

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
	 * 公判担当検察官の入力フォーム部分を取得する
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getSaibanKensatsukanInputForm", method = RequestMethod.GET)
	public ModelAndView getSaibanKensatsukanInputForm(@PathVariable Long saibanSeq, @PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			SaibanEditKeijiInputForm.SaibanKensatsukanInputForm ankenSosakikanListInputForm = service.createSaibanKensatsukanInputForm(saibanSeq);
			mv = getMyModelAndView(ankenSosakikanListInputForm, SAIBAN_KENSATSUKAN_INPUT_FRAGMENT_PATH, SAIBAN_KENSATSUKAN_INPUT_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 顧客追加 登録
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param customerAddInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registSaibanCustomerAdd", method = RequestMethod.POST)
	public ModelAndView registSaibanCustomerAdd(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) SaibanEditKeijiInputForm.SaibanCustomerAddInputForm customerAddInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		// 特になし

		// BindingResultのエラーチェック
		// 特になし

		// 画面独自のバリデーションチェック
		// 特になし

		// DBバリデーション
		// 特になし

		try {

			// 保存処理
			service.registSaibanCustomerAdd(saibanSeq, ankenId, customerAddInputForm);

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

		// 保存成功後は、顧客情報表示部分を返却する
		mv = this.getSaibanCustomerView(saibanSeq, ankenId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "被告人"));
		return mv;
	}

	/**
	 * 顧客削除
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/removeSaibanCustomer", method = RequestMethod.POST)
	public ModelAndView removeSaibanCustomerFromTojisha(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "customerId") Long customerId) {

		ModelAndView mv = null;

		// 表示用データの設定
		// 特になし

		// BindingResultのエラーチェック
		// 特になし

		// 画面独自のバリデーションチェック
		// 特になし

		// DBバリデーション
		// 特になし

		try {

			// 保存処理
			service.removeSaibanCustomer(saibanSeq, customerId);

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

		// 保存成功後は、顧客情報表示部分を返却する
		mv = this.getSaibanCustomerView(saibanSeq, ankenId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00037, "被告人"));
		return mv;
	}

	/**
	 * 筆頭を変更（顧客情報部分と、共犯者部分で共通で使用）
	 * 
	 * <pre>
	 * ※どちらの箇所から呼ばれたとしても、更新処理後、顧客情報部分と共犯者部分の両方を再レンダリングする必要があるため、
	 *   このメソッドでは、顧客情報部分のHTMLを返却する。
	 *   （共犯者部分はこのメソッドの戻り値を受けたあと、フロント側で別途リクエストを送信し、再レンダリングをすること）
	 * </pre>
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param customerId toHittouがtrueの場合に、「筆頭」とする顧客のID
	 * @param kanyoshaSeq toHittouがtrueの場合に、「筆頭」とする関与者のSEQ
	 * @param toHittou 「筆頭」状態に更新するかどうか。falseの場合は全ての当事者データを「筆頭」ではない状態に更新する。
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveTojishaHittou", method = RequestMethod.POST)
	public ModelAndView saveTojishaHittou(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "customerId", required = false) Long customerId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq,
			@RequestParam(name = "toHittou") boolean toHittou) {

		ModelAndView mv = null;

		try {

			// 保存処理
			service.saveTojishaHittou(saibanSeq, customerId, kanyoshaSeq, toHittou);

		} catch (AppException e) {
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00013));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、顧客情報表示部分を返却する
		mv = this.getSaibanCustomerView(saibanSeq, ankenId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "筆頭"));
		return mv;
	}

	/**
	 * 公判担当検察官の保存
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveKensatsukan", method = RequestMethod.POST)
	public ModelAndView saveKensatsukan(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) SaibanEditKeijiInputForm.SaibanKensatsukanInputForm inputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		service.setDisplayData(inputForm);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(inputForm, SAIBAN_KENSATSUKAN_INPUT_FRAGMENT_PATH, SAIBAN_KENSATSUKAN_INPUT_FORM_NAME, result);
		}

		// DBアクセスバリデーションは現状特になし

		try {

			// 保存処理
			service.saveKensatsukan(saibanSeq, inputForm);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00013));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getSaibanKensatsukanView(saibanSeq, ankenId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "公判担当検察官"));
		return mv;
	}

	/**
	 * 送付書出力(公判担当者)
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @param souhushoType
	 * @param response
	 */
	@RequestMapping(value = "/outputSouhushoKensatsukan", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputSouhushoKensatsukan(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "souhushoType") String souhushoType,
			HttpServletResponse response) {

		try {

			// 出力する送付書タイプを判定
			if (CommonConstant.SouhushoType.IPPAN.equalsByCode(souhushoType)) {
				// 一般用
				commonChohyoService.outputSouhushoIppanForKohan(response, ankenId, saibanSeq);

			} else if (CommonConstant.SouhushoType.FAX_USE.equalsByCode(souhushoType)) {
				// FAX用
				commonChohyoService.outputSouhushoFaxForKohan(response, ankenId, saibanSeq);

			} else {
				// 選択項目なし

			}

		} catch (Exception ex) {
			// 送付書の出力失敗
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName, ex);
		}
	}

	/**
	 * 公判期日請書を出力する
	 *
	 * @param response
	 * @param saibanSeq
	 * @param ankenId
	 * @param scheduleSeq
	 */
	@RequestMapping(value = "/outputKohanKijitsu", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputKohanKijitsu(HttpServletResponse response, @PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "scheduleSeq") Long scheduleSeq) {

		try {

			// 出力処理
			commonSaibanService.outputKohanKijitsu(response, saibanSeq, scheduleSeq);

		} catch (Exception ex) {

			// 送付書の出力失敗
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName, ex);
		}

	}

	/**
	 * 裁判期日登録
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/registSaibanKijitsu", method = RequestMethod.POST)
	public Map<String, Object> registSaibanKijitsu(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@Validated @ModelAttribute SaibanScheduleInputForm inputForm,
			BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力値の相関バリデートチェック
		commonSaibanService.saibanScheduleInputFormValidatedForRegist(inputForm, result);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 入力値のDB整合性チェック
		if (commonSaibanService.acessDBValdateRegistForSchedule(inputForm, response)) {
			return response;
		}

		try {

			// 裁判期日登録
			commonSaibanService.registSaibanKijitsuSchedule(saibanSeq, inputForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "裁判期日"));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));
			return response;
		}
	}

	/**
	 * 裁判期日更新
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/saveSaibanKijitsu", method = RequestMethod.POST)
	public Map<String, Object> saveSaibanKijitsu(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@Validated @ModelAttribute SaibanScheduleInputForm inputForm,
			BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力値の相関バリデートチェック
		commonSaibanService.saibanScheduleInputFormValidatedForUpdate(inputForm, result);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 入力値のDB整合性チェック
		if (commonSaibanService.acessDBValdateUpdateForSchedule(inputForm, response)) {
			return response;
		}

		try {
			// 裁判期日更新
			commonSaibanService.updateSaibanKijitsuSchedule(inputForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "裁判期日"));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 裁判期日 期日結果の保存
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param saibanKijitsuResultInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveSaibanKijitsuResult", method = RequestMethod.POST)
	public ModelAndView saveSaibanKijitsuResult(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) SaibanEditKeijiInputForm.SaibanKijitsuResultInputForm saibanKijitsuResultInputForm,
			BindingResult result) {

		// 表示用データの設定
		// 特になし

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 入力フォームにerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(saibanKijitsuResultInputForm, SAIBAN_KIJITSU_RESULT_INPUT_FRAGMENT_PATH, SAIBAN_KIJITSU_RESULT_INPUT_VIEW_FORM_NAME, result);
		}

		// 画面独自のバリデーションチェック
		// 特になし

		// DBバリデーション
		// 特になし

		try {

			// 保存処理
			service.saveSaibanKijitsuResult(saibanKijitsuResultInputForm);

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

		// 保存後の画面再描画処理は、フロント側で実行する

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "期日結果"));
		return null;
	}

	/**
	 * 裁判期日削除
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @param scheduleSeq
	 * @param saibanLimitSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteSaibanKijitsu", method = RequestMethod.POST)
	public Map<String, Object> deleteSaibanKijitsu(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "scheduleSeq", required = false) Long scheduleSeq,
			@RequestParam("saibanLimitSeq") Long saibanLimitSeq) {

		Map<String, Object> response = new HashMap<>();

		try {

			// 裁判期日削除
			commonSaibanService.deleteSaibanKijitsuSchedule(saibanSeq, scheduleSeq, saibanLimitSeq);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00028, "裁判期日"));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 顧客 当事者表記の変更
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param customerId
	 * @param tojishaHyoki
	 * @return
	 */
	@RequestMapping(value = "/saveCustomerTojishaHyoki", method = RequestMethod.POST)
	public ModelAndView saveCustomerTojishaHyoki(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "customerId") Long customerId,
			@RequestParam(name = "tojishaHyoki") TojishaHyoki tojishaHyoki) {

		ModelAndView mv = null;

		try {

			// 保存処理
			service.saveCustomerTojishaHyoki(saibanSeq, customerId, tojishaHyoki);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00013));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、顧客情報表示部分を返却する
		mv = this.getSaibanCustomerView(saibanSeq, ankenId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "当事者表記"));
		return mv;
	}

	/**
	 * 共犯者 当事者表記の変更
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @param tojishaHyoki
	 * @return
	 */
	@RequestMapping(value = "/saveKanyoshaTojishaHyoki", method = RequestMethod.POST)
	public ModelAndView saveKanyoshaTojishaHyoki(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "kanyoshaSeq") Long kanyoshaSeq,
			@RequestParam(name = "tojishaHyoki") TojishaHyoki tojishaHyoki) {

		ModelAndView mv = null;

		try {

			// 保存処理
			service.saveKanyoshaTojishaHyoki(saibanSeq, kanyoshaSeq, tojishaHyoki);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00013));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、共犯者情報表示部分を返却する
		mv = this.getSaibanOtherView(saibanSeq, ankenId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "当事者表記"));
		return mv;
	}

	/**
	 * 共犯者者削除
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/removeSaibanKanyosha", method = RequestMethod.POST)
	public ModelAndView removeSaibanKanyosha(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "kanyoshaSeq") Long kanyoshaSeq) {

		ModelAndView mv = null;

		try {

			// 削除処理
			service.removeSaibanKanyosha(saibanSeq, kanyoshaSeq);

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

		// 削除成功後は、その他当事者情報表示部分を返却する
		mv = this.getSaibanOtherView(saibanSeq, ankenId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00037, "共犯者"));
		return mv;
	}

	/**
	 * 関与者を案件から削除する前のチェック（被害者、弁護人いずれも可）
	 * 
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/removeSaibanKanyoshaFromAnkenBeforeCheck", method = RequestMethod.POST)
	public Map<String, Object> removeSaibanKanyoshaFromAnkenBeforeCheck(@RequestParam(name = "kanyoshaSeq") Long kanyoshaSeq) {

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
	 * 関与者を案件から削除する（被害者、弁護人いずれも可）
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/removeSaibanKanyoshaFromAnken", method = RequestMethod.POST)
	public ModelAndView removeSaibanKanyoshaFromAnken(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "kanyoshaSeq") Long kanyoshaSeq) {

		ModelAndView mv = null;
		Map<String, String> errorMessage = new HashMap<>();
		// DB整合姓チェック
		if (!this.accessDBValidForDelete(kanyoshaSeq, errorMessage)) {
			super.setAjaxProcResultFailure(errorMessage.get("message"));
			return null;
		}
		try {

			// 削除処理
			service.deleteCommonKanyoshaFromAnken(kanyoshaSeq);

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

		// 削除成功後は、その他当事者情報表示部分を返却する
		mv = this.getSaibanOtherView(saibanSeq, ankenId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00037, "共犯者"));
		return mv;
	}

	/**
	 * 共犯者の弁護人を外す
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/removeSaibanRelatedKanyosha", method = RequestMethod.POST)
	public ModelAndView removeSaibanRelatedKanyosha(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "kanyoshaSeq") Long kanyoshaSeq) {

		ModelAndView mv = null;

		try {

			// 代理人を外す処理
			service.removeSaibanRelatedKanyosha(saibanSeq, kanyoshaSeq);

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

		// 処理成功後は、その他当事者情報表示部分を返却する
		mv = this.getSaibanOtherView(saibanSeq, ankenId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00037, "弁護人"));
		return mv;
	}

	/**
	 * 顧客案件のサイドメニュー部分を取得する
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenMenuView", method = RequestMethod.GET)
	public ModelAndView getAnkenMenuView(@PathVariable Long saibanSeq, @PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// サイドメニュー画面の取得
			mv = ModelAndViewUtils.getModelAndView(CUSTOMER_ANKENMENU_FRAGMENT_PATH);
			// パラメータ情報を設定
			mv.addObject("sideMenuAnkenId", ankenId);
			mv.addObject("sideMenuCustomerId", null);
			mv.addObject("selectedSaiban", saibanSeq);
			mv.addObject("selectedTabClass", "is_saiban");

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
	 * 削除時のDB整合バリデート
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
