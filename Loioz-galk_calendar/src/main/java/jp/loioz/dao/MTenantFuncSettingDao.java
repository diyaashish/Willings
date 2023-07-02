package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.MTenantFuncSettingEntity;

/**
 * テナント機能設定Daoクラス
 */
@ConfigAutowireable
@Dao
public interface MTenantFuncSettingDao {

	/**
	 * 全てのテナント機能設定情報を取得する
	 */
	@Select
	List<MTenantFuncSettingEntity> selectAll();
	
	/**
	 * 指定のIDのテナント機能設定情報を取得する
	 * 
	 * @param funcSettingId
	 * @return
	 */
	@Select
	MTenantFuncSettingEntity selectByFuncSettingId(String funcSettingId);
	
	/**
	 * １件のテナント機能設定情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(MTenantFuncSettingEntity entity);
}
