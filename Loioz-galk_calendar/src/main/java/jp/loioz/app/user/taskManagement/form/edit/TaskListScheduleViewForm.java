package jp.loioz.app.user.taskManagement.form.edit;

import jp.loioz.dto.TaskListDto;
import lombok.Data;

/**
 * タスク管理画面のフォームクラス
 */
@Data
public class TaskListScheduleViewForm {

	/** タスクリスト詳細 */
	private TaskListDto taskDto = new TaskListDto();

}
