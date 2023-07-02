package jp.loioz.app.user.meiboList.form;

import java.time.LocalDate;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.constant.CommonConstant.PersonAttributeCd;
import jp.loioz.common.constant.SortConstant.SortOrder;
import jp.loioz.common.constant.meiboList.MeiboListConstant.AdvisorMeiboListSortItem;
import jp.loioz.common.constant.meiboList.MeiboListConstant.AllMeiboListSortItem;
import jp.loioz.common.constant.meiboList.MeiboListConstant.BengoshiMeiboListSortItem;
import jp.loioz.common.constant.meiboList.MeiboListConstant.CustomerAllMeiboListSortItem;
import jp.loioz.common.constant.meiboList.MeiboListConstant.CustomerHojinMeiboListSortItem;
import jp.loioz.common.constant.meiboList.MeiboListConstant.CustomerKojinMeiboListSortItem;
import jp.loioz.common.constant.meiboList.MeiboListConstant.MeiboMenu;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.condition.meiboList.AdvisorMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.AdvisorMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.AllMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.AllMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.BengoshiMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.BengoshiMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.CustomerAllMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.CustomerAllMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.CustomerHojinMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.CustomerHojinMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.CustomerKojinMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.CustomerKojinMeiboListSortCondition;
import lombok.Data;

/**
 * 名簿一覧：検索条件フォーム
 */
@Data
public class MeiboListSearchForm {

	/** 名簿メニュー */
	private String meiboMenuCd = MeiboMenu.ALL.getCd();

	/** ヘッダー検索 */
	private String quickSearch;

	/** 検索条件表示有無 */
	private boolean showConditions;

	/** 「すべて」の検索条件 */
	private AllMeiboListSearchForm allMeiboListSearchForm;

	/** 「顧客」の検索条件 */
	private CustomerAllMeiboListSearchForm customerAllMeiboListSearchForm;

	/** 「顧客_個人」の検索条件 */
	private CustomerKojinMeiboListSearchForm customerKojinMeiboListSearchForm;

	/** 「顧客_法人」の検索条件 */
	private CustomerHojinMeiboListSearchForm customerHojinMeiboListSearchForm;

	/** 「顧問」の検索条件 */
	private AdvisorMeiboListSearchForm advisorMeiboListSearchForm;

	/** 「弁護士」の検索条件 */
	private BengoshiMeiboListSearchForm bengoshiMeiboListSearchForm;

	/**
	 * フォームの初期化
	 */
	public void initForm() {
		this.meiboMenuCd = MeiboMenu.ALL.getCd();
		this.quickSearch = null;
		this.showConditions = false;
		this.allMeiboListSearchForm = new AllMeiboListSearchForm();
		this.customerAllMeiboListSearchForm = new CustomerAllMeiboListSearchForm();
		this.customerKojinMeiboListSearchForm = new CustomerKojinMeiboListSearchForm();
		this.customerHojinMeiboListSearchForm = new CustomerHojinMeiboListSearchForm();
		this.advisorMeiboListSearchForm = new AdvisorMeiboListSearchForm();
		this.bengoshiMeiboListSearchForm = new BengoshiMeiboListSearchForm();
	}
	
	/**
	 * フォームの初期化
	 */
	public void initForm(String meiboMenuCd) {
		this.meiboMenuCd = meiboMenuCd;
		this.quickSearch = null;
		this.showConditions = false;
		this.allMeiboListSearchForm = new AllMeiboListSearchForm();
		this.customerAllMeiboListSearchForm = new CustomerAllMeiboListSearchForm();
		this.customerKojinMeiboListSearchForm = new CustomerKojinMeiboListSearchForm();
		this.customerHojinMeiboListSearchForm = new CustomerHojinMeiboListSearchForm();
		this.advisorMeiboListSearchForm = new AdvisorMeiboListSearchForm();
		this.bengoshiMeiboListSearchForm = new BengoshiMeiboListSearchForm();
	}

	/**
	 * クイック検索時のフォーム初期化
	 */
	public void initQuickSearchForm() {
		this.meiboMenuCd = MeiboMenu.ALL.getCd();
		// 「すべて」画面に表示するため検索条件を初期化する
		this.allMeiboListSearchForm = new AllMeiboListSearchForm();
		this.customerAllMeiboListSearchForm = new CustomerAllMeiboListSearchForm();
		this.customerKojinMeiboListSearchForm = new CustomerKojinMeiboListSearchForm();
		this.customerHojinMeiboListSearchForm = new CustomerHojinMeiboListSearchForm();
		this.advisorMeiboListSearchForm = new AdvisorMeiboListSearchForm();
		this.bengoshiMeiboListSearchForm = new BengoshiMeiboListSearchForm();
	}

	/**
	 * ページ番号を初期化
	 */
	public void defaultPage() {
		MeiboMenu meiboMenu = MeiboMenu.of(this.meiboMenuCd);

		switch (meiboMenu) {
		case ALL:
			this.allMeiboListSearchForm.setDefaultPage();
			break;
		case CUSTOMER_ALL:
			this.customerAllMeiboListSearchForm.setDefaultPage();
			break;
		case CUSTOMER_KOJIN:
			this.customerKojinMeiboListSearchForm.setDefaultPage();
			break;
		case CUSTOMER_HOJIN:
			this.customerHojinMeiboListSearchForm.setDefaultPage();
			break;
		case ADVISOR:
			this.advisorMeiboListSearchForm.setDefaultPage();
			break;
		case BENGOSHI:
			this.bengoshiMeiboListSearchForm.setDefaultPage();
			break;
		default:
			break;
		}
	}

	/**
	 * クイック検索のパラメータを削除
	 */
	public void removeQuickSearchParam() {
		this.quickSearch = null;
	}

	/**
	 * 「すべて」の検索条件フォーム
	 */
	@Data
	public static class AllMeiboListSearchForm implements PagerForm {

		/** キーワード検索 */
		private String searchWord;
		
		/** 名簿ID */
		private String personId;

		/** 名前検索 */
		private String keywords;

		/** 属性 */
		private String personAttributeCd;

		/** 区分 */
		private String kubun;

		/** 住所 */
		private String address;

		/** 電話番号 */
		private String telNo;

		/** メールアドレス */
		private String mailAddress;

		/** 登録日From */
		private String customerCreateDateFrom;

		/** 登録日To */
		private String customerCreateDateTo;

		/** ページ番号 */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/** ソート項目 */
		private AllMeiboListSortItem sortItem = AllMeiboListSortItem.PERSON_ID;

		/** ソート順 */
		private SortOrder sortOrder = SortOrder.DESC;

		/** 登録日FromのLocalDate型取得 */
		public LocalDate getCustomerCreateLocalDateFrom() {
			try {
				return DateUtils.parseToLocalDate(this.customerCreateDateFrom, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 登録日ToのLocalDate型取得 */
		public LocalDate getCustomerCreateLocalDateTo() {
			try {
				return DateUtils.parseToLocalDate(this.customerCreateDateTo, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 検索条件オブジェクトを作成 */
		public AllMeiboListSearchCondition toAllMeiboListSearchCondition() {
			return AllMeiboListSearchCondition.builder()
					.searchWord(this.searchWord)
					.personId(this.personId)
					.keyWords(StringUtils.removeSpaceCharacter(this.keywords))
					.customerType(CustomerType.of(this.kubun))
					.personAttributeCd(PersonAttributeCd.of(this.personAttributeCd))
					.address(this.address)
					.telNo(StringUtils.removeChars(this.telNo, CommonConstant.HYPHEN))
					.mailAddress(this.mailAddress)
					.customerCreateDateFrom(this.getCustomerCreateLocalDateFrom())
					.customerCreateDateTo(this.getCustomerCreateLocalDateTo())
					.build();
		}

		/** ソート条件オブジェクトを作成 */
		public AllMeiboListSortCondition toAllMeiboListSortCondition() {
			return AllMeiboListSortCondition.builder()
					.sortItem(this.sortItem)
					.sortOrder(this.sortOrder)
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
			// 検索ワードがあればカウントアップ
			if (!StringUtils.isEmpty(searchWord)) {
				count++;
			}
			
			// 名簿IDがあればカウントアップ
			if (!StringUtils.isEmpty(personId)) {
				count++;
			}

			// 名前があればカウントアップ
			if (!StringUtils.isEmpty(keywords)) {
				count++;
			}

			// 区分があればカウントアップ
			if (!StringUtils.isEmpty(kubun)) {
				count++;
			}

			// 属性があればカウントアップ
			if (!StringUtils.isEmpty(personAttributeCd)) {
				count++;
			}

			// 住所があればカウントアップ
			if (!StringUtils.isEmpty(address)) {
				count++;
			}

			// 電話番号があればカウントアップ
			if (!StringUtils.isEmpty(telNo)) {
				count++;
			}

			// メールアドレスがあればカウントアップ
			if (!StringUtils.isEmpty(mailAddress)) {
				count++;
			}

			// 登録日があればカウントアップ
			if (!StringUtils.isEmpty(customerCreateDateFrom) || !StringUtils.isEmpty(customerCreateDateTo)) {
				count++;
			}

			return count;
		}

	}

	/**
	 * 「顧客」の検索条件フォーム
	 */
	@Data
	public static class CustomerAllMeiboListSearchForm implements PagerForm {

		/** キーワード検索 */
		private String searchWord;
		
		/** 名簿ID */
		private String personId;

		/** 名前検索 */
		private String keywords;

		/** 住所 */
		private String address;

		/** 電話番号 */
		private String telNo;

		/** メールアドレス */
		private String mailAddress;

		/** 登録日From */
		private String customerCreateDateFrom;

		/** 登録日To */
		private String customerCreateDateTo;

		/** 特記事項 */
		private String remarks;

		/** ページ番号 */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/** ソート項目 */
		private CustomerAllMeiboListSortItem sortItem = CustomerAllMeiboListSortItem.PERSON_ID;

		/** ソート順 */
		private SortOrder sortOrder = SortOrder.DESC;

		/** 登録日FromのLocalDate型取得 */
		public LocalDate getCustomerCreateLocalDateFrom() {
			try {
				return DateUtils.parseToLocalDate(this.customerCreateDateFrom, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 登録日ToのLocalDate型取得 */
		public LocalDate getCustomerCreateLocalDateTo() {
			try {
				return DateUtils.parseToLocalDate(this.customerCreateDateTo, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 検索条件オブジェクトを作成 */
		public CustomerAllMeiboListSearchCondition toCustomerAllMeiboListSearchCondition() {
			return CustomerAllMeiboListSearchCondition.builder()
					.searchWord(this.searchWord)
					.personId(this.personId)
					.keyWords(StringUtils.removeSpaceCharacter(this.keywords))
					.address(this.address)
					.telNo(StringUtils.removeChars(this.telNo, CommonConstant.HYPHEN))
					.mailAddress(this.mailAddress)
					.customerCreateDateFrom(this.getCustomerCreateLocalDateFrom())
					.customerCreateDateTo(this.getCustomerCreateLocalDateTo())
					.remarks(this.remarks)
					.build();
		}

		/** ソート条件オブジェクトを作成 */
		public CustomerAllMeiboListSortCondition toCustomerAllMeiboListSortCondition() {
			return CustomerAllMeiboListSortCondition.builder()
					.sortItem(this.sortItem)
					.sortOrder(this.sortOrder)
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
			// 検索ワードがあればカウントアップ
			if (!StringUtils.isEmpty(searchWord)) {
				count++;
			}
			
			// 名簿Noがあればカウントアップ
			if (!StringUtils.isEmpty(personId)) {
				count++;
			}

			// 名前があればカウントアップ
			if (!StringUtils.isEmpty(keywords)) {
				count++;
			}

			// 住所があればカウントアップ
			if (!StringUtils.isEmpty(address)) {
				count++;
			}

			// 電話番号があればカウントアップ
			if (!StringUtils.isEmpty(telNo)) {
				count++;
			}

			// 登録日があればカウントアップ
			if (!StringUtils.isEmpty(customerCreateDateFrom) || !StringUtils.isEmpty(customerCreateDateTo)) {
				count++;
			}

			// 特記事項があればカウントアップ
			if (!StringUtils.isEmpty(remarks)) {
				count++;
			}

			return count;
		}

	}

	/**
	 * 「顧客_個人」の検索条件フォーム
	 */
	@Data
	public static class CustomerKojinMeiboListSearchForm implements PagerForm {

		/** キーワード検索 */
		private String searchWord;
		
		/** 名簿ID */
		private String personId;

		/** 名前検索 */
		private String keywords;

		/** 住所 */
		private String address;

		/** 電話番号 */
		private String telNo;

		/** メールアドレス */
		private String mailAddress;

		/** 登録日From */
		private String customerCreateDateFrom;

		/** 登録日To */
		private String customerCreateDateTo;

		/** 特記事項 */
		private String remarks;

		/** ページ番号 */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/** ソート項目 */
		private CustomerKojinMeiboListSortItem sortItem = CustomerKojinMeiboListSortItem.PERSON_ID;

		/** ソート順 */
		private SortOrder sortOrder = SortOrder.DESC;

		/** 登録日FromのLocalDate型取得 */
		public LocalDate getCustomerCreateLocalDateFrom() {
			try {
				return DateUtils.parseToLocalDate(this.customerCreateDateFrom, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 登録日ToのLocalDate型取得 */
		public LocalDate getCustomerCreateLocalDateTo() {
			try {
				return DateUtils.parseToLocalDate(this.customerCreateDateTo, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 検索条件オブジェクトを作成 */
		public CustomerKojinMeiboListSearchCondition toCustomerKojinMeiboListSearchCondition() {
			return CustomerKojinMeiboListSearchCondition.builder()
					.searchWord(this.searchWord)
					.personId(this.personId)
					.keyWords(StringUtils.removeSpaceCharacter(this.keywords))
					.address(this.address)
					.telNo(StringUtils.removeChars(this.telNo, CommonConstant.HYPHEN))
					.mailAddress(this.mailAddress)
					.customerCreateDateFrom(this.getCustomerCreateLocalDateFrom())
					.customerCreateDateTo(this.getCustomerCreateLocalDateTo())
					.remarks(this.remarks)
					.build();
		}

		/** ソート条件オブジェクトを作成 */
		public CustomerKojinMeiboListSortCondition toCustomerKojinMeiboListSortCondition() {
			return CustomerKojinMeiboListSortCondition.builder()
					.sortItem(this.sortItem)
					.sortOrder(this.sortOrder)
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
			// 検索ワードがあればカウントアップ
			if (!StringUtils.isEmpty(searchWord)) {
				count++;
			}
			
			// 名簿IDがあればカウントアップ
			if (!StringUtils.isEmpty(personId)) {
				count++;
			}

			// 名前があればカウントアップ
			if (!StringUtils.isEmpty(keywords)) {
				count++;
			}

			// 住所があればカウントアップ
			if (!StringUtils.isEmpty(address)) {
				count++;
			}

			// 電話番号があればカウントアップ
			if (!StringUtils.isEmpty(telNo)) {
				count++;
			}

			// メールアドレスがあればカウントアップ
			if (!StringUtils.isEmpty(mailAddress)) {
				count++;
			}

			// 登録日があればカウントアップ
			if (!StringUtils.isEmpty(customerCreateDateFrom) || !StringUtils.isEmpty(customerCreateDateTo)) {
				count++;
			}

			// 特記事項があればカウントアップ
			if (!StringUtils.isEmpty(remarks)) {
				count++;
			}

			return count;
		}

	}

	/**
	 * 「顧客_法人」の検索条件フォーム
	 */
	@Data
	public static class CustomerHojinMeiboListSearchForm implements PagerForm {

		/** キーワード検索 */
		private String searchWord;
		
		/** 名簿ID */
		private String personId;

		/** 名前検索 */
		private String keywords;

		/** 住所 */
		private String address;

		/** 電話番号 */
		private String telNo;

		/** メールアドレス */
		private String mailAddress;

		/** 登録日From */
		private String customerCreateDateFrom;

		/** 登録日To */
		private String customerCreateDateTo;

		/** 特記事項 */
		private String remarks;

		/** ページ番号 */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/** ソート項目 */
		private CustomerHojinMeiboListSortItem sortItem = CustomerHojinMeiboListSortItem.PERSON_ID;

		/** ソート順 */
		private SortOrder sortOrder = SortOrder.DESC;

		/** 登録日FromのLocalDate型取得 */
		public LocalDate getCustomerCreateLocalDateFrom() {
			try {
				return DateUtils.parseToLocalDate(this.customerCreateDateFrom, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 登録日ToのLocalDate型取得 */
		public LocalDate getCustomerCreateLocalDateTo() {
			try {
				return DateUtils.parseToLocalDate(this.customerCreateDateTo, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 検索条件オブジェクトを作成 */
		public CustomerHojinMeiboListSearchCondition toCustomerHojinMeiboListSearchCondition() {
			return CustomerHojinMeiboListSearchCondition.builder()
					.searchWord(this.searchWord)
					.personId(this.personId)
					.keyWords(StringUtils.removeSpaceCharacter(this.keywords))
					.address(this.address)
					.telNo(StringUtils.removeChars(this.telNo, CommonConstant.HYPHEN))
					.mailAddress(this.mailAddress)
					.customerCreateDateFrom(this.getCustomerCreateLocalDateFrom())
					.customerCreateDateTo(this.getCustomerCreateLocalDateTo())
					.remarks(this.remarks)
					.build();
		}

		/** ソート条件オブジェクトを作成 */
		public CustomerHojinMeiboListSortCondition toCustomerHojinMeiboListSortCondition() {
			return CustomerHojinMeiboListSortCondition.builder()
					.sortItem(this.sortItem)
					.sortOrder(this.sortOrder)
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
			// 検索ワードがあればカウントアップ
			if (!StringUtils.isEmpty(searchWord)) {
				count++;
			}
			
			// 名簿IDがあればカウントアップ
			if (!StringUtils.isEmpty(personId)) {
				count++;
			}

			// 名前があればカウントアップ
			if (!StringUtils.isEmpty(keywords)) {
				count++;
			}

			// 住所があればカウントアップ
			if (!StringUtils.isEmpty(address)) {
				count++;
			}

			// 電話番号があればカウントアップ
			if (!StringUtils.isEmpty(telNo)) {
				count++;
			}

			// メールアドレスがあればカウントアップ
			if (!StringUtils.isEmpty(mailAddress)) {
				count++;
			}

			// 登録日があればカウントアップ
			if (!StringUtils.isEmpty(customerCreateDateFrom) || !StringUtils.isEmpty(customerCreateDateTo)) {
				count++;
			}

			// 特記事項があればカウントアップ
			if (!StringUtils.isEmpty(remarks)) {
				count++;
			}

			return count;
		}

	}

	/**
	 * 「顧問」の検索条件フォーム
	 */
	@Data
	public static class AdvisorMeiboListSearchForm implements PagerForm {

		/** キーワード検索 */
		private String searchWord;
		
		/** 名簿No */
		private String personId;

		/** 名簿ID */
		private String customerId;

		/** 名前検索 */
		private String keywords;

		/** 住所 */
		private String address;

		/** 電話番号 */
		private String telNo;

		/** メールアドレス */
		private String mailAddress;

		/** 登録日From */
		private String customerCreateDateFrom;

		/** 登録日To */
		private String customerCreateDateTo;

		/** 特記事項 */
		private String remarks;

		/** ページ番号 */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/** ソート項目 */
		private AdvisorMeiboListSortItem sortItem = AdvisorMeiboListSortItem.PERSON_ID;

		/** ソート順 */
		private SortOrder sortOrder = SortOrder.DESC;

		/** 登録日FromのLocalDate型取得 */
		public LocalDate getCustomerCreateLocalDateFrom() {
			try {
				return DateUtils.parseToLocalDate(this.customerCreateDateFrom, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 登録日ToのLocalDate型取得 */
		public LocalDate getCustomerCreateLocalDateTo() {
			try {
				return DateUtils.parseToLocalDate(this.customerCreateDateTo, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 検索条件オブジェクトを作成 */
		public AdvisorMeiboListSearchCondition toAdvisorMeiboListSearchCondition() {
			return AdvisorMeiboListSearchCondition.builder()
					.searchWord(this.searchWord)
					.personId(this.personId)
					.keyWords(StringUtils.removeSpaceCharacter(this.keywords))
					.address(this.address)
					.telNo(StringUtils.removeChars(this.telNo, CommonConstant.HYPHEN))
					.mailAddress(this.mailAddress)
					.customerCreateDateFrom(this.getCustomerCreateLocalDateFrom())
					.customerCreateDateTo(this.getCustomerCreateLocalDateTo())
					.remarks(this.remarks)
					.build();
		}

		/** ソート条件オブジェクトを作成 */
		public AdvisorMeiboListSortCondition toAdvisorMeiboListSortCondition() {
			return AdvisorMeiboListSortCondition.builder()
					.sortItem(this.sortItem)
					.sortOrder(this.sortOrder)
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
			// 検索ワードがあればカウントアップ
			if (!StringUtils.isEmpty(searchWord)) {
				count++;
			}
			
			// 名簿Noがあればカウントアップ
			if (!StringUtils.isEmpty(personId)) {
				count++;
			}

			// 名簿IDがあればカウントアップ
			if (!StringUtils.isEmpty(customerId)) {
				count++;
			}

			// 名前があればカウントアップ
			if (!StringUtils.isEmpty(keywords)) {
				count++;
			}

			// 住所があればカウントアップ
			if (!StringUtils.isEmpty(address)) {
				count++;
			}

			// 電話番号があればカウントアップ
			if (!StringUtils.isEmpty(telNo)) {
				count++;
			}

			// メールアドレスがあればカウントアップ
			if (!StringUtils.isEmpty(mailAddress)) {
				count++;
			}

			// 登録日があればカウントアップ
			if (!StringUtils.isEmpty(customerCreateDateFrom) || !StringUtils.isEmpty(customerCreateDateTo)) {
				count++;
			}

			// 特記事項があればカウントアップ
			if (!StringUtils.isEmpty(remarks)) {
				count++;
			}

			return count;
		}

	}

	/**
	 * 「弁護士」の検索条件フォーム
	 */
	@Data
	public static class BengoshiMeiboListSearchForm implements PagerForm {

		/** キーワード検索 */
		private String searchWord;
		
		/** 名簿No */
		private String personId;

		/** 名前 */
		private String keywords;

		/** 事務所名 */
		private String jimushoName;

		/** 住所 */
		private String address;

		/** 電話番号 */
		private String telNo;

		/** メールアドレス */
		private String mailAddress;

		/** 登録日From */
		private String createDateFrom;

		/** 登録日To */
		private String createDateTo;

		/** 特記事項 */
		private String remarks;

		/** ページ番号 */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/** ソート項目 */
		private BengoshiMeiboListSortItem sortItem = BengoshiMeiboListSortItem.PERSON_ID;

		/** ソート順 */
		private SortOrder sortOrder = SortOrder.DESC;

		/** 登録日FromのLocalDate型取得 */
		public LocalDate getCreateLocalDateFrom() {
			try {
				return DateUtils.parseToLocalDate(this.createDateFrom, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 登録日ToのLocalDate型取得 */
		public LocalDate getCreateLocalDateTo() {
			try {
				return DateUtils.parseToLocalDate(this.createDateTo, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 検索条件オブジェクトを作成 */
		public BengoshiMeiboListSearchCondition toBengoshiMeiboListSearchCondition() {
			return BengoshiMeiboListSearchCondition.builder()
					.searchWord(this.searchWord)
					.personId(this.personId)
					.keyWords(StringUtils.removeSpaceCharacter(this.keywords))
					.jimushoName(this.jimushoName)
					.address(this.address)
					.telNo(StringUtils.removeChars(this.telNo, CommonConstant.HYPHEN))
					.mailAddress(this.mailAddress)
					.createDateFrom(this.getCreateLocalDateFrom())
					.createDateTo(this.getCreateLocalDateTo())
					.remarks(this.remarks)
					.build();
		}

		/** ソート条件オブジェクトを作成 */
		public BengoshiMeiboListSortCondition toBengoshiMeiboListSortCondition() {
			return BengoshiMeiboListSortCondition.builder()
					.sortItem(this.sortItem)
					.sortOrder(this.sortOrder)
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
			// 検索ワードがあればカウントアップ
			if (!StringUtils.isEmpty(searchWord)) {
				count++;
			}
			
			// 名簿Noがあればカウントアップ
			if (!StringUtils.isEmpty(personId)) {
				count++;
			}

			// 名前があればカウントアップ
			if (!StringUtils.isEmpty(keywords)) {
				count++;
			}

			// 事務所名があればカウントアップ
			if (!StringUtils.isEmpty(jimushoName)) {
				count++;
			}

			// 住所があればカウントアップ
			if (!StringUtils.isEmpty(address)) {
				count++;
			}

			// 電話番号があればカウントアップ
			if (!StringUtils.isEmpty(telNo)) {
				count++;
			}

			// メールアドレスがあればカウントアップ
			if (!StringUtils.isEmpty(mailAddress)) {
				count++;
			}

			// 登録日があればカウントアップ
			if (!StringUtils.isEmpty(createDateFrom) || !StringUtils.isEmpty(createDateTo)) {
				count++;
			}

			// 特記事項があればカウントアップ
			if (!StringUtils.isEmpty(remarks)) {
				count++;
			}

			return count;
		}

	}

}
