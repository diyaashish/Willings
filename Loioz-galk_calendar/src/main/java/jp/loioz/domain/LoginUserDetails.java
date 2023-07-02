package jp.loioz.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jp.loioz.common.constant.AccountSettingConstant.SettingType;
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
import lombok.Data;
import lombok.SneakyThrows;

/**
 * ログインユーザー情報のモデルクラス
 */
@Data
public class LoginUserDetails implements UserDetails, Cloneable {

	private static final long serialVersionUID = 1L;

	/** アカウント情報テーブルの主キー（連番） */
	Long accountSeq;
	/** アカウントID */
	String accountId;
	/** テナント連番 */
	Long tenantSeq;
	/** サブドメイン */
	String subDomain;
	/** パスワード */
	String password;
	/** アカウント名 */
	String accountName;
	/** アカウント種別 */
	AccountType accountType;
	/** アカウント権限 */
	AccountKengen accountKengen;
	/** メールアドレス */
	String mailAddress;
	/** オーナーフラグ */
	boolean isOwner;
	/** システム種別 */
	SystemType systemType;
	/** 事務所名 */
	String tenantName;
	/** プラン設定画面のみアクセス可能かどうか */
	boolean isOnlyPlanSettingAccessible;
	/** 現在の契約プランが「無料」かどうか */
	boolean isFreePlanStatus;
	/** 無料プランの利用期限 */
	LocalDateTime freePlanExpiredAt;

	/** ログイン時の端末種別(このセッションは書き換え禁止) */
	DeviceType loginDeviceType;
	/** 表示する画面種別 */
	ViewType viewType;

	/** テナント機能設定情報 */
	Map<TenantFuncSetting, String> tenantFuncSettingMap = new HashMap<TenantFuncSetting, String>();
	
	/** アカウント設定情報 */
	Map<SettingType, String> accountSettingMap = new HashMap<SettingType, String>();
	
	/** プランタイプ */
	PlanType planType;
	
	/** プランタイプ毎の機能制限情報<br> true:制限あり, false:制限なし */
	Map<PlanType, Map<PlanFuncRestrict, Boolean>> allPlanFuncRestrictMap = new HashMap<>();
	
	/** ロイオズ管理者制御 */
	Map<LoiozAdminControl, String> loiozAdminControlMap = new HashMap<LoiozAdminControl, String>();
	
	/** その他 セッション情報 */
	Map<SessionAttrKeyEnum, String> otherInfoMap = new HashMap<SessionAttrKeyEnum, String>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// ユーザーに付与されている権限情報のコレクションを返却する
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPassword() {
		return this.password;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUsername() {
		return this.accountName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAccountNonExpired() {
		// アカウントの有効期限が切れていない場合はtrueを返却
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAccountNonLocked() {
		// アカウント情報がロックされていない場合はtrueを返却
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		// パスワード情報の有効期限が切れていない場合はtrueを返却
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		// ユーザーが有効な場合はtrueを返却
		return true;
	}

	@SneakyThrows
	@Override
	public LoginUserDetails clone() {
		return (LoginUserDetails) super.clone();
	}
}
