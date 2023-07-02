package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AnkenAddKeijiBean;
import jp.loioz.bean.AnkenBunyaBean;
import jp.loioz.bean.AnkenPersonCntBean;
import jp.loioz.bean.AnkenSaibanKeijiBean;
import jp.loioz.bean.TaskAnkenAddModalBean;
import jp.loioz.domain.condition.TaskAnkenAddModalSearchCondition;
import jp.loioz.entity.TAnkenEntity;

/**
 * 案件情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAnkenDao {

	/**
	 * 案件IDをキーにして案件情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 案件情報
	 */
	default TAnkenEntity selectById(Long ankenId) {
		return selectById(Arrays.asList(ankenId)).stream()
				.findFirst()
				.orElse(null);
	}

	/**
	 * 案件IDをキーにして案件情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 案件情報
	 */
	@Select
	List<TAnkenEntity> selectById(List<Long> ankenIdList);

	/**
	 * 案件IDをキーにして案件情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 案件情報
	 */
	@Select
	TAnkenEntity selectByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーとして、案件情報を取得します
	 * 
	 * @param ankenId
	 * @return 案件情報
	 */
	@Select
	AnkenAddKeijiBean selectAddKeijiById(Long ankenId);

	/**
	 * 顧客IDをキーにして案件情報を取得する
	 *
	 * @param customerId 顧客ID
	 * @return 案件情報
	 */
	@Select
	List<TAnkenEntity> selectByCustomerId(Long customerId);

	/**
	 * 裁判SEQをキーとして、案件情報を取得します
	 * 
	 * @param saibanSeq
	 * @return 案件情報
	 */
	@Select
	TAnkenEntity selectBySaibanSeq(Long saibanSeq);

	/**
	 * 裁判SEQをキーとして、案件情報を取得します
	 * 
	 * @param saibanSeq
	 * @return 案件情報
	 */
	@Select
	AnkenSaibanKeijiBean selectKeijiBySaibanSeq(Long saibanSeq);

	/**
	 * 分野IDをキーとして、案件情報の件数を取得します。<br>
	 * 
	 * @param bunyaId
	 * @return 案件情報
	 */
	@Select
	long selectCountByBunyaId(Long bunyaId);

	/**
	 * 名簿IDをキーにして関連する案件情報を取得する（personIdが関与者として登録されている案件）
	 *
	 * @param personId 名簿ID
	 * @return 案件情報
	 */
	@Select
	List<TAnkenEntity> selectRelatedAnkenByPersonId(Long personId);

	/**
	 * 検索条件から案件タスク追加の候補になる案件ID、名簿IDのリストを取得します。<br>
	 * 既に追加済みの案件は候補とならない。
	 * 
	 * @param condition モーダル検索条件
	 * @param accountSeq 対象アカウントSEQ
	 * @return
	 */
	@Select
	List<TaskAnkenAddModalBean> selectAnkenIdPersonIdBySearchConditions(TaskAnkenAddModalSearchCondition condition, Long accountSeq);

	/**
	 * 案件の先頭１人分の顧客名、顧客合計数を取得します。<br>
	 * 
	 * @param ankenIdList
	 * @return
	 */
	@Select
	List<AnkenPersonCntBean> selectAnkenPersonByAnkenId(List<Long> ankenIdList);

	/**
	 * 案件IDをキーとして、案件分野Beanを取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	default AnkenBunyaBean selectAnkenBunyaBeanByAnkenId(Long ankenId) {
		return selectAnkenBunyaBeanByAnkenId(Arrays.asList(ankenId)).stream().findFirst().orElse(null);
	}

	/**
	 * 案件IDをキーとして、案件分野Beanを取得する
	 * 
	 * @param ankenIdList
	 * @return
	 */
	@Select
	List<AnkenBunyaBean> selectAnkenBunyaBeanByAnkenId(List<Long> ankenIdList);

	/**
	 * 1件の案件情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TAnkenEntity entity);

	/**
	 * 1件の案件情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TAnkenEntity entity);

	/**
	 * 1件の案件情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TAnkenEntity entity);

}