package jp.loioz.app.user.statementSetting.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.statementSetting.form.StatementSettingInputForm;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MStatementSettingDao;
import jp.loioz.domain.value.AccountingNumber;
import jp.loioz.entity.MStatementSettingEntity;

/**
 * 精算書の設定画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StatementSettingService extends DefaultService {

	/** 精算書設定Daoクラス */
	@Autowired
	private MStatementSettingDao mStatementSettingDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 精算書設定フラグメントの入力フォームオブジェクトを作成
	 * 
	 * @return
	 */
	public StatementSettingInputForm.StatementSettingFragmentInputForm createStatementSettingFragmentInputForm() {

		var inputForm = new StatementSettingInputForm.StatementSettingFragmentInputForm();
		this.setDispProperties(inputForm);

		// データの取得処理
		MStatementSettingEntity mStatementSettingEntity = mStatementSettingDao.select();

		// データの設定
		inputForm.setStatementSettingSeq(mStatementSettingEntity.getStatementSettingSeq());
		inputForm.setDefaultTitle(mStatementSettingEntity.getDefaultTitle());
		inputForm.setDefaultSubText(mStatementSettingEntity.getDefaultSubText());
		inputForm.setSubjectPrefix(mStatementSettingEntity.getSubjectPrefix());
		inputForm.setSubjectSuffix(mStatementSettingEntity.getSubjectSuffix());
		inputForm.setStatementNoPrefix(mStatementSettingEntity.getStatementNoPrefix());
		inputForm.setStatementNoYFmtCd(mStatementSettingEntity.getStatementNoYFmt());
		inputForm.setStatementNoMFmtCd(mStatementSettingEntity.getStatementNoMFmt());
		inputForm.setStatementNoDFmtCd(mStatementSettingEntity.getStatementNoDFmt());
		inputForm.setStatementNoDelimiter(mStatementSettingEntity.getStatementNoDelimiter());
		inputForm.setStatementNoNumberingTypeCd(mStatementSettingEntity.getStatementNoNumberingType());
		inputForm.setStatementNoZeroPadEnabled(SystemFlg.codeToBoolean(mStatementSettingEntity.getStatementNoZeroPadFlg()));
		inputForm.setStatementNoZeroPadDigits(mStatementSettingEntity.getStatementNoZeroPadDigits());
		inputForm.setTransactionDatePrintEnabled(SystemFlg.codeToBoolean(mStatementSettingEntity.getTransactionDatePrintFlg()));
		inputForm.setRefundDatePrintEnabled(SystemFlg.codeToBoolean(mStatementSettingEntity.getRefundDatePrintFlg()));
		inputForm.setDefaultRemarks(mStatementSettingEntity.getDefaultRemarks());

		return inputForm;
	}

	/**
	 * 精算書設定フラグメントの入力フォームオブジェクトの表示用プロパティを設定する
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(StatementSettingInputForm.StatementSettingFragmentInputForm inputForm) {

		// データの取得処理
		MStatementSettingEntity mStatementSettingEntity = mStatementSettingDao.select();

		// データの設定
		inputForm.setPreview(AccountingNumber.fromEntity(mStatementSettingEntity).getAccgNoView(LocalDate.now(), 1));
	}

	/**
	 * 精算書設定の更新処理
	 * 
	 * @param inputForm
	 */
	public void update(StatementSettingInputForm.StatementSettingFragmentInputForm inputForm) throws AppException {

		// データの取得処理
		MStatementSettingEntity mStatementSettingEntity = mStatementSettingDao.select();

		mStatementSettingEntity.setDefaultTitle(inputForm.getDefaultTitle());
		mStatementSettingEntity.setDefaultSubText(inputForm.getDefaultSubText());
		mStatementSettingEntity.setSubjectPrefix(inputForm.getSubjectPrefix());
		mStatementSettingEntity.setSubjectSuffix(inputForm.getSubjectSuffix());
		mStatementSettingEntity.setStatementNoPrefix(inputForm.getStatementNoPrefix());
		mStatementSettingEntity.setStatementNoYFmt(inputForm.getStatementNoYFmtCd());
		mStatementSettingEntity.setStatementNoMFmt(inputForm.getStatementNoMFmtCd());
		mStatementSettingEntity.setStatementNoDFmt(inputForm.getStatementNoDFmtCd());
		mStatementSettingEntity.setStatementNoDelimiter(inputForm.getStatementNoDelimiter());
		mStatementSettingEntity.setStatementNoNumberingType(inputForm.getStatementNoNumberingTypeCd());
		mStatementSettingEntity.setStatementNoZeroPadFlg(SystemFlg.booleanToCode(inputForm.isStatementNoZeroPadEnabled()));
		mStatementSettingEntity.setStatementNoZeroPadDigits(inputForm.getStatementNoZeroPadDigits());
		mStatementSettingEntity.setTransactionDatePrintFlg(SystemFlg.booleanToCode(inputForm.isTransactionDatePrintEnabled()));
		mStatementSettingEntity.setRefundDatePrintFlg(SystemFlg.booleanToCode(inputForm.isRefundDatePrintEnabled()));
		mStatementSettingEntity.setDefaultRemarks(inputForm.getDefaultRemarks());

		try {
			// 更新処理
			mStatementSettingDao.update(mStatementSettingEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

}