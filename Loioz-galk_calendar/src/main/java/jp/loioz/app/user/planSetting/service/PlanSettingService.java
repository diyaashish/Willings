package jp.loioz.app.user.planSetting.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.seasar.doma.DomaNullPointerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.box.sdk.BoxAPIConnection;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FolderMetadata;
import com.google.api.services.drive.Drive;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.api.box.constants.BoxConstants;
import jp.loioz.app.common.api.box.service.CommonBoxApiService;
import jp.loioz.app.common.api.dropbox.constants.DropboxConstants;
import jp.loioz.app.common.api.dropbox.service.CommonDropboxApiService;
import jp.loioz.app.common.api.google.constants.GoogleConstants;
import jp.loioz.app.common.api.google.service.CommonGoogleApiService;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.app.common.service.CommonKaikeiService;
import jp.loioz.app.common.service.CommonLoiozAdminControlService;
import jp.loioz.app.common.service.CommonPlanCalcService;
import jp.loioz.app.common.service.CommonPlanService;
import jp.loioz.app.common.service.CommonTaxService;
import jp.loioz.app.common.service.CommonTenantFuncSettingService;
import jp.loioz.app.user.planSetting.dto.PlanSettingSessionInfoDto;
import jp.loioz.app.user.planSetting.exception.PlanSettingAuthException;
import jp.loioz.app.user.planSetting.form.PlanSettingForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.MailConstants.MailIdList;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.TenantFuncSettingConstant.TenantFuncSetting;
import jp.loioz.common.constant.plan.PlanConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.constant.plan.PlanConstant.PlanStatus;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.mail.MailService;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.MTenantMgtDao;
import jp.loioz.dao.TConnectedExternalServiceDao;
import jp.loioz.dao.TPaymentCardDao;
import jp.loioz.dao.TPlanHistoryDao;
import jp.loioz.dao.TPlanSettingSessionDao;
import jp.loioz.dao.TRootFolderBoxDao;
import jp.loioz.dao.TRootFolderDropboxDao;
import jp.loioz.dao.TRootFolderGoogleDao;
import jp.loioz.domain.UriService;
import jp.loioz.domain.mail.builder.AbstractMailBuilder;
import jp.loioz.domain.mail.builder.M0005MailBuilder;
import jp.loioz.domain.mail.builder.M0006MailBuilder;
import jp.loioz.domain.mail.builder.M0007MailBuilder;
import jp.loioz.domain.mail.builder.M9002MailBuilder;
import jp.loioz.domain.mail.builder.M9003MailBuilder;
import jp.loioz.domain.mail.builder.M9004MailBuilder;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.PaymentCardDto;
import jp.loioz.dto.PlanInfo;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.MTenantMgtEntity;
import jp.loioz.entity.TAuthTokenEntity;
import jp.loioz.entity.TConnectedExternalServiceEntity;
import jp.loioz.entity.TPaymentCardEntity;
import jp.loioz.entity.TPlanHistoryEntity;
import jp.loioz.entity.TPlanSettingSessionEntity;
import jp.loioz.entity.TRootFolderBoxEntity;
import jp.loioz.entity.TRootFolderDropboxEntity;
import jp.loioz.entity.TRootFolderGoogleEntity;
import lombok.Data;

/**
 * プラン設定画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PlanSettingService extends DefaultService {

	/**
	 * 日割り計算用の内部クラス
	 */
	@Data
	public class CalcChargeDto implements Cloneable {
		/** {@link #charge} の適用開始日 */
		LocalDate chargeStartDate;
		/** プランの月額料金 */
		BigDecimal charge;
		
		@Override
		public CalcChargeDto clone() {
			CalcChargeDto dto = null;
			try {
				dto = (CalcChargeDto)super.clone();
			} catch (Exception e) {
			}
			return dto;
		}
	}
	
	/** ロボットペイメントで管理されている店舗ID */
	@Value("${payment.aid}")
	private String paymentAid;

	/** ロボットペイメント側に登録している接続認証コード */
	@Value("${payment.connection.auth.code}")
	private String paymentConnectionAuthCode;

	/** プラン画面用のセッション情報Daoクラス */
	@Autowired
	private TPlanSettingSessionDao tPlanSettingSessionDao;
	
	/** テナント管理用のDaoクラス */
	@Autowired
	private MTenantMgtDao mTenantMgtDao;

	/** 利用プラン履歴のDaoクラス */
	@Autowired
	private TPlanHistoryDao tPlanHistoryDao;

	/** 支払いカードのDaoクラス */
	@Autowired
	private TPaymentCardDao tPaymentCardDao;
	
	/** テナント用のDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** アカウント用のDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** GoogleルートフォルダDaoクラス */
	@Autowired
	private TRootFolderGoogleDao tRootFolderGoogleDao;

	/** BoxルートフォルダDaoクラス */
	@Autowired
	private TRootFolderBoxDao tRootFolderBoxDao;

	/** DropboxルートフォルダDaoクラス */
	@Autowired
	private TRootFolderDropboxDao tRootFolderDropboxDao;

	/** 接続中外部サービスDaoクラス */
	@Autowired
	private TConnectedExternalServiceDao tConnectedExternalServiceDao;
	
	/** 共通OAuthサービスクラス */
	@Autowired
	private CommonOAuthService commonOAuthService;

	/** 共通GoogleApiサービス */
	@Autowired
	private CommonGoogleApiService commonGoogleApiService;

	/** 共通BoxApiサービス */
	@Autowired
	private CommonBoxApiService commonBoxApiService;

	/** 共通DropboxApiサービス */
	@Autowired
	private CommonDropboxApiService commonDropboxApiService;

	/** 旧会計管理の共通サービス */
	@Autowired
	private CommonKaikeiService commonKaikeiService;

	/** 新会計管理の共通サービス */
	@Autowired
	private CommonAccgService commonAccgService;
	
	/** 契約プランの共通サービス */
	@Autowired
	private CommonPlanService commonPlanService;

	/** 契約プランの計算処理共通サービス */
	@Autowired
	private CommonPlanCalcService commonPlanCalcService;
	
	/** 税金に関する処理の共通サービス */
	@Autowired
	private CommonTaxService commonTaxService;

	/** テナント機能設定に関する処理の共通サービス */
	@Autowired
	private CommonTenantFuncSettingService commonTenantFuncSettingService;
	
	/** ロイオズ管理者制御に関する処理の共通サービス */
	@Autowired
	private CommonLoiozAdminControlService commonLoiozAdminControlService;
	
	/** メセージサービス */
	@Autowired
	private MessageService messageService;

	/** メールサービス */
	@Autowired
	private MailService mailService;

	/** URIサービスクラス */
	@Autowired
	private UriService uriService;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 認証キーをもとに、プラン画面に渡されたSession情報を取得。<br>
	 * 管理DBから必要な情報を取得し、プラン画面で利用するSession情報Dtoを作成、取得する。
	 * 
	 * @param authKey
	 * @param cookieSubDomain
	 * @param NOT_PERMIT_ACCESS_PATH_LIST_FOR_ONLY_PLAN_SETTING_ACCESSIBLE_USER
	 * @return
	 */
	public PlanSettingSessionInfoDto getSessionInfoByAuthKey(String authKey, String cookieSubDomain, List<String> NOT_PERMIT_ACCESS_PATH_LIST_FOR_ONLY_PLAN_SETTING_ACCESSIBLE_USER) {
		
		// 
		// authKeyの正当性チェック
		// 
		
		// 認証キーからSession情報取得
		TPlanSettingSessionEntity sessionEntity = tPlanSettingSessionDao.selectByAuthKey(authKey);
		if (sessionEntity == null) {
			// Session情報が取得できないケースは下記のケース
			//  ・１人のユーザーが複数タブや複数ブラウザでプラン画面へアクセスしたケース（二重ログインと同様のケース）
			//  ・不正なauthKeyを利用したアクセスのケース
			throw new PlanSettingAuthException();
		}
		
		// Session情報の有効期限チェック
		LocalDateTime updatedAt = sessionEntity.getUpdatedAt();
		LocalDateTime expiredLine = LocalDateTime.now()
				.minusHours(PlanConstant.HOURS_TO_JUDGE_VALID_SESSION_TABLE_RECORD);
		if (updatedAt.isBefore(expiredLine)) {
			// Session情報の最終更新時間が有効期限切れラインより過去の時間の場合（古い場合）
			throw new PlanSettingAuthException();
		}
		
		MTenantMgtEntity mTenantMgtEntity = mTenantMgtDao.selectBySeq(sessionEntity.getTenantSeq());
		
		// 認証キーの正当性チェック（サブドメイン）
		if (!mTenantMgtEntity.getSubDomain().equals(cookieSubDomain)) {
			// SessionDtoのテナント番号（リクエストで送信されてきた認証キーをキーとして取得したSessionレコードのテナント番号）から取得したテナント情報のサブドメインと、
			// 認証キーと一緒にリクエストで送信されてきたサブドメインが一致しない場合は認証エラーとする
			throw new PlanSettingAuthException();
		}
		
		// 
		// アクセス権限チェック
		// 
		
		boolean isOnlyPlanSettingAccessible = SystemFlg.codeToBoolean(sessionEntity.getOnlyPlanSettingAccessibleFlg());
		
		// プラン画面以外アクセスできない状態の場合、アクセス不可なメソッドへのアクセスではないかどうか
		if (isOnlyPlanSettingAccessible) {
			// 現在のリクエストパス
			URI requestUri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
			String requestPath = requestUri.getPath();
			// アクセス不可なパスへのアクセスかどうかチェック
			boolean isNotPermitPathRequest = NOT_PERMIT_ACCESS_PATH_LIST_FOR_ONLY_PLAN_SETTING_ACCESSIBLE_USER.contains(requestPath);
			if (isNotPermitPathRequest) {
				throw new PlanSettingAuthException();
			}
		}
		
		// 
		// sessionDtoに設定する値の取得
		// 
		
		// テナントのサブドメインを設定したコンテキストパス
		String tenantSubDomain = mTenantMgtEntity.getSubDomain();
		final String TENANT_DOMAIN = uriService.getTenantDomain(mTenantMgtEntity.getSubDomain());
		String uriUntilContextPath = ServletUriComponentsBuilder.fromCurrentContextPath()
				.host(TENANT_DOMAIN)
				.toUriString();
		
		// 無料ステータスかどうか
		boolean isFreePlanStatus = false;
		// 無料プランの利用可能日数
		Long daysCountFreePlanAvailable= null;
		PlanStatus planStatusEnum = DefaultEnum.getEnum(PlanStatus.class, mTenantMgtEntity.getPlanStatus());
		if (planStatusEnum == PlanStatus.FREE) {
			isFreePlanStatus = true;

			LocalDate expiredDate = DateUtils.convertLocalDate(mTenantMgtEntity.getExpiredAt());
			daysCountFreePlanAvailable = DateUtils.getDaysCountFromNow(expiredDate);
		}
		
		// プランタイプ
		PlanType planType = PlanType.of(mTenantMgtEntity.getPlanType());
		
		// 
		// SessionDto生成
		// 
		PlanSettingSessionInfoDto sessionDto = PlanSettingSessionInfoDto
				.builder()
				.tenantSeq(sessionEntity.getTenantSeq())
				.accountSeq(sessionEntity.getAccountSeq())
				.isOnlyPlanSettingAccessible(isOnlyPlanSettingAccessible)
				.subDomain(tenantSubDomain)
				.tenantUrlContextPath(uriUntilContextPath)
				.isFreePlanStatus(isFreePlanStatus)
				.daysCountFreePlanAvailable(daysCountFreePlanAvailable)
				.planType(planType)
				.build();
		
		return sessionDto;
	}
	
	/**
	 * テナントDBにアクセスし、テナントやログインユーザーの情報をSessionDtoに設定する。
	 * 
	 * @param sessionDto
	 */
	public void setTenantInfoToDto(PlanSettingSessionInfoDto sessionDto) {
		
		Long tenantSeq = sessionDto.getTenantSeq();
		Long accountSeq = sessionDto.getAccountSeq();
		
		MTenantEntity tenant = mTenantDao.selectBySeq(tenantSeq);
		MAccountEntity user = mAccountDao.selectBySeq(accountSeq);
		
		// 権限チェック（管理者ユーザーかどうか）
		boolean isManager = AccountKengen.SYSTEM_MNG.equalsByCode(user.getAccountKengen());
		if (!isManager) {
			throw new PlanSettingAuthException();
		}
		
		if (tenant != null) {
			// 事務所
			sessionDto.setTenantName(tenant.getTenantName());
		}
		
		if (user != null) {
			// ユーザーID
			sessionDto.setLoginAccountId(user.getAccountId());
			// ユーザー名
			sessionDto.setLoginAccountName(PersonName.fromEntity(user).getName());
			// メールアドレス
			sessionDto.setLoginAccountMailAddress(user.getAccountMailAddress());
			// アカウントタイプ
			sessionDto.setAccountType(AccountType.of(user.getAccountType()));
		}
		
		// テナント機能設定情報を設定
		Map<TenantFuncSetting, String> tenantFuncSettingMap = commonTenantFuncSettingService.getSessionSettingMap();
		sessionDto.setTenantFuncSettingMap(tenantFuncSettingMap);
		
		// プランタイプ機能制限情報Map
		Map<PlanType, Map<PlanFuncRestrict, Boolean>> allPlanFuncRestrictMap = commonPlanService.getAllPlanFuncRestrictMap();
		sessionDto.setAllPlanFuncRestrictMap(allPlanFuncRestrictMap);
		
		// ロイオズ管理者制御情報を設定
		Map<LoiozAdminControl, String> loiozAdminControlMap = commonLoiozAdminControlService.getSessionSettingMap();
		sessionDto.setLoiozAdminControlMap(loiozAdminControlMap);
	}
	
	// ▼▼▼▼▼ 情報の取得系 ▼▼▼▼▼
	
	/**
	 * プラン料金情報を取得する
	 * 
	 * @param planTypeId
	 * @param licenseCount
	 * @param storageCapacity
	 * @return
	 */
	public PlanInfo getPlanChargeInfo(String planTypeId, Long licenseCount, Long storageCapacity) {

		PlanInfo planInfo = new PlanInfo();

		PlanType planType = PlanType.of(planTypeId);
		planInfo.setPlanType(planType);
		
		planInfo.setOneLicenseCharge(Long.valueOf(planType.getLicensePrice().getPrice()));
		
		Long licenseCharge = commonPlanService.getLicenseCharge(planType, licenseCount);
		Long storageCharge = commonPlanService.getStorageCharge(storageCapacity);

		planInfo.setLicenseCount(licenseCount);
		planInfo.setStorageCapacity(storageCapacity);
		planInfo.setLicenseCharge(licenseCharge);
		planInfo.setStorageCharge(storageCharge);
		planInfo.setSumCharge(licenseCharge + storageCharge);

		// 消費税額
		LocalDate now = LocalDate.now();
		Long taxAmount = commonTaxService.calcTaxAmount(planInfo.getSumCharge(), now);
		planInfo.setTaxAmountOfSumCharge(taxAmount);
		
		return planInfo;
	}
	
	/**
	 * 無料トライアル（無料期間）の期日を取得する
	 * 
	 * @param sessionDto
	 * @return
	 */
	public LocalDateTime getFreeTrialExpiredAt(PlanSettingSessionInfoDto sessionDto) {
		
		return commonPlanService.getFreePlanExpiredAt(sessionDto.getTenantSeq());
	}
	
	/**
	 * 現在の契約情報を取得する
	 * 
	 * @param sessionDto
	 * @return
	 */
	public PlanInfo getNowPlanInfo(PlanSettingSessionInfoDto sessionDto) {

		return commonPlanService.getNowPlanInfo(sessionDto.getTenantSeq());
	}

	/**
	 * 現在の契約ステータスを取得する
	 * 
	 * @param sessionDto
	 * @return
	 */
	public PlanStatus getNowPlanStatus(PlanSettingSessionInfoDto sessionDto) {

		return commonPlanService.getNowPlanStatus(sessionDto.getTenantSeq());
	}

	/**
	 * 現在支払いに使用しているカード情報を取得する
	 * 
	 * @return
	 */
	public PaymentCardDto getNowCardInfo() {

		return commonPlanService.getNowCardInfo();
	}
	
	/**
	 * 現在利用中（有効な状態にしている）のライセンス数を取得する
	 * 
	 * @return
	 */
	public Long getNowUsingLicenseCount() {

		return commonPlanService.getNowUsingLicenseCount();
	}
	
	/**
	 * 現在、事務所が連携している外部ストレージサービスのEnum値を取得する<br>
	 * ※事務所が外部ストレージサービスと連携していない（loiozストレージを利用中の）場合はNULLを返却する
	 * 
	 * @return ExternalServiceのEnum値
	 */
	public ExternalService getConnectStorageService() {
		TConnectedExternalServiceEntity tConnectedExternalServiceEntity = tConnectedExternalServiceDao.selectStorageConnectedService();
		if (tConnectedExternalServiceEntity == null) {
			return null;
		}
		return ExternalService.of(tConnectedExternalServiceEntity.getExternalServiceId());
	}
	
	// ▼▼▼▼▼ 登録／更新系 ▼▼▼▼▼
	
	/**
	 * 無料利用期間中のプラン内容の更新処理。
	 * <pre>
	 *  無料利用期間中にプランの内容（ライセンス数のみ）を変更するための処理で、
	 *  下記の両方の状態を満たす場合のみ実行可能。
	 *  
	 *  ・ステータスが「無料」の場合
	 *  ・有効期限内の場合
	 *  
	 *  ※ストレージ量は無料期間中は変更不可のため、更新は行わない
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @throws AppException
	 */
	public void freePlanUpdate(PlanSettingSessionInfoDto sessionDto, PlanSettingForm viewForm) throws AppException {
		
		// 現在のトランザクション内での値
		PlanInfo nowPlanInfoInTransaction = this.getNowPlanInfo(sessionDto);
		
		// トランザクション内で取得した情報をもとに、再度本当に処理を実行してよい状態かのバリデーションを行う
		this.validExecutableStatusForFreePlanUpdate(nowPlanInfoInTransaction);
		
		// プランタイプが会計機能を利用不可のものに変更となる場合、会計情報を削除する
		boolean isChangeUnavailableKaikei = this.isChangeUnavailableKaikei(sessionDto, PlanType.of(viewForm.getPlanTypeIdForSave()));
		if (isChangeUnavailableKaikei) {
			// 旧会計のデータをすべて削除
			commonKaikeiService.deleteAllKaikeiData();
			// 新会計のデータをすべて削除
			commonAccgService.deleteAllAccgTempData();
		}
		
		// 無料プラン内容の更新
		this.freePlanDBUpdate(sessionDto, viewForm);
	}
	
	/**
	 * 無料期間中のプランの登録処理
	 * <pre>
	 *  現在のトランザクション内にて、本処理が実行可能な状態かのチェックと、登録処理の実行を行う。
	 * </pre>
	 * <pre>
	 *  この処理は、無料期間中にプランを利用していない状態からプラン登録を行う処理で、
	 *  下記の状態の場合のみ実行可能。
	 *  
	 *  ・契約ステータスが「無料」で無料期間中の場合
	 *  
	 *  ※無料期間中では、「無料」以外のステータスは「有効」（既にプランが登録されている状態）しか設定されないため、
	 *   「解約」「変更中」のステータスではこの処理は実行されない。
	 *   （解約を行った場合も、解約ではなく「無料」ステータスに戻すようにし、プラン変更の場合も、解約処理後にこの処理を実行するため、通常の無料期間中のプラン登録と同じ状態での実行となる。）
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @throws AppException
	 */
	public void planRegistForDuringFreeTrial(PlanSettingSessionInfoDto sessionDto, PlanSettingForm viewForm) throws AppException {
		
		// 現在のトランザクション内での値
		PlanInfo nowPlanInfoInTransaction = this.getNowPlanInfo(sessionDto);
		
		LocalDateTime now = LocalDateTime.now();
		
		// トランザクション内で取得した情報をもとに、再度本当に処理を実行してよい状態かのバリデーションを行う
		this.validExecutableStatusForPlanRegistForDuringFreeTrial(sessionDto, nowPlanInfoInTransaction, now);
		
		// プランタイプが会計機能を利用不可のものに変更となる場合、会計情報を削除する
		boolean isChangeUnavailableKaikei = this.isChangeUnavailableKaikei(sessionDto, PlanType.of(viewForm.getPlanTypeIdForSave()));
		if (isChangeUnavailableKaikei) {
			// 旧会計のデータをすべて削除
			commonKaikeiService.deleteAllKaikeiData();
			// 新会計のデータをすべて削除
			commonAccgService.deleteAllAccgTempData();
		}
		
		// プラン登録
		this.planRegist(sessionDto, viewForm);
	}
	
	/**
	 * プランの登録処理（プランの登録処理で実行される純粋なプランの登録処理）。
	 * <pre>
	 *  現在のトランザクション内にて、本処理が実行可能な状態かのチェックと、登録処理の実行を行う。
	 * </pre>
	 * <pre>
	 *  この処理は、無料期間終了後にプランを利用していない状態から、プランを利用し始めるためのプランの登録処理で、
	 *  プランを登録していない下記の状態の場合のみ実行可能。
	 *  
	 *  ・有料登録を行わない状態で無料期間が終了した場合（「無料」ステータスで無料期間切れの状態）
	 *  ・解約状態での有効期限切れの場合（※「解約」ステータスでの有効期限内の場合は本処理ではなく、プランの更新処理を行う。）
	 *  
	 *  ※「変更中」ステータスの場合は、有効期限の内外に関わらず、この処理ではなく、プランの更新処理を行う。
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @throws AppException
	 */
	public void planRegistForStartingUsePlanAfterFreeTrial(PlanSettingSessionInfoDto sessionDto, PlanSettingForm viewForm) throws AppException {
		
		// 現在のトランザクション内での値
		PlanInfo nowPlanInfoInTransaction = this.getNowPlanInfo(sessionDto);
		
		LocalDateTime now = LocalDateTime.now();
		
		// トランザクション内で取得した情報をもとに、再度本当に処理を実行してよい状態かのバリデーションを行う
		this.validExecutableStatusForPlanRegistForStartingUsePlanAfterFreeTrial(sessionDto, nowPlanInfoInTransaction, now);
		
		// プランタイプが会計機能を利用不可のものに変更となる場合、会計情報を削除する
		boolean isChangeUnavailableKaikei = this.isChangeUnavailableKaikei(sessionDto, PlanType.of(viewForm.getPlanTypeIdForSave()));
		if (isChangeUnavailableKaikei) {
			// 旧会計のデータをすべて削除
			commonKaikeiService.deleteAllKaikeiData();
			// 新会計のデータをすべて削除
			commonAccgService.deleteAllAccgTempData();
		}
		
		// 登録済みアカウントの「無効」化
		// ※無料期間中ではないプラン登録の場合（無料期間切れ、解約期間切れの場合）は、ユーザーがアカウント管理画面へ行けないため
		//   プログラム側で各アカウントを「無効」状態に変更するが、
		if (nowPlanInfoInTransaction.getPlanStatus() == PlanStatus.FREE) {
			// 現在が「無料」ステータスの場合（無料期間切れからのプラン登録の場合）
			Long newLicenseCount = Long.valueOf(viewForm.getLicenseNumForSave());
			if (this.isSmallerThanNowUsingLicenseCount(newLicenseCount)) {
				// 入力値のライセンス数の値が、現在の利用アカウント数より小さい場合、
				// 現在の利用アカウントを無効な状態にする（自分自身のアカウントのみ有効な状態とする。）
				this.updateAllAccountLicenseToDisabledOtherThanLoginUser(sessionDto);
			}
		} else {
			// 上記以外のケース（解約後の有効期限切れ状態からのプラン登録の場合のみこのケースとなる）
			// 現在の利用アカウントを無効な状態にする（自分自身のアカウントのみ有効な状態とする。）
			this.updateAllAccountLicenseToDisabledOtherThanLoginUser(sessionDto);
		}
		
		// プラン登録
		this.planRegist(sessionDto, viewForm);
	}
	
	/**
	 * プランの停止と変更中ステータスへの更新（解約ステータスの場合は変更中ステータスへの更新は行わない）。
	 * <pre>
	 *  現在有効となっているプラン（自動課金）の停止処理と、解約ステータス以外のケースでは、変更中ステータスへの更新を行う。
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @param nowPlanStatusUsedBeforeProc 本処理の前で取得／利用したその時点での「現在プランステータス」
	 * @throws AppException
	 */
	public void planStopAndChangingStatus(PlanSettingSessionInfoDto sessionDto, PlanSettingForm viewForm, PlanStatus nowPlanStatusUsedBeforeProc) throws AppException {
		
		// 現在のトランザクション内での値
		PlanInfo nowPlanInfoInTransaction = this.getNowPlanInfo(sessionDto);
		PlanStatus nowPlanStatusInTransaction = nowPlanInfoInTransaction.getPlanStatus();
		
		// 更新処理を行って良い状態かどうかのバリデーション処理
		this.validExecutableStatusForPlanStopAndChangingStatus(sessionDto, nowPlanStatusUsedBeforeProc, nowPlanInfoInTransaction);
		
		// 最新の自動課金番号を取得
		BigInteger autoChargeNumber = null;
		if (nowPlanStatusInTransaction == PlanStatus.CANCELED) {
			// 現在「解約」状態の場合は、最新の履歴情報から取得する
			TPlanHistoryEntity latestHistory = tPlanHistoryDao.selectLatestHistory();
			autoChargeNumber = latestHistory.getAutoChargeNumber();
		} else {
			autoChargeNumber = nowPlanInfoInTransaction.getAutoChargeNumber();
		}
		
		// 「変更中」ステータスへの更新
		if (!(nowPlanStatusInTransaction == PlanStatus.CANCELED || nowPlanStatusInTransaction == PlanStatus.CHANGING)) {
			// ステータスが「解約」、「変更中」ではない場合 -> 「有効」状態からのプラン変更処理の場合
			
			// loioz側のDBの状態を「変更中」の状態に変更する
			this.planStatusUpdateChanging(sessionDto, viewForm, autoChargeNumber);
		}

		// 当月のプランを停止
		// ※現在のステータスの状態がどんな状態であれ、当月の自動課金が継続している状態で、プランの変更を行う場合は
		//   まず現在の自動課金情報を停止し、停止後に変更後のプランの自動課金情報を新規登録するかたちでプランの変更を行う。
		// プラン停止APIを実行（ロボットペイメントの外部API）
		this.execPlanStopApi(autoChargeNumber);
	}
	
	/**
	 * プランの登録処理（プラン変更処理内の停止処理の後に実行される）。
	 * <pre>
	 *  現在のトランザクション内にて、本処理が実行可能な状態かのチェックと、登録処理の実行を行う。
	 *  
	 *  下記の２つの条件によって、処理分岐が行われる。
	 *  （どのステータス状態でも、行う処理自体は「当月から翌月以降も継続する課金情報を登録」処理となる）
	 *  
	 *  【条件】
	 *  １．当月中の初回のプラン変更かどうか（当月中にまだプラン変更を行っていないかどうか）
	 *  ２．本処理を行う前のプランステータス
	 *  
	 *  【条件の組み合わせと実行する処理】
	 *  １=True  ２=変更中      → 当月から翌月以降も継続する課金情報を登録
	 *  １=True  ２=変更中以外  → 当月から翌月以降も継続する課金情報を登録
	 *  １=False ２=変更中      → 当月から翌月以降も継続する課金情報を登録
	 *  １=False ２=解約        → 当月から翌月以降も継続する課金情報を登録
	 *  １=False ２=変更中、解約以外 → 例外
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @param nowPlanStatusUsedBeforeProc 本処理の前で取得／利用したその時点での「現在プランステータス」
	 * @throws AppException
	 */
	public void planRegistAfterStopAndChangingStatus(PlanSettingSessionInfoDto sessionDto, PlanSettingForm viewForm, PlanStatus nowPlanStatusUsedBeforeProc) throws AppException {

		// 現在のトランザクション内での値
		PlanInfo nowPlanInfoInTransaction = this.getNowPlanInfo(sessionDto);
		
		PlanType requestPlanType = PlanType.of(viewForm.getPlanTypeIdForSave());
		Long requestChangeLicenseNum = Long.valueOf(viewForm.getLicenseNumForSave());
		Long requestStorageCapacity = Long.valueOf(viewForm.getStorageCapacityForSave());
		
		// 登録処理を行って良い状態かどうかのバリデーション処理
		this.validExecutableStatusForplanRegistAfterStopAndChangingStatus(sessionDto,
				nowPlanStatusUsedBeforeProc, nowPlanInfoInTransaction,
				requestPlanType, requestChangeLicenseNum, requestStorageCapacity);
		
		// プランタイプが会計機能を利用不可のものに変更となる場合、会計情報を削除する
		boolean isChangeUnavailableKaikei = this.isChangeUnavailableKaikei(sessionDto, requestPlanType);
		if (isChangeUnavailableKaikei) {
			// 旧会計のデータをすべて削除
			commonKaikeiService.deleteAllKaikeiData();
			// 新会計のデータをすべて削除
			commonAccgService.deleteAllAccgTempData();
		}
		
		// 登録処理
		this.planRegist(sessionDto, viewForm);
	}
	
	/**
	 * 無料期間中のプランの削除処理
	 * 
	 * <pre>
	 *  現在のトランザクション内にて、本処理が実行可能な状態かのチェックと、プランの削除処理の実行を行う。
	 * </pre>
	 * 
	 * @param sessionDto
	 * @throws AppException
	 */
	public void planDeleteDuringFreeTrial(PlanSettingSessionInfoDto sessionDto) throws AppException {
		
		// 現在のトランザクション内での値
		// プラン情報
		PlanInfo nowPlanInfoInTransaction = this.getNowPlanInfo(sessionDto);
		// 無料期間中かどうか
		boolean nowIsDuringTheFreePeriodInTransaction = this.nowIsDuringTheFreePeriod(sessionDto);
		
		// 無料期間中のプランの削除を行って良い状態かどうかのバリデーション処理
		this.validExecutableStatusForplanDeleteDuringFreeTrial(nowPlanInfoInTransaction, nowIsDuringTheFreePeriodInTransaction);
		
		// プランの削除処理
		BigInteger autoChargeNumber = nowPlanInfoInTransaction.getAutoChargeNumber();
		this.planDeleteOnlyDuringFreeTrial(sessionDto, autoChargeNumber);
	}
	
	/**
	 * プラン解約処理。
	 * <pre>
	 *  現在のトランザクション内にて、本処理が実行可能な状態かのチェックと、解約処理の実行を行う。
	 *  
	 *  【処理】
	 *  現在有効な当月の自動課金情報を当月末の課金を最後に停止状態とする
	 * </pre>
	 * 
	 * @param sessionDto
	 * @throws AppException
	 */
	public void planCancel(PlanSettingSessionInfoDto sessionDto) throws AppException {
		
		// 現在のトランザクション内での値
		PlanInfo nowPlanInfoInTransaction = this.getNowPlanInfo(sessionDto);
		PlanStatus nowPlanStatusInTransaction = nowPlanInfoInTransaction.getPlanStatus();
		
		// 解約処理を行って良い状態かどうかのバリデーション処理
		this.validExecutableStatusForCancel(nowPlanStatusInTransaction);
		
		// 解約の場合は、プランタイプ、ライセンス数、ストレージ容量は変更しない（現在の登録値を利用する）
		PlanType planType = nowPlanInfoInTransaction.getPlanType();
		Long nowLicenseCount = nowPlanInfoInTransaction.getLicenseCount();
		Long nowStorageCapa = nowPlanInfoInTransaction.getStorageCapacity();

		// 契約ステータス（解約とする）
		PlanStatus newPlanStatus = PlanStatus.CANCELED;
		// 利用期限は当月末とする
		LocalDateTime expiredAt = DateUtils.getEndDateTimeOfThisMonth(LocalDateTime.now());

		// テナントの契約ステータスを更新
		this.updateTenantPlanStatus(sessionDto, newPlanStatus, planType, nowLicenseCount, nowStorageCapa, expiredAt);

		// 自動課金番号を取得する
		BigInteger cancelTargetAutoChargeNumberBi = nowPlanInfoInTransaction.getAutoChargeNumber();
		
		// 利用プラン履歴を登録する
		// 解約処理は無料期間終了後しか行えないため固定でfalseとする（※無料期間中の解約はプランの停止処理となるため解約処理ではない）
		boolean nowIsFreeStatusAndFreeTrial = false;
		this.registPlanHistory(sessionDto, newPlanStatus, planType, nowLicenseCount, nowStorageCapa, expiredAt, cancelTargetAutoChargeNumberBi, nowIsFreeStatusAndFreeTrial);

		// 自動課金番号を更新する（解約のためNULLで更新）
		updateTenantAutoChargeNumber(sessionDto, null);

		// 自動課金の停止更新APIを実行（ロボットペイメントの外部API）
		this.execPlanCancelUpdateApi(cancelTargetAutoChargeNumberBi);
	}
	
	/**
	 * カード情報の変更
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @throws AppException
	 */
	public void cardUpdate(PlanSettingSessionInfoDto sessionDto, PlanSettingForm viewForm) throws AppException {

		String token = viewForm.getToken();

		// カード情報を更新する
		PaymentCardDto cardDto = viewForm.getCardInfo();
		this.savePaymentCard(cardDto);

		// 自動課金番号取得
		BigInteger autoChargeNumberBi = null;
		PlanInfo nowPlanInfo = this.getNowPlanInfo(sessionDto);
		if (nowPlanInfo.getPlanStatus() == PlanStatus.CANCELED) {
			// 現在「解約」状態の場合は、最新の履歴情報から取得する
			TPlanHistoryEntity latestHistory = tPlanHistoryDao.selectLatestHistory();
			autoChargeNumberBi = latestHistory.getAutoChargeNumber();
		} else {
			autoChargeNumberBi = nowPlanInfo.getAutoChargeNumber();
		}
		
		// 自動課金のカード情報の変更APIを実行（ロボットペイメントの外部API）
		this.execCardUpdateApi(sessionDto, token, autoChargeNumberBi);
	}
	
	// ▼▼▼▼▼ 状態判定／バリデーション系 ▼▼▼▼▼
	
	/**
	 * 現在が無料期間中かどうかを判定
	 * 
	 * @param sessionDto
	 * @return
	 */
	public boolean nowIsDuringTheFreePeriod(PlanSettingSessionInfoDto sessionDto) {
		return commonPlanService.nowIsDuringTheFreePeriod(sessionDto.getTenantSeq());
	}
	
	/**
	 * 現在日時が月末日（決済処理が行われる日）であるかどうかを判定する
	 * 
	 * @return true:月末日である false:月末日ではない
	 */
	public boolean nowIsMonthLastDate() {
		LocalDate now = LocalDate.now();
		return this.isMonthLastDate(now);
	}
	/**
	 * 対象日が月末日（決済処理が行われる日）であるかどうかを判定する
	 * 
	 * @param targetDate
	 * @return true:月末日である false:月末日ではない
	 */
	public boolean isMonthLastDate(LocalDate targetDate) {
		return DateUtils.isMonthLastDate(targetDate);
	}
	
	/**
	 * プランを既に登録しているかどうかを、自動課金番号の登録状況によって判定する。
	 * <pre>
	 *  自動課金番号が未登録の状態（MTenantMgtEntityの自動課金番号が未登録の状態）となるのは下記の2つの状態の場合のみで、
	 *  それ以外の状態では、ロボットペイメント側に登録されている最新の自動課金情報の自動課金番号を保持している。
	 *  
	 *  ■自動課金番号が未登録となる状態
	 *  ・無料プランの利用時
	 *  ・解約状態の場合（有効期限内、有効期限外を問わない）
	 * </pre>
	 * 
	 * @param sessionDto
	 * @return
	 */
	public boolean isAlreadRegist(PlanSettingSessionInfoDto sessionDto) {

		MTenantMgtEntity tenantMgtEntity = mTenantMgtDao.selectBySeq(sessionDto.getTenantSeq());
		BigInteger autoChargeNumber = tenantMgtEntity.getAutoChargeNumber();

		return this.isAlreadRegist(autoChargeNumber);
	}
	
	/**
	 * 有効期限切れの状態かを判定する
	 * 
	 * @param sessionDto
	 * @return
	 */
	public boolean isExpiredStatus(PlanSettingSessionInfoDto sessionDto) {
		
		MTenantMgtEntity tenantMgtEntity = mTenantMgtDao.selectBySeq(sessionDto.getTenantSeq());
		PlanStatus nowPlanStatus = DefaultEnum.getEnum(PlanStatus.class, tenantMgtEntity.getPlanStatus());
		
		if (nowPlanStatus == PlanStatus.FREE || nowPlanStatus == PlanStatus.CANCELED || nowPlanStatus == PlanStatus.CHANGING) {
			// 有効期限が設定されるステータスの場合
			
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime expiredAt = tenantMgtEntity.getExpiredAt();
			
			if (now.isAfter(expiredAt)) {
				// 有効期限が切れている場合
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 無料プランの更新が行える状態かどうかを判定
	 * 
	 * @param nowPlanInfo
	 * @return
	 */
	public boolean isFreePlanUpdatableStatus(PlanInfo nowPlanInfo) {
		
		PlanStatus nowPlanStatus = nowPlanInfo.getPlanStatus();
		LocalDateTime now = LocalDateTime.now();
		
		if (nowPlanStatus == PlanStatus.FREE) {
			// 現在のステータスが「無料」の場合は、有効期限内の場合のみ更新処理を可能とする。
			// （「無料」で有効期限が過ぎている場合は、無料プランの更新処理ではなく、プランの登録処理を行うため、更新処理は実施不可とする。）
			
			LocalDateTime expiredAt = nowPlanInfo.getExpiredAt();
			
			if (!now.isAfter(expiredAt)) {
				// 有効期限内の場合
				return true;
			}
			
			return false;
			
		} else {
			// 「無料」ステータスではない場合は、無料プランの更新処理は行えない
			return false;
		}
	}
	
	/**
	 * 無料期間中のプランの更新処理が行えるかどうかを判定
	 * <pre>
	 *  ステータスが「有効」で、かつ、無料期間中の場合のみtrueとなる
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param nowPlanStatus
	 * @param nowIsDuringTheFreePeriod
	 * @return
	 */
	public boolean isUpdatableStatusDuringFreeTrial(PlanSettingSessionInfoDto sessionDto, PlanStatus nowPlanStatus, boolean nowIsDuringTheFreePeriod) {
		
		if (nowPlanStatus != PlanStatus.ENABLED) {
			// 「有効」ステータスではない場合は更新不可
			// （無料期間中は「無料」か「有効」のステータスしかなく、更新は「有効」のステータスでのみ実行可能）
			return false;
		}
		
		if (!nowIsDuringTheFreePeriod) {
			// 無料期間中ではない場合（無料期間が終了している場合）は無料期間中の更新処理は不可
			return false;
		}
		
		boolean isAlreadRegist = this.isAlreadRegist(sessionDto);
		if (!isAlreadRegist) {
			// プランが登録されていない場合は更新処理は不可
			return false;
		}
		
		return true;
	}
	
	/**
	 * プランの登録処理が行える状態かどうかを判定
	 * 
	 * @param sessionDto
	 * @return
	 */
	public boolean isRegistableStatus(PlanSettingSessionInfoDto sessionDto) {
		
		PlanInfo nowPlanInfo = commonPlanService.getNowPlanInfo(sessionDto.getTenantSeq());
		LocalDateTime now = LocalDateTime.now();
		
		return this.isRegistableStatus(sessionDto, nowPlanInfo, now);
	}
	
	/**
	 * プランの更新処理が行える状態かどうかを判定
	 * 
	 * @param sessionDto
	 * @param nowPlanInfo
	 * @return
	 */
	public boolean isUpdatableStatus(PlanSettingSessionInfoDto sessionDto, PlanInfo nowPlanInfo) {
		
		LocalDateTime now = LocalDateTime.now();
		PlanStatus nowPlanStatus = nowPlanInfo.getPlanStatus();
		
		if (nowPlanStatus == PlanStatus.CANCELED) {
			// 現在のステータスが「解約」の場合は、有効期限内の場合のみ更新処理を可能とする。
			// （「解約」で有効期限が過ぎている場合は、更新処理ではなく、プランの登録処理を行うため、更新処理は実施不可とする。）
			
			LocalDateTime expiredAt = nowPlanInfo.getExpiredAt();
			
			if (!now.isAfter(expiredAt)) {
				// 有効期限内の場合
				return true;
			}
			
			return false;
			
		} else {
			// 契約ステータスが「解約」以外の場合はすでにプランが登録されている場合は更新可能とする
			return this.isAlreadRegist(sessionDto);
		}
	}
	
	/**
	 * 無料期間中のプランの削除（即停止）が行える状態かどうかを判定する。
	 * 
	 * @param nowPlanInfo
	 * @param nowIsDuringTheFreePeriod
	 * @return
	 */
	public boolean isExecutableStatusForPlanDeleteDuringFreeTrial(PlanInfo nowPlanInfo, boolean nowIsDuringTheFreePeriod) {
		
		// ステータスが有効かどうか
		PlanStatus nowPlanStatus = nowPlanInfo.getPlanStatus();
		if (nowPlanStatus != PlanStatus.ENABLED) {
			// 「有効」ではない場合は削除不可
			return false;
		}
		
		// プランが登録されているかどうか
		BigInteger autoChargeNumber = nowPlanInfo.getAutoChargeNumber();
		boolean isAlreadRegist = this.isAlreadRegist(autoChargeNumber);
		if (!isAlreadRegist) {
			// 登録されていない場合は削除不可
			return false;
		}
		
		// 無料期間中かどうか
		if (!nowIsDuringTheFreePeriod) {
			// 無料期間中ではない場合は削除不可
			return false;
		}
		
		return true;
	}
	
	/**
	 * 解約処理が行える状態かどうかを判定する。
	 * 
	 * @param sessionDto
	 * @return
	 */
	public boolean isExecutableStatusForCancel(PlanSettingSessionInfoDto sessionDto) {
		
		PlanStatus nowPlanStatus = this.getNowPlanStatus(sessionDto);
		
		return isExecutableStatusForCancel(nowPlanStatus);
	}
	
	/**
	 * プラン登録／変更回数が制限数を超えないかどうかを判定する
	 * 
	 * @return true:超えない（プラン変更可能） false:超える（プラン変更不可）
	 */
	public boolean isNotPlanChangeCountExceedsLimit() {
		
		// プラン変更の回数上限値
		int LIMIT_COUNT = PlanConstant.LIMIT_COUNT_OF_PLAN_CHANGE_FOR_MONTH;
		
		// 今月の変更回数を取得
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime thisMonthFirst = DateUtils.getFirstDateTimeOfThisMonth(now);
		LocalDateTime thisMonthEnd = DateUtils.getEndDateTimeOfThisMonth(now);
		
		// プランの変更回数は、プランが登録された回数（ステータスが「有効」として登録された履歴の回数）でカウントする。
		//   プランの変更回数を考慮する際に重要なのは、「ロボットペイメント側の金額上限の枠のうち、どの程度利用済み状態か」であり、
		//   利用済み状態となる金額は、当月にロボットペイメント側に作成された自動課金情報のレコードの金額の合算値になる。
		//   loioz側でロボットペイメント側の自動課金情報のレコードを作成するAPIは課金の登録処理でのみコールされるため、
		//   変更回数のチェックは、プラン登録の回数をもとにチェックをすればよい。
		List<TPlanHistoryEntity> historys = tPlanHistoryDao.selectStatusAndCreatedAtIsBetweenHistory(PlanStatus.ENABLED.getCd(), thisMonthFirst, thisMonthEnd);
		
		if (CollectionUtils.isEmpty(historys)) {
			return true;
		}
		
		if (historys.size() > LIMIT_COUNT) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * プラン金額が上限値を超えないかどうかを判定する
	 * 
	 * @param planType
	 * @param licenseNum
	 * @param storageCapacity
	 * @return true:超えない（プラン登録可能） false:超える（プラン登録不可）
	 */
	public boolean isNotPlanChargeExceedsLimit(PlanType planType, Long licenseNum, Long storageCapacity) {
		
		// プラン金額の上限値
		long LIMIT_CHARGE = PlanConstant.LIMIT_CHARGE_AMOUNT;
		
		// 金額を算出（1ヶ月間の料金を元に判定。日割りの金額では判定しない。）
		Long planCharge = commonPlanService.getSumCharge(planType, licenseNum, storageCapacity);
		
		// 消費税額
		LocalDate now = LocalDate.now();
		Long taxAmount = commonTaxService.calcTaxAmount(planCharge, now);
		
		// 税込み額
		Long planChargeIncludedTax = planCharge + taxAmount;
		
		if (planChargeIncludedTax > LIMIT_CHARGE) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 保存可能なライセンス数かを判定する
	 * 
	 * @param saveLicenseNum
	 * @return
	 */
	public boolean isSaveableLicenseCount(Long saveLicenseNum) {
		if (saveLicenseNum == null) {
			return false;
		}
		
		// 現在利用中のライセンス数
		Long nowUsingLicenseCount = commonPlanService.getNowUsingLicenseCount();

		if (nowUsingLicenseCount == null) {
			return true;
		}
		
		int compareResult = saveLicenseNum.compareTo(nowUsingLicenseCount);
		if (compareResult == -1) {
			// 保存しようとしているライセンス数が、利用中のライセンス数より小さい場合
			return false;
		}

		return true;
	}
	
	/**
	 * 保存可能なストレージ量かどうか
	 * 
	 * @param saveStorageCapacity
	 * @return
	 */
	public boolean isSaveableStorageCapacity(Long saveStorageCapacity) {
		if (saveStorageCapacity == null) {
			return false;
		}
		
		double saveStorageCapacityDouble = saveStorageCapacity.doubleValue();
		
		// 現在利用中のストレージ量
		double nowUsingStorageCapacityDouble = commonPlanService.getNowUsingStorageCapacity();
		
		// 登録しようとしているストレージ量が、既に利用しているストレージ量より小さい場合は登録不可な値とする。
		if (saveStorageCapacityDouble < nowUsingStorageCapacityDouble) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 対象の値が現在のライセンス数／ストレージ容量と同じ値かを判定する
	 * 
	 * @param sessionDto
	 * @param planType プランタイプ
	 * @param licenseNum ライセンス数
	 * @param storageCapacity ストレージ容量
	 * @return
	 */
	public boolean isSameLicenseAndStorageAsCurrentRegist(PlanSettingSessionInfoDto sessionDto, PlanType planType, Long licenseNum, Long storageCapacity) {

		PlanInfo planInfo = commonPlanService.getNowPlanInfo(sessionDto.getTenantSeq());

		PlanType currentPlanType = planInfo.getPlanType();
		Long currentLicenseCount = planInfo.getLicenseCount();
		Long currentStorageCapacity = planInfo.getStorageCapacity();

		boolean isSametPlanType = currentPlanType == planType;
		boolean isSameLicenseCount = currentLicenseCount.equals(licenseNum);
		boolean isSameStorageCapacity = currentStorageCapacity.equals(storageCapacity);

		if (isSametPlanType && isSameLicenseCount && isSameStorageCapacity) {
			return true;
		}

		return false;
	}

	/**
	 * 新しいプランタイプに変更することで、会計機能が利用不可になるかを判定する<br>
	 * 
	 * @param sessionDto
	 * @param newPlanType
	 * @return
	 */
	public boolean isChangeUnavailableKaikei(PlanSettingSessionInfoDto sessionDto, PlanType newPlanType) {
		
		PlanType nowPlanType = sessionDto.getPlanType();
		
		// 現在の登録プランが「スタンダート」で、いまから登録しようとしているのが「スターター」の場合、true
		boolean isPlanDown = (nowPlanType == PlanType.STANDARD && newPlanType == PlanType.STARTER);
		
		// 「スタンダード」、「スターター」それぞれの会計機能の権限情報を取得（取得値は反転させる）
		Map<PlanType, Map<PlanFuncRestrict, Boolean>> allPlanFuncRestrictMap =  sessionDto.getAllPlanFuncRestrictMap();
		Boolean  standardCanUsePlanFuncKaikei = !allPlanFuncRestrictMap.get(PlanType.STANDARD).get(PlanFuncRestrict.PF0002);
		Boolean  starterCanUsePlanFuncKaikei = !allPlanFuncRestrictMap.get(PlanType.STARTER).get(PlanFuncRestrict.PF0002);
		
		// 「スタンダード」→「スターター」の変更で、会計機能が利用不可になる場合、true
		boolean becomeUnavailableKaikei = (standardCanUsePlanFuncKaikei && !starterCanUsePlanFuncKaikei);
		
		if (isPlanDown && becomeUnavailableKaikei) {
			// 会計機能が利用不可になる場合
			return true;
		}
		
		return false;
	}
	
	/**
	 * 旧会計管理のデータが存在するかチェックする
	 * 
	 * @return true:旧会計管理のデータが
	 */
	public boolean isExistsOldKaikeiData() {
		
		return commonKaikeiService.isExistsOldKaikeiDataOnTenant();
	}
	
	// ▼▼▼▼▼ メール送信 ▼▼▼▼▼

	/**
	 * プラン登録処理の通知メールをユーザーに送信する
	 * 
	 * @param sessionDto
	 */
	public void sendRegistPlanMail2User(PlanSettingSessionInfoDto sessionDto) {

		M0005MailBuilder mailBuilder = new M0005MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_5.getCd(), mailBuilder);

		// ログインユーザー（プランを登録したユーザー）にメールを送信
		mailBuilder.makeAddressTo(Arrays.asList(sessionDto.getLoginAccountMailAddress()));

		// メール本文の内容を設定
		this.setUserMailBody(sessionDto, mailBuilder);

		// メール送信
		mailService.sendAsync(mailBuilder);
	}

	/**
	 * プラン登録処理の通知メールをシステム管理者に送信する
	 * 
	 * @param sessionDto
	 */
	public void sendRegistPlanMail2SystemManager(PlanSettingSessionInfoDto sessionDto) {

		M9002MailBuilder mailBuilder = new M9002MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_9002.getCd(), mailBuilder);

		// メール本文の内容を設定
		this.setSystemManageMailBody(sessionDto, mailBuilder);

		// メール送信
		mailService.sendAsync(mailBuilder);
	}

	/**
	 * プラン変更処理の通知メールをユーザーに送信する
	 * 
	 * @param sessionDto
	 */
	public void sendUpdatePlanMail2User(PlanSettingSessionInfoDto sessionDto) {

		M0006MailBuilder mailBuilder = new M0006MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_6.getCd(), mailBuilder);

		// ログインユーザー（プランを変更したユーザー）にメールを送信
		mailBuilder.makeAddressTo(Arrays.asList(sessionDto.getLoginAccountMailAddress()));

		// メール本文の内容を設定
		this.setUserMailBody(sessionDto, mailBuilder);

		// メール送信
		mailService.sendAsync(mailBuilder);
	}

	/**
	 * プラン変更処理の通知メールをシステム管理者に送信する
	 * 
	 * @param sessionDto
	 */
	public void sendUpdatePlanMail2SystemManager(PlanSettingSessionInfoDto sessionDto) {

		M9003MailBuilder mailBuilder = new M9003MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_9003.getCd(), mailBuilder);

		// メール本文の内容を設定
		this.setSystemManageMailBody(sessionDto, mailBuilder);

		// メール送信
		mailService.sendAsync(mailBuilder);
	}

	/**
	 * プラン解約通知メールをユーザーに送信する
	 * 
	 * @param sessionDto
	 */
	public void sendCancelPlanMail2User(PlanSettingSessionInfoDto sessionDto) {

		M0007MailBuilder mailBuilder = new M0007MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_7.getCd(), mailBuilder);

		// ログインユーザー（プランを変更したユーザー）にメールを送信
		mailBuilder.makeAddressTo(Arrays.asList(sessionDto.getLoginAccountMailAddress()));

		// メール本文の内容を設定
		this.setM0007MailBody(sessionDto, mailBuilder);

		// メール送信
		mailService.sendAsync(mailBuilder);
	}

	/**
	 * プラン解約通知メールをシステム管理者に送信する
	 * 
	 * @param sessionDto
	 */
	public void sendCancelPlanMail2SystemManager(PlanSettingSessionInfoDto sessionDto) {

		M9004MailBuilder mailBuilder = new M9004MailBuilder();
		mailService.loadMailTemplate(MailIdList.MAIL_9004.getCd(), mailBuilder);

		// メール本文の内容を設定
		this.setM9004MailBody(sessionDto, mailBuilder);

		// メール送信
		mailService.sendAsync(mailBuilder);
	}

	/**
	 * 引数のビルダーにユーザー宛メール本文情報を設定する
	 * 
	 * @param sessionDto
	 * @param mailBuilder
	 * @return
	 */
	private AbstractMailBuilder setUserMailBody(PlanSettingSessionInfoDto sessionDto, AbstractMailBuilder mailBuilder) {

		// 登録されているプラン情報
		PlanInfo planInfo = this.getNowPlanInfo(sessionDto);

		// ご利用プラン（プランタイプ）
		String planTypeTitle = planInfo.getPlanType().getTitle();
		
		// 数量
		String licenseCount = String.valueOf(planInfo.getLicenseCount());
		String storageCapacity = String.valueOf(planInfo.getStorageCapacity());

		// 事務所情報
		MTenantEntity tenant = mTenantDao.selectBySeq(sessionDto.getTenantSeq());

		// 事務所、本人情報
		String officeId = sessionDto.getSubDomain();
		String officeName = tenant.getTenantName();
		String userName = sessionDto.getLoginAccountName();
		String registMailAddress = sessionDto.getLoginAccountMailAddress();

		// 金額
		String licenseCharge = String.valueOf(planInfo.getLicenseCharge());
		String storageCharge = String.valueOf(planInfo.getStorageCharge());
		Long sumChargeIncludedTaxLong = planInfo.getSumCharge() + planInfo.getTaxAmountOfSumCharge();
		String sumChargeIncludedTax = String.valueOf(sumChargeIncludedTaxLong);

		// 1ライセンスあたりの金額
		String licenseChargePerUnit = String.valueOf(planInfo.getOneLicenseCharge());

		// 上記で取得した値を本文に設定
		mailBuilder.makeBody(userName, planTypeTitle, licenseCount, storageCapacity,
				officeId, officeName, userName, registMailAddress,
				licenseCharge, storageCharge, sumChargeIncludedTax, licenseChargePerUnit);

		return mailBuilder;
	}

	/**
	 * 引数のビルダーにシステム管理宛メール本文情報を設定する
	 * 
	 * @param sessionDto
	 * @param mailBuilder
	 * @return
	 */
	private AbstractMailBuilder setSystemManageMailBody(PlanSettingSessionInfoDto sessionDto, AbstractMailBuilder mailBuilder) {

		// 登録されているプラン情報
		PlanInfo planInfo = this.getNowPlanInfo(sessionDto);

		// ご利用プラン（プランタイプ）
		String planTypeTitle = planInfo.getPlanType().getTitle();
		
		// 数量
		String licenseCount = String.valueOf(planInfo.getLicenseCount());
		String storageCapacity = String.valueOf(planInfo.getStorageCapacity());

		// 事務所情報
		MTenantEntity tenant = mTenantDao.selectBySeq(sessionDto.getTenantSeq());

		// 事務所、本人情報
		String officeId = sessionDto.getSubDomain();
		String officeName = tenant.getTenantName();
		String userName = sessionDto.getLoginAccountName();
		String registMailAddress = sessionDto.getLoginAccountMailAddress();

		// 上記で取得した値を本文に設定
		mailBuilder.makeBody(planTypeTitle, licenseCount, storageCapacity,
				officeId, officeName, userName, registMailAddress);

		return mailBuilder;
	}

	/**
	 * 引数のビルダーにユーザー宛解約メール本文情報を設定する
	 * 
	 * @param sessionDto
	 * @param mailBuilder
	 * @return
	 */
	private M0007MailBuilder setM0007MailBody(PlanSettingSessionInfoDto sessionDto, M0007MailBuilder mailBuilder) {

		// 登録されているプラン情報
		PlanInfo planInfo = this.getNowPlanInfo(sessionDto);

		// ご利用プラン（プランタイプ）
		String planTypeTitle = planInfo.getPlanType().getTitle();
		
		// 数量
		String licenseCount = String.valueOf(planInfo.getLicenseCount());
		String storageCapacity = String.valueOf(planInfo.getStorageCapacity());

		// 事務所情報
		MTenantEntity tenant = mTenantDao.selectBySeq(sessionDto.getTenantSeq());

		// 事務所、本人情報
		String officeId = sessionDto.getSubDomain();
		String officeName = tenant.getTenantName();
		String userName = sessionDto.getLoginAccountName();
		String registMailAddress = sessionDto.getLoginAccountMailAddress();

		// ログインURIを取得
		String loginUri = uriService.getUserLoginUrlWithSubDomain(officeId);

		// 上記で取得した値を本文に設定
		mailBuilder.makeBody(userName, planTypeTitle, licenseCount, storageCapacity, loginUri,
				officeId, officeName, userName, registMailAddress);

		return mailBuilder;
	}

	/**
	 * 引数のビルダーにシステム管理宛解約メール本文情報を設定する
	 * 
	 * @param sessionDto
	 * @param mailBuilder
	 * @return
	 */
	private M9004MailBuilder setM9004MailBody(PlanSettingSessionInfoDto sessionDto, M9004MailBuilder mailBuilder) {

		// 登録されているプラン情報
		PlanInfo planInfo = this.getNowPlanInfo(sessionDto);

		// ご利用プラン（プランタイプ）
		String planTypeTitle = planInfo.getPlanType().getTitle();
		
		// 数量
		String licenseCount = String.valueOf(planInfo.getLicenseCount());
		String storageCapacity = String.valueOf(planInfo.getStorageCapacity());

		// 事務所情報
		MTenantEntity tenant = mTenantDao.selectBySeq(sessionDto.getTenantSeq());

		// 事務所、本人情報
		String officeId = sessionDto.getSubDomain();
		String officeName = tenant.getTenantName();
		String userName = sessionDto.getLoginAccountName();
		String registMailAddress = sessionDto.getLoginAccountMailAddress();

		// 上記で取得した値を本文に設定
		mailBuilder.makeBody(planTypeTitle, licenseCount, storageCapacity,
				officeId, officeName, userName, registMailAddress);

		return mailBuilder;
	}

	/**
	 * Googleルートフォルダの存在チェックを行う
	 * 
	 * @param accountSeq
	 * @return
	 * @throws AppException
	 */
	public boolean isDeletedGoogleRootFolder(Long accountSeq) throws AppException {

		TRootFolderGoogleEntity tRootFolderGoogleEntity = tRootFolderGoogleDao.selectEnabledRoot();
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getAuthToken(ExternalService.GOOGLE, accountSeq);
		Drive googleDriveService = commonGoogleApiService.getDriveService(tAuthTokenEntity.getAccessToken());
		boolean isDeleted = commonGoogleApiService.isFolderDeleted(googleDriveService, tRootFolderGoogleEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Googleアプリのルートフォルダ(loioz)へURLを取得する
	 * 
	 * @return
	 */
	public String getGoogleRootFolderUrl() {
		TRootFolderGoogleEntity tRootFolderGoogleEntity = tRootFolderGoogleDao.selectEnabledRoot();
		return String.format(GoogleConstants.GOOGLE_DRIVE_FOLDER_URL_BASE, tRootFolderGoogleEntity.getFolderId());
	}

	/**
	 * Boxルートフォルダの存在チェックを行う
	 * 
	 * @param accountSeq
	 * @return
	 * @throws AppException
	 */
	public boolean isDeletedBoxRootFolder(Long accountSeq) throws AppException {

		TRootFolderBoxEntity tRootFolderBoxEntity = tRootFolderBoxDao.selectEnabledRoot();
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getAuthToken(ExternalService.BOX, accountSeq);
		BoxAPIConnection api = commonBoxApiService.getBoxAPIConnection(tAuthTokenEntity.getAccessToken(), tAuthTokenEntity.getRefreshToken());
		boolean isDeleted = commonBoxApiService.isDeleted(api, tRootFolderBoxEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Boxアプリのルートフォルダ(loioz)へURLを取得する
	 * 
	 * @return
	 */
	public String getBoxRootFolderUrl() {
		TRootFolderBoxEntity tRootFolderBoxEntity = tRootFolderBoxDao.selectEnabledRoot();
		String url = BoxConstants.BOX_APP_BASE_URL;
		url += tRootFolderBoxEntity.getFolderId();
		return url;
	}

	/**
	 * Dropboxアプリのルートフォルダが存在するかどうか判断する
	 *
	 * @return
	 * @throws AppException
	 */
	public boolean isNotFoundDropboxRootFolder(Long accountSeq) throws AppException {

		TRootFolderDropboxEntity tRootFolderDropboxEntity = tRootFolderDropboxDao.selectEnabledRoot();
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getAuthToken(ExternalService.DROPBOX, accountSeq);
		DbxClientV2 cliantApi = commonDropboxApiService.getDbxClientV2(tAuthTokenEntity.getAccessToken());
		boolean isDeleted = commonDropboxApiService.isNotFoundFolder(cliantApi, tRootFolderDropboxEntity.getFolderId());
		return isDeleted;
	}

	/**
	 * Dropboxアプリのルートフォルダ(loioz)へURLを取得する
	 * 
	 * @return
	 */
	public String getDropboxRootFolderUrl(Long accountSeq) {

		TRootFolderDropboxEntity tRootFolderDropboxEntity = tRootFolderDropboxDao.selectEnabledRoot();
		TAuthTokenEntity tAuthTokenEntity = commonOAuthService.getAuthToken(ExternalService.DROPBOX, accountSeq);
		DbxClientV2 cliantApi = commonDropboxApiService.getDbxClientV2(tAuthTokenEntity.getAccessToken());

		FolderMetadata folderMetadata = null;
		try {
			// フォルダ情報の取得
			folderMetadata = commonDropboxApiService.getFolderMetaData(cliantApi, tRootFolderDropboxEntity.getFolderId());
		} catch (Exception e) {
			// 呼び出し元コントローラメソッドで削除チェックを行うことを想定しているため、ここでエラーが発生することは想定していない。
			throw new RuntimeException(e);
		}

		String url = DropboxConstants.DROPBOX_APP_BASE;
		url += folderMetadata.getPathLower();

		return url;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	// ▼▼▼▼▼ 情報の取得系 ▼▼▼▼▼

	/**
	 * プラン登録の際に使用する課金開始日の値を取得する
	 * <pre>
	 *  初回のプラン登録、プラン更新、いずれの場合も、<br>
	 *  自動課金情報を登録する場合は課金開始日は当月末となる。
	 *  
	 *  【プラン更新の場合】
	 *  プラン更新の場合、プランの登録処理を行う前に、変更前のプランは即停止を行い、
	 *  停止後に更新後の状態のプランを新規登録するため、課金開始日は当月末とする。
	 * </pre>
	 * 
	 * @param now 現在時間
	 * @param nowIsFreeStatusAndFreeTrial 現在が無料ステータスでの無料トライアル中かどうか
	 * @param nowStatusExpiredAt 現在のプランの有効期限
	 * @return 登録する自動課金情報の課金開始日
	 */
	private LocalDate getChargeStartDateForPlanRegist(LocalDate now, boolean nowIsFreeStatusAndFreeTrial, LocalDateTime nowStatusExpiredAt) {
		
		if (nowIsFreeStatusAndFreeTrial) {
			// 無料期間中の場合（無料期間中のプラン登録の場合）
			
			LocalDate freeTrialExpiredDate = DateUtils.convertLocalDate(nowStatusExpiredAt);
			boolean isMonthLastDate = this.isMonthLastDate(freeTrialExpiredDate);
			if (isMonthLastDate) {
				// 無料期間終了日の日付が月末日
				
				// 課金開始日は無料期間終了日の翌月末とする
				return DateUtils.getLastDateOfNextMonth(freeTrialExpiredDate);
			} else {
				// 無料期間終了日の日付が月末日以外
				
				// 課金開始日は無料期間終了日の当月とする
				return DateUtils.getLastDateOfThisMonth(freeTrialExpiredDate);
			}
		}
		
		// 無料期間中ではない場合（無料期間終了後のプラン登録の場合）
		
		boolean nowIsMonthLastDate = this.isMonthLastDate(now);
		if (nowIsMonthLastDate) {
			// 月末日
			
			// 月末日にプラン登録を行えるのは「無料」、「解約」状態で利用期限が過ぎている場合（課金が完全に行われていない状態の場合）。
			// （他のケースの場合（プラン登録ではなく、プラン更新処理を行うケース）では月末日での処理の実行はバリデーションで実施不可となっている）
			// 上記の場合の月末日でのプラン登録では、課金開始日は翌月末とする。
			
			return DateUtils.getLastDateOfNextMonth(now);
		} else {
			// 月末日以外
			
			// ステータスの状態に関わらず、プラン登録の場合の課金開始日は当月末となる。
			// -> 「解約」や「変更中」のステータスの場合も、プランの登録処理の前に、当月の課金を行う自動課金は停止されており、
			//    自動課金情報は存在しない状態となているため、課金開始日は必ず当月末となる。
			
			return DateUtils.getLastDateOfThisMonth(now);
		}
	}
	
	/**
	 * 初回の課金金額を取得する（プラン登録処理用）
	 * 
	 * @param sessionDto
	 * @param now
	 * @param nowIsFreeStatusAndFreeTrial
	 * @param nowStatusExpiredAt
	 * @param newPlanStatus
	 * @param newPlanType
	 * @param newLicenseCount
	 * @param newStorageCapacity
	 * @param chargeStartDate
	 * @param afterNextCharge
	 * @return
	 */
	private Long getFirstChargeAmountForPlanRegist(PlanSettingSessionInfoDto sessionDto, LocalDate now,
			boolean nowIsFreeStatusAndFreeTrial, LocalDateTime nowStatusExpiredAt,
			PlanStatus newPlanStatus, PlanType newPlanType, Long newLicenseCount, Long newStorageCapacity,
			LocalDate chargeStartDate, Long afterNextCharge) {
		
		// 初回の課金金額
		Long firstCharge = null;
		
		if (nowIsFreeStatusAndFreeTrial) {
			// 無料期間中の場合（無料期間中のプラン登録の場合）
			
			LocalDate freeTrialExpiredDate = DateUtils.convertLocalDate(nowStatusExpiredAt);
			LocalDate nextDateOfFreeTrialExpired = DateUtils.getNextDate(freeTrialExpiredDate);
			
			if (!DateUtils.isEqualYearMonth(nextDateOfFreeTrialExpired, chargeStartDate)) {
				// 「無料期間の終了日の翌日」と「課金開始日」が同じ年月ではない場合はエラーとする
				logger.error("「無料期間の終了日の翌日」と「課金開始日」が同じ年月になっていない");
				throw new IllegalArgumentException();
			}
			
			// 「無料期間の終了日の翌日」から月末までの日割り
			boolean isIncludeStartDate = true;
			firstCharge = commonPlanService.getChargeOfUntilEndOfMonth(nextDateOfFreeTrialExpired, afterNextCharge, isIncludeStartDate);
			
			return firstCharge;
		}
		
		// 無料期間中ではない場合（無料期間終了後のプラン登録の場合）
		
		if (DateUtils.isEqualYearMonth(now, chargeStartDate)) {
			// 課金開始日が当月の場合
			
			boolean nowIsMonthLastDate = DateUtils.isMonthLastDate(now);
			if (nowIsMonthLastDate) {
				// 現在日（日割り計算の開始日）が月末日の場合、当月の課金は既に終了しているため、不要な課金とならないよう、
				// 自動課金登録APIに渡す「初回の課金金額」（当月の課金金額）は0円とする。
				firstCharge = 0L;
			} else {
				// 初回の課金金額を計算（日割り金額）
				firstCharge = this.calcTargetDateMonthCharge(sessionDto, now, newPlanStatus, newPlanType, newLicenseCount, newStorageCapacity);
			}
			
		} else {
			// 課金開始日が当月ではない場合＝翌月の場合
			
			// 課金開始日が翌月の場合、翌月の課金金額は指定プランの1ヶ月間の金額（日割り計算が発生しない2回目以降の金額と同じ金額）となる
			firstCharge = afterNextCharge;
		}
		
		return firstCharge;
	}
	
	// ▼▼▼▼▼ 登録／更新系 ▼▼▼▼▼
	
	/**
	 * プラン画面で利用するSession情報テーブルのOnlyPlanSettingAccessibleFlgの値を更新する。
	 * 
	 * @param sessionDto
	 * @param isOnlyPlanSettingAccessible
	 * @throws AppException
	 */
	private void updateSessionTableOnlyPlanSettingAccessibleFlg(PlanSettingSessionInfoDto sessionDto, boolean isOnlyPlanSettingAccessible) throws AppException {
		
		TPlanSettingSessionEntity updateEntity = tPlanSettingSessionDao.selectByPK(sessionDto.getTenantSeq(), sessionDto.getAccountSeq());
		if (updateEntity == null) {
			// コントローラ側でのsessionDtoの取得時にチェックを行っているため基本的にはありえないケース。
			throw new IllegalStateException("セッションテーブルのレコードが存在しません。");
		}
		
		// 更新値を設定
		updateEntity.setOnlyPlanSettingAccessibleFlg(SystemFlg.booleanToCode(isOnlyPlanSettingAccessible));
		
		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tPlanSettingSessionDao.update(updateEntity);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}
		
		// SessionDtoの値も合わせて更新する
		sessionDto.setOnlyPlanSettingAccessible(isOnlyPlanSettingAccessible);
	}
	
	/**
	 * 無料期間中のプラン内容の変更処理
	 * <pre>
	 *  無料期間中に変更が可能なのは、プランタイプ、ライセンス数のみのため、
	 *  プランタイプ、ライセンス数のみ値を更新し、ストレージ量やその他の値の更新は行わない。
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @throws AppException
	 */
	private void freePlanDBUpdate(PlanSettingSessionInfoDto sessionDto, PlanSettingForm viewForm) throws AppException {
		
		String planTypeId = viewForm.getPlanTypeIdForSave();
		Long newLicenseCount = Long.valueOf(viewForm.getLicenseNumForSave());
		
		// 管理DBのテナント情報の更新
		MTenantMgtEntity mTenantMgtEntity = mTenantMgtDao.selectBySeq(sessionDto.getTenantSeq());
		mTenantMgtEntity.setLicenseCount(newLicenseCount);
		mTenantMgtEntity.setPlanType(planTypeId);
		this.updateTenantMgt(mTenantMgtEntity);
		
		// 無料プランの履歴情報の更新
		TPlanHistoryEntity freePlanHistory = this.getFreePlanHistory();
		freePlanHistory.setLicenseCount(newLicenseCount);
		freePlanHistory.setPlanType(planTypeId);
		this.updatePlanHistory(freePlanHistory);
	}
	
	/**
	 * プランの登録処理。プランの登録処理とプランの更新処理の両方で利用される。
	 * 
	 * <pre>
	 *  ■プランの登録処理は下記の２つの処理によって実施される
	 *  
	 *  １．ロボットペイメント側の課金情報の登録処理
	 *  ２．loioz側のDBへのプラン情報の登録／更新処理
	 *  
	 *  本処理では、１が成功し、２以降の処理でエラーが発生した場合、
	 *  １．の状態を元に戻す（登録した課金情報の停止）APIを再度コールし、ロールバックを行う。
	 *  （loiozDBのロールバックはフレームワークのトランザクションによりロールバックされる。）
	 *  
	 *  ロボットペイメント側のロールバックに失敗した場合はErrorログにて、運用にて対応する旨を通知する。
	 * </pre>
	 * 
	 * <pre>
	 *  ■プランの登録処理は下記の３つの条件によって処理の分岐が行われるが、行う処理自体は「当月から翌月以降も継続する課金情報を登録」処理となる。
	 *  
	 *  【条件】
	 *  １．当月中の初回のプラン変更かどうか（当月中にまだプラン変更を行っていないかどうか）
	 *  ２．本処理を行う前のプランステータス
	 *  ３．本処理を行う前のプランの利用期限内かどうか
	 *  
	 *  【条件の組み合わせと実行する処理】
	 *  １=True  ２=変更中     ３=True           → 当月から翌月以降も継続する課金情報を登録
	 *  １=True  ２=変更中     ３=False          → 当月から翌月以降も継続する課金情報を登録
	 *  １=True  ２=変更中以外 ３=どちらでもよい → 当月から翌月以降も継続する課金情報を登録
	 *  １=False ２=変更中     ３=どちらでもよい → 当月から翌月以降も継続する課金情報を登録
	 *  １=False ２=解約       ３=どちらでもよい → 当月から翌月以降も継続する課金情報を登録
	 *  １=False ２=変更中、解約以外       ３=どちらでもよい → 例外
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @throws AppException
	 */
	private void planRegist(PlanSettingSessionInfoDto sessionDto, PlanSettingForm viewForm) throws AppException {
		
		// 現在の（更新前の）契約情報を取得
		PlanInfo nowRegistPlanInfo = this.getNowPlanInfo(sessionDto);
		PlanStatus nowRegistPlanStatus = nowRegistPlanInfo.getPlanStatus();
		LocalDateTime nowStatusExpiredAt = nowRegistPlanInfo.getExpiredAt();
		
		String token = viewForm.getToken();
		// プランタイプ、ライセンス数、ストレージ容量は必須入力のため、NULLチェックは行わない
		PlanType newPlanType = PlanType.of(viewForm.getPlanTypeIdForSave());
		Long newLicenseCount = Long.valueOf(viewForm.getLicenseNumForSave());
		Long newStorageCapacity = Long.valueOf(viewForm.getStorageCapacityForSave());
		
		// 契約ステータス（プランを登録したので有効とする）
		PlanStatus newPlanStatus = PlanStatus.ENABLED;
		// 利用期限（プランを登録したので利用期限はなしとする） ※利用期限は契約ステータスが「無料」、「解約」、「変更中」に変更した場合に設定をする
		LocalDateTime expiredAt = null;
		
		// テナントの契約ステータスを更新
		this.updateTenantPlanStatus(sessionDto, newPlanStatus, newPlanType, newLicenseCount, newStorageCapacity, expiredAt);
		
		LocalDate now = LocalDate.now();
		
		// 現在が無料ステータスでの無料トライアル中かどうか
		boolean nowIsFreeStatusAndFreeTrial = commonPlanService.nowIsFreeStatusAndFreeTrial(now, nowRegistPlanStatus, nowStatusExpiredAt);
		
		String autoChargeNumber = null;
		
		// プランの登録
		switch(nowRegistPlanStatus) {
			case FREE:
			case CANCELED:
			case CHANGING:
				// 初回の有料プラン登録の場合
				// 登録処理直前の契約ステータスが「解約」の場合
				// 登録処理直前の契約ステータスが「変更中」の場合
				
				// 自動課金情報の登録APIを実行（ロボットペイメントの外部API）
				autoChargeNumber = this.execPlanRegist(sessionDto, token, now, nowRegistPlanStatus, nowStatusExpiredAt, nowIsFreeStatusAndFreeTrial, newPlanStatus, newPlanType, newLicenseCount, newStorageCapacity);
				break;
			default:
				// 「無料」「解約」「変更中」以外のステータスはありえない（この処理は実行されない）ためエラーとする
				logger.error("プランの登録処理が「無料」「解約」「変更中」以外の契約ステータスの状態で実行されたためエラー。");
				throw new RuntimeException();
		}
		
		BigInteger autoChargeNumberBi = new BigInteger(autoChargeNumber);

		try {
			
			// 利用プラン履歴を登録
			this.registPlanHistory(sessionDto, newPlanStatus, newPlanType, newLicenseCount, newStorageCapacity, expiredAt, autoChargeNumberBi, nowIsFreeStatusAndFreeTrial);

			// 自動課金番号を登録する
			this.updateTenantAutoChargeNumber(sessionDto, autoChargeNumberBi);

			// カード情報を登録する
			PaymentCardDto cardDto = viewForm.getCardInfo();
			this.savePaymentCard(cardDto);

			// セッションに保持するプラン設定画面以外の画面へのアクセス権限を更新する
			this.updateSessionTableOnlyPlanSettingAccessibleFlg(sessionDto, false);
			
		} catch (Exception e) {
			// ロボットペイメントへの課金情報の登録は成功したが、loioz側への課金データの登録に失敗したため、
			// ロボットペイメント側の課金情報を停止し、loioz側へのDB更新トランザクションをロールバックする

			// 登録したプランの停止処理
			switch(nowRegistPlanStatus) {
				case FREE:
				case CANCELED:
				case CHANGING:
					try {
						// 課金情報を停止（ロボットペイメントの外部API）
						this.execPlanStopApi(autoChargeNumberBi);
					} catch (Exception ex) {
						// ロボットペイメントへ登録した課金情報の削除（停止）にも失敗した場合は、
						// Errorログを出力し、メール発砲等により、運用にて対応する。

						// ロボットペイメント側に課金情報が登録されたが、loioz側に課金情報が登録できなかった場合、loioz側では対象ユーザーの課金状態は更新されない古い状態になる。（自動課金番号、契約ステータス、プラン履歴）。
						// 上記の状態では、新たに登録されたロボットペイメント側の課金情報とloioz側の課金情報が紐付かなくなってしまうため、ユーザーが新たに課金情報の登録や更新を行った場合に、新たな課金情報がロボットペイメント側に登録されてしまい、二重課金が発生することになる。
						// そのため、loioz側の課金情報との紐付きがなくなってしまったロボットペイメント側の自動課金情報について、停止処理を行うことでloioz側の課金情報との整合性を保つようにする。
						logger.error(messageService.getMessage(MessageEnum.MSG_E00065, Locale.JAPANESE), autoChargeNumberBi.toString());
					}
					break;
				default:
					// このケースに来ることはない（上記の同様のswitch文で例外を投げているため）。
			}

			// 本メソッドで実行したloiozDBへの処理をすべてロールバック
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 課金情報の登録を実行する
	 * 
	 * @param sessionDto
	 * @param token
	 * @param now
	 * @param nowRegistPlanStatus
	 * @param nowStatusExpiredAt
	 * @param nowIsFreeStatusAndFreeTrial 現在が無料ステータスでの無料トライアル中かどうか
	 * @param newPlanStatus
	 * @param newPlanType
	 * @param newLicenseCount
	 * @param newStorageCapacity
	 * @return
	 * @throws AppException
	 */
	private String execPlanRegist(PlanSettingSessionInfoDto sessionDto, String token, LocalDate now,
			PlanStatus nowRegistPlanStatus, LocalDateTime nowStatusExpiredAt, boolean nowIsFreeStatusAndFreeTrial,
			PlanStatus newPlanStatus, PlanType newPlanType, Long newLicenseCount, Long newStorageCapacity) throws AppException {
		
		// 課金開始日
		LocalDate chargeStartDate = this.getChargeStartDateForPlanRegist(now, nowIsFreeStatusAndFreeTrial, nowStatusExpiredAt);
		
		// 2回目以降の課金金額
		Long afterNextCharge = commonPlanService.getSumCharge(newPlanType, newLicenseCount, newStorageCapacity);
		
		// 初回の課金金額
		Long firstCharge = this.getFirstChargeAmountForPlanRegist(sessionDto, now, nowIsFreeStatusAndFreeTrial, nowStatusExpiredAt,
				newPlanStatus, newPlanType, newLicenseCount, newStorageCapacity, chargeStartDate, afterNextCharge);
		
		// 自動課金情報の登録APIを実行（ロボットペイメントの外部API）
		return this.execPlanRegistApi(sessionDto, token, chargeStartDate, firstCharge, afterNextCharge, nowIsFreeStatusAndFreeTrial);
	}
	
	/**
	 * 課金情報の登録APIを実行する
	 * 
	 * @param sessionDto
	 * @param token
	 * @param chargeStartDate
	 * @param firstCharge
	 * @param afterNextCharge
	 * @param nowIsFreeStatusAndFreeTrial
	 * @return
	 * @throws AppException
	 */
	private String execPlanRegistApi(PlanSettingSessionInfoDto sessionDto, String token, LocalDate chargeStartDate, Long firstCharge, Long afterNextCharge, boolean nowIsFreeStatusAndFreeTrial) throws AppException {
		
		// 課金開始日
		String chargeStartDateStr = DateUtils.parseToString(chargeStartDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		
		// 2回目以降の課金金額
		String acam = String.valueOf(afterNextCharge);
		// 2回目以降の課金の税額
		Long afterNextChargeTaxAmount = commonTaxService.calcTaxAmount(afterNextCharge, DateUtils.getLastDateOfNextMonth(chargeStartDate));
		String actx = String.valueOf(afterNextChargeTaxAmount);
		
		// DBから取得する
		Long tenantSeq = sessionDto.getTenantSeq();
		MTenantEntity tenantEntity = mTenantDao.selectBySeq(tenantSeq);

		// 電話番号（ハイフンを除去）
		String tenantTelNo = tenantEntity.getTenantTelNo();
		String pn = StringUtils.removeChars(tenantTelNo, "-");
		// メールアドレス
		String em = tenantEntity.getTenantDaihyoMailAddress();

		long unixTime = this.getNowUnixTime();
		String authHash = this.getConnectAuthHash(unixTime);

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("aid", paymentAid)); // 店舗ID
		params.add(new BasicNameValuePair("jb", "CAPTURE")); // 決済ジョブタイプ（固定値）
		params.add(new BasicNameValuePair("rt", "1")); // 結果返信方法（0:キックバック、1:レスポンス）
		params.add(new BasicNameValuePair("tkn", token)); // カード情報トークン
		params.add(new BasicNameValuePair("pn", pn)); // 電話
		params.add(new BasicNameValuePair("em", em)); // メールアドレス
		params.add(new BasicNameValuePair("am", "0")); // 商品金額（円）（自動課金情報を登録した時点で即発生する課金）
		params.add(new BasicNameValuePair("tx", "0")); // 商品税金額（円）（自動課金情報を登録した時点で即発生する課金）
		params.add(new BasicNameValuePair("sf", "0")); // 初回課金の送料額（円）（自動課金情報を登録した時点で即発生する課金）
		params.add(new BasicNameValuePair("actp", "4")); // 課金周囲は「毎月」とする
		params.add(new BasicNameValuePair("acam", acam)); // 課金2回目以降の課金額（円）
		params.add(new BasicNameValuePair("ut", String.valueOf(unixTime))); // 認証情報
		params.add(new BasicNameValuePair("hak", authHash)); // 認証情報
		// 以下、任意設定パラメータ
		params.add(new BasicNameValuePair("actx", actx)); // 課金2回目以降の税金額（円）
		params.add(new BasicNameValuePair("acsf", "0")); // 課金2回目以降の送料額（円）
		params.add(new BasicNameValuePair("ac1", "99")); // 末日を課金日とする
		params.add(new BasicNameValuePair("ac3", "0")); // 課金停止回数は指定しない

		// 初回課金金額
		String tram = String.valueOf(firstCharge);
		// 初回課金の税額
		Long firstChargeTaxAmount = commonTaxService.calcTaxAmount(firstCharge, chargeStartDate);
		String trtx = String.valueOf(firstChargeTaxAmount);
		
		// 任意設定パラメータ
		params.add(new BasicNameValuePair("trtp", "3")); // お試し期間タイプ（3:お試し期間）
		params.add(new BasicNameValuePair("tr3", chargeStartDateStr)); // お試し期限（お試し期間の課金発生日）
		params.add(new BasicNameValuePair("tram", tram)); // お試し期間金額（円）（初回課金金額）
		params.add(new BasicNameValuePair("trtx", trtx)); // お試し期間税額（円）（初回課金税額）

		// リクエストURL
		String url = "https://credit.j-payment.co.jp/gateway/gateway_token.aspx";

		HttpResponse<String> response = this.doGetRequest(url, params);

		if (200 != response.statusCode()) {
			// 登録失敗
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		Map<String, String> responseParamMap = this.toMapGatewayTokenResponse(response.body());

		String erroCd = responseParamMap.get("ec");
		if (StringUtils.isNotEmpty(erroCd)) {
			// 登録失敗
			logger.error("ロボットペイメント側への課金情報の登録処理に失敗した（エラーコード：" + erroCd + "）");
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		// 自動課金番号
		return responseParamMap.get("acid");
	}
	
	/**
	 * プランのステータス状態を「変更中」に更新する
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @param autoChargeNumber 現在の自動課金番号
	 * @throws AppException
	 */
	private void planStatusUpdateChanging(PlanSettingSessionInfoDto sessionDto, PlanSettingForm viewForm, BigInteger autoChargeNumber) throws AppException {
		
		// プランタイプ、ライセンス数、ストレージ容量は必須入力のため、NULLチェックは行わない
		PlanType planType = PlanType.of(viewForm.getPlanTypeIdForSave());
		Long licenseCount = Long.valueOf(viewForm.getLicenseNumForSave());
		Long storageCapa = Long.valueOf(viewForm.getStorageCapacityForSave());
		
		// プランの変更処理は「更新→登録」と、2つの処理が完了することで処理完了となるため、
		// 本「更新」処理の完了の状態のみの状態では、ステータスは「変更中」とする。
		// 「更新」の後に行う「登録」処理でエラーが発生した場合は、ステータスは「変更中」のままとなり、
		// ユーザーに「登録」処理を再度実行してもらうように促すつくりとする。
		PlanStatus updatePlanStatus = PlanStatus.CHANGING;
		// 「変更中」のステータスの場合は、解約と同じ状態（次回の自動課金の実行で課金が終了となる状態）のため、利用期限は月末までとする
		LocalDateTime expiredAt = DateUtils.getEndDateTimeOfThisMonth(LocalDateTime.now());
		
		// プランタイプが会計機能を利用不可のものに変更となる場合、会計情報を削除する
		// ※※ プランの「停止」→「登録」処理の両方が正常に終了すれば、この処理は必要ないが（プランの登録処理側でこれと同じ処理が行われるため）、
		//       「登録」処理に失敗し、「登録」処理がロールバックされるケースでは、ここ（テナントのプランタイプを入力値で更新するここ）で会計情報の削除処理を行い、
		//       テナントのプランタイプの状態と、登録データの状態の整合性を保つ必要がある。
		// ※※ プランの「停止」→「登録」処理の両方が正常に終了するケースでは、この処理は2回実行されることになるが問題ない。(削除処理なので2回実行されても特に問題はない)
		// ※※ 「解約」ステータスからのプラン変更の場合、ここの処理は通らないため、「登録」処理側にもこの処理と同じ処理は必要となる。
		boolean isChangeUnavailableKaikei = this.isChangeUnavailableKaikei(sessionDto, planType);
		if (isChangeUnavailableKaikei) {
			// 旧会計のデータをすべて削除
			commonKaikeiService.deleteAllKaikeiData();
			// 新会計のデータをすべて削除
			commonAccgService.deleteAllAccgTempData();
		}
		
		// テナントの契約ステータスを更新
		this.updateTenantPlanStatus(sessionDto, updatePlanStatus, planType, licenseCount, storageCapa, expiredAt);
		
		// 利用プラン履歴を登録する
		// ステータス「変更」への更新処理は無料期間終了後しか行えないため固定でfalseとする
		// （※無料期間中のプラン変更はペイメント側の課金情報を停止→登録するのではなく、更新を行うため、「変更中」へのステータス更新は必要はなく、ステータスは有効のままとなるため。）
		boolean nowIsFreeStatusAndFreeTrial = false;
		this.registPlanHistory(sessionDto, updatePlanStatus, planType, licenseCount, storageCapa, expiredAt, autoChargeNumber, nowIsFreeStatusAndFreeTrial);
		
		// 自動課金番号を更新する
		this.updateTenantAutoChargeNumber(sessionDto, autoChargeNumber);
	}
	
	/**
	 * 無料期間中のみ可能なプランの削除処理
	 * 
	 * <pre>
	 * 無料期間中以外に行うプランの解約とは異なり、
	 * ロボットペイメント側の自動課金を即停止し、無料期間中に行ったプラン登録で作成したデータを削除することで、
	 * プランの登録を行っていない状態（無料トライアルの状態）に戻す処理を行う。
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param autoChargeNumber 停止対象の自動課金番号
	 * @throws AppException
	 */
	private void planDeleteOnlyDuringFreeTrial(PlanSettingSessionInfoDto sessionDto, BigInteger autoChargeNumber) throws AppException {
		
		// 無料ステータスを設定する
		PlanStatus newPlanStatus = PlanStatus.FREE;
		
		// 無料プランのプラン履歴情報から無料期限を取得
		TPlanHistoryEntity freePlanHistory = this.getFreePlanHistory();
		LocalDateTime freeTrialExpiredAt = freePlanHistory.getExpiredAt();
		
		// テナントの契約ステータスを更新
		this.updateTenantPlanStatus(sessionDto, newPlanStatus, freeTrialExpiredAt);
		// 自動課金番号を削除
		BigInteger newAutoChargeNumber = null;
		this.updateTenantAutoChargeNumber(sessionDto, newAutoChargeNumber);
		
		// 無料ステータス以外のプラン履歴の削除
		this.deleteAllPlanHistoryExceptFreeHistory();
		
		// カード情報削除
		this.deletePaymentCard();
		
		// 課金停止APIコール
		this.execPlanStopApi(autoChargeNumber);
	}
	
	/**
	 * 無料プランの履歴情報を取得する
	 * 
	 * @return
	 */
	private TPlanHistoryEntity getFreePlanHistory() {
		
		TPlanHistoryEntity freePlanHistory = tPlanHistoryDao.selectFreePlanHistory();
		if (freePlanHistory == null) {
			throw new IllegalStateException("無料ステータスのプラン履歴が存在していない");
		}
		LocalDateTime freeTrialExpiredAt = freePlanHistory.getExpiredAt();
		if (freeTrialExpiredAt == null) {
			throw new IllegalStateException("無料ステータスのプラン履歴に有効期限が設定されていない");
		}
		
		return freePlanHistory;
	}
	
	/**
	 * 課金情報の停止更新APIを実行する
	 * 
	 * @param autoChargeNumberBi
	 * @throws AppException
	 */
	private void execPlanCancelUpdateApi(BigInteger autoChargeNumberBi) throws AppException {

		// 解約（停止）対象の自動課金番号
		String acid = autoChargeNumberBi.toString();

		long unixTime = this.getNowUnixTime();
		String authHash = this.getConnectAuthHash(unixTime);

		// 最後の課金が行われるようにする

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("aid", paymentAid)); // 店舗ID
		params.add(new BasicNameValuePair("acid", acid)); // 更新対象の自動課金番号
		params.add(new BasicNameValuePair("cmd", "1")); // 変更タイプ（固定）
		params.add(new BasicNameValuePair("ut", String.valueOf(unixTime))); // 認証情報
		params.add(new BasicNameValuePair("hak", authHash)); // 認証情報

		params.add(new BasicNameValuePair("ac3", "1"));// 課金停止回数指定 ※1を指定（次回の課金を最後の課金とし、以降課金後自動課金を停止する）

		// リクエストURL
		String url = "https://credit.j-payment.co.jp/gateway/accgate.aspx";

		HttpResponse<String> response = this.doGetRequest(url, params);

		if (200 != response.statusCode()) {
			// 保存失敗
			throw new AppException(MessageEnum.MSG_E00013, null);
		}

		Map<String, String> responseParamMap = this.toMapAccgateResponse(response.body());

		String result = responseParamMap.get("result");
		if (!"OK".equals(result)) {
			// 保存失敗
			logger.error("ロボットペイメント側への課金情報の停止更新処理（停止回数を1回に更新する解約処理）に失敗した（エラーコード：" + result + "）");
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}
	
	/**
	 * 課金情報の停止APIを実行する
	 * 
	 * @param autoChargeNumberBi
	 * @throws AppException
	 */
	private void execPlanStopApi(BigInteger autoChargeNumberBi) throws AppException {

		// 停止対象の自動課金番号
		String acid = autoChargeNumberBi.toString();

		long unixTime = this.getNowUnixTime();
		String authHash = this.getConnectAuthHash(unixTime);

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("aid", paymentAid)); // 店舗ID
		params.add(new BasicNameValuePair("cmd", "1")); // 停止処理タイプ（固定）
		params.add(new BasicNameValuePair("acid", acid)); // 停止対象の自動課金番号
		params.add(new BasicNameValuePair("ut", String.valueOf(unixTime))); // 認証情報
		params.add(new BasicNameValuePair("hak", authHash)); // 認証情報

		// リクエストURL
		String url = "https://credit.j-payment.co.jp/gateway/acsgate.aspx";

		HttpResponse<String> response = this.doGetRequest(url, params);

		if (200 != response.statusCode()) {
			// 停止失敗
			throw new AppException(MessageEnum.MSG_E00013, null);
		}

		Map<String, String> responseParamMap = this.toMapAcsgateResponse(response.body());

		String result = responseParamMap.get("result");
		if (!("OK".equals(result) || "ER:ER024".equals(result))) {
			// OKまたはER:ER024（既に停止が完了している）ではない場合はエラー
			// 停止失敗
			logger.error("ロボットペイメント側への課金情報の停止処理（プラン変更時に行う自動課金の即停止処理）に失敗した（エラーコード：" + result + "）");
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}
	
	/**
	 * 課金情報のカード情報変更APIを実行する
	 * 
	 * @param sessionDto
	 * @param token
	 * @param autoChargeNumberBi
	 * @throws AppException
	 */
	private void execCardUpdateApi(PlanSettingSessionInfoDto sessionDto, String token, BigInteger autoChargeNumberBi) throws AppException {

		Long tenantSeq = sessionDto.getTenantSeq();
		MTenantEntity tenantEntity = mTenantDao.selectBySeq(tenantSeq);

		// 電話番号（ハイフンを除去）
		String tenantTelNo = tenantEntity.getTenantTelNo();
		String pn = StringUtils.removeChars(tenantTelNo, "-");
		// メールアドレス
		String em = tenantEntity.getTenantDaihyoMailAddress();

		long unixTime = this.getNowUnixTime();
		String authHash = this.getConnectAuthHash(unixTime);

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("aid", paymentAid)); // 店舗ID
		params.add(new BasicNameValuePair("acid", autoChargeNumberBi.toString())); // 変更対象の自動課金番号
		params.add(new BasicNameValuePair("cmd", "1")); // 変更タイプ（固定）
		params.add(new BasicNameValuePair("tkn", token)); // カード情報トークン
		params.add(new BasicNameValuePair("ut", String.valueOf(unixTime))); // 認証情報
		params.add(new BasicNameValuePair("hak", authHash)); // 認証情報
		// 以下、任意設定パラメータ
		params.add(new BasicNameValuePair("pn", pn)); // 電話番号
		params.add(new BasicNameValuePair("em", em)); // メールアドレス

		// リクエストURL
		String url = "https://credit.j-payment.co.jp/gateway/accgate_token.aspx";

		HttpResponse<String> response = this.doGetRequest(url, params);

		if (200 != response.statusCode()) {
			// 保存失敗
			throw new AppException(MessageEnum.MSG_E00013, null);
		}

		Map<String, String> responseParamMap = this.toMapAccgateTokenResponse(response.body());

		String result = responseParamMap.get("result");
		if (!"OK".equals(result)) {
			// 保存失敗
			logger.error("ロボットペイメント側へのカード情報の更新処理に失敗した（エラーコード：" + result + "）");
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}
	
	/**
	 * テナントの契約ステータスを更新する
	 * <pre>
	 * nullで更新するケースもあり
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param status
	 * @param planType
	 * @param licenseCount
	 * @param storageCapacity
	 * @param expiredAt
	 * @throws AppException
	 */
	private void updateTenantPlanStatus(PlanSettingSessionInfoDto sessionDto, PlanStatus status, PlanType planType, Long licenseCount, Long storageCapacity, LocalDateTime expiredAt) throws AppException {

		MTenantMgtEntity mTenantMgtEntityForUpdateStatus = mTenantMgtDao.selectBySeq(sessionDto.getTenantSeq());

		mTenantMgtEntityForUpdateStatus.setPlanStatus(status.getCd());
		mTenantMgtEntityForUpdateStatus.setPlanType(planType.getId());
		mTenantMgtEntityForUpdateStatus.setLicenseCount(licenseCount);
		mTenantMgtEntityForUpdateStatus.setStorageCapacity(storageCapacity);
		mTenantMgtEntityForUpdateStatus.setExpiredAt(expiredAt);

		this.updateTenantMgt(mTenantMgtEntityForUpdateStatus);
	}
	
	/**
	 * テナントの契約ステータスを更新する
	 * <pre>
	 * nullで更新するケースもあり
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param status
	 * @param expiredAt
	 * @throws AppException
	 */
	private void updateTenantPlanStatus(PlanSettingSessionInfoDto sessionDto, PlanStatus status, LocalDateTime expiredAt) throws AppException {
		
		MTenantMgtEntity mTenantMgtEntityForUpdateStatus = mTenantMgtDao.selectBySeq(sessionDto.getTenantSeq());
		
		mTenantMgtEntityForUpdateStatus.setPlanStatus(status.getCd());
		mTenantMgtEntityForUpdateStatus.setExpiredAt(expiredAt);
		
		this.updateTenantMgt(mTenantMgtEntityForUpdateStatus);
	}
	
	/**
	 * テナント管理情報のupdate処理
	 * 
	 * @param mTenantMgtEntity
	 * @throws AppException
	 */
	private void updateTenantMgt(MTenantMgtEntity mTenantMgtEntity) throws AppException {
		
		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = mTenantMgtDao.update(mTenantMgtEntity);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}
	
	/**
	 * プラン履歴情報のupdate処理
	 * 
	 * @param tPlanHistoryEntity
	 * @throws AppException
	 */
	private void updatePlanHistory(TPlanHistoryEntity tPlanHistoryEntity) throws AppException {
		
		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tPlanHistoryDao.update(tPlanHistoryEntity);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}
	
	/**
	 * 契約プランの変更履歴を登録する
	 * 
	 * <pre>
	 * このメソッドは「無料期間中のプラン変更」の処理では利用しないこと。
	 * 
	 * （つまりこのメソッド内では、現在日が無料期間より前となるケースは、「無料期間中のプラン登録（無料ステータスからのプラン登録）」のケースのみとなり、
	 * それ以外のケースでは、無料期間終了後のプラン登録やプラン変更、解約などのケースとなる。）
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param registStatus
	 * @param planType
	 * @param licenseCount
	 * @param storageCapacity
	 * @param expiredAt
	 * @param autoChargeNumber
	 * @param nowIsFreeStatusAndFreeTrial 現在が無料ステータスでの無料トライアル中かどうか
	 * @return
	 * @throws AppException
	 */
	private TPlanHistoryEntity registPlanHistory(PlanSettingSessionInfoDto sessionDto,
			PlanStatus registStatus, PlanType planType, Long licenseCount, Long storageCapacity,
			LocalDateTime expiredAt, BigInteger autoChargeNumber, boolean nowIsFreeStatusAndFreeTrial) throws AppException {
		
		TPlanHistoryEntity insertEntity = new TPlanHistoryEntity();
		insertEntity.setTenantSeq(sessionDto.getTenantSeq());

		insertEntity.setPlanStatus(registStatus.getCd());
		insertEntity.setPlanType(planType.getId());
		insertEntity.setLicenseCount(licenseCount);
		insertEntity.setStorageCapacity(storageCapacity);
		insertEntity.setExpiredAt(expiredAt);
		insertEntity.setAutoChargeNumber(autoChargeNumber);

		LocalDate now = LocalDate.now();
		
		// 当月の料金
		Long chargeThisMonth = null;
		if (nowIsFreeStatusAndFreeTrial) {
			// 無料期間中の場合（無料期間中のプラン登録の場合）
			
			// このケースでは、履歴データの「ChargeThisMonth」は「無料トライアル終了日」の当月、
			// 「ChargeAfterNextMonth」は「無料トライアル終了日」の月の翌月以降の金額とする。
			// 
			// ※通常では、
			//   履歴データの「ChargeThisMonth」は履歴登録日の当月、
			//   「ChargeAfterNextMonth」は履歴登録日の月の翌月以降の金額となる。
			
			LocalDateTime freeTrialExpiredAt = this.getFreeTrialExpiredAt(sessionDto);
			LocalDate freeTrialExpiredDate = DateUtils.convertLocalDate(freeTrialExpiredAt);
			
			boolean freeTrialExpiredDateIsMonthLastDate = DateUtils.isMonthLastDate(freeTrialExpiredDate);
			if (freeTrialExpiredDateIsMonthLastDate) {
				// 無料期間の終了日が月末日
				
				// 当月は無料のため「当月の課金金額」は0円とする。
				chargeThisMonth = 0L;
			} else {
				// 無料期間の終了日が月末日以外
				
				LocalDate nextDateOfFreeTrialExpired = DateUtils.getNextDate(freeTrialExpiredDate);
				Long fullMonthCharge = commonPlanService.getSumCharge(planType, licenseCount, storageCapacity);
				
				// 「無料期間の終了日の翌日」から月末までの日割り
				boolean isIncludeStartDate = true;
				chargeThisMonth = commonPlanService.getChargeOfUntilEndOfMonth(nextDateOfFreeTrialExpired, fullMonthCharge, isIncludeStartDate);
			}
			
		} else {
			// 無料期間中ではない場合（無料期間終了後の、プラン登録、プラン変更、解約の場合）
			
			boolean nowIsMonthLastDate = DateUtils.isMonthLastDate(now);
			if (nowIsMonthLastDate) {
				// 現在日が月末日の場合、当月の課金は既に終了しているため、現在有効な当月の課金金額を設定する必要がある。
				chargeThisMonth = this.calcThisMonthChargeForRegistPlanHistoryCaseNowIsMonthLastDate(sessionDto);
			} else {
				// 当月の料金を計算（日割り金額）
				chargeThisMonth = this.calcTargetDateMonthCharge(sessionDto, now, registStatus, planType, licenseCount, storageCapacity);
			}
		}
		
		// 翌月以降の料金
		Long chargeAfterNextMonth = null;
		
		switch(registStatus) {
			case ENABLED:
			case CHANGING:
				chargeAfterNextMonth = commonPlanService.getSumCharge(planType, licenseCount, storageCapacity);
				break;
			case CANCELED:
				chargeAfterNextMonth = null;
				break;
			case FREE:
				// 新規に無料プランを登録するケースは存在しない（無料プランの履歴は初回アカウント登録の際のみ登録される）
				logger.error("初回以降登録されないはずの無料プランのステータスが登録されようとしている");
				throw new RuntimeException();
			default:
				logger.error("Impossible status");
				throw new RuntimeException();
		}
		
		// 当月と翌月以降の料金情報を設定
		if (chargeThisMonth != null) {
			insertEntity.setChargeThisMonth(BigDecimal.valueOf(chargeThisMonth));
		}
		if (chargeAfterNextMonth != null) {
			insertEntity.setChargeAfterNextMonth(BigDecimal.valueOf(chargeAfterNextMonth));
		}

		int insertCount = tPlanHistoryDao.insert(insertEntity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
		
		return insertEntity;
	}
	
	/**
	 * 「無料」ステータス以外の全てのプラン履歴を削除する。
	 * <pre>
	 * 「無料」ステータスのプラン履歴はプラン履歴テーブルに1件しか存在しないため、
	 * この処理を行うと、プラン履歴テーブルのデータは1件のみ（「無料」ステータスの履歴のみ）となる。
	 * </pre>
	 * @throws AppException 
	 * 
	 */
	private void deleteAllPlanHistoryExceptFreeHistory() throws AppException {
		
		TPlanHistoryEntity freePlanHistory = tPlanHistoryDao.selectFreePlanHistory();
		if (freePlanHistory == null) {
			throw new IllegalStateException("無料ステータスのプラン履歴が存在していない");
		}
		
		// 無料ステータスの履歴を除く、全てのプラン履歴
		List<TPlanHistoryEntity> allPlanHistoryExceptFree = tPlanHistoryDao.selectAllPlanHistoryExceptFreePlanHistory();
		
		int[] deleteCountArray;
		try {
			// 削除
			deleteCountArray = tPlanHistoryDao.batchDelete(allPlanHistoryExceptFree);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
		
		if (deleteCountArray.length != allPlanHistoryExceptFree.size()) {
			// 実際の削除件数と削除対象の件数が異なる場合はエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}
	
	/**
	 * テナントの自動課金番号を更新する
	 * 
	 * @param sessionDto
	 * @param autoChargeNumber
	 * @throws AppException
	 */
	private void updateTenantAutoChargeNumber(PlanSettingSessionInfoDto sessionDto, BigInteger autoChargeNumber) throws AppException {

		MTenantMgtEntity mTenantMgtEntityForUpdateAutoChargeNumber = mTenantMgtDao.selectBySeq(sessionDto.getTenantSeq());
		mTenantMgtEntityForUpdateAutoChargeNumber.setAutoChargeNumber(autoChargeNumber);

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = mTenantMgtDao.update(mTenantMgtEntityForUpdateAutoChargeNumber);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}
	
	/**
	 * カード情報を保存する
	 * 
	 * @param dto
	 * @throws AppException
	 */
	private void savePaymentCard(PaymentCardDto dto) throws AppException {

		// 更新対象データを取得
		// 1件か0件のデータが登録されているため、データが存在しない場合は初回の登録となる。
		TPaymentCardEntity saveEntity = tPaymentCardDao.selectOne();

		if (saveEntity == null) {
			// 新規登録
			this.registPaymentCard(dto);
		} else {
			// 更新
			this.updatePaymentCard(dto, saveEntity);
		}
	}

	/**
	 * 支払いカード情報を登録する
	 * 
	 * @param dto
	 * @throws AppException
	 */
	private void registPaymentCard(PaymentCardDto dto) throws AppException {

		TPaymentCardEntity insertEntity = new TPaymentCardEntity();
		insertEntity.setCardNumberLast4(dto.getCardNumberLast4());
		insertEntity.setExpiredYear(dto.getExpiredYear());
		insertEntity.setExpiredMonth(dto.getExpiredMonth());
		insertEntity.setLastName(dto.getLastName());
		insertEntity.setFirstName(dto.getFirstName());

		int insertCount = tPaymentCardDao.insert(insertEntity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	/**
	 * 支払いカード情報を更新する
	 * 
	 * @param dto
	 * @param updateEntity
	 * @throws AppException
	 */
	private void updatePaymentCard(PaymentCardDto dto, TPaymentCardEntity updateEntity) throws AppException {

		updateEntity.setCardNumberLast4(dto.getCardNumberLast4());
		updateEntity.setExpiredYear(dto.getExpiredYear());
		updateEntity.setExpiredMonth(dto.getExpiredMonth());
		updateEntity.setFirstName(dto.getFirstName());
		updateEntity.setLastName(dto.getLastName());

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tPaymentCardDao.update(updateEntity);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}
	
	/**
	 * カード情報を削除する
	 * 
	 * @throws AppException
	 */
	private void deletePaymentCard() throws AppException {
		
		// カード情報は登録されていたとしても1件しか存在しない
		TPaymentCardEntity deleteEntity = tPaymentCardDao.selectOne();
		if (deleteEntity == null) {
			return;
		}
		
		// 件数チェック用
		int deleteCount = 0;
		
		try {
			// 削除
			deleteCount = tPaymentCardDao.delete(deleteEntity);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
		
		if (deleteCount != 1) {
			// 削除件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}
	
	/**
	 * 全てのユーザーのライセンス（ステータス）を無効状態に更新する
	 * <pre>
	 * ※ログインユーザーのみは無効状態には更新しない（有効状態に更新する）
	 * </pre>
	 * 
	 * @param sessionDto
	 * @throws AppException
	 */
	private void updateAllAccountLicenseToDisabledOtherThanLoginUser(PlanSettingSessionInfoDto sessionDto) throws AppException {
		
		List<MAccountEntity> enabledAccountList = mAccountDao.selectEnabledAccount();
		Long loginUserAccountSeq = sessionDto.getAccountSeq();
		
		for (MAccountEntity entity : enabledAccountList) {
			if (entity.getAccountSeq().equals(loginUserAccountSeq)) {
				entity.setAccountStatus(CommonConstant.AccountStatus.ENABLED.getCd());
			} else {
				entity.setAccountStatus(CommonConstant.AccountStatus.DISABLED.getCd());
			}
		}
		
		// 更新件数
		int[] resultArray = null;

		try {
			// 更新
			resultArray = mAccountDao.updateList(enabledAccountList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00036, ex);
		}
		if (resultArray.length != enabledAccountList.size()) {
			// 実際の更新件数と更新対象の件数が異なる場合はエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00036, null);
		}
	}
	
	// ▼▼▼▼▼ 当月の日割り料金計算処理 ▼▼▼▼▼
	
	/**
	 * 引数情報より、新プランが開始する当月の課金金額を算出
	 * <pre>
	 * このメソッドは「無料トライアル中のプラン登録」、「無料期間中のプラン変更」の処理では利用しないこと。
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param newPlanStartDate 新しいプランの開始日（現在日時）
	 * @param newStatus
	 * @param newPlanType
	 * @param newLicenseCount
	 * @param newStorageCapacity
	 * @return
	 */
	private long calcTargetDateMonthCharge(PlanSettingSessionInfoDto sessionDto, LocalDate newPlanStartDate,
			PlanStatus newStatus, PlanType newPlanType, Long newLicenseCount, Long newStorageCapacity) {
		
		long targetDateMonthCharge = 0L;
		
		if (newStatus == null) {
			logger.error("新しい契約ステータスがnullになっている");
			throw new IllegalArgumentException();
		}
		
		if (newStatus == PlanStatus.CANCELED) {
			// 新しく登録するプランが「解約」の場合
			// 当月の料金を算出
			targetDateMonthCharge = this.calcThisMonthChargeForChangeCancelStatus(sessionDto);
			return targetDateMonthCharge;
		}
		
		// 新しいプランの月額
		Long newPlanMonthCharge = commonPlanService.getSumCharge(newPlanType, newLicenseCount, newStorageCapacity);
		
		// 指定月の開始、終了日を取得する
		LocalDateTime firstDateTime = DateUtils.getFirstDateTimeOfThisMonth(newPlanStartDate);
		LocalDateTime endDateTime = DateUtils.getEndDateTimeOfThisMonth(newPlanStartDate);
		
		// プランの金額が変更されうる履歴のステータス
		List<String> planChargeChangesStatus = Arrays.asList(PlanStatus.ENABLED.getCd(), PlanStatus.CHANGING.getCd());
		
		// 指定月内に登録された、当月金額を算出するために必要な履歴情報
		List<TPlanHistoryEntity> targetMonthHistorys = tPlanHistoryDao.selectStatusAndCreatedAtIsBetweenHistoryOrderSeq(planChargeChangesStatus, firstDateTime, endDateTime);
		// 指定月より前の履歴のうち最新の履歴
		TPlanHistoryEntity latestHistoryBeforeTargetMonth = tPlanHistoryDao.selectOneBeforeCreateAtHistory(firstDateTime);
		
		boolean isIncludeStartDate;
		boolean isIncludeEndDate;
		Long chargeOfUntilEndOfMonth;
		BigDecimal decimalChargeOfUntilEndOfMonth;
		BigDecimal targetDateMonthDecimalCharge;
		
		if (CollectionUtils.isEmpty(targetMonthHistorys)) {
			// 指定月内にプランの金額の変更が行われていない場合
			
			if (latestHistoryBeforeTargetMonth == null) {
				// 指定月より前に作成された履歴が1件も存在しない場合
				
				// 指定月が無料期間の利用開始月の場合のみこの状態となる
				
				// 対象日まで無料プランを利用しているため、対象日から当月末までの料金を当月の料金とする
				isIncludeStartDate = true;
				chargeOfUntilEndOfMonth = commonPlanService.getChargeOfUntilEndOfMonth(newPlanStartDate, newPlanMonthCharge, isIncludeStartDate);
				
				// 当月金額
				return targetDateMonthCharge = chargeOfUntilEndOfMonth;
				
			} else {
				
				PlanStatus latestHistoryBeforeTargetMonthPlanStatus = DefaultEnum.getEnum(PlanStatus.class, latestHistoryBeforeTargetMonth.getPlanStatus());
				
				switch(latestHistoryBeforeTargetMonthPlanStatus) {
					case FREE:
					case CANCELED:
					case CHANGING:
						// 前月末時点で無料、解約、変更中のステータスで、当月に入っている状態での、当月の初回のプラン登録／変更のケース
						
						// 対象日までサービスを利用していないため、対象日から当月末までの料金を当月の料金とする
						isIncludeStartDate = true;
						chargeOfUntilEndOfMonth = commonPlanService.getChargeOfUntilEndOfMonth(newPlanStartDate, newPlanMonthCharge, isIncludeStartDate);
						
						// 当月金額
						targetDateMonthCharge = chargeOfUntilEndOfMonth;
						break;
					case ENABLED:
						// 通常通り利用しているケース
						
						if (DateUtils.isMonthFirstDate(newPlanStartDate)) {
							// 対象日（プランの開始日）が月初（1日）の場合は1ヶ月分の料金を設定
							// 当月金額
							targetDateMonthCharge = newPlanMonthCharge;
						} else {
							// 月初（1日）ではない場合は日割り計算を行う
							
							// 無料期間の終了日
							LocalDateTime freeTrialExpiredAt = this.getFreeTrialExpiredAt(sessionDto);
							LocalDate freeTrialExpiredDate = DateUtils.convertLocalDate(freeTrialExpiredAt);

							// この処理は、無料期間中には実行されないので、
							// newPlanStartDate（現在日時）が無料期間の終了日より前の日付になることはない、そうでない場合はエラーとする
							if (newPlanStartDate.isBefore(freeTrialExpiredDate) || newPlanStartDate.isEqual(freeTrialExpiredDate)) {
								throw new IllegalStateException("無料期間中には実行されない処理が実行されました。");
							}
							
							// 以前のプランの月額
							Long beforeMonthPlanCharge = latestHistoryBeforeTargetMonth.getChargeAfterNextMonth().longValue();
							
							boolean nowAndExpiredDateIsEqualYearMonth = DateUtils.isEqualYearMonth(newPlanStartDate, freeTrialExpiredDate);
							if (nowAndExpiredDateIsEqualYearMonth) {
								// 新プランの登録日（現在日）と、無料期間の終了日の月が同じ月の場合
								
								// 下記の2つの金額の日割りを計算して、今月の料金とする
								// ・無料期間終了日の翌日～新プラン開始日までの日割り
								// ・新プラン開始日～月末までの日割り
								
								LocalDate nextDateOfFreeTrialExpired = DateUtils.getNextDate(freeTrialExpiredDate);
								
								boolean includeCountStartDate = true;
								boolean includeCountEndDate = false;
								BigDecimal decimalChargeOfExpiredToNewPlanStartDateDecimal = commonPlanService.getDecimalChargeOfStartToEnd(nextDateOfFreeTrialExpired, newPlanStartDate, beforeMonthPlanCharge, includeCountStartDate, includeCountEndDate);
								
								isIncludeStartDate = true;
								decimalChargeOfUntilEndOfMonth = commonPlanService.getDecimalChargeOfUntilEndOfMonth(newPlanStartDate, newPlanMonthCharge, isIncludeStartDate);
								
								// 2つの日割りを加算
								// このケースでNULLはないためNULLチェックは行わない
								targetDateMonthDecimalCharge = decimalChargeOfExpiredToNewPlanStartDateDecimal.add(decimalChargeOfUntilEndOfMonth);
								
								// 当月金額
								targetDateMonthCharge = commonPlanCalcService.roudToLong(targetDateMonthDecimalCharge);
							} else {
								// 新プランの登録日（現在日）が、無料期間の終了日の月ではない（翌月以降の月）の場合
								
								// 下記の2つの金額の日割りを計算して、今月の料金とする
								// ・月初～新プラン開始日までの日割り
								// ・新プラン開始日～月末までの日割り
								
								isIncludeEndDate = false;
								BigDecimal decimalChargeOfFromStartOfMonth = commonPlanService.getDecimalChargeOfFromStartOfMonth(newPlanStartDate, beforeMonthPlanCharge, isIncludeEndDate);
								
								isIncludeStartDate = true;
								decimalChargeOfUntilEndOfMonth = commonPlanService.getDecimalChargeOfUntilEndOfMonth(newPlanStartDate, newPlanMonthCharge, isIncludeStartDate);
								
								// 2つの日割りを加算
								// このケースでNULLはないためNULLチェックは行わない
								targetDateMonthDecimalCharge = decimalChargeOfFromStartOfMonth.add(decimalChargeOfUntilEndOfMonth);
								
								// 当月金額
								targetDateMonthCharge = commonPlanCalcService.roudToLong(targetDateMonthDecimalCharge);
							}
						}
						break;
					default:
						logger.error("ありえないステータスによる処理の実行(ステータスコード" + latestHistoryBeforeTargetMonthPlanStatus.getCd() + ")");
						throw new RuntimeException();
				}
				
				return targetDateMonthCharge;
			}
		} else {
			// 指定月内にプランの金額の変更が行われている場合
			
			if (DateUtils.isMonthFirstDate(newPlanStartDate)) {
				// 対象日（プランの開始日）が月初（1日）の場合は1ヶ月分の料金を設定
				// 当月金額
				targetDateMonthCharge = newPlanMonthCharge;
				return targetDateMonthCharge;
			}
			
			// 当月の料金計算用のデータリスト
			List<CalcChargeDto> chargeDtos;
			
			if (latestHistoryBeforeTargetMonth == null) {
				// 指定月より前に作成された履歴が1件も存在しない場合
				// （指定月が無料期間の利用開始月の場合のみこの状態となる）
				
				// 当月料金を算出
				chargeDtos = this.createDataListForClacNewPlanMonthCharge(sessionDto , newPlanStartDate, newPlanType, newLicenseCount, newStorageCapacity,
						targetMonthHistorys, null);
				targetDateMonthCharge = this.calcMonthChargeByDailyCalculation(chargeDtos);
				
				return targetDateMonthCharge;
				
			} else {
				
				PlanStatus latestHistoryPlanStatus = DefaultEnum.getEnum(PlanStatus.class, latestHistoryBeforeTargetMonth.getPlanStatus());
				
				switch(latestHistoryPlanStatus) {
					case FREE:
					case CANCELED:
					case CHANGING:
						// 前月末時点で無料、解約、変更中のステータスで、当月に入っている状態での、当月の初回以降のプラン登録／変更のケース
						
						// 当月料金を算出
						chargeDtos = this.createDataListForClacNewPlanMonthCharge(sessionDto , newPlanStartDate, newPlanType, newLicenseCount, newStorageCapacity,
								targetMonthHistorys, null);
						targetDateMonthCharge = this.calcMonthChargeByDailyCalculation(chargeDtos);
						break;
					case ENABLED:
						// 通常通り利用しているケース
						
						// 当月料金を算出
						chargeDtos = this.createDataListForClacNewPlanMonthCharge(sessionDto , newPlanStartDate, newPlanType, newLicenseCount, newStorageCapacity,
								targetMonthHistorys, latestHistoryBeforeTargetMonth);
						targetDateMonthCharge = this.calcMonthChargeByDailyCalculation(chargeDtos);
						break;
					default:
						logger.error("ありえないステータスによる処理の実行(ステータスコード" + latestHistoryPlanStatus.getCd() + ")");
						throw new RuntimeException();
				}
				
				return targetDateMonthCharge;
			}
		}
	}
	
	/**
	 * 解約ステータスへの変更時の当月の料金を算出
	 * <pre>
	 *  解約の場合は金額の変更は行わないので、
	 *  現在有効となっている料金がそのまま当月の料金となる。
	 * </pre>
	 * 
	 * @param sessionDto
	 * @return
	 */
	private Long calcThisMonthChargeForChangeCancelStatus(PlanSettingSessionInfoDto sessionDto) {
		
		LocalDateTime now = LocalDateTime.now();
		LocalDate nowDate = DateUtils.convertLocalDate(now);
		
		Long thisMonthCharge;
		
		// 直近の履歴（ステータスが有効のもの）を取得する
		// ※解約処理は現在が「有効」ステータスでなければ実行できないため、直近の履歴は必ず有効のステータスとなる。
		TPlanHistoryEntity latestEnabledStatusHistory = tPlanHistoryDao.selectLatestHistoryByStatus(PlanStatus.ENABLED.getCd());
		
		// 無料期間の終了日
		LocalDateTime freeTrialExpiredAt = this.getFreeTrialExpiredAt(sessionDto);
		
		// 直近の履歴が「無料期間中のプラン登録の履歴」の場合（=無料期間中に登録された履歴の場合）
		boolean isHistoryRegistDuringFreeTrial = commonPlanService.targetHistoryRegistDuringFreeTrial(latestEnabledStatusHistory, freeTrialExpiredAt);
		if (isHistoryRegistDuringFreeTrial) {
			
			// この処理は、無料期間中の解約処理では実行されないので、
			// nowが無料期間の終了日より前の日付になることはない、そうでない場合はエラーとする
			LocalDate freeTrialExpiredDate = DateUtils.convertLocalDate(freeTrialExpiredAt);
			if (!nowDate.isAfter(freeTrialExpiredDate)) {
				throw new IllegalStateException("無料期間中には実行されない処理が実行されました。");
			}
			
			boolean nowAndExpiredAtIsEqualYearMonth = DateUtils.isEqualYearMonth(now, freeTrialExpiredAt);
			if (nowAndExpiredAtIsEqualYearMonth) {
				// 現在日時と無料期間の終了日時が同じ月の場合
				thisMonthCharge = latestEnabledStatusHistory.getChargeThisMonth().longValue();
			} else {
				// 現在日の月が、無料期間の終了日の月以降の場合
				thisMonthCharge = latestEnabledStatusHistory.getChargeAfterNextMonth().longValue();
			}
			
			return thisMonthCharge;
		}
		
		LocalDateTime historyDateTime = latestEnabledStatusHistory.getHistoryCreatedAt();
		if (DateUtils.isEqualYearMonth(historyDateTime, now)) {
			// 当月の履歴の場合
			thisMonthCharge = latestEnabledStatusHistory.getChargeThisMonth().longValue();
		} else {
			// 当月以前の履歴の場合
			thisMonthCharge = latestEnabledStatusHistory.getChargeAfterNextMonth().longValue();
		}
		
		return thisMonthCharge;
	}
	
	/**
	 * 現在日が月末日の場合の、履歴情報登録時の当月の課金金額を取得する。
	 * 
	 * @param sessionDto
	 * @return
	 */
	private Long calcThisMonthChargeForRegistPlanHistoryCaseNowIsMonthLastDate(PlanSettingSessionInfoDto sessionDto) {
		
		LocalDate now = LocalDate.now();
		if (!DateUtils.isMonthLastDate(now)) {
			throw new IllegalStateException("現在日時は月末日でなければいけない。");
		}
		
		// 最新の履歴情報
		TPlanHistoryEntity latestHistory = tPlanHistoryDao.selectLatestHistory();
		LocalDateTime latestHistoryCreatedAt = latestHistory.getHistoryCreatedAt();
		PlanStatus latestHistoryStatus = DefaultEnum.getEnum(PlanStatus.class, latestHistory.getPlanStatus());
		
		// 無料期間の終了日
		LocalDateTime freeTrialExpiredAt = this.getFreeTrialExpiredAt(sessionDto);
		
		// 最新の履歴が「無料期間中のプラン登録の履歴」の場合（=無料期間中に登録された履歴の場合）
		boolean isHistoryRegistDuringFreeTrial = commonPlanService.targetHistoryRegistDuringFreeTrial(latestHistory, freeTrialExpiredAt);
		if (isHistoryRegistDuringFreeTrial) {
			
			if (latestHistoryStatus != PlanStatus.ENABLED) {
				throw new IllegalStateException("履歴のステータスが「有効」になっていません。");
			}
			
			// 直近の履歴が「無料期間中のプラン登録の履歴」となっているこのケースでは、
			// 次の履歴を登録する処理は無料期間の終了後しか実行されないので、そうでない場合はエラーとする
			LocalDate freeTrialExpiredDate = DateUtils.convertLocalDate(freeTrialExpiredAt);
			if (!now.isAfter(freeTrialExpiredDate)) {
				throw new IllegalStateException("無料期間中には実行されない処理が実行されました。");
			}
			
			Long thisMonthCharge = null;
			
			boolean nowAndExpiredAtIsEqualYearMonth = DateUtils.isEqualYearMonth(now, freeTrialExpiredAt);
			if (nowAndExpiredAtIsEqualYearMonth) {
				// 現在日時と無料期間の終了日時が同じ月の場合
				
				thisMonthCharge = latestHistory.getChargeThisMonth().longValue();
			} else {
				// 現在日の月が、無料期間の終了日の月以降の場合
				
				thisMonthCharge = latestHistory.getChargeAfterNextMonth().longValue();
			}
			
			return thisMonthCharge;
		}
		
		//
		// 最新の履歴が「無料期間中のプラン登録の履歴」以外の履歴の場合
		//
		
		boolean nowAndCreatedAtIsEqualYearMonth = DateUtils.isEqualYearMonth(now, latestHistoryCreatedAt);
		if (nowAndCreatedAtIsEqualYearMonth) {
			// 現在日時と最新の履歴情報の登録日が同じ月の場合
			switch(latestHistoryStatus) {
				case FREE:
					// 当月は無料で利用していた場合(nowは必ず月末日) -> 当月の料金は0円
					return 0L;
				case ENABLED:
				case CHANGING:
				case CANCELED:
					return latestHistory.getChargeThisMonth().longValue();
				default:
					throw new RuntimeException("最新の履歴が無料、有効、変更中、解約以外となっている。");
			}
		} else {
			// 現在日時と最新の履歴情報の登録日が異なる -> 現在日時より過去の月に登録された履歴が最新の場合
			switch(latestHistoryStatus) {
				case FREE:
					// 当月は無料で利用していたか、無料のまま有効期限が切れている場合 -> 当月の料金は0円
					return 0L;
				case ENABLED:
					// 現在もサービス利用中の状態の場合
					return latestHistory.getChargeAfterNextMonth().longValue();
				case CHANGING:
				case CANCELED:
					// 当月はサービス利用不可の状態の場合（有効期限が切れているため） -> 当月の料金は0円
					return 0L;
				default:
					throw new RuntimeException("最新の履歴が無料、有効、変更中、解約以外となっている。");
			}
		}
	}
	
	/**
	 * 新プラン登録月の課金金額を算出するために必要なデータリストを生成する。
	 * 
	 * @param sessionDto
	 * @param newPlanStartDate 新プランの開始日
	 * @param newPlanType 新プランのプランタイプ
	 * @param newLicenseCount 新プランのライセンス数
	 * @param newStorageCapacity 新プランのストレージ量
	 * @param newPlanStartMonthHistorys 新プランの開始日の当月内で、いままで実施されたプラン金額の変更の履歴情報
	 * @param latestHistoryBeforeNewPlanStartMonth 新プランの開始日の当月より前で、直近に行われたプラン金額の変更の履歴情報。
	 *                                              この値がnullの場合は月初からnewPlanStartMonthHistorysの最初の履歴の登録日までの日割りの金額を計算するためのデータは作成されない。
	 * @return 新プラン登録月の課金金額を算出するために必要なデータリスト
	 */
	private List<CalcChargeDto> createDataListForClacNewPlanMonthCharge(PlanSettingSessionInfoDto sessionDto, LocalDate newPlanStartDate,
			PlanType newPlanType, Long newLicenseCount, Long newStorageCapacity,
			List<TPlanHistoryEntity> newPlanStartMonthHistorys, TPlanHistoryEntity latestHistoryBeforeNewPlanStartMonth) {
		
		List<CalcChargeDto> chargeDtos = new ArrayList<>();
		
		// 無料期間の情報
		LocalDateTime freeTrialExpiredAt = this.getFreeTrialExpiredAt(sessionDto);
		LocalDate freeTrialExpiredDate = DateUtils.convertLocalDate(freeTrialExpiredAt);
		
		if (latestHistoryBeforeNewPlanStartMonth != null) {
			// 新プランの開始月以前の履歴情報が設定されている場合、
			// 「月初」～「新プランの開始月に登録された最初の履歴の登録日」の期間の金額計算用データを作成する。
			
			// 最初のデータを作成(適用開始日は当月の初日とする)
			LocalDate targetMonthFirstDate = DateUtils.getFirstDateOfThisMonth(newPlanStartDate);
			BigDecimal beforeMonthPlanCharge = latestHistoryBeforeNewPlanStartMonth.getChargeAfterNextMonth();
			
			CalcChargeDto calcFirstChargeDto = new CalcChargeDto();
			
			if (targetMonthFirstDate.isBefore(freeTrialExpiredDate) || targetMonthFirstDate.isEqual(freeTrialExpiredDate)) {
				// 対象日が無料期間の終了日以前の場合
				
				// 課金開始日として無料期間の終了日の翌日を設定
				LocalDate nextDateOfFreeTrialExpired = DateUtils.getNextDate(freeTrialExpiredDate);
				calcFirstChargeDto.setChargeStartDate(nextDateOfFreeTrialExpired);
			} else {
				// 対象日が無料期間の終了日より後の場合
				
				// 課金開始日として対象日を設定
				calcFirstChargeDto.setChargeStartDate(targetMonthFirstDate);
			}
			calcFirstChargeDto.setCharge(beforeMonthPlanCharge);
			chargeDtos.add(calcFirstChargeDto);
		}
		
		// 真ん中のデータを作成
		for (TPlanHistoryEntity history : newPlanStartMonthHistorys) {
			
			// 履歴データの作成日（履歴のプランの内容が適用された日）
			LocalDate historyCreatedDate = DateUtils.convertLocalDate(history.getHistoryCreatedAt());
			// 履歴のプランの月額料金（日割りを行わない場合の金額）
			BigDecimal historyPlanCharge = history.getChargeAfterNextMonth();
			
			CalcChargeDto calcMiddleChargeDto = new CalcChargeDto();
			
			if (historyCreatedDate.isBefore(freeTrialExpiredDate) || historyCreatedDate.isEqual(freeTrialExpiredDate)) {
				// 履歴作成日が無料期間の終了日以前の場合
				
				// 課金開始日として無料期間の終了日の翌日を設定
				LocalDate nextDateOfFreeTrialExpired = DateUtils.getNextDate(freeTrialExpiredDate);
				calcMiddleChargeDto.setChargeStartDate(nextDateOfFreeTrialExpired);
			} else {
				// 履歴作成日が無料期間の終了日より後の場合
				
				// 課金開始日として履歴作成日を設定
				calcMiddleChargeDto.setChargeStartDate(historyCreatedDate);
			}
			calcMiddleChargeDto.setCharge(historyPlanCharge);
			chargeDtos.add(calcMiddleChargeDto);
		}
		
		//
		// 履歴の登録日が、無料期間の終了日以前となりうる履歴は、
		// 前月の最後の履歴か、当月の最初の履歴しかないため、最後のデータでは、無料期間の終了日については考慮しない。
		//
		
		// 最後のデータを作成
		Long chargeAfterNextMonth = commonPlanService.getSumCharge(newPlanType, newLicenseCount, newStorageCapacity);
		CalcChargeDto calcLastChargeDto = new CalcChargeDto();
		calcLastChargeDto.setChargeStartDate(newPlanStartDate);
		calcLastChargeDto.setCharge(BigDecimal.valueOf(chargeAfterNextMonth));
		chargeDtos.add(calcLastChargeDto);
		
		return chargeDtos;
	}
	
	/**
	 * 計算対象データリストを元に、日割り計算で月額料金を計算する。
	 * 
	 * @param chargeDtos
	 * @return
	 */
	private Long calcMonthChargeByDailyCalculation(List<CalcChargeDto> chargeDtos) {
		
		long DEFAULT_CHARGE = 0L;
		
		if (CollectionUtils.isEmpty(chargeDtos)) {
			return DEFAULT_CHARGE;
		}
		
		BigDecimal monthChargeDecimal = BigDecimal.valueOf(0);
		
		// 最初の要素
		CalcChargeDto firstChargeDto = chargeDtos.get(0);
		
		if (chargeDtos.size() == 1) {
			// 最初の要素しかない場合
			LocalDate firstChargeStartDate = firstChargeDto.getChargeStartDate();
			BigDecimal firstChargeDecimal = firstChargeDto.getCharge();
			Long firstCharge = firstChargeDecimal != null ? firstChargeDecimal.longValue() : 0L;
			
			// 金額が未設定の場合
			if (firstCharge == 0L) {
				return DEFAULT_CHARGE;
			}
			
			boolean isIncludeStartDate = true;
			return commonPlanService.getChargeOfUntilEndOfMonth(firstChargeStartDate, firstCharge, isIncludeStartDate);
		}
		
		// 最後の要素
		CalcChargeDto lastChargeDto = chargeDtos.get(chargeDtos.size() - 1);
		
		// 最初の要素の情報
		LocalDate firstChargeStartDate = firstChargeDto.getChargeStartDate();
		BigDecimal firstChargeDecimal = firstChargeDto.getCharge();
		Long fisrtCharge = firstChargeDecimal != null ? firstChargeDecimal.longValue() : 0L;
		
		// 最後の要素の情報
		LocalDate lastChargeStartDate = lastChargeDto.getChargeStartDate();
		BigDecimal lastChargeDecimal = lastChargeDto.getCharge();
		Long lastCharge = lastChargeDecimal != null ? lastChargeDecimal.longValue() : 0L;
		
		if (!DateUtils.isEqualYearMonth(firstChargeStartDate, lastChargeStartDate)) {
			// 最初の要素と最後の要素の年月が異なる場合
			logger.error("日割り計算をするデータが異なる年月のデータとなっている");
			throw new RuntimeException();
		}
		
		if (chargeDtos.size() == 2) {
			// 最初の要素と最後の要素しかない場合
			
			// 最初の要素の金額
			boolean includeCountStartDate = true;
			boolean includeCountEndDate = false;
			BigDecimal chargeOfStartToEndOfFirst = commonPlanService.getDecimalChargeOfStartToEnd(firstChargeStartDate, lastChargeStartDate, fisrtCharge, includeCountStartDate, includeCountEndDate);
			
			// 最後の要素の日割り金額
			// 最後の要素は月末までの日割りで月末日も課金日に含める
			includeCountEndDate = true;
			LocalDate endDate = DateUtils.getLastDateOfThisMonth(firstChargeStartDate);
			BigDecimal chargeOfStartToEndOfLast = commonPlanService.getDecimalChargeOfStartToEnd(lastChargeStartDate, endDate, lastCharge, includeCountStartDate, includeCountEndDate);
			
			// 合算
			monthChargeDecimal = monthChargeDecimal.add(chargeOfStartToEndOfFirst)
													.add(chargeOfStartToEndOfLast);
			
			// 整数に変換
			return commonPlanCalcService.roudToLong(monthChargeDecimal);
		} else {
			// 最初の要素と最後の要素の間に要素が存在している場合
			
			LocalDate startDate = DateUtils.clone(firstChargeStartDate);
			Long monthlyCharge = fisrtCharge;
			boolean includeCountStartDate = true;
			boolean includeCountEndDate = false;
			
			// 最初の要素を除いた新しいリストを生成
			// （最初の要素はループ処理で不要なため含めない）
			// （元のリストの状態を変更しないようにするため、deepCopyしたリストを生成）
			List<CalcChargeDto> clonedChargeDtos = chargeDtos.stream()
					.skip(1)
					.map(e -> e.clone())
					.collect(Collectors.toList());
			
			// 最初の要素から、最後のひとつ前までの要素の日割り料金を合算
			for (CalcChargeDto chargeDto : clonedChargeDtos) {
				
				// 対象期間の日割り料金
				LocalDate endDate = chargeDto.getChargeStartDate();
				BigDecimal chargeOfStartToEnd = commonPlanService.getDecimalChargeOfStartToEnd(startDate, endDate, monthlyCharge, includeCountStartDate, includeCountEndDate);
				
				// 合算
				monthChargeDecimal = monthChargeDecimal.add(chargeOfStartToEnd);
				
				// 次のループの準備
				startDate = endDate;
				BigDecimal dtoCharge = chargeDto.getCharge();
				monthlyCharge = dtoCharge != null ? dtoCharge.longValue() : 0L;
			}
			
			// 最後の要素の日割り金額を合算
			// 最後の要素は月末までの日割りで月末日も課金日に含める
			includeCountEndDate = true;
			LocalDate endDate = DateUtils.getLastDateOfThisMonth(startDate);
			BigDecimal chargeOfStartToEnd = commonPlanService.getDecimalChargeOfStartToEnd(startDate, endDate, monthlyCharge, includeCountStartDate, includeCountEndDate);
			
			monthChargeDecimal = monthChargeDecimal.add(chargeOfStartToEnd);
			
			// 整数に変換
			return commonPlanCalcService.roudToLong(monthChargeDecimal);
		}
	}
	
	// ▼▼▼▼▼ 状態判定／バリデーション系 ▼▼▼▼▼
	
	/**
	 * プランを既に登録しているかどうかを、自動課金番号の登録状況によって判定する。
	 * <pre>
	 *  自動課金番号が未登録の状態（MTenantMgtEntityの自動課金番号が未登録の状態）となるのは下記の2つの状態の場合のみで、
	 *  それ以外の状態では、ロボットペイメント側に登録されている最新の自動課金情報の自動課金番号を保持している。
	 *  
	 *  ■自動課金番号が未登録となる状態
	 *  ・無料プランの利用時
	 *  ・解約状態の場合（有効期限内、有効期限外を問わない）
	 * </pre>
	 * 
	 * @param autoChargeNumber
	 * @return
	 */
	private boolean isAlreadRegist(BigInteger autoChargeNumber) {

		if (autoChargeNumber == null) {
			// 自動課金番号が登録されていない場合はプランの登録（課金）を行っていない
			return false;
		}

		return true;
	}
	
	/**
	 * 対象の数値が現在の利用アカウント数（ライセンスが有効な状態のアカウント数）より小さいがどうかを判定する
	 * 
	 * @param val
	 * @return true: 小さい場合 false: 等しいか大きい場合
	 */
	private boolean isSmallerThanNowUsingLicenseCount(Long val) {
		
		if (val == null) {
			return true;
		}
		
		Long nowUsingLicenseCount = this.getNowUsingLicenseCount();
		if (nowUsingLicenseCount == null) {
			return false;
		}
		
		int compareResult = val.compareTo(nowUsingLicenseCount);
		if (compareResult == -1) {
			// 引数値の方が値が小さい場合
			return true;
		}
		
		return false;
	}
	
	/**
	 * プランの登録処理が行える状態かどうかを判定
	 * 
	 * @param sessionDto
	 * @param nowPlanInfo
	 * @param now
	 * @return
	 */
	private boolean isRegistableStatus(PlanSettingSessionInfoDto sessionDto, PlanInfo nowPlanInfo, LocalDateTime now) {
		
		PlanStatus nowPlanStatus = nowPlanInfo.getPlanStatus();

		if (nowPlanStatus == PlanStatus.CANCELED) {
			// 現在のステータスが「解約」の場合は、有効期限が過ぎている場合のみ登録処理を可能とする。
			// （「解約」で有効期限が過ぎていない場合は、登録処理ではなく、プランの変更処理を行うため、登録処理は実施不可とする。）
			
			LocalDateTime expiredAt = nowPlanInfo.getExpiredAt();
			
			if (now.isAfter(expiredAt)) {
				// 有効期限が過ぎている場合
				return true;
			}
			
			return false;
			
		} else {
			// 契約ステータスが「解約」以外の場合はまだプランが登録されていない場合は登録可能とする
			return !this.isAlreadRegist(sessionDto);
		}
	}
	
	/**
	 * 解約処理が行える状態かどうかを判定する。
	 * <pre>
	 *  現在の契約ステータスが「有効」ではない場合は、解約処理は行えない。
	 * </pre>
	 * 
	 * @param nowPlanStatus
	 * @return
	 */
	private boolean isExecutableStatusForCancel(PlanStatus nowPlanStatus) {
		
		if (nowPlanStatus != PlanStatus.ENABLED) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 対象の値が現在のライセンス数／ストレージ容量と同じ値かを判定する
	 * 
	 * @param planInfo
	 * @param planType プランタイプ
	 * @param licenseNum ライセンス数
	 * @param storageCapacity ストレージ容量
	 * @return
	 */
	private boolean isSameLicenseAndStorageAsCurrentRegist(PlanInfo planInfo, PlanType planType, Long licenseNum, Long storageCapacity) {

		PlanType currentPlanType = planInfo.getPlanType();
		Long currentLicenseCount = planInfo.getLicenseCount();
		Long currentStorageCapacity = planInfo.getStorageCapacity();

		boolean isSamePlanType = currentPlanType == planType;
		boolean isSameLicenseCount = currentLicenseCount.equals(licenseNum);
		boolean isSameStorageCapacity = currentStorageCapacity.equals(storageCapacity);

		if (isSamePlanType && isSameLicenseCount && isSameStorageCapacity) {
			return true;
		}

		return false;
	}
	
	// ▼▼▼▼▼ 状態判定／バリデーション系（登録/更新を行うトランザクション内で行うバリデーション） ▼▼▼▼▼
	// ※基本的にはコントローラ側で行ったバリデーションを再度行い、トランザクション開始の時点の状態で、本当に処理を実行可能な状態かを検証する
	
	/**
	 * 無料プランの更新処理を実行可能な状態かを検証する。
	 * 実行可能な状態でなければ、例外をスローする。
	 * 
	 * <pre>
	 *  バリデーション処理はController側でも行っているが、
	 *  プラン更新処理と同一トランザクション内で値の検証を行いたいため、本バリデーション処理を行う。
	 *  
	 *  基本的にこのバリデーション処理で検証NGとなるケースは、Controller側での検証処理と本処理のトランザクションの間に別の処理が実行されたことで、
	 *  プランの契約状態が変わってしまったような楽観ロックエラーのケースとなる。
	 * </pre>
	 * 
	 * @param nowPlanInfoInTransaction
	 * @throws AppException 
	 */
	private void validExecutableStatusForFreePlanUpdate(PlanInfo nowPlanInfoInTransaction) throws AppException {
		
		// 無料プラン内容の更新が行える状態かどうかをチェック
		boolean isFreePlanUpdatableStatus = this.isFreePlanUpdatableStatus(nowPlanInfoInTransaction);
		if (!isFreePlanUpdatableStatus) {
			// コントローラで行ったバリデーションの後にプランの状態が変わった場合は楽観ロックエラーとする
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}
	
	/**
	 * 無料期間中のプラン登録処理を実行可能な状態かを検証する。
	 * 実行可能な状態でなければ、例外をスローする。
	 * 
	 * <pre>
	 *  バリデーション処理はController側でも行っているが、
	 *  プラン登録処理と同一トランザクション内で値の検証を行いたいため、本バリデーション処理を行う。
	 *  
	 *  基本的にこのバリデーション処理で検証NGとなるケースは、Controller側での検証処理と本処理のトランザクションの間に別の処理が実行されたことで、
	 *  プランの契約状態が変わってしまったような楽観ロックエラーのケースとなる。
	 * </pre>
	 * <pre>
	 *  無料期間中のプラン登録は、無料期間中にプランを利用していない状態からプラン登録を行う処理で、
	 *  下記の状態の場合のみ実行可能。
	 *  
	 *  ・契約ステータスが「無料」で無料期間中の場合
	 *  
	 *  ※無料期間中では、「無料」以外のステータスは「有効」（既にプランが登録されている状態）しか設定されないため、
	 *   「解約」「変更中」のステータスではこの処理は実行されない。
	 *   （解約を行った場合も、解約ではなく「無料」ステータスに戻すようにし、プラン変更の場合も、停止→登録というステップを行わず、更新を行うためステータスは有効から変わらない。）
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param nowPlanInfoInTransaction
	 * @param now
	 * @throws AppException 
	 */
	private void validExecutableStatusForPlanRegistForDuringFreeTrial(PlanSettingSessionInfoDto sessionDto, PlanInfo nowPlanInfoInTransaction, LocalDateTime now) throws AppException {
		
		// 無料期間中のプラン登録を行える状態かどうかをチェック
		PlanStatus nowPlanStatus = nowPlanInfoInTransaction.getPlanStatus();
		boolean isRegistableStatus = this.isRegistableStatus(sessionDto, nowPlanInfoInTransaction, now);
		if (nowPlanStatus != PlanStatus.FREE || !isRegistableStatus) {
			// コントローラで行ったバリデーションの後にプランの状態が変わった場合は楽観ロックエラーとする
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}
	
	/**
	 * 無料期間終了後のプラン登録処理を実行可能な状態かを検証する。
	 * 実行可能な状態でなければ、例外をスローする。
	 * 
	 * <pre>
	 *  バリデーション処理はController側でも行っているが、
	 *  プラン登録処理と同一トランザクション内で値の検証を行いたいため、本バリデーション処理を行う。
	 *  
	 *  基本的にこのバリデーション処理で検証NGとなるケースは、Controller側での検証処理と本処理のトランザクションの間に別の処理が実行されたことで、
	 *  プランの契約状態が変わってしまったような楽観ロックエラーのケースとなる。
	 * </pre>
	 * <pre>
	 *  この処理は、無料期間終了後にプランを利用していない状態から、プランを利用し始めるためのプランの登録処理で、
	 *  プランを登録していない下記の状態の場合のみ実行可能。
	 *  
	 *  ・有料登録を行わない状態で無料期間が終了した場合（「無料」ステータスで無料期間切れの状態）
	 *  ・解約状態での有効期限切れの場合（※「解約」ステータスでの有効期限内の場合は本処理ではなく、プランの更新処理を行う。）
	 *  
	 *  ※「変更中」ステータスの場合は、有効期限の内外に関わらず、この処理ではなく、プランの更新処理を行う。
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param nowPlanInfoInTransaction
	 * @param now
	 * @throws AppException 
	 */
	private void validExecutableStatusForPlanRegistForStartingUsePlanAfterFreeTrial(PlanSettingSessionInfoDto sessionDto, PlanInfo nowPlanInfoInTransaction, LocalDateTime now) throws AppException {
		
		// 無料期間終了後の（無料or解約の場合の）プラン登録を行える状態かどうかをチェック
		boolean isRegistableStatus = this.isRegistableStatus(sessionDto, nowPlanInfoInTransaction, now);
		if (!isRegistableStatus) {
			// コントローラで行ったバリデーションの後にプランの状態が変わった場合は楽観ロックエラーとする
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}
	
	/**
	 * プランの停止と変更中ステータスへの更新処理を実行可能な状態かを検証する。
	 * 実行可能な状態でなければ、例外をスローする。
	 * <pre>
	 *  バリデーション処理はController側でも行っているが、
	 *  プランの停止と変更中ステータスへの更新処理と同一トランザクション内で値の検証を行いたいため、本バリデーション処理を行う。
	 *  
	 *  基本的にこのバリデーション処理で検証NGとなるケースは、Controller側での検証処理と本処理のトランザクションの間に別の処理が実行されたことで、
	 *  プランの契約状態が変わってしまったような楽観ロックエラーのケースとなる。
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param nowPlanStatusUsedBeforeProc
	 * @param nowPlanInfoInTransaction
	 * @throws AppException
	 */
	private void validExecutableStatusForPlanStopAndChangingStatus(PlanSettingSessionInfoDto sessionDto, PlanStatus nowPlanStatusUsedBeforeProc, PlanInfo nowPlanInfoInTransaction) throws AppException {
		
		PlanStatus nowPlanStatusInTransaction = nowPlanInfoInTransaction.getPlanStatus();
		
		// 引数で受けた前処理で利用した値と、現在のトランザクション内で再取得した値が同じかをチェック
		if (nowPlanStatusInTransaction != nowPlanStatusUsedBeforeProc) {
			// 値が異なる場合は楽観ロックエラーとする
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// ※Controller側で行っている、事前のバリデーション処理と同じ処理だが、
		// Serviceの処理のトランザクション内でも確認をしたいため実施。
		// プラン変更が可能かどうか
		if (!this.isUpdatableStatus(sessionDto, nowPlanInfoInTransaction)) {
			// コントローラで行ったバリデーションの後のコンマ数秒の間に状態が変わったケースなので楽観ロックエラーとする
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}
	
	/**
	 * プラン更新処理の登録処理（プランの停止後に実施する処理）を実行可能な状態かを検証する。
	 * 実行可能な状態でなければ、例外をスローする。
	 * <pre>
	 *  バリデーション処理はControllerやプランの停止処理の実行前にも行っているが、
	 *  処理のトランザクションが別れてしまうため、登録処理のトランザクション内でもバリデーション処理を行う。
	 *  
	 *  基本的にこのバリデーション処理で検証NGとなるケースは、前処理と本処理のトランザクションの間に別の処理が実行されたことで、
	 *  プランの契約状態が変わってしまったような楽観ロックエラーのケースとなる。
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param nowPlanStatusUsedBeforeProc
	 * @param nowPlanInfoInTransaction
	 * @param requestPlanType
	 * @param requestChangeLicenseNum
	 * @param requestStorageCapacity
	 * @throws AppException
	 */
	private void validExecutableStatusForplanRegistAfterStopAndChangingStatus(PlanSettingSessionInfoDto sessionDto,
			PlanStatus nowPlanStatusUsedBeforeProc, PlanInfo nowPlanInfoInTransaction,
			PlanType requestPlanType, Long requestChangeLicenseNum, Long requestStorageCapacity) throws AppException {
		
		PlanStatus nowPlanStatusInTransaction = nowPlanInfoInTransaction.getPlanStatus();
		
		// トランザクション内で取得したステータスの楽観ロックチェック
		if (nowPlanStatusUsedBeforeProc == PlanStatus.CHANGING
				|| nowPlanStatusUsedBeforeProc == PlanStatus.CANCELED) {
			// プランの変更処理を開始する前の時点でのステータス（元のステータス）が、「変更中」か「解約」の場合は、
			// 前処理で契約ステータスの変更は行われないので、本処理のトランザクション内で取得する現在の契約ステータスと、前処理の時点でのステータスは同じ値となるはず。
			
			if (nowPlanStatusUsedBeforeProc != nowPlanStatusInTransaction) {
				// 値が異なる場合は楽観ロックエラーとする
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
		} else {
			// プラン更新処理を開始する前の時点でのステータスが「変更中」、「解約」以外の場合（現状では「有効」のステータスの場合のみ）は、前処理であるプランの停止or変更処理にて、
			// ステータスが「変更中」に更新される。
			
			if (nowPlanStatusInTransaction != PlanStatus.CHANGING) {
				// 現在のステータスが「変更中」ではない場合は楽観ロックエラーとする
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
		}
		
		// ※Controller側で行っている、事前のバリデーション処理と同じ処理だが、
		// Serviceの処理のトランザクション内でも確認をしたいため実施。
		// プラン変更処理
		if (!this.isUpdatableStatus(sessionDto, nowPlanInfoInTransaction)) {
			// コントローラで行ったバリデーションの後のコンマ数秒の間に状態が変わったケースなので楽観ロックエラーとする
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// ※Controller側で行っている、事前のバリデーション処理と同じ処理だが、
		// Serviceの処理のトランザクション内でも確認をしたいため実施。
		if (nowPlanStatusInTransaction == PlanStatus.CHANGING) {
			// ステータスが「変更中」の場合のみチェック
			
			if (!this.isSameLicenseAndStorageAsCurrentRegist(nowPlanInfoInTransaction, requestPlanType, requestChangeLicenseNum, requestStorageCapacity)) {
				// 送信パラメータの値が現在の登録値と同じ値ではない場合（不正な値の場合）はエラーとする
				// ※プランの変更処理が中途半端な状態で終了（エラー）となってしまった場合に「変更中」ステータスとなるが、
				//   変更処理を行うプランタイプ、ライセンス数、ストレージ量はDBに登録されている状態と同じでなけば変更処理は不可とする。
				
				// コントローラで行ったバリデーションの後のコンマ数秒の間にプランの状態が変わった場合のケースなので楽観ロックエラーとする
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
		}
	}
	
	/**
	 * 無料期間中のプランの削除処理を実行可能な状態かを検証する。
	 * 実行可能な状態でなければ、例外をスローする。
	 * <pre>
	 *  バリデーション処理はController側でも行っているが、
	 *  プランの削除処理と同一トランザクション内で値の検証を行いたいため、本バリデーション処理を行う。
	 *  
	 *  基本的にこのバリデーション処理で検証NGとなるケースは、Controller側での検証処理と本処理のトランザクションの間に別の処理が実行されたことで、
	 *  プランの契約状態が変わってしまったような楽観ロックエラーのケースとなる。
	 * </pre>
	 * 
	 * @param nowPlanInfo
	 * @param nowIsDuringTheFreePeriod
	 * @throws AppException
	 */
	private void validExecutableStatusForplanDeleteDuringFreeTrial(PlanInfo nowPlanInfo, boolean nowIsDuringTheFreePeriod) throws AppException {
		
		// 無料期間中のプランの削除が可能な状態かどうか
		if (!this.isExecutableStatusForPlanDeleteDuringFreeTrial(nowPlanInfo, nowIsDuringTheFreePeriod)) {
			// コントローラで行ったバリデーションの後に状態が変わったケースなので楽観ロックエラーとする
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}
	
	/**
	 * プランの解約処理を実行可能な状態かを検証する。
	 * 実行可能な状態でなければ、例外をスローする。
	 * <pre>
	 *  バリデーション処理はController側でも行っているが、
	 *  プランの解約処理と同一トランザクション内で値の検証を行いたいため、本バリデーション処理を行う。
	 *  
	 *  基本的にこのバリデーション処理で検証NGとなるケースは、Controller側での検証処理と本処理のトランザクションの間に別の処理が実行されたことで、
	 *  プランの契約状態が変わってしまったような楽観ロックエラーのケースとなる。
	 * </pre>
	 * 
	 * @param nowPlanStatus
	 * @return
	 * @throws AppException 
	 */
	private void validExecutableStatusForCancel(PlanStatus nowPlanStatus) throws AppException {

		// プランの解約が可能な状態かどうか
		if (!this.isExecutableStatusForCancel(nowPlanStatus)) {
			// コントローラで行ったバリデーションの後に状態が変わったケースなので楽観ロックエラーとする
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}
	
	// ▼▼▼▼▼ その他の処理 ▼▼▼▼▼
	
	/**
	 * 現在時間のUNIXタイムスタンプを取得する
	 * 
	 * @return
	 */
	private long getNowUnixTime() {

		// 現在時間
		long unixTime = System.currentTimeMillis() / 1000L;

		return unixTime;
	}

	/**
	 * ロボットペイメントのAPIをコールするための接続認証情報のハッシュ値を取得
	 * 
	 * @param unixTime
	 * @return
	 */
	private String getConnectAuthHash(long unixTime) {

		// UNIXタイムスタンプ + 接続認証コード
		String hashSrc = unixTime + paymentConnectionAuthCode;

		// ハッシュ化（SHA256）
		String sha256 = DigestUtils.sha256Hex(hashSrc);

		return sha256;
	}

	/**
	 * プランの登録リクエストによって返却されるレスポンス情報をMapに変換する
	 * 
	 * @param responseBody
	 * @return
	 */
	private Map<String, String> toMapGatewayTokenResponse(String responseBody) {

		List<String> resParams = StringUtils.toArray(responseBody);

		Map<String, String> resultMap = new HashMap<>();

		// ロボットペイメントの仕様でパラメータの数と順番が決まっているため、
		// 固定でインデックス番号を指定してMapに設定する。

		resultMap.put("gid", resParams.get(0)); // 決済番号
		resultMap.put("rst", resParams.get(1)); // 決済結果
		resultMap.put("ap", resParams.get(2)); // カード会社承認番号
		String errorConde = resParams.get(3);
		resultMap.put("ec", errorConde); // エラーコード
		
		if (StringUtils.isEmpty(errorConde)) {
			// エラーコードが設定されていない場合（処理が成功した場合）のみその他のデータを追加する
			resultMap.put("god", resParams.get(4)); // オーダーコード
			resultMap.put("cod", resParams.get(5)); // 店舗オーダーコード
			resultMap.put("am", resParams.get(6)); // 決済金額
			resultMap.put("tx", resParams.get(7)); // 税金額
			resultMap.put("sf", resParams.get(8)); // 送料
			resultMap.put("ta", resParams.get(9)); // 合計金額
			resultMap.put("actp", resParams.get(10)); // 課金周期
			resultMap.put("acam", resParams.get(11)); // 自動課金金額
			resultMap.put("acid", resParams.get(12)); // 自動課金番号
			resultMap.put("other", resParams.get(13)); // その他パラメータの連結文字列
		}

		return resultMap;
	}

	/**
	 * プランの更新リクエストによって返却されるレスポンス情報をMapに変換する
	 * 
	 * @param responseBody
	 * @return
	 */
	private Map<String, String> toMapAccgateResponse(String responseBody) {

		List<String> resParams = StringUtils.toArray(responseBody);

		Map<String, String> resultMap = new HashMap<>();

		resultMap.put("result", resParams.get(0)); // APIの実行結果

		return resultMap;
	}

	/**
	 * プランの停止リクエストによって返却されるレスポンス情報をMapに変換する
	 * 
	 * @param responseBody
	 * @return
	 */
	private Map<String, String> toMapAcsgateResponse(String responseBody) {

		List<String> resParams = StringUtils.toArray(responseBody);

		Map<String, String> resultMap = new HashMap<>();

		resultMap.put("result", resParams.get(0)); // APIの実行結果

		return resultMap;
	}

	/**
	 * カード情報/課金情報の変更リクエストによって返却されるレスポンス情報をMapに変換する
	 * 
	 * @param responseBody
	 * @return
	 */
	private Map<String, String> toMapAccgateTokenResponse(String responseBody) {

		List<String> resParams = StringUtils.toArray(responseBody);

		Map<String, String> resultMap = new HashMap<>();

		resultMap.put("result", resParams.get(0)); // APIの実行結果

		return resultMap;
	}
	
	/**
	 * 対象URLへのGETリクエストを送信する
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws AppException
	 */
	private HttpResponse<String> doGetRequest(String url, List<NameValuePair> params) throws AppException {

		HttpResponse<String> response;

		try {
			
			URI uri = new URIBuilder(url)
					.setParameters(params)
					.build();

			HttpRequest request = HttpRequest.newBuilder()
					.uri(uri)
					.GET()
					.build();
			HttpClient httpClient = HttpClient.newBuilder()
					.build();

			response = httpClient.send(request, BodyHandlers.ofString(java.nio.charset.StandardCharsets.UTF_8));

		} catch (URISyntaxException | IOException | InterruptedException e) {

			throw new AppException(null, e);
		}

		return response;
	}

}