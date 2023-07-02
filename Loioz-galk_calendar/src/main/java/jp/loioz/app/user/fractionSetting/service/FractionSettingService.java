package jp.loioz.app.user.fractionSetting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.fractionSetting.form.FractionSettingInputForm;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.MTenantDao;
import jp.loioz.entity.MTenantEntity;

/**
 * 端数処理 画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FractionSettingService extends DefaultService {

	/** 事務所情報取得用のDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 事務所設定：端数処理入力フラグメントオブジェクトを取得
	 *
	 * @return
	 */
	public FractionSettingInputForm.FractionSettingInputFragmentForm getFractionSettingInputFragmentForm() {

		// フォームの作成
		FractionSettingInputForm.FractionSettingInputFragmentForm inputForm = new FractionSettingInputForm.FractionSettingInputFragmentForm();

		// Init処理
		this.setDispFractionSettingInputFragmentForm(inputForm);

		// 事務所連番を取得
		Long tenantSeq = SessionUtils.getTenantSeq();

		// 詳細情報を取得
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(tenantSeq);
		if (mTenantEntity == null) {
			// テナント情報が取得できない場合は、想定外なのでシステムエラーとする
			throw new RuntimeException("テナント情報が取得できませんでした。tenantSeq : " + tenantSeq);
		}

		inputForm.setHoshuHasuType(mTenantEntity.getHoshuHasuType());
		inputForm.setTaxHasuType(mTenantEntity.getTaxHasuType());

		return inputForm;
	}

	/**
	 * 事務所設定：端数処理入力フラグメントオブジェクトに表示用データを設定する
	 * 
	 * @param inputForm
	 */
	public void setDispFractionSettingInputFragmentForm(FractionSettingInputForm.FractionSettingInputFragmentForm inputForm) {

		// 現状はなし
	}

	/**
	 * 端数処理の保存（更新）処理
	 *
	 * @param フォーム情報
	 */
	public void update(FractionSettingInputForm.FractionSettingInputFragmentForm inputForm) throws AppException {

		// 事務所連番を取得
		Long tenantSeq = SessionUtils.getTenantSeq();

		// 更新対象のデータをDBから取得
		MTenantEntity updateMTenantEntity = mTenantDao.selectBySeq(tenantSeq);

		// 事務所情報の設定
		updateMTenantEntity.setTaxHasuType(inputForm.getTaxHasuType());
		updateMTenantEntity.setHoshuHasuType(inputForm.getHoshuHasuType());

		try {
			// 更新処理
			mTenantDao.update(updateMTenantEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}