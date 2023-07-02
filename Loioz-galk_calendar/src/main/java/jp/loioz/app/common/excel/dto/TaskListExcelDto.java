package jp.loioz.app.common.excel.dto;

import java.util.List;

import jp.loioz.dto.TaskListDto;
import jp.loioz.dto.TaskListForTaskManagementDto;
import lombok.Data;

/**
 * タスク一覧用のExcelDto
 */
@Data
public class TaskListExcelDto {

	/** 選択中のタスク画面 */
	private String selectedTaskListMenu;

	/** Excelに出力する案件一覧データ（今日のタスク、期限付きのタスク、すべてのタスク、期限を過ぎたタスク、完了したタスク用 */
	private List<TaskListDto> taskListDtoList;

	/** Excelに出力する案件一覧データ（割り当てられたタスク、割り当てたタスク用） */
	private List<TaskListForTaskManagementDto> taskListForTaskManagementDtoList;
}