package jp.loioz.app.common.api.box.service;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxTrash;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.api.box.constants.BoxConstants;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.external.StorageConstants;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TFolderBoxDao;
import jp.loioz.dao.TRootFolderBoxDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.PersonId;
import jp.loioz.entity.TAuthTokenEntity;
import jp.loioz.entity.TFolderBoxEntity;
import jp.loioz.entity.TRootFolderBoxEntity;

/**
 * Boxのサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BoxService extends DefaultService {

	@Autowired
	private TRootFolderBoxDao tRootFolderBoxDao;

	@Autowired
	private TFolderBoxDao tFolderBoxDao;

	@Autowired
	private CommonBoxApiService commonBoxApiService;

	@Autowired
	private CommonOAuthService commonOAuthService;

	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * Boxアプリのルートフォルダが削除済かどうか判断する
	 *
	 * @return
	 * @throws AppException
	 */
	public boolean isDeletedRootFolder() throws AppException {

		TRootFolderBoxEntity tRootFolderBoxEntity = tRootFolderBoxDao.selectEnabledRoot();

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.BOX);
		BoxAPIConnection api = commonBoxApiService.getBoxAPIConnection(tAuthTokenEntity.getAccessToken(), tAuthTokenEntity.getRefreshToken());
		boolean isDeleted = commonBoxApiService.isDeleted(api, tRootFolderBoxEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Boxアプリの顧客ルートフォルダが削除済かどうか判断する
	 * 
	 * @param customerId
	 * @return
	 * @throws AppException
	 */
	public boolean isDeletedCustomerRootFolder(Long customerId) throws AppException {

		TFolderBoxEntity tFolderBoxEntity = tFolderBoxDao.selectEnabledByFindCustomerId(customerId);
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.BOX);
		BoxAPIConnection api = commonBoxApiService.getBoxAPIConnection(tAuthTokenEntity.getAccessToken(), tAuthTokenEntity.getRefreshToken());
		boolean isDeleted = commonBoxApiService.isDeleted(api, tFolderBoxEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Boxアプリの顧客フォルダが削除済かどうか判断する
	 * 
	 * @param customerId
	 * @return
	 * @throws AppException
	 */
	public boolean isDeletedCustomerFolder(Long customerId) throws AppException {

		TFolderBoxEntity tFolderBoxEntity = tFolderBoxDao.selectEnabledByCustomerId(customerId);
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.BOX);
		BoxAPIConnection api = commonBoxApiService.getBoxAPIConnection(tAuthTokenEntity.getAccessToken(), tAuthTokenEntity.getRefreshToken());
		boolean isDeleted = commonBoxApiService.isDeleted(api, tFolderBoxEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Boxアプリの案件ルートフォルダが削除済かどうか判断する
	 * 
	 * @param ankenId
	 * @return
	 * @throws AppException
	 */
	public boolean isDeletedAnkenRootFolder(Long ankenId) throws AppException {

		TFolderBoxEntity tFolderBoxEntity = tFolderBoxDao.selectEnabledByFindAnkenId(ankenId);
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.BOX);
		BoxAPIConnection api = commonBoxApiService.getBoxAPIConnection(tAuthTokenEntity.getAccessToken(), tAuthTokenEntity.getRefreshToken());
		boolean isDeleted = commonBoxApiService.isDeleted(api, tFolderBoxEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Boxアプリの案件フォルダが削除済かどうか判断する
	 * 
	 * @param ankenId
	 * @return
	 * @throws AppException
	 */
	public boolean isDeletedAnkenFolder(Long ankenId) throws AppException {

		TFolderBoxEntity tFolderBoxEntity = tFolderBoxDao.selectEnabledByAnkenId(ankenId);
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.BOX);
		BoxAPIConnection api = commonBoxApiService.getBoxAPIConnection(tAuthTokenEntity.getAccessToken(), tAuthTokenEntity.getRefreshToken());
		boolean isDeleted = commonBoxApiService.isDeleted(api, tFolderBoxEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * 案件ルートフォルダ情報がDBに登録されているか確認する
	 * 
	 * @param ankenId
	 * @return
	 */
	public boolean existsAnkenRootFoloderDB(Long ankenId) {
		TFolderBoxEntity tFolderBoxEntity = tFolderBoxDao.selectEnabledByFindAnkenId(ankenId);
		if (tFolderBoxEntity == null) {
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
		TFolderBoxEntity tFolderBoxEntity = tFolderBoxDao.selectEnabledByAnkenId(ankenId);
		if (tFolderBoxEntity == null) {
			return false;
		}
		return true;
	}

	/**
	 * 顧客ルートフォルダ情報がDBに登録されているか確認する
	 * 
	 * @param ankenId
	 * @return
	 */
	public boolean existsCustomerRootFoloderDB(Long customerId) {
		TFolderBoxEntity tFolderBoxEntity = tFolderBoxDao.selectEnabledByFindCustomerId(customerId);
		if (tFolderBoxEntity == null) {
			return false;
		}
		return true;
	}

	/**
	 * 顧客フォルダ情報がDBに登録されているか確認する
	 * 
	 * @param ankenId
	 * @return
	 */
	public boolean existsCustomerFoloderDB(Long customerId) {
		TFolderBoxEntity tFolderBoxEntity = tFolderBoxDao.selectEnabledByCustomerId(customerId);
		if (tFolderBoxEntity == null) {
			return false;
		}
		return true;
	}

	/**
	 * Boxアプリのルートフォルダ(loioz)へURLを取得する
	 * 
	 * @return
	 */
	public String getRootFolderUrl() {
		TRootFolderBoxEntity tRootFolderBoxEntity = tRootFolderBoxDao.selectEnabledRoot();
		return createBoxURL(tRootFolderBoxEntity.getFolderId());
	}

	/**
	 * Boxアプリの案件フォルダへURLを取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	public String getAnkenFolderUrl(Long ankenId) {
		TFolderBoxEntity tFolderBoxEntity = tFolderBoxDao.selectEnabledByAnkenId(ankenId);
		return createBoxURL(tFolderBoxEntity.getFolderId());
	}

	/**
	 * Boxアプリの顧客フォルダへURLを取得する
	 * 
	 * @param customerId
	 * @return
	 */
	public String getCustomerFolderUrl(Long customerId) {
		TFolderBoxEntity tFolderBoxEntity = tFolderBoxDao.selectEnabledByCustomerId(customerId);
		return createBoxURL(tFolderBoxEntity.getFolderId());
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

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.BOX);
		BoxAPIConnection api = commonBoxApiService.getBoxAPIConnection(tAuthTokenEntity.getAccessToken(), tAuthTokenEntity.getRefreshToken());

		TRootFolderBoxEntity tRootFolderBoxEntity = tRootFolderBoxDao.selectEnabledRoot();
		TFolderBoxEntity ankenRoot = tFolderBoxDao.selectEnabledByFindAnkenId(ankenId);

		String createdAnkenRootFolderId = "";
		String createdAnkenFolderId = "";

		try {
			if (ankenRoot == null || reCreateRoot) {
				// 該当する「案件ID：○-○」をフォルダがない場合、もしくは再作成する場合

				Long idFrom = LoiozNumberUtils.getFrom(ankenId, StorageConstants.FOLDER_INTERVAL);
				Long idTo = LoiozNumberUtils.getTo(ankenId, StorageConstants.FOLDER_INTERVAL);

				// APIで案件ルートフォルダを作成
				// ルートフォルダ配下に「案件ID：○-○」をフォルダを作成する
				createdAnkenRootFolderId = commonBoxApiService.createFolder(api, tRootFolderBoxEntity.getFolderId(), String.format(StorageConstants.ANKEN_FOLDER_BASE, idFrom, idTo));

				if (reCreateRoot) {
					// DBには存在するはずなので、DBからルートフォルダ情報を削除する
					tFolderBoxDao.delete(ankenRoot);

					// ルートフォルダ配下のフォルダデータを削除する
					List<TFolderBoxEntity> tFolderBoxEntities = tFolderBoxDao.selectEnabledByFromToAnkenId(idFrom, idTo);
					tFolderBoxDao.delete(tFolderBoxEntities);
				}

				// 案件ルートフォルダの登録処理
				ankenRoot = new TFolderBoxEntity();
				ankenRoot.setRootFolderBoxSeq(tRootFolderBoxEntity.getRootFolderBoxSeq());
				ankenRoot.setAnkenId(0L);
				ankenRoot.setFolderId(createdAnkenRootFolderId);
				ankenRoot.setAnkenCustomerIdFrom(idFrom);
				ankenRoot.setAnkenCustomerIdTo(idTo);
				tFolderBoxDao.insert(ankenRoot);
			}

			// ============== ここまでがRootFolderの作成 =============================

			// APIで案件フォルダを作成
			createdAnkenFolderId = commonBoxApiService.createFolder(api, ankenRoot.getFolderId(), AnkenId.of(ankenId).toString());

			if (reCreateChild) {
				TFolderBoxEntity tFolderBoxEntity = tFolderBoxDao.selectEnabledByAnkenId(ankenId);
				tFolderBoxDao.delete(tFolderBoxEntity);
			}

			// 案件フォルダの登録処理
			TFolderBoxEntity newAnkenRootFolder = new TFolderBoxEntity();
			newAnkenRootFolder.setRootFolderBoxSeq(ankenRoot.getRootFolderBoxSeq());
			newAnkenRootFolder.setAnkenId(ankenId);
			newAnkenRootFolder.setFolderId(createdAnkenFolderId);
			tFolderBoxDao.insert(newAnkenRootFolder);

		} catch (Exception e) {
			// エラー発生時に、APIがすでに投げていた場合、フォルダの削除を行う

			Consumer<String> apiRollbackFn = (folderId) -> {
				try {
					// フォルダの削除処理は、共通処理では行わない
					BoxFolder boxFolder = new BoxFolder(api, folderId);
					boxFolder.delete(true);

					// ゴミ箱からも完全に削除
					BoxTrash trash = new BoxTrash(api);
					trash.deleteFolder(folderId);
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

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.BOX);
		BoxAPIConnection api = commonBoxApiService.getBoxAPIConnection(tAuthTokenEntity.getAccessToken(), tAuthTokenEntity.getRefreshToken());

		TRootFolderBoxEntity tRootFolderBoxEntity = tRootFolderBoxDao.selectEnabledRoot();
		TFolderBoxEntity customerRoot = tFolderBoxDao.selectEnabledByFindCustomerId(customerId);

		String createdCustomerRootFolderId = "";
		String createdCustomerFolderId = "";

		try {
			if (customerRoot == null || reCreateRoot) {
				// 該当する「顧客ID：○-○」をフォルダがない場合、もしくは再作成する場合

				Long idFrom = LoiozNumberUtils.getFrom(customerId, StorageConstants.FOLDER_INTERVAL);
				Long idTo = LoiozNumberUtils.getTo(customerId, StorageConstants.FOLDER_INTERVAL);

				// APIで顧客ルートフォルダを作成
				// ルートフォルダ配下に「顧客ID：○-○」をフォルダを作成する
				createdCustomerRootFolderId = commonBoxApiService.createFolder(api, tRootFolderBoxEntity.getFolderId(), String.format(StorageConstants.PERSON_FOLDER_BASE, idFrom, idTo));

				if (reCreateRoot) {
					// DBには存在するはずなので、DBからルートフォルダ情報を削除する
					tFolderBoxDao.delete(customerRoot);

					// ルートフォルダ配下のフォルダデータを削除する
					List<TFolderBoxEntity> tFolderBoxEntities = tFolderBoxDao.selectEnabledByFromToCustomerId(idFrom, idTo);
					tFolderBoxDao.delete(tFolderBoxEntities);
				}

				// 顧客ルートフォルダの登録処理
				customerRoot = new TFolderBoxEntity();
				customerRoot.setRootFolderBoxSeq(tRootFolderBoxEntity.getRootFolderBoxSeq());
				customerRoot.setCustomerId(0L);
				customerRoot.setFolderId(createdCustomerRootFolderId);
				customerRoot.setAnkenCustomerIdFrom(idFrom);
				customerRoot.setAnkenCustomerIdTo(idTo);
				tFolderBoxDao.insert(customerRoot);
			}

			// ============== ここまでがRootFolderの作成 =============================

			// APIで顧客フォルダを作成
			createdCustomerFolderId = commonBoxApiService.createFolder(api, customerRoot.getFolderId(), PersonId.of(customerId).toString());

			if (reCreateChild) {
				TFolderBoxEntity tFolderBoxEntity = tFolderBoxDao.selectEnabledByCustomerId(customerId);
				tFolderBoxDao.delete(tFolderBoxEntity);
			}

			// 顧客フォルダの登録処理
			TFolderBoxEntity newCustomerRootFolder = new TFolderBoxEntity();
			newCustomerRootFolder.setRootFolderBoxSeq(customerRoot.getRootFolderBoxSeq());
			newCustomerRootFolder.setCustomerId(customerId);
			newCustomerRootFolder.setFolderId(createdCustomerFolderId);
			tFolderBoxDao.insert(newCustomerRootFolder);

		} catch (Exception e) {
			// エラー発生時に、APIがすでに投げていた場合、フォルダの削除を行う

			Consumer<String> apiRollbackFn = (folderId) -> {
				try {
					// フォルダの削除処理は、共通処理では行わない
					BoxFolder boxFolder = new BoxFolder(api, folderId);
					boxFolder.delete(true);

					// ゴミ箱からも完全に削除
					BoxTrash trash = new BoxTrash(api);
					trash.deleteFolder(folderId);
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
	private String createBoxURL(String folderId) {

		String url = BoxConstants.BOX_APP_BASE_URL;
		url += folderId;

		return url;
	}

}
