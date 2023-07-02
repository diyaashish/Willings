package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.PersonBean;
import jp.loioz.entity.TPersonAddHojinEntity;

/**
 * 法人名簿付帯情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TPersonAddHojinDao {

	/**
	 * 名簿IDをキーにして法人名簿付帯情報を取得する
	 *
	 * @param personId 名簿ID
	 * @return 法人名簿付帯情報
	 */
	@Select
	TPersonAddHojinEntity selectPersonAddHojinByPersonId(Long personId);

	/**
	 * 名簿IDをキーにして法人名簿付帯情報、郵送方法を取得する
	 *
	 * @param personId 名簿ID
	 * @return 法人名簿付帯情報、郵送方法
	 */
	@Select
	PersonBean selectAddHojinAndTransferTypeByPersonId(Long personId);

	/**
	 * 1件の法人名簿付帯情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TPersonAddHojinEntity entity);

	/**
	 * 1件の法人名簿付帯情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TPersonAddHojinEntity entity);

	/**
	 * 1件の法人名簿付帯情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TPersonAddHojinEntity entity);

}
