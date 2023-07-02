package jp.loioz.app.user.calendar.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.validation.accessDB.CommonScheduleValidator;
import jp.loioz.app.user.calendar.form.CalendarViewForm;
import jp.loioz.app.user.calendar.service.CalendarService;
import jp.loioz.common.constant.CommonConstant.MessageLevel;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.log.Logger;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.service.http.CookieService;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.domain.UriService;

/**
 * カレンダー画面のコントローラークラス
 */
@Controller
@RequestMapping(value = "user/calendar")
@SessionAttributes(CalendarController.OPTIONS_FORM_NAME)
public class CalendarController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/calendar/calendar";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 選択肢のフォームオブジェクト名 */
	public static final String OPTIONS_FORM_NAME = "calendarOptionsViewForm";

	/** サービスクラス */
	@Autowired
	private CalendarService service;

	/** 予定表DB整合性チェック */
	@Autowired
	private CommonScheduleValidator commonScheduleValidator;

	/** Uriサービスクラス */
	@Autowired
	private UriService uriService;

	/** Cookieサービスクラス */
	@Autowired
	private CookieService cookieService;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/** リダイレクト先の初期化 */
	@ModelAttribute
	RedirectViewBuilder setUpRedirectViewBuilder(RedirectAttributes redirectAttributes) {
		String redirectPath = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.index(null));
		return new RedirectViewBuilder(redirectAttributes, redirectPath);
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
	public ModelAndView index(HttpServletRequest request) {

		// 画面表示情報を取得
		CalendarViewForm viewForm = service.createViewForm();

		// テナント作成時のリダイレクトを判定
		HttpSession session = request.getSession();
		boolean isTenantCreate = SystemFlg.codeToBoolean(String.valueOf(session.getAttribute("tenantCreateFlg")));
		if (isTenantCreate) {
			// 一時的に保存したセッションを破棄する
			session.setAttribute("tenantCreateFlg", null);
			// 初回登録完了後は、「loiozようこそ」、GTMタグを表示する
			return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME).addObject("isTenantCreate", true);
		}

		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * メッセージ表示用初期画面表示処理
	 * 
	 * @param levelCd
	 * @param message
	 * @param attributes
	 * @return
	 */
	@RequestMapping(value = "/redirectIndexWithMsg", method = RequestMethod.GET)
	public ModelAndView redirectIndexWithMsg(
			@RequestParam("level") String levelCd,
			@RequestParam("message") String message,
			RedirectAttributes attributes) {

		String redirectUrl = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.index(null));

		RedirectViewBuilder builder = new RedirectViewBuilder(attributes, redirectUrl);
		return builder.build(MessageHolder.ofLevel(MessageLevel.of(levelCd), message));
	}

}