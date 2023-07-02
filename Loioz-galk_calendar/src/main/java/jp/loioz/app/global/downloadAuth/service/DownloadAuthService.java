package jp.loioz.app.global.downloadAuth.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.global.common.exception.GlobalAuthException;
import jp.loioz.app.global.common.service.CommonDownloadService;
import jp.loioz.app.global.downloadAuth.form.DownloadAuthInputForm;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.entity.TAccgDocDownloadEntity;

/**
 * グローバルダウンロード認証のコントローラークラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DownloadAuthService extends DefaultService {

	/** ロガー */
	@Autowired
	private Logger logger;

	/** 共通：ダウンロードサービス */
	@Autowired
	private CommonDownloadService commonDownloadService;

	/**
	 * アクセスが有効かどうか
	 *
	 * @param downloadUrlKey
	 * @return
	 * @throws GlobalAuthException
	 */
	public void varificationAccessEnableCheck(String downloadUrlKey) throws GlobalAuthException {
		// 認証キーからダウンロード情報を取得する。エラーが発生しなければ有効
		TAccgDocDownloadEntity tAccgDocDownloadEntity = commonDownloadService.getAccgDocDownloadEntity(downloadUrlKey);
		
		// 有効期限の検証。エラーが発生しなければ有効
		LocalDate now = LocalDate.now();
		commonDownloadService.validDownloadLimit(tAccgDocDownloadEntity, now);
	}

	/**
	 * パスワード入力が必要かどうか
	 * 
	 * @param downloadUrlKey
	 * @return
	 * @throws GlobalAuthException
	 */
	public boolean needPassWord(String downloadUrlKey) throws GlobalAuthException {

		TAccgDocDownloadEntity tAccgDocDownloadEntity = commonDownloadService.getAccgDocDownloadEntity(downloadUrlKey);
		if (!StringUtils.isEmpty(tAccgDocDownloadEntity.getDownloadViewPassword())) {
			return true;
		}

		return false;
	}

	/**
	 * パスワード認証をせずに、認証トークンを取得する<br>
	 * ※パスワード認証が必要なデータの場合はエラーとなる
	 * 
	 * @param downloadUrlKey
	 * @return
	 */
	public String getVerificationTokenForPassRequired(String downloadUrlKey) throws GlobalAuthException {

		TAccgDocDownloadEntity tAccgDocDownloadEntity = commonDownloadService.getAccgDocDownloadEntity(downloadUrlKey);
		if (!StringUtils.isEmpty(tAccgDocDownloadEntity.getDownloadViewPassword())) {
			throw new RuntimeException("パスワード認証が必要なデータのため、取得できません");
		}

		return tAccgDocDownloadEntity.getVerificationToken();
	}

	/**
	 * パスワード認証入力フォームオブジェクトを作成する
	 * 
	 * @param varificationKey
	 * @return
	 */
	public DownloadAuthInputForm.DownloadPasswordInputForm createDownloadPasswordInputForm(String varificationKey) {

		var inputForm = new DownloadAuthInputForm.DownloadPasswordInputForm();

		setDispProperties(inputForm);

		return inputForm;
	}

	/**
	 * 表示用プロパティを設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(DownloadAuthInputForm.DownloadPasswordInputForm inputForm) {
		// 現状なし
	}

	/**
	 * パスワード認証を行う
	 *
	 * @param tenantSeq
	 * @param downloadUrlKey
	 * @param inputForm
	 * @return 検証トークン
	 * @throws GlobalAuthException
	 * @throws AppException
	 */
	public String passwordAuth(Long tenantSeq, String downloadUrlKey, DownloadAuthInputForm.DownloadPasswordInputForm inputForm) throws GlobalAuthException, AppException {

		TAccgDocDownloadEntity tAccgDocDownloadEntity;
		try {
			// データの取得
			tAccgDocDownloadEntity = commonDownloadService.getAccgDocDownloadEntity(downloadUrlKey);

		} catch (GlobalAuthException ex) {
			// 認証キーの有効チェックは行っている想定なので、ここではRuntimeException
			logger.error("キー情報からデータの取得ができませんでした。");
			throw new RuntimeException("キー情報からデータの取得ができませんでした。", ex);
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(inputForm.getPassWord(), tAccgDocDownloadEntity.getDownloadViewPassword())) {
			// パスワードが一致しない場合
			throw new AppException(MessageEnum.MSG_E00001, null);
		}

		// 認証完了の場合、検証トークンを返却する
		return tAccgDocDownloadEntity.getVerificationToken();
	}

}
