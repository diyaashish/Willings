package jp.loioz.app.user.taskManagement.form.list;

import java.util.ArrayList;
import java.util.List;

import jp.loioz.dto.TaskListDto;
import lombok.Data;

/**
 * タスク管理画面のフォームクラス
 */
@Data
public class TaskListViewForm {

	/** タスク一覧情報 */
	private List<TaskListDto> taskList = new ArrayList<TaskListDto>();

}
