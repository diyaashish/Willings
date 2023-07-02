package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.domain.condition.NyukinListSearchCondition;
import jp.loioz.dto.NyukinListBean;

/**
 * 入金一覧Dao
 */
@ConfigAutowireable
@Dao
public interface NyukinListDao {

	/**
	 * 検索条件による入金予定の取得
	 * 
	 */
	@Select
	List<NyukinListBean> selectBeansBySeqList(List<Long> nyukinSeqList);

	/**
	 * 検索条件による入金予定SEQの取得
	 * 
	 * <pre>
	 * ページャーあり
	 * </pre>
	 * 
	 * @param conditions
	 * @param options
	 * @return IDリスト
	 */
	@Select
	List<Long> selectSeqByConditions(NyukinListSearchCondition conditions, SelectOptions options);

}
