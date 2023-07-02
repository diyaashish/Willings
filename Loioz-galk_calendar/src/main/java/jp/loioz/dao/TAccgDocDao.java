package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AccgInvoiceStatementBean;
import jp.loioz.bean.AnkenPersonRelationBean;
import jp.loioz.entity.TAccgDocEntity;

/**
 * 会計書類Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgDocDao {

	/**
	 * 会計書類データを取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	default TAccgDocEntity selectAccgDocByAccgDocSeq(Long accgDocSeq) {
		return selectAccgDocByAccgDocSeq(Arrays.asList(accgDocSeq)).stream().findFirst().orElse(null);
	}

	/**
	 * 会計書類データを取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TAccgDocEntity> selectAccgDocByAccgDocSeq(List<Long> accgDocSeqList);

	/**
	 * 会計書類データを取得する
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	@Select
	List<TAccgDocEntity> selectAccgDocByParams(Long ankenId, Long personId);

	/**
	 * 請求書の会計書類データを取得する
	 * 
	 * @param ankenId
	 * @param billToPersonId
	 * @return
	 */
	@Select
	List<TAccgDocEntity> selectAccgDocInvoiceByBillToPersonId(Long ankenId, Long billToPersonId);

	/**
	 * 精算書の会計書類データを取得する
	 * 
	 * @param ankenId
	 * @param refundToPersonId
	 * @return
	 */
	@Select
	List<TAccgDocEntity> selectAccgDocStatementByRefundToPersonId(Long ankenId, Long refundToPersonId);

	/**
	 * 請求書/精算書一覧用Beanを取得する
	 * 
	 * @return
	 */
	@Select
	List<AccgInvoiceStatementBean> selectAccgInvoiceStateBeanByAnkenIdAndPersonIdAndLimit(Long ankenId, Long personId, Integer limitCount);

	/**
	 * 請求書/精算書Beanを取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	AccgInvoiceStatementBean selectAccgInvoiceStateBeanByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類に紐づく、顧客情報と案件情報を取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	AnkenPersonRelationBean selectAnkenPersonRelationBeanByAccgDocSeq(Long accgDocSeq);

	/**
	 * 名簿IDをキーとして、会計書類情報を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TAccgDocEntity> selectAccgDocByPersonId(Long personId);

	/**
	 * 名簿IDをキーとして、会計書類情報の件数を取得する
	 * 
	 * @param personId
	 * @return
	 */
	default int selectAccgDocCountByPersonId(Long personId) {
		return selectAccgDocByPersonId(personId).size();
	}

	/**
	 * 案件IDをキーとして、会計書類情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<TAccgDocEntity> selectAccgDocByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーとして、会計書類情報の件数を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	default int selectAccgDocCountByAnkenId(Long ankenId) {
		return selectAccgDocByAnkenId(ankenId).size();
	}

	/**
	 * 1件の会計書類データを登録する
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TAccgDocEntity entity);

	/**
	 * 1件の会計書類データを更新する
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(TAccgDocEntity entity);

	/**
	 * 1件の会計書類データを削除する
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TAccgDocEntity entity);
	
	/**
	 * 会計書類データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgDoc();

}
