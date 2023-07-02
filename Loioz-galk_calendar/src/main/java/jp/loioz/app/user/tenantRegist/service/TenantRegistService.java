package jp.loioz.app.user.tenantRegist.service;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonPlanService;
import jp.loioz.app.user.tenantRegist.form.TenantRegistForm;
import jp.loioz.app.user.tenantRegist.form.TenantSettingForm;
import jp.loioz.app.user.tenantRegist.form.UserForm;
import jp.loioz.common.command.CommandExecutor;
import jp.loioz.common.command.exception.CommandException;
import jp.loioz.common.command.exception.CommandTimeoutException;
import jp.loioz.common.command.exception.UnexpectedExitCodeException;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountStatus;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.HoshuHasuType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaxHasuType;
import jp.loioz.common.constant.MailConstants.MailIdList;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanStatus;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import jp.loioz.common.dataSource.SchemaContextHolder;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.mail.MailService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.config.DatabaseConfig;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.MTenantMgtDao;
import jp.loioz.dao.TPlanHistoryDao;
import jp.loioz.domain.UriService;
import jp.loioz.domain.mail.builder.M0002MailBuilder;
import jp.loioz.domain.mail.builder.M9001MailBuilder;
import jp.loioz.domain.value.PersonName;
import jp.loioz.domain.verification.TempAccountVerificationService;
import jp.loioz.dto.PlanInfo;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.MTenantMgtEntity;
import jp.loioz.entity.TPlanHistoryEntity;

/**
 * アカウント情報詳細入力画面用のサービスクラス
 */
@Service
@Profile({"production", "staging", "demo", "localdocker"})
public class TenantRegistService extends DefaultService {
	/** テナント管理情報 */
	@Autowired
	private MTenantMgtDao mTenantMgtDao;

	/** アカウント情報 */
	@Autowired
	private MAccountDao mAccountDao;

	/** テナント情報 */
	@Autowired
	private MTenantDao mTenantDao;

	/** ロガー */
	@Autowired
	private Logger log;

	/** URIサービスクラス */
	@Autowired
	private UriService uriService;

	/** アカウント認証(管理DB)サービスクラス */
	@Autowired
	private TempAccountVerificationService tempAccountVerificationService;

	/** メール送信サービス */
	@Autowired
	private MailService mailService;

	/** 利用プラン履歴のDaoクラス */
	@Autowired
	private TPlanHistoryDao tPlanHistoryDao;

	/** 契約プランの共通サービス */
	@Autowired
	private CommonPlanService commonPlanService;

	/** データベース設定 */
	@Autowired
	private DatabaseConfig databaseConfig;

	/** スクリプトルートパス */
	@Value("${scripts.root}")
	private String scriptsRoot;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * サブドメイン名が既に登録されているか確認する
	 *
	 * @param subDomain サブドメイン名
	 * @return サブドメイン名が登録されている場合はtrue
	 */
	public boolean domainExists(String subDomain) {
		MTenantMgtEntity entity = mTenantMgtDao.selectBySubDomain(subDomain);

		if (entity != null || PlanConstant.PLAN_SETTING_SUB_DOMAIN.equals(subDomain)) {
			// 既に登録されている、もしくは、プラン設定画面用に利用しているサブドメインと同じ場合
			return true;
		}

		return false;
	}

	/**
	 * テナント管理情報を登録する
	 *
	 * @param form フォームオブジェクト
	 * @return 新規に採番されたテナント連番
	 */
	public Long registTenantMgt(TenantRegistForm form) {
		MTenantMgtEntity entity = new MTenantMgtEntity();

		entity.setSubDomain(form.getDomain().getDomain());
		entity.setTenantName(form.getTenant().getTenantName());
		entity.setTenantType(form.getTenant().getTenantType());
		LocalDateTime now = LocalDateTime.now();

		// 初期登録(無料)プランの契約情報
		entity.setPlanStatus(PlanStatus.FREE.getCd());
		entity.setPlanType(PlanType.STANDARD.getId());
		entity.setLicenseCount(PlanConstant.FREE_PLAN_LICENSE_COUNT);
		entity.setStorageCapacity(PlanConstant.FREE_PLAN_STORAGE_CAPACITY);
		entity.setExpiredAt(DateUtils.get13DaysAfterDateTimeFrom(now));

		entity.setCreatedAt(now);
		entity.setCreatedBy(Long.valueOf(0));
		entity.setUpdatedAt(now);
		entity.setUpdatedBy(Long.valueOf(0));
		entity.setSysCreatedAt(now);
		entity.setSysCreatedBy(Long.valueOf(0));
		entity.setSysUpdatedAt(now);
		entity.setSysUpdatedBy(Long.valueOf(0));

		mTenantMgtDao.insert(entity);

		return entity.getTenantSeq();
	}

	/**
	 * テナント管理情報を削除する
	 *
	 * @param tenantSeq テナント連番
	 */
	public void deleteTenantMgt(Long tenantSeq) {
		MTenantMgtEntity entity = mTenantMgtDao.selectBySeq(tenantSeq);
		mTenantMgtDao.delete(entity);
	}

	/**
	 * テナントDBを作成する
	 *
	 * @param tenantSeq テナント連番
	 * @throws AppException テナントDB作成に失敗した場合
	 */
	public void createTenantDB(Long tenantSeq) throws AppException {

		// テナントDB作成スクリプトのパス
		Path scriptPath = Paths.get(scriptsRoot, "tenant_create.sh").toAbsolutePath().normalize();

		try {
			// コマンド実行
			CommandExecutor.command("sh", scriptPath.toString(), tenantSeq.toString()).stdOutCallback(line -> {
				log.info(line);
			}).execute();

		} catch (CommandTimeoutException e) {
			log.warn("コマンドの実行が {}秒 でタイムアウトしました。({})", e.getTimeoutSeconds(), e.getCommandAsString());
			throw new AppException(null, e);

		} catch (UnexpectedExitCodeException e) {
			log.warn("想定外の終了コード '{}' が返却されました。({})", e.getExitCode(), e.getCommandAsString());
			throw new AppException(null, e);

		} catch (CommandException e) {
			log.warn("コマンドの実行に失敗しました。({})", e.getCommandAsString(), e);
			throw new AppException(null, e);
		}

		// 作成したテナントDBのデータソースを追加
		try {
			databaseConfig.putDataSource(tenantSeq);
		} catch (Exception e) {
			// 新規テナントDBのリソース追加に失敗
			log.error("新規テナントDBのリソース追加に失敗");
			throw new AppException(null, e);
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
	 * アカウント情報詳細入力画面の入力情報を登録する
	 *
	 * @param form フォームオブジェクト
	 * @param tenantSeq テナント連番
	 * @param mailAddress メールアドレス
	 * @throws AppException 登録に失敗した場合
	 */
	@Transactional(rollbackFor = AppException.class)
	public void registFormInfo(TenantRegistForm form, Long tenantSeq, String mailAddress) throws AppException {

		// アカウント情報を登録
		registAccount(form.getUser(), tenantSeq);
		// テナント情報を登録
		registTenant(form.getTenant(), tenantSeq, mailAddress);

		// 初回の無料プランの履歴情報を登録
		registFirstFreeHistory(tenantSeq);

		// ステータスを完了にする
		tempAccountVerificationService.updateToComplete(form.getKey());
	}

	/**
	 * 無料アカウント登録完了メールをユーザーに送信する
	 *
	 * @param mailAddress メールアドレス
	 * @param form フォームオブジェクト
	 */
	public void sendCompleteMail2User(String mailAddress, TenantRegistForm form) {
		TenantSettingForm tenantSettingForm = form.getTenant();
		String loginId = form.getUser().getAccountId();
		String subDomain = form.getDomain().getDomain();

		// ログインURIを取得
		String loginUri = uriService.getUserLoginUrlWithSubDomain(subDomain);

		// 代表者氏名
		PersonName daihyoName = new PersonName(
				tenantSettingForm.getTenantDaihyoNameSei(),
				tenantSettingForm.getTenantDaihyoNameMei(),
				tenantSettingForm.getTenantDaihyoNameSeiKana(),
				tenantSettingForm.getTenantDaihyoNameMeiKana());

		// メール送信
		sendCompleteMail2User(mailAddress, daihyoName.getName(), loginUri, subDomain, tenantSettingForm.getTenantName(), loginId);
	}

	/**
	 * 無料アカウント登録完了メールをシステム管理者に送信する
	 *
	 * @param mailAddress メールアドレス
	 * @param form フォームオブジェクト
	 */
	public void sendCompleteMail2SystemManager(String mailAddress, TenantRegistForm form) {
		TenantSettingForm tenantSettingForm = form.getTenant();
		String subDomain = form.getDomain().getDomain();

		// 代表者氏名
		PersonName daihyoName = new PersonName(
				tenantSettingForm.getTenantDaihyoNameSei(),
				tenantSettingForm.getTenantDaihyoNameMei(),
				tenantSettingForm.getTenantDaihyoNameSeiKana(),
				tenantSettingForm.getTenantDaihyoNameMeiKana());

		// メール送信
		sendCompleteMail2SystemManager(subDomain, tenantSettingForm.getTenantName(), daihyoName.getName(), mailAddress);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * アカウント情報を登録する
	 *
	 * @param form フォームオブジェクト
	 * @param tenantSeq テナント連番
	 * @param mailAddress メールアドレス
	 * @return アカウント連番
	 * @throws AppException 登録に失敗した場合
	 */
	private Long registAccount(UserForm form, Long tenantSeq) throws AppException {
		MAccountEntity entity = new MAccountEntity();

		entity.setAccountId(form.getAccountId());
		entity.setTenantSeq(tenantSeq);
		entity.setPassword(new BCryptPasswordEncoder().encode(form.getPassword().getPassword()));
		entity.setAccountNameSei(form.getAccountNameSei());
		entity.setAccountNameSeiKana(form.getAccountNameSeiKana());
		entity.setAccountNameMei(form.getAccountNameMei());
		entity.setAccountNameMeiKana(form.getAccountNameMeiKana());
		entity.setAccountType(form.getAccountType().getCd());
		entity.setAccountMailAddress(form.getAccountMailAddress());
		// 初期アカウントは1：有効
		entity.setAccountStatus(AccountStatus.ENABLED.getCd());
		// 初期アカウントは2：システム管理者
		entity.setAccountKengen(AccountKengen.SYSTEM_MNG.getCd());
		// 初期アカウントは弁護士の場合のみ経営者
		entity.setAccountOwnerFlg(AccountType.LAWYER == form.getAccountType() ? SystemFlg.FLG_ON.getCd() : SystemFlg.FLG_OFF.getCd());
		// 初期アカウントはloiozブルー
		entity.setAccountColor(CommonConstant.LOIOZ_BULE);

		// アカウント登録
		int insertCount = mAccountDao.insert(entity);
		if (insertCount != 1) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			log.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return entity.getAccountSeq();
	}

	/**
	 * テナント情報を登録する
	 *
	 * @param form フォームオブジェクト
	 * @param tenantSeq テナント連番
	 * @param mailAddress 新規申込みメールアドレス
	 * @throws AppException
	 */
	private void registTenant(TenantSettingForm form, Long tenantSeq, String mailAddress) throws AppException {
		MTenantEntity entity = new MTenantEntity();

		entity.setTenantSeq(tenantSeq);
		entity.setTenantName(form.getTenantName());
		entity.setTenantType(form.getTenantType());
		entity.setTenantZipCd(form.getTenantZipCd());
		entity.setTenantAddress1(form.getTenantAddress1());
		entity.setTenantAddress2(form.getTenantAddress2());
		entity.setTenantTelNo(form.getTenantTelNo());
		entity.setTenantFaxNo(form.getTenantFaxNo());
		entity.setTenantDaihyoNameSei(form.getTenantDaihyoNameSei());
		entity.setTenantDaihyoNameSeiKana(form.getTenantDaihyoNameSeiKana());
		entity.setTenantDaihyoNameMei(form.getTenantDaihyoNameMei());
		entity.setTenantDaihyoNameMeiKana(form.getTenantDaihyoNameMeiKana());

		entity.setTenantDaihyoMailAddress(mailAddress);

		// 初期値は「切り捨て」を自動で設定する。
		entity.setTaxHasuType(TaxHasuType.TRUNCATION.getCd());
		entity.setHoshuHasuType(HoshuHasuType.TRUNCATION.getCd());

		int insertCount = mTenantDao.insert(entity);
		if (insertCount != 1) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			log.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	/**
	 * 初回の無料プランのプラン履歴情報を登録する
	 *
	 * @param tenantSeq
	 * @throws AppException
	 */
	private void registFirstFreeHistory(Long tenantSeq) throws AppException {

		// 管理DBから初回に登録したプラン情報を取得
		PlanInfo planInfo = commonPlanService.getNowPlanInfo(tenantSeq);

		// エンティティを生成
		TPlanHistoryEntity insertEntity = new TPlanHistoryEntity();
		insertEntity.setTenantSeq(tenantSeq);
		insertEntity.setPlanType(planInfo.getPlanType().getId());
		insertEntity.setPlanStatus(planInfo.getPlanStatus().getCd());
		insertEntity.setLicenseCount(planInfo.getLicenseCount());
		insertEntity.setStorageCapacity(planInfo.getStorageCapacity());
		insertEntity.setExpiredAt(planInfo.getExpiredAt());

		// 初回の無料プランでは、自動課金番号は登録されないのでnullを固定で設定
		insertEntity.setAutoChargeNumber(null);

		// 金額を設定
		Long sumCharge = planInfo.getSumCharge();
		insertEntity.setChargeThisMonth(BigDecimal.valueOf(sumCharge));
		insertEntity.setChargeAfterNextMonth(BigDecimal.valueOf(sumCharge));

		int insertCount = tPlanHistoryDao.insert(insertEntity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			log.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	/**
	 * 登録完了メールをユーザーに送信する
	 *
	 * @param mailAddress メールアドレス
	 * @param daihyoName 申し込み者名(代表名)
	 * @param loginUrl ログインURL
	 * @param subDomain 事務所ID(SubDomain)
	 * @param tenantName テナント名
	 * @param loginId ログインID
	 */
	private void sendCompleteMail2User(String mailAddress, String daihyoName, String loginUrl, String subDomain, String tenantName, String loginId) {
		// メール作成
		M0002MailBuilder mailBuilder = new M0002MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_2.getCd(), mailBuilder);
		mailBuilder.makeAddressTo(Arrays.asList(mailAddress));
		mailBuilder.makeBody(daihyoName, loginUrl, subDomain, tenantName, mailAddress, loginId);

		// メール送信
		mailService.sendAsync(mailBuilder);
	}

	/**
	 * 登録完了メールをユーザーに送信する
	 *
	 * @param subDomain 事務所ID
	 * @param tenantName テナント名
	 * @param daihyoName 申し込み者名(代表名)
	 * @param mailAddress メールアドレス
	 */
	private void sendCompleteMail2SystemManager(String subDomain, String tenantName, String daihyoName, String mailAddress) {
		// メール作成
		M9001MailBuilder mailBuilder = new M9001MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_9001.getCd(), mailBuilder);
		mailBuilder.makeBody(subDomain, tenantName, daihyoName, mailAddress);

		// メール送信
		mailService.sendAsync(mailBuilder);
	}
}
