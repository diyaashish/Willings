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
import jp.loioz.app.common.validation.accessDB.CommonKanyoshaValidator;
import jp.loioz.app.user.saibanManagement.form.SaibanEditMinjiInputForm;
import jp.loioz.app.user.saibanManagement.form.SaibanEditMinjiViewForm;
import jp.loioz.app.user.saibanManagement.form.SaibanScheduleInputForm;
import jp.loioz.app.user.saibanManagement.service.SaibanEditMinjiService;
import jp.loioz.app.user.schedule.annotations.HasSchedule;
import jp.loioz.app.user.schedule.form.ScheduleDetail;
import jp.loioz.common.constant.CommonConstant.MessageLevel;
import jp.loioz.common.constant.CommonConstant.ShutteiType;
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
 * 裁判管理（民事）画面のコントローラークラス
 */
@Controller
@HasSchedule
@RequestMapping(value = "user/saibanMinjiManagement/{saibanSeq}/{ankenId}")
public class SaibanEditMinjiController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/saibanManagement/saibanEditMinji";

	/******************************************************************
	 * サイドメニュー
	 ******************************************************************/

	/** 顧客案件メニューfragmentのパス */
	private static final String CUSTOMER_ANKENMENU_FRAGMENT_PATH = "common/customerAnkenMenu::menuList";

	/******************************************************************
	 * 基本情報
	 ******************************************************************/

	/** 裁判基本情報表示fragmentのパス */
	private static final String SAIBAN_BASIC_VIEW_FRAGMENT_PATH = "user/saibanManagement/saibanEditMinjiFragment::saibanBasicViewFragment";
	/** 裁判基本情報入力フォームfragmentのパス */
	private static final String SAIBAN_BASIC_INPUT_FRAGMENT_PATH = "user/saibanManagement/saibanEditMinjiFragment::saibanBasicInputFragment";

	/** 裁判 基本情報の表示用フォームオブジェクト名 */
	private static final String SAIBAN_BASIC_VIEW_FORM_NAME = "saibanBasicViewForm";
	/** 裁判 基本情報の入力用フォームオブジェクト名 */
	private static final String SAIBAN_BASIC_INPUT_FORM_NAME = "saibanBasicInputForm";

	/******************************************************************
	 * 裁判追加登録など
	 ******************************************************************/

	/** 裁判 オプション 情報表示fragmentのパス */
	private static final String SAIBAN_OPTION_VIEW_FRAGMENT_PATH = "user/saibanManagement/saibanEditMinjiFragment::saibanOptionViewFragment";

	/** 裁判 オプション 情報の表示用フォームオブジェクト名 */
	private static final String SAIBAN_OPTION_VIEW_FORM_NAME = "saibanOptionViewForm";

	/******************************************************************
	 * タブ
	 ******************************************************************/

	/** 裁判 タブ 情報表示fragmentのパス */
	private static final String SAIBAN_TAB_VIEW_FRAGMENT_PATH = "user/saibanManagement/saibanEditMinjiFragment::saibanTabViewFragment";

	/** 裁判 タブ 情報の表示用フォームオブジェクト名 */
	private static final String SAIBAN_TAB_VIEW_FORM_NAME = "saibanTabViewForm";

	/******************************************************************
	 * 期日
	 ******************************************************************/

	/** 裁判 期日 情報表示fragmentのパス */
	private static final String SAIBAN_KIJITSU_VIEW_FRAGMENT_PATH = "user/saibanManagement/saibanEditMinjiFragment::saibanKijitsuViewFragment";

	/** 裁判 期日 情報の表示用フォームオブジェクト名 */
	private static final String SAIBAN_KIJITSU_VIEW_FORM_NAME = "saibanKijitsuViewForm";

	/** 裁判 期日結果 入力fragmentのパス */
	private static final String SAIBAN_KIJITSU_RESULT_INPUT_FRAGMENT_PATH = "user/saibanManagement/saibanEditMinjiFragment::saibanKijitsuResultInputFragment";

	/** 裁判 期日結果 入力用フォームオブジェクト名 */
	private static final String SAIBAN_KIJITSU_RESULT_INPUT_VIEW_FORM_NAME = "saibanKijitsuResultInputForm";

	/******************************************************************
	 * 顧客
	 ******************************************************************/

	/** 裁判 顧客 情報表示fragmentのパス */
	private static final String SAIBAN_CUSTOMER_VIEW_FRAGMENT_PATH = "user/saibanManagement/saibanEditMinjiFragment::saibanCustomerViewFragment";

	/** 裁判 顧客 情報の表示用フォームオブジェクト名 */
	private static final String SAIBAN_CUSTOMER_VIEW_FORM_NAME = "saibanCustomerViewForm";

	/** 裁判 顧客 追加入力fragmentのパス */
	private static final String SAIBAN_CUSTOMER_ADD_INPUT_FRAGMENT_PATH = "user/saibanManagement/saibanEditMinjiFragment::saibanCustomerAddInputFragment";

	/** 裁判 顧客 追加入力用フォームオブジェクト名 */
	private static final String SAIBAN_CUSTOMER_ADD_INPUT_VIEW_FORM_NAME = "saibanCustomerAddInputForm";

	/******************************************************************
	 * その他当事者
	 ******************************************************************/

	/** 裁判 その他当事者 情報表示fragmentのパス */
	private static final String SAIBAN_OTHER_VIEW_FRAGMENT_PATH = "user/saibanManagement/saibanEditMinjiFragment::saibanOtherViewFragment";

	/** 裁判 その他当事者 情報の表示用フォームオブジェクト名 */
	private static final String SAIBAN_OTHER_VIEW_FORM_NAME = "saibanOtherViewForm";

	/******************************************************************
	 * 相手方情報
	 ******************************************************************/

	/** 裁判 相手方 情報表示fragmentのパス */
	private static final String SAIBAN_AITEGATA_VIEW_FRAGMENT_PATH = "user/saibanManagement/saibanEditMinjiFragment::saibanAitegataViewFragment";

	/** 裁判 相手方 情報の表示用フォームオブジェクト名 */
	private static final String SAIBAN_AITEGATA_VIEW_FORM_NAME = "saibanAitegataViewForm";

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyInputForm";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/** サービスクラス */
	@Autowired
	private SaibanEditMinjiService service;

	/** 共通裁判サービスクラス */
	@Autowired
	private CommonSaibanService commonSaibanService;

	/** 共通案件サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	@Autowired
	private CommonKanyoshaValidator commonKanyoshaValidator;

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

		// 画面での編集可否判定（初期表示は基本・事件を表示、編集できるためfalse）
		boolean isDisplayOnly = false;

		// 画面表示情報を取得
		SaibanEditMinjiViewForm viewForm = service.createViewForm(saibanSeq, ankenId);

		// 基本情報を取得
		SaibanEditMinjiViewForm.SaibanBasicViewForm basicViewForm = service.createSaibanBasicViewForm(saibanSeq);
		viewForm.setSaibanBasicViewForm(basicViewForm);

		// 裁判オプション情報
		SaibanEditMinjiViewForm.SaibanOptionViewForm optionViewForm = service.createSaibanOptionViewForm(saibanSeq);
		viewForm.setSaibanOptionViewForm(optionViewForm);

		// タブ情報（基本事件は分離/取下不可なのでfalse）
		boolean bunriTorisageButtonFlg = false;
		SaibanEditMinjiViewForm.SaibanTabViewForm tabViewForm = service.createSaibanTabViewForm(saibanSeq, saibanSeq, bunriTorisageButtonFlg);
		viewForm.setSaibanTabViewForm(tabViewForm);

		// 期日情報を取得（初期表示では、全期間分のデータは取得しない。）
		boolean isAllTimeKijitsuList = false;
		boolean isChildSaiban = false;
		SaibanEditMinjiViewForm.SaibanKijitsuViewForm kijitsuViewForm = service.createSaibanKijitsuViewForm(saibanSeq, isAllTimeKijitsuList, isDisplayOnly, isChildSaiban);
		viewForm.setSaibanKijitsuViewForm(kijitsuViewForm);

		// 顧客情報を取得
		SaibanEditMinjiViewForm.SaibanCustomerViewForm customerViewForm = service.createSaibanCustomerViewForm(saibanSeq, isDisplayOnly);
		viewForm.setSaibanCustomerViewForm(customerViewForm);

		// その他当事者情報を取得
		SaibanEditMinjiViewForm.SaibanOtherViewForm otherViewForm = service.createSaibanOtherViewForm(saibanSeq, isDisplayOnly);
		viewForm.setSaibanOtherViewForm(otherViewForm);

		// 相手方情報を取得
		SaibanEditMinjiViewForm.SaibanAitegataViewForm aitegataViewForm = service.createSaibanAitegataViewForm(saibanSeq, isDisplayOnly);
		viewForm.setSaibanAitegataViewForm(aitegataViewForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * メッセージ表示をともなう初期表示画面へのリダイレクト処理
	 * 
	 * @param saibanSeq
	 * @param ankenId
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
			SaibanEditMinjiViewForm.SaibanBasicViewForm basicViewForm = service.createSaibanBasicViewForm(saibanSeq);
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
			SaibanEditMinjiInputForm.SaibanBasicInputForm basicInfoInputForm = service.createSaibanBasicInputForm(saibanSeq);
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
	 * オプション 表示フォーム部分を取得する
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getSaibanOptionViewForm", method = RequestMethod.GET)
	public ModelAndView getSaibanOptionView(@PathVariable Long saibanSeq, @PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 裁判の基本情報表示フォームを設定した画面の取得
			SaibanEditMinjiViewForm.SaibanOptionViewForm optionViewForm = service.createSaibanOptionViewForm(saibanSeq);
			mv = getMyModelAndView(optionViewForm, SAIBAN_OPTION_VIEW_FRAGMENT_PATH, SAIBAN_OPTION_VIEW_FORM_NAME);

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
	 * 裁判タブ 表示フォーム部分を取得する
	 * 
	 * @param saibanSeq
	 * @param targetSaibanSeq
	 * @param bunriTorisageButtonFlg
	 * @return
	 */
	@RequestMapping(value = "/getSaibanTabViewForm", method = RequestMethod.GET)
	public ModelAndView getSaibanTabView(@PathVariable Long saibanSeq) {

		return getSaibanTabViewBySaibanSeq(saibanSeq, saibanSeq, false);

	}

	/**
	 * 裁判タブ 表示フォーム部分を取得する
	 * 
	 * @param saibanSeq
	 * @param targetSaibanSeq
	 * @param bunriTorisageButtonFlg
	 * @return
	 */
	@RequestMapping(value = "/getSaibanTabViewBySaibanSeqForm", method = RequestMethod.GET)
	public ModelAndView getSaibanTabViewBySaibanSeq(@PathVariable Long saibanSeq,
			@RequestParam(name = "targetSaibanSeq") Long targetSaibanSeq,
			@RequestParam(name = "bunriTorisageButtonFlg") boolean bunriTorisageButtonFlg) {

		ModelAndView mv = null;

		try {

			// 裁判の基本情報表示フォームを設定した画面の取得
			SaibanEditMinjiViewForm.SaibanTabViewForm tabViewForm = service.createSaibanTabViewForm(saibanSeq, targetSaibanSeq, bunriTorisageButtonFlg);
			mv = getMyModelAndView(tabViewForm, SAIBAN_TAB_VIEW_FRAGMENT_PATH, SAIBAN_TAB_VIEW_FORM_NAME);

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
	 * @param isDisplayOnly
	 * @return
	 */
	@RequestMapping(value = "/getSaibanKijitsuViewForm", method = RequestMethod.GET)
	public ModelAndView getSaibanKijitsuView(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "isAllTimeKijitsuList", required = false) boolean isAllTimeKijitsuList,
			@RequestParam(name = "isDisplayOnly") boolean isDisplayOnly,
			@RequestParam(name = "isChildSaiban") boolean isChildSaiban) {

		ModelAndView mv = null;

		try {

			// 裁判の基本情報表示フォームを設定した画面の取得
			SaibanEditMinjiViewForm.SaibanKijitsuViewForm kijitsuViewForm = service.createSaibanKijitsuViewForm(saibanSeq, isAllTimeKijitsuList, isDisplayOnly, isChildSaiban);
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
	 * 裁判民事画面：期日予定情報の詳細モーダルデータを取得
	 * 
	 * ThymeleafのモデルデータのJson化は予定データの日付フォーマット処理が考慮されていないため、
	 * 名簿画面のスケジュール一覧レンダリング直後にAjaxにより取得する
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @param isAllTimeKijitsuList
	 * @param isChildSaiban
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getSaibanKijitsuDetails", method = RequestMethod.GET)
	public Map<Long, ScheduleDetail> getSaibanKijitsuDetails(
			@PathVariable Long saibanSeq,
			@PathVariable Long ankenId,
			@RequestParam(name = "isAllTimeKijitsuList", required = false) boolean isAllTimeKijitsuList,
			@RequestParam(name = "isChildSaiban") boolean isChildSaiban) {

		try {
			Map<Long, ScheduleDetail> scheduleDetails = service.getSaibanKijitsuDetails(saibanSeq, isAllTimeKijitsuList, isChildSaiban);
			super.setAjaxProcResultSuccess();

			return scheduleDetails;
		} catch (Exception ex) {
			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

	}

	/**
	 * リクエストパラメータで指定した裁判SEQの裁判期日 表示フォーム部分を取得する
	 * 
	 * @param ankenId
	 * @param isAllTimeKijitsuList
	 * @param saibanSeq
	 * @param isDisplayOnly
	 * @param isChildSaiban
	 * @return
	 */
	@RequestMapping(value = "/getSaibanKijitsuViewBySaibanSeqForm", method = RequestMethod.GET)
	public ModelAndView getSaibanKijitsuViewBySaibanSeq(@PathVariable Long ankenId,
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "isAllTimeKijitsuList", required = false) boolean isAllTimeKijitsuList,
			@RequestParam(name = "isDisplayOnly") boolean isDisplayOnly,
			@RequestParam(name = "isChildSaiban") boolean isChildSaiban) {

		return getSaibanKijitsuView(saibanSeq, ankenId, isAllTimeKijitsuList, isDisplayOnly, isChildSaiban);

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
			SaibanEditMinjiInputForm.SaibanKijitsuResultInputForm saibanKijitsuResultInputForm = service.createSaibanKijitsuResultInputForm(saibanLimitSeq);
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
	 * @param isDisplayOnly
	 * @return
	 */
	@RequestMapping(value = "/getSaibanCustomerViewForm", method = RequestMethod.GET)
	public ModelAndView getSaibanCustomerView(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "isDisplayOnly") boolean isDisplayOnly) {

		ModelAndView mv = null;

		try {

			// 裁判の顧客情報表示フォームを設定した画面の取得
			SaibanEditMinjiViewForm.SaibanCustomerViewForm customerViewForm = service.createSaibanCustomerViewForm(saibanSeq, isDisplayOnly);
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
	 * リクエストパラメータで指定した裁判SEQの顧客情報 表示フォーム部分を取得する
	 * 
	 * @param ankenId
	 * @param saibanSeq
	 * @param isDisplayOnly
	 * @return
	 */
	@RequestMapping(value = "/getSaibanCustomerViewBySaibanSeqForm", method = RequestMethod.GET)
	public ModelAndView getSaibanCustomerViewBySaibanSeq(@PathVariable Long ankenId,
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "isDisplayOnly") boolean isDisplayOnly) {

		return getSaibanCustomerView(saibanSeq, ankenId, isDisplayOnly);

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
			SaibanEditMinjiInputForm.SaibanCustomerAddInputForm customerAddInputForm = service.createSaibanCustomerAddInputForm(saibanSeq, ankenId);
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
	 * その他当事者 表示フォーム部分を取得する
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getSaibanOtherViewForm", method = RequestMethod.GET)
	public ModelAndView getSaibanOtherView(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "isDisplayOnly") boolean isDisplayOnly) {

		ModelAndView mv = null;

		try {

			// 裁判の基本情報表示フォームを設定した画面の取得
			SaibanEditMinjiViewForm.SaibanOtherViewForm otherViewForm = service.createSaibanOtherViewForm(saibanSeq, isDisplayOnly);
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
	 * リクエストパラメータで指定した裁判SEQのその他当事者 表示フォーム部分を取得する
	 * 
	 * @param ankenId
	 * @param saibanSeq
	 * @return
	 */
	@RequestMapping(value = "/getSaibanOtherViewBySaibanSeqForm", method = RequestMethod.GET)
	public ModelAndView getSaibanOtherViewBySaibanSeqForm(@PathVariable Long ankenId,
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "isDisplayOnly") boolean isDisplayOnly) {

		return getSaibanOtherView(saibanSeq, ankenId, isDisplayOnly);

	}

	/**
	 * 相手方 表示フォーム部分を取得する
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param isDisplayOnly
	 * @return
	 */
	@RequestMapping(value = "/getSaibanAitegataViewForm", method = RequestMethod.GET)
	public ModelAndView getSaibanAitegataView(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "isDisplayOnly") boolean isDisplayOnly) {

		ModelAndView mv = null;

		try {

			// 裁判の基本情報表示フォームを設定した画面の取得
			SaibanEditMinjiViewForm.SaibanAitegataViewForm aitegataViewForm = service.createSaibanAitegataViewForm(saibanSeq, isDisplayOnly);
			mv = getMyModelAndView(aitegataViewForm, SAIBAN_AITEGATA_VIEW_FRAGMENT_PATH, SAIBAN_AITEGATA_VIEW_FORM_NAME);

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
	 * リクエストパラメータで指定した裁判SEQの相手方 表示フォーム部分を取得する
	 * 
	 * @param ankenId
	 * @param saibanSeq
	 * @param isDisplay
	 * @return
	 */
	@RequestMapping(value = "/getSaibanAitegataViewBySaibanSeqForm", method = RequestMethod.GET)
	public ModelAndView getSaibanAitegataViewBySaibanSeq(@PathVariable Long ankenId,
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "isDisplayOnly") boolean isDisplayOnly) {

		return getSaibanAitegataView(saibanSeq, ankenId, isDisplayOnly);

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
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) SaibanEditMinjiInputForm.SaibanBasicInputForm basicInputForm,
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
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) SaibanEditMinjiInputForm.SaibanKijitsuResultInputForm saibanKijitsuResultInputForm,
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
	 * 筆頭を変更（顧客情報部分と、その他当事者部分で共通で使用）
	 * 
	 * <pre>
	 * ※どちらの箇所から呼ばれたとしても、更新処理後、顧客情報部分とその他当事者部分の両方を再レンダリングする必要があるため、
	 *   このメソッドでは、顧客情報部分のHTMLを返却する。
	 *   （その他当事者部分はこのメソッドの戻り値を受けたあと、フロント側で別途リクエストを送信し、再レンダリングをすること）
	 * </pre>
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param customerId toHittouがtrueの場合に、「筆頭」とする顧客のID
	 * @param kanyoshaSeq toHittouがtrueの場合に、「筆頭」とする関与者のID
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
		mv = this.getSaibanCustomerView(saibanSeq, ankenId, false);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "筆頭"));
		return mv;
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
		mv = this.getSaibanCustomerView(saibanSeq, ankenId, false);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "当事者表記"));
		return mv;
	}

	/**
	 * その他当事者 当事者表記の変更
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

		// 保存成功後は、その他当事者情報表示部分を返却する
		mv = this.getSaibanOtherView(saibanSeq, ankenId, false);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "当事者表記"));
		return mv;
	}

	/**
	 * 筆頭を変更
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @param toHittou
	 * @return
	 */
	@RequestMapping(value = "/saveAitegataHittou", method = RequestMethod.POST)
	public ModelAndView saveAitegataHittou(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq,
			@RequestParam(name = "toHittou", required = false) boolean toHittou) {

		ModelAndView mv = null;

		try {

			// 保存処理
			service.saveAitegataHittou(saibanSeq, kanyoshaSeq, toHittou);

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

		// 保存成功後は、表示画面を返却する
		boolean isDisplayOnly = false;
		mv = this.getSaibanAitegataView(saibanSeq, ankenId, isDisplayOnly);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "筆頭"));
		return mv;
	}

	/**
	 * 相手方 当事者表記の変更
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @param tojishaHyoki
	 * @return
	 */
	@RequestMapping(value = "/saveAitegataTojishaHyoki", method = RequestMethod.POST)
	public ModelAndView saveAitegataTojishaHyoki(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
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

		// 保存成功後は、その他当事者情報表示部分を返却する
		boolean isDisplayOnly = false;
		mv = this.getSaibanAitegataView(saibanSeq, ankenId, isDisplayOnly);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "当事者表記"));
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
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) SaibanEditMinjiInputForm.SaibanCustomerAddInputForm customerAddInputForm,
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
		mv = this.getSaibanCustomerView(saibanSeq, ankenId, false);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "当事者"));
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
		mv = this.getSaibanCustomerView(saibanSeq, ankenId, false);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00037, "当事者"));
		return mv;
	}

	/**
	 * その他当事者削除
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
		mv = this.getSaibanOtherView(saibanSeq, ankenId, false);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00037, "その他当事者"));
		return mv;
	}

	/**
	 * その他当事者の代理人を外す
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
		mv = this.getSaibanOtherView(saibanSeq, ankenId, false);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00037, "代理人"));
		return mv;
	}

	/**
	 * 相手方削除
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/removeSaibanAitegata", method = RequestMethod.POST)
	public ModelAndView removeSaibanAitegata(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
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

		// 削除成功後は、相手方情報表示部分を返却する
		boolean isDisplayOnly = false;
		mv = this.getSaibanAitegataView(saibanSeq, ankenId, isDisplayOnly);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00037, "相手方"));
		return mv;
	}

	/**
	 * 相手方の代理人を外す
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/removeSaibanRelatedAitegata", method = RequestMethod.POST)
	public ModelAndView removeSaibanRelatedAitegata(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
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
		boolean isDisplayOnly = false;
		mv = this.getSaibanAitegataView(saibanSeq, ankenId, isDisplayOnly);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00037, "代理人"));
		return mv;
	}

	/**
	 * 裁判削除確認
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
	 * 口頭弁論期日請書を出力する
	 *
	 * @param response
	 * @param ankenId
	 * @param saibanSeq
	 * @param scheduleSeq
	 */
	@RequestMapping(value = "/outputKotoBenron", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputKotoBenron(HttpServletResponse response, @PathVariable Long ankenId,
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "scheduleSeq") Long scheduleSeq) {

		try {

			// 出力処理
			commonSaibanService.outputKotoBenron(response, saibanSeq, scheduleSeq);

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
	 * 期日一覧 出廷の変更
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param saibanLimitSeq
	 * @param shutteiType
	 * @return
	 */
	@RequestMapping(value = "/saveKijitsuShutteiType", method = RequestMethod.POST)
	public ModelAndView saveKijitsuShutteiType(@PathVariable Long saibanSeq, @PathVariable Long ankenId,
			@RequestParam(name = "saibanLimitSeq") Long saibanLimitSeq,
			@RequestParam(name = "shutteiType") ShutteiType shutteiType) {

		try {

			// 保存処理
			service.saveShutteiType(saibanLimitSeq, shutteiType);

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

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "出廷"));
		return null;
	}

	/**
	 * 併合紐づけ
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @param childSaibanSeq
	 * @return
	 */
	@RequestMapping(value = "/heigo", method = RequestMethod.POST)
	public ModelAndView heigo(
			@PathVariable Long saibanSeq,
			@PathVariable Long ankenId,
			@RequestParam(name = "childSaibanSeq") Long childSaibanSeq) {

		ModelAndView mv = null;

		try {

			service.heigoSaibanMinji(saibanSeq, childSaibanSeq);

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

		// 成功後は、表示画面を返却する
		mv = this.getSaibanBasicView(saibanSeq, ankenId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00038, "被併合事件"));
		return mv;
	}

	/**
	 * 反訴紐づけ
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @param childSaibanSeq
	 * @return
	 */
	@RequestMapping(value = "/hanso", method = RequestMethod.POST)
	public ModelAndView hanso(
			@PathVariable Long saibanSeq,
			@PathVariable Long ankenId,
			@RequestParam(name = "childSaibanSeq") Long childSaibanSeq) {

		ModelAndView mv = null;

		try {

			service.hansoSaibanMinji(saibanSeq, childSaibanSeq);

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

		// 成功後は、表示画面を返却する
		mv = this.getSaibanBasicView(saibanSeq, ankenId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00038, "反訴事件"));
		return mv;
	}

	/**
	 *
	 * 本訴・反訴の画面作成
	 *
	 * @param ankenId
	 * @param branchNumber
	 * @param redirectViewBuilder
	 * @return
	 */
	@RequestMapping(value = "/hanso/create", method = RequestMethod.GET)
	public ModelAndView createHansoSaiban(
			@PathVariable Long ankenId,
			@PathVariable Long branchNumber) {

		return null;
	}

	/**
	 * 分離/取下
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @param childSaibanSeq
	 * @return
	 */
	@RequestMapping(value = "/bunriTorisage", method = RequestMethod.POST)
	public ModelAndView bunriTorisage(
			@PathVariable Long saibanSeq,
			@PathVariable Long ankenId,
			@RequestParam(name = "childSaibanSeq") Long childSaibanSeq) {

		ModelAndView mv = null;

		try {

			service.bunriTorisageSaibanMinji(saibanSeq, childSaibanSeq);

		} catch (Exception ex) {
			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		// 成功後は、表示画面を返却する
		mv = this.getSaibanBasicView(saibanSeq, ankenId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00128));
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
	 * 関与者削除処理前チェック
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteKanyoshaBeforeCheck", method = RequestMethod.POST)
	public Map<String, Object> deleteKanyoshaBeforeCheck(@RequestParam Long kanyoshaSeq) {

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
	 * その他当事者エリア関与者の削除処理
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/deleteOtherKanyosha", method = RequestMethod.POST)
	public ModelAndView deleteOtherKanyosha(@PathVariable Long saibanSeq,
			@PathVariable Long ankenId, @RequestParam Long kanyoshaSeq) {

		ModelAndView mv = null;

		Map<String, String> errorMessage = new HashMap<>();
		// DB整合姓チェック
		if (!this.accessDBValidForDelete(kanyoshaSeq, errorMessage)) {
			super.setAjaxProcResultFailure(errorMessage.get("message"));
			return null;
		}

		try {
			// 関与者の登録処理
			service.deleteKanyoshaInfo(kanyoshaSeq);

		} catch (AppException e) {
			// エラー内容を返却
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 削除成功後は、その他当事者情報表示部分を返却する
		mv = this.getSaibanOtherView(saibanSeq, ankenId, false);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}
		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00037, "案件"));
		return mv;
	}

	/**
	 * 相手方エリア関与者の削除処理
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/deleteAitegataKanyosha", method = RequestMethod.POST)
	public ModelAndView deleteAitegataKanyosha(@PathVariable Long saibanSeq,
			@PathVariable Long ankenId, @RequestParam Long kanyoshaSeq) {

		ModelAndView mv = null;

		Map<String, String> errorMessage = new HashMap<>();
		// DB整合姓チェック
		if (!this.accessDBValidForDelete(kanyoshaSeq, errorMessage)) {
			super.setAjaxProcResultFailure(errorMessage.get("message"));
			return null;
		}

		try {
			// 関与者の登録処理
			service.deleteKanyoshaInfo(kanyoshaSeq);

		} catch (AppException e) {
			// エラー内容を返却
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 削除成功後は、相手方情報表示部分を返却する
		mv = this.getSaibanAitegataView(saibanSeq, ankenId, false);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}
		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00037, "案件"));
		return mv;
	}

	// **********************************************************************************
	// privateメソッド
	// **********************************************************************************

	/**
	 * 削除時のDB整合バリデート
	 * 
	 * @param kanyoshaSeq
	 * @param errorMap
	 * @return
	 */
	private boolean accessDBValidForDelete(Long kanyoshaSeq, Map<String, String> errorMap) {

		// 削除が不可能な場合、ここで返却
		if (!commonKanyoshaValidator.canDeleteKanyosha(kanyoshaSeq)) {
			errorMap.put("message", getMessage(MessageEnum.MSG_E00086, "会計情報が登録されている。もしくは預かり元として登録されている"));
			return false;
		}

		return true;
	}

}
