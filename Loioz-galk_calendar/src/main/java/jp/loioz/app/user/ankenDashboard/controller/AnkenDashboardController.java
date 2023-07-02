package jp.loioz.app.user.ankenDashboard.controller;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.ankenDashboard.form.AnkenDashboardSearchForm;
import jp.loioz.app.user.ankenDashboard.form.AnkenDashboardViewForm;
import jp.loioz.app.user.ankenDashboard.service.AnkenDashboardService;
import jp.loioz.app.user.gyomuHistory.annitations.HasGyomuHistoryByAnken;
import jp.loioz.app.user.schedule.annotations.HasSchedule;
import jp.loioz.app.user.schedule.form.ScheduleDetail;
import jp.loioz.common.constant.MessageEnum;

/**
 * 案件ダッシュボード管理画面のコントローラークラス
 */
@Controller
@HasSchedule
@HasGyomuHistoryByAnken
@RequestMapping(value = "user/ankenDashboard/{ankenId}")
@SessionAttributes(AnkenDashboardController.SEARCH_FORM_NAME)
public class AnkenDashboardController extends DefaultController {

	/** コントローラと対応するviewパス */
	public static final String MY_VIEW_PATH = "user/ankenDashboard/ankenDashboard";
	private static final String ANKEN_DASHBOARD_TASK_VIEW_FRAGMENT_PATH = "user/ankenDashboard/ankenDashboardFragment::ankenDashboardTaskViewFragment";
	private static final String ANKEN_DASHBOARD_SCHEDULE_VIEW_FRAGMENT_PATH = "user/ankenDashboard/ankenDashboardFragment::ankenDashboardScheduleViewFragment";
	private static final String ANKEN_DASHBOARD_GYOMU_HISTORY_VIEW_FRAGMENT_PATH = "user/ankenDashboard/ankenDashboardFragment::ankenDashboardGyomuHistoryViewFragment";

	/** セッション管理するフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "ankenDashboardSearchForm";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";
	private static final String ANKEN_DASHBOARD_TASK_VIEW_FORM_NAME = "ankenDashboardTaskViewForm";
	private static final String ANKEN_DASHBOARD_SCHEDULE_VIEW_FORM_NAME = "ankenDashboardScheduleViewForm";
	private static final String ANKEN_DASHBOARD_GYOMU_HISTORY_VIEW_FORM_NAME = "ankenDashboardGyomuHistoryViewForm";

	/** フォームクラスの初期化 */
	@ModelAttribute(SEARCH_FORM_NAME)
	AnkenDashboardSearchForm setUpForm(@PathVariable Long ankenId) {
		return new AnkenDashboardSearchForm();
	}

	/** サービスクラス */
	@Autowired
	private AnkenDashboardService service;

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
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) AnkenDashboardSearchForm searchForm, @PathVariable Long ankenId) {

		// 別案件を開いた場合は、searchFormを初期化
		if (!Objects.equals(searchForm.getKeepAnkenId(), ankenId)) {
			searchForm.initForm();
			searchForm.setKeepAnkenId(ankenId);
		}

		// 画面表示情報を取得
		AnkenDashboardViewForm viewForm = service.createViewForm(ankenId, searchForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 初期表示<br>
	 *
	 * <pre>
	 * 顧客一覧画面から案件管理画面に遷移する場合用。
	 * </pre>
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @param customerSearchForm 顧客一覧画面の検索条件フォーム
	 * @return 画面表示情報
	 */
	@RequestMapping(value = "/{customerId}/", method = RequestMethod.GET)
	public ModelAndView fromCustomerList(
			@ModelAttribute(SEARCH_FORM_NAME) AnkenDashboardSearchForm searchForm,
			@PathVariable Long ankenId,
			@PathVariable Long customerId) {

		// 別案件を開いた場合は、searchFormを初期化
		if (!Objects.equals(searchForm.getKeepAnkenId(), ankenId)) {
			searchForm.initForm();
			searchForm.setKeepAnkenId(ankenId);
		}

		// 画面表示情報を取得
		AnkenDashboardViewForm viewForm = service.createViewForm(ankenId, searchForm);

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * タスクエリアの取得処理<br>
	 * 
	 * @param searchForm
	 * @param ankenId
	 * @return タスクエリアのHTML情報
	 */
	@RequestMapping(value = "/getAnkenDashboardTaskViewFragment", method = RequestMethod.GET)
	public ModelAndView getAnkenDashboardTaskViewFragment(
			@ModelAttribute(SEARCH_FORM_NAME) AnkenDashboardSearchForm searchForm,
			@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {
			// タスク情報のみを取得する
			AnkenDashboardViewForm.AnkenDashboardTaskViewForm ankenDashboardTaskViewForm = service.createAnkenDashBoardTaskViewForm(ankenId,
					searchForm);
			mv = getMyModelAndView(ankenDashboardTaskViewForm, ANKEN_DASHBOARD_TASK_VIEW_FRAGMENT_PATH, ANKEN_DASHBOARD_TASK_VIEW_FORM_NAME);

		} catch (Exception ex) {
			return ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;

	}

	/**
	 * スケジュールエリアの取得処理<br>
	 * 
	 * @param searchForm
	 * @param ankenId
	 * @return スケジュールエリアのHTML情報
	 */
	@RequestMapping(value = "/getAnkenDashboardScheduleViewFragment", method = RequestMethod.GET)
	public ModelAndView getScheduleArea(
			@ModelAttribute(SEARCH_FORM_NAME) AnkenDashboardSearchForm searchForm,
			@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {
			// スケジュール情報のみを取得する
			AnkenDashboardViewForm.AnkenDashboardScheduleViewForm ankenDashboardScheduleViewForm = service
					.createAnkenDashboardScheduleViewForm(ankenId, searchForm);
			mv = getMyModelAndView(ankenDashboardScheduleViewForm, ANKEN_DASHBOARD_SCHEDULE_VIEW_FRAGMENT_PATH,
					ANKEN_DASHBOARD_SCHEDULE_VIEW_FORM_NAME);

		} catch (Exception ex) {
			return ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;

	}

	/**
	 * 案件ダッシュボード画面：予定情報の詳細モーダルデータを取得
	 * 
	 * ThymeleafのモデルデータのJson化は予定データの日付フォーマット処理が考慮されていないため、
	 * 名簿画面のスケジュール一覧レンダリング直後にAjaxにより取得する
	 * 
	 * @param searchForm
	 * @param ankenId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getAnkenDashScheduleDetail", method = RequestMethod.GET)
	public Map<Long, ScheduleDetail> getAnkenDashScheduleDetail(AnkenDashboardSearchForm searchForm, @PathVariable Long ankenId) {

		try {
			// スケジュール情報のみを取得する
			Map<Long, ScheduleDetail> scheduleDetails = service.getAnkenDashScheduleDetails(ankenId, searchForm);

			super.setAjaxProcResultSuccess();
			return scheduleDetails;

		} catch (Exception ex) {
			setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}
	}

	/**
	 * 業務履歴エリアの取得処理<br>
	 * 
	 * @param searchForm
	 * @param ankenId
	 * @return 業務履歴エリアのHTML情報
	 */
	@RequestMapping(value = "/getAnkenDashboardGyomuHistoryViewFragment", method = RequestMethod.GET)
	public ModelAndView getGyomuHistoryArea(
			@ModelAttribute(SEARCH_FORM_NAME) AnkenDashboardSearchForm searchForm,
			@PathVariable Long ankenId) {

		ModelAndView mv = null;

		try {
			// 業務履歴情報のみを取得する
			AnkenDashboardViewForm.AnkenDashboardGyomuHistoryViewForm ankenDashboardGyomuHistoryViewForm = service
					.createAnkenDashboardGyomuHistoryViewForm(ankenId, searchForm);
			mv = getMyModelAndView(ankenDashboardGyomuHistoryViewForm, ANKEN_DASHBOARD_GYOMU_HISTORY_VIEW_FRAGMENT_PATH,
					ANKEN_DASHBOARD_GYOMU_HISTORY_VIEW_FORM_NAME);

		} catch (Exception ex) {
			return ajaxException(ex);
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

}
