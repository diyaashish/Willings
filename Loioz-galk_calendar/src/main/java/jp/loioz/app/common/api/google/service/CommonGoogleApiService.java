package jp.loioz.app.common.api.google.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.api.google.constants.GoogleConstants;
import jp.loioz.app.common.api.google.constants.GoogleConstants.DriveAPIError;
import jp.loioz.app.common.api.google.constants.GoogleConstants.DriveFileField;
import jp.loioz.app.common.api.google.constants.GoogleConstants.DriveMineType;
import jp.loioz.app.common.api.google.constants.GoogleConstants.GoogleAPIScope;
import jp.loioz.app.common.api.google.constants.GoogleConstants.OAuthAPIError;
import jp.loioz.app.common.api.google.controller.GoogleController;
import jp.loioz.app.common.api.google.form.GoogleAuthStateJson;
import jp.loioz.app.common.api.google.form.GoogleConnectDto;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.external.StorageConstants;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.HttpUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.UriService;

/**
 * googleAPI用サービスクラス<br>
 * 
 * <pre>
 * ※ 基本的にAPI呼び出ししかしないので、トランザクションアノテーションはつけない
 * （発生したExceptionをサービスクラスの呼び出し元で握りつぶすことを可能性を考慮しているため）
 * ただし、今後DBへの登録・更新をこのクラスで行う場合は、メソッド単位でトランザクションアノテーションをつけること
 * </pre>
 */
@Service
public class CommonGoogleApiService extends DefaultService {

	/** Google アプリ名 */
	@Value("${google.app-name}")
	private String appName;

	/** Google クライアントID */
	@Value("${google.client-id}")
	private String clientId;

	/** Box シークレットキー */
	@Value("${google.client-secret}")
	private String clientSecret;

	@Autowired
	private UriService uriService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * Google認証のURLを作成する
	 * 
	 * @param googleAuthStateJson
	 * @return
	 */
	public String createAuthRequestUrl(GoogleAuthStateJson googleAuthStateJson) {

		// リダイレクトURLを作成する。
		String redirectUri = this.createRedirectUrl();

		// Google認証のURL
		GoogleAuthorizationCodeRequestUrl authRequestUrl = new GoogleAuthorizationCodeRequestUrl(clientId, redirectUri, Arrays.asList(GoogleAPIScope.DRIVE.getScope()));
		return authRequestUrl.setState(HttpUtils.base64UrlEncode(googleAuthStateJson.getJsonStr())).setAccessType("offline").setApprovalPrompt("force").build();
	}

	/**
	 * Googleトークンの取得処理
	 * 
	 * @param code
	 * @return
	 * @throws AppException
	 */
	public GoogleConnectDto requestAccessToken(String code) throws AppException {

		String redirectUri = this.createRedirectUrl();

		// トークン取得リクエストの作成
		GoogleAuthorizationCodeTokenRequest tokenRequest = new GoogleAuthorizationCodeTokenRequest(this.getTransport(), this.getJsonFactory(), clientId, clientSecret, code, redirectUri);

		try {
			// Googleトークンの取得処理
			GoogleTokenResponse response = tokenRequest.execute();

			GoogleConnectDto dto = new GoogleConnectDto();
			dto.setAccessToken(response.getAccessToken());
			dto.setRefreshToken(response.getRefreshToken());
			dto.setRefreshed(false);
			return dto;

		} catch (IOException ex) {

			if (ex instanceof TokenResponseException) {
				// トークンリクエスト処理で認証エラーが発生した場合
				TokenResponseException tokenResponseException = (TokenResponseException) ex;

				String error = "";
				if (tokenResponseException.getDetails() == null) {
					error = tokenResponseException.getStatusMessage();
				} else {
					error = tokenResponseException.getDetails().getError();
				}

				MessageEnum messageEnum = OAuthAPIError.getAppErrorMsgEnum(tokenResponseException.getStatusCode(), error);
				String createArgsErrorMsg = createArgsErrorMsg(tokenResponseException.getStatusCode(), error);

				if (messageEnum == null) {
					// エラーが判別できなかった場合
					throw new RuntimeException(ex);
				}
				throw new AppException(messageEnum, ex, createArgsErrorMsg);
			}
			// 想定外のエラー
			throw new RuntimeException("Google認証で想定外のエラーが発生しました。", ex);
		}
	}

	/**
	 * Googleリフレッシュ処理
	 * 
	 * @param refreshToken
	 * @return
	 * @throws AppException
	 */
	public GoogleConnectDto refreshToken(String accessToken, String refreshToken) throws AppException {

		GoogleConnectDto dto = new GoogleConnectDto();

		// リフレッシュトークン取得リクエストの作成
		GoogleRefreshTokenRequest refreshRequest = new GoogleRefreshTokenRequest(this.getTransport(), this.getJsonFactory(), refreshToken, clientId, clientSecret);

		try {
			// 適当なAPIを実行し、有効期限内か判断する
			this.getDriveService(accessToken).files().list().setPageSize(1).execute();

			// ファイルの取得でエラーが発生しない場合は、有効期限内
			dto.setRefreshed(false);
			return dto;

		} catch (IOException e) {
			// どんなエラーが発生しても、トークンのリフレッシュを行う。
			try {
				// Googleトークンリフレッシュの処理
				GoogleTokenResponse response = refreshRequest.execute();

				// 更新したトークン情報を返却
				dto.setAccessToken(response.getAccessToken());
				dto.setRefreshToken(refreshToken); // RefreshToken は取得されないらしいので確認
				dto.setRefreshed(true);
				return dto;

			} catch (IOException ex) {
				if (ex instanceof TokenResponseException) {
					// リフレッシュ処理で認証エラーが発生した場合
					TokenResponseException tokenResponseException = (TokenResponseException) ex;

					String error = "";
					if (tokenResponseException.getDetails() == null) {
						error = tokenResponseException.getStatusMessage();
					} else {
						error = tokenResponseException.getDetails().getError();
					}

					MessageEnum messageEnum = OAuthAPIError.getAppErrorMsgEnum(tokenResponseException.getStatusCode(), error);
					String createArgsErrorMsg = createArgsErrorMsg(tokenResponseException.getStatusCode(), error);

					if (messageEnum == null) {
						// エラーが判別できなかった場合
						throw new RuntimeException(ex);
					}
					throw new AppException(messageEnum, ex, createArgsErrorMsg);
				}
				// 想定外のエラー
				throw new RuntimeException("Google認証で想定外のエラーが発生しました。", ex);
			}
		}
	}

	/**
	 * トークンの無効化処理
	 * 
	 * @param accessToken
	 * @throws AppException
	 */
	public void revokeToken(String accessToken) throws AppException {

		// javaSDK内にrevoke処理が見つからない(検索しても出てこない)ので、ここだけSDKを利用せずにリクエストを投げる。
		try {
			HttpRequest request = HttpRequest.newBuilder(URI.create(GoogleConstants.GOOGLE_AUTH_ROVOKE_BASE))
					.POST(BodyPublishers.ofString("token=" + accessToken))
					.setHeader("content-type", "application/x-www-form-urlencoded")
					.build();
			HttpClient httpClient = HttpClient.newBuilder()
					.build();

			HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString(java.nio.charset.StandardCharsets.UTF_8));

			if (200 != response.statusCode()) {
				// responseに何が返ってくるか不明。 正常終了なら200で、エラーなら400が返って来るとしかGoogleには書いてない
				// 連携解除は、基本的にDBのレコードの有無で判断するので、revoke処理が失敗してもレコード削除で正常終了を想定している(ここでthrowしたAppExceptionを握りつぶす)
				// AppExceptionとして飛ばすが、ここのエラーを画面に表示することはない
				throw new AppException(null, null);
			}

		} catch (IOException | InterruptedException e) {
			throw new AppException(null, e);
		}

	}

	/**
	 * GoogleDriveを扱うAPIクラスを取得する
	 * 
	 * @param accessToken
	 * @return
	 */
	public Drive getDriveService(String accessToken) {
		Drive service = new Drive.Builder(this.getTransport(), this.getJsonFactory(), this.getCredential(accessToken))
				.setApplicationName(appName)
				.build();
		return service;
	}

	/**
	 * loiozルートフォルダの作成
	 * 
	 * @param googleDriveService
	 * @return
	 */
	public String createRootFolder(Drive googleDriveService) throws AppException {

		File fileMetadata = new File();
		fileMetadata.setName(StorageConstants.ROOT_FOLDER_NAME);
		fileMetadata.setMimeType(DriveMineType.FOLDER.getMinetype());

		try {
			File file = googleDriveService.files().create(fileMetadata)
					.setFields(this.createFileField(DriveFileField.ID))
					.execute();

			return file.getId();
		} catch (IOException e) {
			// 基本的にGoogleJsonResponseExceptionのみ発生する
			if (e instanceof GoogleJsonResponseException) {
				// GoogleJsonResponseExceptionにキャスト
				GoogleJsonResponseException googleJsonResponseException = (GoogleJsonResponseException) e;
				GoogleJsonError jsonErrors = googleJsonResponseException.getDetails();

				// レスポンスからエラーを判別する
				String createArgErrorMsg = createArgsErrorMsg(jsonErrors.getCode(), jsonErrors.getErrors().stream().findFirst().orElseThrow().getReason());
				MessageEnum messageEnum = DriveAPIError.getAppErrorMsgEnum(jsonErrors.getCode(), jsonErrors.getErrors().stream().findFirst().orElseThrow().getReason());

				if (messageEnum == null) {
					throw new RuntimeException(e);
				}
				throw new AppException(messageEnum, e, createArgErrorMsg);
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * フォルダの作成
	 * 
	 * @param googleDriveService
	 * @param parentFolderId
	 * @param folderName
	 * @return
	 */
	public String createFolder(Drive googleDriveService, String parentFolderId, String folderName) throws AppException {

		File fileMetadata = new File();
		fileMetadata.setName(folderName);
		fileMetadata.setParents(Arrays.asList(parentFolderId));
		fileMetadata.setMimeType(DriveMineType.FOLDER.getMinetype());

		try {
			File file = googleDriveService.files().create(fileMetadata)
					.setFields(this.createFileField(DriveFileField.ID))
					.execute();

			return file.getId();
		} catch (IOException e) {
			// 基本的にGoogleJsonResponseExceptionのみ発生する
			if (e instanceof GoogleJsonResponseException) {
				// GoogleJsonResponseExceptionにキャスト
				GoogleJsonResponseException googleJsonResponseException = (GoogleJsonResponseException) e;
				GoogleJsonError jsonErrors = googleJsonResponseException.getDetails();

				// レスポンスからエラーを判別する
				String createArgErrorMsg = createArgsErrorMsg(jsonErrors.getCode(), jsonErrors.getErrors().stream().findFirst().orElseThrow().getReason());
				MessageEnum messageEnum = DriveAPIError.getAppErrorMsgEnum(jsonErrors.getCode(), jsonErrors.getErrors().stream().findFirst().orElseThrow().getReason());

				if (messageEnum == null) {
					throw new RuntimeException(e);
				}
				throw new AppException(messageEnum, e, createArgErrorMsg);
			}
			throw new RuntimeException(e);
		}

	}

	/**
	 * ファイル情報を取得する
	 * 
	 * @param googleDriveService
	 * @param folderId
	 * @return
	 * @throws AppException
	 */
	public File getFolderInfo(Drive googleDriveService, String folderId) throws AppException {

		try {
			// フォルダ情報の取得処理
			File file = googleDriveService.files().get(folderId).setFields(DriveFileField.getAllStr()).execute();

			return file;
		} catch (IOException e) {
			// 基本的にGoogleJsonResponseExceptionのみ発生する
			if (e instanceof GoogleJsonResponseException) {
				// GoogleJsonResponseExceptionにキャスト
				GoogleJsonResponseException googleJsonResponseException = (GoogleJsonResponseException) e;
				GoogleJsonError jsonErrors = googleJsonResponseException.getDetails();

				// レスポンスからエラーを判別する
				String createArgErrorMsg = createArgsErrorMsg(jsonErrors.getCode(), jsonErrors.getErrors().stream().findFirst().orElseThrow().getReason());
				MessageEnum messageEnum = DriveAPIError.getAppErrorMsgEnum(jsonErrors.getCode(), jsonErrors.getErrors().stream().findFirst().orElseThrow().getReason());

				if (messageEnum == null) {
					throw new RuntimeException(e);
				}
				throw new AppException(messageEnum, e, createArgErrorMsg);
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * 指定したフォルダが存在するかどうか確認する。
	 * APIでエラーが発生した場合は、false
	 *
	 * @param googleDriveService
	 * @param folderName
	 * @param folderId NULL許容 指定したID内の検索
	 * @return
	 */
	public boolean existsFolder(Drive googleDriveService, String folderName, String folderId) {

		if (StringUtils.isEmpty(folderId)) {
			folderId = "root"; // 指定がない場合はルートフォルダから検索
		}

		String q = "mimeType = 'application/vnd.google-apps.folder' and '%s' in parents and name = '%s' and trashed=false";
		try {
			FileList fileList = googleDriveService.files().list().setQ(String.format(q, folderId, folderName)).execute();

			if (fileList.getFiles().isEmpty()) {
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Googleフォルダが削除されているかどうか。
	 * 
	 * @param googleDriveService
	 * @param folderId
	 * @return
	 * @throws AppException
	 */
	public boolean isFolderDeleted(Drive googleDriveService, String folderId) throws AppException {
		try {
			// フォルダ情報の取得処理
			File file = googleDriveService.files().get(folderId).setFields(this.createFileField(DriveFileField.TRASHED)).execute();

			// 削除されているかどうか
			return file.getTrashed();
		} catch (IOException e) {
			// 基本的にGoogleJsonResponseExceptionのみ発生する
			if (e instanceof GoogleJsonResponseException) {
				// GoogleJsonResponseExceptionにキャスト
				GoogleJsonResponseException googleJsonResponseException = (GoogleJsonResponseException) e;
				GoogleJsonError jsonErrors = googleJsonResponseException.getDetails();

				// レスポンスからエラーを判別する
				String createArgErrorMsg = createArgsErrorMsg(jsonErrors.getCode(), jsonErrors.getErrors().stream().findFirst().orElseThrow().getReason());
				MessageEnum messageEnum = DriveAPIError.getAppErrorMsgEnum(jsonErrors.getCode(), jsonErrors.getErrors().stream().findFirst().orElseThrow().getReason());

				if (messageEnum == null) {
					throw new RuntimeException(e);
				}

				if (messageEnum == MessageEnum.MSG_E00134) {
					// 共有されていない、完全削除済
					return true;
				}

				throw new AppException(messageEnum, e, createArgErrorMsg);
			}
			throw new RuntimeException(e);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * GoogleAPIで必要なTransport情報
	 * 
	 * @return
	 */
	private NetHttpTransport getTransport() {
		return new NetHttpTransport();
	}

	/**
	 * GoogleAPIで必要なJsonFactory情報
	 * 
	 * @return
	 */
	private GsonFactory getJsonFactory() {
		return new GsonFactory();
	}

	/**
	 * Googleクレデンシャル情報を作成する
	 * 
	 * @param accessToken
	 * @return
	 */
	private Credential getCredential(String accessToken) {
		Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod());
		credential.setAccessToken(accessToken);
		return credential;
	}

	/**
	 * リダイレクトURLの作成
	 * 
	 * @return
	 */
	private String createRedirectUrl() {
		String uri = ModelAndViewUtils.getRedirectPath(GoogleController.class, controller -> controller.googleAuthGatewayRedirect(null, null, null));
		uri = uri.replaceFirst(UrlConstant.SLASH, CommonConstant.BLANK);
		uri = uri.substring(0, uri.indexOf(UrlConstant.QUESTION));
		return uriService.createUrl("", uri);
	}

	/**
	 * エラーメッセージの最後に何のエラーが発生したかを判別できるように表示用エラーを作成する
	 * 
	 * @param errorName
	 * @param innerErrorName
	 * @param errorValue
	 * @return
	 */
	private String createArgsErrorMsg(int statusCode, String reason) throws AppException {

		String errorMessage = "【Error: statusCode: %s, cause : %s】";
		return String.format(errorMessage, statusCode, reason);
	}

	/**
	 * GoogleDriveApiのフィールドを作成
	 * 
	 * @param fields
	 * @return
	 */
	private String createFileField(DriveFileField... fields) {

		List<String> fieldList = Stream.of(fields).map(DriveFileField::getFieldName).collect(Collectors.toList());
		return StringUtils.list2Comma(fieldList);
	}

}
