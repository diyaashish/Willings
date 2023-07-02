package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.SaibanTsuikisoJikenBean;
import jp.loioz.entity.TSaibanJikenEntity;

/**
 * 裁判-事件用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSaibanJikenDao {

	/**
	 * 事件SEQをキーにして裁判-事件を取得する
	 * 
	 * @param jikenSeq 事件SEQ
	 * @return 裁判-事件
	 */
	@Select
	TSaibanJikenEntity selectByJikenSeq(Long jikenSeq);

	/**
	 * 裁判SEQをキーにして裁判-事件を取得する
	 *
	 * @param saibanSeqList 裁判SEQ
	 * @return 裁判-事件
	 */
	@Select
	List<TSaibanJikenEntity> selectBySaibanSeq(List<Long> saibanSeqList);

	/**
	 * 裁判SEQをキーにして刑事裁判の本訴裁判-事件を取得する
	 *
	 * @param saibanSeqList 裁判SEQ
	 * @return 裁判-事件
	 */
	@Select
	List<TSaibanJikenEntity> selectByKeijiSaibanSeq(List<Long> keijiSaibanSeqList);

	/**
	 * 裁判SEQをキーにして刑事裁判の本訴裁判-事件を取得する
	 *
	 * @param saibanSeq
	 * @param mainJikenSeq
	 * @return
	 */
	@Select
	List<SaibanTsuikisoJikenBean> selectTsuikisoBySaibanSeq(Long saibanSeq, Long mainJikenSeq);

	/**
	 * 裁判SEQをキーにして裁判-事件を取得する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 裁判-事件
	 */
	default List<TSaibanJikenEntity> selectBySaibanSeq(Long saibanSeq) {
		return selectBySaibanSeq(Arrays.asList(saibanSeq));
	}

	/**
	 * 裁判SEQをキーにして裁判-事件を1件取得する(民事裁判用)
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 裁判-事件
	 */
	default TSaibanJikenEntity selectSingleBySaibanSeq(Long saibanSeq) {
		return selectBySaibanSeq(saibanSeq).stream()
				.findFirst()
				.orElse(null);
	}

	/**
	 * 1件の裁判-事件を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TSaibanJikenEntity entity);

	/**
	 * 1件の裁判-事件を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TSaibanJikenEntity entity);

	/**
	 * 1件の裁判-事件を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TSaibanJikenEntity entity);

	/**
	 * 複数件の裁判-事件を削除する
	 * 
	 * @param entityList
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] delete(List<TSaibanJikenEntity> entityList);
}
