package jp.loioz.app.user.info.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.info.service.InfoReadHistoryService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.SessionUtils;

/**
 * お知らせ既読履歴のコントローラークラス
 */
@Controller
@RequestMapping("user/information")
public class InfoReadHistoryController extends DefaultController {

	/* お知らせ既読履歴のserviceクラス */
	@Autowired
	private InfoReadHistoryService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 登録処理
	 *
	 * @param infoMgtSeq
	 * @param msgHolder
	 * @return 遷移先の画面
	 */
	@ResponseBody
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	public Map<String, Object> regist(@RequestParam(name = "infoMgtSeq") Long infoMgtSeq, MessageHolder msgHolder) {

		Map<String, Object> response = new HashMap<>();
		if (infoMgtSeq == null) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00059));
			return response;
		}

		try {
			// 保存処理
			service.save(SessionUtils.getLoginAccountSeq(), infoMgtSeq);
			response.put("succeeded", true);
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}
}