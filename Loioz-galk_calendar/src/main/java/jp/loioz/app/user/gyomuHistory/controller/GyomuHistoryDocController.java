package jp.loioz.app.user.gyomuHistory.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.gyomuHistory.service.GyomuHistoryDocService;
import jp.loioz.common.constant.OutputConstant;

@Controller
@RequestMapping("user/gyomuHistory")
public class GyomuHistoryDocController extends DefaultController {

	/** 業務履歴出力用サービスクラス **/
	@Autowired
	private GyomuHistoryDocService docService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 業務履歴一覧 Excel出力
	 *
	 * @param session
	 * @param response
	 */
	@RequestMapping(value = "/excel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void excelGyomuHistory(HttpSession session, HttpServletResponse response,
			@RequestParam(name = "ankenId", required = false) Long ankenId,
			@RequestParam(name = "ankenName", required = false) String ankenName,
			@RequestParam(name = "customerId", required = false) Long customerId,
			@RequestParam(name = "customerName", required = false) String customerName,
			@RequestParam(name = "transitionType", required = true) String transitionType,
			@RequestParam(name = "gyomuHistorySeqs", required = true) List<Long> gyomuHistorySeqs) {
		try {
			docService.excel(response, gyomuHistorySeqs, ankenId, ankenName, customerId, customerName, transitionType);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
