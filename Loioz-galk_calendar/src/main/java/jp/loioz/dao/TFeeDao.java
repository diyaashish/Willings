package jp.loioz.dao;

import java.util.Collections;
import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.bean.AccgFeeSumListBean;
import jp.loioz.bean.FeeDetailListBean;
import jp.loioz.bean.FeeListBean;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.domain.condition.FeeDetailSortCondition;
import jp.loioz.domain.condition.FeeListSearchCondition;
import jp.loioz.domain.condition.FeeListSortCondition;
import jp.loioz.entity.TFeeEntity;

/**
 * 報酬用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TFeeDao {

	/**
	 * 報酬データを取得します。<br>
	 * 
	 * @param feeSeq
	 * @return
	 */
	@Select
	TFeeEntity selectFeeByFeeSeq(Long feeSeq);

	/**
	 * 報酬データを取得します。<br>
	 * 
	 * @param feeSeqList
	 * @return
	 */
	@Select
	List<TFeeEntity> selectFeeByFeeSeqList(List<Long> feeSeqList);

	/**
	 * 会計書類SEQをキーとして、報酬情報を取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TFeeEntity> selectFeeEntityByAccgDocSeq(Long accgDocSeq);

	/**
	 * 案件ID、名簿IDをキーとして報酬情報を取得する
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	@Select
	List<TFeeEntity> selectFeeEntityByParams(Long ankenId, Long personId);

	/**
	 * 報酬管理画面の検索条件に該当する報酬データ件数を取得します。
	 * 
	 * @param feeListSearchCondition
	 * @param feeListSortCondition
	 * @return
	 */
	default int selectByListSearchConditionsCount(FeeListSearchCondition feeListSearchCondition, FeeListSortCondition feeListSortCondition) {
		List<FeeListBean> feeList = this.selectByListSearchConditions(feeListSearchCondition, feeListSortCondition, true);
		
		// データ無い場合は0件
		if (LoiozCollectionUtils.isEmpty(feeList)) {
			return 0;
		}
		
		return feeList.get(0).getCount().intValue();
	}

	/**
	 * 報酬管理画面のデータ取得（ページング用）。<br>
	 * 
	 * @param feeListSearchCondition
	 * @param feeListSortCondition
	 * @param options
	 * @return
	 */
	default List<FeeListBean> selectByListSearchConditions(FeeListSearchCondition feeListSearchCondition, FeeListSortCondition feeListSortCondition, SelectOptions options) {
		return this.selectByListSearchConditions(feeListSearchCondition, feeListSortCondition, false, options);
	}

	/**
	 * 報酬管理画面のデータ取得（全件）。<br>
	 * 
	 * @param feeListSearchCondition
	 * @param feeListSortCondition
	 * @return
	 */
	default List<FeeListBean> selectByListSearchConditions(FeeListSearchCondition feeListSearchCondition, FeeListSortCondition feeListSortCondition) {
		return this.selectByListSearchConditions(feeListSearchCondition, feeListSortCondition, false);
	}

	/**
	 * 報酬管理画面の一覧データを１ページ分取得します。<br>
	 * FeeListBeanで定義してある担当弁護士、担当事務は取得しません。<br>
	 * TAnkenTantoDao.selectAnkenTantoLaywerJimuByIdで取得してください。<br>
	 * 
	 * @param feeListSearchCondition
	 * @param feeListSortCondition
	 * @param countFlg
	 * @param options
	 * @return
	 */
	@Select
	List<FeeListBean> selectByListSearchConditions(FeeListSearchCondition feeListSearchCondition,
			FeeListSortCondition feeListSortCondition, boolean countFlg, SelectOptions options);

	/**
 	 * 報酬管理画面の一覧データを取得します。<br>
	 * FeeListBeanで定義してある担当弁護士、担当事務は取得しません。<br>
	 * TAnkenTantoDao.selectAnkenTantoLaywerJimuByIdで取得してください。<br>
	 * 
	 * @param feeListSearchCondition
	 * @param feeListSortCondition
	 * @param countFlg
	 * @return
	 */
	@Select
	List<FeeListBean> selectByListSearchConditions(FeeListSearchCondition feeListSearchCondition,
			FeeListSortCondition feeListSortCondition, boolean countFlg);

	/**
	 * 報酬明細情報を取得します。<br>
	 * 
	 * @param ankenId
	 * @param personId
	 * @param feeSeq
	 * @param feeSeqList
	 * @param feeDetailSortCondition
	 * @return
	 */
	@Select
	List<FeeDetailListBean> selectFeeDetailDataByParams(Long ankenId, Long personId, Long feeSeq, List<Long> feeSeqList,
			FeeDetailSortCondition feeDetailSortCondition);

	/**
	 * 報酬明細一覧情報を取得します。<br>
	 * 
	 * @param ankenId
	 * @param personId
	 * @param feeDetailSortCondition
	 * @return
	 */
	default List<FeeDetailListBean> selectFeeDetailListByParams(Long ankenId, Long personId,
			FeeDetailSortCondition feeDetailSortCondition) {
		return this.selectFeeDetailDataByParams(ankenId, personId, null, Collections.emptyList(), feeDetailSortCondition);
	}

	/**
	 * 報酬明細情報を1件取得します。<br>
	 * 
	 * @param feeSeq
	 * @return
	 */
	default FeeDetailListBean selectFeeDetailByFeeSeq(Long feeSeq) {
		List<FeeDetailListBean> list = this.selectFeeDetailDataByParams(null, null, feeSeq, Collections.emptyList(), new FeeDetailSortCondition());
		return list.stream().findFirst().orElse(null);
	}

	/**
	 * 報酬明細情報のリストを取得します。<br>
	 * 
	 * @param feeSeqList
	 * @return
	 */
	default List<FeeDetailListBean> selectFeeDetailByFeeSeqList(List<Long> feeSeqList) {
		List<FeeDetailListBean> list = this.selectFeeDetailDataByParams(null, null, null, feeSeqList, new FeeDetailSortCondition());
		return list;
	}

	/**
	 * <pre>
	 * 案件ID、名簿IDに紐づく未精算報酬の中から どの請求書／精算書でも使用されていない報酬と 会計書類SEQの請求書／精算書で使用されている報酬を取得する。
	 * 
	 * <pre>
	 * 
	 * @param ankenId 案件ID
	 * @param personId 名簿ID
	 * @param accgDocSeq 会計書類SEQ
	 * @return
	 */
	@Select
	List<FeeDetailListBean> selectFeeNotUsedInAnotherAccgDocByParams(Long ankenId, Long personId, Long accgDocSeq);

	/**
	 * 名簿IDと案件IDをキーとして、売上明細に紐づく「入金済み」ステータスの報酬情報をすべて取得する
	 * 
	 * @param personId
	 * @param ankenId
	 * @return
	 */
	@Select
	List<TFeeEntity> selectDepositStatusSalesFeeByPersonIdAndAnkenId(Long personId, Long ankenId);

	/**
	 * 報酬（入金ステータス毎）の合計を取得します。
	 * 
	 * @param personId
	 * @param ankenId
	 * @return
	 */
	@Select
	List<AccgFeeSumListBean> selectSumFeeStatusByPersonIdAndAnkenId(Long personId, Long ankenId);

	/**
	 * 名簿IDをキーとして、報酬情報を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TFeeEntity> selectFeeByPersonId(Long personId);

	/**
	 * 名簿IDをキーとして、報酬情報の件数を取得する
	 * 
	 * @param personId
	 * @return
	 */
	default int selectFeeCountByPersonId(Long personId) {
		return selectFeeByPersonId(personId).size();
	}

	/**
	 * 案件IDをキーとして、報酬情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<TFeeEntity> selectFeeByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーとして、報酬情報の件数を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	default int selectFeeCountByAnkenId(Long ankenId) {
		return selectFeeByAnkenId(ankenId).size();
	}

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TFeeEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchInsert
	int[] batchInsert(List<TFeeEntity> entityList);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TFeeEntity entity);

	/**
	 * 複数更新
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] batchUpdate(List<TFeeEntity> entityList);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TFeeEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TFeeEntity> entityList);

	/**
	 * 報酬データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTFee();

}
