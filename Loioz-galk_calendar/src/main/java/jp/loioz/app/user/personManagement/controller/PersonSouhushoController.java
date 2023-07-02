package jp.loioz.app.user.personManagement.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.service.chohyo.CommonChohyoService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;

/**
 * 名簿管理画面-送付書選択モーダルのコントローラークラス
 */
@Controller
@RequestMapping("user/personManagement")
public class PersonSouhushoController extends DefaultController {

	/** 帳票の共通サービスクラス */
	@Autowired
	private CommonChohyoService commonChohyoService;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 送付書出力
	 *
	 * @param personId
	 * @param ankenId
	 * @param souhushoType
	 * @param response
	 */
	@RequestMapping(value = "/souhusho/outputSouhusho", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void docxOutputSouhusho(
			@RequestParam(name = "personId") Long personId,
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "souhushoType") String souhushoType,
			HttpServletResponse response) {

		try {

			if (CommonConstant.SouhushoType.IPPAN.equalsByCode(souhushoType)) {
				// 一般
				commonChohyoService.outputSouhushoIppan(response, personId, ankenId, null);

			} else if (CommonConstant.SouhushoType.ININN.equalsByCode(souhushoType)) {
				// 委任
				commonChohyoService.outputSouhushoInin(response, personId, ankenId, null);

			} else if (CommonConstant.SouhushoType.FAX_USE.equalsByCode(souhushoType)) {
				// FAX
				commonChohyoService.outputSouhushoFax(response, personId, ankenId, null);
			}

		} catch (Exception ex) {
			// 送付書の出力失敗
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName, ex);
		}
	}
}
