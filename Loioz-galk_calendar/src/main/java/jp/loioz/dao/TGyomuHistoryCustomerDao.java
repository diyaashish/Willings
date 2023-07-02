package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TGyomuHistoryCustomerEntity;

@ConfigAutowireable
@Dao
public interface TGyomuHistoryCustomerDao {

	/**
	 * 業務履歴SEQをキーとして、業務履歴-顧客情報を取得します。
	 * 
	 * @param gyomuHistorySeq
	 * @return 複数の業務履歴-顧客情報
	 */
	default List<TGyomuHistoryCustomerEntity> selectByParentSeq(Long gyomuHistorySeq) {
		return selectByParentSeq(Arrays.asList(gyomuHistorySeq));
	}

	/**
	 * 業務履歴SEQをキーとして、業務履歴-顧客情報を取得します。
	 * 
	 * @param gyomuHistorySeq
	 * @return 複数の業務履歴-顧客情報
	 */
	@Select
	List<TGyomuHistoryCustomerEntity> selectByParentSeq(List<Long> gyomuHistorySeq);

	/**
	 * 顧客IDをキーとして、業務履歴SEQのリストを取得します。
	 * 
	 * @param customerId 顧客ID
	 * @return 業務履歴SEQリスト
	 */
	@Select
	List<Long> selectSeqByCustomerId(Long customerId);

	/**
	 * 顧客IDをキーとして、業務履歴SEQのリストを取得します。
	 * 
	 * @param customerId 顧客ID
	 * @return 業務履歴SEQリスト
	 */
	@Select
	List<TGyomuHistoryCustomerEntity> selectByCustomerId(Long customerId);

	/**
	 * 顧客IDと、業務履歴SEQのリストをキーとして、業務履歴-顧客情報を取得します。
	 * 
	 * @param customerId 顧客ID
	 * @param gyomuHistorySeqList 業務履歴のSEQリスト
	 * @return 業務履歴-顧客情報
	 */
	@Select
	List<TGyomuHistoryCustomerEntity> selectByCustomerIdAndGyomuHistorySeqList(Long customerId, List<Long> gyomuHistorySeqList);

	/**
	 * 登録
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TGyomuHistoryCustomerEntity entity);

	/**
	 * 複数登録
	 *
	 * @param entity
	 * @return
	 */
	@BatchInsert
	int[] insert(List<TGyomuHistoryCustomerEntity> entity);

	/**
	 * 複数削除（物理）
	 *
	 * @param entity
	 * @return
	 */
	@BatchDelete
	int[] delete(List<TGyomuHistoryCustomerEntity> entity);

}
