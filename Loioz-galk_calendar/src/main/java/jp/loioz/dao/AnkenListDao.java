package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.bean.AnkenAitegataCntBean;
import jp.loioz.bean.AnkenListBean;
import jp.loioz.bean.AnkenTantoFlgBean;
import jp.loioz.bean.SaibanAitegataBean;
import jp.loioz.bean.SaibanListBean;
import jp.loioz.bean.SaibanSeqCustomerBean;
import jp.loioz.bean.SaibanTantoFlgBean;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.domain.condition.AnkenListSearchCondition;
import jp.loioz.domain.condition.AnkenListSortCondition;
import jp.loioz.domain.condition.SaibanListSearchCondition;
import jp.loioz.domain.condition.SaibanListSortCondition;

/**
 * 案件一覧用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface AnkenListDao {

	// =========================================================================
	// 案件一覧情報取得 メソッド
	// =========================================================================

	/**
	 * 検索条件に該当する案件データの件数を取得します
	 * 
	 * @param ankenListSearchCondition
	 * @param ankenListSortCondition
	 * @return
	 */
	default int selectAnkenListBySearchConditionsCount(AnkenListSearchCondition ankenListSearchCondition,
			AnkenListSortCondition ankenListSortCondition) {
		List<AnkenListBean> ankenListBeanList = this.selectAnkenListBySearchConditions(ankenListSearchCondition,
				ankenListSortCondition, true);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(ankenListBeanList)) {
			return 0;
		}

		return ankenListBeanList.get(0).getCount().intValue();
	}

	/**
	 * 【顧問取引先の案件】検索条件に該当する案件データの件数を取得します
	 * 
	 * @param ankenListSearchCondition
	 * @param ankenListSortCondition
	 * @return
	 */
	default int selectAdvisorAnkenListBySearchConditionsCount(AnkenListSearchCondition ankenListSearchCondition,
			AnkenListSortCondition ankenListSortCondition) {
		List<AnkenListBean> ankenListBeanList = this.selectAdvisorAnkenListBySearchConditions(ankenListSearchCondition,
				ankenListSortCondition, true);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(ankenListBeanList)) {
			return 0;
		}

		return ankenListBeanList.get(0).getCount().intValue();
	}

	/**
	 * 検索条件に該当する案件データを取得します
	 * 
	 * @param ankenListSearchCondition
	 * @param ankenListSortCondition
	 * @return
	 */
	default List<AnkenListBean> selectAnkenListBySearchConditions(AnkenListSearchCondition ankenListSearchCondition,
			AnkenListSortCondition ankenListSortCondition) {
		return this.selectAnkenListBySearchConditions(ankenListSearchCondition, ankenListSortCondition, false);
	}

	/**
	 * 【顧問取引先の案件】検索条件に該当する案件データを取得します
	 * 
	 * @param ankenListSearchCondition
	 * @param ankenListSortCondition
	 * @return
	 */
	default List<AnkenListBean> selectAdvisorAnkenListBySearchConditions(AnkenListSearchCondition ankenListSearchCondition,
			AnkenListSortCondition ankenListSortCondition) {
		return this.selectAdvisorAnkenListBySearchConditions(ankenListSearchCondition, ankenListSortCondition, false);
	}

	/**
	 * 検索条件に該当する案件データをoptionsで指定した分取得します（ページング用）
	 * 
	 * @param ankenListSearchCondition
	 * @param ankenListSortCondition
	 * @param options
	 * @return
	 */
	default List<AnkenListBean> selectAnkenListBySearchConditions(AnkenListSearchCondition ankenListSearchCondition,
			AnkenListSortCondition ankenListSortCondition, SelectOptions options) {
		return this.selectAnkenListBySearchConditions(ankenListSearchCondition, ankenListSortCondition, false, options);
	}

	/**
	 * 【顧問取引先の案件】検索条件に該当する案件データをoptionsで指定した分取得します（ページング用）
	 * 
	 * @param ankenListSearchCondition
	 * @param ankenListSortCondition
	 * @param options
	 * @return
	 */
	default List<AnkenListBean> selectAdvisorAnkenListBySearchConditions(AnkenListSearchCondition ankenListSearchCondition,
			AnkenListSortCondition ankenListSortCondition, SelectOptions options) {
		return this.selectAdvisorAnkenListBySearchConditions(ankenListSearchCondition, ankenListSortCondition, false, options);
	}

	/**
	 * クイック検索による
	 * 名簿一覧：すべてのデータ件数を取得する
	 * 
	 * @param quickSearchText
	 * @param ankenStatusList
	 * @param sortConditions
	 * @return
	 */
	default int selectAnkenListByQuickSearchCount(String quickSearchText, List<String> ankenStatusList, AnkenListSortCondition sortConditions) {
		List<AnkenListBean> ankenListBeanList = this.selectAnkenListByQuickSearch(quickSearchText, ankenStatusList, sortConditions, true);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(ankenListBeanList)) {
			return 0;
		}

		return ankenListBeanList.get(0).getCount().intValue();
	}
	
	/**
	 * クイック検索による
	 * 案件一覧：すべてのデータを取得する
	 * 
	 * @param quickSearchText
	 * @param ankenStatusList
	 * @param sortConditions
	 * @return
	 */
	default List<AnkenListBean> selectAnkenListByQuickSearch(String quickSearchText, List<String> ankenStatusList, AnkenListSortCondition sortConditions) {
		return this.selectAnkenListByQuickSearch(quickSearchText, ankenStatusList, sortConditions, false);
	}
	
	/**
	 * クイック検索による
	 * 案件一覧：すべてのデータを取得する（ページング用）
	 * 
	 * @param quickSearchText
	 * @param ankenStatusList
	 * @param sortConditions
	 * @param options
	 * @return
	 */
	default List<AnkenListBean> selectAnkenListByQuickSearch(String quickSearchText, List<String> ankenStatusList, AnkenListSortCondition sortConditions, SelectOptions options) {
		return this.selectAnkenListByQuickSearch(quickSearchText, ankenStatusList, sortConditions, false, options);
	}
	
	/**
	 * 案件一覧情報を取得します。<br>
	 * AnkenListBeanで定義してある相手方情報、担当弁護士、担当事務、担当フラグの情報は取得していません。<br>
	 * selectAnkenAitegataByAnkenId、selectAnkenTantoLaywerJimuById、selectTantoAnkenFlgByIdで取得してください。<br>
	 * countFlg がtrueの場合は、ListにAnkenListBeanが1つ格納されています。countフィールドから件数が取得できます。<br>
	 *
	 * @param ankenListSearchCondition 検索条件
	 * @param ankenListSortCondition ソート条件
	 * @param countFlg 件数取得フラグ true：件数を取得、 false：裁判データを取得
	 * @param options
	 * @return 案件一覧情報
	 */
	@Select
	List<AnkenListBean> selectAnkenListBySearchConditions(AnkenListSearchCondition ankenListSearchCondition,
			AnkenListSortCondition ankenListSortCondition, boolean countFlg, SelectOptions options);

	/**
	 * 案件一覧情報を取得します（options無し）。
	 * AnkenListBeanで定義してある相手方情報、担当弁護士、担当事務、担当フラグの情報は取得していません。別SQLで取得してください。<br>
	 * selectAnkenAitegataByAnkenId、selectAnkenTantoLaywerJimuById、selectTantoAnkenFlgByIdで取得してください。<br>
	 * countFlg がtrueの場合は、ListにAnkenListBeanが1つ格納されています。countフィールドから件数が取得できます。<br>
	 *
	 * @param ankenListSearchCondition 検索条件
	 * @param ankenListSortCondition ソート条件
	 * @param countFlg 件数取得フラグ true：件数を取得、 false：裁判データを取得
	 * @return 案件一覧情報
	 */
	@Select
	List<AnkenListBean> selectAnkenListBySearchConditions(AnkenListSearchCondition ankenListSearchCondition,
			AnkenListSortCondition ankenListSortCondition, boolean countFlg);

	/**
	 * 【顧問取引先の案件】案件一覧情報を取得します（options無し）。
	 * AnkenListBeanで定義してある相手方情報、担当弁護士、担当事務、担当フラグの情報は取得していません。別SQLで取得してください。<br>
	 * selectAnkenAitegataByAnkenId、selectAnkenTantoLaywerJimuById、selectTantoAnkenFlgByIdで取得してください。<br>
	 * countFlg がtrueの場合は、ListにAnkenListBeanが1つ格納されています。countフィールドから件数が取得できます。<br>
	 *
	 * @param ankenListSearchCondition 検索条件
	 * @param ankenListSortCondition ソート条件
	 * @param countFlg 件数取得フラグ true：件数を取得、 false：裁判データを取得
	 * @return 案件一覧情報
	 */
	@Select
	List<AnkenListBean> selectAdvisorAnkenListBySearchConditions(AnkenListSearchCondition ankenListSearchCondition,
			AnkenListSortCondition ankenListSortCondition, boolean countFlg);

	/**
	 * 【顧問取引先の案件】案件一覧情報を取得します。<br>
	 * AnkenListBeanで定義してある相手方情報、担当弁護士、担当事務、担当フラグの情報は取得していません。<br>
	 * selectAnkenAitegataByAnkenId、selectAnkenTantoLaywerJimuById、selectTantoAnkenFlgByIdで取得してください。<br>
	 * countFlg がtrueの場合は、ListにAnkenListBeanが1つ格納されています。countフィールドから件数が取得できます。<br>
	 *
	 * @param ankenListSearchCondition 検索条件
	 * @param ankenListSortCondition ソート条件
	 * @param countFlg 件数取得フラグ true：件数を取得、 false：裁判データを取得
	 * @param options
	 * @return 案件一覧情報
	 */
	@Select
	List<AnkenListBean> selectAdvisorAnkenListBySearchConditions(AnkenListSearchCondition ankenListSearchCondition,
			AnkenListSortCondition ankenListSortCondition, boolean countFlg, SelectOptions options);

	/**
	 * クイック検索による
	 * 案件一覧：すべての検索条件に一致するすべてのデータを取得する
	 * 
	 * <pre>
	 * ※SQLは下記と同様だが、SelectOptionsを指定しないのでページャによる件数制御を行わない
	 * {@link AnkenListDao#selectAnkenListByQuickSearch(String, List<String>, AnkenListSortCondition, boolean, SelectOptions)}
	 * <pre>
	 * 
	 * @param quickSearchText
	 * @param ankenStatusList
	 * @param sortConditions
	 * @param countFlg
	 * @return
	 */
	@Select
	List<AnkenListBean> selectAnkenListByQuickSearch(String quickSearchText, List<String> ankenStatusList, AnkenListSortCondition ankenListSortCondition, boolean countFlg);
	
	/**
	 * クイック検索による
	 * 案件一覧：すべてに表示する１ページ分の情報を取得する
	 * 
	 * @param quickSearchText
	 * @param ankenStatusList
	 * @param sortConditions
	 * @param countFlg
	 * @param options
	 * @return
	 */
	@Select
	List<AnkenListBean> selectAnkenListByQuickSearch(String quickSearchText, List<String> ankenStatusList, AnkenListSortCondition ankenListSortCondition, boolean countFlg, SelectOptions options);
	
	/**
	 * 案件IDに関する（案件の）相手方情報を取得します
	 * 
	 * @param ankenIdList
	 * @return
	 */
	@Select
	List<AnkenAitegataCntBean> selectAnkenAitegataByAnkenId(List<Long> ankenIdList);

	/**
	 * 案件IDに関する担当フラグ情報を取得します(accountSeqが担当する案件かどうか）
	 * 
	 * @param accountSeq
	 * @param ankenIdList
	 * @return
	 */
	@Select
	List<AnkenTantoFlgBean> selectTantoAnkenFlgById(Long accountSeq, List<Long> ankenIdList);

	// =========================================================================
	// 裁判一覧情報取得 メソッド
	// =========================================================================

	/**
	 * 検索条件に該当する刑事裁判データの件数を取得します
	 * 
	 * @param saibanListSearchCondition
	 * @param saibanListSortCondition
	 * @return
	 */
	default int selectKeijiSaibanListBySearchConditionsCount(SaibanListSearchCondition saibanListSearchCondition,
			SaibanListSortCondition saibanListSortCondition) {
		List<SaibanListBean> saibanListBeanList = this.selectKeijiSaibanListBySearchConditions(saibanListSearchCondition,
				saibanListSortCondition, true);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(saibanListBeanList)) {
			return 0;
		}

		return saibanListBeanList.get(0).getCount().intValue();
	}

	/**
	 * 検索条件に該当する刑事裁判データを取得します
	 * 
	 * @param saibanListSearchCondition
	 * @param saibanListSortCondition
	 * @return
	 */
	default List<SaibanListBean> selectKeijiSaibanListBySearchConditions(SaibanListSearchCondition saibanListSearchCondition,
			SaibanListSortCondition saibanListSortCondition) {
		return this.selectKeijiSaibanListBySearchConditions(saibanListSearchCondition, saibanListSortCondition, false);
	}

	/**
	 * 検索条件に該当する刑事裁判データをoptionsで指定した分取得します（ページング用）
	 * 
	 * @param saibanListSearchCondition
	 * @param saibanListSortCondition
	 * @param options
	 * @return
	 */
	default List<SaibanListBean> selectKeijiSaibanListBySearchConditions(SaibanListSearchCondition saibanListSearchCondition,
			SaibanListSortCondition saibanListSortCondition, SelectOptions options) {
		return this.selectKeijiSaibanListBySearchConditions(saibanListSearchCondition, saibanListSortCondition, false, options);
	}

	/**
	 * 刑事裁判一覧情報を取得します。<br>
	 * SaibanListBeanで定義してある顧客、担当弁護士、担当事務、担当フラグの情報は取得していません。<br>
	 * selectSaibanCustomerBySeq、selectSaibanTantoLaywerJimuBySeq、selectTantoSaibanFlgBySeqで取得してください。<br>
	 * countFlg がtrueの場合は、ListにSaibanListBeanが1つ格納されています。countフィールドから件数が取得できます。<br>
	 *
	 * @param saibanListSearchCondition 検索条件
	 * @param saibanListSortCondition ソート条件
	 * @param countFlg 件数取得フラグ true：件数を取得、 false：裁判データを取得
	 * @param options
	 * @return 裁判一覧情報
	 */
	@Select
	List<SaibanListBean> selectKeijiSaibanListBySearchConditions(SaibanListSearchCondition saibanListSearchCondition,
			SaibanListSortCondition saibanListSortCondition, boolean countFlg, SelectOptions options);

	/**
	 * 刑事裁判一覧情報を取得します（options無し）。
	 * SaibanListBeanで定義してある顧客、担当弁護士、担当事務、担当フラグの情報は取得していません。<br>
	 * selectSaibanCustomerBySeq、selectSaibanTantoLaywerJimuBySeq、selectTantoSaibanFlgBySeqで取得してください。<br>
	 * countFlg がtrueの場合は、ListにSaibanListBeanが1つ格納されています。countフィールドから件数が取得できます。<br>
	 *
	 * @param saibanListSearchCondition 検索条件
	 * @param saibanListSortCondition ソート条件
	 * @param countFlg 件数取得フラグ true：件数を取得、 false：裁判データを取得
	 * @return 裁判一覧情報
	 */
	@Select
	List<SaibanListBean> selectKeijiSaibanListBySearchConditions(SaibanListSearchCondition saibanListSearchCondition,
			SaibanListSortCondition saibanListSortCondition, boolean countFlg);

	/**
	 * 検索条件に該当する民事裁判データの件数を取得します
	 * 
	 * @param saibanListSearchCondition
	 * @param saibanListSortCondition
	 * @return
	 */
	default int selectMinjiSaibanListBySearchConditionsCount(SaibanListSearchCondition saibanListSearchCondition,
			SaibanListSortCondition saibanListSortCondition) {
		List<SaibanListBean> saibanListBeanList = this.selectMinjiSaibanListBySearchConditions(saibanListSearchCondition,
				saibanListSortCondition, true);

		// データが無い場合は0件
		if (LoiozCollectionUtils.isEmpty(saibanListBeanList)) {
			return 0;
		}

		return saibanListBeanList.get(0).getCount().intValue();
	}

	/**
	 * 検索条件に該当する民事裁判データを取得します
	 * 
	 * @param saibanListSearchCondition
	 * @param saibanListSortCondition
	 * @return
	 */
	default List<SaibanListBean> selectMinjiSaibanListBySearchConditions(SaibanListSearchCondition saibanListSearchCondition,
			SaibanListSortCondition saibanListSortCondition) {
		return this.selectMinjiSaibanListBySearchConditions(saibanListSearchCondition, saibanListSortCondition, false);
	}

	/**
	 * 検索条件に該当する民事裁判データをoptionsで指定した分取得します（ページング用）
	 * 
	 * @param saibanListSearchCondition
	 * @param saibanListSortCondition
	 * @param options
	 * @return
	 */
	default List<SaibanListBean> selectMinjiSaibanListBySearchConditions(SaibanListSearchCondition saibanListSearchCondition,
			SaibanListSortCondition saibanListSortCondition, SelectOptions options) {
		return this.selectMinjiSaibanListBySearchConditions(saibanListSearchCondition, saibanListSortCondition, false, options);
	}

	/**
	 * 民事裁判一覧情報を取得します。<br>
	 * SaibanListBeanで定義してある顧客、相手方、担当弁護士、担当事務、担当フラグの情報は取得していません。<br>
	 * selectSaibanCustomerBySeq、selectSaibanAitegataBySaibanSeq、selectSaibanTantoLaywerJimuBySeq、selectTantoSaibanFlgBySeqで取得してください。<br>
	 * countFlg がtrueの場合は、ListにSaibanListBeanが1つ格納されています。countフィールドから件数が取得できます。<br>
	 *
	 * @param saibanListSearchCondition 検索条件
	 * @param saibanListSortCondition ソート条件
	 * @param countFlg 件数取得フラグ true：件数を取得、 false：裁判データを取得
	 * @param options
	 * @return 裁判一覧情報
	 */
	@Select
	List<SaibanListBean> selectMinjiSaibanListBySearchConditions(SaibanListSearchCondition saibanListSearchCondition,
			SaibanListSortCondition saibanListSortCondition, boolean countFlg, SelectOptions options);

	/**
	 * 民事裁判一覧情報を取得します（options無し）。
	 * SaibanListBeanで定義してある顧客、相手方、担当弁護士、担当事務、担当フラグの情報は取得していません。<br>
	 * selectSaibanCustomerBySeq、selectSaibanAitegataBySaibanSeq、selectSaibanTantoLaywerJimuBySeq、selectTantoSaibanFlgBySeqで取得してください。<br>
	 * countFlg がtrueの場合は、ListにSaibanListBeanが1つ格納されています。countフィールドから件数が取得できます。<br>
	 *
	 * @param saibanListSearchCondition 検索条件
	 * @param saibanListSortCondition ソート条件
	 * @param countFlg 件数取得フラグ true：件数を取得、 false：裁判データを取得
	 * @return 裁判一覧情報
	 */
	@Select
	List<SaibanListBean> selectMinjiSaibanListBySearchConditions(SaibanListSearchCondition saibanListSearchCondition,
			SaibanListSortCondition saibanListSortCondition, boolean countFlg);

	/**
	 * 裁判SEQに関する（民事裁判の）相手方情報を取得します
	 * 
	 * @param saibanSeqList
	 * @return
	 */
	@Select
	List<SaibanAitegataBean> selectSaibanAitegataBySaibanSeq(List<Long> saibanSeqList);

	/**
	 * 裁判SEQに関する担当フラグ情報を取得します(accountSeqが担当する裁判かどうか）
	 * 
	 * @param accountSeq
	 * @param saibanSeqList
	 * @return
	 */
	@Select
	List<SaibanTantoFlgBean> selectTantoSaibanFlgBySeq(Long accountSeq, List<Long> saibanSeqList);

	/**
	 * 裁判SEQに関する（民事、刑事裁判の）顧客情報を取得します
	 * 
	 * @param saibanSeqList
	 * @return
	 */
	@Select
	List<SaibanSeqCustomerBean> selectSaibanCustomerBySeq(List<Long> saibanSeqList);

}