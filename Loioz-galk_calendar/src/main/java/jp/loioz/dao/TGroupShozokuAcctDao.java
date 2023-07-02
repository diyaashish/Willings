package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.dto.GroupShozokuDto;
import jp.loioz.entity.TGroupShozokuAcctEntity;

/**
 * グループ所属アカウント用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TGroupShozokuAcctDao {

	/**
	 * 表示用グループ所属ユーザーを取得する
	 *
	 * @param groupId
	 * @return グループ所属ユーザーリスト
	 */
	@Select
	List<GroupShozokuDto> selectGroupShozokuAccount(Long groupId);

	/**
	 * グループ所属ユーザー情報を取得する
	 *
	 * @param groupId
	 * @return グループ所属ユーザーリスト
	 */
	@Select
	List<TGroupShozokuAcctEntity> selectByGroupId(Long groupId);

	/**
	 * 全てのグループ所属ユーザー情報を取得する
	 *
	 * @return グループ所属ユーザー情報
	 */
	@Select
	List<TGroupShozokuAcctEntity> selectAll();

	/**
	 * 複数件のグループ所属アカウント情報を登録する
	 *
	 * @param tGroupShozokuAcctEntityList
	 * @return 登録したデータ件数
	 */
	@BatchInsert
	int[] insert(List<TGroupShozokuAcctEntity> tGroupShozokuAcctEntityList);

	/**
	 * 複数件のグループ所属アカウント情報を削除する
	 *
	 * @param tGroupShozokuAcctEntityList
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] delete(List<TGroupShozokuAcctEntity> tGroupShozokuAcctEntityList);
}