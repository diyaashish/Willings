package jp.loioz.app.user.mailBaseSetting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.mailBaseSetting.form.MailBaseSettingInputForm;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.dao.MMailSettingDao;
import jp.loioz.entity.MMailSettingEntity;

/**
 * メール設定 基本設定画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MailBaseSettingService extends DefaultService {

	/** メール設定Daoクラス */
	@Autowired
	private MMailSettingDao mMailSettingDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * メール設定画面：ダウンロード期限の入力用オブジェクトを作成
	 * 
	 * @return
	 */
	public MailBaseSettingInputForm.DownloadPeriodSettingInputForm createDownloadPeriodSettingInputForm() {

		var inputForm = new MailBaseSettingInputForm.DownloadPeriodSettingInputForm();
		setDispProperties(inputForm);

		MMailSettingEntity mMailSettingEntity = mMailSettingDao.select();
		inputForm.setReportDownloadPeroidDays(LoiozNumberUtils.parseAsString(mMailSettingEntity.getDownloadDayCount()));
		return inputForm;
	}

	/**
	 * メール設定画面：ダウンロード期限の表示用プロパティを設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(MailBaseSettingInputForm.DownloadPeriodSettingInputForm inputForm) {
		// 現状無し
	}

	/**
	 * メール設定画面：メールパスワード設定の入力用オブジェクトを作成
	 * 
	 * @return
	 */
	public MailBaseSettingInputForm.MailPasswordSettingInputForm createMailPasswordSettingInputForm() {

		var inputForm = new MailBaseSettingInputForm.MailPasswordSettingInputForm();
		setDispProperties(inputForm);

		MMailSettingEntity mMailSettingEntity = mMailSettingDao.select();
		inputForm.setDownloadViewPasswordEnabled(SystemFlg.codeToBoolean(mMailSettingEntity.getDownloadViewPasswordEnableFlg()));
		return inputForm;
	}

	/**
	 * メール設定画面：メールパスワード設定の表示用プロパティを設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(MailBaseSettingInputForm.MailPasswordSettingInputForm inputForm) {
		// 現状無し
	}

	/**
	 * ダウンロード期間情報の保存処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveDownloadPeriodSetting(MailBaseSettingInputForm.DownloadPeriodSettingInputForm inputForm) throws AppException {

		MMailSettingEntity mMailSettingEntity = mMailSettingDao.select();
		mMailSettingEntity.setDownloadDayCount(LoiozNumberUtils.parseAsLong(inputForm.getReportDownloadPeroidDays()));

		try {
			// 更新処理
			mMailSettingDao.update(mMailSettingEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * メールパスワード設定情報の保存処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveMailPasswordSetting(MailBaseSettingInputForm.MailPasswordSettingInputForm inputForm) throws AppException {

		MMailSettingEntity mMailSettingEntity = mMailSettingDao.select();
		mMailSettingEntity.setDownloadViewPasswordEnableFlg(SystemFlg.booleanToCode(inputForm.isDownloadViewPasswordEnabled()));

		try {
			// 更新処理
			mMailSettingDao.update(mMailSettingEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

}