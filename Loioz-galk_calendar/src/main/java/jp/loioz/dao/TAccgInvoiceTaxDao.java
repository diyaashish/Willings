package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TAccgInvoiceTaxEntity;

/**
 * 請求項目-消費税Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgInvoiceTaxDao {

	/**
	 * 会計書類SEQに紐づく請求項目-消費税情報を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TAccgInvoiceTaxEntity> selectAccgInvoiceTaxByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類SEQに紐づく10%の請求項目-消費税情報を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	TAccgInvoiceTaxEntity selectAccgInvoiceTax10ByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類SEQに紐づく8%の請求項目-消費税情報を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	TAccgInvoiceTaxEntity selectAccgInvoiceTax8ByAccgDocSeq(Long accgDocSeq);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgInvoiceTaxEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgInvoiceTaxEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgInvoiceTaxEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgInvoiceTaxEntity> entityList);

	/**
	 * 請求項目-消費税データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgInvoiceTax();
	
}
