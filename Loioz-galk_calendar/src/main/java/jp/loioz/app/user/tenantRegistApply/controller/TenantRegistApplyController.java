package jp.loioz.app.user.tenantRegistApply.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.tenantRegistApply.form.TenantRegistApplyForm;
import jp.loioz.app.user.tenantRegistApply.service.TenantRegistApplyService;
import jp.loioz.common.aspect.annotation.DenySubDomain;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.domain.verification.TempAccountVerificationService;

/**
 * 新規アカウント申込み画面のコントローラークラス
 */
@Controller
@RequestMapping("user/accountRegist")
@DenySubDomain
public class TenantRegistApplyController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/tenantRegistApply/tenantRegistApply";

	/** 申込み完了viewのパス */
	private static final String MY_VIEW_PATH_ACCEPTED = "user/tenantRegistApply/tenantRegistApplyAccepted";

	/** viewのformで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "tenantRegistApplyForm";

	/** 無料アカウント登録画面のサービスクラス */
	@Autowired
	private TenantRegistApplyService tenantRegistApplyService;

	/** アカウント認証(管理DB)サービスクラス */
	@Autowired
	private TempAccountVerificationService tempAccountVerificationService;

	/** フォームクラスの初期化 */
	@ModelAttribute
	TenantRegistApplyForm setUpForm() {
		return new TenantRegistApplyForm();
	}

	/** リダイレクト先の初期化 */
	@ModelAttribute
	RedirectViewBuilder setUpRedirectViewBuilder(RedirectAttributes redirectAttributes) {
		String redirectPath = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.index());
		return new RedirectViewBuilder(redirectAttributes, redirectPath);
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
	public ModelAndView index() {
		return ModelAndViewUtils.getModelAndView(MY_VIEW_PATH);
	}

	/**
	 * 新規アカウント申込み
	 *
	 * @param form 画面フォーム
	 * @param result バリデーション結果
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "form", params = "regist", method = RequestMethod.POST)
	public ModelAndView regist(@Validated TenantRegistApplyForm form, BindingResult result, RedirectViewBuilder redirectViewBuilder) {

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			return redirectViewBuilder.buildAsError(VIEW_FORM_NAME, form, result, MessageEnum.MSG_E00001.getMessageKey());
		}

		String mailAddress = form.getMailAddress();

		// 認証キーを生成
		String key = tempAccountVerificationService.createVerificationKey();

		// 一時アカウント情報を登録
		try {
			tenantRegistApplyService.registTempAccount(key, mailAddress);
		} catch (AppException e) {
			return redirectViewBuilder.buildAsError(VIEW_FORM_NAME, form, result, getMessage(e.getMessageKey()));
		}

		// 認証用メールを送信
		tenantRegistApplyService.sendVerificationMail(mailAddress, key);
		// 認証の通知メールを送信
		tenantRegistApplyService.sendVerificationMailSystemMgt(mailAddress);

		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.accepted(mailAddress));
	}

	/**
	 * 認証用メール送信完了画面
	 *
	 * @return 画面情報
	 */
	@RequestMapping(value = "/accepted", method = RequestMethod.GET)
	public ModelAndView accepted(String mailAddress) {
		return ModelAndViewUtils.getModelAndView(MY_VIEW_PATH_ACCEPTED).addObject("mailAddress", mailAddress);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
