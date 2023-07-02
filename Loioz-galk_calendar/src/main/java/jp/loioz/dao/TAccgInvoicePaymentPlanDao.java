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

import jp.loioz.bean.AccgInvoicePaymentPlanBean;
import jp.loioz.entity.TAccgInvoicePaymentPlanEntity;

/**
 * 支払計画Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgInvoicePaymentPlanDao {

	/**
	 * 支払計画データを取得します<br>
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@Select
	List<TAccgInvoicePaymentPlanEntity> selectInvoicePaymentPlanByInvoiceSeq(Long invoiceSeq);

	/**
	 * 支払計画データを取得します<br>
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TAccgInvoicePaymentPlanEntity> selectInvoicePaymentPlanByAccgDocSeq(Long accgDocSeq);

	/**
	 * 支払計画データ、売上金額（見込み）、売上金額（実績）を取得します。<br>
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<AccgInvoicePaymentPlanBean> selectInvoicePaymentPlanSalesByAccgDocSeq(Long accgDocSeq);

	/**
	 * 支払計画データ、売上金額（見込み）、売上金額（実績）を取得します。<br>
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@Select
	List<AccgInvoicePaymentPlanBean> selectInvoicePaymentPlanSalesByInvoiceSeq(Long invoiceSeq);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgInvoicePaymentPlanEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entities
	 * @return
	 */
	@BatchInsert
	int[] batchInsert(List<TAccgInvoicePaymentPlanEntity> entities);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgInvoicePaymentPlanEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgInvoicePaymentPlanEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgInvoicePaymentPlanEntity> entityList);
	
	/**
	 * 支払計画データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgInvoicePaymentPlan();
	
}
