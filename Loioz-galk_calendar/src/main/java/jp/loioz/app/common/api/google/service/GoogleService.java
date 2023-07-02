package jp.loioz.app.common.api.google.service;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.services.drive.Drive;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.api.google.constants.GoogleConstants;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.external.StorageConstants;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TFolderGoogleDao;
import jp.loioz.dao.TRootFolderGoogleDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.PersonId;
import jp.loioz.entity.TAuthTokenEntity;
import jp.loioz.entity.TFolderGoogleEntity;
import jp.loioz.entity.TRootFolderGoogleEntity;

/**
 * Googleサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GoogleService extends DefaultService {

	@Autowired
	private CommonOAuthService commonOAuthService;

	@Autowired
	private CommonGoogleApiService commonGoogleApiService;

	@Autowired
	private TFolderGoogleDao tFolderGoogleDao;

	@Autowired
	private TRootFolderGoogleDao tRootFolderGoogleDao;

	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * Googleアプリのルートフォルダ(loioz)へURLを取得する
	 * 
	 * @return
	 */
	public String getRootFolderUrl() {
		TRootFolderGoogleEntity tRootFolderGoogleEntity = tRootFolderGoogleDao.selectEnabledRoot();
		return createGoogleFolderUrl(tRootFolderGoogleEntity.getFolderId());
	}

	/**
	 * Googleアプリの案件フォルダへURLを取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	public String getAnkenFolderUrl(Long ankenId) {
		TFolderGoogleEntity tFolderGoogleEntity = tFolderGoogleDao.selectEnabledByAnkenId(ankenId);
		return this.createGoogleFolderUrl(tFolderGoogleEntity.getFolderId());
	}

	/**
	 * Googleアプリの顧客フォルダへURLを取得する
	 * 
	 * @param customerId
	 * @return
	 */
	public String getCustomerFolderUrl(Long customerId) {
		TFolderGoogleEntity tFolderGoogleEntity = tFolderGoogleDao.selectEnabledByCustomerId(customerId);
		return this.createGoogleFolderUrl(tFolderGoogleEntity.getFolderId());
	}

	/**
	 * 案件ルートフォルダ情報がDBに登録されているか確認する
	 * 
	 * @param ankenId
	 * @return
	 */
	public boolean existsAnkenRootFoloderDB(Long ankenId) {
		TFolderGoogleEntity tFolderGoogleEntity = tFolderGoogleDao.selectEnabledByFindAnkenId(ankenId);
		if (tFolderGoogleEntity == null) {
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
		TFolderGoogleEntity tFolderGoogleEntity = tFolderGoogleDao.selectEnabledByAnkenId(ankenId);
		if (tFolderGoogleEntity == null) {
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
		TFolderGoogleEntity tFolderGoogleEntity = tFolderGoogleDao.selectEnabledByFindCustomerId(customerId);
		if (tFolderGoogleEntity == null) {
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
		TFolderGoogleEntity tFolderGoogleEntity = tFolderGoogleDao.selectEnabledByCustomerId(customerId);
		if (tFolderGoogleEntity == null) {
			return false;
		}
		return true;
	}

	/**
	 * Googleアプリのloiozルートフォルダが削除済かどうか判断する
	 *
	 * @return
	 * @throws AppException
	 */
	public boolean isDeletedRootFolder() throws AppException {

		TRootFolderGoogleEntity tRootFolderGoogleEntity = tRootFolderGoogleDao.selectEnabledRoot();

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.GOOGLE);
		Drive googleDriveService = commonGoogleApiService.getDriveService(tAuthTokenEntity.getAccessToken());
		boolean isDeleted = commonGoogleApiService.isFolderDeleted(googleDriveService, tRootFolderGoogleEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Googleアプリの案件ルートフォルダが削除済かどうか判断する
	 * 
	 * @param ankenId
	 * @return
	 * @throws AppException
	 */
	public boolean isDeletedAnkenRootFolder(Long ankenId) throws AppException {

		TFolderGoogleEntity tFolderGoogleEntity = tFolderGoogleDao.selectEnabledByFindAnkenId(ankenId);
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.GOOGLE);
		Drive googleDriveService = commonGoogleApiService.getDriveService(tAuthTokenEntity.getAccessToken());
		boolean isDeleted = commonGoogleApiService.isFolderDeleted(googleDriveService, tFolderGoogleEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Googleアプリの案件フォルダが削除済かどうか判断する
	 * 
	 * @param ankenId
	 * @return
	 * @throws AppException
	 */
	public boolean isDeletedAnkenFolder(Long ankenId) throws AppException {

		TFolderGoogleEntity tFolderGoogleEntity = tFolderGoogleDao.selectEnabledByAnkenId(ankenId);
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.GOOGLE);
		Drive googleDriveService = commonGoogleApiService.getDriveService(tAuthTokenEntity.getAccessToken());
		boolean isDeleted = commonGoogleApiService.isFolderDeleted(googleDriveService, tFolderGoogleEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Googleアプリの顧客ルートフォルダが削除済かどうか判断する
	 * 
	 * @param customerId
	 * @return
	 * @throws AppException
	 */
	public boolean isDeletedCustomerRootFolder(Long customerId) throws AppException {

		TFolderGoogleEntity tFolderGoogleEntity = tFolderGoogleDao.selectEnabledByFindCustomerId(customerId);
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.GOOGLE);
		Drive googleDriveService = commonGoogleApiService.getDriveService(tAuthTokenEntity.getAccessToken());
		boolean isDeleted = commonGoogleApiService.isFolderDeleted(googleDriveService, tFolderGoogleEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Googleアプリの顧客フォルダが削除済かどうか判断する
	 * 
	 * @param customerId
	 * @return
	 * @throws AppException
	 */
	public boolean isDeletedCustomerFolder(Long customerId) throws AppException {

		TFolderGoogleEntity tFolderGoogleEntity = tFolderGoogleDao.selectEnabledByCustomerId(customerId);
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.GOOGLE);
		Drive googleDriveService = commonGoogleApiService.getDriveService(tAuthTokenEntity.getAccessToken());
		boolean isDeleted = commonGoogleApiService.isFolderDeleted(googleDriveService, tFolderGoogleEntity.getFolderId());
		return isDeleted;
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

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.GOOGLE);
		Drive googleDriveService = commonGoogleApiService.getDriveService(tAuthTokenEntity.getAccessToken());

		TRootFolderGoogleEntity tRootFolderGoogleEntity = tRootFolderGoogleDao.selectEnabledRoot();
		TFolderGoogleEntity ankenRoot = tFolderGoogleDao.selectEnabledByFindAnkenId(ankenId);

		String createdAnkenRootFolderId = "";
		String createdAnkenFolderId = "";

		try {
			if (ankenRoot == null || reCreateRoot) {
				// 該当する「案件ID：○-○」をフォルダがない場合、もしくは再作成する場合

				Long idFrom = LoiozNumberUtils.getFrom(ankenId, StorageConstants.FOLDER_INTERVAL);
				Long idTo = LoiozNumberUtils.getTo(ankenId, StorageConstants.FOLDER_INTERVAL);

				String rootFolderName = String.format(StorageConstants.ANKEN_FOLDER_BASE, idFrom, idTo);
				boolean existsFolderRoot = commonGoogleApiService.existsFolder(googleDriveService, rootFolderName, tRootFolderGoogleEntity.getFolderId());
				if (existsFolderRoot) {
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
					throw new AppException(MessageEnum.MSG_E00137, null, rootFolderName, "作成");
				}

				// APIで案件ルートフォルダを作成
				// 「loioz」フォルダ配下に「案件ID：○-○」をフォルダを作成する
				createdAnkenRootFolderId = commonGoogleApiService.createFolder(googleDriveService, tRootFolderGoogleEntity.getFolderId(), rootFolderName);

				if (reCreateRoot) {
					// DBには存在するはずなので、DBからルートフォルダ情報を削除する
					tFolderGoogleDao.delete(ankenRoot);

					// ルートフォルダ配下のフォルダデータを削除する
					List<TFolderGoogleEntity> tFolderGoogleEntities = tFolderGoogleDao.selectEnabledByFromToAnkenId(idFrom, idTo);
					tFolderGoogleDao.delete(tFolderGoogleEntities);
				}

				// GoogleDriveは同名フォルダの制約が無い(APIコールが正常処理になる)ので、楽観処理をシステム内に記述する
				boolean existsAnkenRoot = existsAnkenRootFoloderDB(ankenId);
				if (existsAnkenRoot) {
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
					throw new AppException(MessageEnum.MSG_E00025, null);
				}

				// 案件ルートフォルダの登録処理
				ankenRoot = new TFolderGoogleEntity();
				ankenRoot.setRootFolderGoogleSeq(tRootFolderGoogleEntity.getRootFolderGoogleSeq());
				ankenRoot.setAnkenId(0L);
				ankenRoot.setFolderId(createdAnkenRootFolderId);
				ankenRoot.setAnkenCustomerIdFrom(idFrom);
				ankenRoot.setAnkenCustomerIdTo(idTo);
				tFolderGoogleDao.insert(ankenRoot);
			}

			// ============== ここまでがRootFolderの作成 =============================

			// API発火前に同名チェックを行う。
			boolean existsFolderChild = commonGoogleApiService.existsFolder(googleDriveService, AnkenId.of(ankenId).toString(), ankenRoot.getFolderId());
			if (!reCreateChild && existsFolderChild) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00137, null, AnkenId.of(ankenId).toString(), "作成");
			}

			// APIで案件フォルダを作成
			createdAnkenFolderId = commonGoogleApiService.createFolder(googleDriveService, ankenRoot.getFolderId(), AnkenId.of(ankenId).toString());

			if (reCreateChild) {
				TFolderGoogleEntity tFolderGoogleEntity = tFolderGoogleDao.selectEnabledByAnkenId(ankenId);
				tFolderGoogleDao.delete(tFolderGoogleEntity);
			}

			// GoogleDriveは同名フォルダの制約が無い(APIコールが正常処理になる)ので、楽観処理をシステム内に記述する
			boolean existsAnkenChild = existsAnkenFoloderDB(ankenId);
			if (existsAnkenChild) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00137, null, AnkenId.of(ankenId).toString(), "作成");
			}

			// 案件フォルダの登録処理
			TFolderGoogleEntity newAnkenRootFolder = new TFolderGoogleEntity();
			newAnkenRootFolder.setRootFolderGoogleSeq(ankenRoot.getRootFolderGoogleSeq());
			newAnkenRootFolder.setAnkenId(ankenId);
			newAnkenRootFolder.setFolderId(createdAnkenFolderId);
			tFolderGoogleDao.insert(newAnkenRootFolder);

		} catch (Exception e) {
			// エラー発生時に、APIがすでに投げていた場合、フォルダの削除を行う

			Consumer<String> apiRollbackFn = (folderId) -> {
				try {
					// 作成フォルダの削除
					googleDriveService.files().delete(folderId);
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
	 * @param reCreateChild 顧-〇〇を再作成する時にtrue (trueの場合必ず、reCreateRootはfalse)
	 * @throws AppException
	 */
	public void createCustomerFolder(Long customerId, boolean reCreateRoot, boolean reCreateChild) throws AppException {

		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getMyAuthToken(ExternalService.GOOGLE);
		Drive googleDriveService = commonGoogleApiService.getDriveService(tAuthTokenEntity.getAccessToken());

		TRootFolderGoogleEntity tRootFolderGoogleEntity = tRootFolderGoogleDao.selectEnabledRoot();
		TFolderGoogleEntity customerRoot = tFolderGoogleDao.selectEnabledByFindCustomerId(customerId);

		String createdCustomerRootFolderId = "";
		String createdCustomerFolderId = "";

		try {
			if (customerRoot == null || reCreateRoot) {
				// 該当する「顧客ID：○-○」をフォルダがない場合、もしくは再作成する場合

				Long idFrom = LoiozNumberUtils.getFrom(customerId, StorageConstants.FOLDER_INTERVAL);
				Long idTo = LoiozNumberUtils.getTo(customerId, StorageConstants.FOLDER_INTERVAL);

				String rootFolderName = String.format(StorageConstants.PERSON_FOLDER_BASE, idFrom, idTo);
				boolean existsFolderRoot = commonGoogleApiService.existsFolder(googleDriveService, rootFolderName, tRootFolderGoogleEntity.getFolderId());
				if (existsFolderRoot) {
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
					throw new AppException(MessageEnum.MSG_E00137, null, rootFolderName, "作成");
				}

				// APIで顧客ルートフォルダを作成
				// 「loioz」フォルダ配下に「案件ID：○-○」をフォルダを作成する
				createdCustomerRootFolderId = commonGoogleApiService.createFolder(googleDriveService, tRootFolderGoogleEntity.getFolderId(), rootFolderName);

				if (reCreateRoot) {
					// DBには存在するはずなので、DBからルートフォルダ情報を削除する
					tFolderGoogleDao.delete(customerRoot);

					// ルートフォルダ配下のフォルダデータを削除する
					List<TFolderGoogleEntity> tFolderGoogleEntities = tFolderGoogleDao.selectEnabledByFromToCustomerId(idFrom, idTo);
					tFolderGoogleDao.delete(tFolderGoogleEntities);
				}

				// GoogleDriveは同名フォルダの制約が無い(APIコールが正常処理になる)ので、楽観処理をシステム内に記述する
				boolean existsCustomerRoot = existsCustomerRootFoloderDB(customerId);
				if (existsCustomerRoot) {
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
					throw new AppException(MessageEnum.MSG_E00025, null);
				}

				// 案件ルートフォルダの登録処理
				customerRoot = new TFolderGoogleEntity();
				customerRoot.setRootFolderGoogleSeq(tRootFolderGoogleEntity.getRootFolderGoogleSeq());
				customerRoot.setCustomerId(0L);
				customerRoot.setFolderId(createdCustomerRootFolderId);
				customerRoot.setAnkenCustomerIdFrom(idFrom);
				customerRoot.setAnkenCustomerIdTo(idTo);
				tFolderGoogleDao.insert(customerRoot);
			}

			// ============== ここまでがRootFolderの作成 =============================

			// API発火前に同名チェックを行う。
			boolean existsFolderChild = commonGoogleApiService.existsFolder(googleDriveService, PersonId.of(customerId).toString(), customerRoot.getFolderId());
			if (existsFolderChild) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00137, null, PersonId.of(customerId).toString(), "作成");
			}

			// APIで顧客フォルダを作成
			createdCustomerFolderId = commonGoogleApiService.createFolder(googleDriveService, customerRoot.getFolderId(), PersonId.of(customerId).toString());

			if (reCreateChild) {
				TFolderGoogleEntity tFolderGoogleEntity = tFolderGoogleDao.selectEnabledByCustomerId(customerId);
				tFolderGoogleDao.delete(tFolderGoogleEntity);
			}

			// GoogleDriveは同名フォルダの制約が無い(APIコールが正常処理になる)ので、楽観処理をシステム内に記述する
			boolean existsCustomerChild = existsCustomerFoloderDB(customerId);
			if (existsCustomerChild) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}

			// 顧客フォルダの登録処理
			TFolderGoogleEntity newCustomerRootFolder = new TFolderGoogleEntity();
			newCustomerRootFolder.setRootFolderGoogleSeq(customerRoot.getRootFolderGoogleSeq());
			newCustomerRootFolder.setCustomerId(customerId);
			newCustomerRootFolder.setFolderId(createdCustomerFolderId);
			tFolderGoogleDao.insert(newCustomerRootFolder);

		} catch (Exception e) {
			// エラー発生時に、APIがすでに投げていた場合、フォルダの削除を行う

			if (!StringUtils.isEmpty(createdCustomerRootFolderId) && !StringUtils.isEmpty(createdCustomerFolderId)) {
				// エラー発生時にルートフォルダ、フォルダをどちらも作成している場合 -> ルートフォルダのみAPI削除
				try {
					// 作成フォルダの削除
					googleDriveService.files().delete(createdCustomerRootFolderId);
				} catch (Exception ex) {
					// ここでエラーが発生しても何もしない。
					logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
				}
			} else if (!StringUtils.isEmpty(createdCustomerRootFolderId)) {
				// エラー発生時にルートフォルダのみ作成している場合 -> ルートフォルダのみAPI削除
				try {
					// 作成フォルダの削除
					googleDriveService.files().delete(createdCustomerRootFolderId);
				} catch (Exception ex) {
					// ここでエラーが発生しても何もしない。
					logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
				}

			} else if (!StringUtils.isEmpty(createdCustomerFolderId)) {
				// エラー発生時に子フォルダのみ作成している場合 -> 子フォルダのみAPI削除
				try {
					// 作成フォルダの削除
					googleDriveService.files().delete(createdCustomerFolderId);
				} catch (Exception ex) {
					// ここでエラーが発生しても何もしない。
					logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
				}
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
	 * GoogleDriveアプリのフォルダに遷移するURLを作成する
	 * 
	 * @param folderId
	 * @return
	 */
	private String createGoogleFolderUrl(String folderId) {
		return String.format(GoogleConstants.GOOGLE_DRIVE_FOLDER_URL_BASE, folderId);
	}

}
