package jp.loioz.app.user.taskManagement.form.edit;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.user.taskManagement.dto.TaskCheckItemDto;
import jp.loioz.common.constant.CommonConstant.HourType;
import jp.loioz.common.constant.CommonConstant.MinuteType;
import jp.loioz.common.constant.CommonConstant.TaskStatus;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.groups.TaskCheckItem;
import jp.loioz.domain.value.AnkenId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TaskEditInputForm {

	// タスク(t_task)情報
	/** タスクSEQ */
	private Long taskSeq;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 件名 */
	@Required
	@MaxDigit(max = 255)
	private String title;

	/** 詳細（内容） */
	@MaxDigit(max = 2000)
	private String content;

	/** サブタスク */
	@Required(groups = TaskCheckItem.class)
	@MaxDigit(groups = TaskCheckItem.class, max = 100)
	private String checkItem;

	/** サブタスクのリスト */
	@Valid
	private List<TaskCheckItemDto> checkList;

	/** 期限(日) */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String limitDate;

	/** 期限(時) */
	@EnumType(value = HourType.class)
	private String limitTimeHour;

	/** 期限(分) */
	@EnumType(value = MinuteType.class)
	private String limitTimeMinute;

	/** タスクステータス */
	@EnumType(value = TaskStatus.class)
	private String taskStatus;

	// タスク-作業者(t_task_worker)
	/** タスク-作業者SEQ */
	private Long taskWorkerSeq;

	/** 作成者名 */
	private String createrName;

	/** 今日のタスクフラグ */
	private boolean isTodayTaskFlg;

	/** 今日のタスク日付 */
	private LocalDate todayTaskDate;

	/** 作業者アカウントSEQリスト マッピング用 */
	private List<Long> workerAccountSeq = Collections.emptyList();

	/** 案件設定用 案件名 + 顧客名 */
	private String ankenNameAndCustomerName;

	/** 担当者セレクトボックス （モーダル画面表示用） */
	private List<SelectOptionForm> tantoOptions = Collections.emptyList();

	/** 担当者欄（モーダル画面表示用） */
	private List<TaskTanto> taskTantoList = Collections.emptyList();

	/** 「今日のタスク」変更可能フラグ */
	private boolean isTodayTaskChangeableFlag;

	/**
	 * 設定されているタスク担当者数を取得します。
	 * 
	 * @return
	 */
	public long getNumberOfSelectedTaskTanto() {
		return taskTantoList.stream().filter(taskTanto -> taskTanto.isWorkerFlg() == true).count();
	}

	/** LocalDate変換 */
	public LocalDate getLimitLocalDate() {
		if (Objects.isNull(this.limitDate)) {
			return null;
		}
		return DateUtils.parseToLocalDate(this.limitDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
	}

	/** String型でセット */
	public void setLimitDateByLocalDate(LocalDate limitDt) {
		setLimitDate(DateUtils.parseToString(limitDt, DateUtils.DATE_FORMAT_SLASH_DELIMITED));
	}

	/** LocalTime変換 */
	public LocalTime getLimitTime() {
		if (StringUtils.isEmpty(this.limitTimeHour) || StringUtils.isEmpty(this.limitTimeMinute)) {
			return null;
		}
		return LocalTime.of(Integer.parseInt(this.limitTimeHour), Integer.parseInt(this.limitTimeMinute));
	}

	/**
	 * 時間の相関バリデート TODO メッセージプロパティに追加
	 */
	@AssertTrue(message = "時間を入力するときは、「時」と「分」をどちらも入力してください")
	public boolean isTimeCorrelateValid() {

		// 時間の相関バリデート
		if (StringUtils.isEmpty(this.limitTimeHour) && StringUtils.isNotEmpty(this.limitTimeMinute)) {
			return false;
		} else if (StringUtils.isEmpty(this.limitTimeMinute) && StringUtils.isNotEmpty(this.limitTimeHour)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 日付と時間の相関バリデート TODO メッセージプロパティに追加
	 */
	@AssertTrue(message = "日付が入力されていない時は、時間は登録できません")
	public boolean isDateCorralateValid() {

		// 日付が入力されていないのに、時間が入力されている場合
		if (StringUtils.isNotEmpty(this.limitTimeMinute) || StringUtils.isNotEmpty(this.limitTimeHour)) {
			if (StringUtils.isEmpty(this.limitDate)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * アカウント名取得
	 * 
	 * @param accountSeq
	 * @return
	 */
	public String getAccountName(Long accountSeq) {
		Map<Long, String> seqToMap = new HashMap<>();

		seqToMap.putAll(this.tantoOptions.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));

		return seqToMap.get(accountSeq);
	}

	/**
	 * タスク登録モーダル担当者欄表示データ
	 */
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TaskTanto {

		/** アカウントSEQ */
		private Long accountSeq;

		/** 担当者フラグ */
		private boolean isWorkerFlg;

		/** アカウント色 */
		private String accountColor;

	}
}
