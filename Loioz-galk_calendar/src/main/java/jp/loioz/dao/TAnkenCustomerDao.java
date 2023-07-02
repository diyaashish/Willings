package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.bean.AnkenBean;
import jp.loioz.bean.AnkenCustomerGroupByAnkenBean;
import jp.loioz.bean.AnkenCustomerRelationBean;
import jp.loioz.bean.AnkenKanryoFlgGroupByAnkenBean;
import jp.loioz.bean.AnkenPersonRelationBean;
import jp.loioz.bean.CustomerAnkenSearchListBean;
import jp.loioz.domain.condition.AnkenCustomerSearchCondition;
import jp.loioz.domain.condition.CaseAccountingCustomerSearchCondition;
import jp.loioz.domain.condition.CustomerAnkenSearchCondition;
import jp.loioz.dto.AnkenCustomerDto;
import jp.loioz.dto.AnkenCustomerListDto;
import jp.loioz.dto.PersonInfoForAnkenDto;
import jp.loioz.entity.TAnkenCustomerEntity;

/**
 * 案件-顧客用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAnkenCustomerDao {

	/**
	 * 案件IDをキーにして案件-顧客を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 案件-顧客
	 */
	default List<TAnkenCustomerEntity> selectByAnkenId(Long ankenId) {
		return selectByAnkenId(Arrays.asList(ankenId));
	}

	/**
	 * 案件IDをキーにして案件-顧客を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 案件-顧客
	 */
	@Select
	List<TAnkenCustomerEntity> selectByAnkenId(List<Long> ankenId);

	/**
	 * 案件IDをキーにして顧客IDのリストを取得する
	 *
	 * @param ankenId 顧客ID
	 * @return 案件IDのリスト
	 */
	@Select
	List<Long> selectCustomerIdsByAnkenId(Long ankenId);

	/**
	 * 顧客IDをキーにして案件IDのリストを取得する
	 *
	 * @param customerId 顧客ID
	 * @return 案件IDのリスト
	 */
	@Select
	List<Long> selectAnkenIdsByCustomerId(Long customerId);

	/**
	 * 案件IDと顧客IDをキーにして案件-顧客を取得する
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return 案件-顧客
	 */
	@Select
	TAnkenCustomerEntity selectByAnkenIdAndCustomerId(Long ankenId, Long customerId);

	/**
	 * 名簿IDが顧客の案件、当事者・関与者に設定されている案件を取得します。<br>
	 * 取得順は顧客案件の未完了、完了、不受任、関与案件の未完了、完了です。<br>
	 * （関与案件のステータスは、顧客全員のステータスが完了していれば完了、それ以外は未完了です）<br>
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<AnkenBean> selectAllAnkenRelatedToPersonId(Long personId);

	/**
	 * 名簿IDが顧客の案件、当事者・関与者に設定されている案件を取得します。<br>
	 * 取得順は顧客案件の未完了、完了、不受任、関与案件の未完了、完了です。<br>
	 * （関与案件のステータスは、顧客全員のステータスが完了していれば完了、それ以外は未完了です）<br>
	 * 
	 * @param personId
	 * @param options
	 * @return
	 */
	@Select
	List<AnkenBean> selectAllAnkenRelatedToPersonId(Long personId, SelectOptions options);

	/**
	 * 顧客IDをキーにして案件-顧客一覧を取得する
	 *
	 * @param customerId 顧客ID
	 * @return 案件-顧客一覧
	 */
	default List<TAnkenCustomerEntity> selectByCustomerId(Long customerId) {
		return selectByCustomerId(Arrays.asList(customerId));
	}

	/**
	 * 顧客IDをキーにして案件-顧客一覧を取得する
	 *
	 * @param customerId 顧客ID
	 * @return 案件-顧客一覧
	 */
	@Select
	List<TAnkenCustomerEntity> selectByCustomerId(List<Long> customerId);

	/**
	 * 対象顧客が「顧客」として登録されている案件の数を取得する
	 * 
	 * @param customerId
	 * @return
	 */
	@Select
	long selectCountByCustomerId(Long customerId);

	/**
	 * 初回面談予定SEQをキーにして案件-顧客を取得する
	 * 
	 * @param shokaiMendanScheduleSeq
	 * @return
	 */
	default TAnkenCustomerEntity selectByShokaiMendanScheduleSeq(Long shokaiMendanScheduleSeq) {
		return selectByShokaiMendanScheduleSeq(Arrays.asList(shokaiMendanScheduleSeq)).stream().findFirst().orElse(null);
	}

	/**
	 * 初回面談予定SEQをキーにして案件-顧客を取得する
	 *
	 * @param shokaiMendanScheduleSeqList 初回面談予定SEQ
	 * @return 案件-顧客
	 */
	@Select
	List<TAnkenCustomerEntity> selectByShokaiMendanScheduleSeq(List<Long> shokaiMendanScheduleSeqList);

	/**
	 * 検索条件をキーにして案件-顧客情報一覧を取得する
	 *
	 * @param condition 検索条件
	 * @return 案件-顧客一覧
	 */
	@Select
	List<AnkenCustomerListDto> selectBySearchCondition(AnkenCustomerSearchCondition condition);

	/**
	 * 顧客に紐づける案件検索結果一覧を取得する
	 * 
	 * @param condition
	 * @return
	 */
	@Select
	List<CustomerAnkenSearchListBean> selectCustomerAnkenSearchBeanByConditions(CustomerAnkenSearchCondition condition);

	/**
	 * 顧客 - 案件のひもづけ情報を取得します ※ 引数NULL考慮
	 *
	 * @param ankenId
	 * @param customerId
	 * @return List<AnkenCustomerRelationBean>
	 */
	@Select
	List<AnkenCustomerRelationBean> selectRelation(Long ankenId, Long customerId);

	/**
	 * 案件名、顧客名が検索ワードに該当する顧客 - 案件情報を取得します<br>
	 * 取得した案件をアカウントSEQが担当している案件かどうか判定フラグを取得します
	 * 
	 * @param searchWord 検索ワード
	 * @param accountSeq アカウントSEQ
	 * @return
	 */
	@Select
	List<AnkenCustomerGroupByAnkenBean> selectCustomerNameAnkenNameGroupByAnken(String searchWord, Long accountSeq);

	/**
	 * 案件IDに該当する顧客 - 案件情報を取得します
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	AnkenCustomerRelationBean selectCustomerNameAnkenNameByAnkenId(Long ankenId);

	/**
	 * 案件の顧客情報を取得します
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<PersonInfoForAnkenDto> selectPersonInfoForAnkenByAnkenId(List<Long> ankenId);

	/**
	 * 案件IDをキーとして、案件-顧客情報を取得
	 * 
	 * @param ankenId
	 * @return TAnkenCustomerEntityにpersonIdを含んだデータ
	 */
	default List<AnkenCustomerDto> selectAnkenCustomerDtoByAnkenId(Long ankenId) {
		return selectAnkenCustomerDtoByAnkenId(Arrays.asList(ankenId));
	}

	/**
	 * 案件IDをキーとして、案件-顧客情報を取得
	 * 
	 * @param ankenId
	 * @return TAnkenCustomerEntityにpersonIdを含んだデータ
	 */
	@Select
	List<AnkenCustomerDto> selectAnkenCustomerDtoByAnkenId(List<Long> ankenId);

	/**
	 * 名簿IDをキーとして、案件-顧客情報を取得
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TAnkenCustomerEntity> selectAnkenCustomerByPersonId(Long personId);

	/**
	 * 案件IDをキーとして、その案件の完了フラグを取得<br>
	 * 案件の顧客全員が完了している場合のみ、その案件は完了とする
	 * 
	 * @param ankenIdList
	 * @return
	 */
	@Select
	List<AnkenKanryoFlgGroupByAnkenBean> selectKanryoFlgByAnkenId(List<Long> ankenIdList);

	/**
	 * １件の案件-名簿情報Beanを取得する
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	@Select
	AnkenPersonRelationBean selectAnkenPersonListBeanByAnkenIdAndPersonId(Long ankenId, Long personId);

	/**
	 * 会計画面 顧客一覧検索条件に基づく案件-名簿情報Beanを取得する
	 * 
	 * @param searchConditions
	 * @return
	 */
	@Select
	List<AnkenPersonRelationBean> selectAnkenPersonListBeanBySearchCondition(CaseAccountingCustomerSearchCondition searchConditions);

	/**
	 * 1件の案件-顧客を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TAnkenCustomerEntity entity);

	/**
	 * 1件の案件-顧客を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TAnkenCustomerEntity entity);

	/**
	 * 1件の案件-顧客を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TAnkenCustomerEntity entity);

	/**
	 * 案件-顧客情報の削除
	 * 
	 * @param entity
	 * @return
	 */
	@BatchDelete
	int[] delete(List<TAnkenCustomerEntity> entity);

}
