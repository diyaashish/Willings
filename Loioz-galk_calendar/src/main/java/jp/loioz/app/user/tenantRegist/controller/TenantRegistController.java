package jp.loioz.app.user.tenantRegist.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.tenantRegist.form.SubDomainSettingForm;
import jp.loioz.app.user.tenantRegist.form.TenantRegistForm;
import jp.loioz.app.user.tenantRegist.service.TenantRegistService;
import jp.loioz.common.aspect.annotation.DenySubDomain;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.log.Logger;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.domain.UriService;
import jp.loioz.domain.verification.TempAccountVerificationService;
import jp.loioz.domain.verification.VerificationErrorType;
import jp.loioz.entity.TTempAccountEntity;

/**
 * 新規アカウント申込み-詳細情報入力画面のコントローラークラス
 */
@Controller
@RequestMapping("user/accountDetailRegist")
public class TenantRegistController extends DefaultController {

	/** 事務所情報入力viewのパス */
	private static final String OWNER_REGIST_VIEW = "user/tenantRegist/tenantRegistFragment";

	/** 事務所情報入力用viewのパス(Ajax) */
	private static final String OWNER_REGIST_VIEW_AJAX = OWNER_REGIST_VIEW + "::ajax-contents";

	/** 登録成功時のログイン用viewのパス(Ajax) */
	private static final String LOGIN_FORM_VIEW_AJAX = "user/tenantRegist/tenantLoginFragment::login-form";

	/** viewのformで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "form";

	/** 認証エラーとメッセージの紐付け */
	private static final Map<VerificationErrorType, String> ERROR_TO_MESSAGE_MAP = new HashMap<VerificationErrorType, String>() {
		private static final long serialVersionUID = 1L;
		{
			this.put(VerificationErrorType.NOT_EXISTS, MessageEnum.MSG_E00003.getMessageKey());
			this.put(VerificationErrorType.EXPIRED, MessageEnum.MSG_E00004.getMessageKey());
			this.put(VerificationErrorType.COMPLETED, MessageEnum.MSG_E00005.getMessageKey());
		}
	};

	/** アカウント情報詳細入力画面のサービスクラス */
	@Autowired
	private TenantRegistService tenantRegistService;

	/** アカウント認証(管理DB)サービスクラス */
	@Autowired
	private TempAccountVerificationService tempAccountVerificationService;

	/** URIサービスクラス */
	@Autowired
	private UriService uriService;

	/** ロガー */
	@Autowired
	private Logger log;

	/** フォームクラスの初期化 */
	@ModelAttribute
	TenantRegistForm setUpForm() {
		return new TenantRegistForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 初期表示<br>
	 * 認証キーが指定されていない場合なので、エラー画面に遷移する
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {
		String errorMsg = getMessage(MessageEnum.MSG_E00003.getMessageKey());
		return errorPage(errorMsg);
	}

	/**
	 * メール認証
	 *
	 * @param key 認証キー
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "{key}", method = RequestMethod.GET)
	@DenySubDomain
	public ModelAndView verify(@PathVariable("key") String key) {

		// 認証キーが有効かチェック
		TTempAccountEntity entity = tempAccountVerificationService.getVerification(key);
		VerificationErrorType error = tempAccountVerificationService.validate(entity);
		if (error != null) {
			String errorMsg = getMessage(ERROR_TO_MESSAGE_MAP.get(error));
			return errorPage(errorMsg);
		}

		String mailAddress = entity.getAccountId();

		TenantRegistForm form = new TenantRegistForm();
		form.setKey(key);
		form.getUser().setAccountMailAddress(mailAddress);// ユーザーのアカウント情報にメールアドレスをデフォルト値として設定

		return getMyModelAndView(form, OWNER_REGIST_VIEW, VIEW_FORM_NAME).addObject("mailAddress", mailAddress);
	}

	/**
	 * 登録処理
	 *
	 * @param form フォームオブジェクト
	 * @param result バリデーション結果
	 * @param session セッション
	 * @param request リクエスト
	 * @param response レスポンス
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "form", params = "regist", method = RequestMethod.POST)
	@DenySubDomain
	public ModelAndView regist(@Validated @ModelAttribute(VIEW_FORM_NAME) TenantRegistForm form, BindingResult result, HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {
		MessageHolder msgHolder = new MessageHolder();

		if (SessionUtils.isAlreadyLoggedUser()) {
			// 別タブなどでロイオズアプリケーションを開いており、既にログイン済みの状態の場合は、
			// セッションを破棄し未ログイン状態とする。
			SessionUtils.logout(request.getSession());
		}

		// 認証キーが有効かチェック
		String key = form.getKey();
		TTempAccountEntity entity = tempAccountVerificationService.getVerification(key);
		VerificationErrorType error = tempAccountVerificationService.validate(entity);
		if (error != null) {
			String errorMsg = getMessage(ERROR_TO_MESSAGE_MAP.get(error));
			return ajaxErrorPage(errorMsg);
		}

		String mailAddress = entity.getAccountId();

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			msgHolder.setErrorMsg(getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			return getMyModelAndView(form, OWNER_REGIST_VIEW_AJAX, VIEW_FORM_NAME, msgHolder).addObject("mailAddress", mailAddress);
		}

		// サブドメインの重複チェック
		Map<String, String> msgMap = new HashMap<>();// エラーがあった時のメッセージを格納
		if (!accessDBValidatedForSubDomain(form.getDomain(), msgMap)) {
			msgHolder.setErrorMsg(msgMap.get("message"));
			return getMyModelAndView(form, OWNER_REGIST_VIEW_AJAX, VIEW_FORM_NAME, msgHolder).addObject("mailAddress", mailAddress);
		}

		// テナント管理情報を登録
		Long tenantSeq = tenantRegistService.registTenantMgt(form);

		// テナントDBを作成
		try {
			tenantRegistService.createTenantDB(tenantSeq);
		} catch (Exception e) {
			// テナントDB作成に失敗した場合
			log.error("テナントDBの作成に失敗しました。不要なテナントDBの情報が残っている可能性があります。(tenantSeq={})", tenantSeq);
			tenantRegistService.deleteTenantMgt(tenantSeq);
			msgHolder.setErrorMsg(getMessage(MessageEnum.MSG_E00007.getMessageKey()));
			return getMyModelAndView(form, OWNER_REGIST_VIEW_AJAX, VIEW_FORM_NAME, msgHolder).addObject("mailAddress", mailAddress);
		}

		// DB接続先をテナントDBに切り替える
		tenantRegistService.useTenantDB(tenantSeq);

		// 入力情報を登録
		try {
			tenantRegistService.registFormInfo(form, tenantSeq, mailAddress);
		} catch (Exception e) {
			// 登録に失敗した場合
			log.error("アカウント情報の登録に失敗しました。不要なテナントDBの情報が残っています。(tenantSeq={})", tenantSeq);
			tenantRegistService.deleteTenantMgt(tenantSeq);
			msgHolder.setErrorMsg(getMessage(MessageEnum.MSG_E00007.getMessageKey()));
			return getMyModelAndView(form, OWNER_REGIST_VIEW_AJAX, VIEW_FORM_NAME, msgHolder).addObject("mailAddress", mailAddress);
		}

		// 登録完了メールを送信
		tenantRegistService.sendCompleteMail2User(mailAddress, form); // ユーザーに送信
		tenantRegistService.sendCompleteMail2SystemManager(mailAddress, form); // システム管理者に送信

		// サブドメインのコンテキストパスを計算
		URI currentContextPathUri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUri();
		String subDomainContextPath = uriService.appendSubDomain(currentContextPathUri, form.getDomain().getDomain()).toString();

		// ログインフォームを返却する。画面側のJSでログイン処理にsubmitされる
		return ModelAndViewUtils.getModelAndView(LOGIN_FORM_VIEW_AJAX)
				.addObject("contextPath", subDomainContextPath);
	}

	/**
	 * ドメインの入力チェック
	 * 
	 * @param domainForm
	 * @param result
	 * @return バリデーション結果
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "domainFormValid", method = RequestMethod.POST)
	@DenySubDomain
	public Map<String, Object> domainFormValid(@Validated TenantRegistForm form, BindingResult result) {

		// レスポンスの作成
		Map<String, Object> response = new HashMap<>();

		// ドメインの入力チェック
		if (result.hasFieldErrors("domain.*")) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors("domain.*"));
			return response;
		}

		// サブドメインの重複チェック
		Map<String, String> msgMap = new HashMap<>();// エラーがあった時のメッセージを格納
		if (!accessDBValidatedForSubDomain(form.getDomain(), msgMap)) {
			response.put("succeeded", false);
			response.put("message", msgMap.get("message"));
			return response;
		}

		// ここまで来たら入力値は正常
		response.put("succeeded", true);
		return response;
	}

	/**
	 * 事務所情報の入力チェック
	 * 
	 * @param form
	 * @param result
	 * @return バリデーション結果
	 */
	@ResponseBody
	@RequestMapping(value = "tenantFormValid", method = RequestMethod.POST)
	@DenySubDomain
	public Map<String, Object> tenantFormValid(@Validated TenantRegistForm form, BindingResult result) {

		// レスポンスの作成
		Map<String, Object> response = new HashMap<>();

		// テナント情報の入力チェック
		if (result.hasFieldErrors("tenant.*")) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors("tenant.*"));
			return response;
		}

		// ここまで来たら入力値は正常
		response.put("succeeded", true);
		return response;
	}

	/**
	 * アカウント情報の入力チェック
	 * 
	 * @param form
	 * @param result
	 * @return バリデーション結果
	 */
	@ResponseBody
	@RequestMapping(value = "userFormValid", method = RequestMethod.POST)
	@DenySubDomain
	public Map<String, Object> userFormValid(@Validated TenantRegistForm form, BindingResult result) {

		// レスポンスの作成
		Map<String, Object> response = new HashMap<>();

		// テナント情報の入力チェック
		if (result.hasFieldErrors("user.*")) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors("user.*"));
			return response;
		}

		// ここまで来たら入力値は正常
		response.put("succeeded", true);
		return response;
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
		return ModelAndViewUtils.getModelAndView("user/tenantRegist/tenantRegistError")
				.addObject("errorMsg", errorMsg);
	}

	/**
	 * エラー画面を表示する(Ajax)
	 *
	 * @param errorMsg エラーメッセージ
	 * @return エラー画面情報
	 */
	private ModelAndView ajaxErrorPage(String errorMsg) {
		return ModelAndViewUtils.getModelAndView("user/tenantRegist/tenantRegistError::ajax-contents")
				.addObject("errorMsg", errorMsg);
	}

	/**
	 * ドメインのDB整合性チェック
	 * 
	 * @param form ドメイン情報
	 * @param errorMsg (key : message) エラーがあった場合のメッセージを格納
	 * @return 検証結果
	 */
	private boolean accessDBValidatedForSubDomain(SubDomainSettingForm form, Map<String, String> errorMsg) {

		// サブドメインの重複チェック
		if (tenantRegistService.domainExists(form.getDomain())) {
			errorMsg.put("message", getMessage(MessageEnum.MSG_E00006));
			return false;
		}

		// ここまで来たら、エラーなし
		return true;
	}
}
