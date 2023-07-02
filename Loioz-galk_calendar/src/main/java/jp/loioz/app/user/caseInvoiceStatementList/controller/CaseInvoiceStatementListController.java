package jp.loioz.app.user.caseInvoiceStatementList.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.caseInvoiceStatementList.form.CaseInvoiceStatementListViewForm;
import jp.loioz.app.user.caseInvoiceStatementList.service.CaseInvoiceStatementListService;
import jp.loioz.common.constant.CommonConstant.MessageLevel;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.UriUtils;
import jp.loioz.domain.UriService;

/**
 * 「請求・精算」画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/caseInvoiceStatementList/{personId}/{ankenId}")
public class CaseInvoiceStatementListController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/caseInvoiceStatementList/caseInvoiceStatementList";

	/** 表示で使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 請求書／精算書のサービスクラス */
	@Autowired
	private CaseInvoiceStatementListService service;

	/** URIサービスクラス */
	@Autowired
	private UriService uriService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 請求書／精算書画面初期表示
	 * 
	 * @param personId
	 * @param ankenId
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(
			@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId) {

		ModelAndView mv = null;

		// 案件ID、名簿IDに紐づく請求書／精算書情報を取得する
		CaseInvoiceStatementListViewForm viewForm = service.createViewForm();

		// 会計管理用の案件情報を取得
		service.getAccgData(viewForm, ankenId, personId);
		// 請求書／精算書を取得
		service.searchCaseInvoiceStatementList(viewForm, ankenId, personId);

		mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);

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
	 * @param personId
	 * @param ankenId
	 * @param levelCd
	 * @param message
	 * @param attributes
	 * @return
	 */
	@RequestMapping(value = "/redirectIndexWithMsg", method = RequestMethod.GET)
	public ModelAndView redirectIndexWithMsg(
			@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId,
			@RequestParam("level") String levelCd,
			@RequestParam("message") String message,
			RedirectAttributes attributes) {

		String redirectUrl = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.index(personId, ankenId));
		if (uriService.currentRequestAnkenSideParamHasTrue()) {
			Map<String, String> queryMap = Map.of(UrlConstant.ACCG_REFERER_ANKEN_SIDE_PARAM_NAME, Boolean.TRUE.toString());
			redirectUrl += UriUtils.createGetRequestQueryParamStr(queryMap);
		}

		RedirectViewBuilder builder = new RedirectViewBuilder(attributes, redirectUrl);
		return builder.build(MessageHolder.ofLevel(MessageLevel.of(levelCd), message));
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
