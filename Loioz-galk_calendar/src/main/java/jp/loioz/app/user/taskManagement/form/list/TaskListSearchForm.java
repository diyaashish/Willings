package jp.loioz.app.user.taskManagement.form.list;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.AllTaskListSortKey;
import jp.loioz.common.constant.CommonConstant.CloseTaskListSortKey;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.constant.CommonConstant.TaskAnkenListDispKey;
import jp.loioz.common.constant.CommonConstant.TaskAnkenListSortKey;
import lombok.Data;

/**
 * タスク管理画面の検索条件フォームクラス
 */
@Data
public class TaskListSearchForm {

	/** 選択中案件一覧メニュー */
	private String selectedTaskListMenu;

	/** タスク検索処理用検索条件フォーム */
	private SearchResultsTaskListSearchForm searchResultsTaskListSearchForm;

	/** メニュー：今日のタスク用検索条件フォーム */
	private TodayTaskListSearchForm todayTaskListSearchForm;

	/** メニュー：期限付きのタスク用検索条件フォーム */
	private FutureTaskListSearchForm futureTaskListSearchForm;

	/** メニュー：すべてのタスク用検索条件フォーム */
	private AllTaskListSearchForm allTaskListSearchForm;

	/** メニュー：期限を過ぎたタスク用検索条件フォーム */
	private OverdueTaskListSearchForm overdueTaskListSearchForm;

	/** メニュー：割り当てられたタスク用検索条件フォーム */
	private AssignedTaskListSearchForm assignedTaskListSearchForm;

	/** メニュー：割り当てたタスク用検索条件フォーム */
	private AssignTaskListSearchForm assignTaskListSearchForm;

	/** メニュー：完了したタスク用検索条件フォーム */
	private CloseTaskListSearchForm closeTaskListSearchForm;

	/** メニュー：案件タスク用検索条件フォーム */
	private TaskAnkenListSearchForm taskAnkenListSearchForm;

	/**
	 * 検索条件フォームの初期化
	 */
	public void initForm() {
		// 検索条件フォーム新規作成
		searchResultsTaskListSearchForm = new SearchResultsTaskListSearchForm();
		todayTaskListSearchForm = new TodayTaskListSearchForm();
		futureTaskListSearchForm = new FutureTaskListSearchForm();
		allTaskListSearchForm = new AllTaskListSearchForm();
		allTaskListSearchForm.setSortKeyCd(AllTaskListSortKey.DEFAULT.getCd());
		overdueTaskListSearchForm = new OverdueTaskListSearchForm();
		assignedTaskListSearchForm = new AssignedTaskListSearchForm();
		assignTaskListSearchForm = new AssignTaskListSearchForm();
		closeTaskListSearchForm = new CloseTaskListSearchForm();
		closeTaskListSearchForm.setSortKeyCd(CloseTaskListSortKey.DEFAULT.getCd());
		taskAnkenListSearchForm = new TaskAnkenListSearchForm();
		taskAnkenListSearchForm.setSortKeyCd(TaskAnkenListSortKey.DEFAULT.getCd());
		taskAnkenListSearchForm.setDispKeyCd(TaskAnkenListDispKey.INCOMPLETE.getCd());
	}

	/**
	 * 検索結果のタスク一覧検索条件フォーム
	 */
	@Data
	public static class SearchResultsTaskListSearchForm implements PagerForm {

		/** 検索ワード */
		private String searchWord;

		// -----------------------------------------------
		// ページャー
		// -----------------------------------------------
		/** ページ番号（これから表示するページ） */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/**
		 * 最初のページ番号に設定
		 */
		public void setDefaultPage() {
			this.page = DEFAULT_PAGE;
		}

	}

	/**
	 * 今日のタスク一覧検索条件フォーム
	 */
	@Data
	public static class TodayTaskListSearchForm implements PagerForm {

		// -----------------------------------------------
		// ページャー
		// -----------------------------------------------
		/** ページ番号（これから表示するページ） */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/**
		 * 最初のページ番号に設定
		 */
		public void setDefaultPage() {
			this.page = DEFAULT_PAGE;
		}

	}

	/**
	 * 期限付きのタスク一覧検索条件フォーム
	 */
	@Data
	public static class FutureTaskListSearchForm implements PagerForm {

		// -----------------------------------------------
		// ページャー
		// -----------------------------------------------
		/** ページ番号（これから表示するページ） */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/**
		 * 最初のページ番号に設定
		 */
		public void setDefaultPage() {
			this.page = DEFAULT_PAGE;
		}

	}

	/**
	 * すべてのタスク一覧検索条件フォーム
	 */
	@Data
	public static class AllTaskListSearchForm implements PagerForm {

		/** ソートキーコード 1:設定順 、2:期限日-昇順、3:期限日-降順 */
		private String sortKeyCd;

		// -----------------------------------------------
		// ページャー
		// -----------------------------------------------
		/** ページ番号（これから表示するページ） */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/**
		 * 最初のページ番号に設定
		 */
		public void setDefaultPage() {
			this.page = DEFAULT_PAGE;
		}

		/**
		 * 一覧の並び替えが可能か判定<br>
		 * ソートが「設定順」の場合のみ一覧の並び替えが可能
		 * 
		 * @return
		 */
		public boolean canSort() {
			if (AllTaskListSortKey.DEFAULT.equalsByCode(sortKeyCd)) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 期限を過ぎたタスク一覧検索条件フォーム
	 */
	@Data
	public static class OverdueTaskListSearchForm implements PagerForm {

		// -----------------------------------------------
		// ページャー
		// -----------------------------------------------
		/** ページ番号（これから表示するページ） */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/**
		 * 最初のページ番号に設定
		 */
		public void setDefaultPage() {
			this.page = DEFAULT_PAGE;
		}

	}

	/**
	 * 割り当てられたタスク一覧検索条件フォーム
	 */
	@Data
	public static class AssignedTaskListSearchForm implements PagerForm {

		// -----------------------------------------------
		// ページャー
		// -----------------------------------------------
		/** ページ番号（これから表示するページ） */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/**
		 * 最初のページ番号に設定
		 */
		public void setDefaultPage() {
			this.page = DEFAULT_PAGE;
		}

	}

	/**
	 * 割り当てたタスク一覧検索条件フォーム
	 */
	@Data
	public static class AssignTaskListSearchForm implements PagerForm {

		// -----------------------------------------------
		// ページャー
		// -----------------------------------------------
		/** ページ番号（これから表示するページ） */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/**
		 * 最初のページ番号に設定
		 */
		public void setDefaultPage() {
			this.page = DEFAULT_PAGE;
		}

	}

	/**
	 * 完了したタスク一覧検索条件フォーム
	 */
	@Data
	public static class CloseTaskListSearchForm implements PagerForm {

		/** ソートキーコード 1:完了順 、2:期限日-昇順、3:期限日-降順 */
		private String sortKeyCd;

		// -----------------------------------------------
		// ページャー
		// -----------------------------------------------
		/** ページ番号（これから表示するページ） */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/**
		 * 最初のページ番号に設定
		 */
		public void setDefaultPage() {
			this.page = DEFAULT_PAGE;
		}
	}

	/**
	 * 案件タスク一覧検索条件フォーム
	 */
	@Data
	public static class TaskAnkenListSearchForm implements PagerForm {

		/** ソートキーコード 1:登録順 or 完了順、2:期限日-昇順、3:期限日-降順 */
		private String sortKeyCd;

		/** 案件ID */
		private Long AnkenId;

		/** 表示切替コード */
		private String dispKeyCd;

		// -----------------------------------------------
		// ページャー
		// -----------------------------------------------
		/** ページ番号（これから表示するページ） */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/**
		 * 最初のページ番号に設定
		 */
		public void setDefaultPage() {
			this.page = DEFAULT_PAGE;
		}
	}

}