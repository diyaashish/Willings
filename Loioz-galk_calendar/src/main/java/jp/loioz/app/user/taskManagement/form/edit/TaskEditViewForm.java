package jp.loioz.app.user.taskManagement.form.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.dto.AccountJoiningBushoDto;
import jp.loioz.dto.ActivityHistoryDto;
import jp.loioz.dto.TaskHistoryDto;
import lombok.Data;

/**
 * タスク編集画面のフォームクラス
 */
@Data
public class TaskEditViewForm {

	/** タスクSEQ */
	private Long taskSeq;

	/** 部署情報 */
	private List<SelectOptionForm> bushoList;

	/** アカウントごとの所属部署情報 */
	private List<AccountJoiningBushoDto> accountJoiningBushoList;

	/** コメント数 */
	private int numberOfComment;

	/** コメント履歴 */
	private List<TaskHistoryDto> commentList;

	/** アクティビティ履歴 */
	private List<ActivityHistoryDto> activityList;

	// タスク-履歴(t_task_history)情報
	/** タスクに紐づく履歴情報 */
	private List<TaskHistoryDto> taskHistoryList = new ArrayList<>();

	/**
	 * 新規登録モード判定
	 * 
	 * @return
	 */
	public boolean isNew() {
		boolean newFlg = false;
		if (Objects.isNull(this.taskSeq)) {
			newFlg = true;
		}
		return newFlg;
	}

}
