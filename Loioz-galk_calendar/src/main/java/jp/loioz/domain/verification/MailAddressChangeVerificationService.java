package jp.loioz.domain.verification;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.dao.TMailAddressChangeVerificationDao;
import jp.loioz.entity.TMailAddressChangeVerificationEntity;

/**
 * メールアドレス変更認証サービスクラス
 */
@Service
public class MailAddressChangeVerificationService extends VerificationService<TMailAddressChangeVerificationEntity> {

	@Autowired
	private TMailAddressChangeVerificationDao tMailAddressChangeVerificationDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	@Override
	public TMailAddressChangeVerificationEntity getVerification(String key) {
		return tMailAddressChangeVerificationDao.selectByKey(key);
	}

	@Override
	protected LocalDateTime getLimitDate(TMailAddressChangeVerificationEntity entity) {
		return entity.getTempLimitDate();
	}

	@Override
	protected String getCompleteFlg(TMailAddressChangeVerificationEntity entity) {
		return entity.getCompleteFlg();
	}

	@Override
	protected int updateToCompleteExe(String key) {
		TMailAddressChangeVerificationEntity entity = tMailAddressChangeVerificationDao.selectByKey(key);
		entity.setCompleteFlg(SystemFlg.FLG_ON.getCd());
		return tMailAddressChangeVerificationDao.update(entity);
	}
}
