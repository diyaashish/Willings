package jp.loioz.app.user.feeDetail.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.user.feeDetail.dto.FeeDetailListDto;
import jp.loioz.app.user.feeDetail.form.FeeDetailInputForm;
import jp.loioz.app.user.feeDetail.form.FeeDetailSearchForm;
import jp.loioz.app.user.feeDetail.form.FeeDetailViewForm;
import jp.loioz.app.user.feeDetail.service.FeeDetailService;
import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.FeePaymentStatus;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.constant.CommonConstant.WithholdingFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.groups.FeeRegist;
import jp.loioz.common.validation.groups.TimeChargeRegist;

/**
 * 「報酬明細」画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/feeDetail/{personId}/{ankenId}")
@SessionAttributes(FeeDetailController.SEARCH_FORM_NAME)
public class FeeDetailController extends DefaultController {

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyFeeDetailInputForm";

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/feeDetail/feeDetail";

	/** 報酬明細の表示フラグメントviewのパス */
	private static final String FEE_DETAIL_FRAGMENT_VIEW_PATH = "user/feeDetail/feeDetailFragment::feeDetailFragment";

	/** 報酬明細の登録フラグメントinputのパス */
	private static final String FEE_DETAIL_FRAGMENT_INPUT_PATH = "user/feeDetail/feeDetailFragment::feeDetailInputRowFragment";

	/** 報酬明細の1行編集フラグメントinputのパス */
	private static final String FEE_DETAIL_FRAGMENT_EDIT_ROW_PATH = "user/feeDetail/feeDetailFragment::feeDetailEditRowFragment";

	/** 報酬明細の1行表示フラグメントinputのパス */
	private static final String FEE_DETAIL_FRAGMENT_VIEW_ROW_PATH = "user/feeDetail/feeDetailFragment::feeDetailViewRowFragment";

	/** 項目候補値のデータリストFragmentパス */
	private static final String FEE_ITEM_LIST_FRAGMENT_PATH = "user/feeDetail/feeDetailFragment::feeItemFragment";

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "feeDetailSearchForm";

	/** 表示で使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 登録で使用するフォームオブジェクト名 */
	private static final String INPUT_FORM_NAME = "inputForm";

	/** 編集で使用するフォームオブジェクト名 */
	private static final String EDIT_FORM_NAME = "editForm";

	/** 明細閉じる処理後の1行表示で使用するフォームオブジェクト名 */
	private static final String VIEW_ROW_FORM_NAME = "feeDetail";

	/** 項目の候補値データオブジェクト名 */
	private static final String FEE_ITEM_LIST_NAME = "feeItemList";

	/** 報酬明細のサービスクラス */
	@Autowired
	private FeeDetailService service;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/** バリデーター */
	private static final Validator javaxValidator = Validation.buildDefaultValidatorFactory().getValidator();

	/** Springバリデーター */
	private static final SpringValidatorAdapter validator = new SpringValidatorAdapter(javaxValidator);

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	/** フォームクラスの初期化 */
	@ModelAttribute(SEARCH_FORM_NAME)
	FeeDetailSearchForm setUpForm() {
		return new FeeDetailSearchForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 報酬明細画面初期表示
	 * 
	 * @param personId
	 * @param ankenId
	 * @param searchForm
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(
			@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId,
			@ModelAttribute(SEARCH_FORM_NAME) FeeDetailSearchForm searchForm) {

		ModelAndView mv = null;

		// 検索条件、ソート条件を初期化する
		searchForm.initForm();
		searchForm.setAnkenId(ankenId);
		searchForm.setPersonId(personId);

		// 案件ID、名簿IDに紐づく報酬明細情報を取得する
		FeeDetailViewForm viewForm = service.createViewForm();
		// ヘッダー情報
		service.getAccgData(viewForm, searchForm);
		// 一覧情報
		service.searchFeeDetail(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 報酬明細一覧ソート処理
	 * 
	 * @param personId
	 * @param ankenId
	 * @param searchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/sort", method = RequestMethod.GET)
	public ModelAndView sort(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId,
			@ModelAttribute(SEARCH_FORM_NAME) FeeDetailSearchForm searchForm, HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, searchForm)) {
			return null;
		}

		// 報酬明細表示用フォームを作成する
		FeeDetailViewForm viewForm = service.createViewForm();
		// ヘッダー情報
		service.getAccgData(viewForm, searchForm);
		// 一覧情報
		service.searchFeeDetail(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, FEE_DETAIL_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 報酬入力エリア表示処理
	 * 
	 * @param personId
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getFeeDetailInputRow", method = RequestMethod.GET)
	public ModelAndView getFeeDetailInputRow(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId) {

		ModelAndView mv = null;

		// 報酬明細入力用フォームを作成する
		FeeDetailInputForm inputForm = service.createInputForm();

		// ステータスの選択リストをセット
		inputForm.setFeePaymentStatusList(FeePaymentStatus.getSelectOptions());
		// 消費税：初期値は「10%」
		inputForm.setTaxRateType(TaxRate.TEN_PERCENT.getCd());
		// 源泉徴収税：初期値は「なし」
		inputForm.setWithholdingFlg(WithholdingFlg.DO_NOT.getCd());
		mv = getMyModelAndView(inputForm, FEE_DETAIL_FRAGMENT_INPUT_PATH, INPUT_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * タイムチャージの報酬額計算
	 * 
	 * @param personId 名簿ID
	 * @param ankenId 案件ID
	 * @param timeChargeTanka 単価
	 * @param timeChargeTime 時間（分）
	 * @param feeSeq 選択中の報酬SEQ（新規登録時は空）
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/calculateTimeCharge", method = RequestMethod.GET)
	public Map<String, Object> calculateTimeCharge(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId, @RequestParam("timeChargeTanka") String timeChargeTanka,
			@RequestParam("timeChargeTime") String timeChargeTime, @RequestParam("feeSeq") String feeSeq) {

		Map<String, Object> response = new HashMap<>();
		response.put("feeSeq", feeSeq);
		String feeAmountStr = "";

		try {
			if (!StringUtils.isExsistEmpty(timeChargeTanka, timeChargeTime)) {
				// タイムチャージ額の計算
				BigDecimal tanka = new BigDecimal(timeChargeTanka);
				BigDecimal decimalMinutes = new BigDecimal(timeChargeTime);
				BigDecimal feeAmount = service.calculateTimeCharge(tanka, decimalMinutes);
				if (feeAmount != null) {
					feeAmountStr = feeAmount.toPlainString();
				}
			}
		} catch (AppException e) {
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		} catch (Exception ex) {
			// 処理失敗
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00091));
			return response;
		}

		response.put("succeeded", true);
		response.put("feeAmount", feeAmountStr);
		return response;
	}

	/**
	 * 報酬登録処理
	 * 
	 * @param personId
	 * @param ankenId
	 * @param feeDetailInputForm
	 * @param result
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/registFee", method = RequestMethod.POST)
	public ModelAndView registFee(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId,
			@Validated({FeeRegist.class}) @ModelAttribute(EMPTY_INPUT_FORM_NAME) FeeDetailInputForm feeDetailInputForm,
			BindingResult result, @ModelAttribute(SEARCH_FORM_NAME) FeeDetailSearchForm searchForm) {

		ModelAndView mv = null;
		
		try {
			// 相関バリデーションチェック
			service.validateFeeDetailInputForm(feeDetailInputForm, result);
			
			// バリデーションチェック、プルダウン選択値存在チェック
			if (result.hasErrors() || checkExistEnum(feeDetailInputForm, result)) {
				// バリデーションエラーが存在する場合
				// ステータスの選択リストをセット
				feeDetailInputForm.setFeePaymentStatusList(FeePaymentStatus.getSelectOptions());
				// 処理失敗とする
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(feeDetailInputForm, FEE_DETAIL_FRAGMENT_INPUT_PATH, INPUT_FORM_NAME, result);
			}
			
			// 報酬データ登録処理
			service.registFee(feeDetailInputForm, searchForm);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		FeeDetailViewForm viewForm = service.createViewForm();
		// ヘッダー情報
		service.getAccgData(viewForm, searchForm);
		// 一覧情報
		service.searchFeeDetail(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, FEE_DETAIL_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "報酬"));
		return mv;
	}

	/**
	 * タイムチャージ報酬登録処理<br>
	 * （報酬額の算出イベントより先に登録処理が実行されてしまう場合に feeDetailInputForm の報酬額が
	 * 正しくないため単価と時間から報酬額を算出しFormにセットしてからバリデーションを実行）
	 * 
	 * @param personId
	 * @param ankenId
	 * @param feeDetailInputForm
	 * @param result
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/registFeeTimeCharge", method = RequestMethod.POST)
	public ModelAndView registFeeTimeCharge(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) FeeDetailInputForm feeDetailInputForm,
			BindingResult result, @ModelAttribute(SEARCH_FORM_NAME) FeeDetailSearchForm searchForm) {

		ModelAndView mv = null;
		
		try {
			// 相関バリデーションチェック
			service.validateFeeDetailInputForm(feeDetailInputForm, result);
			
			// プルダウン選択値存在チェック
			if (checkExistEnum(feeDetailInputForm, result)) {
				// バリデーションエラーが存在する場合
				// ステータスの選択リストをセット
				feeDetailInputForm.setFeePaymentStatusList(FeePaymentStatus.getSelectOptions());
				// 処理失敗とする
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(feeDetailInputForm, FEE_DETAIL_FRAGMENT_INPUT_PATH, INPUT_FORM_NAME, result);
			}
			
			// 単価と時間から報酬額を算出しinputFormにセットする
			this.calculateTimeCharge(feeDetailInputForm);

			// バリデーションを実行
			validator.validate(feeDetailInputForm, result, TimeChargeRegist.class);
			if (result.hasErrors()) {
				// ステータスの選択リストをセット
				feeDetailInputForm.setFeePaymentStatusList(FeePaymentStatus.getSelectOptions());
				// 処理失敗とする
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(feeDetailInputForm, FEE_DETAIL_FRAGMENT_INPUT_PATH, INPUT_FORM_NAME, result);
			}

			// 報酬データ登録処理
			service.registFee(feeDetailInputForm, searchForm);

			// タイムチャージ設定登録処理
			service.registTimeChargeSetting(feeDetailInputForm);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00012));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		FeeDetailViewForm viewForm = service.createViewForm();
		// ヘッダー情報
		service.getAccgData(viewForm, searchForm);
		// 一覧情報
		service.searchFeeDetail(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, FEE_DETAIL_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "報酬"));
		return mv;
	}

	/**
	 * 報酬変更処理
	 * 
	 * @param personId
	 * @param ankenId
	 * @param feeDetailInputForm
	 * @param result
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/editFee", method = RequestMethod.POST)
	public ModelAndView editFee(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId,
			@Validated({FeeRegist.class}) @ModelAttribute(EMPTY_INPUT_FORM_NAME) FeeDetailInputForm feeDetailInputForm,
			BindingResult result, @ModelAttribute(SEARCH_FORM_NAME) FeeDetailSearchForm searchForm) {

		ModelAndView mv = null;
		
		try {
			// 相関バリデーションチェック
			service.validateFeeDetailInputForm(feeDetailInputForm, result);
			
			// バリデーションチェック、プルダウン選択値存在チェック
			if (result.hasErrors() || checkExistEnum(feeDetailInputForm, result)) {
				// バリデーションエラーが存在する場合
				// ステータスの選択リストをセット
				feeDetailInputForm.setFeePaymentStatusList(FeePaymentStatus.getSelectOptions(feeDetailInputForm.getFeePaymentStatus()));
				// 処理失敗とする
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				mv = getMyModelAndViewWithErrors(feeDetailInputForm, FEE_DETAIL_FRAGMENT_EDIT_ROW_PATH, EDIT_FORM_NAME, result);
				mv.addObject("feeSeq", feeDetailInputForm.getFeeSeq());
				return mv;
			}
			
			// 報酬データ変更処理
			service.editFee(feeDetailInputForm);

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00012));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		FeeDetailViewForm viewForm = service.createViewForm();
		// ヘッダー情報
		service.getAccgData(viewForm, searchForm);
		// 一覧情報
		service.searchFeeDetail(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, FEE_DETAIL_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "報酬"));
		return mv;
	}

	/**
	 * タイムチャージ報酬変更処理<br>
	 * （報酬額の算出イベントより先に登録処理が実行されてしまう場合に feeDetailInputForm の報酬額が
	 * 正しくないため単価と時間から報酬額を算出しFormにセットしてからバリデーションを実行）
	 * 
	 * @param personId
	 * @param ankenId
	 * @param feeDetailInputForm
	 * @param result
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/editFeeTimeCharge", method = RequestMethod.POST)
	public ModelAndView editFeeTimeCharge(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) FeeDetailInputForm feeDetailInputForm,
			BindingResult result, @ModelAttribute(SEARCH_FORM_NAME) FeeDetailSearchForm searchForm) {

		ModelAndView mv = null;
		
		try {
			// 相関バリデーションチェック
			service.validateFeeDetailInputForm(feeDetailInputForm, result);
			
			// プルダウン選択値存在チェック
			if (checkExistEnum(feeDetailInputForm, result)) {
				// バリデーションエラーが存在する場合
				// ステータスの選択リストをセット
				feeDetailInputForm.setFeePaymentStatusList(FeePaymentStatus.getSelectOptions(feeDetailInputForm.getFeePaymentStatus()));
				// 処理失敗とする
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				mv = getMyModelAndViewWithErrors(feeDetailInputForm, FEE_DETAIL_FRAGMENT_EDIT_ROW_PATH, EDIT_FORM_NAME, result);
				mv.addObject("feeSeq", feeDetailInputForm.getFeeSeq());
				return mv;
			}
			
			// 単価と時間から報酬額を算出しinputFormにセットする
			this.calculateTimeCharge(feeDetailInputForm);

			// バリデーションを実行
			validator.validate(feeDetailInputForm, result, TimeChargeRegist.class);

			if (result.hasErrors()) {
				// バリデーションエラーが存在する場合
				// ステータスの選択リストをセット
				feeDetailInputForm.setFeePaymentStatusList(FeePaymentStatus.getSelectOptions(feeDetailInputForm.getFeePaymentStatus()));
				// 処理失敗とする
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				mv = getMyModelAndViewWithErrors(feeDetailInputForm, FEE_DETAIL_FRAGMENT_EDIT_ROW_PATH, EDIT_FORM_NAME, result);
				mv.addObject("feeSeq", feeDetailInputForm.getFeeSeq());
				return mv;
			}

			// 報酬データ変更処理
			service.editFeeTimeCharge(feeDetailInputForm);

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00012));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		FeeDetailViewForm viewForm = service.createViewForm();
		// ヘッダー情報
		service.getAccgData(viewForm, searchForm);
		// 一覧情報
		service.searchFeeDetail(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, FEE_DETAIL_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "報酬"));
		return mv;
	}

	/**
	 * 報酬編集用フォーム表示
	 * 
	 * @param personId
	 * @param ankenId
	 * @param feeSeq
	 * @param isChecked
	 * @return
	 */
	@RequestMapping(value = "/openEditFeeFragment", method = RequestMethod.GET)
	public ModelAndView openEditFeeFragment(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId, @RequestParam("feeSeq") Long feeSeq,
			@RequestParam("isChecked") boolean isChecked) {
		// 返却
		ModelAndView mv = null;

		FeeDetailInputForm inputForm = service.createInputForm();

		// 画面の行チェックボックスチェック状態をセット
		inputForm.setChecked(isChecked);
		try {
			service.setFee(feeSeq, inputForm);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		mv = getMyModelAndView(inputForm, FEE_DETAIL_FRAGMENT_EDIT_ROW_PATH, EDIT_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 報酬編集用フォーム閉じる処理
	 * 
	 * @param personId
	 * @param ankenId
	 * @param feeSeq
	 * @param isChecked
	 * @return
	 */
	@RequestMapping(value = "/closeEditFeeFragment", method = RequestMethod.GET)
	public ModelAndView closeEditFeeFragment(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId, @RequestParam("feeSeq") Long feeSeq,
			@RequestParam("isChecked") boolean isChecked) {

		ModelAndView mv = null;

		FeeDetailListDto dto = new FeeDetailListDto();
		try {
			dto = service.getFee(feeSeq);
			dto.setChecked(isChecked);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		mv = getMyModelAndView(dto, FEE_DETAIL_FRAGMENT_VIEW_ROW_PATH, VIEW_ROW_FORM_NAME);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		mv.addObject("feeSeq", feeSeq);
		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 報酬の削除前チェック処理<br>
	 * 紐づく請求書、精算書が発行ステータスが下書きか確認します。<br>
	 * 
	 * @param personId
	 * @param ankenId
	 * @param feeSeq
	 * @param searchForm
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/checkOfBeforeDeleteFeeFunc", method = RequestMethod.GET)
	public Map<String, Object> checkOfBeforeDeleteFeeFunc(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId, @RequestParam("feeSeq") Long feeSeq,
			@ModelAttribute(SEARCH_FORM_NAME) FeeDetailSearchForm searchForm) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 紐づく請求書、精算書の発行ステータスチェック
			Map<String, Object> checkResult = service.checkIssueStatusIssued(feeSeq);
			boolean isIssued = (boolean) checkResult.get("isIssued");
			if (isIssued) {
				AccgDocType docType = (AccgDocType) checkResult.get("accgDocType");
				response.put("successed", false);
				response.put("message", getMessage(MessageEnum.MSG_W00016, docType.getVal(), "削除"));
				return response;
			}
			response.put("successed", true);
			return response;
		} catch (AppException e) {
			response.put("successed", false);
			response.put("message", getMessage(e.getErrorType(), e.getMessageArgs()));
			return response;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			response.put("successed", false);
			response.put("message", getMessage(MessageEnum.MSG_E00025));
			return response;
		}
	}

	/**
	 * 報酬削除処理
	 * 
	 * @param personId
	 * @param ankenId
	 * @param feeSeq
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/deleteFee", method = RequestMethod.POST)
	public ModelAndView deleteFee(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId, @RequestParam("feeSeq") Long feeSeq,
			@ModelAttribute(SEARCH_FORM_NAME) FeeDetailSearchForm searchForm) {

		ModelAndView mv = null;

		try {
			// 報酬に関するデータの削除
			service.deleteFeeAndUpdateTax(feeSeq);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		FeeDetailViewForm viewForm = service.createViewForm();
		// ヘッダー情報
		service.getAccgData(viewForm, searchForm);
		// 一覧情報
		service.searchFeeDetail(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, FEE_DETAIL_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "報酬"));
		return mv;
	}

	/**
	 * 項目の候補を取得
	 * 
	 * @param personId
	 * @param ankenId
	 * @param searchWord
	 * @return
	 */
	@RequestMapping(value = "/searchFeeDataList", method = RequestMethod.GET)
	public ModelAndView searchFeeDataList(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId, @RequestParam("searchWord") String searchWord) {

		ModelAndView mv = null;

		List<SelectOptionForm> feeItemList = service.searchFeeDataList(searchWord);
		mv = getMyModelAndView(feeItemList, FEE_ITEM_LIST_FRAGMENT_PATH, FEE_ITEM_LIST_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * feeDetailInputFormの単価と時間から報酬額を算出しfeeDetailInputFormにセットします。
	 * 
	 * @param feeDetailInputForm
	 * @throws AppException
	 */
	private void calculateTimeCharge(FeeDetailInputForm feeDetailInputForm) throws AppException {
		if (!StringUtils.isExsistEmpty(feeDetailInputForm.getTimeChargeTanka(), feeDetailInputForm.getTimeChargeTime())) {
			// タイムチャージ額の計算
			BigDecimal tanka = new BigDecimal(feeDetailInputForm.getTimeChargeTanka());
			BigDecimal decimalMinutes = new BigDecimal(feeDetailInputForm.getTimeChargeTime());
			BigDecimal feeAmount = service.calculateTimeCharge(tanka, decimalMinutes);
			if (feeAmount != null) {
				feeDetailInputForm.setFeeAmount(feeAmount.toPlainString());
			} else {
				feeDetailInputForm.setFeeAmount("");
			}
		} else {
			feeDetailInputForm.setFeeAmount("");
		}
	}

	/**
	 * 報酬登録項目「ステータス」、「消費税」、「源泉徴収税」の選択値がEnumに定義されている値か確認します。<br>
	 * Enumに定義されていない値だった場合はエラーとして true を返します。
	 * 
	 * @param feeDetailInputForm
	 * @param result
	 * @return
	 */
	private boolean checkExistEnum(FeeDetailInputForm feeDetailInputForm, BindingResult result) {

		boolean errFlg = false;

		// ステータス
		if (!FeePaymentStatus.isExist(feeDetailInputForm.getFeePaymentStatus())) {
			errFlg = true;
			result.rejectValue("feePaymentStatus", null, getMessage(MessageEnum.VARIDATE_MSG_E00001));
		}

		// 消費税
		if (!TaxFlg.isExist(feeDetailInputForm.getTaxFlg())) {
			errFlg = true;
			result.rejectValue("taxFlg", null, getMessage(MessageEnum.VARIDATE_MSG_E00001));
		}
		if (!TaxRate.isExist(feeDetailInputForm.getTaxRateType())) {
			errFlg = true;
			result.rejectValue("taxRate", null, getMessage(MessageEnum.VARIDATE_MSG_E00001));
		}

		// 源泉徴収税
		if (!WithholdingFlg.isExist(feeDetailInputForm.getWithholdingFlg())) {
			errFlg = true;
			result.rejectValue("withholdingFlg", null, getMessage(MessageEnum.VARIDATE_MSG_E00001));
		}

		return errFlg;
	}

	/**
	 * セッション情報が有るか確認する。<br>
	 * 
	 * <pre>
	 * 複数タブで操作している場合、いずれかのタブで再ログインをし、Session（ブラウザが保持するCookie）が変わった場合、
	 * 開いていたタスク画面で再操作をしようとすると、Sessionが変わっているため、SearchFormの中の値がNULLになっている状態が発生する。
	 * そのまま後続処理を行うとNullPointerExceptionなどエラーが発生するため、Sessionが変わった（SearchFormの中の値がNULL）などの場合は
	 * 後続処理を行わないようにする。
	 * 
	 * POSTリクエストの場合、上記のSessionが変わった状態でリクエストすると、
	 * フレームワーク側のCSRFトークンチェックでNGとなり、Controllerの処理までこないため、ここではPOSTの処理は考慮しない。
	 * </pre>
	 * 
	 * @param httpMethod
	 * @param searchForm
	 * @return
	 */
	private boolean existSession(HttpMethod httpMethod, FeeDetailSearchForm searchForm) {

		// POSTの場合はセッション有無を確認しない
		if (httpMethod == null || httpMethod == HttpMethod.POST) {
			return true;
		}

		// セッションにフォームが有るか確認する
		if (searchForm == null || searchForm.getFeeDetailSortItem() == null) {
			super.setAjaxProcResultFailure(MessageEnum.MSG_E00161.getMessageKey());
			return false;
		} else {
			return true;
		}
	}

}
