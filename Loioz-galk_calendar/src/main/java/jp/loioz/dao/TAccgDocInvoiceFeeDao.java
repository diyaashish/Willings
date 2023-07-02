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

import jp.loioz.entity.TAccgDocInvoiceFeeEntity;

/**
 * 請求報酬項目Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgDocInvoiceFeeDao {

	/**
	 * 報酬SEQに紐づく請求報酬項目データを取得します
	 * 
	 * @param feeSeq
	 * @return
	 */
	@Select
	TAccgDocInvoiceFeeEntity selectAccgDocInvoiceFeeByFeeSeq(Long feeSeq);

	/**
	 * 報酬SEQに紐づく請求報酬項目データを取得します
	 * 
	 * @param feeSeqList
	 * @return
	 */
	@Select
	List<TAccgDocInvoiceFeeEntity> selectAccgDocInvoiceFeeByFeeSeqList(List<Long> feeSeqList);

	/**
	 * 請求項目SEQに紐づく請求報酬項目データを取得します
	 * 
	 * @param docInvoiceSeqList
	 * @return
	 */
	@Select
	List<TAccgDocInvoiceFeeEntity> selectAccgDocInvoiceFeeBydocInvoiceSeqList(List<Long> docInvoiceSeqList);

	/**
	 * 会計書類SEQに紐づく請求報酬項目データを取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TAccgDocInvoiceFeeEntity> selectAccgDocInvoiceFeeByAccgDocSeq(Long accgDocSeq);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgDocInvoiceFeeEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchInsert
	int[] batchInsert(List<TAccgDocInvoiceFeeEntity> entityList);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgDocInvoiceFeeEntity entity);

	/**
	 * 複数更新
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] batchUpdate(List<TAccgDocInvoiceFeeEntity> entityList);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgDocInvoiceFeeEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgDocInvoiceFeeEntity> entityList);
	
	/**
	 * 請求報酬項目データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgDocInvoiceFee();
	
}
