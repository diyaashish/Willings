package jp.loioz.app.user.depositRecvMaster.form;

import jp.loioz.common.constant.CommonConstant.DepositType;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 預り金項目入力フォームオブジェクト
 */
@Data
public class DepositRecvMasterInputFormForm {

	/**
	 * 預り金項目画面：編集モーダル入力用フォームオブジェクト
	 */
	@Data
	public static class DepositRecvMasterEditModalInputForm {

		// 表示項目
		// 現状なし

		// 入力項目

		/** 預り金項目SEQ */
		private Long depositItemSeq;

		/** 預り金種別 */
		@Required
		@EnumType(value = DepositType.class)
		private String depositType;

		/** 名称 */
		@Required
		@MaxDigit(max = 30)
		private String depositItemName;

		/** 備考 */
		@MaxDigit(max = 100)
		private String remarks;

		/**
		 * 新規登録モード判定
		 *
		 * @return
		 */
		public boolean isNew() {
			boolean newFlg = false;
			if (this.depositItemSeq == null) {
				newFlg = true;
			}
			return newFlg;
		}

	}

}
