package jp.loioz.app.user.seisansho.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.seisansho.form.SeisanshoEditForm;
import jp.loioz.app.user.seisansho.form.SeisanshoViewForm;
import jp.loioz.app.user.seisansho.service.SeisanshoEditService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.SeikyuType;
import jp.loioz.common.constant.CommonConstant.SeisanKirokuKubun;
import jp.loioz.common.constant.CommonConstant.TargetType;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.utility.data.LoiozDecimalFormat;
import jp.loioz.dto.SeisanshoCreateDto;

/**
 * 精算書画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@LoiozAdminControlCheck(items = {LoiozAdminControl.OLD_KAIKEI_OPEN})
@Controller
@RequestMapping(UrlConstant.SEISANSHO_URL)
public class SeisanshoEditController extends DefaultController {

	/** 精算書Createのviewのパス */
	private static final String MY_VIEW_PATH = "user/seisansho/seisanshoEdit";

	/** 報酬情報：検索した際のviewパス */
	public static final String HOSHU_LIST = MY_VIEW_PATH + "::hoshuList";

	/** 会計記録情報：検索した際のviewパス */
	public static final String KAIKEI_KIROKU_LIST = MY_VIEW_PATH + "::kaikeiKirokuList";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 精算書作成サービスクラス */
	@Autowired
	private SeisanshoEditService service;

	/**
	 * 精算書作成画面 初期表示<br>
	 *
	 * @param viewForm
	 * @param msgHolder
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView indexCreate(SeisanshoViewForm viewForm, MessageHolder msgHolder) {
		try {
			// 精算作成 初期表示データ取得
			service.createViewForm(viewForm);

			if (viewForm.getSeisanSeq() != null) {
				// 編集の場合

				if (viewForm.isSeisanComplete()) {
					// 精算完了の場合
					if (viewForm.isShowAnkenCompletedMsg()) {
						// 案件ステータスが完了状態である旨のメッセージ
						viewForm.setInfoMsg(getMessage(MessageEnum.MSG_I00047));
					} else if (viewForm.isShowSeisanCompletedMsg()) {
						// 精算完了日が設定されている旨のメッセージ
						viewForm.setInfoMsg(getMessage(MessageEnum.MSG_I00048));
					}
				} else if (!viewForm.isAbledEdit()) {
					// 編集不可の場合
					viewForm.setInfoMsg(getMessage(MessageEnum.MSG_I00117));
				}
			}

		} catch (AppException ex) {
			// エラーメッセージを表示します。
			msgHolder.setErrorMsg(super.getMessage(ex.getMessageKey()));
			return getMyModelAndViewByModalError(msgHolder);
		}
		return this.getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 精算書作成処理
	 *
	 * @param editForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> regist(@Validated SeisanshoEditForm editForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 精算書作成Formオブジェクトの相関チェック
		this.inputFormValidated(editForm, result);

		// バリデーションエラーが存在する場合
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("errors", result.getFieldErrors());
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			return response;
		}

		// 精算書作成FormオブジェクトのDB整合性チェック
		if (accessDBValidated(editForm)) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00025));
			return response;
		}

		try {
			// 精算記録の登録
			service.registSeisanKiroku(editForm);

			// 正常終了で返却
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "精算書"));
			return response;

		} catch (AppException ex) {
			// エラー内容を返却します。
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getMessageKey()));
			return response;
		}

	}

	/**
	 * 精算書更新処理
	 *
	 * @param editForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> update(@Validated SeisanshoEditForm editForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 精算書作成Formオブジェクトの相関チェック
		this.inputFormValidated(editForm, result);

		// バリデーションエラーが存在する場合
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("errors", result.getFieldErrors());
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			return response;
		}

		// 精算書作成FormオブジェクトのDB整合性チェック
		if (accessDBValidated(editForm)) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00025));
			return response;
		}

		try {
			// データの取得
			SeisanshoViewForm viewForm = service.recalculation(editForm.getKaikeiSeqList());

			// 精算の更新処理
			service.updateSeisanKiroku(viewForm, editForm);

			/* Map形式で返却 */
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "精算書"));
			return response;

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得します。
			response.put("succeeded", false);
			response.put("message", super.getMessage(ex.getMessageKey()));
			return response;
		}

	}

	/**
	 * 精算書削除処理
	 *
	 * @param seisanSeq
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> delete(@RequestParam("seisanSeq") Long seisanSeq) {

		Map<String, Object> response = new HashMap<>();

		try {
			service.deleteSeisanKiroku(seisanSeq);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00118));
			return response;

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得します。
			response.put("succeeded", false);
			response.put("message", super.getMessage(ex.getMessageKey()));
			return response;
		}
	}

	/**
	 * 精算額を預り金としてプールする
	 *
	 * @param editForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/seisanGakuPool", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> seisanGakuPool(@Validated SeisanshoEditForm editForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 精算書作成Formオブジェクトの相関チェック
		this.inputFormValidated(editForm, result);

		// バリデーションエラーが存在する場合
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("errors", result.getFieldErrors());
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			return response;
		}

		// 精算書作成FormオブジェクトのDB整合性チェック
		if (accessDBValidated(editForm)) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00025));
			return response;
		}

		try {
			// プール処理
			service.saveSeisanKirokuForPool(editForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00024));
			return response;

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得します。
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getMessageKey()));
			return response;
		}

	}

	/**
	 * 再計算処理
	 *
	 * @param map 再計算のリクエスト情報
	 * @param result バリデーション結果
	 * @param msgHolder メッセージの保持クラス
	 * @return 画面情報
	 */
	@RequestMapping(value = "/recalculation", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> recalculation(SeisanshoEditForm form, BindingResult result, MessageHolder msgHolder) {

		// Responseの定義
		Map<String, Object> response = new HashMap<>();

		try {
			// 精算書の計算を行う処理
			SeisanshoViewForm viewForm = service.recalculation(form.getKaikeiSeqList());

			// 取得したデータから必要なもののみを返却
			response.put("kaikeiKirokuNyukinTotal", viewForm.getKaikeiKirokuNyukinTotal());
			response.put("kaikeiKirokuShukkinTotal", viewForm.getKaikeiKirokuShukkinTotal());
			response.put("kaikeiKirokuHoshuTotal", viewForm.getKaikeiKirokuHoshuTotal());
			response.put("kaikeiKirokuTaxTotal", viewForm.getKaikeiKirokuTaxTotal());
			response.put("kaikeiKirokuHoshuTax8perTotal", viewForm.getKaikeiKirokuHoshuTax8perTotal());
			response.put("kaikeiKirokuHoshuTax10perTotal", viewForm.getKaikeiKirokuHoshuTax10perTotal());
			response.put("kaikeiKirokuHoshuShoukei", viewForm.getKaikeiKirokuHoshuShoukei());
			response.put("kaikeiKirokuGensenTotal", viewForm.getKaikeiKirokuGensenTotal());
			response.put("kaikeiKirokuSashiHikiTotal", viewForm.getKaikeiKirokuSashiHikiTotal());
			response.put("seisangakuTotal", viewForm.getSeisangakuTotal());
			response.put("dispSeisangakuTotal", viewForm.getDispSeisangakuTotal());

		} catch (AppException ex) {
			// errorがあったのでerrorを返す
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getMessageKey()));
			return response;
		}
		response.put("succeeded", true);
		response.put("message", getMessage(MessageEnum.MSG_I00002.getMessageKey()));
		return response;

	}

	// ===============================================================
	// バリデーション処理
	// ===============================================================

	/**
	 * 相関バリデーションチェック
	 *
	 * @param form
	 * @param result
	 */
	private void inputFormValidated(SeisanshoEditForm form, BindingResult result) {

		// すでにバリデーションエラーが存在する場合は相関チェックを行いません
		if (result.hasErrors()) {
			return;
		}

		// 精算するデータが未選択チェック
		if (ListUtils.isEmpty(form.getKaikeiSeqList())) {
			result.rejectValue("kaikeiSeqList", null, getMessage(MessageEnum.MSG_E00024, "精算する対象"));
		}

		// 精算書作成時の作成条件
		SeisanshoCreateDto dto = form.getSeisanshoCreateDto();

		// DtoがeditForm配下でインスタンス化しているので、インスタンス化した名前を定義
		final String childFieldName = "seisanshoCreateDto.";

		BigDecimal seisanGaku = form.getSeisanGakuTotal();

		// DBで処理できる精算額が超えている
		if (seisanGaku.precision() > CommonConstant.MAX_KINGAKU_KETA) {
			// 10桁を超えた場合はエラー
			result.rejectValue("seisanGakuTotal", null, getMessage(MessageEnum.MSG_E00083));
		}

		if (SeisanKirokuKubun.SEISAN.equalsByCode(dto.getSeisanKubun())) {
			// 精算の時(金額が0以上の時)

			if (form.isSeisanZero()) {
				// 精算額がゼロの場合、なにもせずに返却(入力項目がないため)
				return;
			}

			// プールの時
			if (dto.isPoolFlg()) {
				if (Objects.isNull(dto.getPoolAnkenId())) {
					result.rejectValue(childFieldName + "poolAnkenId", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				}
				// 先の処理は行わない
				return;
			}

			// 支払期日は必須
			if (StringUtils.isEmpty(dto.getPaymentLimitDt())) {
				result.rejectValue(childFieldName + "paymentLimitDt", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}

			// 返金者口座が必須
			if (Objects.isNull(dto.getHenkinKozaSeq())) {
				result.rejectValue(childFieldName + "henkinKozaSeq", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}

			// 返金先口座 相関必須チェック
			if (StringUtils.isEmpty(dto.getHenkinSakiType())
					|| !targetTypeRequiredValidated(dto.getHenkinSakiType(), dto.getHenkinCustomerId(), dto.getHenkinKanyoshaSeq())) {
				result.rejectValue(childFieldName + "henkinSakiType", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}

		} else if (SeisanKirokuKubun.SEIKYU.equalsByCode(dto.getSeisanKubun())) {
			// 請求の時 (金額がマイナスのとき)

			// ***********************************************
			// 一括時も分割時も行う相関チェックを先に行う
			// ***********************************************

			// 支払者 相関必須チェック
			if (StringUtils.isEmpty(dto.getShiharaishaType())
					|| !targetTypeRequiredValidated(dto.getShiharaishaType(), dto.getShiharaiCustomerId(), dto.getShiharaiKanyoshaSeq())) {
				result.rejectValue(childFieldName + "shiharaishaType", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}

			// 請求口座が必須
			if (Objects.isNull(dto.getSeikyuKozaSeq())) {
				result.rejectValue(childFieldName + "seikyuKozaSeq", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
			}

			if (SeikyuType.IKKATSU.equalsByCode(dto.getSeikyuType())) {
				// 一括の時

				// 入金予定日が必須
				if (StringUtils.isEmpty(dto.getPaymentYoteiDate())) {
					result.rejectValue(childFieldName + "paymentYoteiDate", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				}

			} else if (SeikyuType.BUNKATSU.equalsByCode(dto.getSeikyuType())) {
				// 分割の時

				if (StringUtils.isEmpty(dto.getMonthlyPay())) {
					// 月々支払額の必須チェック
					result.rejectValue(childFieldName + "monthlyPay", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				} else {
					// 月々支払額の入力フォーマットチェック
					LoiozDecimalFormat decimalFormat = new LoiozDecimalFormat();
					decimalFormat.setParseBigDecimal(true);
					String dispMonthlyPay = dto.getMonthlyPay();
					BigDecimal monthlyPay = new BigDecimal(0);
					try {
						monthlyPay = (BigDecimal) decimalFormat.parse(dispMonthlyPay);
						// 最大桁数
						if (monthlyPay.precision() > CommonConstant.MAX_KINGAKU_INPUT_DIGIT) {
							result.rejectValue(childFieldName + "monthlyPay", null, getMessage(MessageEnum.MSG_E00015));
						}
						// 月額が精算額を超えている
						if (seisanGaku.negate().compareTo(monthlyPay) < 0) {
							result.rejectValue(childFieldName + "monthlyPay", null, getMessage(MessageEnum.MSG_E00042, "精算額より少ない金額"));
						}

					} catch (ParseException e) {
						result.rejectValue("nyukinYoteiDto.displayNyushukkinYoteiGaku", null, getMessage(MessageEnum.MSG_E00030));
					}
				}

				// 支払い開始年の必須チェック ※数値のFormで単項目で行う
				if (StringUtils.isEmpty(dto.getShiharaiStartYear())) {
					result.rejectValue(childFieldName + "shiharaiStartYear", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				}

				// 支払い開始月の必須チェック ※数値のFormで単項目で行う
				if (StringUtils.isEmpty(dto.getShiharaiStartMonth())) {
					result.rejectValue(childFieldName + "shiharaiStartMonth", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				}

				// 端数の必須チェック
				if (StringUtils.isEmpty(dto.getHasu())) {
					result.rejectValue(childFieldName + "hasu", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));
				}

			} else {
				// 想定外のケース
				throw new RuntimeException();
			}

		} else {
			// 想定外のケース
			throw new RuntimeException();
		}

	}

	/**
	 * 支払い者種別によって必須項目のバリデートを検証する
	 *
	 * @param targetType
	 * @param customerId
	 * @param kanyoshaSeq
	 * @return
	 */
	private boolean targetTypeRequiredValidated(String targetType, Long customerId, Long kanyoshaSeq) {

		// 種別がNULLの判定はここでは正常とする(事前にNULLチェックをしている想定)
		if (StringUtils.isEmpty(targetType)) {
			return true;
		}

		if (TargetType.CUSTOMER.equalsByCode(targetType)) {
			// 顧客の時、入力チェックしない
			return true;

		} else if (TargetType.KANYOSHA.equalsByCode(targetType)) {
			// 関与者の時、関与者SEQは必須
			if (Objects.isNull(kanyoshaSeq)) {
				return false;
			}
		} else {
			// 想定外のケース
			throw new RuntimeException();
		}

		// ここまで来たらエラーなし
		return true;
	}

	/**
	 * 入力値のDB整合性チェック
	 *
	 * @param editForm
	 * @return
	 */
	private boolean accessDBValidated(SeisanshoEditForm editForm) {
		return service.accessDBValidated(editForm);
	}

}
