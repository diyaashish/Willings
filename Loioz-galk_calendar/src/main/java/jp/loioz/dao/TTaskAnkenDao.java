package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TTaskAnkenEntity;

/**
 * 案件タスクDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TTaskAnkenDao {

	/**
	 * 案件タスク追加データを取得します。<br>
	 * 
	 * @param accountSeq
	 * @return
	 */
	@Select
	List<TTaskAnkenEntity> selectTaskAnkenByAccountSeq(Long accountSeq);

	/**
	 * 案件タスク追加データを取得します。<br>
	 * 
	 * @param accountSeq
	 * @param ankenId（null可）
	 * @return
	 */
	@Select
	List<TTaskAnkenEntity> selectTaskAnkenByAccountSeqAnkenId(Long accountSeq, Long ankenId);

	/**
	 * パラメーターアカウントの案件タスク表示順最大値を取得します。<br>
	 * @param accountSeq
	 * @return
	 */
	@Select
	int selectMaxDispOrderByAccountSeq(Long accountSeq);

	/**
	 * 1件の案件タスク情報を登録します。
	 * 
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TTaskAnkenEntity entity);

	/**
	 * 1件の案件タスク情報を更新します。
	 * 
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TTaskAnkenEntity entity);

	/**
	 * 1件の案件タスク情報を削除します。
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TTaskAnkenEntity entity);

	/**
	 * 複数の案件タスク情報を削除します。
	 * 
	 * @param entity
	 * @return
	 */
	@BatchDelete
	int[] delete(List<TTaskAnkenEntity> entity);

}
