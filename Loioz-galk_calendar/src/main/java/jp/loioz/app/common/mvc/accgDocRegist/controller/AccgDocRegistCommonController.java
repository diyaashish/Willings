package jp.loioz.app.common.mvc.accgDocRegist.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.mvc.accgDocRegist.form.AccgDocRegistModalInputForm;
import jp.loioz.app.common.mvc.accgDocRegist.service.AccgDocRegistCommonService;
import jp.loioz.app.user.invoiceDetail.controller.InvoiceDetailController;
import jp.loioz.app.user.statementDetail.controller.StatementDetailController;
import jp.loioz.common.constant.CommonConstant.MessageLevel;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.dto.PersonDto;

/**
 * 請求書・精算書作成の共通コントローラー
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "common/mvc/accgDocRegist")
public class AccgDocRegistCommonController extends DefaultController {

	/** 請求書・精算書登録処理後のレスポンスヘッダー設定値 */
	public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_ACCG_DOC_REGIST_REDIRECT = "100";

	/** 請求書・精算書作成モーダルのコントローラーパス */
	private static final String ACCG_DOC_REGIST_MODAL_PATH = "common/mvc/accgDoc/accgDocRegistModal";
	/** 請求書・精算書作成モーダルフラグメントコントローラーパス */
	private static final String ACCG_DOC_REGIST_MODAL_FRAGMENT_PATH = ACCG_DOC_REGIST_MODAL_PATH + "::accgDocRegistModalFragment";
	/** 顧客候補値のデータリストフラグメントパス */
	private static final String CUSTOMER_LIST_FRAGMENT_PATH = ACCG_DOC_REGIST_MODAL_PATH + "::customerListFragment";
	/** 選択済み顧客フラグメントパス */
	private static final String CUSTOMER_SELECTED_FRAGMENT_PATH = ACCG_DOC_REGIST_MODAL_PATH + "::customerSelectedFragment";
	/** 案件リストボックスフラグメントパス */
	private static final String ANKEN_LIST_FRAGMENT_PATH = ACCG_DOC_REGIST_MODAL_PATH + "::ankenListFragment";

	/** 請求書・精算書作成モーダルのフラグメントフォームオブジェクト名 */
	private static final String ACCG_DOC_REGIST_MODAL_INPUT_FORM_NAME = "accgDocRegistModalInputForm";
	/** 顧客候補値のデータリストフラグメントオブジェクト名 */
	private static final String CUSTOMER_LIST_NAME = "customerList";
	/** 選択済み顧客フラグメントオブジェクト名 */
	private static final String PERSON_DTO_NAME = "person";
	/** 案件リストボックスフラグメントオブジェクト名 */
	private static final String ANKEN_LIST_NAME = "ankenList";

	/** 請求書・精算書作成モーダルのサービスクラス */
	@Autowired
	private AccgDocRegistCommonService service;

	/**
	 * 請求書・精算書作成モーダルの新規作成用
	 * 
	 * @return
	 */
	@RequestMapping(value = "/createAccgDocRegistModal", method = RequestMethod.GET)
	public ModelAndView createAccgDocRegistModal() {

		ModelAndView mv = null;

		AccgDocRegistModalInputForm inputForm = service.createAccgDocRegistModalInputForm();
		mv = getMyModelAndView(inputForm, ACCG_DOC_REGIST_MODAL_FRAGMENT_PATH, ACCG_DOC_REGIST_MODAL_INPUT_FORM_NAME);

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 顧客候補を取得します
	 * 
	 * @param searchWord
	 * @return
	 */
	@RequestMapping(value = "/getCustomerList", method = RequestMethod.GET)
	public ModelAndView getCustomerList(@RequestParam(name = "searchWord") String searchWord) {

		ModelAndView mv = null;

		List<SelectOptionForm> customerList = service.getCustomerList(searchWord);
		mv = getMyModelAndView(customerList, CUSTOMER_LIST_FRAGMENT_PATH, CUSTOMER_LIST_NAME);

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 選択済み顧客エリアを表示します
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/renderSelectedCustomer", method = RequestMethod.GET)
	public ModelAndView renderSelectedCustomer(@RequestParam(name = "personId") Long personId) {

		ModelAndView mv = null;

		PersonDto dto = null;
		try {
			dto = service.getPerson(personId);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		mv = getMyModelAndView(dto, CUSTOMER_SELECTED_FRAGMENT_PATH, PERSON_DTO_NAME);

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件リストボックスを取得します
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenList", method = RequestMethod.GET)
	public ModelAndView getAnkenList(@RequestParam(name = "personId") Long personId) {

		ModelAndView mv = null;

		List<SelectOptionForm> ankenList = service.getAnkenList(personId);

		mv = getMyModelAndView(ankenList, ANKEN_LIST_FRAGMENT_PATH, ANKEN_LIST_NAME);

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 請求書-新規登録
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registInvoice", method = RequestMethod.POST)
	public ModelAndView registInvoice(@Validated AccgDocRegistModalInputForm form, BindingResult result) {

		if (result.hasErrors()) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00024, "顧客と案件"));
			return null;
		}

		try {
			// 登録処理
			Long invoiceSeq = service.registInvoice(form.getPersonId(), form.getAnkenId());

			// 登録処理後の画面遷移先URLをレスポンスヘッダーに設定する
			String redirectUrl = ModelAndViewUtils.getRedirectPath(
					InvoiceDetailController.class,
					controller -> controller.redirectIndexWithMsg(invoiceSeq, MessageLevel.INFO.getCd(), getMessage(MessageEnum.MSG_I00139, "請求書（下書き）"), null));

			super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_ACCG_DOC_REGIST_REDIRECT, redirectUrl);
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 精算書-新規登録
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registStatement", method = RequestMethod.POST)
	public ModelAndView registStatement(@Validated AccgDocRegistModalInputForm form, BindingResult result) {

		if (result.hasErrors()) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00024, "顧客と案件"));
			return null;
		}

		try {
			// 登録処理
			Long statementSeq = service.registStatement(form.getPersonId(), form.getAnkenId());

			// 登録処理後の画面遷移先URLをレスポンスヘッダーに設定する
			String redirectUrl = ModelAndViewUtils.getRedirectPath(
					StatementDetailController.class,
					controller -> controller.redirectIndexWithMsg(statementSeq, MessageLevel.INFO.getCd(), getMessage(MessageEnum.MSG_I00139, "精算書（下書き）"), null));
			super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_ACCG_DOC_REGIST_REDIRECT, redirectUrl);
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 請求書を新規登録し、報酬や預り金を紐づけます。
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registInvoiceAndLinkFeeAndDeposit", method = RequestMethod.POST)
	public ModelAndView registInvoiceAndLinkFeeAndDeposit(@Validated AccgDocRegistModalInputForm form, BindingResult result) {

		try {
			// 登録処理
			Long invoiceSeq = service.registInvoice(form.getPersonId(), form.getAnkenId(), form.getFeeSeqList(), form.getDepositRecvSeqList());

			// 登録処理後の画面遷移先URLをレスポンスヘッダーに設定する
			String redirectUrl = ModelAndViewUtils.getRedirectPath(
					InvoiceDetailController.class,
					controller -> controller.redirectIndexWithMsg(invoiceSeq, MessageLevel.INFO.getCd(), getMessage(MessageEnum.MSG_I00139, "請求書（下書き）"), null));
			super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_ACCG_DOC_REGIST_REDIRECT, redirectUrl);
			return null;

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

	}

	/**
	 * 精算書を新規作成し報酬や預り金を紐づけます。
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registStatementAndLinkFeeAndDeposit", method = RequestMethod.POST)
	public ModelAndView registStatementAndLinkFeeAndDeposit(@Validated AccgDocRegistModalInputForm form, BindingResult result) {

		try {
			// 登録処理
			Long statementSeq = service.registStatement(form.getPersonId(), form.getAnkenId(), form.getFeeSeqList(), form.getDepositRecvSeqList());

			// 登録処理後の画面遷移先URLをレスポンスヘッダーに設定する
			String redirectUrl = ModelAndViewUtils.getRedirectPath(
					StatementDetailController.class,
					controller -> controller.redirectIndexWithMsg(statementSeq, MessageLevel.INFO.getCd(), getMessage(MessageEnum.MSG_I00139, "精算書（下書き）"), null));
			super.setAjaxProcResult(HEADER_VALUE_OF_AJAX_PROC_RESULT_ACCG_DOC_REGIST_REDIRECT, redirectUrl);
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
