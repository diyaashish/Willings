package jp.loioz.app.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.form.FunctionalLazyLoadForm;
import jp.loioz.app.common.form.WrapHeaderForm;
import jp.loioz.app.common.service.WrapHeaderService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.SessionAttrKeyEnum;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;

/**
 * ヘッダー（パンくず）部分に関するコントローラークラス
 */
@Controller
@RequestMapping(value = "common/wrapHeader")
public class WrapHeaderController extends DefaultController {

	/** wrap内ヘッダーサービスクラス */
	@Autowired
	private WrapHeaderService wrapHeaderService;
	
	/** ヘッダー（パンくず）部分のfragmentのパス */
	private static final String WRAP_HEADER_FRAGMENT_PATH = "common/wrapHeader::wrapHeader";
	
	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;
	
	/**
	 * ヘッダー（パンくず）部分を取得する
	 * 
	 * @param sideMenuPersonId
	 * @param sideMenuAnkenId
	 * @param selectedTabClass
	 * @return
	 */
	@RequestMapping(value = "getWrapHeaderView", method = RequestMethod.GET)
	public ModelAndView getWrapHeaderView(
			@RequestParam(name = "sideMenuPersonId", required = false) Long sideMenuPersonId,
			@RequestParam(name = "sideMenuAnkenId", required = false) Long sideMenuAnkenId,
			@RequestParam(name = "selectedTabClass", required = false) String selectedTabClass) {
		
		ModelAndView mv = null;

		try {

			// パンくず部分の取得
			mv = ModelAndViewUtils.getModelAndView(WRAP_HEADER_FRAGMENT_PATH);
			
			// パラメータ情報を設定
			mv.addObject("returnListScreen", SessionUtils.getSessionVaule(SessionAttrKeyEnum.RETURN_LIST_SCREEN));
			mv.addObject("returnPrevScreenName", SessionUtils.getSessionVaule(SessionAttrKeyEnum.RETURN_PREV_SCREEN_NAME));
			mv.addObject("returnPrevScreenUrl", SessionUtils.getSessionVaule(SessionAttrKeyEnum.RETURN_PREV_SCREEN_URL));
			mv.addObject("selectedTabClass", selectedTabClass);
			
			if (sideMenuPersonId != null) {
				// 顧客軸の場合
				mv.addObject("sideMenuPersonId", sideMenuPersonId);
				
				FunctionalLazyLoadForm<Long, WrapHeaderForm> wrapCustomerHeader = new FunctionalLazyLoadForm<>((customerId) -> {
					return wrapHeaderService.createWrapCustomerHeaderForm(customerId);
				});
				mv.addObject("wrapCustomerHeader", wrapCustomerHeader);
			}
			
			if (sideMenuAnkenId != null) {
				// 案件軸の場合
				mv.addObject("sideMenuAnkenId", sideMenuAnkenId);
				
				FunctionalLazyLoadForm<Long, WrapHeaderForm> wrapAnkenHeader = new FunctionalLazyLoadForm<>((ankenId) -> {
					return wrapHeaderService.createWrapAnkenHeaderForm(ankenId);
				});
				mv.addObject("wrapAnkenHeader", wrapAnkenHeader);
			}

		} catch (Exception ex) {
			// 上記のメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// メソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
		
	}

}
