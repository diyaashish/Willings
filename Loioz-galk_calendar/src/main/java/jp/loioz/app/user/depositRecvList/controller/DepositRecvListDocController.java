package jp.loioz.app.user.depositRecvList.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.depositRecvList.form.DepositRecvListSearchForm;
import jp.loioz.app.user.depositRecvList.service.DepositRecvListDocService;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.log.Logger;

/**
 * 預り金／実費画面の一覧出力コントローラークラス
 */
@Controller
@RequestMapping(value = "user/depositRecvList")
@SessionAttributes(DepositRecvListDocController.SEARCH_FORM_NAME)
public class DepositRecvListDocController extends DefaultController {

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "depositRecvListSearchForm";

	/** ロガー */
	@Autowired
	private Logger logger;

	/** 預り金／実費出力用のサービスクラス */
	@Autowired
	private DepositRecvListDocService depositRecvListDocService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 預り金／実費 CSV出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputDepositRecvListCsv", method = RequestMethod.POST)
	public void outputDepositRecvListCsv(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) DepositRecvListSearchForm searchForm) {
		try {
			depositRecvListDocService.outputDepositRecvListCsv(response, searchForm);
		} catch (Exception ex) {
			logger.error("error", ex);
		}
	}

	/**
	 * 預り金／実費 Excel出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputDepositRecvListExcel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputDepositRecvListExcel(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) DepositRecvListSearchForm searchForm) {
		try {
			depositRecvListDocService.outputDepositRecvListExcel(response, searchForm);
		} catch (Exception ex) {
			logger.error("error", ex);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
