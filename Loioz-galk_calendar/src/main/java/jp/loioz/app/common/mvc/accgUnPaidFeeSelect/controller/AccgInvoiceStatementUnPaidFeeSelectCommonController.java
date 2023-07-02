package jp.loioz.app.common.mvc.accgUnPaidFeeSelect.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.mvc.accgUnPaidFeeSelect.dto.AccgInvoiceStatementUnPaidFeeSelectDto;
import jp.loioz.app.common.mvc.accgUnPaidFeeSelect.form.AccgInvoiceStatementUnPaidFeeSelectForm;
import jp.loioz.app.common.mvc.accgUnPaidFeeSelect.service.AccgInvoiceStatementUnPaidFeeSelectCommonService;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

/**
 * 未精算報酬選択モーダルコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "common/mvc/accgInvoiceStatementUnPaidFeeSelect")
public class AccgInvoiceStatementUnPaidFeeSelectCommonController extends DefaultController {

	/** 未精算報酬選択モーダルのコントローラーパス */
	private static final String ACCG_INVOICE_STATEMENT_UN_PAID_FEE_SELECT_MODAL_PATH = "common/mvc/accgUnPaidFeeSelect/accgInvoiceStatementUnPaidFeeSelectModal";

	/** 未精算報酬選択モーダルのフラグメントフォームオブジェクト名 */
	private static final String ACCG_INVOICE_STATEMENT_UN_PAID_FEE_SELECT_MODAL_FORM_NAME = "accgInvoiceStatementUnPaidFeeSelectForm";

	/** 未精算報酬選択のサービスクラス */
	@Autowired
	private AccgInvoiceStatementUnPaidFeeSelectCommonService service;

	/**
	 * 未精算報酬選択モーダルを開く
	 * 
	 * @param ankenId
	 * @param personId
	 * @param accgDocSeq
	 * @param excludeFeeSeqList
	 */
	@RequestMapping(value = "/createAccgInvoiceStatementUnPaidFeeSelectModal", method = RequestMethod.POST)
	public ModelAndView createAccgInvoiceStatementUnPaidFeeSelectModal(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "personId") Long personId,
			@RequestParam(name = "accgDocSeq") Long accgDocSeq,
			@RequestParam(name = "feeSeqList") List<Long> excludeFeeSeqList) {

		ModelAndView mv = null;

		// 未精算報酬データを取得
		List<AccgInvoiceStatementUnPaidFeeSelectDto> unPaidFeeDtoList = service.getUnPaidFeeList(ankenId,
				personId, accgDocSeq, excludeFeeSeqList);

		AccgInvoiceStatementUnPaidFeeSelectForm form = new AccgInvoiceStatementUnPaidFeeSelectForm();
		form.setUnPaidFeeList(unPaidFeeDtoList);
		mv = getMyModelAndView(form, ACCG_INVOICE_STATEMENT_UN_PAID_FEE_SELECT_MODAL_PATH,
				ACCG_INVOICE_STATEMENT_UN_PAID_FEE_SELECT_MODAL_FORM_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}
}
