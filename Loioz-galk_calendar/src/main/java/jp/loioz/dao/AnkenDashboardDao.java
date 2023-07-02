package jp.loioz.dao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AnkenDashGyomuHistoryBean;
import jp.loioz.bean.AnkenDashScheduleBean;
import jp.loioz.bean.AnkenDashTaskBean;

/**
 * 案件ダッシュボードのDaoクラス
 */
@ConfigAutowireable
@Dao
public interface AnkenDashboardDao {

	/**
	 * 案件ダッシュボード：タスク情報の取得
	 * 
	 * @param isAllDispTaskList
	 * @param ankenId
	 * @return
	 */
	@Select
	List<AnkenDashTaskBean> selectTaskBean(boolean isAllDispTaskList, Long ankenId);

	/**
	 * 案件ダッシュボード：タスク情報の期日前件数を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	default long noCompCount(Long ankenId) {
		return Optional.ofNullable(selectTaskBean(false, ankenId)).orElse(Collections.emptyList())
				.stream().map(AnkenDashTaskBean::getTaskSeq).distinct().count();
	}

	/**
	 * 案件ダッシュボード：スケジュール情報の取得
	 *
	 * @param isAllTimeScheduleList true：全ての時間の予定を取得候補とする false：本日以降の予定のみを取得候補とする（本日は含む）
	 * @param isOnlyKijituScheduleList true:裁判期日の予定のみを取得候補とする false:全ての予定を取得候補とする
	 * @param ankenId
	 * @param accountSeq 閲覧権限の有無で使用 ※NULLの場合は、権限に関係なく取得
	 * @return
	 */
	@Select
	List<AnkenDashScheduleBean> selectScheduleBean(boolean isAllTimeScheduleList, boolean isOnlyKijituScheduleList, Long ankenId, Long accountSeq);

	/**
	 * 案件ダッシュボード：スケジュール情報の期日前件数を取得する
	 * 
	 * @param ankenId
	 * @param accountSeq 閲覧権限の有無で使用 ※NULLの場合は、権限に関係なく取得
	 * @return
	 */
	default long beforeKijituCount(Long ankenId, Long accountSeq) {
		return Optional.ofNullable(selectScheduleBean(false, true, ankenId, accountSeq)).orElse(Collections.emptyList())
				.stream().map(AnkenDashScheduleBean::getScheduleSeq).distinct().count();
	}

	/**
	 * 案件ダッシュボード：業務履歴情報の取得
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<AnkenDashGyomuHistoryBean> selectGyomuHistoryBean(Long ankenId);

}
