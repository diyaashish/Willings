package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TAccountVerificationEntity;

/**
 * アカウント認証情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccountVerificationDao {

	/**
	 * 1件のアカウント認証情報を取得する
	 *
	 * @param 取得対象のPK値
	 * @return アカウント認証情報
	 */
	@Select
	TAccountVerificationEntity selectByKey(String key);

	/**
	 * 1件のアカウント認証情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TAccountVerificationEntity entity);

	/**
	 * 1件のアカウント認証情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TAccountVerificationEntity entity);

}
