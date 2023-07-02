package jp.loioz.app.user.kaikeiManagement.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.groups.Default;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.service.CommonKaikeiService;
import jp.loioz.app.common.validation.accessDB.CommonAnkenValidator;
import jp.loioz.app.common.validation.accessDB.CommonCustomerValidator;
import jp.loioz.app.common.validation.accessDB.CommonKaikeiValidator;
import jp.loioz.app.user.kaikeiManagement.form.AnkenMeisaiEditForm;
import jp.loioz.app.user.kaikeiManagement.form.CustomerKozaInfoForm;
import jp.loioz.app.user.kaikeiManagement.form.HoshuMeisaiEditForm;
import jp.loioz.app.user.kaikeiManagement.form.KozaInfoForm;
import jp.loioz.app.user.kaikeiManagement.form.NyushukkinEditForm;
import jp.loioz.app.user.kaikeiManagement.form.NyushukkinYoteiEditForm;
import jp.loioz.app.user.kaikeiManagement.form.ShiharaiPlanEditForm;
import jp.loioz.app.user.kaikeiManagement.form.ShiharaiPlanEditForm.ShiharaiPlanYotei;
import jp.loioz.app.user.kaikeiManagement.form.ajax.AutoCalcKingakuAjaxRequest;
import jp.loioz.app.user.kaikeiManagement.form.ajax.AutoCalcTankaAjaxRequest;
import jp.loioz.app.user.kaikeiManagement.form.ajax.AutoCalcTimeChargeTimeAjaxRequest;
import jp.loioz.app.user.kaikeiManagement.form.ajax.ChangeTaxRateAjaxRequest;
import jp.loioz.app.user.kaikeiManagement.service.KaikeiEditService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.GensenChoshu;
import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.constant.CommonConstant.LawyerHoshu;
import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.constant.CommonConstant.SeikyuType;
import jp.loioz.common.constant.CommonConstant.TargetType;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.CommonConstant.TimeChargeTimeShitei;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.log.Logger;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.utility.ValidateUtils;
import jp.loioz.common.utility.data.LoiozDecimalFormat;
import jp.loioz.common.validation.groups.Nyukin;
import jp.loioz.common.validation.groups.Shukkin;
import jp.loioz.dto.AnkenMeisaiDto;
import jp.loioz.dto.CustomerKanyoshaPulldownDto;
import jp.loioz.dto.GinkoKozaDto;
import jp.loioz.dto.KaikeiKirokuDto;
import jp.loioz.dto.KaikeiManagementRelationDto;
import jp.loioz.dto.NyushukkinYoteiEditDto;
import jp.loioz.dto.ShiharaiPlanDto;

/**
 * 会計管理画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@LoiozAdminControlCheck(items = {LoiozAdminControl.OLD_KAIKEI_OPEN})
@Controller
@RequestMapping(UrlConstant.KAIKEI_MANAGEMENT_URL)
public class KaikeiEditController extends DefaultController {

	/** コントローラーに対する入出金登録モーダルviewパス */
	private static final String MY_VIEW_PATH = "user/kaikeiManagement/kaikeiEditModal";

	/** コントローラーに対する報酬明細モーダルviewパス */
	private static final String AJAX_MODAL_PATH_HOSHU_MEISAI = "user/kaikeiManagement/hoshuMeisaiModal::hoshuMeisai";

	/** コントローラーに対する報酬登録モーダルviewパス */
	private static final String AJAX_MODAL_PATH_HOSHU = "user/kaikeiManagement/hoshuEditModal::hoshuEdit";

	/** コントローラーに対する会計記録入出金モーダルviewパス */
	private static final String AJAX_MODAL_PATH_NYUSHUKKIN = "user/kaikeiManagement/nyushukkinEditModal::nyushukkinEdit";

	/** コントローラーに対する支払い計画モーダルviewパス */
	private static final String AJAX_MODAL_PATH_SHIHARAIPLAN = "user/kaikeiManagement/shiharaiPlanEditModal::shiharaiPlanEdit";

	/** コントローラーに対する支払い計画モーダルviewパス */
	private static final String AJAX_MODAL_PATH_SHIHARAIPLAN_ADD = "user/kaikeiManagement/shiharaiPlanEditModal::addPlanContents";

	/** コントローラーに対する入出金予定モーダルviewパス */
	private static final String AJAX_MODAL_PATH_NYUSHUKKINYOTEI = "user/kaikeiManagement/nyushukkinYoteiEditModal::nyuShukkinYoteiEdit";

	/** コントローラーに対する入出金予定モーダル内 関与者プルダウンパス */
	private static final String NYUSHUKKINYOTEI_KANYOSHA_LIST_PATH = "user/kaikeiManagement/nyushukkinYoteiEditModal::kanyoshaList";

	/** コントローラーに対する入出金予定モーダル内 関与者プルダウンパス */
	private static final String NYUSHUKKINYOTEI_TENANT_ACCOUNT_KOZA_LIST_PATH = "user/kaikeiManagement/nyushukkinYoteiEditModal::tenantAccountGinkoKozaList";

	/** コントローラーに対する案件顧客モーダルviewパス */
	private static final String AJAX_MODAL_PATH_ANKEN_CUSTOMER = MY_VIEW_PATH + "::ankenModal";

	/** 報酬モーダルviewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME_HOSHU_MESAI = "hoshuMeisaiEditForm";

	/** 報酬モーダルviewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME_HOSHU = "ankenMeisaiEditForm";

	/** 会計記録入出金モーダルviewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME_NYUSHUKKIN = "nyushukkinEditForm";

	/** 入出金予定モーダルviewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME_NYUSHUKKINYOTEI = "nyuShukkinYoteiEditForm";

	/** 支払い計画モーダルviewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME_SHIHARAIPLAN = "shiharaiPlanEditForm";

	/** 案件顧客モーダルviewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME_ANKEN_CUSTOMER = "ankenMeisaiEditForm";

	/** 会計管理編集画面用のサービスクラス */
	@Autowired
	KaikeiEditService service;

	/** 会計管理の共通サービス */
	@Autowired
	CommonKaikeiService commonKaikeiService;

	/** 共通顧客バリデーター */
	@Autowired
	CommonCustomerValidator commonCustomerValidator;

	/** 共通案件バリデーター */
	@Autowired
	CommonAnkenValidator commonAnkenValidator;

	/** 共通会計バリデーター */
	@Autowired
	CommonKaikeiValidator commonKaikeiValidator;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================
	// ****************************************************************
	// 案件明細
	// ****************************************************************
	/**
	 * 報酬登録、編集モーダル初期表示
	 *
	 * @param kaikeiKirokuSeq 会計記録SEQ
	 * @param transitionFlg 遷移元判定フラグ
	 * @param transitionAnkenId 遷移元案件ID
	 * @param transitionCustomerId 遷移元顧客ID
	 * @return 画面情報
	 */
	@ResponseBody
	@RequestMapping(value = "/ankenMeisaiEdit", method = RequestMethod.POST)
	public ModelAndView ankenMeisaiEdit(
			@RequestParam(name = "kaikeiKirokuSeq", required = false) Long kaikeiKirokuSeq,
			@RequestParam(name = "transitionFlg", required = false) String transitionFlg,
			@RequestParam(name = "transitionAnkenId", required = false) Long transitionAnkenId,
			@RequestParam(name = "transitionCustomerId", required = false) Long transitionCustomerId, MessageHolder msgHolder) {

		AnkenMeisaiEditForm ankenMeisaiEditForm = new AnkenMeisaiEditForm();

		try {
			// 会計記録SEQを元に、初期データを設定します。
			ankenMeisaiEditForm = service.setInitDataForAnkenMeisai(kaikeiKirokuSeq, transitionFlg, transitionAnkenId, transitionCustomerId);
		} catch (AppException ex) {
			// エラーメッセージを表示します。
			msgHolder.setErrorMsg(super.getMessage(ex.getMessageKey()));
			return getMyModelAndViewByModalError(msgHolder);
		}

		return getMyModelAndView(ankenMeisaiEditForm, AJAX_MODAL_PATH_HOSHU, VIEW_FORM_NAME_HOSHU);
	}

	/**
	 * 過去のタイムチャージの単価を取得します。<br>
	 *
	 * <pre>
	 * 弁護士項目「タイムチャージ」を選択した時用です。
	 * タイムチャージの登録履歴がある場合、単価を引き継ぎます。
	 * </pre>
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return 過去のタイムチャージ単価
	 */
	@ResponseBody
	@RequestMapping(value = "/getRecentTanka", method = RequestMethod.POST)
	public Map<String, Object> getRecentTanka(
			@RequestParam(name = "ankenId", required = false) Long ankenId,
			@RequestParam(name = "customerId", required = false) Long customerId) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 過去のタイムチャージ単価を取得します。
			String recentTanka = service.getRecentTanka(ankenId, customerId);
			response.put("succeeded", true);
			response.put("recentTanka", recentTanka);
		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00059));
			return response;
		}
		return response;
	}

	/**
	 * 案件ID、顧客IDに紐づく受任日から、消費税率を取得します。<br>
	 *
	 * <pre>
	 * 報酬登録モーダルで、案件を切替えた時用です。
	 * </pre>
	 *
	 * @param ankenMeisaiEditForm 案件明細情報の編集用フォーム
	 * @return response 返却用Map
	 */
	@ResponseBody
	@RequestMapping(value = "/changeTaxRate", method = RequestMethod.POST)
	public Map<String, Object> changeTaxRate(@Validated ChangeTaxRateAjaxRequest requestForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// バリデーションエラー処理
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("hasValid", true);
			return response;
		}

		try {
			LocalDate targetDate = LocalDate.now();
			String hoshuKomokuId = requestForm.getHoshuKomokuId();
			String timeChargeTimeShitei = requestForm.getTimeChargeTimeShitei();

			if (LawyerHoshu.TIME_CHARGE.equalsByCode(hoshuKomokuId) && TimeChargeTimeShitei.START_END_TIME.equalsByCode(timeChargeTimeShitei)) {
				// タイムチャージで、開始・終了日時を指定する場合
				targetDate = DateUtils.parseToLocalDate(requestForm.getTimeChargeStartDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} else {
				// それ以外の場合
				targetDate = DateUtils.parseToLocalDate(requestForm.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			}

			// 消費税率を求めます。
			String taxRateCd = service.getTaxRate(requestForm.getAnkenId(), requestForm.getCustomerId(), targetDate);

			response.put("succeeded", true);
			response.put("hasValid", false);
			response.put("taxRateStr", taxRateCd);
			return response;

		} catch (Exception ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			return response;
		}

	}

	/**
	 * 源泉徴収の計算式、源泉徴収額、報酬額、消費税、報酬計を計算。<br>
	 *
	 * <pre>
	 * 報酬登録モーダルで、金額を入力した際の自動計算用です。
	 * </pre>
	 *
	 * @param ankenMeisaiEditForm 案件明細情報の編集用フォーム
	 * @param result バリデーション結果
	 * @return response 返却用Map
	 */
	@ResponseBody
	@RequestMapping(value = "/autoCalcKingaku", method = RequestMethod.POST)
	public Map<String, Object> autoCalcKingaku(@Validated AutoCalcKingakuAjaxRequest requestForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// バリデーションエラーがある場合
		if (result.hasErrors()) {
			response.put("hasKingakuList", false);
			response.put("hasValid", true);
			response.put("succeed", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		try {
			// 金額を String -> BigDecimal に変換します。
			BigDecimal decimalKingaku = LoiozNumberUtils.parseAsBigDecimal(requestForm.getKingaku());

			// parseできなかった場合
			if (decimalKingaku == null) {
				throw new AppException(MessageEnum.MSG_E00030, null);
			}

			// ------------------------------------------------------------
			// 計算式、源泉徴収額、消費税、報酬計を計算します。
			// ------------------------------------------------------------
			Map<String, String> resultMap = service.autoCalc(decimalKingaku, requestForm.getTaxFlg(), requestForm.getTaxRate(), requestForm.getGensenchoshuFlg());

			response.put("hasValid", false);
			response.put("succeed", true);
			response.put("calcFormulaLabel1", resultMap.get("calcFormula1"));
			response.put("calcFormulaLabel2", resultMap.get("calcFormula2"));
			response.put("hoshuGakuText", resultMap.get("hoshuGaku"));
			response.put("gensenText", resultMap.get("gensenGaku"));
			response.put("taxText", resultMap.get("taxGaku"));
			response.put("hoshuText", resultMap.get("totalKingaku"));
			return response;

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得します。
			response.put("hasValid", false);
			response.put("succeed", false);
			response.put("message", getMessage(ex.getMessageKey()));
			return response;
		}
	}

	/**
	 * 開始時間と終了時間の差分(経過時間)、計算式・報酬計を計算します。
	 *
	 * <pre>
	 * 弁護士報酬項目が「タイムチャージ」の時用です。
	 * </pre>
	 *
	 * @param ankenMeisaiEditForm 案件明細情報の編集用フォーム
	 * @param result バリデーション結果
	 * @return response 返却用Map
	 */
	@ResponseBody
	@RequestMapping(value = "/autoCalcTanka", method = RequestMethod.POST)
	public Map<String, Object> autoCalcTanka(@Validated AutoCalcTankaAjaxRequest requestForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// ********************************
		// 相関バリデート
		// ********************************
		this.autoCalcTankaRequestValidated(requestForm, result);

		// validationエラーがある時
		if (result.hasErrors()) {
			response.put("hasValid", true);
			response.put("succeed", false);

			// ここでは金額の算出を行うだけでメッセージなども表示しない
			// ※ 画面で表示しているオブジェクトとRequestを受け取ったオブジェクトが異なるので、Field名が異なる
			return response;
		}

		try {
			// 源泉徴収、消費税、報酬計を計算します。
			Map<String, String> resultMap = service.autoCalcTanka(requestForm);

			response.put("hasValid", false);
			response.put("succeed", true);
			response.put("calcFormulaLabel1", resultMap.get("calcFormula1"));
			response.put("calcFormulaLabel2", resultMap.get("calcFormula2"));
			response.put("hoshuGakuText", resultMap.get("hoshuGaku"));
			response.put("gensenText", resultMap.get("gensenGaku"));
			response.put("timeText", requestForm.getTimeChargeTime());
			response.put("taxText", resultMap.get("taxGaku"));
			response.put("hoshuText", resultMap.get("totalKingaku"));
			return response;

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得します。
			response.put("hasValid", false);
			response.put("succeed", false);
			response.put("message", getMessage(ex.getMessageKey()));
			return response;
		}

	}

	/**
	 * 開始時間と終了時間の差分(経過時間)を計算します。
	 *
	 * <pre>
	 * 弁護士報酬項目が「タイムチャージ」の時用です。
	 * </pre>
	 *
	 * @param requestForm
	 * @param result バリデーション結果
	 * @return response 返却用Map
	 */
	@ResponseBody
	@RequestMapping(value = "/autoCalcTimeChargeTime", method = RequestMethod.GET)
	public Map<String, Object> autoCalcTimeChargeTime(@Validated AutoCalcTimeChargeTimeAjaxRequest requestForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// ********************************
		// 相関バリデート（バリデーションの中で、TimeChargeTimeの算出とformへの設定も行う）
		// ********************************
		this.autoCalcTimeChargeTimeRequestValidated(requestForm, result);

		// validationエラーがある時
		if (result.hasErrors()) {
			response.put("hasValid", true);
			response.put("succeed", false);

			// ここでは時間の算出を行うだけでメッセージなども表示しない
			// ※ 画面で表示しているオブジェクトとRequestを受け取ったオブジェクトが異なるので、Field名が異なる
			return response;
		}

		response.put("hasValid", false);
		response.put("succeed", true);

		response.put("timeText", requestForm.getTimeChargeTime());

		return response;
	}

	/**
	 * 案件明細(弁護士報酬)の新規登録を行います。
	 *
	 * @param ankenMeisaiEditForm 案件明細情報の編集用フォーム
	 * @param result バリデーション結果
	 * @param msgHolder メッセージの保持クラス
	 * @return 画面情報
	 * @throws AppException
	 */
	@ResponseBody
	@RequestMapping(value = "/saveAnkenMeisai", method = RequestMethod.POST)
	public Map<String, Object> saveAnkenMeisai(@Validated AnkenMeisaiEditForm ankenMeisaiEditForm, BindingResult result, MessageHolder msgHolder) throws AppException {

		Map<String, Object> response = new HashMap<>();

		// 相関バリデート
		this.ankenMeisaiDtoValidated(ankenMeisaiEditForm.getTransitionType(), ankenMeisaiEditForm.getAnkenMeisaiDto(), result);

		// ----------------------------------------------------
		// バリデーションエラーがある場合
		// ----------------------------------------------------
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// ----------------------------------------------------
		// 精算完了済の場合は登録させない
		// ----------------------------------------------------
		Boolean seisanCompFlg = false;
		if (TransitionType.CUSTOMER.equals(ankenMeisaiEditForm.getTransitionType())) {
			seisanCompFlg = service.seisanCompCheck(ankenMeisaiEditForm.getAnkenMeisaiDto().getAnkenId(), ankenMeisaiEditForm.getTransitionCustomerId());
		} else {
			seisanCompFlg = service.seisanCompCheck(ankenMeisaiEditForm.getTransitionAnkenId(), ankenMeisaiEditForm.getAnkenMeisaiDto().getCustomerId());
		}

		if (seisanCompFlg) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00063));
			return response;
		}
		// ----------------------------------------------------
		// 登録処理
		// ----------------------------------------------------
		try {
			service.saveAnkenMeisai(ankenMeisaiEditForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "報酬"));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 更新処理(弁護士報酬)を行います。
	 *
	 * @param ankenMeisaiEditForm 報酬情報の編集用フォーム
	 * @param result バリデーション結果
	 * @param msgHolder メッセージの保持クラス
	 * @return 画面情報
	 * @throws AppException
	 */
	@ResponseBody
	@RequestMapping(value = "/updateAnkenMeisai", method = RequestMethod.POST)
	public Map<String, Object> updateAnkenMeisai(@Validated AnkenMeisaiEditForm ankenMeisaiEditForm, BindingResult result) throws AppException {

		Map<String, Object> response = new HashMap<>();

		// 相関バリデート
		this.ankenMeisaiDtoValidated(ankenMeisaiEditForm.getTransitionType(), ankenMeisaiEditForm.getAnkenMeisaiDto(), result);

		// ----------------------------------------------------
		// バリデーションエラーがある場合
		// ----------------------------------------------------
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}
		// ----------------------------------------------------
		// 精算完了済の場合は登録させない
		// ----------------------------------------------------
		Boolean seisanCompFlg = false;
		if (TransitionType.CUSTOMER.equals(ankenMeisaiEditForm.getTransitionType())) {
			seisanCompFlg = service.seisanCompCheck(ankenMeisaiEditForm.getAnkenMeisaiDto().getAnkenId(), ankenMeisaiEditForm.getTransitionCustomerId());
		} else {
			seisanCompFlg = service.seisanCompCheck(ankenMeisaiEditForm.getTransitionAnkenId(), ankenMeisaiEditForm.getAnkenMeisaiDto().getCustomerId());
		}

		if (seisanCompFlg) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00063));
			return response;
		}
		// ----------------------------------------------------
		// 更新処理
		// ----------------------------------------------------
		try {
			service.updateAnkenMeisai(ankenMeisaiEditForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "報酬"));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 削除処理を行います。
	 *
	 * @param ankenMeisaiEditForm 案件明細情報の編集用フォーム
	 * @param msgHolder メッセージの保持クラス
	 * @return 画面情報
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteAnkenMeisai", method = RequestMethod.POST)
	public Map<String, Object> deleteAnkenMeisai(AnkenMeisaiEditForm ankenMeisaiEditForm) {

		Map<String, Object> response = new HashMap<>();
		// ----------------------------------------------------
		// 削除処理
		// ----------------------------------------------------
		try {
			service.deleteAnkenMeisai(ankenMeisaiEditForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00001));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	// ****************************************************************
	// 報酬明細
	// ****************************************************************
	/**
	 * 初期表示
	 *
	 * @param transitionFlg 遷移元判定フラグ
	 * @param transitionAnkenId 遷移元案件ID
	 * @param transitionCustomerId 遷移元顧客ID
	 * @param hiddenFlg 非表示フラグ
	 * @return 画面表示情報
	 */
	@ResponseBody
	@RequestMapping(value = "/hoshuMeisai", method = RequestMethod.POST)
	public ModelAndView hoshuMeisai(
			@RequestParam(name = "transitionType") TransitionType transitionType,
			@RequestParam(name = "transitionAnkenId", required = false) Long transitionAnkenId,
			@RequestParam(name = "transitionCustomerId", required = false) Long transitionCustomerId,
			@RequestParam(name = "selectedAnkenId", required = false) Long ankenId,
			@RequestParam(name = "selectedCustomerId", required = false) Long customerId,
			@RequestParam(name = "seisanCompDispFlg") boolean seisanCompDispFlg) {

		// 案件ID、顧客ID、非表示フラグをキーにして、初期データを設定します。
		HoshuMeisaiEditForm hoshuMeisaiEditForm = service.createHoshuMeisaiForm(transitionType, transitionAnkenId, transitionCustomerId, seisanCompDispFlg, ankenId, customerId);

		return getMyModelAndView(hoshuMeisaiEditForm, AJAX_MODAL_PATH_HOSHU_MEISAI, VIEW_FORM_NAME_HOSHU_MESAI);
	}

	/**
	 * 前回の情報から、源泉徴収するかどうかを判断する。
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return 0：源泉徴収しない、1：源泉徴収する
	 */
	@ResponseBody
	@RequestMapping(value = "/changeGensenChoshu", method = RequestMethod.POST)
	public String changeGensenChoshu(@RequestParam(name = "ankenId", required = false) Long ankenId,
			@RequestParam(name = "customerId", required = false) Long customerId) {

		String gensenChoshu = GensenChoshu.DO_NOT.getCd();

		gensenChoshu = service.getPrevGensenChoshu(ankenId, customerId);

		return gensenChoshu;
	}

	// ****************************************************************
	// 会計記録
	// ****************************************************************
	/**
	 * 入出金モーダル初期表示
	 *
	 * @param kaikeiKirokuSeq 会計記録SEQ
	 * @param transitionFlg 遷移元判定フラグ
	 * @param transitionAnkenId 遷移元案件ID
	 * @param transitionCustomerId 遷移元顧客ID
	 * @param msgHolder メッセージの保持クラス
	 * @return 画面情報
	 */
	@ResponseBody
	@RequestMapping(value = "/nyushukkinEdit", method = RequestMethod.POST)
	public ModelAndView nyushukkinEdit(@RequestParam(name = "kaikeiKirokuSeq", required = false) Long kaikeiKirokuSeq,
			@RequestParam(name = "transitionFlg", required = false) String transitionFlg,
			@RequestParam(name = "transitionAnkenId", required = false) Long transitionAnkenId,
			@RequestParam(name = "transitionCustomerId", required = false) Long transitionCustomerId, MessageHolder msgHolder) {

		NyushukkinEditForm nyushukkinEditForm = new NyushukkinEditForm();

		try {
			// 会計記録SEQを元に、初期データを設定します。
			nyushukkinEditForm = service.setInitDataForNyushukkin(kaikeiKirokuSeq, transitionFlg, transitionAnkenId, transitionCustomerId);

		} catch (AppException ex) {
			// エラーメッセージを表示します。
			msgHolder.setErrorMsg(super.getMessage(ex.getMessageKey()));
			return getMyModelAndViewByModalError(msgHolder);
		}
		return getMyModelAndView(nyushukkinEditForm, AJAX_MODAL_PATH_NYUSHUKKIN, VIEW_FORM_NAME_NYUSHUKKIN);
	}

	/**
	 * 入出金情報の新規登録を行います。
	 *
	 * @param nyushukkinEditForm 入出金情報の編集用フォーム
	 * @param result バリデーション結果
	 * @param msgHolder メッセージの保持クラス
	 * @return メッセージ
	 */
	@ResponseBody
	@RequestMapping(value = "/saveNyushukkin", method = RequestMethod.POST)
	public Map<String, Object> saveNyushukkin(@Validated NyushukkinEditForm nyushukkinEditForm, BindingResult result, MessageHolder msgHolder) {

		Map<String, Object> response = new HashMap<>();

		// 顧客ID or 案件IDの入力チェック
		validateRequiredIdForKaikeiKirokuDto(nyushukkinEditForm.getTransitionType(), nyushukkinEditForm.getKaikeiKirokuDto(), result);

		// 項目と金額の入力必須チェック
		validateNyushukkinKomokuAndKingaku(nyushukkinEditForm, result);

		// ----------------------------------------------------
		// バリデーションエラーがある場合
		// ----------------------------------------------------
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}
		// ----------------------------------------------------
		// 精算完了済の場合は登録させない
		// ----------------------------------------------------
		Boolean seisanCompFlg = false;
		if (TransitionType.CUSTOMER.equals(nyushukkinEditForm.getTransitionType())) {
			seisanCompFlg = service.seisanCompCheck(nyushukkinEditForm.getKaikeiKirokuDto().getAnkenId(),
					nyushukkinEditForm.getTransitionCustomerId());
		} else {
			seisanCompFlg = service.seisanCompCheck(nyushukkinEditForm.getTransitionAnkenId(),
					nyushukkinEditForm.getKaikeiKirokuDto().getCustomerId());
		}

		if (seisanCompFlg) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00063));
			return response;
		}
		// ----------------------------------------------------
		// 登録処理
		// ----------------------------------------------------
		try {
			service.saveNyushukkin(nyushukkinEditForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "入出金"));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 更新処理を行います。
	 *
	 * @param nyushukkinEditForm 入出金情報の編集用フォーム
	 * @param result バリデーション結果
	 * @param msgHolder メッセージの保持クラス
	 * @return 画面情報
	 */
	@ResponseBody
	@RequestMapping(value = "/updateNyushukkin", method = RequestMethod.POST)
	public Map<String, Object> updateNyushukkin(@Validated NyushukkinEditForm nyushukkinEditForm, BindingResult result, MessageHolder msgHolder) {

		Map<String, Object> response = new HashMap<>();

		// 顧客ID or 案件IDの入力チェック
		validateRequiredIdForKaikeiKirokuDto(nyushukkinEditForm.getTransitionType(), nyushukkinEditForm.getKaikeiKirokuDto(), result);

		// 項目と金額の入力必須チェック
		validateNyushukkinKomokuAndKingaku(nyushukkinEditForm, result);

		// ----------------------------------------------------
		// バリデーションエラーがある場合
		// ----------------------------------------------------
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;

		}

		KaikeiKirokuDto kaikeiKirokuDto = nyushukkinEditForm.getKaikeiKirokuDto();
		Long nyushukkinYoteiSeq = kaikeiKirokuDto.getNyushukkinYoteiSeq();

		if (nyushukkinYoteiSeq == null && StringUtils.isEmpty(kaikeiKirokuDto.getKingaku())) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// ----------------------------------------------------
		// 精算完了済の場合は登録させない
		// ----------------------------------------------------
		boolean seisanCompFlg = false;
		if (TransitionType.CUSTOMER.equals(nyushukkinEditForm.getTransitionType())) {
			seisanCompFlg = service.seisanCompCheck(nyushukkinEditForm.getKaikeiKirokuDto().getAnkenId(), nyushukkinEditForm.getTransitionCustomerId());
		} else {
			seisanCompFlg = service.seisanCompCheck(nyushukkinEditForm.getTransitionAnkenId(), nyushukkinEditForm.getKaikeiKirokuDto().getCustomerId());
		}

		if (seisanCompFlg) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00063));
			return response;
		}
		// ----------------------------------------------------
		// 更新処理
		// ----------------------------------------------------
		try {
			service.updateNyushukkin(nyushukkinEditForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "入出金"));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 削除処理を行います。
	 *
	 * @param nyushukkinEditForm 入出金情報の編集用フォーム
	 * @param msgHolder メッセージの保持クラス
	 * @return 画面情報
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteNyushukkin", method = RequestMethod.POST)
	public Map<String, Object> deleteNyushukkin(@Validated NyushukkinEditForm nyushukkinEditForm, BindingResult result, MessageHolder msgHolder) {

		Map<String, Object> response = new HashMap<>();
		// ----------------------------------------------------
		// バリデーションエラーがある場合
		// ----------------------------------------------------
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;

		}
		// ----------------------------------------------------
		// 削除処理
		// ----------------------------------------------------
		try {
			// 削除処理
			service.deleteNyushukkin(nyushukkinEditForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00001));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	// ******************************************************************************
	// 入出金予定
	// ******************************************************************************
	/**
	 * 入出金予定モーダル
	 *
	 * @param viewForm
	 * @return
	 */
	@RequestMapping(value = "/nyuShukkinYoteiEdit", method = RequestMethod.POST)
	public ModelAndView nyuShukkinYoteiEdit(NyushukkinYoteiEditForm viewForm) {

		try {
			// 画面表示用Formオブジェクトの作成
			service.setInitNyushukkinYoteiForm(viewForm);

			// 編集情報の設定
			service.setEditNyushukkinYoteiForm(viewForm);

		} catch (AppException e) {
			// エラー発生時にメッセージを表示
			return getMyModelAndViewByModalError(MessageHolder.ofError(getMessage(e.getErrorType())));
		}

		return getMyModelAndView(viewForm, AJAX_MODAL_PATH_NYUSHUKKINYOTEI, VIEW_FORM_NAME_NYUSHUKKINYOTEI);
	}

	/**
	 * 関与者の取得
	 *
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaList", method = RequestMethod.POST)
	public ModelAndView getKanyoshaList(@RequestParam(name = "ankenId") Long ankenId) {
		if (ankenId == null) {
			return getMyModelAndView(Collections.emptyList(), NYUSHUKKINYOTEI_KANYOSHA_LIST_PATH, "kanyoshaList");
		}
		List<CustomerKanyoshaPulldownDto> kanyoshaPulldown = commonKaikeiService.customerKanyoshaPulldownList(ankenId);
		return getMyModelAndView(kanyoshaPulldown, NYUSHUKKINYOTEI_KANYOSHA_LIST_PATH, "kanyoshaList");
	}

	/**
	 * 担当弁護士・テナント口座情報リストの取得
	 *
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getTenantAccountKozaList", method = RequestMethod.POST)
	public ModelAndView getTenantAccountKozaList(@RequestParam(name = "ankenId") Long ankenId) {
		if (ankenId == null) {
			return getMyModelAndView(Collections.emptyList(), NYUSHUKKINYOTEI_KANYOSHA_LIST_PATH, "kanyoshaList");
		}
		List<GinkoKozaDto> tenantAccountKozaList = commonKaikeiService.getTenantAccountGinkoKozaList(ankenId);
		return getMyModelAndView(tenantAccountKozaList, NYUSHUKKINYOTEI_TENANT_ACCOUNT_KOZA_LIST_PATH, "tenantAccountGinkoKozaList");
	}

	/**
	 * 入金予定の新規登録
	 *
	 * @param viewForm
	 * @param result
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/nyukinYotei/regist", method = RequestMethod.POST)
	public Map<String, Object> nyukinYoteiRegist(@Validated({Default.class, Nyukin.class}) NyushukkinYoteiEditForm viewForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 相関バリデート
		this.nyushukkinEditFormValidated(viewForm, result, NyushukkinType.NYUKIN);

		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("errors", result.getFieldErrors());
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			return response;
		}

		// DB整合性チェック
		if (this.nyushukkinEditFromAccessDBValidated(viewForm, response, NyushukkinType.NYUKIN)) {
			return response;
		}

		try {
			service.registNyukinYotei(viewForm.getNyushukkinYoteiEditDto());
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "入金予定"));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}

	}

	/**
	 * 入金予定の更新処理
	 *
	 * @param viewForm
	 * @param result
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/nyukinYotei/update", method = RequestMethod.POST)
	public Map<String, Object> nyukinYoteiUpdate(@Validated({Default.class, Nyukin.class}) NyushukkinYoteiEditForm viewForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 相関バリデート
		this.nyushukkinEditFormValidated(viewForm, result, NyushukkinType.NYUKIN);

		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("errors", result.getFieldErrors());
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			return response;
		}

		// DB整合性チェック
		if (this.nyushukkinEditFromAccessDBValidated(viewForm, response, NyushukkinType.NYUKIN)) {
			return response;
		}

		try {
			service.updateNyukinYotei(viewForm.getNyushukkinYoteiEditDto());
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "入金予定"));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 入金予定の削除処理
	 *
	 * @param viewForm
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/nyukinYotei/delete", method = RequestMethod.POST)
	public Map<String, Object> nyukinYoteiDelete(NyushukkinYoteiEditForm viewForm) {

		Map<String, Object> response = new HashMap<>();

		try {
			service.deleteNyukinYotei(viewForm.getNyushukkinYoteiEditDto());
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00001));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 出金予定の新規登録
	 *
	 * @param viewForm
	 * @param result
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/shukkinYotei/regist", method = RequestMethod.POST)
	public Map<String, Object> shukkinYoteiRegist(@Validated({Default.class, Shukkin.class}) NyushukkinYoteiEditForm viewForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 相関バリデート
		this.nyushukkinEditFormValidated(viewForm, result, NyushukkinType.SHUKKIN);

		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("errors", result.getFieldErrors());
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			return response;
		}

		// DB整合性チェック
		if (this.nyushukkinEditFromAccessDBValidated(viewForm, response, NyushukkinType.SHUKKIN)) {
			return response;
		}

		try {
			service.registShukkinYotei(viewForm.getNyushukkinYoteiEditDto());
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "出金予定"));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}

	}

	/**
	 * 出金予定の更新処理
	 *
	 * @param viewForm
	 * @param result
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/shukkinYotei/update", method = RequestMethod.POST)
	public Map<String, Object> shukkinYoteiUpdate(@Validated({Default.class, Shukkin.class}) NyushukkinYoteiEditForm viewForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 相関バリデート
		this.nyushukkinEditFormValidated(viewForm, result, NyushukkinType.SHUKKIN);

		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("errors", result.getFieldErrors());
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			return response;
		}

		// DB整合性チェック
		if (this.nyushukkinEditFromAccessDBValidated(viewForm, response, NyushukkinType.SHUKKIN)) {
			return response;
		}

		try {
			service.updateShukkinYotei(viewForm.getNyushukkinYoteiEditDto());
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "出金予定"));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}

	}

	/**
	 * 出金予定の削除処理
	 *
	 * @param viewForm
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/shukkinYotei/delete", method = RequestMethod.POST)
	public Map<String, Object> shukkinYoteiDelete(NyushukkinYoteiEditForm viewForm) {

		Map<String, Object> response = new HashMap<>();

		try {
			service.deleteShukkinYotei(viewForm.getNyushukkinYoteiEditDto());
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00001));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 入出金予定の相関バリデーション
	 *
	 * @param form
	 * @param result
	 * @param nyushukkinType
	 */
	private void nyushukkinEditFormValidated(NyushukkinYoteiEditForm form, BindingResult result, NyushukkinType nyushukkinType) {

		// 共通相関バリデーション
		this.nyushukkinEditFormCommonValidated(form, result);

		if (NyushukkinType.NYUKIN == nyushukkinType) {
			// 入金の相関バリデーション
			this.nyushukkinEditFormNyukinValidated(form, result);
		} else if (NyushukkinType.SHUKKIN == nyushukkinType) {
			// 出金の相関バリデーション
			this.nyushukkinEditFormShukkinValidated(form, result);
		} else {
			// 引数に値がない場合は考慮しない
			throw new RuntimeException();
		}

	}

	/**
	 * 入金・出金予定編集の共通相関バリデーション
	 *
	 * @param form
	 * @param result
	 */
	private void nyushukkinEditFormCommonValidated(NyushukkinYoteiEditForm form, BindingResult result) {

		String filedName = "nyushukkinYoteiEditDto";
		NyushukkinYoteiEditDto dto = form.getNyushukkinYoteiEditDto();

		// 入金日と入金額はどちらかが入力されたら必須
		if (!StringUtils.isAllEmpty(dto.getNyushukkinDate(), dto.getNyushukkinGaku())) {
			if (StringUtils.isEmpty(dto.getNyushukkinDate())) {
				result.rejectValue(filedName + ".nyushukkinDate", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}
			if (StringUtils.isEmpty(dto.getNyushukkinGaku())) {
				result.rejectValue(filedName + ".nyushukkinGaku", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}
		}

		// 予定額と実績は同値であること
		if (!ValidateUtils.isDecimalEqualValid(
				LoiozNumberUtils.parseAsBigDecimal(dto.getNyushukkinYoteiGaku()),
				LoiozNumberUtils.parseAsBigDecimal(dto.getNyushukkinGaku()))) {
			result.rejectValue(filedName + ".nyushukkinYoteiGaku", null, getMessage(MessageEnum.MSG_E00002, "予定額", "実績"));
			result.rejectValue(filedName + ".nyushukkinGaku", null, getMessage(MessageEnum.MSG_E00002, "予定額", "実績"));
		}

	}

	/**
	 * 入金編集の相関バリデーション
	 *
	 * @param form
	 * @param result
	 */
	private void nyushukkinEditFormNyukinValidated(NyushukkinYoteiEditForm form, BindingResult result) {

		String filedName = "nyushukkinYoteiEditDto";
		NyushukkinYoteiEditDto dto = form.getNyushukkinYoteiEditDto();

		// 支払先が「関与者から選択」だが プルダウンで選択されていない
		if (TargetType.KANYOSHA.equalsByCode(dto.getNyukinShiharaishaType()) && dto.getNyukinShiharaishaKanyoshaSeq() == null) {
			result.rejectValue(filedName + ".nyukinShiharaishaKanyoshaSeq", null, getMessage(MessageEnum.MSG_E00024, "関与者"));
		}

	}

	/**
	 * 出金予定編集の相関バリデーション
	 *
	 * @param form
	 * @param result
	 */
	private void nyushukkinEditFormShukkinValidated(NyushukkinYoteiEditForm form, BindingResult result) {

		String filedName = "nyushukkinYoteiEditDto";
		NyushukkinYoteiEditDto dto = form.getNyushukkinYoteiEditDto();

		// 支払先が「関与者から選択」だが プルダウンで選択されていない
		if (TargetType.KANYOSHA.equalsByCode(dto.getShukkinShiharaiSakiType()) && dto.getShukkinShiharaiSakiKanyoshaSeq() == null) {
			result.rejectValue(filedName + ".shukkinShiharaiSakiKanyoshaSeq", null, getMessage(MessageEnum.MSG_E00024, "関与者"));
		}

	}

	/**
	 * 入出金予定編集 DB整合性チェック
	 *
	 * @param form
	 * @param response
	 * @param nyushukkinType
	 * @return hasError
	 */
	private boolean nyushukkinEditFromAccessDBValidated(NyushukkinYoteiEditForm form, Map<String, Object> response, NyushukkinType nyushukkinType) {

		boolean hasError = false;

		// DB整合性チェック
		Map<String, String> errorMsgMap = new HashMap<>();

		this.nyushukkinEditFromCommonAccessDBValidated(form, errorMsgMap);

		if (NyushukkinType.NYUKIN == nyushukkinType) {
			this.nyushukkinEditFromNyukinAccessDBValidated(form, errorMsgMap);
		} else if (NyushukkinType.SHUKKIN == nyushukkinType) {
			this.nyushukkinEditFromShukkinAccessDBValidated(form, errorMsgMap);
		} else {
			// 引数が指定されていない場合は考慮しない。
			throw new RuntimeException();
		}

		if (!errorMsgMap.isEmpty()) {
			response.put("succeeded", false);
			response.put("message", errorMsgMap.get("message"));
			hasError = true;
		}

		return hasError;
	}

	/**
	 * 入出金予定編集 共通DB整合性チェック
	 *
	 * @param form
	 * @param errorMsgMap
	 */
	private void nyushukkinEditFromCommonAccessDBValidated(NyushukkinYoteiEditForm form, Map<String, String> errorMsgMap) {

		// すでにErrorが格納されている場合は、何もせずに返却
		if (!errorMsgMap.isEmpty()) {
			return;
		}

		NyushukkinYoteiEditDto dto = form.getNyushukkinYoteiEditDto();

		// 精算完了の場合 ->編集できないようにする
		if (service.seisanCompCheck(dto.getAnkenId(), dto.getCustomerId())) {
			errorMsgMap.put("message", getMessage(MessageEnum.MSG_E00063));
			return;
		}

		// 顧客と案件のリレーションを確認
		if (!commonCustomerValidator.isValidRelatingAnken(dto.getCustomerId(), dto.getAnkenId())) {
			errorMsgMap.put("message", getMessage(MessageEnum.MSG_E00025));
			return;
		}

	}

	/**
	 * 入金予定編集 DB整合性チェック
	 *
	 * @param form
	 * @param errorMsgMap
	 */
	private void nyushukkinEditFromNyukinAccessDBValidated(NyushukkinYoteiEditForm form, Map<String, String> errorMsgMap) {

		// すでにErrorが格納されている場合は、何もせずに返却
		if (!errorMsgMap.isEmpty()) {
			return;
		}

		MessageEnum errorMessageEnum = service.nyushukkinEditFromNyukinAccessDBValidated(form);
		if (errorMessageEnum != null) {
			errorMsgMap.put("message", getMessage(errorMessageEnum));
			return;
		}
	}

	/**
	 * 出金予定編集 DB整合性チェック
	 *
	 * @param form
	 * @param errorMsgMap
	 */
	private void nyushukkinEditFromShukkinAccessDBValidated(NyushukkinYoteiEditForm form, Map<String, String> errorMsgMap) {

		// すでにErrorが格納されている場合は、何もせずに返却
		if (!errorMsgMap.isEmpty()) {
			return;
		}

		MessageEnum errorMessageEnum = service.nyushukkinEditFromShukkinAccessDBValidated(form);
		if (errorMessageEnum != null) {
			errorMsgMap.put("message", getMessage(errorMessageEnum));
			return;
		}
	}

	// ****************************************************************
	// 支払計画
	// ****************************************************************
	/**
	 * 初期表示
	 *
	 * @param seisanSeq 精算SEQ
	 * @return 画面情報
	 */
	@RequestMapping(value = "/shiharaiPlanEdit", method = RequestMethod.POST)
	public ModelAndView shiharaiPlanEdit(@RequestParam(name = "seisanSeq", required = false) Long seisanSeq, MessageHolder msgHolder) {

		ShiharaiPlanEditForm shiharaiPlanEditForm = new ShiharaiPlanEditForm();
		try {
			service.setInitDataForShiharaiPlan(seisanSeq, shiharaiPlanEditForm);
		} catch (AppException ex) {
			// エラーメッセージを表示します。
			msgHolder.setErrorMsg(super.getMessage(ex.getMessageKey()));
			return getMyModelAndViewByModalError(msgHolder);
		}

		return getMyModelAndView(shiharaiPlanEditForm, AJAX_MODAL_PATH_SHIHARAIPLAN, VIEW_FORM_NAME_SHIHARAIPLAN);
	}

	/**
	 * 支払い計画追加処理
	 *
	 * @param seisanSeq 精算SEQ
	 * @return 画面情報
	 */
	@RequestMapping(value = "/addPlanContents", method = RequestMethod.POST)
	public ModelAndView addPlanContents(@RequestParam(name = "index") Long newAddIndex) {

		ShiharaiPlanEditForm shiharaiPlanEditForm = new ShiharaiPlanEditForm();
		shiharaiPlanEditForm.setShiharaiPlanYoteiList(Arrays.asList(ShiharaiPlanYotei.builder().newAddIndex(newAddIndex).build()));

		return getMyModelAndView(shiharaiPlanEditForm, AJAX_MODAL_PATH_SHIHARAIPLAN_ADD, VIEW_FORM_NAME_SHIHARAIPLAN);
	}

	/**
	 * 再計算処理を行います。
	 *
	 * @param shiharaiPlanEditForm 支払計画情報の編集用フォーム
	 * @param result バリデーション結果
	 * @param msgHolder メッセージの保持クラス
	 * @return 画面情報
	 */
	@ResponseBody
	@RequestMapping(value = "/reCalcShiharaiPlanCheck", method = RequestMethod.POST)
	public Map<String, Object> reCalcShiharaiPlanCheck(@Validated ShiharaiPlanEditForm shiharaiPlanEditForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 相関バリデーション
		this.shiharaiPlanInputFormValidated(shiharaiPlanEditForm, result, true);

		if (result.hasFieldErrors("shiharaiPlanDto.*")) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors("shiharaiPlanDto.*"));
			return response;
		}

		try {
			service.shiharaiPlanReCalc(shiharaiPlanEditForm);
			response.put("succeeded", true);
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}

	}

	/**
	 * 再計算処理を行います。
	 *
	 * @param shiharaiPlanEditForm 支払計画情報の編集用フォーム
	 * @param result バリデーション結果
	 * @param msgHolder メッセージの保持クラス
	 * @return 画面情報
	 */
	@RequestMapping(value = "/reCalcShiharaiPlan", method = RequestMethod.POST)
	public ModelAndView reCalcShiharaiPlan(ShiharaiPlanEditForm shiharaiPlanEditForm) {

		try {
			service.shiharaiPlanReCalc(shiharaiPlanEditForm);
		} catch (AppException e) {
			return getMyModelAndViewByModalError(MessageHolder.ofError(getMessage(e.getErrorType())));
		}
		return getMyModelAndView(shiharaiPlanEditForm, AJAX_MODAL_PATH_SHIHARAIPLAN, VIEW_FORM_NAME_SHIHARAIPLAN);
	}

	/**
	 * 入力データから差額を計算する
	 *
	 * @param seisanSeq
	 * @param shiharaiPlanEditForm
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/clacSagaku", method = RequestMethod.POST)
	public Map<String, Object> clacSagaku(ShiharaiPlanEditForm shiharaiPlanEditForm) {

		Map<String, Object> response = new HashMap<>();

		try {
			BigDecimal sagaku = service.shiharaiPlanCalcSagaku(shiharaiPlanEditForm);
			response.put("succeeded", true);
			response.put("sagaku", commonKaikeiService.toDispAmountLabel(sagaku));
			response.put("isExpected", BigDecimal.ZERO.compareTo(sagaku) == 0);// 差額が0円ではない場合、response先で制御を行う
			response.put("isMinus", BigDecimal.ZERO.compareTo(sagaku) == 1);// 差額が0円より少ない場合、response先で制御を行う
			return response;

		} catch (AppException e) {
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 支払計画の更新処理
	 *
	 * @param shiharaiPlanEditForm
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/shiharaiPlanSave", method = RequestMethod.POST)
	public Map<String, Object> shiharaiPlanSave(@Validated ShiharaiPlanEditForm shiharaiPlanEditForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 回収不能として処理する場合は、入力内容に関係なく処理を行うためバリデーションチェックは行わない
		if (shiharaiPlanEditForm.isUncollectible()) {
			return this.shiharaiPlanSaveForUncollectible(shiharaiPlanEditForm, response);
		}

		// 相関バリデーション
		this.shiharaiPlanInputFormValidated(shiharaiPlanEditForm, result, false);

		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("errors", result.getFieldErrors());
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			return response;
		}

		// DB整合性チェック
		if (shiharaiPlanInputFormAccessDBValidated(shiharaiPlanEditForm, response)) {
			return response;
		}

		try {
			service.shiharaiPlanSave(shiharaiPlanEditForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "支払計画"));
			return response;

		} catch (AppException e) {
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 支払計画の登録 回収不能の場合
	 *
	 * @param shiharaiPlanEditForm
	 * @param response
	 * @return
	 */
	private Map<String, Object> shiharaiPlanSaveForUncollectible(ShiharaiPlanEditForm shiharaiPlanEditForm, Map<String, Object> response) {

		try {
			service.shiharaiPlanSaveForUncollectible(shiharaiPlanEditForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "支払計画"));
			return response;

		} catch (AppException e) {
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 支払計画 保存処理時のバリデーション
	 *
	 * @param shiharaiPlanEditForm
	 * @param result
	 */
	private void shiharaiPlanInputFormValidated(ShiharaiPlanEditForm shiharaiPlanEditForm, BindingResult result, boolean isReClac) {
		this.shiharaiPlanDtoInputDataValidated(shiharaiPlanEditForm, result);
		this.shiharaiPlanNewAddDataValidated(shiharaiPlanEditForm, result);
		this.shiharaiPlanUpdateDataValidated(shiharaiPlanEditForm, result);

		if (isReClac) {
			this.shiharaiPlanDtoReClacValidated(shiharaiPlanEditForm, result);
		}
	}

	/**
	 * 再計算処理のみ必要なバリデーションチェック
	 *
	 * @param shiharaiPlanEditForm
	 * @param result
	 */
	private void shiharaiPlanDtoReClacValidated(ShiharaiPlanEditForm shiharaiPlanEditForm, BindingResult result) {

		Function<String, String> fieldName = name -> "shiharaiPlanDto." + name;
		ShiharaiPlanDto dto = shiharaiPlanEditForm.getShiharaiPlanDto();

		if (SeikyuType.BUNKATSU.equalsByCode(dto.getSeikyuType()) && dto.getSeisanSeq() != null) {
			// 残金の取得
			BigDecimal zankin = commonKaikeiService.getZankin(dto.getSeisanSeq());
			if (!StringUtils.isEmpty(dto.getMonthShiharaiGaku())) {
				BigDecimal monthShiharaiGaku = LoiozNumberUtils.parseAsBigDecimal(dto.getMonthShiharaiGaku());
				// 残金より月々支払額が少ない場合
				if (zankin.compareTo(monthShiharaiGaku) == -1) {
					result.rejectValue(fieldName.apply("monthShiharaiGaku"), null, getMessage(MessageEnum.MSG_E00042, "残金より少ない金額"));
				}
			}
		}
	}

	/**
	 * 支払計画 計画作成条件のバリデーション
	 *
	 * @param shiharaiPlanEditForm
	 * @param result
	 */
	private void shiharaiPlanDtoInputDataValidated(ShiharaiPlanEditForm shiharaiPlanEditForm, BindingResult result) {

		Function<String, String> fieldName = name -> "shiharaiPlanDto." + name;
		ShiharaiPlanDto dto = shiharaiPlanEditForm.getShiharaiPlanDto();

		if (SeikyuType.IKKATSU.equalsByCode(dto.getSeikyuType())) {
			// 支払日の必須チェック
			if (StringUtils.isEmpty(dto.getShiharaiDate())) {
				result.rejectValue(fieldName.apply("shiharaiDate"), null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}
		} else if (SeikyuType.BUNKATSU.equalsByCode(dto.getSeikyuType())) {

			// 月々の支払額の必須チェック
			if (StringUtils.isEmpty(dto.getMonthShiharaiGaku())) {
				result.rejectValue(fieldName.apply("monthShiharaiGaku"), null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}
			// 端数種別の必須チェック
			if (StringUtils.isEmpty(dto.getHasu())) {
				result.rejectValue(fieldName.apply("hasu"), null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}
			// 月末か日付指定かの必須チェック
			if (StringUtils.isEmpty(dto.getShiharaiDayType())) {
				result.rejectValue(fieldName.apply("shiharaiDayType"), null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}
			// 支払日(分割：年) の必須チェック
			if (StringUtils.isEmpty(dto.getShiharaiYear())) {
				result.rejectValue(fieldName.apply("shiharaiYear"), null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}
			// 支払日(分割：月) の必須チェック
			if (StringUtils.isEmpty(dto.getShiharaiMonth())) {
				result.rejectValue(fieldName.apply("shiharaiMonth"), null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}
			// 入金予定日の必須チェック
			if (StringUtils.isEmpty(dto.getShiharaiDayType())) {
				result.rejectValue(fieldName.apply("shiharaiDay"), null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}

		} else {
			// なにもしない
		}
	}

	/**
	 * 新規追加データの入力チェック
	 *
	 * @param shiharaiPlanEditForm
	 * @param result
	 */
	private void shiharaiPlanNewAddDataValidated(ShiharaiPlanEditForm shiharaiPlanEditForm, BindingResult result) {

		BiFunction<Long, String, String> fieldName = (index, name) -> "shiharaiPlanYoteiNewAdd" + "[" + index + "]." + name;
		Map<Long, ShiharaiPlanYotei> shiharaiPlanYoteiNewAdd = shiharaiPlanEditForm.getShiharaiPlanYoteiNewAdd();

		shiharaiPlanYoteiNewAdd.keySet().forEach(key -> {
			ShiharaiPlanYotei data = shiharaiPlanYoteiNewAdd.get(key);
			// 予定額と実績が同じでない場合はエラー
			if (!ValidateUtils.isDecimalEqualValid(LoiozNumberUtils.parseAsBigDecimal(data.getNyushukkinYoteiGaku()),
					LoiozNumberUtils.parseAsBigDecimal(data.getNyushukkinGaku()))) {
				result.rejectValue(fieldName.apply(key, "nyushukkinYoteiGaku"), null, getMessage(MessageEnum.MSG_E00002, "予定額", "実績"));
				result.rejectValue(fieldName.apply(key, "nyushukkinGaku"), null, getMessage(MessageEnum.MSG_E00002, "予定額", "実績"));
			}
			// 入金日と入金額はどちらかが入力されたら必須
			if (!StringUtils.isAllEmpty(data.getNyushukkinDate(), data.getNyushukkinGaku())) {
				if (StringUtils.isEmpty(data.getNyushukkinDate())) {
					result.rejectValue(fieldName.apply(key, "nyushukkinDate"), null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				}
				if (StringUtils.isEmpty(data.getNyushukkinGaku())) {
					result.rejectValue(fieldName.apply(key, "nyushukkinGaku"), null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				}
			}
		});

	}

	/**
	 * 更新データの入力チェック
	 *
	 * @param shiharaiPlanEditForm
	 * @param result
	 */
	private void shiharaiPlanUpdateDataValidated(ShiharaiPlanEditForm shiharaiPlanEditForm, BindingResult result) {

		BiFunction<Long, String, String> fieldName = (index, name) -> "shiharaiPlanYoteiEdit" + "[" + index + "]." + name;
		Map<Long, ShiharaiPlanYotei> shiharaiPlanYoteiEdit = shiharaiPlanEditForm.getShiharaiPlanYoteiEdit();

		shiharaiPlanYoteiEdit.keySet().forEach(key -> {
			ShiharaiPlanYotei data = shiharaiPlanYoteiEdit.get(key);
			// 更新のため、入出金予定SEQは必須チェック
			if (data.getNyushukkinYoteiSeq() == null) {
				result.rejectValue(fieldName.apply(key, "nyushukkinYoteiSeq"), null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}
			// 予定額と実績が同じでない場合はエラー
			if (!ValidateUtils.isDecimalEqualValid(LoiozNumberUtils.parseAsBigDecimal(data.getNyushukkinYoteiGaku()),
					LoiozNumberUtils.parseAsBigDecimal(data.getNyushukkinGaku()))) {
				result.rejectValue(fieldName.apply(key, "nyushukkinYoteiGaku"), null, getMessage(MessageEnum.MSG_E00002, "予定額", "実績"));
				result.rejectValue(fieldName.apply(key, "nyushukkinGaku"), null, getMessage(MessageEnum.MSG_E00002, "予定額", "実績"));
			}
			// 入金日と入金額はどちらかが入力されたら必須
			if (!StringUtils.isAllEmpty(data.getNyushukkinDate(), data.getNyushukkinGaku())) {
				if (StringUtils.isEmpty(data.getNyushukkinDate())) {
					result.rejectValue(fieldName.apply(key, "nyushukkinDate"), null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				}
				if (StringUtils.isEmpty(data.getNyushukkinGaku())) {
					result.rejectValue(fieldName.apply(key, "nyushukkinGaku"), null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				}
			}
		});
	}

	/**
	 * DB整合姓チェック
	 *
	 * @param form
	 * @param response
	 * @return
	 */
	private boolean shiharaiPlanInputFormAccessDBValidated(ShiharaiPlanEditForm form, Map<String, Object> response) {

		boolean hasError = false;

		// DB整合性チェック
		Map<String, String> errorMsgMap = new HashMap<>();

		this.shiharaiPlanInputFormCommonAccessDBValidated(form, errorMsgMap);

		if (!errorMsgMap.isEmpty()) {
			response.put("succeeded", false);
			response.put("message", errorMsgMap.get("message"));
			hasError = true;
		}

		return hasError;
	}

	/**
	 * 共通：支払計画のDB整合姓チェック
	 *
	 * @param form
	 * @param errorMsgMap
	 */
	private void shiharaiPlanInputFormCommonAccessDBValidated(ShiharaiPlanEditForm form, Map<String, String> errorMsgMap) {

		if (!errorMsgMap.isEmpty()) {
			return;
		}

		int inputDataCount = form.getShiharaiPlanYoteiEdit().size() + form.getShiharaiPlanYoteiNewAdd().size();
		// 一括請求だが、支払回数が一回ではない場合
		if (SeikyuType.IKKATSU.equalsByCode(form.getShiharaiPlanDto().getSeikyuType()) && inputDataCount != 1) {
			errorMsgMap.put("message", getMessage(MessageEnum.MSG_E00067, String.valueOf(1)));
			return;
		}
		// 1件も支払計画が存在しない場合
		if (inputDataCount == 0) {
			errorMsgMap.put("message", getMessage(MessageEnum.MSG_E00042, "1件以上の支払計画"));
			return;
		}
		// 分割上限を超える場合
		if (inputDataCount > CommonConstant.BUNKATU_LIMIT_COUNT) {
			errorMsgMap.put("message", getMessage(MessageEnum.MSG_E00067, String.valueOf(CommonConstant.BUNKATU_LIMIT_COUNT)));
			return;
		}

		BigDecimal sagaku = new BigDecimal(0);
		try {
			// 差額を計算
			sagaku = service.shiharaiPlanCalcSagaku(form);
		} catch (AppException ex) {
			// 計算時に発生したAppExceptionもエラーとして画面に表示
			errorMsgMap.put("message", getMessage(ex.getErrorType()));
			return;
		}

		// 差額が0円ではない場合、計画通り支払いを済ませても精算額と一致しないためエラー
		if (BigDecimal.ZERO.compareTo(sagaku) != 0) {
			// 計算時には
			errorMsgMap.put("message", getMessage(MessageEnum.MSG_E00002, "計画総額", "精算額"));
			return;
		}

	}

	// ****************************************************************
	// 精算書作成対象モーダル
	// ****************************************************************

	/**
	 * 初期表示
	 *
	 * @param seisanSeq 精算SEQ
	 * @return 画面情報
	 */
	@ResponseBody
	@RequestMapping(value = "/seisanshoModal", method = RequestMethod.POST)
	public ModelAndView seisanshoModal(@RequestParam(name = "transitionType", required = false) String transitionType,
			@RequestParam(name = "transitionAnkenId", required = false) Long transitionAnkenId,
			@RequestParam(name = "transitionCustomerId", required = false) Long transitionCustomerId, MessageHolder msgHolder) {

		List<KaikeiManagementRelationDto> relationDtoList = new ArrayList<KaikeiManagementRelationDto>();
		AnkenMeisaiEditForm ankenMeisaiEditForm = new AnkenMeisaiEditForm();

		if (TransitionType.ANKEN.getCd().equals(transitionType)) {
			relationDtoList = service.getRelationListForAnken(transitionAnkenId).stream().filter(dto -> (!AnkenStatus.KANRYO.equalsByCode(dto.getAnkenStatusCd()) && !AnkenStatus.FUJUNIN.equalsByCode(dto.getAnkenStatusCd()))).collect(Collectors.toList());
			ankenMeisaiEditForm.setTransitionAnkenId(transitionAnkenId);
		} else {
			relationDtoList = service.getRelationListForCustomer(transitionCustomerId).stream().filter(dto -> (!AnkenStatus.KANRYO.equalsByCode(dto.getAnkenStatusCd()) && !AnkenStatus.FUJUNIN.equalsByCode(dto.getAnkenStatusCd()))).collect(Collectors.toList());
			ankenMeisaiEditForm.setTransitionCustomerId(transitionCustomerId);
		}
		ankenMeisaiEditForm.setKaikeiManagementList(relationDtoList);

		return getMyModelAndView(ankenMeisaiEditForm, AJAX_MODAL_PATH_ANKEN_CUSTOMER, VIEW_FORM_NAME_ANKEN_CUSTOMER);
	}

	/**
	 * 口座詳細情報取得（画面リクエスト用）事務所口座、案件担当口座
	 *
	 * @param KozaInfoForm form
	 * @param BindingResult result
	 * @return Map response
	 */
	@ResponseBody
	@RequestMapping(value = "/getKozaInfo", method = RequestMethod.POST)
	public Map<String, Object> getKozaInfo(@Validated KozaInfoForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		try {
			// 口座詳細情報取得
			GinkoKozaDto viewForm = commonKaikeiService.getKozaDetail(form.getGinkoAccountSeq());

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00002));
			response.put("ginkoAccountSeq", viewForm.getAccountName());
			response.put("labelName", viewForm.getLabelName());
			response.put("ginkoName", viewForm.getGinkoName());
			response.put("shitenName", viewForm.getShitenName());
			response.put("shitenNo", viewForm.getShitenNo());
			response.put("kozaNo", viewForm.getKozaNo());
			response.put("kozaName", viewForm.getKozaName());

			// 口座種別
			KozaType kozaTypeName = KozaType.of(viewForm.getKozaType());
			if (kozaTypeName != null) {
				response.put("kozaType", DefaultEnum.getVal(kozaTypeName));
			} else {
				response.put("kozaType", "");
			}

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));
			return response;
		}
		return response;
	}

	/**
	 * 口座詳細情報取得（画面リクエスト用）事務所口座、案件担当口座
	 *
	 * @param CustomerKozaInfoForm form
	 * @param BindingResult result
	 * @return Map response
	 */
	@ResponseBody
	@RequestMapping(value = "/getCustomerKozaInfo", method = RequestMethod.POST)
	public Map<String, Object> getCustomerKozaInfo(@Validated CustomerKozaInfoForm kozaInfoForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();
		GinkoKozaDto dto = new GinkoKozaDto();

		try {
			Long customerId = kozaInfoForm.getCustomerId();
			Long kanyoshaSeq = kozaInfoForm.getKanyoshaSeq();
			String errMsg = "名簿情報";
			if (customerId != null) {
				// 顧客口座詳細情報取得
				dto = commonKaikeiService.getCustomerKozaDetail(customerId);

			} else if (kanyoshaSeq != null) {
				// 関与者口座詳細情報取得
				dto = commonKaikeiService.getKanyoshaKozaDetail(kanyoshaSeq);
			} else {
				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00082));
				return response;
			}

			// 口座情報の取得有無
			if (dto != null) {
				// 口座情報あり
				response.put("succeeded", true);
				response.put("message", getMessage(MessageEnum.MSG_I00002));
				response.put("ginkoName", dto.getGinkoName());
				response.put("shitenNo", dto.getShitenNo());
				response.put("shitenName", dto.getShitenName());
				response.put("kozaNo", dto.getKozaNo());
				response.put("kozaName", dto.getKozaName());
				// 口座種別
				KozaType kozaTypeName = KozaType.of(dto.getKozaType());
				if (kozaTypeName != null) {
					response.put("kozaType", DefaultEnum.getVal(kozaTypeName));
				} else {
					response.put("kozaType", "");
				}
			} else {
				// 口座情報が登録ない

				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00080, errMsg));
			}

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			return response;
		}

		return response;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	// ****************************************************************
	// 共通
	// ****************************************************************

	/**
	 * 顧客ID or 案件ID の必須チェック<br>
	 *
	 * <pre>
	 * 遷移元によって必須の項目が動的になるので、メソッドとして実装
	 * </pre>
	 *
	 * @param transitionType
	 * @param ankenId
	 * @param customerId
	 */
	private boolean isValidRequiredIdForTransitonType(TransitionType transitionType, Long customerId, Long ankenId) {

		if (TransitionType.CUSTOMER.equals(transitionType)) {
			// 顧客の場合は案件IDが必須
			if (ankenId == null) {
				return true;
			}

		} else if (TransitionType.ANKEN.equals(transitionType)) {
			// 案件の場合は顧客IDが必須
			if (customerId == null) {
				return true;
			}

		} else {
			// 想定外の場合（なにもしない）
			throw new RuntimeException("遷移元が顧客か案件か判断できません");
		}

		return false;
	}

	/**
	 * autoCalcTankaの相関validateメソッド
	 *
	 * @param form
	 * @param result
	 */
	private void autoCalcTankaRequestValidated(AutoCalcTankaAjaxRequest form, BindingResult result) {

		// 単項目でのバリデーションエラーがすでにある場合は、相関チェックできないものがあるので何もせずに返却
		if (result.hasErrors()) {
			return;
		}

		// 開始日時と終了日時で指定した場合、時間の計算を行う。
		if (TimeChargeTimeShitei.START_END_TIME.equalsByCode(form.getTimeChargeTimeShitei())) {
			Long timeChargeTime = commonKaikeiService.calcDifferenceStartAndEndTime(
					form.getDateFrom(), form.getTimeFrom(),
					form.getDateTo(), form.getTimeTo());
			form.setTimeChargeTime(timeChargeTime);
		}

		// 経過時間の整合性チェック
		if (ValidateUtils.isLess0NumValid(form.getTimeChargeTime())) {
			result.rejectValue("timeChargeTime", null, getMessage(MessageEnum.MSG_E00060));
		}

	}

	/**
	 * autoCalcTimeChargeTimeの相関validateメソッド<br>
	 * ※バリデーションのためtimeChargeTimeを算出するので、合わせてfromへ値を設定する
	 *
	 * @param form
	 * @param result
	 */
	private void autoCalcTimeChargeTimeRequestValidated(AutoCalcTimeChargeTimeAjaxRequest form, BindingResult result) {

		// 単項目でのバリデーションエラーがすでにある場合は、相関チェックできないものがあるので何もせずに返却
		if (result.hasErrors()) {
			return;
		}

		// 開始日時と終了日時で指定した場合、時間の計算を行う。
		if (TimeChargeTimeShitei.START_END_TIME.equalsByCode(form.getTimeChargeTimeShitei())) {
			Long timeChargeTime = commonKaikeiService.calcDifferenceStartAndEndTime(
					form.getDateFrom(), form.getTimeFrom(),
					form.getDateTo(), form.getTimeTo());
			form.setTimeChargeTime(timeChargeTime);
		}

		// 経過時間の整合性チェック
		if (ValidateUtils.isLess0NumValid(form.getTimeChargeTime())) {
			result.rejectValue("timeChargeTime", null, getMessage(MessageEnum.MSG_E00060));
		}
	}

	// ****************************************************************
	// 案件明細
	// ****************************************************************

	/**
	 * 案件明細モーダルの相関バリデーション
	 *
	 * @param transitionType
	 * @param ankenMeisaiDto
	 * @param result
	 * @throws AppException
	 */
	private void ankenMeisaiDtoValidated(TransitionType transitionType, AnkenMeisaiDto ankenMeisaiDto, BindingResult result) throws AppException {

		Boolean returnFlg = false;
		// 単項目でのバリデーションエラーがすでにある場合は、相関チェックできないものがあるので何もせずに返却
		if (result.hasErrors()) {
			return;
		}

		// IDの必須チェック
		if (isValidRequiredIdForTransitonType(transitionType, ankenMeisaiDto.getCustomerId(), ankenMeisaiDto.getAnkenId())) {
			// 画面上は要素がどっちかしかないのでエラーはどっちも格納する
			result.rejectValue("ankenMeisaiDto.customerId", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			result.rejectValue("ankenMeisaiDto.ankenId", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
		}

		// 報酬項目によって異なるバリデーションが異なる
		if (ankenMeisaiDto.getHoshuKomokuId().equals(LawyerHoshu.TIME_CHARGE.getCd())) {
			// タイムチャージの場合、単価が必須
			BigDecimal timeChargeTanka = new BigDecimal(0);
			if (StringUtils.isEmpty(ankenMeisaiDto.getTimeChargeTanka())) {
				result.rejectValue("ankenMeisaiDto.timeChargeTanka", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				return;
			} else {

				LoiozDecimalFormat decimalFormat = new LoiozDecimalFormat();
				decimalFormat.setParseBigDecimal(true);
				String dispTimeChargeTanka = ankenMeisaiDto.getTimeChargeTanka();
				try {
					timeChargeTanka = (BigDecimal) decimalFormat.parse(dispTimeChargeTanka);
					// 最大桁数以上入力された場合
					if (timeChargeTanka.precision() > CommonConstant.MAX_KINGAKU_INPUT_DIGIT) {
						result.rejectValue("ankenMeisaiDto.timeChargeTanka", null, getMessage(MessageEnum.MSG_E00015));
					}
				} catch (ParseException e) {
					result.rejectValue("ankenMeisaiDto.timeChargeTanka", null, getMessage(MessageEnum.MSG_E00030));
				}
			}

			// 「税抜、税込」の選択値は、税抜以外はNG（タイムチャージは固定で税抜としているため）
			if (!TaxFlg.FOREIGN_TAX.getCd().equals(ankenMeisaiDto.getTaxFlg())) {
				result.rejectValue("ankenMeisaiDto.taxFlg", null, getMessage(MessageEnum.MSG_E00001));
			}

			Long calcTimeChargeTime = Long.valueOf(0);
			if (TimeChargeTimeShitei.START_END_TIME.equalsByCode(ankenMeisaiDto.getTimeChargeTimeShitei())) {
				// 日付から計算の場合のみ、開始終了日時の必須チェック

				// 開始日、開始時間
				if (StringUtils.isEmpty(ankenMeisaiDto.getTimeChargeStartDate()) || StringUtils.isEmpty(ankenMeisaiDto.getTimeChargeStartTime())) {
					result.rejectValue("ankenMeisaiDto.timeChargeStartDate", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
					returnFlg = true;
				}
				// 終了日、終了時間
				if (StringUtils.isEmpty(ankenMeisaiDto.getTimeChargeEndDate()) || StringUtils.isEmpty(ankenMeisaiDto.getTimeChargeEndTime())) {
					result.rejectValue("ankenMeisaiDto.timeChargeEndDate", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
					returnFlg = true;
				}
				// 日付チェックでエラーがある場合returnする。
				if (returnFlg) {
					return;
				}
				// 時間(分)は計算で求める
				calcTimeChargeTime = commonKaikeiService.calcDifferenceStartAndEndTime(
						ankenMeisaiDto.getTimeChargeStartDate(), ankenMeisaiDto.getTimeChargeStartTime(),
						ankenMeisaiDto.getTimeChargeEndDate(), ankenMeisaiDto.getTimeChargeEndTime());

				String timeChargeTime = ankenMeisaiDto.getTimeChargeTime();
				if (StringUtils.isEmpty(timeChargeTime)) {
					// リクエストパラメータのtimeChargeTime（タイムチャージ時間）の値が無い場合
					// （入力値による自動計算の結果が画面に反映される前に保存処理が実行されたケース）
					// -> 上記の計算で求めたタイムチャージ時間の値をDtoにセットする（計算結果がnullの場合はセットしない）
					if (calcTimeChargeTime != null) {
						ankenMeisaiDto.setTimeChargeTime(String.valueOf(calcTimeChargeTime));
					}
				}

			} else {
				// 分から計算の場合は、時間(分)が必須
				if (StringUtils.isEmpty(ankenMeisaiDto.getTimeChargeTime())) {
					result.rejectValue("ankenMeisaiDto.timeChargeTime", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
					return;
				}
				calcTimeChargeTime = Long.valueOf(ankenMeisaiDto.getTimeChargeTime());
			}

			// 時間が0分以下の場合
			if (ValidateUtils.isLess0NumValid(calcTimeChargeTime)) {
				result.rejectValue("ankenMeisaiDto.timeChargeTime", null, getMessage(MessageEnum.MSG_E00060));
				return;
			}

			// すでにエラーがある場合はチェック終了とする
			if (result.hasErrors()) {
				return;
			}

			// 金額（報酬額）の桁数チェック
			// 単価 × 時間 で金額（報酬額）を算出
			BigDecimal decimalMinutes = BigDecimal.valueOf(calcTimeChargeTime);
			BigDecimal shukkinGaku = service.hoshuCal(timeChargeTanka, decimalMinutes);
			// 最大桁数以上となる場合
			if (shukkinGaku.precision() > CommonConstant.MAX_KINGAKU_INPUT_DIGIT) {
				String message = getMessage(MessageEnum.MSG_E00157, "報酬額", String.valueOf(CommonConstant.MAX_KINGAKU_INPUT_DIGIT));
				result.rejectValue("ankenMeisaiDto.timeChargeTanka", null, message);
				return;
			}

		} else {
			// それ以外の項目の場合、金額が必須
			if (StringUtils.isEmpty(ankenMeisaiDto.getKingaku())) {
				result.rejectValue("ankenMeisaiDto.kingaku", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				return;
			} else {

				// 金額の桁数チェック
				LoiozDecimalFormat decimalFormat = new LoiozDecimalFormat();
				decimalFormat.setParseBigDecimal(true);
				String dispKingaku = ankenMeisaiDto.getKingaku();
				BigDecimal kingaku = new BigDecimal(0);
				try {
					kingaku = (BigDecimal) decimalFormat.parse(dispKingaku);
					// 最大桁数以上入力された場合
					if (kingaku.precision() > CommonConstant.MAX_KINGAKU_INPUT_DIGIT) {
						result.rejectValue("ankenMeisaiDto.kingaku", null, getMessage(MessageEnum.MSG_E00015));
					}
				} catch (ParseException e) {
					result.rejectValue("ankenMeisaiDto.kingaku", null, getMessage(MessageEnum.MSG_E00030));
				}
			}

			// 「税抜、税込」の選択値で不正な値の場合NG（選択肢以外の値が送信された場合）
			String taxFlg = ankenMeisaiDto.getTaxFlg();
			if (!TaxFlg.FOREIGN_TAX.getCd().equals(taxFlg)
					&& !TaxFlg.INTERNAL_TAX.getCd().equals(taxFlg)) {
				result.rejectValue("ankenMeisaiDto.taxFlg", null, getMessage(MessageEnum.MSG_E00001));
			}
		}

	}

	// ****************************************************************
	// 会計記録
	// ****************************************************************

	/**
	 * 顧客ID or 案件ID の必須チェック<br>
	 *
	 * <pre>
	 * 遷移元によって必須の項目が動的になるので、メソッドとして実装
	 * </pre>
	 *
	 * @param transitionType
	 * @param kaikeiKirokuDto
	 * @param result
	 */
	private void validateRequiredIdForKaikeiKirokuDto(TransitionType transitionType, KaikeiKirokuDto kaikeiKirokuDto, BindingResult result) {

		if (TransitionType.CUSTOMER.equals(transitionType)) {
			// 顧客の場合は案件IDが必須
			if (kaikeiKirokuDto.getAnkenId() == null) {
				result.rejectValue("kaikeiKirokuDto.ankenId", MessageEnum.VARIDATE_MSG_E00002.getMessageKey(),
						getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}

		} else if (TransitionType.ANKEN.equals(transitionType)) {
			// 案件の場合は顧客IDが必須
			if (kaikeiKirokuDto.getCustomerId() == null) {
				result.rejectValue("kaikeiKirokuDto.customerId", MessageEnum.VARIDATE_MSG_E00002.getMessageKey(),
						getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}

		} else {
			// 想定外の場合（なにもしない）
			logger.error("遷移元が顧客か案件か判断できません");
		}

	}

	/**
	 * 入出金項目IDと、金額の入力必須チェック
	 *
	 * @param kaikeiKirokuDto 入出金情報
	 * @param result バリデーション結果
	 */
	private void validateNyushukkinKomokuAndKingaku(NyushukkinEditForm nyushukkinEditForm, BindingResult result) {

		KaikeiKirokuDto kaikeiKirokuDto = nyushukkinEditForm.getKaikeiKirokuDto();

		if (NyushukkinType.NYUKIN.equalsByCode(kaikeiKirokuDto.getNyushukkinType())) {
			// 入金の場合
			if (kaikeiKirokuDto.getNyukinKomokuId() == null) {
				result.rejectValue("kaikeiKirokuDto.nyukinKomokuId", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}

		} else {
			// 出金の場合
			if (kaikeiKirokuDto.getShukkinKomokuId() == null) {
				result.rejectValue("kaikeiKirokuDto.shukkinKomokuId", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}
		}

		if (StringUtils.isEmpty(kaikeiKirokuDto.getKingaku())) {
			// 金額が未設定
			result.rejectValue("kaikeiKirokuDto.kingaku", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
		} else {

			LoiozDecimalFormat decimalFormat = new LoiozDecimalFormat();
			decimalFormat.setParseBigDecimal(true);
			String dispKingaku = kaikeiKirokuDto.getKingaku();
			BigDecimal kingaku = new BigDecimal(0);
			try {
				kingaku = (BigDecimal) decimalFormat.parse(dispKingaku);
				// 最大桁数
				if (kingaku.precision() > CommonConstant.MAX_KINGAKU_INPUT_DIGIT) {
					result.rejectValue("kaikeiKirokuDto.kingaku", null, getMessage(MessageEnum.MSG_E00015));
					return;
				}
				// 入金の場合は0、0以下の場合はエラーとする
				if (NyushukkinType.NYUKIN.equalsByCode(kaikeiKirokuDto.getNyushukkinType())) {
					if (kingaku.compareTo(BigDecimal.ZERO) <= 0) {
						result.rejectValue("kaikeiKirokuDto.kingaku", null, getMessage(MessageEnum.MSG_E00058));
					}
				} else {
					// 出金の場合は0、場合はエラーとする（マイナス値は入力可能とする）
					if (kingaku.compareTo(BigDecimal.ZERO) == 0) {
						result.rejectValue("kaikeiKirokuDto.kingaku", null, getMessage(MessageEnum.MSG_E00058));
					}
				}
			} catch (ParseException e) {
				result.rejectValue("kaikeiKirokuDto.kingaku", null, getMessage(MessageEnum.MSG_E00030));
			}
		}
	}

}
