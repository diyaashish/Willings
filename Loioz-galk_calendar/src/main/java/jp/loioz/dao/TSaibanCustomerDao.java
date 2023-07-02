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

import jp.loioz.bean.SaibanCustomerBean;
import jp.loioz.entity.TSaibanCustomerEntity;

/**
 * 裁判-顧客用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSaibanCustomerDao {

	/**
	 * 裁判SEQと顧客IDをキーにして裁判-顧客を取得する
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param customerId 顧客ID
	 * @return 裁判-顧客
	 */
	@Select
	TSaibanCustomerEntity selectBySaibanSeqAndCustomerId(Long saibanSeq, Long customerId);

	/**
	 * 裁判SEQをキーにして裁判-顧客を取得する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 裁判-顧客
	 */
	@Select
	List<TSaibanCustomerEntity> selectBySaibanSeq(Long saibanSeq);

	/**
	 * 裁判SEQをキーにして裁判-顧客を取得する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 裁判-顧客
	 */
	@Select
	List<SaibanCustomerBean> selectBySeq(Long saibanSeq);

	/**
	 * 裁判SEQをキーにして裁判-顧客を取得する
	 * 
	 * @param saibanSeqList
	 * @return
	 */
	@Select
	List<SaibanCustomerBean> selectSaibanCustomerBeanBySaibanSeqList(List<Long> saibanSeqList);

	/**
	 * 1件の裁判-顧客を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TSaibanCustomerEntity entity);

	/**
	 * 1件の裁判-顧客を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TSaibanCustomerEntity entity);

	/**
	 * 1件の裁判-顧客を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TSaibanCustomerEntity entity);

	/**
	 * 複数件の裁判-顧客情報を登録します。
	 * 
	 * @param entityList
	 * @return 登録したデータ件数
	 */
	@BatchInsert
	int[] insert(List<TSaibanCustomerEntity> entityList);

	/**
	 * 複数件の裁判-顧客情報を更新します。
	 * 
	 * @param entityList
	 * @return 更新したデータ件数
	 */
	@BatchUpdate
	int[] update(List<TSaibanCustomerEntity> entityList);

	/**
	 * 複数件の裁判-顧客情報を削除します。
	 * 
	 * @param entityList
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] delete(List<TSaibanCustomerEntity> entityList);
}
