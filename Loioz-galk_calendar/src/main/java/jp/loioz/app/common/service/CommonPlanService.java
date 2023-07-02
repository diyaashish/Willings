package jp.loioz.app.common.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.CommonConstant.StorageUnit;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.constant.plan.PlanConstant.PlanStatus;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import jp.loioz.common.constant.plan.PlanConstant.StoragePrice;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MPlanFuncRestrictDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.MTenantMgtDao;
import jp.loioz.dao.TFileConfigurationManagementDao;
import jp.loioz.dao.TPaymentCardDao;
import jp.loioz.dao.TPlanHistoryDao;
import jp.loioz.dto.PaymentCardDto;
import jp.loioz.dto.PlanInfo;
import jp.loioz.entity.MPlanFuncRestrictEntity;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.MTenantMgtEntity;
import jp.loioz.entity.TPaymentCardEntity;
import jp.loioz.entity.TPlanHistoryEntity;

/**
 * 契約プランの共通サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonPlanService extends DefaultService {

	/** テナント管理用のDaoクラス */
	@Autowired
	private MTenantMgtDao mTenantMgtDao;

	/** テナント用のDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;
	
	/** アカウント用のDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 支払いカードのDaoクラス */
	@Autowired
	private TPaymentCardDao tPaymentCardDao;

	/** 利用プラン履歴のDaoクラス */
	@Autowired
	private TPlanHistoryDao tPlanHistoryDao;
	
	/** プラン別機能制限のDaoクラス */
	@Autowired
	private MPlanFuncRestrictDao mPlanFuncRestrictDao;
	
	/** ファイル構成管理のDaoクラス */
	@Autowired
	private TFileConfigurationManagementDao tFileConfigurationManagementDao;

	/** 契約プランの計算処理共通サービス */
	@Autowired
	private CommonPlanCalcService commonPlanCalcService;

	/** 税金に関する処理の共通サービス */
	@Autowired
	private CommonTaxService commonTaxService;

	/** ファイル管理関連の共通サービス */
	@Autowired
	private CommonFileManagementService commonFileManagementService;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 現在の契約プラン情報を取得する
	 * 
	 * <pre>
	 *  ※当月にプラン変更を行っている場合、返却値のオブジェクトが保持するautoChargeNumber（自動課金番号）は、
	 *    当月末の課金を実行する自動課金の番号ではなく、翌月以降の課金を行う自動課金の番号になる。
	 * </pre>
	 *
	 * @param tenantSeq
	 * @return
	 */
	public PlanInfo getNowPlanInfo(Long tenantSeq) {

		PlanInfo planInfo = new PlanInfo();
		
		// テナントDBのテーブル
		MTenantEntity tenantEntity = mTenantDao.selectBySeq(tenantSeq);
		// 事務所名
		planInfo.setTenantName(tenantEntity.getTenantName());

		// 管理DBのテーブル
		MTenantMgtEntity mTenantMgtEntity = mTenantMgtDao.selectBySeq(tenantSeq);
		
		// サブドメイン
		planInfo.setSubDomain(mTenantMgtEntity.getSubDomain());

		// ステータス
		PlanStatus planStatus = DefaultEnum.getEnum(PlanStatus.class, mTenantMgtEntity.getPlanStatus());
		planInfo.setPlanStatus(planStatus);
		// 利用期限
		planInfo.setExpiredAt(mTenantMgtEntity.getExpiredAt());

		// プランタイプ
		PlanType planType = PlanType.of(mTenantMgtEntity.getPlanType());
		planInfo.setPlanType(planType);
		
		// 1ライセンスの料金
		planInfo.setOneLicenseCharge(Long.valueOf(planType.getLicensePrice().getPrice()));
		
		// ライセンス
		Long licenseCount = mTenantMgtEntity.getLicenseCount();
		planInfo.setLicenseCount(licenseCount);
		planInfo.setLicenseCharge(this.getLicenseCharge(planType, licenseCount));

		// ストレージ
		Long storageCapacity = mTenantMgtEntity.getStorageCapacity();
		planInfo.setStorageCapacity(storageCapacity);
		planInfo.setStorageCharge(this.getStorageCharge(storageCapacity));

		// 合計金額
		if (planStatus != PlanStatus.FREE) {
			// 無料プラン以外の場合
			planInfo.setSumCharge(planInfo.getLicenseCharge() + planInfo.getStorageCharge());
		} else {
			// 無料プランの場合(金額を無料とする)
			planInfo.setSumCharge(0L);
		}

		// 消費税額
		LocalDate now = LocalDate.now();
		Long taxAmount = commonTaxService.calcTaxAmount(planInfo.getSumCharge(), now);
		planInfo.setTaxAmountOfSumCharge(taxAmount);

		// 自動課金番号
		planInfo.setAutoChargeNumber(mTenantMgtEntity.getAutoChargeNumber());

		return planInfo;
	}

	/**
	 * 現在が無料期間中かどうかを判定する
	 * 
	 * @param tenantSeq
	 * @return
	 */
	public boolean nowIsDuringTheFreePeriod(Long tenantSeq) {
		
		LocalDateTime freePlanExpiredAt = this.getFreePlanExpiredAt(tenantSeq);
		
		if (freePlanExpiredAt == null) {
			return false;
		}
		
		LocalDateTime now = LocalDateTime.now();
		
		return now.isBefore(freePlanExpiredAt) || now.isEqual(freePlanExpiredAt);
	}
	
	/**
	 * 無料期間の期限を取得する
	 * 
	 * @param tenantSeq
	 * @return
	 */
	public LocalDateTime getFreePlanExpiredAt(Long tenantSeq) {
		
		PlanInfo nowPlanInfo = this.getNowPlanInfo(tenantSeq);
		
		if (nowPlanInfo.getPlanStatus() == PlanStatus.FREE) {
			// 現在の契約ステータスが「無料」の場合 -> 現在のプラン情報の有効期限を返却
			return nowPlanInfo.getExpiredAt();
		} else {
			// 現在の契約ステータスが「無料」以外の場合
			// -> 現在のプラン情報としては、無料プランの利用期限を保持していないため、プラン履歴情報から無料期間を取得する
			TPlanHistoryEntity tPlanHistoryEntity = tPlanHistoryDao.selectFreePlanHistory();
			return tPlanHistoryEntity.getExpiredAt();
		}
	}
	
	/**
	 * 現在（引数情報）が無料ステータスでの無料期間中の状態かどうかを判定する
	 * 
	 * @param nowStatus
	 * @param now
	 * @param freeTrialExpiredAt
	 * @return
	 */
	public boolean nowIsFreeStatusAndFreeTrial(LocalDate now, PlanStatus nowStatus, LocalDateTime nowStatusExpiredAt) {
		if (nowStatus == PlanStatus.FREE) {
			boolean nowIsFreeTrial = this.targetDateWithinExpiredAt(now, nowStatusExpiredAt);
			if (nowIsFreeTrial) {
				// 無料ステータスでの無料期間中の場合
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 対象の日付が、指定の有効期限内かどうかを判定する
	 * 
	 * @param targetDate 有効期限内かどうかを調べたい日付
	 * @param expiredAt 有効期限
	 * @return
	 */
	public boolean targetDateWithinExpiredAt(LocalDate targetDate, LocalDateTime expiredAt) {
		// プラン設定の有効期限（無料期間の終了期限、解約期間の終了期限、変更中期間の終了期限）の時間設定は
		// 23:59になっているため日付のみでも期限内かどうかの判定が可能
		LocalDate expiredDate = DateUtils.convertLocalDate(expiredAt);
		boolean targetDateWithinExpiredAt = targetDate.isBefore(expiredDate) || targetDate.isEqual(expiredDate);
		
		return targetDateWithinExpiredAt;
	}
	
	/**
	 * 対象の履歴が無料期間中に登録された履歴（=無料期間中のプラン登録の履歴）かどうかを判定する<br>
	 * ※trueが返却される場合、targetHistoryの履歴は、プラン履歴テーブルの２レコード目の履歴であり、プランステータスは必ず「有効」となる
	 * 
	 * @param targetHistory
	 * @param freeTrialExpiredAt
	 * @return
	 */
	public boolean targetHistoryRegistDuringFreeTrial(TPlanHistoryEntity targetHistory, LocalDateTime freeTrialExpiredAt) {
		
		PlanStatus targetHistoryStatus = DefaultEnum.getEnum(PlanStatus.class, targetHistory.getPlanStatus());
		if (targetHistoryStatus == PlanStatus.FREE) {
			// 対象の履歴が無料トライアルの履歴（1レコード目の履歴）の場合は除外する
			return false;
		}
		
		// 履歴の登録日
		LocalDateTime historyCreatedAt = targetHistory.getHistoryCreatedAt();
		
		if (historyCreatedAt.isBefore(freeTrialExpiredAt) || historyCreatedAt.isEqual(freeTrialExpiredAt)) {
			// 履歴が無料期間中に登録されている場合（=対象の履歴が無料期間中のプラン登録の履歴の場合）
			
			return true;
		} else {
			// 履歴が無料期間終了後に登録されている場合（無料期間終了後の、プラン登録、プラン変更などの履歴）
			
			return false;
		}
	}
	
	/**
	 * 現在の契約ステータスを取得する
	 *
	 * @param tenantSeq
	 * @return
	 */
	public PlanStatus getNowPlanStatus(Long tenantSeq) {

		PlanInfo nowPlanInfo = this.getNowPlanInfo(tenantSeq);

		return nowPlanInfo.getPlanStatus();
	}

	/**
	 * 現在が無料ストレージプランかどうかを判定する
	 * 
	 * <pre>
	 *  プラン状況に関係なく、ストレージ上限によって判別する。
	 *  15GBの場合はtrue, それ以外はfalse
	 * </pre>
	 *
	 * @param tenantSeq
	 * @return
	 */
	public boolean nowIsFreeStorage(Long tenantSeq) {

		PlanInfo nowPlanInfo = this.getNowPlanInfo(tenantSeq);
		StoragePrice storageCapacityPrice = StoragePrice.valueOf(nowPlanInfo.getStorageCapacity().intValue());
		if (storageCapacityPrice == null) {
			throw new IllegalArgumentException("存在しないストレージ量：" + nowPlanInfo.getStorageCapacity());
		}

		if (StoragePrice.STORAGE_15 == storageCapacityPrice) {
			// 15GB以外の場合は無料
			return true;
		} else {
			// 15GBの場合は有料
			return false;
		}
	}

	/**
	 * 現在支払いに使用しているカード情報を取得する
	 *
	 * @return
	 */
	public PaymentCardDto getNowCardInfo() {

		PaymentCardDto dto = new PaymentCardDto();

		TPaymentCardEntity entity = tPaymentCardDao.selectOne();

		if (entity == null) {
			dto.setCardNumberLast4("");
			dto.setExpiredYear("");
			dto.setExpiredMonth("");
			dto.setFirstName("");
			dto.setLastName("");
		} else {
			dto.setCardNumberLast4(entity.getCardNumberLast4());
			dto.setExpiredYear(entity.getExpiredYear());
			dto.setExpiredMonth(entity.getExpiredMonth());
			dto.setFirstName(entity.getFirstName());
			dto.setLastName(entity.getLastName());
		}

		return dto;
	}

	/**
	 * ライセンス料金を取得する
	 *
	 * @param planType
	 * @param licenseCount
	 * @return ライセンスの合計金額（税抜）（単位：円）
	 */
	public Long getLicenseCharge(PlanType planType, Long licenseCount) {

		if (licenseCount == null || licenseCount < 0) {
			return 0L;
		}
		
		Long licenseChargePerUnit = Long.valueOf(planType.getLicensePrice().getPrice());

		return licenseChargePerUnit * licenseCount;
	}

	/**
	 * ストレージ料金を取得する
	 *
	 * @param storageCapacity
	 * @return ストレージの合計金額（税抜）（単位：円）
	 */
	public Long getStorageCharge(Long storageCapacity) {

		if (storageCapacity == null || storageCapacity < 0) {
			return 0L;
		}

		StoragePrice storageCapacityPrice = StoragePrice.valueOf(storageCapacity.intValue());
		if (storageCapacityPrice == null) {
			throw new IllegalArgumentException("存在しないストレージ量：" + storageCapacity);
		}
		
		return Long.valueOf(storageCapacityPrice.getPrice());
	}

	/**
	 * ライセンス料とストレージ料の合計金額を取得する
	 *
	 * @param planType
	 * @param licenseCount
	 * @param storageCapacity
	 * @return ライセンスとストレージの合計金額（税抜）（単位：円）
	 */
	public Long getSumCharge(PlanType planType, Long licenseCount, Long storageCapacity) {
		
		Long licenseCharge = this.getLicenseCharge(planType, licenseCount);
		Long storageCharge = this.getStorageCharge(storageCapacity);

		return licenseCharge + storageCharge;
	}

	/**
	 * 現在利用可能なライセンス数を取得する
	 *
	 * @param tenantSeq
	 * @return
	 */
	public Long getNowUseableLicenseCount(Long tenantSeq) {

		PlanInfo planInfo = this.getNowPlanInfo(tenantSeq);

		return planInfo.getLicenseCount();
	}

	/**
	 * 現在の利用中ライセンス数を取得する
	 *
	 * @return
	 */
	public Long getNowUsingLicenseCount() {

		return mAccountDao.selectUsingLicenseAccountCount();
	}

	/**
	 * 現在の利用中ストレージ量を取得する
	 *
	 * @return 利用中の容量（GB）
	 */
	public double getNowUsingStorageCapacity() {

		// バイト単位
		Long usingFileStrage = tFileConfigurationManagementDao.getUsingFileStrage();
		if (usingFileStrage == null) {
			return 0;
		}

		// GB単位の値に変換
		double usingFileStrageGB = commonFileManagementService.storageUnitChangeTo(StorageUnit.GIGA, usingFileStrage);

		return usingFileStrageGB;
	}

	/**
	 * 対象日から月末までの日割りの合計料金を取得する（Long型）
	 *
	 * @param startDate 対象日（開始日）
	 * @param monthlyCharge 月額料金
	 * @param isIncludeCountOfStartDate 対象日の料金を含むかどうか（true: 含む）
	 * @return
	 */
	public Long getChargeOfUntilEndOfMonth(LocalDate startDate, Long monthlyCharge, boolean isIncludeCountOfStartDate) {

		Long chargeOfUntilEndOfMonth = null;

		BigDecimal decimalChargeOfUntilEndOfMonth = this.getDecimalChargeOfUntilEndOfMonth(startDate, monthlyCharge, isIncludeCountOfStartDate);
		chargeOfUntilEndOfMonth = commonPlanCalcService.roudToLong(decimalChargeOfUntilEndOfMonth);

		return chargeOfUntilEndOfMonth;
	}
	
	/**
	 * 対象日から月末までの日割りの合計料金を取得する（BigDecimal型）
	 *
	 * @param startDate 対象日（開始日）
	 * @param monthlyCharge 月額料金
	 * @param isIncludeCountOfStartDate 対象日の料金を含むかどうか（true: 含む）
	 * @return
	 */
	public BigDecimal getDecimalChargeOfUntilEndOfMonth(LocalDate startDate, Long monthlyCharge, boolean isIncludeCountOfStartDate) {

		BigDecimal chargeOfUntilEndOfMonth = null;

		if (isIncludeCountOfStartDate && DateUtils.isMonthFirstDate(startDate)) {
			// 開始日を課金発生日としてカウントし、かつ、開始日が月初の場合、金額は月額料金と同じになる

			chargeOfUntilEndOfMonth = new BigDecimal(monthlyCharge);
		} else {

			// 当月の1日の料金
			boolean isTruncateDecimal = false;
			BigDecimal oneDayChargeDecimal = commonPlanCalcService.calcOneDayCharge(startDate, monthlyCharge, isTruncateDecimal);

			// 月末までの日数
			Long dayCountUntilEndOfMonth = DateUtils.getDayCountUntilEndOfMonth(startDate, isIncludeCountOfStartDate);

			// 月末までの料金
			chargeOfUntilEndOfMonth = commonPlanCalcService.calcDecimalChargeForDays(oneDayChargeDecimal, dayCountUntilEndOfMonth);
		}

		return chargeOfUntilEndOfMonth;
	}
	
	/**
	 * 月初から対象日までの日割りの合計料金を取得する
	 *
	 * @param endDate 対象日（終了日）
	 * @param monthlyCharge 月額料金
	 * @param isIncludeCountOfEndDate 対象日の料金を含むかどうか（true: 含む）
	 * @return
	 */
	public BigDecimal getDecimalChargeOfFromStartOfMonth(LocalDate endDate, Long monthlyCharge, boolean isIncludeCountOfEndDate) {

		// 当月の1日の料金
		boolean isTruncateDecimal = false;
		BigDecimal oneDayChargeDecimal = commonPlanCalcService.calcOneDayCharge(endDate, monthlyCharge, isTruncateDecimal);

		// 月初からの日数
		Long dayCountUntilEndOfMonth = DateUtils.getDayCountFromStartOfMonth(endDate, isIncludeCountOfEndDate);

		// 月初からの料金
		BigDecimal chargeOfFromStartOfMonth = commonPlanCalcService.calcDecimalChargeForDays(oneDayChargeDecimal, dayCountUntilEndOfMonth);

		return chargeOfFromStartOfMonth;
	}
	
	/**
	 * 開始日から終了日までの日割りの合計料金を取得する
	 * 
	 * <pre>
	 * ※開始日と終了日の年月は同じでなければならない。
	 *   異なる年月の場合は{@link IllegalArgumentException}が発生する。
	 * </pre>
	 *
	 * @param startDate 開始日
	 * @param endDate 終了日
	 * @param monthlyCharge 月額料金
	 * @param isIncludeCountOfStartDate 開始日の料金を含むかどうか（true: 含む）
	 * @param isIncludeCountOfEndDate 終了日の料金を含むかどうか（true: 含む）
	 * @return
	 */
	public BigDecimal getDecimalChargeOfStartToEnd(LocalDate startDate, LocalDate endDate, Long monthlyCharge, boolean isIncludeCountOfStartDate,
			boolean isIncludeCountOfEndDate) {

		if (!DateUtils.isEqualYearMonth(startDate, endDate)) {
			logger.error("開始日と終了日は同じ年月でなければならない。");
			throw new IllegalArgumentException();
		}

		// 当月の1日の料金
		boolean isTruncateDecimal = false;
		BigDecimal oneDayChargeDecimal = commonPlanCalcService.calcOneDayCharge(startDate, monthlyCharge, isTruncateDecimal);

		// 指定期間の日数
		Long dayCountStartToEnd = DateUtils.getDayCountStartToEnd(startDate, endDate, isIncludeCountOfStartDate, isIncludeCountOfEndDate);

		// 開始日から終了日までの料金
		BigDecimal chargeOfStartToEnd = commonPlanCalcService.calcDecimalChargeForDays(oneDayChargeDecimal, dayCountStartToEnd);

		return chargeOfStartToEnd;
	}
	
	/**
	 * すべてのプランタイプ毎の機能制限情報Mapを取得する
	 * 
	 * @param planType
	 * @return {@literal Map<PlanType, Map<PlanFuncRestrict（制限対象機能）, Boolean（制限するかどうか true:制限する）>>}
	 */
	public Map<PlanType, Map<PlanFuncRestrict, Boolean>> getAllPlanFuncRestrictMap() {
		
		// 制限情報Map
		Map<PlanType, Map<PlanFuncRestrict, Boolean>> planFuncRestrictMap = new HashMap<>();
		
		Map<PlanFuncRestrict, Boolean> starterPlanFuncRestrictMap = new HashMap<>();
		Map<PlanFuncRestrict, Boolean> standardPlanFuncRestrictMap = new HashMap<>();
		
		List<MPlanFuncRestrictEntity> planFuncRestrictEntityList = mPlanFuncRestrictDao.selectAll();
		for (MPlanFuncRestrictEntity entity : planFuncRestrictEntityList) {
			
			// Mapのkey
			PlanFuncRestrict planFuncRestrict = PlanFuncRestrict.of(entity.getFuncRestrictId());
			
			// Mapのvalue
			// スターターの権限Map
			starterPlanFuncRestrictMap.put(planFuncRestrict, SystemFlg.codeToBoolean(entity.getStarterRestrictFlg()));
			// スタンダードの権限Map
			standardPlanFuncRestrictMap.put(planFuncRestrict, SystemFlg.codeToBoolean(entity.getStandardRestrictFlg()));
		}
		
		// 1つのMapにまとめる
		planFuncRestrictMap.put(PlanType.STARTER, starterPlanFuncRestrictMap);
		planFuncRestrictMap.put(PlanType.STANDARD, standardPlanFuncRestrictMap);
		
		return planFuncRestrictMap;
	}
	
	// =========================================================================
	// private メソッド
	// =========================================================================

}
