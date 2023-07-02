package jp.loioz.app.common.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.user.schedule.form.ScheduleCommonViewForm.Room;
import jp.loioz.dao.MHolidayDao;
import jp.loioz.dao.MRoomDao;
import jp.loioz.entity.MHolidayEntity;
import jp.loioz.entity.MRoomEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class CommonCalendarService {

	/** 祝日マスタ(MGT) */
	@Autowired
	private MHolidayDao mHolidayMgtDao;

	/** 施設 */
	@Autowired
	private MRoomDao mRoomDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 祝日情報のMapを取得 key：日付, value：祝日名
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public Map<LocalDate, String> getHolidaysMap(LocalDate from, LocalDate to) {

		var mHolidayEntities = mHolidayMgtDao.selectByFromTo(from, to);
		Map<LocalDate, String> holidaysMap = mHolidayEntities.stream().collect(
				Collectors.toMap(
						MHolidayEntity::getHolidayDate,
						MHolidayEntity::getHolidayName));

		return holidaysMap;
	}

	/**
	 * 施設候補リストを取得します
	 * 
	 * @return
	 */
	public List<SelectOptionForm> getRoomList() {

		// 会議室
		List<Room> roomList = mRoomDao.selectEnabled()
				.stream()
				.sorted(Comparator.comparing(MRoomEntity::getDispOrder))
				.map(entity -> Room.builder()
						.roomId(entity.getRoomId())
						.roomName(entity.getRoomName())
						.build())
				.collect(Collectors.toList());

		return roomList.stream().map(room -> {
			SelectOptionForm form = new SelectOptionForm(room.getRoomId(), room.getRoomName());
			return form;
		}).collect(Collectors.toList());
	}

}
