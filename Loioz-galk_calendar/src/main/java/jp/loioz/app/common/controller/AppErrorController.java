package jp.loioz.app.common.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.domain.UriService;

/**
 * エラーページコントローラ
 */
@Controller
public class AppErrorController implements ErrorController {

	@Autowired
	private UriService uriService;

	/** HttpServletRequest */
	@Autowired
	HttpServletRequest httpServletRequest;
	
	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;
	
	/**
	 * ログイン済みの際に存在しないリクエストパスへリクエストが来た場合に実行する処理。<br>
	 * ※マッピング名は、"/error" とすること（デフォルトの値と合わせるため）
	 */
	@RequestMapping(value = "/error")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ModelAndView error404() {
		
		// エラーが発生したもとのURL情報をWARNログに出力
		// （フレームワーク以外（実装者）がこのメソッドを実行する場合、RequestDispatcher.ERROR_REQUEST_URIの取得ができないため、pathに設定する値はnullになる。）
		String method = httpServletRequest.getMethod();
		Object errorRequestUriOjb = httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
		String errorRequestUri = String.valueOf(errorRequestUriOjb);
		
		String url = ServletUriComponentsBuilder.fromCurrentContextPath()
			.path(errorRequestUri)
			.build()
			.toUriString();
		
		logger.warn("AppErrorController#error404エラー発生URL：" + method + " " + url);
		
		return ModelAndViewUtils.getModelAndView("error/notFoundError");
	}

	/**
	 * 権限エラー
	 */
	@RequestMapping(UrlConstant.PERMISSION_ERROR_URL)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ModelAndView permissionError() {
		ModelAndView model = ModelAndViewUtils.getModelAndView("error/permissionError");
		String subDomain = SessionUtils.getSubDomain();
		model.addObject("returnUrl", uriService.getLoginSuccessUrl(subDomain));
		return model;
	}

	/**
	 * プラン機能制限エラー
	 */
	@RequestMapping(UrlConstant.PLAN_FUNC_RESTRICT_ERROR_URL)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ModelAndView planFuncRestrictError() {
		ModelAndView model = ModelAndViewUtils.getModelAndView("error/planFuncRestrictError");
		String subDomain = SessionUtils.getSubDomain();
		model.addObject("returnUrl", uriService.getLoginSuccessUrl(subDomain));
		return model;
	}
	
	/**
	 * 利用中にプランが変更した旨のエラー
	 */
	@RequestMapping(UrlConstant.PLAN_CHANGED_ERROR_URL)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ModelAndView planChangedError() {
		ModelAndView model = ModelAndViewUtils.getModelAndView("error/planChangedError");
		String subDomain = SessionUtils.getSubDomain();
		model.addObject("returnUrl", uriService.getLoginSuccessUrl(subDomain));
		return model;
	}
	
	/**
	 * 二重ログインエラー
	 */
	@RequestMapping(UrlConstant.MULTI_LOGIN_ERROR_URL)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ModelAndView multiLoginError() {
		return ModelAndViewUtils.getModelAndView("error/multiLoginError");
	}

	/**
	 * システムエラー
	 */
	@RequestMapping("/systemError")
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView systemError() {
		return ModelAndViewUtils.getModelAndView("error");
	}

	/**
	 * getErrorPath()をOverrideするだけ 特に処理は必要ありません
	 */
	@Override
	public String getErrorPath() {
		return "";
	}
}
