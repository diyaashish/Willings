package jp.loioz.app.user.preLogin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.preLogin.form.PreLoginForm;
import jp.loioz.app.user.preLogin.service.PreLoginService;
import jp.loioz.common.aspect.annotation.PermitAlsoDisabledPlanStatusUser;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.exception.AppException;

/**
 * プレログイン画面用のコントローラークラス
 */
@Controller
@RequestMapping(UrlConstant.PRE_LOGIN_URL)
public class PreLoginController extends DefaultController {

	/** コントローラに対応するviewパス **/
	private static final String MY_VIEW_PATH = "user/preLogin/preLogin";

	/** viewで使用するフォームオブジェクト名 **/
	private static final String VIEW_FORM_NAME = "viewForm";
	
	/** 画面サービス */
	@Autowired
	private PreLoginService preLoginService;
	
	/**
	 * 初期表示
	 * 
	 * @return
	 */
	@PermitAlsoDisabledPlanStatusUser
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {

		PreLoginForm viewForm = new PreLoginForm();
		
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}
	
	/**
	 * フォームの事務所IDをもとに、ログイン画面のURLを取得する
	 * 
	 * 
	 * @param form
	 * @param result
	 * @param gRecaptchaResponse
	 * @return
	 * @throws AppException 
	 */
	@ResponseBody
	@PermitAlsoDisabledPlanStatusUser
	@RequestMapping(value = "/getLoginUrl", method = RequestMethod.GET)
	public Map<String, Object> getLoginUrl(@Validated PreLoginForm form, BindingResult result,
			@RequestParam(name = "g-recaptcha-response") String gRecaptchaResponse) throws AppException {
		
		// レスポンスの作成
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", null);
			response.put("errors", result.getFieldErrors());
			return response;
		}
		
		try {
			// reCAPTCHAのレスポンスデータの妥当性チェック
			preLoginService.verifyRecaptchaResponse(gRecaptchaResponse);
		} catch(AppException e) {
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
		
		String subDomain = form.getJimushoId();
		String loginUrl = preLoginService.getLoginUrl(subDomain);
		
		response.put("succeeded", true);
		response.put("loginUrl", loginUrl);
		
		return response;
	}
	
}
