package jp.loioz.app.user.planSetting.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.seasar.doma.DomaNullPointerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.plan.PlanConstant;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.TPlanSettingSessionDao;
import jp.loioz.entity.TPlanSettingSessionEntity;

/**
 * プラン設定画面の入り口を提供するControllerに対応するサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PlanSettingGateWayService extends DefaultService {

	/** プラン画面用のセッション情報Daoクラス */
	@Autowired
	private TPlanSettingSessionDao tPlanSettingSessionDao;
	
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================
	
	/**
	 * セッション情報をDB（セッションテーブル）に格納する
	 * 
	 * @return 保存したSession情報を取得するための認証キー
	 * @throws AppException
	 */
	public String storeSessionInfoInDB() throws AppException {
		
		Long tenantSeq = SessionUtils.getTenantSeq();
		Long accountSeq = SessionUtils.getLoginAccountSeq();
		Boolean isOnlyPlanSettingAccessible = SessionUtils.isOnlyPlanSettingAccessible();
		
		// 対象テナントの、削除期限切れの古いSessionレコードを削除
		this.deleteSessionPassedDeletionDeadlineData(tenantSeq);
		
		// 対象テナント、対象アカウントの情報をSession情報としてDBに保存する
		String authKey = this.storeSessionInfoInDB(tenantSeq, accountSeq, isOnlyPlanSettingAccessible);
		
		return authKey;
	}
	
	// =========================================================================
	// private メソッド
	// =========================================================================
	
	/**
	 * 対象テナントのデータについて、<br>
	 * 削除期限が過ぎているデータを削除する。（不要なゴミデータが残り続けないようにするため）
	 * 
	 * @param tenantSeq
	 * @throws AppException 
	 */
	private void deleteSessionPassedDeletionDeadlineData(Long tenantSeq) throws AppException {
		
		// 削除期限のライン
		LocalDateTime deletionDeadline = LocalDateTime.now()
				.minusHours(PlanConstant.HOURS_TO_KEEP_SESSION_TABLE_RECORD);
		
		// 削除対象取得
		List<TPlanSettingSessionEntity> passedDeletionDeadlineDateList = tPlanSettingSessionDao.selectPassedDeletionDeadline(tenantSeq, deletionDeadline);
		if (CollectionUtils.isEmpty(passedDeletionDeadlineDateList)) {
			return;
		}
		
		int[] deleteCountArray;
		try {
			// 削除
			deleteCountArray = tPlanSettingSessionDao.deleteList(passedDeletionDeadlineDateList);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
		
		if (deleteCountArray.length != passedDeletionDeadlineDateList.size()) {
			// 実際の削除件数と削除対象の件数が異なる場合はエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}
	
	/**
	 * Session情報をDBに保存する
	 * 
	 * @param tenantSeq
	 * @param accountSeq
	 * @param isOnlyPlanSettingAccessible
	 * @return
	 * @throws AppException
	 */
	private String storeSessionInfoInDB(Long tenantSeq, Long accountSeq, Boolean isOnlyPlanSettingAccessible) throws AppException {
		
		// 認証キーを生成
		String authKey = UUID.randomUUID().toString();
		
		// 現在のセッションのアカウントのsessionレコードを取得（初めてのアクセスの場合はnullになる）
		TPlanSettingSessionEntity sessionEntity = tPlanSettingSessionDao.selectByPK(tenantSeq, accountSeq);
		if (sessionEntity == null) {
			// Sessionレコードが存在しない場合
			
			sessionEntity = new TPlanSettingSessionEntity();
			// PK
			sessionEntity.setTenantSeq(tenantSeq);
			sessionEntity.setAccountSeq(accountSeq);
			// 認証キー
			sessionEntity.setAuthKey(authKey);
			// その他情報
			sessionEntity.setOnlyPlanSettingAccessibleFlg(SystemFlg.booleanToCode(isOnlyPlanSettingAccessible));
			
			// 登録
			int insertCount = tPlanSettingSessionDao.insert(sessionEntity);
			if (insertCount != 1) {
				// 登録処理に失敗した場合
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
				throw new AppException(MessageEnum.MSG_E00012, null);
			}
			
		} else {
			// Sessionレコードが存在する場合
			
			// PK以外の値のすべてを再設定（この時点の状態で更新）する
			
			// 認証キー
			sessionEntity.setAuthKey(authKey);
			// その他情報
			sessionEntity.setOnlyPlanSettingAccessibleFlg(SystemFlg.booleanToCode(isOnlyPlanSettingAccessible));
			
			// 更新
			int updateCount = 0;
			try {
				// 処理実行
				updateCount = tPlanSettingSessionDao.update(sessionEntity);
			} catch (OptimisticLockingFailureException ex) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());

				throw new AppException(MessageEnum.MSG_E00013, ex);
			}

			if (updateCount != 1) {
				// 更新件数エラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

				throw new AppException(MessageEnum.MSG_E00013, null);
			}
		}
		
		return authKey;
	}
}
