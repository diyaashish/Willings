package jp.loioz.app.user.tenantRegistApply.service;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.tenantRegist.controller.TenantRegistController;
import jp.loioz.common.constant.MailConstants.MailIdList;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.mail.MailService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.TTempAccountDao;
import jp.loioz.domain.mail.builder.M0001MailBuilder;
import jp.loioz.domain.mail.builder.M9007MailBuilder;
import jp.loioz.entity.TTempAccountEntity;

/**
 * 新規アカウント申込み画面のサービスクラス
 */
@Service
public class TenantRegistApplyService extends DefaultService {

	@Autowired
	private TTempAccountDao tTempAccountDao;

	/** メール送信サービス */
	@Autowired
	private MailService mailService;

	/** ロガー */
	@Autowired
	private Logger log;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 一時アカウント情報を登録する
	 *
	 * @param key 認証キー
	 * @param mailAddress メールアドレス
	 * @throws AppException 登録に失敗した場合
	 */
	@Transactional(rollbackFor = AppException.class)
	public void registTempAccount(String key, String mailAddress) throws AppException {
		// 一時アカウント情報
		TTempAccountEntity entity = new TTempAccountEntity();
		entity.setTempAccountKey(key);
		entity.setAccountId(mailAddress);
		entity.setTempLimitDate(LocalDateTime.now().plusDays(1));
		entity.setCompleteFlg(SystemFlg.FLG_OFF.getCd());

		// 一時アカウント情報登録
		int insertCount = tTempAccountDao.insert(entity);
		if (insertCount != 1) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			log.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	/**
	 * 認証用メールを送信する
	 *
	 * @param mailAddress メールアドレス
	 * @param key 認証キー
	 */
	public void sendVerificationMail(String mailAddress, String key) {
		// 認証用URL
		ServletUriComponentsBuilder currentContextPath = ServletUriComponentsBuilder.fromCurrentContextPath();
		String verificationUri = MvcUriComponentsBuilder.relativeTo(currentContextPath.path("/"))
				.withMethodCall(on(TenantRegistController.class).verify(key))
				.toUriString();

		// メール作成
		M0001MailBuilder mailBuilder = new M0001MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_1.getCd(), mailBuilder);
		mailBuilder.makeAddressTo(Arrays.asList(mailAddress));
		mailBuilder.makeBody(mailAddress, verificationUri);

		// メール送信
		mailService.sendAsync(mailBuilder);
	}

	/**
	 * 認証の通知メールをシステム管理者に送信する
	 * 
	 * @param mailAddress
	 */
	public void sendVerificationMailSystemMgt(String mailAddress) {

		// メール作成
		M9007MailBuilder mailBuilder = new M9007MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_9007.getCd(), mailBuilder);
		mailBuilder.makeBody(mailAddress);

		// メール送信
		mailService.sendAsync(mailBuilder);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
