package jp.loioz.dao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TAnkenKoryuEntity;

/**
 * 案件 - 勾留用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAnkenKoryuDao {

	/**
	 * SEQをキーにして案件-勾留情報を取得する
	 * 
	 * @param koryuSeq
	 * @return
	 */
	@Select
	TAnkenKoryuEntity selectBySeq(Long koryuSeq);

	/**
	 * 案件IDをキーにして案件-勾留情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 案件 - 勾留
	 */
	@Select
	List<TAnkenKoryuEntity> selectByAnkenId(Long ankenId);

	/**
	 * 案件IDと顧客IDをキーにして案件-勾留情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return 案件 - 勾留
	 */
	@Select
	List<TAnkenKoryuEntity> selectByAnkenIdAndCustomerId(Long ankenId, Long customerId);

	/**
	 * 案件-顧客に紐づく勾留情報の件数を取得します
	 * 
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	default long selectCountByAnkenIdAndCustomerId(Long ankenId, Long customerId) {
		return Optional.ofNullable(selectByAnkenIdAndCustomerId(ankenId, customerId)).orElseGet(Collections::emptyList).size();
	}

	/**
	 * 案件IDと顧客IDをキーにして、勾留情報を取得する
	 *
	 * @param ankenId
	 * @param customerIdList
	 * @return
	 */
	@Select
	List<TAnkenKoryuEntity> selectByAnkenIdCustomerIdList(Long ankenId, List<Long> customerIdList);

	/**
	 * 1件の案件-勾留情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TAnkenKoryuEntity entity);

	/**
	 * 1件の案件-勾留情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TAnkenKoryuEntity entity);

	/**
	 * 1件の案件-勾留情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TAnkenKoryuEntity entity);

	/**
	 * 複数件の案件-勾留情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] delete(List<TAnkenKoryuEntity> entity);
}
