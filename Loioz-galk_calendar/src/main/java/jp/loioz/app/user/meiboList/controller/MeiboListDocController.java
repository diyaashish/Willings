package jp.loioz.app.user.meiboList.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.meiboList.form.MeiboListSearchForm;
import jp.loioz.app.user.meiboList.service.MeiboListDocService;
import jp.loioz.common.constant.OutputConstant;

/**
 * 名簿一覧の出力用コントローラー
 */
@Controller
@RequestMapping(value = "user/meiboList/doc")
@SessionAttributes(MeiboListController.SEARCH_FORM_NAME)
public class MeiboListDocController extends DefaultController {

	/** 検索条件フォーム名 */
	private static final String SEARCH_FORM_NAME = MeiboListController.SEARCH_FORM_NAME;

	/** 名簿一覧出力サービス */
	@Autowired
	MeiboListDocService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 名簿一覧：すべて CSV出力
	 * 
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputAllMeiboListCsv", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputAllMeiboListCsv(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// CSVの出力処理
		service.outputAllMeiboListCsv(response, searchForm);
	}

	/**
	 * 名簿一覧：すべて Excel出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputAllMeiboListExcel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputAllMeiboListExcel(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// Excelの出力処理
		service.outputAllMeiboListExcel(response, searchForm);
	}

	/**
	 * 名簿一覧：すべて 筆まめ出力
	 * 
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputAllMeiboListFudemame", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputFudemameAllMeiboListFudemame(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// 筆まめの出力処理
		service.outputAllMeiboListFudemame(response, searchForm);
	}

	/**
	 * 名簿一覧：顧客 CSV出力
	 * 
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputCustomerAllMeiboListCsv", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputCustomerAllMeiboListCsv(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// CSVの出力処理
		service.outputCustomerAllMeiboListCsv(response, searchForm);
	}

	/**
	 * 名簿一覧：顧客 Excel出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputCustomerAllMeiboListExcel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputCustomerAllMeiboListExcel(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// Excelの出力処理
		service.outputCustomerAllMeiboListExcel(response, searchForm);
	}

	/**
	 * 名簿一覧：顧客 筆まめ出力
	 * 
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputCustomerAllMeiboListFudemame", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputCustomerAllMeiboListFudemame(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// 筆まめの出力処理
		service.outputCustomerAllMeiboListFudemame(response, searchForm);
	}

	/**
	 * 名簿一覧：個人顧客 CSV出力
	 * 
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputCustomerKojinMeiboListCsv", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputCustomerKojinMeiboListCsv(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// CSVの出力処理
		service.outputCustomerKojinMeiboListCsv(response, searchForm);
	}

	/**
	 * 名簿一覧：個人顧客 Excel出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputCustomerKojinMeiboListExcel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputCustomerKojinMeiboListExcel(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// Excelの出力処理
		service.outputCustomerKojinMeiboListExcel(response, searchForm);
	}

	/**
	 * 名簿一覧：個人顧客 筆まめ出力
	 * 
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputCustomerKojinMeiboListFudemame", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputCustomerKojinMeiboListFudemame(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// CSVの出力処理
		service.outputCustomerKojinMeiboListFudemame(response, searchForm);
	}

	/**
	 * 名簿一覧：法人顧客 CSV出力
	 * 
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputCustomerHojinMeiboListCsv", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputCustomerHojinMeiboListCsv(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// CSVの出力処理
		service.outputCustomerHojinMeiboListCsv(response, searchForm);
	}

	/**
	 * 名簿一覧：法人顧客 Excel出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputCustomerHojinMeiboListExcel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputCustomerHojinMeiboListExcel(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// Excelの出力処理
		service.outputCustomerHojinMeiboListExcel(response, searchForm);
	}

	/**
	 * 名簿一覧：法人顧客 筆まめ出力
	 * 
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputCustomerHojinMeiboListFudemame", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputCustomerHojinMeiboListFudemame(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// CSVの出力処理
		service.outputCustomerHojinMeiboListFudemame(response, searchForm);
	}

	/**
	 * 名簿一覧：顧問 CSV出力
	 * 
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputAdvisorMeiboListCsv", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputAdvisorMeiboListCsv(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// CSVの出力処理
		service.outputAdvisorMeiboListCsv(response, searchForm);
	}

	/**
	 * 名簿一覧：顧問 Excel出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputAdvisorMeiboListExcel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputAdvisorMeiboListExcel(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// Excelの出力処理
		service.outputAdvisorMeiboListExcel(response, searchForm);
	}

	/**
	 * 名簿一覧：顧問 筆まめ出力
	 * 
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputAdvisorMeiboListFudemame", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputAdvisorMeiboListFudemame(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// CSVの出力処理
		service.outputAdvisorMeiboListFudemame(response, searchForm);
	}

	/**
	 * 名簿一覧：弁護士 CSV出力
	 * 
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputBengoshiMeiboListCsv", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputBengoshiMeiboListCsv(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// CSVの出力処理
		service.outputBengoshiMeiboListCsv(response, searchForm);
	}

	/**
	 * 名簿一覧：弁護士 Excel出力
	 *
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputBengoshiMeiboListExcel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputBengoshiMeiboListExcel(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// Excelの出力処理
		service.outputBengoshiMeiboListExcel(response, searchForm);
	}

	/**
	 * 名簿一覧：弁護士 筆まめ出力
	 * 
	 * @param response
	 * @param searchForm
	 */
	@RequestMapping(value = "/outputBengoshiMeiboListFudemame", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputBengoshiMeiboListFudemame(HttpServletResponse response, @ModelAttribute(SEARCH_FORM_NAME) MeiboListSearchForm searchForm) {

		// 筆まめの出力処理
		service.outputBengoshiMeiboListFudemame(response, searchForm);
	}

}
