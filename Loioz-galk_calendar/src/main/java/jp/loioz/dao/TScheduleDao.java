package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TScheduleEntity;

/**
 * 予定情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TScheduleDao {

	/**
	 * 予定SEQを指定して予定情報を取得する
	 *
	 * @param scheduleSeq 予定SEQ
	 * @return 予定情報
	 */
	default TScheduleEntity selectBySeq(Long scheduleSeq) {
		return selectBySeq(Arrays.asList(scheduleSeq)).stream()
				.findFirst()
				.orElse(null);
	}

	/**
	 * 予定SEQを指定して予定情報を取得する
	 *
	 * @param scheduleSeq 予定SEQ
	 * @return 予定情報
	 */
	@Select
	List<TScheduleEntity> selectBySeq(List<Long> scheduleSeqList);

	/**
	 * 顧客IDを指定して予定情報を取得する
	 * 
	 * @param customerId
	 * @return 予定情報
	 */
	@Select
	List<TScheduleEntity> selectByCustomerId(Long customerId);

	/**
	 * 案件IDを指定して予定情報を取得する
	 *
	 * @param scheduleSeq 予定SEQ
	 * @return 予定情報
	 */
	@Select
	List<TScheduleEntity> selectByAnkenId(Long ankenId);

	/**
	 * 裁判期日SEQを指定して予定情報を取得する
	 * 
	 * @param saibanLimitSeq 裁判期日SEQ
	 * @return 予定情報
	 */
	@Select
	TScheduleEntity selectBySaibanLimitSeq(Long saibanLimitSeq);

	/**
	 * 裁判期日SEQを指定して予定情報を取得する
	 *
	 * @param saibanLimitSeqList 裁判期日SEQリスト
	 * @return 予定情報
	 */
	@Select
	List<TScheduleEntity> selectBySaibanLimitSeqList(List<Long> saibanLimitSeqList);

	/**
	 * 顧客IDと案件IDをキーとして、初回面談日の予定を取得します。
	 * 
	 * @param customerId
	 * @param ankenId
	 * @return 初回面談予定
	 */
	@Select
	TScheduleEntity selectByAnkenIdAndCustomerId(Long customerId, Long ankenId);

	/**
	 * 1件の予定情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TScheduleEntity entity);

	/**
	 * 1件の予定情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TScheduleEntity entity);

}
