package jp.loioz.app.common.mvc.accgDepositRecvSelect.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.mvc.accgDepositRecvSelect.form.AccgInvoiceStatementDepositRecvSelectInputForm;
import jp.loioz.app.common.mvc.accgDepositRecvSelect.form.AccgInvoiceStatementDepositRecvSelectSearchForm;
import jp.loioz.app.common.mvc.accgDepositRecvSelect.form.AccgInvoiceStatementDepositRecvSelectViewForm;
import jp.loioz.app.common.mvc.accgDepositRecvSelect.service.AccgInvoiceStatementDepositRecvSelectCommonService;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.user.depositRecvDetail.form.DepositRecvDetailInputForm;
import jp.loioz.common.constant.CommonConstant.DepositType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.log.Logger;

/**
 * 預り金選択モーダルコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "common/mvc/accgInvoiceStatementDepositRecvSelect")
@SessionAttributes(AccgInvoiceStatementDepositRecvSelectCommonController.SEARCH_FORM_NAME)
public class AccgInvoiceStatementDepositRecvSelectCommonController extends DefaultController {

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyDepositRecvSelectModalInputForm";

	/** 預り金選択モーダルのコントローラーパス */
	private static final String ACCG_INVOICE_STATEMENT_DEPOSIT_RECV_SELECT_MODAL_PATH = "common/mvc/accgDepositRecvSelect/accgInvoiceStatementDepositRecvSelectModal";

	/** 預り金明細の登録Fragmentのパス */
	private static final String DEPOSIT_RECV_INPUT_PATH = "common/mvc/accgDepositRecvSelect/accgInvoiceStatementDepositRecvSelectModalFragment::depositRecvSelectModalInputRowFragment";

	/** 項目候補値のデータリストFragmentパス */
	private static final String DEPOSIT_RECV_ITEM_LIST_FRAGMENT_PATH = "common/mvc/accgDepositRecvSelect/accgInvoiceStatementDepositRecvSelectModalFragment::depositRecvSelectModalItemFragment";

	/** 預り金選択モーダルのフラグメント表示フォームオブジェクト名 */
	private static final String ACCG_INVOICE_STATEMENT_DEPOSIT_RECV_SELECT_MODAL_FORM_NAME = "accgInvoiceStatementDepositRecvSelectViewForm";

	/** 登録で使用するフォームオブジェクト名 */
	private static final String INPUT_FORM_NAME = "inputForm";

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "accgInvoiceStatementDepositRecvSelectSearchForm";

	/** 項目の候補値データオブジェクト名 */
	private static final String DEPOSIT_RECV_ITEM_LIST_NAME = "depositRecvItemList";

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/** 預り金選択のサービスクラス */
	@Autowired
	private AccgInvoiceStatementDepositRecvSelectCommonService service;

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	/** フォームクラスの初期化 */
	@ModelAttribute(SEARCH_FORM_NAME)
	AccgInvoiceStatementDepositRecvSelectSearchForm setUpForm() {
		return new AccgInvoiceStatementDepositRecvSelectSearchForm();
	}

	/**
	 * 預り金選択モーダルを開く
	 * 
	 * @param searchForm
	 */
	@RequestMapping(value = "/createAccgInvoiceStatementDepositRecvSelectModal", method = RequestMethod.POST)
	public ModelAndView createAccgInvoiceStatementDepositRecvSelectModal(
			@ModelAttribute(SEARCH_FORM_NAME) AccgInvoiceStatementDepositRecvSelectSearchForm searchForm) {
		
		ModelAndView mv = null;
		
		// 預り金データを取得
		AccgInvoiceStatementDepositRecvSelectViewForm form = service
				.createAccgInvoiceStatementDepositRecvSelectModal(searchForm);
		mv = getMyModelAndView(form,
				ACCG_INVOICE_STATEMENT_DEPOSIT_RECV_SELECT_MODAL_PATH,
				ACCG_INVOICE_STATEMENT_DEPOSIT_RECV_SELECT_MODAL_FORM_NAME);
		
		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金入力エリアを表示する
	 * 
	 * @param ankenId
	 * @param personId
	 * @param depositType
	 * @return
	 */
	@RequestMapping(value = "/openInputDepositRecv", method = RequestMethod.GET)
	public ModelAndView openInputDepositRecv(@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "personId") Long personId, @RequestParam(name = "depositType") String depositType) {

		ModelAndView mv = null;

		// 預り金明細入力用フォームを作成する
		DepositRecvDetailInputForm inputForm = service.createInputForm();
		inputForm.setDepositType(depositType);

		mv = getMyModelAndView(inputForm, DEPOSIT_RECV_INPUT_PATH, INPUT_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 項目の候補を取得
	 * 
	 * @param personId
	 * @param ankenId
	 * @param searchWord
	 * @param depositType
	 * @return
	 */
	@RequestMapping(value = "/searchDepositRecvDataList", method = RequestMethod.GET)
	public ModelAndView searchDepositRecvDataList(@RequestParam("searchWord") String searchWord,
			@RequestParam("depositType") String depositType) {

		ModelAndView mv = null;

		List<SelectOptionForm> depositRecvItemList = service.searchDepositRecvDataList(searchWord, depositType);
		mv = getMyModelAndView(depositRecvItemList, DEPOSIT_RECV_ITEM_LIST_FRAGMENT_PATH, DEPOSIT_RECV_ITEM_LIST_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金登録処理
	 * 
	 * @param searchForm
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registDepositRecv", method = RequestMethod.POST)
	public ModelAndView registDepositRecv(
			@ModelAttribute(SEARCH_FORM_NAME) AccgInvoiceStatementDepositRecvSelectSearchForm searchForm,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) AccgInvoiceStatementDepositRecvSelectInputForm inputForm,
			BindingResult result) {

		ModelAndView mv = null;

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(inputForm, DEPOSIT_RECV_INPUT_PATH, INPUT_FORM_NAME, result);
		}

		try {
			// 登録処理
			inputForm.setAnkenId(searchForm.getAnkenId());
			inputForm.setPersonId(searchForm.getPersonId());
			service.registDepositRecv(inputForm);
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
		AccgInvoiceStatementDepositRecvSelectViewForm form = service
				.createAccgInvoiceStatementDepositRecvSelectModal(searchForm);
		mv = getMyModelAndView(form,
				ACCG_INVOICE_STATEMENT_DEPOSIT_RECV_SELECT_MODAL_PATH,
				ACCG_INVOICE_STATEMENT_DEPOSIT_RECV_SELECT_MODAL_FORM_NAME);

		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}
		
		// モーダル表示メッセージ
		if (DepositType.NYUKIN.equalsByCode(form.getDepositType())) {
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "預り金項目"));
			
		} else if (DepositType.SHUKKIN.equalsByCode(form.getDepositType())) {
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "実費項目"));
			
		} else {
			throw new RuntimeException("想定外の入出金タイプ[depositType=" + form.getDepositType() + "]");
		}
		
		return mv;
	}

}
