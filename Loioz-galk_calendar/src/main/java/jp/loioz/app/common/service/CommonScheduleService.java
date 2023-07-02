package jp.loioz.app.common.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.schedule.form.ScheduleDetail;
import jp.loioz.app.user.schedule.form.ajax.ScheduleBySeqRequest;
import jp.loioz.app.user.schedule.service.ScheduleCommonService;

/**
 * 共通予定処理クラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonScheduleService extends DefaultService {

	/** スケジュール共通サービス */
	@Autowired
	private ScheduleCommonService scheduleCommonService;

	/**
	 * 予定SEQで予定を取得する ※予定SEQが一意となるデータを返却する
	 * 
	 * ScheduleCommonService.getSchedulePKOneのラッパーメソッド
	 * 外部画面サービスから呼び出す場合は当メソッドを利用する
	 * 
	 * @param scheduleSeqList
	 * @return
	 */
	public Map<Long, ScheduleDetail> getSchedulePKOne(List<Long> scheduleSeqList) {

		if (CollectionUtils.isEmpty(scheduleSeqList)) {
			return Collections.emptyMap();
		}

		ScheduleBySeqRequest request = new ScheduleBySeqRequest();
		request.setScheduleSeq(scheduleSeqList);

		return scheduleCommonService.getSchedulePKOne(request);
	}

}
