package jp.loioz.app.user.statementDetail.controller;

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
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.RepayInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementInputForm.RepayRowInputForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementViewForm;
import jp.loioz.app.common.form.accg.AccgInvoiceStatementViewForm.RepayViewForm;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.user.caseInvoiceStatementList.controller.CaseInvoiceStatementListController;
import jp.loioz.app.user.invoiceDetail.controller.InvoiceDetailController;
import jp.loioz.app.user.statementDetail.service.StatementDetailService;
import jp.loioz.app.user.statementList.controller.StatementListController;
import jp.loioz.common.constant.CommonConstant.AccgDocFileType;
import jp.loioz.common.constant.CommonConstant.AccgDocSendType;
import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.AccggInvoiceStatementDetailViewTab;
import jp.loioz.common.constant.CommonConstant.MessageLevel;
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
 * 精算書詳細画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/statementDetail/{statementSeq}")
public class StatementDetailController extends DefaultController {

	/** 精算書削除完了のヘッダーメッセージ */
	public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_STATEMENT_DELETE_SUCCESS_REDIRECT = "statement_delete_success_redirect";

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/statementDetail/statementDetail";

	/** 共通：請求書/精算書フラグメントパス */
	private static final String COMMON_ACCG_INVOICE_STATEMENT_FRAGMENT_PATH = "common/accg/accgInvoiceStatementFragment";
	
	/** 精算書詳細の既入金表示フラグメントのパス */
	private static final String STATEMEN_DETAIL_REPAY_VIEW_FRAGMENT_PATH = COMMON_ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::repayViewFragment";

	/** 精算書詳細の請求表示フラグメントのパス */
	private static final String STATEMEN_DETAIL_INVOICE_VIEW_FRAGMENT_PATH = COMMON_ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::invoiceViewFragment";

	/** 精算書詳細の請求入力フラグメントのパス */
	private static final String STATEMEN_DETAIL_INVOICE_INPUT_FRAGMENT_PATH = COMMON_ACCG_INVOICE_STATEMENT_FRAGMENT_PATH + "::invoiceInputFragment";
	
	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 精算書詳細の既入金表示で使用するフォームオブジェクト名 */
	private static final String STATEMEN_DETAIL_REPAY_VIEW_FORM_NAME = "repayViewForm";

	/** 精算書詳細の請求表示で使用するフォームオブジェクト名 */
	private static final String STATEMEN_DETAIL_INVOICE_VIEW_FORM_NAME = "invoiceViewForm";

	/** 精算書詳細の請求入力で使用するフォームオブジェクト名 */
	private static final String STATEMEN_DETAIL_INVOICE_INPUT_FORM_NAME = "invoiceInputForm";
	
	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyAccgInvoiceStatementInputForm";
	
	/** 精算書詳細のサービスクラス */
	@Autowired
	private StatementDetailService statementDetailService;

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

	/**
	 * 精算書詳細画面初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable(name = "statementSeq") Long statementSeq) {
		
		ModelAndView mv = null;
		
		try {
			AccgInvoiceStatementViewForm viewForm = statementDetailService.createStatementViewForm(statementSeq);
			mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		} catch (DataNotFoundException e) {
			String errMessage = StringUtils.lineBreakStr2Code(getMessage(MessageEnum.MSG_E00198));
			String redirectUrl = ModelAndViewUtils.getRedirectPath(StatementListController.class,
					controller -> controller.displayWhenStatementDetailsFails(null, null, errMessage));
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
	 * @param statementSeq
	 * @param levelCd
	 * @param message
	 * @param attributes
	 * @return
	 */
	@RequestMapping(value = "/redirectIndexWithMsg", method = RequestMethod.GET)
	public ModelAndView redirectIndexWithMsg(
			@PathVariable(name = "statementSeq") Long statementSeq,
			@RequestParam("level") String levelCd,
			@RequestParam("message") String message,
			RedirectAttributes attributes) {

		String redirectUrl = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.index(statementSeq));
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
	 * 精算書詳細フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getAccgInvoiceStatementDetailFragment", method = RequestMethod.GET)
	public ModelAndView getAccgInvoiceStatementDetailFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		// 精算書詳細情報取得
		try {
			AccgInvoiceStatementViewForm viewForm = statementDetailService.createStatementViewForm(statementSeq);
			mv = getMyModelAndView(viewForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_FORM_NAME);

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
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getAnkenViewFragment", method = RequestMethod.GET)
	public ModelAndView getAnkenViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		// 案件表示フラグメント情報取得
		try {
			AccgInvoiceStatementViewForm.AnkenForm ankenForm = statementDetailService.getAnkenForm(statementSeq);
			mv = getMyModelAndView(ankenForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_ANKEN_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_ANKEN_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// ajaxでのDataNotFoundExceptionは、楽観ロックエラーとする
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
	 * 精算書発行前のチェック
	 * 
	 * @param statementSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/checkOfBeforeIssue", method = RequestMethod.POST)
	public Map<String, Object> checkOfBeforeIssue(@PathVariable(name = "statementSeq") Long statementSeq) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 発行前チェック
			statementDetailService.checkOfBeforeIssue(AccgDocType.STATEMENT, statementSeq);

			// 精算額が0以下の場合、請求書に変更するか画面で確認
			if (statementDetailService.checkIfStatementAmountIsMinus(statementSeq)) {
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
	 * @param statementSeq
	 * @param ankenId
	 * @param personId
	 * @param formatChangeFlg フォーマット変更フラグ。true:請求書に変更, false：精算書のまま
	 * @return
	 */
	@RequestMapping(value = "/issue", method = RequestMethod.POST)
	public ModelAndView issue(
			@PathVariable(name = "statementSeq") Long statementSeq,
			@RequestParam("ankenId") Long ankenId,
			@RequestParam("personId") Long personId,
			@RequestParam("formatChangeFlg") boolean formatChangeFlg) {

		// 発行処理
		Long tenantSeq = SessionUtils.getTenantSeq();
		try {
			// 発行前チェック
			statementDetailService.checkOfBeforeIssue(AccgDocType.STATEMENT, statementSeq, formatChangeFlg);

			// 削除するS3オブジェクトキーの格納用Set
			Set<String> deleteS3ObjectKeys = new HashSet<>();

			if (formatChangeFlg) {
				// 精算書 から 請求書への変更を行う場合 -> 種別変更処理
				Long invoiceSeq = statementDetailService.changeToInvoice(statementSeq, ankenId, deleteS3ObjectKeys);

				// 登録処理の中で削除したS3オブジェクトキーの削除処理
				this.deleteS3Object(deleteS3ObjectKeys);

				// 完了後のリダイレクトパスを作成
				String redirectUrl = ModelAndViewUtils.getRedirectPath(
						InvoiceDetailController.class,
						(controller) -> controller.redirectIndexWithMsg(invoiceSeq, MessageLevel.INFO.getCd(), getMessage(MessageEnum.MSG_I00138, "精算書", "請求書"), null));

				// 処理完了メッセージとして、リダイレクトURLを設定
				// 完了処理が入力パラメータによって分岐するため、例外的にこの実装とする)
				super.setAjaxProcResultSuccess(redirectUrl);
				return null;

			} else {
				// 種別の変更を行わない場合 -> 発行処理
				// 請求書への変更処理では、発行ではなく下書き状態となるのでPDFを作成しなくても、精算書画面からの発行 or タブ選択時に新たにPDFが作成される想定のため

				// 発行処理の前段処理(精算書PDFの再作成)
				statementDetailService.beforeTransactionalDocStatementPdfCreate(statementSeq, tenantSeq, deleteS3ObjectKeys);

				// 発行処理の前段処理(実費明細PDFの再作成)
				statementDetailService.beforeTransactionalDipositRecordPdfCreate(statementSeq, tenantSeq, deleteS3ObjectKeys);

				// 発行処理
				statementDetailService.issue(statementSeq, ankenId, personId);

				// 登録処理の中で削除したS3オブジェクトキーの削除処理
				this.deleteS3Object(deleteS3ObjectKeys);

				// 発行完了のメッセージを表示
				super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00136, "精算書"));
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
	 * 発行前に戻す処理
	 * 
	 * @param statementSeq
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/updateStatementToDraftAndRemoveRelatedData", method = RequestMethod.POST)
	public ModelAndView updateStatementToDraftAndRemoveRelatedData(
			@PathVariable(name = "statementSeq") Long statementSeq,
			@RequestParam("ankenId") Long ankenId,
			@RequestParam("personId") Long personId) {

		try {
			// 発行前に戻す
			statementDetailService.updateStatementToDraftAndRemoveRelatedData(statementSeq, ankenId, personId);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00137, "精算書"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 削除前のチェック処理をおこないます。
	 * 
	 * @param statementSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/checkOfBeforeDelete", method = RequestMethod.GET)
	public Map<String, Object> checkOfBeforeDelete(@PathVariable(name = "statementSeq") Long statementSeq) {

		Map<String, Object> response = new HashMap<>();

		Long accgDocSeq;
		try {
			accgDocSeq = statementDetailService.getAccgDocSeq(statementSeq);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return response;
		}
		
		// 送付済みの書類があるか確認
		if (statementDetailService.checkIfAccgDocHasBeenSent(accgDocSeq)) {
			// 書類を送付している場合は削除不可とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00086, "送付済みの"));
			return response;
		}

		super.setAjaxProcResultSuccess();
		return response;
	}

	/**
	 * 精算書削除処理
	 * 
	 * @param statementSeq
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ModelAndView delete(@PathVariable(name = "statementSeq") Long statementSeq,
			@RequestParam("ankenId") Long ankenId, @RequestParam("personId") Long personId) {

		try {
			// 削除処理
			statementDetailService.delete(statementSeq, ankenId, personId);

			String redirectUrl;
			if (uriService.currentRequestAnkenSideParamHasTrue()) {
				redirectUrl = ModelAndViewUtils.getRedirectPath(CaseInvoiceStatementListController.class, controller -> controller.redirectIndexWithMsg(personId, ankenId, MessageLevel.INFO.getCd(), getMessage(MessageEnum.MSG_I00028, "精算書"), null));
			} else {
				redirectUrl = ModelAndViewUtils.getRedirectPath(StatementListController.class, controller -> controller.redirectIndexWithMsg(MessageLevel.INFO.getCd(), getMessage(MessageEnum.MSG_I00028, "精算書"), null));
			}
			super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_STATEMENT_DELETE_SUCCESS_REDIRECT, redirectUrl);
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 精算書_送付入力用画面情報の取得
	 *
	 * @param statementSeq
	 * @param sendTypeCd
	 * @return
	 */
	@RequestMapping(value = "/getStatementAccgDocFileSendInputFragment", method = RequestMethod.GET)
	public ModelAndView getStatementAccgDocFileSendInputFragment(
			@PathVariable(name = "statementSeq") Long statementSeq,
			@RequestParam(name = "sendType") String sendTypeCd,
			@RequestParam(name = "mailTemplateSeq", required = false) Long mailTemplateSeq) {
		ModelAndView mv = null;

		// ログインユーザーSEQを取得
		Long accountSeq = SessionUtils.getLoginAccountSeq();
		String tenantName = SessionUtils.getTenantName();

		try {
			// メール/WEB共有入力用オブジェクトを作成する
			AccgInvoiceStatementInputForm.FileSendInputForm inputForm = statementDetailService.createStatementFileSendInputForm(statementSeq, AccgDocSendType.of(sendTypeCd), accountSeq, tenantName, mailTemplateSeq);
			mv = getMyModelAndView(inputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FORM_NAME);

		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
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
	 * 精算書＿送付確認表示 入力フォームに戻る
	 * 
	 * @param inputForm
	 * @return
	 */
	@RequestMapping(value = "/getReturnStatementAccgDocFileSendInputFragment", method = RequestMethod.POST)
	public ModelAndView getReturnStatementAccgDocFileSendInputFragment(AccgInvoiceStatementInputForm.FileSendInputForm inputForm) {

		// 精算書書入力フォームの表示用プロパティの設定
		statementDetailService.setDispProperties(inputForm);

		super.setAjaxProcResultSuccess();
		return getMyModelAndView(inputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FORM_NAME);
	}

	/**
	 * 精算書_送付プレビュー画面情報の取得
	 *
	 * @param statementSeq
	 * @param sendTypeCd
	 * @return
	 */
	@RequestMapping(value = "/getStatementAccgDocFileSendPreviewFragment", method = RequestMethod.POST)
	public ModelAndView getStatementAccgDocFileSendPreviewFragment(@PathVariable(name = "statementSeq") Long statementSeq, @Validated AccgInvoiceStatementInputForm.FileSendInputForm inputForm, BindingResult result) {

		// 入力チェック
		inputForm.rejectValues(result);

		// 表示用プロパティの設定
		statementDetailService.setDispProperties(inputForm);

		// プレビュー表示用プロパティの設定
		statementDetailService.setAccgDocFileSendPreviewDispProperties(inputForm);

		if (result.hasErrors()) {
			// バリデーションチェックに引っかかる場合は、確認画面は表示せず入力エラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FORM_NAME, result);
		}

		// 確認画面を表示する
		super.setAjaxProcResultSuccess();
		return getMyModelAndView(inputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_PREVIEW_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FORM_NAME);
	}

	/**
	 * 精算書画面_送付処理
	 *
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/accgStatementFileSend", method = RequestMethod.POST)
	public ModelAndView accgStatementFileSend(@Validated AccgInvoiceStatementInputForm.FileSendInputForm inputForm, BindingResult result) {

		Long tenantSeq = SessionUtils.getTenantSeq();
		Long accountSeq = SessionUtils.getLoginAccountSeq();

		// 入力チェック
		inputForm.rejectValues(result);

		if (result.hasErrors()) {
			statementDetailService.setDispProperties(inputForm);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_FILE_SEND_INPUT_FORM_NAME, result);
		}

		try {
			// 送付処理
			statementDetailService.accgStatementFileSend(tenantSeq, accountSeq, inputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00140, "メール", "送信"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 精算書：印刷して送付フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getFilePrintSendViewForm", method = RequestMethod.GET)
	public ModelAndView getFilePrintSendViewForm(@PathVariable(name = "statementSeq") Long statementSeq) {

		// 印刷して送付フラグメントフラグメント情報取得
		try {
			AccgInvoiceStatementViewForm.FilePrintSendViewForm filePrintSendViewForm = statementDetailService.getFilePrintSendViewForm(statementSeq);
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
	 * 精算書：送付ファイルのダウンロード
	 * 
	 * @param statementSeq
	 * @param response
	 */
	@RequestMapping(value = "/printDownload", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void printDownload(@PathVariable(name = "statementSeq") Long statementSeq, HttpServletResponse response) {

		// 精算書のダウンロード
		try {
			statementDetailService.printDownload(statementSeq, response);

		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
		} catch (Exception e) {
			// 想定しないダウンロードエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00034));
		}
	}

	/**
	 * 精算書：送付ファイルをダウンロードして、送付済みにする
	 * 
	 * @param statementSeq
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/downloadAndChangeToSend", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public ModelAndView downloadAndChangeToSend(@PathVariable(name = "statementSeq") Long statementSeq, HttpServletResponse response) {

		// 精算書を送付ファイルをダウンロードして、送付済みにする
		try {
			statementDetailService.downloadAndChangeToSend(statementSeq, response);
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
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getSettingViewFragment", method = RequestMethod.GET)
	public ModelAndView getSettingViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		// 設定表示フラグメント情報取得
		try {
			AccgInvoiceStatementViewForm.SettingViewForm settingViewForm = statementDetailService.getSettingViewForm(statementSeq);
			mv = getMyModelAndView(settingViewForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_SETTING_VIEW_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_SETTING_VIEW_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// ajaxでのDataNotFoundExceptionは、楽観ロックエラーとする
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
	 * 設定入力フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getSettingInputFragment", method = RequestMethod.GET)
	public ModelAndView getSettingInputFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		// 設定入力フラグメント情報取得
		try {
			AccgInvoiceStatementInputForm.SettingInputForm settingInputForm = statementDetailService.getSettingInputForm(statementSeq);
			mv = getMyModelAndView(settingInputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_SETTING_INPUT_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_SETTING_INPUT_FORM_NAME);
		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
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
	 * @param statementSeq
	 * @param settingInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
	public ModelAndView saveSetting(
			@PathVariable(name = "statementSeq") Long statementSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.SettingInputForm settingInputForm,
			BindingResult result) {

		// バリデーションチェック
		try {
			if (result.hasErrors()) {
				// 売上計上先選択リストをセット
				statementDetailService.setDispProperties(statementSeq, settingInputForm);
				// 処理失敗
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(settingInputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_SETTING_INPUT_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_SETTING_INPUT_FORM_NAME, result);
			}

			// 設定データ保存処理
			statementDetailService.saveSetting(statementSeq, settingInputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "設定"));
			return null;

		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}
	}

	/**
	 * 内部メモ表示フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getMemoViewFragment", method = RequestMethod.GET)
	public ModelAndView getMemoViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		// 設定表示フラグメント情報取得
		try {
			AccgInvoiceStatementViewForm.MemoViewForm memoViewForm = statementDetailService.getMemoViewForm(statementSeq);
			mv = getMyModelAndView(memoViewForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_MEMO_VIEW_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_MEMO_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// ajaxでのDataNotFoundExceptionは、楽観ロックエラーとする
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
	 * 内部メモ入力フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getMemoInputFragment", method = RequestMethod.GET)
	public ModelAndView getMemoInputFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		// 設定入力フラグメント情報取得
		try {
			AccgInvoiceStatementInputForm.MemoInputForm memoInputForm = statementDetailService.getMemoInputForm(statementSeq);
			mv = getMyModelAndView(memoInputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_MEMO_INPUT_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_MEMO_INPUT_FORM_NAME);
		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
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
	 * @param statementSeq
	 * @param memoInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveMemo", method = RequestMethod.POST)
	public ModelAndView saveMemo(
			@PathVariable(name = "statementSeq") Long statementSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.MemoInputForm memoInputForm,
			BindingResult result) {

		try {

			// 内部メモデータ保存処理
			statementDetailService.saveMemo(statementSeq, memoInputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "内部メモ"));
			return null;

		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}
	}

	/**
	 * タブ表示フラグメント取得
	 * 
	 * @param statementSeq
	 * @param selectedTabCd
	 * @return
	 */
	@RequestMapping(value = "/getDocContentsViewFragment", method = RequestMethod.GET)
	public ModelAndView getDocContentsViewFragment(@PathVariable(name = "statementSeq") Long statementSeq, @RequestParam(name = "selectedTabCd", required = false) String selectedTabCd) {

		ModelAndView mv = null;

		AccggInvoiceStatementDetailViewTab selectedTab = AccggInvoiceStatementDetailViewTab.of(selectedTabCd);

		// タブ表示フラグメント情報取得
		try {
			AccgInvoiceStatementViewForm.DocContentsForm docContentsForm = statementDetailService.getDocContentsForm(statementSeq, selectedTab);
			mv = getMyModelAndView(docContentsForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_DOC_CONTENTS_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_DOC_CONTENTS_FORM_NAME);

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
	 * 基本情報_タイトル表示フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseTitleViewFragment", method = RequestMethod.GET)
	public ModelAndView getBaseTitleViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		// 基本情報_タイトル表示フラグメント情報取得
		try {
			AccgInvoiceStatementViewForm.BaseTitleViewForm baseTitleViewForm = statementDetailService.getBaseTitleViewForm(statementSeq);
			mv = getMyModelAndView(baseTitleViewForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TITLE_VIEW_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TITLE_VIEW_FORM_NAME);
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
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseTitleInputFragment", method = RequestMethod.GET)
	public ModelAndView getBaseTitleInputFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		// 基本情報_タイトル入力フラグメント情報取得
		try {
			AccgInvoiceStatementInputForm.BaseTitleInputForm baseTitleInputForm = statementDetailService.getBaseTitleInputForm(statementSeq);
			mv = getMyModelAndView(baseTitleInputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TITLE_INPUT_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TITLE_INPUT_FORM_NAME);
		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
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
	 * @param statementSeq
	 * @param baseTitleInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveBaseTitle", method = RequestMethod.POST)
	public ModelAndView saveBaseTitle(
			@PathVariable(name = "statementSeq") Long statementSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.BaseTitleInputForm baseTitleInputForm,
			BindingResult result) {

		if (result.hasErrors()) {
			// バリデーションチェック
			// 表示用項目の設定
			statementDetailService.setDispProperties(baseTitleInputForm);
			// エラーメッセージをResponseに設定
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(baseTitleInputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TITLE_INPUT_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TITLE_INPUT_FORM_NAME, result);
		}

		try {
			// 基本情報_タイトルデータ保存処理
			statementDetailService.saveBaseTitle(statementSeq, baseTitleInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "基本情報"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 基本情報_請求先表示フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseToViewFragment", method = RequestMethod.GET)
	public ModelAndView getBaseToViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		try {
			// 基本情報_請求先表示フラグメント情報取得
			AccgInvoiceStatementViewForm.BaseToViewForm baseToViewForm = statementDetailService.getBaseToViewForm(statementSeq);
			mv = getMyModelAndView(baseToViewForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TO_VIEW_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TO_VIEW_FORM_NAME);

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
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseToInputFragment", method = RequestMethod.GET)
	public ModelAndView getBaseToInputFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		try {
			// 基本情報_請求先入力フラグメント情報取得
			AccgInvoiceStatementInputForm.BaseToInputForm baseToInputForm = statementDetailService.getBaseToInputForm(statementSeq);
			mv = getMyModelAndView(baseToInputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TO_INPUT_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TO_INPUT_FORM_NAME);

		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
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
	public Map<String, Object> getBaseToNameAndDetail(@PathVariable(name = "statementSeq") Long statementSeq,
			@RequestParam(name = "personId") Long personId) {

		Map<String, Object> response = new HashMap<>();

		try {
			BaseToInputForm form = statementDetailService.getBaseToNameAndDetail(personId);
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
	 * @param statementSeq
	 * @param baseToInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveBaseTo", method = RequestMethod.POST)
	public ModelAndView saveBaseTo(
			@PathVariable(name = "statementSeq") Long statementSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.BaseToInputForm baseToInputForm,
			BindingResult result) {

		try {
			// バリデーションチェック
			if (result.hasErrors()) {
				// 表示用プロパティの設定
				statementDetailService.setDispProperties(baseToInputForm);
				// エラーメッセージを設定
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(baseToInputForm,
						AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TO_INPUT_FRAGMENT_PATH,
						AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_TO_INPUT_FORM_NAME, result);
			}

			// 基本情報_請求先データ保存処理
			statementDetailService.saveBaseTo(statementSeq, baseToInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "基本情報"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 基本情報_請求元表示フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseFromViewFragment", method = RequestMethod.GET)
	public ModelAndView getBaseFromViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		try {
			// 基本情報_請求元表示フラグメント情報取得
			AccgInvoiceStatementViewForm.BaseFromViewForm baseFromViewForm = statementDetailService.getBaseFromViewForm(statementSeq);
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
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseFromInputFragment", method = RequestMethod.GET)
	public ModelAndView getBaseFromInputFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		try {
			// 基本情報_請求先入力フラグメント情報取得
			AccgInvoiceStatementInputForm.BaseFromInputForm baseFromInputForm = statementDetailService.getBaseFromInputForm(statementSeq);
			mv = getMyModelAndView(baseFromInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_FROM_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_FROM_INPUT_FORM_NAME);

		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
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
	 * @param statementSeq
	 * @param baseFromInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveBaseFrom", method = RequestMethod.POST)
	public ModelAndView saveBaseFrom(
			@PathVariable(name = "statementSeq") Long statementSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.BaseFromInputForm baseFromInputForm,
			BindingResult result) {

		// バリデーションチェック
		if (result.hasErrors()) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(baseFromInputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_FROM_INPUT_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_FROM_INPUT_FORM_NAME, result);
		}

		try {
			// 基本情報_請求元データ保存処理
			statementDetailService.saveBaseFrom(statementSeq, baseFromInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "基本情報"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 基本情報_挿入文表示フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseOtherViewFragment", method = RequestMethod.GET)
	public ModelAndView getBaseOtherViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		try {
			// 基本情報_挿入文表示フラグメント情報取得
			AccgInvoiceStatementViewForm.BaseOtherViewForm baseOtherViewForm = statementDetailService.getBaseOtherViewForm(statementSeq);
			mv = getMyModelAndView(baseOtherViewForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_OTHER_VIEW_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_OTHER_VIEW_FORM_NAME);

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
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getBaseOtherInputFragment", method = RequestMethod.GET)
	public ModelAndView getBaseOtherInputFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		try {
			// 基本情報_挿入文入力フラグメント情報取得
			AccgInvoiceStatementInputForm.BaseOtherInputForm baseOtherInputForm = statementDetailService.getBaseOtherInputForm(statementSeq);
			mv = getMyModelAndView(baseOtherInputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_OTHER_INPUT_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_OTHER_INPUT_FORM_NAME);

		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
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
	 * @param statementSeq
	 * @param baseOtherInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveBaseOther", method = RequestMethod.POST)
	public ModelAndView saveBaseOther(
			@PathVariable(name = "statementSeq") Long statementSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.BaseOtherInputForm baseOtherInputForm,
			BindingResult result) {

		// バリデーションチェック
		if (result.hasErrors()) {
			// 表示用プロパティの設定
			statementDetailService.setDispProperties(baseOtherInputForm);
			// エラーメッセージを設定
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(baseOtherInputForm, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_OTHER_INPUT_FRAGMENT_PATH, AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BASE_OTHER_INPUT_FORM_NAME, result);
		}

		try {
			// 基本情報_挿入文データ保存処理
			statementDetailService.saveBaseOther(statementSeq, baseOtherInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "基本情報"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 既入金表示フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getRepayViewFragment", method = RequestMethod.GET)
	public ModelAndView getRepayViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		// 既入金表示フラグメント情報取得
		try {
			Long accgDocSeq = statementDetailService.getAccgDocSeq(statementSeq);
			RepayViewForm repayViewForm = statementDetailService.getRepayViewForm(accgDocSeq);
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
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getRepayInputFragment", method = RequestMethod.GET)
	public ModelAndView getRepayInputFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		// 既入金入力フラグメント情報取得
		try {
			Long accgDocSeq = statementDetailService.getAccgDocSeq(statementSeq);
			RepayInputForm repayInputForm = statementDetailService.getRepayInputForm(accgDocSeq);
			mv = getMyModelAndView(repayInputForm,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.INVOICE_DETAIL_REPAY_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
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
	 * 既入金項目のまとめ表示の設定 or 解除をおこないます。<br>
	 * 既入金項目の並び替えをしている場合に画面上の表示順とrepayInputFormの行データのセット順が異なるため<br>
	 * バリデーションチェックは、@Validatedを使用せず、 ソート（repayInputFormの行データを画面上の表示順に合わせた）後にバリデーションを実行する。<br>
	 * （@Validatedを使用した場合に、repayInputFormやBindengResultの表示順を合わせることが難しいため）
	 * 
	 * @param statementSeq
	 * @param repayInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/groupOrUngroupSimilarRepayItems", method = RequestMethod.POST)
	public ModelAndView groupOrUngroupSimilarRepayItems(@PathVariable(name = "statementSeq") Long statementSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.RepayInputForm repayInputForm,
			BindingResult result) {
		
		ModelAndView mv = null;
		
		List<RepayRowInputForm> repayRowList = repayInputForm.getRepayRowList();
		if (LoiozCollectionUtils.isNotEmpty(repayRowList)) {
			repayRowList = repayRowList.stream().filter(row -> row.getDocRepayOrder() != null).collect(Collectors.toList());
		}
		
		// docRepayOrder順にソート
		repayRowList = statementDetailService.sortRepayList(repayRowList);
		
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
		List<RepayRowInputForm> repayInputFormList = statementDetailService.groupOrUngroupSimilarRepayItems(repayInputForm);

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
	 * @param statementSeq
	 * @param repayInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveRepay", method = RequestMethod.POST)
	public ModelAndView saveRepay(@PathVariable(name = "statementSeq") Long statementSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.RepayInputForm repayInputForm,
			BindingResult result) {
		
		ModelAndView mv = null;
		Long accgDocSeq = repayInputForm.getAccgDocSeq();
		
		// 有効な行を取得
		List<RepayRowInputForm> repayRowList = statementDetailService.getEnabledRepayList(repayInputForm.getRepayRowList());
		
		// docRepayOrder順にソート
		repayRowList = statementDetailService.sortRepayList(repayRowList);
		
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
			statementDetailService.saveRepay(statementSeq, repayInputForm);
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
			AccgInvoiceStatementViewForm.RepayViewForm repayViewForm = statementDetailService.getRepayViewForm(accgDocSeq);
			mv = getMyModelAndView(repayViewForm, STATEMEN_DETAIL_REPAY_VIEW_FRAGMENT_PATH, STATEMEN_DETAIL_REPAY_VIEW_FORM_NAME);
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
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getInvoiceViewFragment", method = RequestMethod.GET)
	public ModelAndView getInvoiceViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		// 請求表示フラグメント情報取得
		try {
			AccgInvoiceStatementViewForm.InvoiceViewForm invoiceViewForm = statementDetailService.getInvoiceViewForm(statementSeq);
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
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getInvoiceInputFragment", method = RequestMethod.GET)
	public ModelAndView getInvoiceInputFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		// 請求入力フラグメント情報取得
		try {
			Long accgDocSeq = statementDetailService.getAccgDocSeq(statementSeq);
			AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm = statementDetailService.getInvoiceInputForm(accgDocSeq);
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
	 * @param statementSeq
	 * @param invoiceInputForm
	 * @return
	 */
	@RequestMapping(value = "/getInvoiceTotalAmountInputFragment", method = RequestMethod.POST)
	public ModelAndView getInvoiceTotalAmountInputFragment(@PathVariable(name = "statementSeq") Long statementSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm) {

		ModelAndView mv = null;

		// 請求入力合計額フラグメント情報取得
		try {
			Long accgDocSeq = statementDetailService.getAccgDocSeq(statementSeq);
			List<InvoiceRowInputForm> invoiceRowInputFormList = invoiceInputForm.getInvoiceRowList();

			AccgInvoiceStatementInputForm.InvoiceTotalAmountInputForm invoiceTotalAmountInputForm = statementDetailService.getInvoiceTotalAmountInputForm(accgDocSeq, invoiceRowInputFormList);
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
	 * @param statementSeq
	 * @param repayInputForm
	 * @param depositRecvSeqList
	 * @return
	 */
	@RequestMapping(value = "/addNyukinDepositToRepayInput", method = RequestMethod.POST)
	public ModelAndView addNyukinDepositToRepayInput(@PathVariable(name = "statementSeq") Long statementSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.RepayInputForm repayInputForm,
			@RequestParam(name = "depositRecvSeqList") List<Long> depositRecvSeqList) {

		ModelAndView mv = null;

		List<RepayRowInputForm> repayRowList = repayInputForm.getRepayRowList();
		if (LoiozCollectionUtils.isNotEmpty(repayRowList)) {
			repayRowList = repayInputForm.getRepayRowList().stream().filter(row -> row.getDocRepayOrder() != null).collect(Collectors.toList());
		}

		// docRepayOrder順にソート
		repayRowList = statementDetailService.sortRepayList(repayRowList);

		repayInputForm.setRepayRowList(repayRowList);

		// 既入金入力フラグメント情報取得
		try {
			List<RepayRowInputForm> repayInputFormList = statementDetailService.addNyukinDepositToRepayInput(repayInputForm, depositRecvSeqList);
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
	 * @param statementSeq
	 * @param invoiceRowCount
	 * @return
	 */
	@RequestMapping(value = "/addFeeRow", method = RequestMethod.GET)
	public ModelAndView addFeeRow(@PathVariable(name = "statementSeq") Long statementSeq,
			@RequestParam("invoiceRowCount") Long invoiceRowCount) {

		ModelAndView mv = null;

		try {
			Long accgDocSeq = statementDetailService.getAccgDocSeq(statementSeq);
			AccgInvoiceStatementInputForm.InvoiceRowInputForm inputForm = statementDetailService.createNewFeeRowInputForm(accgDocSeq, invoiceRowCount);
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
	 * @param statementSeq
	 * @param unPaidFeeSeqList
	 * @param invoiceRowCount
	 * @return
	 */
	@RequestMapping(value = "/addUnPaidFee", method = RequestMethod.POST)
	public ModelAndView addUnPaidFee(@PathVariable(name = "statementSeq") Long statementSeq,
			@RequestParam("unPaidFeeSeqList") List<Long> unPaidFeeSeqList,
			@RequestParam("invoiceRowCount") Long invoiceRowCount) {
		
		ModelAndView mv = null;
		
		try {
			Long accgDocSeq = statementDetailService.getAccgDocSeq(statementSeq);
			List<InvoiceRowInputForm> inputFormList = statementDetailService.createNewUnPaidFeeRowInputForm(accgDocSeq, invoiceRowCount, unPaidFeeSeqList);
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
	 * @param statementSeq
	 * @param invoiceRowCount
	 * @return
	 */
	@RequestMapping(value = "/addDepositRecvRow", method = RequestMethod.GET)
	public ModelAndView addDepositRecvRow(@PathVariable(name = "statementSeq") Long statementSeq,
			@RequestParam("invoiceRowCount") Long invoiceRowCount) {

		ModelAndView mv = null;

		try {
			Long accgDocSeq = statementDetailService.getAccgDocSeq(statementSeq);
			AccgInvoiceStatementInputForm.InvoiceRowInputForm inputForm = statementDetailService.createNewDepositRecvRowInputForm(accgDocSeq, invoiceRowCount);
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
	 * @param statementSeq
	 * @param invoiceInputForm
	 * @param depositRecvSeqList
	 * @return
	 */
	@RequestMapping(value = "/addShukkinDepositToInvoiceInput", method = RequestMethod.POST)
	public ModelAndView addShukkinDepositToInvoiceInput(@PathVariable(name = "statementSeq") Long statementSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm,
			@RequestParam(name = "depositRecvSeqList") List<Long> depositRecvSeqList) {

		ModelAndView mv = null;

		List<InvoiceRowInputForm> invoiceRowList = invoiceInputForm.getInvoiceRowList();
		if (LoiozCollectionUtils.isNotEmpty(invoiceRowList)) {
			invoiceRowList = invoiceInputForm.getInvoiceRowList().stream().filter(row -> row.getDocInvoiceOrder() != null).collect(Collectors.toList());
		}

		// docInvoiceOrder順にソート
		invoiceRowList = statementDetailService.sortInvoiceList(invoiceRowList);

		invoiceInputForm.setInvoiceRowList(invoiceRowList);

		try {
			List<InvoiceRowInputForm> invoiceInputFormList = statementDetailService.addShukkinDepositToInvoiceInput(invoiceInputForm, depositRecvSeqList);
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
	 * @param statementSeq
	 * @param invoiceRowCount
	 * @return
	 */
	@RequestMapping(value = "/addDiscountRow", method = RequestMethod.GET)
	public ModelAndView addDiscountRow(@PathVariable(name = "statementSeq") Long statementSeq,
			@RequestParam("invoiceRowCount") Long invoiceRowCount) {

		ModelAndView mv = null;

		try {
			Long accgDocSeq = statementDetailService.getAccgDocSeq(statementSeq);
			AccgInvoiceStatementInputForm.InvoiceRowInputForm inputForm = statementDetailService.createNewDiscountRowInputForm(accgDocSeq, invoiceRowCount);
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
	 * @param statementSeq
	 * @param invoiceRowCount
	 * @return
	 */
	@RequestMapping(value = "/addTextRow", method = RequestMethod.GET)
	public ModelAndView addTextRow(@PathVariable(name = "statementSeq") Long statementSeq,
			@RequestParam("invoiceRowCount") Long invoiceRowCount) {

		ModelAndView mv = null;

		try {
			Long accgDocSeq = statementDetailService.getAccgDocSeq(statementSeq);
			AccgInvoiceStatementInputForm.InvoiceRowInputForm inputForm = statementDetailService.createNewTextRowInputForm(accgDocSeq, invoiceRowCount);
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
	 * 請求項目のまとめ表示の設定 or 解除をおこないます。<br>
	 * 請求項目の並び替えをしている場合に画面上の表示順とinvoiceInputFormの行データのセット順が異なるため<br>
	 * バリデーションチェックは、@Validatedを使用せず、 ソート（invoiceInputFormの行データを画面上の表示順に合わせた）後にバリデーションを実行する。<br>
	 * （@Validatedを使用した場合に、invoiceInputFormやBindengResultの表示順を合わせることが難しいため）
	 * 
	 * @param statementSeq
	 * @param repayInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/groupOrUngroupSimilarInvoiceItems", method = RequestMethod.POST)
	public ModelAndView groupOrUngroupSimilarInvoiceItems(@PathVariable(name = "statementSeq") Long statementSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm,
			BindingResult result) {
		
		ModelAndView mv = null;
		
		List<InvoiceRowInputForm> invoiceRowList = invoiceInputForm.getInvoiceRowList();
		if (LoiozCollectionUtils.isNotEmpty(invoiceRowList)) {
			invoiceRowList = invoiceRowList.stream().filter(row -> row.getDocInvoiceOrder() != null).collect(Collectors.toList());
		}
		
		// docInvoiceOrder順にソート
		invoiceRowList = statementDetailService.sortInvoiceList(invoiceRowList);
		
		invoiceInputForm.setInvoiceRowList(invoiceRowList);
		
		// 相関チェック
		try {
			// 相関チェック（請求書発行ステータスチェック、請求額上限チェック、実費データのみバリデーションチェック）
			this.validationBeforeGroupOrUngroupSimilarInvoiceItems(invoiceInputForm, statementSeq, mv, result);
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
		List<InvoiceRowInputForm> invoiceInputFormList = statementDetailService.groupOrUngroupSimilarInvoiceItems(invoiceInputForm);
		
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
	 * @param statementSeq
	 * @param invoiceInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveInvoice", method = RequestMethod.POST)
	public ModelAndView saveInvoice(@PathVariable(name = "statementSeq") Long statementSeq,
			@ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm,
			BindingResult result) {
		
		ModelAndView mv = null;
		
		// 有効な行を取得
		List<InvoiceRowInputForm> invoiceRowList = statementDetailService.getEnabledInvoiceList(invoiceInputForm.getInvoiceRowList());
		
		// docInvoiceOrder順にソート
		invoiceRowList = statementDetailService.sortInvoiceList(invoiceRowList);
		
		invoiceInputForm.setInvoiceRowList(invoiceRowList);
		
		// バリデーションチェック
		try {
			result = this.validationBeforeInvoiceSave(invoiceInputForm, statementSeq, result);
			
			if (result.hasErrors()) {
				// 処理失敗
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(invoiceInputForm, STATEMEN_DETAIL_INVOICE_INPUT_FRAGMENT_PATH, STATEMEN_DETAIL_INVOICE_INPUT_FORM_NAME, result);
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
		try {
			statementDetailService.saveInvoice(statementSeq, invoiceInputForm);
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
			AccgInvoiceStatementViewForm.InvoiceViewForm invoiceViewForm = statementDetailService.getInvoiceViewForm(statementSeq);
			mv = getMyModelAndView(invoiceViewForm, STATEMEN_DETAIL_INVOICE_VIEW_FRAGMENT_PATH, STATEMEN_DETAIL_INVOICE_VIEW_FORM_NAME);
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
	 * @param statementSeq
	 * @param timeChargeTanka
	 * @param timeChargeTime
	 * @param index
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/calculateInputTimeCharge", method = RequestMethod.GET)
	public Map<String, Object> calculateInputTimeCharge(@PathVariable(name = "statementSeq") Long statementSeq,
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
				BigDecimal feeAmount = statementDetailService.calculateTimeCharge(tanka, decimalMinutes);
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
	 * @param statementSeq
	 * @param searchWord
	 * @return
	 */
	@RequestMapping(value = "/searchFeeItemName", method = RequestMethod.GET)
	public ModelAndView searchFeeItemName(@PathVariable(name = "statementSeq") Long statementSeq,
			@RequestParam("searchWord") String searchWord) {

		ModelAndView mv = null;

		List<SelectOptionForm> itemList = statementDetailService.searchFeeDataList(searchWord);
		mv = getMyModelAndView(itemList,
				AccgInvociceStatementPathAndAttrConstant.ITEM_LIST_FRAGMENT_PATH,
				AccgInvociceStatementPathAndAttrConstant.ITEM_LIST_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金項目の候補を取得
	 * 
	 * @param statementSeq
	 * @param searchWord
	 * @param depositType
	 * @return
	 */
	@RequestMapping(value = "/searchDepositRecvItemName", method = RequestMethod.GET)
	public ModelAndView searchDepositRecvItemName(@PathVariable(name = "statementSeq") Long statementSeq,
			@RequestParam("searchWord") String searchWord, @RequestParam("depositType") String depositType) {

		ModelAndView mv = null;

		List<SelectOptionForm> itemList = statementDetailService.searchDepositRecvDataList(searchWord, depositType);
		mv = getMyModelAndView(itemList,
				AccgInvociceStatementPathAndAttrConstant.ITEM_LIST_FRAGMENT_PATH,
				AccgInvociceStatementPathAndAttrConstant.ITEM_LIST_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * お振込先表示フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getBankDetailViewFragment", method = RequestMethod.GET)
	public ModelAndView getBankDetailViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		try {
			// お振込先表示フラグメント情報取得
			AccgInvoiceStatementViewForm.BankDetailViewForm bankDetailViewForm = statementDetailService.getBankDetailViewForm(statementSeq);
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
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getBankDetailInputFragment", method = RequestMethod.GET)
	public ModelAndView getBankDetailInputFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		try {
			// お振込先入力フラグメント情報取得
			AccgInvoiceStatementInputForm.BankDetailInputForm bankDetailInputForm = statementDetailService.getBankDetailInputForm(statementSeq);
			mv = getMyModelAndView(bankDetailInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BANK_DETAIL_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BANK_DETAIL_INPUT_FORM_NAME);

		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
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
	 * @param statementSeq
	 * @param bankDetailInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveBankDetail", method = RequestMethod.POST)
	public ModelAndView saveBankDetail(
			@PathVariable(name = "statementSeq") Long statementSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.BankDetailInputForm bankDetailInputForm,
			BindingResult result) {

		// バリデーションチェック
		if (result.hasErrors()) {
			// エラーメッセージを設定
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(bankDetailInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BANK_DETAIL_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_BANK_DETAIL_INPUT_FORM_NAME, result);
		}

		try {
			// お振込先データ保存処理
			statementDetailService.saveBankDetail(statementSeq, bankDetailInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "お振込先"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 備考表示フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getRemarksViewFragment", method = RequestMethod.GET)
	public ModelAndView getRemarksViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		try {
			// 備考表示フラグメント情報取得
			AccgInvoiceStatementViewForm.RemarksViewForm remarksViewForm = statementDetailService.getRemarksViewForm(statementSeq);
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
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getRemarksInputFragment", method = RequestMethod.GET)
	public ModelAndView getRemarksInputFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		try {
			// 備考入力フラグメント情報取得
			AccgInvoiceStatementInputForm.RemarksInputForm remarksInputForm = statementDetailService.getRemarksInputForm(statementSeq);
			mv = getMyModelAndView(remarksInputForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_REMARKS_INPUT_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_REMARKS_INPUT_FORM_NAME);

		} catch (AppException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
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
	 * @param statementSeq
	 * @param remarksInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveRemarks", method = RequestMethod.POST)
	public ModelAndView saveRemarks(
			@PathVariable(name = "statementSeq") Long statementSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementInputForm.RemarksInputForm remarksInputForm,
			BindingResult result) {

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
			statementDetailService.saveRemarks(statementSeq, remarksInputForm);

			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "備考"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 精算書PDFの再作成が必要かを判定し、必要な場合のみ作成中画面表示情報を取得する
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getStatementPdfCreatingViewFragment", method = RequestMethod.GET)
	public ModelAndView getStatementPdfCreatingViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv;
		try {
			if (statementDetailService.needCreateStatementPdf(statementSeq)) {
				// 請求書PDFが作成・再作成を行う場合 -> 正常終了として、作成中画面を表示
				mv = this.getPdfCreatingViewFragment(AccgDocFileType.STATEMENT);

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
	 * 精算書画面_精算書タブ画面表示情報を取得する
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getDocStatementPdfViewFragment", method = RequestMethod.GET)
	public ModelAndView getDocStatementPdfViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;
		Long tenantSeq = SessionUtils.getTenantSeq();

		try {
			// 削除するS3オブジェクトキーの格納用Set
			Set<String> deleteS3ObjectKey = new HashSet<>();

			// 画面表示オブジェクトの前段処理
			statementDetailService.beforeTransactionalDocStatementPdfCreate(statementSeq, tenantSeq, deleteS3ObjectKey);

			// 登録処理の中で削除したS3オブジェクトキーの削除処理
			this.deleteS3Object(deleteS3ObjectKey);

			// 画面表示オブジェクトの作成
			AccgInvoiceStatementViewForm.DocStatementPdfViewForm viewForm = statementDetailService.getDocStatementPdfViewForm(statementSeq);
			mv = getMyModelAndView(viewForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_STATEMENT_PDF_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_STATEMENT_PDF_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// Ajaxによる取得時のDataNotFoundは、画面の再取得時に発生したケースなので楽観エラーとして処理する
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
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
	 * 精算書のPDFプレビューを表示する
	 * 
	 * @param statementSeq
	 * @param response
	 */
	@RequestMapping(value = "/docStatementPdfPreview", method = RequestMethod.GET)
	public void docStatementPdfPreview(@PathVariable(name = "statementSeq") Long statementSeq, HttpServletResponse response) {
		statementDetailService.docStatementPdfPreview(statementSeq, response);
	}

	/**
	 * 実費明細PDFの再作成が必要かを判定し、必要な場合のみ作成中画面表示情報を取得する
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getDipositRecordPdfCreatingViewFragment", method = RequestMethod.GET)
	public ModelAndView getDipositRecordPdfCreatingViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv;
		try {
			if (statementDetailService.needCreateDipositRecordPdf(statementSeq)) {
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
	 * 精算書画面_実費明細タブ画面表示情報を取得する
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getDipositRecordPdfViewFragment", method = RequestMethod.GET)
	public ModelAndView getDipositRecordPdfViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;
		Long tenantSeq = SessionUtils.getTenantSeq();

		try {
			// 削除するS3オブジェクトキーの格納用Set
			Set<String> deleteS3ObjectKey = new HashSet<>();

			// 画面表示オブジェクトの前段処理(S3APIの発火)
			statementDetailService.beforeTransactionalDipositRecordPdfCreate(statementSeq, tenantSeq, deleteS3ObjectKey);

			// 前段処理の中で削除したS3オブジェクトキーの削除処理
			this.deleteS3Object(deleteS3ObjectKey);

			// 画面表示オブジェクトの作成
			AccgInvoiceStatementViewForm.DipositRecordPdfViewForm viewForm = statementDetailService.getDipositRecordPdfViewForm(statementSeq);
			mv = getMyModelAndView(viewForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_DIPOSIT_RECORD_PDF_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_DIPOSIT_RECORD_PDF_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// Ajaxによる取得時のDataNotFoundは、画面の再取得時に発生したケースなので楽観エラーとして処理する
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
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
	 * @param statementSeq
	 * @param response
	 */
	@RequestMapping(value = "/dipositRecordPdfPreview", method = RequestMethod.GET)
	public void dipositRecordPdfPreview(@PathVariable(name = "statementSeq") Long statementSeq, HttpServletResponse response) {
		statementDetailService.dipositRecordPdfPreview(statementSeq, response);
	}

	/**
	 * 取引状況表示フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getAccgDocSummaryViewFragment", method = RequestMethod.GET)
	public ModelAndView getAccgDocSummaryViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		try {
			// 取引状況表示フラグメント情報取得
			AccgDocSummaryForm accgDocSummaryForm = statementDetailService.getDocSummaryForm(statementSeq);
			mv = getMyModelAndView(accgDocSummaryForm,
					ViewPathAndAttrConstant.ACCG_DOC_SUMMARY_FRAGMENT_PATH,
					ViewPathAndAttrConstant.ACCG_DOC_SUMMARY_VIEW_FORM_NAME);

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
	 * 進行状況表示フラグメント取得
	 * 
	 * @param statementSeq
	 * @return
	 */
	@RequestMapping(value = "/getDocActivityViewFragment", method = RequestMethod.GET)
	public ModelAndView getDocActivityViewFragment(@PathVariable(name = "statementSeq") Long statementSeq) {

		ModelAndView mv = null;

		try {
			// 進行状況表示フラグメント情報取得
			AccgInvoiceStatementViewForm.DocActivityForm docActivityForm = statementDetailService.getDocActivityForm(statementSeq);
			mv = getMyModelAndView(docActivityForm,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_DOC_ACTIVITY_FRAGMENT_PATH,
					AccgInvociceStatementPathAndAttrConstant.ACCG_DETAIL_DOC_ACTIVITY_VIEW_FORM_NAME);

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

	// =========================================================================
	// private メソッド
	// =========================================================================

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
		statementDetailService.validateRepayRowList(accgDocSeq, ankenId, personId, repayInputForm.getRepayRowList(), result);

		return result;
	}

	/**
	 * 請求項目保存前のバリデーションチェック。
	 * 
	 * @param invoiceInputForm
	 * @param statementSeq
	 * @param result
	 * @return
	 * @throws AppException
	 */
	private BindingResult validationBeforeInvoiceSave(AccgInvoiceStatementInputForm.InvoiceInputForm invoiceInputForm,
			Long statementSeq, BindingResult result) throws AppException {
		// 単価と時間から報酬額を算出
		statementDetailService.calculateTimeCharge(invoiceInputForm);

		// バリデーションを実行
		validator.validate(invoiceInputForm, result);

		// 相関チェック
		Long accgDocSeq = statementDetailService.getAccgDocSeq(statementSeq);
		statementDetailService.validateInvoiceRowList(accgDocSeq, invoiceInputForm.getAnkenId(),
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
			Long statementSeq, ModelAndView mv, BindingResult result) throws AppException {
		// 相関チェック
		Long accgDocSeq = statementDetailService.getAccgDocSeq(statementSeq);
		statementDetailService.validationBeforeGroupOrUngroupSimilarInvoiceItems(accgDocSeq, invoiceInputForm.getAnkenId(),
				invoiceInputForm.getPersonId(), invoiceInputForm.getInvoiceRowList(), result);
		return result;
	}

	/**
	 * DBから削除したS3オブジェクトキーの実態ファイル削除
	 * 
	 * @param deleteS3ObjectKeys
	 */
	private void deleteS3Object(Set<String> deleteS3ObjectKeys) {
		statementDetailService.deleteS3Object(deleteS3ObjectKeys);
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
