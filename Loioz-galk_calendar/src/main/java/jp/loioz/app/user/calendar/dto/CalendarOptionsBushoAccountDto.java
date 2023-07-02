package jp.loioz.app.user.calendar.dto;

import java.awt.Color;
import java.util.List;

import jp.loioz.common.constant.CommonConstant.AccountType;
import lombok.Builder;
import lombok.Data;

/**
 * カレンダー選択肢-部署+アカウント
 */
@Data
public class CalendarOptionsBushoAccountDto {

	/** 部署ID */
	private Long bushoId;

	/** 部署名 */
	private String bushoName;

	/** アカウント名 */
	private List<OptionsAccount> optionsAccountList;

	/**
	 * アカウント
	 */
	@Data
	@Builder
	public static class OptionsAccount {

		/** アカウント連番 */
		private Long accountSeq;

		/** アカウント名 */
		private String accountName;

		/** アカウント種別 */
		private AccountType accountType;

		/** アカウント色 */
		private Color accountColor;

		/** 有効かどうか */
		private boolean isEnabled;

		/** アカウント色(16進数文字列) */
		public String getAccountColorHex() {
			if (accountColor == null) {
				return null;
			} else {
				return String.format("#%02X%02X%02X", accountColor.getRed(), accountColor.getGreen(), accountColor.getBlue());
			}
		}

		/** アカウント色(予定の文字色) */
		public String getAccountColorText() {
			if (accountColor == null) {
				return null;
			} else {
				String color = "#000";
				int red = accountColor.getRed();
				int green = accountColor.getGreen();
				int blue = accountColor.getBlue();
				if ((red * 0.299 + green * 0.587 + blue * 0.114) < 186) {
					color = "#fff";
				}
				return color;
			}
		}
	}

}