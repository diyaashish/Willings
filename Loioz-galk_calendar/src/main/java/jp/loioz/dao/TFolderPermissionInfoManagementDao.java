package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TFolderPermissionInfoManagementEntity;

@ConfigAutowireable
@Dao
public interface TFolderPermissionInfoManagementDao {

	/**
	 * ファイル詳細管理IDリストをキーに検索する
	 * 
	 * @param fileConfigurationManagementList
	 * @return フォルダ権限情報管理
	 */
	@Select
	List<TFolderPermissionInfoManagementEntity> selectByfileConfigurationManagementList(List<Long> fileConfigurationManagementList);

	/**
	 * ファイル構成管理IDに紐づく１件のフォルダ閲覧権限情報を検索する
	 * 
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return フォルダ閲覧権限情報
	 */
	@Select
	TFolderPermissionInfoManagementEntity selectByFileConfigurationManagementId(Long fileConfigurationManagementId);

	/**
	 * フォルダ権限情報管理IDに紐づく１件のフォルダ閲覧権限情報を検索する
	 * 
	 * @param folderPermissionInfoManagementId フォルダ権限情報管理ID
	 * @return フォルダ閲覧権限情報
	 */
	@Select
	TFolderPermissionInfoManagementEntity selectByFolderPermissionInfoManagementId(Long folderPermissionInfoManagementId);

	/**
	 * １件のフォルダ閲覧権限情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TFolderPermissionInfoManagementEntity entity);

	/**
	 * ファイル構成管理IDから、対象のフォルダ配下の全てのフォルダ閲覧権限情報を取得する
	 * 
	 * @param fileConfigurationManagement 取得対象(親フォルダ)のファイル構成管理ID
	 * @param folderPath 親フォルダ配下のフォルダパス(Like検索で使用)
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @return 対象のフォルダ配下の全てのフォルダ閲覧権限情報
	 */
	@Select
	List<TFolderPermissionInfoManagementEntity> getFolderPermissionInfoUnderFolder(Long fileConfigurationManagementId,
			String folderPath, Long rootFolderRelatedInfoManagementId);

	/**
	 * 複数件のフォルダ閲覧権限情報を削除する
	 * 
	 * @param entityList 削除対象のフォルダ閲覧権限リスト
	 * @return 削除件数
	 */
	@BatchDelete
	int[] batchDelete(List<TFolderPermissionInfoManagementEntity> entityList);

	/**
	 * ゴミ箱内の全てのフォルダ閲覧権限情報を取得する
	 * 
	 * @param isSystemManager ログインユーザーがシステム管理者か否か
	 * @param loginAccountSeq ログインユーザーのアカウント連番
	 * @return ゴミ箱内のすべてのフォルダ閲覧権限情報(削除権限のあるもののみ)
	 */
	@Select
	List<TFolderPermissionInfoManagementEntity> getAllFolderPermissionInfoInTrashBox(boolean isSystemManager, Long loginAccountSeq);

	/**
	 * 1件のフォルダ閲覧権限情報を更新します。
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TFolderPermissionInfoManagementEntity entity);
}
