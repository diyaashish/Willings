package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.domain.condition.KanyoshaModalCustomerSearchCondition;
import jp.loioz.entity.TPersonEntity;

/**
 * 共通関与者モーダル内で利用するDaoクラス
 */
@ConfigAutowireable
@Dao
public interface KanyoshaModalDao {

	/**
	 * 検索条件から顧客情報を取得する
	 * 
	 * @param condition
	 * @return
	 */
	@Select
	List<TPersonEntity> selectPersonBySearchConditions(KanyoshaModalCustomerSearchCondition condition);

}
