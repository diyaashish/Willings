package jp.loioz.domain.verification;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.dao.TTempAccountDao;
import jp.loioz.entity.TTempAccountEntity;

/**
 * アカウント認証(管理DB)サービスクラス
 */
@Service
public class TempAccountVerificationService extends VerificationService<TTempAccountEntity> {

	@Autowired
	private TTempAccountDao tTempAccountDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	@Override
	public TTempAccountEntity getVerification(String key) {
		return tTempAccountDao.selectByKey(key);
	}

	@Override
	protected LocalDateTime getLimitDate(TTempAccountEntity entity) {
		return entity.getTempLimitDate();
	}

	@Override
	protected String getCompleteFlg(TTempAccountEntity entity) {
		return entity.getCompleteFlg();
	}

	@Override
	protected int updateToCompleteExe(String key) {
		TTempAccountEntity entity = tTempAccountDao.selectByKey(key);
		entity.setCompleteFlg(SystemFlg.FLG_ON.getCd());
		return tTempAccountDao.update(entity);
	}
}
