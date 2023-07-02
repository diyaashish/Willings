package jp.loioz.app.common.api.dropbox.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.ServerException;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.oauth.DbxOAuthError;
import com.dropbox.core.oauth.DbxOAuthException;
import com.dropbox.core.oauth.DbxRefreshResult;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.auth.AuthError;
import com.dropbox.core.v2.files.CreateFolderError;
import com.dropbox.core.v2.files.CreateFolderErrorException;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.GetMetadataError;
import com.dropbox.core.v2.files.GetMetadataErrorException;
import com.dropbox.core.v2.files.LookupError;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteError;
import com.dropbox.core.v2.sharing.DbxUserSharingRequests;
import com.dropbox.core.v2.sharing.ShareFolderLaunch;
import com.dropbox.core.v2.sharing.SharedFolderAccessError;
import com.dropbox.core.v2.sharing.SharedFolderAccessErrorException;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.api.dropbox.constants.DropboxConstants;
import jp.loioz.app.common.api.dropbox.controller.DropboxController;
import jp.loioz.app.common.api.dropbox.form.DropboxAuthStateJson;
import jp.loioz.app.common.api.dropbox.form.DropboxConnectDto;
import jp.loioz.app.common.api.dropbox.form.DropboxCreateRootFolderResultDto;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.external.StorageConstants;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.HttpUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.UriService;

/**
 * DropboxAPI用サービスクラス<br>
 * 
 * <pre>
 * ※ 基本的にAPI呼び出ししかしないので、トランザクションアノテーションはつけない
 * （発生したExceptionをサービスクラスの呼び出し元で握りつぶすことを可能性を考慮しているため）
 * ただし、今後DBへの登録・更新をこのクラスで行う場合は、メソッド単位でトランザクションアノテーションをつけること
 * </pre>
 */
@Service
public class CommonDropboxApiService extends DefaultService {

	/** Dropbox アプリ名 */
	@Value("${dropbox.app-name}")
	private String appName;

	/** Dropbox アプリキー */
	@Value("${dropbox.app-key}")
	private String appKey;

	/** Dropbox シークレットキー */
	@Value("${dropbox.app-secret}")
	private String appSecret;

	@Autowired
	private UriService uriService;

	@Autowired
	private Logger logger;

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * DropboxAuthUrlを作成する
	 * 
	 * @param dbxAuthStateJson
	 * @return
	 */
	public String createAuthRequestUrl(DropboxAuthStateJson dbxAuthStateJson) {

		// リダイレクトURLを作成する
		String redirectUrl = this.createRedirectUrl();

		// Box認証サーバーへのリダイレクトURLを作成
		return String.format(DropboxConstants.AUTHORIZATION_BASE, appKey, redirectUrl, HttpUtils.base64UrlEncode(dbxAuthStateJson.getJsonStr()));
	}

	/**
	 * アクセストークンの取得処理を行う
	 * 
	 * @param code
	 * @return
	 * @throws AppException
	 */
	public DropboxConnectDto requestAccessToken(String code) throws AppException {

		DbxRequestConfig config = this.getDbxRequestConfig();
		DbxAppInfo appInfo = this.getDbxAppInfo();

		// Dropboxではcodeを取得する際のredirect_urlが必要なので取得しておく
		String redirectUrl = this.createRedirectUrl();

		// Auth認証用クラスを作成する
		DbxWebAuth webAuth = new DbxWebAuth(config, appInfo);

		try {
			// アクセストークンの取得リクエスト
			DbxAuthFinish authFin = webAuth.finishFromCode(code, redirectUrl);

			DropboxConnectDto connected = new DropboxConnectDto();
			connected.setAccessToken(authFin.getAccessToken());
			connected.setRefreshToken(authFin.getRefreshToken());
			return connected;
		} catch (DbxException e) {
			this.throwAppError(e);
			throw new RuntimeException("想定外のエラー", e);
		}
	}

	/**
	 * アクセストークンのリフレッシュ処理
	 * 
	 * @param accessToken
	 * @param refreshToken
	 * @return
	 * @throws AppException
	 */
	public DropboxConnectDto refreshToken(String accessToken, String refreshToken) throws AppException {

		DropboxConnectDto connectData = new DropboxConnectDto();

		try {
			// トークンが切れているかの確認を行う
			getDbxClientV2(accessToken).users().getCurrentAccount();

			// トークンが切れていないので、そのまま
			connectData.setRefreshed(false);
			connectData.setAccessToken(accessToken);
			connectData.setRefreshToken(refreshToken);
			return connectData;

		} catch (InvalidAccessTokenException e) {
			// トークンが切れていたらここに来る
			DbxRequestConfig config = this.getDbxRequestConfig();
			DbxCredential credential = new DbxCredential(accessToken, 60 * 60 * 1000L, refreshToken, appKey, appSecret);
			try {
				// リフレッシュ処理
				DbxRefreshResult refreshResult = credential.refresh(config);

				// Dropboxはリフレッシュトークンを使い回す（更新されない）らしい
				connectData.setRefreshed(true);
				connectData.setAccessToken(refreshResult.getAccessToken());
				connectData.setRefreshToken(refreshToken);
				return connectData;

			} catch (DbxOAuthException ex) {
				DbxOAuthError dbxOAuthError = ex.getDbxOAuthError();
				String argsErrorMsg = createArgsErrorMsg(dbxOAuthError.getClass().getSimpleName(), "", dbxOAuthError.getError());
				logger.warn("APIErrorが発生しました。", ex);
				throw new AppException(MessageEnum.MSG_E00119, ex, argsErrorMsg);
			} catch (DbxException ex) {
				this.throwAppError(e);
				throw new RuntimeException("想定外のエラー", e);

			}

		} catch (DbxException e) {
			this.throwAppError(e);
			throw new RuntimeException("想定外のエラー", e);
		}
	}

	/**
	 * トークンの無効化処理(AccessTokenとRefreshTokenの両方がrevokeされる)
	 * 
	 * @param accessToken
	 * @throws AppException
	 */
	public void revokeToken(String accessToken) throws AppException {

		DbxClientV2 cliant = this.getDbxClientV2(accessToken);
		try {
			// トークンの無効化
			cliant.auth().tokenRevoke();

		} catch (DbxException e) {
			this.throwAppError(e);
			throw new RuntimeException("想定外のエラー", e);
		}
	}

	/**
	 * DbxClientV2をアクセストークンから取得する
	 * 
	 * @param accessToken
	 * @return
	 */
	public DbxClientV2 getDbxClientV2(String accessToken) {

		DbxRequestConfig config = this.getDbxRequestConfig();
		return new DbxClientV2(config, accessToken);
	}

	/**
	 * Dropboxアプリにloiozルートフォルダを作成する
	 * 
	 * @param cliant
	 * @return
	 */
	public DropboxCreateRootFolderResultDto createRootFolder(DbxClientV2 cliant) throws AppException {

		DropboxCreateRootFolderResultDto resultDto = new DropboxCreateRootFolderResultDto();

		DbxUserFilesRequests filesRequests = cliant.files();
		DbxUserSharingRequests sharingRequests = cliant.sharing();

		CreateFolderResult createFolderResult = null;
		try {
			// folderの作成処理
			createFolderResult = filesRequests.createFolderV2("/" + StorageConstants.ROOT_FOLDER_NAME, false);
			resultDto.setFolderId(createFolderResult.getMetadata().getId());

		} catch (CreateFolderErrorException e) {
			CreateFolderError createFolderError = e.errorValue;
			WriteError writeError = e.errorValue.getPathValue();
			String argsMessage = createArgsErrorMsg(createFolderError.getClass().getSimpleName(), writeError.getClass().getSimpleName(), writeError.tag().name());
			if (writeError.isConflict()) {
				// フォルダ作成時にコンフリクト
				logger.warn("APIErrorが発生しました。", e);
				throw new AppException(MessageEnum.MSG_E00121, e, argsMessage);
			} else if (writeError.isNoWritePermission()) {
				// フォルダ作成の権限が無い場合
				logger.warn("APIErrorが発生しました。", e);
				throw new AppException(MessageEnum.MSG_E00122, e, argsMessage);
			} else if (writeError.isInsufficientSpace()) {
				// 作成に必要な容量が不足している場合
				logger.warn("APIErrorが発生しました。", e);
				throw new AppException(MessageEnum.MSG_E00123, e, argsMessage);
			} else if (writeError.isTooManyWriteOperations()) {
				// 書き込み系のAPIリクエストが多い場合
				logger.warn("APIErrorが発生しました。", e);
				throw new AppException(MessageEnum.MSG_E00125, e, argsMessage);
			} else {
				// 想定外のエラー
				logger.error("フォルダ作成時APIで想定外のエラーが発生しました。", e);
				throw new RuntimeException("想定外のエラー", e);
			}
		} catch (DbxException e) {
			this.throwAppError(e);
			throw new RuntimeException("想定外のエラー", e);
		}

		try {
			// 共有の設定を行う
			ShareFolderLaunch shareFolderLaunch = sharingRequests.shareFolder(createFolderResult.getMetadata().getPathLower());
			resultDto.setShareFolderId(shareFolderLaunch.getCompleteValue().getSharedFolderId());

		} catch (DbxException e) {
			this.throwAppError(e);
			throw new RuntimeException("想定外のエラー", e);
		}

		return resultDto;
	}

	/**
	 * フォルダの作成処理
	 * 
	 * @param cliant DropboxのユーザーAPI
	 * @param parentFolderId 当フォルダ直下にフォルダを作成する
	 * @param createFolderName 作成するフォルダ名
	 * @return 作成したフォルダのフォルダID
	 */
	public String createFolder(DbxClientV2 cliant, String parentFolderId, String createFolderName) throws AppException {

		DbxUserFilesRequests filesRequests = cliant.files();
		Metadata parentFolderMetaData = null;
		try {
			// 親フォルダ情報を取得する
			parentFolderMetaData = filesRequests.getMetadata(parentFolderId);

		} catch (DbxException e) {
			this.throwAppError(e);
			throw new RuntimeException("想定外のエラー", e);
		}

		// 親フォルダのpathを取得する
		String parentPath = parentFolderMetaData.getPathLower();

		CreateFolderResult createFolderResult = null;
		try {
			// 親フォルダ直下にフォルダを作成する
			createFolderResult = filesRequests.createFolderV2(parentPath + "/" + createFolderName, true);

		} catch (CreateFolderErrorException e) {
			CreateFolderError createFolderError = e.errorValue;
			WriteError writeError = e.errorValue.getPathValue();
			String argsMessage = createArgsErrorMsg(createFolderError.getClass().getSimpleName(), writeError.getClass().getSimpleName(), writeError.toStringMultiline());
			if (writeError.isConflict()) {
				// フォルダ作成時にコンフリクト
				logger.warn("APIErrorが発生しました。", e);
				throw new AppException(MessageEnum.MSG_E00121, e, argsMessage);
			} else if (writeError.isNoWritePermission()) {
				// フォルダ作成の権限が無い場合
				logger.warn("APIErrorが発生しました。", e);
				throw new AppException(MessageEnum.MSG_E00122, e, argsMessage);
			} else if (writeError.isInsufficientSpace()) {
				// 作成に必要な容量が不足している場合
				logger.warn("APIErrorが発生しました。", e);
				throw new AppException(MessageEnum.MSG_E00123, e, argsMessage);
			} else if (writeError.isTooManyWriteOperations()) {
				// 書き込み系のAPIリクエストが多い場合
				logger.warn("APIErrorが発生しました。", e);
				throw new AppException(MessageEnum.MSG_E00125, e, argsMessage);
			} else {
				// 想定外のエラー
				logger.error("フォルダ作成時APIで想定外のエラーが発生しました。", e);
				throw new RuntimeException("想定外のエラー", e);
			}

		} catch (DbxException e) {
			this.throwAppError(e);
			throw new RuntimeException("想定外のエラー", e);
		}

		// フォルダIDを返却する
		return createFolderResult.getMetadata().getId();
	}

	/**
	 * フォルダ情報の取得
	 * 
	 * @param cliant
	 * @param folderId
	 * @return
	 * @throws AppException
	 */
	public FolderMetadata getFolderMetaData(DbxClientV2 cliant, String folderId) throws AppException {
		return getFolderMetaData(cliant, folderId, null);
	}

	/**
	 * フォルダ情報の取得
	 * 
	 * @param cliant
	 * @param folderId
	 * @param shareFolderId
	 * @return
	 * @throws AppException
	 */
	public FolderMetadata getFolderMetaData(DbxClientV2 cliant, String folderId, String shareFolderId) throws AppException {

		DbxUserFilesRequests filesRequests = cliant.files();
		FolderMetadata folderMetaData = null;

		if (!StringUtils.isEmpty(shareFolderId)) {
			try {
				// シェアフォルダ情報を取得する
				cliant.sharing().getFolderMetadata(shareFolderId);
			} catch (SharedFolderAccessErrorException e) {
				SharedFolderAccessError sharedFolderAccessError = e.errorValue;
				String argsErrorMsg = createArgsErrorMsg(sharedFolderAccessError.getClass().getSimpleName(), "", sharedFolderAccessError.toString());
				// 何かしらのエラーが起きた場合は共有がされていない
				throw new AppException(MessageEnum.MSG_E00120, e, argsErrorMsg);
			} catch (DbxException e) {
				this.throwAppError(e);
				throw new RuntimeException("想定外のエラー", e);
			}
		}

		try {
			// folderのmetadataを取得する
			folderMetaData = (FolderMetadata) filesRequests.getMetadata(folderId);
			return folderMetaData;
		} catch (GetMetadataErrorException e) {
			GetMetadataError getMetadataError = e.errorValue;
			LookupError lookupError = e.errorValue.getPathValue();
			String argsMsg = this.createArgsErrorMsg(getMetadataError.getClass().getSimpleName(), lookupError.getClass().getSimpleName(), lookupError.tag().name());
			if (lookupError.isNotFound()) {
				// フォルダが見つからない場合
				throw new AppException(MessageEnum.MSG_E00124, e, argsMsg);
			}
			logger.error("想定外のエラーが発生しました。", e);
			throw new RuntimeException("想定外のエラー", e);
		} catch (DbxException e) {
			this.throwAppError(e);
			throw new RuntimeException("想定外のエラー", e);
		}

	}

	/**
	 * 指定したフォルダが存在するかどうか確認する。
	 * APIでエラーが発生した場合は、false
	 * 
	 * @param cliant
	 * @param folderPath
	 * @return
	 */
	public boolean existsFolder(DbxClientV2 cliant, String folderPath) {
		boolean existsFolder = false;
		try {
			cliant.files().listFolder(folderPath);
			existsFolder = true;
		} catch (DbxException e) {
			existsFolder = false;
		}
		return existsFolder;
	}

	/**
	 * フォルダーが共有されているか確認する
	 * 
	 * @param cliant
	 * @param shareFolderId
	 * @return
	 * @throws AppException
	 */
	public boolean isShared(DbxClientV2 cliant, String shareFolderId) throws AppException {

		try {
			// シェアフォルダ情報を取得する
			cliant.sharing().getFolderMetadata(shareFolderId);

			return true;
		} catch (SharedFolderAccessErrorException e) {

			// 何かしらのエラーが起きた場合は共有がされていない
			return false;
		} catch (DbxException e) {
			this.throwAppError(e);
			throw new RuntimeException("想定外のエラー", e);
		}

	}

	/**
	 * フォルダが存在するか確認します。(共有されていない場合も含む)
	 * 
	 * @param cliant
	 * @param folderId
	 * @return
	 * @throws AppException
	 */
	public boolean isNotFoundFolder(DbxClientV2 cliant, String folderId) throws AppException {

		DbxUserFilesRequests filesRequests = cliant.files();
		Metadata folderMetaData = null;

		try {
			// folderのmetadataを取得する
			folderMetaData = filesRequests.getMetadata(folderId);
			folderMetaData.getPathLower();

			return false;
		} catch (GetMetadataErrorException e) {
			LookupError error = e.errorValue.getPathValue();
			if (error.isNotFound()) {
				return true;
			}
			logger.error("想定外のエラーが発生しました。", e);
			throw new RuntimeException("想定外のエラー", e);
		} catch (DbxException e) {
			this.throwAppError(e);
			throw new RuntimeException("想定外のエラー", e);
		}
	}

	/**
	 * Dropbox側の共通エラーを投げる
	 * 
	 * @param dbxException
	 * @throws AppException
	 */
	public void throwAppError(DbxException dbxException) throws AppException {

		if (dbxException instanceof ServerException) {
			// ServerExceptionはHTTP 500コード
			ServerException serverException = (ServerException) dbxException;
			logger.warn("Dropboxサーバーエラーが発生しています。", serverException);
			throw new AppException(MessageEnum.MSG_E00126, serverException, serverException.getMessage());
		}

		if (dbxException instanceof InvalidAccessTokenException) {
			// アクセストークン周りのエラーの場合
			InvalidAccessTokenException invalidAccessTokenException = (InvalidAccessTokenException) dbxException;
			AuthError authError = invalidAccessTokenException.getAuthError();
			String argsMsg = this.createArgsErrorMsg(authError.getClass().getSimpleName(), "", authError.toStringMultiline());
			logger.warn("Dropboxのアクセストークンエラーが発生しています。", invalidAccessTokenException);
			throw new AppException(MessageEnum.MSG_E00119, invalidAccessTokenException, argsMsg);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * DbxRequestConfigを取得する
	 * 
	 * @return
	 */
	private DbxRequestConfig getDbxRequestConfig() {
		return new DbxRequestConfig(appName);
	}

	/**
	 * DbxAppInfoを取得する
	 * 
	 * @return
	 */
	private DbxAppInfo getDbxAppInfo() {
		return new DbxAppInfo(appKey, appSecret);
	}

	/**
	 * Dropbox用のリダイレクトURLを作成する
	 * 
	 * @return
	 */
	private String createRedirectUrl() {

		String uri = ModelAndViewUtils.getRedirectPath(DropboxController.class, controller -> controller.dropboxAuthGatewayRedirect(null, null, null));
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
	private String createArgsErrorMsg(String errorName, String innerErrorName, String errorValue) {

		String errorArgsMsg = "【Error : %s%s by %s】";

		return String.format(errorArgsMsg, errorName, StringUtils.isEmpty(innerErrorName) ? "" : "." + innerErrorName, errorValue);
	}

}
