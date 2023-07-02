package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TBatchExecuteHistoryEntity;

/**
 * バッチ実行履歴Dao
 *
 */

@ConfigAutowireable
@Dao
public interface TBatchExecuteHistoryDao {

	/***
	 * 1件のバッチ実行履歴情報を登録する
	 * 
	 * @param tBatchExecuteHistoryEntity バッチ実行履歴情報
	 */
	@Insert
	int insert(TBatchExecuteHistoryEntity tBatchExecuteHistoryEntity);
	
}
