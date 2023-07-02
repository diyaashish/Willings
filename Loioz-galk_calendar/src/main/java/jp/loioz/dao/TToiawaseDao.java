package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.domain.condition.ToiawaseListSearchCondition;
import jp.loioz.entity.TToiawaseEntity;

/**
 * 問い合わせ用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TToiawaseDao {

	/**
	 * １件の問い合わせ情報を取得します
	 *
	 * @param accountSeq
	 * @return
	 */
	default TToiawaseEntity selectBySeq(Long toiawaseSeq) {
		return selectBySeq(Arrays.asList(toiawaseSeq)).stream().findFirst().orElse(null);
	}

	/**
	 * 複数件の問い合わせ情報を取得します
	 * 
	 * @param toiawaseSeq
	 * @return
	 */
	@Select
	List<TToiawaseEntity> selectBySeq(List<Long> toiawaseSeq);

	/**
	 * １ページ分のの問い合わせ情報を取得します
	 * 
	 * @param toiawaseSeq
	 * @return
	 */
	@Select
	List<TToiawaseEntity> selectListBySeq(List<Long> toiawaseSeq);

	/**
	 * 問い合わせ一覧の検索処理(SEQ)
	 * 
	 * @param conditions
	 * @param options
	 * @return
	 */
	@Select
	List<Long> selectByConditions(ToiawaseListSearchCondition conditions, SelectOptions options);

	/**
	 * 未読になっている問い合わせSEQをすべて取得する
	 * 
	 * @return
	 */
	@Select
	List<Long> selectByNonRead();

	/**
	 * 未読になっている問い合わせSEQの件数を取得する
	 * 
	 * @return
	 */
	default int unreadCount() {
		return selectByNonRead().size();
	}

	/**
	 * 1件の問い合わせ情報を登録します
	 * 
	 * @param entity
	 * @return 件数
	 */
	@Insert
	int insert(TToiawaseEntity entity);

	/**
	 * 1件の問い合わせ情報を更新します
	 * 
	 * @param entity
	 * @return 件数
	 */
	@Update
	int update(TToiawaseEntity entity);

}
