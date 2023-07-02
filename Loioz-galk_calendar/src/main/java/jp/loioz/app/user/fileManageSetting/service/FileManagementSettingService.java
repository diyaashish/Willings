package jp.loioz.app.user.fileManageSetting.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxTrash;
import com.box.sdk.BoxUser;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.sharing.AccessLevel;
import com.dropbox.core.v2.sharing.DbxUserSharingRequests;
import com.dropbox.core.v2.sharing.SharedFolderMembers;
import com.dropbox.core.v2.sharing.UserMembershipInfo;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.User;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.api.box.service.CommonBoxApiService;
import jp.loioz.app.common.api.dropbox.form.DropboxConnectDto;
import jp.loioz.app.common.api.dropbox.form.DropboxCreateRootFolderResultDto;
import jp.loioz.app.common.api.dropbox.service.CommonDropboxApiService;
import jp.loioz.app.common.api.google.form.GoogleConnectDto;
import jp.loioz.app.common.api.google.service.CommonGoogleApiService;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.app.common.service.CommonPlanService;
import jp.loioz.app.user.fileManageSetting.form.FileManagementSettingViewForm;
import jp.loioz.app.user.fileManageSetting.form.RootFolderInfoDto;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.CommonConstant.ExternalServiceType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.external.StorageConstants;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TConnectedExternalServiceDao;
import jp.loioz.dao.TRootFolderBoxDao;
import jp.loioz.dao.TRootFolderDropboxDao;
import jp.loioz.dao.TRootFolderGoogleDao;
import jp.loioz.entity.TAuthTokenEntity;
import jp.loioz.entity.TConnectedExternalServiceEntity;
import jp.loioz.entity.TRootFolderBoxEntity;
import jp.loioz.entity.TRootFolderDropboxEntity;
import jp.loioz.entity.TRootFolderGoogleEntity;

/**
 * ファイル管理設定画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FileManagementSettingService extends DefaultService {

	@Autowired
	private CommonPlanService commonPlanService;

	@Autowired
	private CommonOAuthService commonOAuthService;

	@Autowired
	private CommonGoogleApiService commonGoogleApiService;

	@Autowired
	private CommonBoxApiService commonBoxApiService;

	@Autowired
	private CommonDropboxApiService commonDropboxApiService;

	@Autowired
	private TConnectedExternalServiceDao tConnectedExternalServiceDao;

	@Autowired
	private TRootFolderGoogleDao tRootFolderGoogleDao;

	@Autowired
	private TRootFolderBoxDao tRootFolderBoxDao;

	@Autowired
	private TRootFolderDropboxDao tRootFolderDropboxDao;

	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 連携中の外部ストレージサービス(Enum)を取得する。
	 * 
	 * @return 未連携の場合はnull
	 */
	public ExternalService getConnectStorageService() {
		TConnectedExternalServiceEntity tConnectedExternalServiceEntity = tConnectedExternalServiceDao.selectStorageConnectedService();
		if (tConnectedExternalServiceEntity == null) {
			return null;
		}
		return ExternalService.of(tConnectedExternalServiceEntity.getExternalServiceId());
	}

	/**
	 * ファイル管理設定画面表示を作成する
	 * 
	 * @return
	 */
	public FileManagementSettingViewForm createViewForm() {

		FileManagementSettingViewForm viewForm = new FileManagementSettingViewForm();
		return viewForm;
	}

	/**
	 * 外部連携選択用フォームを作成する
	 * 
	 * @return
	 */
	public FileManagementSettingViewForm.ExternalStorageSelectViewForm createExternalStorageSelectViewForm() {

		FileManagementSettingViewForm.ExternalStorageSelectViewForm externalStorageSelectViewForm = new FileManagementSettingViewForm.ExternalStorageSelectViewForm();

		ExternalService externalService = getConnectStorageService();
		if (externalService != null) {
			externalStorageSelectViewForm.setStorageType(externalService.getCd());
		}

		// loiozのファイルストレージが無料の状態かどうか
		boolean nowLoiozStorageIsFree = commonPlanService.nowIsFreeStorage(SessionUtils.getTenantSeq());
		externalStorageSelectViewForm.setNowLoiozStorageIsFree(nowLoiozStorageIsFree);

		return externalStorageSelectViewForm;
	}

	/**
	 * viewFomにAPI関連のデータを設定する
	 * 
	 * @param externalService
	 * @param viewForm
	 */
	public void setAPIData(ExternalService externalService, FileManagementSettingViewForm.ExternalStorageSelectViewForm externalStorageSelectViewForm) throws AppException {

		RootFolderInfoDto rootFolderInfo = null;
		switch (externalService) {
		case GOOGLE:
			rootFolderInfo = this.getRootFolderGoogleDto();
			break;
		case BOX:
			rootFolderInfo = this.getRootFolderBoxDto();
			break;
		case DROPBOX:
			rootFolderInfo = this.getRootFolderDropboxDto();
			break;
		default:
			break;
		}

		externalStorageSelectViewForm.setRootFolderInfo(rootFolderInfo);
	}

	/**
	 * Google連携処理（初期連携）
	 * 
	 * @param code
	 * @throws AppException
	 */
	public void googleAuthConnect(String code) throws AppException {

		if (!commonPlanService.nowIsFreeStorage(SessionUtils.getTenantSeq())) {
			// 有料プランの場合、連携時のプリチェックで確認しているのでここではエラーにする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		String rootFolderId = "";

		// Googleトークンの取得処理
		GoogleConnectDto resultDto = commonGoogleApiService.requestAccessToken(code);

		// GoogleDriveのAPIクラスを作成
		Drive googleDriveService = commonGoogleApiService.getDriveService(resultDto.getAccessToken());

		try {

			boolean existsFolder = commonGoogleApiService.existsFolder(googleDriveService, StorageConstants.ROOT_FOLDER_NAME, null);
			if (existsFolder) {
				// 再作成する際に同名フォルダがあった場合
				throw new AppException(MessageEnum.MSG_E00137, null, StorageConstants.ROOT_FOLDER_NAME, "再作成");
			}

			// APIによるフォルダ作成
			rootFolderId = commonGoogleApiService.createRootFolder(googleDriveService);

		} catch (Exception e) {
			try {
				if (!StringUtils.isEmpty(rootFolderId)) {
					// 削除処理
					googleDriveService.files().delete(rootFolderId).execute();
				}
				// トークンを無効化する
				commonGoogleApiService.revokeToken(resultDto.getRefreshToken());
			} catch (Exception ex) {
				// ここでエラーが発生しても何もしない。
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}
			throw e;
		}

		// ここまできたらAPIはすべて正常に動作したことを示す
		try {
			// トークン情報の登録処理
			TAuthTokenEntity insertAuthTokenEntity = new TAuthTokenEntity();
			insertAuthTokenEntity.setAccountSeq(SessionUtils.getLoginAccountSeq());
			insertAuthTokenEntity.setAccessToken(resultDto.getAccessToken());
			insertAuthTokenEntity.setRefreshToken(resultDto.getRefreshToken());
			insertAuthTokenEntity.setExternalServiceId(ExternalService.GOOGLE.getCd());
			commonOAuthService.insert(insertAuthTokenEntity);

			// 接続情報を保存
			insertConnectExternalStorage(ExternalService.GOOGLE);

			// ルートフォルダ情報の登録
			TRootFolderGoogleEntity rootGoogleFolder = new TRootFolderGoogleEntity();
			rootGoogleFolder.setFolderId(rootFolderId);
			tRootFolderGoogleDao.insert(rootGoogleFolder);

		} catch (Exception e) {
			// 手動でトークンを失効させ、作成されたフォルダを削除する。
			try {
				// 削除処理
				googleDriveService.files().delete(rootFolderId).execute();
				// トークンを無効化する
				commonGoogleApiService.revokeToken(resultDto.getRefreshToken());
			} catch (Exception ex) {
				// ここでエラーが発生しても何もしない。
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}
			throw e;
		}

	}

	/**
	 * Box連携処理（初期連携）
	 * 
	 * @param externalService
	 * @throws AppException
	 */
	public void boxAuthConnect(String code) throws AppException {

		if (!commonPlanService.nowIsFreeStorage(SessionUtils.getTenantSeq())) {
			// 有料プランの場合、連携時のプリチェックで確認しているのでここではエラーにする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		BoxAPIConnection api = null;
		String rootFolderId = "";

		// 必要なAPIはすべてここで行う。
		try {
			// 接続処理 ※このときにアクセストークンが発行される
			api = commonBoxApiService.requestAccessToken(code);

			// ロイオズルートフォルダを作成
			rootFolderId = commonBoxApiService.createRootFolder(api, false);

		} catch (Exception e) {
			// 手動でトークンを失効させ、作成されたフォルダを削除する。
			try {
				if (!StringUtils.isEmpty(rootFolderId)) {
					// フォルダの削除処理は、共通処理では行わない
					BoxFolder boxFolder = new BoxFolder(api, rootFolderId);
					boxFolder.delete(true);

					// ゴミ箱からも完全に削除
					BoxTrash trash = new BoxTrash(api);
					trash.deleteFolder(rootFolderId);
				}
				if (api != null) {
					// 作成したトークンのリフレッシュトークンを無効化する
					commonBoxApiService.revokeRefreshToken(api.getRefreshToken());
				}
			} catch (Exception ex) {
				// ここで発生したエラーは対処出来ないので握りつぶす
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}

			throw e;
		}

		// ここまできたらAPIはすべて正常に動作したことを示す
		try {
			// トークン情報の登録処理
			TAuthTokenEntity insertAuthTokenEntity = new TAuthTokenEntity();
			insertAuthTokenEntity.setAccountSeq(SessionUtils.getLoginAccountSeq());
			insertAuthTokenEntity.setAccessToken(api.getAccessToken());
			insertAuthTokenEntity.setRefreshToken(api.getRefreshToken());
			insertAuthTokenEntity.setExternalServiceId(ExternalService.BOX.getCd());
			commonOAuthService.insert(insertAuthTokenEntity);

			// 接続情報を保存
			insertConnectExternalStorage(ExternalService.BOX);

			// ルートフォルダ情報の登録
			TRootFolderBoxEntity rootBoxFolder = new TRootFolderBoxEntity();
			rootBoxFolder.setFolderId(rootFolderId);
			tRootFolderBoxDao.insert(rootBoxFolder);

		} catch (Exception e) {
			// 手動でトークンを失効させ、作成されたフォルダを削除する。
			try {

				// フォルダの削除処理は、共通処理では行わない
				BoxFolder boxFolder = new BoxFolder(api, rootFolderId);
				boxFolder.delete(true);

				// ゴミ箱からも完全に削除
				BoxTrash trash = new BoxTrash(api);
				trash.deleteFolder(rootFolderId);

				// 作成したトークンのリフレッシュトークンを無効化する
				commonBoxApiService.revokeRefreshToken(api.getRefreshToken());

			} catch (Exception ex) {
				// ここで発生したエラーは対処出来ないので握りつぶす
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}
			throw e;
		}

	}

	/**
	 * Dropbox連携処理（初期連携）
	 * 
	 * @param externalService
	 * @throws AppException
	 */
	public void dropboxAuthConnect(String code) throws AppException {

		if (!commonPlanService.nowIsFreeStorage(SessionUtils.getTenantSeq())) {
			// 有料プランの場合、連携時のプリチェックで確認しているのでここではエラーにする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		DropboxCreateRootFolderResultDto resultDto;
		String rootFolderId = "";

		// アクセストークンの発行
		DropboxConnectDto dropboxConnectDto = commonDropboxApiService.requestAccessToken(code);
		DbxClientV2 client = commonDropboxApiService.getDbxClientV2(dropboxConnectDto.getAccessToken());

		// APIリクエスト
		try {

			boolean existsFolder = commonDropboxApiService.existsFolder(client, "/" + StorageConstants.ROOT_FOLDER_NAME);
			if (existsFolder) {
				// 再作成する際に同名フォルダがあった場合
				throw new AppException(MessageEnum.MSG_E00127, null, StorageConstants.ROOT_FOLDER_NAME, "再作成");
			}

			resultDto = commonDropboxApiService.createRootFolder(client);
			rootFolderId = resultDto.getFolderId();

		} catch (Exception e) {
			try {
				if (!StringUtils.isEmpty(rootFolderId)) {
					// 削除処理
					client.files().deleteV2(rootFolderId);
				}
				// トークンを削除
				client.auth().tokenRevoke();

			} catch (Exception ex) {
				// ここでエラーが発生しても何もしない。
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}
			throw e;
		}

		try {
			// トークン情報の登録処理
			TAuthTokenEntity insertAuthTokenEntity = new TAuthTokenEntity();
			insertAuthTokenEntity.setAccountSeq(SessionUtils.getLoginAccountSeq());
			insertAuthTokenEntity.setAccessToken(dropboxConnectDto.getAccessToken());
			insertAuthTokenEntity.setRefreshToken(dropboxConnectDto.getRefreshToken());
			insertAuthTokenEntity.setExternalServiceId(ExternalService.DROPBOX.getCd());
			commonOAuthService.insert(insertAuthTokenEntity);

			// 接続情報を保存
			insertConnectExternalStorage(ExternalService.DROPBOX);

			// ルートフォルダ情報の登録
			TRootFolderDropboxEntity rootDropboxFolder = new TRootFolderDropboxEntity();
			rootDropboxFolder.setFolderId(rootFolderId);
			rootDropboxFolder.setSharedFolderId(resultDto.getShareFolderId());
			tRootFolderDropboxDao.insert(rootDropboxFolder);

		} catch (Exception e) {
			try {
				// 削除処理
				client.files().deleteV2(rootFolderId);
				// トークンを削除
				client.auth().tokenRevoke();

			} catch (DbxException ex) {
				// ここでエラーが発生してもなにもしない
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}
			throw e;
		}

	}

	/**
	 * 外部連携ストレージの連携解除処理
	 *
	 * @throws AppException
	 */
	public void disconnectExternalStorage() throws AppException {

		TConnectedExternalServiceEntity tConnectedExternalServiceEntity = tConnectedExternalServiceDao.selectStorageConnectedService();
		if (tConnectedExternalServiceEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		ExternalService externalService = ExternalService.of(tConnectedExternalServiceEntity.getExternalServiceId());
		switch (externalService) {
		case GOOGLE:
			this.disconnectGoogleService();
			break;
		case BOX:
			this.disconnectBoxService();
			break;
		case DROPBOX:
			this.disconnectDropboxService();
			break;
		default:
			throw new RuntimeException("想定外の値");
		}

		// 接続サービス情報を削除
		tConnectedExternalServiceDao.delete(tConnectedExternalServiceEntity);

	}

	/**
	 * 外部ストレージのルートフォルダを再作成する
	 * 
	 * @param externalStorageService
	 * @throws AppException
	 */
	public void reCreateRootFolder(ExternalService externalStorageService) throws AppException {

		switch (externalStorageService) {
		case GOOGLE:
			this.reCreateGoogleRootFoloder();
			break;
		case BOX:
			this.reCreateBoxRootFoloder();
			break;
		case DROPBOX:
			this.reCreateDropboxRootFoloder();
			break;
		default:
			throw new RuntimeException("想定外の値");
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 外部サービス接続の登録処理
	 * 
	 * @param externalService
	 * @throws AppException
	 */
	private void insertConnectExternalStorage(ExternalService externalService) throws AppException {

		TConnectedExternalServiceEntity tConnectedExternalServiceEntity = tConnectedExternalServiceDao.selectStorageConnectedService();
		if (tConnectedExternalServiceEntity != null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TConnectedExternalServiceEntity insertTConnectedExternalServiceEntity = new TConnectedExternalServiceEntity();
		insertTConnectedExternalServiceEntity.setServiceType(ExternalServiceType.STORAGE.getCd());
		insertTConnectedExternalServiceEntity.setExternalServiceId(externalService.getCd());

		tConnectedExternalServiceDao.insert(insertTConnectedExternalServiceEntity);
	}

	/**
	 * Googleのloiozルートフォルダ情報を取得する
	 * 
	 * @return
	 * @throws AppException
	 */
	private RootFolderInfoDto getRootFolderGoogleDto() throws AppException {

		// DBからToken情報を取得
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.GOOGLE);
		TRootFolderGoogleEntity tRootFolderGoogleEntity = tRootFolderGoogleDao.selectEnabledRoot();

		// GoogleDrive APIの実行
		Drive googleDriveService = commonGoogleApiService.getDriveService(tAuthTokenEntity.getAccessToken());
		File folderInfo = commonGoogleApiService.getFolderInfo(googleDriveService, tRootFolderGoogleEntity.getFolderId());

		// オーナー情報の取得
		User owner = folderInfo.getOwners().stream().findFirst().orElse(null);
		Date date = new Date(folderInfo.getCreatedTime().getValue());
		LocalDate createdAt = DateUtils.parseDateToLocalDate(date);

		RootFolderInfoDto rootFolderDto = new RootFolderInfoDto();
		rootFolderDto.setFolderName(folderInfo.getName());
		rootFolderDto.setOwnerName(owner.getDisplayName());
		rootFolderDto.setOwnerServiceAccountId(owner.getEmailAddress());
		rootFolderDto.setCreatedAt(DateUtils.parseToString(createdAt, DateUtils.DATE_JP_YYYY_M_D));
		return rootFolderDto;

	}

	/**
	 * Boxのloiozルートフォルダ情報を取得する
	 * 
	 * @return
	 * @throws AppException
	 */
	private RootFolderInfoDto getRootFolderBoxDto() throws AppException {

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.BOX);
		TRootFolderBoxEntity tRootFolderBoxEntity = tRootFolderBoxDao.selectEnabledRoot();
		BoxAPIConnection api = commonBoxApiService.getBoxAPIConnection(tAuthTokenEntity.getAccessToken(), tAuthTokenEntity.getRefreshToken());
		BoxFolder.Info folderInfo = commonBoxApiService.getFolderInfo(api, tRootFolderBoxEntity.getFolderId());
		BoxUser.Info ownerInfo = folderInfo.getOwnedBy();

		LocalDate createdAt = DateUtils.parseDateToLocalDate(folderInfo.getCreatedAt());

		RootFolderInfoDto rootFolderDto = new RootFolderInfoDto();
		rootFolderDto.setFolderName(folderInfo.getName());
		rootFolderDto.setOwnerName(ownerInfo.getName());
		rootFolderDto.setOwnerServiceAccountId(ownerInfo.getLogin());
		rootFolderDto.setCreatedAt(DateUtils.parseToString(createdAt, DateUtils.DATE_JP_YYYY_M_D));
		return rootFolderDto;

	}

	/**
	 * ルートフォルダの再作成処理
	 * ※ GoogleApp側のフォルダを削除はしない
	 * 
	 * @throws AppException
	 */
	private void reCreateGoogleRootFoloder() throws AppException {

		String rootFolderId = "";

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.GOOGLE);

		// GoogleDriveのAPIクラスを作成
		Drive googleDriveService = commonGoogleApiService.getDriveService(tAuthTokenEntity.getAccessToken());

		try {

			boolean existsFolder = commonGoogleApiService.existsFolder(googleDriveService, StorageConstants.ROOT_FOLDER_NAME, null);
			if (existsFolder) {
				// 再作成する際に同名フォルダがあった場合
				throw new AppException(MessageEnum.MSG_E00137, null, StorageConstants.ROOT_FOLDER_NAME, "再作成");
			}

			// APIによるフォルダ作成
			rootFolderId = commonGoogleApiService.createRootFolder(googleDriveService);

		} catch (Exception e) {
			try {
				if (!StringUtils.isEmpty(rootFolderId)) {
					// 削除処理
					googleDriveService.files().delete(rootFolderId).execute();
				}
			} catch (Exception ex) {
				// ここでエラーが発生しても何もしない。
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}
			throw e;
		}

		// ここまできたらAPIはすべて正常に動作したことを示す
		try {
			// 現在利用されているルートフォルダは削除済にする
			TRootFolderGoogleEntity tRootFolderGoogleEntity = tRootFolderGoogleDao.selectEnabledRoot();
			tRootFolderGoogleEntity.setFlgDelete();
			tRootFolderGoogleDao.update(tRootFolderGoogleEntity);

			// ルートフォルダ情報の登録
			TRootFolderGoogleEntity rootGoogleFolder = new TRootFolderGoogleEntity();
			rootGoogleFolder.setFolderId(rootFolderId);
			tRootFolderGoogleDao.insert(rootGoogleFolder);

		} catch (Exception e) {
			// 手動でトークンを失効させ、作成されたフォルダを削除する。
			try {
				// 削除処理
				googleDriveService.files().delete(rootFolderId).execute();
			} catch (Exception ex) {
				// ここでエラーが発生しても何もしない。
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}
			throw e;
		}

	}

	/**
	 * ルートフォルダの再作成処理
	 * ※ BoxApp側のフォルダを削除はしない
	 * 
	 * @throws AppException
	 */
	private void reCreateBoxRootFoloder() throws AppException {

		BoxAPIConnection api = null;
		String newRootFolderId = "";
		try {
			TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.BOX);
			api = commonBoxApiService.getBoxAPIConnection(tAuthTokenEntity.getAccessToken(), tAuthTokenEntity.getRefreshToken());

			// 新しくRoouFoloderを作成
			newRootFolderId = commonBoxApiService.createRootFolder(api, true);

		} catch (Exception e) {
			// 手動でトークンを失効させ、作成されたフォルダを削除する。
			try {
				if (!StringUtils.isEmpty(newRootFolderId)) {
					// フォルダの削除処理は、共通処理では行わない
					BoxFolder boxFolder = new BoxFolder(api, newRootFolderId);
					boxFolder.delete(true);

					// ゴミ箱からも完全に削除
					BoxTrash trash = new BoxTrash(api);
					trash.deleteFolder(newRootFolderId);
				}
			} catch (Exception ex) {
				// ここで発生したエラーは対処出来ないので握りつぶす
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}

			throw e;
		}

		// ここまできたらAPIはすべて正常に動作したことを示す
		try {
			// 現在利用されているルートフォルダは削除済にする
			TRootFolderBoxEntity tRootFolderBoxEntity = tRootFolderBoxDao.selectEnabledRoot();
			tRootFolderBoxEntity.setFlgDelete();
			tRootFolderBoxDao.update(tRootFolderBoxEntity);

			// ルートフォルダ情報の登録
			TRootFolderBoxEntity newRootFoloder = new TRootFolderBoxEntity();
			newRootFoloder.setFolderId(newRootFolderId);
			tRootFolderBoxDao.insert(newRootFoloder);

		} catch (Exception e) {
			// 手動でトークンを失効させ、作成されたフォルダを削除する。
			try {
				// フォルダの削除処理は、共通処理では行わない
				BoxFolder boxFolder = new BoxFolder(api, newRootFolderId);
				boxFolder.delete(true);

				// ゴミ箱からも完全に削除
				BoxTrash trash = new BoxTrash(api);
				trash.deleteFolder(newRootFolderId);

			} catch (Exception ex) {
				// ここで発生したエラーは対処出来ないので握りつぶす
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}
			throw e;
		}

	}

	/**
	 * Dropboxフォルダの再作成処理
	 * 
	 * @throws AppException
	 */
	private void reCreateDropboxRootFoloder() throws AppException {

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.DROPBOX);
		DbxClientV2 cliantApi = commonDropboxApiService.getDbxClientV2(tAuthTokenEntity.getAccessToken());

		DropboxCreateRootFolderResultDto resultDto = null;

		// APIリクエスト
		try {

			boolean existsFolder = commonDropboxApiService.existsFolder(cliantApi, "/" + StorageConstants.ROOT_FOLDER_NAME);
			if (existsFolder) {
				// 再作成する際に同名フォルダがあった場合
				throw new AppException(MessageEnum.MSG_E00127, null, StorageConstants.ROOT_FOLDER_NAME, "再作成");
			}

			resultDto = commonDropboxApiService.createRootFolder(cliantApi);

		} catch (Exception e) {
			try {
				if (resultDto != null && !StringUtils.isEmpty(resultDto.getFolderId())) {
					// 削除処理
					cliantApi.files().deleteV2(resultDto.getFolderId());
				}

			} catch (Exception ex) {
				// ここでエラーが発生しても何もしない。
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}
			throw e;
		}

		try {
			TRootFolderDropboxEntity tRootFolderDropboxEntity = tRootFolderDropboxDao.selectEnabledRoot();
			tRootFolderDropboxEntity.setFlgDelete();
			tRootFolderDropboxDao.update(tRootFolderDropboxEntity);

			// ルートフォルダ情報の登録
			TRootFolderDropboxEntity rootDropboxFolder = new TRootFolderDropboxEntity();
			rootDropboxFolder.setFolderId(resultDto.getFolderId());
			rootDropboxFolder.setSharedFolderId(resultDto.getShareFolderId());
			tRootFolderDropboxDao.insert(rootDropboxFolder);

		} catch (Exception e) {
			try {
				// 削除処理
				cliantApi.files().deleteV2(resultDto.getFolderId());

			} catch (Exception ex) {
				// ここでエラーが発生してもなにもしない
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}
			throw e;
		}

	}

	/**
	 * Dropboxのloiozルートフォルダ情報を取得する
	 * 
	 * @return
	 * @throws AppException
	 */
	private RootFolderInfoDto getRootFolderDropboxDto() throws AppException {

		try {
			// DBからトークンとフォルダ情報を取得
			TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.DROPBOX);
			TRootFolderDropboxEntity tRootFolderDropboxEntity = tRootFolderDropboxDao.selectEnabledRoot();

			// APIを準備
			DbxClientV2 userApi = commonDropboxApiService.getDbxClientV2(tAuthTokenEntity.getAccessToken());
			DbxUserSharingRequests sharingRequest = userApi.sharing();

			// フォルダ情報を取得する
			FolderMetadata folderInfo = commonDropboxApiService.getFolderMetaData(userApi, tRootFolderDropboxEntity.getFolderId(), tRootFolderDropboxEntity.getSharedFolderId());

			// シェア情報を取得する (オーナー情報はここからでないと取得できない)
			String shareFolderId = folderInfo.getSharedFolderId();
			SharedFolderMembers shareMemberMetaData = sharingRequest.listFolderMembers(shareFolderId);

			// フォルダのシェア中のユーザー情報を取得し、オーナーのみ抽出
			List<UserMembershipInfo> memberList = shareMemberMetaData.getUsers();
			UserMembershipInfo owner = memberList.stream().filter(member -> AccessLevel.OWNER == member.getAccessType()).findFirst().get();

			RootFolderInfoDto rootFolderDto = new RootFolderInfoDto();
			rootFolderDto.setFolderName(folderInfo.getName());
			rootFolderDto.setOwnerName(owner.getUser().getDisplayName());
			rootFolderDto.setOwnerServiceAccountId(owner.getUser().getEmail());
			rootFolderDto.setCreatedAt(DateUtils.parseToString(tRootFolderDropboxEntity.getCreatedAt(), DateUtils.DATE_JP_YYYY_M_D));
			return rootFolderDto;

		} catch (DbxException e) {
			// 想定外のエラー
			commonDropboxApiService.throwAppError(e);
			throw new RuntimeException("想定外のエラー", e);
		}

	}

	/**
	 * Google連携関連の情報を削除する
	 */
	private void disconnectGoogleService() {
		TRootFolderGoogleEntity tRootFolderGoogleEntity = tRootFolderGoogleDao.selectEnabledRoot();
		tRootFolderGoogleEntity.setFlgDelete();
		tRootFolderGoogleDao.update(tRootFolderGoogleEntity);

		// Googleの他サービスを利用している場合は、削除すると利用できなくなってしまうので次回実装時に修正が必要かも
		List<TAuthTokenEntity> tAuthTokenEntities = commonOAuthService.getExternalServiceAuthToken(ExternalService.GOOGLE);
		commonOAuthService.delete(tAuthTokenEntities);
	}

	/**
	 * Box連携関連の情報を削除する
	 */
	private void disconnectBoxService() {

		TRootFolderBoxEntity tRootFolderBoxEntity = tRootFolderBoxDao.selectEnabledRoot();
		tRootFolderBoxEntity.setFlgDelete();
		tRootFolderBoxDao.update(tRootFolderBoxEntity);

		List<TAuthTokenEntity> tAuthTokenEntities = commonOAuthService.getExternalServiceAuthToken(ExternalService.BOX);
		commonOAuthService.delete(tAuthTokenEntities);
	}

	/**
	 * Dropbox連携関連の情報を削除する
	 */
	private void disconnectDropboxService() {

		TRootFolderDropboxEntity tRootFolderDropboxEntity = tRootFolderDropboxDao.selectEnabledRoot();
		tRootFolderDropboxEntity.setFlgDelete();
		tRootFolderDropboxDao.update(tRootFolderDropboxEntity);

		List<TAuthTokenEntity> tAuthTokenEntities = commonOAuthService.getExternalServiceAuthToken(ExternalService.DROPBOX);
		commonOAuthService.delete(tAuthTokenEntities);
	}

}
