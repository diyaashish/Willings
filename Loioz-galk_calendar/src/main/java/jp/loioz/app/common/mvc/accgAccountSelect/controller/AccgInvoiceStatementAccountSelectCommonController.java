package jp.loioz.app.common.mvc.accgAccountSelect.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.mvc.accgAccountSelect.form.AccgInvoiceStatementAccountSelectForm;
import jp.loioz.app.common.mvc.accgAccountSelect.service.AccgInvoiceStatementAccountSelectCommonService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.dto.GinkoKozaDto;

/**
 * 口座選択モーダルコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "common/mvc/accgInvoiceStatementAccountSelect")
public class AccgInvoiceStatementAccountSelectCommonController extends DefaultController {

	/** 口座選択モーダルのコントローラーパス */
	private static final String ACCG_INVOICE_STATEMENT_ACCOUNT_SELECT_MODAL_PATH = "common/mvc/accgAccountSelect/accgInvoiceStatementAccountSelectModal";

	/** 口座選択モーダルのフラグメントフォームオブジェクト名 */
	private static final String ACCG_INVOICE_STATEMENT_ACCOUNT_SELECT_MODAL_FORM_NAME = "accgInvoiceStatementAccountSelectForm";

	/** 口座選択のサービスクラス */
	@Autowired
	private AccgInvoiceStatementAccountSelectCommonService service;

	/**
	 * 口座選択モーダルを開く
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/createAccgInvoiceStatementAccountSelectModal", method = RequestMethod.GET)
	public ModelAndView createAccgInvoiceStatementAccountSelectModal(@RequestParam(name = "accgDocSeq") Long accgDocSeq, @RequestParam(name = "ankenId") Long ankenId) {

		ModelAndView mv = null;

		// 銀行口座候補を取得
		List<GinkoKozaDto> ginkoKozaList = service.getTenantAccountGinkoKozaList(accgDocSeq, ankenId);

		AccgInvoiceStatementAccountSelectForm form = new AccgInvoiceStatementAccountSelectForm();
		form.setBankAccountList(ginkoKozaList);
		mv = getMyModelAndView(form, ACCG_INVOICE_STATEMENT_ACCOUNT_SELECT_MODAL_PATH, ACCG_INVOICE_STATEMENT_ACCOUNT_SELECT_MODAL_FORM_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 銀行口座情報を以下形式の文字列で取得します<br>
	 *   銀行名 支店名（支店番号 xxx） \r\n
	 *   口座種類 xxxxxxxxxx　口座名義 山田 太郎
	 * 
	 * @param ginkoAccountSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/selectTenantBankDetail", method = RequestMethod.POST)
	public Map<String, Object> selectTenantBankDetail(@RequestParam(name = "ginkoAccountSeq") Long ginkoAccountSeq) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 銀行口座情報を取得します
			String tenantBeanDetail = service.getTenantBankDetail(ginkoAccountSeq);
			response.put("successed", true);
			response.put("tenantBeanDetail", tenantBeanDetail);
			return response;
		} catch (AppException e) {
			response.put("successed", false);
			response.put("message", getMessage(MessageEnum.MSG_E00025));
			return response;
		}
	}

}
