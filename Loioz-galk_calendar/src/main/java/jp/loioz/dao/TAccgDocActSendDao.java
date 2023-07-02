package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AccgDocActSendBean;
import jp.loioz.entity.TAccgDocActSendEntity;

/**
 * 会計書類対応-送付Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgDocActSendDao {

	/**
	 * PKをキーとして、会計書類対応-送付データの取得<br>
	 * 
	 * @param accgDocActSendSeq
	 * @return
	 */
	@Select
	TAccgDocActSendEntity selectAccgDocActSendByActSendSeq(Long accgDocActSendSeq);

	/**
	 * 会計書類対応-送付データを取得します。<br>
	 * 
	 * @param accgDocActSeqList
	 * @return
	 */
	@Select
	List<TAccgDocActSendEntity> selectAccgDocActSendByAccgDocActSeq(List<Long> accgDocActSeqList);

	/**
	 * 会計書類対応-送付データを取得します。<br>
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<AccgDocActSendBean> selectAccgDocActSendBeanByAccgDocActSeq(List<Long> accgDocActSeqList);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgDocActSendEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgDocActSendEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgDocActSendEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgDocActSendEntity> entityList);

	/**
	 * 会計書類対応-送付データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgDocActSend();
	
}
