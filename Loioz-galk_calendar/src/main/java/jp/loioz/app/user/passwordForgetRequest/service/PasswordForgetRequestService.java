package jp.loioz.app.user.passwordForgetRequest.service;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.passwordForgetRequest.controller.PasswordForgetRequestController;
import jp.loioz.app.user.passwordForgetRequest.form.PasswordChangeForm;
import jp.loioz.common.constant.MailConstants.MailIdList;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.dataSource.SchemaContextHolder;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.service.mail.MailService;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MTenantMgtDao;
import jp.loioz.dao.TAccountVerificationDao;
import jp.loioz.domain.mail.builder.M0003MailBuilder;
import jp.loioz.domain.verification.AccountVerificationService;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MTenantMgtEntity;
import jp.loioz.entity.TAccountVerificationEntity;

/**
 * パスワード忘れ申請画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PasswordForgetRequestService extends DefaultService {

	@Autowired
	private MTenantMgtDao mTenantMgtDao;

	@Autowired
	private MAccountDao mAccountDao;

	@Autowired
	private TAccountVerificationDao tAccountVerificationDao;

	/** アカウント認証(テナントDB)サービスクラス */
	@Autowired
	private AccountVerificationService accountVerificationService;

	/** メール送信サービス */
	@Autowired
	private MailService mailService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * サブドメイン名からテナント連番を取得する
	 *
	 * @param subDomain サブドメイン名
	 * @return テナント連番
	 */
	public Long getTenantSeq(String subDomain) {
		MTenantMgtEntity mTnMgtEntity = mTenantMgtDao.selectBySubDomain(subDomain);
		if (mTnMgtEntity != null) {
			return mTnMgtEntity.getTenantSeq();
		} else {
			return null;
		}
	}

	/**
	 * DB接続先をテナントDBに切り替える
	 *
	 * @param tenantSeq テナント連番
	 */
	public void useTenantDB(Long tenantSeq) {
		SchemaContextHolder.setTenantSeq(tenantSeq);
	}

	/**
	 * アカウントが登録されているか確認する
	 *
	 * @param accountId アカウントID
	 * @return アカウントが登録されている場合はtrue
	 */
	public boolean accountExists(String accountId) {
		return getAccount(accountId) != null;
	}

	/**
	 * メールアドレスが設定されたアカウントであるか確認する
	 *
	 * @param accountId アカウントID
	 * @return アカウントが登録されていて、メールアドレスが設定されている場合はtrue
	 */
	public boolean hasMailAddressAccount(String accountId) {
		MAccountEntity account = getAccount(accountId);
		return account != null && !StringUtils.isEmpty(account.getAccountMailAddress());
	}

	/**
	 * アカウント認証情報を登録する
	 *
	 * @param accountId アカウントID
	 */
	public void registAccountVerification(String accountId) {

		// アカウント情報を取得
		// アカウント存在チェックは済んでいる前提
		MAccountEntity account = getAccount(accountId);
		String mailAddress = account.getAccountMailAddress();

		// 認証キーを生成
		String key = accountVerificationService.createVerificationKey();

		TAccountVerificationEntity entity = new TAccountVerificationEntity();

		entity.setVerificationKey(key);
		entity.setAccountId(accountId);
		entity.setTempLimitDate(LocalDateTime.now().plusDays(1));
		entity.setCompleteFlg(SystemFlg.FLG_OFF.getCd());

		tAccountVerificationDao.insert(entity);

		// 認証用メールを送信
		sendPasswordForgetRequestMail(mailAddress, key);
	}

	/**
	 * 認証用メールを送信する
	 *
	 * @param mailAddress メールアドレス
	 * @param key 認証キー
	 */
	public void sendPasswordForgetRequestMail(String mailAddress, String key) {

		// メール認証URL
		ServletUriComponentsBuilder currentContextPath = ServletUriComponentsBuilder.fromCurrentContextPath();
		String verificationUri = MvcUriComponentsBuilder
				.relativeTo(currentContextPath.path("/"))
				.withMethodCall(on(PasswordForgetRequestController.class).verify(key))
				.toUriString();

		// メール作成
		M0003MailBuilder mailBuilder = new M0003MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_3.getCd(), mailBuilder);
		mailBuilder.makeAddressTo(Arrays.asList(mailAddress));
		mailBuilder.makeBody(mailAddress, verificationUri);

		// メール送信
		mailService.sendAsync(mailBuilder);
	}

	/**
	 * アカウントのパスワードを変更する
	 *
	 * @param form フォームオブジェクト
	 * @param accountId アカウントID
	 * @throws AppException 変更に失敗した場合
	 */
	public void changePassword(PasswordChangeForm form, String accountId) throws AppException {
		// パスワード変更
		MAccountEntity entity = getAccount(accountId);
		if (entity == null) {
			throw new AppException(null, null);
		}

		entity.setPassword(new BCryptPasswordEncoder().encode(form.getPassword().getPassword()));

		mAccountDao.update(entity);

		// 認証状態を完了にする
		accountVerificationService.updateToComplete(form.getKey());
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * アカウント情報を取得する
	 *
	 * @param accountId アカウントID
	 * @return アカウント情報
	 */
	private MAccountEntity getAccount(String accountId) {
		return mAccountDao.selectEnabledAccountById(accountId);
	}

}
