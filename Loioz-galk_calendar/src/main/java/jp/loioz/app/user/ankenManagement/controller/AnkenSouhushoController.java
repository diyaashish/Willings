package jp.loioz.app.user.ankenManagement.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.service.chohyo.CommonChohyoService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;

/**
 * 案件管理画面-送付書選択モーダルのコントローラークラス
 */
@Controller
@RequestMapping("user/ankenManagement")
public class AnkenSouhushoController extends DefaultController {

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
	 * @param ankenId
	 * @param personId
	 * @param souhushoType
	 * @param response
	 */
	@RequestMapping(value = "/souhusho/outputSouhusho", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputSouhusho(
			@RequestParam(name = "ankenId", required = false) Long ankenId,
			@RequestParam(name = "personId") Long personId,
			@RequestParam(name = "souhushoType") String souhushoType,
			HttpServletResponse response) {

		// 名簿情報が削除されている場合は排他メッセージを表示する
		if (commonChohyoService.getPersonEntity(personId) == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00025));
			return;
		}

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

	/**
	 * 送付書出力(捜査機関)
	 *
	 * @param ankenId
	 * @param sosakikanSeq
	 * @param souhushoType
	 * @param response
	 */
	@RequestMapping(value = "/souhusho/outputSouhushoSosakikan", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputSouhushoSosakikan(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "sosakikanSeq") Long sosakikanSeq,
			@RequestParam(name = "souhushoType") String souhushoType,
			HttpServletResponse response) {

		try {

			// 出力する送付書タイプを判定
			if (CommonConstant.SouhushoType.IPPAN.equalsByCode(souhushoType)) {
				// 一般用
				commonChohyoService.outputSouhushoIppanForSosaKikan(response, ankenId, sosakikanSeq);

			} else if (CommonConstant.SouhushoType.FAX_USE.equalsByCode(souhushoType)) {
				// FAX用
				commonChohyoService.outputSouhushoFaxForSosaKikan(response, ankenId, sosakikanSeq);

			} else {
				// 何もしない
			}

		} catch (Exception ex) {
			// 送付書の出力失敗
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName, ex);
		}

	}
}
