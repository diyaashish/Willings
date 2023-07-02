package jp.loioz.app.common.mvc.accgSendFilePopover.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.mvc.accgSendFilePopover.form.AccgSendFilePopoverViewForm;
import jp.loioz.app.common.mvc.accgSendFilePopover.service.AccgSendFilePopoverService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.exception.AppException;

/**
 * 会計送付ファイルポップオーバーのコントローラー
 */
@Controller
@RequestMapping(value = "common/mvc/accgSendFilePopover")
public class AccgSendFilePopoverController extends DefaultController {

	/** 会計送付ファイルポップオーバーフラグメントフラグメントパス */
	private static final String ACCG_SEND_FILE_POPOVER_FRAGMENT_VIEW_PATH = "common/mvc/accgSendFilePopover/popover::accgSendFileActivityPopoverFragment";
	/** 会計送付ファイルポップオーバー表示フォーム名 */
	private static final String ACCG_SEND_FILE_POPOVER_FRAGMENT_VIEW_FORM_NAME = "accgSendFileActivityPopoverViewForm";

	/** 画面サービス */
	@Autowired
	private AccgSendFilePopoverService service;

	/**
	 * 会計送付ファイルポップオーバーの取得
	 * 
	 * @param accgDocActSendSeq
	 * @return
	 */
	@RequestMapping(value = "/getAccgSendFilePopoverFragment", method = RequestMethod.GET)
	public ModelAndView getAccgSendFilePopoverFragment(@RequestParam(name = "accgDocActSendSeq") Long accgDocActSendSeq) {

		ModelAndView mv = null;

		try {
			AccgSendFilePopoverViewForm viewForm = service.createAccgSendFilePopoverViewForm(accgDocActSendSeq);
			mv = getMyModelAndView(viewForm, ACCG_SEND_FILE_POPOVER_FRAGMENT_VIEW_PATH, ACCG_SEND_FILE_POPOVER_FRAGMENT_VIEW_FORM_NAME);

		} catch (AppException ex) {
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return null;
		}

		if (mv == null) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 送付済みファイルのダウンロード処理
	 * 
	 * @param accgDocFileSeq
	 * @param response
	 */
	@RequestMapping(value = "/downloadSendFile", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void downloadSendFile(@RequestParam("accgDocFileSeq") Long accgDocFileSeq, HttpServletResponse response) {
		try {
			// ダウンロード処理
			service.downloadSendFile(accgDocFileSeq, response);

		} catch (AppException ex) {
			// エラーメッセージの設定
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType(), ex.getMessageArgs()));
		}
	}

}
