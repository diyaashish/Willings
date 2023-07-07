package jp.loioz.app.common.mvc.calendarEdit.form;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * カレンダー登録・編集モーダル
 */
@Data
public class CalendarEditModalInputForm {

	/** 予定SEQ */
	private Long viewScheduleSeq;

	/** 予定-基本情報 */
	private CalendarBasicInputForm calendarBasicInputForm;

	/** 予定-施設設定 */
	private CalendarSelectedOptionRoomListInputForm calendarSelectedOptionRoomListInputForm;

	/** 予定-関連設定 */
	private CalendarRelatedSettingInputForm calendarRelatedSettingInputForm;

	/** 予定-参加者 */
	private CalendarGuestAccountInputForm calendarGuestAccountInputForm;

	/**
	 * 予定データフォーム
	 */
	@Data
	public static class CalendarBasicInputForm {

		/** 件名 */
		@Required
		private String subject;

		/** 開始日 */
		@Required
		@DateTimeFormat(pattern = "yyyy/MM/dd")
		private LocalDate dateFrom;

		/** 終了日 */
		@Required
		@DateTimeFormat(pattern = "yyyy/MM/dd")
		private LocalDate dateTo;

		/** 場所 */
		private String place;

		/** 施設選択 */
		private boolean isSelectedRoom;

		/** 公開範囲 */
		@Required
		private String openRangeCd;

		/** 通知 */
		@Required
		private String notificationTypeCd;

		/** 予定の色 */
		@Required
		private String scheduleColorTypeCd;

		/** ログインアカウントSEQ */
		private Long loginAccountSeq;

		/** ログインアカウント名 */
		private String loginAccountName;

		/** ログインアカウントカラー */
		private String loginAccountColor;

		/** アカウント名の検索 */
		private String searchAccountWord;

	}

	/**
	 * 予定-施設設定フォーム
	 */
	@Data
	public static class CalendarSelectedOptionRoomListInputForm {

		/** 施設ID */
		private Long selectedRoomId;

		/** 施設選択リスト */
		private List<SelectOptionForm> selectOptionRoomList;

	}

	/**
	 * 予定データの関連設定フォーム
	 */
	@Data
	public static class CalendarRelatedSettingInputForm {

		/** 名簿ID */
		private Long personId;

		/** 案件ID */
		private Long ankenId;

		/** 裁判SEQ */
		private Long saibanSeq;

		/** 裁判期日SEQ */
		private Long saibanLimitSeq;

		/** 予定紐づけ設定 */
		private String calendarRelatedSettingCd;

		/** 名簿／案件／裁判の検索ワード */
		private String searchWord;

		/** 予定紐づけ名称 */
		private String calendarRelatedDataName;

	}

	/**
	 * 予定-招待者フォーム
	 */
	@Data
	public static class CalendarGuestAccountInputForm {

		/** 招待者リスト */
		private List<GuestAccountSelectOption> guestAccountSelectOptionList;

	}

	/**
	 * 招待者
	 */
	@Data
	public static class GuestAccountSelectOption {

		/** アカウントSEQ */
		private Long guestAccountSeq;

		/** アカウント名 */
		private String guestAccountName;

		/** 招待種別CD */
		private String invitationTypeCd;

		/** 招待種別表示 */
		private String invitationTypeVal;

		/** 参加状況 */
		private String attendanceStatusCd;

		/** 参加者コメント */
		private String guestAccountComment;


		// New participation status attributes
		private boolean optionalParticipation;
		private boolean informationSharing;

	}

}
