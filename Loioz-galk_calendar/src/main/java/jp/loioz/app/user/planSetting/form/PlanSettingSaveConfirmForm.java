package jp.loioz.app.user.planSetting.form;

import jp.loioz.app.user.planSetting.dto.PlanSettingSessionInfoDto;
import jp.loioz.common.constant.plan.PlanConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * プラン設定画面の保存内容確認フォームクラス
 */
@Data
public class PlanSettingSaveConfirmForm {
	
	/** ライセンス数 */
	@Required
	@Numeric
	@MinNumericValue(min = PlanConstant.LICENSE_NUM_MIN)
	@MaxNumericValue(max = PlanConstant.LICENSE_NUM_MAX)
	private String licenseVal;
	
	/** ストレージ容量 */
	@Required
	@Numeric
	@MinNumericValue(min = PlanConstant.STORAGE_CAPA_MIN)
	@MaxNumericValue(max = PlanConstant.STORAGE_CAPA_MAX)
	private String storageVal;
	
	/** 変更前のプランタイプ */
	@Required
	private String planTypeBefore;
	/** 変更後のプランタイプ */
	@Required
	private String planTypeAfter;
	
	// カード情報は、フロントで必須チェックをしており、
	// 表示処理上、空の値が来ても問題ないので特にバリデーションチェックをしていない。
	// （そのまま保存処理に進んだ場合は、そちらでバリデーションNGとなる。）
	
	/** カード：有効期限（年） */
	private String cardExpireYear;
	/** カード：有効期限（月） */
	private String cardExpireMonth;
	/** カード：名義人 */
	private String cardName;
	/** カード：番号（マスキング済み） */
	private String cardNumMasked;
	/** カード：セキュリティコード（マスキング済み） */
	private String cardSecurityCdMasked;
	
	/** 登録モーダルを表示するか */
	private boolean isRegistModal;
	/** 無料プランの更新モーダルを表示するか */
	private boolean isFreePlanUpdateModal;
	/** 利用中のライセンス数より入力値が小さい旨のメッセージを表示するかどうか（プラン登録の場合のみ） */
	private boolean isShowSmallerLicenseMsgForPlanRegist;
	
	/** 確認後に実行するアクション処理のボタンのID */
	private String actionButtonId;
	
	// サーバー側で設定する値
	
	/** 1ライセンスの料金（税抜）（月額） */
	private Long oneLicenseCharge;
	/** ライセンス料（税抜）（月額） */
	private Long licenseCharge;
	/** ストレージ料（税抜）（月額） */
	private Long storageCharge;
	/** 合計金額（税抜）（月額） */
	private Long sumCharge;
	/** 合計金額（月額）の消費税額 */
	private Long taxAmountOfSumCharge;
	
	/** プラン変更により、会計機能が利用不可になるか（trueの場合利用不可） */
	private boolean isChangeUnavailableKaikei;
	
	/** 旧会計管理のデータが存在するか（trueの場合存在する） */
	private boolean isExistsOldKaikeiData;
	
	/** Session情報Dto */
	private PlanSettingSessionInfoDto sessionDto;
	
	/**
	 * 合計金額（税込）を取得する
	 * 
	 * @return
	 */
	public String getSumChargeIncludedTax() {
		// nullは考慮しない
		return String.valueOf(this.sumCharge + this.taxAmountOfSumCharge);
	}
	
	/**
	 * 変更前のプランタイプの表示名を取得する
	 * 
	 * @return
	 */
	public String getPlanTypeBeforeName() {
		PlanType planTypeBeforeEnum = PlanType.of(this.planTypeBefore);
		if (planTypeBeforeEnum == null) {
			return "";
		}
		return planTypeBeforeEnum.getTitle();
	}
	
	/**
	 * 変更後のプランタイプの表示名を取得する
	 * 
	 * @return
	 */
	public String getPlanTypeAfterName() {
		PlanType planTypeAfterEnum = PlanType.of(this.planTypeAfter);
		if (planTypeAfterEnum == null) {
			return "";
		}
		return planTypeAfterEnum.getTitle();
	}
	
	/**
	 * プランタイプが変更されるかどうか
	 * 
	 * @return
	 */
	public boolean isPlanTypeChange() {
		return !this.planTypeBefore.equals(this.planTypeAfter);
	}
	
}
