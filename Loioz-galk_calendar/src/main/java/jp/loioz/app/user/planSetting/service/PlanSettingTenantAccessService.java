package jp.loioz.app.user.planSetting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.plan.PlanConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanStatus;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.MTenantMgtDao;
import jp.loioz.dao.TPlanSettingSessionDao;
import jp.loioz.entity.MTenantMgtEntity;
import jp.loioz.entity.TPlanSettingSessionEntity;

/**
 * プラン設定画面からテナント側（ログインSessionを持っている側）にアクセスして処理する用のコントローラーに対応したサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PlanSettingTenantAccessService extends DefaultService {
	
	/** プラン画面用のセッション情報Daoクラス */
	@Autowired
	private TPlanSettingSessionDao tPlanSettingSessionDao;
	
	/** テナント管理用のDaoクラス */
	@Autowired
	private MTenantMgtDao mTenantMgtDao;
	
	// =========================================================================
	// public メソッド
	// =========================================================================
	
	/**
	 * ログインSessionの下記を最新の状態に更新する
	 * 
	 * <pre>
	 * ・planType
	 * ・isOnlyPlanSettingAccessible
	 * ・isFreePlanStatus
	 * ・isFreePlanExpiredAt
	 * </pre>
	 * 
	 * @param authKey
	 */
	public void updateSessionValueAfterPlanSave(String authKey) {
		
		// 認証キーからSession情報取得
		TPlanSettingSessionEntity sessionEntity = tPlanSettingSessionDao.selectByAuthKey(authKey);
		// アクセスの正常性を検証
		this.validAccessStatus(sessionEntity);
		
		MTenantMgtEntity tenantMgtEntity = mTenantMgtDao.selectBySeq(sessionEntity.getTenantSeq());
		PlanType planType = PlanType.of(tenantMgtEntity.getPlanType());
		PlanStatus planStatus = PlanStatus.of(tenantMgtEntity.getPlanStatus());
		
		// Sessionのプランタイプを最新化
		SessionUtils.setPlanType(planType);
		
		// SessionのOnlyPlanSettingAccessibleの値をfalseに更新
		if (PlanConstant.PlanStatus.ENABLED == planStatus) {
			// 現在のテナントの契約ステータスが「有効」の場合のみ実施
			// 
			// ※OnlyPlanSettingAccessibleの値をtrueからfalseに更新する必要があるのは、
			//   プランが有効ではない状態でかつ、有効期限が切れている状態から、プランを登録した場合であり、プランを登録すると必ず契約ステータスは「有効」になる。
			//   ステータスが「有効」である場合は、ログイン時に必ずOnlyPlanSettingAccessibleの値はfalseになるので、falseで更新してOKとなる。
			SessionUtils.setOnlyPlanSettingAccessible(false);
			
			SessionUtils.setFreePlanStatus(false);
			SessionUtils.setFreePlanExpiredAt(null);
		}
		
		if (PlanConstant.PlanStatus.FREE == planStatus) {
			// 現在のテナントの契約ステータスが「無料」の場合のみ実施
			
			SessionUtils.setFreePlanStatus(true);
			SessionUtils.setFreePlanExpiredAt(tenantMgtEntity.getExpiredAt());
		}
	}
	
	// =========================================================================
	// private メソッド
	// =========================================================================
	
	/**
	 * アクセス状態が正常かどうかを検証する<br>
	 * 異常がある場合は例外をスローする
	 * 
	 * @param sessionEntity 認証キーから取得したSession情報
	 * @return true: 正常
	 */
	private boolean validAccessStatus(TPlanSettingSessionEntity sessionEntity) {
		
		if (sessionEntity == null) {
			throw new IllegalStateException("認証キーに対応するSessionテーブルのレコードが存在しません。");
		}
		
		Long planSettingSessionTenantSeq = sessionEntity.getTenantSeq();
		Long planSettingSessionAccountSeq = sessionEntity.getAccountSeq();
		if (!planSettingSessionTenantSeq.equals(SessionUtils.getTenantSeq())
				|| !planSettingSessionAccountSeq.equals(SessionUtils.getLoginAccountSeq())) {
			// テナントSEQとアカウントSEQについて、認証キーから取得した値と、SessionUtilsから取得した値が異なる場合はエラーとする
			// （万が一、元のサーバー以外にこの処理の実行が来てしまった場合にも、未ログイン状態となり処理の実行がここまで来れられないため、基本的にはこのエラーになることはないが念の為チェック。）
			throw new IllegalStateException("認証キーから取得したSession情報とSessionUtilsの情報が一致しません。");
		}
		
		return true;
	}
}
