package jp.loioz.domain.verification;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.dao.TAccountVerificationDao;
import jp.loioz.entity.TAccountVerificationEntity;

/**
 * アカウント認証(テナントDB)サービスクラス
 */
@Service
public class AccountVerificationService extends VerificationService<TAccountVerificationEntity> {

	@Autowired
	private TAccountVerificationDao tAccountVerificationDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	@Override
	public TAccountVerificationEntity getVerification(String key) {
		return tAccountVerificationDao.selectByKey(key);
	}

	@Override
	protected LocalDateTime getLimitDate(TAccountVerificationEntity entity) {
		return entity.getTempLimitDate();
	}

	@Override
	protected String getCompleteFlg(TAccountVerificationEntity entity) {
		return entity.getCompleteFlg();
	}

	@Override
	protected int updateToCompleteExe(String key) {
		TAccountVerificationEntity entity = tAccountVerificationDao.selectByKey(key);
		entity.setCompleteFlg(SystemFlg.FLG_ON.getCd());
		return tAccountVerificationDao.update(entity);
	}
}
