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

import jp.loioz.entity.TAnkenJikenEntity;

/**
 * 案件 - 事件用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAnkenJikenDao {

	/**
	 * 事件SEQをキーにして事件情報を取得する
	 *
	 * @param jikenSeq
	 * @return 事件情報
	 */
	@Select
	TAnkenJikenEntity selectBySeq(Long jikenSeq);

	/**
	 * 案件IDをキーにして案件-事件情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 案件 - 事件
	 */
	@Select
	List<TAnkenJikenEntity> selectByAnkenId(Long ankenId);

	/**
	 * 案件ID、顧客IDをキーにして案件-事件情報を取得する
	 *
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	@Select
	List<TAnkenJikenEntity> selectByAnkenIdCustomerId(Long ankenId, Long customerId);

	/**
	 * 案件ID、顧客IDリストをキーにして案件-事件情報を取得する
	 *
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	@Select
	List<TAnkenJikenEntity> selectByAnkenIdCustomerIdList(Long ankenId, List<Long> customerIdList);

	/**
	 * 案件-顧客に紐づく事件情報の件数を取得します
	 * 
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	default long selectCountByAnkenIdAndCustomerId(Long ankenId, Long customerId) {
		return Optional.ofNullable(selectByAnkenIdCustomerId(ankenId, customerId)).orElseGet(Collections::emptyList).size();
	}

	/**
	 * 1件の案件-事件情報を登録する
	 * 
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TAnkenJikenEntity entity);

	/**
	 * 1件の案件-事件情報を更新する
	 * 
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TAnkenJikenEntity entity);

	/**
	 * 1件の案件-事件情報を削除する
	 * 
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TAnkenJikenEntity entity);

	/**
	 * 複数件の案件-事件情報を削除する
	 * 
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] delete(List<TAnkenJikenEntity> entity);
}
