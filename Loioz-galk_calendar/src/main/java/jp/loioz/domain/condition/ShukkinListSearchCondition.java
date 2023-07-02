package jp.loioz.domain.condition;

import java.time.LocalDate;
import java.util.List;

import jp.loioz.app.user.nyushukkinYotei.shukkin.enums.ShukkinSearchRifineType;
import jp.loioz.app.user.nyushukkinYotei.shukkin.enums.ShukkinSearchYoteiDateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 出金一覧の検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShukkinListSearchCondition extends SearchCondition {

	/** 顧客ID */
	private Long customerId;

	/** 顧客名 */
	private String customerName;

	/** 出金予定日絞り込み種別 */
	private ShukkinSearchYoteiDateType shukkinSearchYoteiDateType;

	/** 絞り込み日付From */
	private LocalDate searchDateFrom;

	/** 絞り込み日付To */
	private LocalDate searchDateTo;

	/** 表示する月を判断 **/
	private LocalDate yearMonth;

	/** 出金口座 */
	private Long kozaSeq;

	/** 出金絞り込み種別 */
	private ShukkinSearchRifineType shukkinSearchRifineType;

	// 案件担当者
	/** 担当者 */
	private AnkenTantoSearchCondition tantosha;

	public AnkenTantoSearchCondition getTanto() {
		if (this.tantosha == null) {
			this.tantosha = new AnkenTantoSearchCondition();
		}
		return this.tantosha;
	}

	/**
	 * 案件担当者の検索条件
	 */
	@Data
	@EqualsAndHashCode(callSuper = true)
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AnkenTantoSearchCondition extends SearchCondition {

		/** 担当弁護士アカウント */
		private List<Long> tantoLawyerSeqList;

		/** 担当事務員アカウント */
		private List<Long> tantoJimuSeqList;

	}

}
