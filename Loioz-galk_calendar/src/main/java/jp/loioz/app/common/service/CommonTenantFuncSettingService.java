package jp.loioz.app.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.doma.DomaNullPointerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.TenantFuncSettingConstant.TenantFuncSetting;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.MTenantFuncSettingDao;
import jp.loioz.entity.MTenantFuncSettingEntity;

/**
 * テナント機能設定（m_tenant_func_settingテーブル）の共通サービス処理
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonTenantFuncSettingService extends DefaultService {
	
	@Autowired
	private MTenantFuncSettingDao mTenantFuncSettingDao;
	
	@Autowired
	private Logger logger;
	
	// =========================================================================
	// public メソッド
	// =========================================================================
	
	/**
	 * セッションに保持するテナント機能設定情報を取得する。<br>
	 * ログイン処理で、テナント機能設定情報をセッションに格納する際に使用する。
	 */
	public Map<TenantFuncSetting, String> getSessionSettingMap() {
		
		// 登録されているテナント機能設定情報を取得
		List<MTenantFuncSettingEntity> tenantFuncSettingEntityList =  mTenantFuncSettingDao.selectAll();
		Map<TenantFuncSetting, String> tenantFuncSettingMap = new HashMap<>();
		
		if (CollectionUtils.isEmpty(tenantFuncSettingEntityList)) {
			return tenantFuncSettingMap;
		}
		
		// Mapに格納
		tenantFuncSettingEntityList.stream()
			.forEach(entity -> {
				TenantFuncSetting tenantFuncSettingEnum = TenantFuncSetting.of(entity.getFuncSettingId());
				String funcSettingValue = entity.getFuncSettingValue();
				
				tenantFuncSettingMap.put(tenantFuncSettingEnum, funcSettingValue);
			});
		
		return tenantFuncSettingMap;
	}
	
	/**
	 * 指定のテナント設定情報を更新する
	 * 
	 * @param tenantFuncSetting
	 * @param settingValue
	 * @throws AppException
	 */
	public void updateSetting(TenantFuncSetting tenantFuncSetting, String settingValue) throws AppException {
		
		if (tenantFuncSetting == null || settingValue == null) {
			throw new IllegalArgumentException("必要なパラメータがNULLになっています。");
		}
		
		// 更新対象を取得
		// ※レコードは必ず存在するため、NULLチェックはしない
		MTenantFuncSettingEntity updateEntity = mTenantFuncSettingDao.selectByFuncSettingId(tenantFuncSetting.getId());
		
		String dbVal = updateEntity.getFuncSettingValue();
		if (settingValue.equals(dbVal)) {
			// 登録されている値と同じ値の場合は、更新は行わない
			return;
		}
		
		// 更新値を設定
		updateEntity.setFuncSettingValue(settingValue);
		
		// 件数チェック用
		int updateCount = 0;
		try {
			// 更新
			updateCount = mTenantFuncSettingDao.update(updateEntity);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
		
		// sessionの値も更新
		this.updateSessionSetting(tenantFuncSetting, settingValue);
	}
	
	/**
	 * 指定のテナント機能設定の値（登録値）を取得する
	 * 
	 * @param tenantFuncSetting テナント機能設定Enum
	 * @return
	 */
	public String getSavedSettingValue(TenantFuncSetting tenantFuncSetting) {
		
		if (tenantFuncSetting == null) {
			return null;
		}
		
		// 指定のテナント機能設定情報を取得
		MTenantFuncSettingEntity mTenantFuncSettingEntity = mTenantFuncSettingDao.selectByFuncSettingId(tenantFuncSetting.getId());
		if (mTenantFuncSettingEntity == null) {
			return null;
		}
		
		return mTenantFuncSettingEntity.getFuncSettingValue();
	}
	
	// =========================================================================
	// private メソッド
	// =========================================================================
	
	/**
	 * Sessionに保持するテナント設定情報の、指定の設定項目の値を更新する
	 * 
	 * @param tenantFuncSetting
	 * @param settingValue
	 */
	private void updateSessionSetting(TenantFuncSetting tenantFuncSetting, String settingValue) {
		
		if (tenantFuncSetting == null || settingValue == null) {
			throw new IllegalArgumentException("必要なパラメータがNULLになっています。");
		}
		
		SessionUtils.setTenantFuncSettingValue(tenantFuncSetting, settingValue);
	}
	
}
