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

import jp.loioz.bean.AccgDocInvoiceBean;
import jp.loioz.entity.TAccgDocInvoiceEntity;

/**
 * 請求項目Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgDocInvoiceDao {

	/**
	 * 請求項目SEQに紐づく請求項目情報（TAccgDocInvoiceEntity）を取得します。
	 * 
	 * @param docInvoiceSeq
	 * @return
	 */
	@Select
	TAccgDocInvoiceEntity selectAccgDocInvoiceByDocInvoiceSeq(Long docInvoiceSeq);

	/**
	 * 会計書類SEQに紐づく請求項目情報（TAccgDocInvoiceEntity）を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TAccgDocInvoiceEntity> selectAccgDocInvoiceByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類SEQに紐づく請求項目情報（AccgDocInvoiceBean）を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<AccgDocInvoiceBean> selectAccgDocInvoiceBeanByAccgDocSeq(Long accgDocSeq);

	/**
	 * 請求項目SEQに紐づく請求項目情報を取得します
	 * 
	 * @param accgDocSeqList
	 * @return
	 */
	@Select
	List<TAccgDocInvoiceEntity> selectAccgDocInvoiceByDocInvoiceSeqList(List<Long> docInvoiceSeqList);

	/**
	 * 預り金SEQに紐づく請求項目情報を取得します
	 * 
	 * @param depositRecvSeqList
	 * @return
	 */
	@Select
	List<TAccgDocInvoiceEntity> selectAccgDocInvoiceByDepositRecvSeqList(List<Long> depositRecvSeqList);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgDocInvoiceEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchInsert
	int[] batchInsert(List<TAccgDocInvoiceEntity> entityList);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgDocInvoiceEntity entity);

	/**
	 * 複数更新
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] batchUpdate(List<TAccgDocInvoiceEntity> entityList);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgDocInvoiceEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgDocInvoiceEntity> entityList);
	
	/**
	 * 請求項目データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgDocInvoice();
	
}
