package jp.loioz.app.user.toiawase.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.toiawase.service.ToiawaseFileService;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.exception.AppException;

/**
 * 問い合わせ-添付コントローラークラス
 */
@Controller
@RequestMapping("user/toiawase")
public class ToiawaseFileController extends DefaultController {

	/** 問い合わせファイルサービス */
	@Autowired
	private ToiawaseFileService fileService;

	/**
	 * 問い合わせ-添付のダウンロード
	 *
	 * @param toiawaseAttachmentSeq
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value = "/attachmentDownLoad", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void attachmentDownLoad(@RequestParam Long toiawaseAttachmentSeq, HttpServletResponse response) {

		try {
			// ファイルダウンロード処理
			fileService.fileDownloadFromAmazonS3(toiawaseAttachmentSeq, response);

		} catch (AppException e) {
			// エラー発生時の処理はサービス側で設定済なので何もしない
		}
	}

}
