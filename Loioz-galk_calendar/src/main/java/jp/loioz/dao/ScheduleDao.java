package jp.loioz.dao;

import java.time.LocalDate;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.domain.condition.ScheduleSearchCondition;
import jp.loioz.entity.TScheduleEntity;

/**
 * 予定表画面用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface ScheduleDao {

	/**
	 * 日付範囲を指定して予定情報を取得する
	 *
	 * @param dateFrom 日付From
	 * @param dateTo 日付To
	 * @return 予定情報
	 */
	@Select
	List<TScheduleEntity> selectScheduleByDateRange(LocalDate dateFrom, LocalDate dateTo);

	/**
	 * 検索条件を指定して、予定に紐づくアカウントSEQを取得する
	 *
	 * @param condition 検索条件
	 * @return アカウントSEQ
	 */
	@Select
	List<Long> selectAccountScheduleAvailability(ScheduleSearchCondition condition);

	/**
	 * 日付範囲を指定して会議室の予定情報を取得する
	 *
	 * @param dateFrom 日付From
	 * @param dateTo 日付To
	 * @return 予定情報
	 */
	@Select
	List<TScheduleEntity> selectRoomScheduleByDateRange(LocalDate dateFrom, LocalDate dateTo);

	/**
	 * 検索条件を指定して会議室予約状況を取得する
	 *
	 * @param condition 検索条件
	 * @return 予定情報
	 */
	@Select
	List<TScheduleEntity> selectRoomAvailability(ScheduleSearchCondition condition);

}