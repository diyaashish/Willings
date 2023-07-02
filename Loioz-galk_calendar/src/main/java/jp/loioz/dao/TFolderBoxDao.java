package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TFolderBoxEntity;

/**
 * BoxRootFolder管理用Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TFolderBoxDao {

	/**
	 * Boxの顧客フォルダ情報を取得する
	 * 
	 * @param customerId
	 * @return
	 */
	@Select
	TFolderBoxEntity selectEnabledByCustomerId(Long customerId);

	/**
	 * Boxの顧客フォルダ(ルート)情報を取得する
	 * 
	 * @return
	 */
	@Select
	TFolderBoxEntity selectEnabledByFindCustomerId(Long customerId);

	/**
	 * Boxの案件フォルダ情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	TFolderBoxEntity selectEnabledByAnkenId(Long ankenId);

	/**
	 * Boxの案件フォルダ(ルート)情報を取得する
	 * 
	 * @return
	 */
	@Select
	TFolderBoxEntity selectEnabledByFindAnkenId(Long ankenId);

	/**
	 * Boxの案件フォルダ情報を取得する
	 * 
	 * @param idFrom
	 * @param idTo
	 * @return
	 */
	@Select
	List<TFolderBoxEntity> selectEnabledByFromToAnkenId(Long idFrom, Long idTo);

	/**
	 * Boxの顧客フォルダ情報を取得する
	 * 
	 * @param idFrom
	 * @param idTo
	 * @return
	 */
	@Select
	List<TFolderBoxEntity> selectEnabledByFromToCustomerId(Long idFrom, Long idTo);

	/**
	 * Boxのフォルダーの登録処理
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TFolderBoxEntity entity);

	/**
	 * Boxのフォルダーの削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TFolderBoxEntity entity);

	/**
	 * Boxのフォルダーの削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@BatchDelete
	int[] delete(List<TFolderBoxEntity> entity);

}
