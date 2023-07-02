package jp.loioz.app.user.invitedAccountRegist.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.validation.accessDB.CommonAccountValidator;
import jp.loioz.app.user.invitedAccountRegist.form.InvitedAccountRegistForm;
import jp.loioz.app.user.invitedAccountRegist.service.InvitedAccountRegistService;
import jp.loioz.common.aspect.annotation.RequireSubDomain;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.dataSource.SchemaContextHolder;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.UriService;
import jp.loioz.domain.verification.InvitedAccountVerificationService;
import jp.loioz.domain.verification.VerificationErrorType;
import jp.loioz.entity.TInvitedAccountVerificationEntity;

/**
 * 招待アカウント登録画面のコントローラークラス
 */
@Controller
@RequestMapping("user/invitedAccountRegist")
@RequireSubDomain
public class InvitedAccountRegistController extends DefaultController {

	/** 招待アカウント登録viewのパス */
	private static final String INVITED_ACCOUNT_REGIST_VIEW = "user/invitedAccountRegist/invitedAccountRegist";

	/** 招待アカウントログインフォームfragmentのパス */
	private static final String INVITED_ACCOUNT_REGIST_FORM_FRAGMENT_PATH = "user/invitedAccountRegist/invitedAccountRegistFragment::invitedAccountRegistFormFragment";
	
	/** 招待アカウントログインフォームfragmentのパス */
	private static final String INVITED_ACCOUNT_LOGIN_FRAGMENT_PATH = "user/invitedAccountRegist/invitedAccountRegistFragment::invitedAccountLoginFormFragment";

	/** フォームオブジェクト名 */
	private static final String INPUT_FORM_NAME = "inputForm";
	
	/** サービスクラス */
	@Autowired
	private InvitedAccountRegistService service;

	/** 招待アカウント認証サービスクラス */
	@Autowired
	private InvitedAccountVerificationService invitedAccountVerificationService;
	
	/** URIサービスクラス */
	@Autowired
	private UriService uriService;

	/** 共通アカウントDB整合性バリデーター */
	@Autowired
	private CommonAccountValidator commonAccountValidator;

	/** ロガー */
	@Autowired
	private Logger logger;

	/** 認証エラーとメッセージの紐付け */
	private static final Map<VerificationErrorType, String> ERROR_TO_MESSAGE_MAP = new HashMap<VerificationErrorType, String>() {
		private static final long serialVersionUID = 1L;
		{
			this.put(VerificationErrorType.NOT_EXISTS, "E00146");
			this.put(VerificationErrorType.EXPIRED, "E00147");
			this.put(VerificationErrorType.COMPLETED, "E00148");
		}
	};

	// =========================================================================
	// public メソッド
	// =========================================================================
	
	/**
	 * 初期表示
	 * 
	 * @param key
	 * @return
	 */
	@RequestMapping(value = "/{key}", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable("key") String key) {
		
		// サブドメインが存在しない場合はエラー
		String subDomain = uriService.getSubDomainName();
		Long tenantSeq = service.getTenantSeq(subDomain);
		if (tenantSeq == null) {
			// 認証エラー画面を返却
			return this.verificationErrorPage(getMessage(MessageEnum.MSG_E00146));
		}
		
		try {
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
			
			// 認証キーが有効かチェック
			TInvitedAccountVerificationEntity entity = invitedAccountVerificationService.getVerification(key);
			VerificationErrorType error = invitedAccountVerificationService.validate(entity);
			if (error != null) {
				// 認証エラー画面を返却
				return this.verificationErrorPage(getMessage(ERROR_TO_MESSAGE_MAP.get(error)));
			}
			
			InvitedAccountRegistForm inputForm = new InvitedAccountRegistForm();
			
			//
			// formにメールアドレスを設定する (メールアドレスはEntityから取得する)
			//
			inputForm.setKey(key);
			inputForm.setAccountMailAddress(entity.getMailAddress());
			
			return getMyModelAndView(inputForm, INVITED_ACCOUNT_REGIST_VIEW, INPUT_FORM_NAME);
			
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}
	}
	
	/**
	 * アカウント登録
	 * 
	 * @param form
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/accountRegist", method = RequestMethod.POST)
	public ModelAndView accountRegist(@Validated InvitedAccountRegistForm form, BindingResult result) {
		ModelAndView mv;

		// サブドメインが存在しない場合はエラー
		String subDomain = uriService.getSubDomainName();
		Long tenantSeq = service.getTenantSeq(subDomain);
		if (tenantSeq == null) {
			// 認証エラー画面を返却
			super.setAjaxProcResultFailure("");
			return this.ajaxVerificationErrorPage(getMessage(MessageEnum.MSG_E00146));
		}
		
		try {
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
			
			// 認証キーが有効かチェック
			TInvitedAccountVerificationEntity entity = invitedAccountVerificationService.getVerification(form.getKey());
			VerificationErrorType error = invitedAccountVerificationService.validate(entity);
			if (error != null) {
				// 認証エラー画面を返却
				super.setAjaxProcResultFailure("");
				return this.ajaxVerificationErrorPage(getMessage(ERROR_TO_MESSAGE_MAP.get(error)));
			}

			// formのプロパティのバリデーション
			if (result.hasErrors()) {
				// バリデーションエラーが存在する場合
				
				// 処理失敗とする
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 入力フォームにerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(form, INVITED_ACCOUNT_REGIST_FORM_FRAGMENT_PATH, INPUT_FORM_NAME, result);
			}

			//DBアクセスを伴うチェック
			String dberrorMessage = accessDBValidatedForAccountRegistSave(form,tenantSeq);
			if (!StringUtils.isEmpty(dberrorMessage)) {
				
				// 認証エラー画面（ajax用）を返却
				super.setAjaxProcResultFailure(dberrorMessage);
				// 入力フォームにerror情報を設定した画面を返却
				return getMyModelAndViewWithErrors(form, INVITED_ACCOUNT_REGIST_FORM_FRAGMENT_PATH, INPUT_FORM_NAME, result);
			}
			
			try {

				// アカウントテーブルへの登録処理＆招待アカウント認証テーブルへの更新処理（1件）
				service.accountRegist(form,tenantSeq);

			} catch (AppException ex) {

				// 想定されるエラー(楽観ロックエラーなど)
				super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
				return null;

			} catch (Exception ex) {

				// 想定しないシステムエラー
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00012));
				logger.error("error", ex);

				return null;
			}
			
			// フロント側処理へ
			String currentContextPath = ServletUriComponentsBuilder.fromCurrentContextPath()
					.toUriString();
			mv = ModelAndViewUtils.getModelAndView(INVITED_ACCOUNT_LOGIN_FRAGMENT_PATH)
					.addObject("contextPath", currentContextPath);
			
			// メール通知
			// 登録処理とは別トランザクションで行う
			
			try {
				// 【登録者宛】アカウント登録完了メール
				service.sendAccountRegistMail(form, tenantSeq);
			} catch (Exception e) {
				logger.warn("アカウント登録完了メール（登録者宛）の送信に失敗しました。");
			}
			try {
				// 【管理者宛】アカウント登録完了メール
				service.sendAccountRegistAdminMail(form, tenantSeq);
			} catch (Exception e) {
				logger.warn("アカウント登録完了メール（管理者宛）の送信に失敗しました。");
			}
			
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00132));
			return mv;
			
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}
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
	private ModelAndView verificationErrorPage(String errorMsg) {
		return ModelAndViewUtils.getModelAndView("user/invitedAccountRegist/invitedAccountRegistVerificationError").addObject("errorMsg", errorMsg);
	}

	/**
	 * エラー画面を表示する(Ajax)
	 *
	 * @param errorMsg エラーメッセージ
	 * @return エラー画面情報
	 */
	private ModelAndView ajaxVerificationErrorPage(String errorMsg) {
		return ModelAndViewUtils.getModelAndView("user/invitedAccountRegist/invitedAccountRegistFragment::invitedAccountRegistVerificationError").addObject("errorMsg", errorMsg);
	}

	/**
	 * DBアクセスを伴うチェック
	 * 
	 * @param viewForm
	 * @param tenantSeq
	 * @return
	 */
	private String accessDBValidatedForAccountRegistSave(InvitedAccountRegistForm viewForm, long tenantSeq) {

		// ライセンス数制限チェック
		if (service.checkLicenseLimitForAccountRegistSave(viewForm, tenantSeq)) {
			// ライセンスを全て利用している場合
			return getMessage(MessageEnum.MSG_E00090);
		}

		// アカウントIDの重複チェック
		if (commonAccountValidator.checkAccountIdExists(
				viewForm.getAccountId(),null)){
			// アカウントIDがすでに使用されている
			return  getMessage(MessageEnum.MSG_E00031, "アカウントID");
		}

		return "";
	}
}
