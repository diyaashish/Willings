package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.dto.SaibanshoBuListDto;
import jp.loioz.entity.MSaibanshoBuEntity;

@ConfigAutowireable
@Dao
public interface MSaibanshoBuDao {

	/**
	 * 裁判所部検索（1件）
	 *
	 * @param saibanshoId
	 * @param keizokubuId
	 *
	 * @return MSaibanshoBuEntity
	 */
	@Select
	MSaibanshoBuEntity selectById(Long keizokubuId);

	/**
	 * 裁判所IDをキーにして裁判所部情報を取得する
	 *
	 * @param saibanshoId 裁判所ID
	 * @return 裁判所部情報
	 */
	@Select
	List<MSaibanshoBuEntity> selectBySaibanshoId(Long saibanshoId);

	/**
	 * 裁判所IDをキーにして裁判所部情報を取得する
	 *
	 * @param saibanshoId
	 * @return
	 */
	@Select
	List<SaibanshoBuListDto> selectDtoBySaibanshoId(Long saibanshoId);

	/**
	 * 登録処理（1件）
	 *
	 * @param entity
	 * @return 件数
	 */
	@Insert
	int insert(MSaibanshoBuEntity entity);

	/**
	 * 更新処理（1件）
	 *
	 * @param entity
	 * @return 件数
	 */
	@Update
	int update(MSaibanshoBuEntity entity);

}
