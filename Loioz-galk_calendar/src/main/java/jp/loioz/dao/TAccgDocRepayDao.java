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

import jp.loioz.bean.RepayBean;
import jp.loioz.bean.RepaySummaryBean;
import jp.loioz.entity.TAccgDocRepayEntity;

/**
 * 既入金Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgDocRepayDao {

	/**
	 * 既入金項目SEQに紐づく既入金情報を取得します
	 * 
	 * @param docRepaySeq
	 * @return
	 */
	@Select
	TAccgDocRepayEntity selectDocRepayByDocRepaySeq(Long docRepaySeq);

	/**
	 * 既入金項目SEQに紐づく既入金情報を取得します
	 * 
	 * @param docRepaySeqList
	 * @return
	 */
	@Select
	List<TAccgDocRepayEntity> selectDocRepayByDocRepaySeqList(List<Long> docRepaySeqList);

	/**
	 * 会計書類SEQに紐づく既入金情報を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TAccgDocRepayEntity> selectDocRepayByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類SEQに紐づく既入金の合計を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	RepaySummaryBean selectSummaryByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類SEQに紐づく既入金情報を取得します<br>
	 * （預り金SEQはGROUP_CONCATで取得）
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<RepayBean> selectRepayBeanListByAccgDocSeq(Long accgDocSeq);

	/**
	 * 預り金SEQに紐づく既入金情報を取得します
	 * 
	 * @param depositRecvSeqList
	 * @return
	 */
	@Select
	List<TAccgDocRepayEntity> selectDocRepayByDepositRecvSeq(List<Long> depositRecvSeqList);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgDocRepayEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchInsert
	int[] batchInsert(List<TAccgDocRepayEntity> entityList);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgDocRepayEntity entity);

	/**
	 * 複数更新
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] batchUpdate(List<TAccgDocRepayEntity> entityList);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgDocRepayEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgDocRepayEntity> entityList);
	
	/**
	 * 既入金データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgDocRepay();
	
}
