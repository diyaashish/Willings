package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.dto.RootFolderInfoDto;
import jp.loioz.entity.TRootFolderRelatedInfoManagementEntity;

@ConfigAutowireable
@Dao
public interface TRootFolderRelatedInfoManagementDao {

	/**
	 * 顧客IDをキーとして、ルートフォルダ関連情報管理を取得します。
	 * 
	 * @param customerId
	 * @return 複数のルートフォルダ関連情報
	 */
	@Select
	TRootFolderRelatedInfoManagementEntity selectByCustomerId(Long customerId);

	/**
	 * 顧客IDをキーとして、ルートフォルダ関連情報管理を取得します。
	 * 
	 * @param customerId
	 * @return 複数のルートフォルダ関連情報
	 */
	@Select
	TRootFolderRelatedInfoManagementEntity selectByAnkenId(Long ankenId);

	/**
	 * ルートフォルダ関連情報管理IDからルートフォルダ関連情報を取得する
	 * 
	 * @param rootFolderRelatedInfoManagementId 取得対象のルートフォルダ関連情報管理ID
	 * @return ルートフォルダ関連情報
	 */
	@Select
	RootFolderInfoDto getRootFolderRelatedInfoByRootFolderRelatedInfoManagementId(Long rootFolderRelatedInfoManagementId);

	/**
	 * １件のルートフォルダ関連情報管理情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TRootFolderRelatedInfoManagementEntity entity);

	/**
	 * 1件の関連情報管理情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TRootFolderRelatedInfoManagementEntity entity);

}
