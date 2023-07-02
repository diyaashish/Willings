package jp.loioz.common.handlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.app.common.service.CommonBackLinkService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.HttpUtils;
import jp.loioz.common.utility.SessionUtils;

/**
 * 戻るリンク（パンくずに表示のリンク）の初期化、設定処理を行うHandlerInterceptor
 */
public class SettingBackLinkSessionValueHandlerInterceptor implements HandlerInterceptor {

	/** 戻るリンク（パンくずに表示のリンク）の共通サービスクラス */
	@Autowired
	private CommonBackLinkService commonBackLinkService;

	/**
	 * <pre>
	 * ヘッダー部分の戻るリンクの初期化、設定を行う。
	 * 
	 * 戻るリンクの情報は、
	 * 案件軸の画面から、名簿情報画面へ遷移したときに設定され、
	 * 名簿軸の画面から、名簿軸以外の画面へ遷移したときに初期化（リセット）するようにする。
	 * 
	 * 初期化、設定は、画面遷移の場合のみ行うため、
	 * 初期化、設定を行うリクエストの候補は下記とする。
	 * ・GETメソッド
	 * ・ajaxのリクエストは対象外
	 * 
	 * </pre>
	 * 
	 * <pre>
	 * {@inheritDoc}
	 * </pre>
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		Long tenantSeq = SessionUtils.getTenantSeq();
		Long loginAccountSeq = SessionUtils.getLoginAccountSeq();
		if (CommonUtils.anyNull(tenantSeq, loginAccountSeq)) {
			// 未ログイン状態の場合 -> 戻るリンクの初期化、設定は行わず、Handlerメソッドを実行
			return true;
		}

		// GETリクエストかのチェック
		String method = request.getMethod();
		if (!"GET".equals(method)) {
			// リクエストがGETではない場合 -> 戻るリンクの初期化、設定は行わず、Handlerメソッドを実行
			return true;
		}

		// ajaxリクエストかのチェック
		boolean isAjaxRequest = HttpUtils.isAjax(request);
		if (isAjaxRequest) {
			// リクエストがajaxの場合 -> 戻るリンクの初期化、設定は行わず、Handlerメソッドを実行
			return true;
		}

		// リファラー
		String referer = request.getHeader(HttpHeaders.REFERER);
		// リクエストURI
		String requestUri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();

		// 一覧へ戻るリンクを初期化
		commonBackLinkService.resetBackListLinkSetting(referer, requestUri);
		// 一覧へ戻るリンクを設定
		commonBackLinkService.setBackListLinkSetting(referer, requestUri);

		// 戻るリンクを初期化
		commonBackLinkService.resetBackPrevLinkSetting(referer, requestUri);
		// 戻るリンクを設定
		commonBackLinkService.setBackPrevLinkSetting(referer, requestUri);

		// 会計管理：案件軸の遷移かどうかを設定する
		commonBackLinkService.setAccgAnkenRefererSetting(requestUri);

		// Handlerメソッドを実行
		return true;
	}

}
