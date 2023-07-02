package jp.loioz.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TPlanSettingSessionEntity;

/**
 * プラン画面用のセッション情報Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TPlanSettingSessionDao {

	/**
	 * PKで1件のセッション情報を取得します
	 * 
	 * @param tenantSeq
	 * @param accountSeq
	 * @return
	 */
	@Select
	TPlanSettingSessionEntity selectByPK(Long tenantSeq, Long accountSeq);
	
	/**
	 * 認証キーで1件のセッション情報を取得します
	 * 
	 * @param authKey
	 * @return
	 */
	@Select
	TPlanSettingSessionEntity selectByAuthKey(String authKey);
	
	/**
	 * 指定の有効期限ラインより古い（有効期限切れ）のデータを取得する。<br>
	 * （削除対象の取得）
	 * 
	 * @param tenantSeq 対象テナント
	 * @param deletionDeadline 削除期限ライン（これより最終更新が古いデータを有効期限切れとする）
	 * @return
	 */
	@Select
	List<TPlanSettingSessionEntity> selectPassedDeletionDeadline(Long tenantSeq, LocalDateTime deletionDeadline);
	
	/**
	 * 1件のセッション情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TPlanSettingSessionEntity entity);

	/**
	 * 1件のセッション情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TPlanSettingSessionEntity entity);

	/**
	 * 複数件のセッション情報を削除する
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchDelete
	int[] deleteList(List<TPlanSettingSessionEntity> entityList);
}
