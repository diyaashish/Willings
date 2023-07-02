package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TFolderGoogleEntity;

/**
 * GoogleRootFolder管理用Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TFolderGoogleDao {

	/**
	 * Googleの顧客フォルダ情報を取得する
	 * 
	 * @param customerId
	 * @return
	 */
	@Select
	TFolderGoogleEntity selectEnabledByCustomerId(Long customerId);

	/**
	 * Googleの顧客フォルダ(ルート)情報を取得する
	 * 
	 * @return
	 */
	@Select
	TFolderGoogleEntity selectEnabledByFindCustomerId(Long customerId);

	/**
	 * Googleの案件フォルダ情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	TFolderGoogleEntity selectEnabledByAnkenId(Long ankenId);

	/**
	 * Googleの案件フォルダ(ルート)情報を取得する
	 * 
	 * @return
	 */
	@Select
	TFolderGoogleEntity selectEnabledByFindAnkenId(Long ankenId);

	/**
	 * Googleの案件フォルダ情報を取得する
	 * 
	 * @param idFrom
	 * @param idTo
	 * @return
	 */
	@Select
	List<TFolderGoogleEntity> selectEnabledByFromToAnkenId(Long idFrom, Long idTo);

	/**
	 * Googleの顧客フォルダ情報を取得する
	 * 
	 * @param idFrom
	 * @param idTo
	 * @return
	 */
	@Select
	List<TFolderGoogleEntity> selectEnabledByFromToCustomerId(Long idFrom, Long idTo);

	/**
	 * Googleのフォルダーの登録処理
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TFolderGoogleEntity entity);

	/**
	 * Googleのフォルダーの削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TFolderGoogleEntity entity);

	/**
	 * Googleのフォルダーの削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@BatchDelete
	int[] delete(List<TFolderGoogleEntity> entity);

}
