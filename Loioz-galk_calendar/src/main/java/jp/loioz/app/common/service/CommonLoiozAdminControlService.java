package jp.loioz.app.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.dao.MLoiozAdminControlDao;
import jp.loioz.entity.MLoiozAdminControlEntity;

/**
 * ロイオズ管理者制御（m_loioz_admin_controlテーブル）の共通サービス処理
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonLoiozAdminControlService extends DefaultService {
	
	@Autowired
	private MLoiozAdminControlDao mLoiozAdminControlDao;
	
	// =========================================================================
	// public メソッド
	// =========================================================================
	
	/**
	 * セッションに保持するロイオズ管理者制御情報を取得する。<br>
	 * ログイン処理で、ロイオズ管理者制御情報をセッションに格納する際に使用する。
	 */
	public Map<LoiozAdminControl, String> getSessionSettingMap() {
		
		// 登録されているロイオズ管理者制御情報を取得
		List<MLoiozAdminControlEntity> loiozAdminControlEntityList = mLoiozAdminControlDao.selectAll();
		Map<LoiozAdminControl, String> loiozAdminControlMap = new HashMap<>();
		
		if (CollectionUtils.isEmpty(loiozAdminControlEntityList)) {
			return loiozAdminControlMap;
		}
		
		// Mapに格納
		loiozAdminControlEntityList.stream()
			.forEach(entity -> {
				LoiozAdminControl loiozAdminControlEnum = LoiozAdminControl.of(entity.getAdminControlId());
				String adminControlValue = entity.getAdminControlValue();
				
				loiozAdminControlMap.put(loiozAdminControlEnum, adminControlValue);
			});
		
		return loiozAdminControlMap;
	}
	
	// =========================================================================
	// private メソッド
	// =========================================================================
	
}
