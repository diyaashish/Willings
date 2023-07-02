package jp.loioz.app.user.ankenDashboard.form;

import jp.loioz.common.constant.CommonConstant.DashGyomuAreaDispType;
import lombok.Data;

/**
 * 案件ダッシュボードの表示に関連するパラメータを保持するフォームオブジェクト
 */
@Data
public class AnkenDashboardSearchForm {

	/** Session管理している案件ID */
	private Long keepAnkenId;

	/** タスク一覧：すべて表示 */
	private boolean allDispTaskList;

	/** 予定一覧：すべて表示 */
	private boolean allDispScheduleList;

	/** 業務履歴エリア：表示種別 */
	private DashGyomuAreaDispType dispType = DashGyomuAreaDispType.GYOMUHISTORY;

	/** 初期遷移時の初期化処理 */
	public void initForm() {
		this.keepAnkenId = null;
		this.allDispTaskList = false;
		this.allDispScheduleList = false;
		this.dispType = DashGyomuAreaDispType.GYOMUHISTORY;
	}

}
