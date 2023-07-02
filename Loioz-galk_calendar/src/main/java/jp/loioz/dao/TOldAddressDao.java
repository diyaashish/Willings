package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TOldAddressEntity;

/**
 * 旧住所用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TOldAddressDao {

	/**
	 * 1件の旧住所を取得する
	 * 
	 * @param oldAddressSeq
	 * @return
	 */
	@Select
	TOldAddressEntity selectBySeq(Long oldAddressSeq);

	/**
	 * 名簿IDをキーにして旧住所を取得する
	 *
	 * @param personId 名簿ID
	 * @return 旧住所
	 */
	@Select
	List<TOldAddressEntity> selectOldAddressByPersonId(Long personId);

	/**
	 * 1件の旧住所を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TOldAddressEntity entity);

	/**
	 * 1件の旧住所を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TOldAddressEntity entity);

	/**
	 * 1件の旧住所を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TOldAddressEntity entity);

	/**
	 * 複数件の旧住所を削除する
	 * 
	 * @param entityList 削除対象のリスト
	 * @return 削除件数
	 */
	@BatchDelete
	int[] batchDelete(List<TOldAddressEntity> entityList);

}
