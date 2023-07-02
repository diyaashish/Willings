package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.MAccountImgEntity;

/**
 * アカウント画像Dao
 */
@ConfigAutowireable
@Dao
public interface MAccountImgDao {

	/**
	 * SEQをキーとして、アカウント画像情報を取得する
	 * 
	 * @param accountImgSeq
	 * @return
	 */
	@Select
	MAccountImgEntity selectBySeq(Long accountImgSeq);

	/**
	 * アカウントSEQをキーとして、アカウント画像情報を取得する
	 * 
	 * @param accountSeq
	 * @return
	 */
	@Select
	MAccountImgEntity selectByAccountSeq(Long accountSeq);

	/**
	 * 登録処理
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(MAccountImgEntity entity);

	/**
	 * 更新処理
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(MAccountImgEntity entity);

	/**
	 * 削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(MAccountImgEntity entity);

}
