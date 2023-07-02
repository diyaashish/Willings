package jp.loioz.dao;

import java.util.Arrays;
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

import jp.loioz.bean.SaibanRelatedKanyoshaBean;
import jp.loioz.bean.SaibanRelatedSelfJoinKanyoshaBean;
import jp.loioz.entity.TSaibanRelatedKanyoshaEntity;

/**
 * 裁判-関与者関係者用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSaibanRelatedKanyoshaDao {

	/**
	 * 裁判SEQをキーにして裁判-関与者関係者を取得する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 裁判-関与者関係者
	 */
	@Select
	List<TSaibanRelatedKanyoshaEntity> selectBySaibanSeq(Long saibanSeq);

	/**
	 * 関与者IDをキーにして裁判-関与者関係者を取得する
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	@Select
	List<TSaibanRelatedKanyoshaEntity> selectSaibanRelatedKanyoshaByKanyoshaSeq(Long kanyoshaSeq);

	/**
	 * 案件ID・裁判枝番をキーにして、裁判-関与者関係者を取得する
	 * 
	 * @param ankenId 案件ID
	 * @param branchNo 裁判枝番
	 * @return 裁判-関与者関係者
	 */
	@Select
	List<TSaibanRelatedKanyoshaEntity> selectNotDairininBySaibanSeq(Long saibanSeq);

	/**
	 * 案件IDをキーにして、裁判-関与者関係者を取得する
	 * 
	 * @param ankenId 案件ID
	 * @return 裁判-関与者関係者
	 */
	@Select
	List<TSaibanRelatedKanyoshaEntity> selectByAnkenId(Long ankenId);

	/**
	 * 引数のパラメータに応じて、裁判-関与者関係者Bean情報を取得する
	 * 
	 * @param saibanSeq
	 * @param kanyoshaType
	 * @param dairiFlg
	 * @return
	 */
	@Select
	List<SaibanRelatedKanyoshaBean> selectSaibanRelatedKanyoshaBeanByParams(Long saibanSeq, String kanyoshaType, String dairiFlg);

	/**
	 * 裁判SEQをキーにして、関与者種別別の関与者を取得する
	 * 
	 * @param saibanSeq
	 * @param kanyoshaType
	 * @return
	 */
	@Select
	List<TSaibanRelatedKanyoshaEntity> selectNotDairininBySeq(Long saibanSeq, String kanyoshaType);

	/**
	 * 裁判SEQ、関与者SEQをキーにして、裁判-関与者関係者を取得する
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param kanyoshaSeq 関与者SEQ
	 * @return
	 */
	@Select
	TSaibanRelatedKanyoshaEntity selectSaibanRelatedKanyoshaEntityByParams(Long saibanSeq, Long kanyoshaSeq);

	/**
	 * 名簿IDをキーにして、裁判-関与者情報を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TSaibanRelatedKanyoshaEntity> selectSaibanRelatedKanyoshaByPersonId(Long personId);

	/**
	 * 裁判SEQとひも付き関与者SEQをキーとして、裁判-関係者情報を取得する
	 * 
	 * @param saibanSeq
	 * @param relatedKanyoshaSeq
	 * @return
	 */
	@Select
	List<TSaibanRelatedKanyoshaEntity> selectKanyoshaDairininByParams(Long saibanSeq, Long relatedKanyoshaSeq);

	/**
	 * 裁判SEQをキーとして、裁判-関係者情報と代理人情報を自己結合した情報を取得する
	 * 
	 * @param saibanSeq
	 * @return
	 */
	default List<SaibanRelatedSelfJoinKanyoshaBean> selectSaibanRelatedSelfJoinKanyoshaBeanBySaibanSeq(Long saibanSeq) {
		return selectSaibanRelatedSelfJoinKanyoshaBeanBySaibanSeqList(Arrays.asList(saibanSeq));
	}

	/**
	 * 裁判SEQをキーとして、裁判-関係者情報と代理人情報を自己結合した情報を取得する
	 * 
	 * @param saibanSeqList
	 * @return
	 */
	@Select
	List<SaibanRelatedSelfJoinKanyoshaBean> selectSaibanRelatedSelfJoinKanyoshaBeanBySaibanSeqList(List<Long> saibanSeqList);

	/**
	 * １件の裁判-関与者情報を登録する
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TSaibanRelatedKanyoshaEntity entity);

	/**
	 * 複数件の裁判-関与者情報を登録する
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchInsert
	int[] insert(List<TSaibanRelatedKanyoshaEntity> entityList);

	/**
	 * １件の裁判-関与者情報を更新する
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(TSaibanRelatedKanyoshaEntity entity);

	/**
	 * 複数件の裁判-関与者情報を更新する
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] update(List<TSaibanRelatedKanyoshaEntity> entityList);

	/**
	 * １件の裁判-関与者情報を削除する
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TSaibanRelatedKanyoshaEntity entity);

	/**
	 * 複数件の裁判-関与者情報を削除する
	 * 
	 * @param entityList
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] delete(List<TSaibanRelatedKanyoshaEntity> entityList);
}
