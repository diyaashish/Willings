package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.TenantDetailKozaBean;
import jp.loioz.entity.MTenantEntity;

/**
 * テナント用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MTenantDao {

	/**
	 * テナント情報を取得する
	 *
	 * @param tenantSeq
	 * @return
	 */
	@Select
	MTenantEntity selectBySeq(Long tenantSeq);

	/**
	 * テナント情報＋口座情報（既定ありもしくは、1件目）を取得する
	 *
	 * @param tenantSeq
	 * @return
	 */
	@Select
	TenantDetailKozaBean selectTenantDetailAndKozaBySeq(Long tenantSeq);

	/**
	 * １件のテンプレート情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(MTenantEntity entity);

	/**
	 * １件のテンプレート情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(MTenantEntity entity);

}
