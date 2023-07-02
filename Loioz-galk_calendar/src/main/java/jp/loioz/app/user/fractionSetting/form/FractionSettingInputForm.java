package jp.loioz.app.user.fractionSetting.form;

import jp.loioz.common.constant.CommonConstant.HoshuHasuType;
import jp.loioz.common.constant.CommonConstant.TaxHasuType;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 端数処理 画面のフォームクラス
 */
@Data
public class FractionSettingInputForm {

	/**
	 * 端数設定入力フォームフラグメント
	 */
	@Data
	public static class FractionSettingInputFragmentForm {

		// 表示用パラメータ
		// 現状なし

		// 入力パラメータ

		/** 消費税の端数処理の方法 */
		@Required
		@EnumType(TaxHasuType.class)
		private String taxHasuType;

		/** 報酬の端数処理の方法 */
		@Required
		@EnumType(HoshuHasuType.class)
		private String HoshuHasuType;
	}

}