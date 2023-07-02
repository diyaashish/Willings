package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TKeijiAnkenCustomerEntity;

/**
 * 刑事案件-顧客情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TKeijiAnkenCustomerDao {

	/**
	 * 顧客ID,案件IDをキーとして、顧客-案件：刑事情報を取得します
	 * 
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	@Select
	TKeijiAnkenCustomerEntity selectByAnkenIdAndCustomerId(Long ankenId, Long customerId);

	/**
	 * 案件IDをキーにして刑事案件-顧客情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 案件情報
	 */
	@Select
	List<TKeijiAnkenCustomerEntity> selectByAnkenId(Long ankenId);

	/**
	 * 1件の刑事案件-顧客情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TKeijiAnkenCustomerEntity entity);

	/**
	 * 1件の刑事案件-顧客情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TKeijiAnkenCustomerEntity entity);

	/**
	 * 1件の刑事案件-顧客情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TKeijiAnkenCustomerEntity entity);

	/**
	 * 複数件の刑事案件-顧客情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] batchDelete(List<TKeijiAnkenCustomerEntity> entities);
}