package jp.loioz.app.user.recordDetail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.user.recordDetail.form.RecordDetailInputForm;
import jp.loioz.app.user.recordDetail.form.RecordDetailViewForm;
import jp.loioz.app.user.recordDetail.service.RecordDetailService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

/**
 * 取引実績詳細画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/recordDetail/{accgRecordSeq}")
public class RecordDetailController extends DefaultController {

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyRecordDetailInputForm";

	/** 取引実績詳細 プリチェックリクエストに対するのレスポンス 会計書類（請求書・精算書）設定値 */
	public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_ACCG_DETAIL_SAVE_OVER_PAYMENT_PRE_CHECK_RESULT = "success_over_payment_pre_check";

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/recordDetail/recordDetail";

	/** 取引実績詳細：会計書類（請求書・精算書）フラグメントviewのパス */
	private static final String RECORD_DETAIL_HEADER_FRAGMENT_VIEW_PATH = "user/recordDetail/recordDetailFragment::recordDetailHeaderViewFargment";

	/** 取引実績詳細：報酬、預り金／実費内訳フラグメントviewのパス */
	private static final String BREAKDOWN_OF_FEE_AND_DEPOSIT_FRAGMENT_VIEW_PATH = "user/recordDetail/recordDetailFragment::breakdownOfFeeAndDepositViewFragment";

	/** 取引実績詳細：一覧フラグメントviewのパス */
	private static final String RECORD_DETAIL_LIST_FRAGMENT_VIEW_PATH = "user/recordDetail/recordDetailFragment::recordDetailListViewFragment";

	/** 取引実績詳細：支払計画一覧フラグメントViewのパス */
	private static final String PAYMENT_PLAN_LIST_FRAGMENT_VIEW_PATH = "user/recordDetail/recordDetailFragment::paymentPlanListViewFragment";

	/** 取引実績詳細：行登録フラグメントパス */
	private static final String RECORD_DETAIL_ROW_INPUT_FRAGMENT_PATH = "user/recordDetail/recordDetailFragment::recordDetailRowInputFragment";

	/** 取引実績詳細：過入金返金行登録フラグメントパス */
	private static final String RECORD_DETAIL_OVER_PAYMENT_REFUND_ROW_INPUT_FRAGMENT_PATH = "user/recordDetail/recordDetailFragment::recordDetailOverPaymentRefundRowInputFragment";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 取引実績詳細：会計書類（請求書・精算書）画面表示用オブジェクト */
	private static final String RECORD_DETAIL_HEADER_VIEW_FORM_NAME = "recordDetailAccgDocViewForm";

	/** 取引実績詳細：報酬、預り金／実費内訳フラグメント表示用オブジェクト名 */
	private static final String BREAKDOWN_OF_FEE_AND_DEPOSIT_VIEW_FORM_NAME = "breakdownOfFeeAndDepositViewForm";

	/** 取引実績詳細：一覧フラグメントviewのパス */
	private static final String RECORD_DETAIL_LIST_VIEW_FORM_NAME = "recordListViewForm";

	/** 取引実績詳細：取引実績一覧画面表示用オブジェクト名 */
	private static final String PAYMENT_PLAN_LIST_VIEW_FORM_NAME = "paymentPlanListViewForm";

	/** 取引実績詳細：行登録フォームオブジェクト名 */
	private static final String RECORD_DETAIL_ROW_INPUT_FORM_NAME = "recordDetailRowInputForm";

	/** 取引実績詳細：加入金返金行登録フォームオブジェクト名 */
	private static final String RECORD_DETAIL_OVER_PAYMENT_REFUND_ROW_INPUT_FORM_NAME = "recordOverpayRefundRowInputForm";

	/** 取引実績詳細のサービスクラス */
	@Autowired
	private RecordDetailService recordDetailService;

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 取引実績詳細画面初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable Long accgRecordSeq) {

		try {
			// 画面表示情報を作成
			RecordDetailViewForm viewForm = recordDetailService.createRecordDetailViewForm(accgRecordSeq);
			return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 取引実績画面：会計書類（請求書・精算書）画面情報の取得
	 * 
	 * @param accgRecordSeq
	 * @return
	 */
	@RequestMapping(value = "/getRecordDetailAccgDocViewFargment", method = RequestMethod.GET)
	public ModelAndView getRecordDetailAccgDocViewFargment(@PathVariable Long accgRecordSeq) {

		ModelAndView mv = null;

		try {
			// 画面表示情報を作成
			RecordDetailViewForm.RecordDetailAccgDocViewForm viewForm = recordDetailService.createRecordDetailAccgDocViewForm(accgRecordSeq);
			mv = getMyModelAndView(viewForm, RECORD_DETAIL_HEADER_FRAGMENT_VIEW_PATH, RECORD_DETAIL_HEADER_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// DataNotFoundExceptionはAjax取得ではシステムエラーになってしまうので、楽観ロックエラーとする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 取引実績画面：報酬、預り金／実費内訳画面情報取得
	 * 
	 * @param accgRecordSeq
	 * @return
	 */
	@RequestMapping(value = "/getBreakdownOfFeeAndDepositViewFragment", method = RequestMethod.GET)
	public ModelAndView getBreakdownOfFeeAndDepositViewFragment(@PathVariable Long accgRecordSeq) {

		ModelAndView mv = null;

		try {
			// 画面表示情報を作成
			RecordDetailViewForm.BreakdownOfFeeAndDepositViewForm viewForm = recordDetailService.createBreakdownOfFeeAndDepositViewForm(accgRecordSeq);
			mv = getMyModelAndView(viewForm, BREAKDOWN_OF_FEE_AND_DEPOSIT_FRAGMENT_VIEW_PATH, BREAKDOWN_OF_FEE_AND_DEPOSIT_VIEW_FORM_NAME);
		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 取引実績画面：一覧画面情報の取得
	 * 
	 * @param accgRecordSeq
	 * @return
	 */
	@RequestMapping(value = "/getRecordDetailListViewFragment", method = RequestMethod.GET)
	public ModelAndView getRecordDetailListViewFragment(@PathVariable Long accgRecordSeq) {

		ModelAndView mv = null;

		try {
			// 画面表示情報を作成
			RecordDetailViewForm.RecordListViewForm viewForm = recordDetailService.createRecordListViewForm(accgRecordSeq);
			mv = getMyModelAndView(viewForm, RECORD_DETAIL_LIST_FRAGMENT_VIEW_PATH, RECORD_DETAIL_LIST_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// DataNotFoundExceptionはAjax取得ではシステムエラーになってしまうので、楽観ロックエラーとする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 取引実績画面：支払計画一覧画面情報の取得
	 * 
	 * @param accgRecordSeq
	 * @return
	 */
	@RequestMapping(value = "/getPaymentPlanListViewFragment", method = RequestMethod.GET)
	public ModelAndView getPaymentPlanListViewFragment(@PathVariable Long accgRecordSeq) {

		ModelAndView mv = null;

		try {
			// 画面表示情報を作成
			RecordDetailViewForm.PaymentPlanListViewForm viewForm = recordDetailService.createPaymentPlanListViewForm(accgRecordSeq);
			mv = getMyModelAndView(viewForm, PAYMENT_PLAN_LIST_FRAGMENT_VIEW_PATH, PAYMENT_PLAN_LIST_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// DataNotFoundExceptionはAjax取得ではシステムエラーになってしまうので、楽観ロックエラーとする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 取引実績画面：取引実積登録用表示情報を取得する
	 * 
	 * @param accgRecordSeq
	 * @param accgDocSeq
	 * @return
	 */
	@RequestMapping(value = "/createRecordDetail", method = RequestMethod.GET)
	public ModelAndView createRecordDetail(@PathVariable Long accgRecordSeq, @RequestParam Long accgDocSeq) {

		ModelAndView mv = null;

		try {
			// 画面表示情報を作成
			RecordDetailInputForm.RecordDetailRowInputForm inputForm = recordDetailService.createRecordDetailRowInputForm(accgRecordSeq, accgDocSeq);
			mv = getMyModelAndView(inputForm, RECORD_DETAIL_ROW_INPUT_FRAGMENT_PATH, RECORD_DETAIL_ROW_INPUT_FORM_NAME);

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 取引実績画面：取引実積-編集用表示情報を取得する
	 * 
	 * @param accgRecordSeq
	 * @param accgDocSeq
	 * @param accgRecordDetailSeq
	 * @return
	 */
	@RequestMapping(value = "/editRecordDetail", method = RequestMethod.GET)
	public ModelAndView editRecordDetail(@PathVariable Long accgRecordSeq, @RequestParam Long accgDocSeq, @RequestParam Long accgRecordDetailSeq) {

		ModelAndView mv = null;

		try {
			// 画面表示情報を作成
			RecordDetailInputForm.RecordDetailRowInputForm inputForm = recordDetailService.createRecordDetailRowInputForm(accgRecordSeq, accgDocSeq, accgRecordDetailSeq);
			mv = getMyModelAndView(inputForm, RECORD_DETAIL_ROW_INPUT_FRAGMENT_PATH, RECORD_DETAIL_ROW_INPUT_FORM_NAME);

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 保存処理前、過入金発生確認プリチェック
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/savePreCheckOverPayment", method = RequestMethod.POST)
	public ModelAndView savePreCheckOverPayment(@Validated RecordDetailInputForm.RecordDetailRowInputForm inputForm, BindingResult result) {

		try {
			// 相関バリデート
			recordDetailService.saveRecordDetailValidate(inputForm, result);

			// 入力チェック
			if (result.hasErrors()) {
				recordDetailService.setDispProperties(inputForm);
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				return getMyModelAndViewWithErrors(inputForm, RECORD_DETAIL_ROW_INPUT_FRAGMENT_PATH, RECORD_DETAIL_ROW_INPUT_FORM_NAME, result);
			}

			// 保存処理によって、過入金が発生するかどうか
			boolean isOccurOverPayment = recordDetailService.isOccurOverPayment(inputForm);

			super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_ACCG_DETAIL_SAVE_OVER_PAYMENT_PRE_CHECK_RESULT, String.valueOf(isOccurOverPayment));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 取引実績画面：新規登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registRecordDetail", method = RequestMethod.POST)
	public ModelAndView registRecordDetail(@Validated RecordDetailInputForm.RecordDetailRowInputForm inputForm, BindingResult result) {

		try {
			// 相関バリデート
			recordDetailService.saveRecordDetailValidate(inputForm, result);

			// 入力チェック
			if (result.hasErrors()) {
				recordDetailService.setDispProperties(inputForm);
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				return getMyModelAndView(inputForm, RECORD_DETAIL_ROW_INPUT_FRAGMENT_PATH, RECORD_DETAIL_ROW_INPUT_FORM_NAME);
			}

			// 登録処理
			recordDetailService.registRecordDetail(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "取引実績"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 取引実績画面：更新処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/updateRecordDetail", method = RequestMethod.POST)
	public ModelAndView updateRecordDetail(@Validated RecordDetailInputForm.RecordDetailRowInputForm inputForm, BindingResult result) {

		try {
			// 相関バリデート
			recordDetailService.saveRecordDetailValidate(inputForm, result);

			// 入力チェック
			if (result.hasErrors()) {
				recordDetailService.setDispProperties(inputForm);
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				return getMyModelAndView(inputForm, RECORD_DETAIL_ROW_INPUT_FRAGMENT_PATH, RECORD_DETAIL_ROW_INPUT_FORM_NAME);
			}

			// 更新処理
			recordDetailService.updateRecordDetail(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "取引実績"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 取引実績画面：削除処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/deleteRecordDetail", method = RequestMethod.POST)
	public ModelAndView deleteRecordDetail(@PathVariable Long accgRecordSeq, @RequestParam(name = "accgRecordDetailSeq") Long accgRecordDetailSeq) {

		try {
			// 削除処理
			recordDetailService.deleteRecordDetail(accgRecordSeq, accgRecordDetailSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "取引実績"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 取引実績画面：過入金返金の登録用画面情報を取得する
	 *
	 * @param accgRecordSeq
	 * @param accgDocSeq
	 * @return
	 */
	@RequestMapping(value = "/createRecordOverPaymentRefund", method = RequestMethod.GET)
	public ModelAndView createRecordOverPaymentRefund(@PathVariable Long accgRecordSeq, @RequestParam Long accgDocSeq) {

		ModelAndView mv = null;

		try {
			// 画面表示情報を作成
			RecordDetailInputForm.RecordOverpayRefundRowInputForm inputForm = recordDetailService.createRecordOverpayRefundRowInputForm(accgDocSeq, accgRecordSeq);
			mv = getMyModelAndView(inputForm, RECORD_DETAIL_OVER_PAYMENT_REFUND_ROW_INPUT_FRAGMENT_PATH, RECORD_DETAIL_OVER_PAYMENT_REFUND_ROW_INPUT_FORM_NAME);

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 取引実績画面：過入金返金の編集用画面情報を取得する
	 * 
	 * @param accgRecordSeq
	 * @param accgDocSeq
	 * @param accgRecordDetailSeq
	 * @return
	 */
	@RequestMapping(value = "/editRecordOverPaymentRefund", method = RequestMethod.GET)
	public ModelAndView editRecordOverPaymentRefund(@PathVariable Long accgRecordSeq, @RequestParam Long accgDocSeq, @RequestParam Long accgRecordDetailSeq) {

		ModelAndView mv = null;

		try {
			// 画面表示情報を作成
			RecordDetailInputForm.RecordOverpayRefundRowInputForm inputForm = recordDetailService.createRecordOverpayRefundRowInputForm(accgDocSeq, accgRecordSeq, accgRecordDetailSeq);
			mv = getMyModelAndView(inputForm, RECORD_DETAIL_OVER_PAYMENT_REFUND_ROW_INPUT_FRAGMENT_PATH, RECORD_DETAIL_OVER_PAYMENT_REFUND_ROW_INPUT_FORM_NAME);

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 取引実績画面：過入金返金の登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registRecordDetailRefund", method = RequestMethod.POST)
	public ModelAndView registRecordDetailRefund(@Validated RecordDetailInputForm.RecordOverpayRefundRowInputForm inputForm, BindingResult result) {

		try {
			// 入力チェック
			if (result.hasErrors()) {
				recordDetailService.setDispProperties(inputForm);
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				return getMyModelAndView(inputForm, RECORD_DETAIL_OVER_PAYMENT_REFUND_ROW_INPUT_FRAGMENT_PATH, RECORD_DETAIL_OVER_PAYMENT_REFUND_ROW_INPUT_FORM_NAME);
			}

			// 登録処理
			recordDetailService.registRecordDetailRefund(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "過入金返金"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 取引実績画面：過入金返金の更新処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/updateRecordDetailRefund", method = RequestMethod.POST)
	public ModelAndView updateRecordDetailRefund(@Validated RecordDetailInputForm.RecordOverpayRefundRowInputForm inputForm, BindingResult result) {

		try {
			// 入力チェック
			if (result.hasErrors()) {
				recordDetailService.setDispProperties(inputForm);
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				return getMyModelAndView(inputForm, RECORD_DETAIL_OVER_PAYMENT_REFUND_ROW_INPUT_FRAGMENT_PATH, RECORD_DETAIL_OVER_PAYMENT_REFUND_ROW_INPUT_FORM_NAME);
			}

			// 登録処理
			recordDetailService.updateRecordDetailRefund(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "過入金返金"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 取引実績画面：過入金返金の取り下げ
	 *
	 * @param accgRecordSeq
	 * @param accgDocSeq
	 * @return
	 */
	@RequestMapping(value = "/dropRefund", method = RequestMethod.POST)
	public ModelAndView dropRefund(@PathVariable Long accgRecordSeq, @RequestParam Long accgDocSeq) {

		try {
			// 過入金返金の取り下げ
			recordDetailService.dropRefund(accgDocSeq, accgRecordSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "過入金返金"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 取引実績画面：「回収不能にする」
	 * 
	 * @param accgRecordSeq
	 * @param accgDocSeq
	 * @return
	 */
	@RequestMapping(value = "/changeToUncollectible", method = RequestMethod.POST)
	public ModelAndView changeToUncollectible(@PathVariable Long accgRecordSeq, @RequestParam Long accgDocSeq) {

		try {
			// 回収不能に変更
			recordDetailService.changeToUncollectible(accgDocSeq, accgRecordSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00045, "回収不能に"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 取引実績画面：「回収不能を解除する」
	 * 
	 * @param accgRecordSeq
	 * @param accgDocSeq
	 * @return
	 */
	@RequestMapping(value = "/undoFromUncollectible", method = RequestMethod.POST)
	public ModelAndView undoFromUncollectible(@PathVariable Long accgRecordSeq, @RequestParam Long accgDocSeq) {

		try {
			// 回収不能を解除
			recordDetailService.undoFromUncollectible(accgDocSeq, accgRecordSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00045, "回収不能を解除"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
