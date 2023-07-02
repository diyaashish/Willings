package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TAnkenAddKeijiEntity;

/**
 * 刑事案件付帯情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAnkenAddKeijiDao {

	/**
	 * 案件IDをキーにして刑事案件付帯情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 案件情報
	 */
	@Select
	TAnkenAddKeijiEntity selectByAnkenId(Long ankenId);

	/**
	 * 1件の刑事案件付帯情報情報を登録する
	 * 
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TAnkenAddKeijiEntity entity);

	/**
	 * 1件の刑事案件付帯情報を更新する
	 * 
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TAnkenAddKeijiEntity entity);

	/**
	 * 1件の刑事案件付帯情報を削除する
	 * 
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TAnkenAddKeijiEntity entity);
}