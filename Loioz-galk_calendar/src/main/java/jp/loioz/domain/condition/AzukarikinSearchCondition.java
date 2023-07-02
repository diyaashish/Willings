package jp.loioz.domain.condition;

import java.time.LocalDate;
import java.util.List;

import jp.loioz.app.user.azukarikinManagement.Enums.CustomerTotalSearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AzukarikinSearchCondition extends SearchCondition {

	/** 顧客計検索種別 */
	private CustomerTotalSearchType totalSearchType;

	/** 0円を非表示 */
	private boolean excludeZero;

	/** 最終入金日From */
	private LocalDate lastNyukinDateFrom;

	/** 最終入金日To */
	private LocalDate lastNyukinDateTo;

	/** 売上計上先アカウント選択 */
	private TantoSearchCondition tantosha;

	/** 案件ステータス選択 */
	private AnkenStatusSeachCondition status;

	/** getterを上書き */
	public TantoSearchCondition getTantosha() {
		if (this.tantosha == null) {
			this.tantosha = new TantoSearchCondition();
		}
		return this.tantosha;
	}

	/** getterを上書き */
	public AnkenStatusSeachCondition getStatus() {
		if (this.status == null) {
			this.status = new AnkenStatusSeachCondition();
		}
		return this.status;
	}

	/**
	 * 担当者の検索条件
	 */
	@Data
	@EqualsAndHashCode(callSuper = true)
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TantoSearchCondition extends SearchCondition {

		/** 売上計上先アカウント */
		private List<Long> salesOwnerSeqList;

		/** 担当弁護士アカウント */
		private List<Long> tantoLawyerSeqList;

		/** 担当事務員アカウント */
		private List<Long> tantoJimuSeqList;

	}

	/**
	 * 案件ステータスの検索条件
	 */
	@Data
	@EqualsAndHashCode(callSuper = true)
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AnkenStatusSeachCondition extends SearchCondition {

		/** ステータスリスト */
		private List<String> ankenStatus;
	}

}
