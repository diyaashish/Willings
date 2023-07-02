package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TUncollectibleEntity;

/**
 * 回収不能金
 */
@ConfigAutowireable
@Dao
public interface TUncollectibleDao {

	/**
	 * SEQをキーとして、回収不能金情報を取得する
	 * 
	 * @param uncollectibleSeq
	 * @return
	 */
	@Select
	TUncollectibleEntity selectUncollectibleByUncollectibleSeq(Long uncollectibleSeq);

	/**
	 * 名簿IDと案件IDをキーとして、回収不能金情報を取得する
	 *
	 * @param personId
	 * @param ankenId
	 * @return
	 */
	@Select
	TUncollectibleEntity selectUncollectibleByPersonIdAndAnkenId(Long personId, Long ankenId);

	/**
	 * 名簿IDをキーとして、回収不能金情報を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TUncollectibleEntity> selectUncollectibleByPersonId(Long personId);

	/**
	 * 名簿IDをキーとして、回収不能金情報の件数を取得する
	 * 
	 * @param personId
	 * @return
	 */
	default int selectUncollectibleCountByPersonId(Long personId) {
		return selectUncollectibleByPersonId(personId).size();
	}

	/**
	 * 案件IDをキーとして、回収不能金情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<TUncollectibleEntity> selectUncollectibleByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーとして、回収不能金情報の件数を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	default int selectUncollectibleCountByAnkenId(Long ankenId) {
		return selectUncollectibleByAnkenId(ankenId).size();
	}

	/**
	 * １件の登録処理
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TUncollectibleEntity entity);

	/**
	 * １件の更新処理
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(TUncollectibleEntity entity);

	/**
	 * １件の削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TUncollectibleEntity entity);

	/**
	 * 回収不能金データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTUncollectible();
	
}
