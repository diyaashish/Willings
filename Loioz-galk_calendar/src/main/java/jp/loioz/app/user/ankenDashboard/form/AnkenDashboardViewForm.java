package jp.loioz.app.user.ankenDashboard.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.ankenDashboard.dto.AnkenDashboardGyomuHistoryListDto;
import jp.loioz.app.user.ankenDashboard.dto.AnkenDashboardScheduleListDto;
import jp.loioz.app.user.ankenDashboard.dto.AnkenDashboardTaskListDto;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.dto.BunyaDto;
import lombok.Data;

/**
 * 案件ダッシュボードの画面表示フォームクラス
 */
@Data
public class AnkenDashboardViewForm {

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野 */
	private BunyaDto bunya;

	/** 顧客ID */
	private Long customerId;

	/** タスクエリア表示用オブジェクト */
	private AnkenDashboardTaskViewForm ankenDashboardTaskViewForm;

	/** 予定エリア表示用オブジェクト */
	private AnkenDashboardScheduleViewForm ankenDashboardScheduleViewForm;

	/** 業務履歴エリア表示用オブジェクト */
	private AnkenDashboardGyomuHistoryViewForm ankenDashboardGyomuHistoryViewForm;

	/**
	 * タスクエリア表示用オブジェクト
	 */
	@Data
	public static class AnkenDashboardTaskViewForm {

		/** 案件ID */
		private AnkenId ankenId;

		/** 未処理：件数 */
		private long noCompCount;

		/** タスク一覧 */
		private List<AnkenDashboardTaskListDto> taskList = Collections.emptyList();

	}

	/**
	 * 予定エリア表示用オブジェクト
	 */
	@Data
	public static class AnkenDashboardScheduleViewForm {

		/** 案件ID */
		private AnkenId ankenId;

		/** 案件名 */
		private String ankenName;

		/** 分野 */
		private BunyaDto bunya;

		/** 期日前件数 */
		private long beforeKijituCount;

		/** スケジュール一覧 */
		private List<AnkenDashboardScheduleListDto> scheduleList = Collections.emptyList();

	}

	/**
	 * 業務履歴エリア表示用オブジェクト
	 */
	@Data
	public static class AnkenDashboardGyomuHistoryViewForm {

		/** 案件ID */
		private AnkenId ankenId;

		/** 業務履歴情報 */
		List<AnkenDashboardGyomuHistoryListDto> gyomuHistoryList = Collections.emptyList();

	}
}
