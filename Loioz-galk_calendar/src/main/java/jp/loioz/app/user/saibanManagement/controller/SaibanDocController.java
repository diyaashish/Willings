package jp.loioz.app.user.saibanManagement.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.saibanManagement.service.SaibanDocService;
import jp.loioz.common.constant.OutputConstant;

/**
 * 裁判管理・送付書出力のコントローラークラス
 */
@Controller
@RequestMapping(value = "user/saibanManagement/{ankenId}/{branchNumber}")
public class SaibanDocController extends DefaultController {

	/** サービスクラス */
	@Autowired
	private SaibanDocService service;

	/**
	 * 口頭弁論期日請書を出力する
	 *
	 * @param searchForm
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/doc/kotobenron", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void docKoToBenron(HttpServletResponse response, @PathVariable Long ankenId, @PathVariable Long branchNumber,
			@RequestParam(name = "scheduleSeq") Long scheduleSeq) {

		// 出力処理
		service.docKotoBenron(response, ankenId, branchNumber, scheduleSeq);
	}

}