package jp.loioz.app.user.planSetting.dto;

import java.util.Map;

import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.TenantFuncSettingConstant.TenantFuncSetting;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PlanSettingSessionInfoDto {
	
	//
	// Sessionテーブルから取得し、設定する値。
	// Dto生成時にのみ設定可能。
	//
	
	/** テナントSEQ */
	private Long tenantSeq;
	
	/** アカウントSEQ */
	private Long accountSeq;
	
	/** プラン設定画面のみアクセス可能かどうか（例外的にSetter付与） */
	@Setter
	private boolean isOnlyPlanSettingAccessible;
	
	//
	// 管理DBのテーブルから取得し、設定する値。
	// Dto生成時にのみ設定可能。
	//
	
	/** テナント用のサブドメイン */
	private String subDomain;
	
	/** テナント用のアクセスURLコンテキストパス */
	private String tenantUrlContextPath;
	
	/** 無料ステータスかどうか */
	private boolean isFreePlanStatus;
	
	/** 無料プランの利用可能日数 */
	private Long daysCountFreePlanAvailable;
	
	/** プランタイプ */
	private PlanType planType;
	
	//
	// テナントDBの各テーブルから取得し、設定する値。
	// Dto生成後でも設定可能。
	// （Dto生成時のみ設定可能としたかったが、Dto生成時のDB向き先変更の設定等の都合でそうできなかったため、
	//   仕方なくDto生成後でも設定可能としてるが、本来は１度だけ値を設定して、以降変更しない用途を想定している。）
	//
	
	/** テナント名 */
	@Setter
	private String tenantName;
	
	/** ログインID */
	@Setter
	private String loginAccountId;

	/** ログインアカウント名 */
	@Setter
	private String loginAccountName;
	
	/** ログインアカウントのメールアドレス */
	@Setter
	private String loginAccountMailAddress;
	
	/** ログインアカウント種別 */
	@Setter
	private AccountType accountType;
	
	/** プランタイプ毎の機能制限情報<br> true:制限あり, false:制限なし */
	@Setter
	private Map<PlanType, Map<PlanFuncRestrict, Boolean>> allPlanFuncRestrictMap;
	
	/** テナント機能設定情報 */
	@Setter
	private Map<TenantFuncSetting, String> tenantFuncSettingMap;
	
	/** ロイオズ管理者制御 */
	@Setter
	private Map<LoiozAdminControl, String> loiozAdminControlMap;
	
	/** sessionのユーザーが弁護士かどうか */
	public boolean isLawyer() {
		return this.accountType == AccountType.LAWYER;
	}
	
	/** プランタイプのタイトルを取得する */
	public String getPlanTypeTitle() {
		return this.planType.getTitle();
	}
	
	/**
	 * プランタイプによる機能制限情報から、指定の制限機能の利用可否を取得する。<br>
	 * ture:利用可能, false:利用不可
	 * 
	 * @param planFuncRestrict
	 * @return
	 */
	public boolean canUsePlanFunc(PlanFuncRestrict planFuncRestrict) {
		
		return this.canUsePlanFunc(this.planType, planFuncRestrict);
	}
	
	/**
	 * 指定のプランタイプの、指定の機能が利用可能かどうかを取得する。
	 * 
	 * @param planType
	 * @param planFuncRestrict
	 * @return
	 */
	public boolean canUsePlanFunc(PlanType planType, PlanFuncRestrict planFuncRestrict) {
		
		// 現在のプランタイプの権限Mapを取得
		Map<PlanFuncRestrict, Boolean> planFuncRestrictMap = this.allPlanFuncRestrictMap.get(planType);
		
		// mapの値はtrueが「制限する」のため、値を反転させて返却
		return !planFuncRestrictMap.get(planFuncRestrict);
	}
	
	/**
	 * テナント機能設定情報の、指定の設定項目の値を取得する
	 * 
	 * @param tenantFuncSetting
	 * @return
	 */
	public String getTenantFuncSettingValue(TenantFuncSetting tenantFuncSetting) {
		return this.tenantFuncSettingMap.get(tenantFuncSetting);
	}
	
	/**
	 * ロイオズ管理者制御情報の、指定の制御項目の値を取得する
	 * 
	 * @param loiozAdminControl
	 * @return
	 */
	public String getLoiozAdminControlValue(LoiozAdminControl loiozAdminControl) {
		return this.loiozAdminControlMap.get(loiozAdminControl);
	}
}
