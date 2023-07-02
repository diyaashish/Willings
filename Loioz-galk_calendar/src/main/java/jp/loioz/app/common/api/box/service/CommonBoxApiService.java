package jp.loioz.app.common.api.box.service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxFolder.Info;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.api.box.constants.BoxConstants.BoxAPIErrorResponse;
import jp.loioz.app.common.api.box.controller.BoxController;
import jp.loioz.app.common.api.box.form.BoxAuthStateJson;
import jp.loioz.app.common.api.box.form.BoxConnectDto;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.external.StorageConstants;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.HttpUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.domain.UriService;

/**
 * boxAPI用サービスクラス<br>
 * 
 * <pre>
 * ※ 基本的にAPI呼び出ししかしないので、トランザクションアノテーションはつけない
 * （発生したExceptionをサービスクラスの呼び出し元で握りつぶすことを可能性を考慮しているため）
 * ただし、今後DBへの登録・更新をこのクラスで行う場合は、メソッド単位でトランザクションアノテーションをつけること
 * </pre>
 */
@Service
public class CommonBoxApiService extends DefaultService {

	/** Box クライアントID */
	@Value("${box.client-id}")
	private String clientId;

	/** Box シークレットキー */
	@Value("${box.client-secret}")
	private String clientSecret;

	@Autowired
	private UriService uriService;

	@Autowired
	private Logger logger;

	/**
	 * Box認証のURLを取得する
	 *
	 * @param boxAuthStateJson
	 * @return
	 */
	public URL createAuthRequestUrl(BoxAuthStateJson boxAuthStateJson) {
		return createAuthRequestUrl(boxAuthStateJson, Collections.emptyList());
	}

	/**
	 * Box認証のURLを取得する
	 * 
	 * @param state
	 * @param scope
	 * @return
	 */
	public URL createAuthRequestUrl(BoxAuthStateJson boxAuthStateJson, List<String> scope) {

		String redirectUri = this.createRedirectUrl();

		// Box認証サーバーへのリダイレクトURLを作成
		URL authorizationUrl = BoxAPIConnection.getAuthorizationURL(clientId, URI.create(redirectUri), HttpUtils.base64UrlEncode(boxAuthStateJson.getJsonStr()), scope);

		return authorizationUrl;
	}

	/**
	 * OAuth認証後のcodeからアクセストークンのリクエストを行う
	 * 
	 * @param code
	 * @return
	 */
	public BoxAPIConnection requestAccessToken(String code) throws AppException {

		try {
			BoxAPIConnection api = new BoxAPIConnection(clientId, clientSecret, code);
			api.setAutoRefresh(true);
			return api;
		} catch (BoxAPIException e) {
			MessageEnum messageEnum = BoxAPIErrorResponse.getAppErrorMsgEnum(e.getResponseCode(), e.getResponse());
			if (messageEnum == null) {
				throw e;
			}
			String errorArgMsg = this.createArgsErrorMsg(e);
			logger.warn(errorArgMsg, e);
			throw new AppException(messageEnum, e, errorArgMsg);
		}
	}

	/**
	 * アクセストークンのリフレッシュ処理<br>
	 * 
	 * <pre>
	 * 　APIを利用する際は一番最初にこのメソッドを行うこと。
	 * 　また、リフレッシュが行われた場合、アクセストークンとリフレッシュトークンが再発行されるので、DBの更新を行うこと
	 * </pre>
	 * 
	 * @param accessToken
	 * @param refreshToken
	 * @return
	 */
	public BoxConnectDto refresh(String accessToken, String refreshToken) throws AppException {

		BoxConnectDto boxConnectDto = new BoxConnectDto();

		// ここのnewではBoxにリクエストを投げていないのでtry-catchは不要
		BoxAPIConnection api = this.getBoxAPIConnection(accessToken, refreshToken);

		if (api.needsRefresh()) {
			// needRefreshの判定時にバッファとして１分間設けられている(SDKの仕様)ので後続処理でトークンが切れることはない

			try {
				BoxFolder rootFolder = new BoxFolder(api, "0");
				rootFolder.getInfo();

				// リフレッシュが不要の場合はなにもしない。
				boxConnectDto.setRefreshed(false);
				boxConnectDto.setAccessToken(accessToken);
				boxConnectDto.setRefreshToken(refreshToken);
				return boxConnectDto;

			} catch (BoxAPIException e) {
				try {
					// リフレッシュ処理
					api.refresh();

					boxConnectDto.setRefreshed(true);
					boxConnectDto.setAccessToken(api.getAccessToken());
					boxConnectDto.setRefreshToken(api.getRefreshToken());
					return boxConnectDto;
				} catch (BoxAPIException ex) {
					MessageEnum messageEnum = BoxAPIErrorResponse.getAppErrorMsgEnum(ex.getResponseCode(), ex.getResponse());
					if (messageEnum == null) {
						throw ex;
					}

					String errorArgMsg = this.createArgsErrorMsg(ex);
					throw new AppException(messageEnum, ex, errorArgMsg);
				}
			}

		} else {
			// リフレッシュが不要の場合はなにもしない。
			boxConnectDto.setRefreshed(false);
			boxConnectDto.setAccessToken(accessToken);
			boxConnectDto.setRefreshToken(refreshToken);
			return boxConnectDto;
		}
	}

	/**
	 * アクセストークンの無効化
	 * 
	 * @param accessToken
	 * @param refreshToken
	 */
	public void revokeAccessToken(String accessToken, String refreshToken) throws AppException {

		BoxAPIConnection api = this.getBoxAPIConnection(accessToken, refreshToken);
		try {
			// 無効化のリクエスト
			api.revokeToken();
		} catch (BoxAPIException e) {
			MessageEnum messageEnum = BoxAPIErrorResponse.getAppErrorMsgEnum(e.getResponseCode(), e.getResponse());
			if (messageEnum == null) {
				throw e;
			}
			String errorArgMsg = this.createArgsErrorMsg(e);
			throw new AppException(messageEnum, e, errorArgMsg);
		}

	}

	/**
	 * リフレッシュトークンの無効化
	 * 
	 * @param refreshToken
	 * @throws AppException
	 */
	public void revokeRefreshToken(String refreshToken) throws AppException {

		// SDKのrevokeアクションでは、accessTokenフィールドの失効を行う仕様なので、refreshTokenをaccessTokenフィールドに設定して、revoke処理をする
		BoxAPIConnection api = new BoxAPIConnection(clientId, clientSecret);
		api.setAccessToken(refreshToken);

		try {
			// 無効化のリクエスト
			api.revokeToken();
		} catch (BoxAPIException e) {
			MessageEnum messageEnum = BoxAPIErrorResponse.getAppErrorMsgEnum(e.getResponseCode(), e.getResponse());
			if (messageEnum == null) {
				throw e;
			}
			String errorArgMsg = this.createArgsErrorMsg(e);
			throw new AppException(messageEnum, e, errorArgMsg);
		}

	}

	/**
	 * Box接続情報を取得する<br>
	 * 
	 * BoxAPIConnectionを利用する場合は、このメソッドを利用すること
	 * 
	 * @param accessToken
	 * @param refreshToken
	 * @return
	 */
	public BoxAPIConnection getBoxAPIConnection(String accessToken, String refreshToken) {

		// Box APIを接続
		BoxAPIConnection api = new BoxAPIConnection(clientId, clientSecret, accessToken, refreshToken);

		// トークンをDBに保持するのでリフレッシュトークンの自動更新はしない
		api.setAutoRefresh(false);

		return api;
	}

	/**
	 * ルートフォルダの作成
	 * 
	 * @param api
	 * @return
	 */
	public String createRootFolder(BoxAPIConnection api, boolean isReCreate) throws AppException {

		try {
			// ルートフォルダーを取得
			BoxFolder folder = BoxFolder.getRootFolder(api);

			// ロイオズルートフォルダーを作成
			Info loiozFolder = folder.createFolder(StorageConstants.ROOT_FOLDER_NAME);
			String folderId = loiozFolder.getID();

			return folderId;
		} catch (BoxAPIException e) {
			MessageEnum messageEnum = BoxAPIErrorResponse.getAppErrorMsgEnum(e.getResponseCode(), e.getResponse());
			if (messageEnum == null) {
				throw e;
			}
			String errorArgMsg = this.createArgsErrorMsg(e);
			logger.warn(errorArgMsg, e);
			if (messageEnum == MessageEnum.MSG_E00114) {
				String fncMsg = isReCreate ? "作成" : "連携フォルダを作成";
				throw new AppException(messageEnum, e, StorageConstants.ROOT_FOLDER_NAME, fncMsg, errorArgMsg);
			} else {
				throw new AppException(messageEnum, e, errorArgMsg);
			}
		}

	}

	/**
	 * フォルダーの作成処理
	 * 
	 * @param api
	 * @param folderId
	 * @param folderName
	 * @return
	 */
	public String createFolder(BoxAPIConnection api, String targetFolderId, String folderName) throws AppException {

		try {
			// 親フォルダー情報を取得
			BoxFolder targetFolder = new BoxFolder(api, targetFolderId);

			// ロイオズルートフォルダーを作成
			Info createdFolder = targetFolder.createFolder(folderName);
			String folderId = createdFolder.getID();

			return folderId;

		} catch (BoxAPIException e) {
			MessageEnum messageEnum = BoxAPIErrorResponse.getAppErrorMsgEnum(e.getResponseCode(), e.getResponse());
			if (messageEnum == null) {
				throw e;
			}
			String errorArgMsg = this.createArgsErrorMsg(e);
			logger.warn(errorArgMsg, e);
			if (messageEnum == MessageEnum.MSG_E00114) {
				throw new AppException(messageEnum, e, folderName, "作成", errorArgMsg);
			} else {
				throw new AppException(messageEnum, e, errorArgMsg);
			}
		}

	}

	/**
	 * BoxFolder情報の取得処理
	 * 
	 * @param api
	 * @param folderId
	 * @return
	 * @throws AppException
	 */
	public BoxFolder.Info getFolderInfo(BoxAPIConnection api, String folderId) throws AppException {
		try {
			BoxFolder rootFolder = new BoxFolder(api, folderId);
			BoxFolder.Info folderInfo = rootFolder.getInfo();
			return folderInfo;

		} catch (BoxAPIException e) {
			MessageEnum messageEnum = BoxAPIErrorResponse.getAppErrorMsgEnum(e.getResponseCode(), e.getResponse());
			if (messageEnum == null) {
				throw e;
			}
			String errorArgMsg = this.createArgsErrorMsg(e);
			throw new AppException(messageEnum, e, errorArgMsg);
		}
	}

	/**
	 * フォルダが削除されているかどうかを確認する
	 * 
	 * @param folderId
	 */
	public boolean isDeleted(BoxAPIConnection api, String folderId) throws AppException {

		try {
			// フォルダ情報の取得
			BoxFolder targetFolder = new BoxFolder(api, folderId);

			// getInfoメソッドを呼び出した時にAPIを投げている
			targetFolder.getInfo();

			// 取得できた場合は、フォルダは存在する
			return false;

		} catch (BoxAPIException e) {
			MessageEnum messageEnum = BoxAPIErrorResponse.getAppErrorMsgEnum(e.getResponseCode(), e.getResponse());
			if (messageEnum == null) {
				throw e;
			}

			// 削除済エラーの場合はtrue
			if (messageEnum == MessageEnum.MSG_E00113) {
				return true;
			}

			// その他のエラーの場合、AppException
			String errorArgMsg = this.createArgsErrorMsg(e);
			throw new AppException(messageEnum, e, errorArgMsg);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * リダイレクトURLの作成
	 * 
	 * @return
	 */
	private String createRedirectUrl() {
		String uri = ModelAndViewUtils.getRedirectPath(BoxController.class, controller -> controller.boxAuthGatewayRedirect(null, null, null, null));
		uri = uri.replaceFirst(UrlConstant.SLASH, CommonConstant.BLANK);
		uri = uri.substring(0, uri.indexOf(UrlConstant.QUESTION));
		return uriService.createUrl("", uri);
	}

	/**
	 * BoxAPIエラーのメッセージ最後に表示する原因を取得する
	 * 
	 * @param boxAPIException
	 * @return
	 * @throws AppException
	 */
	private String createArgsErrorMsg(BoxAPIException boxAPIException) throws AppException {

		String code = "";
		String error = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readValue(boxAPIException.getResponse(), JsonNode.class);
			code = node.get("code") != null ? node.get("code").asText() : "";
			error = node.get("error") != null ? node.get("error").asText() : "";
		} catch (IOException e) {
			// 何もしない
		}

		String errorMessage = "【Error: statusCode: %s, cause : %s】";
		return String.format(errorMessage, boxAPIException.getResponseCode(), code.length() > error.length() ? code : error);
	}

}
