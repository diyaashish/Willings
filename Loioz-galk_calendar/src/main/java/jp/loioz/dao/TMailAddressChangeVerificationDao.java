package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TMailAddressChangeVerificationEntity;

/**
 * メールアドレス変更認証情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TMailAddressChangeVerificationDao {

	/**
	 * 1件のメールアドレス変更認証情報を取得する
	 *
	 * @param 取得対象のPK値
	 * @return メールアドレス変更認証情報
	 */
	@Select
	TMailAddressChangeVerificationEntity selectByKey(String key);

	/**
	 * 1件のメールアドレス変更認証情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TMailAddressChangeVerificationEntity entity);

}
