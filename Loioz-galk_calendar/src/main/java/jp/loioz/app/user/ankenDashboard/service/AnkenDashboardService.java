package jp.loioz.app.user.ankenDashboard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonSaibanService;
import jp.loioz.app.common.service.CommonScheduleService;
import jp.loioz.app.user.ankenDashboard.dto.AnkenDashboardGyomuHistoryListDto;
import jp.loioz.app.user.ankenDashboard.dto.AnkenDashboardScheduleListDto;
import jp.loioz.app.user.ankenDashboard.dto.AnkenDashboardTaskListDto;
import jp.loioz.app.user.ankenDashboard.dto.AnkenDashboardTaskListDto.TaskTanto;
import jp.loioz.app.user.ankenDashboard.form.AnkenDashboardSearchForm;
import jp.loioz.app.user.ankenDashboard.form.AnkenDashboardViewForm;
import jp.loioz.app.user.schedule.form.ScheduleDetail;
import jp.loioz.bean.AnkenDashGyomuHistoryBean;
import jp.loioz.bean.AnkenDashScheduleBean;
import jp.loioz.bean.AnkenDashTaskBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.ShutteiType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaskStatus;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.AnkenDashboardDao;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MRoomDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.CustomerDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MRoomEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TSaibanJikenEntity;

/**
 * 案件管理画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AnkenDashboardService extends DefaultService {

	/** アカウント共通サービス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** アカウントマスタDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 共通裁判サービスクラス */
	@Autowired
	private CommonSaibanService commonSaibanService;

	/** 共通分野サービスクラス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 共通予定サービスクラス */
	@Autowired
	private CommonScheduleService commonScheduleService;

	/** 会議室Daoクラス */
	@Autowired
	private MRoomDao mRooomDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 裁判-事件Daoクラス */
	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	/** 案件ダッシュボードDaoクラス */
	@Autowired
	private AnkenDashboardDao ankenDashboardDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 画面表示情報を作成する<br>
	 *
	 * @param ankenId
	 * @param searchForm
	 * @return
	 */
	public AnkenDashboardViewForm createViewForm(Long ankenId, AnkenDashboardSearchForm searchForm) {

		// 案件情報を取得
		TAnkenEntity anken = tAnkenDao.selectById(ankenId);

		// 案件情報が存在しない場合
		if (anken == null) {
			throw new DataNotFoundException("案件情報が存在しません。[ankenId=" + ankenId + "]");
		}

		// 案件情報を設定
		AnkenDashboardViewForm viewForm = new AnkenDashboardViewForm();
		viewForm.setAnkenId(AnkenId.of(ankenId));
		viewForm.setAnkenName(anken.getAnkenName());
		viewForm.setBunya(commonBunyaService.getBunya(anken.getBunyaId()));

		// タスク情報の設定
		AnkenDashboardViewForm.AnkenDashboardTaskViewForm ankenDashboardTaskViewForm = createAnkenDashBoardTaskViewForm(ankenId, searchForm);
		viewForm.setAnkenDashboardTaskViewForm(ankenDashboardTaskViewForm);

		// スケジュール情報の設定
		AnkenDashboardViewForm.AnkenDashboardScheduleViewForm ankenDashboardScheduleViewForm = createAnkenDashboardScheduleViewForm(ankenId,
				searchForm);
		viewForm.setAnkenDashboardScheduleViewForm(ankenDashboardScheduleViewForm);

		// 業務履歴情報の設定
		AnkenDashboardViewForm.AnkenDashboardGyomuHistoryViewForm ankenDashboardGyomuHistoryViewForm = createAnkenDashboardGyomuHistoryViewForm(
				ankenId, searchForm);
		viewForm.setAnkenDashboardGyomuHistoryViewForm(ankenDashboardGyomuHistoryViewForm);

		return viewForm;
	}

	/**
	 * 案件ダッシュボード：タスク情報表示用オブジェクトを作成
	 * 
	 * @param ankenId
	 * @param searchForm
	 * @return
	 */
	public AnkenDashboardViewForm.AnkenDashboardTaskViewForm createAnkenDashBoardTaskViewForm(Long ankenId, AnkenDashboardSearchForm searchForm) {
		AnkenDashboardViewForm.AnkenDashboardTaskViewForm ankenDashboardTaskViewForm = new AnkenDashboardViewForm.AnkenDashboardTaskViewForm();

		// アカウント名Map
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		// アカウント取得すべて
		List<MAccountEntity> accountListAll = mAccountDao.selectAll();

		// データの取得処理
		long noCompCount = ankenDashboardDao.noCompCount(ankenId);// 未処理件数を取得 ※ここの件数は全体なので検索条件には関わらず一定値になる
		List<AnkenDashTaskBean> ankenDashTaskBeans = ankenDashboardDao.selectTaskBean(searchForm.isAllDispTaskList(), ankenId); // 検索条件に基づきタスクを取得

		// データの変換処理
		// タスクの作業者情報も取得するため、グループ化 ※取得した表示順を保つため、LinkedHashMapで扱うこと
		Map<Long, List<AnkenDashTaskBean>> taskSeqToMap = ankenDashTaskBeans.stream()
				.collect(Collectors.groupingBy(AnkenDashTaskBean::getTaskSeq, LinkedHashMap::new, Collectors.toList()));

		// タスク一覧情報を作成
		List<AnkenDashboardTaskListDto> ankenDashboardTaskListDtoList = taskSeqToMap.values().stream().map(beans -> {

			AnkenDashboardTaskListDto ankenDashboardTaskListDto = new AnkenDashboardTaskListDto();

			// アカウント情報以外は、同じ情報なので、一番最初の配列から設定する
			AnkenDashTaskBean ankenDashTaskBean = beans.stream().findFirst().orElse(new AnkenDashTaskBean());
			ankenDashboardTaskListDto.setTaskSeq(ankenDashTaskBean.getTaskSeq());
			ankenDashboardTaskListDto.setAnkenId(ankenDashTaskBean.getAnkenId());
			ankenDashboardTaskListDto.setTitle(ankenDashTaskBean.getTitle());
			ankenDashboardTaskListDto.setContent(ankenDashTaskBean.getContent());
			ankenDashboardTaskListDto.setTaskStatus(ankenDashTaskBean.getTaskStatus());
			ankenDashboardTaskListDto.setCommentCount(ankenDashTaskBean.getCommentCount());
			if (SystemFlg.codeToBoolean(ankenDashTaskBean.getAllDayFlg())) {
				ankenDashboardTaskListDto
						.setLimitDt(DateUtils.parseToString(ankenDashTaskBean.getLimitDtTo(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			} else {
				ankenDashboardTaskListDto
						.setLimitDt(DateUtils.parseToString(ankenDashTaskBean.getLimitDtTo(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
			}

			// 担当者を設定
			List<TaskTanto> taskTantoList = new ArrayList<>();
			for (AnkenDashTaskBean taskBean : beans) {
				TaskTanto taskTanto = createTaskTantoWithAccountSeq(taskBean.getAccountSeq(), accountNameMap, accountListAll, taskBean.getEntrustFlg());
				if (taskTanto != null) {
					taskTantoList.add(taskTanto);
				}
			}
			ankenDashboardTaskListDto.setTaskTantoList(taskTantoList);

			// 期限日の文字色指定
			ankenDashboardTaskListDto.setLimitDateStyle(getLimitDateStyle(ankenDashboardTaskListDto));

			// サブタスク件数
			ankenDashboardTaskListDto.setCheckItemCount(ankenDashTaskBean.getCheckItemCount());
			ankenDashboardTaskListDto.setCompleteCheckItemCount(ankenDashTaskBean.getCompleteCheckItemCount());

			return ankenDashboardTaskListDto;
		}).collect(Collectors.toList());

		ankenDashboardTaskViewForm.setAnkenId(AnkenId.of(ankenId));
		ankenDashboardTaskViewForm.setNoCompCount(noCompCount);
		ankenDashboardTaskViewForm.setTaskList(ankenDashboardTaskListDtoList);

		return ankenDashboardTaskViewForm;
	}

	/**
	 * 案件ダッシュボード：予定情報表示用オブジェクトを作成
	 * 
	 * @param ankenId
	 * @param searchForm
	 * @return
	 */
	public AnkenDashboardViewForm.AnkenDashboardScheduleViewForm createAnkenDashboardScheduleViewForm(Long ankenId,
			AnkenDashboardSearchForm searchForm) {
		AnkenDashboardViewForm.AnkenDashboardScheduleViewForm ankenDashboardScheduleViewForm = new AnkenDashboardViewForm.AnkenDashboardScheduleViewForm();

		// アカウント名Map
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		// 部署名Map
		List<MRoomEntity> mRoomEntities = mRooomDao.selectAll();
		Map<Long, String> roomNameMap = mRoomEntities.stream().collect(Collectors.toMap(MRoomEntity::getRoomId, MRoomEntity::getRoomName));

		// データの取得処理
		TAnkenEntity tAnkenEntity = tAnkenDao.selectByAnkenId(ankenId);
		long beforeKijituCount = ankenDashboardDao.beforeKijituCount(ankenId, SessionUtils.getLoginAccountSeq());
		boolean isOnlyKijituScheduleList = false;
		List<AnkenDashScheduleBean> ankenDashScheduleBeanList = ankenDashboardDao.selectScheduleBean(searchForm.isAllDispScheduleList(), isOnlyKijituScheduleList,
				ankenId, SessionUtils.getLoginAccountSeq());

		// データの変換処理
		Map<Long, List<AnkenDashScheduleBean>> scheduleSeqToMap = ankenDashScheduleBeanList.stream()
				.collect(Collectors.groupingBy(AnkenDashScheduleBean::getScheduleSeq, LinkedHashMap::new, Collectors.toList()));
		List<AnkenDashboardScheduleListDto> ankenDashboardScheduleListDtoList = scheduleSeqToMap.values().stream().map(beans -> {
			AnkenDashboardScheduleListDto ankenDashboardScheduleListDto = new AnkenDashboardScheduleListDto();

			// アカウント情報以外は、同じ情報なので、一番最初の配列から設定する
			AnkenDashScheduleBean ankenDashScheduleBean = beans.stream().findFirst().orElse(new AnkenDashScheduleBean());
			ankenDashboardScheduleListDto.setScheduleSeq(ankenDashScheduleBean.getScheduleSeq());
			ankenDashboardScheduleListDto.setCustomerId(ankenDashScheduleBean.getCustomerId());
			ankenDashboardScheduleListDto.setAnkenId(ankenDashScheduleBean.getAnkenId());
			ankenDashboardScheduleListDto.setSaibanSeq(ankenDashScheduleBean.getSaibanSeq());
			ankenDashboardScheduleListDto.setSaibanBranchNo(ankenDashScheduleBean.getSaibanBranchNo());
			ankenDashboardScheduleListDto.setSaibanLimitSeq(ankenDashScheduleBean.getSaibanLimitSeq());
			ankenDashboardScheduleListDto.setSubject(ankenDashScheduleBean.getSubject());
			ankenDashboardScheduleListDto.setAllDay(SystemFlg.codeToBoolean(ankenDashScheduleBean.getAllDayFlg()));
			ankenDashboardScheduleListDto.setDateFrom(ankenDashScheduleBean.getDateFrom());
			ankenDashboardScheduleListDto.setTimeFrom(ankenDashScheduleBean.getTimeFrom());
			ankenDashboardScheduleListDto.setDateTo(ankenDashScheduleBean.getDateTo());
			ankenDashboardScheduleListDto.setTimeTo(ankenDashScheduleBean.getTimeTo());
			if (ankenDashScheduleBean.getRoomId() == null) {
				ankenDashboardScheduleListDto.setPlace(ankenDashScheduleBean.getPlace());
			} else {
				ankenDashboardScheduleListDto.setPlace(roomNameMap.get(ankenDashScheduleBean.getRoomId()));
			}
			ankenDashboardScheduleListDto.setShutteiType(ShutteiType.of(ankenDashScheduleBean.getShutteiType()));
			ankenDashboardScheduleListDto.setJikenName(ankenDashScheduleBean.getJikenName());
			ankenDashboardScheduleListDto.setCaseNumber(CaseNumber.of(ankenDashScheduleBean.getJiken_gengo(), ankenDashScheduleBean.getJiken_year(), ankenDashScheduleBean.getJiken_mark(), ankenDashScheduleBean.getJiken_no()));

			// 子要素の作成
			List<String> accounNames = beans.stream().map(bean -> {
				return accountNameMap.get(bean.getAccountSeq());
			}).collect(Collectors.toList());
			ankenDashboardScheduleListDto.setAccountNameList(accounNames);

			Long saibanSeq = ankenDashScheduleBean.getSaibanSeq();
			if (saibanSeq != null) {
				// 裁判当事者名Map
				Map<Long, Map<String, String>> saibanTojishaNameMap = commonSaibanService.getSaibanTojishaNameMap(List.of(saibanSeq));
				Map<String, String> tojishaLabelMap = saibanTojishaNameMap.getOrDefault(saibanSeq, new HashMap<String, String>());
				String tojishaNameLabel = tojishaLabelMap.getOrDefault(CommonSaibanService.SAIBAN_TOJISHA_LABEL_MAP_TOJISHA_KEY, "");
				String aitegataNameLabel = tojishaLabelMap.getOrDefault(CommonSaibanService.SAIBAN_TOJISHA_LABEL_MAP_AITEGATA_KEY, "");
				ankenDashboardScheduleListDto.setSaibanTojishaNameLabel(tojishaNameLabel);
				ankenDashboardScheduleListDto.setSaibanAitegataNameLabel(aitegataNameLabel);
			}

			return ankenDashboardScheduleListDto;

		}).collect(Collectors.toList());

		ankenDashboardScheduleViewForm.setAnkenId(AnkenId.of(tAnkenEntity.getAnkenId()));
		ankenDashboardScheduleViewForm.setAnkenName(tAnkenEntity.getAnkenName());
		ankenDashboardScheduleViewForm.setBunya(commonBunyaService.getBunya(tAnkenEntity.getBunyaId()));

		ankenDashboardScheduleViewForm.setBeforeKijituCount(beforeKijituCount);
		ankenDashboardScheduleViewForm.setScheduleList(ankenDashboardScheduleListDtoList);

		return ankenDashboardScheduleViewForm;
	}

	/**
	 * 案件ダッシュボードの予定詳細データを取得
	 * 
	 * @param ankenId
	 * @param searchForm
	 * @return
	 */
	public Map<Long, ScheduleDetail> getAnkenDashScheduleDetails(Long ankenId, AnkenDashboardSearchForm searchForm) {

		boolean isOnlyKijituScheduleList = false;
		List<AnkenDashScheduleBean> ankenDashScheduleBeanList = ankenDashboardDao.selectScheduleBean(searchForm.isAllDispScheduleList(), isOnlyKijituScheduleList, ankenId, SessionUtils.getLoginAccountSeq());

		Set<Long> scheduleSeqSet = ankenDashScheduleBeanList.stream().map(AnkenDashScheduleBean::getScheduleSeq).collect(Collectors.toSet());

		return commonScheduleService.getSchedulePKOne(new ArrayList<>(scheduleSeqSet));
	}

	/**
	 * 案件ダッシュボード：業務履歴情報表示用オブジェクトを作成
	 * 
	 * @param ankenId
	 * @param searchForm
	 * @return
	 */
	public AnkenDashboardViewForm.AnkenDashboardGyomuHistoryViewForm createAnkenDashboardGyomuHistoryViewForm(Long ankenId,
			AnkenDashboardSearchForm searchForm) {
		AnkenDashboardViewForm.AnkenDashboardGyomuHistoryViewForm ankenDashboardGyomuHistoryViewForm = new AnkenDashboardViewForm.AnkenDashboardGyomuHistoryViewForm();

		// アカウント名Map
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		// データの取得処理
		List<AnkenDashGyomuHistoryBean> ankenDashGyomuHistoryBeanList = ankenDashboardDao.selectGyomuHistoryBean(ankenId);

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// 裁判情報
		List<Long> saibanMinjiSeqList = ankenDashGyomuHistoryBeanList.stream()
				.filter(e -> e.getSaibanSeq() != null && !bunyaMap.get(e.getBunyaId()).isKeiji()).map(AnkenDashGyomuHistoryBean::getSaibanSeq)
				.collect(Collectors.toList());
		List<Long> saibanKeijiSeqList = ankenDashGyomuHistoryBeanList.stream()
				.filter(e -> e.getSaibanSeq() != null && bunyaMap.get(e.getBunyaId()).isKeiji()).map(AnkenDashGyomuHistoryBean::getSaibanSeq)
				.collect(Collectors.toList());
		List<TSaibanJikenEntity> minjiSaibanList = tSaibanJikenDao.selectBySaibanSeq(saibanMinjiSeqList);
		List<TSaibanJikenEntity> keijiSaibanList = tSaibanJikenDao.selectByKeijiSaibanSeq(saibanKeijiSeqList);
		Map<Long, String> saibanJikenNameMap = new HashMap<Long, String>();
		Map<Long, CaseNumber> saibanJikenNoMap = new HashMap<Long, CaseNumber>();
		minjiSaibanList.forEach(e -> {
			saibanJikenNameMap.put(e.getSaibanSeq(), e.getJikenName());
			saibanJikenNoMap.put(e.getSaibanSeq(), CaseNumber.fromEntity(e));
		});
		keijiSaibanList.forEach(e -> {
			saibanJikenNameMap.put(e.getSaibanSeq(), e.getJikenName());
			saibanJikenNoMap.put(e.getSaibanSeq(), CaseNumber.fromEntity(e));
		});

		// 取得データを履歴のSEQでまとめる（対象履歴が複数顧客に紐づく場合にListの要素数が複数となる）
		Map<Long, List<AnkenDashGyomuHistoryBean>> gyomuHistorySeqToMap = ankenDashGyomuHistoryBeanList.stream().collect(
				Collectors.groupingBy(
						AnkenDashGyomuHistoryBean::getGyomuHistorySeq,
						LinkedHashMap::new,
						Collectors.toList()));

		// 取得データを表示用Dtoに変換
		List<AnkenDashboardGyomuHistoryListDto> gyomuHistoryList = gyomuHistorySeqToMap.values().stream().map(beans -> {
			AnkenDashboardGyomuHistoryListDto ankenDashboardGyomuHistoryListDto = new AnkenDashboardGyomuHistoryListDto();

			// 全てのbeansで同じ値なので、一番最初の要素の値を設定する
			AnkenDashGyomuHistoryBean ankenDashGyomuHistoryBean = beans.stream().findFirst().orElse(new AnkenDashGyomuHistoryBean());
			ankenDashboardGyomuHistoryListDto.setGyomuHistorySeq(ankenDashGyomuHistoryBean.getGyomuHistorySeq());
			ankenDashboardGyomuHistoryListDto.setTransitionType(TransitionType.of(ankenDashGyomuHistoryBean.getTransitionType()));
			ankenDashboardGyomuHistoryListDto.setSubject(ankenDashGyomuHistoryBean.getSubject());
			ankenDashboardGyomuHistoryListDto.setMainText(ankenDashGyomuHistoryBean.getMainText());
			ankenDashboardGyomuHistoryListDto.setImportantFlg(ankenDashGyomuHistoryBean.getImportantFlg());
			ankenDashboardGyomuHistoryListDto.setSentDengon(SystemFlg.codeToBoolean(ankenDashGyomuHistoryBean.getDengonSentFlg()));
			ankenDashboardGyomuHistoryListDto.setKoteiFlg(ankenDashGyomuHistoryBean.getKoteiFlg());
			ankenDashboardGyomuHistoryListDto.setSupportedAt(
					DateUtils.parseToString(ankenDashGyomuHistoryBean.getSupportedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
			ankenDashboardGyomuHistoryListDto.setCreaterName(accountNameMap.get(ankenDashGyomuHistoryBean.getCreatedBy()));
			ankenDashboardGyomuHistoryListDto.setCreatedAt(
					DateUtils.parseToString(ankenDashGyomuHistoryBean.getCreatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
			ankenDashboardGyomuHistoryListDto.setUpdatedAt(
					DateUtils.parseToString(ankenDashGyomuHistoryBean.getUpdatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));

			// 案件情報
			ankenDashboardGyomuHistoryListDto.setAnkenId(ankenDashGyomuHistoryBean.getAnkenId());
			// 裁判情報
			ankenDashboardGyomuHistoryListDto.setSaibanSeq(ankenDashGyomuHistoryBean.getSaibanSeq());
			ankenDashboardGyomuHistoryListDto.setSaibanBranchNo(ankenDashGyomuHistoryBean.getSaibanBranchNo());
			ankenDashboardGyomuHistoryListDto.setJikenName(saibanJikenNameMap.get(ankenDashGyomuHistoryBean.getSaibanSeq()));
			ankenDashboardGyomuHistoryListDto.setCaseNumber(saibanJikenNoMap.get(ankenDashGyomuHistoryBean.getSaibanSeq()));

			// beansの要素によって値が異なるもの
			// 顧客情報 子要素として作成
			List<CustomerDto> customerList = beans.stream().map(bean -> {
				CustomerDto customer = new CustomerDto();
				customer.setCustomerId(Objects.nonNull(bean.getCustomerId()) ? CustomerId.of(bean.getCustomerId()) : null);
				customer.setCustomerName(bean.getCustomerName());
				return customer;
			}).collect(Collectors.toList());
			ankenDashboardGyomuHistoryListDto.setCustomerList(customerList);

			return ankenDashboardGyomuHistoryListDto;
		}).limit(CommonConstant.ANKEN_DASH_GYOMU_HISTORY_LIMIT).collect(Collectors.toList());
		// 取得件数がそこまで多くならないことが想定されるので、作成したデータをjavaのlimitで制御する
		// 今後、パフォーマンスの改善が必要な場合は、SQLを2段階取得(Pagerの一覧取得)に変更するなどの対策が必要

		ankenDashboardGyomuHistoryViewForm.setAnkenId(AnkenId.of(ankenId));
		ankenDashboardGyomuHistoryViewForm.setGyomuHistoryList(gyomuHistoryList);
		return ankenDashboardGyomuHistoryViewForm;
	}

	/**
	 * 期限日の文字スタイルを取得します。<br>
	 * 期日を超えていた場合、背景値として task_over を返します。
	 *
	 * @param dto
	 * @return
	 */
	private String getLimitDateStyle(AnkenDashboardTaskListDto dto) {

		final String overTaskStyle = "task_over";

		// 完了したタスク
		if (Objects.equals(dto.getTaskStatus(), TaskStatus.COMPLETED.getCd())) {
			return dto.getTaskStatus();
		}

		// 期日を超えていた場合
		if (!StringUtils.isEmpty(dto.getLimitDt())) {

			// 日付のみの場合の文字数
			int numberOfCharactersInDate = 10;

			if (dto.getLimitDt().length() > numberOfCharactersInDate) {
				LocalDateTime localDateTime = LocalDateTime.parse(dto.getLimitDt(), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
				if (localDateTime.isBefore(LocalDateTime.now())) {
					return overTaskStyle;
				}
			} else {
				LocalDate localDate = LocalDate.parse(dto.getLimitDt(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
				if (localDate.isBefore(LocalDate.now())) {
					return overTaskStyle;
				}
			}
		}

		return dto.getTaskStatus();
	}

	/**
	 * 担当者のフォーム情報を作成する
	 * 
	 * @param accountSeq
	 * @param accountNameMap
	 * @param acountEntityList
	 * @param entrustFlg
	 * @return
	 */
	private TaskTanto createTaskTantoWithAccountSeq(Long accountSeq, Map<Long, String> accountNameMap, List<MAccountEntity> acountEntityList, String entrustFlg) {

		// 担当者の場合に担当者情報を作成する（委任者の場合は担当者として表示しないため）
		if (SystemFlg.FLG_OFF.equalsByCode(entrustFlg)) {
			TaskTanto taskTanto = new TaskTanto();
			// SEQセット
			taskTanto.setWorkerAccountSeq(accountSeq);
			// 名前セット
			taskTanto.setWorkerAccountName(accountNameMap.get(accountSeq));
			// 色セット
			for (MAccountEntity entity : acountEntityList) {
				if (accountSeq.equals(entity.getAccountSeq())) {
					taskTanto.setAccountColor(entity.getAccountColor());
				}
			}

			return taskTanto;
		}
		return null;
	}

}
