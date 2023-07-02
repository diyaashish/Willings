package jp.loioz.app.global.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.global.common.exception.GlobalAuthException;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MTenantMgtDao;
import jp.loioz.entity.MTenantMgtEntity;

/**
 * グローバル共通サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonGlobalService {

	/** ロガー */
	@Autowired
	private Logger logger;

	/** MGTDB：テナントMGTDaoクラス */
	@Autowired
	private MTenantMgtDao mTenantMgtDao;

	/**
	 * テナントの認証キー(5桁 + テナント番号)からテナント番号を取得する
	 * 
	 * @param tenantAuthKey
	 * @return
	 */
	public Long decipherTenantSeq(String tenantAuthKey) throws GlobalAuthException {

		if (StringUtils.isEmpty(tenantAuthKey)) {
			// 空文字対応
			logger.warn("テナントSEQの取得に失敗しました。");
			throw new GlobalAuthException(MessageEnum.MSG_E00175, "テナントSEQの取得に失敗しました。");
		}

		Long tenantSeq;
		try {
			// テナントSEQの取得
			String tenantSeqStr = tenantAuthKey.substring(CommonConstant.GLOBAL_TENANT_AUTH_DUMMY_LENGTH);
			tenantSeq = Long.valueOf(tenantSeqStr);

		} catch (IndexOutOfBoundsException | NumberFormatException ex) {
			// 不正なリクエストパラメータ
			logger.error("テナントSEQの取得に失敗しました。\n" + "「tenantAuthKey：" + tenantAuthKey + "」", ex);
			throw new GlobalAuthException(MessageEnum.MSG_E00175, "テナントSEQの取得に失敗しました。", ex);
		}

		MTenantMgtEntity mTenantMgtEntity = mTenantMgtDao.selectBySeq(tenantSeq);
		if (mTenantMgtEntity == null) {
			// 不正なリクエストパラメータ
			logger.error("テナントSEQの取得に失敗しました。\n" + "「tenantAuthKey：" + tenantAuthKey + "」");
			throw new GlobalAuthException(MessageEnum.MSG_E00175, "テナントSEQの取得に失敗しました。");
		}

		// テナントSEQを返却
		return mTenantMgtEntity.getTenantSeq();
	}

}
