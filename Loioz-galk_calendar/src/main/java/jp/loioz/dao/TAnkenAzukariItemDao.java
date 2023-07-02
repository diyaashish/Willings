package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.domain.condition.AzukariItemSearchCondition;
import jp.loioz.dto.AzukariItemListDto;
import jp.loioz.entity.TAnkenAzukariItemEntity;

/**
 * 預り品用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAnkenAzukariItemDao {

	/**
	 * 案件IDと顧客IDをキーにして、預かり品情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return 預かり品情報リスト
	 */
	@Select
	List<TAnkenAzukariItemEntity> selectByAnkenIdAndCustomerId(Long ankenId, Long customerId);

	/**
	 * 案件IDをキーにして、預かり品情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 預かり品情報リスト
	 */
	@Select
	List<TAnkenAzukariItemEntity> selectByAnkenId(Long ankenId);

	/**
	 * 預り品SEQリストと一致する預り品情報を取得する
	 *
	 * @param 預り品SEQリスト
	 * @return 預り品情報
	 */
	@Select
	List<AzukariItemListDto> selectAzukariItemListByAnkenItemSeq(List<Long> ankenItemSeqList);

	/**
	 * 複数件の預り品SEQを取得する
	 *
	 * @param 案件ID
	 * @param SelectOptions
	 * @return 預り品SEQリスト
	 */
	@Select
	List<Long> selectConditions(AzukariItemSearchCondition conditions, SelectOptions options);

	/**
	 * 1件の預り品情報を取得する
	 *
	 * @param 預り品SEQ
	 * @return 預り品情報
	 */
	@Select
	TAnkenAzukariItemEntity selectBySeq(Long ankenItemSeq);

	/**
	 * 関与者SEQをキーとして、返却先が該当の関与者SEQの預かり品情報を取得する
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	@Select
	List<TAnkenAzukariItemEntity> selectReturnToKanyoshaByKanyoshaSeq(Long kanyoshaSeq);

	/**
	 * 関与者SEQをキーとして、預かり元が該当の関与者SEQの預かり品情報を取得する
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	@Select
	List<TAnkenAzukariItemEntity> selectAzukariFromKanyoshaByKanyoshaSeq(Long kanyoshaSeq);

	/**
	 * 名簿IDをキーにして預り品情報を取得する<br>
	 * （返却先顧客IDが無い場合は関与者SEQから名簿IDを取得）
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TAnkenAzukariItemEntity> selectAnkenAzukariItemByPersonId(Long personId);

	/**
	 * 名簿IDをキーにして預り品情報を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TAnkenAzukariItemEntity> selectAnkenAzukariItemFromKanyoshaByPersonId(Long personId);

	/**
	 * 1件の預り品情報を登録する
	 *
	 * @param tAnkenAzukariItemEntity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TAnkenAzukariItemEntity tAnkenAzukariItemEntity);

	/**
	 * 1件の預り品情報を更新する
	 *
	 * @param tAnkenAzukariItemEntity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TAnkenAzukariItemEntity tAnkenAzukariItemEntity);

	/**
	 * 1件の預り品情報を物理削除する
	 *
	 * @param tAnkenAzukariItemEntity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TAnkenAzukariItemEntity tAnkenAzukariItemEntity);

	/**
	 * 預かり品の複数更新処理
	 * 
	 * @param tAnkenAzukariItemEntities
	 * @return 更新件数
	 */
	@BatchUpdate
	int[] update(List<TAnkenAzukariItemEntity> tAnkenAzukariItemEntities);

	/**
	 * 預かり品の複数削除処理
	 * 
	 * @param tAnkenAzukariItemEntities
	 * @return 削除件数
	 */
	@BatchDelete
	int[] delete(List<TAnkenAzukariItemEntity> tAnkenAzukariItemEntities);
}
