package jp.loioz.app.user.officeSetting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.officeSetting.form.OfficeSettingInputForm;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.FileUtils;
import jp.loioz.dao.MInvoiceSettingDao;
import jp.loioz.dao.MStatementSettingDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.entity.MInvoiceSettingEntity;
import jp.loioz.entity.MStatementSettingEntity;
import jp.loioz.entity.MTenantEntity;

/**
 * 事務所設定画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OfficeSettingService extends DefaultService {

	/** 事務所情報取得用のDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** 請求書設定マスタのDaoクラス */
	@Autowired
	private MInvoiceSettingDao mInvoiceSettingDao;
	
	/** 精算書設定マスタのDaoクラス */
	@Autowired
	private MStatementSettingDao mStatementSettingDao;
	
	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 事務所情報入力フォームの作成
	 * 
	 * @return
	 */
	public OfficeSettingInputForm.OfficeInfoSettingInputForm createOfficeInfoSettingInputForm(Long tenantSeq) {

		var inputForm = new OfficeSettingInputForm.OfficeInfoSettingInputForm();

		// 表示用プロパティの設定
		this.setDispProperties(tenantSeq, inputForm);

		// 入力値の設定
		MTenantEntity mTenantEntity = this.getTenantEntity(tenantSeq);
		inputForm.setTenantName(mTenantEntity.getTenantName());
		inputForm.setTenantType(mTenantEntity.getTenantType());
		inputForm.setTenantZipCd(mTenantEntity.getTenantZipCd());
		inputForm.setTenantAddress1(mTenantEntity.getTenantAddress1());
		inputForm.setTenantAddress2(mTenantEntity.getTenantAddress2());
		inputForm.setTenantTelNo(mTenantEntity.getTenantTelNo());
		inputForm.setTenantFaxNo(mTenantEntity.getTenantFaxNo());
		inputForm.setTenantInvoiceRegistrationNo(mTenantEntity.getTenantInvoiceRegistrationNo());

		// 事務所印の表示チェック
		//
		// ※事務所印の表示チェックの状態は「請求書設定」のテーブルの値で表示する
		//   （事務所印の表示チェックを保存する場合、保存先は「事務所情報」テーブルではなく、「請求書設定」「精算書設定」の2つのテーブルの事務所印表示フラグのカラムとなる。
		//    そのため「請求書設定」「精算書設定」の2つのテーブルの事務所印表示フラグの値は必ず同じ値になっているため、どちらの値をもとに表示をしてもよいが、
		//    「請求書設定」のテーブルの値を利用する。）
		//
		MInvoiceSettingEntity mInvoiceSettingEntity = mInvoiceSettingDao.select();
		inputForm.setTenantStampPrintEnabled(SystemFlg.codeToBoolean(mInvoiceSettingEntity.getTenantStampPrintFlg()));
		
		return inputForm;
	}

	/**
	 * 事務所情報入力フォーム 表示用プロパティの設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(Long tenantSeq, OfficeSettingInputForm.OfficeInfoSettingInputForm inputForm) {
		// 現状無し
	}

	/**
	 * 事務印情報入力フォームの作成
	 * 
	 * @return
	 */
	public OfficeSettingInputForm.OfficeStampSettingInputForm createOfficeStampSettingInputForm(Long tenantSeq) {

		var inputForm = new OfficeSettingInputForm.OfficeStampSettingInputForm();

		// 表示用プロパティの設定
		this.setDispProperties(tenantSeq, inputForm);

		// 入力値の設定
		// 現状なし

		return inputForm;
	}

	/**
	 * 事務印情報入力フォーム 表示用プロパティの設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(Long tenantSeq, OfficeSettingInputForm.OfficeStampSettingInputForm inputForm) {

		MTenantEntity mTenantEntity = this.getTenantEntity(tenantSeq);

		// 事務所印の表示用文字列の設定
		byte[] officeStamp = FileUtils.toByteArray(mTenantEntity.getTenantStampImg());
		if (officeStamp != null) {
			// 画像情報を設定
			inputForm.setExistsImage(true);
			inputForm.setBase64ImageSrc(FileUtils.toHtmlImgSrc(officeStamp, mTenantEntity.getTenantStampImgExtension()));
		}

	}

	/**
	 * 事務所情報の更新
	 * 
	 * @param tenantSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveOfficeInfo(Long tenantSeq, OfficeSettingInputForm.OfficeInfoSettingInputForm inputForm) throws AppException {

		// テナント情報の取得
		MTenantEntity mTenantEntity = this.getTenantEntity(tenantSeq);

		mTenantEntity.setTenantName(inputForm.getTenantName());
		mTenantEntity.setTenantType(inputForm.getTenantType());
		mTenantEntity.setTenantZipCd(inputForm.getTenantZipCd());
		mTenantEntity.setTenantAddress1(inputForm.getTenantAddress1());
		mTenantEntity.setTenantAddress2(inputForm.getTenantAddress2());
		mTenantEntity.setTenantTelNo(inputForm.getTenantTelNo());
		mTenantEntity.setTenantFaxNo(inputForm.getTenantFaxNo());
		mTenantEntity.setTenantInvoiceRegistrationNo(inputForm.getTenantInvoiceRegistrationNo());

		try {
			// 更新処理
			mTenantDao.update(mTenantEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		
		// 事務所印の表示チェック
		//
		// ※事務所印の表示チェックの状態の保存先は「事務所情報」テーブルでなく、
		//    「請求書設定」「精算書設定」の2つのテーブルの事務所印表示フラグのカラムになる。
		//    （2つのテーブルに同じ値を設定する。）
		
		String tenantStampPrintFlg = SystemFlg.booleanToCode(inputForm.isTenantStampPrintEnabled());
		
		MInvoiceSettingEntity mInvoiceSettingEntity = mInvoiceSettingDao.select();
		MStatementSettingEntity mStatementSettingEntity = mStatementSettingDao.select();
		
		mInvoiceSettingEntity.setTenantStampPrintFlg(tenantStampPrintFlg);
		mStatementSettingEntity.setTenantStampPrintFlg(tenantStampPrintFlg);
		
		try {
			// 更新処理
			mInvoiceSettingDao.update(mInvoiceSettingEntity);
			mStatementSettingDao.update(mStatementSettingEntity);
			
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		
	}

	/**
	 * 事務所印情報の更新
	 * 
	 * @param tenantSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveOfficeStamp(Long tenantSeq, OfficeSettingInputForm.OfficeStampSettingInputForm inputForm) throws AppException {

		// テナント情報の取得
		MTenantEntity mTenantEntity = this.getTenantEntity(tenantSeq);

		// ファイルをEntityに設定
		MultipartFile tenantStamp = inputForm.getMultiPartFile();
		mTenantEntity.setTenantStampImg(FileUtils.toBlob(tenantStamp));
		mTenantEntity.setTenantStampImgExtension(FileUtils.getExtension(tenantStamp.getOriginalFilename()));

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

	/**
	 * 事務所印情報の削除
	 * 
	 * @param tenantSeq
	 * @throws AppException
	 */
	public void deleteOfficeStamp(Long tenantSeq) throws AppException {

		// テナント情報の取得
		MTenantEntity mTenantEntity = this.getTenantEntity(tenantSeq);

		// 印鑑カラムにnullを設定
		mTenantEntity.setTenantStampImg(null);
		mTenantEntity.setTenantStampImgExtension(null);

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