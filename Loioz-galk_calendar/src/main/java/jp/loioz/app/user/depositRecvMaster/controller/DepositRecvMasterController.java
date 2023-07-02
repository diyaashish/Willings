package jp.loioz.app.user.depositRecvMaster.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.depositRecvMaster.form.DepositRecvMasterSearchForm;
import jp.loioz.app.user.depositRecvMaster.form.DepositRecvMasterViewForm;
import jp.loioz.app.user.depositRecvMaster.service.DepositRecvMasterService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 預り金項目画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping("user/depositRecvMaster")
@DenyAccountKengen(value = AccountKengen.GENERAL)
@SessionAttributes(DepositRecvMasterController.SEARCH_FORM_NAME)
public class DepositRecvMasterController extends DefaultController {

	/** コントローラと対応するviewパス **/
	public static final String MY_VIEW_PATH = "user/depositRecvMaster/depositRecvMaster";

	/** 預り金項目一覧fragmentのパス */
	private static final String DEPOSIT_RECV_MASTER_LIST_VIEW_FRAGMENT_PATH = "user/depositRecvMaster/depositRecvMasterFragment::depositRecvMasterViewFragment";

	/** viewで使用するフォームオブジェクト名 **/
	public static final String VIEW_FORM_NAME = "viewForm";

	/** 検索条件のFormクラス */
	public static final String SEARCH_FORM_NAME = "depositRecvMasterSearchForm";

	/** 預り金項目一覧fragmentフォームオブジェクト名 */
	private static final String DEPOSIT_RECV_MASTER_LIST_FRAGMENT_VIEW_FORM = "depositRecvMasterListViewForm";

	/** 預り金項目画面のサービスクラス */
	@Autowired
	private DepositRecvMasterService service;

	/** 検索条件のModelAttribute */
	@ModelAttribute(SEARCH_FORM_NAME)
	DepositRecvMasterSearchForm setUpSearchForm() {
		return new DepositRecvMasterSearchForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) DepositRecvMasterSearchForm searchForm) {

		// 初期化
		searchForm.initForm();

		// 画面情報をリターン
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.list(searchForm));
	}

	/**
	 * 預り金項目一覧表示処理
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@ModelAttribute(SEARCH_FORM_NAME) DepositRecvMasterSearchForm searchForm) {

		// viewFormの作成
		DepositRecvMasterViewForm viewForm = new DepositRecvMasterViewForm();

		// 預かり金項目一覧フラグメント画面オブジェクトを作成
		DepositRecvMasterViewForm.DepositRecvMasterListViewForm depositRecvMasterListViewForm = service.createDepositRecvMasterListViewForm(searchForm.getDepositRecvMasterListSearchForm());
		viewForm.setDepositRecvMasterListViewForm(depositRecvMasterListViewForm);

		// 画面情報をリターン
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 預り金項目一覧フラグメントの表示
	 *
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getDepositRecvMasterListSearch", method = RequestMethod.GET)
	public ModelAndView getDepositRecvMasterListSearch(@ModelAttribute(SEARCH_FORM_NAME) DepositRecvMasterSearchForm searchForm) {

		// 預かり金項目一覧フラグメント画面オブジェクトを作成
		DepositRecvMasterViewForm.DepositRecvMasterListViewForm depositRecvMasterListViewForm = service.createDepositRecvMasterListViewForm(searchForm.getDepositRecvMasterListSearchForm());

		// 画面情報を返却
		super.setAjaxProcResultSuccess();
		return getMyModelAndView(depositRecvMasterListViewForm, DEPOSIT_RECV_MASTER_LIST_VIEW_FRAGMENT_PATH, DEPOSIT_RECV_MASTER_LIST_FRAGMENT_VIEW_FORM);
	}

	/**
	 * 預り金項目一覧フラグメントの検索
	 *
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/depositRecvMasterListSearch", method = RequestMethod.GET)
	public ModelAndView depositRecvMasterListSearch(@ModelAttribute(SEARCH_FORM_NAME) DepositRecvMasterSearchForm searchForm) {

		// ページ数を初期化
		DepositRecvMasterSearchForm.DepositRecvMasterListSearchForm depositRecvMasterListSearchForm = searchForm.getDepositRecvMasterListSearchForm();
		depositRecvMasterListSearchForm.setDefaultPage();

		// 預かり金項目一覧フラグメント画面オブジェクトを作成
		DepositRecvMasterViewForm.DepositRecvMasterListViewForm depositRecvMasterListViewForm = service.createDepositRecvMasterListViewForm(searchForm.getDepositRecvMasterListSearchForm());

		// 画面情報を返却
		super.setAjaxProcResultSuccess();
		return getMyModelAndView(depositRecvMasterListViewForm, DEPOSIT_RECV_MASTER_LIST_VIEW_FRAGMENT_PATH, DEPOSIT_RECV_MASTER_LIST_FRAGMENT_VIEW_FORM);
	}

	/**
	 * 表示順変更処理
	 * 
	 * @param targetId
	 * @param index
	 * @return
	 */
	@RequestMapping(value = "/updateDispOrder", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateDispOrder(
			@RequestParam(name = "targetId") String targetId,
			@RequestParam(name = "index") String index) {

		Map<String, Object> response = new HashMap<>();
		List<Long> dispOrder = new ArrayList<Long>();

		try {
			/* 表示順変更処理 */
			dispOrder = service.updateDispOrder(targetId, index);

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得する
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getMessageKey()));
			return response;
		}
		response.put("succeeded", true);
		response.put("dispOrder", dispOrder);
		response.put("message", getMessage(MessageEnum.MSG_I00023, "表示順"));
		return response;
	}

}
