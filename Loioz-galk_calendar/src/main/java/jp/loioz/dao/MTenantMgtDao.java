package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.MTenantMgtEntity;

/**
 * テナント管理用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MTenantMgtDao {

	/**
	 * テナント連番から1件のテナント管理を取得する
	 *
	 * @param tenantSeq 取得対象のテナント連番
	 * @return テナント管理
	 */
	@Select
	MTenantMgtEntity selectBySeq(Long tenantSeq);

	/**
	 * サブドメイン名から1件のテナント管理を取得する
	 *
	 * @param subDomain 取得対象のサブドメイン名
	 * @return テナント管理
	 */
	@Select
	MTenantMgtEntity selectBySubDomain(String subDomain);

	/**
	 * １件のテナント管理を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(MTenantMgtEntity entity);

	/**
	 * １件のテナント管理を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(MTenantMgtEntity entity);

	/**
	 * １件のテナント管理を物理削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(MTenantMgtEntity entity);
}
