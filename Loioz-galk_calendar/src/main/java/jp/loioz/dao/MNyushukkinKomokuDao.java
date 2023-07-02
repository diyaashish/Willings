package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.MNyushukkinKomokuEntity;

@ConfigAutowireable
@Dao
public interface MNyushukkinKomokuDao {

	/**
	 * 1件の入出金項目情報を取得する
	 *
	 * @param nyushukkinKomokuId 入出金項目ID
	 * @return 入出金項目情報
	 */
	default MNyushukkinKomokuEntity selectById(Long nyushukkinKomokuId) {
		return selectById(Arrays.asList(nyushukkinKomokuId)).stream().findFirst().orElse(null);
	}

	/**
	 * 入出金項目IDを指定して入出金項目情報を取得する
	 *
	 * @param nyushukkinKomokuIdList 入出金項目ID
	 * @return 入出金項目情報
	 */
	@Select
	List<MNyushukkinKomokuEntity> selectById(List<Long> nyushukkinKomokuIdList);

	/**
	 * 入出金項目を全て取得
	 *
	 * @return 入出金項目情報のリスト
	 */
	@Select
	List<MNyushukkinKomokuEntity> selectAll();

	/**
	 * 入出金項目を全て取得
	 *
	 * @return 入出金項目情報のリスト
	 */
	@Select
	List<MNyushukkinKomokuEntity> selectEnableAll();

	/**
	 * 入出金タイプから一覧情報を取得
	 *
	 * @param nyushukkinType 入出金タイプ
	 * @return 入出金項目情報のリスト
	 */
	@Select
	List<MNyushukkinKomokuEntity> selectByNyushukkinType(String nyushukkinType);

	/**
	 * 入出金タイプから一覧情報を取得<br>
	 *
	 * <pre>
	 * 削除可能なもののみ取得する。
	 * </pre>
	 *
	 * @param nyushukkinType 入出金タイプ
	 * @return 入出金項目情報のリスト
	 */
	@Select
	List<MNyushukkinKomokuEntity> selectByDeletableByNyushukkinType(String nyushukkinType);

	/**
	 * 入出金項目の表示順最大値を取得
	 *
	 * @return
	 */
	@Select
	long maxDisp();

	/**
	 * 登録処理（1件）
	 *
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(MNyushukkinKomokuEntity entity);

	/**
	 * 更新処理（1件）
	 *
	 * @param entity
	 * @return
	 */
	@Update
	int update(MNyushukkinKomokuEntity entity);

	/**
	 * 更新処理（複数件）
	 *
	 * @param entity
	 * @return
	 */
	@BatchUpdate
	int[] update(List<MNyushukkinKomokuEntity> entities);
}
