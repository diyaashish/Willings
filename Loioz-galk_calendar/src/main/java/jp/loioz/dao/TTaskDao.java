package jp.loioz.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.dto.NumberOfTaskPerAnkenIdBean;
import jp.loioz.dto.TaskCommentCountWorkerBean;
import jp.loioz.dto.TaskCountBean;
import jp.loioz.dto.TaskListBean;
import jp.loioz.entity.TTaskEntity;

/**
 * タスク用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TTaskDao {

	/**
	 * SEQをキーとしてタスク情報を取得する
	 * 
	 * @param seq
	 * @return タスク情報
	 */
	@Select
	TTaskEntity selectBySeq(Long seq);

	/**
	 * 案件IDをキーとしてタスク情報を取得する
	 * 
	 * @param seq
	 * @return タスク情報
	 */
	@Select
	List<TTaskEntity> selectByAnkenId(Long ankenId);

	/**
	 * タスクSEQ、アカウントIDをキーとして、表示用タスク情報を取得
	 * 
	 * @param accountId
	 * @return 1件のBean型タスク情報
	 */
	@Select
	TaskListBean selectBeanBySeqAccountId(Long taskSeq, Long accountId);

	/**
	 * タスクSEQをキーとして、表示用タスク情報を取得<br>
	 * 案件ダッシュボードなどのタスク一覧用<br>
	 * 案件ダッシュボード画面などのタスクはログインユーザが担当しないタスクを表示する場合は、アカウントIDをnullにする。
	 * 
	 * @param accountId
	 * @param taskSeq
	 * @return 1件のBean型タスク情報
	 */
	@Select
	TaskListBean selectBeanBySeq(Long accountId, Long taskSeq);

	/**
	 * アカウントSEQをキーにして、タスクの未読件数を取得します。
	 *
	 * @param accountSeq
	 * @return アカウントSEQ未読件数
	 */
	@Select
	int selectUnreadCount(Long accountSeq);

	/**
	 * アカウントIDに紐づく、下記タスク件数を取得します。順番どおりにListに格納されています<br>
	 * 1.今日のタスク件数<br>
	 * 2.今日のタスクの未読件数<br>
	 * 3.期限付きのタスク件数<br>
	 * 4.期限付きのタスクの未読件数<br>
	 * 5.すべてのタスク件数<br>
	 * 6.すべてのタスクの未読件数<br>
	 * 7.期限を過ぎたタスク件数<br>
	 * 8.期限を過ぎたタスクの未読件数<br>
	 * 9.割り当てられたタスク件数<br>
	 * 10.割り当てられたタスクの未読件数<br>
	 * 11.割り当てたタスク件数<br>
	 * 12.割り当てたタスクの未読件数<br>
	 * 13.完了したタスクの未読件数<br>
	 * 
	 * ※このSQLのWHERE句を変更したときは、下記メソッドで使用しているSQLのWHERE句を同じように変更する。<br>
	 * ・selectTodayTaskByAccountIdCount
	 * ・selectFutureTaskByAccountIdCount
	 * ・selectAllTaskCountByAccountId
	 * ・selectOverdueTaskByAccountIdCount
	 * ・selectAssignedTaskByAccountIdCount
	 * ・selectAssignTaskByAccountIdCount
	 * 
	 * @param accountId
	 * @param date
	 * @param yesterday
	 * @return
	 */
	@Select
	List<TaskCountBean> selectTaskCount(Long accountId, LocalDate date, LocalDate yesterday);

	/**
	 * 案件に紐づく完了していないタスクの数を取得します。<br>
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<NumberOfTaskPerAnkenIdBean> selectTaskCountByAnkenId(List<Long> ankenIdList);

	/**
	 * タスクSEQに紐づくコメント数、作業者、チェックアイテム数、終了したチェックアイテム数を取得<br>
	 * 
	 * @param taskSeqList
	 * @return
	 */
	@Select
	List<TaskCommentCountWorkerBean> selectTaskCommentCountAndWorkerByTaskSeq(List<Long> taskSeqList);

	/**
	 * アカウントIDと検索ワードに紐づくタスクデータを取得<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param accountId
	 * @param searchWord
	 * @param options
	 * @return
	 */
	@Select
	List<TaskListBean> selectSearchResultsTaskByAccountIdAndSearchWord(Long accountId, String searchWord, SelectOptions options);

	/**
	 * アカウントIDと検索ワードに紐づくタスクデータを取得（options無し）
	 * 
	 * @param accountId
	 * @param searchWord
	 * @return
	 */
	@Select
	List<TaskListBean> selectSearchResultsTaskByAccountIdAndSearchWord(Long accountId, String searchWord);

	/**
	 * アカウントIDと検索ワードに紐づくタスク数を取得（options無し）
	 * 
	 * @param accountId
	 * @param searchWord
	 * @return
	 */
	@Select
	int selectSearchResultsTaskByAccountIdAndSearchWordCount(Long accountId, String searchWord);

	/**
	 * アカウントIDと検索ワードに紐づくタスクデータを取得
	 * 
	 * @param accountId
	 * @param searchWord
	 * @return
	 */
	@Select
	List<Long> selectSearchResultsTaskSeqByAccountIdAndSearchWord(Long accountId, String searchWord);

	/**
	 * アカウントIDに紐づく今日のタスクデータを取得<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param accountId
	 * @param date
	 * @param options
	 * @return
	 */
	@Select
	List<TaskListBean> selectTodayTaskByAccountId(Long accountId, LocalDate date, SelectOptions options);

	/**
	 * アカウントIDに紐づく今日のタスクデータを取得（options無し）<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param accountId
	 * @param date
	 * @return
	 */
	@Select
	List<TaskListBean> selectTodayTaskByAccountId(Long accountId, LocalDate date);

	/**
	 * アカウントIDに紐づく今日のタスク数を取得（options無し）<br>
	 * ※このSQLのWHERE句を変更したときは、下記メソッドで使用しているSQLのWHERE句を同じように変更する。<br>
	 * ・selectTaskCount
	 * 
	 * @param accountId
	 * @param date
	 * @return
	 */
	@Select
	int selectTodayTaskByAccountIdCount(Long accountId, LocalDate date);

	/**
	 * アカウントIDに紐づく今日のタスクのタスクSEQを取得
	 * 
	 * @param accountId
	 * @param date
	 * @return
	 */
	@Select
	List<Long> selectTodayTaskSeqByAccountId(Long accountId, LocalDate date);

	/**
	 * アカウントIDに紐づく期限付きのタスクデータを取得<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param accountId
	 * @param yesterday
	 * @return
	 */
	@Select
	List<TaskListBean> selectFutureTaskByAccountId(Long accountId, LocalDate yesterday);

	/**
	 * アカウントIDに紐づく期限付きのタスク数を取得<br>
	 * ※このSQLのWHERE句を変更したときは、下記メソッドで使用しているSQLのWHERE句を同じように変更する。<br>
	 * ・selectTaskCount
	 * 
	 * @param accountId
	 * @param yesterday
	 * @return
	 */
	@Select
	int selectFutureTaskByAccountIdCount(Long accountId, LocalDate yesterday);

	/**
	 * アカウントIDに紐づくすべてのタスクデータを取得<br>
	 *  ・自分が作成者で、割り当てが誰もいないタスク<br>
	 *  ・自分が作成者で、割り当てに自分が含まれるタスク<br>
	 *  
	 * @param accountId
	 * @param sortKeyCd
	 * @param options
	 * @return
	 */
	default List<TaskListBean> selectAllTaskByAccountId(Long accountId, String sortKeyCd, SelectOptions options) {
		return this.selectAllTaskByAccountId(accountId, sortKeyCd, false, null, options);
	}

	/**
	 * アカウントIDに紐づくすべてのタスクデータを取得（options無し）<br>
	 *  ・自分が作成者で、割り当てが誰もいないタスク<br>
	 *  ・自分が作成者で、割り当てに自分が含まれるタスク<br>
	 *  
	 * @param accountId
	 * @param sortKeyCd
	 * @return
	 */
	default List<TaskListBean> selectAllTaskByAccountId(Long accountId, String sortKeyCd) {
		return this.selectAllTaskByAccountId(accountId, sortKeyCd, false, null);
	}

	/**
	 * アカウントID、タスクSEQに紐づくすべてのタスクデータを取得（options無し）<br>
	 * 
	 * @param accountId
	 * @param taskSeq
	 * @return
	 */
	default TaskListBean selectAllTaskByTaskSeq(Long accountId, Long taskSeq) {
		List<TaskListBean> taskList = this.selectAllTaskByAccountId(accountId, null, false, taskSeq);
		if (LoiozCollectionUtils.isNotEmpty(taskList)) {
			return taskList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * アカウントIDに紐づくすべてのタスク件数を取得<br>
	 * ※このSQLのWHERE句を変更したときは、下記メソッドで使用しているSQLのWHERE句を同じように変更する。<br>
	 * ・selectTaskCount
	 * 
	 * @param accountId
	 * @return
	 */
	default int selectAllTaskCountByAccountId(Long accountId) {
		List<TaskListBean> taskListBean = this.selectAllTaskByAccountId(accountId, null, true, null);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(taskListBean)) {
			return 0;
		}

		return taskListBean.get(0).getCount().intValue();
	}

	/**
	 * アカウントIDに紐づくすべてのタスクSEQを取得<br>
	 * 
	 * @param accountId
	 * @param sortKeyCd
	 * @return
	 */
	default List<Long> selectAllTaskSeqByAccountId(Long accountId, String sortKeyCd) {
		List<TaskListBean> taskListBean = this.selectAllTaskByAccountId(accountId, sortKeyCd, false, null);
		if (LoiozCollectionUtils.isEmpty(taskListBean)) {
			return List.of();
		}
		List<Long> taskSeqList = new ArrayList<>();
		for (TaskListBean bean : taskListBean) {
			taskSeqList.add(bean.getTaskSeq());
		}
		return taskSeqList;
	}

	/**
	 * アカウントIDに紐づくすべてのタスクデータを取得<br>
	 * sortKeyCdで並び順を指定<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param accountId
	 * @param sortKeyCd 1:設定順 、2:期限日-昇順、3:期限日-降順
	 * @param countFlg
	 * @param options
	 * @param taskSeq
	 * @return
	 */
	@Select
	List<TaskListBean> selectAllTaskByAccountId(Long accountId, String sortKeyCd, boolean countFlg, Long taskSeq, SelectOptions options);

	/**
	 * アカウントIDに紐づくすべてのタスクデータを取得（options無し）<br>
	 * sortKeyCdで並び順を指定<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param accountId
	 * @param sortKeyCd 1:設定順 、2:期限日-昇順、3:期限日-降順
	 * @param countFlg
	 * @param taskSeq
	 * @return
	 */
	@Select
	List<TaskListBean> selectAllTaskByAccountId(Long accountId, String sortKeyCd, boolean countFlg, Long taskSeq);

	/**
	 * アカウントIDに紐づく期限を過ぎたタスクデータを取得<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param accountId
	 * @param dateTime
	 * @param options
	 * @return
	 */
	@Select
	List<TaskListBean> selectOverdueTaskByAccountId(Long accountId, LocalDate date, SelectOptions options);

	/**
	 * アカウントIDに紐づく期限を過ぎたタスクデータを取得（options無し）<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param accountId
	 * @param dateTime
	 * @return
	 */
	@Select
	List<TaskListBean> selectOverdueTaskByAccountId(Long accountId, LocalDate date);

	/**
	 * アカウントIDに紐づく期限を過ぎたタスク数を取得（options無し）<br>
	 * ※このSQLのWHERE句を変更したときは、下記メソッドで使用しているSQLのWHERE句を同じように変更する。<br>
	 * ・selectTaskCount
	 * 
	 * @param accountId
	 * @param dateTime
	 * @param options
	 * @return
	 */
	@Select
	int selectOverdueTaskByAccountIdCount(Long accountId, LocalDate date);

	/**
	 * アカウントIDに紐づく期限を過ぎたタスクのタスクSEQを取得
	 * 
	 * @param accountId
	 * @return
	 */
	@Select
	List<Long> selectOverdueTaskSeqByAccountId(Long accountId, LocalDate date);

	/**
	 * アカウントIDに紐づく割り当てられたタスクデータを取得<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param accountId
	 * @return
	 */
	@Select
	List<TaskListBean> selectAssignedTaskByAccountId(Long accountId);

	/**
	 * アカウントIDに紐づく割り当てられたタスク数を取得<br>
	 * ※このSQLのWHERE句を変更したときは、下記メソッドで使用しているSQLのWHERE句を同じように変更する。<br>
	 * ・selectTaskCount
	 * 
	 * @param accountId
	 * @return
	 */
	@Select
	int selectAssignedTaskByAccountIdCount(Long accountId);

	/**
	 * アカウントIDに紐づく割り当てたタスクデータを取得<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param accountId
	 * @return
	 */
	@Select
	List<TaskListBean> selectAssignTaskByAccountId(Long accountId);

	/**
	 * アカウントIDに紐づく割り当てたタスク数を取得<br>
	 * ※このSQLのWHERE句を変更したときは、下記メソッドで使用しているSQLのWHERE句を同じように変更する。<br>
	 * ・selectTaskCount
	 * 
	 * @param accountId
	 * @return
	 */
	@Select
	int selectAssignTaskByAccountIdCount(Long accountId);

	/**
	 * アカウントIDに紐づく完了したタスクデータを取得<br>
	 * sortKeyCdで並び順を指定<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param accountId
	 * @param sortKeyCd 1:完了順 、2:期限日-昇順、3:期限日-降順
	 * @param options
	 * @return
	 */
	@Select
	List<TaskListBean> selectCloseTaskByAccountId(Long accountId, String sortKeyCd, SelectOptions options);

	/**
	 * アカウントIDに紐づく完了したタスクデータを取得（options無し）<br>
	 * sortKeyCdで並び順を指定<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param accountId
	 * @param sortKeyCd 1:完了順 、2:期限日-昇順、3:期限日-降順
	 * @return
	 */
	@Select
	List<TaskListBean> selectCloseTaskByAccountId(Long accountId, String sortKeyCd);

	/**
	 * アカウントIDに紐づく完了したタスク数を取得（options無し）
	 * 
	 * @param accountId
	 * @return
	 */
	@Select
	int selectCloseTaskByAccountIdCount(Long accountId);

	/**
	 * アカウントIDに紐づく完了したタスクのタスクSEQを取得<br>
	 * sortKeyCdで並び順を指定<br>
	 * 
	 * @param accountId
	 * @param sortKeyCd 1:完了順 、2:期限日-昇順、3:期限日-降順
	 * @return
	 */
	@Select
	List<Long> selectCloseTaskSeqByAccountId(Long accountId, String sortKeyCd);

	/**
	 * 案件IDに紐づくすべてのタスクデータを取得<br>
	 * 
	 * @param ankenId
	 * @param dispKeyCd
	 * @param sortKeyCd
	 * @param options
	 * @return
	 */
	default List<TaskListBean> selectTaskAnkenByAnkenId(Long ankenId, String dispKeyCd, String sortKeyCd, SelectOptions options) {
		return this.selectTaskAnkenByAnkenId(ankenId, dispKeyCd, sortKeyCd, false, options);
	}

	/**
	 * 案件IDに紐づくすべてのタスクデータを取得（options無し）<br>
	 * 
	 * @param ankenId
	 * @param dispKeyCd
	 * @param sortKeyCd
	 * @return
	 */
	default List<TaskListBean> selectTaskAnkenByAnkenId(Long ankenId, String dispKeyCd, String sortKeyCd) {
		return this.selectTaskAnkenByAnkenId(ankenId, dispKeyCd, sortKeyCd, false);
	}

	/**
	 * 案件IDに紐づくタスク数を取得（options無し）<br>
	 * @param ankenId
	 * @param isCompleteTaskDisp
	 * @return
	 */
	default int selectTaskAnkenCountByAnkenId(Long ankenId, String dispKeyCd) {
		List<TaskListBean> taskListBean = this.selectTaskAnkenByAnkenId(ankenId, dispKeyCd, null, true);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(taskListBean)) {
			return 0;
		}

		return taskListBean.get(0).getCount().intValue();
	}

	/**
	 * 案件IDに紐づくすべてのタスクSEQを取得<br>
	 * 
	 * @param ankenId
	 * @param dispKeyCd
	 * @param sortKeyCd
	 * @return
	 */
	default List<Long> selectTaskAnkenTaskSeqByAnkenId(Long ankenId, String dispKeyCd, String sortKeyCd) {
		List<TaskListBean> taskListBean = this.selectTaskAnkenByAnkenId(ankenId, dispKeyCd, sortKeyCd, false);
		if (LoiozCollectionUtils.isEmpty(taskListBean)) {
			return List.of();
		}
		List<Long> taskSeqList = new ArrayList<>();
		for (TaskListBean bean : taskListBean) {
			taskSeqList.add(bean.getTaskSeq());
		}
		return taskSeqList;
	}

	/**
	 * 案件IDに紐づくすべてのタスクデータを取得<br>
	 * sortKeyCdで並び順を指定<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param ankenId 案件ID
	 * @param dispKeyCd 表示切替コード
	 * @param sortKeyCd ソートコード
	 * @param countFlg タスク件取得フラグ
	 * @param options
	 * @return
	 */
	@Select
	List<TaskListBean> selectTaskAnkenByAnkenId(Long ankenId, String dispKeyCd, String sortKeyCd, boolean countFlg, SelectOptions options);

	/**
	 * 案件IDに紐づくすべてのタスクデータを取得（options無し）<br>
	 * sortKeyCdで並び順を指定<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param ankenId 案件ID
	 * @param dispKeyCd 表示切替コード
	 * @param sortKeyCd ソートコード
	 * @param countFlg タスク件取得フラグ
	 * @return
	 */
	@Select
	List<TaskListBean> selectTaskAnkenByAnkenId(Long ankenId, String dispKeyCd, String sortKeyCd, boolean countFlg);

	/**
	 * アカウントIDが紐づくすべてのタスク + 割り当てられたタスクを取得します。<br>
	 * assignFlg で true を渡すと 割り当てたタスクも取得します。<br>
	 * ソートキーで「設定順」を選択すると「すべてのタスク（表示順でソート）」が先頭になり、その次に「割り当てたタスク」、「割り当てられたタスク」で取得されます。<br>
	 * （割り当てたタスク、割り当てられたタスクに「表示順」が設定されていないため）<br>
	 * 対象件数が多く、SQLが重くなるためコメント数、作業者、チェックアイテム数、終了したチェックアイテム数は取得していません。<br>
	 * 別途 selectTaskCommentCountAndWorkerByTaskSeq から該当タスクSEQ分のコメント数などを取得してください。<br>
	 * 
	 * @param accountId 
	 * @param sortKeyCd 1:設定順 、2:期限日-昇順、3:期限日-降順
	 * @param assignFlg 割り当てたタスクを取得するか
	 * @param taskSeq 
	 * @return
	 */
	@Select
	List<TaskListBean> selectRelatedTaskByAccountId(Long accountId, String sortKeyCd, boolean assignFlg, Long taskSeq);

	/**
	 * アカウントIDが紐づく関連するタスクを取得します。<br>
	 * 
	 * @see TTaskDao#selectRelatedTaskByAccountId(Long, String, boolean, Long)
	 * @param accountId
	 * @param sortKeyCd
	 * @param assignFlg
	 * @return
	 */
	default List<TaskListBean> selectRelatedTaskByAccountId(Long accountId, String sortKeyCd, boolean assignFlg) {
		return this.selectRelatedTaskByAccountId(accountId, sortKeyCd, assignFlg, null);
	}

	/**
	 * アカウントID、タスクSEQに紐づく関連するタスクを取得します。<br>
	 * 
	 * @see TTaskDao#selectRelatedTaskByAccountId(Long, String, boolean, Long)
	 * @param accountId
	 * @param assignFlg
	 * @param taskSeq
	 * @return
	 */
	default TaskListBean selectRelatedTaskByAccountId(Long accountId, boolean assignFlg, Long taskSeq) {
		List<TaskListBean> taskList = this.selectRelatedTaskByAccountId(accountId, null, assignFlg, taskSeq);
		if (LoiozCollectionUtils.isNotEmpty(taskList)) {
			return taskList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TTaskEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TTaskEntity entity);

	/**
	 * 複数更新
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] batchUpdate(List<TTaskEntity> entityList);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TTaskEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TTaskEntity> entityList);
}
