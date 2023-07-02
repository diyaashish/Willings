package jp.loioz.app.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.loioz.app.common.DefaultController;

/**
 * 静的コンテンツのコントローラークラス
 */
@Controller
public class StaticContentsController extends DefaultController {

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 利用規約
	 */
	@RequestMapping(value = "/user/static/terms", method = RequestMethod.GET)
	public String terms() {
		return "common/static/terms";
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
