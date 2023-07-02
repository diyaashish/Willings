package jp.loioz.app.user.ankenList.form;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.AnkenListMenu;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.constant.SortConstant.AnkenListSortItem;
import jp.loioz.common.constant.SortConstant.SaibanListSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.groups.AdvisorAnkenListSearchDate;
import jp.loioz.common.validation.groups.AllAnkenListSearchDate;
import jp.loioz.common.validation.groups.AnkenListInitSearch;
import jp.loioz.common.validation.groups.KeijiSaibanListSearchDate;
import jp.loioz.common.validation.groups.MinjiSaibanListSearchDate;
import jp.loioz.common.validation.groups.MyAnkenListSearchDate;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.MAccountEntity;
import lombok.Data;

/**
 * 案件一覧画面の検索条件フォームクラス
 */
@Data
public class AnkenListSearchForm {

	/** 選択中案件一覧メニュー */
	private String selectedAnkenListMenu;

	/** ヘッダー検索 */
	private String quickSearch;
	
	/** 検索条件エリアの開閉フラグ */
	private boolean isSearchConditionOpen;

	/** 検索条件担当弁護士のプルダウン情報 */
	private List<MAccountEntity> tantoLawyerList;

	/** 検索条件担当事務のプルダウン情報 */
	private List<MAccountEntity> tantoJimuList;

	/** メニュー：すべての案件検索条件フォーム */
	@Valid
	private AllAnkenListSearchForm allAnkenListSearchForm;

	/** メニュー：自分の担当案件検索条件フォーム */
	@Valid
	private MyAnkenListSearchForm myAnkenListSearchForm;

	/** メニュー：顧問案件検索条件フォーム */
	@Valid
	private AdvisorAnkenListSearchForm advisorAnkenListSearchForm;

	/** メニュー：民事裁判案件検索条件フォーム */
	@Valid
	private MinjiSaibanListSearchForm minjiSaibanListSearchForm;

	/** メニュー：刑事裁判案件検索条件フォーム */
	@Valid
	private KeijiSaibanListSearchForm keijiSaibanListSearchForm;

	/**
	 * 検索条件をクリアする
	 */
	public void clearSearchForm() {
		selectedAnkenListMenu = null;
		isSearchConditionOpen = false;
		allAnkenListSearchForm = null;
		myAnkenListSearchForm = null;
		advisorAnkenListSearchForm = null;
		minjiSaibanListSearchForm = null;
		keijiSaibanListSearchForm = null;
	}

	/**
	 * 案件一覧検索条件の初期設定
	 */
	public void initForm() {

		// 初期画面は「すべての案件」を表示する
		selectedAnkenListMenu = AnkenListMenu.All_ANKEN_LIST.getCd();

		// クイック検索ワード
		quickSearch = null;
		
		// 検索条件エリアは閉じた状態
		isSearchConditionOpen = false;

		// 担当弁護士プルダウン情報
		tantoLawyerList = new ArrayList<MAccountEntity>();

		// 担当事務プルダウン情報
		tantoJimuList = new ArrayList<MAccountEntity>();

		// 検索条件フォーム新規作成
		allAnkenListSearchForm = new AllAnkenListSearchForm();
		myAnkenListSearchForm = new MyAnkenListSearchForm();
		advisorAnkenListSearchForm = new AdvisorAnkenListSearchForm();
		minjiSaibanListSearchForm = new MinjiSaibanListSearchForm();
		keijiSaibanListSearchForm = new KeijiSaibanListSearchForm();

		// 「すべての案件」の検索条件フォームの案件ステータスに「終了以外」をセットする
		allAnkenListSearchForm.setAnkenStatus(CommonConstant.ANKEN_STATUS_INCOMPLETE_CD);

		// 「自分の担当案件」の検索条件フォームの案件ステータスに「終了以外」をセットする
		myAnkenListSearchForm.setAnkenStatus(CommonConstant.ANKEN_STATUS_INCOMPLETE_CD);

		// 「顧問案件」の検索条件フォームの案件ステータスに「終了以外」をセットする
		advisorAnkenListSearchForm.setAnkenStatus(CommonConstant.ANKEN_STATUS_INCOMPLETE_CD);

		// 「民事裁判」の検索条件フォームの裁判手続きに「終了以外」をセットする
		minjiSaibanListSearchForm.setSaibanStatus(CommonConstant.SAIBAN_STATUS_INCOMPLETE_CD);

		// 「刑事裁判」の検索条件フォームの裁判手続きに「終了以外」をセットする
		keijiSaibanListSearchForm.setSaibanStatus(CommonConstant.SAIBAN_STATUS_INCOMPLETE_CD);

		// 初期表示の一覧ソート順
		allAnkenListSearchForm.setAllAnkenListSortKey(AnkenListSortItem.ANKEN_ID);
		allAnkenListSearchForm.setAllAnkenListSortOrder(SortOrder.DESC);
		myAnkenListSearchForm.setMyAnkenListSortKey(AnkenListSortItem.ANKEN_ID);
		myAnkenListSearchForm.setMyAnkenListSortOrder(SortOrder.DESC);
		advisorAnkenListSearchForm.setAdvisorAnkenListSortKey(AnkenListSortItem.ANKEN_ID);
		advisorAnkenListSearchForm.setAdvisorAnkenListSortOrder(SortOrder.DESC);
		minjiSaibanListSearchForm.setMinjiSaibanListSortKey(SaibanListSortItem.ANKEN_ID);
		minjiSaibanListSearchForm.setMinjiSaibanListSortOrder(SortOrder.DESC);
		keijiSaibanListSearchForm.setKeijiSaibanListSortKey(SaibanListSortItem.ANKEN_ID);
		keijiSaibanListSearchForm.setKeijiSaibanListSortOrder(SortOrder.DESC);
	}
	
	/**
	 * クイック検索時のフォーム初期化
	 */
	public void initQuickSearchForm() {
		// 「すべての案件」を表示する
		selectedAnkenListMenu = AnkenListMenu.All_ANKEN_LIST.getCd();
		
		// 検索条件エリアは閉じた状態
		isSearchConditionOpen = false;

		// 担当弁護士プルダウン情報
		tantoLawyerList = new ArrayList<MAccountEntity>();

		// 担当事務プルダウン情報
		tantoJimuList = new ArrayList<MAccountEntity>();

		// 検索条件フォーム新規作成
		allAnkenListSearchForm = new AllAnkenListSearchForm();
		myAnkenListSearchForm = new MyAnkenListSearchForm();
		advisorAnkenListSearchForm = new AdvisorAnkenListSearchForm();
		minjiSaibanListSearchForm = new MinjiSaibanListSearchForm();
		keijiSaibanListSearchForm = new KeijiSaibanListSearchForm();

		// 「すべての案件」の検索条件フォームの案件ステータスに「終了以外」をセットする
		allAnkenListSearchForm.setAnkenStatus(CommonConstant.ANKEN_STATUS_INCOMPLETE_CD);

		// 「自分の担当案件」の検索条件フォームの案件ステータスに「終了以外」をセットする
		myAnkenListSearchForm.setAnkenStatus(CommonConstant.ANKEN_STATUS_INCOMPLETE_CD);

		// 「顧問案件」の検索条件フォームの案件ステータスに「終了以外」をセットする
		advisorAnkenListSearchForm.setAnkenStatus(CommonConstant.ANKEN_STATUS_INCOMPLETE_CD);

		// 「民事裁判」の検索条件フォームの裁判手続きに「終了以外」をセットする
		minjiSaibanListSearchForm.setSaibanStatus(CommonConstant.SAIBAN_STATUS_INCOMPLETE_CD);

		// 「刑事裁判」の検索条件フォームの裁判手続きに「終了以外」をセットする
		keijiSaibanListSearchForm.setSaibanStatus(CommonConstant.SAIBAN_STATUS_INCOMPLETE_CD);

		// 初期表示の一覧ソート順
		allAnkenListSearchForm.setAllAnkenListSortKey(AnkenListSortItem.ANKEN_ID);
		allAnkenListSearchForm.setAllAnkenListSortOrder(SortOrder.DESC);
		myAnkenListSearchForm.setMyAnkenListSortKey(AnkenListSortItem.ANKEN_ID);
		myAnkenListSearchForm.setMyAnkenListSortOrder(SortOrder.DESC);
		advisorAnkenListSearchForm.setAdvisorAnkenListSortKey(AnkenListSortItem.ANKEN_ID);
		advisorAnkenListSearchForm.setAdvisorAnkenListSortOrder(SortOrder.DESC);
		minjiSaibanListSearchForm.setMinjiSaibanListSortKey(SaibanListSortItem.ANKEN_ID);
		minjiSaibanListSearchForm.setMinjiSaibanListSortOrder(SortOrder.DESC);
		keijiSaibanListSearchForm.setKeijiSaibanListSortKey(SaibanListSortItem.ANKEN_ID);
		keijiSaibanListSearchForm.setKeijiSaibanListSortOrder(SortOrder.DESC);
	}

	/**
	 * すべての案件一覧の検索条件初期設定
	 */
	public void initAllAnkenListSearchForm() {

		// 分野のセレクトボックスのデータを取り出して初期化後に再セットする
		List<BunyaDto> bunyaList = allAnkenListSearchForm.getBunyaList();

		// 検索条件フォーム新規作成
		allAnkenListSearchForm = new AllAnkenListSearchForm();

		// 「すべての案件」の検索条件フォームの案件ステータスに「終了以外」をセットする
		allAnkenListSearchForm.setAnkenStatus(CommonConstant.ANKEN_STATUS_INCOMPLETE_CD);

		// 分野のセレクトボックスデータをセットする
		allAnkenListSearchForm.setBunyaList(bunyaList);

		// 初期表示の一覧ソート順
		allAnkenListSearchForm.setAllAnkenListSortKey(AnkenListSortItem.ANKEN_ID);
		allAnkenListSearchForm.setAllAnkenListSortOrder(SortOrder.DESC);
	}

	/**
	 * 自分の担当案件一覧の検索条件初期設定
	 */
	public void initMyAnkenListSearchForm() {

		// 分野のセレクトボックスのデータを取り出して初期化後に再セットする
		List<BunyaDto> bunyaList = myAnkenListSearchForm.getBunyaList();

		// 検索条件フォーム新規作成
		myAnkenListSearchForm = new MyAnkenListSearchForm();

		// 「自分の担当案件」の検索条件フォームの案件ステータスに「終了以外」をセットする
		myAnkenListSearchForm.setAnkenStatus(CommonConstant.ANKEN_STATUS_INCOMPLETE_CD);

		// ログインユーザのアカウントタイプによって検索条件の担当弁護士、担当時をログインユーザに設定
		AccountType loginUserType = SessionUtils.getAccountType();
		if (AccountType.LAWYER.equalsByCode(loginUserType.getCd())) {
			myAnkenListSearchForm.setTantoLaywer(Long.valueOf(loginUserType.getCd()));
		} else if (AccountType.JIMU.equalsByCode(loginUserType.getCd())) {
			myAnkenListSearchForm.setTantoJimu(Long.valueOf(loginUserType.getCd()));
		}

		// 分野のセレクトボックスデータをセットする
		myAnkenListSearchForm.setBunyaList(bunyaList);

		// 初期表示の一覧ソート順
		myAnkenListSearchForm.setMyAnkenListSortKey(AnkenListSortItem.ANKEN_ID);
		myAnkenListSearchForm.setMyAnkenListSortOrder(SortOrder.DESC);
	}

	/**
	 * 顧問取引先の案件の検索条件初期設定
	 */
	public void initAdvisorAnkenListSearchForm() {

		// 分野のセレクトボックスのデータを取り出して初期化後に再セットする
		List<BunyaDto> bunyaList = advisorAnkenListSearchForm.getBunyaList();

		// 検索条件フォーム新規作成
		advisorAnkenListSearchForm = new AdvisorAnkenListSearchForm();

		// 「顧問案件」の検索条件フォームの案件ステータスに「終了以外」をセットする
		advisorAnkenListSearchForm.setAnkenStatus(CommonConstant.ANKEN_STATUS_INCOMPLETE_CD);

		// ログインユーザのアカウントタイプによって検索条件の担当弁護士、担当時をログインユーザに設定
		AccountType loginUserType = SessionUtils.getAccountType();
		if (AccountType.LAWYER.equalsByCode(loginUserType.getCd())) {
			advisorAnkenListSearchForm.setTantoLaywer(Long.valueOf(loginUserType.getCd()));
		} else if (AccountType.JIMU.equalsByCode(loginUserType.getCd())) {
			advisorAnkenListSearchForm.setTantoJimu(Long.valueOf(loginUserType.getCd()));
		}

		// 分野のセレクトボックスデータをセットする
		advisorAnkenListSearchForm.setBunyaList(bunyaList);

		// 初期表示の一覧ソート順
		advisorAnkenListSearchForm.setAdvisorAnkenListSortKey(AnkenListSortItem.ANKEN_ID);
		advisorAnkenListSearchForm.setAdvisorAnkenListSortOrder(SortOrder.DESC);
	}

	/**
	 * 民事裁判一覧の検索条件初期設定
	 */
	public void initMinjiSaibanListSearchForm() {

		// 分野のセレクトボックスのデータを取り出して初期化後に再セットする
		List<BunyaDto> bunyaList = minjiSaibanListSearchForm.getBunyaList();

		// 検索条件フォーム新規作成
		minjiSaibanListSearchForm = new MinjiSaibanListSearchForm();

		// 「民事裁判」の検索条件フォームの裁判手続きに「終了以外」をセットする
		minjiSaibanListSearchForm.setSaibanStatus(CommonConstant.SAIBAN_STATUS_INCOMPLETE_CD);

		// 分野のセレクトボックスデータをセットする
		minjiSaibanListSearchForm.setBunyaList(bunyaList);

		// 初期表示の一覧ソート順
		minjiSaibanListSearchForm.setMinjiSaibanListSortKey(SaibanListSortItem.ANKEN_ID);
		minjiSaibanListSearchForm.setMinjiSaibanListSortOrder(SortOrder.DESC);
	}

	/**
	 * 刑事裁判一覧の検索条件初期設定
	 */
	public void initKeijiSaibanListSearchForm() {

		// 分野のセレクトボックスのデータを取り出して初期化後に再セットする
		List<BunyaDto> bunyaList = keijiSaibanListSearchForm.getBunyaList();

		// 検索条件フォーム新規作成
		keijiSaibanListSearchForm = new KeijiSaibanListSearchForm();

		// 「刑事裁判」の検索条件フォームの裁判手続きに「終了以外」をセットする
		keijiSaibanListSearchForm.setSaibanStatus(CommonConstant.SAIBAN_STATUS_INCOMPLETE_CD);

		// 分野のセレクトボックスデータをセットする
		keijiSaibanListSearchForm.setBunyaList(bunyaList);

		// 初期表示の一覧ソート順
		keijiSaibanListSearchForm.setKeijiSaibanListSortKey(SaibanListSortItem.ANKEN_ID);
		keijiSaibanListSearchForm.setKeijiSaibanListSortOrder(SortOrder.DESC);
	}

	/**
	 * 選択中のメニュー名を取得します
	 * 
	 * @return
	 */
	public String getPageTitle() {
		if (StringUtils.isEmpty(selectedAnkenListMenu)) {
			return "";
		} else {
			return AnkenListMenu.of(selectedAnkenListMenu).getVal();
		}
	}

	/**
	 * 案件系のメニューを選択しているか判定します<br>
	 * 案件メニューであれば true 、裁判メニューであればfalse
	 * 
	 * @return
	 */
	public boolean isAnkenListMenu() {
		if (AnkenListMenu.All_ANKEN_LIST.equalsByCode(selectedAnkenListMenu)) {
			return true;
		} else if (AnkenListMenu.MY_ANKEN_LIST.equalsByCode(selectedAnkenListMenu)) {
			return true;
		} else if (AnkenListMenu.ADVISOR_ANKEN_LIST.equalsByCode(selectedAnkenListMenu)) {
			return true;
		}
		return false;
	}

	/**
	 * クイック検索のパラメータを削除
	 */
	public void removeQuickSearchParam() {
		this.quickSearch = null;
	}
	
	/**
	 * メニュー：すべての案件検索条件フォーム
	 */
	@Data
	public static class AllAnkenListSearchForm implements PagerForm {

		/** 案件ステータス */
		private String ankenStatus;

		/** 検索ワード */
		private String searchWord;

		/** 分野 */
		private String bunyaId;

		/** 分野の一覧情報 */
		private List<BunyaDto> bunyaList;

		/** 担当弁護士 */
		private Long tantoLaywer;

		/** 担当事務 */
		private Long tantoJimu;

		/** 案件登録日From **/
		@LocalDatePattern(groups = {AnkenListInitSearch.class, AllAnkenListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String ankenCreateDateFrom;

		/** 案件登録日To **/
		@LocalDatePattern(groups = {AnkenListInitSearch.class, AllAnkenListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String ankenCreateDateTo;

		/** 受任日From */
		@LocalDatePattern(groups = {AnkenListInitSearch.class, AllAnkenListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String jyuninDateFrom;

		/** 受任日To */
		@LocalDatePattern(groups = {AnkenListInitSearch.class, AllAnkenListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String jyuninDateTo;

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
		/** 案件一覧のソートキー */
		private AnkenListSortItem allAnkenListSortKey;

		/** 案件一覧のソート順 */
		private SortOrder allAnkenListSortOrder;

		/**
		 * 最初のページ番号に設定
		 */
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
			// 案件ステータス「すべて」以外はカウントアップ
			if (!CommonConstant.ANKEN_STATUS_ALL_CD.equals(ankenStatus)) {
				count++;
			}
			// 検索ワードがあればカウントアップ
			if (!StringUtils.isEmpty(searchWord)) {
				count++;
			}
			// 分野IDがあればカウントアップ
			if (!StringUtils.isEmpty(bunyaId)) {
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
			// 案件登録日があればカウントアップ
			if (!StringUtils.isEmpty(ankenCreateDateFrom) || !StringUtils.isEmpty(ankenCreateDateTo)) {
				count++;
			}
			// 受任日があればカウントアップ
			if (!StringUtils.isEmpty(jyuninDateFrom) || !StringUtils.isEmpty(jyuninDateTo)) {
				count++;
			}
			return count;
		}

	}

	/**
	 * メニュー：自分の案件検索条件フォーム
	 */
	@Data
	public static class MyAnkenListSearchForm implements PagerForm {

		/** 案件ステータス */
		private String ankenStatus;

		/** 検索ワード */
		private String searchWord;

		/** 分野 */
		private String bunyaId;

		/** 分野の一覧情報 */
		private List<BunyaDto> bunyaList;

		/** 担当弁護士 */
		private Long tantoLaywer;

		/** 担当事務 */
		private Long tantoJimu;

		/** 案件登録日From **/
		@LocalDatePattern(groups = {AnkenListInitSearch.class, MyAnkenListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String ankenCreateDateFrom;

		/** 案件登録日To **/
		@LocalDatePattern(groups = {AnkenListInitSearch.class, MyAnkenListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String ankenCreateDateTo;

		/** 受任日From */
		@LocalDatePattern(groups = {AnkenListInitSearch.class, MyAnkenListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String jyuninDateFrom;

		/** 受任日To */
		@LocalDatePattern(groups = {AnkenListInitSearch.class, MyAnkenListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String jyuninDateTo;

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
		/** 案件一覧のソートキー */
		private AnkenListSortItem myAnkenListSortKey;

		/** 案件一覧のソート順 */
		private SortOrder myAnkenListSortOrder;

		/**
		 * 最初のページ番号に設定
		 */
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
			// 案件ステータス「すべて」以外はカウントアップ
			if (!CommonConstant.ANKEN_STATUS_ALL_CD.equals(ankenStatus)) {
				count++;
			}
			// 検索ワードがあればカウントアップ
			if (!StringUtils.isEmpty(searchWord)) {
				count++;
			}
			// 分野があればカウントアップ
			if (!StringUtils.isEmpty(bunyaId)) {
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
			// 案件登録日があればカウントアップ
			if (!StringUtils.isEmpty(ankenCreateDateFrom) || !StringUtils.isEmpty(ankenCreateDateTo)) {
				count++;
			}
			// 受任日があればカウントアップ
			if (!StringUtils.isEmpty(jyuninDateFrom) || !StringUtils.isEmpty(jyuninDateTo)) {
				count++;
			}
			return count;
		}
	}

	/**
	 * メニュー：顧問案件検索条件フォーム
	 */
	@Data
	public static class AdvisorAnkenListSearchForm implements PagerForm {

		/** 案件ステータス */
		private String ankenStatus;

		/** 検索ワード */
		private String searchWord;

		/** 分野 */
		private String bunyaId;

		/** 分野の一覧情報 */
		private List<BunyaDto> bunyaList;

		/** 担当弁護士 */
		private Long tantoLaywer;

		/** 担当事務 */
		private Long tantoJimu;

		/** 案件登録日From **/
		@LocalDatePattern(groups = {AnkenListInitSearch.class, AdvisorAnkenListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String ankenCreateDateFrom;

		/** 案件登録日To **/
		@LocalDatePattern(groups = {AnkenListInitSearch.class, AdvisorAnkenListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String ankenCreateDateTo;

		/** 受任日From */
		@LocalDatePattern(groups = {AnkenListInitSearch.class, AdvisorAnkenListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String jyuninDateFrom;

		/** 受任日To */
		@LocalDatePattern(groups = {AnkenListInitSearch.class, AdvisorAnkenListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String jyuninDateTo;

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
		/** 案件一覧のソートキー */
		private AnkenListSortItem advisorAnkenListSortKey;

		/** 案件一覧のソート順 */
		private SortOrder advisorAnkenListSortOrder;

		/**
		 * 最初のページ番号に設定
		 */
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
			// 案件ステータス「すべて」以外はカウントアップ
			if (!CommonConstant.ANKEN_STATUS_ALL_CD.equals(ankenStatus)) {
				count++;
			}
			// 検索ワードがあればカウントアップ
			if (!StringUtils.isEmpty(searchWord)) {
				count++;
			}
			// 分野があればカウントアップ
			if (!StringUtils.isEmpty(bunyaId)) {
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
			// 案件登録日があればカウントアップ
			if (!StringUtils.isEmpty(ankenCreateDateFrom) || !StringUtils.isEmpty(ankenCreateDateTo)) {
				count++;
			}
			// 受任日があればカウントアップ
			if (!StringUtils.isEmpty(jyuninDateFrom) || !StringUtils.isEmpty(jyuninDateTo)) {
				count++;
			}
			return count;
		}
	}

	/**
	 * メニュー：民事裁判検索条件フォーム
	 */
	@Data
	public static class MinjiSaibanListSearchForm implements PagerForm {

		/** 分野 */
		private String bunyaId;

		/** 分野の一覧情報 */
		private List<BunyaDto> bunyaList;

		/** 検索ワード */
		private String searchWord;

		/** 裁判手続き */
		private String saibanStatus;

		/** 担当弁護士 */
		private Long tantoLaywerId;

		/** 担当事務 */
		private Long tantoJimuId;

		/** 申立日From **/
		@LocalDatePattern(groups = {AnkenListInitSearch.class, MinjiSaibanListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String saibanStartDateFrom;

		/** 申立日To **/
		@LocalDatePattern(groups = {AnkenListInitSearch.class, MinjiSaibanListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String saibanStartDateTo;

		/** 終了日From */
		@LocalDatePattern(groups = {AnkenListInitSearch.class, MinjiSaibanListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String saibanEndDateFrom;

		/** 終了日To */
		@LocalDatePattern(groups = {AnkenListInitSearch.class, MinjiSaibanListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String saibanEndDateTo;

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
		/** 裁判一覧のソートキー */
		private SaibanListSortItem minjiSaibanListSortKey;

		/** 裁判一覧のソート順 */
		private SortOrder minjiSaibanListSortOrder;

		/**
		 * 最初のページ番号に設定
		 */
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
			// 分野があればカウントアップ
			if (!StringUtils.isEmpty(bunyaId)) {
				count++;
			}

			// 検索ワードがあればカウントアップ
			if (!StringUtils.isEmpty(searchWord)) {
				count++;
			}

			// 裁判手続きがあればカウントアップ
			if (!CommonConstant.SAIBAN_STATUS_ALL_CD.equals(saibanStatus)) {
				count++;
			}

			// 担当弁護士があればカウントアップ
			if (tantoLaywerId != null) {
				count++;
			}

			// 担当事務があればカウントアップ
			if (tantoJimuId != null) {
				count++;
			}

			// 申立日があればカウントアップ
			if (!StringUtils.isEmpty(saibanStartDateFrom) || !StringUtils.isEmpty(saibanStartDateTo)) {
				count++;
			}

			// 終了日があればカウントアップ
			if (!StringUtils.isEmpty(saibanEndDateFrom) || !StringUtils.isEmpty(saibanEndDateTo)) {
				count++;
			}
			return count;
		}
	}

	/**
	 * メニュー：刑事裁判検索条件フォーム
	 */
	@Data
	public static class KeijiSaibanListSearchForm implements PagerForm {

		/** 分野 */
		private String bunyaId;

		/** 分野の一覧情報 */
		private List<BunyaDto> bunyaList;

		/** 検索ワード */
		private String searchWord;

		/** 裁判手続き */
		private String saibanStatus;

		/** 担当弁護士 */
		private Long tantoLaywerId;

		/** 担当事務 */
		private Long tantoJimuId;

		/** 起訴日、上訴日From **/
		@LocalDatePattern(groups = {AnkenListInitSearch.class, KeijiSaibanListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String saibanStartDateFrom;

		/** 起訴日、上訴日To **/
		@LocalDatePattern(groups = {AnkenListInitSearch.class, KeijiSaibanListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String saibanStartDateTo;

		/** 判決日From */
		@LocalDatePattern(groups = {AnkenListInitSearch.class, KeijiSaibanListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String saibanEndDateFrom;

		/** 判決日To */
		@LocalDatePattern(groups = {AnkenListInitSearch.class, KeijiSaibanListSearchDate.class}, format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String saibanEndDateTo;

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
		/** 裁判一覧のソートキー */
		private SaibanListSortItem keijiSaibanListSortKey;

		/** 裁判一覧のソート順 */
		private SortOrder keijiSaibanListSortOrder;

		/**
		 * 最初のページ番号に設定
		 */
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
			// 分野があればカウントアップ
			if (!StringUtils.isEmpty(bunyaId)) {
				count++;
			}

			// 検索ワードがあればカウントアップ
			if (!StringUtils.isEmpty(searchWord)) {
				count++;
			}

			// 裁判手続きがあればカウントアップ
			if (!CommonConstant.SAIBAN_STATUS_ALL_CD.equals(saibanStatus)) {
				count++;
			}

			// 担当弁護士があればカウントアップ
			if (tantoLaywerId != null) {
				count++;
			}

			// 担当事務があればカウントアップ
			if (tantoJimuId != null) {
				count++;
			}

			// 起訴日、上訴日があればカウントアップ
			if (!StringUtils.isEmpty(saibanStartDateFrom) || !StringUtils.isEmpty(saibanStartDateTo)) {
				count++;
			}

			// 判決日があればカウントアップ
			if (!StringUtils.isEmpty(saibanEndDateFrom) || !StringUtils.isEmpty(saibanEndDateTo)) {
				count++;
			}
			return count;
		}
	}

}