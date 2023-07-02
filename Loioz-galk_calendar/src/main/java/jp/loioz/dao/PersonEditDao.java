package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.CustomerScheduleListBean;

/**
 * 名簿管理画面用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface PersonEditDao {

	/**
	 * 名簿管理画面のスケジュール一覧に表示するスケジュールSEQを取得する
	 * 
	 * @param isAllTimeScheduleList
	 * @param customerId
	 * @param accountSeq
	 * @return
	 */
	@Select
	List<Long> selectSeqByConditions(boolean isAllTimeScheduleList, Long customerId, Long accountSeq);

	/**
	 * スケジュールSEQをキーとして、顧客管理画面のスケジュール一覧情報を取得する
	 * 
	 * @param scheduleSeqList
	 * @return
	 */
	@Select
	List<CustomerScheduleListBean> selectScheduleListBean(List<Long> scheduleSeqList);
}
