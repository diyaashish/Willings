package jp.loioz.app.user.invitedAccountRegist.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonPlanService;
import jp.loioz.app.user.invitedAccountRegist.form.InvitedAccountRegistForm;
import jp.loioz.app.user.officeAccountSetting.controller.OfficeAccountListController;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.MailConstants.MailIdList;
import jp.loioz.common.dataSource.SchemaContextHolder;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.mail.MailService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.MTenantMgtDao;
import jp.loioz.domain.UriService;
import jp.loioz.domain.mail.builder.M0011MailBuilder;
import jp.loioz.domain.mail.builder.M0012MailBuilder;
import jp.loioz.domain.verification.InvitedAccountVerificationService;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.MTenantMgtEntity;

/**
 * 招待アカウント登録画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InvitedAccountRegistService extends DefaultService {

	@Autowired
	private MTenantMgtDao mTenantMgtDao;

	@Autowired
	private MAccountDao mAccountDao;

	/** 契約プランの共通サービス */
	@Autowired
	private CommonPlanService commonPlanService;

	@Autowired
	private Logger logger;

	/** メール送信サービス */
	@Autowired
	private MailService mailService;
	
	/** URIサービスクラス */
	@Autowired
	private UriService uriService;

	/** テナントDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** 招待アカウント認証サービスクラス */
	@Autowired
	private InvitedAccountVerificationService invitedAccountVerificationService;

	

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
	 * ライセンス数制限チェック（保存処理用）
	 *
	 * @param viewForm
	 * @return
	 */
	public boolean checkLicenseLimitForAccountRegistSave(InvitedAccountRegistForm viewForm, long tenantSeq) {
		boolean error = false;

		// 利用しているライセンス数
		Long usingLicenseCount = commonPlanService.getNowUsingLicenseCount();
		// 利用可能なライセンス数
		Long useableLicenseCount = commonPlanService.getNowUseableLicenseCount(tenantSeq);

		if (usingLicenseCount >= useableLicenseCount) {
			// 利用可能なライセンスを全て利用している場合
			error = true;
		}

		return error;
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
	
	/**
	 * アカウントテーブルへの登録処理（1件）
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public void accountRegist(InvitedAccountRegistForm viewForm, Long tenatSeq) throws AppException {
		// エンティティの作成
		MAccountEntity registEntity = new MAccountEntity();

		// 登録するデータの作成
		registEntity.setAccountId(viewForm.getAccountId());
		registEntity.setTenantSeq(tenatSeq);
		registEntity.setPassword(new BCryptPasswordEncoder().encode(viewForm.getPassword().getPassword()));
		registEntity.setAccountNameSei(viewForm.getAccountNameSei());
		registEntity.setAccountNameSeiKana(viewForm.getAccountNameSeiKana());
		registEntity.setAccountNameMei(viewForm.getAccountNameMei());
		registEntity.setAccountNameMeiKana(viewForm.getAccountNameMeiKana());
		registEntity.setAccountType(viewForm.getAccountType().getCd());

		// ステータスの初期値は1：有効
		registEntity.setAccountStatus(CommonConstant.AccountStatus.ENABLED.getCd());

		registEntity.setAccountMailAddress(viewForm.getAccountMailAddress());

		// アカウント権限は１：一般
		registEntity.setAccountKengen(CommonConstant.AccountKengen.GENERAL.getCd());

		// アカウントオーナフラグは0:オーナー以外
		registEntity.setAccountOwnerFlg(CommonConstant.SystemFlg.FLG_OFF.getCd());

		// カラーはloiozブルー
		registEntity.setAccountColor(CommonConstant.LOIOZ_BULE);

		try {
			// 登録処理
			mAccountDao.insert(registEntity);

			// 更新処理
			invitedAccountVerificationService.updateToComplete(viewForm.getKey());
			
		}catch (AppException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, e);
		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}
	
	/**
	 * 【登録者宛】アカウント登録完了メールを送信する
	 * 
	 * @param viewForm
	 * @param tenantSeq
	 */
	public void sendAccountRegistMail(InvitedAccountRegistForm viewForm, Long tenantSeq) {
		
		// サブドメイン
		String subDomain = uriService.getSubDomainName();
		// 事務所情報
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(tenantSeq);
		
		// 事務所名
		String tenantName = mTenantEntity.getTenantName();
		// メールアドレス
		String mailAddress = viewForm.getAccountMailAddress();
		// アカウントID
		String accountId = viewForm.getAccountId();
		// 名前
		String fullName = viewForm.getAccountNameSei() + " " + viewForm.getAccountNameMei();
		// ログインURL
		String loginUrl = uriService.getUserLoginUrlWithSubDomain(subDomain);

		// メール作成
		M0011MailBuilder mailBuilder = new M0011MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_11.getCd(), mailBuilder);
		mailBuilder.makeAddressTo(Arrays.asList(mailAddress));
		mailBuilder.makeTitle(tenantName);
		mailBuilder.makeBody(mailAddress, tenantName, accountId, fullName, loginUrl);
		
		// メール送信
		mailService.sendAsync(mailBuilder);
	}
	
	/**
	 * 【システム管理者宛】アカウント登録完了メールを送信する
	 * 
	 * @param viewForm
	 * @param tenantSeq
	 */
	public void sendAccountRegistAdminMail(InvitedAccountRegistForm viewForm, Long tenantSeq) {

		// 事務所情報
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(tenantSeq);

		// テナント代表者は送信先に含める
		Set<String> mailAddressToSet = new HashSet<>();
		mailAddressToSet.add(mTenantEntity.getTenantDaihyoMailAddress());
		// アカウント権限：システム管理者のメールアドレスを送信先の設定
		List<MAccountEntity> mAccountEntities = mAccountDao.selectEnabledAccountByAccountKengen(AccountKengen.SYSTEM_MNG.getCd());
		mAccountEntities.stream().map(MAccountEntity::getAccountMailAddress).filter(StringUtils::isNotEmpty).forEach(mailAddressToSet::add);
		
		// アカウント登録者の名前
		String fullName = viewForm.getAccountNameSei() + " " + viewForm.getAccountNameMei();
		// アカウントID
		String accountId = viewForm.getAccountId();
		// メールアドレス
		String mailAddress = viewForm.getAccountMailAddress();
		// アカウント管理画面のURL
		String url = uriService.getUserUrl(OfficeAccountListController.class, controller -> controller.index(null));
		
		// メール作成
		M0012MailBuilder mailBuilder = new M0012MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_12.getCd(), mailBuilder);
		// 送信先の設定 ※メールアドレスが存在しない場合は、仕様上想定外なので空チェックは行わない
		mailBuilder.makeAddressTo(new ArrayList<>(mailAddressToSet));
		mailBuilder.makeTitle(fullName);
		mailBuilder.makeBody(fullName, accountId, mailAddress, url);
		
		// メール送信
		mailService.sendAsync(mailBuilder);
	}
}
