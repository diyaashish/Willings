package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TAccgRecordDetailOverPaymentEntity;

/**
 * 取引実績明細-過入金Dao
 */
@ConfigAutowireable
@Dao
public interface TAccgRecordDetailOverPaymentDao {

	/**
	 * PKをキーとして、取引実績-過入金情報を取得
	 * 
	 * @param accgRecordDetailOverPayment
	 * @return
	 */
	@Select
	TAccgRecordDetailOverPaymentEntity selectAccgRecordDetailOverPaymentBySeq(Long accgRecordDetailOverPaymentSeq);

	/**
	 * 取引実績詳細SEQをキーとして、取引実績-過入金情報を取得
	 * 
	 * @param accgRecordDetailSeq
	 * @return
	 */
	@Select
	TAccgRecordDetailOverPaymentEntity selectAccgRecordDetailOverPaymentByAccgRecordDetailSeq(Long accgRecordDetailSeq);

	/**
	 * 取引実績明細SEQのリストをキーとして、取引実績-過入金情報を取得
	 * 
	 * @param accgRecordDetailSeqList
	 * @return
	 */
	@Select
	List<TAccgRecordDetailOverPaymentEntity> selectAccgRecordDetailOverPaymentByAccgRecordDetailSeqList(List<Long> accgRecordDetailSeqList);

	/**
	 * 取引実績詳細SEQをキーとして、取引実績-過入金情報を取得
	 * 
	 * <pre>
	 * ※ 取引実績-過入金情報は、詳細情報に対して１対１（取引実績情報に対して１対N）の想定だったが、
	 * 　 仕様変更によって、取引実績情報に対しても１対１となった(過入金は１件のみしか発生しない仕様)
	 * </pre>
	 * 
	 * @param accgRecordSeq
	 * @return
	 */
	@Select
	TAccgRecordDetailOverPaymentEntity selectAccgRecordDetailOverPaymentByAccgRecordSeq(Long accgRecordSeq);

	/**
	 * 1件の取引実績明細-過入金を登録する
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TAccgRecordDetailOverPaymentEntity entity);

	/**
	 * １件の取引実績明細-過入金を更新する
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(TAccgRecordDetailOverPaymentEntity entity);

	/**
	 * １件の取引実績明細-過入金を削除する
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TAccgRecordDetailOverPaymentEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgRecordDetailOverPaymentEntity> entityList);

	/**
	 * 取引実績明細-過入金データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgRecordDetailOverPayment();
	
}
