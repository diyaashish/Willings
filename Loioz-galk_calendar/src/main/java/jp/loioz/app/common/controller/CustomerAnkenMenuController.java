package jp.loioz.app.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.loioz.common.constant.SessionAttrKeyEnum;
import jp.loioz.common.utility.SessionUtils;

/**
 * 案件顧客メニューに関するコントローラークラス
 */
@Controller
@RequestMapping(value = "common/customerAnkenMenu")
public class CustomerAnkenMenuController {

	/**
	 * 顧客案件メニューの開閉プロパティ（クリック）をSessionに保存する
	 * 
	 * @param ankenMenuOpenClick
	 */
	@ResponseBody
	@RequestMapping(value = "/setCustomerAnkenMenuOpenClickForSession", method = RequestMethod.POST)
	public void setCustomerAnkenMenuOpenClickForSession(@RequestParam(name = "customerAnkenMenuOpenClick") boolean customerAnkenMenuOpenClick) {
		// セッションに案件メニューの開閉状態を保持する
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.CUSTOMER_ANKEN_MENU_OPEN_CLICK, String.valueOf(customerAnkenMenuOpenClick));
		// リサイズの開閉状態はブランクにする
		// SessionUtils.setSessionVaule(SessionAttrKeyEnum.CUSTOMER_ANKEN_MENU_OPEN_RESIZE, "");
	}

	/**
	 * 顧客案件メニューの開閉プロパティ（クリック）をSession値を破棄する
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteCustomerAnkenMenuOpenClickForSession", method = RequestMethod.POST)
	public void deleteCustomerAnkenMenuOpenClickForSession() {
		// セッションに案件メニューの開閉状態を保持する
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.CUSTOMER_ANKEN_MENU_OPEN_CLICK, "");
	}
	
	/**
	 * 顧客案件メニューの開閉プロパティ（リサイズ）をSessionに保存する
	 * 
	 * @param ankenMenuOpenResize
	 */
	@ResponseBody
	@RequestMapping(value = "/setCustomerAnkenMenuOpenResizeForSession", method = RequestMethod.POST)
	public void setCustomerAnkenMenuOpenResizeForSession(@RequestParam(name = "customerAnkenMenuOpenResize") boolean customerAnkenMenuOpenResize) {
		// セッションに案件メニューの開閉状態（リサイズ）を保持する
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.CUSTOMER_ANKEN_MENU_OPEN_RESIZE, String.valueOf(customerAnkenMenuOpenResize));
	}

}
