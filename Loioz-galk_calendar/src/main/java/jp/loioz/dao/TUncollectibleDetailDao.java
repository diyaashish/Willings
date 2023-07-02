package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TUncollectibleDetailEntity;

/**
 * 回収不能詳細Dao
 */
@ConfigAutowireable
@Dao
public interface TUncollectibleDetailDao {

	/**
	 * 回収不能詳細SEQをキーとして、回収不能詳細情報の取得する
	 * 
	 * @param uncollectibleDetailSeq
	 * @return
	 */
	@Select
	TUncollectibleDetailEntity selectUncollectibleDetailByUncollectibleDetailSeq(Long uncollectibleDetailSeq);

	/**
	 * 回収不能金SEQをキーとして、回収不能詳細情報をすべて取得する
	 * 
	 * @param uncollectibleSeq
	 * @return
	 */
	@Select
	List<TUncollectibleDetailEntity> selectUncollectibleDetailByUncollectibleSeq(Long uncollectibleSeq);

	/**
	 * 登録処理
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TUncollectibleDetailEntity entity);

	/**
	 * 更新処理
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(TUncollectibleDetailEntity entity);

	/**
	 * 削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TUncollectibleDetailEntity entity);
	
	/**
	 * 回収不能詳細データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTUncollectibleDetail();

}
