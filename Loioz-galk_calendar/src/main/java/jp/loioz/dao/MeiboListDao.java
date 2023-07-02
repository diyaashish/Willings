package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.bean.meiboList.AdvisorMeiboListBean;
import jp.loioz.bean.meiboList.AllMeiboListBean;
import jp.loioz.bean.meiboList.BengoshiMeiboListBean;
import jp.loioz.bean.meiboList.CustomerAllMeiboListBean;
import jp.loioz.bean.meiboList.CustomerHojinMeiboListBean;
import jp.loioz.bean.meiboList.CustomerKojinMeiboListBean;
import jp.loioz.bean.meiboList.FudemameAdvisorMeiboListBean;
import jp.loioz.bean.meiboList.FudemameAllMeiboListBean;
import jp.loioz.bean.meiboList.FudemameBengoshiMeiboListBean;
import jp.loioz.bean.meiboList.FudemameCustomerAllMeiboListBean;
import jp.loioz.bean.meiboList.FudemameCustomerHojinMeiboListBean;
import jp.loioz.bean.meiboList.FudemameCustomerKojinMeiboListBean;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.domain.condition.meiboList.AdvisorMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.AdvisorMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.AllMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.AllMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.BengoshiMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.BengoshiMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.CustomerAllMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.CustomerAllMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.CustomerHojinMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.CustomerHojinMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.CustomerKojinMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.CustomerKojinMeiboListSortCondition;

@ConfigAutowireable
@Dao
public interface MeiboListDao {

	/**
	 * クイック検索による
	 * 名簿一覧：すべてのデータ件数を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default int selectAllMeiboByQuickSearchCount(String quickSearchText, AllMeiboListSortCondition sortConditions) {
		List<AllMeiboListBean> customerAllBeanList = this.selectAllMeiboByQuickSearch(quickSearchText, sortConditions, true);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(customerAllBeanList)) {
			return 0;
		}

		return customerAllBeanList.get(0).getCount().intValue();
	}

	/**
	 * クイック検索による
	 * 名簿一覧：すべてに表示する情報をすべて取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default List<AllMeiboListBean> selectAllMeiboByQuickSearch(String quickSearchText, AllMeiboListSortCondition sortConditions) {
		return this.selectAllMeiboByQuickSearch(quickSearchText, sortConditions, false);
	}

	/**
	 * クイック検索による
	 * 名簿一覧：すべてに表示する１ページ分の情報を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param options
	 * @return
	 */
	default List<AllMeiboListBean> selectAllMeiboByQuickSearch(String quickSearchText, AllMeiboListSortCondition sortConditions, SelectOptions options) {
		return this.selectAllMeiboByQuickSearch(quickSearchText, sortConditions, false, options);
	}

	/**
	 * クイック検索による
	 * 名簿一覧：すべてに表示するKey情報をすべて取得する
	 * 
	 * <pre>
	 * ※SQLは下記と同様だが、SelectOptionsを指定しないのでページャによる件数制御を行わない
	 * {@link jp.loioz.dao.MeiboListDao#selectAllMeiboByConditions(AllMeiboListSearchCondition, AllMeiboListSortCondition, SelectOptions) selectAllMeiboByConditions(AllMeiboListSearchCondition searchConditions, AllMeiboListSortCondition sortConditions, SelectOptions options)}
	 * <pre>
	 * 
	 * ※ 本SQLを修正する際は、selectAllMeiboFudemameBeanByQuickSearch.sqlを修正すること
	 *
	 * @param quickSearchText
	 * @param sortConditions
	 * @param countFlg
	 * @return
	 */
	@Select
	List<AllMeiboListBean> selectAllMeiboByQuickSearch(String quickSearchText, AllMeiboListSortCondition sortConditions, boolean countFlg);

	/**
	 * クイック検索による
	 * 名簿一覧：すべてに表示する１ページ分のKey情報を取得する
	 * （顧客と関与者を一つのSEQで取得する必要があるので取得データは顧客IDと関与者ID）
	 * 
	 * ※ 本SQLを修正する際は、selectAllMeiboFudemameBeanByQuickSearch.sqlを修正すること
	 * 
	 * @param quickSearchText
	 * @param sortConditions
	 * @param countFlg
	 * @param options
	 * @return
	 */
	@Select
	List<AllMeiboListBean> selectAllMeiboByQuickSearch(String quickSearchText, AllMeiboListSortCondition sortConditions, boolean countFlg, SelectOptions options);

	/**
	 * 名簿一覧：すべての筆まめCSV用SQL
	 * 
	 * ※ 本SQLは、検索条件と表示順を画面表示条件と同様にする必要があるので
	 * selectAllMeiboByQuickSearch.sqlを修正する際にはこのSQLも修正すること
	 *
	 * @param quickSearchText
	 * @param sortConditions
	 * @param countFlg
	 * @return
	 */
	@Select
	List<FudemameAllMeiboListBean> selectAllMeiboFudemameBeanByQuickSearch(String quickSearchText, AllMeiboListSortCondition sortConditions, boolean countFlg);

	/**
	 * 名簿一覧：すべてのデータ件数を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default int selectAllMeiboByConditionsCount(AllMeiboListSearchCondition searchConditions, AllMeiboListSortCondition sortConditions) {
		List<AllMeiboListBean> customerAllBeanList = this.selectAllMeiboByConditions(searchConditions, sortConditions, true);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(customerAllBeanList)) {
			return 0;
		}

		return customerAllBeanList.get(0).getCount().intValue();
	}

	/**
	 * 名簿一覧：すべてに表示する情報をすべて取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default List<AllMeiboListBean> selectAllMeiboByConditions(AllMeiboListSearchCondition searchConditions, AllMeiboListSortCondition sortConditions) {
		return this.selectAllMeiboByConditions(searchConditions, sortConditions, false);
	}

	/**
	 * 名簿一覧：すべてに表示する１ページ分の情報を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param options
	 * @return
	 */
	default List<AllMeiboListBean> selectAllMeiboByConditions(AllMeiboListSearchCondition searchConditions, AllMeiboListSortCondition sortConditions, SelectOptions options) {
		return this.selectAllMeiboByConditions(searchConditions, sortConditions, false, options);
	}

	/**
	 * 名簿一覧：すべてに表示するKey情報をすべて取得する
	 * 
	 * <pre>
	 * ※SQLは下記と同様だが、SelectOptionsを指定しないのでページャによる件数制御を行わない
	 * {@link jp.loioz.dao.MeiboListDao#selectAllMeiboByConditions(AllMeiboListSearchCondition, AllMeiboListSortCondition, boolean, SelectOptions) selectAllMeiboByConditions(AllMeiboListSearchCondition searchConditions, AllMeiboListSortCondition sortConditions, boolean countFlg, SelectOptions
	 * options)}
	 * <pre>
	 * 
	 * ※ 本SQLを修正する際は、selectAllMeiboFudemameBeanByConditions.sqlを修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param countFlg
	 * @return
	 */
	@Select
	List<AllMeiboListBean> selectAllMeiboByConditions(AllMeiboListSearchCondition searchConditions, AllMeiboListSortCondition sortConditions, boolean countFlg);

	/**
	 * 名簿一覧：すべてに表示する１ページ分のKey情報を取得する
	 * （顧客と関与者を一つのSEQで取得する必要があるので取得データは顧客IDと関与者ID）
	 * 
	 * ※ 本SQLを修正する際は、selectAllMeiboFudemameBeanByConditions.sqlを修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param countFlg
	 * @param options
	 * @return
	 */
	@Select
	List<AllMeiboListBean> selectAllMeiboByConditions(AllMeiboListSearchCondition searchConditions, AllMeiboListSortCondition sortConditions, boolean countFlg, SelectOptions options);

	/**
	 * 名簿一覧：すべての筆まめCSV用SQL
	 * 
	 * ※ 本SQLは、検索条件と表示順を画面表示条件と同様にする必要があるので
	 * selectAllMeiboByConditions.sqlを修正する際にはこのSQLも修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param countFlg
	 * @return
	 */
	@Select
	List<FudemameAllMeiboListBean> selectAllMeiboFudemameBeanByConditions(AllMeiboListSearchCondition searchConditions, AllMeiboListSortCondition sortConditions, boolean countFlg);

	/**
	 * 名簿一覧：個人のデータ件数を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default int selectCustomerKojinMeiboByConditionsCount(CustomerKojinMeiboListSearchCondition searchConditions, CustomerKojinMeiboListSortCondition sortConditions) {
		List<CustomerKojinMeiboListBean> customerAllBeanList = this.selectCustomerKojinMeiboByConditions(searchConditions, sortConditions, true);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(customerAllBeanList)) {
			return 0;
		}

		return customerAllBeanList.get(0).getCount().intValue();
	}

	/**
	 * 名簿一覧：個人に表示する情報をすべて取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default List<CustomerKojinMeiboListBean> selectCustomerKojinMeiboByConditions(CustomerKojinMeiboListSearchCondition searchConditions, CustomerKojinMeiboListSortCondition sortConditions) {
		return this.selectCustomerKojinMeiboByConditions(searchConditions, sortConditions, false);
	}

	/**
	 * 名簿一覧：個人に表示する１ページ分の情報を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param options
	 * @return
	 */
	default List<CustomerKojinMeiboListBean> selectCustomerKojinMeiboByConditions(CustomerKojinMeiboListSearchCondition searchConditions, CustomerKojinMeiboListSortCondition sortConditions, SelectOptions options) {
		return this.selectCustomerKojinMeiboByConditions(searchConditions, sortConditions, false, options);
	}

	/**
	 * 名簿一覧：個人顧客に表示するKey情報をすべて取得する
	 * 
	 * <pre>
	 * ※SQLは下記と同様だが、SelectOptionsを指定しないのでページャによる件数制御を行わない
	 * {@link jp.loioz.dao.MeiboListDao#selectCustomerKojinMeiboByConditions(CustomerKojinMeiboListSearchCondition searchConditions, CustomerKojinMeiboListSortCondition sortConditions, SelectOptions options)
	 * selectCustomerHojinMeiboByConditions(CustomerKojinMeiboListSearchCondition searchConditions, CustomerKojinMeiboListSortCondition sortConditions, SelectOptions options)}
	 * <pre>
	 * 
	 * ※ 本SQLを修正する際は、selectCustomerKojinMeiboFudemameBeanByConditions.sqlを修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param countFlg
	 * @return
	 */
	@Select
	List<CustomerKojinMeiboListBean> selectCustomerKojinMeiboByConditions(CustomerKojinMeiboListSearchCondition searchConditions, CustomerKojinMeiboListSortCondition sortConditions, boolean countFlg);

	/**
	 * 名簿一覧：個人顧客に表示する１ページ分の顧客ID情報を取得する
	 * 
	 * ※ 本SQLを修正する際は、selectCustomerKojinMeiboFudemameBeanByConditions.sqlを修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param countFlg
	 * @param options
	 * @return 顧客ID
	 */
	@Select
	List<CustomerKojinMeiboListBean> selectCustomerKojinMeiboByConditions(CustomerKojinMeiboListSearchCondition searchConditions, CustomerKojinMeiboListSortCondition sortConditions, boolean countFlg, SelectOptions options);

	/**
	 * 名簿一覧：個人顧客の筆まめCSVに出力する情報をすべて取得する
	 *
	 * ※ 本SQLは、検索条件と表示順を画面表示条件と同様にする必要があるので
	 * selectCustomerKojinMeiboByConditions.sqlを修正する際にはこのSQLも修正すること
	 *
	 * @param searchConditions
	 * @param sortConditions
	 * @param countFlg
	 * @return
	 */
	@Select
	List<FudemameCustomerKojinMeiboListBean> selectCustomerKojinMeiboFudemameBeanByConditions(CustomerKojinMeiboListSearchCondition searchConditions, CustomerKojinMeiboListSortCondition sortConditions, boolean countFlg);

	/**
	 * 名簿一覧：法人のデータ件数を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default int selectCustomerHojinMeiboByConditionsCount(CustomerHojinMeiboListSearchCondition searchConditions, CustomerHojinMeiboListSortCondition sortConditions) {
		List<CustomerHojinMeiboListBean> customerAllBeanList = this.selectCustomerHojinMeiboByConditions(searchConditions, sortConditions, true);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(customerAllBeanList)) {
			return 0;
		}

		return customerAllBeanList.get(0).getCount().intValue();
	}

	/**
	 * 名簿一覧：法人に表示する情報をすべて取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default List<CustomerHojinMeiboListBean> selectCustomerHojinMeiboByConditions(CustomerHojinMeiboListSearchCondition searchConditions, CustomerHojinMeiboListSortCondition sortConditions) {
		return this.selectCustomerHojinMeiboByConditions(searchConditions, sortConditions, false);
	}

	/**
	 * 名簿一覧：法人に表示する１ページ分の情報を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param options
	 * @return
	 */
	default List<CustomerHojinMeiboListBean> selectCustomerHojinMeiboByConditions(CustomerHojinMeiboListSearchCondition searchConditions, CustomerHojinMeiboListSortCondition sortConditions, SelectOptions options) {
		return this.selectCustomerHojinMeiboByConditions(searchConditions, sortConditions, false, options);
	}

	/**
	 * 名簿一覧：法人顧客に表示するKey情報をすべて取得する
	 * 
	 * <pre>
	 * ※SQLは下記と同様だが、SelectOptionsを指定しないのでページャによる件数制御を行わない
	 * {@link jp.loioz.dao.MeiboListDao#selectCustomerHojinMeiboByConditions(CustomerHojinMeiboListSearchCondition searchConditions, CustomerHojinMeiboListSortCondition sortConditions, SelectOptions options)
	 * selectCustomerKojinMeiboByConditions(CustomerHojinMeiboListSearchCondition searchConditions, CustomerHojinMeiboListSortCondition sortConditions, SelectOptions options)}
	 * <pre>
	 * 
	 * ※ 本SQLを修正する際は、selectCustomerKojinMeiboFudemameBeanByConditions.sqlを修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param countFlg
	 * @return
	 */
	@Select
	List<CustomerHojinMeiboListBean> selectCustomerHojinMeiboByConditions(CustomerHojinMeiboListSearchCondition searchConditions, CustomerHojinMeiboListSortCondition sortConditions, boolean countFlg);

	/**
	 * 名簿一覧：法人顧客に表示する１ページ分の顧客ID情報を取得する
	 * 
	 * ※ 本SQLを修正する際は、selectCustomerKojinMeiboFudemameBeanByConditions.sqlを修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param countFlg
	 * @param options
	 * @return 顧客ID
	 */
	@Select
	List<CustomerHojinMeiboListBean> selectCustomerHojinMeiboByConditions(CustomerHojinMeiboListSearchCondition searchConditions, CustomerHojinMeiboListSortCondition sortConditions, boolean countFlg, SelectOptions options);

	/**
	 * 名簿一覧：法人顧客の筆まめCSVに出力する情報をすべて取得する
	 *
	 * ※ 本SQLは、検索条件と表示順を画面表示条件と同様にする必要があるので
	 * selectCustomerHojinMeiboByConditions.sqlを修正する際にはこのSQLも修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param countFlg
	 * @return
	 */
	@Select
	List<FudemameCustomerHojinMeiboListBean> selectCustomerHojinMeiboFudemameBeanByConditions(CustomerHojinMeiboListSearchCondition searchConditions, CustomerHojinMeiboListSortCondition sortConditions, boolean countFlg);

	/**
	 * 名簿一覧：顧客のデータ件数を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default int selectCustomerAllMeiboByConditionsCount(CustomerAllMeiboListSearchCondition searchConditions, CustomerAllMeiboListSortCondition sortConditions) {
		List<CustomerAllMeiboListBean> customerAllBeanList = this.selectCustomerAllMeiboByConditions(searchConditions, sortConditions, true);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(customerAllBeanList)) {
			return 0;
		}

		return customerAllBeanList.get(0).getCount().intValue();
	}

	/**
	 * 名簿一覧：顧客に表示する情報をすべて取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default List<CustomerAllMeiboListBean> selectCustomerAllMeiboByConditions(CustomerAllMeiboListSearchCondition searchConditions, CustomerAllMeiboListSortCondition sortConditions) {
		return this.selectCustomerAllMeiboByConditions(searchConditions, sortConditions, false);
	}

	/**
	 * 名簿一覧：顧客に表示する１ページ分の情報を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param options
	 * @return
	 */
	default List<CustomerAllMeiboListBean> selectCustomerAllMeiboByConditions(CustomerAllMeiboListSearchCondition searchConditions, CustomerAllMeiboListSortCondition sortConditions, SelectOptions options) {
		return this.selectCustomerAllMeiboByConditions(searchConditions, sortConditions, false, options);
	}

	/**
	 * 名簿一覧：顧客に表示する情報をすべて取得する
	 * 
	 * ※ 本SQLを修正する際は、selectCustomerAllMeiboFudemameBeanByConditions.sqlを修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	@Select
	List<CustomerAllMeiboListBean> selectCustomerAllMeiboByConditions(CustomerAllMeiboListSearchCondition searchConditions, CustomerAllMeiboListSortCondition sortConditions, boolean countFlg);

	/**
	 * 名簿一覧：顧客に表示する１ページ分の情報を取得する
	 * 
	 * ※ 本SQLを修正する際は、selectCustomerAllMeiboFudemameBeanByConditions.sqlを修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param options
	 * @return
	 */
	@Select
	List<CustomerAllMeiboListBean> selectCustomerAllMeiboByConditions(CustomerAllMeiboListSearchCondition searchConditions, CustomerAllMeiboListSortCondition sortConditions, boolean countFlg, SelectOptions options);

	/**
	 * 名簿一覧：顧客の筆まめCSV出力データをすべて取得する
	 * 
	 * ※ 本SQLは、検索条件と表示順を画面表示条件と同様にする必要があるので
	 * selectCustomerAllMeiboByConditions.sqlを修正する際にはこのSQLも修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param countFlg
	 * @return
	 */
	@Select
	List<FudemameCustomerAllMeiboListBean> selectCustomerAllMeiboFudemameBeanByConditions(CustomerAllMeiboListSearchCondition searchConditions, CustomerAllMeiboListSortCondition sortConditions, boolean countFlg);

	/**
	 * 名簿一覧：顧問のデータ件数を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default int selectAdvisorMeiboByConditionsCount(AdvisorMeiboListSearchCondition searchConditions, AdvisorMeiboListSortCondition sortConditions) {
		List<AdvisorMeiboListBean> advisorMeiboBeanList = this.selectAdvisorMeiboByConditions(searchConditions, sortConditions, true);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(advisorMeiboBeanList)) {
			return 0;
		}

		return advisorMeiboBeanList.get(0).getCount().intValue();
	}

	/**
	 * 名簿一覧：顧問に表示する情報をすべて取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default List<AdvisorMeiboListBean> selectAdvisorMeiboByConditions(AdvisorMeiboListSearchCondition searchConditions, AdvisorMeiboListSortCondition sortConditions) {
		return this.selectAdvisorMeiboByConditions(searchConditions, sortConditions, false);
	}

	/**
	 * 名簿一覧：顧問に表示する１ページ分の情報を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param options
	 * @return
	 */
	default List<AdvisorMeiboListBean> selectAdvisorMeiboByConditions(AdvisorMeiboListSearchCondition searchConditions, AdvisorMeiboListSortCondition sortConditions, SelectOptions options) {
		return this.selectAdvisorMeiboByConditions(searchConditions, sortConditions, false, options);
	}

	/**
	 * 名簿一覧：顧問に表示する情報をすべて取得する
	 * 
	 * ※ 本SQLを修正する際は、selectAdvisorMeiboFudemameBeanByConditions.sqlを修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	@Select
	List<AdvisorMeiboListBean> selectAdvisorMeiboByConditions(AdvisorMeiboListSearchCondition searchConditions, AdvisorMeiboListSortCondition sortConditions, boolean countFlg);

	/**
	 * 名簿一覧：顧問に表示する１ページ分の情報を取得する
	 * 
	 * ※ 本SQLを修正する際は、selectAdvisorMeiboFudemameBeanByConditions.sqlを修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param options
	 * @return
	 */
	@Select
	List<AdvisorMeiboListBean> selectAdvisorMeiboByConditions(AdvisorMeiboListSearchCondition searchConditions, AdvisorMeiboListSortCondition sortConditions, boolean countFlg, SelectOptions options);

	/**
	 * 名簿一覧：顧問の筆まめCSV出力データをすべて取得する
	 * 
	 * ※ 本SQLは、検索条件と表示順を画面表示条件と同様にする必要があるので
	 * selectAdvisorMeiboByConditions.sqlを修正する際にはこのSQLも修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param countFlg
	 * @return
	 */
	@Select
	List<FudemameAdvisorMeiboListBean> selectAdvisorMeiboFudemameBeanByConditions(AdvisorMeiboListSearchCondition searchConditions, AdvisorMeiboListSortCondition sortConditions, boolean countFlg);

	/**
	 * 名簿一覧：弁護士のデータ件数を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default int selectBengoshiMeiboByConditionsCount(BengoshiMeiboListSearchCondition searchConditions, BengoshiMeiboListSortCondition sortConditions) {
		List<BengoshiMeiboListBean> bengoshiBeanList = this.selectBengoshiMeiboByConditions(searchConditions, sortConditions, true);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(bengoshiBeanList)) {
			return 0;
		}

		return bengoshiBeanList.get(0).getCount().intValue();
	}

	/**
	 * 名簿一覧：弁護士に表示する情報をすべて取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	default List<BengoshiMeiboListBean> selectBengoshiMeiboByConditions(BengoshiMeiboListSearchCondition searchConditions, BengoshiMeiboListSortCondition sortConditions) {
		return this.selectBengoshiMeiboByConditions(searchConditions, sortConditions, false);
	}

	/**
	 * 名簿一覧：弁護士に表示する１ページ分の情報を取得する
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param options
	 * @return
	 */
	default List<BengoshiMeiboListBean> selectBengoshiMeiboByConditions(BengoshiMeiboListSearchCondition searchConditions, BengoshiMeiboListSortCondition sortConditions, SelectOptions options) {
		return this.selectBengoshiMeiboByConditions(searchConditions, sortConditions, false, options);
	}

	/**
	 * 名簿一覧：弁護士に表示する情報をすべて取得する
	 * 
	 * ※ 本SQLを修正する際は、selectBengoshiMeiboFudemameBeanByConditions.sqlを修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	@Select
	List<BengoshiMeiboListBean> selectBengoshiMeiboByConditions(BengoshiMeiboListSearchCondition searchConditions, BengoshiMeiboListSortCondition sortConditions, boolean countFlg);

	/**
	 * 名簿一覧：弁護士に表示する１ページ分の情報を取得する
	 * 
	 * ※ 本SQLを修正する際は、selectBengoshiMeiboFudemameBeanByConditions.sqlを修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param options
	 * @return
	 */
	@Select
	List<BengoshiMeiboListBean> selectBengoshiMeiboByConditions(BengoshiMeiboListSearchCondition searchConditions, BengoshiMeiboListSortCondition sortConditions, boolean countFlg, SelectOptions options);

	/**
	 * 名簿一覧：弁護士の筆まめCSV出力データをすべて取得する
	 * 
	 * ※ 本SQLは、検索条件と表示順を画面表示条件と同様にする必要があるので
	 * selectBengoshiMeiboByConditions.sqlを修正する際にはこのSQLも修正すること
	 * 
	 * @param searchConditions
	 * @param sortConditions
	 * @param countFlg
	 * @return
	 */
	@Select
	List<FudemameBengoshiMeiboListBean> selectBengoshiMeiboFudemameBeanByConditions(BengoshiMeiboListSearchCondition searchConditions, BengoshiMeiboListSortCondition sortConditions, boolean countFlg);

}
