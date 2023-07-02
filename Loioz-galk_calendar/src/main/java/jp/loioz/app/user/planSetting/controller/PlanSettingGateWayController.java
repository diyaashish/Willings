package jp.loioz.app.user.planSetting.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.planSetting.service.PlanSettingGateWayService;
import jp.loioz.common.aspect.annotation.PermitAlsoDisabledPlanStatusUser;
import jp.loioz.common.aspect.annotation.PermitOnlyManager;
import jp.loioz.common.constant.CookieConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.service.http.CookieService;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.domain.UriService;

/**
 * プラン設定画面の入り口を提供するコントローラークラス
 */
@Controller
@RequestMapping(UrlConstant.PLANSETTING_GATEWAY_URL)
public class PlanSettingGateWayController extends DefaultController {

	/** プラン設定画面側で認証エラーが発生した場合に返却するviewのパス */
	private static final String AUTH_ERROR_VIEW_PATH = "user/planSetting/error/planSettingAuthError";
	
	/** URIを扱うサービスクラス */
	@Autowired
	private UriService uriService;

	/** Cookieを扱うサービスクラス */
	@Autowired
	private CookieService cookieService;
	
	/** プラン設定画面の入り口を提供するControllerに対応するサービスクラス */
	@Autowired
	private PlanSettingGateWayService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * プラン画面へアクセスする準備を行い、プラン画面へのアクセスURLをクライアントに返却するメソッド
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@PermitOnlyManager
	@ResponseBody
	@RequestMapping(value = PlanConstant.PLAN_SETTING_GATEWAY_PATH, method = RequestMethod.GET)
	public Map<String, Object> goRegistTab(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> responseMap = new HashMap<>();

		final String PLAN_SETTING_TAB_DOMAIN = uriService.getTenantDomain(PlanConstant.PLAN_SETTING_SUB_DOMAIN);

		String uriUntilContextPath = ServletUriComponentsBuilder.fromCurrentContextPath()
				.host(PLAN_SETTING_TAB_DOMAIN)
				.toUriString();

		responseMap.put("uriUntilContextPath", uriUntilContextPath);
		
		String authKey = "";
		try {
			// Session情報をプラン画面に渡すためにDBに登録
			authKey = service.storeSessionInfoInDB();
		} catch (Exception ex) {
			responseMap.put("succeeded", false);
			return responseMap;
		}
		
		// 認証キーをCookieに設定
		cookieService.addCookieForHttp(CookieConstant.COOKIE_NAME_OF_PLAN_SETTING_ACCESS_AUTH_KEY, authKey, null, response);
		
		responseMap.put("succeeded", true);
		return responseMap;
	}
	
	/**
	 * プラン画面へアクセスする準備を行い、リダイレクトでプラン画面へアクセスさせるメソッド。
	 * <pre>
	 * 主にログイン直後にプラン画面へ直接アクセスするケースのためのものため、@PermitAlsoDisabledPlanStatusUserを付与し、
	 * 無料期間が過ぎたり、解約の期限が切れたユーザーもアクセス可能とする。
	 * </pre>
	 * 
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@PermitAlsoDisabledPlanStatusUser
	@PermitOnlyManager
	@RequestMapping(value = PlanConstant.PLAN_SETTING_GATEWAY_REDIRECT_PATH, method = RequestMethod.GET)
	public void prepareAndRedirectPlanSetting(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		// Session情報をプラン画面に渡すためにDBに登録
		String authKey;
		try {
			authKey = service.storeSessionInfoInDB();
		} catch (AppException e) {
			// 楽観ロックエラーなどが発生しても、redirectではエラーメッセージの表示などができないためシステムエラーとする。
			throw new RuntimeException(e);
		}
		
		// 認証キーをCookieに設定
		cookieService.addCookieForHttp(CookieConstant.COOKIE_NAME_OF_PLAN_SETTING_ACCESS_AUTH_KEY, authKey, null, response);
		
		// プラン画面へリダイレクト
		final String PLAN_SETTING_TAB_DOMAIN = uriService.getTenantDomain(PlanConstant.PLAN_SETTING_SUB_DOMAIN);
		String uriUntilContextPath = ServletUriComponentsBuilder.fromCurrentContextPath()
				.host(PLAN_SETTING_TAB_DOMAIN)
				.toUriString();
		String plansettingUrl = uriUntilContextPath + "/" + UrlConstant.PLANSETTING_URL + "/";
		
		response.sendRedirect(plansettingUrl);
	}
	
	/**
	 * プラン画面での認証エラー画面を返却する<br>
	 * 
	 * ※このメソッドはURL直アクセスでの認証エラーも考慮し、未ログイン状態でのアクセスも可能とする（WebSecurityConfig.javaで定義）
	 * 
	 * @return
	 */
	@PermitAlsoDisabledPlanStatusUser
	@RequestMapping(value = PlanConstant.PLAN_SETTING_AUTH_ERROR_PATH, method = RequestMethod.GET)
	public ModelAndView planSettingAuthError() {
		return ModelAndViewUtils.getModelAndView(AUTH_ERROR_VIEW_PATH);
	}
}
