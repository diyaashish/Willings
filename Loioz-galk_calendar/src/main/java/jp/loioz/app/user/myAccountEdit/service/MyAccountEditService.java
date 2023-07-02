package jp.loioz.app.user.myAccountEdit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.myAccountEdit.form.MyAccountEditInputForm;
import jp.loioz.common.constant.CommonConstant.ImageRoleType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.FileUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MAccountImgDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MAccountImgEntity;

/**
 * 「アカウント情報の設定」画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MyAccountEditService extends DefaultService {

	/** アカウント情報Dao */
	@Autowired
	private MAccountDao mAccountDao;

	/** アカウント画像情報Dao */
	@Autowired
	private MAccountImgDao mAccountImgDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 個人情報設定：個人設定フラグメント入力フォームの作成
	 * 
	 * @return
	 * @throws AppException
	 */
	public MyAccountEditInputForm.AccountSettingInputForm createAccountSettingInputForm() throws AppException {

		Long accountSeq = SessionUtils.getLoginAccountSeq();
		MyAccountEditInputForm.AccountSettingInputForm inputForm = new MyAccountEditInputForm.AccountSettingInputForm();

		// 表示用プロパティの設定
		this.setDispProperties(inputForm);

		// 有効なアカウント情報の取得
		MAccountEntity mAccountEntity = mAccountDao.selectEnabledAccountByAccountSeq(accountSeq);
		if (mAccountEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// ***********************************
		// アカウント情報を設定する
		// ***********************************
		inputForm.setAccountId(mAccountEntity.getAccountId());
		inputForm.setAccountNameSei(mAccountEntity.getAccountNameSei());
		inputForm.setAccountNameSeiKana(mAccountEntity.getAccountNameSeiKana());
		inputForm.setAccountNameMei(mAccountEntity.getAccountNameMei());
		inputForm.setAccountNameMeiKana(mAccountEntity.getAccountNameMeiKana());
		inputForm.setAccountMailAddress(mAccountEntity.getAccountMailAddress());
		inputForm.setAccountColor(mAccountEntity.getAccountColor());
		inputForm.setAccountInvoiceRegistrationNo(mAccountEntity.getAccountInvoiceRegistrationNo());
		inputForm.setLawyerStampPrintEnabled(SystemFlg.codeToBoolean(mAccountEntity.getAccountLawyerStampPrintFlg()));
		inputForm.setAccountMailSignature(mAccountEntity.getAccountMailSignature());

		return inputForm;
	}

	/**
	 * 個人情報設定：個人設定フラグメント入力フォームの表示用プロパティを設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(MyAccountEditInputForm.AccountSettingInputForm inputForm) {

		// 現状なし
	}

	/**
	 * 更新処理
	 *
	 * @param form
	 * @throws AppException
	 */
	public void saveAccountSetting(MyAccountEditInputForm.AccountSettingInputForm inputForm) throws AppException {

		Long accountSeq = SessionUtils.getLoginAccountSeq();

		MAccountEntity mAccountEntity = mAccountDao.selectEnabledAccountByAccountSeq(accountSeq);
		if (mAccountEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// アカウント情報を設定
		mAccountEntity.setAccountId(inputForm.getAccountId());
		mAccountEntity.setAccountNameSei(inputForm.getAccountNameSei());
		mAccountEntity.setAccountNameSeiKana(inputForm.getAccountNameSeiKana());
		mAccountEntity.setAccountNameMei(inputForm.getAccountNameMei());
		mAccountEntity.setAccountNameMeiKana(inputForm.getAccountNameMeiKana());
		mAccountEntity.setAccountMailAddress(inputForm.getAccountMailAddress());
		mAccountEntity.setAccountColor(inputForm.getAccountColor());
		mAccountEntity.setAccountInvoiceRegistrationNo(inputForm.getAccountInvoiceRegistrationNo());
		mAccountEntity.setAccountLawyerStampPrintFlg(SystemFlg.booleanToCode(inputForm.isLawyerStampPrintEnabled()));
		mAccountEntity.setAccountMailSignature(inputForm.getAccountMailSignature());

		try {
			// 更新処理
			mAccountDao.update(mAccountEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

		// セッション情報を編集
		setSessionUserDetail(mAccountEntity);
	}

	/**
	 * 個人情報設定 弁護士職印フラグメント入力フォームの作成
	 * 
	 * @return
	 */
	public MyAccountEditInputForm.LawyerStampInputForm createLawyerStampInputForm() throws AppException {

		// 入力フォームオブジェクトを作成
		MyAccountEditInputForm.LawyerStampInputForm inputForm = new MyAccountEditInputForm.LawyerStampInputForm();

		// 表示用プロパティの設定
		this.setDispProperties(inputForm);

		// DBから画像データを取得 -> base64 に変換 -> 設定
		MAccountImgEntity mAccountImgEntity = mAccountImgDao.selectByAccountSeq(SessionUtils.getLoginAccountSeq());
		// 新規作成されたアカウントはDBに登録されていない
		if (mAccountImgEntity == null) {
			// DB値以外のパラメータはここで設定
			return inputForm;
		}

		// 入力値パラメータの設定
		inputForm.setAccountImgSeq(mAccountImgEntity.getAccountImgSeq());

		return inputForm;
	}

	/**
	 * 個人情報設定 弁護士職印フラグメント入力フォームの表示用プロパティを設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(MyAccountEditInputForm.LawyerStampInputForm inputForm) {

		// DBから画像データを取得 -> base64 に変換 -> 設定
		MAccountImgEntity mAccountImgEntity = mAccountImgDao.selectByAccountSeq(SessionUtils.getLoginAccountSeq());

		// 新規作成されたアカウントはDBに登録されていない
		if (mAccountImgEntity == null) {
			// DB値以外のパラメータはここで設定
			return;
		}

		byte[] lawyerStamp = FileUtils.toByteArray(mAccountImgEntity.getImgContents());
		if (lawyerStamp != null) {
			// 画像情報を設定
			inputForm.setBase64ImageSrc(FileUtils.toHtmlImgSrc(lawyerStamp, mAccountImgEntity.getImgExtension()));
		}
	}

	/**
	 * 個人情報設定 弁護士職印の登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registLawyerStamp(MyAccountEditInputForm.LawyerStampInputForm inputForm) throws AppException {

		// アカウントSEQ
		Long accountSeq = SessionUtils.getLoginAccountSeq();

		// ユーザーのチェック
		MAccountEntity mAccountEntity = mAccountDao.selectEnabledAccountByAccountSeq(accountSeq);
		if (mAccountEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		MultipartFile file = inputForm.getMultiPartFile();
		String extension = FileUtils.getExtension(file.getOriginalFilename());

		MAccountImgEntity mAccountImgEntity = new MAccountImgEntity();
		mAccountImgEntity.setAccountSeq(accountSeq);
		mAccountImgEntity.setImgContents(FileUtils.toBlob(file));
		mAccountImgEntity.setImgExtension(extension);
		mAccountImgEntity.setImgType(ImageRoleType.LAWYER_STAMP.getCd());

		try {
			// 登録処理
			mAccountImgDao.insert(mAccountImgEntity);

			// アカウント情報に登録した画像SEQを保存する
			mAccountEntity.setAccountLawyerStampImgSeq(mAccountImgEntity.getAccountImgSeq());
			mAccountDao.update(mAccountEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 個人情報設定 弁護士職印の更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateLawyerStamp(MyAccountEditInputForm.LawyerStampInputForm inputForm) throws AppException {

		// アカウントSEQ
		Long accountSeq = SessionUtils.getLoginAccountSeq();

		// ユーザーのチェック
		MAccountEntity mAccountEntity = mAccountDao.selectEnabledAccountByAccountSeq(accountSeq);
		if (mAccountEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// ユーザー印鑑情報の存在チェック
		MAccountImgEntity mAccountImgEntity = mAccountImgDao.selectBySeq(inputForm.getAccountImgSeq());
		if (mAccountImgEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		MultipartFile file = inputForm.getMultiPartFile();
		String extension = FileUtils.getExtension(file.getOriginalFilename());

		mAccountImgEntity.setImgContents(FileUtils.toBlob(file));
		mAccountImgEntity.setImgExtension(extension);
		mAccountImgEntity.setImgType(ImageRoleType.LAWYER_STAMP.getCd());

		try {
			// 登録処理
			mAccountImgDao.update(mAccountImgEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 弁護士印情報の削除処理
	 * 
	 * @param accountImgSeq
	 * @throws AppException
	 */
	public void deleteLawyerStamp(Long accountImgSeq) throws AppException {

		// ユーザー印鑑情報の存在チェック
		MAccountImgEntity mAccountImgEntity = mAccountImgDao.selectBySeq(accountImgSeq);
		if (mAccountImgEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// ユーザーのチェック
		MAccountEntity mAccountEntity = mAccountDao.selectEnabledAccountByAccountSeq(mAccountImgEntity.getAccountSeq());
		if (mAccountEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 削除処理
			mAccountImgDao.delete(mAccountImgEntity);

			// アカウント情報の画像SEQをnullに設定
			mAccountEntity.setAccountLawyerStampImgSeq(null);
			mAccountDao.update(mAccountEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * セッション情報の更新
	 *
	 * @param mAccountEntity
	 * @param changePassWord
	 */
	private void setSessionUserDetail(MAccountEntity mAccountEntity) {
		// Session情報を書き換える
		SessionUtils.setAccountId(mAccountEntity.getAccountId());
		SessionUtils.setAccountName(PersonName.fromEntity(mAccountEntity).getName());
		SessionUtils.setMailAddress(mAccountEntity.getAccountMailAddress());
	}

}