package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.PersonBean;
import jp.loioz.entity.TPersonAddKojinEntity;

/**
 * 個人名簿付帯情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TPersonAddKojinDao {

	/**
	 * 名簿IDをキーにして個人名簿付帯情報を取得する
	 *
	 * @param personId 名簿ID
	 * @return 個人名簿付帯情報
	 */
	@Select
	TPersonAddKojinEntity selectPersonAddKojinByPersonId(Long personId);

	/**
	 * 名簿IDをキーにして個人名簿付帯情報、郵送方法を取得する
	 *
	 * @param personId 顧客ID
	 * @return 個人名簿付帯情報、郵送方法
	 */
	@Select
	PersonBean selectAddKojinAndTransferTypeByPersonId(Long personId);

	/**
	 * 1件の個人名簿付帯情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TPersonAddKojinEntity entity);

	/**
	 * 1件の個人名簿付帯情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TPersonAddKojinEntity entity);

	/**
	 * 1件の個人名簿付帯情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TPersonAddKojinEntity entity);

}
