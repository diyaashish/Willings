package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.common.constant.CommonConstant.ContractStatus;
import jp.loioz.entity.TAdvisorContractEntity;

/**
 * 顧問契約用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAdvisorContractDao {

	/**
	 * 1件の顧問契約情報を取得
	 * 
	 * @param advisorContractSeq
	 * @return
	 */
	@Select
	TAdvisorContractEntity selectBySeq(Long advisorContractSeq);
	
	/**
	 * 顧問契約の一覧を取得（ページング用）
	 * 
	 * @param personId
	 * @param options
	 * @return
	 */
	@Select
	List<TAdvisorContractEntity> selectContractListPageByPersonId(Long personId, SelectOptions options);

	/**
	 * 顧問契約の一覧を取得（全件）
	 * 
	 * @param personId
	 * @param options
	 * @return
	 */
	@Select
	List<TAdvisorContractEntity> selectContractListPageByPersonId(Long personId);

	/**
	 * 対象名簿の対象ステータスコードのデータ件数を取得
	 * 
	 * @param personId 名簿ID
	 * @param contractStatusCdList ステータスコードCDのリスト
	 * @return
	 */
	@Select
	long selectContractCountByParams(Long personId, List<String> contractStatusCdList);
	
	/**
	 * 対象名簿の、現在アクティブな顧問契約の数を取得
	 * 
	 * @param personId
	 * @return
	 */
	default long selectActiveContractCountByPersonId(Long personId) {
		
		// 「新規」と「進行中」のステータスがアクティブなものとする
		List<String> activeContractStatusCdList = Arrays.asList(
				ContractStatus.CONTRACT_NEW.getCd(),
				ContractStatus.CONTRACTING.getCd());
		
		return this.selectContractCountByParams(personId, activeContractStatusCdList);
	}
	
	
	
	/**
	 * １件の顧問契約を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TAdvisorContractEntity entity);
	
	/**
	 * １件の顧問契約を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TAdvisorContractEntity entity);
	
	/**
	 * １件の顧問契約を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TAdvisorContractEntity entity);

	/**
	 * 複数の顧問契約を削除する
	 * 
	 * @param entity
	 * @return
	 */
	@BatchDelete
	int[] delete(List<TAdvisorContractEntity> entity);

}
