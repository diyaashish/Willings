package jp.loioz.domain.condition;

import java.util.List;

import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.domain.value.SaibanId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 裁判情報の検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaibanSearchCondition extends SearchCondition {

	// 裁判情報

	/** 裁判ID */
	private SaibanId saibanId;

	/** 裁判ステータス */
	private List<Long> saibanStatus;

	// 裁判-事件

	/** 事件事件 */
	private SaibanJikenSearchCondition jiken;

	public SaibanJikenSearchCondition getJiken() {
		if (this.jiken == null) {
			this.jiken = new SaibanJikenSearchCondition();
		}
		return this.jiken;
	}

	/**
	 * 裁判-事件の検索条件
	 */
	@Data
	@EqualsAndHashCode(callSuper = true)
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaibanJikenSearchCondition extends SearchCondition {

		/** 事件番号元号 */
		private EraType jikenGengo;

		/** 事件番号年 */
		private String jikenYear;

		/** 事件番号符号 */
		private String jikenMark;

		/** 事件番号 */
		private String jikenNo;
	}

	// 裁判-担当者

	/** 担当者 */
	private SaibanTantoSearchCondition tanto;

	public SaibanTantoSearchCondition getTanto() {
		if (this.tanto == null) {
			this.tanto = new SaibanTantoSearchCondition();
		}
		return this.tanto;
	}

	/**
	 * 裁判-担当者の検索条件
	 */
	@Data
	@EqualsAndHashCode(callSuper = true)
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaibanTantoSearchCondition extends SearchCondition {

		/** 担当弁護士アカウント連番 */
		private List<Long> tantoLawyerSeqList;

		/** 担当事務員アカウント連番 */
		private List<Long> tantoJimuSeqList;
	}
}
