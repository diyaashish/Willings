package jp.loioz.app.user.invoiceDetail.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.form.accg.AccgDocSummaryForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.BaseToInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.InvoiceInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.InvoiceRowInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.RepayRowInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementViewForm;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.user.caseInvoiceStatementList.controller.CaseInvoiceStatementListController;
import jp.loioz.app.user.invoiceDetail.service.InvoiceDetailService;
import jp.loioz.app.user.invoiceList.controller.InvoiceListController;
import jp.loioz.app.user.statementDetail.controller.StatementDetailController;
import jp.loioz.common.constant.CommonConstant.AccgDocFileType;
import jp.loioz.common.constant.CommonConstant.AccgDocSendType;
import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.AccggInvoiceStatementDetailViewTab;
import jp.loioz.common.constant.CommonConstant.MessageLevel;
import jp.loioz.common.constant.CommonConstant.SeikyuType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.ViewPathAndAttrConstant;
import jp.loioz.common.constant.ViewPathAndAttrConstant.AccgInvociceStatementPathAndAttrConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.log.Logger;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.utility.UriUtils;
import jp.loioz.domain.UriService;

/**
 * 請求書詳細画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/invoiceDetail/{invoiceSeq}")
public class InvoiceDetailController extends DefaultController {

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyAccgInvoiceStatementInputForm";

	/** コントローラと対応するパス */
	private static final String MY_VIEW_PATH = "user/invoiceDetail/invoiceDetail";

	/** 共通：請求書/精算書フラグメントパス */
	private static final String COMMON_ACCG_INVOICE_STATEMENT_FRAGMENT_PATH = "common/accg/accgInvoiceStatementFragment";

	/** 請求書詳細の支払条件表示フラグメントのパス */
	private static final String INVOICE_DETAIL_PAYMENT_PLAN_CONDITION_VIEW_FRAGMENT_PATH = COMMON_ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::paymentPlanFragment";

	/** 請求書詳細の支払条件入力フラグメントのパス */
	private static final String INVOICE_DETAIL_PAYMENT_PLAN_CONDITION_INPUT_FRAGMENT_PATH = COMMON_ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::paymentPlanConditionInputFragment";

	/** 表示で使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 請求書詳細の支払条件表示用オブジェクト名 */
	private static final String INVOICE_DETAIL_PAYMENT_PLAN_CONDITION_VIEW_FORM_NAME = "paymentPlanConditionViewForm";

	/** 請求書詳細の支払条件入力用オブジェクト名 */
	private static final String INVOICE_DETAIL_PAYMENT_PLAN_CONDITION_INPUT_FORM_NAME = "paymentPlanConditionInputForm";

	/** 請求書削除完了のヘッダーメッセージ */
	public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_INVOICE_DELETE_SUCCESS_REDIRECT = "invoice_delete_success_redirect";

	/** 請求書詳細のサービスクラス */
	@Autowired
	private InvoiceDetailService service;

	/** Uriサービスクラス */
	@Autowired
	private UriService uriService;

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

	// =========================================================================
	// public メソッド
	// =========================================================================

	// =========================================================================
	// 画面遷移
	// =========================================================================

	/**
	 * 請求書詳細画面初期表示
	 * 
	 * @param invoiceSeq
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 請求書詳細情報取得
		try {
			AccgInvoiceStatementViewForm viewForm = service.createViewForm();
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			service.searchInvoiceDetail(viewForm, accgDocSeq, invoiceSeq);
			mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		} catch (DataNotFoundException e) {
			String errMessage = StringUtils.lineBreakStr2Code(getMessage(MessageEnum.MSG_E00197));
			String redirectUrl = ModelAndViewUtils.getRedirectPath(InvoiceListController.class,
					controller -> controller.displayWhenInvoiceDetailsFails(null, null, errMessage));
			return ModelAndViewUtils.getRedirectModelAndView(redirectUrl);
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * メッセージ表示用初期画面表示処理
	 * 
	 * @param invoiceSeq
	 * @param levelCd
	 * @param message
	 * @param attributes
	 * @return
	 */
	@RequestMapping(value = "/redirectIndexWithMsg", method = RequestMethod.GET)
	public ModelAndView redirectIndexWithMsg(
			@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam("level") String levelCd,
			@RequestParam("message") String message,
			RedirectAttributes attributes) {

		String redirectUrl = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.index(invoiceSeq));
		if (uriService.currentRequestAnkenSideParamHasTrue()) {
			Map<String, String> queryMap = Map.of(UrlConstant.ACCG_REFERER_ANKEN_SIDE_PARAM_NAME, Boolean.TRUE.toString());
			redirectUrl += UriUtils.createGetRequestQueryParamStr(queryMap);
		}

		RedirectViewBuilder builder = new RedirectViewBuilder(attributes, redirectUrl);
		return builder.build(MessageHolder.ofLevel(MessageLevel.of(levelCd), message));
	}

	// =========================================================================
	// Ajax通信
	// =========================================================================

	/**
	 * 請求書詳細フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getAccgInvoiceStatementDetailFragment", method = RequestMethod.GET)
	public ModelAndView getAccgInvoiceStatementDetailFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 請求書詳細情報取得
		try {
			AccgInvoiceStatementViewForm viewForm = service.createViewForm();
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			service.searchInvoiceDetail(viewForm, accgDocSeq, invoiceSeq);
			mv = getMyModelAndView(viewForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_FORM_NAME);

		} catch (DataNotFoundException e) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getAnkenViewFragment", method = RequestMethod.GET)
	public ModelAndView getAnkenViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 案件表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.AnkenForm ankenForm = service.getAnkenForm(accgDocSeq);
			mv = getMyModelAndView(ankenForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_ANKEN_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_ANKEN_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 発行前のチェック処理をおこないます。
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/checkOfBeforeIssue", method = RequestMethod.POST)
	public Map<String, Object> checkOfBeforeIssue(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 発行前チェック
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			service.checkOfBeforeIssue(AccgDocType.INVOICE, accgDocSeq);

			// 請求額が0以下の場合、請求書に変更するか画面で確認
			if (service.checkIfInvoiceAmountIsMinus(accgDocSeq)) {
				response.put("needChangeToStatement", true);
			} else {
				response.put("needChangeToStatement", false);
			}

			// ここまで来たら正常終了
			super.setAjaxProcResultSuccess();
			return response;

		} catch (AppException e) {
			// アプリケーションエラー
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return response;

		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return response;
		}
	}

	/**
	 * 発行処理
	 * 
	 * @param invoiceSeq
	 * @param ankenId
	 * @param personId
	 * @param formatChangeFlg フォーマット変更フラグ。true:精算書に変更, false：請求書のまま
	 * @return
	 */
	@RequestMapping(value = "/issue", method = RequestMethod.POST)
	public ModelAndView issue(
			@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam("ankenId") Long ankenId,
			@RequestParam("personId") Long personId,
			@RequestParam("formatChangeFlg") boolean formatChangeFlg) {

		Long accgDocSeq;

		try {
			// 発行処理前のチェック
			accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			service.checkOfBeforeIssue(AccgDocType.INVOICE, accgDocSeq);

			// formatChangeFlgがtrueのときは、請求額が0以下であること、formatChangeFlgがfalseのときは、請求額が1以上であること
			if (formatChangeFlg) {
				if (!service.checkIfInvoiceAmountIsMinus(accgDocSeq)) {
					// 金額が1以上なので楽観ロックエラー
					throw new AppException(MessageEnum.MSG_E00025, null);
				}
			} else {
				if (service.checkIfInvoiceAmountIsMinus(accgDocSeq)) {
					// 請求額が0以下なので楽観ロックエラー
					throw new AppException(MessageEnum.MSG_E00025, null);
				}
			}
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		// 発行処理
		Long tenantSeq = SessionUtils.getTenantSeq();
		try {
			// 削除するS3オブジェクトキーの格納用Set
			Set<String> deleteS3ObjectKeys = new HashSet<>();

			if (formatChangeFlg) {
				// 請求書から精算書への変更を行う場合 -> 種別変更処理
				Long statementSeq = service.changeToStatement(accgDocSeq, ankenId, deleteS3ObjectKeys);

				// 登録処理の中で削除したS3オブジェクトキーの削除処理
				this.deleteS3Object(deleteS3ObjectKeys);

				// 完了後のリダイレクトパスを作成
				String redirectUrl = ModelAndViewUtils.getRedirectPath(
						StatementDetailController.class,
						(controller) -> controller.redirectIndexWithMsg(statementSeq, MessageLevel.INFO.getCd(),
								getMessage(MessageEnum.MSG_I00138, "請求書", "精算書"), null));

				// 処理完了メッセージとして、リダイレクトURLを設定
				// 完了処理が入力パラメータによって分岐するため、例外的にこの実装とする)
				super.setAjaxProcResultSuccess(redirectUrl);
				return null;

			} else {
				// 請求書 から 精算書への変更を行わない場合 -> 発行処理
				// 精算書への変更処理では、発行ではなく下書き状態となるのでPDFを作成しなくても、精算書画面からの発行 or タブ選択時に新たにPDFが作成される想定のため

				// 発行処理の前段処理(請求書PDFの再作成)
				service.beforeTransactionalDocInvoicePdfCreate(invoiceSeq, tenantSeq, deleteS3ObjectKeys);

				// 発行処理の前段処理(実費明細PDFの再作成)
				service.beforeTransactionalDipositRecordPdfCreate(accgDocSeq, tenantSeq, deleteS3ObjectKeys);

				// 発行処理
				service.issue(accgDocSeq, ankenId, personId);

				// 登録処理の中で削除したS3オブジェクトキーの削除処理
				this.deleteS3Object(deleteS3ObjectKeys);

				super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00136, "請求書"));
				return null;
			}

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

	}

	/**
	 * 削除前のチェック処理をおこないます。
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/checkOfBeforeDelete", method = RequestMethod.GET)
	public Map<String, Object> checkOfBeforeDelete(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		Map<String, Object> response = new HashMap<>();

		Long accgDocSeq;
		try {
			accgDocSeq = service.getAccgDocSeq(invoiceSeq);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return response;
		}
		
		// 送付済みの書類があるか確認
		if (service.checkIfAccgDocHasBeenSent(accgDocSeq)) {
			// 書類を送付している場合は削除不可とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00086, "送付済みの"));
			return response;
		}
		
		// この請求書から作成された預り金請求のデータが、他の請求書／精算書で利用されているか
		boolean isCreatedDepositInvoiceAndUsingOther = service.isCreatedDepositInvoiceAndUsingOther(accgDocSeq);
		if (isCreatedDepositInvoiceAndUsingOther) {
			// 利用されている場合は削除不可とする
			super.setAjaxProcResultFailure(
					getMessage(MessageEnum.MSG_E00086, "この請求書で請求を行った預り金が、<br>他の請求書／精算書で利用されている"));
			return response;
		}
		
		super.setAjaxProcResultSuccess();
		return response;
	}

	/**
	 * 請求書削除処理
	 * 
	 * @param invoiceSeq
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ModelAndView delete(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam("ankenId") Long ankenId, @RequestParam("personId") Long personId) {

		ModelAndView mv = null;

		// 削除処理
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);

			// 削除処理
			service.delete(accgDocSeq, ankenId, personId);

			String redirectUrl;
			if (uriService.currentRequestAnkenSideParamHasTrue()) {
				redirectUrl = ModelAndViewUtils.getRedirectPath(
						CaseInvoiceStatementListController.class,
						(controller) -> controller.redirectIndexWithMsg(personId, ankenId, MessageLevel.INFO.getCd(), getMessage(MessageEnum.MSG_I00028, "請求書"), null));
			} else {
				redirectUrl = ModelAndViewUtils.getRedirectPath(InvoiceListController.class,
						controller -> controller.deleteSuccessRedirect(null));
			}
			super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_INVOICE_DELETE_SUCCESS_REDIRECT, redirectUrl);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		return mv;
	}

	/**
	 * 発行前に戻す処理
	 * 
	 * @param invoiceSeq
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/updateInvoiceToDraftAndRemoveRelatedData", method = RequestMethod.POST)
	public ModelAndView updateInvoiceToDraftAndRemoveRelatedData(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam("ankenId") Long ankenId, @RequestParam("personId") Long personId) {

		ModelAndView mv = null;

		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);

			// 発行前に戻す
			service.updateInvoiceToDraftAndRemoveRelatedData(accgDocSeq, ankenId, personId);

			// 表示画面取得
			AccgInvoiceStatementViewForm viewForm = service.createViewForm();
			service.searchInvoiceDetail(viewForm, accgDocSeq, invoiceSeq);
			mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00137, "請求書"));
		return mv;
	}

	/**
	 * 請求書_送付入力用画面情報の取得
	 *
	 * @param invoiceSeq
	 * @param sendTypeCd
	 * @return
	 */
	@RequestMapping(value = "/getInvoiceAccgDocFileSendInputFragment", method = RequestMethod.GET)
	public ModelAndView getInvoiceAccgDocFileSendInputFragment(
			@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam(name = "sendType") String sendTypeCd,
			@RequestParam(name = "mailTemplateSeq", required = false) Long mailTemplateSeq) {
		ModelAndView mv = null;

		// ログインユーザーSEQを取得
		Long accountSeq = SessionUtils.getLoginAccountSeq();
		String tenantName = SessionUtils.getTenantName();

		try {
			AccgInvoiceStatementInputForm.FileSendInputForm inputForm = service.createInvoiceFileSendInputForm(
					invoiceSeq, AccgDocSendType.of(sendTypeCd), accountSeq, tenantName, mailTemplateSeq);
			mv = getMyModelAndView(inputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 請求書＿送付確認表示 入力フォームに戻る
	 * 
	 * @param inputForm
	 * @return
	 */
	@RequestMapping(value = "/getReturnInvoiceAccgDocFileSendInputFragment", method = RequestMethod.POST)
	public ModelAndView getReturnInvoiceAccgDocFileSendInputFragment(AccgInvoiceStatementInputForm.FileSendInputForm inputForm) {

		// 請求書入力フォームの表示用プロパティの設定
		service.setDispProperties(inputForm);

		super.setAjaxProcResultSuccess();
		return getMyModelAndView(inputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FRAGMENT_PATH,
				AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FORM_NAME);
	}

	/**
	 * 請求書_送付プレビュー画面情報の取得
	 *
	 * @param invoiceSeq
	 * @param sendTypeCd
	 * @return
	 */
	@RequestMapping(value = "/getInvoiceAccgDocFileSendPreviewFragment", method = RequestMethod.POST)
	public ModelAndView getInvoiceAccgDocFileSendPreviewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@Validated AccgInvoiceStatementInputForm.FileSendInputForm inputForm, BindingResult result) {

		// 入力チェック
		inputForm.rejectValues(result);

		// 表示用プロパティの設定
		service.setDispProperties(inputForm);

		// プレビュー表示用プロパティの設定
		service.setAccgDocFileSendPreviewDispProperties(inputForm);

		if (result.hasErrors()) {
			// バリデーションチェックに引っかかる場合は、確認画面は表示せず入力エラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FORM_NAME, result);
		}

		// 確認画面を表示する
		super.setAjaxProcResultSuccess();
		return getMyModelAndView(inputForm,
				AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_PREVIEW_FRAGMENT_PATH,
				AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FORM_NAME);
	}

	/**
	 * 請求書画面_送付処理
	 *
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/accgInvoiceFileSend", method = RequestMethod.POST)
	public ModelAndView accgInvoiceFileSend(@Validated AccgInvoiceStatementInputForm.FileSendInputForm inputForm, BindingResult result) {

		Long tenantSeq = SessionUtils.getTenantSeq();
		Long accountSeq = SessionUtils.getLoginAccountSeq();

		// 入力チェック
		inputForm.rejectValues(result);

		if (result.hasErrors()) {
			service.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FORM_NAME, result);
		}

		try {
			// 送付処理
			service.accgInvoiceFileSend(tenantSeq, accountSeq, inputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00140, "メール", "送信"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 請求書：印刷して送付フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getFilePrintSendViewForm", method = RequestMethod.GET)
	public ModelAndView getFilePrintSendViewForm(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		// 印刷して送付フラグメントフラグメント情報取得
		try {
			AccgInvoiceStatementViewForm.FilePrintSendViewForm filePrintSendViewForm = service.getFilePrintSendViewForm(invoiceSeq);
			super.setAjaxProcResultSuccess();
			return getMyModelAndView(filePrintSendViewForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_PRINT_SEND_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_PRINT_SEND_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}
	}

	/**
	 * 請求書：送付ファイルのダウンロード
	 * 
	 * @param invoiceSeq
	 * @param response
	 */
	@RequestMapping(value = "/printDownload", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void printDownload(@PathVariable(name = "invoiceSeq") Long invoiceSeq, HttpServletResponse response) {

		// 請求書のダウンロード
		try {
			service.printDownload(invoiceSeq, response);

		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
		} catch (Exception e) {
			// 想定しないダウンロードエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00034));
		}
	}

	/**
	 * 請求書：ステータスを「送付済み」にする（送付ファイルのダウンロード含む）
	 * 
	 * @param invoiceSeq
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/downloadAndChangeToSend", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public ModelAndView downloadAndChangeToSend(@PathVariable(name = "invoiceSeq") Long invoiceSeq, HttpServletResponse response) {

		// 請求書を送付ファイルをダウンロードして、送付済みにする
		try {
			service.downloadAndChangeToSend(invoiceSeq, response);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00045, "送付済みに"));
			return null;

		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception e) {
			// 想定しないダウンロードエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00034));
			return null;
		}
	}

	/**
	 * 設定表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getSettingViewFragment", method = RequestMethod.GET)
	public ModelAndView getSettingViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 設定表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.SettingViewForm settingViewForm = service.getSettingViewForm(accgDocSeq);
			mv = getMyModelAndView(settingViewForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_SETTING_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_SETTING_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 設定入力フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getSettingInputFragment", method = RequestMethod.GET)
	public ModelAndView getSettingInputFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 設定入力フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.SettingInputForm settingInputForm = service.getSettingInputForm(accgDocSeq);
			mv = getMyModelAndView(settingInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_SETTING_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_SETTING_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 設定入力フラグメント保存処理
	 * 
	 * @param invoiceSeq
	 * @param settingInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
	public ModelAndView saveSetting(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.SettingInputForm settingInputForm,
			BindingResult result) {

		// バリデーションチェック、プルダウン選択値存在チェック
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			if (result.hasErrors() || checkExistInvoiceTypeEnum(settingInputForm.getInvoiceType(), accgDocSeq, result)) {
				// 売上計上先選択リストをセット
				service.setDispProperties(accgDocSeq, settingInputForm);
				// 処理失敗
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(settingInputForm,
						AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_SETTING_INPUT_FRAGMENT_PATH,
						AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_SETTING_INPUT_FORM_NAME, result);
			}

			// 設定データ保存処理
			service.saveSetting(accgDocSeq, settingInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "設定"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

	}

	/**
	 * 内部メモ表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getMemoViewFragment", method = RequestMethod.GET)
	public ModelAndView getMemoViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 設定表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.MemoViewForm settingViewForm = service.getMemoViewForm(accgDocSeq);
			mv = getMyModelAndView(settingViewForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_MEMO_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_MEMO_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;

		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;

		}
		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 内部メモ入力フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getMemoInputFragment", method = RequestMethod.GET)
	public ModelAndView getMemoInputFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 設定入力フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.MemoInputForm memoInputForm = service.getMemoInputForm(accgDocSeq);
			mv = getMyModelAndView(memoInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_MEMO_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_MEMO_INPUT_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;

		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}
		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 内部メモ入力フラグメント保存処理
	 * 
	 * @param invoiceSeq
	 * @param ｍemoInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveMemo", method = RequestMethod.POST)
	public ModelAndView saveMemo(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.MemoInputForm ｍemoInputForm,
			BindingResult result) {

		// バリデーションチェック、プルダウン選択値存在チェック
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			if (result.hasErrors()) {
				// 処理失敗
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(ｍemoInputForm,
						AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_MEMO_INPUT_FRAGMENT_PATH,
						AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_MEMO_INPUT_FORM_NAME, result);
			}

			// 内部メモデータ保存処理
			service.saveMemo(accgDocSeq, ｍemoInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "内部メモ"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

	}

	/**
	 * タブ表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getDocContentsViewFragment", method = RequestMethod.GET)
	public ModelAndView getDocContentsViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam(name = "selectedTabCd", required = false) String selectedTabCd) {

		ModelAndView mv = null;

		AccggInvoiceStatementDetailViewTab selectedTab = AccggInvoiceStatementDetailViewTab.of(selectedTabCd);

		// タブ表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.DocContentsForm docContentsForm = service.getDocContentsForm(accgDocSeq, selectedTab);
			mv = getMyModelAndView(docContentsForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_DOC_CONTENTS_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_DOC_CONTENTS_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 基本情報_タイトル表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseTitleViewFragment", method = RequestMethod.GET)
	public ModelAndView getBaseTitleViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 基本情報_タイトル表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.BaseTitleViewForm baseTitleViewForm = service.getBaseTitleViewForm(accgDocSeq);
			mv = getMyModelAndView(baseTitleViewForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TITLE_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TITLE_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 基本情報_タイトル入力フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseTitleInputFragment", method = RequestMethod.GET)
	public ModelAndView getBaseTitleInputFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 基本情報_タイトル入力フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.BaseTitleInputForm baseTitleInputForm = service.getBaseTitleInputForm(accgDocSeq);
			mv = getMyModelAndView(baseTitleInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TITLE_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TITLE_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 基本情報_タイトル入力フラグメント保存処理
	 * 
	 * @param invoiceSeq
	 * @param baseTitleInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveBaseTitle", method = RequestMethod.POST)
	public ModelAndView saveBaseTitle(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.BaseTitleInputForm baseTitleInputForm,
			BindingResult result) {

		Long accgDocSeq;

		try {
			accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			// 相関チェック（請求書が発行されている場合は変更不可とする）
			if (!service.checkIfInvoiceIsDraftStatus(accgDocSeq)) {
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_W00016, "請求書", "編集"));
				return null;
			}
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		// バリデーションチェック
		if (result.hasErrors()) {
			// 表示用プロパティの設定
			service.setDispProperties(baseTitleInputForm);
			// エラー情報をレスポンスに設定
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(baseTitleInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TITLE_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TITLE_INPUT_FORM_NAME, result);
		}

		try {
			// 基本情報_タイトルデータ保存処理
			service.saveBaseTitle(accgDocSeq, baseTitleInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "基本情報"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;

		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

	}

	/**
	 * 基本情報_請求先表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseToViewFragment", method = RequestMethod.GET)
	public ModelAndView getBaseToViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 基本情報_請求先表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.BaseToViewForm baseToViewForm = service.getBaseToViewForm(accgDocSeq);
			mv = getMyModelAndView(baseToViewForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TO_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TO_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 基本情報_請求先入力フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseToInputFragment", method = RequestMethod.GET)
	public ModelAndView getBaseToInputFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 基本情報_請求先入力フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.BaseToInputForm baseToInputForm = service.getBaseToInputForm(accgDocSeq);
			mv = getMyModelAndView(baseToInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TO_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TO_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 基本情報_請求先の名称、敬称、詳細情報取得
	 * 
	 * @param invoiceSeq
	 * @param personId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getBaseToNameAndDetail", method = RequestMethod.GET)
	public Map<String, Object> getBaseToNameAndDetail(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam(name = "personId") Long personId) {

		Map<String, Object> response = new HashMap<>();

		try {
			BaseToInputForm form = service.getBaseToNameAndDetail(personId);
			response.put("baseToName", form.getBaseToName());
			response.put("baseToNameEnd", form.getBaseToNameEnd());
			response.put("baseToDetail", form.getBaseToDetail());
			
			super.setAjaxProcResultSuccess();
			return response;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return response;
		}
	}

	/**
	 * 基本情報_請求先入力フラグメント保存処理
	 * 
	 * @param invoiceSeq
	 * @param baseToInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveBaseTo", method = RequestMethod.POST)
	public ModelAndView saveBaseTo(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.BaseToInputForm baseToInputForm,
			BindingResult result) {

		Long accgDocSeq;

		try {
			accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			// 相関チェック（請求書が発行されている場合は変更不可とする）
			if (!service.checkIfInvoiceIsDraftStatus(accgDocSeq)) {
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_W00016, "請求書", "編集"));
				return null;
			}
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		// バリデーションチェック
		if (result.hasErrors()) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 発行先の選択リストを取得
			Long ankenId = baseToInputForm.getAnkenId();
			baseToInputForm.setCustomerKanyoshaList(service.getCustomerKanyoshaList(ankenId));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(baseToInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TO_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TO_INPUT_FORM_NAME, result);
		}

		try {
			// 基本情報_請求先データ保存処理
			service.saveBaseTo(accgDocSeq, baseToInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "基本情報"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

	}

	/**
	 * 基本情報_請求元表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseFromViewFragment", method = RequestMethod.GET)
	public ModelAndView getBaseFromViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 基本情報_請求元表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.BaseFromViewForm baseFromViewForm = service.getBaseFromViewForm(accgDocSeq);
			mv = getMyModelAndView(baseFromViewForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_FROM_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_FROM_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 基本情報_請求元入力フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseFromInputFragment", method = RequestMethod.GET)
	public ModelAndView getBaseFromInputFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 基本情報_請求先入力フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.BaseFromInputForm baseFromInputForm = service.getBaseFromInputForm(accgDocSeq);
			mv = getMyModelAndView(baseFromInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_FROM_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_FROM_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 基本情報_請求元入力フラグメント保存処理
	 * 
	 * @param invoiceSeq
	 * @param baseToInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveBaseFrom", method = RequestMethod.POST)
	public ModelAndView saveBaseFrom(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.BaseFromInputForm baseFromInputForm,
			BindingResult result) {

		Long accgDocSeq;

		try {
			accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			// 相関チェック（請求書が発行されている場合は変更不可とする）
			if (!service.checkIfInvoiceIsDraftStatus(accgDocSeq)) {
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_W00016, "請求書", "編集"));
				return null;
			}
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		// バリデーションチェック
		if (result.hasErrors()) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(baseFromInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_FROM_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_FROM_INPUT_FORM_NAME, result);
		}

		try {
			// 基本情報_請求元データ保存処理
			service.saveBaseFrom(accgDocSeq, baseFromInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "基本情報"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

	}

	/**
	 * 基本情報_挿入文表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseOtherViewFragment", method = RequestMethod.GET)
	public ModelAndView getBaseOtherViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 基本情報_挿入文表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.BaseOtherViewForm baseOtherViewForm = service.getBaseOtherViewForm(accgDocSeq);
			mv = getMyModelAndView(baseOtherViewForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_OTHER_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_OTHER_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 基本情報_挿入文入力フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseOtherInputFragment", method = RequestMethod.GET)
	public ModelAndView getBaseOtherInputFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 基本情報_挿入文入力フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.BaseOtherInputForm baseOtherInputForm = service.getBaseOtherInputForm(accgDocSeq);
			mv = getMyModelAndView(baseOtherInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_OTHER_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_OTHER_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 基本情報_挿入文入力フラグメント保存処理
	 * 
	 * @param invoiceSeq
	 * @param baseOtherInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveBaseOther", method = RequestMethod.POST)
	public ModelAndView saveBaseOther(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.BaseOtherInputForm baseOtherInputForm, BindingResult result) {

		Long accgDocSeq;

		try {
			accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			// 相関チェック（請求書が発行されている場合は変更不可とする）
			if (!service.checkIfInvoiceIsDraftStatus(accgDocSeq)) {
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_W00016, "請求書", "編集"));
				return null;
			}
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		// バリデーションチェック
		if (result.hasErrors()) {
			// 表示用プロパティの設定
			service.setDispProperties(baseOtherInputForm);
			// エラーメッセージを設定
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(baseOtherInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_OTHER_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_OTHER_INPUT_FORM_NAME, result);
		}

		try {
			// 基本情報_挿入文データ保存処理
			service.saveBaseOther(accgDocSeq, baseOtherInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "基本情報"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

	}

	/**
	 * 既入金表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getRepayViewFragment", method = RequestMethod.GET)
	public ModelAndView getRepayViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 既入金表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.RepayViewForm repayViewForm = service.getRepayViewForm(accgDocSeq);
			mv = getMyModelAndView(repayViewForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 既入金入力フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getRepayInputFragment", method = RequestMethod.GET)
	public ModelAndView getRepayInputFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 既入金入力フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.RepayInputForm repayInputForm = service.getRepayInputForm(accgDocSeq);
			mv = getMyModelAndView(repayInputForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * DBに登録してある請求額と同じか確認し、異なる場合は支払計画の再作成メッセージを表示するためメッセージキーをセットする<br>
	 * 既入金項目の並び替えをしている場合に画面上の表示順とrepayInputFormの行データのセット順が異なるため<br>
	 * バリデーションチェックは、@Validatedを使用せず、 ソート（repayInputFormの行データを画面上の表示順に合わせた）後にバリデーションを実行する。<br>
	 * （@Validatedを使用した場合に、repayInputFormやBindengResultの表示順を合わせることが難しいため）
	 * 
	 * @param invoiceSeq
	 * @param repayInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/checkInvoiceAmountChangeRepay", method = RequestMethod.POST)
	public ModelAndView checkInvoiceAmountChangeRepay(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.RepayInputForm repayInputForm,
			BindingResult result) {
		
		ModelAndView mv = null;
		Long accgDocSeq = repayInputForm.getAccgDocSeq();
		
		List<RepayRowInputForm> repayRowList = repayInputForm.getRepayRowList();
		if (LoiozCollectionUtils.isNotEmpty(repayRowList)) {
			repayRowList = repayRowList.stream().filter(row -> row.getDocRepayOrder() != null).collect(Collectors.toList());
		}
		
		// docRepayOrder順にソート
		repayRowList = service.sortRepayList(repayRowList);
		
		repayInputForm.setRepayRowList(repayRowList);
		
		// バリデーションチェック
		try {
			result = this.validationBeforeRepaySave(repayInputForm, result);
			
			if (result.hasErrors()) {
				// 処理失敗
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(repayInputForm,
						AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_FRAGMENT_PATH,
						AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_FORM_NAME, result);
			}
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}
		
		try {
			// 支払計画が作成されていて、請求項目の金額に変更がある場合は画面で確認
			if (service.checkIfPaymentPlanCreated(invoiceSeq) && service.checkInvoiceAmountChange(accgDocSeq, repayInputForm)) {
				super.setAjaxProcResultFailure(MessageEnum.MSG_W00014.getMessageKey());
				return null;
			}
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 既入金項目のまとめ表示の設定 or 解除をおこないます。<br>
	 * 既入金項目の並び替えをしている場合に画面上の表示順とrepayInputFormの行データのセット順が異なるため<br>
	 * バリデーションチェックは、@Validatedを使用せず、 ソート（repayInputFormの行データを画面上の表示順に合わせた）後にバリデーションを実行する。<br>
	 * （@Validatedを使用した場合に、repayInputFormやBindengResultの表示順を合わせることが難しいため）
	 * 
	 * @param invoiceSeq
	 * @param repayInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/groupOrUngroupSimilarRepayItems", method = RequestMethod.POST)
	public ModelAndView groupOrUngroupSimilarRepayItems(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.RepayInputForm repayInputForm,
			BindingResult result) {
		
		ModelAndView mv = null;
		
		List<RepayRowInputForm> repayRowList = repayInputForm.getRepayRowList();
		if (LoiozCollectionUtils.isNotEmpty(repayRowList)) {
			repayRowList = repayRowList.stream().filter(row -> row.getDocRepayOrder() != null).collect(Collectors.toList());
		}
		
		// docRepayOrder順にソート
		repayRowList = service.sortRepayList(repayRowList);
		
		repayInputForm.setRepayRowList(repayRowList);
		
		// バリデーションチェック
		try {
			result = this.validationBeforeRepaySave(repayInputForm, result);
			
			if (result.hasErrors()) {
				// 処理失敗
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// まとめチェックを変更前に戻す
				if (repayInputForm.isRepaySumFlg()) {
					repayInputForm.setRepaySumFlg(false);
				} else {
					repayInputForm.setRepaySumFlg(true);
				}
				// 自分自身の画面にerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(repayInputForm,
						AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_FRAGMENT_PATH,
						AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_FORM_NAME, result);
			}
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			// まとめチェックを変更前に戻す
			if (repayInputForm.isRepaySumFlg()) {
				repayInputForm.setRepaySumFlg(false);
			} else {
				repayInputForm.setRepaySumFlg(true);
			}
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(repayInputForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_FORM_NAME, result);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}
		
		// 既入金項目グループ化 or 解除
		List<RepayRowInputForm> repayInputFormList = service.groupOrUngroupSimilarRepayItems(repayInputForm);

		mv = getMyModelAndView(repayInputFormList,
				AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_LIST_FRAGMENT_PATH,
				AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_LIST_INPUT_FORM_NAME);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 既入金入力フラグメント保存処理<br>
	 * 既入金項目の並び替えをしている場合に画面上の表示順とrepayInputFormの行データのセット順が異なるため<br>
	 * バリデーションチェックは、@Validatedを使用せず、 ソート（repayInputFormの行データを画面上の表示順に合わせた）後にバリデーションを実行する。<br>
	 * （@Validatedを使用した場合に、repayInputFormやBindengResultの表示順を合わせることが難しいため）
	 * 
	 * @param invoiceSeq
	 * @param repayInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveRepay", method = RequestMethod.POST)
	public ModelAndView saveRepay(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.RepayInputForm repayInputForm,
			BindingResult result) {
		
		ModelAndView mv = null;
		Long accgDocSeq = repayInputForm.getAccgDocSeq();
		
		// 有効な行を取得
		List<RepayRowInputForm> repayRowList = service.getEnabledRepayList(repayInputForm.getRepayRowList());
		
		// docRepayOrder順にソート
		repayRowList = service.sortRepayList(repayRowList);
		
		repayInputForm.setRepayRowList(repayRowList);
		
		// バリデーションチェック
		try {
			result = this.validationBeforeRepaySave(repayInputForm, result);
			
			if (result.hasErrors()) {
				// 処理失敗
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(repayInputForm,
						AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_FRAGMENT_PATH,
						AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_FORM_NAME, result);
			}
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		// 既入金データ保存処理
		try {
			service.saveRepay(invoiceSeq, repayInputForm);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00012));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		try {
			AccgInvoiceStatementViewForm.RepayViewForm repayViewForm = service.getRepayViewForm(accgDocSeq);
			mv = getMyModelAndView(repayViewForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "既入金"));
		return mv;
	}

	/**
	 * 請求表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getInvoiceViewFragment", method = RequestMethod.GET)
	public ModelAndView getInvoiceViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 請求表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.InvoiceViewForm invoiceViewForm = service.getInvoiceViewForm(accgDocSeq);
			mv = getMyModelAndView(invoiceViewForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 請求入力フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getInvoiceInputFragment", method = RequestMethod.GET)
	public ModelAndView getInvoiceInputFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 請求入力フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm = service.getInvoiceInputForm(accgDocSeq);
			mv = getMyModelAndView(invoiceInputForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 請求入力合計額フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @param invoiceInputForm
	 * @return
	 */
	@RequestMapping(value = "/getInvoiceTotalAmountInputFragment", method = RequestMethod.POST)
	public ModelAndView getInvoiceTotalAmountInputFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm) {

		ModelAndView mv = null;

		// 請求入力合計額フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			List<InvoiceRowInputForm> invoiceRowInputFormList = invoiceInputForm.getInvoiceRowList();
			AccgInvoiceStatementInputForm.InvoiceTotalAmountInputForm invoiceTotalAmountInputForm = service.getInvoiceTotalAmountInputForm(accgDocSeq, invoiceRowInputFormList);
			mv = getMyModelAndView(invoiceTotalAmountInputForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_TOTAL_AMOUNT_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_TOTAL_AMOUNT_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * パラメータで受け取った預り金（入金）を既入金入力フラグメントに追加します。<br>
	 * 
	 * @param invoiceSeq
	 * @param repayInputForm
	 * @param depositRecvSeqList
	 * @return
	 */
	@RequestMapping(value = "/addNyukinDepositToRepayInput", method = RequestMethod.POST)
	public ModelAndView addNyukinDepositToRepayInput(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.RepayInputForm repayInputForm,
			@RequestParam(name = "depositRecvSeqList") List<Long> depositRecvSeqList) {

		ModelAndView mv = null;

		List<RepayRowInputForm> repayRowList = repayInputForm.getRepayRowList();
		if (LoiozCollectionUtils.isNotEmpty(repayRowList)) {
			repayRowList = repayInputForm.getRepayRowList().stream().filter(row -> row.getDocRepayOrder() != null).collect(Collectors.toList());
		}

		// docRepayOrder順にソート
		repayRowList = service.sortRepayList(repayRowList);

		repayInputForm.setRepayRowList(repayRowList);

		// 既入金入力フラグメント情報取得
		try {
			List<RepayRowInputForm> repayInputFormList = service.addNyukinDepositToRepayInput(repayInputForm, depositRecvSeqList);
			mv = getMyModelAndView(repayInputFormList,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_LIST_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_LIST_INPUT_FORM_NAME);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 報酬用請求入力フラグメント（1行）追加
	 * 
	 * @param invoiceSeq
	 * @param invoiceRowCount
	 * @return
	 */
	@RequestMapping(value = "/addFeeRow", method = RequestMethod.GET)
	public ModelAndView addFeeRow(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam("invoiceRowCount") Long invoiceRowCount) {

		ModelAndView mv = null;

		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.InvoiceRowInputForm inputForm = service.createNewFeeRowInputForm(accgDocSeq, invoiceRowCount);
			mv = getMyModelAndView(inputForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_ROW_FEE_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_ROW_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 未精算報酬用請求入力フラグメント追加
	 * 
	 * @param invoiceSeq
	 * @param unPaidFeeSeqList
	 * @param invoiceRowCount
	 * @return
	 */
	@RequestMapping(value = "/addUnPaidFee", method = RequestMethod.POST)
	public ModelAndView addUnPaidFee(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam("unPaidFeeSeqList") List<Long> unPaidFeeSeqList,
			@RequestParam("invoiceRowCount") Long invoiceRowCount) {
		
		ModelAndView mv = null;
		
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			List<InvoiceRowInputForm> inputFormList = service.createNewUnPaidFeeRowInputForm(accgDocSeq, invoiceRowCount, unPaidFeeSeqList);
			mv = getMyModelAndView(inputFormList,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_INPUT_LIST_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_LIST_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金用請求入力フラグメント（1行）追加
	 * 
	 * @param invoiceSeq
	 * @param invoiceRowCount
	 * @return
	 */
	@RequestMapping(value = "/addDepositRecvRow", method = RequestMethod.GET)
	public ModelAndView addDepositRecvRow(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam("invoiceRowCount") Long invoiceRowCount) {

		ModelAndView mv = null;

		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.InvoiceRowInputForm inputForm = service.createNewDepositRecvRowInputForm(accgDocSeq, invoiceRowCount);
			mv = getMyModelAndView(inputForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_ROW_DEPOSIT_EXPENSE_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_ROW_DEPOSIT_EXPENSE_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * パラメータで受け取った預り金（出金）を請求項目入力フラグメントに追加します。<br>
	 * 
	 * @param invoiceSeq
	 * @param invoiceInputForm
	 * @param depositRecvSeqList
	 * @return
	 */
	@RequestMapping(value = "/addShukkinDepositToInvoiceInput", method = RequestMethod.POST)
	public ModelAndView addShukkinDepositToInvoiceInput(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm,
			@RequestParam(name = "depositRecvSeqList") List<Long> depositRecvSeqList) {

		ModelAndView mv = null;

		List<InvoiceRowInputForm> invoiceRowList = invoiceInputForm.getInvoiceRowList();
		if (LoiozCollectionUtils.isNotEmpty(invoiceRowList)) {
			invoiceRowList = invoiceInputForm.getInvoiceRowList().stream().filter(row -> row.getDocInvoiceOrder() != null).collect(Collectors.toList());
		}

		// docInvoiceOrder順にソート
		invoiceRowList = service.sortInvoiceList(invoiceRowList);

		invoiceInputForm.setInvoiceRowList(invoiceRowList);

		try {
			List<InvoiceRowInputForm> invoiceInputFormList = service.addShukkinDepositToInvoiceInput(invoiceInputForm, depositRecvSeqList);
			mv = getMyModelAndView(invoiceInputFormList,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_INPUT_LIST_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_LIST_INPUT_FORM_NAME);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 値引き用請求入力フラグメント（1行）追加
	 * 
	 * @param invoiceSeq
	 * @param invoiceRowCount
	 * @return
	 */
	@RequestMapping(value = "/addDiscountRow", method = RequestMethod.GET)
	public ModelAndView addDiscountRow(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam("invoiceRowCount") Long invoiceRowCount) {

		ModelAndView mv = null;

		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.InvoiceRowInputForm inputForm = service.createNewDiscountRowInputForm(accgDocSeq, invoiceRowCount);
			mv = getMyModelAndView(inputForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_ROW_DISCOUNT_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_ROW_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * テキスト用請求入力フラグメント（1行）追加
	 * 
	 * @param invoiceSeq
	 * @param invoiceRowCount
	 * @return
	 */
	@RequestMapping(value = "/addTextRow", method = RequestMethod.GET)
	public ModelAndView addTextRow(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam("invoiceRowCount") Long invoiceRowCount) {

		ModelAndView mv = null;

		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.InvoiceRowInputForm inputForm = service.createNewTextRowInputForm(accgDocSeq, invoiceRowCount);
			mv = getMyModelAndView(inputForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_ROW_TEXT_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_ROW_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * DBに登録してある請求額と同じか確認し、異なる場合は支払計画の再作成メッセージを表示するためメッセージキーをセットする<br>
	 * （報酬額の算出イベントより先に登録処理が実行されてしまう場合に invoiceInputForm の報酬額が
	 * 正しくないため単価と時間から報酬額を算出しFormにセットしてからバリデーションを実行）
	 * 
	 * @param invoiceSeq
	 * @param invoiceInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/checkInvoiceAmountChangeInvoice", method = RequestMethod.POST)
	public ModelAndView checkInvoiceAmountChangeInvoice(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm,
			BindingResult result) {
		
		ModelAndView mv = null;
		
		List<InvoiceRowInputForm> invoiceRowList = invoiceInputForm.getInvoiceRowList();
		if (LoiozCollectionUtils.isNotEmpty(invoiceRowList)) {
			invoiceRowList = invoiceRowList.stream().filter(row -> row.getDocInvoiceOrder() != null).collect(Collectors.toList());
		}
		
		// docInvoiceOrder順にソート
		invoiceRowList = service.sortInvoiceList(invoiceRowList);
		
		invoiceInputForm.setInvoiceRowList(invoiceRowList);
		
		// バリデーションチェック
		try {
			result = this.validationBeforeInvoiceSave(invoiceInputForm, invoiceSeq, result);
			
			if (result.hasErrors()) {
				// 処理失敗
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(invoiceInputForm,
						AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_INPUT_FRAGMENT_PATH,
						AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_INPUT_FORM_NAME, result);
			}
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		try {
			// 支払計画が作成されていて、請求項目の金額に変更がある場合は画面で確認
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			if (service.checkIfPaymentPlanCreated(invoiceSeq) && service.checkInvoiceAmountChange(accgDocSeq, invoiceInputForm)) {
				super.setAjaxProcResultFailure(MessageEnum.MSG_W00014.getMessageKey());
				return null;
			}
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 請求項目のまとめ表示の設定 or 解除をおこないます。<br>
	 * 請求項目の並び替えをしている場合に画面上の表示順とinvoiceInputFormの行データのセット順が異なるため<br>
	 * バリデーションチェックは、@Validatedを使用せず、 ソート（invoiceInputFormの行データを画面上の表示順に合わせた）後にバリデーションを実行する。<br>
	 * （@Validatedを使用した場合に、invoiceInputFormやBindengResultの表示順を合わせることが難しいため）
	 * 
	 * @param invoiceSeq
	 * @param repayInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/groupOrUngroupSimilarInvoiceItems", method = RequestMethod.POST)
	public ModelAndView groupOrUngroupSimilarInvoiceItems(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm,
			BindingResult result) {
		
		ModelAndView mv = null;
		
		List<InvoiceRowInputForm> invoiceRowList = invoiceInputForm.getInvoiceRowList();
		if (LoiozCollectionUtils.isNotEmpty(invoiceRowList)) {
			invoiceRowList = invoiceRowList.stream().filter(row -> row.getDocInvoiceOrder() != null).collect(Collectors.toList());
		}
		
		// docInvoiceOrder順にソート
		invoiceRowList = service.sortInvoiceList(invoiceRowList);
		
		invoiceInputForm.setInvoiceRowList(invoiceRowList);
		
		// 相関チェック
		try {
			// 相関チェック（請求書発行ステータスチェック、請求額上限チェック、実費データのみバリデーションチェック）
			this.validationBeforeGroupOrUngroupSimilarInvoiceItems(invoiceInputForm, invoiceSeq, mv, result);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
		
		// バリデーションチェック
		if (result.hasErrors()) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// まとめチェックを変更前に戻す
			if (invoiceInputForm.isExpenseSumFlg()) {
				invoiceInputForm.setExpenseSumFlg(false);
			} else {
				invoiceInputForm.setExpenseSumFlg(true);
			}
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(invoiceInputForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_INPUT_FORM_NAME, result);
		}
		
		// 請求項目グループ化 or 解除
		List<InvoiceRowInputForm> invoiceInputFormList = service.groupOrUngroupSimilarInvoiceItems(invoiceInputForm);
		
		mv = getMyModelAndView(invoiceInputFormList,
				AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_INPUT_LIST_FRAGMENT_PATH,
				AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_LIST_INPUT_FORM_NAME);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}
		
		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 請求入力フラグメント保存処理<br>
	 * （報酬額の算出イベントより先に登録処理が実行されてしまう場合に invoiceInputForm の報酬額が
	 * 正しくないため単価と時間から報酬額を算出しFormにセットしてからバリデーションを実行）
	 * 
	 * @param invoiceSeq
	 * @param invoiceInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveInvoice", method = RequestMethod.POST)
	public ModelAndView saveInvoice(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm,
			BindingResult result) {
		
		ModelAndView mv = null;
		
		// 有効な行を取得しFormに再セット
		List<InvoiceRowInputForm> invoiceRowList = service.getEnabledInvoiceList(invoiceInputForm.getInvoiceRowList());
		
		// docInvoiceOrder順にソート
		invoiceRowList = service.sortInvoiceList(invoiceRowList);
		
		invoiceInputForm.setInvoiceRowList(invoiceRowList);
		
		// バリデーションチェック
		try {
			result = this.validationBeforeInvoiceSave(invoiceInputForm, invoiceSeq, result);
			
			if (result.hasErrors()) {
				// 処理失敗
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(invoiceInputForm,
						AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_INPUT_FRAGMENT_PATH,
						AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_INPUT_FORM_NAME, result);
			}
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		// 請求データ保存処理
		Long accgDocSeq;
		try {
			accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			service.saveInvoice(invoiceSeq, invoiceInputForm);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00012));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		try {
			AccgInvoiceStatementViewForm.InvoiceViewForm invoiceViewForm = service.getInvoiceViewForm(accgDocSeq);
			mv = getMyModelAndView(invoiceViewForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_INVOICE_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "請求項目"));
		return mv;
	}

	/**
	 * タイムチャージの報酬額計算
	 * 
	 * @param invoiceSeq
	 * @param timeChargeTanka
	 * @param timeChargeTime
	 * @param index
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/calculateInputTimeCharge", method = RequestMethod.GET)
	public Map<String, Object> calculateInputTimeCharge(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam("timeChargeTanka") String timeChargeTanka,
			@RequestParam("timeChargeTime") String timeChargeTime, @RequestParam("index") Long index) {

		Map<String, Object> response = new HashMap<>();
		response.put("index", index);
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
	 * 報酬項目の候補を取得
	 * 
	 * @param invoiceSeq
	 * @param searchWord
	 * @return
	 */
	@RequestMapping(value = "/searchFeeItemName", method = RequestMethod.GET)
	public ModelAndView searchFeeItemName(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam("searchWord") String searchWord) {

		ModelAndView mv = null;

		List<SelectOptionForm> itemList = service.searchFeeDataList(searchWord);
		mv = getMyModelAndView(itemList,
				AccgInvociceStatementPathAndAttrConstant.ITEM_LIST_FRAGMENT_PATH,
				AccgInvociceStatementPathAndAttrConstant.ITEM_LIST_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金項目の候補を取得
	 * 
	 * @param invoiceSeq
	 * @param searchWord
	 * @param depositType
	 * @return
	 */
	@RequestMapping(value = "/searchDepositRecvItemName", method = RequestMethod.GET)
	public ModelAndView searchDepositRecvItemName(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@RequestParam("searchWord") String searchWord, @RequestParam("depositType") String depositType) {

		ModelAndView mv = null;

		List<SelectOptionForm> itemList = service.searchDepositRecvDataList(searchWord, depositType);
		mv = getMyModelAndView(itemList,
				AccgInvociceStatementPathAndAttrConstant.ITEM_LIST_FRAGMENT_PATH,
				AccgInvociceStatementPathAndAttrConstant.ITEM_LIST_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 請求書PDFの再作成が必要かを判定し、必要な場合のみ作成中画面表示情報を取得する
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getInvoicePdfCreatingViewFragment", method = RequestMethod.GET)
	public ModelAndView getInvoicePdfCreatingViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv;
		try {
			if (service.needCreateInvoicePdf(invoiceSeq)) {
				// 請求書PDFが作成・再作成を行う場合 -> 正常終了として、作成中画面を表示
				mv = this.getPdfCreatingViewFragment(AccgDocFileType.INVOICE);

			} else {
				// 作成・再作成を行わない場合 -> 正常終了として、Nullを返却する
				mv = null;
			}

			super.setAjaxProcResultSuccess();
			return mv;

		} catch (DataNotFoundException ex) {
			// Ajaxによる取得時のDataNotFoundは、画面の再取得時に発生したケースなので楽観エラーとして処理する
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

	}

	/**
	 * 請求書画面_請求書タブ画面表示情報を取得する
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getDocInvoicePdfViewFragment", method = RequestMethod.GET)
	public ModelAndView getDocInvoicePdfViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;
		Long tenantSeq = SessionUtils.getTenantSeq();

		try {
			// 削除するS3オブジェクトキーの格納用Set
			Set<String> deleteS3ObjectKey = new HashSet<>();

			// 画面表示オブジェクトの前段処理
			service.beforeTransactionalDocInvoicePdfCreate(invoiceSeq, tenantSeq, deleteS3ObjectKey);

			// 登録処理の中で削除したS3オブジェクトキーの削除処理
			this.deleteS3Object(deleteS3ObjectKey);

			// 画面表示オブジェクトの作成
			AccgInvoiceStatementViewForm.DocInvoicePdfViewForm viewForm = service.getDocInvoicePdfViewForm(invoiceSeq);
			mv = getMyModelAndView(viewForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_INVOICE_PDF_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_INVOICE_PDF_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// Ajaxによる取得時のDataNotFoundは、画面の再取得時に発生したケースなので楽観エラーとして処理する
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面情報の取得に失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;

	}

	/**
	 * 請求書のPDFプレビューを表示する
	 * 
	 * @param invoiceSeq
	 */
	@RequestMapping(value = "/docInvoicePdfPreview", method = RequestMethod.GET)
	public void docInvoicePdfPreview(@PathVariable(name = "invoiceSeq") Long invoiceSeq, HttpServletResponse response) {
		service.docInvoicePdfPreview(invoiceSeq, response);
	}

	/**
	 * 請求書PDFの再作成が必要かを判定し、必要な場合のみ作成中画面表示情報を取得する
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getDipositRecordPdfCreatingViewFragment", method = RequestMethod.GET)
	public ModelAndView getDipositRecordPdfCreatingViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv;
		try {
			if (service.needCreateDipositRecordPdf(invoiceSeq)) {
				// 請求書PDFが作成・再作成を行う場合 -> 正常終了として、作成中画面を表示
				mv = this.getPdfCreatingViewFragment(AccgDocFileType.DEPOSIT_DETAIL);

			} else {
				// 作成・再作成を行わない場合 -> 正常終了として、Nullを返却する
				mv = null;
			}

			super.setAjaxProcResultSuccess();
			return mv;

		} catch (DataNotFoundException ex) {
			// Ajaxによる取得時のDataNotFoundは、画面の再取得時に発生したケースなので楽観エラーとして処理する
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

	}

	/**
	 * 請求書画面_実費明細タブ画面表示情報を取得する
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getDipositRecordPdfViewFragment", method = RequestMethod.GET)
	public ModelAndView getDipositRecordPdfViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;
		Long tenantSeq = SessionUtils.getTenantSeq();

		try {
			// チェックメソッド
			if (!service.checkDepositDetailAttachFlg(invoiceSeq)) {
				throw new AppException(MessageEnum.MSG_E00025, null);
			}

			// 会計書類SEQの取得処理
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);

			// 削除するS3オブジェクトキーの格納用Set
			Set<String> deleteS3ObjectKey = new HashSet<>();

			// 画面表示オブジェクトの前段処理(S3APIの発火)
			service.beforeTransactionalDipositRecordPdfCreate(accgDocSeq, tenantSeq, deleteS3ObjectKey);

			// 前段処理の中で削除したS3オブジェクトキーの削除処理
			this.deleteS3Object(deleteS3ObjectKey);

			// 画面表示オブジェクトの作成
			AccgInvoiceStatementViewForm.DipositRecordPdfViewForm viewForm = service.getDipositRecordPdfViewForm(accgDocSeq);
			mv = getMyModelAndView(viewForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_DIPOSIT_RECORD_PDF_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_DIPOSIT_RECORD_PDF_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// Ajaxによる取得時のDataNotFoundは、画面の再取得時に発生したケースなので楽観エラーとして処理する
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面情報の取得に失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;

	}

	/**
	 * 実費明細のPDFプレビューを表示する
	 * 
	 * @param invoiceSeq
	 */
	@RequestMapping(value = "/dipositRecordPdfPreview", method = RequestMethod.GET)
	public void dipositRecordPdfPreview(@PathVariable(name = "invoiceSeq") Long invoiceSeq, HttpServletResponse response) {
		service.dipositRecordPdfPreview(invoiceSeq, response);
	}

	/**
	 * 請求書画面_支払い計画タブ画面表示情報を取得する
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getPaymentPlanConditionViewFragment", method = RequestMethod.GET)
	public ModelAndView getPaymentPlanConditionViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		try {
			// オブジェクトの作成
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.PaymentPlanConditionViewForm viewForm = service.getPaymentPlanConditionViewForm(accgDocSeq);
			mv = getMyModelAndView(viewForm,
					INVOICE_DETAIL_PAYMENT_PLAN_CONDITION_VIEW_FRAGMENT_PATH,
					INVOICE_DETAIL_PAYMENT_PLAN_CONDITION_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// Ajaxによる取得時のDataNotFoundは、画面の再取得時に発生したケースなので楽観エラーとして処理する
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面情報の取得に失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;

	}

	/**
	 * 請求書画面_支払い計画タブ画面表示情報を取得する
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/rebuildInvoicePlanPdf", method = RequestMethod.POST)
	public ModelAndView rebuildInvoicePlanPdf(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		try {
			// テナントSEQの取得
			Long tenantSeq = SessionUtils.getTenantSeq();

			// 支払計画PDFの再ビルド
			service.rebuildInvoicePlanPdf(invoiceSeq, tenantSeq);

			super.setAjaxProcResultSuccess();
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 請求書画面_支払い計画タブ：支払条件入力用オブジェクトを取得する
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getPaymentPlanConditionInputFragment", method = RequestMethod.GET)
	public ModelAndView getPaymentPlanConditionInputFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		try {
			// オブジェクトの作成
			AccgInvoiceStatementInputForm.PaymentPlanConditionInputForm inputForm = service.createPaymentPlanConditionInputForm(invoiceSeq);
			mv = getMyModelAndView(inputForm,
					INVOICE_DETAIL_PAYMENT_PLAN_CONDITION_INPUT_FRAGMENT_PATH,
					INVOICE_DETAIL_PAYMENT_PLAN_CONDITION_INPUT_FORM_NAME);

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		if (mv == null) {
			// 画面情報の取得に失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 支払計画PDFの作成中画面情報を取得する
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getPaymentPlanPdfCreatingViewFragment", method = RequestMethod.GET)
	public ModelAndView getPaymentPlanPdfCreatingViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = this.getPdfCreatingViewFragment(AccgDocFileType.INVOICE_PAYMENT_PLAN);
		
		super.setAjaxProcResultSuccess();
		return mv;
	}
	
	/**
	 * 登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registPaymentPlanCondition", method = RequestMethod.POST)
	public ModelAndView registPaymentPlanCondition(
			@Validated AccgInvoiceStatementInputForm.PaymentPlanConditionInputForm inputForm, BindingResult result) {

		// テナントSEQを取得
		Long tenantSeq = SessionUtils.getTenantSeq();

		// 相関バリデーションチェック
		service.savePaymentPlanConditionValidate(inputForm, result);

		// 入力エラーチェック
		if (result.hasErrors()) {
			service.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm,
					INVOICE_DETAIL_PAYMENT_PLAN_CONDITION_INPUT_FRAGMENT_PATH,
					INVOICE_DETAIL_PAYMENT_PLAN_CONDITION_INPUT_FORM_NAME, result);
		}

		try {
			// 削除するS3オブジェクトキーの格納用Set
			Set<String> deleteS3ObjectKey = new HashSet<>();

			// 登録処理
			service.registPaymentPlanCondition(inputForm, tenantSeq, deleteS3ObjectKey);

			// 登録処理の中で削除したS3オブジェクトキーの削除処理
			this.deleteS3Object(deleteS3ObjectKey);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00045, "分割予定表を作成"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 更新処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/updatePaymentPlanCondition", method = RequestMethod.POST)
	public ModelAndView updatePaymentPlanCondition(@Validated AccgInvoiceStatementInputForm.PaymentPlanConditionInputForm inputForm, BindingResult result) {

		// テナントSEQを取得
		Long tenantSeq = SessionUtils.getTenantSeq();

		// 相関バリデーションチェック
		service.savePaymentPlanConditionValidate(inputForm, result);

		// 入力エラーチェック
		if (result.hasErrors()) {
			service.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm,
					INVOICE_DETAIL_PAYMENT_PLAN_CONDITION_INPUT_FRAGMENT_PATH,
					INVOICE_DETAIL_PAYMENT_PLAN_CONDITION_INPUT_FORM_NAME, result);
		}

		try {
			// 削除するS3オブジェクトキーの格納用Set
			Set<String> deleteS3ObjectKey = new HashSet<>();

			// 更新処理
			service.updatePaymentPlanCondition(inputForm, tenantSeq, deleteS3ObjectKey);

			// 更新処理の中で削除したS3オブジェクトキーの削除処理
			this.deleteS3Object(deleteS3ObjectKey);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00045, "分割予定表を再作成"));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 支払計画のPDFプレビューを表示する
	 * 
	 * @param invoiceSeq
	 */
	@RequestMapping(value = "/planPreview", method = RequestMethod.GET)
	public void planPreview(@PathVariable(name = "invoiceSeq") Long invoiceSeq, HttpServletResponse response) {
		service.planPreview(invoiceSeq, response);
	}

	/**
	 * お振込先表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getBankDetailViewFragment", method = RequestMethod.GET)
	public ModelAndView getBankDetailViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// お振込先表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.BankDetailViewForm bankDetailViewForm = service.getBankDetailViewForm(accgDocSeq);
			mv = getMyModelAndView(bankDetailViewForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BANK_DETAIL_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BANK_DETAIL_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * お振込先入力フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getBankDetailInputFragment", method = RequestMethod.GET)
	public ModelAndView getBankDetailInputFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		try {
			// お振込先入力フラグメント情報取得
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.BankDetailInputForm bankDetailInputForm = service.getBankDetailInputForm(accgDocSeq);
			mv = getMyModelAndView(bankDetailInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BANK_DETAIL_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BANK_DETAIL_INPUT_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * お振込先入力フラグメント保存処理
	 * 
	 * @param invoiceSeq
	 * @param bankDetailInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveBankDetail", method = RequestMethod.POST)
	public ModelAndView saveBankDetail(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.BankDetailInputForm bankDetailInputForm, BindingResult result) {

		Long accgDocSeq;

		try {
			accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			// 相関チェック（請求書が発行されている場合は変更不可とする）
			if (!service.checkIfInvoiceIsDraftStatus(accgDocSeq)) {
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_W00016, "請求書", "編集"));
				return null;
			}
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		// バリデーションチェック
		if (result.hasErrors()) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(bankDetailInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BANK_DETAIL_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BANK_DETAIL_INPUT_FORM_NAME, result);
		}

		try {
			// お振込先データ保存処理
			service.saveBankDetail(accgDocSeq, bankDetailInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "お振込先"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

	}

	/**
	 * 備考表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getRemarksViewFragment", method = RequestMethod.GET)
	public ModelAndView getRemarksViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 備考表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.RemarksViewForm remarksViewForm = service.getRemarksViewForm(accgDocSeq);
			mv = getMyModelAndView(remarksViewForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_REMARKS_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_REMARKS_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 備考入力フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getRemarksInputFragment", method = RequestMethod.GET)
	public ModelAndView getRemarksInputFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 備考入力フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementInputForm.RemarksInputForm remarksInputForm = service.getRemarksInputForm(accgDocSeq);
			mv = getMyModelAndView(remarksInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_REMARKS_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_REMARKS_INPUT_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 備考入力フラグメント保存処理
	 * 
	 * @param invoiceSeq
	 * @param remarksInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveRemarks", method = RequestMethod.POST)
	public ModelAndView saveRemarks(@PathVariable(name = "invoiceSeq") Long invoiceSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.RemarksInputForm remarksInputForm, BindingResult result) {

		Long accgDocSeq;

		try {
			accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			// 相関チェック（請求書が発行されている場合は変更不可とする）
			if (!service.checkIfInvoiceIsDraftStatus(accgDocSeq)) {
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_W00016, "請求書", "編集"));
				return null;
			}
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		// バリデーションチェック
		if (result.hasErrors()) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(remarksInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_REMARKS_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_REMARKS_INPUT_FORM_NAME, result);
		}

		try {
			// 備考データ保存処理
			service.saveRemarks(accgDocSeq, remarksInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "備考"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

	}

	/**
	 * 取引状況表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getAccgDocSummaryViewFragment", method = RequestMethod.GET)
	public ModelAndView getAccgDocSummaryViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 取引状況表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgDocSummaryForm accgDocSummaryForm = service.getDocSummaryForm(accgDocSeq);
			mv = getMyModelAndView(accgDocSummaryForm,
					ViewPathAndAttrConstant.ACCG_DOC_SUMMARY_FRAGMENT_PATH,
					ViewPathAndAttrConstant.ACCG_DOC_SUMMARY_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 進行状況表示フラグメント取得
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@RequestMapping(value = "/getDocActivityViewFragment", method = RequestMethod.GET)
	public ModelAndView getDocActivityViewFragment(@PathVariable(name = "invoiceSeq") Long invoiceSeq) {

		ModelAndView mv = null;

		// 進行状況表示フラグメント情報取得
		try {
			Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
			AccgInvoiceStatementViewForm.DocActivityForm docActivityForm = service.getDocActivityForm(accgDocSeq);

			mv = getMyModelAndView(docActivityForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_DOC_ACTIVITY_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_DOC_ACTIVITY_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 「請求項目」がEnumに定義されている値か確認します。<br>
	 * Enumに定義されていない値だった場合はエラーとして true を返します。
	 * 
	 * @param invoiceType
	 * @param accgDocSeq
	 * @param result
	 * @return
	 */
	private boolean checkExistInvoiceTypeEnum(String invoiceType, Long accgDocSeq, BindingResult result) throws AppException {

		boolean errFlg = false;

		// 請求書が発行されている場合は、画面で変更できないためチェックをしない
		if (!service.checkIfInvoiceIsDraftStatus(accgDocSeq)) {
			return errFlg;
		}

		// 請求方法
		if (!SeikyuType.isExist(invoiceType)) {
			errFlg = true;
			result.rejectValue("invoiceType", null, getMessage(MessageEnum.VARIDATE_MSG_E00001));
		}

		return errFlg;
	}

	/**
	 * 既入金項目保存前のバリデーションチェック。
	 * 
	 * @param repayInputForm
	 * @param result
	 * @return
	 * @throws AppException
	 */
	private BindingResult validationBeforeRepaySave(AccgInvoiceStatementInputForm.RepayInputForm repayInputForm,
			BindingResult result) throws AppException {
		// バリデーションを実行
		validator.validate(repayInputForm, result);

		// 相関チェック（発行ステータスチェック、必須チェック、既入金上限チェック）
		Long ankenId = repayInputForm.getAnkenId();
		Long personId = repayInputForm.getPersonId();
		Long accgDocSeq = repayInputForm.getAccgDocSeq();
		service.validateRepayRowList(accgDocSeq, ankenId, personId, repayInputForm.getRepayRowList(), result);

		return result;
	}

	/**
	 * 請求項目保存前のバリデーションチェック。
	 * 
	 * @param invoiceInputForm
	 * @param invoiceSeq
	 * @param result
	 * @return
	 * @throws AppException
	 */
	private BindingResult validationBeforeInvoiceSave(AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm,
			Long invoiceSeq, BindingResult result) throws AppException {
		// 単価と時間から報酬額を算出
		service.calculateTimeCharge(invoiceInputForm);

		// バリデーションを実行
		validator.validate(invoiceInputForm, result);

		// 相関チェック
		Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
		service.validateInvoiceRowList(accgDocSeq, invoiceInputForm.getAnkenId(),
				invoiceInputForm.getPersonId(), invoiceInputForm.getInvoiceRowList(), result);

		return result;
	}

	/**
	 * 請求項目まとめ表示前のバリデーションチェック。
	 * 
	 * @param invoiceInputForm
	 * @param invoiceSeq
	 * @param mv
	 * @param result
	 * @return
	 * @throws AppException
	 */
	private BindingResult validationBeforeGroupOrUngroupSimilarInvoiceItems(InvoiceInputForm invoiceInputForm,
			Long invoiceSeq, ModelAndView mv, BindingResult result) throws AppException {
		// 相関チェック
		Long accgDocSeq = service.getAccgDocSeq(invoiceSeq);
		service.validationBeforeGroupOrUngroupSimilarInvoiceItems(accgDocSeq, invoiceInputForm.getAnkenId(),
				invoiceInputForm.getPersonId(), invoiceInputForm.getInvoiceRowList(), result);
		return result;
	}

	/**
	 * DBから削除したS3オブジェクトキーの実態ファイル削除
	 * 
	 * @param deleteS3ObjectKeys
	 */
	private void deleteS3Object(Set<String> deleteS3ObjectKeys) {
		service.deleteS3Object(deleteS3ObjectKeys);
	}

	/**
	 * PDF作成中Mvを取得
	 * 
	 * @param fileType
	 * @return
	 */
	private ModelAndView getPdfCreatingViewFragment(AccgDocFileType fileType) {
		return getMyModelAndView(
				new AccgInvoiceStatementInputForm.PdfCreatingViewForm(fileType),
				ViewPathAndAttrConstant.AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_PDF_CREATING_FRAGMENT_PATH,
				ViewPathAndAttrConstant.AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_PDF_CREATING_VIEW_FORM_NAME);
	}

}
