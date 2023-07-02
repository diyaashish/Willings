package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TSaibanLimitRelationEntity;

/**
 * 裁判-裁判期日用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSaibanLimitRelationDao {

	/**
	 * 裁判SEQをキーにして裁判-裁判期日を取得する
	 * 
	 * @param saibanSeq
	 * @return 裁判-裁判期日
	 */
	@Select
	List<TSaibanLimitRelationEntity> selectBySaibanSeq(Long saibanSeq);

	/**
	 * 裁判期日SEQをキーにして裁判-裁判期日を取得する
	 *
	 * @param saibanLimitSeq 裁判期日SEQ
	 * @return 裁判-裁判期日
	 */
	@Select
	List<TSaibanLimitRelationEntity> selectBySaibanLimitSeq(Long saibanLimitSeq);

	/**
	 * 1件の裁判-裁判期日を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TSaibanLimitRelationEntity entity);

	/**
	 * 1件の裁判-裁判期日を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TSaibanLimitRelationEntity entity);

}
