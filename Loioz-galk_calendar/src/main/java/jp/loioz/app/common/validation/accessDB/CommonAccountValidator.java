package jp.loioz.app.common.validation.accessDB;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import jp.loioz.dao.MAccountDao;
import jp.loioz.entity.MAccountEntity;

/**
 * アカウント情報のDB整合性バリデータークラス
 */
@Component
public class CommonAccountValidator {

	/** アカウントDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * アカウントIDの重複チェック
	 *
	 * @param accountId
	 * @param accountSeq
	 * @return true: 重複あり、false：重複なし
	 */
	public boolean checkAccountIdExists(String accountId, Long accountSeq) {
		boolean error = false;
		// 存在チェック
		List<MAccountEntity> mAccountEntityList = mAccountDao.selectByIdExists(accountId, accountSeq);
		if (!mAccountEntityList.isEmpty()) {
			error = true;
		}
		return error;
	}

	/**
	 * パスワード認証DBチェック
	 * 
	 * @param inputPassWord
	 * @param accountSeq
	 * @return
	 */
	public boolean passWordMatchesValidate(String inputPassWord, Long accountSeq) {

		MAccountEntity mAccountEntity = mAccountDao.selectEnabledAccountByAccountSeq(accountSeq);
		if (mAccountEntity == null) {
			// データがない場合も、認証は失敗とみなす
			return false;
		}
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if (!passwordEncoder.matches(inputPassWord, mAccountEntity.getPassword())) {
			// パスワードが一致しない場合 -> 認証失敗
			return false;
		}

		return true;
	}

}
