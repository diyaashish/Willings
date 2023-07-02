package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TRootFolderBoxEntity;

/**
 * BoxRootFolder管理用Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TRootFolderBoxDao {

	/**
	 * 有効なルートフォルダ情報を取得します。
	 * 
	 * @return
	 */
	@Select
	TRootFolderBoxEntity selectEnabledRoot();

	/**
	 * Boxのルートフォルダーの登録処理
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TRootFolderBoxEntity entity);

	/**
	 * Boxのルートフォルダーの更新処理
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(TRootFolderBoxEntity entity);

}
