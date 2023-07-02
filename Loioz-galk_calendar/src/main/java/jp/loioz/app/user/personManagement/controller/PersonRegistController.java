package jp.loioz.app.user.personManagement.controller;

import java.time.LocalDate;

import javax.validation.groups.Default;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.user.personManagement.form.personEdit.PersonRegistInputForm;
import jp.loioz.app.user.personManagement.service.PersonRegistService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozObjectUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.validation.groups.Hojin;
import jp.loioz.common.validation.groups.Kojin;

/**
 * 名簿新規登録画面のcontrollerクラス
 *
 */
@Controller
@RequestMapping(value = "user/personManagement/new")
public class PersonRegistController extends DefaultController {

	/** 新規登録に対応するviewパス */
	private static final String MY_VIEW_PATH = "user/personManagement/personRegist";

	/** 名簿基本情報入力フォームfragmentのパス */
	private static final String PERSON_REGIST_BASIC_INPUT_FRAGMENT_PATH = "user/personManagement/personRegistFragment::personRegistBasicInputFragment";

	/** 名簿基本情報の入力用フォームオブジェクト名 */
	private static final String PERSON_REGIST_BASIC_INPUT_FORM_NAME = "personRegistBasicInputForm";

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyInputForm";

	/** 新規登録のserviceクラス */
	@Autowired
	private PersonRegistService service;

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
	 * 初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {

		PersonRegistInputForm inputform = new PersonRegistInputForm();

		// 基本情報フォームを設定
		PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm = service.createNewPersonBasicInputForm();
		inputform.setPersonRegistBasicInputForm(basicInputForm);

		ModelAndView mv = ModelAndViewUtils.getModelAndView(MY_VIEW_PATH);
		// フォームオブジェクトを設定
		mv.addObject("personRegistBasicInputForm", basicInputForm);

		return mv;
	}

	/**
	 * 名簿（個人）登録の初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/addKojin", method = RequestMethod.GET)
	public ModelAndView addKojin() {

		PersonRegistInputForm inputform = new PersonRegistInputForm();

		// 基本情報フォームを設定
		PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm = service.createNewPersonBasicInputForm();
		// 顧客区分に「個人」を設定
		basicInputForm.setCustomerType(CustomerType.KOJIN);
		inputform.setPersonRegistBasicInputForm(basicInputForm);

		ModelAndView mv = ModelAndViewUtils.getModelAndView(MY_VIEW_PATH);
		// フォームオブジェクトを設定
		mv.addObject("personRegistBasicInputForm", basicInputForm);

		return mv;
	}

	/**
	 * 名簿（企業・団体）登録の初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/addHojin", method = RequestMethod.GET)
	public ModelAndView addHojin() {

		PersonRegistInputForm inputform = new PersonRegistInputForm();

		// 基本情報フォームを設定
		PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm = service.createNewPersonBasicInputForm();
		// 顧客区分に「企業・団体」を設定
		basicInputForm.setCustomerType(CustomerType.HOJIN);
		inputform.setPersonRegistBasicInputForm(basicInputForm);

		ModelAndView mv = ModelAndViewUtils.getModelAndView(MY_VIEW_PATH);
		// フォームオブジェクトを設定
		mv.addObject("personRegistBasicInputForm", basicInputForm);

		return mv;
	}

	/**
	 * 初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/addLawyer", method = RequestMethod.GET)
	public ModelAndView addLawyer() {

		PersonRegistInputForm inputform = new PersonRegistInputForm();

		// 基本情報フォームを設定
		PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm = service.createNewPersonBasicInputForm();
		// 顧客区分に「弁護士」を設定
		basicInputForm.setCustomerType(CustomerType.LAWYER);
		inputform.setPersonRegistBasicInputForm(basicInputForm);

		ModelAndView mv = ModelAndViewUtils.getModelAndView(MY_VIEW_PATH);
		// フォームオブジェクトを設定
		mv.addObject("personRegistBasicInputForm", basicInputForm);

		return mv;
	}

	/**
	 * 【個人】名簿基本情報の登録
	 * 
	 * @param basicInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKojinPersonBasic", method = RequestMethod.POST)
	public ModelAndView registKojinPersonBasic(
			@Validated({Default.class,
					Kojin.class}) @ModelAttribute(EMPTY_INPUT_FORM_NAME) PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		ModelAndView mv = null;

		// 表示用データの設定
		service.setDisplayData(basicInputForm);

		// 画面独自のバリデーションチェック
		this.inputFormValidatedForSaveKojinCustomerBasic(basicInputForm, result);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 入力フォームにerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(basicInputForm, PERSON_REGIST_BASIC_INPUT_FRAGMENT_PATH, PERSON_REGIST_BASIC_INPUT_FORM_NAME,
					result);
		}

		try {

			// 登録処理
			service.registPersonBasic(basicInputForm);

			// 保存終了後、レスポンスを受けるajax側で名簿編集画面へ遷移するために、personIdが必要なので、
			// personId取得のために、画面情報を返却する
			mv = getMyModelAndView(basicInputForm, PERSON_REGIST_BASIC_INPUT_FRAGMENT_PATH, PERSON_REGIST_BASIC_INPUT_FORM_NAME);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00012));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "名簿情報"));
		return mv;
	}

	/**
	 * 【企業・団体】名簿基本情報の登録
	 * 
	 * @param basicInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registHojinPersonBasic", method = RequestMethod.POST)
	public ModelAndView registHojinPersonBasic(
			@Validated({Default.class,
					Hojin.class}) @ModelAttribute(EMPTY_INPUT_FORM_NAME) PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		service.setDisplayData(basicInputForm);

		// 画面独自のバリデーションチェック
		this.inputFormValidatedForSaveHojinCustomerBasic(basicInputForm);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(basicInputForm, PERSON_REGIST_BASIC_INPUT_FRAGMENT_PATH, PERSON_REGIST_BASIC_INPUT_FORM_NAME,
					result);
		}

		try {

			// 保存処理
			service.registPersonBasic(basicInputForm);

			// 保存終了後、レスポンスを受けるajax側で名簿編集画面へ遷移するために、personIdが必要なので、
			// personId取得のために、画面情報を返却する
			mv = getMyModelAndView(basicInputForm, PERSON_REGIST_BASIC_INPUT_FRAGMENT_PATH, PERSON_REGIST_BASIC_INPUT_FORM_NAME);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00012));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "名簿情報"));
		return mv;
	}

	/**
	 * 【弁護士】名簿基本情報の登録
	 * 
	 * @param basicInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registLawyerPersonBasic", method = RequestMethod.POST)
	public ModelAndView registLawyerPersonBasic(
			@Validated({Default.class,
					Kojin.class}) @ModelAttribute(EMPTY_INPUT_FORM_NAME) PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		ModelAndView mv = null;

		// 表示用データの設定
		service.setDisplayData(basicInputForm);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 入力フォームにerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(basicInputForm, PERSON_REGIST_BASIC_INPUT_FRAGMENT_PATH, PERSON_REGIST_BASIC_INPUT_FORM_NAME,
					result);
		}

		try {

			// 登録処理
			service.registPersonBasic(basicInputForm);

			// 保存終了後、レスポンスを受けるajax側で名簿編集画面へ遷移するために、personIdが必要なので、
			// personId取得のために、画面情報を返却する
			mv = getMyModelAndView(basicInputForm, PERSON_REGIST_BASIC_INPUT_FRAGMENT_PATH, PERSON_REGIST_BASIC_INPUT_FORM_NAME);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00012));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "名簿情報"));
		return mv;
	}


	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 画面独自のバリデーションチェック（個人の名簿基本情報の登録処理用）
	 * 
	 * @param basicInputForm
	 * @param result
	 */
	private void inputFormValidatedForSaveKojinCustomerBasic(PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm,
			BindingResult result) {

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

		// 故人フラグのチェックが有るときに死亡日を確認する
		LocalDate deathDate = null;
		if (basicInputForm.getDeathFlg()) {
			// 死亡日不正入力エラーメッセージ
			String deathDateInputErrorMessage = getMessage(MessageEnum.MSG_E00166.getMessageKey());

			EraType deathEra = basicInputForm.getDeathEra();
			Long deathYear = basicInputForm.getDeathYear();
			Long deathMonth = basicInputForm.getDeathMonth();
			Long deathDay = basicInputForm.getDeathDay();

			// 正しい日付かチェックする
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
	 * 画面独自のバリデーションチェック（法人の名簿基本情報の登録処理用）
	 * 
	 * @param basicInputForm
	 */
	private void inputFormValidatedForSaveHojinCustomerBasic(PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm) {

		// 法人の登録ではブランクとする
		basicInputForm.setCustomerNameMei(CommonConstant.BLANK);
		basicInputForm.setCustomerNameMeiKana(CommonConstant.BLANK);

		// バリデーションチェックは現状ない
	}
}
