package jp.loioz.app.user.mailTemplate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.mailTemplate.form.MailTemplateViewForm;
import jp.loioz.app.user.mailTemplate.service.MailTemplateService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * メールテンプレート画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/mailTemplate")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class MailTemplateController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/mailTemplate/mailTemplate";

	/** メールテンプレート設定：一覧フラグメントviewのパス */
	private static final String MAIL_TEMPLATE_FRAGMENT_VIEW_PATH = "user/mailTemplate/mailTemplateFragment::mailTemlpateListViewFragment";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** メールテンプレート設定：一覧フラグメントフォームオブジェクト */
	private static final String MAIL_TEMPLATE_FRAGMENT_VIEW_FORM_NAME = "mailTemlpateListViewForm";

	/** メールテンプレートの設定のサービスクラス */
	@Autowired
	private MailTemplateService mailTemplateService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * メールテンプレートの設定画面初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {

		// 画面フォームオブジェクトを作成
		var viewForm = new MailTemplateViewForm();

		// テンプレート一覧画面オブジェクトを作成
		MailTemplateViewForm.MailTemlpateListViewForm mailTemlpateListViewForm = mailTemplateService.createMailTemlpateListViewForm();
		viewForm.setMailTemlpateListViewForm(mailTemlpateListViewForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * メールテンプレート一覧画面情報を取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getMailTemlpateListViewFragment", method = RequestMethod.GET)
	public ModelAndView getMailTemlpateListViewFragment() {

		ModelAndView mv = null;

		// テンプレート一覧画面オブジェクトを作成
		MailTemplateViewForm.MailTemlpateListViewForm mailTemlpateListViewForm = mailTemplateService.createMailTemlpateListViewForm();
		mv = getMyModelAndView(mailTemlpateListViewForm, MAIL_TEMPLATE_FRAGMENT_VIEW_PATH, MAIL_TEMPLATE_FRAGMENT_VIEW_FORM_NAME);

		if (mv == null) {
			// 画面オブジェクトの取得失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * メールテンプレートの削除完了後画面表示用
	 *
	 * @param attributes
	 * @return
	 */
	@RequestMapping(value = "/deleteSuccessRedirect", method = RequestMethod.GET)
	public ModelAndView deleteSuccessRedirect(RedirectAttributes attributes) {

		RedirectViewBuilder builder = new RedirectViewBuilder(attributes, ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.index()));
		return builder.build(MessageHolder.ofInfo(getMessage(MessageEnum.MSG_I00028, "メールテンプレート")));
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
