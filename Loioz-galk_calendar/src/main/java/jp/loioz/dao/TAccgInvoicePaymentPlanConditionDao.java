package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TAccgInvoicePaymentPlanConditionEntity;

/**
 * 支払分割条件Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgInvoicePaymentPlanConditionDao {

	
	/**
	 * 精算書SEQをキーとして、支払分割条件情報を取得する
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@Select
	TAccgInvoicePaymentPlanConditionEntity selectAccgInvoicePaymentPlanConditionByInvoiceSeq(Long invoiceSeq);

	/**
	 * 支払分割条件データを取得します。<br>
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	TAccgInvoicePaymentPlanConditionEntity selectInvoicePaymentPlanConditionByAccgDocSeq(Long accgDocSeq);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgInvoicePaymentPlanConditionEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgInvoicePaymentPlanConditionEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgInvoicePaymentPlanConditionEntity entity);

	/**
	 * 支払分割条件データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgInvoicePaymentPlanCondition();
	
}
