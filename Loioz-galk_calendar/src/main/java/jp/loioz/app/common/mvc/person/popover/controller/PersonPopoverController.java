package jp.loioz.app.common.mvc.person.popover.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.mvc.person.popover.form.PersonPopoverViewForm;
import jp.loioz.app.common.mvc.person.popover.service.PersonPopoverService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;

/**
 * 名簿情報共通コントローラー
 */
@Controller
@RequestMapping(value = "common/mvc/person/popover")
public class PersonPopoverController extends DefaultController {

	/** 共通名簿情報ポップオーバー */
	private static final String PERSON_POPOVER_VIEW_PATH = "common/mvc/person/popover/personPopover";

	/** 共通名簿情報ポップオーバーフラグメントフラグメントパス */
	private static final String PERSON_POPOVER_FRAGMENT_VIEW_PATH = PERSON_POPOVER_VIEW_PATH + "::personInfoFragment";
	/** 共通顧客登録モーダルの顧客登録ビューの入力フォーム名 */
	private static final String PERSON_POPOVER_FRAGMENT_VIEW_FORM_NAME = "viewForm";

	/** 名簿ポッパーサービス */
	@Autowired
	private PersonPopoverService service;

	// Commonなのでindexメソッドはなし

	/**
	 * 顧客登録モーダルの表示
	 * 
	 * @param ankenId
	 * @param customerType
	 * @return
	 */
	@RequestMapping(value = "/getPersonPopoverFragment", method = RequestMethod.GET)
	public ModelAndView getPersonPopoverFragment(@RequestParam(name = "personId") Long personId) {

		ModelAndView mv = null;

		try {
			PersonPopoverViewForm viewForm = service.createPersonPopoverViewForm(personId);
			mv = getMyModelAndView(viewForm, PERSON_POPOVER_FRAGMENT_VIEW_PATH, PERSON_POPOVER_FRAGMENT_VIEW_FORM_NAME);

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
