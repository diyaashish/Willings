package jp.loioz.app.user.personManagement.controller;

import java.time.LocalDate;
import java.util.Map;

import javax.validation.groups.Default;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.user.gyomuHistory.annitations.HasGyomuHistoryByCustomer;
import jp.loioz.app.user.personManagement.form.personEdit.PersonEditInputForm;
import jp.loioz.app.user.personManagement.form.personEdit.PersonEditViewForm;
import jp.loioz.app.user.personManagement.service.PersonEditService;
import jp.loioz.app.user.schedule.annotations.HasSchedule;
import jp.loioz.app.user.schedule.form.ScheduleDetail;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.ContactCategory;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.MessageLevel;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozObjectUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.groups.Hojin;
import jp.loioz.common.validation.groups.Kojin;
import jp.loioz.common.validation.groups.Lawyer;

/**
 * 名簿編集画面のコントローラークラス
 */
@Controller
@HasSchedule
@HasGyomuHistoryByCustomer
@RequestMapping(value = "user/personManagement/edit/{personId}")
public class PersonEditController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/personManagement/personEdit";

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyInputForm";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/******************************************************************
	 * サイドメニュー
	 ******************************************************************/

	/** 顧客案件メニューfragmentのパス */
	private static final String CUSTOMER_ANKENMENU_FRAGMENT_PATH = "common/customerAnkenMenu::menuList";

	/******************************************************************
	 * 基本情報
	 ******************************************************************/

	/** 名簿基本情報表示fragmentのパス */
	private static final String PERSON_BASIC_VIEW_FRAGMENT_PATH = "user/personManagement/personEditFragment::personBasicViewFragment";
	/** 名簿基本情報入力フォームfragmentのパス */
	private static final String PERSON_BASIC_INPUT_FRAGMENT_PATH = "user/personManagement/personEditFragment::personBasicInputFragment";

	/** 名簿基本情報の表示用フォームオブジェクト名 */
	private static final String PERSON_BASIC_VIEW_FORM_NAME = "personBasicViewForm";
	/** 名簿基本情報の入力用フォームオブジェクト名 */
	private static final String PERSON_BASIC_INPUT_FORM_NAME = "personBasicInputForm";

	/******************************************************************
	 * 基本情報
	 ******************************************************************/

	/** 名簿スケジュール一覧情報表示fragmentのパス */
	private static final String PERSON_SCHEDULE_LIST_VIEW_FRAGMENT_PATH = "user/personManagement/personEditFragment::personScheduleListViewFragment";
	/** 名簿スケジュール一覧情報表示用フォームオブジェクト名 */
	private static final String PERSON_SCHEDULE_LIST_VIEW_FORM_NAME = "personScheduleListViewForm";

	/******************************************************************
	 * 住所・旧住所
	 ******************************************************************/

	/** 住所情報表示fragmentのパス */
	private static final String PERSON_ADDRESS_VIEW_FRAGMENT_PATH = "user/personManagement/personEditFragment::personAddressViewFragment";
	/** 住所情報入力フォームfragmentのパス */
	private static final String PERSON_ADDRESS_INPUT_FRAGMENT_PATH = "user/personManagement/personEditFragment::personAddressInputFragment";

	/** 住所情報の表示用フォームオブジェクト名 */
	private static final String PERSON_ADDRESS_VIEW_FORM_NAME = "personAddressViewForm";
	/** 住所情報の入力用フォームオブジェクト名 */
	private static final String PERSON_ADDRESS_INPUT_FORM_NAME = "personAddressInputForm";

	/******************************************************************
	 * 案件情報
	 ******************************************************************/

	/** 案件情報表示fragmentのパス */
	private static final String ANKEN_VIEW_FRAGMENT_PATH = "user/personManagement/personEditFragment::customerAnkenViewFragment";

	/** 案件情報の表示用フォームオブジェクト名 */
	private static final String ANKEN_VIEW_FORM_NAME = "customerAnkenViewForm";

	/******************************************************************
	 * 関連する案件情報
	 ******************************************************************/

	/** 関連する案件情報表示fragmentのパス */
	private static final String RELATED_ANKEN_VIEW_FRAGMENT_PATH = "user/personManagement/personEditFragment::relatedAnkenViewFragment";

	/** 関連する案件情報の表示用フォームオブジェクト名 */
	private static final String RELATED_ANKEN_VIEW_FORM_NAME = "relatedAnkenViewForm";

	/******************************************************************
	 * 連絡先
	 ******************************************************************/

	/** 連絡先情報表示fragmentのパス */
	private static final String PERSON_CONTACT_VIEW_FRAGMENT_PATH = "user/personManagement/personEditFragment::personContactViewFragment";
	/** 連絡先情報入力フォームfragmentのパス */
	private static final String PERSON_CONTACT_INPUT_FRAGMENT_PATH = "user/personManagement/personEditFragment::personContactInputFragment";
	/** 電話連絡情報入力フォームfragmentのパス */
	private static final String PERSON_ALLOW_TYPE_INPUT_FRAGMENT_PATH = "user/personManagement/personEditFragment::personAllowTypeInputFragment";

	/** 連絡先情報の表示用フォームオブジェクト名 */
	private static final String PERSON_CONTACT_VIEW_FORM_NAME = "personContactViewForm";
	/** 連絡先情報の入力用フォームオブジェクト名 */
	private static final String PERSON_CONTACT_INPUT_FORM_NAME = "personContactInputForm";
	/** 電話連絡情報の入力用フォームオブジェクト名 */
	private static final String PERSON_ALLOW_TYPE_INPUT_FORM_NAME = "personAllowTypeInputForm";

	/******************************************************************
	 * 精算口座
	 ******************************************************************/

	/** 口座情報表示fragmentのパス */
	private static final String PERSON_KOZA_VIEW_FRAGMENT_PATH = "user/personManagement/personEditFragment::personKozaViewFragment";
	/** 口座情報入力フォームfragmentのパス */
	private static final String PERSON_KOZA_INPUT_FRAGMENT_PATH = "user/personManagement/personEditFragment::personKozaInputFragment";

	/** 口座情報の表示用フォームオブジェクト名 */
	private static final String PERSON_KOZA_VIEW_FORM_NAME = "personKozaViewForm";
	/** 口座情報の入力用フォームオブジェクト名 */
	private static final String PERSON_KOZA_INPUT_FORM_NAME = "personKozaInputForm";

	/** サービスクラス */
	@Autowired
	private PersonEditService service;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示<br>
	 * 基本情報、住所情報、連絡先情報、口座情報、案件情報、関与案件、予定データを取得しviewFormにセットします。<br>
	 * 各データ取得時に発生するExceptionはcatchせずシステムエラー画面を表示します。<br>
	 * （ブックマークからアクセスされることを考慮し排他エラーメッセージではなくシステムエラー画面を表示）
	 *
	 * @param personId 名簿ID
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable Long personId) {

		// 画面表示情報を取得
		PersonEditViewForm viewForm = service.createViewForm();

		// 名簿の基本情報表示フォーム ブックマークからのアクセスを考慮し楽観ロック処理をおこなわない
		PersonEditViewForm.PersonBasicViewForm basicViewForm = service.createPersonBasicViewForm(personId);
		viewForm.setPersonBasicViewForm(basicViewForm);

		// 名簿の住所情報表示フォーム ブックマークからのアクセスを考慮し楽観ロック処理をおこなわない
		PersonEditViewForm.PersonAddressViewForm addressViewForm = service.createPersonAddressViewForm(personId);
		viewForm.setPersonAddressViewForm(addressViewForm);

		// 名簿の連絡先情報表示フォーム ブックマークからのアクセスを考慮し楽観ロック処理をおこなわない
		PersonEditViewForm.PersonContactViewForm contactViewForm = service.createPersonContactViewForm(personId);
		viewForm.setPersonContactViewForm(contactViewForm);

		// 名簿の口座情報表示フォーム ブックマークからのアクセスを考慮し楽観ロック処理をおこなわない
		PersonEditViewForm.PersonKozaViewForm kozaViewForm = service.createPersonKozaViewForm(personId);
		viewForm.setPersonKozaViewForm(kozaViewForm);

		// 名簿の案件情報表示フォーム ブックマークからのアクセスを考慮し楽観ロック処理をおこなわない
		if (basicViewForm.getCustomerId() != null) {
			PersonEditViewForm.CustomerAnkenViewForm ankenViewForm = service.createCustomerAnkenViewForm(basicViewForm.getCustomerId().longValue(), CommonConstant.INCOMPLETE_ANKEN_LIST_FOR_CUSTOMERS);
			viewForm.setCustomerAnkenViewForm(ankenViewForm);
		}

		// 名簿の関連する案件情報表示フォーム ブックマークからのアクセスを考慮し楽観ロック処理をおこなわない
		if (basicViewForm.getCustomerId() != null) {
			PersonEditViewForm.RelatedAnkenViewForm relatedAnkenViewForm = service.createRelatedAnkenViewForm(basicViewForm.getCustomerId().longValue(), CommonConstant.INCOMPLETE_ANKEN_LIST_FOR_CUSTOMERS);
			viewForm.setRelatedAnkenViewForm(relatedAnkenViewForm);
		}

		// 名簿のスケジュール一覧表示フォーム
		boolean isAllTimeScheduleList = false;
		PersonEditViewForm.PersonScheduleListViewForm personScheduleListViewForm = service.createPersonScheduleListViewForm(personId, isAllTimeScheduleList);
		viewForm.setPersonScheduleListViewForm(personScheduleListViewForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * メッセージ表示をともなう初期表示画面へのリダイレクト処理
	 * 
	 * @param customerId
	 * @param levelCd メッセージレベル
	 * @param message メッセージ
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "redirectIndexWithMessage", method = RequestMethod.GET)
	public ModelAndView redirectIndexWithMessage(@PathVariable Long personId,
			@RequestParam("level") String levelCd, @RequestParam("message") String message,
			RedirectAttributes redirectAttributes) {

		// 自画面のindexメソッドへのリダイレクトオブジェクトを生成
		String redirectPath = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.index(personId));
		RedirectViewBuilder redirectViewBuilder = new RedirectViewBuilder(redirectAttributes, redirectPath);

		MessageLevel level = MessageLevel.of(levelCd);
		return redirectViewBuilder.build(MessageHolder.ofLevel(level, StringUtils.lineBreakStr2Code(message)));
	}

	// ▼ サイドメニュー

	/**
	 * 顧客案件のサイドメニュー部分を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "getCustomerAnkenMenuView", method = RequestMethod.GET)
	public ModelAndView getCustomerAnkenMenuView(@PathVariable Long personId) {

		ModelAndView mv = null;

		try {

			// サイドメニュー画面の取得
			mv = ModelAndViewUtils.getModelAndView(CUSTOMER_ANKENMENU_FRAGMENT_PATH);
			// パラメータ情報を設定
			mv.addObject("sideMenuCustomerId", personId);
			mv.addObject("selectedTabClass", "is_kokyaku");

		} catch (Exception ex) {
			// 上記のメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// メソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	// ▼ 基本情報部分

	/**
	 * 名簿の基本情報表示フォーム部分を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/getCustomerBasicView", method = RequestMethod.GET)
	public ModelAndView getCustomerBasicView(@PathVariable Long personId) {

		ModelAndView mv = null;

		try {

			// 名簿の基本情報表示フォームを設定した画面の取得
			PersonEditViewForm.PersonBasicViewForm basicViewForm = service.createPersonBasicViewForm(personId);
			mv = getMyModelAndView(basicViewForm, PERSON_BASIC_VIEW_FRAGMENT_PATH, PERSON_BASIC_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 名簿の基本情報入力フォーム部分を取得する
	 * 
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/getPersonBasicInputForm", method = RequestMethod.GET)
	public ModelAndView getPersonBasicInputForm(@PathVariable Long personId) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			PersonEditInputForm.PersonBasicInputForm basicInputForm = service.createPersonBasicInputForm(personId);
			mv = getMyModelAndView(basicInputForm, PERSON_BASIC_INPUT_FRAGMENT_PATH, PERSON_BASIC_INPUT_FORM_NAME);

		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 【個人】名簿の基本情報の保存
	 * 
	 * @param personId
	 * @param basicInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveKojinCustomerBasic", method = RequestMethod.POST)
	public ModelAndView saveKojinCustomerBasic(@PathVariable Long personId,
			@Validated({Default.class,
					Kojin.class}) @ModelAttribute(EMPTY_INPUT_FORM_NAME) PersonEditInputForm.PersonBasicInputForm basicInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		try {
			service.setDisplayData(basicInputForm, personId);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		// 画面独自のバリデーションチェック
		this.inputFormValidatedForSaveKojinCustomerBasic(basicInputForm, result);

		// DBアクセスバリデーション
		String errorMsg = this.accessDBValidatedForSaveCustomerBasic(basicInputForm, personId);
		if (StringUtils.isNotEmpty(errorMsg)) {
			// super.setAjaxProcResultFailure(errorMsg);
			result.rejectValue("customerType", null, errorMsg);
		}

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 入力フォームにerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(basicInputForm, PERSON_BASIC_INPUT_FRAGMENT_PATH, PERSON_BASIC_INPUT_FORM_NAME, result);
		}

		try {

			// 保存処理
			service.saveKojinPersonBasic(personId, basicInputForm);

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00013));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getCustomerBasicView(personId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "名簿情報"));
		return mv;
	}

	/**
	 * 【法人】名簿の基本情報の保存
	 * 
	 * @param personId
	 * @param basicInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveHojinCustomerBasic", method = RequestMethod.POST)
	public ModelAndView saveHojinCustomerBasic(@PathVariable Long personId,
			@Validated({Default.class,
					Hojin.class}) @ModelAttribute(EMPTY_INPUT_FORM_NAME) PersonEditInputForm.PersonBasicInputForm basicInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		try {
			service.setDisplayData(basicInputForm, personId);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		// 画面独自のバリデーションチェック
		this.inputFormValidatedForSaveHojinCustomerBasic(basicInputForm);

		// DBアクセスバリデーション
		String errorMsg = this.accessDBValidatedForSaveCustomerBasic(basicInputForm, personId);
		if (StringUtils.isNotEmpty(errorMsg)) {
			// super.setAjaxProcResultFailure(errorMsg);
			result.rejectValue("customerType", null, errorMsg);
		}

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(basicInputForm, PERSON_BASIC_INPUT_FRAGMENT_PATH, PERSON_BASIC_INPUT_FORM_NAME, result);
		}

		try {

			// 保存処理
			service.saveHojinPersonBasic(personId, basicInputForm);

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00013));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getCustomerBasicView(personId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "名簿情報"));
		return mv;
	}

	/**
	 * 【弁護士】名簿の基本情報の保存
	 * 
	 * @param personId
	 * @param basicInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveLawyerCustomerBasic", method = RequestMethod.POST)
	public ModelAndView saveLawyerCustomerBasic(@PathVariable Long personId,
			@Validated({Default.class,
					Lawyer.class}) @ModelAttribute(EMPTY_INPUT_FORM_NAME) PersonEditInputForm.PersonBasicInputForm basicInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		try {
			service.setDisplayData(basicInputForm, personId);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		// DBアクセスバリデーション
		String errorMsg = this.accessDBValidatedForSaveCustomerBasic(basicInputForm, personId);
		if (StringUtils.isNotEmpty(errorMsg)) {
			// super.setAjaxProcResultFailure(errorMsg);
			result.rejectValue("customerType", null, errorMsg);
		}

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 入力フォームにerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(basicInputForm, PERSON_BASIC_INPUT_FRAGMENT_PATH, PERSON_BASIC_INPUT_FORM_NAME, result);
		}

		try {

			// 保存処理
			service.saveLawyerPersonBasic(personId, basicInputForm);

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00013));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getCustomerBasicView(personId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "名簿情報"));
		return mv;
	}

	// ▼ 住所情報部分

	/**
	 * 名簿の住所情報入力フォーム部分を取得する
	 * 
	 * @param customerId
	 * @param isNewRegist 新規登録用の入力フォームかどうか
	 * @param isBaseAddress 居住地かどうか
	 * @param isTourokuAddress 登録地（住民票登録地/登記簿登録地）かどうか
	 * @param isTransferAddress 郵送先住所かどうか
	 * @param oldAddressSeq 旧住所SEQ
	 * @param viewFormCustomerTypeCd 画面表示時の顧客種別コード
	 * @return
	 */
	@RequestMapping(value = "/getPersonAddressInputForm", method = RequestMethod.GET)
	public ModelAndView getPersonAddressInputForm(@PathVariable Long personId, boolean isNewRegist, boolean isBaseAddress,
			boolean isTourokuAddress, boolean isTransferAddress, Long oldAddressSeq, String viewFormCustomerTypeCd) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			PersonEditInputForm.PersonAddressInputForm addressInputForm = service.createPersonAddressInputForm(personId, isNewRegist,
					isBaseAddress, isTourokuAddress, isTransferAddress, oldAddressSeq, viewFormCustomerTypeCd);
			mv = getMyModelAndView(addressInputForm, PERSON_ADDRESS_INPUT_FRAGMENT_PATH, PERSON_ADDRESS_INPUT_FORM_NAME);

		} catch (DataNotFoundException ex) {

			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (AppException ae) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (Exception ex) {
			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 名簿の住所情報表示フォーム部分を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/getCustomerAddressView", method = RequestMethod.GET)
	public ModelAndView getCustomerAddressView(@PathVariable Long personId) {

		ModelAndView mv = null;

		try {
			// 名簿の住所情報表示フォームを設定した画面の取得
			PersonEditViewForm.PersonAddressViewForm addressViewForm = service.createPersonAddressViewForm(personId);
			mv = getMyModelAndView(addressViewForm, PERSON_ADDRESS_VIEW_FRAGMENT_PATH, PERSON_ADDRESS_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {

			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 住所情報表示フォームを取得し表示する。（住所入力フォーム変更時用）<br>
	 * 顧客種別は画面表示時のものを使用する（ 顧客種別はDBに登録してある最新値ではなく画面表示自の状態をFormで持つため）<br>
	 * 
	 * @param personId
	 * @param customerTypeCd 顧客種別コード（画面表示時の）
	 * @return
	 */
	@RequestMapping(value = "/renderAddressViewWhenInputAddress", method = RequestMethod.GET)
	public ModelAndView renderAddressViewWhenInputAddress(@PathVariable Long personId, @RequestParam(name = "customerTypeCd") String customerTypeCd) {

		ModelAndView mv = null;

		try {
			// 名簿の住所情報表示フォームを設定した画面の取得
			PersonEditViewForm.PersonAddressViewForm addressViewForm = service.createPersonAddressViewForm(personId);
			addressViewForm.setCustomerType(CustomerType.of(customerTypeCd));
			mv = getMyModelAndView(addressViewForm, PERSON_ADDRESS_VIEW_FRAGMENT_PATH, PERSON_ADDRESS_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {

			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 名簿の住所情報の保存
	 * 
	 * @param personId
	 * @param addressInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveCustomerAddress", method = RequestMethod.POST)
	public ModelAndView saveCustomerAddress(@PathVariable Long personId,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) PersonEditInputForm.PersonAddressInputForm addressInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		try {
			service.setDisplayData(addressInputForm, personId);
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(addressInputForm, PERSON_ADDRESS_INPUT_FRAGMENT_PATH, PERSON_ADDRESS_INPUT_FORM_NAME, result);
		}

		String errorMsg = null;

		// 画面独自のフォームバリデーション
		errorMsg = this.inputFormValidatedForSaveCustomerAddress(addressInputForm);
		if (StringUtils.isNotEmpty(errorMsg)) {
			super.setAjaxProcResultFailure(errorMsg);
			// 画面の再レンダリングは必要ないためなにも返さない
			return null;
		}

		// DBアクセスバリデーション
		errorMsg = this.accessDBValidatedForSaveCustomerAddress(addressInputForm, personId);
		if (StringUtils.isNotEmpty(errorMsg)) {
			super.setAjaxProcResultFailure(errorMsg);
			// 画面の再レンダリングは必要ないためなにも返さない
			return null;
		}

		try {

			// 保存処理
			service.savePersonAddress(personId, addressInputForm);

		} catch (DataNotFoundException ex) {

			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00013));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getCustomerAddressView(personId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "住所情報"));
		return mv;
	}

	/**
	 * 名簿の住所情報の削除
	 * 
	 * @param personId 名簿ID
	 * @param isBaseAddress
	 * @param isTourokuAddress
	 * @param isTransferAddress
	 * @param oldAddressSeq
	 * @param viewFormCustomerTypeCd
	 * 
	 * @return
	 */
	@RequestMapping(value = "/deleteAddress", method = RequestMethod.POST)
	public ModelAndView deleteAddress(@PathVariable Long personId,
			@RequestParam(name = "isBaseAddress") Boolean isBaseAddress,
			@RequestParam(name = "isTourokuAddress") Boolean isTourokuAddress,
			@RequestParam(name = "isTransferAddress") Boolean isTransferAddress,
			@RequestParam(name = "oldAddressSeq", required = false) Long oldAddressSeq,
			@RequestParam(name = "viewFormCustomerTypeCd", required = false) String viewFormCustomerTypeCd) {

		ModelAndView mv = null;

		// バリデーションエラーのチェックは無し

		// 画面独自のフォームバリデーションは無し

		// DBアクセスバリデーション
		String errorMsg = this.accessDBValidatedForDeleteCustomerAddress(viewFormCustomerTypeCd, personId);
		if (StringUtils.isNotEmpty(errorMsg)) {
			super.setAjaxProcResultFailure(errorMsg);
			// 画面の再レンダリングは必要ないためなにも返さない
			return null;
		}

		PersonEditInputForm.PersonAddressInputForm addressInputForm = new PersonEditInputForm.PersonAddressInputForm();

		// 新規登録かどうか
		addressInputForm.setNewRegist(false);
		// 居住地/所在地データかどうか
		addressInputForm.setBaseAddress(isBaseAddress);
		// 登録地（住民票登録地/登記簿登録地）データかどうか
		addressInputForm.setTourokuAddress(isTourokuAddress);
		// もともと登録地（住民票登録地/登記簿登録地）データだったかどうか
		addressInputForm.setTourokuAddressBeforeUpdate(isTourokuAddress);
		// 郵送先住所データかどうか
		addressInputForm.setTransferAddress(isTransferAddress);
		// もともと郵送先住所データだったかどうか
		addressInputForm.setTransferAddressBeforeUpdate(isTransferAddress);
		// 旧住所データ連番
		addressInputForm.setOldAddressSeq(oldAddressSeq);
		// 住所データ（空）
		addressInputForm.setAddress(PersonEditInputForm.Address.builder().build());

		try {

			// 削除処理(空である住所に関連する項目で更新）
			service.savePersonAddress(personId, addressInputForm);

		} catch (DataNotFoundException ex) {

			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00014));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getCustomerAddressView(personId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "住所情報"));
		return mv;
	}

	// ▼ 連絡先情報部分

	/**
	 * 名簿の連絡先情報入力フォーム部分を取得する
	 * 
	 * @param personId
	 * @param category 連絡先カテゴリ（電話、FAXなど）
	 * @param contactSeq 連絡先データ連番（連絡先テーブルのSEQ）
	 * @return
	 */
	@RequestMapping(value = "/getPersonContactInputForm", method = RequestMethod.GET)
	public ModelAndView getPersonContactInputForm(@PathVariable Long personId, ContactCategory category, Long contactSeq) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			PersonEditInputForm.PersonContactInputForm contactInputForm = service.createPersonContactInputForm(personId, category,
					contactSeq);
			mv = getMyModelAndView(contactInputForm, PERSON_CONTACT_INPUT_FRAGMENT_PATH, PERSON_CONTACT_INPUT_FORM_NAME);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 名簿の連絡先情報表示フォーム部分を取得する
	 * 
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/getCustomerContactView", method = RequestMethod.GET)
	public ModelAndView getCustomerContactView(@PathVariable Long personId) {

		ModelAndView mv = null;

		try {

			// 名簿の住所情報表示フォームを設定した画面の取得
			PersonEditViewForm.PersonContactViewForm contactViewForm = service.createPersonContactViewForm(personId);
			mv = getMyModelAndView(contactViewForm, PERSON_CONTACT_VIEW_FRAGMENT_PATH, PERSON_CONTACT_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {

			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 名簿の連絡先情報の保存
	 * 
	 * @param personId
	 * @param addressInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/savePersonContact", method = RequestMethod.POST)
	public ModelAndView savePersonContact(@PathVariable Long personId,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) PersonEditInputForm.PersonContactInputForm contactInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		service.setDisplayData(contactInputForm);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(contactInputForm, PERSON_CONTACT_INPUT_FRAGMENT_PATH, PERSON_CONTACT_INPUT_FORM_NAME, result);
		}

		String errorMsg = null;

		// 画面独自のフォームバリデーション
		errorMsg = this.inputFormValidatedForSavePersonContact(contactInputForm);
		if (StringUtils.isNotEmpty(errorMsg)) {
			super.setAjaxProcResultFailure(errorMsg);
			// 画面の再レンダリングは必要ないためなにも返さない
			return null;
		}

		// DBアクセスバリデーション
		errorMsg = this.accessDBValidatedForSavePersonContact(contactInputForm, personId);
		if (StringUtils.isNotEmpty(errorMsg)) {
			super.setAjaxProcResultFailure(errorMsg);
			// 画面の再レンダリングは必要ないためなにも返さない
			return null;
		}

		try {

			// 保存処理
			service.saveCustomerContact(personId, contactInputForm);

		} catch (DataNotFoundException ex) {

			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00013));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getCustomerContactView(personId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "連絡先情報"));
		return mv;
	}

	/**
	 * 名簿の連絡先情報の削除
	 * 
	 * @param categoryCd 連絡先区分コード
	 * @param contactSeqVal 連絡先SEQ
	 * @return
	 */
	@RequestMapping(value = "/deleteContact", method = RequestMethod.POST)
	public ModelAndView deleteContact(@PathVariable Long personId,
			@RequestParam(name = "categoryCd") Long categoryCd,
			@RequestParam(name = "contactSeq") Long contactSeq) {

		ModelAndView mv = null;

		try {

			// 削除処理
			service.deleteContact(categoryCd, contactSeq);

		} catch (DataNotFoundException ex) {

			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00014));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getCustomerContactView(personId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "連絡先情報"));
		return mv;
	}

	/**
	 * 名簿の電話連絡情報の保存
	 * 
	 * @param personId
	 * @param allowTypeInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/savePersonAllowType", method = RequestMethod.POST)
	public ModelAndView savePersonAllowType(@PathVariable Long personId,
			@Validated PersonEditInputForm.PersonAllowTypeInputForm allowTypeInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		service.setDisplayData(allowTypeInputForm);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(allowTypeInputForm, PERSON_ALLOW_TYPE_INPUT_FRAGMENT_PATH, PERSON_ALLOW_TYPE_INPUT_FORM_NAME, result);
		}

		try {

			// 保存処理
			service.saveCustomerContact(personId, allowTypeInputForm);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00013));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getCustomerContactView(personId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "電話連絡情報"));
		return mv;
	}

	/**
	 * 名簿の電話連絡情報の削除
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/deleteAllowType", method = RequestMethod.POST)
	public ModelAndView deleteAllowType(@PathVariable Long personId) {

		ModelAndView mv = null;

		try {

			// 削除処理
			service.deleteAllowType(personId);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00014));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getCustomerContactView(personId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "電話連絡情報"));
		return mv;
	}

	/**
	 * 名簿の電話連絡情報入力フォーム部分を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/getPersonAllowTypeInputForm", method = RequestMethod.GET)
	public ModelAndView getPersonAllowTypeInputForm(@PathVariable Long personId) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			PersonEditInputForm.PersonAllowTypeInputForm allowTypeInputForm = service.createPersonAllowTypeInputForm(personId);
			mv = getMyModelAndView(allowTypeInputForm, PERSON_ALLOW_TYPE_INPUT_FRAGMENT_PATH, PERSON_ALLOW_TYPE_INPUT_FORM_NAME);
		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 名簿の連絡先情報入力フォーム部分を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/editAllowType", method = RequestMethod.GET)
	public ModelAndView editAllowType(@PathVariable Long personId) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			PersonEditInputForm.PersonAllowTypeInputForm allowTypeInputForm = service.createPersonAllowTypeInputForm(personId);
			mv = getMyModelAndView(allowTypeInputForm, PERSON_ALLOW_TYPE_INPUT_FRAGMENT_PATH, PERSON_ALLOW_TYPE_INPUT_FORM_NAME);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	// ▼ 口座情報部分

	/**
	 * 名簿の口座情報入力フォーム部分を取得する
	 * 
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/getPersonKozaInputForm", method = RequestMethod.GET)
	public ModelAndView getPersonKozaInputForm(@PathVariable Long personId, boolean isNewRegist) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			PersonEditInputForm.PersonKozaInputForm kozaInputForm = service.createPersonKozaInputForm(personId, isNewRegist);
			mv = getMyModelAndView(kozaInputForm, PERSON_KOZA_INPUT_FRAGMENT_PATH, PERSON_KOZA_INPUT_FORM_NAME);
		} catch (DataNotFoundException ex) {

			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		} catch (AppException ae) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ae.getErrorType()));
			return null;
		} catch (Exception ex) {

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 名簿の口座情報表示フォーム部分を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/getCustomerKozaView", method = RequestMethod.GET)
	public ModelAndView getCustomerKozaView(@PathVariable Long personId) {

		ModelAndView mv = null;

		try {

			// 名簿の住所情報表示フォームを設定した画面の取得
			PersonEditViewForm.PersonKozaViewForm kozaViewForm = service.createPersonKozaViewForm(personId);
			mv = getMyModelAndView(kozaViewForm, PERSON_KOZA_VIEW_FRAGMENT_PATH, PERSON_KOZA_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {

			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (Exception ex) {
			// 上記のserviceのメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// serviceのメソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 名簿の口座情報の保存
	 * 
	 * @param personId
	 * @param kozaInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/savePersonKoza", method = RequestMethod.POST)
	public ModelAndView savePersonKoza(@PathVariable Long personId,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) PersonEditInputForm.PersonKozaInputForm kozaInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		service.setDisplayData(kozaInputForm, personId);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(kozaInputForm, PERSON_KOZA_INPUT_FRAGMENT_PATH, PERSON_KOZA_INPUT_FORM_NAME, result);
		}

		String errorMsg = null;

		// 画面独自のフォームバリデーション
		errorMsg = this.inputFormValidatedForSavePersonKoza(kozaInputForm);
		if (StringUtils.isNotEmpty(errorMsg)) {
			super.setAjaxProcResultFailure(errorMsg);
			// 画面の再レンダリングは必要ないためなにも返さない
			return null;
		}

		// DBアクセスバリデーションは現状特になし

		try {

			// 保存処理
			service.savePersonKoza(personId, kozaInputForm);

		} catch (DataNotFoundException ex) {

			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00013));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getCustomerKozaView(personId);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "精算口座情報"));
		return mv;
	}

	/**
	 * 名簿の口座情報の削除
	 * 
	 * @param customerId 名簿ID
	 * @return
	 */
	@RequestMapping(value = "/deleteKoza", method = RequestMethod.GET)
	public ModelAndView deleteKoza(@PathVariable Long personId) {

		ModelAndView mv = null;

		// バリデーションは現状特になし

		// 画面独自のフォームバリデーションは現状特になし

		// DBアクセスバリデーションは現状特になし

		try {

			// 削除処理
			service.deleteKoza(personId);

		} catch (DataNotFoundException ex) {

			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00014));
			logger.error("error", ex);

			return null;
		}

		// 削除成功後は、表示画面を返却する
		mv = this.getCustomerKozaView(personId);
		if (mv == null) {
			// 削除は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "精算口座情報"));
		return mv;
	}

	/**
	 * 名簿の案件情報表示フォーム部分を取得する
	 * 
	 * @param personId
	 * @param isAllAnkenList
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/getCustomerAnkenView", method = RequestMethod.GET)
	public ModelAndView getCustomerAnkenView(@PathVariable Long personId,
			@RequestParam boolean isAllAnkenList,
			@RequestParam Long customerId) {

		ModelAndView mv = null;

		// 名簿の案件情報表示フォームを設定した画面の取得
		PersonEditViewForm.CustomerAnkenViewForm viewForm = service.createCustomerAnkenViewForm(customerId, isAllAnkenList);
		mv = getMyModelAndView(viewForm, ANKEN_VIEW_FRAGMENT_PATH, ANKEN_VIEW_FORM_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 名簿の関連案件情報表示フォーム部分を取得する
	 * 
	 * @param personId
	 * @param isAllAnkenList
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/getRelatedAnkenView", method = RequestMethod.GET)
	public ModelAndView getRelatedAnkenView(@PathVariable Long personId,
			@RequestParam boolean isAllAnkenList,
			@RequestParam Long customerId) {

		ModelAndView mv = null;

		// 名簿の関連案件情報表示フォームを設定した画面の取得
		PersonEditViewForm.RelatedAnkenViewForm viewForm = service.createRelatedAnkenViewForm(customerId, isAllAnkenList);
		mv = getMyModelAndView(viewForm, RELATED_ANKEN_VIEW_FRAGMENT_PATH, RELATED_ANKEN_VIEW_FORM_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 名簿のスケジュール情報表示フォーム部分を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/getPersonScheduleListView", method = RequestMethod.GET)
	public ModelAndView getPersonScheduleListView(@PathVariable Long personId, @RequestParam boolean isAllTimeScheduleList) {

		ModelAndView mv = null;

		try {

			// 名簿の予定一覧情報表示フォームを設定した画面の取得
			PersonEditViewForm.PersonScheduleListViewForm viewForm = service.createPersonScheduleListViewForm(personId, isAllTimeScheduleList);
			mv = getMyModelAndView(viewForm, PERSON_SCHEDULE_LIST_VIEW_FRAGMENT_PATH, PERSON_SCHEDULE_LIST_VIEW_FORM_NAME);

		} catch (DataNotFoundException ex) {

			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;

		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 名簿編集画面：予定情報の詳細モーダルデータを取得
	 * 
	 * ThymeleafのモデルデータのJson化は予定データの日付フォーマット処理が考慮されていないため、
	 * 名簿画面のスケジュール一覧レンダリング直後にAjaxにより取得する
	 * 
	 * @param personId
	 * @param isAllTimeScheduleList
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getPersonScheduleDetails", method = RequestMethod.GET)
	public Map<Long, ScheduleDetail> getPersonScheduleDetails(@PathVariable Long personId, @RequestParam boolean isAllTimeScheduleList) {

		try {
			// 予定詳細情報を取得
			Map<Long, ScheduleDetail> scheduleDetails = service.getPersonScheduleDetails(personId, isAllTimeScheduleList);
			super.setAjaxProcResultSuccess();
			return scheduleDetails;

		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return null;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	// ▼ 基本情報部分

	/**
	 * 画面独自のバリデーションチェック（個人の名簿基本情報の保存処理用）
	 * 
	 * @param basicInputForm
	 * @param result
	 */
	private void inputFormValidatedForSaveKojinCustomerBasic(PersonEditInputForm.PersonBasicInputForm basicInputForm, BindingResult result) {

		// 生年月日入力エラーメッセージ
		String localDateTimePatternDefaultMessage = getMessage(MessageEnum.MSG_E00102.getMessageKey());
		String notExistDateErrorMessage = getMessage(MessageEnum.MSG_E00167.getMessageKey());

		// 生年月日が正しい日付か確認する
		EraType birthEra = basicInputForm.getBirthEra();
		Long birthYear = basicInputForm.getBirthYear();
		Long birthMonth = basicInputForm.getBirthMonth();
		Long birthDay = basicInputForm.getBirthDay();

		LocalDate birthDate = null;
		if (LoiozObjectUtils.allNotNull(birthEra, birthYear, birthMonth, birthDay)) {
			try {
				birthDate = DateUtils.parseToLocalDate(birthEra.getCd(), birthYear, birthMonth, birthDay);
				// 日付が生成できない場合は不正と判断して年号にエラーを付与する
				if (birthDate == null) {
					result.rejectValue("birthEra", null, notExistDateErrorMessage);
				}
			} catch (Exception e) {
				// 変換エラーが起きた場合は不正と判断して年号にエラーを付与する
				result.rejectValue("birthEra", null, notExistDateErrorMessage);
			}
		} else if (!CommonUtils.allNull(birthYear, birthMonth, birthDay)) {
			// 年月日がすべて未選択でない場合
			result.rejectValue("birthEra", null, localDateTimePatternDefaultMessage);
		}

		// 故人フラグのチェックが有るときに死亡日が正しい日付か確認する
		LocalDate deathDate = null;
		if (basicInputForm.getDeathFlg()) {
			// 死亡日入力エラーメッセージ
			String deathDateInputErrorMessage = getMessage(MessageEnum.MSG_E00166.getMessageKey());

			EraType deathEra = basicInputForm.getDeathEra();
			Long deathYear = basicInputForm.getDeathYear();
			Long deathMonth = basicInputForm.getDeathMonth();
			Long deathDay = basicInputForm.getDeathDay();

			if (LoiozObjectUtils.allNotNull(deathEra, deathYear, deathMonth, deathDay)) {
				try {
					deathDate = DateUtils.parseToLocalDate(deathEra.getCd(), deathYear, deathMonth, deathDay);
					// 日付が生成できない場合は不正と判断して年号にエラーを付与する
					if (deathDate == null) {
						result.rejectValue("deathEra", null, notExistDateErrorMessage);
					}
				} catch (Exception e) {
					// 変換エラーが起きた場合は不正と判断して年号にエラーを付与する
					result.rejectValue("deathEra", null, notExistDateErrorMessage);
				}
			} else if (!CommonUtils.allNull(deathYear, deathMonth, deathDay)) {
				// 年月日で未選択の項目がある場合
				result.rejectValue("deathEra", null, deathDateInputErrorMessage);
			}
		}

		// 故人フラグのチェック、生年月日、死亡日が有るときに死亡日が生年月日以前でないことをチェックする
		if (basicInputForm.getDeathFlg() && birthDate != null && deathDate != null) {
			// 死亡日不正入力エラーメッセージ
			String deathDateInputErrorMessage = getMessage(MessageEnum.MSG_E00171.getMessageKey());

			if (!deathDate.isAfter(birthDate) && !deathDate.isEqual(birthDate)) {
				result.rejectValue("deathEra", null, deathDateInputErrorMessage);
			}
		}

	}

	/**
	 * 画面独自のバリデーションチェック（法人の名簿基本情報の保存処理用）
	 * 
	 * @param basicInputForm
	 */
	private void inputFormValidatedForSaveHojinCustomerBasic(PersonEditInputForm.PersonBasicInputForm basicInputForm) {

		// 法人の登録ではブランクとする
		basicInputForm.setCustomerNameMei(CommonConstant.BLANK);
		basicInputForm.setCustomerNameMeiKana(CommonConstant.BLANK);

		// バリデーションチェックは現状ない
	}

	/**
	 * DBアクセスが必要なバリデーションチェック（基本情報の保存処理用）
	 * 
	 * @param addressInputForm
	 * @param personId
	 * @return
	 */
	private String accessDBValidatedForSaveCustomerBasic(PersonEditInputForm.PersonBasicInputForm basicInputForm, Long personId) {

		try {
			service.accessDBValidatedForSaveCustomerBasic(basicInputForm, personId);
		} catch (AppException ex) {
			// エラーメッセージを返却
			return getMessage(ex.getErrorType());
		}

		return null;
	}

	// ▼ 住所情報部分

	/**
	 * 入力フォームバリデーション（住所情報の保存処理用）
	 * 
	 * @param addressInputForm
	 * @return
	 */
	private String inputFormValidatedForSaveCustomerAddress(PersonEditInputForm.PersonAddressInputForm addressInputForm) {

		String errorMsg = null;

		if (addressInputForm.isNewRegist()) {
			// 新規登録の場合

			PersonEditInputForm.Address address = addressInputForm.getAddress();
			if (address.isEmpty()) {
				// 住所情報未入力の場合
				errorMsg = getMessage(MessageEnum.MSG_E00097);
				return errorMsg;
			}
		}

		return errorMsg;
	}

	/**
	 * DBアクセスが必要なバリデーションチェック（住所情報の保存処理用）
	 * 
	 * @param addressInputForm
	 * @param personId
	 * @return
	 */
	private String accessDBValidatedForSaveCustomerAddress(PersonEditInputForm.PersonAddressInputForm addressInputForm, Long personId) {

		try {
			service.accessDBValidatedForSavePersonAddress(addressInputForm, personId);
		} catch (AppException ex) {
			// エラーメッセージを返却
			return getMessage(ex.getErrorType());
		}

		return null;
	}

	/**
	 * DBアクセスが必要なバリデーションチェック（住所情報の削除処理用）
	 * 
	 * @param customerTypeCd
	 * @param personId
	 * @return
	 */
	private String accessDBValidatedForDeleteCustomerAddress(String customerTypeCd, Long personId) {

		try {
			service.accessDBValidatedForDeletePersonAddress(customerTypeCd, personId);
		} catch (AppException ex) {
			// エラーメッセージを返却
			return getMessage(ex.getErrorType());
		}

		return null;
	}

	// ▼ 連絡先情報部分

	/**
	 * 入力フォームバリデーション（連絡先情報の保存処理用）
	 * 
	 * @param addressInputForm
	 * @return
	 */
	private String inputFormValidatedForSavePersonContact(PersonEditInputForm.PersonContactInputForm contactInputForm) {

		String errorMsg = null;

		if (contactInputForm.getContactSeq() == null) {
			// 新規登録の場合

			if (contactInputForm.isEmpty()) {
				// 連絡先情報未入力の場合
				errorMsg = getMessage(MessageEnum.MSG_E00098);
				return errorMsg;
			}
		}

		return errorMsg;
	}

	/**
	 * DBアクセスが必要なバリデーションチェック（連絡先情報の保存処理用）
	 * 
	 * @param contactInputForm
	 * @param personId
	 * @return
	 */
	private String accessDBValidatedForSavePersonContact(PersonEditInputForm.PersonContactInputForm contactInputForm, Long personId) {

		try {
			service.accessDBValidatedForSaveCustomerContact(contactInputForm, personId);
		} catch (AppException ex) {
			// エラーメッセージを返却
			return getMessage(ex.getErrorType());
		}

		return null;
	}

	// ▼ 口座情報部分

	/**
	 * 入力フォームバリデーション（口座情報の保存処理用）
	 * 
	 * @param addressInputForm
	 * @return
	 */
	private String inputFormValidatedForSavePersonKoza(PersonEditInputForm.PersonKozaInputForm kozaInputForm) {

		String errorMsg = null;

		if (kozaInputForm.isNewRegist()) {
			// 新規登録の場合

			if (kozaInputForm.isEmpty()) {
				// 口座情報未入力の場合
				errorMsg = getMessage(MessageEnum.MSG_E00100);
				return errorMsg;
			}
		}

		return errorMsg;
	}
}
