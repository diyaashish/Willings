package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.MLoiozAdminControlEntity;

/**
 * ロイオズ管理者制御Daoクラス
 */
@ConfigAutowireable
@Dao
public interface MLoiozAdminControlDao {

	/**
	 * 全てのロイオズ管理者制御情報を取得する
	 */
	@Select
	List<MLoiozAdminControlEntity> selectAll();
}
