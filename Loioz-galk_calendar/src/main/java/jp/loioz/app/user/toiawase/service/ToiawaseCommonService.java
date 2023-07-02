package jp.loioz.app.user.toiawase.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.toiawase.controller.ToiawaseDetailController;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.SystemType;
import jp.loioz.common.constant.MailConstants.MailIdList;
import jp.loioz.common.service.mail.MailService;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.TToiawaseDao;
import jp.loioz.dao.TToiawaseDetailDao;
import jp.loioz.domain.UriService;
import jp.loioz.domain.mail.builder.M0009MailBuilder;
import jp.loioz.domain.mail.builder.M9005MailBuilder;
import jp.loioz.domain.mail.builder.M9006MailBuilder;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.TToiawaseDetailEntity;
import jp.loioz.entity.TToiawaseEntity;

/**
 * 共通：問い合わせサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ToiawaseCommonService extends DefaultService {

	/** メール送信サービス */
	@Autowired
	private MailService mailService;

	/** URIサービス */
	@Autowired
	private UriService uriService;

	/** テナントDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** アカウントDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 問い合わせDaoクラス */
	@Autowired
	private TToiawaseDao tToiawaseDao;

	/** 問い合わせ-詳細Daoクラス */
	@Autowired
	private TToiawaseDetailDao tToiawaseDetailDao;

	/**
	 * 問い合わせ登録処理
	 * ※ 登録前にシステム管理者作成日時などを設定する
	 * 
	 * @param entity
	 */
	public void insert(TToiawaseEntity entity) {

		var now = LocalDateTime.now();

		entity.setShokaiCreatedAt(now);
		entity.setLastUpdateAt(now);
		entity.setCreatedAt(now);
		entity.setCreatedBy(SessionUtils.getLoginAccountSeq());
		entity.setUpdatedAt(now);
		entity.setUpdatedBy(SessionUtils.getLoginAccountSeq());

		tToiawaseDao.insert(entity);
	}

	/**
	 * 問い合わせの更新処理
	 * ※ 登録前にシステム管理者更新日時などを設定する
	 * 
	 * @param entity
	 */
	public void update(TToiawaseEntity entity) {

		var now = LocalDateTime.now();

		entity.setLastUpdateAt(now);
		entity.setUpdatedAt(now);
		entity.setUpdatedBy(SessionUtils.getLoginAccountSeq());

		tToiawaseDao.update(entity);
	}

	/**
	 * 問い合わせ-詳細の登録処理
	 * 
	 * @param entity
	 */
	public void insert(TToiawaseDetailEntity entity) {

		var now = LocalDateTime.now();

		entity.setRegistType(SystemType.USER.getCd());
		entity.setTenantReadFlg(SystemFlg.FLG_ON.getCd());
		entity.setCreatedAt(now);
		entity.setCreatedBy(SessionUtils.getLoginAccountSeq());
		entity.setUpdatedAt(now);
		entity.setUpdatedBy(SessionUtils.getLoginAccountSeq());

		tToiawaseDetailDao.insert(entity);
	}

	/**
	 * 問い合わせ-詳細の更新処理
	 * 
	 * @param entity
	 */
	public void update(TToiawaseDetailEntity entity) {

		var now = LocalDateTime.now();

		entity.setUpdatedAt(now);
		entity.setUpdatedBy(SessionUtils.getLoginAccountSeq());

		tToiawaseDetailDao.update(entity);
	}

	/**
	 * 問い合わせ詳細の更新処理(複数)
	 *
	 * @param entities
	 */
	public void update(List<TToiawaseDetailEntity> entities) {

		var now = LocalDateTime.now();

		for (TToiawaseDetailEntity entity : entities) {
			entity.setUpdatedAt(now);
			entity.setUpdatedBy(SessionUtils.getLoginAccountSeq());
		}

		tToiawaseDetailDao.update(entities);
	}

	/**
	 * システム管理者宛に問い合わせ通知を送信する
	 * 
	 * @param toiawaseSeq
	 * @param toiawaseDetailSeq
	 */
	public void sendToiawaseMail2Admin(Long toiawaseSeq, Long toiawaseDetailSeq) {

		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());

		// 問い合わせ情報
		TToiawaseEntity tToiawaseEntity = tToiawaseDao.selectBySeq(toiawaseSeq);
		TToiawaseDetailEntity tToiawaseDetailEntity = tToiawaseDetailDao.selectBySeq(toiawaseDetailSeq);
		
		// メール作成
		M9005MailBuilder mailBuilder = new M9005MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_9005.getCd(), mailBuilder);

		// 通知メールに記載する問い合わせ内容のテキストは1000文字までとする
		String toiawaseBody = tToiawaseDetailEntity.getBody();
		if (StringUtils.isNotEmpty(toiawaseBody)) {
			final int MAX_TOIAWASE_BODY = 1000;
			if (toiawaseBody.length() > MAX_TOIAWASE_BODY) {
				toiawaseBody = toiawaseBody.substring(0, MAX_TOIAWASE_BODY) + "...";
			}
		}
		
		mailBuilder.makeBody(SessionUtils.getSubDomain(), mTenantEntity.getTenantName(), tToiawaseEntity.getSubject(), toiawaseBody);

		// メール送信
		mailService.sendAsync(mailBuilder);
	}
	
	/**
	 * アカウント権限がシステム管理者のユーザー宛に問い合わせ送信完了通知を送信する
	 */
	public void sendToiawaseMail2SysMgt(Long toiawaseSeq) {

		Set<String> mailAddressToSet = new HashSet<>();

		// テナント代表者は送信先に含める
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		mailAddressToSet.add(mTenantEntity.getTenantDaihyoMailAddress());

		// アカウント権限：システム管理者のメールアドレスを送信先の設定
		List<MAccountEntity> mAccountEntities = mAccountDao.selectEnabledAccountByAccountKengen(AccountKengen.SYSTEM_MNG.getCd());
		mAccountEntities.stream().map(MAccountEntity::getAccountMailAddress).filter(StringUtils::isNotEmpty).forEach(mailAddressToSet::add);

		// メール作成
		M0009MailBuilder mailBuilder = new M0009MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_9.getCd(), mailBuilder);

		// 送信先の設定 ※メールアドレスが存在しない場合は、仕様上想定外なので空チェックは行わない
		mailBuilder.makeAddressTo(new ArrayList<>(mailAddressToSet));

		// 本文に差し込むURLを作成
		String url = uriService.getUserUrl(ToiawaseDetailController.class, controller -> controller.detail(toiawaseSeq));
		mailBuilder.makeBody(url);

		// メール送信
		mailService.sendAsync(mailBuilder);

	}
	
	/**
	 * システム管理者に問い合わせ解決済み通知を送信する
	 * 
	 * @param toiawaseSeq
	 */
	public void sendToiawaseCompletedMail2Admin(Long toiawaseSeq) {
		
		// 事務所情報
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		// 問い合わせ情報
		TToiawaseEntity tToiawaseEntity = tToiawaseDao.selectBySeq(toiawaseSeq);
		
		// メール作成
		M9006MailBuilder mailBuilder = new M9006MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_9006.getCd(), mailBuilder);

		mailBuilder.makeBody(SessionUtils.getSubDomain(), mTenantEntity.getTenantName(), tToiawaseEntity.getSubject());

		// メール送信
		mailService.sendAsync(mailBuilder);
	}

}
