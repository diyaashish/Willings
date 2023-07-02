package jp.loioz.app.user.officeContractManageSetting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.officeContractManageSetting.form.OfficeContractManageSettingInputForm;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MTenantDao;
import jp.loioz.entity.MTenantEntity;

/**
 * 契約担当者設定画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OfficeContractManageSettingService extends DefaultService {

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
	 * 契約担当情報入力フォームの作成
	 * 
	 * @return
	 */
	public OfficeContractManageSettingInputForm.ContactManagerSettingInputForm createContactManagerSettingInputForm(Long tenantSeq) {

		var inputForm = new OfficeContractManageSettingInputForm.ContactManagerSettingInputForm();

		// 表示用プロパティの設定
		this.setDispProperties(tenantSeq, inputForm);

		// 入力値の設定
		MTenantEntity mTenantEntity = this.getTenantEntity(tenantSeq);
		inputForm.setTenantDaihyoNameSei(mTenantEntity.getTenantDaihyoNameSei());
		inputForm.setTenantDaihyoNameSeiKana(mTenantEntity.getTenantDaihyoNameSeiKana());
		inputForm.setTenantDaihyoNameMei(mTenantEntity.getTenantDaihyoNameMei());
		inputForm.setTenantDaihyoNameMeiKana(mTenantEntity.getTenantDaihyoNameMeiKana());
		inputForm.setTenantDaihyoMailAddress(mTenantEntity.getTenantDaihyoMailAddress());

		return inputForm;
	}

	/**
	 * 契約担当情報入力フォーム 表示用プロパティの設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(Long tenantSeq, OfficeContractManageSettingInputForm.ContactManagerSettingInputForm inputForm) {
		// 現状無し
	}

	/**
	 * 契約担当者情報の更新
	 * 
	 * @param tenantSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveContactManager(Long tenantSeq, OfficeContractManageSettingInputForm.ContactManagerSettingInputForm inputForm) throws AppException {

		// テナント情報の取得
		MTenantEntity mTenantEntity = this.getTenantEntity(tenantSeq);

		mTenantEntity.setTenantDaihyoNameSei(inputForm.getTenantDaihyoNameSei());
		mTenantEntity.setTenantDaihyoNameSeiKana(inputForm.getTenantDaihyoNameSeiKana());
		mTenantEntity.setTenantDaihyoNameMei(inputForm.getTenantDaihyoNameMei());
		mTenantEntity.setTenantDaihyoNameMeiKana(inputForm.getTenantDaihyoNameMeiKana());
		mTenantEntity.setTenantDaihyoMailAddress(inputForm.getTenantDaihyoMailAddress());

		try {
			// 更新処理
			mTenantDao.update(mTenantEntity);

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

	/**
	 * テナント情報の取得
	 * 
	 * @param tenantSeq
	 * @return
	 */
	private MTenantEntity getTenantEntity(Long tenantSeq) {
		// 詳細情報を取得
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(tenantSeq);
		if (mTenantEntity == null) {
			// テナント情報が取得できない場合は、想定外なのでシステムエラーとする
			throw new RuntimeException("テナント情報が取得できませんでした。tenantSeq : " + tenantSeq);
		}
		return mTenantEntity;
	}

}