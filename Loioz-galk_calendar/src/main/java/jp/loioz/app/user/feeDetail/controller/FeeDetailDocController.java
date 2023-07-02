package jp.loioz.app.user.feeDetail.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.feeDetail.form.FeeDetailSearchForm;
import jp.loioz.app.user.feeDetail.service.FeeDetailDocService;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.log.Logger;

/**
 * 報酬明細画面の明細出力コントローラークラス
 */
@Controller
@RequestMapping(value = "user/feeDetail/{personId}/{ankenId}")
@SessionAttributes(FeeDetailDocController.SEARCH_FORM_NAME)
public class FeeDetailDocController extends DefaultController {

	/** ロガー */
	@Autowired
	private Logger logger;

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "feeDetailSearchForm";

	/** 報酬明細出力用のサービスクラス */
	@Autowired
	private FeeDetailDocService feeDetailDocService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 報酬明細 Excel出力
	 * 
	 * @param response
	 * @param personId
	 * @param ankenId
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputFeeDetailExcel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputFeeListExcel(HttpServletResponse response,
			@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId,
			@ModelAttribute(SEARCH_FORM_NAME) FeeDetailSearchForm searchForm) {
		
		searchForm.setAnkenId(ankenId);
		searchForm.setPersonId(personId);
		
		try {
			feeDetailDocService.outputFeeDetailExcel(response, searchForm);
		} catch (Exception ex) {
			logger.error("error", ex);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
