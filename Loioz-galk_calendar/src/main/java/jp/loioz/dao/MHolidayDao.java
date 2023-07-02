package jp.loioz.dao;

import java.time.LocalDate;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.MHolidayEntity;

/**
 * 祝日マスタ用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MHolidayDao {

	/**
	 * 日付範囲内の祝日データを取得
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	@Select
	List<MHolidayEntity> selectByFromTo(LocalDate from, LocalDate to);

}
