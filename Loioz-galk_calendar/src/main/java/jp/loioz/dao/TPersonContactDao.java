package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TPersonContactEntity;

/**
 * 名簿連絡先用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TPersonContactDao {

	/**
	 * 1件の名簿連絡先を取得する
	 * 
	 * @param contactSeq
	 * @return
	 */
	@Select
	TPersonContactEntity selectBySeq(Long contactSeq);

	/**
	 * 名簿IDをキーにして名簿連絡先を取得する
	 *
	 * @param personId 名簿ID
	 * @return 名簿連絡先
	 */
	@Select
	List<TPersonContactEntity> selectPersonContactByPersonId(Long personId);

	/**
	 * 名簿IDをキーにして名簿連絡先を取得する
	 * 
	 * @param personIdList
	 * @return 名簿連絡先
	 */
	@Select
	List<TPersonContactEntity> selectPersonContactByPersonIdList(List<Long> personIdList);

	/**
	 * 顧客IDをキーとして、名簿連絡先を取得する
	 *
	 * @param customerId
	 * @return
	 */
	@Select
	List<TPersonContactEntity> selectPersonContactByCustomerId(List<Long> customerId);

	/**
	 * 1件の名簿連絡先を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TPersonContactEntity entity);

	/**
	 * 1件の名簿連絡先を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TPersonContactEntity entity);

	/**
	 * 複数件の名簿連絡先を更新する
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] update(List<TPersonContactEntity> entityList);

	/**
	 * 1件の名簿連絡先を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TPersonContactEntity entity);

	/**
	 * 複数件の名簿連絡先を削除する
	 *
	 * @param entityList
	 * @return 削除件数
	 */
	@BatchDelete
	int[] delete(List<TPersonContactEntity> entityList);
}
