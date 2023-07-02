package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AnkenRelatedKanyoshaBean;
import jp.loioz.bean.AnkenRelatedSelfJoinKanyoshaBean;
import jp.loioz.entity.TAnkenRelatedKanyoshaEntity;

/**
 * 案件-関与者関係者用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAnkenRelatedKanyoshaDao {

	/**
	 * 案件IDをキーにして、案件-関与者関係者を取得する
	 * 
	 * @param ankenId 案件ID
	 * @return 案件-関与者関係者
	 */
	@Select
	List<TAnkenRelatedKanyoshaEntity> selectByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーにして、案件-関与者関係者を取得する
	 * 
	 * @param ankenIdList
	 * @return
	 */
	@Select
	List<TAnkenRelatedKanyoshaEntity> selectAnkenRelatedKanyoshaByAnkenIdList(List<Long> ankenIdList);

	/**
	 * 関与者SEQをキーとして、案件-関与者関係者情報を取得する
	 * 
	 * ※ テーブル変更により、暗黙的に取得データは一意になるが、
	 * テーブル定義が複合主キーなので戻り値がList型
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	@Select
	List<TAnkenRelatedKanyoshaEntity> selectAnkenRelatedKanyoshaByKanyoshaSeq(Long kanyoshaSeq);

	/**
	 * 案件IDとひも付き関与者SEQをキーとして、関与者-関係者情報を取得する
	 * 
	 * @param ankenId
	 * @param relatedKanyoshaSeq
	 * @return
	 */
	@Select
	List<TAnkenRelatedKanyoshaEntity> selectKanyoshaDairininByParams(Long ankenId, Long relatedKanyoshaSeq);

	/**
	 * 案件IDと関与者SEQをキーにして、案件-関与者関係者を取得する
	 * 
	 * @param ankenId 案件ID
	 * @param kanyoshaSeq 関与者SEQ
	 * @return 案件-関与者関係者
	 */
	@Select
	TAnkenRelatedKanyoshaEntity selectAnkenRelatedKanyoshaByParams(Long ankenId, Long kanyoshaSeq);

	/**
	 * 引数のパラメータに応じて、案件-関与者関係者Bean情報を取得する
	 *
	 * @param ankenId
	 * @param kanyoshaType
	 * @param dairiFlg
	 * @return
	 */
	@Select
	List<AnkenRelatedKanyoshaBean> selectAnkenRelatedKanyoshaBeanByParams(Long ankenId, String kanyoshaType, String dairiFlg);

	/**
	 * 名簿IDをキーに案件-関与者関係者情報を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TAnkenRelatedKanyoshaEntity> selectAnkenRelatedKanyoshaByPersonId(Long personId);

	/**
	 * 案件IDをキーとして、案件-関係者情報と代理人情報を自己結合した情報を取得する
	 * 
	 * @param ankenIdList
	 */
	@Select
	List<AnkenRelatedSelfJoinKanyoshaBean> selectAnkenRelatedSelfJoinKanyoshaBeanByAnkenIdList(List<Long> ankenIdList);

	/**
	 * 1件の案件-関与者関係者を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TAnkenRelatedKanyoshaEntity entity);

	/**
	 * 1件の案件-関与者関係者を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TAnkenRelatedKanyoshaEntity entity);

	/**
	 * 1件の案件-関与者関係者を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TAnkenRelatedKanyoshaEntity entity);

	/**
	 * 複数件の案件-関与者関係者を更新します。
	 * 
	 * @param entityList
	 * @return 更新したデータ件数
	 */
	@BatchUpdate
	int[] update(List<TAnkenRelatedKanyoshaEntity> entityList);

	/**
	 * 複数件の案件-関与者関係者を削除します。
	 * 
	 * @param entityList
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] delete(List<TAnkenRelatedKanyoshaEntity> entityList);
}
