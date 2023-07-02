package jp.loioz.app.user.invoiceSetting.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.invoiceSetting.form.InvoiceSettingInputForm;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MInvoiceSettingDao;
import jp.loioz.domain.value.AccountingNumber;
import jp.loioz.entity.MInvoiceSettingEntity;

/**
 * 請求書の設定画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InvoiceSettingService extends DefaultService {

	/** 請求書設定Daoクラス */
	@Autowired
	private MInvoiceSettingDao mInvoiceSettingDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 請求書設定フラグメントの入力フォームオブジェクトを作成
	 * 
	 * @return
	 */
	public InvoiceSettingInputForm.InvoiceSettingFragmentInputForm createInvoiceSettingFragmentInputForm() {

		var inputForm = new InvoiceSettingInputForm.InvoiceSettingFragmentInputForm();
		this.setDispProperties(inputForm);

		// データの取得処理
		MInvoiceSettingEntity mInvoiceSettingEntity = mInvoiceSettingDao.select();

		// データの設定
		inputForm.setInvoiceSettingSeq(mInvoiceSettingEntity.getInvoiceSettingSeq());
		inputForm.setDefaultTitle(mInvoiceSettingEntity.getDefaultTitle());
		inputForm.setDefaultSubText(mInvoiceSettingEntity.getDefaultSubText());
		inputForm.setSubjectPrefix(mInvoiceSettingEntity.getSubjectPrefix());
		inputForm.setSubjectSuffix(mInvoiceSettingEntity.getSubjectSuffix());
		inputForm.setInvoiceNoPrefix(mInvoiceSettingEntity.getInvoiceNoPrefix());
		inputForm.setInvoiceNoYFmtCd(mInvoiceSettingEntity.getInvoiceNoYFmt());
		inputForm.setInvoiceNoMFmtCd(mInvoiceSettingEntity.getInvoiceNoMFmt());
		inputForm.setInvoiceNoDFmtCd(mInvoiceSettingEntity.getInvoiceNoDFmt());
		inputForm.setInvoiceNoDelimiter(mInvoiceSettingEntity.getInvoiceNoDelimiter());
		inputForm.setInvoiceNoNumberingTypeCd(mInvoiceSettingEntity.getInvoiceNoNumberingType());
		inputForm.setInvoiceNoZeroPadEnabled(SystemFlg.codeToBoolean(mInvoiceSettingEntity.getInvoiceNoZeroPadFlg()));
		inputForm.setInvoiceNoZeroPadDigits(mInvoiceSettingEntity.getInvoiceNoZeroPadDigits());
		inputForm.setTransactionDatePrintEnabled(SystemFlg.codeToBoolean(mInvoiceSettingEntity.getTransactionDatePrintFlg()));
		inputForm.setDueDatePrintEnabled(SystemFlg.codeToBoolean(mInvoiceSettingEntity.getDueDatePrintFlg()));
		inputForm.setDefaultRemarks(mInvoiceSettingEntity.getDefaultRemarks());

		return inputForm;
	}

	/**
	 * 請求書設定フラグメントの入力フォームオブジェクトの表示用プロパティを設定する
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(InvoiceSettingInputForm.InvoiceSettingFragmentInputForm inputForm) {

		// データの取得処理
		MInvoiceSettingEntity mInvoiceSettingEntity = mInvoiceSettingDao.select();

		// データの設定
		inputForm.setPreview(AccountingNumber.fromEntity(mInvoiceSettingEntity).getAccgNoView(LocalDate.now(), 1));
	}

	/**
	 * 請求書設定の更新処理
	 * 
	 * @param inputForm
	 */
	public void update(InvoiceSettingInputForm.InvoiceSettingFragmentInputForm inputForm) throws AppException {

		// データの取得処理
		MInvoiceSettingEntity mInvoiceSettingEntity = mInvoiceSettingDao.select();

		mInvoiceSettingEntity.setDefaultTitle(inputForm.getDefaultTitle());
		mInvoiceSettingEntity.setDefaultSubText(inputForm.getDefaultSubText());
		mInvoiceSettingEntity.setSubjectPrefix(inputForm.getSubjectPrefix());
		mInvoiceSettingEntity.setSubjectSuffix(inputForm.getSubjectSuffix());
		mInvoiceSettingEntity.setInvoiceNoPrefix(inputForm.getInvoiceNoPrefix());
		mInvoiceSettingEntity.setInvoiceNoYFmt(inputForm.getInvoiceNoYFmtCd());
		mInvoiceSettingEntity.setInvoiceNoMFmt(inputForm.getInvoiceNoMFmtCd());
		mInvoiceSettingEntity.setInvoiceNoDFmt(inputForm.getInvoiceNoDFmtCd());
		mInvoiceSettingEntity.setInvoiceNoDelimiter(inputForm.getInvoiceNoDelimiter());
		mInvoiceSettingEntity.setInvoiceNoNumberingType(inputForm.getInvoiceNoNumberingTypeCd());
		mInvoiceSettingEntity.setInvoiceNoZeroPadFlg(SystemFlg.booleanToCode(inputForm.isInvoiceNoZeroPadEnabled()));
		mInvoiceSettingEntity.setInvoiceNoZeroPadDigits(inputForm.getInvoiceNoZeroPadDigits());
		mInvoiceSettingEntity.setTransactionDatePrintFlg(SystemFlg.booleanToCode(inputForm.isTransactionDatePrintEnabled()));
		mInvoiceSettingEntity.setDueDatePrintFlg(SystemFlg.booleanToCode(inputForm.isDueDatePrintEnabled()));
		mInvoiceSettingEntity.setDefaultRemarks(inputForm.getDefaultRemarks());

		try {
			// 更新処理
			mInvoiceSettingDao.update(mInvoiceSettingEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

}