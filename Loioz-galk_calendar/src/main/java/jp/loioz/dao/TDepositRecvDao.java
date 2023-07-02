package jp.loioz.dao;

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
import org.springframework.lang.Nullable;

import jp.loioz.bean.DepositRecvDetailListBean;
import jp.loioz.bean.DepositRecvListBean;
import jp.loioz.bean.DepositRecvSummaryBean;
import jp.loioz.bean.TDepositRecvAndDocInvoiceDepositBean;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.domain.condition.DepositRecvDetailSortCondition;
import jp.loioz.domain.condition.DepositRecvListSearchCondition;
import jp.loioz.domain.condition.DepositRecvListSortCondition;
import jp.loioz.entity.TDepositRecvEntity;

/**
 * 預り金用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TDepositRecvDao {

	/**
	 * 預り金データを取得します。<br>
	 * 
	 * @param depositRecvSeq
	 * @return
	 */
	@Select
	TDepositRecvEntity selectDepositRecvByDepositRecvSeq(Long depositRecvSeq);

	/**
	 * 預り金データを取得します。<br>
	 * 
	 * @param depositRecvSeqList
	 * @return
	 */
	@Select
	List<TDepositRecvEntity> selectDepositRecvByDepositRecvSeqList(List<Long> depositRecvSeqList);

	/**
	 * 預り金データを取得します。<br>
	 * 
	 * @param ankenId 案件ID
	 * @param personId 名簿ID
	 * @param depositType 預り金種別（Null許容）
	 * @return
	 */
	@Select
	List<TDepositRecvEntity> selectDepositRecvByParams(Long ankenId, Long personId, @Nullable String depositType);

	/**
	 * 会計書類SEQをキーとして、預り金情報を取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TDepositRecvEntity> selectDepositRecvByAccgDocSeq(Long accgDocSeq);

	/**
	 * 預り金／実費管理画面の検索条件に該当する預り金／実費データ件数を取得します。
	 * 
	 * @param depositRecvListSearchCondition
	 * @param depositRecvListSortCondition
	 * @return
	 */
	default int selectByListSearchConditionsCount(DepositRecvListSearchCondition depositRecvListSearchCondition, DepositRecvListSortCondition depositRecvListSortCondition) {
		List<DepositRecvListBean> depositRecvList = this.selectDepositRecvListBySearchConditions(depositRecvListSearchCondition, depositRecvListSortCondition, true);
		
		// データ無い場合は0件
		if (LoiozCollectionUtils.isEmpty(depositRecvList)) {
			return 0;
		}
		
		return depositRecvList.get(0).getCount().intValue();
	}

	/**
	 * 預り金／実費管理画面のデータ取得（ページング用）。<br>
	 * 
	 * @param depositRecvListSearchCondition
	 * @param depositRecvListSortCondition
	 * @param options
	 * @return
	 */
	default List<DepositRecvListBean> selectByListSearchConditions(DepositRecvListSearchCondition depositRecvListSearchCondition, DepositRecvListSortCondition depositRecvListSortCondition, SelectOptions options) {
		return this.selectDepositRecvListBySearchConditions(depositRecvListSearchCondition, depositRecvListSortCondition, false, options);
	}

	/**
	 * 預り金／実費管理画面のデータ取得（全件）。<br>
	 * 
	 * @param depositRecvListSearchCondition
	 * @param depositRecvListSortCondition
	 * @return
	 */
	default List<DepositRecvListBean> selectByListSearchConditions(DepositRecvListSearchCondition depositRecvListSearchCondition, DepositRecvListSortCondition depositRecvListSortCondition) {
		return this.selectDepositRecvListBySearchConditions(depositRecvListSearchCondition, depositRecvListSortCondition, false);
	}

	/**
	 * 預り金管理画面の一覧データを１ページ分取得します。<br>
	 * DepositRecvListBeanで定義してある担当弁護士、担当事務は取得しません。<br>
	 * TAnkenTantoDao.selectAnkenTantoLaywerJimuByIdで取得してください。<br>
	 * 
	 * @param feeListSearchCondition
	 * @param feeListSortCondition
	 * @param countFlg
	 * @param options
	 * @return
	 */
	@Select
	List<DepositRecvListBean> selectDepositRecvListBySearchConditions(DepositRecvListSearchCondition depositRecvListSearchCondition,
			DepositRecvListSortCondition depositRecvListSortCondition, boolean countFlg, SelectOptions options);

	/**
	 * 預り金管理画面の一覧データを取得します。<br>
	 * DepositRecvListBeanで定義してある担当弁護士、担当事務は取得しません。<br>
	 * TAnkenTantoDao.selectAnkenTantoLaywerJimuByIdで取得してください。<br>
	 * 
	 * @param feeListSearchCondition
	 * @param feeListSortCondition
	 * @param countFlg
	 * @param options
	 * @return
	 */
	@Select
	List<DepositRecvListBean> selectDepositRecvListBySearchConditions(DepositRecvListSearchCondition depositRecvListSearchCondition,
			DepositRecvListSortCondition depositRecvListSortCondition, boolean countFlg);

	/**
	 * 預り金残高、入金額、出金額の合計を取得します。<br>
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	@Select
	DepositRecvSummaryBean selectSummaryByParams(Long ankenId, Long personId);

	/**
	 * 預り金明細情報のリストを取得します。<br>
	 * 
	 * @param ankenId
	 * @param personId
	 * @param depositRecvDetailSortCondition
	 * @return
	 */
	default List<DepositRecvDetailListBean> selectDepositRecvDetailListByParams(Long ankenId, Long personId,
			DepositRecvDetailSortCondition depositRecvDetailSortCondition) {
		return this.selectDepositRecvDetailListByParams(ankenId, personId, null, depositRecvDetailSortCondition);
	}

	/**
	 * 預り金明細情報を1件取得します。<br>
	 * 
	 * @param depositRecvSeq
	 * @return
	 */
	default DepositRecvDetailListBean selectDepositRecvDetailByDepositRecvSeq(Long depositRecvSeq) {
		List<DepositRecvDetailListBean> list = this.selectDepositRecvDetailListByParams(null, null, depositRecvSeq, new DepositRecvDetailSortCondition());
		return list.stream().findFirst().orElse(null);
	}

	/**
	 * 預り金明細一覧情報を取得します。<br>
	 * 
	 * @param ankenId
	 * @param personId
	 * @param depositRecvSeq
	 * @param depositRecvDetailSortCondition
	 * @return
	 */
	@Select
	List<DepositRecvDetailListBean> selectDepositRecvDetailListByParams(Long ankenId, Long personId,
			Long depositRecvSeq, DepositRecvDetailSortCondition depositRecvDetailSortCondition);

	/**
	 * 実費明細一覧用データ取得SQL
	 * 
	 * <pre>
	 * 実費明細書(PDF)作成時に利用することを想定
	 * </pre>
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	@Select
	List<TDepositRecvEntity> selectActualCostDetailByAnkenIdAndPersonId(Long ankenId, Long personId);

	/**
	 * 会計書類SEQをキーとして、預り金予定データを取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TDepositRecvEntity> selectScheduleDepositRecvEntityByCreatedAccgDocSeq(Long accgDocSeq);

	/**
	 * 預り金データと請求項目預り金マッピングに関するSEQを取得します。<br>
	 * 
	 * @param depositRecvSeqList
	 * @return
	 */
	@Select
	List<TDepositRecvAndDocInvoiceDepositBean> selectDepositRecvAndDocInvoiceDepositByDepositRecvSeqList(List<Long> depositRecvSeqList);

	/**
	 * 指定の会計書類で利用されている（入金or出金）の預り金データを取得する
	 * 
	 * @param usingAccgDocSeq 預り金が利用されている会計書類のSEQ
	 * @param depositType 入出金タイプ
	 * @return
	 */
	@Select
	List<TDepositRecvEntity> selectDepositUsedInAccgDocByParams(Long usingAccgDocSeq, String depositType);

	/**
	 * 名簿IDをキーとして、預り金情報を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TDepositRecvEntity> selectDepositRecvByPersonId(Long personId);

	/**
	 * 名簿IDをキーとして、預り金情報の件数を取得する
	 * 
	 * @param personId
	 * @return
	 */
	default int selectDepositRecvCountByPersonId(Long personId) {
		return selectDepositRecvByPersonId(personId).size();
	}

	/**
	 * 案件IDをキーとして、預り金情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<TDepositRecvEntity> selectDepositRecvByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーとして、預り金情報の件数を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	default int selectDepositRecvCountByAnkenId(Long ankenId) {
		return selectDepositRecvByAnkenId(ankenId).size();
	}

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TDepositRecvEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entities
	 * @return
	 */
	@BatchInsert
	int[] batchInsert(List<TDepositRecvEntity> entities);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TDepositRecvEntity entity);

	/**
	 * 複数更新
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] batchUpdate(List<TDepositRecvEntity> entityList);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TDepositRecvEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TDepositRecvEntity> entityList);
	
	/**
	 * 預り金データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTDepositRecv();
	
}
