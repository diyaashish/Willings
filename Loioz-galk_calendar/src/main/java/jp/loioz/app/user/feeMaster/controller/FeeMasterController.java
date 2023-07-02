package jp.loioz.app.user.feeMaster.controller;

import java.util.ArrayList;
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
import jp.loioz.app.user.feeMaster.form.FeeMasterViewForm;
import jp.loioz.app.user.feeMaster.service.FeeMasterService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

/**
 * 報酬項目の設定画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/feeMaster")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class FeeMasterController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/feeMaster/feeMaster";

	/** 報酬項目の設定のフラグメントviewのパス */
	private static final String FEE_MASTER_LIST_FRAGMENT_VIEW_PATH = "user/feeMaster/feeMasterFragment::feeMasterListFragment";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 報酬項目の設定のフラグメントviewフォーム名 */
	private static final String FEE_MASTER_LIST_FRAGMENT_VIEW_FORM_NAME = "feeMasterListViewForm";

	/** 報酬項目の設定のサービスクラス */
	@Autowired
	private FeeMasterService feeMasterService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 報酬項目の設定画面初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {

		// Viewの作成
		FeeMasterViewForm viewForm = new FeeMasterViewForm();

		// 一覧フラグメントの作成
		FeeMasterViewForm.FeeMasterListViewForm feeMasterListViewForm = feeMasterService.createFeeMasterListViewForm();
		viewForm.setFeeMasterListViewForm(feeMasterListViewForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 報酬項目の設定画面一覧フラグメントの画面情報
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getFeeMasterListViewFragment", method = RequestMethod.GET)
	public ModelAndView getFeeMasterListViewFragment() {

		ModelAndView mv = null;

		// 一覧フラグメントの作成
		FeeMasterViewForm.FeeMasterListViewForm feeMasterListViewForm = feeMasterService.createFeeMasterListViewForm();
		mv = getMyModelAndView(feeMasterListViewForm, FEE_MASTER_LIST_FRAGMENT_VIEW_PATH, FEE_MASTER_LIST_FRAGMENT_VIEW_FORM_NAME);

		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
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
			dispOrder = feeMasterService.updateDispOrder(targetId, index);

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

	// =========================================================================
	// private メソッド
	// =========================================================================

}
