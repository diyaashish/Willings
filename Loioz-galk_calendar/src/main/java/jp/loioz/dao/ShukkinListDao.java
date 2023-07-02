package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.domain.condition.ShukkinListSearchCondition;
import jp.loioz.dto.ShukkinListBean;

/**
 * 出金一覧画面Dao
 */
@ConfigAutowireable
@Dao
public interface ShukkinListDao {

	/**
	 * 検索条件による出金予定の取得
	 * 
	 * @return
	 */
	@Select
	List<ShukkinListBean> selectBeansBySeqList(List<Long> shukkinSeqList);

	/**
	 * 検索条件による出金予定SEQの取得
	 * 
	 * <pre>
	 * ページャーあり
	 * </pre>
	 * 
	 * @param conditions
	 * @param options
	 * @return SEQリスト
	 */
	@Select
	List<Long> selectSeqByConditions(ShukkinListSearchCondition conditions, SelectOptions options);

}
