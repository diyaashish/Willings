package jp.loioz.app.user.schedule.form;

import java.util.ArrayList;
import java.util.List;

import jp.loioz.dto.TaskListDto;
import lombok.Data;

/**
 * 予定表画面の画面表示フォームクラス
 */
@Data
public class ScheduleViewForm {

	/** タスク一覧を表示するか否か */ // デフォルトはfalse;;
	private boolean scheduleOpenTaskFlg = false;

	/** タスク一覧情報 */
	private List<TaskListDto> taskList = new ArrayList<TaskListDto>();

	/** 依頼したタスクも表示 */
	private boolean isAllRelatedTask;

	/** タスク一覧のソートキーコード  1:設定順 、2:期限日-昇順、3:期限日-降順 */
	private String taskListSortKeyCd;

}