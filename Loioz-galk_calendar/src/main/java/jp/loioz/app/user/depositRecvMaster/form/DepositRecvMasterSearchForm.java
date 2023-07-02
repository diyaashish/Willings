package jp.loioz.app.user.depositRecvMaster.form;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.domain.condition.DepositRecvMasterListSearchCondition;
import lombok.Data;

/**
 * 預かり金項目一覧画面：検索オブジェクト
 */
@Data
public class DepositRecvMasterSearchForm {

	/** 一覧検索条件 */
	private DepositRecvMasterListSearchForm depositRecvMasterListSearchForm = new DepositRecvMasterListSearchForm();

	/** 初期化処理 */
	public void initForm() {
		this.depositRecvMasterListSearchForm = new DepositRecvMasterListSearchForm();
	}

	/**
	 * 預かり項目一覧の検索条件
	 */
	@Data
	public static class DepositRecvMasterListSearchForm implements PagerForm {

		/** 預り金タイプ **/
		private String depositType;

		/** ページ番号 */
		private Integer page = DEFAULT_PAGE;

		/** 表示件数 */
		private PageSize pageSize = DEFAULT_SIZE;

		/**
		 * 検索オブジェクトに変換
		 * 
		 * @return
		 */
		public DepositRecvMasterListSearchCondition toDepositRecvMasterListSearchCondition() {
			return DepositRecvMasterListSearchCondition.builder()
					.depositType(this.depositType)
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
