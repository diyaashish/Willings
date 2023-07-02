package jp.loioz.app.common.validation.accessDB;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.user.schedule.form.ScheduleInputForm;
import jp.loioz.app.user.schedule.service.ScheduleCommonService;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MRoomDao;
import jp.loioz.dao.ScheduleDao;
import jp.loioz.dao.TSaibanLimitDao;
import jp.loioz.dao.TScheduleAccountDao;
import jp.loioz.dao.TScheduleDao;
import jp.loioz.domain.condition.ScheduleSearchCondition;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MRoomEntity;
import jp.loioz.entity.TSaibanLimitEntity;
import jp.loioz.entity.TScheduleAccountEntity;
import jp.loioz.entity.TScheduleEntity;

/**
 * 予定表共通DBバリデーター
 */
@Component
public class CommonScheduleValidator {

	/** 施設マスタ */
	@Autowired
	private MRoomDao mRoomDao;

	/** アカウントマスタ */
	@Autowired
	private MAccountDao mAccountDao;

	/** スケジュールDao */
	@Autowired
	private ScheduleDao scheduleDao;

	/** スケジュールDaoクラス */
	@Autowired
	private TScheduleDao tScheduleDao;

	/** スケジュールアカウントDaoクラス */
	@Autowired
	private TScheduleAccountDao tScheduleAccountDao;

	/** 裁判期日Daoクラス */
	@Autowired
	private TSaibanLimitDao tSaibanLimitDao;

	/**
	 * 施設の存在チェック(有効のもののみ)
	 *
	 * @param roomID
	 * @return 検証結果
	 */
	public boolean isRoomExistsValid(Long scheduleSeq, Long roomId) {

		// IDがnull -> 検証しない
		if (roomId == null) {
			return true;
		}

		// 登録中の会議室の場合、無効な会議室IDを許容する
		if (scheduleSeq != null) {
			TScheduleEntity tScheduleEntity = tScheduleDao.selectBySeq(scheduleSeq);
			if (Objects.equals(tScheduleEntity.getRoomId(), roomId)) {
				return true;
			}
		}

		MRoomEntity entity = mRoomDao.selectById(roomId);
		if (entity == null) {
			// 無効なID
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 利用可能施設の整合姓チェック
	 *
	 * @param form
	 * @return
	 */
	public boolean isRoomUsingValid(ScheduleInputForm form) {
		// IDがnull -> 検証しない
		if (form.getRoomId() == null) {
			return true;
		}

		// 終日以外の場合、開始日を終了日に指定する
		LocalDate conditionDateTo = form.getDateTo();
		if (!form.isAllday() || conditionDateTo == null) {
			conditionDateTo = form.getDateFrom();
		}

		// 登録する時間に施設を利用する予定を取得
		ScheduleSearchCondition condition = ScheduleSearchCondition.builder()
				.scheduleSeq(form.getScheduleSeq())
				.dateFrom(form.getDateFrom())
				.dateTo(conditionDateTo)
				.timeFrom(form.getTimeFrom())
				.timeTo(form.getTimeTo())
				.repeatYobi(ScheduleCommonService.encodeRepeatYobi(form.getRepeatYobi()))
				.repeatDayOfMonth(form.getRepeatDayOfMonth())
				.build();
		List<TScheduleEntity> scheduleEntityList = scheduleDao.selectRoomAvailability(condition);

		if (scheduleEntityList.stream().anyMatch(entity -> Objects.equals(form.getRoomId(), entity.getRoomId()))) {
			// 利用予定なので、使用不可
			return false;
		} else {
			return true;
		}

	}

	/**
	 * アカウントの整合性チェック
	 *
	 * @param accountList
	 * @return 検証結果
	 */
	public boolean isAccountExistsValid(Long scheduleSeq, List<Long> accountList) {

		// null or 空の場合 -> 検証しない
		if (ListUtils.isEmpty(accountList)) {
			return true;
		}

		List<TScheduleAccountEntity> tScheduleAccountEntity = new ArrayList<>();
		if (scheduleSeq != null) {
			tScheduleAccountEntity = tScheduleAccountDao.selectByScheduleSeq(scheduleSeq);
			if (tScheduleAccountEntity == null) {
				tScheduleAccountEntity = Collections.emptyList();
			}
		}
		List<MAccountEntity> mAccountEntities = mAccountDao.selectEnabledAccountByAccountSeq(accountList);
		if (mAccountEntities == null) {
			mAccountEntities = Collections.emptyList();
		}
		List<Long> orgAccountSeqList = tScheduleAccountEntity.stream().map(TScheduleAccountEntity::getAccountSeq).collect(Collectors.toList());
		List<Long> enabledAccountSeqList = mAccountEntities.stream().map(MAccountEntity::getAccountSeq).collect(Collectors.toList());

		for (Long accountSeq : accountList) {
			if (!orgAccountSeqList.contains(accountSeq) && !enabledAccountSeqList.contains(accountSeq)) {
				// 事前に登録済ではなく、無効アカウントに紐付けた時 -> エラー
				return false;
			}

		}

		return true;
	}

	/**
	 * 初回面談に紐づいた予定がすでに存在するか検証します
	 *
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	public boolean existsShokaiMendanSchedule(Long ankenId, Long customerId) {

		// null の場合 -> 検証しない
		if (CommonUtils.anyNull(ankenId, customerId)) {
			return true;
		}

		TScheduleEntity tScheduleEntity = tScheduleDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		if (tScheduleEntity != null) {
			// 取得できた場合 -> すでに初回面談予定が登録されている
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 紐づく案件ID変更されていないか検証する
	 *
	 * @param scheduleSeq
	 * @param ankenId
	 * @return 検証結果
	 */
	public boolean existsAnkenForSchedule(Long scheduleSeq, Long ankenId) {

		// 引数にnullがある -> 検証しない
		if (CommonUtils.anyNull(scheduleSeq, ankenId)) {
			return true;
		}

		TScheduleEntity tScheduleEntity = tScheduleDao.selectBySeq(scheduleSeq);

		if (tScheduleEntity == null || !Objects.equals(tScheduleEntity.getAnkenId(), ankenId)) {
			// 取得できない or 案件IDが一致しない -> 不正データ
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 紐づく顧客ID変更されていないか検証する
	 *
	 * @param scheduleSeq
	 * @param customerId
	 * @return 検証結果
	 */
	public boolean existsCustomerForSchedule(Long scheduleSeq, Long customerId) {

		// 引数にnullがある -> 検証しない
		if (CommonUtils.anyNull(scheduleSeq, customerId)) {
			return true;
		}

		TScheduleEntity tScheduleEntity = tScheduleDao.selectBySeq(scheduleSeq);

		if (tScheduleEntity == null || !Objects.equals(tScheduleEntity.getCustomerId(), customerId)) {
			// 取得できない or 顧客IDが一致しない -> 不正データ
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 紐づく裁判SEQ変更されていないか検証する
	 *
	 * @param scheduleSeq
	 * @param saibanSeq
	 * @return 検証結果
	 */
	public boolean existsSaibanForSchedule(Long scheduleSeq, Long saibanSeq) {

		// 引数にnullがある -> 検証しない
		if (CommonUtils.anyNull(scheduleSeq, saibanSeq)) {
			return true;
		}

		TScheduleEntity tScheduleEntity = tScheduleDao.selectBySeq(scheduleSeq);

		if (tScheduleEntity == null || !Objects.equals(tScheduleEntity.getSaibanSeq(), saibanSeq)) {
			// 取得できない or 裁判SEQが一致しない -> 不正データ
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 紐づく裁判期日SEQ変更されていないか検証する
	 *
	 * @param scheduleSeq
	 * @param saibanLimitSeq
	 * @return 検証結果
	 */
	public boolean existsSaibanLimitForSchedule(Long scheduleSeq, Long saibanLimitSeq) {

		// 引数にnullがある -> 検証しない
		if (CommonUtils.anyNull(scheduleSeq, saibanLimitSeq)) {
			return true;
		}

		TScheduleEntity tScheduleEntity = tScheduleDao.selectBySeq(scheduleSeq);

		if (tScheduleEntity == null || !Objects.equals(tScheduleEntity.getSaibanLimitSeq(), saibanLimitSeq)) {
			// 取得できない or 裁判期日SEQが一致しない -> 不正データ
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 予定表Topから初回面談予定の項目が編集されていないかをチェックします。
	 *
	 * @param requestData
	 * @return 検証結果
	 */
	public boolean isNonEditScheduleValidForShokaiMendan(ScheduleInputForm requestData) {

		// 引数がnullの場合 -> 検証しない
		if (requestData == null) {
			return true;
		}

		TScheduleEntity tScheduleEntity = tScheduleDao.selectBySeq(requestData.getScheduleSeq());

		// 編集されているかの確認なので、取得できない場合もエラーとする
		if (tScheduleEntity == null) {
			return false;
		}

		// 検証結果を格納する
		List<Boolean> resultList = new ArrayList<>();

		// 各項目の検証結果をListに格納
		resultList.add(Objects.equals(tScheduleEntity.getCustomerId(), requestData.getCustomerId()));// 顧客ID
		resultList.add(Objects.equals(tScheduleEntity.getAnkenId(), requestData.getAnkenId()));// 案件ID

		// 一つでもfalseがある場合 -> エラー
		if (resultList.stream().anyMatch(bool -> !bool)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 予定表Topから裁判期日予定の項目が編集されていないかをチェックします。
	 *
	 * @param scheduleSeq
	 * @return 検証結果
	 */
	public boolean isNonEditScheduleValidForSaibanLimit(ScheduleInputForm requestData) {

		// 引数がnullの場合 -> 検証しない
		if (requestData == null) {
			return true;
		}

		TScheduleEntity tScheduleEntity = tScheduleDao.selectBySeq(requestData.getScheduleSeq());
		TSaibanLimitEntity tSaibanLimitEntity = tSaibanLimitDao.selectBySeq(requestData.getSaibanLimitSeq());

		// 編集されているかの確認なので、取得できない場合もエラーとする
		if (CommonUtils.anyNull(tScheduleEntity, tSaibanLimitEntity)) {
			return false;
		}

		// 検証結果を格納する
		List<Boolean> resultList = new ArrayList<>();

		// 各項目の検証結果をListに格納
		resultList.add(Objects.equals(tScheduleEntity.getSubject(), requestData.getSubject()));// 件名
		resultList.add(Objects.equals(tScheduleEntity.getDateFrom(), requestData.getDateFrom()));// 日付
		resultList.add(Objects.equals(tScheduleEntity.getTimeFrom(), requestData.getTimeFrom()));// 開始日時
		resultList.add(Objects.equals(tScheduleEntity.getTimeTo(), requestData.getTimeTo()));// 開始日時
		resultList.add(Objects.equals(tScheduleEntity.getRoomId(), requestData.getRoomId()));// 場所
		resultList.add(Objects.equals(StringUtils.defaultString(tScheduleEntity.getPlace()), StringUtils.defaultString(requestData.getPlace())));// 場所その他
		resultList.add(Objects.equals(tScheduleEntity.getAnkenId(), requestData.getAnkenId()));// 案件ID
		resultList.add(Objects.equals(tScheduleEntity.getSaibanSeq(), requestData.getSaibanSeq()));// 裁判SEQ
		resultList.add(Objects.equals(tScheduleEntity.getSaibanLimitSeq(), requestData.getSaibanLimitSeq()));// 裁判期日SEQ
		resultList.add(Objects.equals(tSaibanLimitEntity.getShutteiType(), DefaultEnum.getCd(requestData.getShutteiType())));// 出廷要不要

		// 一つでもfalseがある場合 -> エラー
		if (resultList.stream().anyMatch(bool -> !bool)) {
			return false;
		} else {
			return true;
		}
	}

}
