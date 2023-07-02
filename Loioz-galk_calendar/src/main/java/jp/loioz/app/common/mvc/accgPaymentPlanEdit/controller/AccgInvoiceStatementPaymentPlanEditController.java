package jp.loioz.app.common.mvc.accgPaymentPlanEdit.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.mvc.accgPaymentPlanEdit.form.AccgInvoiceStatementPaymentPlanEditForm;
import jp.loioz.app.common.mvc.accgPaymentPlanEdit.service.AccgInvoiceStatementPaymentPlanEditCommonService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.utility.SessionUtils;

/**
 * 支払計画編集モーダルコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "common/mvc/accgInvoiceStatementPaymentPlanEdit")
public class AccgInvoiceStatementPaymentPlanEditController extends DefaultController {

	/** 支払計画編集モーダルのコントローラーパス */
	private static final String ACCG_INVOICE_STATEMENT_PAYMENT_PLAN_EDIT_MODAL_PATH = "common/mvc/accgPaymentPlanEdit/accgInvoiceStatementPaymentPlanEditModal";

	/** 支払計画編集モーダルのフラグメントパス */
	private static final String ACCG_INVOICE_STATEMENT_PAYMENT_PLAN_EDIT_MODAL_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_PAYMENT_PLAN_EDIT_MODAL_PATH + "::shiharaiPlanEditFragment";

	/** 支払計画編集モーダルの支払計画追加フラグメントパス */
	private static final String ACCG_INVOICE_STATEMENT_PAYMENT_PLAN_PAYMENT_ADD_ROW_FRAGMENT_PATH = ACCG_INVOICE_STATEMENT_PAYMENT_PLAN_EDIT_MODAL_PATH + "::paymentPlanAddRowFragment";

	/** 支払計画編集モーダルのフラグメントフォームオブジェクト名 */
	private static final String ACCG_INVOICE_STATEMENT_PAYMENT_PLAN_EDIT_MODAL_FORM_NAME = "editForm";

	/** 支払計画編集モーダルの行追加フラグメントフォームオブジェクト名 */
	private static final String ACCG_INVOICE_STATEMENT_PAYMENT_PLAN_PAYMENT_ADD_ROW_FORM_NAME = "paymentPlanRowInputForm";

	/** 支払計画編集のサービスクラス */
	@Autowired
	private AccgInvoiceStatementPaymentPlanEditCommonService service;

	/**
	 * 支払計画編集モーダルを開く
	 * 
	 * @param invoiceSeq
	 */
	@RequestMapping(value = "/createAccgInvoiceStatementPaymentPlanEditModal", method = RequestMethod.GET)
	public ModelAndView createAccgInvoiceStatementPaymentPlanEditModal(@RequestParam(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		try {
			// 支払計画データを取得
			AccgInvoiceStatementPaymentPlanEditForm form = service.createAccgInvoiceStatementPaymentPlanEditForm(invoiceSeq);
			mv = getMyModelAndView(form, ACCG_INVOICE_STATEMENT_PAYMENT_PLAN_EDIT_MODAL_FRAGMENT_PATH, ACCG_INVOICE_STATEMENT_PAYMENT_PLAN_EDIT_MODAL_FORM_NAME);

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 追加行を取得
	 */
	@RequestMapping(value = "/getPlanAddRowFragment", method = RequestMethod.GET)
	public ModelAndView getPlanAddRowFragment() {

		ModelAndView mv = null;

		// 支払計画追加行データの作成
		// 現状初期表示プロパティ等もないため、インスタンス化のみ
		var form = new AccgInvoiceStatementPaymentPlanEditForm.PaymentPlanRowInputForm();
		mv = getMyModelAndView(form, ACCG_INVOICE_STATEMENT_PAYMENT_PLAN_PAYMENT_ADD_ROW_FRAGMENT_PATH, ACCG_INVOICE_STATEMENT_PAYMENT_PLAN_PAYMENT_ADD_ROW_FORM_NAME);

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 支払計画の保存
	 * 
	 * @param inputForm
	 * @return
	 */
	@RequestMapping(value = "/saveShiharaiPlan", method = RequestMethod.POST)
	public ModelAndView saveShiharaiPlan(@Validated AccgInvoiceStatementPaymentPlanEditForm inputForm, BindingResult result) {

		try {
			if (result.hasErrors()) {
				// 入力エラーが存在する場合
				service.setDispProperties(inputForm);
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				return getMyModelAndViewWithErrors(inputForm, ACCG_INVOICE_STATEMENT_PAYMENT_PLAN_EDIT_MODAL_FRAGMENT_PATH, ACCG_INVOICE_STATEMENT_PAYMENT_PLAN_EDIT_MODAL_FORM_NAME, result);
			}

			// セッションからテナントSEQを取得
			Long tenantSeq = SessionUtils.getTenantSeq();
			// 削除するS3オブジェクトキーの格納用Set
			Set<String> deleteS3ObjectKeys = new HashSet<>();

			// 保存処理
			service.saveShiharaiPlan(inputForm, tenantSeq, deleteS3ObjectKeys);

			// S3オブジェクトの削除APIを実行
			service.deleteS3Object(deleteS3ObjectKeys);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00054, "分割予定"));
			return null;
		} catch (AppException ex) {
			// エラー処理
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}
	}

}
