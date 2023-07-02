package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TLoginRecordEntity;

/**
 * ログイン記録用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TLoginRecordDao {

	/**
	 * テナント連番とアカウント連番（複合PK）で1件のログイン記録情報を取得する
	 * 
	 * @param tenantSeq
	 * @param accountSeq
	 * @return
	 */
	@Select
	TLoginRecordEntity selectByTenantSeqAndAccountSeq(Long tenantSeq, Long accountSeq);
	
	/**
	 * １件のログイン記録情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TLoginRecordEntity entity);

	/**
	 * １件のログイン記録情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TLoginRecordEntity entity);
}
