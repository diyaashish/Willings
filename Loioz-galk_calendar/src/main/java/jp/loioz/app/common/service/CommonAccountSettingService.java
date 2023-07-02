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
import jp.loioz.common.constant.AccountSettingConstant.SettingType;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.MAccountSettingDao;
import jp.loioz.entity.MAccountSettingEntity;

/**
 * アカウント設定（m_account_settingテーブル）の共通サービス処理
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonAccountSettingService extends DefaultService {

	@Autowired
	private MAccountSettingDao mAccountSettingDao;
	
	@Autowired
	private Logger logger;
	
	// =========================================================================
	// public メソッド
	// =========================================================================
	
	/**
	 * セッションに保持するアカウント設定情報を取得する。<br>
	 * ログイン処理で、アカウント設定情報をセッションに格納する際に使用する。
	 */
	public Map<SettingType, String> getSessionSettingMap(Long accountSeq) {
		
		// 現在登録されているアカウント設定情報を取得
		List<MAccountSettingEntity> mAccountSettingEntityList = mAccountSettingDao.selectByAccountSeq(accountSeq);
		Map<SettingType, String> accountSettingMap = new HashMap<>();
		
		if (CollectionUtils.isEmpty(mAccountSettingEntityList)) {
			return accountSettingMap;
		}
		
		// アカウント設定情報をsession格納形式のMapに詰めて返却
		for (MAccountSettingEntity entity : mAccountSettingEntityList) {
			
			SettingType settingType = SettingType.of(entity.getSettingType());
			String val = entity.getSettingValue();
			
			accountSettingMap.put(settingType, val);
		}
		return accountSettingMap;
	}
	
	/**
	 * アカウントの設定項目の値を保存する（登録／更新）
	 * 
	 * @param accountSeq 対象のアカウント
	 * @param settingType 対象の設定項目
	 * @param settingValue 保存する値
	 * @throws AppException 
	 */
	public void saveSetting(Long accountSeq, SettingType settingType, String settingValue) throws AppException {
		
		if (settingType == null || settingValue == null) {
			throw new IllegalArgumentException("必要なパラメータがNULLになっています。");
		}
		
		// 現在登録されているアカウント設定情報を取得
		MAccountSettingEntity mAccountSettingEntity = mAccountSettingDao.selectByAccountSeqAndSettingType(accountSeq, settingType);
		
		if (mAccountSettingEntity == null) {
			// 設定がまだ登録されていない -> 新規登録
			
			MAccountSettingEntity insertEntity = new MAccountSettingEntity();
			insertEntity.setAccountSeq(accountSeq);
			insertEntity.setSettingType(settingType.getCd());
			insertEntity.setSettingValue(settingValue);
			
			// 登録
			int insertCount = mAccountSettingDao.insert(insertEntity);
			
			if (insertCount != 1) {
				// 登録処理に失敗した場合
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
				throw new AppException(MessageEnum.MSG_E00012, null);
			}
			
			// sessionの値も更新
			this.updateSessionSetting(settingType, settingValue);
			
		} else {
			// 設定が既に登録されている -> 更新
			
			String dbVal = mAccountSettingEntity.getSettingValue();
			if (settingValue.equals(dbVal)) {
				// 登録されている値と同じ値の場合は、更新は行わない
				return;
			}
			
			// 更新値を設定
			mAccountSettingEntity.setSettingValue(settingValue);
			
			// 件数チェック用
			int updateCount = 0;
			try {
				// 更新
				updateCount = mAccountSettingDao.update(mAccountSettingEntity);
			} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
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
			
			// sessionの値も更新
			this.updateSessionSetting(settingType, settingValue);
		}
	}
	
	/**
	 * アカウントの設定項目の値を取得する
	 * 
	 * @param accountSeq 対象のアカウント
	 * @param settingType 対象の設定項目
	 * @return
	 */
	public String getSavedSettingValue(Long accountSeq, SettingType settingType) {
		
		// 現在登録されているアカウント設定情報を取得
		MAccountSettingEntity mAccountSettingEntity = mAccountSettingDao.selectByAccountSeqAndSettingType(accountSeq, settingType);
		if (mAccountSettingEntity == null) {
			return null;
		}
		
		return mAccountSettingEntity.getSettingValue();
	}
	
	// =========================================================================
	// private メソッド
	// =========================================================================
	
	/**
	 * Sessionに保持するアカウント設定情報の、指定の設定項目の値を更新する
	 * 
	 * @param settingType 対象の設定項目
	 * @param settingValue 設定する値
	 */
	private void updateSessionSetting(SettingType settingType, String settingValue) {
		
		if (settingType == null || settingValue == null) {
			throw new IllegalArgumentException("必要なパラメータがNULLになっています。");
		}
		
		SessionUtils.setAccountSettingValue(settingType, settingValue);
	}
	
}
