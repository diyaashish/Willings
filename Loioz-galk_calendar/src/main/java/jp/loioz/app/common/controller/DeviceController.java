package jp.loioz.app.common.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.user.schedule.controller.ScheduleController;
import jp.loioz.common.constant.CommonConstant.ViewType;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;

/**
 * スマホ画面関連のController
 */
@Controller
@RequestMapping(value = "common")
public class DeviceController {

	/** ロガー */
	@Autowired
	private Logger log;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * スマホ / PC版の切り替え
	 */
	@RequestMapping(value = "/changeViewType/{viewType}", method = RequestMethod.GET)
	public ModelAndView changeViewType(@PathVariable String viewType, HttpServletRequest request) {

		ViewType type = ViewType.of(viewType);
		if (type == null) {
			// 種別取得できない場合、PC版への切り替えとする
			type = ViewType.PC;

			// URLに含まれるviewType値がEnum値ではない。もしくは取得できない場合はloggerには出力
			log.warn("画面表示種別の再設定時に種別が判別できませんでした。");
		}

		// viewTypeをセッションの保存
		SessionUtils.setViewType(type);

		if (type == ViewType.MOBILE) {
			// Mobile -> 対応している画面はスケジュールしかないので、スケジュール画面に遷移
			return ModelAndViewUtils.getRedirectModelAndView(ScheduleController.class, controller -> controller.index(request));
		} else {
			// PC -> 遷移元の画面にリダイレクト
			String referer = request.getHeader(HttpHeaders.REFERER);
			return ModelAndViewUtils.getRedirectModelAndView(referer);
		}
	}

}
