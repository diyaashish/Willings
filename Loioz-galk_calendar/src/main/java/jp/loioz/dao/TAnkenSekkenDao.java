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

import jp.loioz.entity.TAnkenSekkenEntity;

/**
 * 案件 - 接見用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAnkenSekkenDao {

	/**
	 * SEQをキーとして、接見情報を取得する。
	 * 
	 * @param sekkenSeq
	 * @return
	 */
	@Select
	TAnkenSekkenEntity selectBySeq(Long sekkenSeq);

	/**
	 * 案件IDをキーとして、案件-接見情報を習得します。
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<TAnkenSekkenEntity> selectByAnkenId(Long ankenId);

	/**
	 * 案件IDと顧客IDをキーにして案件-接見情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return 案件 - 接見
	 */
	@Select
	List<TAnkenSekkenEntity> selectByAnkenIdAndCustomerId(Long ankenId, Long customerId);

	/**
	 * 案件-顧客に紐づく接見情報の件数を取得します
	 *
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	default long selectCountByAnkenIdAndCustomerId(Long ankenId, Long customerId) {
		return Optional.ofNullable(selectByAnkenIdAndCustomerId(ankenId, customerId)).orElseGet(Collections::emptyList).size();
	}

	/**
	 * 案件IDと顧客IDリストをキーにして案件-接見情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @param customerIdList 顧客IDリスト
	 * @return 案件 - 接見
	 */
	@Select
	List<TAnkenSekkenEntity> selectByAnkenIdCustomerIdList(Long ankenId, List<Long> customerIdList);

	/**
	 * 1件の案件-接見情報を登録する
	 * 
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TAnkenSekkenEntity entity);

	/**
	 * 1件の案件-接見情報を更新する
	 * 
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TAnkenSekkenEntity entity);

	/**
	 * 1件の案件-接見情報を削除する
	 * 
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TAnkenSekkenEntity entity);

	/**
	 * 複数件の案件-接見情報を削除する
	 * 
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] delete(List<TAnkenSekkenEntity> entity);
}
