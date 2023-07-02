package jp.loioz.dao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AnkenSosakikanBean;
import jp.loioz.entity.TAnkenSosakikanEntity;

/**
 * 案件-捜査機関用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAnkenSosakikanDao {
	/**
	 * 捜査機関連番をキーにして、捜査機関情報を取得する
	 * 
	 * @param ankenId 案件ID
	 * @return 案件-捜査機関
	 */
	@Select
	TAnkenSosakikanEntity selectBySosakikanSeq(Long sosakikanSeq);

	/**
	 * 案件IDをキーにして、案件-捜査機関情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 案件-捜査機関
	 */
	@Select
	List<TAnkenSosakikanEntity> selectByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーにして、画面表示用の案件-捜査機関情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 案件-捜査機関
	 */
	@Select
	List<AnkenSosakikanBean> selectJoinMstByAnkenId(Long ankenId);

	/**
	 * 案件IDに紐付いているデータ件数を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	default long selectCountByAnkenId(Long ankenId) {
		return Optional.ofNullable(selectByAnkenId(ankenId)).orElseGet(Collections::emptyList).size();
	}

	/**
	 * 1件の案件-捜査機関情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TAnkenSosakikanEntity entity);

	/**
	 * 1件の案件-捜査機関情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TAnkenSosakikanEntity entity);

	/**
	 * 1件の案件-捜査機関情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TAnkenSosakikanEntity entity);

	/**
	 * 複数件の案件-捜査機関情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] delete(List<TAnkenSosakikanEntity> entity);
}
