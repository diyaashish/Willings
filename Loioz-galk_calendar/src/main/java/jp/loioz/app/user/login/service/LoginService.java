package jp.loioz.app.user.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant;
import jp.loioz.dao.MTenantMgtDao;
import jp.loioz.entity.MTenantMgtEntity;

/**
 * ログイン画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LoginService extends DefaultService {

	@Autowired
	private MTenantMgtDao mTenantMgtDao;

	public Long getTenantSeq(String subDomain) {
		MTenantMgtEntity mTnMgtEntity = mTenantMgtDao.selectBySubDomain(subDomain);
		if (mTnMgtEntity != null) {
			return mTnMgtEntity.getTenantSeq();
		} else {
			return null;
		}
	}

	/**
	 * プラン設定画面の入り口となるコントローラーメソッドのURLを取得する
	 * 
	 * @return
	 */
	public String getPlanSettingUrl() {

		String planSettingUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/" + UrlConstant.PLANSETTING_GATEWAY_URL + "/" + PlanConstant.PLAN_SETTING_GATEWAY_REDIRECT_PATH)
				.toUriString();

		return planSettingUrl;
	}

}
