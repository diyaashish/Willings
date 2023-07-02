package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TGyomuHistoryAnkenEntity;

@ConfigAutowireable
@Dao
public interface TGyomuHistoryAnkenDao {

	/**
	 * 業務履歴SEQをキーとして、業務履歴-案件情報を取得します。
	 * 
	 * @param gyomuHistorySeq
	 * @return 業務履歴-案件情報
	 */
	default List<TGyomuHistoryAnkenEntity> selectByParentSeq(Long gyomuHistorySeq) {
		return selectByParentSeq(Arrays.asList(gyomuHistorySeq));
	}

	/**
	 * 業務履歴SEQをキーとして、業務履歴-案件情報を取得します。
	 * 
	 * @param gyomuHistorySeq
	 * @return 業務履歴-案件情報
	 */
	@Select
	List<TGyomuHistoryAnkenEntity> selectByParentSeq(List<Long> gyomuHistorySeq);

	/**
	 * 案件IDをキーとして、業務履歴SEQのリストを取得します。
	 * 
	 * @param ankenId 案件ID
	 * @return 業務履歴SEQリスト
	 */
	@Select
	List<Long> selectSeqByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーとして、業務履歴-案件情報を取得します。
	 * 
	 * @param customerId 顧客ID
	 * @return 業務履歴SEQリスト
	 */
	@Select
	List<TGyomuHistoryAnkenEntity> selectByAnkenId(Long ankenId);

	/**
	 * 案件IDと、業務履歴SEQのリストをキーとして、業務履歴-案件情報を取得します。
	 * 
	 * @param ankenId 案件ID
	 * @param gyomuHistorySeqList 業務履歴のSEQリスト
	 * @return 業務履歴-案件情報
	 */
	@Select
	List<TGyomuHistoryAnkenEntity> selectByAnkenIdAndGyomuHistorySeqList(Long ankenId, List<Long> gyomuHistorySeqList);

	/**
	 * 1件の業務履歴-案件を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TGyomuHistoryAnkenEntity entity);

	/**
	 * 複数登録
	 *
	 * @param entity
	 * @return
	 */
	@BatchInsert
	int[] insert(List<TGyomuHistoryAnkenEntity> entity);

	/**
	 * 複数削除（物理）
	 *
	 * @param entity
	 * @return
	 */
	@BatchDelete
	int[] delete(List<TGyomuHistoryAnkenEntity> entity);
}
