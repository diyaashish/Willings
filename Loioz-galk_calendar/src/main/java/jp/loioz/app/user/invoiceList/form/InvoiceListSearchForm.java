package jp.loioz.app.user.invoiceList.form;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.constant.SortConstant.InvoiceListSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.domain.condition.InvoiceListSearchCondition;
import jp.loioz.domain.condition.InvoiceListSortCondition;
import lombok.Data;

/**
 * 請求書一覧：検索条件フォーム
 */
@Data
public class InvoiceListSearchForm implements PagerForm {

	/** 検索条件エリアの開閉フラグ */
	private boolean isSearchConditionOpen;

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
	private String invoiceAmountFrom;

	/** 請求額To */
	private String invoiceAmountTo;

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
	private InvoiceListSortItem invoiceListSortItem;

	/** 一覧のソート順 */
	private SortOrder invoiceListSortOrder;

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
		this.setInvoiceListSortItem(InvoiceListSortItem.INVOICE_DATE);
		this.setInvoiceListSortOrder(SortOrder.DESC);

		// ページ番号を初期化
		this.defaultPage();

		// 各検索条件初期化
		this.searchWord = "";
		this.invoiceNo = "";
		this.invoiceIssueStatus = "";
		this.invoicePaymentStatus = "";
		this.invoiceDateFrom = "";
		this.invoiceDateTo = "";
		this.invoiceAmountFrom = "";
		this.invoiceAmountTo = "";
		this.dueDateFrom = "";
		this.dueDateTo = "";
		this.salesOwner = null;
		this.tantoLaywer = null;
		this.tantoJimu = null;
		this.invoiceType = "";
		this.invoiceMemo = "";
	}

	/**
	 * ページ番号を初期化
	 */
	public void defaultPage() {
		setDefaultPage();
	}

	/** 検索条件オブジェクトを作成 */
	public InvoiceListSearchCondition toInvoiceListSearchCondition() {
		return InvoiceListSearchCondition.builder()
				.searchWord(StringUtils.removeSpaceCharacter(this.searchWord))
				.invoiceNo(this.invoiceNo)
				.invoiceIssueStatus(this.invoiceIssueStatus)
				.invoicePaymentStatus(this.invoicePaymentStatus)
				.invoiceDateFrom(this.invoiceDateFrom)
				.invoiceDateTo(this.invoiceDateTo)
				.invoiceAmountFrom(StringUtils.isEmpty(this.invoiceAmountFrom) ? null : new BigDecimal(this.invoiceAmountFrom.replaceAll(",", "")))
				.invoiceAmountTo(StringUtils.isEmpty(this.invoiceAmountTo) ? null : new BigDecimal(this.invoiceAmountTo.replaceAll(",", "")))
				.dueDateFrom(this.dueDateFrom)
				.dueDateTo(this.dueDateTo)
				.invoiceType(this.invoiceType)
				.salesOwner(this.salesOwner)
				.tantoLaywer(this.tantoLaywer)
				.tantoJimu(this.tantoJimu)
				.invoiceMemo(this.invoiceMemo)
				.build();
	}

	/** ソート条件オブジェクトを作成 */
	public InvoiceListSortCondition toInvoiceListSortCondition() {
		return InvoiceListSortCondition.builder()
				.sortItem(this.invoiceListSortItem)
				.sortOrder(this.invoiceListSortOrder)
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

		// 請求番号があればカウントアップ
		if (!StringUtils.isEmpty(invoiceNo)) {
			count++;
		}

		// 発行ステータスがあればカウントアップ
		if (!StringUtils.isEmpty(invoiceIssueStatus)) {
			count++;
		}

		// 入金状況があればカウントアップ
		if (!StringUtils.isEmpty(invoicePaymentStatus)) {
			count++;
		}

		// 請求日があればカウントアップ
		if (!StringUtils.isEmpty(invoiceDateFrom) || !StringUtils.isEmpty(invoiceDateTo)) {
			count++;
		}

		// 請求額があればカウントアップ
		if (!StringUtils.isEmpty(invoiceAmountFrom) || !StringUtils.isEmpty(invoiceAmountTo)) {
			count++;
		}

		// 支払期日があればカウントアップ
		if (!StringUtils.isEmpty(dueDateFrom) || !StringUtils.isEmpty(dueDateTo)) {
			count++;
		}

		// 支払方法があればカウントアップ
		if (!StringUtils.isEmpty(invoiceType)) {
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
		if (!StringUtils.isEmpty(invoiceMemo)) {
			count++;
		}

		return count;
	}

}
