package jp.loioz.app.common.api.dropbox.service;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FolderMetadata;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.api.dropbox.constants.DropboxConstants;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.external.StorageConstants;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TFolderDropboxDao;
import jp.loioz.dao.TRootFolderDropboxDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.PersonId;
import jp.loioz.entity.TAuthTokenEntity;
import jp.loioz.entity.TFolderDropboxEntity;
import jp.loioz.entity.TRootFolderDropboxEntity;

/**
 * Dropboxサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DropboxService extends DefaultService {

	@Autowired
	private TRootFolderDropboxDao tRootFolderDropboxDao;

	@Autowired
	private TFolderDropboxDao tFolderDropboxDao;

	@Autowired
	private CommonDropboxApiService commonDropboxApiService;

	@Autowired
	private CommonOAuthService commonOAuthService;

	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * Dropboxアプリのルートフォルダが存在するかどうか判断する
	 *
	 * @return
	 * @throws AppException
	 */
	public boolean isNotFoundRootFolder() throws AppException {

		TRootFolderDropboxEntity tRootFolderDropboxEntity = tRootFolderDropboxDao.selectEnabledRoot();

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.DROPBOX);
		DbxClientV2 cliantApi = commonDropboxApiService.getDbxClientV2(tAuthTokenEntity.getAccessToken());
		boolean isDeleted = commonDropboxApiService.isNotFoundFolder(cliantApi, tRootFolderDropboxEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * ルートフォルダがシェアされているかどうか
	 * 
	 * @return
	 * @throws AppException
	 */
	public boolean isSharedRootFolder() throws AppException {

		TRootFolderDropboxEntity tRootFolderDropboxEntity = tRootFolderDropboxDao.selectEnabledRoot();

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.DROPBOX);
		DbxClientV2 cliantApi = commonDropboxApiService.getDbxClientV2(tAuthTokenEntity.getAccessToken());
		boolean isShared = commonDropboxApiService.isShared(cliantApi, tRootFolderDropboxEntity.getSharedFolderId());
		return isShared;
	}

	/**
	 * Dropboxアプリの顧客ルートフォルダが削除済かどうか判断する
	 * 
	 * @param customerId
	 * @return
	 * @throws AppException
	 */
	public boolean isNotFoundCustomerRootFolder(Long customerId) throws AppException {

		TFolderDropboxEntity tFolderDropboxEntity = tFolderDropboxDao.selectEnabledByFindCustomerId(customerId);
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.DROPBOX);
		DbxClientV2 cliantApi = commonDropboxApiService.getDbxClientV2(tAuthTokenEntity.getAccessToken());
		boolean isDeleted = commonDropboxApiService.isNotFoundFolder(cliantApi, tFolderDropboxEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Dropboxアプリの顧客フォルダが削除済かどうか判断する
	 * 
	 * @param customerId
	 * @return
	 * @throws AppException
	 */
	public boolean isNotFoundCustomerFolder(Long customerId) throws AppException {

		TFolderDropboxEntity tFolderDropboxEntity = tFolderDropboxDao.selectEnabledByCustomerId(customerId);
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.DROPBOX);
		DbxClientV2 cliantApi = commonDropboxApiService.getDbxClientV2(tAuthTokenEntity.getAccessToken());
		boolean isDeleted = commonDropboxApiService.isNotFoundFolder(cliantApi, tFolderDropboxEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Dropboxアプリの案件ルートフォルダが削除済かどうか判断する
	 * 
	 * @param ankenId ※null許容
	 * @return
	 * @throws AppException
	 */
	public boolean isNotFoundAnkenRootFolder(Long ankenId) throws AppException {

		TFolderDropboxEntity tFolderDropboxEntity = tFolderDropboxDao.selectEnabledByFindAnkenId(ankenId);
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.DROPBOX);
		DbxClientV2 cliantApi = commonDropboxApiService.getDbxClientV2(tAuthTokenEntity.getAccessToken());
		boolean isDeleted = commonDropboxApiService.isNotFoundFolder(cliantApi, tFolderDropboxEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Dropboxアプリの案件フォルダが削除済かどうか判断する
	 * 
	 * @param ankenId ※null許容
	 * @return
	 * @throws AppException
	 */
	public boolean isNotFoundAnkenFolder(Long ankenId) throws AppException {

		TFolderDropboxEntity tFolderDropboxEntity = tFolderDropboxDao.selectEnabledByAnkenId(ankenId);
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.DROPBOX);
		DbxClientV2 cliantApi = commonDropboxApiService.getDbxClientV2(tAuthTokenEntity.getAccessToken());
		boolean isDeleted = commonDropboxApiService.isNotFoundFolder(cliantApi, tFolderDropboxEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * 案件ルートフォルダ情報がDBに登録されているか確認する
	 * 
	 * @param ankenId
	 * @return
	 */
	public boolean existsAnkenRootFoloderDB(Long ankenId) {
		TFolderDropboxEntity tFolderDropboxEntity = tFolderDropboxDao.selectEnabledByFindAnkenId(ankenId);
		if (tFolderDropboxEntity == null) {
			return false;
		}
		return true;
	}

	/**
	 * 案件フォルダ情報がDBに登録されているか確認する
	 * 
	 * @param ankenId
	 * @return
	 */
	public boolean existsAnkenFoloderDB(Long ankenId) {
		TFolderDropboxEntity tFolderDropboxEntity = tFolderDropboxDao.selectEnabledByAnkenId(ankenId);
		if (tFolderDropboxEntity == null) {
			return false;
		}
		return true;
	}

	/**
	 * 顧客ルートフォルダ情報がDBに登録されているか確認する
	 * 
	 * @param customerId
	 * @return
	 */
	public boolean existsCustomerRootFoloderDB(Long customerId) {
		TFolderDropboxEntity tFolderDropboxEntity = tFolderDropboxDao.selectEnabledByFindCustomerId(customerId);
		if (tFolderDropboxEntity == null) {
			return false;
		}
		return true;
	}

	/**
	 * 顧客フォルダ情報がDBに登録されているか確認する
	 * 
	 * @param customerId
	 * @return
	 */
	public boolean existsCustomerFoloderDB(Long customerId) {
		TFolderDropboxEntity tFolderDropboxEntity = tFolderDropboxDao.selectEnabledByCustomerId(customerId);
		if (tFolderDropboxEntity == null) {
			return false;
		}
		return true;
	}

	/**
	 * Dropboxアプリのルートフォルダ(loioz)へURLを取得する
	 * 
	 * @return
	 */
	public String getRootFolderUrl() {
		TRootFolderDropboxEntity tRootFolderDropboxEntity = tRootFolderDropboxDao.selectEnabledRoot();
		return createDropboxURL(tRootFolderDropboxEntity.getFolderId());
	}

	/**
	 * Dropboxアプリの案件フォルダへURLを取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	public String getAnkenFolderUrl(Long ankenId) {
		TFolderDropboxEntity tFolderDropboxEntity = tFolderDropboxDao.selectEnabledByAnkenId(ankenId);
		return createDropboxURL(tFolderDropboxEntity.getFolderId());
	}

	/**
	 * Dropboxアプリの顧客フォルダへURLを取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	public String getCustomerFolderUrl(Long customerId) {
		TFolderDropboxEntity tFolderDropboxEntity = tFolderDropboxDao.selectEnabledByCustomerId(customerId);
		return createDropboxURL(tFolderDropboxEntity.getFolderId());
	}

	/**
	 * 案件フォルダの作成を行う
	 * 
	 * @param ankenId
	 * @param reCreateRoot 案件ID:〇〇-〇〇を再作成する時にtrue (trueの場合必ず、reCreateChildはfalse)
	 * @param reCreateChild 案-〇〇を再作成する時にtrue (trueの場合必ず、reCreateRootはfalse)
	 * @throws AppException
	 */
	public void createAnkenFolder(Long ankenId, boolean reCreateRoot, boolean reCreateChild) throws AppException {

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.DROPBOX);
		DbxClientV2 cliantApi = commonDropboxApiService.getDbxClientV2(tAuthTokenEntity.getAccessToken());

		TRootFolderDropboxEntity tRootFolderDropboxEntity = tRootFolderDropboxDao.selectEnabledRoot();
		TFolderDropboxEntity ankenRoot = tFolderDropboxDao.selectEnabledByFindAnkenId(ankenId);

		String createdAnkenRootFolderId = "";
		String createdAnkenFolderId = "";

		try {
			if (ankenRoot == null || reCreateRoot) {
				// 該当する「案件ID：○-○」をフォルダがない場合、もしくは再作成する場合

				Long idFrom = LoiozNumberUtils.getFrom(ankenId, StorageConstants.FOLDER_INTERVAL);
				Long idTo = LoiozNumberUtils.getTo(ankenId, StorageConstants.FOLDER_INTERVAL);

				// APIで案件ルートフォルダを作成
				// 「loioz」フォルダ配下に「案件ID：○-○」をフォルダを作成する
				createdAnkenRootFolderId = commonDropboxApiService.createFolder(cliantApi, tRootFolderDropboxEntity.getFolderId(), String.format(StorageConstants.ANKEN_FOLDER_BASE, idFrom, idTo));

				if (reCreateRoot) {
					// DBには存在するはずなので、DBからルートフォルダ情報を削除する
					tFolderDropboxDao.delete(ankenRoot);

					// ルートフォルダ配下のフォルダデータを削除する
					List<TFolderDropboxEntity> tFolderDropboxEntities = tFolderDropboxDao.selectEnabledByFromToAnkenId(idFrom, idTo);
					tFolderDropboxDao.delete(tFolderDropboxEntities);
				}

				// 案件ルートフォルダの登録処理
				ankenRoot = new TFolderDropboxEntity();
				ankenRoot.setRootFolderDropboxSeq(tRootFolderDropboxEntity.getRootFolderDropboxSeq());
				ankenRoot.setAnkenId(0L);
				ankenRoot.setFolderId(createdAnkenRootFolderId);
				ankenRoot.setAnkenCustomerIdFrom(idFrom);
				ankenRoot.setAnkenCustomerIdTo(idTo);
				tFolderDropboxDao.insert(ankenRoot);
			}

			// ============== ここまでがRootFolderの作成 =============================

			// APIで案件フォルダを作成
			createdAnkenFolderId = commonDropboxApiService.createFolder(cliantApi, ankenRoot.getFolderId(), AnkenId.of(ankenId).toString());

			if (reCreateChild) {
				TFolderDropboxEntity tFolderDropboxEntity = tFolderDropboxDao.selectEnabledByAnkenId(ankenId);
				tFolderDropboxDao.delete(tFolderDropboxEntity);
			}

			// 案件フォルダの登録処理
			TFolderDropboxEntity newAnkenRootFolder = new TFolderDropboxEntity();
			newAnkenRootFolder.setRootFolderDropboxSeq(ankenRoot.getRootFolderDropboxSeq());
			newAnkenRootFolder.setAnkenId(ankenId);
			newAnkenRootFolder.setFolderId(createdAnkenFolderId);
			tFolderDropboxDao.insert(newAnkenRootFolder);
		} catch (Exception e) {
			// エラー発生時に、APIがすでに投げていた場合、フォルダの削除を行う

			Consumer<String> apiRollbackFn = (folderId) -> {
				try {
					// 作成フォルダの削除
					cliantApi.files().deleteV2(folderId);
				} catch (Exception ex) {
					// ここでエラーが発生しても何もしない。
					logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
				}
			};

			if (!StringUtils.isEmpty(createdAnkenRootFolderId) && !StringUtils.isEmpty(createdAnkenFolderId)) {
				// エラー発生時にルートフォルダ、フォルダをどちらも作成している場合 -> ルートフォルダのみAPI削除
				apiRollbackFn.accept(createdAnkenRootFolderId);
			} else if (!StringUtils.isEmpty(createdAnkenRootFolderId)) {
				// エラー発生時にルートフォルダのみ作成している場合 -> ルートフォルダのみAPI削除
				apiRollbackFn.accept(createdAnkenRootFolderId);
			} else if (!StringUtils.isEmpty(createdAnkenFolderId)) {
				// エラー発生時に子フォルダのみ作成している場合 -> 子フォルダのみAPI削除
				apiRollbackFn.accept(createdAnkenFolderId);
			} else {
				// なにもしない
			}
			// 発生したエラーはそのままthrow
			throw e;
		}
	}

	/**
	 * 顧客フォルダの作成を行う
	 * 
	 * @param customerId
	 * @param reCreateRoot 顧客ID:〇〇-〇〇を再作成する時にtrue (trueの場合必ず、reCreateChildはfalse)
	 * @param reCreateChild 案-〇〇を再作成する時にtrue (trueの場合必ず、reCreateRootはfalse)
	 * @throws AppException
	 */
	public void createCustomerFolder(Long customerId, boolean reCreateRoot, boolean reCreateChild) throws AppException {

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.DROPBOX);
		DbxClientV2 cliantApi = commonDropboxApiService.getDbxClientV2(tAuthTokenEntity.getAccessToken());

		TRootFolderDropboxEntity tRootFolderDropboxEntity = tRootFolderDropboxDao.selectEnabledRoot();
		TFolderDropboxEntity customerRoot = tFolderDropboxDao.selectEnabledByFindCustomerId(customerId);

		String createdCustomerRootFolderId = "";
		String createdCustomerFolderId = "";

		try {
			if (customerRoot == null || reCreateRoot) {
				// 該当する「顧客ID：○-○」をフォルダがない場合、もしくは再作成する場合

				Long idFrom = LoiozNumberUtils.getFrom(customerId, StorageConstants.FOLDER_INTERVAL);
				Long idTo = LoiozNumberUtils.getTo(customerId, StorageConstants.FOLDER_INTERVAL);

				// APIで顧客ルートフォルダを作成
				// 「loioz」フォルダ配下に「顧客ID：○-○」をフォルダを作成する
				createdCustomerRootFolderId = commonDropboxApiService.createFolder(cliantApi, tRootFolderDropboxEntity.getFolderId(), String.format(StorageConstants.PERSON_FOLDER_BASE, idFrom, idTo));

				if (reCreateRoot) {
					// DBには存在するはずなので、DBからルートフォルダ情報を削除する
					tFolderDropboxDao.delete(customerRoot);

					// ルートフォルダ配下のフォルダデータを削除する
					List<TFolderDropboxEntity> tFolderDropboxEntities = tFolderDropboxDao.selectEnabledByFromToCustomerId(idFrom, idTo);
					tFolderDropboxDao.delete(tFolderDropboxEntities);
				}

				// 顧客ルートフォルダの登録処理
				customerRoot = new TFolderDropboxEntity();
				customerRoot.setRootFolderDropboxSeq(tRootFolderDropboxEntity.getRootFolderDropboxSeq());
				customerRoot.setCustomerId(0L);
				customerRoot.setFolderId(createdCustomerRootFolderId);
				customerRoot.setAnkenCustomerIdFrom(idFrom);
				customerRoot.setAnkenCustomerIdTo(idTo);
				tFolderDropboxDao.insert(customerRoot);
			}

			// ============== ここまでがRootFolderの作成 =============================

			// APIで顧客フォルダを作成
			createdCustomerFolderId = commonDropboxApiService.createFolder(cliantApi, customerRoot.getFolderId(), PersonId.of(customerId).toString());

			if (reCreateChild) {
				TFolderDropboxEntity tFolderDropboxEntity = tFolderDropboxDao.selectEnabledByCustomerId(customerId);
				tFolderDropboxDao.delete(tFolderDropboxEntity);
			}

			// 顧客フォルダの登録処理
			TFolderDropboxEntity newCustomerRootFolder = new TFolderDropboxEntity();
			newCustomerRootFolder.setRootFolderDropboxSeq(customerRoot.getRootFolderDropboxSeq());
			newCustomerRootFolder.setCustomerId(customerId);
			newCustomerRootFolder.setFolderId(createdCustomerFolderId);
			tFolderDropboxDao.insert(newCustomerRootFolder);
		} catch (Exception e) {
			// エラー発生時に、APIがすでに投げていた場合、フォルダの削除を行う

			Consumer<String> apiRollbackFn = (folderId) -> {
				try {
					// 作成フォルダの削除
					cliantApi.files().deleteV2(folderId);
				} catch (Exception ex) {
					// ここでエラーが発生しても何もしない。
					logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
				}
			};

			if (!StringUtils.isEmpty(createdCustomerRootFolderId) && !StringUtils.isEmpty(createdCustomerFolderId)) {
				// エラー発生時にルートフォルダ、フォルダをどちらも作成している場合 -> ルートフォルダのみAPI削除
				apiRollbackFn.accept(createdCustomerRootFolderId);
			} else if (!StringUtils.isEmpty(createdCustomerRootFolderId)) {
				// エラー発生時にルートフォルダのみ作成している場合 -> ルートフォルダのみAPI削除
				apiRollbackFn.accept(createdCustomerRootFolderId);
			} else if (!StringUtils.isEmpty(createdCustomerFolderId)) {
				// エラー発生時に子フォルダのみ作成している場合 -> 子フォルダのみAPI削除
				apiRollbackFn.accept(createdCustomerFolderId);
			} else {
				// なにもしない
			}
			// 発生したエラーはそのままthrow
			throw e;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * folderIdからBoxのURLを作成する
	 * 
	 * @param folderId
	 * @return
	 */
	private String createDropboxURL(String folderId) {

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.DROPBOX);
		DbxClientV2 cliantApi = commonDropboxApiService.getDbxClientV2(tAuthTokenEntity.getAccessToken());

		FolderMetadata folderMetadata = null;
		try {
			// フォルダ情報の取得
			folderMetadata = commonDropboxApiService.getFolderMetaData(cliantApi, folderId);
		} catch (Exception e) {
			// 呼び出し元コントローラメソッドで削除チェックを行うことを想定しているため、ここでエラーが発生することは想定していない。
			throw new RuntimeException(e);
		}

		String url = DropboxConstants.DROPBOX_APP_BASE;
		url += folderMetadata.getPathLower();

		return url;
	}

}
