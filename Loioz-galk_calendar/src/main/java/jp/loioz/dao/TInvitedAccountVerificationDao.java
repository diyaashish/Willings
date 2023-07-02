package jp.loioz.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TInvitedAccountVerificationEntity;

/**
 * メール送信のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TInvitedAccountVerificationDao {

	/**
	 * 1件の招待アカウント認証情報を取得する
	 *
	 * @param 取得対象のPK値
	 * @return 招待アカウント認証情報
	 */
	@Select
	TInvitedAccountVerificationEntity selectByKey(String verificationKey);
	
	/**
	 * 「招待中」状態の招待アカウント認証情報を取得する
	 * 
	 * @param now 現在日時
	 * @return
	 */
	@Select
	List<TInvitedAccountVerificationEntity> selectInviting(LocalDateTime now);
	
	/**
	 * 対象メールの「招待中」状態の招待アカウント認証情報を取得する
	 * 
	 * @param mailAddressList
	 * @param now
	 * @return
	 */
	@Select
	List<TInvitedAccountVerificationEntity> selectInvitingByMailAddress(List<String> mailAddressList, LocalDateTime now);

	/**
	 * 全件の招待アカウント認証を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@BatchInsert
	int[] insertList(List<TInvitedAccountVerificationEntity> entityList);
	
	/**
	 * 1件の招待アカウント認証情報を更新
	 *
	 * @param 取得対象のPK値
	 * @return アカウント認証情報
	 */
	@Update
	int update(TInvitedAccountVerificationEntity entity);

	/**
	 * 1件の招待アカウント認証情報を削除
	 *
	 * @param 取得対象のPK値
	 * @return アカウント認証情報
	 */
	@Delete
	int delete(TInvitedAccountVerificationEntity entity);
}
