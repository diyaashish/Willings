package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.MMailEntity;

@ConfigAutowireable
@Dao
public interface MMailDao {

	/**
	 * 1件のメール情報をメールIDで取得する
	 *
	 * @param mailId メールID
	 * @return メール情報
	 */
	@Select
	MMailEntity selectByMailId(String mailId);

}
