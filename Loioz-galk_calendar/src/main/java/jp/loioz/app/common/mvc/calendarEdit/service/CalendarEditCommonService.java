package jp.loioz.app.common.mvc.calendarEdit.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.mvc.calendarEdit.form.CalendarEditModalInputForm;
import jp.loioz.app.common.mvc.calendarEdit.form.CalendarEditModalInputForm.GuestAccountSelectOption;
import jp.loioz.app.common.service.CommonCalendarService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.CalendarNotificationType;
import jp.loioz.common.constant.CommonConstant.CalendarOpenRange;
import jp.loioz.common.constant.CommonConstant.CalendarRelatedSetting;
import jp.loioz.common.constant.CommonConstant.CalendarScheduleColorType;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.entity.MAccountEntity;

/**
 * カレンダー予定編集サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CalendarEditCommonService extends DefaultService {

	/** アカウントDao */
	@Autowired
	private MAccountDao mAccountDao;

	/** 共通カレンダーサービス */
	@Autowired
	CommonCalendarService commonCalendarService;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/**
	 * 【新規】予定作成モーダルの入力用オブジェクトを作成
	 * 
	 * @return
	 */
	public CalendarEditModalInputForm createCalendarEditModalInputForm() {

		CalendarEditModalInputForm inputForm = new CalendarEditModalInputForm();

		// 予定の基本情報入力フォーム
		CalendarEditModalInputForm.CalendarBasicInputForm calendarBasicInputForm = this.createCalendarBasicInputForm();
		inputForm.setCalendarBasicInputForm(calendarBasicInputForm);

		// 予定の施設選択フォーム
		CalendarEditModalInputForm.CalendarSelectedOptionRoomListInputForm calendarSelectedOptionRoomListInputForm = this.createCalendarSelectedOptionRoomListInputForm();
		inputForm.setCalendarSelectedOptionRoomListInputForm(calendarSelectedOptionRoomListInputForm);

		// 予定の紐づけ設定入力フォーム
		CalendarEditModalInputForm.CalendarRelatedSettingInputForm calendarRelatedSettingInputForm = this.createCalendarRelatedSettingInputForm();
		inputForm.setCalendarRelatedSettingInputForm(calendarRelatedSettingInputForm);

		// 予定の招待者入力フォーム
		CalendarEditModalInputForm.CalendarGuestAccountInputForm calendarGuestAccountInputForm = this.createCalendarGuestAccountInputForm();
		inputForm.setCalendarGuestAccountInputForm(calendarGuestAccountInputForm);

		return inputForm;
	}

	/**
	 * 【新規】予定登録フォーム（基本情報）を取得する
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public CalendarEditModalInputForm.CalendarBasicInputForm createCalendarBasicInputForm() {

		CalendarEditModalInputForm.CalendarBasicInputForm inputForm = new CalendarEditModalInputForm.CalendarBasicInputForm();

		// 開始日
		inputForm.setDateFrom(LocalDate.now());
		// 終了日
		inputForm.setDateTo(LocalDate.now());

		// 施設選択なし
		inputForm.setSelectedRoom(false);

		// 公開範囲
		inputForm.setOpenRangeCd(CalendarOpenRange.PUBLIC.getCd());

		// 通知
		inputForm.setNotificationTypeCd(CalendarNotificationType.PUSH.getCd());

		// ログインアカウントカラー
		inputForm.setScheduleColorTypeCd(CalendarScheduleColorType.LOGIN_ACCOUNT_COLOR.getCd());

		// （表示用）ログインアカウントSEQ
		Long loginAccountSeq = SessionUtils.getLoginAccountSeq();
		MAccountEntity mAccountEntity = mAccountDao.selectBySeq(loginAccountSeq);

		// ログインアカウントSEQ
		inputForm.setLoginAccountSeq(SessionUtils.getLoginAccountSeq());
		// ログインアカウント-名
		inputForm.setLoginAccountName(SessionUtils.getLoginAccountName());
		// ログインアカウント-色
		inputForm.setLoginAccountColor(mAccountEntity.getAccountColor());

		return inputForm;
	}

	/**
	 * 【編集】予定編集フォーム（基本情報）を取得する
	 * 
	 * @param viewScheduleSeq
	 * @return
	 * @throws AppException
	 */
	public CalendarEditModalInputForm.CalendarBasicInputForm getCalendarBasicInputForm(Long viewScheduleSeq) throws AppException {
		CalendarEditModalInputForm.CalendarBasicInputForm inputForm = new CalendarEditModalInputForm.CalendarBasicInputForm();

		return inputForm;
	}

	/**
	 * 【新規】予定-施設選択フォームを取得する
	 * 
	 * @return
	 */
	public CalendarEditModalInputForm.CalendarSelectedOptionRoomListInputForm createCalendarSelectedOptionRoomListInputForm() {
		CalendarEditModalInputForm.CalendarSelectedOptionRoomListInputForm inputForm = new CalendarEditModalInputForm.CalendarSelectedOptionRoomListInputForm();

		// 施設選択候補
		List<SelectOptionForm> roomlist = commonCalendarService.getRoomList();
		inputForm.setSelectOptionRoomList(roomlist);
		inputForm.setSelectedRoomId(2L);

		return inputForm;
	}

	/**
	 * 【編集】予定-施設選択フォームを取得する
	 * 
	 * @return
	 */
	public CalendarEditModalInputForm.CalendarSelectedOptionRoomListInputForm getCalendarSelectedOptionRoomListInputForm(Long viewScheduleSeq) {
		CalendarEditModalInputForm.CalendarSelectedOptionRoomListInputForm inputForm = new CalendarEditModalInputForm.CalendarSelectedOptionRoomListInputForm();

		// 施設選択候補
		List<SelectOptionForm> roomlist = commonCalendarService.getRoomList();
		inputForm.setSelectOptionRoomList(roomlist);

		return inputForm;
	}

	/**
	 * 【新規】予定登録フォーム（関連設定）を取得する
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public CalendarEditModalInputForm.CalendarRelatedSettingInputForm createCalendarRelatedSettingInputForm() {
		CalendarEditModalInputForm.CalendarRelatedSettingInputForm inputForm = new CalendarEditModalInputForm.CalendarRelatedSettingInputForm();

		// 紐づけなし
		inputForm.setCalendarRelatedSettingCd(CalendarRelatedSetting.DEFAULT.getCd());

		return inputForm;
	}

	/**
	 * 【編集】予定編集フォーム（関連設定）を取得する
	 * 
	 * @param viewScheduleSeq
	 * @return
	 * @throws AppException
	 */
	public CalendarEditModalInputForm.CalendarRelatedSettingInputForm getCalendarRelatedSettingInputForm(Long viewScheduleSeq) throws AppException {
		CalendarEditModalInputForm.CalendarRelatedSettingInputForm inputForm = new CalendarEditModalInputForm.CalendarRelatedSettingInputForm();

		return inputForm;
	}

	/**
	 * 【新規】予定登録フォーム（招待者）を取得する
	 * 
	 * @return
	 */
	public CalendarEditModalInputForm.CalendarGuestAccountInputForm createCalendarGuestAccountInputForm() {
		CalendarEditModalInputForm.CalendarGuestAccountInputForm inputForm = new CalendarEditModalInputForm.CalendarGuestAccountInputForm();
		// 参加者
		// sample data
		List<GuestAccountSelectOption> guestAccountSelectOptionList = new ArrayList<GuestAccountSelectOption>();

		// ログインアカウント
		GuestAccountSelectOption loginAccount = new GuestAccountSelectOption();
		loginAccount.setGuestAccountSeq(SessionUtils.getLoginAccountSeq());
		loginAccount.setGuestAccountName(SessionUtils.getLoginAccountName());
		loginAccount.setInvitationTypeCd(CommonConstant.InvitationType.OWNER.getCd());
		loginAccount.setInvitationTypeVal(CommonConstant.InvitationType.OWNER.getVal());
		loginAccount.setAttendanceStatusCd(CommonConstant.AccountInvitationStatus.ATTEND.getCd());

		GuestAccountSelectOption guest1 = new GuestAccountSelectOption();
		guest1.setGuestAccountSeq(2L);
		guest1.setGuestAccountName("account name2");
		guest1.setInvitationTypeCd(CommonConstant.InvitationType.OPTIONAL.getCd());
		guest1.setInvitationTypeVal(CommonConstant.InvitationType.OPTIONAL.getVal());
		guest1.setAttendanceStatusCd(CommonConstant.AccountInvitationStatus.ATTEND.getCd());
		guest1.setGuestAccountComment("OK!");

		GuestAccountSelectOption guest2 = new GuestAccountSelectOption();
		guest2.setGuestAccountSeq(3L);
		guest2.setGuestAccountName("account name3");
		guest2.setInvitationTypeCd(CommonConstant.InvitationType.REQUIRED.getCd());
		guest2.setInvitationTypeVal(CommonConstant.InvitationType.REQUIRED.getVal());
		guest2.setAttendanceStatusCd(CommonConstant.AccountInvitationStatus.ABSENT.getCd());
		guest2.setGuestAccountComment("NG! sorry");

		GuestAccountSelectOption guest3 = new GuestAccountSelectOption();
		guest3.setGuestAccountSeq(4L);
		guest3.setGuestAccountName("account name4");
		guest3.setInvitationTypeCd(CommonConstant.InvitationType.REQUIRED.getCd());
		guest3.setInvitationTypeVal(CommonConstant.InvitationType.REQUIRED.getVal());
		guest3.setAttendanceStatusCd(CommonConstant.AccountInvitationStatus.UNDECIDED.getCd());
		guest3.setGuestAccountComment("");

		guestAccountSelectOptionList.add(loginAccount);
		guestAccountSelectOptionList.add(guest1);
		guestAccountSelectOptionList.add(guest2);
		guestAccountSelectOptionList.add(guest3);

		inputForm.setGuestAccountSelectOptionList(guestAccountSelectOptionList);

		return inputForm;
	}

	/**
	 * 【編集】予定編集フォーム（招待者）を取得する
	 * 
	 * @param viewScheduleSeq
	 * @return
	 * @throws AppException
	 */
	public CalendarEditModalInputForm.CalendarGuestAccountInputForm getCalendarGuestAccountInputForm(Long viewScheduleSeq) throws AppException {
		CalendarEditModalInputForm.CalendarGuestAccountInputForm inputForm = new CalendarEditModalInputForm.CalendarGuestAccountInputForm();

		return inputForm;
	}

	/**
	 * 予定を登録
	 * 
	 * @return
	 * @throws AppException
	 */
	public Long registCalendarSchedule(CalendarEditModalInputForm form) throws AppException {
		return null;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * DB整合性バリデーション
	 * 
	 * @param personId
	 * @param ankenId
	 * @throws AppException
	 */
	private void registCalendarDbValidate() throws AppException {
	}

}
