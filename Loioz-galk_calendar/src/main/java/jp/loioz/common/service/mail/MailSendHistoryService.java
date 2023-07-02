package jp.loioz.common.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.MailConstants.MailSendStatus;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.TMailSendHistoryDao;
import jp.loioz.domain.mail.MailSendDetails;
import jp.loioz.entity.TMailSendHistoryEntity;

@Service
public class MailSendHistoryService extends DefaultService {

	/** ロガークラス */
	@Autowired
	Logger logger;

	@Autowired
	TMailSendHistoryDao tMailSendHistoryDao;

	/**
	 * メール送信履歴を登録
	 */
	public TMailSendHistoryEntity registMailSendHistory(MailSendDetails mailSendDetails) {
		// メール送信前情報を設定
		TMailSendHistoryEntity entity = new TMailSendHistoryEntity();
		entity.setMailId(mailSendDetails.getMailId());
		entity.setMailName(mailSendDetails.getMailName());
		entity.setMailType(mailSendDetails.getMailType());
		entity.setSendFromName(mailSendDetails.getSendFromName());
		entity.setSendFrom(CommonUtils.addressListToStr(mailSendDetails.getSendFrom()));
		entity.setSendTo(CommonUtils.addressListToStr(mailSendDetails.getSendToList()));
		entity.setSendCc(CommonUtils.addressListToStr(mailSendDetails.getSendCcList()));
		entity.setSendBcc(CommonUtils.addressListToStr(mailSendDetails.getSendBccList()));
		entity.setMailTitle(mailSendDetails.getMailTitle());
		entity.setMailBody(mailSendDetails.getMailBody());
		entity.setSendStatus(MailSendStatus.SEND_BEFORE.getCd());
		entity.setSendDt(null);
		int cnt = tMailSendHistoryDao.insert(entity);
		if (cnt > 0) {
			// 送信履歴登録成功
			logger.info(CommonUtils.getCurrentExecuteClassAndMethodName() + "メール送信履歴登録 成功");
		} else {
			// 送信履歴登録失敗
			logger.error(CommonUtils.getCurrentExecuteClassAndMethodName() + "メール送信履歴登録 失敗");
		}
		return entity;
	}

	/**
	 * メール送信履歴を更新
	 */
	public void updateMailSendHistory(TMailSendHistoryEntity entity, MailSendDetails mailSendDetails) {
		entity.setSendStatus(mailSendDetails.getSendStatus());
		entity.setSendDt(mailSendDetails.getSendDt());
		int cnt = tMailSendHistoryDao.update(entity);
		if (cnt > 0) {
			// 送信履歴更新成功
			logger.info(CommonUtils.getCurrentExecuteClassAndMethodName() + "メール送信履歴更新 成功");
		} else {
			// 送信履歴更新失敗
			logger.error(CommonUtils.getCurrentExecuteClassAndMethodName() + "メール送信履歴更新 失敗");
		}
	}

}
