package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TAccgDocRepayTDepositRecvMappingEntity;

/**
 * 既入金項目_預り金テーブルマッピング用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgDocRepayTDepositRecvMappingDao {

	/**
	 * 預り金SEQに紐づく既入金項目-預り金テーブルマッピングデータを取得
	 * 
	 * @param depositRecvSeq
	 * @return
	 */
	@Select
	TAccgDocRepayTDepositRecvMappingEntity selectTAccgDocRepayTDepositRecvMappingEntityByDepositRecvSeq(Long depositRecvSeq);

	/**
	 * 預り金SEQに紐づく既入金項目-預り金テーブルマッピングデータを取得
	 * 
	 * @param depositRecvSeq
	 * @return
	 */
	@Select
	List<TAccgDocRepayTDepositRecvMappingEntity> selectTAccgDocRepayTDepositRecvMappingEntityByDepositRecvSeqList(List<Long> depositRecvSeqList);

	/**
	 * 既入金項目SEQに紐づく既入金項目-預り金テーブルマッピングデータを取得
	 * 
	 * @param docRepaySeq
	 * @return
	 */
	@Select
	List<TAccgDocRepayTDepositRecvMappingEntity> selectTAccgDocRepayTDepositRecvMappingEntityByDocRepaySeq(Long docRepaySeq);

	/**
	 * 既入金項目SEQリストに紐づく既入金項目-預り金テーブルマッピングデータを取得
	 * 
	 * @param docRepaySeqList
	 * @return
	 */
	@Select
	List<TAccgDocRepayTDepositRecvMappingEntity> selectTAccgDocRepayTDepositRecvMappingEntityByDocRepaySeqList(List<Long> docRepaySeqList);

	/**
	 * 既入金項目SEQリスト、預り金SEQリストに紐づく既入金項目-預り金テーブルマッピングデータを取得
	 * 
	 * @param docRepaySeqList
	 * @param depositRecvSeqList
	 * @return
	 */
	@Select
	List<TAccgDocRepayTDepositRecvMappingEntity> selectTAccgDocRepayTDepositRecvMappingEntityByParams(List<Long> docRepaySeqList, List<Long> depositRecvSeqList);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TAccgDocRepayTDepositRecvMappingEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchInsert
	int[] batchInsert(List<TAccgDocRepayTDepositRecvMappingEntity> entityList);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgDocRepayTDepositRecvMappingEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgDocRepayTDepositRecvMappingEntity> entityList);
	
	/**
	 * 既入金項目_預り金テーブルマッピングデータを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgDocRepayTDepositRecvMapping();
	
}
