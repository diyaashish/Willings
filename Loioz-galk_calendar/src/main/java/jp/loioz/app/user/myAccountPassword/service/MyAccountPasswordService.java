package jp.loioz.app.user.myAccountPassword.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.myAccountPassword.form.MyAccountPasswordInputForm;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.entity.MAccountEntity;

/**
 * パスワード変更 画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MyAccountPasswordService extends DefaultService {

	/** アカウント情報Dao */
	@Autowired
	private MAccountDao mAccountDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * アカウントパスワード変更入力オブジェクトを作成する
	 *
	 * @return
	 */
	public MyAccountPasswordInputForm.AccountPassWordInputForm createAccountPassWordInputForm() {

		MyAccountPasswordInputForm.AccountPassWordInputForm inputForm = new MyAccountPasswordInputForm.AccountPassWordInputForm();

		// 表示用プロパティの設定
		this.setDispProperties(inputForm);

		// 作成時の入力値は不要

		// 入力オブジェクトの返却
		return inputForm;
	}

	/**
	 * 画面表示用プロパティを設定
	 *
	 * @param inputForm
	 */
	public void setDispProperties(MyAccountPasswordInputForm.AccountPassWordInputForm inputForm) {

		// 現状なし
	}

	/**
	 * 更新処理
	 *
	 * @param form
	 * @throws AppException
	 */
	public void savePassWord(MyAccountPasswordInputForm.AccountPassWordInputForm inputForm) throws AppException {

		Long accountSeq = SessionUtils.getLoginAccountSeq();
		MAccountEntity mAccountEntity = mAccountDao.selectEnabledAccountByAccountSeq(accountSeq);
		if (mAccountEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// パスワード情報の変更
		String encodedNewPassWord = new BCryptPasswordEncoder().encode(inputForm.getPassword().getPassword());
		mAccountEntity.setPassword(encodedNewPassWord);

		try {
			// 更新処理
			mAccountDao.update(mAccountEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

		// パスワードの変更が正常に完了した場合のみ、セッションのパスワード情報を変更する
		SessionUtils.setPassword(mAccountEntity.getPassword());
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}