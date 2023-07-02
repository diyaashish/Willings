package jp.loioz.domain.condition;

import java.math.BigDecimal;
import java.util.List;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 精算書一覧画面検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementListSearchCondition extends SearchCondition {

	/** キーワード */
	private String searchWord;

	/** 精算番号 */
	private String statementNo;

	/** 発行ステータス */
	private String statementIssueStatus;

	/** 精算状況 */
	private String statementRefundStatus;

	/** 精算日From */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String statementDateFrom;

	/** 精算日To */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String statementDateTo;

	/** 精算額From */
	private BigDecimal statementAmountFrom;

	/** 精算額To */
	private BigDecimal statementAmountTo;

	/** 期日From */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String refundDateFrom;

	/** 期日To */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String refundDateTo;

	/** 検索条件売上計上先のプルダウン情報 */
	private List<SelectOptionForm> salesOwnerList;

	/** 検索条件担当弁護士のプルダウン情報 */
	private List<SelectOptionForm> tantoLawyerList;

	/** 検索条件担当事務のプルダウン情報 */
	private List<SelectOptionForm> tantoJimuList;

	/** 売上計上先 */
	private Long salesOwner;

	/** 担当弁護士 */
	private Long tantoLaywer;

	/** 担当事務 */
	private Long tantoJimu;

	/** メモ */
	private String statementMemo;

}
