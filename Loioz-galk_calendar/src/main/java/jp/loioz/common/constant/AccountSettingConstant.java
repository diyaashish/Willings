package jp.loioz.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * アカウント設定処理系の定数クラス
 */
public class AccountSettingConstant {
	
	/**
	 * 設定項目の選択肢用のインターフェース
	 */
	public interface SettingTypeOption {};
	
	// =========================================================================
	// 設定項目Enumの定義
	// =========================================================================
	
	/**
	 * アカウントの設定の設定項目を定義するEnum<br>
	 * m_account_settingテーブルのsetting_type（設定項目）の値に対応する。<br>
	 * アカウントの設定項目を増やす場合に、このEnumの定義も増やすことになる。
	 */
	@Getter
	@AllArgsConstructor
	public enum SettingType implements DefaultEnum {
		CALENDAR_TASK_SORT(CalendarTaskSortOption.class, "1", "カレンダー画面のタスクのソート設定"),
		CALENDAR_TASK_VIEW(CalendarTaskViewOption.class, "2", "カレンダー画面のタスクの表示設定"),
		;
		
		/** 設定項目の選択肢クラス（選択肢が不要な設定項目の場合はNULL） */
		private Class<? extends SettingTypeOption> optionClazz;
		/** 設定項目のCD */
		private String cd;
		/** 設定項目の説明。処理では使用しない。 */
		private String val;
		
		/**
		 * コードからEnum値を取得する
		 *
		 * @param cd コード
		 * @return Enum
		 */
		public static SettingType of(String cd) {
			return DefaultEnum.getEnum(SettingType.class, cd);
		}
	}
	
	// =========================================================================
	// 以下、設定項目の選択肢Enumの定義
	// =========================================================================
	
	/**
	 * カレンダー画面のタスクのソート設定の選択肢<br>
	 * ※タスク本画面の、「すべてのタスク」「完了したタスク」画面のソートの選択肢もこのEnumを利用する（ただし、設定の保存機能はない）。
	 */
	@Getter
	@AllArgsConstructor
	public enum CalendarTaskSortOption implements DefaultEnum, SettingTypeOption {
		DEFAULT("1", "設定した順"),
		LIMIT_DATE_ASC("2", "期限-昇順"),
		LIMIT_DATE_DESC("3", "期限-降順"),
		;
		
		/** 選択肢のCD */
		private String cd;
		/** 選択肢の表示ラベルの値 */
		private String val;
	}

	/**
	 * カレンダー画面のタスクの表示設定<br>
	 *
	 */
	@Getter
	@AllArgsConstructor
	public enum CalendarTaskViewOption implements DefaultEnum, SettingTypeOption {
		DEFAULT("1", "タスク"), // すべてのタスク + 割り当てられたタスク
		ALL_RELATED_TASK("2", "割り当てたタスクも表示"), // すべてのタスク + 割り当てられたタスク + 割り当てたタスク
		;
		
		/** 選択肢のCD */
		private String cd;
		/** 選択肢の表示ラベルの値 */
		private String val;
	}

}
