package jp.loioz.app.user.ankenManagement.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.user.ankenManagement.form.AnkenEditKeijiInputForm;
import jp.loioz.app.user.ankenManagement.form.AnkenEditKeijiInputForm.AnkenBasicInputForm;
import jp.loioz.app.user.ankenManagement.form.AnkenEditKeijiViewForm;
import jp.loioz.app.user.ankenManagement.service.AnkenEditKeijiService;
import jp.loioz.app.user.gyomuHistory.annitations.HasGyomuHistoryByAnken;
import jp.loioz.app.user.schedule.annotations.HasSchedule;
import jp.loioz.app.user.schedule.form.ScheduleInputForm;
import jp.loioz.app.user.schedule.service.ScheduleCommonService;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.BengoType;
import jp.loioz.common.constant.CommonConstant.KoryuStatus;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.utility.ValidateUtils;
import jp.loioz.domain.value.CustomerId;

/**
 * 案件情報（刑事）コントローラー
 */
@Controller
@HasSchedule
@HasGyomuHistoryByAnken
@RequestMapping(value = "user/ankenKeijiManagement/{ankenId}")
public class AnkenEditKeijiController extends DefaultController {

	/** コントローラと対応するviewパス */
	private static final String MY_VIEW_PATH = "user/ankenManagement/ankenEditKeiji";

	/******************************************************************
	 * サイドメニュー
	 ******************************************************************/

	/** 顧客案件メニューfragmentのパス */
	private static final String CUSTOMER_ANKENMENU_FRAGMENT_PATH = "common/customerAnkenMenu::menuList";

	/******************************************************************
	 * 基本情報
	 ******************************************************************/

	/** 案件基本情報表示fragmentのパス */
	private static final String ANKEN_BASIC_VIEW_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenBasicViewFragment";
	/** 案件基本情報入力フォームfragmentのパス */
	private static final String ANKEN_BASIC_INPUT_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenBasicInputFragment";
	/** 案件基本情報入力フォーム売上計上先選択肢fragmentのパス */
	private static final String ANKEN_BASIC_LAWYER_OPTIONS_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::lawyerOptionList";

	/** 案件基本情報の表示用フォームオブジェクト名 */
	private static final String ANKEN_BASIC_VIEW_FORM_NAME = "ankenBasicViewForm";
	/** 案件基本情報の入力用フォームオブジェクト名 */
	private static final String ANKEN_BASIC_INPUT_FORM_NAME = "ankenBasicInputForm";

	/******************************************************************
	 * 案件-顧客
	 ******************************************************************/

	/** 案件-顧客情報表示fragmentのパス */
	private static final String ANKEN_CUSTOMER_LIST_VIEW_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenCustomerListViewFragment";
	/** 案件-顧客情報表示fragmentのパス */
	private static final String ANKEN_CUSTOMER_LIST_INPUT_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenCustomerListInputFragment";

	/** 案件-顧客一覧情報の表示用フォームオブジェクト名 */
	private static final String ANKEN_CUSTOMER_LIST_VIEW_FORM_NAME = "ankenCustomerListViewForm";
	/** 案件-顧客入力情報の表示用フォームオブジェクト名 */
	private static final String ANKEN_CUSTOMER_LIST_INPUT_FORM_NAME = "ankenCustomerListInputForm";

	/** 案件-顧客（事件） */

	/** 案件-顧客-事件情報表示fragmentのパス */
	private static final String ANKEN_CUSTOMER_JIKEN_LIST_VIEW_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenCustomerJikenListViewFragment";
	/** 案件-顧客-事件情報表示fragmentのパス */
	private static final String ANKEN_CUSTOMER_JIKEN_LIST_INPUT_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenCustomerJikenListInputFragment";

	/** 案件-顧客-事件一覧情報の表示用オブジェクト名 */
	private static final String ANKEN_CUSTOMER_JIKEN_LIST = "ankenCustomerJikenDtoList";
	/** 案件-顧客-事件入力情報の表示用フォームオブジェクト名 */
	private static final String ANKEN_CUSTOMER_JIKEN_LIST_INPUT_FORM_NAME = "ankenCustomerJikenListInputForm";

	/** 案件-顧客（接見） */

	/** 案件-顧客-接見情報表示fragmentのパス */
	private static final String ANKEN_CUSTOMER_SEKKEN_LIST_VIEW_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenCustomerSekkenListViewFragment";
	/** 案件-顧客-接見情報表示fragmentのパス */
	private static final String ANKEN_CUSTOMER_SEKKEN_LIST_INPUT_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenCustomerSekkenListInputFragment";

	/** 案件-顧客-接見一覧情報の表示用オブジェクト名 */
	private static final String ANKEN_CUSTOMER_SEKKEN_LIST = "ankenCustomerSekkenDtoList";
	/** 案件-顧客-接見入力情報の表示用フォームオブジェクト名 */
	private static final String ANKEN_CUSTOMER_SEKKEN_LIST_INPUT_FORM_NAME = "ankenCustomerSekkenListInputForm";

	/** 案件-顧客（在監） */

	/** 案件-顧客-在監情報表示fragmentのパス */
	private static final String ANKEN_CUSTOMER_ZAIKAN_LIST_VIEW_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenCustomerZaikanListViewFragment";
	/** 案件-顧客-在監情報表示fragmentのパス */
	private static final String ANKEN_CUSTOMER_ZAIKAN_LIST_INPUT_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenCustomerZaikanListInputFragment";

	/** 案件-顧客-在監一覧情報の表示用オブジェクト名 */
	private static final String ANKEN_CUSTOMER_ZAIKAN_LIST = "ankenCustomerZaikanDtoList";
	/** 案件-顧客-在監入力情報の表示用フォームオブジェクト名 */
	private static final String ANKEN_CUSTOMER_ZAIKAN_LIST_INPUT_FORM_NAME = "ankenCustomerZaikanListInputForm";

	/******************************************************************
	 * 捜査機関
	 ******************************************************************/

	/** 捜査機関一覧 表示用 fragmentのパス */
	private static final String ANKEN_SOSAKIKAN_LIST_VIEW_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenSosakikanListViewFragment";
	/** 捜査機関一覧 入力用 fragmentのパス */
	private static final String ANKEN_SOSAKIKAN_LIST_INPUT_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenSosakikanListInputFragment";

	/** 捜査機関一覧 表示用 フォームオブジェクト名 */
	private static final String ANKEN_SOSAKIKAN_LIST_VIEW_FORM_NAME = "ankenSosakikanListViewForm";
	/** 捜査機関一覧 入力用 フォームオブジェクト名 */
	private static final String ANKEN_SOSAKIKAN_LIST_INPUT_FORM_NAME = "ankenSosakikanListInputForm";

	/******************************************************************
	 * 案件-関与者(共犯者-弁護人)
	 ******************************************************************/

	/** 関与者（共犯者）情報表示fragmentのパス */
	private static final String ANKEN_KYOHANSHA_LIST_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenKyohanshaListViewFragment";
	/** 関与者（共犯者）一覧情報の表示用フォームオブジェクト名 */
	private static final String ANKEN_KYOHANSHA_LIST_VIEW_FORM_NAME = "ankenKyohanshaListViewForm";

	/******************************************************************
	 * 案件-関与者(被害者-代理人)
	 ******************************************************************/

	/** 関与者（被害者）情報表示fragmentのパス */
	private static final String ANKEN_HIGAISHA_LIST_FRAGMENT_PATH = "user/ankenManagement/ankenEditKeijiFragment::ankenHigaishaListViewFragment";
	/** 関与者（被害者）情報の表示用フォームオブジェクト名 */
	private static final String ANKEN_HIGAISHA_LIST_VIEW_FORM_NAME = "ankenHigaishaListViewForm";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/** サービスクラス */
	@Autowired
	private AnkenEditKeijiService service;

	/** 共通案件サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 共通予定サービスクラス */
	@Autowired
	private ScheduleCommonService scheduleCommonService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 * 
	 * @param ankenId 案件ID
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable Long ankenId) {

		if (!commonAnkenService.isKeiji(ankenId)) {
			// 案件が刑事以外の場合、共通コントローラーに戻す
			return ModelAndViewUtils.getRedirectModelAndView(AnkenEditController.class, controller -> controller.index(ankenId));
		}

		// 画面表示情報を取得
		AnkenEditKeijiViewForm viewForm = service.createViewForm(ankenId);

		// 案件 基本情報表示用オブジェクトの作成
		AnkenEditKeijiViewForm.AnkenBasicViewForm ankenBasicViewForm = service.createAnkenBasicViewForm(ankenId);
		viewForm.setAnkenBasicViewForm(ankenBasicViewForm);

		// 案件-顧客情報表示用オブジェクトの作成
		AnkenEditKeijiViewForm.AnkenCustomerListViewForm ankenCustomerListViewForm = service.createAnkenCustomerListViewForm(ankenId);
		viewForm.setAnkenCustomerListViewForm(ankenCustomerListViewForm);

		// 捜査機関 情報表示用オブジェクトの作成
		AnkenEditKeijiViewForm.AnkenSosakikanListViewForm ankenSosakikanListViewForm = service.createAnkenSosakikanListViewForm(ankenId);
		viewForm.setAnkenSosakikanListViewForm(ankenSosakikanListViewForm);

		// 関与者（共犯者）情報表示用オブジェクトの作成
		AnkenEditKeijiViewForm.AnkenKyohanshaListViewForm ankenKyohanshaListViewForm = service.createAnkenKyohanshaListViewForm(ankenId);
		viewForm.setAnkenKyohanshaListViewForm(ankenKyohanshaListViewForm);

		// 関与者（被害者）情報表示用オブジェクトの作成
		AnkenEditKeijiViewForm.AnkenHigaishaListViewForm ankenHigaishaListViewForm = service.createAnkenHigaishaListViewForm(ankenId);
		viewForm.setAnkenHigaishaListViewForm(ankenHigaishaListViewForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 案件追加後の案件編集画面初期表示用。<br>
	 * 案件基本情報を取得し入力フォームにセットする。<br>
	 * 経営者が1ユーザーしかいない場合は、そのユーザーを入力フォームの売上計上先にセットする。<br>
	 * 
	 * <pre>
	 * 基本情報部分を編集モードで表示するため、
	 * 基本情報入力フォームのインスタンスを取得し、リダイレクト先に渡すように設定する。
	 * 
	 * ※リダイレクトを行うのはブラウザのURLが、このメソッドへのアクセス時のURLではなく、indexメソッドのURLになるようにするため。
	 * </pre>
	 * 
	 * @param ankenId
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/indexAfterAnkenRegist", method = RequestMethod.GET)
	public ModelAndView indexAfterAnkenRegist(@PathVariable Long ankenId,
			RedirectAttributes redirectAttributes) {

		if (!commonAnkenService.isKeiji(ankenId)) {
			// 案件が刑事以外の場合、共通コントローラーに戻す
			return ModelAndViewUtils.getRedirectModelAndView(AnkenEditController.class, controller -> controller.indexAfterAnkenRegist(ankenId, redirectAttributes));
		}

		// 基本情報編集画面の表示データを取得
		AnkenEditKeijiInputForm.AnkenBasicInputForm basicInfoInputForm = service.createAnkenBasicInputForm(ankenId);

		// 売上帰属に選択できるユーザーが1ユーザーしかいない場合、売上計上先にユーザーをセットした状態にする
		basicInfoInputForm = service.setDefaultSalesOwner(basicInfoInputForm);

		// リダイレクト先に引き継ぐ
		redirectAttributes.addFlashAttribute(ANKEN_BASIC_INPUT_FORM_NAME, basicInfoInputForm);

		// 自画面のindexメソッドへのリダイレクトオブジェクトを生成
		String redirectPath = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.index(ankenId));
		RedirectViewBuilder redirectViewBuilder = new RedirectViewBuilder(redirectAttributes, redirectPath);

		return redirectViewBuilder.build();
	}

	/**
	 * 顧客案件のサイドメニュー部分を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getCustomerAnkenMenuView", method = RequestMethod.GET)
	public ModelAndView getCustomerAnkenMenuView(@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// サイドメニュー画面の取得
			mv = ModelAndViewUtils.getModelAndView(CUSTOMER_ANKENMENU_FRAGMENT_PATH);
			// パラメータ情報を設定
			mv.addObject("sideMenuAnkenId", ankenId);
			mv.addObject("selectedTabClass", "is_anken");

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

	/**
	 * 案件民事の基本情報表示フォーム部分を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenBasicView", method = RequestMethod.GET)
	public ModelAndView getAnkenBasicView(@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 顧客の基本情報表示フォームを設定した画面の取得
			AnkenEditKeijiViewForm.AnkenBasicViewForm basicViewForm = service.createAnkenBasicViewForm(ankenId);
			mv = getMyModelAndView(basicViewForm, ANKEN_BASIC_VIEW_FRAGMENT_PATH, ANKEN_BASIC_VIEW_FORM_NAME);

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
	 * 案件の基本情報入力フォーム部分を取得する
	 * 
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenBasicInputForm", method = RequestMethod.GET)
	public ModelAndView getAnkenBasicInputForm(@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			AnkenEditKeijiInputForm.AnkenBasicInputForm basicInfoInputForm = service.createAnkenBasicInputForm(ankenId);
			mv = getMyModelAndView(basicInfoInputForm, ANKEN_BASIC_INPUT_FRAGMENT_PATH, ANKEN_BASIC_INPUT_FORM_NAME);

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
	 * 売上計上先の自動選択処理
	 *
	 * @param ankenId
	 * @param ankenType
	 * @return
	 */
	@RequestMapping(value = "/changeType", method = RequestMethod.GET)
	public ModelAndView setSalesOwner(@PathVariable Long ankenId, @RequestParam("ankenType") String ankenType) {

		ModelAndView mv = null;

		try {
			// 画面表示情報を取得
			AnkenEditKeijiInputForm.AnkenBasicInputForm basicInputForm = new AnkenEditKeijiInputForm.AnkenBasicInputForm();

			// 表示用プロパティの設定
			service.setDisplayData(basicInputForm, ankenId, AnkenType.of(ankenType));
			mv = getMyModelAndView(basicInputForm, ANKEN_BASIC_LAWYER_OPTIONS_FRAGMENT_PATH, ANKEN_BASIC_INPUT_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件基本情報の更新前アラートチェック
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/beforeSaveAnkenBasicCheck", method = RequestMethod.POST)
	public Map<String, Object> beforeSaveAnkenBasicCheck(@PathVariable Long ankenId,
			@Validated AnkenEditKeijiInputForm.AnkenBasicInputForm basicInputForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		if (commonAnkenService.isChangedBunya(ankenId, basicInputForm.getBunya())) {
			// 分野を変更しようとしているケース

			if (commonAnkenService.canChangeBunya(ankenId)) {
				// 分野切替が可能な場合
				response.put("succeed", true);
				response.put("confirm", true);
				response.put("message", "分野に依存する情報を削除します。\nよろしいですか？");
				return response;
			} else {
				// 分野切替が不可能な場合
				response.put("succeed", false);
				response.put("confirm", false);
				response.put("message", getMessage(MessageEnum.MSG_E00046));
				return response;
			}
		}

		// 更新前に確認することがない場合
		response.put("succeed", true);
		response.put("confirm", false);
		return response;
	}

	/**
	 * 顧客の基本情報入力フォーム部分を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenCustomerListView", method = RequestMethod.GET)
	public ModelAndView getAnkenCustomerListView(@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {
			// 入力フォームを設定した画面の取得
			AnkenEditKeijiViewForm.AnkenCustomerListViewForm ankenCustomerListViewForm = service.createAnkenCustomerListViewForm(ankenId);
			mv = getMyModelAndView(ankenCustomerListViewForm, ANKEN_CUSTOMER_LIST_VIEW_FRAGMENT_PATH, ANKEN_CUSTOMER_LIST_VIEW_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 顧客の基本情報入力フォーム部分を取得する
	 *
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenCustomerInputView", method = RequestMethod.GET)
	public ModelAndView getAnkenCustomerInputView(@PathVariable Long ankenId, @RequestParam Long customerId) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			AnkenEditKeijiInputForm.AnkenCustomerInputForm ankenCustomerListInputForm = service.createAnkenCustomerInputForm(ankenId,
					customerId);
			mv = getMyModelAndView(ankenCustomerListInputForm, ANKEN_CUSTOMER_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_LIST_INPUT_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 事件情報入力フォーム部分を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenCustomerJikenListView", method = RequestMethod.GET)
	public ModelAndView getAnkenCustomerJikenListView(@PathVariable Long ankenId, @RequestParam Long customerId) {

		ModelAndView mv = null;

		try {
			// 入力フォームを設定した画面の取得
			AnkenEditKeijiViewForm.AnkenCustomerJikenListViewForm ankenCustomerJikenListViewForm = service
					.createAnkenCustomerJikenListViewForm(ankenId, customerId);
			mv = getMyModelAndView(ankenCustomerJikenListViewForm.getAnkenCustomerJikenDtoList(), ANKEN_CUSTOMER_JIKEN_LIST_VIEW_FRAGMENT_PATH,
					ANKEN_CUSTOMER_JIKEN_LIST);
			mv.addObject("customerId", CustomerId.of(customerId));
			mv.addObject("Completed", ankenCustomerJikenListViewForm.isCompleted());

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 事件情報入力フォーム部分を取得する
	 * 
	 * @param jikenSeq
	 * @return
	 */
	@RequestMapping(value = "/getAnkenCustomerJikenListInputForm", method = RequestMethod.GET)
	public ModelAndView getAnkenCustomerJikenListInputForm(@RequestParam Long jikenSeq, @RequestParam Long ankenId, @RequestParam Long customerId) {

		ModelAndView mv = null;

		try {
			// 入力フォームを設定した画面の取得
			AnkenEditKeijiInputForm.AnkenCustomerJikenListInputForm inputForm = service.createAnkenCustomerJikenListInputForm(jikenSeq, ankenId,
					customerId);
			mv = getMyModelAndView(inputForm, ANKEN_CUSTOMER_JIKEN_LIST_INPUT_FRAGMENT_PATH,
					ANKEN_CUSTOMER_JIKEN_LIST_INPUT_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 接見情報一覧部分を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenCustomerSekkenListView", method = RequestMethod.GET)
	public ModelAndView getAnkenCustomerSekkenListView(@PathVariable Long ankenId, @RequestParam Long customerId) {

		ModelAndView mv = null;

		try {
			// 入力フォームを設定した画面の取得
			AnkenEditKeijiViewForm.AnkenCustomerSekkenListViewForm ankenCustomerSekkenListViewForm = service
					.createAnkenCustomerSekkenListViewForm(ankenId, customerId);
			mv = getMyModelAndView(ankenCustomerSekkenListViewForm.getAnkenCustomerSekkenDtoList(), ANKEN_CUSTOMER_SEKKEN_LIST_VIEW_FRAGMENT_PATH,
					ANKEN_CUSTOMER_SEKKEN_LIST);
			mv.addObject("customerId", CustomerId.of(customerId));
			mv.addObject("Completed", ankenCustomerSekkenListViewForm.isCompleted());

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 接見情報入力フォーム部分を取得する
	 * 
	 * @param sekkenSeq
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenCustomerSekkenListInputForm", method = RequestMethod.GET)
	public ModelAndView getAnkenCustomerSekkenListInputForm(@RequestParam Long sekkenSeq, @RequestParam Long ankenId, @RequestParam Long customerId) {

		ModelAndView mv = null;

		try {
			// 入力フォームを設定した画面の取得
			AnkenEditKeijiInputForm.AnkenCustomerSekkenListInputForm inputForm = service.createAnkenCustomerSekkenListInputForm(sekkenSeq, ankenId,
					customerId);
			mv = getMyModelAndView(inputForm, ANKEN_CUSTOMER_SEKKEN_LIST_INPUT_FRAGMENT_PATH,
					ANKEN_CUSTOMER_SEKKEN_LIST_INPUT_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 在監一覧情報を取得する
	 * 
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenCustomerZaikanListView", method = RequestMethod.GET)
	public ModelAndView getAnkenCustomerZaikanListView(@PathVariable Long ankenId, @RequestParam Long customerId) {

		ModelAndView mv = null;

		try {
			// 入力フォームを設定した画面の取得
			AnkenEditKeijiViewForm.AnkenCustomerZaikanListViewForm ankenCustomerZaikanListViewForm = service.createAnkenCustomerZaikanListViewForm(ankenId, customerId);
			mv = getMyModelAndView(ankenCustomerZaikanListViewForm.getAnkenCustomerZaikanDtoList(), ANKEN_CUSTOMER_ZAIKAN_LIST_VIEW_FRAGMENT_PATH, ANKEN_CUSTOMER_ZAIKAN_LIST);
			mv.addObject("customerId", CustomerId.of(customerId));
			mv.addObject("Completed", ankenCustomerZaikanListViewForm.isCompleted());

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 在監場所情報の入力フォームを取得する
	 * 
	 * @param koryuSeq
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenCustomerZaikanListInputForm", method = RequestMethod.GET)
	public ModelAndView getAnkenCustomerZaikanListInputForm(@RequestParam Long koryuSeq, @RequestParam Long ankenId, @RequestParam Long customerId) {

		ModelAndView mv = null;

		try {
			// 入力フォームを設定した画面の取得
			AnkenEditKeijiInputForm.AnkenCustomerZaikanListInputForm inputForm = service.createAnkenCustomerZaikanListInputForm(koryuSeq, ankenId, customerId);
			mv = getMyModelAndView(inputForm, ANKEN_CUSTOMER_ZAIKAN_LIST_INPUT_FRAGMENT_PATH,
					ANKEN_CUSTOMER_ZAIKAN_LIST_INPUT_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 捜査機関 表示フォーム部分を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenSosakikanListView", method = RequestMethod.GET)
	public ModelAndView getAnkenSosakikanListView(@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {
			// 入力フォームを設定した画面の取得
			AnkenEditKeijiViewForm.AnkenSosakikanListViewForm ankenSosakikanListViewForm = service.createAnkenSosakikanListViewForm(ankenId);
			mv = getMyModelAndView(ankenSosakikanListViewForm, ANKEN_SOSAKIKAN_LIST_VIEW_FRAGMENT_PATH, ANKEN_SOSAKIKAN_LIST_VIEW_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 捜査機関の入力フォーム部分を取得する
	 *
	 * @param ankenId
	 * @param sosakikanSeq
	 * @return
	 */
	@RequestMapping(value = "/getAnkenSosakikanListInputForm", method = RequestMethod.GET)
	public ModelAndView getAnkenSosakikanListInputForm(@PathVariable Long ankenId, @RequestParam Long sosakikanSeq) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			AnkenEditKeijiInputForm.AnkenSosakikanListInputForm ankenSosakikanListInputForm = service.createAnkenSosakikanListInputForm(ankenId,
					sosakikanSeq);
			mv = getMyModelAndView(ankenSosakikanListInputForm, ANKEN_SOSAKIKAN_LIST_INPUT_FRAGMENT_PATH, ANKEN_SOSAKIKAN_LIST_INPUT_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 共犯者（顧客以外）部分を取得する
	 *
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenKyohanshaListView", method = RequestMethod.GET)
	public ModelAndView getAnkenKyohanshaListView(@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			AnkenEditKeijiViewForm.AnkenKyohanshaListViewForm ankenKyohanshaListViewForm = service.createAnkenKyohanshaListViewForm(ankenId);
			mv = getMyModelAndView(ankenKyohanshaListViewForm, ANKEN_KYOHANSHA_LIST_FRAGMENT_PATH, ANKEN_KYOHANSHA_LIST_VIEW_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 被害者一覧を取得する
	 *
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getAnkenHigaishaListView", method = RequestMethod.GET)
	public ModelAndView getAnkenHigaishaListView(@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {

			// 入力フォームを設定した画面の取得
			AnkenEditKeijiViewForm.AnkenHigaishaListViewForm ankenHigaishaListViewForm = service.createAnkenHigaishaListViewForm(ankenId);
			mv = getMyModelAndView(ankenHigaishaListViewForm, ANKEN_HIGAISHA_LIST_FRAGMENT_PATH, ANKEN_HIGAISHA_LIST_VIEW_FORM_NAME);

		} catch (Exception ex) {
			return super.ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件基本情報の更新処理
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveAnkenBasic", method = RequestMethod.POST)
	public ModelAndView saveAnkenBasic(@PathVariable Long ankenId, @Validated AnkenEditKeijiInputForm.AnkenBasicInputForm basicInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用プロパティの設定
		service.setDisplayData(basicInputForm, ankenId, basicInputForm.getAnkenType());

		// 担当者の相関チェック
		commonAnkenService.validateTanto(basicInputForm.getSalesOwner(), result, "salesOwner", "売上計上先");
		commonAnkenService.validateTanto(basicInputForm.getTantoLawyer(), result, "tantoLawyer", "担当弁護士");
		commonAnkenService.validateTanto(basicInputForm.getTantoJimu(), result, "tantoJimu", "担当事務員");

		commonAnkenService.validateSalesOwnerCount(basicInputForm.getSalesOwner(), result, "salesOwner");

		// 入力バリデーション
		if (result.hasErrors()) {
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(basicInputForm, ANKEN_BASIC_INPUT_FRAGMENT_PATH, ANKEN_BASIC_INPUT_FORM_NAME, result);
		}

		// 入力バリデーション
		MessageEnum inputFormValidatedResult = this.inputFormValidated(basicInputForm, result);
		if (inputFormValidatedResult != null) {
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(basicInputForm, ANKEN_BASIC_INPUT_FRAGMENT_PATH, ANKEN_BASIC_INPUT_FORM_NAME, result);
		}

		if (commonAnkenService.isChangedBunya(ankenId, basicInputForm.getBunya())) {
			// 分野を変更するケース

			if (this.accessDBValidated(ankenId)) {
				// 分野切替が不可の場合、処理失敗とする
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00046));
				return null;
			}

			try {
				// 分野切替処理を含む、案件情報の更新処理
				service.saveAnkenBasicHasChangeBunya(ankenId, basicInputForm);

				// 画面画面リダイレクトを行う特殊ケース
				super.setAjaxProcResult(AnkenBasicInputForm.HEADER_VALUE_OF_AJAX_PROC_RESULT_REDIRECT, "");
				return null;

			} catch (AppException e) {
				// 処理失敗とする
				super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
				return null;
			}

		} else {
			// 分野を変更しないケース

			try {
				// 案件情報の更新
				service.saveAnkenBasic(ankenId, basicInputForm);

				// 画面表示Viewを返却
				mv = this.getAnkenBasicView(ankenId);

			} catch (AppException e) {
				// 処理失敗とする
				super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
				return null;
			}
		}

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "案件情報"));
		return mv;
	}

	/**
	 * 顧客の基本情報入力フォーム部分を取得する
	 *
	 * @param ankenId
	 * @param customerId
	 * @param rowInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveAnkenCustomer", method = RequestMethod.POST)
	public ModelAndView saveAnkenCustomer(@PathVariable Long ankenId, @RequestParam Long customerId,
			@Validated AnkenEditKeijiInputForm.AnkenCustomerInputForm rowInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		service.setDisplayData(rowInputForm, ankenId, customerId);

		// 案件-顧客情報のバリデーション
		this.validateAnkenCustomer(rowInputForm, result);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合、処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(rowInputForm, ANKEN_CUSTOMER_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_LIST_INPUT_FORM_NAME, result);
		}

		try {
			// 案件顧客情報の保存処理
			service.saveAnkenCustomer(ankenId, customerId, rowInputForm);

			mv = this.getAnkenCustomerListView(ankenId);
		} catch (AppException e) {
			// エラー内容を返却
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "顧客情報"));
		return mv;
	}

	/**
	 * 顧客紐づけ解除
	 * 
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteCustomer", method = RequestMethod.POST)
	public Map<String, Object> deleteCustomer(@PathVariable Long ankenId, @RequestParam Long customerId) {

		Map<String, Object> response = new HashMap<>();

		// 削除可能かどうかを判定します。
		boolean isAbleToDeleteCustomer = commonAnkenService.isAbleToDeleteCustomer(ankenId, customerId);

		if (!isAbleToDeleteCustomer) {
			// 削除不可の場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00045.getMessageKey()));
			return response;
		}

		try {
			// 案件と顧客の紐づけを解除します。
			service.deleteAnkenCustomer(ankenId, customerId);

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00037.getMessageKey(), "案件"));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00101.getMessageKey(), "案件"));
			return response;
		}
	}

	/**
	 * 勾留ステータスの更新処理
	 * 
	 * @param ankenId
	 * @param customerId
	 * @param koryuStatus
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/saveAnkenCustomerKoryuStatus", method = RequestMethod.POST)
	public Map<String, Object> saveAnkenCustomerKoryuStatus(
			@PathVariable Long ankenId, @RequestParam Long customerId, @RequestParam("koryuStatus") KoryuStatus koryuStatus) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 更新処理
			service.saveAnkenCustomerKoryuStatus(ankenId, customerId, koryuStatus);

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "勾留情報"));
			return response;

		} catch (AppException e) {
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 案件-顧客-事件情報の登録処理
	 * 
	 * @param ankenId
	 * @param customerId
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registAnkenCustomerJiken", method = RequestMethod.POST)
	public ModelAndView registAnkenCustomerJiken(
			@PathVariable Long ankenId, @RequestParam Long customerId,
			@Validated AnkenEditKeijiInputForm.AnkenCustomerJikenListInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;

		// 表示用プロパティの設定
		service.setDisplayData(inputForm);

		if (result.hasErrors()) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_CUSTOMER_JIKEN_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_JIKEN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		// 入力項目の全てが未入力の場合
		if (StringUtils.isAllEmpty(
				inputForm.getJikenName(),
				inputForm.getTaihoDate(),
				inputForm.getKoryuSeikyuDate(),
				inputForm.getKoryuExpirationDate(),
				inputForm.getKoryuExtendedExpirationDate(),
				inputForm.getShobunType(),
				inputForm.getShobunDate())) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00042, "事件情報"));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_CUSTOMER_JIKEN_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_JIKEN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		try {
			// 登録処理
			service.registAnkenCustomerJiken(inputForm);

		} catch (AppException e) {
			// エラーハンドリング
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getAnkenCustomerJikenListView(ankenId, customerId);
		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "事件情報"));
		return mv;
	}

	/**
	 * 案件-顧客-事件情報の更新処理
	 * 
	 * @param ankenId
	 * @param customerId
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/updateAnkenCustomerJiken", method = RequestMethod.POST)
	public ModelAndView updateAnkenCustomerJiken(
			@PathVariable Long ankenId, @RequestParam Long customerId,
			@Validated AnkenEditKeijiInputForm.AnkenCustomerJikenListInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;

		// 表示用プロパティの設定
		service.setDisplayData(inputForm);

		if (result.hasErrors()) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_CUSTOMER_JIKEN_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_JIKEN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		// 入力項目の全てが未入力の場合
		if (StringUtils.isAllEmpty(
				inputForm.getJikenName(),
				inputForm.getTaihoDate(),
				inputForm.getKoryuSeikyuDate(),
				inputForm.getKoryuExpirationDate(),
				inputForm.getKoryuExtendedExpirationDate(),
				inputForm.getShobunType(),
				inputForm.getShobunDate())) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00042, "事件情報"));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_CUSTOMER_JIKEN_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_JIKEN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		try {
			// 更新処理
			service.updateAnkenCustomerJiken(inputForm);

		} catch (AppException e) {
			// エラーハンドリング
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getAnkenCustomerJikenListView(ankenId, customerId);
		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "事件情報"));
		return mv;
	}

	/**
	 * 案件-顧客-事件情報の削除処理
	 * 
	 * @param ankenId
	 * @param customerId
	 * @param jikenSeq
	 * @return
	 */
	@RequestMapping(value = "/deleteAnkenCustomerJiken", method = RequestMethod.POST)
	public ModelAndView deleteAnkenCustomerJiken(
			@PathVariable Long ankenId, @RequestParam Long customerId, @RequestParam Long jikenSeq) {

		ModelAndView mv = null;

		try {
			// 削除処理
			service.deleteAnkenCustomerJiken(jikenSeq);

		} catch (AppException e) {
			// エラーハンドリング
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getAnkenCustomerJikenListView(ankenId, customerId);
		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "事件情報"));
		return mv;
	}

	/**
	 * 案件顧客-接見情報の登録処理
	 * 
	 * @param ankenId
	 * @param customerId
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registAnkenCustomerSekken", method = RequestMethod.POST)
	public ModelAndView registAnkenCustomerSekken(
			@PathVariable Long ankenId, @RequestParam Long customerId,
			@Validated AnkenEditKeijiInputForm.AnkenCustomerSekkenListInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;

		// 相関バリデート
		this.inputFormValidated(inputForm, result);

		// 表示用プロパティの設定
		service.setDisplayData(inputForm);

		if (result.hasErrors()) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_CUSTOMER_SEKKEN_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_SEKKEN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		// 入力項目の全てが未入力の場合
		if (StringUtils.isAllEmpty(
				inputForm.getSekkenStartAt(),
				inputForm.getSekkenEndAt(),
				inputForm.getPlace(),
				inputForm.getRemarks())) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00042, "接見情報"));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_CUSTOMER_SEKKEN_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_SEKKEN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		try {
			// 登録処理
			service.registAnkenCustomerSekken(inputForm);

		} catch (AppException e) {
			// エラーハンドリング
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getAnkenCustomerSekkenListView(ankenId, customerId);
		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "接見情報"));
		return mv;
	}

	/**
	 * 案件顧客-接見情報の更新処理
	 * 
	 * @param ankenId
	 * @param customerId
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/updateAnkenCustomerSekken", method = RequestMethod.POST)
	public ModelAndView updateAnkenCustomerSekken(
			@PathVariable Long ankenId, @RequestParam Long customerId,
			@Validated AnkenEditKeijiInputForm.AnkenCustomerSekkenListInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;

		// 表示用プロパティの設定
		service.setDisplayData(inputForm);

		// 相関バリデート
		this.inputFormValidated(inputForm, result);

		if (result.hasErrors()) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_CUSTOMER_SEKKEN_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_SEKKEN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		// 入力項目の全てが未入力の場合
		if (StringUtils.isAllEmpty(
				inputForm.getSekkenStartAt(),
				inputForm.getSekkenEndAt(),
				inputForm.getPlace(),
				inputForm.getRemarks())) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00042, "接見情報"));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_CUSTOMER_SEKKEN_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_SEKKEN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		try {
			// 更新処理
			service.updateAnkenCustomerSekken(inputForm);

		} catch (AppException e) {
			// エラーハンドリング
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getAnkenCustomerSekkenListView(ankenId, customerId);
		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "接見情報"));
		return mv;
	}

	/**
	 * 案件顧客-接見情報の削除処理
	 * 
	 * @param ankenId
	 * @param customerId
	 * @param sekkenSeq
	 * @return
	 */
	@RequestMapping(value = "/deleteAnkenCustomerSekken", method = RequestMethod.POST)
	public ModelAndView deleteAnkenCustomerSekken(
			@PathVariable Long ankenId, @RequestParam Long customerId, @RequestParam Long sekkenSeq) {

		ModelAndView mv = null;

		try {
			// 削除処理
			service.deleteAnkenCustomerSekken(sekkenSeq);

		} catch (AppException e) {
			// エラーハンドリング
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getAnkenCustomerSekkenListView(ankenId, customerId);
		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "接見情報"));
		return mv;
	}

	/**
	 * 案件顧客-接見情報の相関バリデート
	 * 
	 * @param inputForm
	 * @param result
	 */
	private void inputFormValidated(AnkenEditKeijiInputForm.AnkenCustomerSekkenListInputForm inputForm, BindingResult result) {

		// 接見開始日と接見終了日の日付整合性チェック
		if (!ValidateUtils.isCorrectDateTimeValid(inputForm.getParsedSekkenStartAt(), inputForm.getParsedSekkenEndAt(), false)) {
			result.rejectValue("sekkenStartAt", null, getMessage(MessageEnum.MSG_E00041, "開始日時", "終了日時以降"));
			result.rejectValue("sekkenEndAt", null, getMessage(MessageEnum.MSG_E00041, "開始日時", "終了日時以降"));
		}
	}

	/**
	 * 案件顧客-在監情報の登録処理
	 *
	 * @param ankenId
	 * @param customerId
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registAnkenCustomerZaikan", method = RequestMethod.POST)
	public ModelAndView registAnkenCustomerZaikan(
			@PathVariable Long ankenId, @RequestParam Long customerId,
			@Validated AnkenEditKeijiInputForm.AnkenCustomerZaikanListInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;

		// 表示用プロパティの設定
		service.setDisplayData(inputForm);

		// 相関バリデート
		this.inputFormValidated(inputForm, result);

		if (result.hasErrors()) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_CUSTOMER_ZAIKAN_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_ZAIKAN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		// 入力項目の全てが未入力の場合
		if (StringUtils.isAllEmpty(
				inputForm.getZaikanDate(),
				inputForm.getHoshakuDate(),
				inputForm.getZaikanPlace(),
				inputForm.getTelNo(),
				inputForm.getRemarks())
				&& inputForm.getSosakikanId() == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00042, "在監情報"));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_CUSTOMER_ZAIKAN_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_ZAIKAN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		try {
			// 登録処理
			service.registAnkenCustomerZaikan(inputForm);

		} catch (AppException e) {
			// エラーハンドリング
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getAnkenCustomerZaikanListView(ankenId, customerId);
		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "在監情報"));
		return mv;
	}

	/**
	 * 案件顧客-在監情報の更新処理
	 * 
	 * @param ankenId
	 * @param customerId
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/updateAnkenCustomerZaikan", method = RequestMethod.POST)
	public ModelAndView updateAnkenCustomerZaikan(
			@PathVariable Long ankenId, @RequestParam Long customerId,
			@Validated AnkenEditKeijiInputForm.AnkenCustomerZaikanListInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;

		// 表示用プロパティの設定
		service.setDisplayData(inputForm);

		// 相関バリデート
		this.inputFormValidated(inputForm, result);

		if (result.hasErrors()) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_CUSTOMER_ZAIKAN_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_ZAIKAN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		// 入力項目の全てが未入力の場合
		if (StringUtils.isAllEmpty(
				inputForm.getZaikanDate(),
				inputForm.getHoshakuDate(),
				inputForm.getZaikanPlace(),
				inputForm.getTelNo(),
				inputForm.getRemarks())
				&& inputForm.getSosakikanId() == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00042, "在監情報"));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_CUSTOMER_ZAIKAN_LIST_INPUT_FRAGMENT_PATH, ANKEN_CUSTOMER_ZAIKAN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		try {
			// 更新処理
			service.updateAnkenCustomerZaikan(inputForm);

		} catch (AppException e) {
			// エラーハンドリング
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getAnkenCustomerZaikanListView(ankenId, customerId);
		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "在監情報"));
		return mv;
	}

	/**
	 * 案件顧客-在監情報の削除処理
	 * 
	 * @param ankenId
	 * @param customerId
	 * @param sekkenSeq
	 * @return
	 */
	@RequestMapping(value = "/deleteAnkenCustomerZaikan", method = RequestMethod.POST)
	public ModelAndView deleteAnkenCustomerZaikan(
			@PathVariable Long ankenId, @RequestParam Long customerId, @RequestParam Long koryuSeq) {

		ModelAndView mv = null;

		try {
			// 削除処理
			service.deleteAnkenCustomerZaikan(koryuSeq);

		} catch (AppException e) {
			// エラーハンドリング
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getAnkenCustomerZaikanListView(ankenId, customerId);
		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "在監情報"));
		return mv;
	}

	/**
	 * 案件情報の相関バリデート
	 * 
	 * @param basicInputForm
	 * @param result 項目に対してメッセージを設定したい場合はこのBindingResul
	 * @return 検証NGの場合はMessageEnum、検証OKの場合はnullを返却
	 */
	private MessageEnum inputFormValidated(AnkenEditKeijiInputForm.AnkenBasicInputForm basicInputForm, BindingResult result) {

		// 「私選・国選」の選択値の不正値チェック

		String lawyerSelectType = basicInputForm.getLawyerSelectType();
		if (StringUtils.isNotEmpty(lawyerSelectType)) {
			// ※値が設定されている場合のみチェックを行う
			BengoType bengoType = BengoType.of(lawyerSelectType);
			if (bengoType == null) {
				// Enumに定義されていない値が送信されてきた場合はエラー
				result.rejectValue("lawyerSelectType", null, getMessage(MessageEnum.MSG_E00001));
				return MessageEnum.MSG_E00001;
			}
		}

		return null;
	}

	/**
	 * 捜査機関情報の新規登録処理
	 * 
	 * @param ankenId
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registSosakikan", method = RequestMethod.POST)
	public ModelAndView saveSosakikan(@PathVariable Long ankenId, @Validated AnkenEditKeijiInputForm.AnkenSosakikanListInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;

		// 表示用プロパティを設定
		service.setDisplayData(inputForm);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合、処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, ANKEN_SOSAKIKAN_LIST_INPUT_FRAGMENT_PATH, ANKEN_SOSAKIKAN_LIST_INPUT_FORM_NAME, result);
		}

		// 入力項目の全てが未入力の場合
		if (StringUtils.isAllEmpty(
				inputForm.getSosakikanName(),
				inputForm.getSosakikanTantoBu(),
				inputForm.getSosakikanTelNo(),
				inputForm.getSosakikanExtensionNo(),
				inputForm.getSosakikanFaxNo(),
				inputForm.getSosakikanRoomNo(),
				inputForm.getTantosha1Name(),
				inputForm.getTantosha2Name(),
				inputForm.getRemarks())
				&& inputForm.getSosakikanId() == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00042, "捜査機関情報"));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_SOSAKIKAN_LIST_INPUT_FRAGMENT_PATH, ANKEN_SOSAKIKAN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		try {
			// 捜査機関情報の新規登録
			service.registSosakikan(inputForm);

		} catch (AppException e) {
			// エラー内容を返却
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getAnkenSosakikanListView(ankenId);
		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "捜査機関情報"));
		return mv;
	}

	/**
	 * 捜査機関の更新処理
	 * 
	 * @param ankenId
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/updateSosakikan", method = RequestMethod.POST)
	public ModelAndView updateSosakikan(@PathVariable Long ankenId, @Validated AnkenEditKeijiInputForm.AnkenSosakikanListInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;

		// 表示用プロパティを設定
		service.setDisplayData(inputForm);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合、処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return getMyModelAndViewWithErrors(inputForm, ANKEN_SOSAKIKAN_LIST_INPUT_FRAGMENT_PATH, ANKEN_SOSAKIKAN_LIST_INPUT_FORM_NAME, result);
		}

		// 入力項目の全てが未入力の場合
		if (StringUtils.isAllEmpty(
				inputForm.getSosakikanName(),
				inputForm.getSosakikanTantoBu(),
				inputForm.getSosakikanTelNo(),
				inputForm.getSosakikanExtensionNo(),
				inputForm.getSosakikanFaxNo(),
				inputForm.getSosakikanRoomNo(),
				inputForm.getTantosha1Name(),
				inputForm.getTantosha2Name(),
				inputForm.getRemarks())
				&& inputForm.getSosakikanId() == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00042, "捜査機関情報"));
			mv = getMyModelAndViewWithErrors(inputForm, ANKEN_SOSAKIKAN_LIST_INPUT_FRAGMENT_PATH, ANKEN_SOSAKIKAN_LIST_INPUT_FORM_NAME, result);
			return mv;
		}

		try {
			// 捜査機関情報の更新
			service.updateSosakikan(inputForm);

		} catch (AppException e) {
			// エラー内容を返却
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getAnkenSosakikanListView(ankenId);
		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, "捜査機関情報"));
		return mv;
	}

	/**
	 * 捜査機関情報の削除処理
	 * 
	 * @param ankenId
	 * @param sosakikanSeq
	 * @return
	 */
	@RequestMapping(value = "/deleteSosakikan", method = RequestMethod.POST)
	public ModelAndView deleteSosakikan(@PathVariable Long ankenId, @RequestParam Long sosakikanSeq) {

		ModelAndView mv = null;

		try {
			// 捜査機関情報の更新
			service.deleteSosakikan(sosakikanSeq);

		} catch (AppException e) {
			// エラー内容を返却
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 保存成功後は、表示画面を返却する
		mv = this.getAnkenSosakikanListView(ankenId);
		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "捜査機関情報"));
		return mv;
	}

	/**
	 * 共犯者情報の削除処理
	 * 
	 * @param ankenId
	 * @param kyohanshaKanyoshaSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteKyohansha", method = RequestMethod.POST)
	public Map<String, Object> deleteKyohansha(@PathVariable Long ankenId, @RequestParam(name = "kyohanshaKanyoshaSeq") Long kyohanshaKanyoshaSeq) {
		Map<String, Object> response = new HashMap<>();

		try {

			// 共犯者情報の保存処理
			service.deleteKyohansha(ankenId, kyohanshaKanyoshaSeq);

			response.put("succeed", true);
			response.put("message", getMessage(MessageEnum.MSG_I00037, "共犯者"));
			return response;

		} catch (AppException e) {
			// エラー内容を返却
			response.put("succeed", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 共犯者弁護人の削除処理
	 * 
	 * @param ankenId
	 * @param kyohanshaKanyoshaSeq
	 * @param bengoninKanyoshaSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteKyohanshaBengonin", method = RequestMethod.POST)
	public Map<String, Object> deleteKyohanshaBengonin(@PathVariable Long ankenId,
			@RequestParam(name = "kyohanshaKanyoshaSeq") Long kyohanshaKanyoshaSeq,
			@RequestParam(name = "bengoninKanyoshaSeq") Long bengoninKanyoshaSeq) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 共犯者弁護人情報の保存処理
			service.deleteKyohanshaBengonin(ankenId, kyohanshaKanyoshaSeq, bengoninKanyoshaSeq);

			response.put("succeed", true);
			response.put("message", getMessage(MessageEnum.MSG_I00037, "弁護人"));
			return response;

		} catch (AppException e) {
			// エラー内容を返却
			response.put("succeed", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 被害者情報の削除処理
	 * 
	 * @param ankenId
	 * @param higaishaKanyoshaSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteHigaisha", method = RequestMethod.POST)
	public Map<String, Object> deleteHigaisha(@PathVariable Long ankenId, @RequestParam(name = "higaishaKanyoshaSeq") Long higaishaKanyoshaSeq) {
		Map<String, Object> response = new HashMap<>();

		try {
			// 被害者情報の保存処理
			service.deleteHigaisha(ankenId, higaishaKanyoshaSeq);

			response.put("succeed", true);
			response.put("message", getMessage(MessageEnum.MSG_I00037, "被害者"));
			return response;

		} catch (AppException e) {
			// エラー内容を返却
			response.put("succeed", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 被害者-代理人の削除処理
	 * 
	 * @param ankenId
	 * @param higaishaKanyoshaSeq
	 * @param dairininKanyoshaSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteHigaishaDairinin", method = RequestMethod.POST)
	public Map<String, Object> deleteHigaishaDairinin(@PathVariable Long ankenId,
			@RequestParam(name = "higaishaKanyoshaSeq") Long higaishaKanyoshaSeq,
			@RequestParam(name = "dairininKanyoshaSeq") Long dairininKanyoshaSeq) {

		Map<String, Object> response = new HashMap<>();

		try {

			// 被害者-代理人情報の保存処理
			service.deleteHigaishaDairinin(ankenId, higaishaKanyoshaSeq, dairininKanyoshaSeq);

			response.put("succeed", true);
			response.put("message", getMessage(MessageEnum.MSG_I00037, "代理人"));
			return response;

		} catch (AppException e) {
			// エラー内容を返却
			response.put("succeed", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	/**
	 * 関与者を案件から削除する前のチェック（共犯者、被害者、弁護人、代理人共通）
	 * 
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteKanyoshaFromAnkenBeforeCheck", method = RequestMethod.POST)
	public Map<String, Object> deleteKanyoshaFromAnkenBeforeCheck(@RequestParam(name = "kanyoshaSeq") Long kanyoshaSeq) {

		Map<String, Object> response;
		try {
			response = service.deleteCommonKanyoshaFromAnkenBeforeCheck(kanyoshaSeq);
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
		return response;

	}

	/**
	 * 当事者関与者からの削除処理（共犯者、被害者、弁護人、代理人共通）
	 * 
	 * @param ankenId
	 * @param higaishaKanyoshaSeq
	 * @param dairininKanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/deleteKanyoshaFromAnken", method = RequestMethod.POST)
	public ModelAndView deleteKanyoshaFromAnken(@PathVariable Long ankenId,
			@RequestParam(name = "kanyoshaSeq") Long kanyoshaSeq) {

		ModelAndView mv = null;
		Map<String, String> errorMessage = new HashMap<>();
		// DB整合姓チェック
		if (!this.accessDBValidForDelete(kanyoshaSeq, errorMessage)) {
			super.setAjaxProcResultFailure(errorMessage.get("message"));
			return null;
		}
		try {

			// 削除処理
			service.deleteKanyoshaFromAnken(kanyoshaSeq);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;
		}
		// 正常に削除できたケースは、HTMLがないのでnull
		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00001));
		return mv;

	}

	/**
	 * 初回面談登録
	 *
	 * @param form フォーム
	 * @param result バリデーション結果
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createShokaiMendan", method = RequestMethod.POST)
	public Map<String, Object> createShokaiMendan(@Validated @ModelAttribute ScheduleInputForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力値の相関バリデートチェック(新規)
		scheduleCommonService.scheduleFormValidatedForRegist(form, result);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 入力値のDB整合性チェック(新規)
		if (scheduleCommonService.acessDBValdateRegistForSchedule(form, response)) {
			return response;
		}

		try {
			commonAnkenService.createShokaiMendanSchedule(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "初回面談予定"));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));
			return response;
		}
	}

	/**
	 * 初回面談更新
	 *
	 * @param form フォーム
	 * @param result バリデーション結果
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateShokaiMendan", method = RequestMethod.POST)
	public Map<String, Object> updateShokaiMendan(
			@Validated @ModelAttribute ScheduleInputForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力値の相関バリデートチェック(更新)
		scheduleCommonService.scheduleFormValidatedForUpdate(form, result);

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 入力値のDB整合性チェック(更新)
		if (scheduleCommonService.acessDBValdateUpdateForSchedule(form, response)) {
			return response;
		}

		try {
			commonAnkenService.updateShokaiMendanSchedule(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "初回面談予定"));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));
			return response;
		}
	}

	/**
	 * 初回面談削除
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteShokaiMendan", method = RequestMethod.POST)
	public Map<String, Object> deleteShokaiMendan(@PathVariable Long ankenId,
			@RequestParam("customerId") Long customerId) {

		Map<String, Object> response = new HashMap<>();

		try {
			commonAnkenService.deleteShokaiMendanSchedule(ankenId, customerId);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00001));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00014));
			return response;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 案件顧客-在監情報の相関バリデート
	 * 
	 * @param inputForm
	 * @param result
	 */
	private void inputFormValidated(AnkenEditKeijiInputForm.AnkenCustomerZaikanListInputForm inputForm, BindingResult result) {

		// 在監日と保釈日の日付整合性チェック
		if (!ValidateUtils.isCorrectDateValid(inputForm.getParsedZaikanDate(), inputForm.getParsedHoshakuDate(), true)) {
			result.rejectValue("zaikanDate", null, getMessage(MessageEnum.MSG_E00041, "開始日時", "終了日時以降"));
			result.rejectValue("hoshakuDate", null, getMessage(MessageEnum.MSG_E00041, "開始日時", "終了日時以降"));
		}

	}

	/**
	 * 分野切り替えが可能かどうか
	 * 
	 * @param ankenId
	 * @return
	 */
	private boolean accessDBValidated(Long ankenId) {

		boolean error = false;

		// 分野切替可能かどうかのチェック
		if (!commonAnkenService.canChangeBunya(ankenId)) {
			error = true;
		}

		return error;
	}

	/**
	 * 案件顧客情報のバリデーション
	 * 
	 * @param rowInputForm
	 * @param result
	 */
	private void validateAnkenCustomer(AnkenEditKeijiInputForm.AnkenCustomerInputForm rowInputForm, BindingResult result) {

		// 受任日が入力されていない && 事件処理日が入力されている
		if (rowInputForm.getJuninDateLocalDate() == null && rowInputForm.getJikenshoriKanryoDateLocalDate() != null) {
			if (!result.hasFieldErrors("juninDate")) {
				result.rejectValue("juninDate", MessageEnum.VARIDATE_MSG_E00002.getMessageKey());
			}
		}

		// 日付の整合性チェック
		if (!DateUtils.isCorrectDate(rowInputForm.getJuninDateLocalDate(), rowInputForm.getJikenshoriKanryoDateLocalDate())) {
			result.rejectValue("jikenshoriKanryoDate", MessageEnum.MSG_E00041.getMessageKey(), new String[]{"事件処理完了日", "受任日以降"}, "");
		}

	}

	/**
	 * 削除時のDB整合バリデート
	 * 
	 * @param form
	 * @param response
	 * @return
	 */
	private boolean accessDBValidForDelete(Long kanyoshaSeq, Map<String, String> errorMap) {

		// 削除が不可能な場合、ここで返却
		if (!service.canDeleteKanyosha(kanyoshaSeq)) {
			errorMap.put("message", getMessage(MessageEnum.MSG_E00086, "会計情報が登録されている。もしくは預かり元として登録されている"));
			return false;
		}

		return true;
	}
}
