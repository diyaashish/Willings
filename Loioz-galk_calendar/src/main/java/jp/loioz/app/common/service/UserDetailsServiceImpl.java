package jp.loioz.app.common.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.login.service.LoginService;
import jp.loioz.common.constant.AccountSettingConstant.SettingType;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountStatus;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.SystemType;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.TenantFuncSettingConstant.TenantFuncSetting;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.constant.plan.PlanConstant.PlanStatus;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import jp.loioz.common.dataSource.SchemaContextHolder;
import jp.loioz.common.exception.LoginAccountStatusException;
import jp.loioz.common.exception.LoginTenantPlanStatusException;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.MTenantMgtDao;
import jp.loioz.domain.LoginUserDetails;
import jp.loioz.domain.UriService;
import jp.loioz.domain.value.PersonName;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.MTenantMgtEntity;

/**
 * ログイン認証用のサービスクラス
 */
@Service
public class UserDetailsServiceImpl extends DefaultService implements UserDetailsService {

	/** ログイン処理のエラーメッセージ。※ログイン処理系の他のクラスでも利用するためアクセス修飾子はpublicとしている。 */
	public static final String NOT_FOUND_ACCOUNT_MSG = "アカウントID、パスワードもしくは、\r\nご利用のURLに誤りがないかご確認ください。";

	/** ユーザー情報用のDaoクラス */
	@Autowired
	MAccountDao mAccountDao;

	/** テナント情報のDaoクラス */
	@Autowired
	MTenantDao mTenantDao;

	/** テナント管理用のDaoクラス */
	@Autowired
	MTenantMgtDao mTenantMgtDao;

	/** テナント機能設定の共通サービス */
	@Autowired
	CommonTenantFuncSettingService commonTenantFuncSettingService;
	
	/** アカウント設定の共通サービス */
	@Autowired
	CommonAccountSettingService commonAccountSettingService;
	
	/** ロイオズ管理者制御の共通サービス */
	@Autowired
	CommonLoiozAdminControlService commonLoiozAdminControlService;
	
	/** 契約プランの共通サービス */
	@Autowired
	CommonPlanService commonPlanService;
	
	@Autowired
	UriService uriService;

	@Autowired
	LoginService loginService;

	/**
	 * ユーザー情報の取得
	 *
	 * @param String ユーザー名
	 * @return ユーザー情報
	 */
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

		// サブドメイン名を取得する
		String subDomainName = uriService.getSubDomainName();
		// サブドメイン名からテナント連番を取得
		Long tenantSeq = loginService.getTenantSeq(subDomainName);

		LoginUserDetails loginUserDetails = new LoginUserDetails();
		LocalDateTime now = LocalDateTime.now();

		if (!CommonConstant.ADMIN_TENANT_SEQ.equals(tenantSeq)) {
			// 一般ユーザーのログイン

			if (tenantSeq == null) {
				// アクセスされたサブドメインに対応するテナントが存在しない場合
				// ユーザー情報が存在しないものとして扱う
				throw new UsernameNotFoundException(NOT_FOUND_ACCOUNT_MSG);
			}

			// テナントの情報を取得
			MTenantMgtEntity mTenantMgtEntity = mTenantMgtDao.selectBySeq(tenantSeq);
			PlanStatus planStatusEnum = DefaultEnum.getEnum(PlanStatus.class, mTenantMgtEntity.getPlanStatus());
			PlanType planType = PlanType.of(mTenantMgtEntity.getPlanType());
			LocalDateTime expiredAt = mTenantMgtEntity.getExpiredAt();

			// 対象テナントに接続して事務所情報、アカウント情報を取得
			MTenantEntity tenant = null;
			MAccountEntity user = null;
			try {
				// テナントDBへ接続
				SchemaContextHolder.setTenantSeq(tenantSeq);
				tenant = mTenantDao.selectBySeq(tenantSeq);
				user = mAccountDao.selectById(userName);
				if (user == null) {
					// ユーザー情報が存在しない場合はエラーとする
					throw new UsernameNotFoundException(NOT_FOUND_ACCOUNT_MSG);
				}

				// テナント機能設定情報を設定
				Map<TenantFuncSetting, String> tenantFuncSettingMap = commonTenantFuncSettingService.getSessionSettingMap();
				loginUserDetails.setTenantFuncSettingMap(tenantFuncSettingMap);
				
				// アカウント設定情報を設定
				Map<SettingType, String> accountSettingMap = commonAccountSettingService.getSessionSettingMap(user.getAccountSeq());
				loginUserDetails.setAccountSettingMap(accountSettingMap);

				// プランタイプを設定
				loginUserDetails.setPlanType(planType);
				
				// プランタイプ毎の機能制限情報を設定
				Map<PlanType, Map<PlanFuncRestrict, Boolean>> allPlanFuncRestrictMap = commonPlanService.getAllPlanFuncRestrictMap();
				loginUserDetails.setAllPlanFuncRestrictMap(allPlanFuncRestrictMap);
				
				// ロイオズ管理者制御情報を設定
				Map<LoiozAdminControl, String> loiozAdminControlMap = commonLoiozAdminControlService.getSessionSettingMap();
				loginUserDetails.setLoiozAdminControlMap(loiozAdminControlMap);
				
			} finally {
				// 接続解除
				SchemaContextHolder.clear();
			}

			boolean isManager = AccountKengen.SYSTEM_MNG.equalsByCode(user.getAccountKengen());
			// テナントステータスとアカウント権限によるアクセス制御
			if (planStatusEnum == PlanStatus.STOPPED) {
				// テナントの契約ステータスが停止の場合
				// エラーとする
				throw new LoginTenantPlanStatusException("サービスのご利用が " + planStatusEnum.getVal() + " されているためログインできません");

			} else if (planStatusEnum == PlanStatus.CANCELED) {
				// テナントの契約ステータスが解約の場合

				if (now.isAfter(expiredAt)) {
					// 利用可能期間外の場合

					if (!isManager) {
						// システム管理者以外のユーザーの場合
						// エラーとする
						throw new LoginTenantPlanStatusException("サービスを" + planStatusEnum.getVal() + "しているためログインできません");
					} else {
						// システム管理者の場合
						// プラン設定画面のみアクセス可能とする
						loginUserDetails.setOnlyPlanSettingAccessible(true);
					}
				}
			} else if (planStatusEnum == PlanStatus.CHANGING) {
				// 契約ステータスが「変更中」の場合（プラン変更処理が正常に完了しなかった場合にこのケースとなる）

				if (now.isAfter(expiredAt)) {
					// 利用可能期間外の場合

					if (!isManager) {
						// システム管理者以外のユーザーの場合
						// エラーとする
						throw new LoginTenantPlanStatusException("サービスのご利用内容の変更が完了していないためログインできません");
					} else {
						// システム管理者の場合
						// プラン設定画面のみアクセス可能とする
						loginUserDetails.setOnlyPlanSettingAccessible(true);
					}
				}
			} else if (planStatusEnum == PlanStatus.FREE) {
				// テナントの契約ステータスが無料の場合

				if (now.isAfter(expiredAt)) {
					// 利用可能期間外の場合

					if (!isManager) {
						// システム管理者以外のユーザーの場合
						// エラーとする

						throw new LoginTenantPlanStatusException("無料トライアルが終了しているためログインできません。\r\n"
								+ "利用プランを登録してください。\r\n\r\n"
								+ "※利用プランの登録はシステム管理者のみ行なえます。\r\n"
								+ "【システム管理者】" + this.getTenantSystemMngUsersName(tenantSeq));
					} else {
						// システム管理者の場合
						// プラン設定画面のみアクセス可能とする
						loginUserDetails.setOnlyPlanSettingAccessible(true);
					}
				}
			}

			if (user.getAccountStatus().equals(AccountStatus.DISABLED.getCd())) {
				// ユーザーのアカウントステータスが無効の場合はエラーとする
				throw new LoginAccountStatusException("指定のアカウントの利用は停止されています");
			}

			if (user.getDeletedAt() != null) {
				// ユーザーのアカウントがすでに削除されている場合はエラーとする。
				throw new LoginAccountStatusException("指定したアカウントは既に削除されています");
			}

			// オーナーかどうか
			boolean isOwner = SystemFlg.FLG_ON.equalsByCode(user.getAccountOwnerFlg());

			// エンティティ情報を認証用オブジェクトに変換
			loginUserDetails.setAccountSeq(user.getAccountSeq());
			loginUserDetails.setAccountId(user.getAccountId());
			loginUserDetails.setTenantSeq(user.getTenantSeq());
			loginUserDetails.setSubDomain(mTenantMgtEntity.getSubDomain());
			loginUserDetails.setPassword(user.getPassword());
			loginUserDetails.setAccountName(PersonName.fromEntity(user).getName());
			loginUserDetails.setAccountType(AccountType.of(user.getAccountType()));
			loginUserDetails.setAccountKengen(AccountKengen.of(user.getAccountKengen()));
			loginUserDetails.setMailAddress(user.getAccountMailAddress());
			loginUserDetails.setOwner(isOwner);
			loginUserDetails.setSystemType(SystemType.USER);
			if (tenant != null) {
				// 事務所名を設定
				loginUserDetails.setTenantName(tenant.getTenantName());
			}
			if (planStatusEnum == PlanStatus.FREE) {
				// ログイン以降の画面で表示する「トライアル中」アイコンの内容を表示するために利用するパラメータ
				loginUserDetails.setFreePlanStatus(true);
				loginUserDetails.setFreePlanExpiredAt(expiredAt);
			}

		}

		return loginUserDetails;
	}

	/**
	 * テナントのシステム管理権限を持つユーザーの名前を取得する<br>
	 * （複数名の場合は句読点区切りで取得する）
	 *
	 * @return
	 */
	private String getTenantSystemMngUsersName(Long tenantSeq) {

		List<MAccountEntity> tenantSystemMngUsers = null;

		try {
			// テナントDBへ接続
			SchemaContextHolder.setTenantSeq(tenantSeq);
			tenantSystemMngUsers = mAccountDao.selectEnabledAccountByAccountKengen(CommonConstant.AccountKengen.SYSTEM_MNG.getCd());
		} finally {
			// 接続解除
			SchemaContextHolder.clear();
		}

		StringBuilder sb = new StringBuilder();
		for (MAccountEntity user : tenantSystemMngUsers) {
			sb.append(user.getAccountNameSei());
			sb.append(user.getAccountNameMei());
			sb.append("、");
		}

		// 最後の"、"を削除
		sb.setLength(sb.length() - 1);

		return sb.toString();
	}
}
