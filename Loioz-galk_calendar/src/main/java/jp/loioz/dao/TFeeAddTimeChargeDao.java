package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TFeeAddTimeChargeEntity;

/**
 * タイムチャージ設定用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TFeeAddTimeChargeDao {

	/**
	 * タイムチャージ設定データを取得します
	 * 
	 * @param feeSeq
	 * @return
	 */
	@Select
	TFeeAddTimeChargeEntity selectTimeChargeSettingByFeeSeq(Long feeSeq);

	/**
	 * タイムチャージ設定データを取得します
	 * 
	 * @param feeSeqList
	 * @return
	 */
	@Select
	List<TFeeAddTimeChargeEntity> selectTimeChargeSettingByFeeSeqList(List<Long> feeSeqList);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TFeeAddTimeChargeEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchInsert
	int[] batchInsert(List<TFeeAddTimeChargeEntity> entityList);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TFeeAddTimeChargeEntity entity);

	/**
	 * 複数更新
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] batchUpdate(List<TFeeAddTimeChargeEntity> entityList);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TFeeAddTimeChargeEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TFeeAddTimeChargeEntity> entityList);
	
	/**
	 * タイムチャージ設定データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTFeeAddTimeCharge();

}
