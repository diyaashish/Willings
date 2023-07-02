package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.DengonCustomerBean;
import jp.loioz.entity.TDengonCustomerEntity;

/**
 * 伝言ステータス用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TDengonCustomerDao {

	/**
	 * 伝言SEQをキーとして、伝言-顧客情報を取得する
	 * 
	 * @param dengonSeq
	 * @return
	 */
	@Select
	List<TDengonCustomerEntity> selectByDengonSeq(Long dengonSeq);

	/**
	 * 伝言SEQをキーとして顧客情報を取得する
	 * 
	 * @param dengonSeq
	 * @return
	 */
	default List<DengonCustomerBean> selectBeanByDengonSeq(Long dengonSeq) {
		return selectBeanByDengonSeq(Arrays.asList(dengonSeq));
	}

	/**
	 * 伝言SEQをキーとして顧客情報を取得する
	 * 
	 * @param dengonSeq
	 * @return
	 */
	@Select
	List<DengonCustomerBean> selectBeanByDengonSeq(List<Long> dengonSeq);

	/**
	 * １件の伝言-顧客情報を登録する
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TDengonCustomerEntity entity);

	/**
	 * 複数件の伝言-顧客情報を登録する
	 * 
	 * @param entity
	 * @return
	 */
	@BatchInsert
	int[] insert(List<TDengonCustomerEntity> entity);

	/**
	 * １件の伝言-顧客情報を削除する
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TDengonCustomerEntity entity);

}