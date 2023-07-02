package jp.loioz.app.user.passwordForgetRequest.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.passwordForgetRequest.form.PasswordChangeForm;
import jp.loioz.app.user.passwordForgetRequest.form.PasswordForgetRequestForm;
import jp.loioz.app.user.passwordForgetRequest.service.PasswordForgetRequestService;
import jp.loioz.common.aspect.annotation.RequireSubDomain;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.log.Logger;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.domain.UriService;
import jp.loioz.domain.verification.AccountVerificationService;
import jp.loioz.domain.verification.VerificationErrorType;
import jp.loioz.entity.TAccountVerificationEntity;

/**
 * パスワード忘れ申請画面のコントローラークラス
 */
@Controller
@RequestMapping("user/passwordForgetRequest")
@RequireSubDomain
public class PasswordForgetRequestController extends DefaultController {

	/** パスワード忘れ申請viewのパス */
	private static final String PASSWORD_FORGET_REQUEST_VIEW = "user/passwordForgetRequest/passwordForgetRequest";
	/** パスワード忘れ申請メール認証完了のパス */
	private static final String MAIL_VERIFICATION_ACCEPTED_VIEW_PATH = "user/passwordForgetRequest/passwordForgetRequestAccepted";
	/** パスワード変更viewのパス */
	private static final String PASSWORD_CHANGE_VIEW = "user/passwordForgetRequest/passwordChange";
	/** パスワード変更リクエスト時の入力エラーリダイレクト用viewのパス(Ajax) */
	private static final String PASSWORD_CHANGE_CONTENTS_VIEW_AJAX = PASSWORD_CHANGE_VIEW + "::passwordChangeContents";
	/** パスワード変更完了時のログイン用viewのパス(Ajax) */
	private static final String LOGIN_FORM_VIEW_AJAX = "user/passwordForgetRequest/passwordChangeLogin::login-form";

	/** パスワード忘れ申請のフォームオブジェクト名 */
	private static final String PASSWORD_FORGET_REQUEST_FORM_NAME = "passwordForgetRequestForm";
	/** パスワード変更のフォームオブジェクト名 */
	private static final String PASSWORD_CHANGE_FORM_NAME = "passwordChangeForm";

	/** 認証エラーとメッセージの紐付け */
	private static final Map<VerificationErrorType, String> ERROR_TO_MESSAGE_MAP = new HashMap<VerificationErrorType, String>() {
		private static final long serialVersionUID = 1L;
		{
			this.put(VerificationErrorType.NOT_EXISTS, "E00003");
			this.put(VerificationErrorType.EXPIRED, "E00004");
			this.put(VerificationErrorType.COMPLETED, "E00011");
		}
	};

	/** サービスクラス */
	@Autowired
	private PasswordForgetRequestService service;

	/** アカウント認証(テナントDB)サービスクラス */
	@Autowired
	private AccountVerificationService accountVerificationService;

	/** URIサービスクラス */
	@Autowired
	private UriService uriService;

	/** ロガー */
	@Autowired
	private Logger log;

	/** フォームクラスの初期化 */
	@ModelAttribute
	PasswordForgetRequestForm setUpForm() {
		return new PasswordForgetRequestForm();
	}

	/** フォームクラスの初期化 */
	@ModelAttribute
	PasswordChangeForm setUpPasswordChangeForm() {
		return new PasswordChangeForm();
	}

	/** リダイレクト先の初期化 */
	@ModelAttribute
	RedirectViewBuilder setUpRedirectViewBuilder(RedirectAttributes redirectAttributes) {

		// パスワード忘れ申請画面のパスを取得する
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
		return ModelAndViewUtils.getModelAndView(PASSWORD_FORGET_REQUEST_VIEW);
	}

	/**
	 * パスワード忘れ申請
	 *
	 * @param form 画面フォーム
	 * @param result バリデーション結果
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "form", params = "request", method = RequestMethod.POST)
	public ModelAndView request(@Validated PasswordForgetRequestForm form, BindingResult result, RedirectViewBuilder redirectViewBuilder) {

		// バリデーション
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			return redirectViewBuilder.buildAsError(PASSWORD_FORGET_REQUEST_FORM_NAME, form, result, null);
		}

		// テナント存在チェック
		String subDomain = uriService.getSubDomainName();
		Long tenantSeq = service.getTenantSeq(subDomain);
		if (tenantSeq == null) {
			// テナントが存在しない場合はアカウント存在チェックと同様のエラーメッセージを返却
			result.rejectValue("accountId", MessageEnum.MSG_E00017.getMessageKey());
			return redirectViewBuilder.buildAsError(PASSWORD_FORGET_REQUEST_FORM_NAME, form, result, null);
		}

		// テナントDB切り替え
		service.useTenantDB(tenantSeq);

		String accountId = form.getAccountId();

		// アカウント存在チェック
		if (!service.hasMailAddressAccount(accountId)) {
			// メールアドレスが未登録
			result.rejectValue("accountId", MessageEnum.MSG_E00017.getMessageKey());
			return redirectViewBuilder.buildAsError(PASSWORD_FORGET_REQUEST_FORM_NAME, form, result, null);
		}

		// アカウント認証情報を登録
		try {
			service.registAccountVerification(accountId);
		} catch (Exception e) {
			return redirectViewBuilder.buildAsError(PASSWORD_FORGET_REQUEST_FORM_NAME, form, result,
					getMessage(MessageEnum.MSG_E00012));
		}

		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.accepted());
	}

	/**
	 * 認証用メール送信完了画面
	 *
	 * @return 画面情報
	 */
	@RequestMapping(value = "/accepted", method = RequestMethod.GET)
	public ModelAndView accepted() {
		return ModelAndViewUtils.getModelAndView(MAIL_VERIFICATION_ACCEPTED_VIEW_PATH);
	}

	/**
	 * メール認証
	 *
	 * @param key 認証キー
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "verify/{key}", method = RequestMethod.GET)
	public ModelAndView verify(@PathVariable("key") String key) {

		// サブドメインが存在しない場合はエラー
		String subDomain = uriService.getSubDomainName();
		Long tenantSeq = service.getTenantSeq(subDomain);
		if (tenantSeq == null) {
			return errorPage(getMessage(ERROR_TO_MESSAGE_MAP.get(VerificationErrorType.NOT_EXISTS)));
		}

		// テナントDB切り替え
		service.useTenantDB(tenantSeq);

		// 認証キーが有効かチェック
		TAccountVerificationEntity entity = accountVerificationService.getVerification(key);
		VerificationErrorType error = accountVerificationService.validate(entity);
		if (error != null) {
			return errorPage(getMessage(ERROR_TO_MESSAGE_MAP.get(error)));
		}
		String accountId = entity.getAccountId();

		// アカウント存在チェック
		if (!service.accountExists(accountId)) {
			return errorPage(getMessage(MessageEnum.MSG_E00009));
		}

		PasswordChangeForm form = new PasswordChangeForm();
		form.setKey(key);

		return getMyModelAndView(form, PASSWORD_CHANGE_VIEW, PASSWORD_CHANGE_FORM_NAME).addObject("accountId", accountId);
	}

	/**
	 * パスワード変更
	 *
	 * @param form フォームオブジェクト
	 * @param result バリデーション結果
	 * @param session セッション
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "form", params = "change", method = RequestMethod.POST)
	public ModelAndView change(@Validated PasswordChangeForm form, BindingResult result, HttpSession session) {
		MessageHolder msgHolder = new MessageHolder();

		// サブドメインが存在しない場合はエラー
		String subDomain = uriService.getSubDomainName();
		Long tenantSeq = service.getTenantSeq(subDomain);
		if (tenantSeq == null) {
			return ajaxErrorPage(ERROR_TO_MESSAGE_MAP.get(VerificationErrorType.NOT_EXISTS));
		}

		// テナントDB切り替え
		service.useTenantDB(tenantSeq);

		// 認証キーが有効かチェック
		String key = form.getKey();
		TAccountVerificationEntity entity = accountVerificationService.getVerification(key);
		VerificationErrorType error = accountVerificationService.validate(entity);
		if (error != null) {
			String errorMsg = getMessage(ERROR_TO_MESSAGE_MAP.get(error));
			return ajaxErrorPage(errorMsg);
		}
		String accountId = entity.getAccountId();

		// アカウント存在チェック
		if (!service.accountExists(accountId)) {
			return ajaxErrorPage(getMessage(MessageEnum.MSG_E00009));
		}

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			return getMyModelAndView(form, PASSWORD_CHANGE_CONTENTS_VIEW_AJAX, PASSWORD_CHANGE_FORM_NAME).addObject("accountId", accountId);
		}

		// パスワード変更
		try {
			service.changePassword(form, accountId);
		} catch (Exception e) {
			// 変更に失敗した場合
			log.warn("パスワードの変更に失敗しました。");
			msgHolder.setErrorMsg(getMessage(MessageEnum.MSG_E00010));
			return getMyModelAndView(form, PASSWORD_CHANGE_CONTENTS_VIEW_AJAX, PASSWORD_CHANGE_FORM_NAME, msgHolder).addObject("accountId",
					accountId);
		}

		// ログインフォームを返却する。画面側のJSでログイン処理にsubmitされる
		return ModelAndViewUtils.getModelAndView(LOGIN_FORM_VIEW_AJAX).addObject("accountId", accountId);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * エラー画面を表示する
	 *
	 * @param errorMsg エラーメッセージ
	 * @return エラー画面情報
	 */
	private ModelAndView errorPage(String errorMsg) {
		return ModelAndViewUtils.getModelAndView("user/passwordForgetRequest/passwordChangeError").addObject("errorMsg", errorMsg);
	}

	/**
	 * エラー画面を表示する(Ajax)
	 *
	 * @param errorMsg エラーメッセージ
	 * @return エラー画面情報
	 */
	private ModelAndView ajaxErrorPage(String errorMsg) {
		return ModelAndViewUtils.getModelAndView("user/passwordForgetRequest/passwordChangeError :: ajax-contents").addObject("errorMsg",
				errorMsg);
	}
}
