package jp.loioz.app.user.taskManagement.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.taskManagement.dto.TaskAnkenAddModalDto;
import lombok.Data;

/**
 * 案件タスク追加モーダルの表示フォームクラス
 */
@Data
public class TaskAnkenAddModalViewForm {

	/** 表示件数を超えたフラグ */
	private boolean overViewCntFlg;

	/** 検索結果（すでに案件タスク追加してある案件は取得されない） */
	List<TaskAnkenAddModalDto> taskAnkenAddList = Collections.emptyList();

}