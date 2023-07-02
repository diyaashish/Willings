package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TScheduleAccountEntity;

/**
 * 予定-参加者用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TScheduleAccountDao {

	/**
	 * 予定SEQを指定して予定-参加者を取得する
	 *
	 * @param scheduleSeqList 予定SEQ
	 * @return 予定-参加者
	 */
	@Select
	List<TScheduleAccountEntity> selectByScheduleSeq(List<Long> scheduleSeqList);

	/**
	 * 予定SEQを指定して予定-参加者を取得する
	 *
	 * @param scheduleSeq 予定SEQ
	 * @return 予定-参加者
	 */
	default List<TScheduleAccountEntity> selectByScheduleSeq(Long scheduleSeq) {
		return selectByScheduleSeq(Arrays.asList(scheduleSeq));
	}

	/**
	 * 1件の予定-参加者を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TScheduleAccountEntity entity);

	/**
	 * 1件の予定-参加者を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TScheduleAccountEntity entity);

}
