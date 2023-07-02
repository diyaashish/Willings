package jp.loioz.app.user.schedule.form.ajax;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

/**
 * 会議室予約状況取得のレスポンス情報
 */
@Data
public class RoomAvailabilityResponse {

	/** 予約できない会議室のID */
	private Set<Long> unavailableRoomId = new HashSet<>();
}