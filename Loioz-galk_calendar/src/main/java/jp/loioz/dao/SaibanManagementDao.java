package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.dto.SaibanEditSaibanDto;

/**
 * 裁判管理画面用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface SaibanManagementDao {

	/**
	 * 案件IDをキーにして、裁判管理画面の裁判一覧情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 裁判一覧情報
	 */
	@Select
	List<SaibanEditSaibanDto> selectSaibanByAnkenId(Long ankenId);

}
