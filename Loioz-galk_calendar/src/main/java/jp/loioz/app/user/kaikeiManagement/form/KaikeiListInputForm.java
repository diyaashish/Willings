package jp.loioz.app.user.kaikeiManagement.form;

import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 会計管理 入力フォームオブジェクト
 */
@Data
public class KaikeiListInputForm {

	/**
	 * 案件ステータス 入力フォームオブジェクト
	 */
	@Data
	public static class AnkenStatusInputForm {

		/** 案件ID */
		@Required
		private Long ankenId;

		/** 顧客ID */
		@Required
		private Long customerId;

		/** 精算完了日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String seisanKanryoDate;

		/** 完了チェック */
		private boolean isCompleted;

		/** 案件顧客情報のバージョンNo ここだけ厳密にチェックする */
		@Required
		private Long versionNo;
		
		// 表示用
		
		/** 遷移元判定フラグ（0：案件管理から遷移、1：顧客管理から遷移 */
		@Required
		private String transitionFlg;
		
		/** 案件名（完了操作対象の案件名） */
		private String targetAnkenName;
		
		/** 顧客名（完了操作対象の顧客名） */
		private String targetCustomerName;
		
		/**
		 * 案件軸の画面かどうかを判定する
		 * 
		 * @return
		 */
		public boolean isTransitionAnken() {
			return SystemFlg.FLG_OFF.getCd().equals(this.transitionFlg);
		}
	}

}
