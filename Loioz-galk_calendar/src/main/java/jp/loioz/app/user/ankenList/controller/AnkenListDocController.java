package jp.loioz.app.user.ankenList.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.ankenList.form.AnkenListSearchForm;
import jp.loioz.app.user.ankenList.service.AnkenListDocService;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.log.Logger;

/**
 * 案件一覧画面の一覧出力コントローラークラス
 */
@Controller
@RequestMapping(value = "user/ankenList")
@SessionAttributes(AnkenListDocController.SEARCH_FORM_NAME)
public class AnkenListDocController extends DefaultController {

	/** ロガー */
	@Autowired
	private Logger logger;

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "ankenListSearchForm";

	/** 案件一覧出力用のサービスクラス */
	@Autowired
	private AnkenListDocService ankenListDocService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 案件一覧 CSV出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputAnkenListCsv", method = RequestMethod.POST)
	public void outputAnkenListCsv(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) AnkenListSearchForm searchForm) {
		try {
			ankenListDocService.outputAnkenListCsv(response, searchForm);
		} catch (Exception ex) {
			logger.error("error", ex);
		}
	}

	/**
	 * 案件一覧 Excel出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputAnkenListExcel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputAnkenListExcel(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) AnkenListSearchForm searchForm) {
		try {
			ankenListDocService.outputAnkenListExcel(response, searchForm);
		} catch (Exception ex) {
			logger.error("error", ex);
		}
	}

	/**
	 * 民事裁判一覧 CSV出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputMinjiSaibanListCsv", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputMinjiSaibanListCsv(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) AnkenListSearchForm searchForm) {
		try {
			ankenListDocService.outputMinjiSaibanListCsv(response, searchForm);
		} catch (Exception ex) {
			logger.error("error", ex);
		}
	}

	/**
	 * 民事裁判一覧 Excel出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputMinjiSaibanListExcel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputMinjiSaibanListExcel(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) AnkenListSearchForm searchForm) {
		try {
			ankenListDocService.outputMinjiSaibanListExcel(response, searchForm);
		} catch (Exception ex) {
			logger.error("error", ex);
		}
	}

	/**
	 * 刑事裁判一覧 CSV出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputKeijiSaibanListCsv", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputKeijiSaibanListCsv(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) AnkenListSearchForm searchForm) {
		try {
			ankenListDocService.outputKeijiSaibanListCsv(response, searchForm);
		} catch (Exception ex) {
			logger.error("error", ex);
		}
	}

	/**
	 * 刑事裁判一覧 Excel出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputKeijiSaibanListExcel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputKeijiSaibanListExcel(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) AnkenListSearchForm searchForm) {
		try {
			ankenListDocService.outputKeijiSaibanListExcel(response, searchForm);
		} catch (Exception ex) {
			logger.error("error", ex);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
