package jp.loioz.domain.condition;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 案件情報の検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnkenSearchCondition extends SearchCondition {

	// -----------------------------------------------
	// 案件情報
	// -----------------------------------------------
	/** 案件ID */
	private Long ankenId;

	/** 分野ID */
	private List<Long> bunyaId;

	/** 案件ステータス */
	private List<Long> ankenStatus;

	/** 案件登録日(FROM) */
	private LocalDate ankenCreatedDateFrom;

	/** 案件登録日(TO) */
	private LocalDate ankenCreatedDateTo;

	// 案件担当者

	/** 担当者 */
	private AnkenTantoSearchCondition tanto;

	public AnkenTantoSearchCondition getTanto() {
		if (this.tanto == null) {
			this.tanto = new AnkenTantoSearchCondition();
		}
		return this.tanto;
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

		/** 担当弁護士アカウント連番 */
		private List<Long> tantoLawyerSeqList;

		/** 担当事務員アカウント連番 */
		private List<Long> tantoJimuSeqList;
	}
}
