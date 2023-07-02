package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.common.constant.CommonConstant.ExternalServiceType;
import jp.loioz.entity.TConnectedExternalServiceEntity;

/**
 * 接続中外部サービスDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TConnectedExternalServiceDao {

	/**
	 * 連携中のサービスをすべて取得する
	 * 
	 * @return
	 */
	@Select
	List<TConnectedExternalServiceEntity> selectAll();

	/**
	 * 接続中の外部ストレージサービスを取得します(外部ストレージサービスは一つのみ選択可能)
	 * 
	 * @return
	 */
	default TConnectedExternalServiceEntity selectStorageConnectedService() {
		return selectByServiceType(ExternalServiceType.STORAGE.getCd()).stream().findFirst().orElse(null);
	}

	/**
	 * サービス種別をキーとして、接続中外部サービス情報を取得する
	 * 
	 * @param connectedExternalSeq
	 * @return
	 */
	@Select
	List<TConnectedExternalServiceEntity> selectByServiceType(String serviceType);

	/**
	 * 登録処理
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TConnectedExternalServiceEntity entity);

	/**
	 * 削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TConnectedExternalServiceEntity entity);

}
