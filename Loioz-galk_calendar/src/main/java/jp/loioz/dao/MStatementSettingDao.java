package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.MStatementSettingEntity;

/**
 * 精算書設定マスタのDaoクラス
 * 
 * データはInsertInitで投入され、登録・削除は行わない。
 * ⇒ Insert, Delete処理は追加しないこと
 */
@ConfigAutowireable
@Dao
public interface MStatementSettingDao {

	/**
	 * 精算書設定情報を取得
	 * 
	 * 登録データは必ず1件のみ且つデータは存在していることを想定
	 * データはInsertInitで投入され、登録・削除は行わないため、データは1件のみ
	 * 
	 * @return
	 */
	@Select(ensureResult = true, maxRows = 1)
	MStatementSettingEntity select();

	/**
	 * １件の請求書設定情報を更新
	 *
	 * @param MStatementSettingEntity 精算書設定マスタを管理するEntity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(MStatementSettingEntity entity);
}
