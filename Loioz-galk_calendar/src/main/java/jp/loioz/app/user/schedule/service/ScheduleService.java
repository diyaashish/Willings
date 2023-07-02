package jp.loioz.app.user.schedule.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAccountSettingService;
import jp.loioz.app.user.schedule.form.ScheduleDetail;
import jp.loioz.app.user.schedule.form.ScheduleInputForm;
import jp.loioz.app.user.schedule.form.ScheduleViewForm;
import jp.loioz.app.user.schedule.form.ajax.RoomScheduleRequest;
import jp.loioz.app.user.schedule.form.ajax.RoomScheduleResponse;
import jp.loioz.app.user.schedule.form.ajax.ScheduleRequest;
import jp.loioz.app.user.schedule.form.ajax.ScheduleResponse;
import jp.loioz.app.user.schedule.logic.ScheduleRepeatConditionDateTimeAdapter;
import jp.loioz.app.user.taskManagement.service.TaskCommonService;
import jp.loioz.common.constant.AccountSettingConstant;
import jp.loioz.common.constant.CommonConstant.SchedulePermission;
import jp.loioz.common.constant.CommonConstant.ShutteiType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MHolidayDao;
import jp.loioz.dao.ScheduleDao;
import jp.loioz.dao.TSaibanLimitDao;
import jp.loioz.dao.TScheduleDao;
import jp.loioz.dto.TaskListDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MHolidayEntity;
import jp.loioz.entity.TSaibanLimitEntity;
import jp.loioz.entity.TScheduleEntity;

/**
 * 予定表画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ScheduleService extends DefaultService {

	/** 予定共通サービス */
	@Autowired
	private ScheduleCommonService scheduleCommonService;

	/** 共通アカウント設定用のDaoクラス */
	@Autowired
	CommonAccountSettingService commonAccountSettingService;

	/** タスク共通サービス */
	@Autowired
	private TaskCommonService taskCommonService;

	/** 祝日マスタ(mgt) */
	@Autowired
	private MHolidayDao mHolidayMgtDao;

	@Autowired
	private ScheduleDao scheduleDao;

	@Autowired
	private TScheduleDao tScheduleDao;

	@Autowired
	private MAccountDao mAccountDao;

	@Autowired
	private TSaibanLimitDao tSaibanLimitDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 画面表示情報を作成する
	 *
	 * @return 画面表示情報
	 */
	public ScheduleViewForm createViewForm() {
		ScheduleViewForm form = new ScheduleViewForm();
		return form;
	}

	/**
	 * 入力日時に予定が登録されているアカウントを取得
	 * 
	 * @param form
	 * @return
	 */
	public List<String> getConflictAccount(ScheduleInputForm form) {
		return scheduleCommonService.getConflictAccount(form);
	}

	/**
	 * 登録処理
	 *
	 * @param form フォーム入力値
	 * @return 登録した予定の予定SEQ
	 */
	public Long create(ScheduleInputForm form) {
		return scheduleCommonService.create(form);
	}

	/**
	 * 更新処理
	 *
	 * @param form フォーム入力値
	 */
	public void update(ScheduleInputForm form) {

		scheduleCommonService.update(form);

		if (form.isSaibanLimit()) {
			// 裁判期日の場合、手続きを連動する
			updateSaibanLimit(form);
		}
	}

	/**
	 * 削除処理
	 *
	 * @param scheduleSeq 予定SEQ
	 */
	public void delete(Long scheduleSeq) {

		// 予定
		TScheduleEntity scheduleEntity = tScheduleDao.selectBySeq(scheduleSeq);

		// 予定が存在しない場合
		if (scheduleEntity == null) {
			throw new DataNotFoundException("予定が存在しません。");
		}

		// 裁判期日は予定表からは削除不可
		if (scheduleEntity.getSaibanLimitSeq() != null) {
			// TODO エラー処理
			throw new RuntimeException("裁判期日は予定表からは削除できません。");
		}

		scheduleCommonService.delete(scheduleSeq);
	}

	/**
	 * 予定を取得する
	 *
	 * @param scheduleRequest 取得条件
	 * @return 予定
	 */
	public ScheduleResponse getSchedule(ScheduleRequest scheduleRequest) {

		LocalDate requestDateFrom = scheduleRequest.getDateFrom();
		LocalDate requestDateTo = scheduleRequest.getDateTo();

		// 祝日データの取得
		List<MHolidayEntity> MHolidayEntities = mHolidayMgtDao.selectByFromTo(requestDateFrom, requestDateTo);

		// 祝日データ -> カレンダー表示データ
		List<ScheduleDetail> holidaySchedule = scheduleCommonService.mHolidayEntityToOutputData(MHolidayEntities, requestDateFrom, requestDateTo);

		// 予定
		List<TScheduleEntity> scheduleEntityList = scheduleDao.selectScheduleByDateRange(requestDateFrom, requestDateTo);

		// DBからの取得情報を画面用に変換
		List<ScheduleDetail> scheduleList = scheduleCommonService.scheduleEntityToOutputData(scheduleEntityList, requestDateFrom, requestDateTo);

		// 無効な予定を除外
		List<ScheduleDetail> filteredScheduleList = filterInvalidSchedule(scheduleList);

		// 予定データに祝日を追加する
		filteredScheduleList.addAll(holidaySchedule);

		// 予定を予定繰り返し条件アダプターでラップ
		List<ScheduleRepeatConditionDateTimeAdapter<ScheduleDetail>> scheduleAdapterList = filteredScheduleList.stream()
				.map(ScheduleRepeatConditionDateTimeAdapter::new)
				.collect(Collectors.toList());

		// 日付毎の予定
		Map<LocalDate,
				List<ScheduleRepeatConditionDateTimeAdapter<ScheduleDetail>>> scheduleByDate = scheduleCommonService.expandScheduleRepeatByDate(
						requestDateFrom, requestDateTo, scheduleAdapterList);

		Map<LocalDate, List<String>> scheduleSeqByDate = scheduleByDate.entrySet().stream()
				.collect(Collectors.toMap(
						Entry::getKey,
						entry -> entry.getValue().stream()
								.map(ScheduleRepeatConditionDateTimeAdapter::getScheduleDateTime)
								.map(ScheduleDetail::getScheduleSankasyaPk)
								.collect(Collectors.toList())));

		// 画面表示対象の予定SEQ
		List<String> displayScheduleSeqCandidates = scheduleSeqByDate.values().stream()
				.flatMap(List::stream)
				.distinct()
				.collect(Collectors.toList());

		Map<String, ScheduleDetail> scheduleMap = filteredScheduleList.stream()
				// 画面表示対象外の予定情報を除外
				.filter(schedule -> displayScheduleSeqCandidates.contains(schedule.getScheduleSankasyaPk()))
				// ログインユーザに対して非公開の予定情報をマスキング
				.map(this::maskSchedule)
				.collect(Collectors.toMap(
						ScheduleDetail::getScheduleSankasyaPk,
						Function.identity()));

		// 表示のため、並び順を若い日付順にする
		Map<LocalDate, List<String>> scheduleSeqByDateAscOrder = scheduleSeqByDate.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(oldValue, newValue) -> oldValue, LinkedHashMap::new));

		// 画面で使用する値
		ScheduleResponse scheduleResponse = new ScheduleResponse();
		scheduleResponse.setScheduleByDate(scheduleSeqByDateAscOrder);
		scheduleResponse.setSchedule(scheduleMap);

		return scheduleResponse;
	}

	/**
	 * 会議室毎の予定を取得する
	 *
	 * @param scheduleRequest 取得条件
	 * @return 予定
	 */
	public RoomScheduleResponse getRoomSchedule(RoomScheduleRequest scheduleRequest) {

		LocalDate requestDate = scheduleRequest.getDate();

		// 予定
		List<TScheduleEntity> scheduleEntityList = scheduleDao.selectRoomScheduleByDateRange(requestDate, requestDate);

		// DBからの取得情報を画面用に変換
		List<ScheduleDetail> scheduleList = scheduleCommonService.scheduleEntityToOutputData(scheduleEntityList);

		// 無効な予定を除外
		List<ScheduleDetail> filteredScheduleList = filterInvalidSchedule(scheduleList);

		// 予定を予定繰り返し条件アダプターでラップ
		List<ScheduleRepeatConditionDateTimeAdapter<ScheduleDetail>> scheduleAdapterList = filteredScheduleList.stream()
				.map(ScheduleRepeatConditionDateTimeAdapter::new)
				.collect(Collectors.toList());

		// 日付毎の予定
		Map<LocalDate,
				List<ScheduleRepeatConditionDateTimeAdapter<ScheduleDetail>>> scheduleByDate = scheduleCommonService.expandScheduleRepeatByDate(
						requestDate, requestDate, scheduleAdapterList);

		// 画面表示対象の予定SEQ
		List<String> displayScheduleSeqCandidates = scheduleByDate.getOrDefault(requestDate, Collections.emptyList()).stream()
				.map(ScheduleRepeatConditionDateTimeAdapter::getScheduleDateTime)
				.map(ScheduleDetail::getScheduleSankasyaPk)
				.distinct()
				.collect(Collectors.toList());

		Map<String, ScheduleDetail> scheduleMap = filteredScheduleList.stream()
				// 画面表示対象外の予定情報を除外
				.filter(schedule -> displayScheduleSeqCandidates.contains(schedule.getScheduleSankasyaPk()))
				// ログインユーザに対して非公開の予定情報をマスキング
				.map(this::maskSchedule)
				.collect(Collectors.toMap(
						ScheduleDetail::getScheduleSankasyaPk,
						Function.identity()));

		// 会議室毎の予定
		Map<Long, List<String>> scheduleSeqByRoom = scheduleMap.values().stream()
				.filter(schedule -> schedule.getRoomId() != null)
				.collect(Collectors.groupingBy(
						ScheduleDetail::getRoomId,
						Collectors.mapping(
								ScheduleDetail::getScheduleSankasyaPk,
								Collectors.toList())));

		RoomScheduleResponse scheduleResponse = new RoomScheduleResponse();
		scheduleResponse.setScheduleByRoom(scheduleSeqByRoom);
		scheduleResponse.setSchedule(scheduleMap);

		return scheduleResponse;
	}

	/**
	 * アカウント毎に分割されていない予定情報を取得
	 * 
	 * @param scheduleRequest
	 * @return
	 */
	public ScheduleResponse getScheduleItemList(ScheduleRequest scheduleRequest) {

		LocalDate requestDateFrom = scheduleRequest.getDateFrom();
		LocalDate requestDateTo = scheduleRequest.getDateTo();

		// 祝日データの取得
		List<MHolidayEntity> MHolidayEntities = mHolidayMgtDao.selectByFromTo(requestDateFrom, requestDateTo);

		// 祝日データ -> カレンダー表示データ
		List<ScheduleDetail> holidaySchedule = scheduleCommonService.mHolidayEntityToOutputData(MHolidayEntities, requestDateFrom, requestDateTo);

		// 予定
		List<TScheduleEntity> scheduleEntityList = scheduleDao.selectScheduleByDateRange(requestDateFrom, requestDateTo);

		// DBからの取得情報を画面用に変換
		List<ScheduleDetail> scheduleList = scheduleCommonService.scheduleEntityToNonSplitMemberData(scheduleEntityList, requestDateFrom, requestDateTo);

		// 無効な予定を除外
		List<ScheduleDetail> filteredScheduleList = filterInvalidSchedule(scheduleList);

		// 予定データに祝日を追加する
		filteredScheduleList.addAll(holidaySchedule);

		// ログインユーザーの権限でマスキング
		List<ScheduleDetail> maskedScheduleList = filteredScheduleList.stream().map(this::maskSchedule).collect(Collectors.toList());

		Function<ScheduleDetail, Integer> holidaySort = entity -> {
			// Integerのソートは数値が低い方が早いので、booleanを反転させる
			boolean boolKey = !entity.isHoliday();
			// 反転したbooleanをIntegerにする
			return Integer.parseInt(SystemFlg.booleanToCode(boolKey));
		};

		// 表示順をソートする
		List<ScheduleDetail> result = new ArrayList<>();
		if (Objects.equals(scheduleRequest.getDateFrom(), scheduleRequest.getDateTo())) {
			// 一日のみ取得する場合 -> 祝日 > 日付From > 時間From
			result = maskedScheduleList.stream()
					.sorted(Comparator.comparing(holidaySort).thenComparing(ScheduleDetail::getDateFrom).thenComparing(ScheduleDetail::getHourFrom).thenComparing(ScheduleDetail::getMinFrom))
					.collect(Collectors.toList());
		} else {
			// 複数日取得する場合 -> 日付From > 祝日 > 時間From
			result = maskedScheduleList.stream()
					.sorted(Comparator.comparing(ScheduleDetail::getDateFrom).thenComparing(holidaySort).thenComparing(ScheduleDetail::getHourFrom).thenComparing(ScheduleDetail::getMinFrom))
					.collect(Collectors.toList());
		}

		// 画面で使用する値
		ScheduleResponse scheduleResponse = new ScheduleResponse();
		scheduleResponse.setScheduleList(result);

		return scheduleResponse;
	}

	/**
	 * カレンダー画面で表示するタスク一覧をセットします
	 * 
	 * @param scheduleViewForm
	 */
	public void setTaskListBySchedule(ScheduleViewForm scheduleViewForm) {
		List<TaskListDto> taskList = taskCommonService.getTaskListBySchedule(scheduleViewForm.getTaskListSortKeyCd(), scheduleViewForm.isAllRelatedTask());
		scheduleViewForm.setTaskList(taskList);
	}

	/**
	 * カレンダー画面のタスクの表示設定を保存します
	 * 
	 * @param sortKeyCd
	 */
	public void saveCalendarTaskViewOption(String viewKeyCd) throws AppException {
		commonAccountSettingService.saveSetting(SessionUtils.getLoginAccountSeq(), AccountSettingConstant.SettingType.CALENDAR_TASK_VIEW, viewKeyCd);
	}

	/**
	 * カレンダー画面のソート設定を保存します
	 * 
	 * @param sortKeyCd
	 */
	public void saveCalendarTaskSortKey(String sortKeyCd) throws AppException {
		commonAccountSettingService.saveSetting(SessionUtils.getLoginAccountSeq(), AccountSettingConstant.SettingType.CALENDAR_TASK_SORT, sortKeyCd);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 予定表で無効扱いする(非表示にする)予定をフィルタリングする
	 *
	 * @param scheduleList 予定表示候補
	 * @return 有効な予定
	 */
	private List<ScheduleDetail> filterInvalidSchedule(List<ScheduleDetail> scheduleList) {

		// 有効なアカウント
		List<MAccountEntity> accountEntityList = mAccountDao.selectEnabledAccount();
		List<Long> enabledAccountSeqList = accountEntityList.stream()
				.map(MAccountEntity::getAccountSeq)
				.collect(Collectors.toList());

		// 無効な予定を除外
		List<ScheduleDetail> filteredScheduleList = scheduleList.stream()
				.filter(schedule -> {
					// 有効な参加者が存在しない予定は除外
					return schedule.getMember().stream().anyMatch(enabledAccountSeqList::contains);
				})
				.filter(schedule -> {
					// 裁判期日に紐づいていて出廷不要の予定は除外
					return schedule.getShutteiType() != ShutteiType.NOT_REQUIRED;
				})
				.collect(Collectors.toList());

		return filteredScheduleList;
	}

	/**
	 * ログインユーザに対して非公開の予定情報をマスキングする
	 *
	 * @param orgSchedule 元の予定情報
	 * @return マスキングした予定情報
	 */
	private ScheduleDetail maskSchedule(ScheduleDetail orgSchedule) {

		if (orgSchedule.getOpenRange() != SchedulePermission.MEMBER) {
			// 公開範囲に制限がなければマスキング不要
			return orgSchedule;
		}
		if (orgSchedule.getMember().contains(SessionUtils.getLoginAccountSeq())) {
			// ログインユーザが参加者に含まれていればマスキング不要
			return orgSchedule;
		}

		// 非公開情報のマスキング処理
		ScheduleDetail schedule = new ScheduleDetail();

		schedule.setScheduleSeq(orgSchedule.getScheduleSeq());
		schedule.setScheduleSankasyaPk(orgSchedule.getScheduleSankasyaPk());
		schedule.setSubject("[非公開]");

		schedule.setDateFrom(orgSchedule.getDateFrom());
		schedule.setDateTo(orgSchedule.getDateTo());
		schedule.setAllday(orgSchedule.isAllday());
		if (!orgSchedule.isAllday()) {
			schedule.setTimeFrom(orgSchedule.getTimeFrom());
			schedule.setTimeTo(orgSchedule.getTimeTo());
			schedule.setHourFrom(orgSchedule.getTimeFrom().getHour());
			schedule.setMinFrom(orgSchedule.getTimeFrom().getMinute());
			schedule.setHourTo(orgSchedule.getTimeTo().getHour());
			schedule.setMinTo(orgSchedule.getTimeTo().getMinute());
		}
		schedule.setMember(orgSchedule.getMember());
		schedule.setMemberOne(orgSchedule.getMemberOne());
		schedule.setForbidden(true);
		if (orgSchedule.getRoomId() != null) {
			// 場所が会議室の場合はそのまま表示
			schedule.setRoomId(orgSchedule.getRoomId());
			schedule.setPlaceDisp(orgSchedule.getPlaceDisp());
		} else {
			// 場所がその他の場合はマスキング
			schedule.setPlaceDisp("[非公開]");
		}
		schedule.setWeekViewCnt(orgSchedule.getWeekViewCnt());
		schedule.setWeekViewDate(orgSchedule.getWeekViewDate());
		schedule.setWeekViewOrder(orgSchedule.getWeekViewOrder());

		return schedule;
	}

	/**
	 * 裁判期日の手続きと連動
	 * 
	 * @param form フォーム入力値
	 */
	private void updateSaibanLimit(ScheduleInputForm form) {

		TSaibanLimitEntity tSaibanLimitEntity = tSaibanLimitDao.selectBySeq(form.getSaibanLimitSeq());

		// 手続きを連動
		tSaibanLimitEntity.setContent(form.getMemo());
		tSaibanLimitDao.update(tSaibanLimitEntity);
	}
}