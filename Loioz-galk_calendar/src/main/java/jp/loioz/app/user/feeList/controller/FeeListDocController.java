package jp.loioz.app.user.feeList.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.feeList.form.FeeListSearchForm;
import jp.loioz.app.user.feeList.service.FeeListDocService;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.log.Logger;

/**
 * 報酬画面の一覧出力コントローラークラス
 */
@Controller
@RequestMapping(value = "user/feeList")
@SessionAttributes(FeeListDocController.SEARCH_FORM_NAME)
public class FeeListDocController extends DefaultController {

	/** ロガー */
	@Autowired
	private Logger logger;

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "feeListSearchForm";

	/** 報酬出力用のサービスクラス */
	@Autowired
	private FeeListDocService feeListDocService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 報酬 CSV出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputFeeListCsv", method = RequestMethod.POST)
	public void outputFeeListCsv(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) FeeListSearchForm searchForm) {
		try {
			feeListDocService.outputFeeListCsv(response, searchForm);
		} catch (Exception ex) {
			logger.error("error", ex);
		}
	}

	/**
	 * 報酬 Excel出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputFeeListExcel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputAnkenListExcel(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) FeeListSearchForm searchForm) {
		try {
			feeListDocService.outputFeeListExcel(response, searchForm);
		} catch (Exception ex) {
			logger.error("error", ex);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
