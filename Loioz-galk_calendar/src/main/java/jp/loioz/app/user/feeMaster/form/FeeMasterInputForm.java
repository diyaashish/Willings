package jp.loioz.app.user.feeMaster.form;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 報酬項目の設定画面の入力用フォームオブジェクト
 */
@Data
public class FeeMasterInputForm {

	/**
	 * 報酬項目一覧フラグメントビューフォーム
	 */
	@Data
	public static class FeeMasterItemEditInputForm {

		// 表示用プロパティ
		// 現状なし

		// 入力用プロパティ
		/** 報酬項目SEQ */
		private Long feeItemSeq;

		/** 報酬項目名 */
		@Required
		@MaxDigit(max = 30)
		private String feeItemName;

		/** 備考 */
		@MaxDigit(max = 100)
		private String remarks;

		/** 新規作成かどうか */
		public boolean isNew() {
			return this.feeItemSeq == null;
		}

	}

}
