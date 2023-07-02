package jp.loioz.app.common.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import jp.loioz.app.common.form.PagerForm;

/**
 * ページング機能を持つ画面のコントローラー
 */
public interface PageableController {

	@InitBinder
	public default void initPagerParamBinder(WebDataBinder binder, HttpServletRequest request) {
		bindDefaultPagerParam(binder, request);
	}

	/**
	 * ページング条件のデフォルト値をバインドする
	 *
	 * @param binder
	 * @param request
	 */
	public default void bindDefaultPagerParam(WebDataBinder binder, HttpServletRequest request) {
		// ページ番号がリクエストパラメータに存在しない場合はデフォルト値を設定する
		Object page = request.getParameter(getPageRequestParamName());
		if (page == null) {
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.add(getPageFormPropertyName(), PagerForm.DEFAULT_PAGE);
			binder.bind(pvs);
		}
	}

	/**
	 * ページ番号のリクエストパラメータ名を取得する
	 *
	 * @return リクエストパラメータ名
	 */
	public default String getPageRequestParamName() {
		return "page";
	}

	/**
	 * ページ番号のフォームプロパティ名を取得する
	 *
	 * @return フォームプロパティ名
	 */
	public default String getPageFormPropertyName() {
		return "page";
	}
}
