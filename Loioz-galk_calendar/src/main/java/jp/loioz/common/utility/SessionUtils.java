package jp.loioz.common.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jp.loioz.common.constant.AccountSettingConstant.SettingType;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.DeviceType;
import jp.loioz.common.constant.CommonConstant.SystemType;
import jp.loioz.common.constant.CommonConstant.ViewType;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.SessionAttrKeyEnum;
import jp.loioz.common.constant.TenantFuncSettingConstant.TenantFuncSetting;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import jp.loioz.domain.LoginUserDetails;

/**
 * セッション用のUtilクラス
 */
public class SessionUtils {

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * ログアウト状態にする（セッションと認証情報をクリアする。）
	 *
	 * @param session
	 */
	public static void logout(HttpSession session) {
		if (session != null) {
			try {
				// セッションを破棄
				session.invalidate();
			} catch (IllegalStateException ex) {
				// 既にセッションが破棄されている場合 -> 特に何もしない
			}
		}
		// SpringSecurityの認証情報をクリア
		SecurityContextHolder.getContext().setAuthentication(null);
		SecurityContextHolder.clearContext();
	}

	/**
	 * 既にuserにログインしているかを調べる
	 *
	 * @return true:ログインしている、false:ログインしていない
	 */
	public static boolean isAlreadyLoggedUser() {

		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails == null) {
			return false;
		}

		return loginUserDetails.getSystemType() == SystemType.USER;
	}

	/**
	 * ログインのアカウント連番を取得
	 *
	 * @return ログインアカウントのシーケンス番号
	 */
	public static Long getLoginAccountSeq() {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return null;
		}
		return userDetails.getAccountSeq();
	}

	/**
	 * ログインアカウントIDを取得
	 *
	 * @return ログインアカウントIDを返します。
	 */
	public static String getLoginAccountId() {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return "";
		}
		return userDetails.getAccountId();
	}

	/**
	 * ログインアカウント名を取得
	 *
	 * @return ログインアカウント名を返します。
	 */
	public static String getLoginAccountName() {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return "";
		}
		return userDetails.getAccountName();
	}

	/**
	 * ログインアカウントのテナント連番を取得
	 *
	 * @return ログインアカウントのテナント連番を返します。
	 */
	public static Long getTenantSeq() {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return null;
		}
		return userDetails.getTenantSeq();
	}

	/**
	 * ログインアカウントのサブドメインを取得
	 *
	 * @return ログインアカウントのサブドメインを返します。
	 */
	public static String getSubDomain() {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return null;
		}
		return userDetails.getSubDomain();
	}

	/**
	 * ログインアカウントのアカウント種別を取得
	 *
	 * @return ログインアカウントのアカウント種別を返します。
	 */
	public static AccountType getAccountType() {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return null;
		}
		return userDetails.getAccountType();
	}

	/**
	 * ログインアカウントのアカウント種別が弁護士かどうかを判定する
	 *
	 * @return true:弁護士 / false:弁護士以外
	 */
	public static boolean isLawyer() {
		return SessionUtils.getAccountType() == AccountType.LAWYER;
	}

	/**
	 * ログインアカウントのアカウント種別が事務職員かどうかを判定する
	 *
	 * @return true:事務職員 / false:事務職員以外
	 */
	public static boolean isJimu() {
		return SessionUtils.getAccountType() == AccountType.JIMU;
	}

	/**
	 * ログインアカウントのアカウント権限を取得
	 *
	 * @return ログインアカウントのアカウント種別を返します。
	 */
	public static AccountKengen getAccountKengen() {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return null;
		}
		return userDetails.getAccountKengen();
	}

	/**
	 * ログインアカウントのアカウント権限が一般かどうかを判定する
	 *
	 * @return true:一般 / false:一般以外
	 */
	public static boolean isGeneral() {
		return SessionUtils.getAccountKengen() == AccountKengen.GENERAL;
	}

	/**
	 * ログインアカウントのアカウント権限がシステム管理者かどうかを判定する
	 *
	 * @return true:テナントシステム管理者 / false:テナントシステム管理者
	 */
	public static boolean isSystemMng() {
		return SessionUtils.getAccountKengen() == AccountKengen.SYSTEM_MNG;
	}

	/**
	 * ログインアカウントがオーナー権限を持っているかどうかを判定する
	 *
	 * @return true:オーナー権限あり / false:オーナー権限なし
	 */
	public static boolean isOwner() {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return false;
		}

		return userDetails.isOwner();
	}

	/**
	 * ログインアカウントがシステム管理権限を持っているかどうかを判定する
	 *
	 * @return true:システム管理権限あり / false:システム管理権限なし
	 */
	public static boolean isManager() {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return false;
		}

		return CommonConstant.AccountKengen.SYSTEM_MNG == userDetails.getAccountKengen();
	}

	/**
	 * ログインアカウントのメールアドレスを取得する
	 *
	 * @return true:オーナー権限あり / false:オーナー権限なし
	 */
	public static String getMailAddress() {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return null;
		}

		return userDetails.getMailAddress();
	}

	/**
	 * ログインしているシステムのシステム種別を取得
	 *
	 * @return ログインしているシステムのシステム種別を取得
	 */
	public static SystemType getSystemType() {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return null;
		}
		return userDetails.getSystemType();
	}
	
	/**
	 * ログインアカウントの事務所名を取得
	 *
	 * @return ログインアカウントの事務所名を取得
	 */
	public static String getTenantName() {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return null;
		}
		return userDetails.getTenantName();
	}

	/**
	 * プラン設定画面以外の画面へのアクセス許可値を取得
	 *
	 * @return
	 */
	public static Boolean isOnlyPlanSettingAccessible() {
		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return null;
		}
		return userDetails.isOnlyPlanSettingAccessible();
	}

	/**
	 * 現在のプランが無料プランかどうかの判定値を取得
	 *
	 * @return
	 */
	public static Boolean isFreePlanStatus() {
		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return null;
		}
		return userDetails.isFreePlanStatus();
	}

	/**
	 * 現在日時を基準として、無料プランの利用可能日数を取得する
	 *
	 * @return
	 */
	public static Long getDaysCountFreePlanAvailable() {
		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return null;
		}

		LocalDate expiredDate = DateUtils.convertLocalDate(userDetails.getFreePlanExpiredAt());
		// 無料プランの利用期限が過ぎている場合は、無料プランの利用可能日数は0となる。
		Long dayCountStartToEnd = DateUtils.getDaysCountFromNow(expiredDate);

		return dayCountStartToEnd;
	}

	/**
	 * プラン設定画面以外の画面へのアクセス許可値を更新する
	 *
	 * @param isOnlyPlanSettingAccessible
	 */
	public static void setOnlyPlanSettingAccessible(boolean isOnlyPlanSettingAccessible) {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return;
		}
		userDetails.setOnlyPlanSettingAccessible(isOnlyPlanSettingAccessible);
	}

	/**
	 * 現在のプランが無料プランかどうかの判定値を更新する
	 *
	 * @param isFreePlanStatus
	 */
	public static void setFreePlanStatus(boolean isFreePlanStatus) {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return;
		}
		userDetails.setFreePlanStatus(isFreePlanStatus);
	}

	/**
	 * 無料プランの利用期限の値を更新する
	 *
	 * @param freePlanExpiredAt
	 */
	public static void setFreePlanExpiredAt(LocalDateTime freePlanExpiredAt) {

		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return;
		}
		userDetails.setFreePlanExpiredAt(freePlanExpiredAt);
	}

	/**
	 * テナント機能設定情報の、指定の設定項目の値を取得する
	 * 
	 * @param tenantFuncSetting
	 * @return
	 */
	public static String getTenantFuncSettingValue(TenantFuncSetting tenantFuncSetting) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails == null) {
			return null;
		}
		return loginUserDetails.getTenantFuncSettingMap().get(tenantFuncSetting);
	}
	
	/**
	 * テナント機能設定情報の、指定の設定項目の値を設定（更新）する
	 * 
	 * @param tenantFuncSetting
	 * @param funcSettingValue
	 */
	public static void setTenantFuncSettingValue(TenantFuncSetting tenantFuncSetting, String funcSettingValue) {
		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return;
		}
		userDetails.getTenantFuncSettingMap().put(tenantFuncSetting, funcSettingValue);
	}
	
	/**
	 * アカウント設定情報の、指定の設定項目の値を取得する
	 * 
	 * @param settingType
	 * @return
	 */
	public static String getAccountSettingValue(SettingType settingType) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails == null) {
			return null;
		}
		return loginUserDetails.getAccountSettingMap().get(settingType);
	}
	
	/**
	 * アカウント設定情報の、指定の設定項目の値を設定（更新）する
	 * 
	 * @param settingType
	 * @param settingVlue
	 */
	public static void setAccountSettingValue(SettingType settingType, String settingVlue) {
		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return;
		}
		userDetails.getAccountSettingMap().put(settingType, settingVlue);
	}
	
	/**
	 * ロイオズ管理者制御情報の、指定の制御項目の値を取得する
	 * 
	 * @param loiozAdminControl
	 * @return
	 */
	public static String getLoiozAdminControlValue(LoiozAdminControl loiozAdminControl) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails == null) {
			return null;
		}
		return loginUserDetails.getLoiozAdminControlMap().get(loiozAdminControl);
	}
	
	/**
	 * プランタイプの値を取得する
	 * 
	 * @param settingType
	 * @return
	 */
	public static PlanType getPlanType() {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails == null) {
			return null;
		}
		return loginUserDetails.getPlanType();
	}
	
	/**
	 * プランタイプのタイトルを取得する
	 * 
	 * @return
	 */
	public static String getPlanTypeTitle() {
		PlanType planType = SessionUtils.getPlanType();
		if (planType == null) {
			return "";
		}
		return planType.getTitle();
	}
	
	/**
	 * プランタイプによる機能制限情報から、指定の制限機能の利用可否を取得する。<br>
	 * ture:利用可能, false:利用不可
	 * 
	 * @param planFuncRestrict
	 * @return
	 */
	public static boolean canUsePlanFunc(PlanFuncRestrict planFuncRestrict) {
		LoginUserDetails userDetails = getLoginUserDetails();
		if (userDetails == null) {
			return false;
		}
		
		// 現在のプランタイプの権限Mapを取得
		Map<PlanFuncRestrict, Boolean> planFuncRestrictMap = userDetails.getAllPlanFuncRestrictMap().get(userDetails.getPlanType());
		
		// mapの値はtrueが「制限する」のため、値を反転させて返却
		return !planFuncRestrictMap.get(planFuncRestrict);
	}
	
	/**
	 * プランタイプの再設定
	 *
	 * @param planType
	 */
	public static void setPlanType(PlanType planType) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails != null) {
			loginUserDetails.setPlanType(planType);
		}
	}
	
	/**
	 * セッション情報からKEY名で取得する
	 *
	 * @param enumKey
	 * @return
	 */
	public static String getSessionVaule(SessionAttrKeyEnum enumKey) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails == null) {
			return null;
		}
		return loginUserDetails.getOtherInfoMap().get(enumKey);
	}

	/**
	 * セッション情報にセットする
	 *
	 * @param enumKey
	 * @param value
	 */
	public static void setSessionVaule(SessionAttrKeyEnum enumKey, String value) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails != null) {
			loginUserDetails.getOtherInfoMap().put(enumKey, value);
		}
	}

	/**
	 * アカウント名の再設定
	 *
	 * @param accountName
	 */
	public static void setAccountId(String accountId) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails != null) {
			loginUserDetails.setAccountName(accountId);
		}
	}

	/**
	 * パスワードの再設定
	 *
	 * @param password
	 */
	public static void setPassword(String password) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails != null) {
			loginUserDetails.setPassword(password);
		}
	}

	/**
	 * アカウント名の再設定
	 *
	 * @param accountName
	 */
	public static void setAccountName(String accountName) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails != null) {
			loginUserDetails.setAccountName(accountName);
		}
	}

	/**
	 * アカウント種別の再設定
	 *
	 * @param accountType
	 */
	public static void setAccountType(AccountType accountType) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails != null) {
			loginUserDetails.setAccountType(accountType);
		}
	}

	/**
	 * アカウント権限の再設定
	 *
	 * @param accountKengen
	 */
	public static void setAccountKengen(AccountKengen accountKengen) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails != null) {
			loginUserDetails.setAccountKengen(accountKengen);
		}
	}

	/**
	 * アカウントメールアドレスの再設定
	 *
	 * @param accountType
	 */
	public static void setMailAddress(String accountMailAddress) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails != null) {
			loginUserDetails.setMailAddress(accountMailAddress);
		}
	}

	/**
	 * オーナー権限の再設定
	 *
	 * @param accountType
	 */
	public static void setOwner(boolean isOwner) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails != null) {
			loginUserDetails.setOwner(isOwner);
		}
	}
	
	/**
	 * 事務所名の再設定
	 *
	 * @param accountType
	 */
	public static void setTenantName(String tenantName) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails != null) {
			loginUserDetails.setTenantName(tenantName);
		}
	}

	/**
	 * ロケールを取得する
	 *
	 * @return 固定で日本のロケールを返します。
	 */
	public static Locale getLocale() {
		return Locale.JAPANESE;
	}

	/**
	 * ログイン時の端末種別を取得する
	 */
	public static DeviceType getDeviceType() {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails == null) {
			return null;
		}
		return loginUserDetails.getLoginDeviceType();
	}

	/**
	 * ログイン時の端末種別を設定する ※ 注意: ログイン成功時以外では使用しないこと(ログイン後に端末種類が変わることは考慮しない)
	 *
	 * @param viewType
	 */
	public static void setDeviceType(DeviceType deviceType) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails != null) {
			loginUserDetails.setLoginDeviceType(deviceType);
		}
	}

	/**
	 * 画面表示種別を取得する
	 */
	public static ViewType getViewType() {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails == null) {
			return null;
		}
		return loginUserDetails.getViewType();
	}

	/**
	 * 画面表示種別を再設定する
	 *
	 * @param viewType
	 */
	public static void setViewType(ViewType viewType) {
		LoginUserDetails loginUserDetails = getLoginUserDetails();
		if (loginUserDetails != null) {
			loginUserDetails.setViewType(viewType);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 認証情報を取得
	 *
	 * @return Authentication
	 */
	private static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * ログインユーザー情報を取得
	 *
	 * @return ログインユーザー情報
	 */
	private static LoginUserDetails getLoginUserDetails() {
		Authentication authentication = getAuthentication();
		if (authentication == null) {
			return null;
		}
		LoginUserDetails userDetails;
		try {
			userDetails = (LoginUserDetails) authentication.getPrincipal();
		} catch (ClassCastException e) {
			return null;
		}
		return userDetails;
	}

}
