package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TAccgDocLastUsedNumberEntity;

/**
 * 会計書類-最終採番番号Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgDocLastUsedNumberDao {

	/**
	 * 1件の会計書類-最終採番番号を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	TAccgDocLastUsedNumberEntity selectByType(String accgDocType, String numberingType);

	/**
	 * 1件の会計書類-最終採番番号データを登録する
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TAccgDocLastUsedNumberEntity entity);

	/**
	 * 1件の会計書類-最終採番番号データを更新する
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(TAccgDocLastUsedNumberEntity entity);

	/**
	 * 1件の会計書類-最終採番番号データを削除する
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TAccgDocLastUsedNumberEntity entity);

	/**
	 * 会計書類-最終採番番号データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgDocLastUsedNumber();
	
}
