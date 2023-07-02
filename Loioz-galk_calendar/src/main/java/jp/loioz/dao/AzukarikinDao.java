package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.domain.condition.AzukarikinSearchCondition;
import jp.loioz.dto.AzukarikinAnkenTantoListBean;
import jp.loioz.dto.AzukarikinListBean;

/**
 * 預り金・未収入金管理画面用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface AzukarikinDao {

	/**
	 * 預り金・未収金一覧の顧客IDを取得
	 * 
	 * <pre>
	 * ページャを含む
	 * </pre>
	 * 
	 * @param searchForm
	 * @param options
	 * @return 検索された顧客ID
	 */
	@Select
	List<Long> selectCustomerIdBySearchConditions(AzukarikinSearchCondition condition, SelectOptions options);

	/**
	 * 入力された検索条件と顧客IDから預かり金・未収金一覧情報を検索する
	 * 
	 * @param searchForm
	 * @param customerIdList
	 * @return 預り金・未収金一覧情報
	 */
	@Select
	List<AzukarikinListBean> selectBeansByCustomerIdList(List<Long> customerIdList);

	/**
	 * 検索結果に紐づく案件の件数
	 * 
	 * @param condition
	 * @return 案件件数
	 */
	@Select
	long selectAnkenCount(AzukarikinSearchCondition condition);

	/**
	 * 検索結果に紐づく売上計上先、担当弁護士、事務担当を検索する
	 * 
	 * @param condition
	 * @return List<AnkenTantoDto>
	 */
	@Select
	List<AzukarikinAnkenTantoListBean> selectTantoByAnkenIdList(List<Long> ankenIdConditionList);

}