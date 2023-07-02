package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TFolderDropboxEntity;

/**
 * DropboxFolder管理用Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TFolderDropboxDao {

	/**
	 * Dropboxの顧客フォルダ情報を取得する
	 * 
	 * @param customerId
	 * @return
	 */
	@Select
	TFolderDropboxEntity selectEnabledByCustomerId(Long customerId);

	/**
	 * Dropboxの顧客フォルダ(ルート)情報を取得する
	 * 
	 * @return
	 */
	@Select
	TFolderDropboxEntity selectEnabledByFindCustomerId(Long customerId);

	/**
	 * Dropboxの案件フォルダ情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	TFolderDropboxEntity selectEnabledByAnkenId(Long ankenId);

	/**
	 * Dropboxの案件フォルダ(ルート)情報を取得する
	 * 
	 * @return
	 */
	@Select
	TFolderDropboxEntity selectEnabledByFindAnkenId(Long ankenId);

	/**
	 * Dropboxの案件フォルダ情報を取得する
	 * 
	 * @param idFrom
	 * @param idTo
	 * @return
	 */
	@Select
	List<TFolderDropboxEntity> selectEnabledByFromToAnkenId(Long idFrom, Long idTo);

	/**
	 * Dropboxの顧客フォルダ情報を取得する
	 * 
	 * @param idFrom
	 * @param idTo
	 * @return
	 */
	@Select
	List<TFolderDropboxEntity> selectEnabledByFromToCustomerId(Long idFrom, Long idTo);

	/**
	 * Dropboxのフォルダーの登録処理
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TFolderDropboxEntity entity);

	/**
	 * Dropboxのフォルダーの削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TFolderDropboxEntity entity);

	/**
	 * Dropboxのフォルダーの削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@BatchDelete
	int[] delete(List<TFolderDropboxEntity> entity);

}
