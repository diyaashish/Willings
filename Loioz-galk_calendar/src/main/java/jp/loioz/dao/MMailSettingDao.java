package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.MMailSettingEntity;

/**
 * MMailSettingDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MMailSettingDao {

	/**
	 * メール設定情報を取得
	 * 
	 * 登録データは必ず1件のみ且つデータは存在していることを想定
	 * データはInsertInitで投入され、登録・削除は行わないため、データは1件のみ
	 * 
	 * @return
	 */
	@Select(ensureResult = true, maxRows = 1)
	MMailSettingEntity select();

	/**
	 * 1件のメール設定情報を更新
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(MMailSettingEntity entity);

}
