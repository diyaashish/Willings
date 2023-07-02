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

import jp.loioz.entity.TSalesDetailTaxEntity;

/**
 * 売上明細-消費税用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSalesDetailTaxDao {

	/**
	 * 売上明細SEQに紐づく売上明細-消費税データを取得します
	 * 
	 * @param salesDetailSeq
	 * @return
	 */
	@Select
	List<TSalesDetailTaxEntity> selectSalesDetailTaxBySalesDetailSeq(Long salesDetailSeq);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TSalesDetailTaxEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchInsert
	int[] batchInsert(List<TSalesDetailTaxEntity> entityList);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TSalesDetailTaxEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TSalesDetailTaxEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TSalesDetailTaxEntity> entityList);

	/**
	 * 売上明細-消費税用データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTSalesDetailTax();
	
}
