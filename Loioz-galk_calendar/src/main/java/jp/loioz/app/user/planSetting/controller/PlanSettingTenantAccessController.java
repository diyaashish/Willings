package jp.loioz.app.user.planSetting.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.planSetting.service.PlanSettingTenantAccessService;
import jp.loioz.common.aspect.annotation.PermitAlsoDisabledPlanStatusUser;
import jp.loioz.common.aspect.annotation.PermitOnlyManager;
import jp.loioz.common.constant.CookieConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.service.http.CookieService;

/**
 * プラン設定画面からテナント側（ログインSessionを持っている側）にアクセスして処理する用のコントローラークラス
 */
@Controller
@RequestMapping(UrlConstant.PLANSETTING_TENANT_ACCESS_URL)
public class PlanSettingTenantAccessController extends DefaultController {

	/** コントローラーに対応するサービスクラス */
	@Autowired
	private PlanSettingTenantAccessService service;
	
	/** Cookieを扱うサービスクラス */
	@Autowired
	private CookieService cookieService;
	
	/**
	 * ログインSessionの下記を最新の状態に更新する
	 * 
	 * <pre>
	 * ・planType
	 * ・isOnlyPlanSettingAccessible
	 * ・isFreePlanStatus
	 * ・isFreePlanExpiredAt
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@PermitAlsoDisabledPlanStatusUser
	@PermitOnlyManager
	@ResponseBody
	@RequestMapping(value = "updateSessionValueAfterPlanSave", method = RequestMethod.GET)
	public Map<String, Object> updateSessionValueAfterPlanSave(HttpServletRequest request, HttpServletResponse response) {
		
		Map<String, Object> responseMap = new HashMap<>();
		
		// リクエストから認証キー取得
		String cookieAuthKey = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_PLAN_SETTING_ACCESS_AUTH_KEY, request);
		
		// ※例外ハンドリングは行わない（基本的には不正アクセスでない限りは例外は発生しないため）
		service.updateSessionValueAfterPlanSave(cookieAuthKey);
		
		responseMap.put("succeeded", true);
		return responseMap;
	} 
	
}
