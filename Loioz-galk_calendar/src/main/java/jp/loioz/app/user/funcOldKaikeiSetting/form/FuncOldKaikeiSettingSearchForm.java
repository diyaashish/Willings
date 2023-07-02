package jp.loioz.app.user.funcOldKaikeiSetting.form;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.domain.condition.FuncOldKaikeiSettingListSearchCondition;
import lombok.Data;

/**
 * 入出金項目一覧画面：検索オブジェクト
 */
@Data
public class FuncOldKaikeiSettingSearchForm {

	/** 一覧検索条件 */
	private FuncOldKaikeiSettingListSearchForm funcOldKaikeiSettingListSearchForm = new FuncOldKaikeiSettingListSearchForm();

	/** 初期化処理 */
	public void initForm() {
		this.funcOldKaikeiSettingListSearchForm = new FuncOldKaikeiSettingListSearchForm();
	}

	/**
	 * 入出金項目一覧の検索条件
	 */
	@Data
	public static class FuncOldKaikeiSettingListSearchForm implements PagerForm {

		/** 入出金タイプ **/
		private String nyushukkinType;

		/** ページ番号 */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/**
		 * 検索オブジェクトに変換
		 * 
		 * @return
		 */
		public FuncOldKaikeiSettingListSearchCondition toFuncOldKaikeiSettingListSearchCondition() {
			return FuncOldKaikeiSettingListSearchCondition.builder()
					.nyushukkinType(this.nyushukkinType)
					.build();
		}

		/**
		 * 最初のページ番号に設定
		 */
		public void setDefaultPage() {
			this.page = DEFAULT_PAGE;
		}

	}

}
