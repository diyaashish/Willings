package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.dto.BushoShozokuDto;
import jp.loioz.entity.TBushoShozokuAcctEntity;

/**
 * 部署所属アカウント用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TBushoShozokuAcctDao {

	/**
	 * 表示用部署所属ユーザーを取得する
	 *
	 * @param bushoId
	 * @return 部署所属ユーザーリスト
	 */
	@Select
	List<BushoShozokuDto> selectBushoShozokuAccount(Long bushoId);

	/**
	 * 部署所属ユーザー情報を取得する
	 *
	 * @param bushoId
	 * @return 部署所属ユーザーリスト
	 */
	@Select
	List<TBushoShozokuAcctEntity> selectByBushoId(Long bushoId);

	/**
	 * 有効アカウントに紐づく部署所属アカウント情報を取得します
	 * 
	 * @return
	 */
	@Select
	List<TBushoShozokuAcctEntity> selectByEnabledAccount();

	/**
	 * 部署未所属のアカウントSEQをすべて取得します
	 * 
	 * @return
	 */
	@Select
	List<Long> selectMishozokuAccountSeq();

	/**
	 * 複数件の部署所属アカウント情報を登録する
	 *
	 * @param tBushoShozokuAcctEntityList
	 * @return 登録したデータ件数
	 */
	@BatchInsert
	int[] insert(List<TBushoShozokuAcctEntity> tBushoShozokuAcctEntityList);

	/**
	 * 指定した部署の部署所属アカウント情報を全て削除する
	 *
	 * @param tBushoShozokuAcctEntityList
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] delete(List<TBushoShozokuAcctEntity> tBushoShozokuAcctEntityList);
}