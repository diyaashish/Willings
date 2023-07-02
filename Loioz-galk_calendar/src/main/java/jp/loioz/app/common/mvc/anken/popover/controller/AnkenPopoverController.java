package jp.loioz.app.common.mvc.anken.popover.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.mvc.anken.popover.form.AnkenPopoverSearchForm;
import jp.loioz.app.common.mvc.anken.popover.form.AnkenPopoverViewForm;
import jp.loioz.app.common.mvc.anken.popover.serivce.AnkenPopoverService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;

/**
 * 案件情報ポップオーバーコントローラー
 */
@Controller
@RequestMapping(value = "common/mvc/anken/popover")
public class AnkenPopoverController extends DefaultController {

	/** 案件情報ポップオーバー */
	private static final String ANKEN_POPOVER_VIEW_PATH = "common/mvc/anken/popover/ankenPopover.html";

	/** 案件情報ポップオーバーフラグメントフラグメントパス */
	private static final String ANKEN_POPOVER_FRAGMENT_VIEW_PATH = ANKEN_POPOVER_VIEW_PATH + "::ankenInfoFragment";
	/** 案件情報ポップオーバー表示フォーム名 */
	private static final String ANKEN_POPOVER_FRAGMENT_VIEW_FORM_NAME = "viewForm";

	/** 案件ポッパーサービス */
	@Autowired
	private AnkenPopoverService service;

	/**
	 * 案件情報ポップオーバーの表示
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenPopoverFragment", method = RequestMethod.GET)
	public ModelAndView getAnkenPopoverFragment(AnkenPopoverSearchForm searchForm) {

		ModelAndView mv = null;

		try {
			AnkenPopoverViewForm viewForm = service.createAnkenPopoverViewForm(searchForm);
			mv = getMyModelAndView(viewForm, ANKEN_POPOVER_FRAGMENT_VIEW_PATH, ANKEN_POPOVER_FRAGMENT_VIEW_FORM_NAME);

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

}
