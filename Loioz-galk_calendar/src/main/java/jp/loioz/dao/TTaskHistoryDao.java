package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TTaskHistoryEntity;

@ConfigAutowireable
@Dao
public interface TTaskHistoryDao {

	/**
	 * タスク-履歴SEQをキーとして、一件のタスク-履歴情報を取得します。
	 * 
	 * @param taskHistorySeq
	 * @return 取得結果
	 */
	@Select
	TTaskHistoryEntity selectBySeq(Long taskHistorySeq);

	/**
	 * タスクSEQをキーとして、複数件のタスク-履歴情報を取得します。
	 * 
	 * @param taskSeqList
	 * @return 取得結果
	 */
	@Select
	List<TTaskHistoryEntity> selectByTaskSeqList(List<Long> taskSeqList);

	/**
	 * タスクSEQ(親テーブル)をキーとして、紐づくタスク-履歴情報を全て取得します。
	 * 
	 * @param taskSeq
	 * @return 取得結果
	 */
	@Select
	List<TTaskHistoryEntity> selectByParentSeq(Long taskSeq);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TTaskHistoryEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TTaskHistoryEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TTaskHistoryEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entityList
	 * @return 登録結果
	 */
	@BatchInsert
	int[] insert(List<TTaskHistoryEntity> entityList);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] delete(List<TTaskHistoryEntity> entityList);
}
