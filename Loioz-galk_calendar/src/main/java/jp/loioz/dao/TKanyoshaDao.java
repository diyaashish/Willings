package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AnkenAitegataBean;
import jp.loioz.bean.AnkenKanyoshaBean;
import jp.loioz.bean.KanyoshaBean;
import jp.loioz.bean.KanyoshaContactBean;
import jp.loioz.bean.KanyoshaListBean;
import jp.loioz.domain.condition.KanyoshaListSearchCondition;
import jp.loioz.entity.TKanyoshaEntity;

/**
 * 関与者用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TKanyoshaDao {

	/**
	 * 関与者SEQをキーとして、関与者情報を取得する
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	@Select
	TKanyoshaEntity selectKanyoshaByKanyoshaSeq(Long kanyoshaSeq);

	/**
	 * 関与者SEQと案件IDをキーとして、関与者情報を取得する
	 * 
	 * @param kanyoshaSeq
	 * @param ankenId
	 * @return
	 */
	@Select
	TKanyoshaEntity selectAnkenKanyoshaByParams(Long kanyoshaSeq, Long ankenId);

	/**
	 * 案件IDをキーにして関与者情報を取得する。
	 * 
	 * @param ankenId
	 * @return
	 */
	default List<TKanyoshaEntity> selectKanyoshaListByAnkenId(Long ankenId) {
		return selectKanyoshaListByAnkenId(Arrays.asList(ankenId));
	}

	/**
	 * 案件IDをキーにして関与者情報を取得する。
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<TKanyoshaEntity> selectKanyoshaListByAnkenId(List<Long> ankenIdList);

	/**
	 * 名簿IDをキーにして関与者情報を取得する。
	 * 
	 * @param personId
	 * @return
	 */
	default List<TKanyoshaEntity> selectKanyoshaListByPersonId(Long personId) {
		return selectKanyoshaListByPersonId(Arrays.asList(personId));
	}

	/**
	 * 名簿IDをキーにして関与者情報を取得する。
	 * 
	 * @param personIdList
	 * @return
	 */
	@Select
	List<TKanyoshaEntity> selectKanyoshaListByPersonId(List<Long> personIdList);

	/**
	 * 検索条件から、関与者一覧Beanを取得する
	 * 
	 * @param searchConditions
	 * @return
	 */
	@Select
	List<KanyoshaListBean> selectKanyoshaBySearchConditions(KanyoshaListSearchCondition conditions);

	/**
	 * 複数の案件IDをキーにして、相手方一覧を取得する
	 * 
	 * @param ankenIdList
	 * @return 相手方情報
	 */
	@Select
	List<AnkenAitegataBean> selectAnkenAitegataBeanByAnkenIdList(List<Long> ankenIdList);

	/**
	 * 関与者SEQをキーとして、関与者Bean情報を取得する<br>
	 * 入出金画面用
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	@Select
	jp.loioz.dto.KanyoshaBean selectNyushukkinKanyoshaBeanByKanyoshaSeq(Long kanyoshaSeq);

	/**
	 * 関与者SEQをキーとして、関与者Bean情報を取得する
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	@Select
	KanyoshaBean selectKanyoshaBeanByKanyoshaSeq(Long kanyoshaSeq);

	/**
	 * 関与者SEQをキーとして、関与者の連絡先情報を取得する
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	@Select
	KanyoshaContactBean selectKanyoshaContactByKanyoshaSeq(Long kanyoshaSeq);

	/**
	 * 案件IDをキーして、関与者Bean情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	default List<KanyoshaBean> selectKanyoshaBeanByAnkenId(Long ankenId) {
		return selectKanyoshaBeanByAnkenIdList(Arrays.asList(ankenId));
	}

	/**
	 * 案件IDをキーして、関与者Bean情報を取得する
	 * 
	 * @param ankenIdList
	 * @return
	 */
	@Select
	List<KanyoshaBean> selectKanyoshaBeanByAnkenIdList(List<Long> ankenIdList);

	/**
	 * 案件IDをキーとして、案件関与者Bean情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	default List<AnkenKanyoshaBean> selectAnkenRelatedKanyoshaByAnkenIdList(Long ankenId) {
		return selectAnkenRelatedKanyoshaByAnkenIdList(Arrays.asList(ankenId));
	}

	/**
	 * 案件IDをキーとして、案件関与者Bean情報を取得する
	 * 
	 * @param ankenIdList
	 * @return
	 */
	@Select
	List<AnkenKanyoshaBean> selectAnkenRelatedKanyoshaByAnkenIdList(List<Long> ankenIdList);

	/**
	 * １件の関与者を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TKanyoshaEntity entity);

	/**
	 * １件の関与者情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TKanyoshaEntity entity);

	/**
	 * 複数件の関与者情報を更新する
	 *
	 * @param entitys
	 * @return 複数件更新したデータ件数
	 */
	@BatchUpdate
	int[] batchUpdate(List<TKanyoshaEntity> entitys);

	/**
	 * １件の関与者情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TKanyoshaEntity entity);

	/**
	 * 複数件の関与者情報を削除する
	 *
	 * @param entities
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] batchDelete(List<TKanyoshaEntity> entities);

}