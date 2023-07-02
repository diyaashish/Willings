package jp.loioz.app.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.loioz.app.common.form.FunctionalLazyLoadForm;
import jp.loioz.app.common.form.LazyLoadForm;
import jp.loioz.app.common.form.LoginHeaderForm;
import jp.loioz.app.common.form.WrapHeaderForm;
import jp.loioz.app.common.service.LoginHeaderService;
import jp.loioz.app.common.service.WrapHeaderService;
import jp.loioz.app.user.info.form.InfoViewForm;
import jp.loioz.common.utility.SessionUtils;

/**
 * ユーザ側画面の共通ヘッダ設定
 */
@ControllerAdvice("jp.loioz.app.user")
public class UserHeaderControllerAdvice {

	/** ログインヘッダーのフォーム名 */
	public static final String LOGIN_HEADER_FORM_NAME = "loginHeaderForm";

	@Autowired
	private LoginHeaderService service;

	/** wrap内ヘッダーサービスクラス */
	@Autowired
	private WrapHeaderService wrapHeaderService;

	/**
	 * ログインヘッダーの取得処理
	 *
	 * @param model
	 */
	@ModelAttribute
	public void loginHeaderForm(Model model) {
		if (SessionUtils.isAlreadyLoggedUser()) {
			Long accountSeq = SessionUtils.getLoginAccountSeq();
			LazyLoadForm<LoginHeaderForm> loginHeaderForm = new LazyLoadForm<>(() -> service.createLoginHeaderCountForm(accountSeq));
			model.addAttribute(LOGIN_HEADER_FORM_NAME, loginHeaderForm);

			LazyLoadForm<InfoViewForm> infoForm = new LazyLoadForm<>(() -> service.createInfoForm(accountSeq));
			model.addAttribute("infoForm", infoForm);
		}
	}

	/**
	 * 顧客軸ヘッダーの取得処理
	 *
	 * @return
	 */
	@ModelAttribute("wrapCustomerHeader")
	public FunctionalLazyLoadForm<Long, WrapHeaderForm> wrapCustomerHeader() {
		if (SessionUtils.isAlreadyLoggedUser()) {
			FunctionalLazyLoadForm<Long, WrapHeaderForm> wrapCustomerHeader = new FunctionalLazyLoadForm<>((customerId) -> {
				return wrapHeaderService.createWrapCustomerHeaderForm(customerId);
			});
			return wrapCustomerHeader;
		} else {
			return null;
		}
	}

	/**
	 * 案件軸ヘッダーの取得処理
	 *
	 * @return
	 */
	@ModelAttribute("wrapAnkenHeader")
	public FunctionalLazyLoadForm<Long, WrapHeaderForm> wrapAnkenHeader() {
		if (SessionUtils.isAlreadyLoggedUser()) {
			FunctionalLazyLoadForm<Long, WrapHeaderForm> wrapAnkenHeader = new FunctionalLazyLoadForm<>((ankenId) -> {
				return wrapHeaderService.createWrapAnkenHeaderForm(ankenId);
			});
			return wrapAnkenHeader;
		} else {
			return null;
		}
	}

}