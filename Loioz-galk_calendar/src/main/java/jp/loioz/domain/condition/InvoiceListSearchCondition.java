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
 * 請求書一覧画面検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceListSearchCondition extends SearchCondition {

	/** キーワード */
	private String searchWord;

	/** 請求番号 */
	private String invoiceNo;

	/** 発行ステータス */
	private String invoiceIssueStatus;

	/** 入金状況 */
	private String invoicePaymentStatus;

	/** 請求日From */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String invoiceDateFrom;

	/** 請求日To */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String invoiceDateTo;

	/** 請求額From */
	private BigDecimal invoiceAmountFrom;

	/** 請求額To */
	private BigDecimal invoiceAmountTo;

	/** 支払期日From */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String dueDateFrom;

	/** 支払期日To */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String dueDateTo;

	/** 支払方法 */
	private String invoiceType;

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
	private String invoiceMemo;

}
