package jp.loioz.app.user.myAccountKozaList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.myAccountKozaList.form.MyAccountKozaListForm;
import jp.loioz.app.user.myAccountKozaList.service.MyAccountKozaListService;
import jp.loioz.app.user.schedule.controller.ScheduleController;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 個人の口座設定画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping("user/myAccountKozaList")
public class MyAccountKozaListController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/myAccountKozaList/myAccountKozaList";

	/** コントローラと対応するview名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 個人口座一覧表示フラグメントviewのパス */
	private static final String MY_ACCOUNT_KOZA_LIST_VIEW_FRAGMENT = "user/myAccountKozaList/myAccountKozaListFragment::myAccountKozaListFragment";

	/** 個人の口座設定用一覧画面のサービスクラス */
	@Autowired
	private MyAccountKozaListService service;

	@ModelAttribute(VIEW_FORM_NAME)
	MyAccountKozaListForm setUpEditForm() {
		return new MyAccountKozaListForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView init(MyAccountKozaListForm viewForm) {

		try {
			viewForm = service.createViewForm();
		} catch (AppException e) {
			return ModelAndViewUtils.getRedirectModelAndView(ScheduleController.class, controller -> controller.index(null));
		}

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 個人口座一覧表示フラグメント再表示
	 * 
	 * @return
	 */
	@RequestMapping(value = "/renderMyAccountKozaListViewFragment", method = RequestMethod.GET)
	public ModelAndView renderMyAccountKozaListViewFragment() {
		
		ModelAndView mv = null;
		
		try {
			MyAccountKozaListForm viewForm = service.createViewForm();
			mv = getMyModelAndView(viewForm, MY_ACCOUNT_KOZA_LIST_VIEW_FRAGMENT, VIEW_FORM_NAME);
		} catch (AppException e) {
			return ModelAndViewUtils.getRedirectModelAndView(ScheduleController.class, controller -> controller.index(null));
		}
		
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}
		
		super.setAjaxProcResultSuccess();
		return mv;
	}

}