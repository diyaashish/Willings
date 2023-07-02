package jp.loioz.app.user.saibanManagement.form;

import org.thymeleaf.util.StringUtils;

import jp.loioz.common.validation.annotation.MaxDigit;
import lombok.Data;

/**
 * 裁判編集画面の共通入力フォームクラス
 */
@Data
public class SaibanCommonInputForm {
	
	/**
	 * 裁判官情報の入力フォーム
	 */
	@Data
	public static class SaibankanInputForm {
		
		/** 裁判官名 */
		@MaxDigit(max = 128)
		private String saibankanName;
		
		/**
		 * フォームの入力が空かどうかを判定
		 */
		public boolean isEmpty() {
			return StringUtils.isEmpty(saibankanName);
		}
	}
}
