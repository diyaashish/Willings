package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TAccgInvoiceWithholdingEntity;

/**
 * 請求項目-源泉徴収Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgInvoiceWithholdingDao {

	/**
	 * 会計書類SEQに紐づく請求項目-源泉徴収情報を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	TAccgInvoiceWithholdingEntity selectAccgInvoiceWithholdingByAccgDocSeq(Long accgDocSeq);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgInvoiceWithholdingEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgInvoiceWithholdingEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgInvoiceWithholdingEntity entity);

	/**
	 * 請求項目-源泉徴収データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgInvoiceWithholding();
	
}
