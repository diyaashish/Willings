package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TToiawaseDetailEntity;

/**
 * 問い合わせ-詳細Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TToiawaseDetailDao {

	/**
	 * １件の問い合わせ情報を取得します
	 *
	 * @param accountSeq
	 * @return
	 */
	default TToiawaseDetailEntity selectBySeq(Long toiawaseSeq) {
		return selectBySeq(Arrays.asList(toiawaseSeq)).stream().findFirst().orElse(null);
	}

	/**
	 * 複数件の問い合わせ情報を取得します
	 * 
	 * @param toiawaseSeq
	 * @return
	 */
	@Select
	List<TToiawaseDetailEntity> selectBySeq(List<Long> toiawaseDetailSeq);

	/**
	 * 問い合わせSEQに紐づく情報を取得します
	 * 
	 * @param toiawaseSeq
	 * @return
	 */
	@Select
	List<TToiawaseDetailEntity> selectByToiawaseSeq(Long toiawaseSeq);

	/**
	 * 1件の問い合わせ-詳細情報を登録します
	 * 
	 * @param entity
	 * @return 件数
	 */
	@Insert
	int insert(TToiawaseDetailEntity entity);

	/**
	 * 1件の問い合わせ-詳細情報を更新します
	 * 
	 * @param entity
	 * @return 件数
	 */
	@Update
	int update(TToiawaseDetailEntity entity);

	/**
	 * 複数の問い合わせ-詳細情報を更新します
	 * 
	 * @param entity
	 * @return 件数
	 */
	@BatchUpdate
	int[] update(List<TToiawaseDetailEntity> entity);

}
