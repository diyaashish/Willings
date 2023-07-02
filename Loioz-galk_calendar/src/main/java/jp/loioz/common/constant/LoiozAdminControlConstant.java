package jp.loioz.common.constant;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ロイオズ管理者制御系の定数クラス
 */
public class LoiozAdminControlConstant {

	/**
	 * 制御の設定値の選択肢用インターフェース
	 */
	public interface AdminControlValueOption {};
	
	// =========================================================================
	// 管理者制御の設定項目Enumの定義
	// =========================================================================
	
	/**
	 * 管理者制御を行う項目を定義するEnum<br>
	 * m_loioz_admin_controlテーブルのadmin_control_id（管理者制御ID）の値に対応する。<br>
	 * 制御項目を増やす場合に、このEnumの定義も増やすことになる。
	 */
	@Getter
	@AllArgsConstructor
	public enum LoiozAdminControl {
		// 値は「利用可=1」「利用不可=0」とするため、選択肢クラスはNULLとする
		OLD_KAIKEI_OPEN(null, "AC0001", "旧会計機能の利用を許可"),
		;
		
		/** 設定値の選択肢クラス（選択肢が不要な設定項目の場合はNULL） */
		private Class<? extends AdminControlValueOption> optionClazz;
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
		public static LoiozAdminControl of(String id) {
			return Arrays.stream(LoiozAdminControl.values())
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
