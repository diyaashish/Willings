package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AdvisorContractTantoInfoBean;
import jp.loioz.entity.TAdvisorContractTantoEntity;

/**
 * 顧問契約担当用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAdvisorContractTantoDao {

	/**
	 * 対象の顧問契約に紐づく担当情報を取得
	 * 
	 * @param advisorContractSeq
	 * @return
	 */
	@Select
	List<TAdvisorContractTantoEntity> selectByAdvisorContractSeq(Long advisorContractSeq);
	
	/**
	 * 対象の顧問契約に紐づく担当情報を取得
	 * 
	 * @param accountSeq
	 * @param advisorContractSeqList
	 * @return
	 */
	@Select
	List<AdvisorContractTantoInfoBean> selectContractTantoInfoByParams(List<Long> advisorContractSeqList);
	
	/**
	 * １件の顧問契約担当を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TAdvisorContractTantoEntity entity);
	
	/**
	 * １件の顧問契約担当を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TAdvisorContractTantoEntity entity);
	
	/**
	 * １件の顧問契約担当を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TAdvisorContractTantoEntity entity);

	/**
	 * 複数の顧問契約担当を削除する
	 * 
	 * @param entityList
	 * @return 削除件数リスト
	 */
	@BatchDelete
	int[] batchDelete(List<TAdvisorContractTantoEntity> entityList);
}
