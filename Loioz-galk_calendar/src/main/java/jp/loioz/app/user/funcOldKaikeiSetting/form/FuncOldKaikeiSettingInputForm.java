package jp.loioz.app.user.funcOldKaikeiSetting.form;

import java.util.ArrayList;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.constant.CommonConstant.DisabledFlg;
import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 入出金項目入力フォームオブジェクト
 */
@Data
public class FuncOldKaikeiSettingInputForm {

	/**
	 * 旧会計管理の機能設定の入力用フォーム
	 */
	@Data
	public static class FuncOldKaikeiBasicSettingInputForm {
		
		/** 旧会計機能を有効にするかどうか */
		@Required
		@EnumType(SystemFlg.class)
		private String isOldKaikeiOn;
		
		/**
		 * 「旧会計機能を有効にするかどうか」の選択肢オブジェクトを取得する
		 */
		public ArrayList<SelectOptionForm> getIsOldKaikeiOnOptionList() {
			ArrayList<SelectOptionForm> selectOptionFormList = new ArrayList<>();
			
			SelectOptionForm onOption = new SelectOptionForm(SystemFlg.FLG_ON.getCd(), "有効にする");
			SelectOptionForm offOption = new SelectOptionForm(SystemFlg.FLG_OFF.getCd(), "無効にする");
			
			selectOptionFormList.add(onOption);
			selectOptionFormList.add(offOption);
			
			return selectOptionFormList;
		}
	}
	
	/**
	 * 入出金項目：編集モーダル入力用フォーム
	 */
	@Data
	public static class FuncOldKaikeiSettingEditModalInputForm {

		// 入力項目
		/** 入出金項目SEQ */
		private Long nyushukkinKomokuId;

		/** 入出金種別 */
		@Required
		@EnumType(value = NyushukkinType.class)
		private String nyushukkinType;

		/** 名称 */
		@Required
		@MaxDigit(max = 10)
		private String komokuName;

		/** 課税フラグ */
		@Required
		@EnumType(value = TaxFlg.class)
		private String taxFlg;

		/** 状態 */
		private String disabledFlg;

		/**
		 * 新規登録モード判定
		 *
		 * @return
		 */
		public boolean isNew() {
			boolean newFlg = false;
			if (this.nyushukkinKomokuId == null) {
				newFlg = true;
			}
			return newFlg;
		}

		/**
		 * 利用停止モード判定
		 *
		 * @return
		 */
		public boolean isDisbled() {
			boolean disbledFlg = false;
			if (DisabledFlg.NOT_AVAILABLE.equalsByCode(this.disabledFlg)) {
				disbledFlg = true;
			}
			return disbledFlg;
		}

	}

}
