package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TFileConfigurationManagementEntity;

@ConfigAutowireable
@Dao
public interface TFileConfigurationManagementDao {

	/**
	 * １件のファイル構成管理情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TFileConfigurationManagementEntity entity);

	/**
	 * ルートフォルダ関連情報管理IDをキーとしてファイル構成管理情報を取得する。
	 *
	 * @param rootFolderRelatedInfoManagementId
	 * @return ファイル構成管理情報リスト
	 */
	@Select
	List<TFileConfigurationManagementEntity> selectByRootFolderRelatedInfoManagementId(Long rootFolderRelatedInfoManagementId);

	/**
	 * 同フォルダ階層内でアップロード対象のフォルダ名に一致するレコードのファイル構成管理IDを取得する
	 *
	 * @param fileName 作成対象のフォルダ名
	 * @param fileExtension 拡張子
	 * @param folderPath 検索対象のフォルダパス(ルートフォルダ名以降のパス)
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @param fileKubun 検索対象のファイル区分
	 * @return ファイル構成管理ID
	 */
	@Select
	Long getFileConfigurationManagementIdMatchInFolderNameInSomeFolderHierarchy(String fileName, String fileExtension, String folderPath,
			Long rootFolderRelatedInfoManagementId, String fileKubun);

	/**
	 * ルートフォルダのファイル構成管理IDを取得する
	 *
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @return ファイル構成管理ID
	 */
	@Select
	Long selectFileConfigurationManagementIdOfRootFolder(Long rootFolderRelatedInfoManagementId);

	/**
	 * ファイル構成管理IDからファイル区分を取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return ファイル区分
	 */
	@Select
	String getFileKubunByFileConfigurationManagementId(Long fileConfigurationManagementId);

	/**
	 * ファイル構成管理IDからファイル構成管理情報を取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return ファイル構成管理情報
	 */
	@Select
	TFileConfigurationManagementEntity getTFileConfigurationManagementInfoByFileConfigurationManagementId(Long fileConfigurationManagementId);

	/**
	 * 1件のファイル構成管理情報を削除する
	 *
	 * @param entity 削除対象
	 * @return 削除件数
	 */
	@Delete
	int delete(TFileConfigurationManagementEntity entity);

	/**
	 * ゴミ箱に入っているファイルの中から、対象のフォルダ配下のファイル・フォルダのファイル構成管理情報を取得する
	 *
	 * @param folderPath 配下のフォルダパス(LIKE検索で使用)
	 * @param rootFolderRelatedInfoManagementId 対象のルートフォルダ関連情報管理ID
	 * @return 該当するファイル構成管理情報リスト
	 */
	@Select
	List<TFileConfigurationManagementEntity> getTargetTFileConfigManagementInfoUnderFolderInTrashBox(String folderPath,
			Long rootFolderRelatedInfoManagementId, Long fileConfigurationManagementId);

	/**
	 * 複数件のファイル構成管理情報を削除する
	 *
	 * @param entityList 削除対象のファイル構成管理情報
	 * @return 削除件数
	 */
	@BatchDelete
	int[] batchDelete(List<TFileConfigurationManagementEntity> entityList);

	/**
	 * ゴミ箱内の全てのファイル構成管理情報を取得する
	 *
	 * @param isSystemManager ログインユーザーがシステム管理者か否か
	 * @param loginAccountSeq ログインユーザーのアカウント連番
	 * @return ゴミ箱内の全てのファイル構成管理情報(削除権限のあるもののみ)
	 */
	@Select
	List<TFileConfigurationManagementEntity> getTFileConfigurationManagementInfoInTrashBox(boolean isSystemManager, Long loginAccountSeq);

	/**
	 * 1件のファイル構成管理情報を更新する
	 *
	 * @param tFileConfigurationManagementEntity 更新対象のファイル構成管理情報
	 * @return 更新件数
	 */
	@Update
	int update(TFileConfigurationManagementEntity tFileConfigurationManagementEntity);

	/**
	 * ゴミ箱内に存在する1件のファイル構成管理情報を取得する
	 *
	 * @param fileConfigurationManagementId 取得対象のファイル構成管理ID
	 * @return ファイル構成管理情報
	 */
	@Select
	TFileConfigurationManagementEntity
			getTFileConfigurationManagementInfoByFileConfigurationManagementIdInTrashBox(Long fileConfigurationManagementId);

	/**
	 * ファイル名とフォルダパスから、ファイル構成管理IDを取得する
	 *
	 * @param fileName ファイル名
	 * @param fileExtension 拡張子
	 * @param folderPath フォルダパス
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @return ファイル構成管理ID
	 */
	@Select
	Long getTFileConfigureationIdByFileNameAndFolderPath(String fileName, String fileExtension, String folderPath, Long rootFolderRelatedInfoManagementId);

	/**
	 * テナントが利用しているファイル容量の合計値を取得
	 *
	 * @return 使用ファイル容量
	 */
	@Select
	Long getUsingFileStrage();

}