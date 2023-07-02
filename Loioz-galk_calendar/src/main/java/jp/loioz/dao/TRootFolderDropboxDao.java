package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TRootFolderDropboxEntity;

/**
 * DropboxRootFolder管理用Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TRootFolderDropboxDao {

	/**
	 * 有効なルートフォルダ情報を取得します。
	 * 
	 * @return
	 */
	@Select
	TRootFolderDropboxEntity selectEnabledRoot();

	/**
	 * Dropboxのルートフォルダーの登録処理
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TRootFolderDropboxEntity entity);

	/**
	 * Dropboxのルートフォルダーの更新処理
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(TRootFolderDropboxEntity entity);

}
