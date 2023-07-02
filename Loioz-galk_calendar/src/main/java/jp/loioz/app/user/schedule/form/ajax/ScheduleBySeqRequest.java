package jp.loioz.app.user.schedule.form.ajax;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 予定SEQで予定を取得するAPIのリクエスト情報
 */
@Data
public class ScheduleBySeqRequest {

	/** 予定SEQ */
	private List<Long> scheduleSeq = new ArrayList<>();
}