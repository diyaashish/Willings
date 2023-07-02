package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TPaymentCardEntity;

/**
 * 支払いカードのDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TPaymentCardDao {

	/**
	 * 1件の支払いカード情報を取得する
	 * 
	 * @return
	 */
	@Select
	TPaymentCardEntity selectOne();
	
	/**
	 * 1件の支払いカードを登録する
	 *
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TPaymentCardEntity entity);
	
	/**
	 * 1件の支払いカードを更新する
	 * 
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TPaymentCardEntity entity);
	
	/**
	 * 1件の支払いカードを削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TPaymentCardEntity entity);
}
