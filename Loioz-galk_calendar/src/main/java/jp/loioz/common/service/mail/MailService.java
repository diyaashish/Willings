package jp.loioz.common.service.mail;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.async.AsyncExecutor;
import jp.loioz.common.async.ThreadPoolGroup;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MMailDao;
import jp.loioz.domain.mail.MailSendDetails;
import jp.loioz.domain.mail.builder.AbstractMailBuilder;
import jp.loioz.entity.MMailEntity;
import jp.loioz.entity.TMailSendHistoryEntity;

@Service
public class MailService extends DefaultService {

	/** ロガークラス */
	@Autowired
	Logger logger;

	/** メールテーブル用Daoクラス */
	@Autowired
	MMailDao mMailDao;

	/** メール送信のサービスクラス */
	@Autowired
	MailSendService mailSendService;

	/** メール送信履歴のサービスクラス */
	@Autowired
	MailSendHistoryService mailSendHistoryService;

	/** 非同期処理実行クラス */
	@Autowired
	AsyncExecutor asyncExecutor;

	/** 設定ファイル【開発モード】 */
	@Value("${app.devMode}")
	private String devMode;

	/**
	 * 同期メール送信
	 *
	 * @param builder
	 * @return
	 */
	public boolean send(AbstractMailBuilder builder) {
		return this.sendExe(builder);
	}

	/**
	 * 非同期メール送信
	 *
	 * @param builder
	 */
	public void sendAsync(AbstractMailBuilder builder) {
		asyncExecutor.execute(ThreadPoolGroup.MAIL_SEND, () -> {
			this.sendExe(builder);
		});
	}

	/**
	 * 非同期複数メール送信。<br>
	 * 複数のメールの送信順を保証したい（Listに追加したbuilder順にメール送信する）場合に使用。<br>
	 *
	 * @param builder
	 */
	public void sendAsyncMails(List<AbstractMailBuilder> builderList) {
		asyncExecutor.execute(ThreadPoolGroup.MAIL_SEND, () -> {
			this.sendExe(builderList);
		});
	}

	/**
	 * メール送信
	 *
	 * @param builder
	 * @return
	 */
	private boolean sendExe(AbstractMailBuilder builder) {
		boolean sendFlg = false;
		try {
			// メール送信情報を設定
			MailSendDetails mailSendDetails = new MailSendDetails();
			mailSendDetails.setMailInfo(builder);

			// メール送信履歴テーブル登録
			TMailSendHistoryEntity tMailSendHistoryEntity = this.mailSendHistoryService.registMailSendHistory(mailSendDetails);

			// メール送信サービスでメール送信
			this.mailSendService.sendMail(mailSendDetails);

			// メール送信履歴テーブル更新
			this.mailSendHistoryService.updateMailSendHistory(tMailSendHistoryEntity, mailSendDetails);

			sendFlg = true;

		} catch (MessagingException e) {
			// 送信処理以外の例外
			logger.error(CommonUtils.getCurrentExecuteClassAndMethodName() + " メール送信前の処理に失敗しました", e);
		} finally {

		}
		return sendFlg;
	}

	/**
	 * 複数メール送信<br>
	 * builderListにセットしてあるメール定義どおり順次送信する（送信順はbuilderListにセットしてある順）<br>
	 * 複数のメールの送信順を保証したい（Listに追加したbuilder順にメール送信する）場合に使用。<br>
	 *
	 * @param builderList
	 * @return
	 */
	private boolean sendExe(List<AbstractMailBuilder> builderList) {
		boolean sendFlg = false;
		try {
			for (AbstractMailBuilder builder : builderList) {
				// メール送信情報を設定
				MailSendDetails mailSendDetails = new MailSendDetails();
				mailSendDetails.setMailInfo(builder);

				// メール送信履歴テーブル登録
				TMailSendHistoryEntity tMailSendHistoryEntity = this.mailSendHistoryService.registMailSendHistory(mailSendDetails);

				// メール送信サービスでメール送信
				this.mailSendService.sendMail(mailSendDetails);

				// メール送信履歴テーブル更新
				this.mailSendHistoryService.updateMailSendHistory(tMailSendHistoryEntity, mailSendDetails);
			}

			sendFlg = true;

		} catch (MessagingException e) {
			// 送信処理以外の例外
			logger.error(CommonUtils.getCurrentExecuteClassAndMethodName() + " メール送信前の処理に失敗しました", e);
		} finally {

		}
		return sendFlg;
	}

	/**
	 * メールテンプレート情報を取得
	 *
	 * @param mailId
	 * @return
	 */
	public MMailEntity getMailTemplate(String mailId) {
		return this.mMailDao.selectByMailId(mailId);
	}

	/**
	 * メール情報（テンプレート）を読み込む
	 *
	 * @param mailId
	 * @param builder
	 * @return
	 */
	public boolean loadMailTemplate(String mailId, AbstractMailBuilder builder) {
		try {
			// メール種別に応じたメール送信情報を取得
			MMailEntity mMailEntity = this.getMailTemplate(mailId);
			if (mMailEntity == null) {
				// メールIDが不正エラー
				logger.warn(CommonUtils.getCurrentExecuteClassAndMethodName() + "メールIDが不正");
				return false;
			}

			// メールID、メール名、メール形式、送信元（FROM）、送信元表示名（FROM）、送信先（CC）、送信先（BCC）、件名、本文を取得
			builder.setMailId(mMailEntity.getMailId());
			builder.setMailName(mMailEntity.getMailName());
			builder.setMailType(mMailEntity.getMailType());
			builder.setWorkFrom(mMailEntity.getSendFrom());
			builder.setWorkFromName(mMailEntity.getSendFromName());
			builder.setWorkTo(StringUtils.toArray(mMailEntity.getSendTo()));
			builder.setWorkCc(StringUtils.toArray(mMailEntity.getSendCc()));
			builder.setWorkBcc(StringUtils.toArray(mMailEntity.getSendBcc()));
			if (SystemFlg.FLG_ON.equalsByCode(devMode)) {
				// 開発モード
				builder.setWorkTitle("【開発モード】" + mMailEntity.getMailTitle());

			} else {
				// 本番モード
				builder.setWorkTitle(mMailEntity.getMailTitle());
			}
			builder.setWorkBody(mMailEntity.getMailBody());

			return true;
		} catch (Exception e) {
			// メールIDが不正エラー
			logger.error(CommonUtils.getCurrentExecuteClassAndMethodName() + "メールテンプレート生成エラー");
			return false;
		}
	}

}
