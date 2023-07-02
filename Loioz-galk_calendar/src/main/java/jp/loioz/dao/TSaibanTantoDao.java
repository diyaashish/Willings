package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.SaibanTantoAccountBean;
import jp.loioz.bean.SaibanTantoBean;
import jp.loioz.bean.SaibanTantoLawyerJimuBean;
import jp.loioz.entity.TSaibanTantoEntity;

/**
 * 裁判担当者情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSaibanTantoDao {

	/**
	 * 裁判SEQをキーにして裁判担当者情報を取得する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 裁判担当者情報
	 */
	@Select
	List<TSaibanTantoEntity> selectBySaibanSeq(Long saibanSeq);

	/**
	 * 裁判SEQをキーにしてSaibanTantoAccountBeanのリストを取得する
	 * 
	 * @param saibanSeq
	 * @return List<SaibanTantoAccountBean>
	 */
	@Select
	List<SaibanTantoAccountBean> selectBySeqForAccount(Long saibanSeq);

	/**
	 * 裁判SEQをキーにしてSaibanTantoBeanのリストを取得する
	 * 
	 * @param saibanSeq
	 * @return List<SaibanTantoAccountBean>
	 */
	@Select
	List<SaibanTantoBean> selectBySeqJoinAccount(Long saibanSeq);

	/**
	 * 裁判SEQに関する（民事・刑事裁判の）担当弁護士、担当事務情報を取得します<br>
	 * それぞれ、カンマ区切りで全担当者分の名前を取得<br>
	 * 並び順<br>
	 * 1.主担当<br>
	 * 2.担当者<br>
	 * 
	 * @param saibanSeqList
	 * @return
	 */
	@Select
	List<SaibanTantoLawyerJimuBean> selectSaibanTantoLaywerJimuBySeq(List<Long> saibanSeqList);

	/**
	 * 1件の裁判担当者情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TSaibanTantoEntity entity);

	/**
	 * 1件の裁判担当者情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TSaibanTantoEntity entity);

	/**
	 * 1件の裁判担当者情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TSaibanTantoEntity entity);

	/**
	 * 複数件の裁判担当情報を削除する
	 *
	 * @param entityList
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] delete(List<TSaibanTantoEntity> entityList);
}
