package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TRootFolderGoogleEntity;

/**
 * GoogleRootFolder管理用Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TRootFolderGoogleDao {

	/**
	 * 有効なルートフォルダ情報を取得します。
	 * 
	 * @return
	 */
	@Select
	TRootFolderGoogleEntity selectEnabledRoot();

	/**
	 * Googleのルートフォルダーの登録処理
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TRootFolderGoogleEntity entity);

	/**
	 * Googleのルートフォルダーの更新処理
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(TRootFolderGoogleEntity entity);

}
