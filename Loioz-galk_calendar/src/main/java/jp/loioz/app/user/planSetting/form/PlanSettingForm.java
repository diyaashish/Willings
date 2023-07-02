package jp.loioz.app.user.planSetting.form;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.user.planSetting.dto.PlanSettingSessionInfoDto;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.plan.PlanConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanStatus;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import jp.loioz.common.constant.plan.PlanConstant.StoragePrice;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.DigitRange;
import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.dto.PaymentCardDto;
import jp.loioz.dto.PlanInfo;
import lombok.Data;

/**
 * プラン設定画面のフォームクラス
 */
@Data
public class PlanSettingForm {

	// Session情報Dto
	private PlanSettingSessionInfoDto sessionDto;
	
	// ライセンス数のバリデーション
	/** 最小値（ライセンス数） */
	private static final int LICENSE_NUM_MIN = PlanConstant.LICENSE_NUM_MIN;
	/** 最大値（ライセンス数） */
	private static final int LICENSE_NUM_MAX = PlanConstant.LICENSE_NUM_MAX;
	/** 最小値のバリデーションメッセージ（ライセンス数） */
	private static final String LICENSE_NUM_MIN_VALID_MSG = "ライセンス数は" + LICENSE_NUM_MIN + "以上を指定してください";
	/** 最大値のバリデーションメッセージ（ライセンス数） */
	private static final String LICENSE_NUM_MAX_VALID_MSG = "ライセンス数は" + LICENSE_NUM_MAX + "以下を指定してください";

	// ストレージ容量のバリデーション
	/** 最小値（ストレージ容量） */
	private static final int STORAGE_CAPA_MIN = PlanConstant.STORAGE_CAPA_MIN;
	/** 最大値（ストレージ容量） */
	private static final int STORAGE_CAPA_MAX = PlanConstant.STORAGE_CAPA_MAX;
	/** 最小値のバリデーションメッセージ（ストレージ容量） */
	private static final String STORAGE_CAPA_MIN_VALID_MSG = "ストレージ量は" + STORAGE_CAPA_MIN + "以上を指定してください";
	/** 最大値のバリデーションメッセージ（ストレージ容量） */
	private static final String STORAGE_CAPA_MAX_VALID_MSG = "ストレージ量は" + STORAGE_CAPA_MAX + "以下を指定してください";

	// カード情報のバリデーション
	/** カードの有効期限（年）の桁数 */
	private static final int CARD_EXPIRE_YEAR_DIGIT = 2;
	/** カードの有効期限（年）の桁数のバリデーションメッセージ */
	private static final String CARD_EXPIRE_YEAR_DIGIT_VALID_MSG = "カードの有効期限（年）は" + CARD_EXPIRE_YEAR_DIGIT + "桁で指定してください";
	/** カードの有効期限（月）の桁数 */
	private static final int CARD_EXPIRE_MONTH_DIGIT = 2;
	/** カードの有効期限（月）の桁数のバリデーションメッセージ */
	private static final String CARD_EXPIRE_MONTH_DIGIT_VALID_MSG = "カードの有効期限（月）は" + CARD_EXPIRE_MONTH_DIGIT + "桁で指定してください";

	/** ログイン後、初回のプラン設定画面アクセス時にプラン設定画面のみアクセス可能なステータスだったかどうか */
	private boolean firstAccessStatusIsOnlyPlanSettingAccessible = false;
	
	/** 無料期間中かどうか */
	private boolean duringTheFreePeriod = false;
	/** 無料トライアル（無料期間）の期日 */
	private LocalDateTime freeTrialExpiredAt = null;
	
	/** 連携中外部ストレージサービス（連携していない場合はNULL） */
	private ExternalService connectedExternalStorageService = null;

	// ▼ ラベル項目（表示用）
	/** 事務所名 */
	private String tenantName;
	/** サブドメイン */
	private String subDomain;

	/** 現在の契約ステータス */
	private PlanStatus nowPlanStatus;
	/** 現在の利用期限 */
	private LocalDateTime nowExpiredAt;
	/** 現在のプランタイプ */
	private PlanType nowPlanType;
	/** 1ライセンスの料金 */
	private String oneLicenseCharge;
	/** 現在のライセンス数 */
	private String nowLicenseCount;
	/** 現在のライセンス料金（月額） */
	private String nowLicenseCharge;
	/** 現在のストレージ容量 */
	private String nowStorageCapacity;
	/** 現在のストレージ料金（月額） */
	private String nowStorageCharge;
	/** 合計料金（月額） */
	private String nowSumCharge;
	/** 合計料金（月額）の消費税額 */
	private String taxAmountOfNowSumCharge;
	/** 合計料金（月額）の税込み金額 */
	private String nowSumChargeIncludedTax;
	/** 現在のカード番号 */
	private String nowCardNum;
	/** 現在のカードの有効期限（年） */
	private String nowCardExpiredYear;
	/** 現在のカードの有効期限（月） */
	private String nowCardExpiredMonth;
	/** 現在のカードの名義人（姓） */
	private String nowCardLastName;
	/** 現在のカードの名義人（名） */
	private String nowCardFirstName;

	/** 利用中ライセンス数 */
	private Long usingLicenseCount;

	/** カードの有効期限の入力サンプル値（翌年の2月で固定とする※現在の年月より未来日であればよく、翌年や2月などに特に意味はなく、2年後の3月などでもよい） */
	private final LocalDate sampleCardExpiredDate = LocalDate.now().plusYears(1).withMonth(2);
	
	// ▼ 入力項目（hidden）
	/** カード情報のトークン */
	private String token;

	// ▼ 入力項目ーカード情報（マスキングを行っているDBの保存用）
	/** カード番号 */
	@Numeric
	private String cardNumForSave;
	/** カード有効期限（年） */
	@Numeric
	@DigitRange(min = CARD_EXPIRE_YEAR_DIGIT, max = CARD_EXPIRE_YEAR_DIGIT, message = CARD_EXPIRE_YEAR_DIGIT_VALID_MSG)
	private String expireYearForSave;
	/** カード有効期限（月） */
	@Numeric
	@DigitRange(min = CARD_EXPIRE_MONTH_DIGIT, max = CARD_EXPIRE_MONTH_DIGIT, message = CARD_EXPIRE_MONTH_DIGIT_VALID_MSG)
	private String expireMonthForSave;
	/** カード名義（名） */
	private String cardFirstNameForSave;
	/** カード名義（姓） */
	private String cardLastNameForSave;

	// ▼ 入力項目ープラン情報
	/** プランタイプ（プラン情報の登録／更新時） */
	private String planTypeIdForSave;
	
	/** ライセンス数（プラン情報の登録／更新時） */
	@Numeric
	@MinNumericValue(min = LICENSE_NUM_MIN, message = LICENSE_NUM_MIN_VALID_MSG)
	@MaxNumericValue(max = LICENSE_NUM_MAX, message = LICENSE_NUM_MAX_VALID_MSG)
	private String licenseNumForSave;

	/** ストレージ容量（プラン情報の登録／更新時） */
	@Numeric
	@MinNumericValue(min = STORAGE_CAPA_MIN, message = STORAGE_CAPA_MIN_VALID_MSG)
	@MaxNumericValue(max = STORAGE_CAPA_MAX, message = STORAGE_CAPA_MAX_VALID_MSG)
	private String storageCapacityForSave;

	/** プランタイプ（プラン情報取得時） */
	private String planTypeIdForGetPlanCharge;
	
	/** ライセンス数（プラン情報取得時） */
	@Numeric
	@MinNumericValue(min = LICENSE_NUM_MIN, message = LICENSE_NUM_MIN_VALID_MSG)
	@MaxNumericValue(max = LICENSE_NUM_MAX, message = LICENSE_NUM_MAX_VALID_MSG)
	private String licenseNumForGetPlanCharge;

	/** ストレージ容量（プラン情報取得時） */
	@Numeric
	@MinNumericValue(min = STORAGE_CAPA_MIN, message = STORAGE_CAPA_MIN_VALID_MSG)
	@MaxNumericValue(max = STORAGE_CAPA_MAX, message = STORAGE_CAPA_MAX_VALID_MSG)
	private String storageCapacityForGetPlanCharge;
	
	/**
	 * プランタイプの選択肢を取得する
	 * 
	 * @return SelectOptionFormリスト
	 */
	public List<SelectOptionForm> getPlanTypeSelectOptions() {

		List<SelectOptionForm> optionList = new ArrayList<>();
		
		for (PlanType planType : PlanType.values()) {
			SelectOptionForm option = new SelectOptionForm(Integer.valueOf(planType.getId()), planType.getTitle());
			optionList.add(option);
		}
		
		return optionList;
	}
	
	/**
	 * 現在のプランタイプ（this.nowPlanType）のプランタイトルを取得する
	 * 
	 * @return
	 */
	public String getNowPlanTitle() {
		List<SelectOptionForm> planOptions = this.getPlanTypeSelectOptions();
		
		String nowPlanTypeId = this.nowPlanType.getId();
		String nowPlanTypeTitle = "";
		
		for (SelectOptionForm planOption : planOptions) {
			if (planOption.getValue().equals(nowPlanTypeId)) {
				nowPlanTypeTitle = planOption.getLabel();
			}
		}
		
		return nowPlanTypeTitle;
	}
	
	/**
	 * ストレージ量の選択肢を取得する
	 *
	 * @return
	 */
	public List<StoragePrice> getStorageCapacitySelectOptions() {

		return Arrays.asList(StoragePrice.values());
	}

	/**
	 * 「無料トライアル画面」を表示するかどうかをチェック
	 *
	 * @return
	 */
	public boolean isShowFreeStatusPeriodScreen() {

		boolean isShowFreeStatusPeriodScreen = false;

		if (this.nowPlanStatus == PlanStatus.FREE && !this.isShowPlanRegistScreen()) {
			// 無料プラン利用中で、かつ、登録画面を表示しない場合（無料プラン利用中の有効期限内の場合）
			isShowFreeStatusPeriodScreen = true;
		}

		return isShowFreeStatusPeriodScreen;
	}

	/**
	 * 「プランの登録画面」を表示するかどうかをチェック
	 *
	 * @return
	 */
	public boolean isShowPlanRegistScreen() {

		boolean isShowPlanRegistScreen = false;

		if (this.nowExpiredAt == null) {
			// 有効期限がnullのケースは、ステータスが有効な場合 -> 更新画面を表示するため登録画面は表示しない
			return false;
		}

		LocalDateTime now = LocalDateTime.now();
		boolean isExpired = now.isAfter(this.nowExpiredAt);

		if (this.nowPlanStatus == PlanStatus.FREE || this.nowPlanStatus == PlanStatus.CANCELED) {
			// 無料プランの利用中か解約状態の場合

			if (isExpired) {
				// 利用期限が過ぎている場合
				isShowPlanRegistScreen = true;
			}
		}

		return isShowPlanRegistScreen;
	}

	/**
	 * 「現在のプラン画面」を表示するかどうかをチェック
	 *
	 * @return
	 */
	public boolean isShowNowPlanScreen() {

		boolean isShowNowPlanScreen = false;

		if (!this.isShowFreeStatusPeriodScreen() && !this.isShowPlanRegistScreen()) {
			// 無料プランの更新画面、プランの登録画面を表示しない状態の場合は、現在のプラン画面を表示
			isShowNowPlanScreen = true;
		}

		return isShowNowPlanScreen;
	}

	/**
	 * 契約ステータスが「無料」かどうか
	 *
	 * @return
	 */
	public boolean isFreeStatus() {
		return this.nowPlanStatus == PlanStatus.FREE;
	}
	
	/**
	 * 契約ステータスが「利用中（有効）」かどうか
	 *
	 * @return
	 */
	public boolean isEnabledStatus() {
		return this.nowPlanStatus == PlanStatus.ENABLED;
	}

	/**
	 * 契約ステータスが「変更中」かどうか
	 *
	 * @return
	 */
	public boolean isChangingStatus() {
		return this.nowPlanStatus == PlanStatus.CHANGING;
	}

	/**
	 * 契約ステータスが「解約」かどうか
	 *
	 * @return
	 */
	public boolean isCanceledStatus() {
		return this.nowPlanStatus == PlanStatus.CANCELED;
	}

	/**
	 * 現在のプラン情報をフォームに設定する
	 *
	 * @param planInfo
	 */
	public void setNowPlanInfo(PlanInfo planInfo) {

		// 事務所名
		this.tenantName = planInfo.getTenantName();
		// サブドメイン
		this.subDomain = planInfo.getSubDomain();

		// 契約ステータス
		this.nowPlanStatus = planInfo.getPlanStatus();
		// 利用期限
		this.nowExpiredAt = planInfo.getExpiredAt();

		// プランタイプ
		this.nowPlanType = planInfo.getPlanType();
		
		// ライセンス数
		Long licenseCount = planInfo.getLicenseCount();
		if (licenseCount == null) {
			licenseCount = 0L;
		}
		this.nowLicenseCount = String.valueOf(licenseCount);

		// ライセンス料金
		Long licenseCharge = planInfo.getLicenseCharge();
		if (licenseCharge == null) {
			licenseCharge = 0L;
		}
		this.nowLicenseCharge = String.valueOf(licenseCharge);

		// ストレージ容量
		Long storageCapacity = planInfo.getStorageCapacity();
		if (storageCapacity == null) {
			storageCapacity = 0L;
		}
		this.nowStorageCapacity = String.valueOf(storageCapacity);

		// ストレージ料金
		Long storageCharge = planInfo.getStorageCharge();
		if (storageCharge == null) {
			storageCharge = 0L;
		}
		this.nowStorageCharge = String.valueOf(storageCharge);

		// 合計料金
		Long nowSumCharge = licenseCharge + storageCharge;
		this.nowSumCharge = String.valueOf(nowSumCharge);

		// 消費税額
		Long taxAmountOfSumCharge = planInfo.getTaxAmountOfSumCharge();
		this.taxAmountOfNowSumCharge = String.valueOf(taxAmountOfSumCharge);

		// 税込み額
		this.nowSumChargeIncludedTax = String.valueOf(nowSumCharge + taxAmountOfSumCharge);
	}

	/**
	 * 現在の支払いカード情報をフォームに設定する
	 *
	 * @param dto
	 */
	public void setNowCardInfo(PaymentCardDto dto) {

		this.nowCardNum = dto.getCardNumberLast4();
		this.nowCardExpiredYear = dto.getExpiredYear();
		this.nowCardExpiredMonth = dto.getExpiredMonth();
		this.nowCardFirstName = dto.getFirstName();
		this.nowCardLastName = dto.getLastName();
	}

	/**
	 * カード情報を取得する
	 *
	 * @return
	 */
	public PaymentCardDto getCardInfo() {

		PaymentCardDto dto = new PaymentCardDto();

		dto.setCardNumberLast4(this.cardNumForSave);
		dto.setExpiredYear(this.expireYearForSave);
		dto.setExpiredMonth(this.expireMonthForSave);
		dto.setFirstName(this.cardFirstNameForSave);
		dto.setLastName(this.cardLastNameForSave);

		return dto;
	}

	/**
	 * 表示用のカード番号を取得する
	 *
	 * @return
	 */
	public String getNowCardNumForDisplay() {

		if (StringUtils.isEmpty(this.nowCardNum)) {
			return "";
		}

		return "****" + this.nowCardNum;
	}
	
	/**
	 * 無料トライアル（無料期間）の期日を取得する
	 * 
	 * @return
	 */
	public String getFreeTrialExpiredAtStr() {
		LocalDateTime freeTrialExpiredAt = this.freeTrialExpiredAt;
		if (freeTrialExpiredAt == null) {
			return "";
		}
		
		return DateUtils.parseToString(freeTrialExpiredAt, DateUtils.DATE_JP_YYYY_M_D);
	}
	
	/**
	 * 初回課金の年月（無料期間終了日の翌日の年月）を取得する
	 * 
	 * @return
	 */
	public String getFirstChargeYearMonthStr() {
		LocalDateTime freeTrialExpiredAt = this.freeTrialExpiredAt;
		if (freeTrialExpiredAt == null) {
			return "";
		}
		
		LocalDate freeTrialExpiredDate = DateUtils.convertLocalDate(freeTrialExpiredAt);
		LocalDate freeTrialExpiredNextDate = DateUtils.getNextDate(freeTrialExpiredDate);
		
		return DateUtils.parseToString(freeTrialExpiredNextDate, DateUtils.DATE_JP_YYYY_M);
	}
}