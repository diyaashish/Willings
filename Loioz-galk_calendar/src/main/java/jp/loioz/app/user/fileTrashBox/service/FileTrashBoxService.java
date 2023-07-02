package jp.loioz.app.user.fileTrashBox.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.seasar.doma.DomaNullPointerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonFileManagementService;
import jp.loioz.app.user.fileTrashBox.form.FileTrashBoxListViewForm;
import jp.loioz.app.user.fileTrashBox.form.FileTrashBoxMentenanceViewForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.FileKubun;
import jp.loioz.common.constant.CommonConstant.TrashBoxFlg;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.file.FileStorageService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TFileConfigurationManagementDao;
import jp.loioz.dao.TFileDetailInfoManagementDao;
import jp.loioz.dao.TFolderPermissionInfoManagementDao;
import jp.loioz.dto.FileDetailDto;
import jp.loioz.dto.TrashBoxFileListDto;
import jp.loioz.entity.TFileConfigurationManagementEntity;
import jp.loioz.entity.TFileDetailInfoManagementEntity;
import jp.loioz.entity.TFolderPermissionInfoManagementEntity;

/**
 * ゴミ箱(ファイル管理)画面Service
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FileTrashBoxService extends DefaultService {

	/**
	 * ファイル詳細情報管理Dao
	 */
	@Autowired
	private TFileDetailInfoManagementDao tFileDetailInfoManagementDao;

	/**
	 * ファイル構成管理Dao
	 */
	@Autowired
	private TFileConfigurationManagementDao tFileConfigurationManagementDao;

	/**
	 * フォルダ閲覧権限情報管理Dao
	 */
	@Autowired
	private TFolderPermissionInfoManagementDao tFolderPermissionInfoManagementDao;

	/**
	 * ファイルアップロード共通部品
	 */
	@Autowired
	private FileStorageService fileStorageService;

	/** ファイル管理共通サービスクラス */
	@Autowired
	private CommonFileManagementService commonFileManagementService;
	/**
	 * ロガークラス
	 */
	@Autowired
	private Logger logger;

	/**
	 * ファイル一覧情報をFormにセットする
	 *
	 * @param fileTrashBoxListViewForm 画面表示に使用するForm情報
	 */
	public void createViewForm(FileTrashBoxListViewForm fileTrashBoxListViewForm) {

		// フォルダ毎削除した対象を取得
		List<Long> parentFileConfigurationManagementIdList = tFileDetailInfoManagementDao.getTrashFileFolderData();
		// データを取得（フォルダ毎削除した場合は、中のファイルは表示しない）
		List<TrashBoxFileListDto> fileList = tFileDetailInfoManagementDao.getTrashFileData(parentFileConfigurationManagementIdList);
		fileTrashBoxListViewForm.setTrashBoxFileList(fileList);
		// 契約中のストレージ容量、使用容量、使用率の文言を取得
		fileTrashBoxListViewForm.setDispStrageInfo(commonFileManagementService.getStrageInfo());

	}

	/**
	 * クイック検索結果をフォームにセットする
	 *
	 * @param fileTrashBoxListViewForm 検索条件を含んだ、セット対象のフォーム
	 */
	public void setSearchResultToForm(FileTrashBoxListViewForm fileTrashBoxListViewForm) {

		// TODO システム管理者かそれ以外かを判定させる処理の実装
		boolean isSystemManager = false;

		Long loginAccountSeq = SessionUtils.getLoginAccountSeq();
		if (loginAccountSeq == null) {
			throw new DataNotFoundException("ログインユーザーのアカウント連番が取得できませんでした。");
		}

		// フォルダ毎削除した対象を取得
		List<Long> parentFileConfigurationManagementIdList = tFileDetailInfoManagementDao.getTrashFileFolderData();

		String fileNameForLikeSearch = "%" + fileTrashBoxListViewForm.getFileNameSearchString() + "%";
		List<TrashBoxFileListDto> fileList = tFileDetailInfoManagementDao.getTrashFileDataByFileNameLikeSearch(isSystemManager,
				loginAccountSeq, fileNameForLikeSearch, parentFileConfigurationManagementIdList);
		fileTrashBoxListViewForm.setTrashBoxFileList(fileList);

		// 契約中のストレージ容量、使用容量、使用率の文言を取得
		fileTrashBoxListViewForm.setDispStrageInfo(commonFileManagementService.getStrageInfo());

	}

	/**
	 * ファイル区分を取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return ファイル区分
	 */
	public FileKubun getFileKubun(Long fileConfigurationManagementId) {
		String fileKubun = tFileConfigurationManagementDao
				.getFileKubunByFileConfigurationManagementId(fileConfigurationManagementId);
		if (StringUtils.isEmpty(fileKubun)) {
			throw new DataNotFoundException("ファイル区分の取得に失敗しました。[fileConfigurationManagementId="
					+ fileConfigurationManagementId + "]");
		}
		return DefaultEnum.getEnum(FileKubun.class, fileKubun);
	}

	/**
	 * フォルダ名とダウンロード対象のフォルダパスから、LIKE検索用(前方一致)のフォルダパスを作成する
	 *
	 * @param fileName ダウンロード対象のフォルダ名
	 * @param baseFolderPath 起点となるフォルダパス(ダウンロード対象のフォルダのファイル詳細情報に設定されているフォルダパス)
	 * @return LIKE検索用のフォルダパス
	 */
	private String createFolderPathForLikeSearch(String fileName, String baseFolderPath) {
		return baseFolderPath + fileName + CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER + "%";
	}

	/**
	 * ファイルを元に戻す
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @throws AppException
	 */
	public void undoFile(Long fileConfigurationManagementId) throws AppException {

		// 更新対象取得
		TFileDetailInfoManagementEntity updateTarget = tFileDetailInfoManagementDao
				.getDeleteTFileDetailInfoByFileConfigurationManagementId(fileConfigurationManagementId);

		if (updateTarget == null) {
			throw new DataNotFoundException(
					"更新対象のファイル詳細情報を取得できませんでした。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}

		// ルートフォルダ関連情報管理IDを取得するため、ファイル構成管理情報を取得
		TFileConfigurationManagementEntity tFileConfigurationManagementEntity = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (tFileConfigurationManagementEntity == null) {
			throw new DataNotFoundException(
					"ファイル構成管理情報を取得できませんでした。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}

		// 親フォルダがゴミ箱に入っている場合は、親フォルダを更新対象に含める
		List<TFileDetailInfoManagementEntity> updateTargetList = getParentFolderInfoIfParentFolderInTrashBox(
				updateTarget.getFolderPath(),
				tFileConfigurationManagementEntity.getRootFolderRelatedInfoManagementId(),
				tFileConfigurationManagementEntity.getParentFileConfigurationManagementId());

		// 更新対象のファイルの情報を更新対象に含める
		updateTargetList.add(updateTarget);

		// 更新内容を設定
		updateTargetList.forEach(updateInfo -> {
			// ゴミ箱フラグ
			updateInfo.setTrashBoxFlg(TrashBoxFlg.IS_NOT_TRASH.getCd());
			// 削除操作アカウント連番
			updateInfo.setDeletedOperationAccountSeq(null);
		});

		// 更新
		batchUpdateForTFileDetailInfo(updateTargetList);
	}

	/**
	 * フォルダパスから親フォルダがゴミ箱に入っているかを確認し、ゴミ箱に含まれている親フォルダ情報のリストを返却する
	 *
	 * @param folderPathToBeChecked チェック対象のフォルダパス
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @param parentFileConfigurationManagementId 親ファイル構成管理ID
	 * @return
	 */
	private List<TFileDetailInfoManagementEntity> getParentFolderInfoIfParentFolderInTrashBox(String folderPathToBeChecked,
			Long rootFolderRelatedInfoManagementId, Long parentFileConfigurationManagementId) {

		// 返却用リスト
		List<TFileDetailInfoManagementEntity> returnList = new ArrayList<>();

		// 親フォルダ名配列
		List<String> parentFolderNames = StringUtils.toArrayNoTrim(
				folderPathToBeChecked,
				CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER)
				.stream()
				.skip(1) // リストの先頭の空文字要素を除去
				.collect(Collectors.toList());

		// 検索用フォルダパス
		String parentFolderPath = CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER;

		for (String parentFolderName : parentFolderNames) {

			// ゴミ箱に入っているフォルダ情報を取得
			TFileDetailInfoManagementEntity entity = tFileDetailInfoManagementDao.getTFileDetailInTrashBox(
					parentFolderName, parentFolderPath, rootFolderRelatedInfoManagementId, parentFileConfigurationManagementId);
			if (entity != null) {
				returnList.add(entity);
			}

			parentFolderPath += parentFolderName + CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER;
		}

		return returnList;
	}

	/**
	 * 複数件のファイル詳細情報を更新する
	 *
	 * @param updateTargetList 更新対象のファイル詳細情報のリスト
	 * @throws AppException
	 */
	private void batchUpdateForTFileDetailInfo(List<TFileDetailInfoManagementEntity> updateTargetList) throws AppException {
		// 更新件数
		int[] resultArray = null;

		try {
			// 更新
			resultArray = tFileDetailInfoManagementDao.batchUpdate(updateTargetList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00036, ex);
		}
		if (resultArray.length != updateTargetList.size()) {
			// 実際の更新件数と更新対象の件数が異なる場合はエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00036, null);
		}
	}

	/**
	 * ファイルを戻すファイル・フォルダが同階層内に存在するか
	 *
	 * @param fileConfigurationManagementId ルートフォルダ関連情報管理ID
	 * @return true: 同階層に同名のファイル・フォルダが存在する、false: 同階層に同名のファイル・フォルダが存在しない
	 */
	public boolean existFile(Long fileConfigurationManagementId) {
		// 更新対象取得
		TFileDetailInfoManagementEntity updateTarget = tFileDetailInfoManagementDao
				.getDeleteTFileDetailInfoByFileConfigurationManagementId(fileConfigurationManagementId);

		if (updateTarget == null) {
			throw new DataNotFoundException(
					"ゴミ箱から戻す対象のファイル詳細情報を取得できませんでした。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}

		// ルートフォルダ関連情報管理IDを取得するため、ファイル構成管理情報を取得
		TFileConfigurationManagementEntity tFileConfigurationManagementEntity = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (tFileConfigurationManagementEntity == null) {
			throw new DataNotFoundException(
					"ファイル構成管理情報を取得できませんでした。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}

		List<FileDetailDto> fileList = tFileDetailInfoManagementDao.getMatchInFileNameInSomeFolderHierarchy(
				updateTarget.getFileName(), updateTarget.getFileExtension(), updateTarget.getFolderPath(),
				tFileConfigurationManagementEntity.getRootFolderRelatedInfoManagementId());

		if (LoiozCollectionUtils.isEmpty(fileList)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * フォルダを元に戻す
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @throws AppException
	 */
	public void undoFolder(Long fileConfigurationManagementId) throws AppException {

		// Like用の文字列を作成するために、更新対象の情報を取得
		TFileDetailInfoManagementEntity restoreFileDetailInfo = tFileDetailInfoManagementDao
				.getDeleteTFileDetailInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (restoreFileDetailInfo == null) {
			throw new DataNotFoundException(
					"更新対象のファイル詳細情報を取得できませんでした。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}
		// like検索用文字列を作成
		String stringForLikeSearch = createFolderPathForLikeSearch(restoreFileDetailInfo.getFileName(),
				restoreFileDetailInfo.getFolderPath());

		// ルートフォルダ関連情報管理IDを取得するため、元に戻す対象フォルダのファイル構成管理情報を取得
		TFileConfigurationManagementEntity restoreFolderFileConfigInfo = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (restoreFolderFileConfigInfo == null) {
			throw new DataNotFoundException(
					"ファイル構成管理情報を取得できませんでした。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}

		// 選択対象の配下の更新対象取得
		List<TFileDetailInfoManagementEntity> updateTargetList = tFileDetailInfoManagementDao
				.getFileInfoUnderTargetFolder(stringForLikeSearch,
						restoreFolderFileConfigInfo.getRootFolderRelatedInfoManagementId(),
						TrashBoxFlg.IS_TRASH.getCd());

		// 選択対象の親フォルダがゴミ箱に入っている場合、それらも元に戻す対象に含める
		List<TFileDetailInfoManagementEntity> updateTargetOfParentList = getParentFolderInfoIfParentFolderInTrashBox(
				restoreFileDetailInfo.getFolderPath(),
				restoreFolderFileConfigInfo.getRootFolderRelatedInfoManagementId(),
				restoreFolderFileConfigInfo.getParentFileConfigurationManagementId());
		updateTargetList.addAll(updateTargetOfParentList);

		// 選択した対象を追加
		updateTargetList.add(restoreFileDetailInfo);

		// 更新内容を設定
		updateTargetList.forEach(target -> {
			// ゴミ箱フラグ
			target.setTrashBoxFlg(TrashBoxFlg.IS_NOT_TRASH.getCd());
			// 削除操作アカウント連番
			target.setDeletedOperationAccountSeq(null);
		});

		// 更新
		batchUpdateForTFileDetailInfo(updateTargetList);

	}

	/**
	 * ファイルの完全削除を行う
	 *
	 * @param fileConfigurationManagementId 削除対象のファイル構成管理ID
	 * @throws AppException
	 */
	public void deleteFile(Long fileConfigurationManagementId) throws AppException {

		// 削除対象の取得
		TFileDetailInfoManagementEntity deleteTarget = tFileDetailInfoManagementDao
				.getDeleteTFileDetailInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		// 直前にゴミ箱から戻した等して削除対象を取得できなかった場合
		if (deleteTarget == null) {
			throw new DataNotFoundException(
					"削除対象のファイル構成管理情報を取得できませんでした。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}

		// ■DBレコード削除
		// ファイル詳細情報
		deleteTFileDetailInfo(deleteTarget);

		// ■AmazonS3のストレージから該当ファイルを削除
		List<String> s3Key = new ArrayList<>();
		s3Key.add(deleteTarget.getS3ObjectKey());
		fileStorageService.deleteFile(s3Key);
	}

	/**
	 * ファイル詳細情報の物理削除(1件削除用)
	 *
	 * @param deleteTarget 削除対象のファイル構成管理ID
	 * @throws AppException
	 */
	private void deleteTFileDetailInfo(TFileDetailInfoManagementEntity deleteTarget) throws AppException {

		// 削除件数
		deleteTarget.setFlgDelete();
		try {
			tFileDetailInfoManagementDao.update(deleteTarget);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);

		} catch (DomaNullPointerException ex) {
			// 物理削除直前に他者によって削除された場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
	}

	/**
	 * フォルダの論理削除を行う
	 *
	 * @param fileConfigurationManagementId 削除対象の親フォルダのファイル構成管理ID
	 * @throws AppException
	 */
	public void deleteFolder(Long fileConfigurationManagementId) throws AppException {

		// ■削除対象の親フォルダの情報を取得
		TFileConfigurationManagementEntity tFileConfigurationManagementEntity = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoByFileConfigurationManagementIdInTrashBox(fileConfigurationManagementId);
		if (tFileConfigurationManagementEntity == null) {
			throw new DataNotFoundException(
					"削除対象のファイル構成管理情報を取得できませんでした。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}
		TFileDetailInfoManagementEntity tFileDetailInfoManagementEntity = tFileDetailInfoManagementDao
				.getDeleteTFileDetailInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (tFileDetailInfoManagementEntity == null) {
			throw new DataNotFoundException(
					"削除対象のファイル詳細情報を取得できませんでした。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}

		// LIKE検索用のフォルダパス
		String folderPathForLikeSearch = createFolderPathForLikeSearch(tFileDetailInfoManagementEntity.getFileName(),
				tFileDetailInfoManagementEntity.getFolderPath());

		// ファイル詳細情報の削除後だとファイル構成管理情報の削除対象が取得できないため、先に取得しておく
		List<TFileConfigurationManagementEntity> deleteTargetTFileConfigurationManagementInfoList = tFileConfigurationManagementDao
				.getTargetTFileConfigManagementInfoUnderFolderInTrashBox(folderPathForLikeSearch,
						tFileConfigurationManagementEntity.getRootFolderRelatedInfoManagementId(),
						fileConfigurationManagementId);

		// 親フォルダ情報を追加
		deleteTargetTFileConfigurationManagementInfoList.add(tFileConfigurationManagementEntity);

		// ■レコード削除
		// フォルダ閲覧権限
		List<TFolderPermissionInfoManagementEntity> deleteTFolderPermissionInfoManagementEntities = tFolderPermissionInfoManagementDao
				.getFolderPermissionInfoUnderFolder(fileConfigurationManagementId, folderPathForLikeSearch,
						tFileConfigurationManagementEntity.getRootFolderRelatedInfoManagementId());
		if (deleteTFolderPermissionInfoManagementEntities == null
				|| deleteTFolderPermissionInfoManagementEntities.size() == 0) {
			throw new DataNotFoundException(
					"削除対象のフォルダ閲覧権限情報を取得できませんでした。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}
		deleteTFolderPermissionInfoManagementRecords(deleteTFolderPermissionInfoManagementEntities);

		// ファイル詳細情報
		List<TFileDetailInfoManagementEntity> deleteTFileDetailInfoManagementEntities = tFileDetailInfoManagementDao
				.getFileInfoUnderTargetFolder(folderPathForLikeSearch,
						tFileConfigurationManagementEntity.getRootFolderRelatedInfoManagementId(),
						TrashBoxFlg.IS_TRASH.getCd());
		deleteTFileDetailInfoManagementEntities.add(tFileDetailInfoManagementEntity);
		deleteTFileDetailInfoRecords(deleteTFileDetailInfoManagementEntities);

		// ■AmazonS3からも削除
		List<TFileDetailInfoManagementEntity> deleteS3List = new ArrayList<TFileDetailInfoManagementEntity>();
		deleteTFileDetailInfoManagementEntities.stream().forEach(TFileDetailInfoManagementEntity -> {
			if (TFileDetailInfoManagementEntity.getS3ObjectKey() != null) {
				deleteS3List.add(TFileDetailInfoManagementEntity);
			}
		});
		List<String> s3KeyList = getS3ObjectKeyListFormTFileDetailInfoList(deleteS3List);
		if (!s3KeyList.isEmpty()) {
			fileStorageService.deleteFile(s3KeyList);
		}
	}

	/**
	 * フォルダ閲覧権限情報管理のレコードを削除する
	 *
	 * @param deleteTargetList 削除対象のフォルダ閲覧権限情報
	 * @throws AppException
	 */
	private void deleteTFolderPermissionInfoManagementRecords(List<TFolderPermissionInfoManagementEntity> deleteTargetList) throws AppException {

		// 削除件数
		int[] deleteCountArray;
		try {
			deleteCountArray = tFolderPermissionInfoManagementDao.batchDelete(deleteTargetList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		} catch (DomaNullPointerException ex) {
			// 物理削除直前に他者によって削除された場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
		if (deleteCountArray.length != deleteTargetList.size()) {
			// 実際の削除件数と削除対象の件数が異なる場合はエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * ファイル詳細情報のレコードを論理削除する(複数件削除用)
	 *
	 * @param deleteTargetList 削除対象のファイル詳細情報
	 * @throws AppException
	 */
	private void deleteTFileDetailInfoRecords(List<TFileDetailInfoManagementEntity> deleteTargetList) throws AppException {

		// 削除件数
		int[] deleteCountArray;
		for (TFileDetailInfoManagementEntity entity : deleteTargetList) {
			entity.setFlgDelete();
		}
		try {
			deleteCountArray = tFileDetailInfoManagementDao.batchUpdate(deleteTargetList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		} catch (DomaNullPointerException ex) {
			// 物理削除直前に他者によって削除された場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
		if (deleteCountArray.length != deleteTargetList.size()) {
			// 実際の削除件数と削除対象の件数が異なる場合はエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}

	}

	/**
	 * ファイル構成管理情報を削除する(複数件削除用)
	 *
	 * @param deleteTargetList 削除対象のファイル構成管理情報リスト
	 * @throws AppException
	 */
	private void deleteTFileConfigurationManagementRecords(List<TFileConfigurationManagementEntity> deleteTargetList) throws AppException {

		// 削除件数
		int[] deleteCountArray;
		try {
			deleteCountArray = tFileConfigurationManagementDao.batchDelete(deleteTargetList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		} catch (DomaNullPointerException ex) {
			// 物理削除直前に他者によって削除された場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
		if (deleteCountArray.length != deleteTargetList.size()) {
			// 実際の削除件数と削除対象の件数が異なる場合はエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}

	}

	/**
	 * ファイル詳細情報のリストからS3オブジェクトキーを抜き出す
	 *
	 * @param fileDetailInfoList 抜き出す対象のファイル詳細情報
	 * @return S3オブジェクトキーのリスト
	 */
	private List<String> getS3ObjectKeyListFormTFileDetailInfoList(List<TFileDetailInfoManagementEntity> fileDetailInfoList) {
		List<String> s3KeyList = fileDetailInfoList
				.stream()
				.filter(info -> !StringUtils.isEmpty(info.getS3ObjectKey()))
				.map(info -> info.getS3ObjectKey())
				.collect(Collectors.toList());
		return s3KeyList;
	}

	/**
	 * 一括完全削除処理
	 *
	 * @throws AppException
	 */
	public void allFileAndFolderBatchDelete() throws AppException {

		boolean isSystemManager = false;

		Long loginAccountSeq = SessionUtils.getLoginAccountSeq();
		if (loginAccountSeq == null) {
			throw new DataNotFoundException("ログインユーザーのアカウント連番が取得できませんでした。");
		}

		// ファイル詳細情報の削除後だとファイル構成管理情報の削除対象が取得できないため、先に取得しておく
		List<TFileConfigurationManagementEntity> deleteTFileConfigurationManagementEntities = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoInTrashBox(isSystemManager, loginAccountSeq);
		if (deleteTFileConfigurationManagementEntities == null
				|| deleteTFileConfigurationManagementEntities.size() == 0) {
			throw new AppException(MessageEnum.MSG_E00164, null);
		}

		// ■レコード削除
		// フォルダ閲覧権限
		List<TFolderPermissionInfoManagementEntity> deleteTFolderPermissionInfoManagementEntities = tFolderPermissionInfoManagementDao
				.getAllFolderPermissionInfoInTrashBox(isSystemManager, loginAccountSeq);
		if (deleteTFolderPermissionInfoManagementEntities != null
				&& deleteTFolderPermissionInfoManagementEntities.size() > 0) {
			deleteTFolderPermissionInfoManagementRecords(deleteTFolderPermissionInfoManagementEntities);
		}

		// ファイル詳細情報
		List<TFileDetailInfoManagementEntity> deleteTFileDetailInfoManagementEntities = tFileDetailInfoManagementDao
				.getAllTFileDetailInfoInTrashBox(isSystemManager, loginAccountSeq);
		if (deleteTFileDetailInfoManagementEntities == null
				|| deleteTFileDetailInfoManagementEntities.size() == 0) {
			throw new DataNotFoundException(
					"削除対象のファイル詳細情報を取得できませんでした。[loginAccountSeq=" + loginAccountSeq + "]");
		}
		deleteTFileDetailInfoRecords(deleteTFileDetailInfoManagementEntities);

		// ファイル構成管理情報
		deleteTFileConfigurationManagementRecords(deleteTFileConfigurationManagementEntities);

		// ■AmazonS3のストレージからファイルを一括削除
		List<String> s3KeyList = getS3ObjectKeyListFormTFileDetailInfoList(
				deleteTFileDetailInfoManagementEntities);
		// フォルダのみ削除する場合はS3オブジェクトキーがないためリストが空となる。空リストでdelete処理を実行するとエラーとなるため、空リストの場合はdeleteを実行しない。
		if (!s3KeyList.isEmpty()) {
			fileStorageService.deleteFile(s3KeyList);
		}
	}

	/**
	 * ファイルのメンテナンス情報を区分を取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return ファイル区分
	 */
	public void getFileInfoForMaintenance(FileTrashBoxMentenanceViewForm fileTrashBoxMentenanceViewForm) {
		String fileKubun = tFileConfigurationManagementDao
				.getFileKubunByFileConfigurationManagementId(fileTrashBoxMentenanceViewForm.getFileConfigurationManagementId());
		if (StringUtils.isEmpty(fileKubun)) {
			throw new DataNotFoundException("ファイル区分の取得に失敗しました。[fileConfigurationManagementId="
					+ fileTrashBoxMentenanceViewForm.getFileConfigurationManagementId() + "]");
		}
		fileTrashBoxMentenanceViewForm.setFileKubun(fileKubun);
		// 削除対象の取得
		TFileDetailInfoManagementEntity deleteTarget = tFileDetailInfoManagementDao
				.getDeleteTFileDetailInfoByFileConfigurationManagementId(fileTrashBoxMentenanceViewForm.getFileConfigurationManagementId());
		if (FileKubun.IS_FILE.getCd().equals(fileKubun)) {
			fileTrashBoxMentenanceViewForm.setFileName(deleteTarget.getFileName() + deleteTarget.getFileExtension());
		} else if (FileKubun.IS_FOLDER.getCd().equals(fileKubun)) {
			fileTrashBoxMentenanceViewForm.setFileName(deleteTarget.getFileName());
		} else {
			throw new DataNotFoundException("ファイル区分の取得に失敗しました。[fileConfigurationManagementId="
					+ fileTrashBoxMentenanceViewForm.getFileConfigurationManagementId() + "]");
		}
	}
}
