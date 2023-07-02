package jp.loioz.common.handlerInterceptor;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TLoginRecordDao;
import jp.loioz.entity.TLoginRecordEntity;

/**
 * 二重ログイン防止処理のHandlerInterceptor
 */
public class PreventMultiLoginHandlerInterceptor implements HandlerInterceptor {

	/** アプリケーションの起動モード ※起動時のコマンドライン引数で渡す値のためapplication.propertiesには記載されていない */
	@Value("${app.bootMode}")
	private String appBootMode;

	/** ログイン記録用Dao */
	@Autowired
	private TLoginRecordDao tLoginRecordDao;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		// アプリケーションの起動モードがシステム管理（admin）の場合は何もしない
		if (appBootMode != null && appBootMode.equals(CommonConstant.ADMIN_APP_BOOT_MODE_VAL)) {
			// Handlerメソッドを実行する
			return true;
		}

		Long tenantSeq = SessionUtils.getTenantSeq();
		Long loginAccountSeq = SessionUtils.getLoginAccountSeq();

		if (CommonUtils.anyNull(tenantSeq, loginAccountSeq)) {
			
			// エラーが発生したURL情報をWARNログに出力
			String method = request.getMethod();
			String currentRequestUrl = ServletUriComponentsBuilder.fromCurrentRequestUri()
					.toUriString();
			logger.warn("PreventMultiLoginHandlerInterceptor#preHandleエラー発生URL：" + method + " " + currentRequestUrl);
			
			throw new IllegalStateException("ログインしていないとアクセス不可な処理が未ログイン状態で実行された");
		}

		HttpSession session = request.getSession();

		boolean isLastLoingUserSession = this.isLastLoingUserSession(tenantSeq, loginAccountSeq, session.getId());
		if (!isLastLoingUserSession) {
			// 対象のsessionIdが対象アカウントの最終ログインのsessionIdではない
			// -> 同一アカウントで他のsessionIdが発行された（二重ログインが行われた）場合

			try {
				// セッションを破棄
				session.invalidate();
			} catch (IllegalStateException ex) {
				// 既にセッションが破棄されている場合 -> 特に何もしない
			}

			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
				// ajaxリクエストの場合

				// エラーコードを設定
				response.setStatus(CommonConstant.CUSTOM_MULTI_LOGIN_ERROR_CODE);
			} else {
				// 通常のhttpリクエストの場合 -> エラー画面へのリダイレクト設定
				response.sendRedirect(UrlConstant.MULTI_LOGIN_ERROR_URL);
			}

			// Handlerメソッドは実行しない
			return false;
		}

		// Handlerメソッドを実行する
		return true;
	}

	/**
	 * 引数のsessionIdの値が、<br>
	 * 引数のアカウントの最終ログイン時のsessionIdと同じ値かどうかを判定
	 * 
	 * @param tenantSeq
	 * @param accountSeq
	 * @param sessionId
	 * @return
	 */
	private boolean isLastLoingUserSession(Long tenantSeq, Long accountSeq, String sessionId) {

		TLoginRecordEntity tLoginRecord = tLoginRecordDao.selectByTenantSeqAndAccountSeq(tenantSeq, accountSeq);
		if (Objects.isNull(tLoginRecord)) {
			throw new IllegalStateException("ログインしている場合、必ず存在するデータが存在していない");
		}

		if (StringUtils.isEmpty(sessionId)) {
			throw new IllegalArgumentException("sessionIdは必須の値です");
		}

		String lastLoingSessionId = tLoginRecord.getSessionId();
		if (Objects.equals(sessionId, lastLoingSessionId)) {
			return true;
		}

		return false;
	}
}
