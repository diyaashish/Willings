package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TTempAccountEntity;

/**
 * 一時アカウント情報用のDaoクラス
 */
/**
 * @author P000251
 *
 */
@ConfigAutowireable
@Dao
public interface TTempAccountDao {

	/**
	 * 1件の一時アカウント情報を取得する
	 *
	 * @param 取得対象のPK値
	 * @return 一時アカウント情報のリスト
	 */
	@Select
	TTempAccountEntity selectByKey(String key);

	/**
	 * １件の一時アカウント情報を登録する
	 * 
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TTempAccountEntity entity);

	/**
	 * １件の一時アカウント情報を更新する
	 * 
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TTempAccountEntity entity);

}
