package jp.loioz.app.user.statementList.form;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.constant.SortConstant.SortOrder;
import jp.loioz.common.constant.SortConstant.StatementListSortItem;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.domain.condition.StatementListSearchCondition;
import jp.loioz.domain.condition.StatementListSortCondition;
import lombok.Data;

/**
 * 精算書一覧：検索条件フォーム
 */
@Data
public class StatementListSearchForm implements PagerForm {

	/** 検索条件エリアの開閉フラグ */
	private boolean isSearchConditionOpen;

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
	private String statementAmountFrom;

	/** 精算額To */
	private String statementAmountTo;

	/** 支期日From */
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

	// -----------------------------------------------
	// ページャー
	// -----------------------------------------------
	/** ページ番号（これから表示するページ） */
	private Integer page = DEFAULT_PAGE;

	/** 表示件数 */
	private PageSize pageSize = DEFAULT_SIZE;

	// -----------------------------------------------
	// 一覧ソート情報
	// -----------------------------------------------
	/** 一覧のソートキー */
	private StatementListSortItem statementListSortItem;

	/** 一覧のソート順 */
	private SortOrder statementListSortOrder;

	/**
	 * フォームの初期化
	 */
	public void initForm() {

		// フォームの各値をクリア
		this.clearForm();

	}

	/**
	 * フォームの各値をクリアします。
	 */
	public void clearForm() {

		// 検索条件エリアは閉じた状態
		this.isSearchConditionOpen = false;

		// 売上計上先プルダウン情報
		this.salesOwnerList = new ArrayList<SelectOptionForm>();

		// 担当弁護士プルダウン情報
		this.tantoLawyerList = new ArrayList<SelectOptionForm>();

		// 担当事務プルダウン情報
		this.tantoJimuList = new ArrayList<SelectOptionForm>();

		// 初期表示の一覧ソート順
		this.setStatementListSortItem(StatementListSortItem.STATEMENT_DATE);
		this.setStatementListSortOrder(SortOrder.DESC);

		// ページ番号を初期化
		this.defaultPage();

		// 各検索条件初期化
		this.searchWord = "";
		this.statementNo = "";
		this.statementIssueStatus = "";
		this.statementRefundStatus = "";
		this.statementDateFrom = "";
		this.statementDateTo = "";
		this.statementAmountFrom = "";
		this.statementAmountTo = "";
		this.refundDateFrom = "";
		this.refundDateTo = "";
		this.salesOwner = null;
		this.tantoLaywer = null;
		this.tantoJimu = null;
		this.statementMemo = "";
	}

	/**
	 * ページ番号を初期化
	 */
	public void defaultPage() {
		setDefaultPage();
	}

	/** 検索条件オブジェクトを作成 */
	public StatementListSearchCondition toStatementListSearchCondition() {
		return StatementListSearchCondition.builder()
				.searchWord(StringUtils.removeSpaceCharacter(this.searchWord))
				.statementNo(this.statementNo)
				.statementIssueStatus(this.statementIssueStatus)
				.statementRefundStatus(this.statementRefundStatus)
				.statementDateFrom(this.statementDateFrom)
				.statementDateTo(this.statementDateTo)
				.statementAmountFrom(StringUtils.isEmpty(this.statementAmountFrom) ? null : new BigDecimal(this.statementAmountFrom.replaceAll(",", "")))
				.statementAmountTo(StringUtils.isEmpty(this.statementAmountTo) ? null : new BigDecimal(this.statementAmountTo.replaceAll(",", "")))
				.refundDateFrom(this.refundDateFrom)
				.refundDateTo(this.refundDateTo)
				.salesOwner(this.salesOwner)
				.tantoLaywer(this.tantoLaywer)
				.tantoJimu(this.tantoJimu)
				.statementMemo(this.statementMemo)
				.build();
	}

	/** ソート条件オブジェクトを作成 */
	public StatementListSortCondition toStatementListSortCondition() {
		return StatementListSortCondition.builder()
				.sortItem(this.statementListSortItem)
				.sortOrder(this.statementListSortOrder)
				.build();
	}

	/** ページ番号をデフォルトに設定 */
	public void setDefaultPage() {
		this.page = DEFAULT_PAGE;
	}

	/**
	 * 使用している検索条件項目数を取得する
	 * 
	 * @return
	 */
	public int getSearchConditionCount() {
		int count = 0;

		// キーワードがあればカウントアップ
		if (!StringUtils.isEmpty(searchWord)) {
			count++;
		}

		// 精算番号があればカウントアップ
		if (!StringUtils.isEmpty(statementNo)) {
			count++;
		}

		// 発行ステータスがあればカウントアップ
		if (!StringUtils.isEmpty(statementIssueStatus)) {
			count++;
		}

		// 入金状況があればカウントアップ
		if (!StringUtils.isEmpty(statementRefundStatus)) {
			count++;
		}

		// 精算日があればカウントアップ
		if (!StringUtils.isEmpty(statementDateFrom) || !StringUtils.isEmpty(statementDateTo)) {
			count++;
		}

		// 精算額があればカウントアップ
		if (!StringUtils.isEmpty(statementAmountFrom) || !StringUtils.isEmpty(statementAmountTo)) {
			count++;
		}

		// 期日があればカウントアップ
		if (!StringUtils.isEmpty(refundDateFrom) || !StringUtils.isEmpty(refundDateTo)) {
			count++;
		}

		// 売上計上先があればカウントアップ
		if (salesOwner != null) {
			count++;
		}

		// 担当弁護士があればカウントアップ
		if (tantoLaywer != null) {
			count++;
		}

		// 担当事務があればカウントアップ
		if (tantoJimu != null) {
			count++;
		}

		// メモがあればカウントアップ
		if (!StringUtils.isEmpty(statementMemo)) {
			count++;
		}

		return count;
	}

}
