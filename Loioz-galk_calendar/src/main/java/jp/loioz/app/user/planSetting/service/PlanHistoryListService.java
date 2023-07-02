package jp.loioz.app.user.planSetting.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonPlanService;
import jp.loioz.app.common.service.CommonTaxService;
import jp.loioz.app.user.planSetting.dto.PlanSettingSessionInfoDto;
import jp.loioz.app.user.planSetting.form.PlanHistoryListForm;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.plan.PlanConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanStatus;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.dao.TPlanHistoryDao;
import jp.loioz.dto.PlanHistoryDto;
import jp.loioz.entity.TPlanHistoryEntity;

/**
 * プラン履歴画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PlanHistoryListService extends DefaultService {

	/** 利用プラン履歴のDaoクラス */
	@Autowired
	private TPlanHistoryDao tPlanHistoryDao;
	
	/** 契約プランの共通サービス */
	@Autowired
	private CommonPlanService commonPlanService;
	
	/** 税金に関する処理の共通サービス */
	@Autowired
	private CommonTaxService commonTaxService;

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * プラン履歴一覧情報を取得する
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @return
	 */
	public List<PlanHistoryDto> getPlanHistoryList(PlanSettingSessionInfoDto sessionDto, PlanHistoryListForm viewForm) {

		// プラン履歴取得
		List<TPlanHistoryEntity> planHistoryEntitys = tPlanHistoryDao.selectAll();

		if (planHistoryEntitys.isEmpty()) {
			// 履歴情報が存在しない場合
			return Collections.emptyList();
		}

		List<PlanHistoryDto> historyDtos = new ArrayList<>();

		// 最初の履歴情報（プランステータスが「無料」の履歴）
		PlanHistoryDto firstHistoryDto = this.getFirstHistoryDto(planHistoryEntitys);
		historyDtos.add(firstHistoryDto);
		// 最初の要素を取得したので削除
		planHistoryEntitys.remove(0);

		// 無料期間の終了日時
		LocalDateTime freePlanExpiredAt = commonPlanService.getFreePlanExpiredAt(sessionDto.getTenantSeq());
		
		// 最初の履歴以降の履歴情報（最後の履歴情報を含まない）
		List<PlanHistoryDto> afterFirstHistoryDtos = this.getAfterFirstHistoryDtos(sessionDto, firstHistoryDto, planHistoryEntitys, freePlanExpiredAt);
		historyDtos.addAll(afterFirstHistoryDtos);

		// 最後の履歴情報を調整した履歴情報の結果リスト
		List<PlanHistoryDto> historysOfAdjustedLastHistory = this.getHistorysOfAdjustedLastHistory(sessionDto, historyDtos);

		// 逆順ソート（履歴情報を作成日の降順とする）
		Collections.reverse(historysOfAdjustedLastHistory);
		
		// 表示用に履歴データを変換
		historysOfAdjustedLastHistory = this.dataTransformForDisplay(historysOfAdjustedLastHistory, freePlanExpiredAt);
		
		return historysOfAdjustedLastHistory;
	}

	/**
	 * 最初の履歴情報を取得する
	 * 
	 * @param planHistoryEntitys
	 * @return
	 */
	private PlanHistoryDto getFirstHistoryDto(List<TPlanHistoryEntity> planHistoryEntitys) {

		if (CollectionUtils.isEmpty(planHistoryEntitys)) {
			return null;
		}

		TPlanHistoryEntity historyEntity = planHistoryEntitys.get(0);

		// 最初の履歴情報
		PlanHistoryDto firstHistoryDto = this.getPlanHistoryDto(DateUtils.convertLocalDate(historyEntity.getHistoryCreatedAt()),
				historyEntity.getPlanStatus(),
				historyEntity.getPlanType(),
				historyEntity.getChargeThisMonth(), historyEntity.getChargeAfterNextMonth(), historyEntity.getLicenseCount(),
				historyEntity.getStorageCapacity());

		return firstHistoryDto;
	}

	/**
	 * 最初以降の履歴情報を取得する
	 * 
	 * @param sessionDto
	 * @param firstHistoryDto
	 * @param planHistoryEntitysExcludeFirstHistory
	 * @param freePlanExpiredAt
	 * @return
	 */
	private List<PlanHistoryDto> getAfterFirstHistoryDtos(PlanSettingSessionInfoDto sessionDto, PlanHistoryDto firstHistoryDto,
			List<TPlanHistoryEntity> planHistoryEntitysExcludeFirstHistory, LocalDateTime freePlanExpiredAt) {

		LocalDate freePlanExpiredDate = DateUtils.convertLocalDate(freePlanExpiredAt);
		
		PlanHistoryDto beforeHistoryDto = firstHistoryDto;
		List<PlanHistoryDto> afterFirstHistoryDtos = new ArrayList<>();

		// 先頭以降の履歴の処理
		for (TPlanHistoryEntity entity : planHistoryEntitysExcludeFirstHistory) {

			LocalDate createdAt = DateUtils.convertLocalDate(entity.getHistoryCreatedAt());

			// 履歴の基準日(履歴データが持つ「chargeThisMonth」はこの日の月の金額となる)
			LocalDate historyBaseDate = createdAt;
			
			if (createdAt.isBefore(freePlanExpiredDate) || createdAt.isEqual(freePlanExpiredDate)) {
				// 履歴の登録日が、無料期間中の場合（=対象の履歴が無料期間中のプラン登録の履歴の場合）
				
				// 履歴の基準日を「無料期間の終了日」にする
				historyBaseDate = freePlanExpiredDate;
			}
			
			if (DateUtils.isEqualYearMonth(beforeHistoryDto.getChargeDate(), historyBaseDate)) {
				// 同じ年月の場合

				beforeHistoryDto.setPlanStatus(entity.getPlanStatus());

				// 金額を上書き
				BigDecimal nextChargeThisMonth = entity.getChargeThisMonth();
				BigDecimal nextChargeAfterNextMonth = entity.getChargeAfterNextMonth();
				beforeHistoryDto.setChargeThisMonth(nextChargeThisMonth != null ? nextChargeThisMonth.longValue() : 0L);
				beforeHistoryDto.setChargeAfterNextMonth(nextChargeAfterNextMonth != null ? nextChargeAfterNextMonth.longValue() : 0L);
				
				// プランタイプ、ライセンス数、ストレージ数を上書き
				beforeHistoryDto.setPlanType(PlanType.of(entity.getPlanType()));
				beforeHistoryDto.setLicenseCount(entity.getLicenseCount());
				beforeHistoryDto.setStorageCapacity(entity.getStorageCapacity());

			} else {
				// 違う月の場合

				// 次の月
				LocalDate nextMonthBeforeHistoryDate = beforeHistoryDto.getChargeDate().plusMonths(1L);

				if (DateUtils.isEqualYearMonth(nextMonthBeforeHistoryDate, historyBaseDate)) {
					// 翌月の履歴の場合

					// 対象履歴の履歴情報を設定
					PlanHistoryDto nextHistoryDto = this.getPlanHistoryDto(historyBaseDate, entity.getPlanStatus(), entity.getPlanType(),
							entity.getChargeThisMonth(), entity.getChargeAfterNextMonth(), entity.getLicenseCount(), entity.getStorageCapacity());
					afterFirstHistoryDtos.add(nextHistoryDto);

					beforeHistoryDto = nextHistoryDto;

				} else {
					// 翌月以降の場合

					// プラン内容が変わらない期間の月毎の履歴情報を追加
					List<PlanHistoryDto> noPlanChangeTermHistoryList = this.getNoPlanChangeTermHistoryList(sessionDto, beforeHistoryDto, historyBaseDate);
					afterFirstHistoryDtos.addAll(noPlanChangeTermHistoryList);

					// 次（対象）の履歴の履歴情報を設定
					PlanHistoryDto nextHistoryDto = this.getPlanHistoryDto(historyBaseDate, entity.getPlanStatus(), entity.getPlanType(),
							entity.getChargeThisMonth(), entity.getChargeAfterNextMonth(), entity.getLicenseCount(), entity.getStorageCapacity());
					afterFirstHistoryDtos.add(nextHistoryDto);

					beforeHistoryDto = nextHistoryDto;
				}
			}
		}

		return afterFirstHistoryDtos;
	}

	/**
	 * 最後の履歴情報を調整した結果リストを取得する
	 * 
	 * @param sessionDto
	 * @param historyDtos
	 * @return
	 */
	private List<PlanHistoryDto> getHistorysOfAdjustedLastHistory(PlanSettingSessionInfoDto sessionDto, List<PlanHistoryDto> historyDtos) {

		if (CollectionUtils.isEmpty(historyDtos)) {
			return Collections.emptyList();
		}

		List<PlanHistoryDto> resultList = new ArrayList<>(historyDtos);
		LocalDate now = LocalDate.now();

		// 最後の要素を取得
		int lastHistoryIdx = resultList.size() - 1;
		if (lastHistoryIdx == 0) {
			// 最後の要素が先頭の要素＝ひとつしか履歴情報が存在しない場合

			// 最後の履歴情報と、それ以降の履歴情報を生成し返却
			return this.getLastAndAfterLastHistoryList(sessionDto, resultList.get(0), now);
		}

		PlanHistoryDto lastHistoryDto = resultList.get(lastHistoryIdx);
		LocalDate lastChargeDate = lastHistoryDto.getChargeDate();

		if (DateUtils.isEqualYearMonth(lastChargeDate, now)) {
			// 最後の履歴情報が現在時間の今月のものの場合は、まだ請求させていないものとなるため除外する（月末請求のため）

			resultList.remove(lastHistoryIdx);

		} else if (DateUtils.isCorrectTimeContextYearMonth(lastChargeDate, now)) {
			// 最後の履歴情報より現在の年月が未来の場合

			List<PlanHistoryDto> lastAndAfterLastHistoryList = this.getLastAndAfterLastHistoryList(sessionDto, lastHistoryDto, now);

			if (!CollectionUtils.isEmpty(lastAndAfterLastHistoryList)) {
				// 最後の要素（先頭の要素）は既に結果リストに含まれているので、削除する
				lastAndAfterLastHistoryList.remove(0);
			}

			resultList.addAll(lastAndAfterLastHistoryList);
		}

		return resultList;
	}

	/**
	 * 引数に受けた最後の履歴情報を含む、最後の履歴以降の履歴情報を生成し取得する
	 * 
	 * @param sessionDto
	 * @param lastHistoryDto
	 * @param now
	 * @return
	 */
	private List<PlanHistoryDto> getLastAndAfterLastHistoryList(PlanSettingSessionInfoDto sessionDto, PlanHistoryDto lastHistoryDto, LocalDate now) {

		if (lastHistoryDto == null) {
			return Collections.emptyList();
		}

		List<PlanHistoryDto> resultList = new ArrayList<>();
		resultList.add(lastHistoryDto);

		// プラン内容が変わらない期間の月毎の履歴情報を追加
		List<PlanHistoryDto> noPlanChangeTermHistoryList = this.getNoPlanChangeTermHistoryList(sessionDto, lastHistoryDto, now);
		resultList.addAll(noPlanChangeTermHistoryList);

		int resultLastHistoryIdx = resultList.size() - 1;
		PlanHistoryDto resultLastHistoryDto = resultList.get(resultLastHistoryIdx);
		LocalDate resultLastChargeDate = resultLastHistoryDto.getChargeDate();

		if (DateUtils.isEqualYearMonth(resultLastChargeDate, now)) {
			// 最後の履歴情報が現在時間の今月のものの場合は、まだ請求させていないものとなるため除外する（月末請求のため）
			resultList.remove(resultLastHistoryIdx);
		}

		return resultList;
	}

	/**
	 * プラン内容が変わらない期間の月毎の履歴情報を取得する
	 * 
	 * @param sessionDto
	 * @param startHistoryDto 対象期間の間継続されるプランの情報
	 * @param untilDate プラン内容が変わらない期間が終了する日付
	 * 
	 * @return
	 */
	private List<PlanHistoryDto> getNoPlanChangeTermHistoryList(PlanSettingSessionInfoDto sessionDto, PlanHistoryDto startHistoryDto, LocalDate untilDate) {

		if (startHistoryDto == null || untilDate == null) {
			return Collections.emptyList();
		}

		List<PlanHistoryDto> noPlanChangeTermHistoryList = new ArrayList<>();

		PlanStatus planStatus = DefaultEnum.getEnum(PlanStatus.class, startHistoryDto.getPlanStatus());

		switch (planStatus) {
		case FREE:
			// プラン状態が変更する前月までの履歴情報を追加する（無料状態が継続している間の履歴）
			noPlanChangeTermHistoryList = this.getHistoryListOfFreeStatusUsing(sessionDto, startHistoryDto, untilDate);
			break;
		case ENABLED:
			// プラン状態が変更する前月までの履歴情報を追加する
			noPlanChangeTermHistoryList = this.getHistoryListOfNoPlanChangeTerm(startHistoryDto, untilDate);
			break;
		case CHANGING:
		case CANCELED:
			// プラン変更を行わない期間の履歴情報は追加しない
			break;
		default:
			// プラン変更を行わない期間の履歴情報は追加しない
			break;
		}
		
		return noPlanChangeTermHistoryList;
	}

	/**
	 * 履歴情報のDtoを取得する
	 * 
	 * @param historyDate
	 * @param planStatus
	 * @param planType
	 * @param chargeThisMonth
	 * @param chargeAfterNextMonth
	 * @param licenseCount
	 * @param storageCapacity
	 * @return
	 */
	private PlanHistoryDto getPlanHistoryDto(LocalDate historyDate, String planStatus, String planType,
			BigDecimal chargeThisMonth, BigDecimal chargeAfterNextMonth, Long licenseCount, Long storageCapacity) {

		PlanHistoryDto historyDto = new PlanHistoryDto();
		historyDto.setPlanStatus(planStatus);

		// プランタイプ
		historyDto.setPlanType(PlanType.of(planType));
		
		// 月末の日付を設定する
		historyDto.setChargeDate(DateUtils.getLastDateOfThisMonth(historyDate));

		// 金額を設定する
		historyDto.setChargeThisMonth(chargeThisMonth != null ? chargeThisMonth.longValue() : 0L);
		historyDto.setChargeAfterNextMonth(chargeAfterNextMonth != null ? chargeAfterNextMonth.longValue() : 0L);

		// ライセンス数とストレージ数
		historyDto.setLicenseCount(licenseCount);
		historyDto.setStorageCapacity(storageCapacity);

		return historyDto;
	}

	/**
	 * 無料ステータスの状態で利用を続けていた分の履歴情報を取得する
	 * 
	 * @param sessionDto
	 * @param freeHistoryDto 無料ステータスが登録された月の履歴情報
	 * @param nextStatusHistoryCreateDate 無料ステータスの次の履歴が登録された日付
	 * 
	 * @return freeHistoryDtoの翌月以降のプラン内容と同じ状態の履歴情報のリスト
	 */
	private List<PlanHistoryDto> getHistoryListOfFreeStatusUsing(PlanSettingSessionInfoDto sessionDto, PlanHistoryDto freeHistoryDto, LocalDate nextStatusHistoryCreateDate) {
		
		PlanStatus planStatus = DefaultEnum.getEnum(PlanStatus.class, freeHistoryDto.getPlanStatus());
		if (planStatus != PlanConstant.PlanStatus.FREE) {
			// 履歴情報が無料ステータスのものではない場合はエラーとする
			throw new IllegalArgumentException("無料ステータスの履歴情報ではない");
		}
		
		// 無料ステータスの履歴が登録された月の月初
		LocalDate freeHistoryDate = freeHistoryDto.getChargeDate();
		LocalDate freeHistoryDateMonthFirst = DateUtils.getFirstDateOfThisMonth(freeHistoryDate);
		
		// 無料期限の日付
		LocalDateTime freePlanExpiredAt = commonPlanService.getFreePlanExpiredAt(sessionDto.getTenantSeq());
		LocalDate freePlanExpiredDate = DateUtils.convertLocalDate(freePlanExpiredAt);
		
		// 1ヶ月の間ずっと無料ステータスで無料状態だった最後の月の翌月の月初（課金が開始する月の月初とほぼ同じ意味）
		// （無料期間中にプランを登録した場合のみ例外。この場合は、ステータスは有効状態となるが、無料期間終了まで課金は発生しないため。）
		// 無料ステータスのまま無料期限が切れた場合 -> 無料期限の翌月の月初
		// 無料期間中にプランを登録した場合 -> プランを登録した月の月初
		LocalDate nextMonthFirstOfLastMothFreeStatusAndNoChageEntireMonth = null;
		
		// 次の履歴が無料期限終了後に登録されたかどうか
		boolean nextStatusHistoryRegistAfterFreePlanExpired = nextStatusHistoryCreateDate.isAfter(freePlanExpiredDate);
		if (nextStatusHistoryRegistAfterFreePlanExpired) {
			// 無料期限終了後にプランを登録している場合
			
			boolean isMonthLastDate = DateUtils.isMonthLastDate(freePlanExpiredDate);
			if (isMonthLastDate) {
				// 無料期間終了日が月末日の場合
				nextMonthFirstOfLastMothFreeStatusAndNoChageEntireMonth = DateUtils.getFirstDateOfNextMonth(freePlanExpiredDate);
			} else {
				// 無料期間終了日が月末日以外の場合
				nextMonthFirstOfLastMothFreeStatusAndNoChageEntireMonth = DateUtils.getFirstDateOfThisMonth(freePlanExpiredDate);
			}
			
		} else {
			// 無料期間中にプランを登録している場合
			
			// 次のプランの登録月の月初
			nextMonthFirstOfLastMothFreeStatusAndNoChageEntireMonth = DateUtils.getFirstDateOfThisMonth(nextStatusHistoryCreateDate);
		}
		
		// 無料ステータスで無料状態だった月の数（無料ステータスの履歴が登録された月は除く）
		long freeStatusAndNoChageEntireMonthCount = ChronoUnit.MONTHS.between(freeHistoryDateMonthFirst, nextMonthFirstOfLastMothFreeStatusAndNoChageEntireMonth) - 1;
		
		if (freeStatusAndNoChageEntireMonthCount < 1) {
			// 年月が同じか1ヶ月しかずれていない、もしくは日付の前後関係が逆になっている場合
			return Collections.emptyList();
		}
		
		List<PlanHistoryDto> freeStatusAndNoChageEntireMonthHistoryList = new ArrayList<>();
		LocalDate freeStatusAndNoChageMonth = null;
		
		// 無料ステータスの履歴のプランの情報を取得
		String statusAfterNextMonth = freeHistoryDto.getPlanStatus();
		String planTypeAfterNextMonth = freeHistoryDto.getPlanType().getId();
		Long chargeAfterNextMonth = freeHistoryDto.getChargeAfterNextMonth();
		BigDecimal chargeAfterNextMonthDecimal = chargeAfterNextMonth != null ? new BigDecimal(chargeAfterNextMonth) : null;
		Long licenseCountAfterNextMonth = freeHistoryDto.getLicenseCount();
		Long storageCapacityAfterNextMonth = freeHistoryDto.getStorageCapacity();
		
		// プランの状態が変わらない間の履歴情報を生成
		for (int i = 0; i < freeStatusAndNoChageEntireMonthCount; i++) {

			freeStatusAndNoChageMonth = freeHistoryDate.plusMonths(1L);

			PlanHistoryDto historyDto = this.getPlanHistoryDto(freeStatusAndNoChageMonth, statusAfterNextMonth, planTypeAfterNextMonth,
					chargeAfterNextMonthDecimal, chargeAfterNextMonthDecimal, licenseCountAfterNextMonth, storageCapacityAfterNextMonth);
			PlanHistoryDto nextHistoryDto = new PlanHistoryDto();

			BeanUtils.copyProperties(historyDto, nextHistoryDto);

			freeStatusAndNoChageEntireMonthHistoryList.add(nextHistoryDto);

			// 次のループのための情報を保持
			freeHistoryDate = historyDto.getChargeDate();
		}
		
		return freeStatusAndNoChageEntireMonthHistoryList;
	}
	
	/**
	 * プランの状態が変わらない間の毎月の履歴情報を取得する
	 * 
	 * @param baseHistoryDto プランの状態のベースとなる履歴情報（このプランの状態の履歴リストが返却される）
	 * @param nextStatusHistoryCreateDate 次のプランの状態となる履歴情報が登録された日付
	 * ※baseHistoryDtoのchargeDateより必ず未来の年月の日付とすること
	 * 
	 * @return baseHistoryDtoの翌月以降のプラン内容と同じ状態の履歴情報のリスト
	 */
	private List<PlanHistoryDto> getHistoryListOfNoPlanChangeTerm(PlanHistoryDto baseHistoryDto, LocalDate nextStatusHistoryCreateDate) {

		LocalDate prevHistoryDate = baseHistoryDto.getChargeDate();
		
		// ひとつ前の履歴が登録された月の月初の日付
		LocalDate prevHistoryDateMonthFirst = DateUtils.getFirstDateOfThisMonth(prevHistoryDate);
		// 次の履歴が登録された月の月初の日付
		LocalDate nextStatusHistoryCreateMonthFirst = DateUtils.getFirstDateOfThisMonth(nextStatusHistoryCreateDate);
		
		// ひとつ前の履歴の月と、次の履歴の月の間に、いくつの月があるか（いくつの履歴情報を生成してあげる必要があるか）
		// ※ChronoUnit.MONTHS.between(2021/1/1, 2021/3/1)は2となるが、引き算の結果ではなく、間の月の数を取得したいため最後に-1する
		long betweenNumberMonths = ChronoUnit.MONTHS.between(prevHistoryDateMonthFirst, nextStatusHistoryCreateMonthFirst) - 1;

		if (betweenNumberMonths < 1) {
			// 年月が同じか1ヶ月しかずれていない、もしくは日付の前後関係が逆になっている場合
			return Collections.emptyList();
		}

		List<PlanHistoryDto> noPlanChangeTermHistoryList = new ArrayList<>();
		LocalDate noChangeChargeMonth = null;

		// ベースとなるプランの情報を取得
		String statusAfterNextMonth = baseHistoryDto.getPlanStatus();
		String planTypeAfterNextMonth = baseHistoryDto.getPlanType().getId();
		Long chargeAfterNextMonth = baseHistoryDto.getChargeAfterNextMonth();
		BigDecimal chargeAfterNextMonthDecimal = chargeAfterNextMonth != null ? new BigDecimal(chargeAfterNextMonth) : null;
		Long licenseCountAfterNextMonth = baseHistoryDto.getLicenseCount();
		Long storageCapacityAfterNextMonth = baseHistoryDto.getStorageCapacity();

		// プランの状態が変わらない間の履歴情報を生成
		for (int i = 0; i < betweenNumberMonths; i++) {

			noChangeChargeMonth = prevHistoryDate.plusMonths(1L);

			PlanHistoryDto historyDto = this.getPlanHistoryDto(noChangeChargeMonth, statusAfterNextMonth, planTypeAfterNextMonth,
					chargeAfterNextMonthDecimal, chargeAfterNextMonthDecimal, licenseCountAfterNextMonth, storageCapacityAfterNextMonth);
			PlanHistoryDto nextHistoryDto = new PlanHistoryDto();

			BeanUtils.copyProperties(historyDto, nextHistoryDto);

			noPlanChangeTermHistoryList.add(nextHistoryDto);

			// 次のループのための情報を保持
			prevHistoryDate = historyDto.getChargeDate();
		}

		return noPlanChangeTermHistoryList;
	}
	
	/**
	 * 表示用に履歴データの変換を行う
	 * 
	 * <pre>
	 * 実施する処理は下記
	 * 
	 * ・消費税込みの金額を設定（Dtoが保持する金額情報と日付情報をもとに計算して算出）
	 * ・無料トライアル期間中のステータス・金額補正（Dtoが保持する日付情報をもとに無料トライアル期間中の場合はステータスと金額を補正）
	 * 
	 * </pre>
	 * 
	 * @param historyList
	 * @param freePlanExpiredAt
	 * @return
	 */
	private List<PlanHistoryDto> dataTransformForDisplay(List<PlanHistoryDto> historyList, LocalDateTime freePlanExpiredAt) {
		
		// 無料トライアルの期限日（この日いっぱいは無料期間内）
		LocalDate freePlanExpiredDate = DateUtils.convertLocalDate(freePlanExpiredAt);
		
		for (PlanHistoryDto dto : historyList) {
			
			// 税込み金額の設定
			Long chargeThisMonth = dto.getChargeThisMonth();
			Long taxAmount = commonTaxService.calcTaxAmount(chargeThisMonth, dto.getChargeDate());
			dto.setChargeThisMonthIncludedTax(chargeThisMonth + taxAmount);
			
			// 無料トライアル期間中のステータス・金額補正
			LocalDate chargeDate = dto.getChargeDate();
			if (chargeDate.isBefore(freePlanExpiredDate) || chargeDate.isEqual(freePlanExpiredDate)) {
				// 課金履歴日が無料トライアル期限日より、前、もしくは、同日の場合 = 課金履歴日が無料トライアル期間内の場合
				// -> 無料トライアル期間は、表示上はステータスを無料とし、課金も発生しないので履歴の料金は0とする
				dto.setPlanStatus(PlanConstant.PlanStatus.FREE.getCd());
				dto.setChargeThisMonth(0L);
				dto.setChargeThisMonthIncludedTax(0L);
			}
		}
		
		return historyList;
	}

}
