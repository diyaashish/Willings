package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TMailSendHistoryEntity;

/**
 * メール送信履歴のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TMailSendHistoryDao {

	/**
	 * １件のメール送信履歴情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TMailSendHistoryEntity entity);

	/**
	 * １件のメール送信履歴情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TMailSendHistoryEntity entity);

}
