package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AnkenTantoAccountBean;
import jp.loioz.bean.AnkenTantoLawyerJimuBean;
import jp.loioz.dto.AnkenTantoDto;
import jp.loioz.entity.TAnkenTantoEntity;

/**
 * 案件担当者情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAnkenTantoDao {

	/**
	 * 案件IDをキーにして案件担当者情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 案件担当者情報
	 */
	@Select
	List<TAnkenTantoEntity> selectByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーにして案件担当者情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 案件担当者情報Bean
	 */
	@Select
	List<AnkenTantoAccountBean> selectByIdForAccount(Long ankenId);

	/**
	 * 案件IDリストをキーにして案件担当者情報を取得する
	 *
	 * @param ankenIdList 案件IDリスト
	 * @return 案件担当者情報
	 */
	@Select
	List<AnkenTantoDto> selectByAnkenIdListAndTantoType(List<Long> ankenIdList);

	/**
	 * 案件IDと担当種別をキーにして、案件担当者のアカウントSEQを取得する
	 *
	 * @param ankenId 案件ID
	 * @param tantoType 担当種別
	 * @return アカウントSEQ
	 */
	@Select
	List<Long> selectByAnkenIdAndTantoType(Long ankenId, String tantoType);

	/**
	 * 案件IDに関する（案件の）担当弁護士、担当事務情報を取得します<br>
	 * それぞれ、カンマ区切りで全担当者分の名前を取得<br>
	 * 並び順<br>
	 * 1.主担当<br>
	 * 2.担当者<br>
	 * 
	 * @param ankenIdList
	 * @return
	 */
	@Select
	List<AnkenTantoLawyerJimuBean> selectAnkenTantoLaywerJimuById(List<Long> ankenIdList);

	/**
	 * 1件の案件担当者情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TAnkenTantoEntity entity);

	/**
	 * 1件の案件担当者情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TAnkenTantoEntity entity);

	/**
	 * 1件の案件担当者情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TAnkenTantoEntity entity);

	/**
	 * 複数件の案件担当者情報を削除する
	 * 
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] batchDelete(List<TAnkenTantoEntity> entity);
}
