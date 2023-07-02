package jp.loioz.common.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.ViewType;
import jp.loioz.common.constant.CookieConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.http.CookieService;
import jp.loioz.common.service.http.UserAgentService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.TLoginRecordDao;
import jp.loioz.domain.UriService;
import jp.loioz.entity.TLoginRecordEntity;

/**
 * Spring Securityの認証成功時のハンドラクラス
 */
@Component
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler {
	
	/** URIサービスクラス */
	@Autowired
	private UriService uriService;

	/** Cookieサービスクラス */
	@Autowired
	private CookieService cookieService;

	/** UserAgentサービスクラス */
	@Autowired
	private UserAgentService userAgentService;

	/** ログイン記録用のDao */
	@Autowired
	private TLoginRecordDao tLoginRecordDao;

	/** ログ出力クラス */
	@Autowired
	private Logger logger;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// ログイン時の端末種類をセッションに保持
		SessionUtils.setDeviceType(userAgentService.getDevice(request));
		if (userAgentService.isSmartPhone(request)) {
			// スマホ版ページに遷移するように設定
			SessionUtils.setViewType(ViewType.MOBILE);
		} else {
			// PC版ページに遷移するように設定
			SessionUtils.setViewType(ViewType.PC);
		}

		// サブドメイン情報をCookieに設定
		this.addSubDomainCookie(response);
		// アカウントIDをCookieに保持
		this.settingCookieOfLoginUserId(request, response);

		// ログイン記録の保存
		try {
			this.saveLoginRecord(request.getSession().getId());
		} catch (AppException ex) {
			throw new IOException(ex);
		}

		Boolean isOnlyPlanSettingAccessible = SessionUtils.isOnlyPlanSettingAccessible();
		if (isOnlyPlanSettingAccessible) {
			// プラン設定画面の入り口となるコントローラーメソッドにリダイレクトする

			String uriPlanSetting = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/" + UrlConstant.PLANSETTING_GATEWAY_URL + "/" + PlanConstant.PLAN_SETTING_GATEWAY_REDIRECT_PATH)
					.toUriString();

			response.sendRedirect(uriPlanSetting);
			return;
		}

		// ログイン完了後、初期遷移リダイレクトパスを設定
		String redirectPath = UrlConstant.LOGIN_SUCCESS_URL;

		// テナント作成後のログインかを判別して、リダイレクトパスにパラメータを設定
		boolean isTenantCreate = SystemFlg.codeToBoolean(request.getParameter("tenantCreateFlg"));
		if (isTenantCreate) {
			// 一時的にセッションに保存
			HttpSession session = request.getSession();
			session.setAttribute("tenantCreateFlg", 1);
		}

		// ログイン後の画面にリダイレクトする
		response.sendRedirect(redirectPath);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * サブドメイン情報をCookieに設定する
	 * 
	 * @param subDomainName
	 * @param response
	 */
	private void addSubDomainCookie(HttpServletResponse response) {

		String subDomainName = uriService.getSubDomainName();

		// Cookie追加
		cookieService.addCookieForHttp(CookieConstant.COOKIE_NAME_OF_SUBDOMAIN, subDomainName, null, response);
	}

	/**
	 * ログイン操作時に「アカウントIDの保存」を選択している場合<br>
	 * IDをCookieに保持する。
	 * 
	 * @param request
	 * @param response
	 */
	private void settingCookieOfLoginUserId(HttpServletRequest request, HttpServletResponse response) {

		String loginUserId = request.getParameter(CommonConstant.LOGIN_FORM_OF_ACCOUNT_ID_NAME);
		String keepingFlg = request.getParameter(CommonConstant.LOGIN_FORM_OF_KEEP_ACCOUNT_FLG_FLG_NAME);

		// Cookieからの削除は必ず行う
		cookieService.removeCookie(CookieConstant.COOKIE_NAME_OF_LONGIN_ACCOUNT_ID, response);

		// 「アカウントIDを保存」にチェックがされた場合のみ、Cookieに追加する
		if (Boolean.valueOf(keepingFlg)) {

			// 暗号化 暗号キーとSaltの設定
			TextEncryptor text = Encryptors.text(CookieConstant.COOKIE_ENCRYPTION_KEY, CookieConstant.COOKIE_ENCRYPTION_SALT);
			String deluxEncryptText = text.encrypt(loginUserId);
			cookieService.addCookieForHttp(CookieConstant.COOKIE_NAME_OF_LONGIN_ACCOUNT_ID, deluxEncryptText, CookieConstant.COOKIE_EXPRIRATION_TIME, response);
		}
	}

	/**
	 * ログイン記録情報の保存
	 * 
	 * @param sessionId
	 * @throws AppException
	 */
	private void saveLoginRecord(String sessionId) throws AppException {
		
		Long tenantSeq = SessionUtils.getTenantSeq();
		Long loginAccountSeq = SessionUtils.getLoginAccountSeq();
		
		TLoginRecordEntity updateEntity = tLoginRecordDao.selectByTenantSeqAndAccountSeq(tenantSeq, loginAccountSeq);
		if (updateEntity == null) {
			// 初めてのログイン -> 登録処理
			
			TLoginRecordEntity insertEntity = new TLoginRecordEntity();
			insertEntity.setTenantSeq(tenantSeq);
			insertEntity.setAccountSeq(loginAccountSeq);
			insertEntity.setSessionId(sessionId);
			
			this.registLoginRecord(insertEntity);
		} else {
			// 初回以降のログイン -> 更新処理
			
			updateEntity.setSessionId(sessionId);
			
			this.updateLoginRecord(updateEntity);
		}
	}
	
	/**
	 * ログイン記録情報の登録
	 * 
	 * @param insertEntity
	 * @throws AppException
	 */
	private void registLoginRecord(TLoginRecordEntity insertEntity) throws AppException {
		// 登録件数
		int insertEntityCount = 0;

		// 登録
		insertEntityCount = tLoginRecordDao.insert(insertEntity);

		if (insertEntityCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}
	
	/**
	 * ログイン記録情報の更新
	 * 
	 * @param updateEntity
	 * @throws AppException
	 */
	private void updateLoginRecord(TLoginRecordEntity updateEntity) throws AppException {
		// 更新件数
		int udpateEntityCount = 0;

		try {
			// 更新
			udpateEntityCount = tLoginRecordDao.update(updateEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		if (udpateEntityCount != 1) {
			// 更新処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

}
