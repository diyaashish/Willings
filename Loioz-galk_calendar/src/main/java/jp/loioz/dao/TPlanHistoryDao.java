package jp.loioz.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TPlanHistoryEntity;

/**
 * 利用プラン履歴のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TPlanHistoryDao {
	
	/**
	 * 無料プランの履歴（プラン履歴テーブルの1レコード目にのみ登録されている）を取得する。
	 * 
	 * @return
	 */
	@Select
	TPlanHistoryEntity selectFreePlanHistory();
	
	/**
	 * 無料プランの履歴を除く、全ての履歴を取得する。
	 * 
	 * @return
	 */
	@Select
	List<TPlanHistoryEntity> selectAllPlanHistoryExceptFreePlanHistory();
	
	/**
	 * 直近で登録された利用プラン履歴を取得する
	 * 
	 * @return
	 */
	@Select
	TPlanHistoryEntity selectLatestHistory();
	
	/**
	 * 直近で登録された指定のステータスの利用プラン履歴を取得する
	 * 
	 * @param statusCd
	 * @return
	 */
	@Select
	TPlanHistoryEntity selectLatestHistoryByStatus(String statusCd);
	
	/**
	 * 指定の日時よりものひとつ前に作成されたデータを取得する
	 * 
	 * @param targetDateTime
	 * @return
	 */
	@Select
	TPlanHistoryEntity selectOneBeforeCreateAtHistory(LocalDateTime targetDateTime);
	
	/**
	 * 全ての利用プラン履歴を取得する<br>
	 * 
	 * @return
	 */
	@Select
	List<TPlanHistoryEntity> selectAll();
	
	/**
	 * 指定のステータスで、指定範囲の間に作成された履歴情報を取得する
	 * 
	 * @param statusCd
	 * @param betweenStart
	 * @param betweenEnd
	 * @return
	 */
	@Select
	List<TPlanHistoryEntity> selectStatusAndCreatedAtIsBetweenHistory(String statusCd, LocalDateTime betweenStart, LocalDateTime betweenEnd);
	
	/**
	 * 指定のステータスで、指定範囲の間に作成された履歴情報を取得する。
	 * 並び順は履歴データの降順とする。
	 * 
	 * @param statusCdList
	 * @param betweenStart
	 * @param betweenEnd
	 * @return
	 */
	@Select
	List<TPlanHistoryEntity> selectStatusAndCreatedAtIsBetweenHistoryOrderSeq(List<String> statusCdList, LocalDateTime betweenStart, LocalDateTime betweenEnd);
	
	/**
	 * 1件の利用プラン履歴を登録する
	 *
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TPlanHistoryEntity entity);
	
 	/**
	 * 1件の利用プラン履歴を更新する
	 *
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TPlanHistoryEntity entity);
	
	/**
	 * 複数の利用プラン履歴を削除する
	 * 
	 * @param entityList
	 * @return 削除件数リスト
	 */
	@BatchDelete
	int[] batchDelete(List<TPlanHistoryEntity> entityList);
}
