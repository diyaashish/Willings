package jp.loioz.app.user.ankenDashboard.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import jp.loioz.common.utility.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 案件ダッシュボード：タスク一覧用オブジェクト
 */
@Data
public class AnkenDashboardTaskListDto {

	/** タスクSEQ */
	private Long taskSeq;

	/** 案件ID */
	private Long ankenId;

	/** 件名 */
	private String title;

	/** 内容 */
	private String content;

	/** ステータス */
	private String taskStatus;

	/** 期限 */
	private String limitDt;

	/** 担当者 */
	private List<TaskTanto> taskTantoList;

	/** コメント件数 */
	private long commentCount;

	/** 期限To（日）文字のスタイル */
	private String limitDateStyle;

	/** サブタスク件数 */
	private Long checkItemCount;

	/** 終了したサブタスク件数 */
	private Long completeCheckItemCount;

	/** 表示用の期限 */
	public String getDispLimitDateTime() {

		// 日付のみの場合の文字数
		int numberOfCharactersInDate = 10;

		if (this.limitDt.length() > numberOfCharactersInDate) {
			LocalDateTime localDateTime = LocalDateTime.parse(this.limitDt, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
			return DateUtils.parseToString(localDateTime, DateUtils.DATE_M + "月" + DateUtils.DATE_D + "日") + " (" + localDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE) + ") "
					+ DateUtils.parseToString(localDateTime, DateUtils.TIME_FORMAT_HMM);
		} else {
			LocalDate localDate = LocalDate.parse(this.limitDt, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
			return DateUtils.parseToString(localDate, DateUtils.DATE_M + "月" + DateUtils.DATE_D + "日") + " (" + localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE) + ") ";
		}
	}

	/**
	 * タスク担当者
	 */
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TaskTanto {

		/** 担当者SEQ */
		private Long workerAccountSeq;

		/** 担当者名 */
		private String workerAccountName;

		/** アカウントカラー */
		private String accountColor;
	}

}
