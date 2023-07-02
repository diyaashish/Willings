package jp.loioz.app.user.personCase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.personCase.form.PersonCaseViewForm;
import jp.loioz.app.user.personCase.service.PersonCaseService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 名簿-案件のコントローラークラス
 */
@Controller
@RequestMapping("user/person/{personId}/case")
public class PersonCaseController extends DefaultController {

	/** コントローラと対応するviewパス **/
	public static final String MY_VIEW_PATH = "user/personCase/personCase";

	/** viewで使用するフォームオブジェクト名 **/
	public static final String VIEW_FORM_NAME = "viewForm";

	// 顧客共通ヘッダー
	/** 顧客共通ヘッダーのフラグメントパス */
	private static final String CUSTOMER_ANKENSELECTED_FRAGMENT_PATH = "common/customerAnkenSelected::detailInfo";

	/** 名簿-案件のサービスクラス */
	@Autowired
	private PersonCaseService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable Long personId) {

		// viewFormの作成
		PersonCaseViewForm viewForm = service.createViewForm(personId);

		// 画面情報をリターン
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	// ▼ サイドメニュー

	/**
	 * 顧客案件の共通ヘッダー部分を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/getCustomerAnkenSelectedView", method = RequestMethod.GET)
	public ModelAndView getCustomerAnkenSelectedView(@RequestParam(name = "personId") Long personId) {

		ModelAndView mv = null;

		try {

			// サイドメニュー画面の取得
			mv = ModelAndViewUtils.getModelAndView(CUSTOMER_ANKENSELECTED_FRAGMENT_PATH);
			// パラメータ情報を設定
			mv.addObject("wrapHeaderCustomerId", personId);

		} catch (Exception ex) {
			// 上記のメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// メソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

}
