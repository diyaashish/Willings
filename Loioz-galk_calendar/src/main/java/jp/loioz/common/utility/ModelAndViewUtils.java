package jp.loioz.common.utility;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.function.Function;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * ModelAndView用のUtilクラス。
 */
public class ModelAndViewUtils {

	/**
	 * ModelAndViewオブジェクトを取得する
	 *
	 * @param viewName 返却するviewファイル名
	 * @return 画面情報
	 */
	public static ModelAndView getModelAndView(String viewName) {
		return new ModelAndView(viewName);
	}

	/**
	 * リダイレクト用のModelAndViewオブジェクトを取得する
	 *
	 * @param redirectPath リダイレクトパス
	 * @return 画面情報
	 */
	public static ModelAndView getRedirectModelAndView(String redirectPath) {
		RedirectView rv = new RedirectView(redirectPath);
		return new ModelAndView(rv);
	}

	/**
	 * リダイレクト用のModelAndViewオブジェクトを取得する
	 *
	 * @param controllerType コントローラークラス
	 * @param invocation メソッド呼び出し情報
	 *            ({@link MvcUriComponentsBuilder#fromMethodCall(Object)} を参照)
	 * @return 画面情報
	 */
	public static <T> ModelAndView getRedirectModelAndView(Class<T> controllerType, Function<T, ModelAndView> invocation) {
		String redirectPath = getRedirectPath(controllerType, invocation);
		return getRedirectModelAndView(redirectPath);
	}

	/**
	 * 共通エラー画面のModelAndViewオブジェクトを取得する
	 *
	 * @return 画面情報
	 */
	public static ModelAndView getCommonErrorPage() {
		return ModelAndViewUtils.getRedirectModelAndView("/error");
	}

	/**
	 * コントローラーメソッドの呼び出し情報からリダイレクトパスを取得する
	 *
	 * @param controllerType コントローラークラス
	 * @param invocation メソッド呼び出し情報
	 *            ({@link MvcUriComponentsBuilder#fromMethodCall(Object)} を参照)
	 * @return リダイレクトパス
	 */
	public static <T> String getRedirectPath(Class<T> controllerType, Function<T, ModelAndView> invocation) {
		return MvcUriComponentsBuilder.relativeTo(UriComponentsBuilder.fromPath("/"))
			.withMethodCall(invocation.apply(on(controllerType)))
			.toUriString();
	}
}
