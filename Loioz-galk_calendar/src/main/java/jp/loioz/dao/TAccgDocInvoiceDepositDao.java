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

import jp.loioz.bean.InvoiceDepositSummaryBean;
import jp.loioz.entity.TAccgDocInvoiceDepositEntity;

/**
 * 請求預り金項目Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgDocInvoiceDepositDao {

	/**
	 * 請求預り金項目SEQに紐づく請求項目-預り金データを取得する
	 * 
	 * @param docInvoiceDepositSeq
	 * @return
	 */
	@Select
	TAccgDocInvoiceDepositEntity selectAccgDocInvoiceDepositByDocInvoiceDepositSeq(Long docInvoiceDepositSeq);

	/**
	 * 請求預り金項目SEQに紐づく請求項目-預り金データを取得する
	 * 
	 * @param docInvoiceDepositSeqList
	 * @return
	 */
	@Select
	List<TAccgDocInvoiceDepositEntity> selectAccgDocInvoiceDepositByDocInvoiceDepositSeqList(List<Long> docInvoiceDepositSeqList);
	
	/**
	 * 請求項目SEQに紐づく請求項目-預り金データを取得する
	 * 
	 * @param docInvoiceSeq
	 * @return
	 */
	@Select
	TAccgDocInvoiceDepositEntity selectAccgDocInvoiceDepositByDocInvoiceSeq(Long docInvoiceSeq);

	/**
	 * 請求項目SEQに紐づく請求項目-預り金データを取得する
	 * 
	 * @param docInvoiceSeqList
	 * @return
	 */
	@Select
	List<TAccgDocInvoiceDepositEntity> selectAccgDocInvoiceDepositByDocInvoiceSeqList(List<Long> docInvoiceSeqList);

	/**
	 * 会計書類SEQに紐づく請求項目-預り金データを取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TAccgDocInvoiceDepositEntity> selectAccgDocInvoiceDepositByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類SEQに紐づく請求項目-預り金データの合計を取得する
	 * 
	 * @param AccgDocSeq
	 * @return
	 */
	@Select
	InvoiceDepositSummaryBean selectSummaryByAccgDocSeq(Long accgDocSeq);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgDocInvoiceDepositEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchInsert
	int[] batchInsert(List<TAccgDocInvoiceDepositEntity> entityList);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgDocInvoiceDepositEntity entity);

	/**
	 * 複数更新
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] batchUpdate(List<TAccgDocInvoiceDepositEntity> entityList);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgDocInvoiceDepositEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgDocInvoiceDepositEntity> entityList);
	
	/**
	 * 請求預り金項目データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgDocInvoiceDeposit();
	
}
