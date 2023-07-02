package jp.loioz.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.common.utility.DateUtils;
import lombok.Data;

@Data
public class TaskListForTaskManagementDto {

	/** タスク種類のタイトル */
	private String taskTypeTitle;

	/** タスク種類のステータスEnum */
	private String taskStatusCd;

	/** 割り当てたアカウントSEQ、割り当てられたアカウントSEQ */
	private Long taskAssignSeq;

	/** 割り当てたアカウント名、割り当てられたアカウント名 */
	private String taskAssignName;
	
	/** 期限で分ける場合の基準日 */
	private LocalDate limitDateCategoryDate;

	/** タスク一覧情報 */
	private List<TaskListDto> taskList = new ArrayList<TaskListDto>();

	/** ページ */
	private Page<TaskListDto> page;

	/**
	 * 期限で分ける場合の基準日情報を文字列で取得<br>
	 * ※形式はyyyy/MM/dd
	 * 
	 * @return
	 */
	public String getLimitDateCategoryDateStr() {
		if (this.limitDateCategoryDate == null) {
			return "";
		}
		
		return DateUtils.parseToString(this.limitDateCategoryDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
	}
}
