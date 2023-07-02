package jp.loioz.app.user.schedule.form;

import java.awt.Color;
import java.util.List;

import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.utility.StringUtils;
import lombok.Builder;
import lombok.Data;

/**
 * 予定の共通画面表示フォームクラス
 */
@Data
public class ScheduleCommonViewForm {

	/** 会議室 */
	private List<Room> roomList;

	/** アカウント */
	private List<Account> accountList;

	/** 部署 */
	private List<Busho> bushoList;

	/** グループ */
	private List<Group> groupList;

	/**
	 * 会議室
	 */
	@Data
	@Builder
	public static class Room {

		/** 会議室ID */
		private Long roomId;

		/** 会議室名 */
		private String roomName;

	}

	/**
	 * アカウント
	 */
	@Data
	@Builder
	public static class Account {

		/** アカウント連番 */
		private Long accountSeq;

		/** アカウント名 */
		private String accountName;

		/** アカウント種別 */
		private AccountType accountType;

		/** アカウント色 */
		private Color accountColor;

		/** 所属している部署のID(スペース区切り) */
		private String shozokuBushoIdStr;

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

		/** 検索用アカウント名 */
		private String searchAccountName;

		/** 検索用アカウント名(かな) */
		private String searchAccountNameKana;

		public static class AccountBuilder {
			public AccountBuilder accountColor(String accountColor) {
				this.accountColor = decodeColor(accountColor);
				return this;
			}
		}

		protected static Color decodeColor(String accountColor) {
			Color color = null;
			if (!StringUtils.isEmpty(accountColor)) {
				try {
					color = Color.decode(accountColor);
				} catch (NumberFormatException e) {
					color = null;
				}
			}
			return color;
		}
	}

	/**
	 * 部署
	 */
	@Data
	@Builder
	public static class Busho {

		/** 部署ID */
		private Long bushoId;

		/** 部署名 */
		private String bushoName;

		/** 所属アカウント */
		private List<Account> accountList;
	}

	/**
	 * グループ
	 */
	@Data
	@Builder
	public static class Group {

		/** グループID */
		private Long groupId;

		/** グループ名 */
		private String groupName;

		/** 所属アカウント */
		private List<Account> accountList;
	}
}