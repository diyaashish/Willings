package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TAccgDocInvoiceDepositTDepositRecvMappingEntity;

/**
 * 請求項目-預り金（実費）_預り金テーブルマッピング用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgDocInvoiceDepositTDepositRecvMappingDao {

	/**
	 * 預り金SEQに紐づく請求項目-預り金（実費）_預り金テーブルマッピングデータを取得
	 * 
	 * @param depositRecvSeq
	 * @return
	 */
	@Select
	TAccgDocInvoiceDepositTDepositRecvMappingEntity selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDepositRecvSeq(Long depositRecvSeq);

	/**
	 * 預り金SEQに紐づく請求項目-預り金（実費）_預り金テーブルマッピングデータを取得
	 * 
	 * @param depositRecvSeqList
	 * @return
	 */
	@Select
	List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDepositRecvSeqList(List<Long> depositRecvSeqList);
	
	/**
	 * 請求預り金項目SEQに紐づく請求項目-預り金（実費）_預り金テーブルマッピングデータを取得
	 * 
	 * @param docInvoiceDepositSeq
	 * @return
	 */
	@Select
	List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDocInvoiceDepositSeq(Long docInvoiceDepositSeq);

	/**
	 * 請求預り金項目SEQリストに紐づく請求項目-預り金（実費）_預り金テーブルマッピングデータを取得
	 * 
	 * @param docInvoiceDepositSeqList
	 * @return
	 */
	@Select
	List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByDocInvoiceDepositSeqList(List<Long> docInvoiceDepositSeqList);

	/**
	 * 請求預り金項目SEQ、預り金SEQに紐づく請求項目-預り金(実費）_預り金テーブルマッピングデータを取得
	 * 
	 * @param docInvoiceDepositSeqList
	 * @param depositRecvSeqList
	 * @return
	 */
	@Select
	List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> selectTAccgDocInvoiceDepositTDepositRecvMappingEntityByParams(List<Long> docInvoiceDepositSeqList, List<Long> depositRecvSeqList);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TAccgDocInvoiceDepositTDepositRecvMappingEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchInsert
	int[] batchInsert(List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> entityList);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgDocInvoiceDepositTDepositRecvMappingEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgDocInvoiceDepositTDepositRecvMappingEntity> entityList);

	/**
	 * 請求項目-預り金（実費）_預り金テーブルマッピングデータを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgDocInvoiceDepositTDepositRecvMapping();
	
}
