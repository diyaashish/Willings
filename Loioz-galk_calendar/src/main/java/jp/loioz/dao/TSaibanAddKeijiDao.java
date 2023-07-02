package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.SaibanKensatsukanBean;
import jp.loioz.entity.TSaibanAddKeijiEntity;

/**
 * 刑事裁判付帯情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSaibanAddKeijiDao {

	/**
	 * 裁判SEQをキーにして、刑事裁判付帯情報を取得します。
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 刑事裁判付帯情報
	 */
	default TSaibanAddKeijiEntity selectBySaibanSeq(Long saibanSeq) {
		return selectBySaibanSeq(List.of(saibanSeq)).stream()
				.findFirst()
				.orElse(null);
	}

	/**
	 * 裁判SEQをキーにして、刑事裁判付帯情報を取得します。
	 *
	 * @param saibanSeqList 裁判SEQ
	 * @return 刑事裁判付帯情報
	 */
	@Select
	List<TSaibanAddKeijiEntity> selectBySaibanSeq(List<Long> saibanSeqList);

	/**
	 * 裁判SEQをキーにして、刑事裁判付帯情報を取得します。
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 刑事裁判付帯情報
	 */
	@Select
	SaibanKensatsukanBean selectJoinSosakikanBySeq(Long saibanSeq);

	/**
	 * 1件の刑事裁判付帯情報を登録します。
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TSaibanAddKeijiEntity entity);

	/**
	 * 1件の刑事裁判付帯情報を更新します。
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TSaibanAddKeijiEntity entity);

	/**
	 * 1件の刑事裁判付帯情報を削除します。
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TSaibanAddKeijiEntity entity);
}
