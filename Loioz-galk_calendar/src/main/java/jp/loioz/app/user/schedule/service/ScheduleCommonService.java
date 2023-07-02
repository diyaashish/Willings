package jp.loioz.app.user.schedule.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.HtmlUtils;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonSaibanService;
import jp.loioz.app.common.validation.accessDB.CommonScheduleValidator;
import jp.loioz.app.user.schedule.form.ScheduleCommonViewForm;
import jp.loioz.app.user.schedule.form.ScheduleCommonViewForm.Account;
import jp.loioz.app.user.schedule.form.ScheduleCommonViewForm.Busho;
import jp.loioz.app.user.schedule.form.ScheduleCommonViewForm.Group;
import jp.loioz.app.user.schedule.form.ScheduleCommonViewForm.Room;
import jp.loioz.app.user.schedule.form.ScheduleDetail;
import jp.loioz.app.user.schedule.form.ScheduleInputForm;
import jp.loioz.app.user.schedule.form.ajax.RoomAvailabilityRequest;
import jp.loioz.app.user.schedule.form.ajax.RoomAvailabilityResponse;
import jp.loioz.app.user.schedule.form.ajax.ScheduleBySeqRequest;
import jp.loioz.app.user.schedule.logic.ScheduleDateTimeInput;
import jp.loioz.app.user.schedule.logic.ScheduleRepeatCondition;
import jp.loioz.app.user.schedule.logic.ScheduleRepeatConditionDateTimeAdapter;
import jp.loioz.app.user.schedule.logic.ScheduleRepeatConditionEntityAdapter;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountStatus;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.CalenderOutputType;
import jp.loioz.common.constant.CommonConstant.JsDayOfWeek;
import jp.loioz.common.constant.CommonConstant.SchedulePermission;
import jp.loioz.common.constant.CommonConstant.ScheduleRepeatType;
import jp.loioz.common.constant.CommonConstant.ShutteiType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.LoiozObjectUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MBushoDao;
import jp.loioz.dao.MGroupDao;
import jp.loioz.dao.MRoomDao;
import jp.loioz.dao.ScheduleDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TBushoShozokuAcctDao;
import jp.loioz.dao.TGroupShozokuAcctDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSaibanAddKeijiDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.dao.TSaibanLimitDao;
import jp.loioz.dao.TSaibanTantoDao;
import jp.loioz.dao.TScheduleAccountDao;
import jp.loioz.dao.TScheduleDao;
import jp.loioz.domain.condition.ScheduleSearchCondition;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.domain.value.SaibanId;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MBushoEntity;
import jp.loioz.entity.MGroupEntity;
import jp.loioz.entity.MHolidayEntity;
import jp.loioz.entity.MRoomEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TAnkenTantoEntity;
import jp.loioz.entity.TBushoShozokuAcctEntity;
import jp.loioz.entity.TGroupShozokuAcctEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSaibanAddKeijiEntity;
import jp.loioz.entity.TSaibanEntity;
import jp.loioz.entity.TSaibanJikenEntity;
import jp.loioz.entity.TSaibanLimitEntity;
import jp.loioz.entity.TSaibanTantoEntity;
import jp.loioz.entity.TScheduleAccountEntity;
import jp.loioz.entity.TScheduleEntity;

/**
 * 予定共通サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ScheduleCommonService extends DefaultService {

	/** 予定繰り返し期間上限 */
	public static final Period SCHEDULE_REPEAT_LIMIT = Period.ofYears(1).minusDays(1);

	/** 共通アカウントサービスクラス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 共通案件サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 共通裁判サービスクラス */
	@Autowired
	private CommonSaibanService commonSaibanService;

	/** 共通分野サービスクラス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 共通予定バリデーションサービスクラス */
	@Autowired
	private CommonScheduleValidator commonScheduleValidator;

	/** メッセージ共通サービス */
	@Autowired
	private MessageService messageService;

	@Autowired
	private ScheduleDao scheduleDao;

	@Autowired
	private TScheduleDao tScheduleDao;

	@Autowired
	private TScheduleAccountDao tScheduleAccountDao;

	@Autowired
	private MRoomDao mRoomDao;

	@Autowired
	private MAccountDao mAccountDao;

	@Autowired
	private MBushoDao mBushoDao;

	@Autowired
	private TBushoShozokuAcctDao tBushoShozokuAcctDao;

	@Autowired
	private MGroupDao mGroupDao;

	@Autowired
	private TGroupShozokuAcctDao tGroupShozokuAcctDao;

	@Autowired
	private TPersonDao tPersonDao;

	@Autowired
	private TAnkenDao tAnkenDao;

	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	@Autowired
	private TSaibanDao tSaibanDao;

	@Autowired
	private TSaibanTantoDao tSaibanTantoDao;

	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	@Autowired
	private TSaibanLimitDao tSaibanLimitDao;

	@Autowired
	private TSaibanAddKeijiDao tSaibanAddKeijiDao;

	/**
	 * 予定共通表示情報を作成する
	 *
	 * @return 予定共通表示情報
	 */
	public ScheduleCommonViewForm createCommonViewForm() {

		ScheduleCommonViewForm form = new ScheduleCommonViewForm();

		// 会議室
		List<Room> roomList = mRoomDao.selectEnabled()
				.stream()
				.sorted(Comparator.comparing(MRoomEntity::getDispOrder))
				.map(entity -> Room.builder()
						.roomId(entity.getRoomId())
						.roomName(entity.getRoomName())
						.build())
				.collect(Collectors.toList());
		form.setRoomList(roomList);

		// 所属部署情報の取得
		List<TBushoShozokuAcctEntity> bushoShozokuAcctEntityList = tBushoShozokuAcctDao.selectByEnabledAccount();
		Map<Long, List<TBushoShozokuAcctEntity>> seqToShozokuuAcctMap = bushoShozokuAcctEntityList.stream()
				.collect(Collectors.groupingBy(TBushoShozokuAcctEntity::getAccountSeq));

		// アカウント
		Map<Long, Account> seqToAccountMap = mAccountDao.selectAll()
				.stream()
				.sorted(Comparator.comparing(MAccountEntity::getAccountType).thenComparing(
						Comparator.comparing(MAccountEntity::getAccountSeq)))
				.collect(Collectors.toMap(
						MAccountEntity::getAccountSeq,
						entity -> {
							PersonName accountName = PersonName.fromEntity(entity);
							StringBuilder shozokuBushoIdSb = new StringBuilder(CommonConstant.SANKASHA_SELECT_OPTION_ALL);

							List<TBushoShozokuAcctEntity> shozokuBusho = seqToShozokuuAcctMap.getOrDefault(entity.getAccountSeq(),
									Collections.emptyList());
							if (ListUtils.isEmpty(shozokuBusho)) {
								shozokuBushoIdSb.append(CommonConstant.SPACE);
								shozokuBushoIdSb.append(Long.valueOf(0)); // 未所属が0
							} else {
								seqToShozokuuAcctMap.getOrDefault(entity.getAccountSeq(), Collections.emptyList()).forEach(e -> {
									shozokuBushoIdSb.append(CommonConstant.SPACE);
									shozokuBushoIdSb.append(e.getBushoId());
								});
							}
							return Account.builder()
									.accountSeq(entity.getAccountSeq())
									.accountName(accountName.getName())
									.accountType(AccountType.of(entity.getAccountType()))
									.accountColor(entity.getAccountColor())
									.shozokuBushoIdStr(shozokuBushoIdSb.toString())
									.isEnabled(
											// 無効 or 削除済かどうか
											!AccountStatus.DISABLED.equalsByCode(entity.getAccountStatus())
													&& !Objects.nonNull(entity.getDeletedAt()))
									.searchAccountName(StringUtils.removeAll(accountName.getName(), "[ 　]"))
									.searchAccountNameKana(StringUtils.removeAll(accountName.getNameKana(), "[ 　]"))
									.build();
						},
						(former, latter) -> former,
						LinkedHashMap::new));

		List<Account> accountList = new ArrayList<>(seqToAccountMap.values());
		form.setAccountList(accountList);

		// 部署所属アカウント
		Map<Long, List<Account>> bushoIdKeyMap = bushoShozokuAcctEntityList.stream()
				.filter(entity -> seqToAccountMap.containsKey(entity.getAccountSeq()))
				.sorted(Comparator.comparing(TBushoShozokuAcctEntity::getDispOrder))
				.collect(Collectors.groupingBy(
						TBushoShozokuAcctEntity::getBushoId,
						Collectors.mapping(
								entity -> seqToAccountMap.get(entity.getAccountSeq()),
								Collectors.toList())));

		// 部署
		List<MBushoEntity> bushoEntityList = mBushoDao.selectAll();
		List<Busho> bushoList = new ArrayList<>();

		// 所属ユーザーのアカウントを抽出
		Set<Long> bushoJoinedAccountSeq = new HashSet<>();
		for (MBushoEntity entity : bushoEntityList) {
			List<Account> account = bushoIdKeyMap.get(entity.getBushoId());
			if (account == null) {
				continue;
			}
			bushoJoinedAccountSeq.addAll(account.stream().map(Account::getAccountSeq).collect(Collectors.toSet()));
		}

		// 有効アカウント情報のみを取得
		Set<Long> enabledAccountSeqSet = mAccountDao.selectEnabledAccount()
				.stream().map(MAccountEntity::getAccountSeq).collect(Collectors.toSet());

		// 部門未所属ユーザーのアカウントを抽出
		List<Account> independenceAccount = new ArrayList<>();
		LoiozCollectionUtils.subtract(enabledAccountSeqSet, bushoJoinedAccountSeq).forEach(key -> {
			independenceAccount.add(seqToAccountMap.get(key));
		});

		// 部署リストに登録済の部署を追加
		bushoEntityList.stream()
				.sorted(Comparator.comparing(MBushoEntity::getDispOrder))
				.map(entity -> Busho.builder()
						.bushoId(entity.getBushoId())
						.bushoName(entity.getBushoName())
						.accountList(bushoIdKeyMap.get(entity.getBushoId()))
						.build())
				.forEach(bushoList::add);

		// 部門リストに「部門未所属」を追加
		if (!ListUtils.isEmpty(independenceAccount)) {
			bushoList.add(Busho.builder()
					.bushoId(Long.valueOf(0))
					.bushoName(CommonConstant.BUMON_MI_SHOZOKU)
					.accountList(independenceAccount)
					.build());
		}

		form.setBushoList(bushoList);

		// グループ所属アカウント
		List<TGroupShozokuAcctEntity> groupShozokuAcctEntityList = tGroupShozokuAcctDao.selectAll();

		// グループID毎の所属アカウント
		Map<Long, List<Account>> groupIdToShozokuAccountMap = groupShozokuAcctEntityList.stream()
				.filter(entity -> seqToAccountMap.containsKey(entity.getAccountSeq()))
				.sorted(Comparator.comparing(TGroupShozokuAcctEntity::getDispOrder))
				.collect(Collectors.groupingBy(
						TGroupShozokuAcctEntity::getGroupId,
						Collectors.mapping(
								entity -> seqToAccountMap.get(entity.getAccountSeq()),
								Collectors.toList())));

		// グループ
		List<MGroupEntity> groupEntityList = mGroupDao.selectAll();
		List<Group> groupList = groupEntityList.stream()
				.sorted(Comparator.comparing(MGroupEntity::getDispOrder))
				.map(entity -> Group.builder()
						.groupId(entity.getGroupId())
						.groupName(entity.getGroupName())
						.accountList(groupIdToShozokuAccountMap.get(entity.getGroupId()))
						.build())
				.collect(Collectors.toList());
		form.setGroupList(groupList);

		return form;
	}

	/**
	 * 登録処理
	 *
	 * @param form フォーム入力値
	 * @return 登録した予定の予定SEQ
	 */
	public Long create(ScheduleInputForm form) {

		TScheduleEntity scheduleEntity = new TScheduleEntity();

		// 予定を登録
		populateInputFormToScheduleEntity(form).accept(scheduleEntity);
		tScheduleDao.insert(scheduleEntity);

		Long scheduleSeq = scheduleEntity.getScheduleSeq();

		// 参加者を登録
		updateScheduleAccount(scheduleSeq, Collections.emptyList(), form.getMember());

		return scheduleSeq;
	}

	/**
	 * 更新処理
	 *
	 * @param form フォーム入力値
	 */
	public void update(ScheduleInputForm form) {
		Long scheduleSeq = form.getScheduleSeq();
		save(scheduleSeq, (scheduleEntity, scheduleAccountEntityList) -> {
			// 予定を更新
			populateInputFormToScheduleEntity(form).accept(scheduleEntity);
			tScheduleDao.update(scheduleEntity);

			// 参加者を更新
			updateScheduleAccount(scheduleSeq, scheduleAccountEntityList, form.getMember());
		});
	}

	/**
	 * 削除処理
	 *
	 * @param scheduleSeq 予定SEQ
	 */
	public void delete(Long scheduleSeq) {
		save(scheduleSeq, (scheduleEntity, scheduleAccountEntityList) -> {

			// 予定を削除
			scheduleEntity.setFlgDelete();
			tScheduleDao.update(scheduleEntity);

			// 紐付いている初回面談情報を削除
			if (scheduleEntity.getCustomerId() != null && scheduleEntity.getAnkenId() != null) {
				// 案件-顧客情報を取得
				TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByShokaiMendanScheduleSeq(scheduleSeq);

				// 取得できない場合は何もしない
				if (tAnkenCustomerEntity == null) {
					return;
				}

				// 紐付けを解除して更新
				tAnkenCustomerEntity.setShokaiMendanDate(null);
				tAnkenCustomerEntity.setShokaiMendanScheduleSeq(null);
				tAnkenCustomerEntity.setAnkenStatus(commonAnkenService.getCurrentAnkenStatus(tAnkenCustomerEntity));
				tAnkenCustomerDao.update(tAnkenCustomerEntity);
			}

		});
	}

	/**
	 * 更新・削除共通処理
	 *
	 * @param scheduleSeq 予定SEQ
	 * @param operation 処理内容
	 */
	private void save(Long scheduleSeq, BiConsumer<TScheduleEntity, List<TScheduleAccountEntity>> operation) {

		// 予定
		TScheduleEntity scheduleEntity = tScheduleDao.selectBySeq(scheduleSeq);

		// 予定が存在しない場合
		if (scheduleEntity == null) {
			throw new DataNotFoundException("予定が存在しません。");
		}

		// 予定-参加者
		List<TScheduleAccountEntity> scheduleAccountEntityList = tScheduleAccountDao.selectByScheduleSeq(scheduleSeq);

		// 参加者のみ編集可能か判定
		if (SchedulePermission.MEMBER.equalsByCode(scheduleEntity.getEditRange())) {
			// ログインユーザが参加者に含まれているか判定
			if (scheduleAccountEntityList.stream().noneMatch(entity -> SessionUtils.getLoginAccountSeq().equals(entity.getAccountSeq()))) {
				throw new RuntimeException("予定の編集権限がありません。");
			}
		}

		// 更新・削除処理
		operation.accept(scheduleEntity, scheduleAccountEntityList);
	}

	/**
	 * 予定のフォーム入力値をEntityに反映させる
	 *
	 * @param form フォーム入力値
	 * @return
	 */
	private Consumer<TScheduleEntity> populateInputFormToScheduleEntity(ScheduleInputForm form) {
		return populateDateTimeToScheduleEntity(form) // 日時
				.andThen(scheduleEntity -> {
					// 件名
					scheduleEntity.setSubject(form.getSubject());

					// 場所
					scheduleEntity.setRoomId(null);
					scheduleEntity.setPlace(null);
					if (form.isRoomSelected()) {
						scheduleEntity.setRoomId(form.getRoomId());
					} else {
						String place = "";
						if (!StringUtils.isEmpty(form.getPlace())) {
							place = form.getPlace();
						}
						scheduleEntity.setPlace(place);
					}

					// メモ
					scheduleEntity.setMemo(form.getMemo());

					// 権限範囲
					scheduleEntity.setOpenRange(DefaultEnum.getCd(form.getOpenRange()));
					scheduleEntity.setEditRange(DefaultEnum.getCd(form.getEditRange()));

					// 連携テーブルの外部キー
					scheduleEntity.setCustomerId(form.getCustomerId());
					scheduleEntity.setAnkenId(form.getAnkenId());
					scheduleEntity.setSaibanSeq(form.getSaibanSeq());
					scheduleEntity.setSaibanLimitSeq(form.getSaibanLimitSeq());
				});
	}

	/**
	 * 予定の日時情報をEntityに反映させる
	 *
	 * @param input 日時情報
	 * @return
	 */
	private Consumer<TScheduleEntity> populateDateTimeToScheduleEntity(ScheduleDateTimeInput input) {
		return scheduleEntity -> {

			// 日付の設定
			if (input.isRepeat()) {
				// 繰り返し指定あり
				if (input.isUseRepeatDate()) {
					// 繰り返し期間指定あり
					// 繰り返し期間を日付として設定する
					scheduleEntity.setDateFrom(input.getRepeatDateFrom());
					scheduleEntity.setDateTo(input.getRepeatDateTo());

				} else {
					// 繰り返し期間指定なし
					// 繰り返し日付は入力されないので、Fromは通常の日付を使用
					// Toは繰り返し期間上限を設定
					scheduleEntity.setDateFrom(input.getDateFrom());
					scheduleEntity.setDateTo(calcScheduleRepeatMaxDate(input.getDateFrom()));
				}

			} else {
				// 繰り返し指定なし
				scheduleEntity.setDateFrom(input.getDateFrom());
				if (input.isAllday()) {
					// 終日の場合は日付の範囲を設定
					scheduleEntity.setDateTo(input.getDateTo());
				} else {
					// 終日でない場合は1日分の範囲を設定(From = To)
					scheduleEntity.setDateTo(input.getDateFrom());
				}
			}

			// 終日でない場合のみ時間を設定
			scheduleEntity.setTimeFrom(null);
			scheduleEntity.setTimeTo(null);
			if (!input.isAllday()) {
				scheduleEntity.setTimeFrom(input.getTimeFrom());
				scheduleEntity.setTimeTo(input.getTimeTo());
			}

			scheduleEntity.setAllDayFlg(SystemFlg.booleanToCode(input.isAllday()));
			scheduleEntity.setRepeatFlg(SystemFlg.booleanToCode(input.isRepeat()));

			// 繰り返し設定
			scheduleEntity.setRepeatType(null);
			scheduleEntity.setRepeatYobi(null);
			scheduleEntity.setRepeatDayOfMonth(null);
			if (input.isRepeat()) {
				scheduleEntity.setRepeatType(DefaultEnum.getCd(input.getRepeatType()));

				if (input.getRepeatType() == ScheduleRepeatType.WEEKLY) {
					// 毎週
					Map<JsDayOfWeek, Boolean> repeatYobi = input.getRepeatYobi();

					String repeatYobiStr = encodeRepeatYobi(repeatYobi);

					scheduleEntity.setRepeatYobi(repeatYobiStr);

				} else if (input.getRepeatType() == ScheduleRepeatType.MONTHLY) {
					// 毎月
					scheduleEntity.setRepeatDayOfMonth(input.getRepeatDayOfMonth());
				}
			}
		};
	}

	/**
	 * 予定-参加者を更新する
	 *
	 * @param scheduleSeq 予定SEQ
	 * @param dbScheduleAccountList DBに登録されている参加者
	 * @param formAccountSeqList フォーム入力値の参加者
	 */
	private void updateScheduleAccount(Long scheduleSeq, List<TScheduleAccountEntity> dbScheduleAccountList, List<Long> formAccountSeqList) {

		Map<Long, TScheduleAccountEntity> dbScheduleAccountMap = dbScheduleAccountList.stream()
				.collect(Collectors.toMap(
						TScheduleAccountEntity::getAccountSeq,
						Function.identity()));
		Set<Long> dbAccountSeqSet = dbScheduleAccountMap.keySet();

		LoiozCollectionUtils.subtract(formAccountSeqList, dbAccountSeqSet).forEach(accountSeq -> {
			// 入力データあり、既存データなし
			// 入力データを登録

			TScheduleAccountEntity scheduleAccountEntity = new TScheduleAccountEntity();
			scheduleAccountEntity.setScheduleSeq(scheduleSeq);
			scheduleAccountEntity.setAccountSeq(accountSeq);
			tScheduleAccountDao.insert(scheduleAccountEntity);
		});

		LoiozCollectionUtils.subtract(dbAccountSeqSet, formAccountSeqList).forEach(accountSeq -> {
			// 入力データなし、既存データあり
			// 既存データを削除
			TScheduleAccountEntity entity = dbScheduleAccountMap.get(accountSeq);
			tScheduleAccountDao.delete(entity);
		});
	}

	/**
	 * 予定SEQで予定を取得する
	 *
	 * @param request 取得条件
	 * @return 予定
	 */
	public Map<Long, ScheduleDetail> getSchedule(ScheduleBySeqRequest request) {

		List<Long> scheduleSeqList = request.getScheduleSeq();

		// 予定
		List<TScheduleEntity> scheduleEntityList = tScheduleDao.selectBySeq(scheduleSeqList);

		// DBからの取得情報を画面用に変換
		List<ScheduleDetail> scheduleList = scheduleEntityToOutputData(scheduleEntityList);
		return scheduleList.stream()
				.collect(Collectors.toMap(
						ScheduleDetail::getScheduleSeq,
						Function.identity()));
	}

	/**
	 * 予定SEQで予定を取得する ※予定SEQが一意となるデータを返却する
	 *
	 * @param request 取得条件
	 * @return 予定
	 */
	public Map<Long, ScheduleDetail> getSchedulePKOne(ScheduleBySeqRequest request) {

		List<Long> scheduleSeqList = request.getScheduleSeq();

		// 予定
		List<TScheduleEntity> scheduleEntityList = tScheduleDao.selectBySeq(scheduleSeqList);

		// DBからの取得情報を画面用に変換
		List<ScheduleDetail> scheduleList = scheduleEntityToOutputData(scheduleEntityList);

		// 重複排除した予定を作成する
		List<ScheduleDetail> disinctScheduleList = new ArrayList<ScheduleDetail>();
		List<Long> disinctScheduleSeqList = new ArrayList<Long>();

		scheduleList.stream().forEach(schedule -> {
			Long scheduleSeq = schedule.getScheduleSeq();
			// すでに存在するかチェック
			if (!disinctScheduleSeqList.contains(scheduleSeq)) {
				disinctScheduleSeqList.add(scheduleSeq);
				disinctScheduleList.add(schedule);
			}
		});
		return disinctScheduleList.stream()
				.collect(Collectors.toMap(
						ScheduleDetail::getScheduleSeq,
						Function.identity()));
	}

	/**
	 * 案件IDに紐づく予定SEQを取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	public List<Long> getScheduleSeqByAnkenId(Long ankenId) {

		List<TScheduleEntity> tScheduleEntities = tScheduleDao.selectByAnkenId(ankenId);

		if (ListUtils.isEmpty(tScheduleEntities)) {
			return Collections.emptyList();
		}

		return tScheduleEntities.stream().map(TScheduleEntity::getScheduleSeq).collect(Collectors.toList());
	}

	/**
	 * 予定の繰り返し設定を展開して、日付毎の予定に変換する
	 *
	 * @param lowerLimitDate 日付範囲の下限
	 * @param upperLimitDate 日付範囲の上限
	 * @param scheduleList 予定
	 * @return 日付毎の予定
	 */
	public <T extends ScheduleRepeatCondition> Map<LocalDate, List<T>> expandScheduleRepeatByDate(LocalDate lowerLimitDate, LocalDate upperLimitDate,
			List<T> scheduleList) {

		// 繰り返し設定を展開して、日付/予定のペアに変換
		List<Pair<LocalDate, T>> dateScheduleList = scheduleList.stream()
				.flatMap(schedule -> {

					// 画面表示する日付範囲
					LocalDate dateFrom = LoiozObjectUtils.max(schedule.getDateFrom(), lowerLimitDate);
					LocalDate dateTo = LoiozObjectUtils.min(schedule.getDateTo(), upperLimitDate);

					// 日付範囲を計算できない場合
					if (CommonUtils.anyNull(dateFrom, dateTo) || dateFrom.isAfter(dateTo)) {
						return Stream.empty();
					}

					// 日付範囲を1日毎にオブジェクト化
					Stream<LocalDate> dateRange = dateFrom.datesUntil(dateTo.plusDays(1));

					// 繰り返し設定のある予定の場合
					if (schedule.isRepeat()) {
						// 繰り返しタイプ
						ScheduleRepeatType repeatType = schedule.getRepeatType();

						// 繰り返し設定がONの曜日
						Map<JsDayOfWeek, Boolean> repeatYobi = schedule.getRepeatYobi();
						Set<DayOfWeek> repeatDayOfWeekSet = repeatYobi.entrySet().stream()
								.filter(Entry::getValue)
								.map(entry -> entry.getKey().getDayOfWeek())
								.collect(Collectors.toSet());

						// 毎月の繰り返し日
						int repeatDayOfMonth = LoiozObjectUtils.defaultIfNull(schedule.getRepeatDayOfMonth(), Long.valueOf(0)).intValue();

						dateRange = dateRange.filter(date -> {
							// 繰り返し条件に合致しない日付を除外

							switch (repeatType) {
							// 毎日
							case DAILY:
								return true;

							// 毎週
							case WEEKLY:
								return repeatDayOfWeekSet.contains(date.getDayOfWeek());

							// 毎月
							case MONTHLY:
								return repeatDayOfMonth == date.getDayOfMonth();

							default:
								return false;
							}
						});
					}

					// 日付に予定を付与
					return dateRange.map(date -> Pair.of(date, schedule));
				})
				.collect(Collectors.toList());

		// 日付毎の予定
		Map<LocalDate, List<T>> scheduleByDate = dateScheduleList.stream()
				.collect(Collectors.groupingBy(
						Pair::getLeft,
						Collectors.mapping(
								Pair::getRight,
								Collectors.toList())));

		return scheduleByDate;
	}

	/**
	 * DBから取得した予定情報を画面用に変換する
	 *
	 * @param scheduleEntityList 予定エンティティ
	 * @return 画面用の予定情報
	 */
	public List<ScheduleDetail> scheduleEntityToOutputData(List<TScheduleEntity> scheduleEntityList) {
		return scheduleEntityToOutputData(scheduleEntityList, null, null);
	}

	/**
	 * DBから取得した予定情報を画面用に変換する
	 *
	 * @param scheduleEntityList 予定エンティティ
	 * @param requestDateFrom
	 * @param requestDateTo
	 * @return
	 */
	public List<ScheduleDetail> scheduleEntityToOutputData(List<TScheduleEntity> scheduleEntityList, LocalDate requestDateFrom,
			LocalDate requestDateTo) {

		// 予定-参加者
		List<Long> scheduleSeqList = scheduleEntityList.stream()
				.map(TScheduleEntity::getScheduleSeq)
				.collect(Collectors.toList());
		List<TScheduleAccountEntity> scheduleAccountEntityList = tScheduleAccountDao.selectByScheduleSeq(scheduleSeqList);

		// 予定毎の参加者Map
		Map<Long, List<Long>> scheduleSeqToAccountSeqMap = scheduleAccountEntityList.stream()
				.collect(Collectors.groupingBy(
						TScheduleAccountEntity::getScheduleSeq,
						Collectors.mapping(
								TScheduleAccountEntity::getAccountSeq,
								Collectors.toList())));

		// アカウントSEQからアカウント名を取得するMap
		Map<Long, String> accountSeqToNameMap = commonAccountService.getAccountNameMap();

		// 会議室
		List<MRoomEntity> roomEntityList = mRoomDao.selectAll();

		// 会議室IDから会議室エンティティを取得するMap
		Map<Long, MRoomEntity> roomIdToEntityMap = roomEntityList.stream()
				.collect(Collectors.toMap(
						MRoomEntity::getRoomId,
						Function.identity()));

		// 顧客
		List<Long> customerIdList = scheduleEntityList.stream()
				.map(TScheduleEntity::getCustomerId)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
		List<TPersonEntity> personEntityList = tPersonDao.selectById(customerIdList);

		// 顧客IDから顧客エンティティを取得するMap
		Map<Long, TPersonEntity> customerIdToEntityMap = personEntityList.stream()
				.collect(Collectors.toMap(
						TPersonEntity::getCustomerId,
						Function.identity()));

		// 案件
		List<Long> ankenIdList = scheduleEntityList.stream()
				.map(TScheduleEntity::getAnkenId)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
		List<TAnkenEntity> ankenEntityList = tAnkenDao.selectById(ankenIdList);

		// 案件IDから案件エンティティを取得するMap
		Map<Long, TAnkenEntity> ankenIdToEntityMap = ankenEntityList.stream()
				.collect(Collectors.toMap(
						TAnkenEntity::getAnkenId,
						Function.identity()));

		// 案件-顧客(初回面談)
		List<TAnkenCustomerEntity> ankenCustomerEntityList = tAnkenCustomerDao.selectByShokaiMendanScheduleSeq(scheduleSeqList);

		// 予定SEQから案件-顧客エンティティを取得するMap
		Map<Long, List<TAnkenCustomerEntity>> scheduleSeqToAnkenCustomerEntityMap = ankenCustomerEntityList.stream()
				.filter(entity -> entity.getShokaiMendanScheduleSeq() != null)
				.collect(Collectors.groupingBy(TAnkenCustomerEntity::getShokaiMendanScheduleSeq));

		// 裁判
		List<Long> saibanSeqList = scheduleEntityList.stream()
				.map(TScheduleEntity::getSaibanSeq)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
		List<TSaibanEntity> saibanEntityList = tSaibanDao.selectBySeq(saibanSeqList);

		// 裁判SEQから裁判エンティティを取得するMap
		Map<Long, TSaibanEntity> saibanSeqToEntityMap = saibanEntityList.stream()
				.collect(Collectors.toMap(
						TSaibanEntity::getSaibanSeq,
						Function.identity()));

		// 裁判-事件
		List<TSaibanJikenEntity> jikenEntityList = tSaibanJikenDao.selectBySaibanSeq(saibanSeqList);

		// 事件が複数存在する裁判(刑事裁判)の裁判SEQ
		List<Long> keijiSaibanSeqList = jikenEntityList.stream()
				.collect(Collectors.groupingBy(TSaibanJikenEntity::getSaibanSeq))
				.entrySet()
				.stream()
				.filter(entry -> entry.getValue().size() > 1)
				.map(Entry::getKey)
				.collect(Collectors.toList());

		// 本起訴事件判定ルール
		final Predicate<TSaibanJikenEntity> isMainJiken;
		if (!LoiozCollectionUtils.isEmpty(keijiSaibanSeqList)) {
			List<TSaibanAddKeijiEntity> saibanAddKeijiList = tSaibanAddKeijiDao.selectBySaibanSeq(keijiSaibanSeqList);
			Set<Long> mainJikenSeqSet = saibanAddKeijiList.stream()
					.map(TSaibanAddKeijiEntity::getMainJikenSeq)
					.collect(Collectors.toSet());
			isMainJiken = (jiken -> mainJikenSeqSet.contains(jiken.getJikenSeq()));
		} else {
			isMainJiken = (jiken -> false);
		}

		// 裁判SEQから事件を取得するMap
		Map<Long, TSaibanJikenEntity> saibanSeqToJikenMap = jikenEntityList.stream()
				.collect(Collectors.toMap(
						TSaibanJikenEntity::getSaibanSeq,
						Function.identity(),
						(former, latter) -> isMainJiken.test(latter) ? latter : former));

		// 裁判期日
		List<Long> saibanLimitSeqList = scheduleEntityList.stream()
				.map(TScheduleEntity::getSaibanLimitSeq)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
		List<TSaibanLimitEntity> saibanLimitEntityList = tSaibanLimitDao.selectBySeq(saibanLimitSeqList);

		// 裁判期日SEQから裁判期日エンティティを取得するMap
		Map<Long, TSaibanLimitEntity> saibanLimitSeqToEntityMap = saibanLimitEntityList.stream()
				.collect(Collectors.toMap(
						TSaibanLimitEntity::getSaibanLimitSeq,
						Function.identity(),
						(former, latter) -> former));

		// 裁判当事者名Map
		Map<Long, Map<String, String>> saibanTojishaNameMap = commonSaibanService.getSaibanTojishaNameMap(saibanSeqList);

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// DBからの取得情報を画面用に変換
		List<ScheduleDetail> addScheduleList = new ArrayList<ScheduleDetail>();
		List<ScheduleDetail> scheduleList = scheduleEntityList.stream()
				.map(entity -> {
					ScheduleDetail schedule = new ScheduleDetail();

					schedule.setScheduleSeq(entity.getScheduleSeq());
					schedule.setSubject(entity.getSubject());

					LocalDate dateFrom = entity.getDateFrom();
					LocalDate dateTo = entity.getDateTo();
					schedule.setDateFrom(dateFrom);
					schedule.setDateTo(dateTo);
					// 終日の繰り返し日の設定
					this.repeatAllDaySetting(schedule, requestDateFrom, requestDateTo, dateFrom, dateTo);

					LocalTime timeFrom = entity.getTimeFrom();
					if (timeFrom != null) {
						schedule.setTimeFrom(timeFrom);
						schedule.setHourFrom(timeFrom.getHour());
						schedule.setMinFrom(timeFrom.getMinute());
					}
					LocalTime timeTo = entity.getTimeTo();
					if (timeTo != null) {
						schedule.setTimeTo(timeTo);
						schedule.setHourTo(timeTo.getHour());
						schedule.setMinTo(timeTo.getMinute());
					}
					schedule.setAllday(SystemFlg.codeToBoolean(entity.getAllDayFlg()));
					schedule.setRepeat(SystemFlg.codeToBoolean(entity.getRepeatFlg()));
					schedule.setRepeatType(ScheduleRepeatType.of(entity.getRepeatType()));
					Map<JsDayOfWeek, Boolean> repeatYobi = decodeRepeatYobi(entity.getRepeatYobi());
					schedule.setRepeatYobi(repeatYobi);
					schedule.setRepeatDayOfMonth(entity.getRepeatDayOfMonth());
					LocalDate repeatDateFrom = entity.getDateFrom();
					LocalDate repeatDateTo = entity.getDateTo();
					schedule.setUseRepeatDate(!Objects.equals(calcScheduleRepeatMaxDate(repeatDateFrom), repeatDateTo));
					schedule.setRepeatDateFrom(repeatDateFrom);
					schedule.setRepeatDateTo(repeatDateTo);
					if (entity.getRoomId() != null) {
						MRoomEntity room = roomIdToEntityMap.getOrDefault(entity.getRoomId(), new MRoomEntity());
						schedule.setRoomSelected(true);
						schedule.setRoomId(entity.getRoomId());
						schedule.setPlaceDisp(room.getRoomName());
					} else {
						schedule.setRoomSelected(false);
						schedule.setPlace(entity.getPlace());
						schedule.setPlaceDisp(entity.getPlace());
					}
					schedule.setMemo(entity.getMemo());
					schedule.setViewMemo(this.convertScheduleMemoURLLink(entity.getMemo()));
					schedule.setOpenRange(SchedulePermission.of(entity.getOpenRange()));
					schedule.setEditRange(SchedulePermission.of(entity.getEditRange()));
					schedule.setMember(scheduleSeqToAccountSeqMap.getOrDefault(entity.getScheduleSeq(), Collections.emptyList()));
					// 不正データ対応
					if (!schedule.getMember().isEmpty()) {
						Long accountSeq = schedule.getMember().get(0);
						schedule.setMemberOne(accountSeq);
						schedule.setScheduleSankasyaPk(this.formatKey(schedule.getScheduleSeq(), accountSeq));
					}
					schedule.setCreateUserName(accountSeqToNameMap.get(entity.getCreatedBy()));
					schedule.setCreatedAt(entity.getCreatedAt());
					schedule.setUpdateUserName(accountSeqToNameMap.get(entity.getUpdatedBy()));
					schedule.setUpdatedAt(entity.getUpdatedAt());

					if (entity.getCustomerId() != null) {
						// 顧客情報
						TPersonEntity person = customerIdToEntityMap.getOrDefault(entity.getCustomerId(), new TPersonEntity());
						schedule.setCustomerId(entity.getCustomerId());
						schedule.setCustomerIdDisp(CustomerId.of(entity.getCustomerId()));
						schedule.setCustomerName(PersonName.fromEntity(person).getName());
					}
					if (entity.getAnkenId() != null) {
						// 案件情報
						TAnkenEntity anken = ankenIdToEntityMap.getOrDefault(entity.getAnkenId(), new TAnkenEntity());
						schedule.setAnkenId(entity.getAnkenId());
						schedule.setAnkenIdDisp(AnkenId.of(entity.getAnkenId()));
						schedule.setAnkenName(StringUtils.isNotEmpty(anken.getAnkenName()) ? anken.getAnkenName() : "(案件名未入力)");
						BunyaDto bunya = bunyaMap.get(anken.getBunyaId());
						schedule.setBunyaName(bunya.getVal());
						schedule.setKeiji(bunya.isKeiji());

						if (entity.getCustomerId() != null) {
							List<TAnkenCustomerEntity> ankenCustomerList = scheduleSeqToAnkenCustomerEntityMap.get(entity.getScheduleSeq());
							if (ankenCustomerList != null) {
								boolean isShokaiMendan = ankenCustomerList.stream()
										.anyMatch(ankenCustomer -> Objects.equals(ankenCustomer.getAnkenId(), entity.getAnkenId())
												&& Objects.equals(ankenCustomer.getCustomerId(), entity.getCustomerId()));
								schedule.setShokaiMendan(isShokaiMendan);
							}
						}

					}
					if (entity.getSaibanSeq() != null) {
						// 裁判情報
						TSaibanEntity saiban = saibanSeqToEntityMap.getOrDefault(entity.getSaibanSeq(), new TSaibanEntity());
						TSaibanJikenEntity jiken = saibanSeqToJikenMap.getOrDefault(entity.getSaibanSeq(), new TSaibanJikenEntity());
						schedule.setSaibanSeq(entity.getSaibanSeq());
						schedule.setSaibanId(SaibanId.fromEntity(saiban));
						schedule.setSaibanBranchNumber(saiban.getSaibanBranchNo());
						schedule.setCaseNumber(CaseNumber.fromEntity(jiken));
						schedule.setJikenName(StringUtils.isNotEmpty(jiken.getJikenName()) ? jiken.getJikenName() : "(事件名未入力)");

						TAnkenEntity anken = ankenIdToEntityMap.getOrDefault(entity.getAnkenId(), new TAnkenEntity());
						BunyaDto bunya = bunyaMap.get(anken.getBunyaId());
						schedule.setKeiji(bunya.isKeiji());

						if (schedule.isKeiji()) {
							schedule.setTojishaNameTitleLabel("被告人");
						} else {
							schedule.setTojishaNameTitleLabel("当事者");
							schedule.setAitegataNameTitleLabel("相手方");
						}

						Map<String, String> tojishaLabelMap = saibanTojishaNameMap.getOrDefault(entity.getSaibanSeq(), new HashMap<String, String>());
						String tojishaNameLabel = tojishaLabelMap.getOrDefault(CommonSaibanService.SAIBAN_TOJISHA_LABEL_MAP_TOJISHA_KEY, "");
						String aitegataNameLabel = tojishaLabelMap.getOrDefault(CommonSaibanService.SAIBAN_TOJISHA_LABEL_MAP_AITEGATA_KEY, "");

						schedule.setTojishaNameLabel(StringUtils.isNotEmpty(tojishaNameLabel) ? tojishaNameLabel : "");
						schedule.setAitegataNameLabel(StringUtils.isNotEmpty(aitegataNameLabel) ? aitegataNameLabel : "");

						if (entity.getSaibanLimitSeq() != null) {
							TSaibanLimitEntity saibanLimit = saibanLimitSeqToEntityMap.getOrDefault(entity.getSaibanLimitSeq(),
									new TSaibanLimitEntity());
							schedule.setSaibanLimitSeq(entity.getSaibanLimitSeq());
							schedule.setSaibanLimit(true);
							schedule.setShutteiType(ShutteiType.of(saibanLimit.getShutteiType()));
						}
					}
					// メンバー分の予定を生成する
					schedule.getMember().forEach(memberAccountSeq -> {
						// 初期設定した予定以外を判定する
						if (!schedule.getMemberOne().equals(memberAccountSeq)) {
							// 他の参加者の予定を生成する
							ScheduleDetail otherSankasyaSchedule = null;
							try {
								// 予定表の情報をコピー
								otherSankasyaSchedule = schedule.clone();
								// 他の参加者の情報を設定する
								otherSankasyaSchedule.setMemberOne(memberAccountSeq);
								// スケジュール + 参加者で一意のKeyを設定する
								otherSankasyaSchedule.setScheduleSankasyaPk(formatKey(
										otherSankasyaSchedule.getScheduleSeq(), memberAccountSeq));
							} catch (CloneNotSupportedException e) {
								// 何もしない
							}
							addScheduleList.add(otherSankasyaSchedule);
						}
					});

					return schedule;
				})
				.collect(Collectors.toList());

		// 追加用のスケジュールがあるかチェックする
		if (addScheduleList.size() > 0) {
			// 通常の予定に追加する
			scheduleList.addAll(addScheduleList);
		}

		// 終日の繰り替えし予定が長い順に並び替え
		List<ScheduleDetail> result = scheduleList.stream()
				.sorted(Comparator.comparing(ScheduleDetail::getWeekViewCnt).reversed().thenComparing(ScheduleDetail::getWeekViewDate))
				.collect(Collectors.toList());

		return result;
	}

	/**
	 * 祝日情報をカレンダー情報に変換する
	 *
	 * @param mHolidayEntities
	 * @param requestDateFrom
	 * @param requestDateTo
	 * @return
	 */
	public List<ScheduleDetail> mHolidayEntityToOutputData(List<MHolidayEntity> mHolidayEntities, LocalDate requestDateFrom,
			LocalDate requestDateTo) {
		return mHolidayEntities.stream()
				.map(entity -> {
					ScheduleDetail schedule = new ScheduleDetail();
					schedule.setHoliday(true);
					schedule.setScheduleSankasyaPk(CalenderOutputType.HOLIDAY.getInitial() + CommonConstant.HYPHEN + entity.getHolidaySeq());// 0-H-祝日SEQ
					schedule.setScheduleSeq(null);
					schedule.setSubject(entity.getHolidayName());
					LocalDate dateFrom = entity.getHolidayDate();
					LocalDate dateTo = entity.getHolidayDate();
					// 終日の繰り返し日の設定
					this.repeatAllDaySetting(schedule, requestDateFrom, requestDateTo, dateFrom, dateTo);
					schedule.setDateFrom(dateFrom);
					schedule.setDateTo(dateTo);
					schedule.setAllday(true);
					return schedule;
				})
				.collect(Collectors.toList());
	}

	/**
	 * DBから取得した予定情報を画面用に変換する(アカウント毎に分割しない)
	 *
	 * @param scheduleEntityList 予定エンティティ
	 * @param requestDateFrom
	 * @param requestDateTo
	 * @return 画面用の予定情報
	 */
	public List<ScheduleDetail> scheduleEntityToNonSplitMemberData(List<TScheduleEntity> scheduleEntityList, LocalDate requestDateFrom,
			LocalDate requestDateTo) {

		// 予定-参加者
		List<Long> scheduleSeqList = scheduleEntityList.stream()
				.map(TScheduleEntity::getScheduleSeq)
				.collect(Collectors.toList());
		List<TScheduleAccountEntity> scheduleAccountEntityList = tScheduleAccountDao.selectByScheduleSeq(scheduleSeqList);

		// 予定毎の参加者Map
		Map<Long, List<Long>> scheduleSeqToAccountSeqMap = scheduleAccountEntityList.stream()
				.collect(Collectors.groupingBy(
						TScheduleAccountEntity::getScheduleSeq,
						Collectors.mapping(
								TScheduleAccountEntity::getAccountSeq,
								Collectors.toList())));

		// アカウントSEQからアカウント名を取得するMap
		Map<Long, String> accountSeqToNameMap = commonAccountService.getAccountNameMap();

		// 会議室
		List<MRoomEntity> roomEntityList = mRoomDao.selectAll();

		// 会議室IDから会議室エンティティを取得するMap
		Map<Long, MRoomEntity> roomIdToEntityMap = roomEntityList.stream()
				.collect(Collectors.toMap(
						MRoomEntity::getRoomId,
						Function.identity()));

		// 顧客
		List<Long> customerIdList = scheduleEntityList.stream()
				.map(TScheduleEntity::getCustomerId)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
		List<TPersonEntity> personEntityList = tPersonDao.selectById(customerIdList);

		// 顧客IDから名簿エンティティを取得するMap
		Map<Long, TPersonEntity> customerIdToEntityMap = personEntityList.stream()
				.collect(Collectors.toMap(
						TPersonEntity::getCustomerId,
						Function.identity()));

		// 案件
		List<Long> ankenIdList = scheduleEntityList.stream()
				.map(TScheduleEntity::getAnkenId)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
		List<TAnkenEntity> ankenEntityList = tAnkenDao.selectById(ankenIdList);

		// 案件IDから案件エンティティを取得するMap
		Map<Long, TAnkenEntity> ankenIdToEntityMap = ankenEntityList.stream()
				.collect(Collectors.toMap(
						TAnkenEntity::getAnkenId,
						Function.identity()));

		// 案件-顧客(初回面談)
		List<TAnkenCustomerEntity> ankenCustomerEntityList = tAnkenCustomerDao.selectByShokaiMendanScheduleSeq(scheduleSeqList);

		// 予定SEQから案件-顧客エンティティを取得するMap
		Map<Long, List<TAnkenCustomerEntity>> scheduleSeqToAnkenCustomerEntityMap = ankenCustomerEntityList.stream()
				.filter(entity -> entity.getShokaiMendanScheduleSeq() != null)
				.collect(Collectors.groupingBy(TAnkenCustomerEntity::getShokaiMendanScheduleSeq));

		// 裁判
		List<Long> saibanSeqList = scheduleEntityList.stream()
				.map(TScheduleEntity::getSaibanSeq)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
		List<TSaibanEntity> saibanEntityList = tSaibanDao.selectBySeq(saibanSeqList);

		// 裁判SEQから裁判エンティティを取得するMap
		Map<Long, TSaibanEntity> saibanSeqToEntityMap = saibanEntityList.stream()
				.collect(Collectors.toMap(
						TSaibanEntity::getSaibanSeq,
						Function.identity()));

		// 裁判-事件
		List<TSaibanJikenEntity> jikenEntityList = tSaibanJikenDao.selectBySaibanSeq(saibanSeqList);

		// 事件が複数存在する裁判(刑事裁判)の裁判SEQ
		List<Long> keijiSaibanSeqList = jikenEntityList.stream()
				.collect(Collectors.groupingBy(TSaibanJikenEntity::getSaibanSeq))
				.entrySet()
				.stream()
				.filter(entry -> entry.getValue().size() > 1)
				.map(Entry::getKey)
				.collect(Collectors.toList());

		// 本起訴事件判定ルール
		final Predicate<TSaibanJikenEntity> isMainJiken;
		if (!LoiozCollectionUtils.isEmpty(keijiSaibanSeqList)) {
			List<TSaibanAddKeijiEntity> saibanAddKeijiList = tSaibanAddKeijiDao.selectBySaibanSeq(keijiSaibanSeqList);
			Set<Long> mainJikenSeqSet = saibanAddKeijiList.stream()
					.map(TSaibanAddKeijiEntity::getMainJikenSeq)
					.collect(Collectors.toSet());
			isMainJiken = (jiken -> mainJikenSeqSet.contains(jiken.getJikenSeq()));
		} else {
			isMainJiken = (jiken -> false);
		}

		// 裁判SEQから事件を取得するMap
		Map<Long, TSaibanJikenEntity> saibanSeqToJikenMap = jikenEntityList.stream()
				.collect(Collectors.toMap(
						TSaibanJikenEntity::getSaibanSeq,
						Function.identity(),
						(former, latter) -> isMainJiken.test(latter) ? latter : former));

		// 裁判期日
		List<Long> saibanLimitSeqList = scheduleEntityList.stream()
				.map(TScheduleEntity::getSaibanLimitSeq)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
		List<TSaibanLimitEntity> saibanLimitEntityList = tSaibanLimitDao.selectBySeq(saibanLimitSeqList);

		// 裁判期日SEQから裁判期日エンティティを取得するMap
		Map<Long, TSaibanLimitEntity> saibanLimitSeqToEntityMap = saibanLimitEntityList.stream()
				.collect(Collectors.toMap(
						TSaibanLimitEntity::getSaibanLimitSeq,
						Function.identity(),
						(former, latter) -> former));

		// 裁判当事者名Map
		Map<Long, Map<String, String>> saibanTojishaNameMap = commonSaibanService.getSaibanTojishaNameMap(saibanSeqList);
		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		List<ScheduleDetail> scheduleList = scheduleEntityList.stream()
				.map(entity -> {
					ScheduleDetail schedule = new ScheduleDetail();

					schedule.setScheduleSeq(entity.getScheduleSeq());
					schedule.setSubject(entity.getSubject());

					LocalDate dateFrom = entity.getDateFrom();
					LocalDate dateTo = entity.getDateTo();
					schedule.setDateFrom(dateFrom);
					schedule.setDateTo(dateTo);
					// 終日の繰り返し日の設定
					this.repeatAllDaySetting(schedule, requestDateFrom, requestDateTo, dateFrom, dateTo);

					LocalTime timeFrom = entity.getTimeFrom();
					if (timeFrom != null) {
						schedule.setTimeFrom(timeFrom);
						schedule.setHourFrom(timeFrom.getHour());
						schedule.setMinFrom(timeFrom.getMinute());
					}
					LocalTime timeTo = entity.getTimeTo();
					if (timeTo != null) {
						schedule.setTimeTo(timeTo);
						schedule.setHourTo(timeTo.getHour());
						schedule.setMinTo(timeTo.getMinute());
					}
					schedule.setAllday(SystemFlg.codeToBoolean(entity.getAllDayFlg()));
					schedule.setRepeat(SystemFlg.codeToBoolean(entity.getRepeatFlg()));
					schedule.setRepeatType(ScheduleRepeatType.of(entity.getRepeatType()));
					Map<JsDayOfWeek, Boolean> repeatYobi = decodeRepeatYobi(entity.getRepeatYobi());
					schedule.setRepeatYobi(repeatYobi);
					schedule.setRepeatDayOfMonth(entity.getRepeatDayOfMonth());
					LocalDate repeatDateFrom = entity.getDateFrom();
					LocalDate repeatDateTo = entity.getDateTo();
					schedule.setUseRepeatDate(!Objects.equals(calcScheduleRepeatMaxDate(repeatDateFrom), repeatDateTo));
					schedule.setRepeatDateFrom(repeatDateFrom);
					schedule.setRepeatDateTo(repeatDateTo);
					if (entity.getRoomId() != null) {
						MRoomEntity room = roomIdToEntityMap.getOrDefault(entity.getRoomId(), new MRoomEntity());
						schedule.setRoomSelected(true);
						schedule.setRoomId(entity.getRoomId());
						schedule.setPlaceDisp(room.getRoomName());
					} else {
						schedule.setRoomSelected(false);
						schedule.setPlace(entity.getPlace());
						schedule.setPlaceDisp(entity.getPlace());
					}
					schedule.setMemo(entity.getMemo());
					schedule.setViewMemo(this.convertScheduleMemoURLLink(entity.getMemo()));
					schedule.setOpenRange(SchedulePermission.of(entity.getOpenRange()));
					schedule.setEditRange(SchedulePermission.of(entity.getEditRange()));
					schedule.setMember(scheduleSeqToAccountSeqMap.getOrDefault(entity.getScheduleSeq(), Collections.emptyList()));
					// 不正データ対応
					if (!schedule.getMember().isEmpty()) {
						Long accountSeq = schedule.getMember().get(0);
						schedule.setMemberOne(accountSeq);
						schedule.setScheduleSankasyaPk(this.formatKey(schedule.getScheduleSeq(), accountSeq));
					}
					schedule.setCreateUserName(accountSeqToNameMap.get(entity.getCreatedBy()));
					schedule.setCreatedAt(entity.getCreatedAt());
					schedule.setUpdateUserName(accountSeqToNameMap.get(entity.getUpdatedBy()));
					schedule.setUpdatedAt(entity.getUpdatedAt());

					if (entity.getCustomerId() != null) {
						// 名簿情報
						TPersonEntity personEntity = customerIdToEntityMap.getOrDefault(entity.getCustomerId(), new TPersonEntity());
						schedule.setCustomerId(entity.getCustomerId());
						schedule.setCustomerIdDisp(CustomerId.of(entity.getCustomerId()));
						schedule.setCustomerName(PersonName.fromEntity(personEntity).getName());
					}
					if (entity.getAnkenId() != null) {
						// 案件情報
						TAnkenEntity anken = ankenIdToEntityMap.getOrDefault(entity.getAnkenId(), new TAnkenEntity());
						schedule.setAnkenId(entity.getAnkenId());
						schedule.setAnkenIdDisp(AnkenId.of(entity.getAnkenId()));
						schedule.setAnkenName(anken.getAnkenName());
						schedule.setBunyaName(bunyaMap.get(anken.getBunyaId()).getVal());

						if (entity.getCustomerId() != null) {
							List<TAnkenCustomerEntity> ankenCustomerList = scheduleSeqToAnkenCustomerEntityMap.get(entity.getScheduleSeq());
							if (ankenCustomerList != null) {
								boolean isShokaiMendan = ankenCustomerList.stream()
										.anyMatch(ankenCustomer -> Objects.equals(ankenCustomer.getAnkenId(), entity.getAnkenId())
												&& Objects.equals(ankenCustomer.getCustomerId(), entity.getCustomerId()));
								schedule.setShokaiMendan(isShokaiMendan);
							}
						}

					}
					if (entity.getSaibanSeq() != null) {
						// 裁判情報
						TSaibanEntity saiban = saibanSeqToEntityMap.getOrDefault(entity.getSaibanSeq(), new TSaibanEntity());
						TSaibanJikenEntity jiken = saibanSeqToJikenMap.getOrDefault(entity.getSaibanSeq(), new TSaibanJikenEntity());
						schedule.setSaibanSeq(entity.getSaibanSeq());
						schedule.setSaibanId(SaibanId.fromEntity(saiban));
						schedule.setSaibanBranchNumber(saiban.getSaibanBranchNo());
						schedule.setCaseNumber(CaseNumber.fromEntity(jiken));
						schedule.setJikenName(jiken.getJikenName());

						Map<String, String> tojishaLabelMap = saibanTojishaNameMap.getOrDefault(entity.getSaibanSeq(), new HashMap<String, String>());
						String tojishaNameLabel = tojishaLabelMap.getOrDefault(CommonSaibanService.SAIBAN_TOJISHA_LABEL_MAP_TOJISHA_KEY, "");
						String aitegataNameLabel = tojishaLabelMap.getOrDefault(CommonSaibanService.SAIBAN_TOJISHA_LABEL_MAP_AITEGATA_KEY, "");

						schedule.setTojishaNameLabel(StringUtils.isNotEmpty(tojishaNameLabel) ? tojishaNameLabel : "");
						schedule.setAitegataNameLabel(StringUtils.isNotEmpty(aitegataNameLabel) ? aitegataNameLabel : "");

						if (entity.getSaibanLimitSeq() != null) {
							TSaibanLimitEntity saibanLimit = saibanLimitSeqToEntityMap.getOrDefault(entity.getSaibanLimitSeq(),
									new TSaibanLimitEntity());
							schedule.setSaibanLimitSeq(entity.getSaibanLimitSeq());
							schedule.setSaibanLimit(true);
							schedule.setShutteiType(ShutteiType.of(saibanLimit.getShutteiType()));
						}
					}

					return schedule;
				})
				.collect(Collectors.toList());

		// ソートして返却
		return scheduleList.stream()
				.sorted(Comparator.comparing(ScheduleDetail::getWeekViewCnt).reversed().thenComparing(ScheduleDetail::getWeekViewDate))
				.collect(Collectors.toList());

	}

	/**
	 * 終日予定の繰り返し表示する日を設定
	 *
	 * @param schedule
	 * @param requestDateFrom
	 * @param requestDateTo
	 * @param dateFrom
	 * @param dateTo
	 */
	private void repeatAllDaySetting(ScheduleDetail schedule, LocalDate requestDateFrom, LocalDate requestDateTo,
			LocalDate dateFrom, LocalDate dateTo) {

		int weekViewCnt = 0;
		if (dateFrom.isEqual(dateTo)) {
			weekViewCnt = 1;
			schedule.setWeekViewDate(dateFrom);
		} else {
			boolean first = false;
			boolean end = false;
			long kikan = 1;
			if (requestDateFrom == null || requestDateTo == null) {
				requestDateFrom = dateFrom;

			} else {
				kikan = ChronoUnit.DAYS.between(requestDateFrom, requestDateTo) + 1;
			}
			// 1週間
			for (long i = 0; i < kikan; i++) {
				LocalDate target = requestDateFrom.plusDays(i);
				// 表示期間内にデータが存在するか判定
				if (target.isAfter(dateTo)) {
					continue;
				}
				if (target.isEqual(dateFrom) || target.isAfter(dateFrom) || target.isEqual(dateTo)) {
					weekViewCnt++;
					first = true;
				}
				if (first && !end) {
					schedule.setWeekViewDate(target);
					end = true;
				}
			}
		}
		// 期間表示する日数
		schedule.setWeekViewCnt(weekViewCnt);

	}

	/**
	 * 予定と参加者で一意になるKeyを発行
	 *
	 * @param scheduleSeq
	 * @param accountSeq
	 * @return
	 */
	public String formatKey(Long scheduleSeq, Long accountSeq) {
		return scheduleSeq.toString() + "-" + accountSeq.toString();
	}

	/**
	 * Mapの曜日繰り返し設定を文字列にエンコードする
	 *
	 * @param decodedRepeatYobi 繰り返し設定(曜日毎のON/OFF)
	 * @return 繰り返し設定文字列
	 */
	public static String encodeRepeatYobi(Map<JsDayOfWeek, Boolean> decodedRepeatYobi) {
		Map<JsDayOfWeek, Boolean> repeatYobiMap = LoiozObjectUtils.defaultIfNull(decodedRepeatYobi, Collections.emptyMap());

		return Arrays.stream(JsDayOfWeek.values())
				.map(dayOfWeek -> SystemFlg.booleanToCode(repeatYobiMap.getOrDefault(dayOfWeek, false)))
				.collect(Collectors.joining());
	}

	/**
	 * 文字列の曜日繰り返し設定をMapにデコードする
	 *
	 * @param encodedRepeatYobi 繰り返し設定文字列
	 * @return 繰り返し設定(曜日毎のON/OFF)
	 */
	public static Map<JsDayOfWeek, Boolean> decodeRepeatYobi(String encodedRepeatYobi) {
		String repeatYobiStr = StringUtils.defaultIfEmpty(encodedRepeatYobi, StringUtils.leftPad("", 7, SystemFlg.FLG_OFF.getCd()));

		Map<JsDayOfWeek, Boolean> repeatYobiMap = new HashMap<>();
		LoiozCollectionUtils.zipping(
				Arrays.asList(repeatYobiStr.split("")),
				Arrays.asList(JsDayOfWeek.values()),
				(repeatYobiStrFlg, jsDayOfWeek) -> {
					repeatYobiMap.put(jsDayOfWeek, SystemFlg.codeToBoolean(repeatYobiStrFlg));
				});
		return repeatYobiMap;
	}

	/**
	 * 会議室の予約状況を取得する
	 *
	 * @param request 予約条件
	 * @return 予約状況
	 */
	public RoomAvailabilityResponse getRoomAvailability(RoomAvailabilityRequest request) {

		// 画面入力値をDBの構造に合わせて変換(Entityを中間オブジェクトとして使用)
		TScheduleEntity tempEntity = new TScheduleEntity();
		populateDateTimeToScheduleEntity(request).accept(tempEntity);

		// 検索条件
		ScheduleSearchCondition condition = ScheduleSearchCondition.builder()
				.scheduleSeq(request.getScheduleSeq())
				.dateFrom(tempEntity.getDateFrom())
				.dateTo(tempEntity.getDateTo())
				.timeFrom(tempEntity.getTimeFrom())
				.timeTo(tempEntity.getTimeTo())
				.repeatYobi(tempEntity.getRepeatYobi())
				.repeatDayOfMonth(tempEntity.getRepeatDayOfMonth())
				.build();

		// 会議室の予約状況を取得
		List<TScheduleEntity> scheduleEntityList = scheduleDao.selectRoomAvailability(condition);

		// 会議室予約条件を予定繰り返し条件アダプターでラップ
		ScheduleRepeatCondition requestScheduleAdapter = new ScheduleRepeatConditionDateTimeAdapter<>(request);

		// リクエストの予約条件を日付毎に展開
		Map<LocalDate, List<ScheduleRepeatCondition>> requestScheduleByDate = expandScheduleRepeatByDate(
				condition.getDateFrom(), condition.getDateTo(), List.of(requestScheduleAdapter));

		// 予定Entityを予定繰り返し条件アダプターでラップ
		List<ScheduleRepeatConditionEntityAdapter> scheduleAdapterList = scheduleEntityList.stream()
				.map(ScheduleRepeatConditionEntityAdapter::new)
				.collect(Collectors.toList());

		// DBの予約条件を日付毎に展開
		Map<LocalDate, List<ScheduleRepeatConditionEntityAdapter>> dbScheduleByDate = expandScheduleRepeatByDate(
				condition.getDateFrom(), condition.getDateTo(), scheduleAdapterList);

		// リクエストとDBの予約条件を比較して、予約できない会議室を判定
		Set<Long> unavailableRoomIdSet = requestScheduleByDate.keySet().stream()
				.filter(dbScheduleByDate::containsKey)
				// リクエストの日付に該当するDB情報を取得
				.flatMap(requestDate -> dbScheduleByDate.get(requestDate).stream())
				// DB情報の会議室IDを予約不可会議室とする
				.map(entityAdapter -> entityAdapter.getEntity().getRoomId())
				.distinct()
				.collect(Collectors.toSet());

		RoomAvailabilityResponse response = new RoomAvailabilityResponse();
		response.setUnavailableRoomId(unavailableRoomIdSet);

		return response;
	}

	/**
	 * 入力日時に予定が登録されているアカウントを取得
	 *
	 * @param form
	 * @return
	 */
	public List<String> getConflictAccount(ScheduleInputForm form) {

		List<String> conflictAccountName = new ArrayList<>();

		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();
		List<Long> yoteiRelateAccount = getUserAvailability(form);
		form.getMember().stream()
				.filter(yoteiRelateAccount::contains) // 重複したユーザーでフィルタ
				.map(accountNameMap::get) // 名前に変換
				.forEach(conflictAccountName::add); // Listに追加

		return conflictAccountName;
	}

	/**
	 * 入力日時に予定が登録されているアカウントを取得(検索処理)
	 *
	 * @param form
	 * @return
	 */
	public List<Long> getUserAvailability(ScheduleInputForm form) {

		TScheduleEntity tempEntity = new TScheduleEntity();
		populateDateTimeToScheduleEntity(form).accept(tempEntity);
		tempEntity.setScheduleSeq(form.getScheduleSeq());

		// 検索条件
		ScheduleSearchCondition condition = ScheduleSearchCondition.builder()
				.scheduleSeq(tempEntity.getScheduleSeq())
				.dateFrom(tempEntity.getDateFrom())
				.dateTo(tempEntity.getDateTo())
				.timeFrom(tempEntity.getTimeFrom())
				.timeTo(tempEntity.getTimeTo())
				.repeatYobi(tempEntity.getRepeatYobi())
				.repeatDayOfMonth(tempEntity.getRepeatDayOfMonth())
				.build();

		return scheduleDao.selectAccountScheduleAvailability(condition);
	}

	/**
	 * 参加者プルダウン「案件担当」に該当するユーザー情報を取得する
	 *
	 * @param ankenId
	 * @return
	 */
	public List<Long> getAnkenTantoUserSeq(Long ankenId) {

		List<TAnkenTantoEntity> tAnkenTantoEntities = tAnkenTantoDao.selectByAnkenId(ankenId);
		var mainTantoUserSeq = tAnkenTantoEntities.stream()
				.filter(e -> CommonConstant.TantoType.LAWYER.equalsByCode(e.getTantoType())
						|| CommonConstant.TantoType.JIMU.equalsByCode(e.getTantoType()))
				.map(TAnkenTantoEntity::getAccountSeq)
				.collect(Collectors.toSet());

		return new ArrayList<>(mainTantoUserSeq);
	}

	/**
	 * 参加者プルダウン「裁判担当」に該当するユーザー情報を取得する
	 *
	 * @param ankenId
	 * @return
	 */
	public List<Long> getSaibanTantoUserSeq(Long saibanSeq) {

		List<TSaibanTantoEntity> tAnkenTantoEntities = tSaibanTantoDao.selectBySaibanSeq(saibanSeq);
		var mainTantoUserSeq = tAnkenTantoEntities.stream()
				.filter(e -> CommonConstant.TantoType.LAWYER.equalsByCode(e.getTantoType())
						|| CommonConstant.TantoType.JIMU.equalsByCode(e.getTantoType()))
				.map(TSaibanTantoEntity::getAccountSeq)
				.collect(Collectors.toSet());

		return new ArrayList<>(mainTantoUserSeq);
	}

	/**
	 * 参加者選択に主担当アイコンが必要なユーザー(案件主担当)を取得する
	 *
	 * @param ankenId
	 * @return
	 */
	public List<Long> getAnkenMainTantoUserSeq(Long ankenId) {

		var mainTantoUserSeq = new HashSet<Long>();

		List<TAnkenTantoEntity> tAnkenTantoEntities = tAnkenTantoDao.selectByAnkenId(ankenId);
		List<TAnkenTantoEntity> tantoLawyer = tAnkenTantoEntities.stream().filter(e -> CommonConstant.TantoType.LAWYER.equalsByCode(e.getTantoType()))
				.collect(Collectors.toList());
		List<TAnkenTantoEntity> tantoJimu = tAnkenTantoEntities.stream().filter(e -> CommonConstant.TantoType.JIMU.equalsByCode(e.getTantoType()))
				.collect(Collectors.toList());

		// 各担当者が二人以上の場合のみ、主担当の設定を行う
		if (tantoLawyer != null && tantoLawyer.size() > 1) {
			tantoLawyer.stream().filter(e -> SystemFlg.codeToBoolean(e.getAnkenMainTantoFlg())).map(TAnkenTantoEntity::getAccountSeq)
					.forEach(mainTantoUserSeq::add);
		}
		if (tantoJimu != null && tantoJimu.size() > 1) {
			tantoJimu.stream().filter(e -> SystemFlg.codeToBoolean(e.getAnkenMainTantoFlg())).map(TAnkenTantoEntity::getAccountSeq)
					.forEach(mainTantoUserSeq::add);
		}

		return new ArrayList<>(mainTantoUserSeq);
	}

	/**
	 * 参加者選択に主担当アイコンが必要なユーザー(裁判主担当)を取得する
	 *
	 * @param saibanSeq
	 * @return
	 */
	public List<Long> getSaibanMainTantoUserSeq(Long saibanSeq) {

		var mainTantoUserSeq = new HashSet<Long>();

		List<TSaibanTantoEntity> tAnkenTantoEntities = tSaibanTantoDao.selectBySaibanSeq(saibanSeq);
		List<TSaibanTantoEntity> tantoLawyer = tAnkenTantoEntities.stream()
				.filter(e -> CommonConstant.TantoType.LAWYER.equalsByCode(e.getTantoType())).collect(Collectors.toList());
		List<TSaibanTantoEntity> tantoJimu = tAnkenTantoEntities.stream().filter(e -> CommonConstant.TantoType.JIMU.equalsByCode(e.getTantoType()))
				.collect(Collectors.toList());

		// 各担当者が二人以上の場合のみ、主担当の設定を行う
		if (tantoLawyer != null && tantoLawyer.size() > 1) {
			tantoLawyer.stream().filter(e -> SystemFlg.codeToBoolean(e.getSaibanMainTantoFlg())).map(TSaibanTantoEntity::getAccountSeq)
					.forEach(mainTantoUserSeq::add);
		}
		if (tantoJimu != null && tantoJimu.size() > 1) {
			tantoJimu.stream().filter(e -> SystemFlg.codeToBoolean(e.getSaibanMainTantoFlg())).map(TSaibanTantoEntity::getAccountSeq)
					.forEach(mainTantoUserSeq::add);
		}

		return new ArrayList<>(mainTantoUserSeq);
	}

	/**
	 * 予定繰り返しの上限日付を取得する
	 *
	 * @param dateFrom 繰り返し開始日
	 * @return 繰り返し終了日
	 */
	public static LocalDate calcScheduleRepeatMaxDate(LocalDate dateFrom) {
		if (dateFrom == null) {
			return null;
		}
		return dateFrom.plus(SCHEDULE_REPEAT_LIMIT);
	}

	// ===================================================
	// バリデーション
	// ===================================================

	/**
	 * 画面独自のバリデーションチェック（登録）
	 *
	 * @param form
	 * @param response
	 * @return
	 */
	public void scheduleFormValidatedForRegist(ScheduleInputForm form, BindingResult result) {

		// 共通のチェック
		this.scheduleFormValidate(form, result);
	}

	/**
	 * 画面独自のバリデーションチェック（更新）
	 *
	 * @param form
	 * @param response
	 * @return
	 */
	public void scheduleFormValidatedForUpdate(ScheduleInputForm form, BindingResult result) {
		// 共通のチェック
		this.scheduleFormValidate(form, result);
	}

	/**
	 * 画面独自のバリデーションチェック（共通）
	 *
	 * @param form
	 * @param response
	 * @return
	 */
	public void scheduleFormValidate(ScheduleInputForm form, BindingResult result) {

		// *******************************************
		// ■日時項目のチェック
		// *******************************************
		// 日付(dateFromの必須)
		boolean isDateRequired = false;
		if (form.getDateFrom() == null) {
			result.rejectValue("dateFrom", null, messageService.getMessage(MessageEnum.VARIDATE_MSG_E00002, SessionUtils.getLocale()));
			isDateRequired = true;
		}

		// 日付がnullじゃない場合
		if (!isDateRequired) {
			LocalDateTime dateTimeFrom = LocalDateTime.of(form.getDateFrom(), form.getTimeFrom());
			LocalDateTime dateTimeTo = LocalDateTime.of(form.getDateFrom(), form.getTimeTo());
			// 日付の整合性チェック
			if (!DateUtils.isCorrectDateTime(dateTimeFrom, dateTimeTo) || dateTimeFrom.isEqual(dateTimeTo)) {
				result.rejectValue("dateFrom", null, messageService.getMessage(MessageEnum.MSG_E00027, SessionUtils.getLocale()));
			}
		}

		// *******************************************
		// ■場所項目のチェック
		// *******************************************

		// 必須（施設）
		if (form.isRoomSelected()) {
			Long roomId = form.getRoomId();
			if (roomId == null) {
				result.rejectValue("roomId", null, messageService.getMessage(MessageEnum.MSG_E00024, SessionUtils.getLocale(), "施設"));
			}
		}

		// *******************************************
		// ■参加者項目のチェック
		// *******************************************
		// 必須（参加者）
		List<Long> selectMember = form.getMember();
		if (selectMember.isEmpty()) {
			result.rejectValue("member", null, messageService.getMessage(MessageEnum.MSG_E00024, SessionUtils.getLocale(), "参加者"));
		}

		// *******************************************
		// ■初回面談項目のチェック
		// *******************************************

		// 顧客IDの必須チェック
		if (form.getCustomerId() == null) {
			result.rejectValue("customerId", null, messageService.getMessage(MessageEnum.MSG_E00001, SessionUtils.getLocale()));
		}

		// 案件IDの必須チェック
		if (form.getAnkenId() == null) {
			result.rejectValue("ankenId", null, messageService.getMessage(MessageEnum.MSG_E00001, SessionUtils.getLocale()));
		}
	}

	/**
	 * 登録時のDB整合性チェック
	 *
	 * @param form
	 * @param response
	 * @return エラーの有無
	 */
	public boolean acessDBValdateRegistForSchedule(ScheduleInputForm form, Map<String, Object> response) {

		// DB整合性チェック
		Map<String, MessageEnum> errorMsgMap = new HashMap<>();
		boolean error = false;
		final String key = "messageKey";

		// エラーの判定と格納
		accessDBValidateForScheduleCommon(form, key, errorMsgMap);
		accessDBValidateForScheduleRegist(form, key, errorMsgMap);

		if (!errorMsgMap.isEmpty()) {
			response.put("succeeded", false);
			response.put("message", messageService.getMessage(errorMsgMap.get(key), SessionUtils.getLocale()));
			error = true;
		}
		return error;
	}

	/**
	 * 更新時のDB整合性チェック
	 *
	 * @param form
	 * @param response
	 * @return エラーの有無
	 */
	public boolean acessDBValdateUpdateForSchedule(ScheduleInputForm form, Map<String, Object> response) {

		// DB整合性チェック
		Map<String, MessageEnum> errorMsgMap = new HashMap<>();
		boolean error = false;
		final String key = "messageKey";

		// エラーの判定と格納
		accessDBValidateForScheduleCommon(form, key, errorMsgMap);
		accessDBValidateForScheduleUpdate(form, key, errorMsgMap);

		if (!errorMsgMap.isEmpty()) {
			response.put("succeeded", false);
			response.put("message", messageService.getMessage(errorMsgMap.get(key), SessionUtils.getLocale()));
			error = true;
		}
		return error;
	}

	/**
	 * DB整合性チェック(共通)
	 *
	 * @param form
	 * @param key エラーを格納するMapのキー
	 * @param errorMsgMap エラー発生時にエラーメッセージを格納する
	 * @return 検証結果
	 */
	public void accessDBValidateForScheduleCommon(ScheduleInputForm form, String key, Map<String, MessageEnum> errorMsgMap) {

		// すでにエラーが有る場合は、検証は行わない
		if (!errorMsgMap.isEmpty()) {
			return;
		}

		// ****************************************
		// 施設IDが有効かどうか判定
		// ****************************************
		if (form.isRoomSelected()) {
			// 施設IDの整合性チェック
			if (!commonScheduleValidator.isRoomExistsValid(form.getScheduleSeq(), form.getRoomId())) {
				errorMsgMap.put(key, MessageEnum.MSG_E00025);
				return;
			}
		}

		// ****************************************
		// アカウントIDが有効かどうか判定
		// ****************************************
		// アカウントの整合性チェック
		if (!commonScheduleValidator.isAccountExistsValid(form.getScheduleSeq(), form.getMember())) {
			errorMsgMap.put(key, MessageEnum.MSG_E00025);
			return;
		}

	}

	/**
	 * DB整合性チェック(更新)
	 *
	 * @param form
	 * @param key
	 * @param errorMsgMap
	 */
	private void accessDBValidateForScheduleRegist(ScheduleInputForm form, String key, Map<String, MessageEnum> errorMsgMap) {

		// すでにエラーが有る場合は、検証は行わない
		if (!errorMsgMap.isEmpty()) {
			return;
		}

		// ****************************************
		// 顧客IDと案件IDが有効かどうか判定
		// ****************************************
		// ■ すでに初回面談日が登録されているかどうか
		if (!commonScheduleValidator.existsShokaiMendanSchedule(form.getAnkenId(), form.getCustomerId())) {
			// 初回面談がすでに登録されている場合 -> エラー
			errorMsgMap.put(key, MessageEnum.MSG_E00025);
			return;
		}

	}

	/**
	 * DB整合性チェック(更新)
	 *
	 * @param form
	 * @param key
	 * @param errorMsgMap
	 */
	private void accessDBValidateForScheduleUpdate(ScheduleInputForm form, String key, Map<String, MessageEnum> errorMsgMap) {

		// すでにエラーが有る場合は、検証は行わない
		if (!errorMsgMap.isEmpty()) {
			return;
		}

		// ****************************************
		// 顧客IDと案件IDが有効かどうか判定
		// ****************************************
		// ■案件ID(紐付けする値)が変更されていないか
		if (!commonScheduleValidator.existsAnkenForSchedule(form.getScheduleSeq(), form.getAnkenId())) {
			errorMsgMap.put(key, MessageEnum.MSG_E00025);
			return;
		}

		// ■顧客ID(紐付けする値)が変更されていないか
		if (!commonScheduleValidator.existsCustomerForSchedule(form.getScheduleSeq(), form.getCustomerId())) {
			errorMsgMap.put(key, MessageEnum.MSG_E00025);
			return;
		}
	}

	/**
	 * タスク詳細のURLを、正規表現を使用し、 リンクタグ（<a href=...）に変換し<br>
	 * 文字列内の改行コードをbrタグに変換する。
	 *
	 * @param memo スケジュールメモの文字列。
	 * @return リンクに変換された文字列。
	 */
	private String convertScheduleMemoURLLink(String memo) {
		if (StringUtils.isEmpty(memo)) {
			return "";
		}
		String escapeStr = HtmlUtils.htmlEscape(memo);
		Matcher matcher = CommonConstant.CONV_URL_LINK_PTN.matcher(escapeStr);
		String text = matcher.replaceAll("<a target=\"_blank\" href=\"$0\" rel=\"noopener\" class=\"scheduleMemoLink\">$0</a>");
		return text.replaceAll("\\r\\n|\\r|\\n", "<br>");
	}
}