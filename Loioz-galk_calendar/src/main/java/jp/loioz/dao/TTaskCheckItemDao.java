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

import jp.loioz.entity.TTaskCheckItemEntity;

/**
 * タスク-チェックアイテム用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TTaskCheckItemDao {

	/**
	 * タスク-チェック項目SEQに該当するチェックリスト情報を取得
	 * 
	 * @param taskCheckItemSeq
	 * @return
	 */
	@Select
	TTaskCheckItemEntity selectBySeq(Long taskCheckItemSeq);

	/**
	 * タスクSEQに関するチェックリスト情報を取得
	 * 
	 * @param taskSeq
	 * @return
	 */
	@Select
	List<TTaskCheckItemEntity> selectByTaskSeq(Long taskSeq);

	/**
	 * タスクSEQに関するチェックリストの中で最大表示順を取得
	 * 
	 * @param taskSeq
	 * @return 
	 */
	@Select
	long selectMaxCheckItemDispOrderByTaskSeq(Long taskSeq);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TTaskCheckItemEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entityList
	 * @return 登録結果
	 */
	@BatchInsert
	int[] insert(List<TTaskCheckItemEntity> entityList);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TTaskCheckItemEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TTaskCheckItemEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TTaskCheckItemEntity> entityList);
}
