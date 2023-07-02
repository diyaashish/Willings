package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TPersonAddLawyerEntity;

/**
 * 名簿-弁護士付帯情報Dao
 */
@ConfigAutowireable
@Dao
public interface TPersonAddLawyerDao {

	/**
	 * 名簿IDをキーとして、弁護士付帯情報を取得する
	 * 
	 * @param personId
	 * @return
	 */
	default TPersonAddLawyerEntity selectPersonAddLawyerByPersonId(Long personId) {
		return selectPersonAddLawyerByPersonId(Arrays.asList(personId)).stream().findFirst().orElse(null);
	};

	/**
	 * 名簿IDをキーとして、弁護士付帯情報を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TPersonAddLawyerEntity> selectPersonAddLawyerByPersonId(List<Long> personId);

	/**
	 * 名簿IDをキーとして、顧客タイプが「弁護士」の弁護士付帯情報を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TPersonAddLawyerEntity> selectCustomerTypeLawyerAddInformationByPersonId(List<Long> personId);

	/**
	 * 名簿IDをキーとして、弁護士付帯情報を1件取得する
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	TPersonAddLawyerEntity selectById(Long personId);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TPersonAddLawyerEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TPersonAddLawyerEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TPersonAddLawyerEntity entity);
}
