package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.PersonBean;
import jp.loioz.entity.TPersonEntity;

/**
 * 顧客情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TPersonDao {

	/**
	 * 名簿IDをキーにして顧客情報を取得する
	 *
	 * @param personId 名簿ID
	 * @return 顧客情報
	 */
	default TPersonEntity selectPersonByPersonId(Long personId) {
		return selectPersonByPersonId(Arrays.asList(personId)).stream()
				.findFirst()
				.orElse(null);
	}

	/**
	 * 名簿IDをキーにして名簿情報を取得する
	 *
	 * @param personId 名簿ID
	 * @return 名簿情報
	 */
	@Select
	List<TPersonEntity> selectPersonByPersonId(List<Long> personIdList);

	/**
	 * 顧客IDをキーにして名簿情報を取得する
	 *
	 * @param customerId 顧客ID
	 * @return 名簿情報
	 */
	default TPersonEntity selectById(Long customerId) {
		return selectById(Arrays.asList(customerId)).stream()
				.findFirst()
				.orElse(null);
	}

	/**
	 * 顧客IDをキーにして名簿情報を取得する
	 *
	 * @param customerId 顧客ID
	 * @return 名簿情報
	 */
	@Select
	List<TPersonEntity> selectById(List<Long> customerIdList);

	/**
	 * 案件IDをキーとして、名簿情報を取得する。
	 *
	 * @param ankenId 案件ID
	 * @return 名簿情報
	 */
	@Select
	List<TPersonEntity> selectByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーとして、名簿情報を取得する
	 * 
	 * @param ankenIdList
	 * @return
	 */
	@Select
	List<TPersonEntity> selectCustomerByAnkenIdList(List<Long> ankenIdList);

	/**
	 * 名簿IDをキーにして名簿情報＋付帯情報を取得する
	 * 
	 * @param personId 名簿ID
	 * @return 名簿付帯情報
	 */
	@Select
	PersonBean selectPersonAddDataByPersonId(Long personId);

	/**
	 * 名簿情報を名前により検索する。（弁護士は含めない）
	 * 
	 * @param customerIdList 引数の顧客IDは除外する NULL許容
	 * @param searchName 検索文字列 (姓名,せいめい)
	 * @param limitCount 取得件数 ※NULL許容だが全データ取得になるので注意
	 * @return
	 */
	@Select
	List<TPersonEntity> selectByNameExclusionId(List<Long> customerIdList, String searchName, Integer limitCount);

	/**
	 * パラメータにより、名簿情報(顧客のみ)の情報を取得する
	 * 
	 * @param excludeCustomerIdList
	 * @param searchName
	 * @param limitCount
	 * @return
	 */
	@Select
	List<TPersonEntity> selectExcludedCustomerByParams(List<Long> excludeCustomerIdList, String searchName, Integer limitCount);

	/**
	 * 案件に顧客として設定してある名簿情報を取得します
	 * 
	 * @param searchName
	 * @param limitCount
	 * @return
	 */
	@Select
	List<TPersonEntity> selectPersonOfDealByParams(String searchName, Integer limitCount);

	/**
	 * 1件の顧客情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TPersonEntity entity);

	/**
	 * 1件の顧客情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TPersonEntity entity);

	/**
	 * 1件の顧客情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TPersonEntity entity);
}