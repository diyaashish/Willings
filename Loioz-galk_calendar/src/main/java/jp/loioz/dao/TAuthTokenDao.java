package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TAuthTokenEntity;

/**
 * トークン管理テーブルのDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAuthTokenDao {

	/**
	 * DBからアクセストークンを取得する <br>
	 * 
	 * ※色々な箇所で利用しないように、{@link jp.loioz.app.common.api.box.service.CommonBoxApiService CommonBoxApiService} でのみ利用する
	 * 
	 * @param accountSeq
	 * @return
	 */
	@Select
	List<TAuthTokenEntity> selectByAccountSeq(Long accountSeq);

	/**
	 * DBからアクセストークンを取得する <br>
	 * 
	 * ※色々な箇所で利用しないように、{@link jp.loioz.app.common.api.box.service.CommonBoxApiService CommonBoxApiService} でのみ利用する
	 * 
	 * @param accountSeq
	 * @param externalServiceId
	 * @return
	 */
	@Select
	TAuthTokenEntity selectByAccountSeqAndExternalServiceId(Long accountSeq, String externalServiceId);

	/**
	 * DBからアクセストークンを取得する <br>
	 * 
	 * ※色々な箇所で利用しないように、{@link jp.loioz.app.common.api.box.service.CommonBoxApiService CommonBoxApiService} でのみ利用する
	 * 
	 * @param externalServiceId
	 * @return
	 */
	@Select
	List<TAuthTokenEntity> selectByExternalServiceId(String externalServiceId);

	/**
	 * トークン情報の登録処理
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TAuthTokenEntity entity);

	/**
	 * トークン情報の更新処理
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(TAuthTokenEntity entity);

	/**
	 * トークン情報の削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TAuthTokenEntity entity);

	/**
	 * トークン情報の削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@BatchDelete
	int[] delete(List<TAuthTokenEntity> entity);

}
