package jp.loioz.app.user.preLogin.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.domain.UriService;

/**
 * プレログイン画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PreLoginService extends DefaultService {

	/** Google reCAPTCHA のシークレットキー */
	@Value("${google.recaptcha.secret}")
	private String googleRecaptchaSecret;
	
	/** URIを扱うサービスクラス */
	@Autowired
	private UriService uriService;
	
	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================
	
	/**
	 * reCAPTCHA のresponse値の検証を行う。
	 * 
	 * <pre>
	 * 検証NGの場合はAppExceptionをスローする。
	 * 検証OKの場合は戻り値はない。
	 * </pre>
	 * 
	 * @param gRecaptchaResponse
	 * @throws AppException
	 */
	public void verifyRecaptchaResponse(String gRecaptchaResponse) throws AppException {
		
		// reCAPTCHA のresponse値の妥当性を検証
		HttpResponse<String> apiResponse = this.callGoogleRecaptchaVerifyAPI(gRecaptchaResponse);
		
		if (200 != apiResponse.statusCode()) {
			// 検証失敗（通信失敗などのケースのためシステムエラーとする）
			throw new AppException(MessageEnum.MSG_E00091, null);
		}
		
		// APIのレスポンス情報をMapに変換
		Map<String, String> responseMap = this.toMapGoogleRecaptchaVerifyResponse(apiResponse.body());
		
		String isSuccess = responseMap.get("success");
		if (!Boolean.valueOf(isSuccess)) {
			// 検証結果がNGの場合
			// ※検証がNGになるのは、基本は不正なリクエスト（gRecaptchaResponseの値が不正なケース）のみとなるためシステムエラーとする
			throw new AppException(MessageEnum.MSG_E00091, null);
		}
	}
	
	/**
	 * 対象サブドメインのログイン画面のURLを取得する
	 * 
	 * @return
	 */
	public String getLoginUrl(String subDomain) {

		final String TENANT_DOMAIN = uriService.getTenantDomain(subDomain);
		String loginUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
				.host(TENANT_DOMAIN)
				.path("/" + UrlConstant.LOGIN_URL + "/")
				.toUriString();

		return loginUrl;
	}
	
	// =========================================================================
	// private メソッド
	// =========================================================================
	
	/**
	 * Google reCAPTCHA のresponse値の検証を行う（Googleの検証APIをコールし検証結果を取得する）
	 * 
	 * @param gRecaptchaResponse
	 * @return
	 * @throws AppException
	 */
	private HttpResponse<String> callGoogleRecaptchaVerifyAPI(String gRecaptchaResponse) throws AppException {
		
		URI uri = URI.create("https://www.google.com/recaptcha/api/siteverify");
		
		String params =String.join(
			"&",
			"secret" + "=" + googleRecaptchaSecret,
			"response" + "=" + gRecaptchaResponse
		);
		BodyPublisher bodyPublisher = BodyPublishers.ofString(params);
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(uri)
				.POST(bodyPublisher)
				// recaptchaの検証メソッドはjsonではなくurlパラメータとして値を送信する必要があるため下記を設定
				.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
				.build();
		HttpClient httpClient = HttpClient.newBuilder()
				.build();
		
		HttpResponse<String> response;
		
		try {
			
			response = httpClient.send(request, BodyHandlers.ofString(java.nio.charset.StandardCharsets.UTF_8));
		
		} catch (IOException | InterruptedException e) {
			
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + " Google reCAPTCHAの認証APIの通信で失敗", e);
			
			throw new AppException(MessageEnum.MSG_E00091, e);
		}
		
		return response;
	}
	
	/**
	 * Google reCAPTCHA のresponse値の検証APIのレスポンス情報をMapに変換する
	 * 
	 * @param responseBody
	 * @return
	 */
	private Map<String, String> toMapGoogleRecaptchaVerifyResponse(String responseBody) {
		
		Map<String, String> resultMap = new HashMap<>();
		
		// Google側のAPI仕様でパラメータが決まっているため、
		// 固定でインデックス番号を指定してMapに設定する。
		
		try {
			
			JSONObject jsonObject = new JSONObject(responseBody);
		
			String success = String.valueOf(jsonObject.get("success"));
			resultMap.put("success", success); // 成功、失敗のbool値
			
			Boolean successBool = Boolean.valueOf(success);
			if (successBool) {
				// 成功ステータスの場合
				return resultMap;
			}
			
			// 失敗ステータスの場合
			Object errorCodesObj = jsonObject.get("error-codes");
			JSONArray errorCodes = (JSONArray)errorCodesObj;
		
			logger.error("Google reCAPTCHAの認証APIでエラーが発生しました。[error-codes] " + errorCodes);
			
			return resultMap;
			
		} catch (JSONException e) {
			// JSONの形式に不備がある場合などに発生する。
			// GoogleのAPIが返却するレスポンスの形式が変わらない限り、基本的には発生しない
			
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + " Google reCAPTCHAの認証APIのレスポンスデータが想定外の形式でした。 [ResponseBody] " + responseBody, e);
			
			throw new RuntimeException("レスポンスデータの形式が不正。レスポンスデータ：" + responseBody, e);
		}
	}

}
