package jp.loioz.dto;

import lombok.Data;

@Data
public class TaskCountDto {

	/** 今日のタスク件数 */
	private Integer numberOfTodayTask;

	/** 今日のタスクの新着、未確認タスク数 */
	private Integer numberOfTodayNewTask;

	/** 期限付きのタスク数 */
	private Integer numberOfFutureTask;

	/** 期限付きのタスクの新着、未確認タスク数 */
	private Integer numberOfFutureNewTask;

	/** すべてのタスク数 */
	private Integer numberOfAllTask;

	/** すべてのタスクの新着、未確認タスク数 */
	private Integer numberOfAllNewTask;

	/** 期限を過ぎたタスク数 */
	private Integer numberOfOverdueTask;

	/** 期限を過ぎたタスクの新着、未確認タスク数 */
	private Integer numberOfOverdueNewTask;

	/** 割り当てられたタスク数 */
	private Integer numberOfAssignedTask;

	/** 割り当てられたタスクの新着、未確認タスク数 */
	private Integer numberOfAssignedNewTask;

	/** 割り当てたタスク数 */
	private Integer numberOfAssignTask;

	/** 割り当てたタスクの中の新着、未確認タスク数 */
	private Integer numberOfAssignNewTask;

	/** 完了したタスクの中の新着、未確認タスク数 */
	private Integer numberOfCompletedNewTask;

}
