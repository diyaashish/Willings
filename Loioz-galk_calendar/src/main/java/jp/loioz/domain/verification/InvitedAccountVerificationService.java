package jp.loioz.domain.verification;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.dao.TInvitedAccountVerificationDao;
import jp.loioz.entity.TInvitedAccountVerificationEntity;

/**
 * 招待アカウント認証サービスクラス
 */
@Service
public class InvitedAccountVerificationService extends VerificationService<TInvitedAccountVerificationEntity> {

	@Autowired
	private TInvitedAccountVerificationDao tInvitedAccountVerificationDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	@Override
	public TInvitedAccountVerificationEntity getVerification(String key) {
		return tInvitedAccountVerificationDao.selectByKey(key);
	}

	@Override
	protected LocalDateTime getLimitDate(TInvitedAccountVerificationEntity entity) {
		return entity.getTempLimitDate();
	}

	@Override
	protected String getCompleteFlg(TInvitedAccountVerificationEntity entity) {
		return entity.getCompleteFlg();
	}

	@Override
	protected int updateToCompleteExe(String key) {
		TInvitedAccountVerificationEntity entity = tInvitedAccountVerificationDao.selectByKey(key);
		entity.setCompleteFlg(SystemFlg.FLG_ON.getCd());
		return tInvitedAccountVerificationDao.update(entity);
	}
}
