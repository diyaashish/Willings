package jp.loioz.common.constant;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * テナント機能設定系の定数クラス
 */
public class TenantFuncSettingConstant {

	/**
	 * 機能設定の設定値の選択肢用インターフェース
	 */
	public interface FuncSettingValueOption {};
	
	// =========================================================================
	// テナントの機能設定Enumの定義
	// =========================================================================
	
	/**
	 * テナントの機能設定を行う機能を定義するEnum<br>
	 * m_tenant_func_settingテーブルのfunc_setting_id（機能設定ID）の値に対応する。<br>
	 * テナントの機能設定項目を増やす場合に、このEnumの定義も増やすことになる。
	 */
	@Getter
	@AllArgsConstructor
	public enum TenantFuncSetting {
		// 値は「ON=1」「OFF=0」とするため、選択肢クラスはNULLとする
		OLD_KAIKEI_ON_OFF(null, "TF0001", "旧会計機能のON、OFF"),
		;
		
		/** 設定値の選択肢クラス（選択肢が不要な設定項目の場合はNULL） */
		private Class<? extends FuncSettingValueOption> optionClazz;
		/** 機能設定ID */
		private String id;
		/** 機能設定名。処理では使用しない。 */
		private String name;
		
		/**
		 * IDからEnumを取得する
		 * 
		 * @param id
		 * @return
		 */
		public static TenantFuncSetting of(String id) {
			return Arrays.stream(TenantFuncSetting.values())
				.filter(e -> e.getId().equals(id))
				.findFirst()
				.orElse(null);
		}
	}
	
	// =========================================================================
	// 以下、設定項目の選択肢Enumの定義
	// =========================================================================
	
	// 現状では選択肢Enumはないが、選択肢がある項目を追加した場合は選択肢Enumを定義する
	// ※参考: AccountSettingConstant.java
	
}
