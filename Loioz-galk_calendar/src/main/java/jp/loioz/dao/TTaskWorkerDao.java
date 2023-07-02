package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TTaskWorkerEntity;

@ConfigAutowireable
@Dao
public interface TTaskWorkerDao {

	/**
	 * タスク-作業者SEQをキーとして、タスク-作業者情報を取得します。
	 * 
	 * @param seq
	 * @return １件のタスク-作業者情報
	 */
	@Select
	TTaskWorkerEntity selectBySeq(Long taskWorkerSeq);

	/**
	 * タスク-作業者SEQをキーとして、複数件のタスク-作業者情報を取得します。
	 * 
	 * @param seq
	 * @return １件のタスク-作業者情報
	 */
	@Select
	List<TTaskWorkerEntity> selectBySeqList(List<Long> taskWorkerSeqList);

	/**
	 * タスクSEQをキーとして、複数件のタスク-履歴情報を取得します。
	 * 
	 * @param taskSeqList
	 * @return 取得結果
	 */
	@Select
	List<TTaskWorkerEntity> selectByTaskSeqList(List<Long> taskSeqList);

	/**
	 * 親テーブルのSEQとアカウントSEQをキーとして、 タスク-作業者情報を取得します。
	 * 
	 * @param taskSeq
	 * @param accountSeq
	 * @return １件のタスク-作業者情報
	 */
	@Select
	TTaskWorkerEntity selectByParentSeqAndAccountSeq(Long taskSeq, Long accountSeq);

	/**
	 * 親テーブルのSEQと作業者SEQのリストをキーとして、 タスク-作業者情報を取得します。
	 * 
	 * @param taskSeq
	 * @param accountSeq
	 * @return １件のタスク-作業者情報
	 */
	@Select
	List<TTaskWorkerEntity> selectByParentSeqAndWorkerSeqList(Long taskSeq, List<Long> workerSeqList);

	/**
	 * アカウントSEQをキーとして、 そのひとの表示順の最大値を取得します。
	 * 
	 * @param accountSeq
	 * @return
	 */
	@Select
	int selectMaxDispOrderByAccountSeq(Long accountSeq);

	/**
	 * アカウントSEQをキーとして、 そのひとの今日やるタスクの表示順の最大値を取得します。
	 * 
	 * @param accountSeq
	 * @return
	 */
	@Select
	int selectMaxTodayTaskDispOrderByAccountSeq(Long accountSeq);

	/**
	 * アカウントSEQとタスク-作業者SEQをキーとして、パラメータで渡したタスクの中で表示順の最小値を取得します。
	 * 
	 * @param accountSeq
	 * @param taskWorkerSeqList
	 * @return
	 */
	@Select
	int selectMinDispOrderByTaskWorkerSeq(Long accountSeq, List<String> taskWorkerSeqList);

	/**
	 * アカウントSEQとタスク-作業者SEQをキーとして、 パラメータで渡したタスクの中で今日やるタスクの表示順の最小値を取得します。
	 * 
	 * @param accountSeq
	 * @param taskWorkerSeqList
	 * @return
	 */
	@Select
	int selectMinTodayTaskDispOrderByTaskWorkerSeq(Long accountSeq, List<String> taskWorkerSeqList);

	/**
	 * 親テーブルのSEQをキーとして、タスク-作業者情報を取得する
	 * 
	 * @param taskSeq
	 * @return
	 */
	@Select
	List<TTaskWorkerEntity> selectByParentSeq(Long taskSeq);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TTaskWorkerEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entityList
	 * @return 登録結果
	 */
	@BatchInsert
	int[] insert(List<TTaskWorkerEntity> entityList);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] delete(List<TTaskWorkerEntity> entityList);

}
