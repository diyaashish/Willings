
package jp.loioz.app.user.azukariItem.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.azukariItem.form.AzukariItemDownloadForm;
import jp.loioz.app.user.azukariItem.service.AzukariItemDocService;
import jp.loioz.common.constant.OutputConstant;

/**
 * 預り品出力画面のcontrollerクラス
 */
@Controller
@RequestMapping("user/azukariItem/document")
public class AzukariItemDocController extends DefaultController {

	/* 預り品一覧のserviceクラス */
	@Autowired
	private AzukariItemDocService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 預かり証出力
	 *
	 * @param azukariSeqList
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/azukari", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void createAzukariSho(
			HttpServletResponse response, AzukariItemDownloadForm azukariItemDownloadForm) {

		try {
			service.createAzukariSho(response, azukariItemDownloadForm);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 受領証出力
	 *
	 * @param azukariSeqLists
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/juryo", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void createJuryoSho(
			HttpServletResponse response, AzukariItemDownloadForm azukariItemDownloadForm) {
		try {
			service.createJuryoSho(response, azukariItemDownloadForm);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}