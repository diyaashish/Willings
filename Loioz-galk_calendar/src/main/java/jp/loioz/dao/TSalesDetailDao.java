package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TSalesDetailEntity;

/**
 * 売上明細用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSalesDetailDao {

	/**
	 * 売上明細SEQに紐づく売上明細データを取得します
	 * 
	 * @param salesSeq
	 * @return
	 */
	@Select
	TSalesDetailEntity selectSalesDetailBySalesDetailSeq(Long salesDetailSeq);

	/**
	 * 売上SEQに紐づく売上明細データを取得します
	 * 
	 * @param salesSeq
	 * @return
	 */
	@Select
	List<TSalesDetailEntity> selectSalesDetailBySalesSeq(Long salesSeq);

	/**
	 * 名簿ID、案件IDに紐づく売上明細データを取得します
	 * 
	 * @param personId
	 * @param ankenId
	 * @return
	 */
	@Select
	List<TSalesDetailEntity> selectSalesDetailByPersonIdAndAnkenId(Long personId, Long ankenId);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TSalesDetailEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TSalesDetailEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TSalesDetailEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TSalesDetailEntity> entityList);
	
	/**
	 * 売上明細用データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTSalesDetail();

}
