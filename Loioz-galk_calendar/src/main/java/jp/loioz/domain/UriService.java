package jp.loioz.domain;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import jp.loioz.app.user.tenantRegistApply.controller.TenantRegistApplyController;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.dao.MTenantMgtDao;
import jp.loioz.entity.MTenantMgtEntity;

/**
 * URIを扱うサービスクラス
 */
@Service
public class UriService {

	/** サーバーのドメイン名(ユーザー側) */
	@Value("${domain.name}")
	private String domainName;

	/** テナント管理用のDaoクラス */
	@Autowired
	private MTenantMgtDao mTenantMgtDao;

	/**
	 * サブドメイン名を取得する
	 *
	 * @param uri URI
	 * @return サブドメイン名
	 */
	public String getSubDomainName(URI uri) {
		String host = ServletUriComponentsBuilder.fromUri(uri).build().getHost();

		if (host != null && host.endsWith("." + domainName)) {
			// サブドメインを切り出す
			return Stream.of(host.split(Pattern.quote("."))).findFirst().orElse("");

		} else {
			// サブドメイン付きのアクセスではない場合
			return "";
		}
	}

	/**
	 * 現在のリクエスト情報からサブドメイン名を取得する
	 *
	 * @return サブドメイン名
	 */
	public String getSubDomainName() {
		URI currentUri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUri();
		return getSubDomainName(currentUri);
	}

	/**
	 * サブドメイン付きのアクセスであるか判定する
	 *
	 * @param uri URI
	 * @return サブドメイン付きの場合はtrue、それ以外はfalse
	 */
	public boolean isSubDomainAccess(URI uri) {
		return !StringUtils.isEmpty(getSubDomainName(uri));
	}

	/**
	 * 現在のリクエスト情報がサブドメイン付きのアクセスであるか判定する
	 *
	 * @return サブドメイン付きの場合はtrue、それ以外はfalse
	 */
	public boolean isSubDomainAccess() {
		return !StringUtils.isEmpty(getSubDomainName());
	}

	/**
	 * URIにサブドメインを付与する
	 *
	 * @param uri URI
	 * @param subDomain サブドメイン
	 * @return サブドメイン付きURI
	 */
	public URI appendSubDomain(URI uri, String subDomain) {

		// 既にサブドメイン付きの場合はそのまま返却
		if (isSubDomainAccess(uri)) {
			return uri;
		}

		String host = ServletUriComponentsBuilder.fromUri(uri).build().getHost();

		return UriComponentsBuilder.fromUri(uri).host(subDomain + "." + host).build().toUri();
	}

	/**
	 * ドメイン名を取得する（サブドメインは含まない）
	 *
	 * @return
	 */
	public String getDomain() {
		return domainName;
	}

	/**
	 * テナントのドメインを取得する
	 *
	 * @param subDomain サブドメイン
	 * @return テナントのドメイン([サブドメイン].[ユーザー画面ドメイン])
	 */
	public String getTenantDomain(String subDomain) {
		return subDomain + "." + domainName;
	}

	/**
	 * アカウント申込画面のURLを取得する
	 *
	 * @return
	 */
	public String getAccountRegistUrl() {
		UriComponentsBuilder currentContextPath = ServletUriComponentsBuilder.fromCurrentContextPath().path("/");
		return MvcUriComponentsBuilder.relativeTo(currentContextPath)
				.withMethodCall(on(TenantRegistApplyController.class).index())
				.host(domainName)
				.toUriString();
	}

	/**
	 * テナント連番からユーザー側のログインURLを取得する
	 *
	 * @param tenantSeq
	 * @return
	 */
	public String getUserLoginUrl(Long tenantSeq) {

		// テナント連番からサブドメインを取得
		MTenantMgtEntity entity = mTenantMgtDao.selectBySeq(tenantSeq);
		String subDomain = entity.getSubDomain();

		return getUserLoginUrlWithSubDomain(subDomain);
	}

	/**
	 * 現在のリクエスト情報のサブドメインからユーザー側のログインURLを取得する
	 *
	 * @return
	 */
	public String getUserLoginUrl() {

		// 現在のリクエスト情報を取得
		UriComponents uri = ServletUriComponentsBuilder.fromCurrentContextPath().build();

		// サブドメインを取得
		String subDomain = this.getSubDomainName(uri.toUri());

		return getUserLoginUrlWithSubDomain(subDomain);
	}

	/**
	 * サブドメインを指定してユーザー側のログインURLを取得する
	 *
	 * @param subDomain
	 * @return
	 */
	public String getUserLoginUrlWithSubDomain(String subDomain) {
		return getUserLoginUrl(withSubDomain(subDomain));
	}

	/**
	 * サブドメインを設定するURIビルダーオプションを取得する
	 *
	 * @param subDomain
	 * @return
	 */
	public UnaryOperator<UriComponentsBuilder> withSubDomain(String subDomain) {
		return builder -> {
			return builder.host(this.getTenantDomain(subDomain));
		};
	}

	/**
	 * ユーザー側のログインURLを取得する
	 *
	 * @param initialId ID初期値(null可)
	 * @param builderOption URIビルダー追加設定(null可)
	 * @return
	 */
	public String getUserLoginUrl(@Nullable UnaryOperator<UriComponentsBuilder> builderOption) {

		// ログイン画面URLを構築
		UriComponentsBuilder currentContextPath = ServletUriComponentsBuilder.fromCurrentContextPath().path("/");
		UriComponentsBuilder loginUriBuilder = MvcUriComponentsBuilder.relativeTo(currentContextPath)
				.withMethodCall(on(jp.loioz.app.user.login.controller.LoginController.class).index(null, null, null));
		if (builderOption != null) {
			loginUriBuilder = builderOption.apply(loginUriBuilder);
		}

		return loginUriBuilder.toUriString();
	}

	/**
	 * ユーザー側、任意の画面URLを取得します
	 *
	 * @param controllerType コントローラークラス
	 * @param invocation メソッド呼び出し情報<br>
	 * ({@link ModelAndViewUtils#getRedirectModelAndView(Object,Object)} と同様の呼び出し方法)
	 * @return
	 */
	public <T> String getUserUrl(Class<T> controllerType, Function<T, ModelAndView> invocation) {

		// 任意画面の画面URLを構築
		UriComponentsBuilder currentContextPath = ServletUriComponentsBuilder.fromCurrentContextPath().path("/");
		UriComponentsBuilder uriBuilder = MvcUriComponentsBuilder.relativeTo(currentContextPath)
				.withMethodCall(invocation.apply(on(controllerType)));
		return uriBuilder.toUriString();
	}

	/**
	 * リクエストパラメータに{@link UrlConstant#ACCG_REFERER_ANKEN_SIDE_PARAM_NAME} が含まれており、
	 * 値が”true”であるかどうか
	 * 
	 * @return
	 */
	public boolean currentRequestAnkenSideParamHasTrue() {

		UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequest().build();
		MultiValueMap<String, String> params = uriComponents.getQueryParams();

		List<String> isAccgAnkenSideParams = params.getOrDefault(UrlConstant.ACCG_REFERER_ANKEN_SIDE_PARAM_NAME, Collections.emptyList());
		return isAccgAnkenSideParams.stream().map(Boolean::parseBoolean).anyMatch(Boolean::booleanValue);
	}

	/**
	 * グローバル関連のURLを作成
	 * 
	 * @param controllerType コントローラークラス
	 * @param invocation メソッド呼び出し情報<br>
	 * ({@link ModelAndViewUtils#getRedirectModelAndView(Object,Object)} と同様の呼び出し方法)
	 * @return
	 */
	public <T> String getGlobalUrl(Class<T> controllerType, Function<T, ModelAndView> invocation) {
		UriComponentsBuilder currentContextPath = ServletUriComponentsBuilder.fromCurrentContextPath().path("/");
		return MvcUriComponentsBuilder.relativeTo(currentContextPath)
				.withMethodCall(invocation.apply(on(controllerType)))
				.host(domainName)
				.toUriString();
	}

	/**
	 * アカウント招待URLを取得する
	 *
	 * @param companySeq
	 * @param key
	 * @return
	 */
	public String getAccountDetailRegistUrl(Long companySeq, String key) {

		// 現在のリクエスト情報を取得
		UriComponents uri = ServletUriComponentsBuilder.fromCurrentContextPath().build();
		String scheme = uri.getScheme();
		String domain = uri.getHost();
		int port = uri.getPort();

		// 認証キーを入れる
		String verificationKey = key;

		// ログイン画面URLを構築
		return this.buildUrl(scheme, domain, port, UrlConstant.REGIST_URL, verificationKey);
	}

	/**
	 * ログイン成功後のURLを取得する
	 *
	 * @param companySeq
	 * @param key
	 * @return
	 */
	public String getLoginSuccessUrl(String subDomain) {

		// 現在のリクエスト情報を取得
		UriComponents uri = ServletUriComponentsBuilder.fromCurrentContextPath().build();
		String scheme = uri.getScheme();
		String domain = subDomain + UrlConstant.DOT + this.getDomain();
		int port = uri.getPort();

		// ログイン画面URLを構築
		return this.buildUrl(scheme, domain, port, UrlConstant.LOGIN_SUCCESS_URL.replaceFirst(UrlConstant.SLASH, CommonConstant.BLANK), null);
	}

	/**
	 * 任意のURLを作成します
	 * 
	 * @param uri
	 * @return 作成されたURL
	 */
	public String createUrl(String subDomain, String uri) {

		// 現在のリクエスト情報を取得
		UriComponents requestUri = ServletUriComponentsBuilder.fromCurrentContextPath().build();
		String scheme = requestUri.getScheme();
		String domain = "";
		if (StringUtils.isEmpty(subDomain)) {
			domain = this.getDomain();
		} else {
			domain = subDomain + UrlConstant.DOT + this.getDomain();
		}
		int port = requestUri.getPort();

		return this.buildUrl(scheme, domain, port, uri, null);
	}

	/**
	 * URL情報を構築する
	 *
	 * @param scheme スキーマ
	 * @param domain ドメイン
	 * @param port ポート番号
	 * @param uri URI
	 * @param verificationKey 認証キー
	 * @return returnUrl
	 */
	private String buildUrl(String scheme, String domain, int port, String uri, String verificationKey) {

		String returnUrl = null;
		if (verificationKey == null) {
			if (scheme.equals("http")) {
				if (port != 80 && port != -1) {
					// (http)ポート番号を含める
					returnUrl = scheme + "://" + domain + ":" + port + "/" + uri;
				} else {
					returnUrl = scheme + "://" + domain + "/" + uri;
				}
			} else if (scheme.equals("https")) {
				if (port != 443 && port != -1) {
					// (https)ポート番号を含める
					returnUrl = scheme + "://" + domain + ":" + port + "/" + uri;
				} else {
					returnUrl = scheme + "://" + domain + "/" + uri;
				}
			}
		} else {
			// 認証キーが存在する場合
			if (scheme.equals("http")) {
				if (port != 80 && port != -1) {
					// (http)ポート番号を含める
					returnUrl = scheme + "://" + domain + ":" + port + "/" + uri + "/" + verificationKey;
				} else {
					returnUrl = scheme + "://" + domain + "/" + uri + "/" + verificationKey;
				}
			} else if (scheme.equals("https")) {
				if (port != 443 && port != -1) {
					// (https)ポート番号を含める
					returnUrl = scheme + "://" + domain + ":" + port + "/" + uri + "/" + verificationKey;
				} else {
					returnUrl = scheme + "://" + domain + "/" + uri + "/" + verificationKey;
				}
			}
		}

		return returnUrl;
	}

}
