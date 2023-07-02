package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.MPlanFuncRestrictEntity;

/**
 * プラン別機能制限のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MPlanFuncRestrictDao {

	/**
	 * すべてのプラン別機能制限情報を取得する
	 * 
	 * @return
	 */
	@Select
	List<MPlanFuncRestrictEntity> selectAll();
}
