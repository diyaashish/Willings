package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TSaibanSaibankanEntity;

/**
 * 裁判-裁判官用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSaibanSaibankanDao {

	/**
	 * 裁判SEQをキーにして裁判-裁判官を取得する
	 * 
	 * @param saibanSeq
	 * @return 裁判-裁判官
	 */
	@Select
	List<TSaibanSaibankanEntity> selectBySaibanSeq(Long saibanSeq);
	
	/**
	 * 複数件の裁判-裁判官情報を登録する
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchInsert
	int[] insert(List<TSaibanSaibankanEntity> entityList);

	/**
	 * 複数件の裁判-裁判官情報を更新する
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] update(List<TSaibanSaibankanEntity> entityList);

	/**
	 * 複数件の裁判-裁判官情報を削除する
	 * 
	 * @param entityList
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] delete(List<TSaibanSaibankanEntity> entityList);
}
