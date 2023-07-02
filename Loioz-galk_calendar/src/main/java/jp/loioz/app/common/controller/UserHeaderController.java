package jp.loioz.app.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.form.LoginHeaderForm;
import jp.loioz.app.common.form.userHeader.HeaderSearchListSearchForm;
import jp.loioz.app.common.form.userHeader.HeaderSearchListViewForm;
import jp.loioz.app.common.service.LoginHeaderService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.ViewPathAndAttrConstant;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;

/**
 * ユーザ側画面の共通ヘッダ設定
 */
@Controller
@RequestMapping(value = "common/userheader")
public class UserHeaderController extends DefaultController {

	/** CommonLayout UserHeaderのフラグメントViewPath */
	private final static String USER_HEADER_VIEW_PATH = "commonLayoutFragment::loginHeaderFragmentUseSessionVal";

	/** ヘッダー検索フラグメント画面オブジェクト */
	private final static String USER_HEADER_SEARCH_FRAGMENT_SEARCH_FORM = "headerSearchListSearchForm";

	/** ヘッダー検索フラグメントパス */
	private final static String USER_HEADER_SEARCH_FRAGMENT_PATH = ViewPathAndAttrConstant.USER_HEADER_SEARCH_FRAGMENT_PATH;
	/** ヘッダー検索フラグメント画面オブジェクト */
	private final static String USER_HEADER_SEARCH_FRAGMENT_VIEW_FORM = ViewPathAndAttrConstant.USER_HEADER_SEARCH_FRAGMENT_VIEW_FORM;

	@Autowired
	private LoginHeaderService service;

	/**
	 * UserHeaderの取得
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getUserHeader", method = RequestMethod.GET)
	public ModelAndView getUserHeader() {

		// Viewを作成
		ModelAndView mv = ModelAndViewUtils.getModelAndView(USER_HEADER_VIEW_PATH);

		// アカウントSEQを取得
		Long accountSeq = SessionUtils.getLoginAccountSeq();

		// ヘッダーフォームを作成
		LoginHeaderForm loginHeaderForm = service.createLoginHeaderCountForm(accountSeq);
		mv.addObject(UserHeaderControllerAdvice.LOGIN_HEADER_FORM_NAME, loginHeaderForm);

		// ※ お知らせ情報はHeader内にコンテンツがないので当メソッドでは不要
		// 必要なオブジェクトが増えた場合、追加していく

		return mv;
	}

	/**
	 * ヘッダー検索フラグメントの取得
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getUserHeaderSearchFragment", method = RequestMethod.GET)
	public ModelAndView getUserHeaderSearchFragment(@ModelAttribute(USER_HEADER_SEARCH_FRAGMENT_SEARCH_FORM) HeaderSearchListSearchForm searchForm) {

		// Viewを作成
		ModelAndView mv = null;

		// 画面オブジェクトを作成する
		HeaderSearchListViewForm viewForm = service.getSearchResultHeaderSearchListViewForm(searchForm);

		// 画面ビューの作成
		mv = getMyModelAndView(viewForm, USER_HEADER_SEARCH_FRAGMENT_PATH, USER_HEADER_SEARCH_FRAGMENT_VIEW_FORM);
		if (mv == null) {
			this.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		// 正常終了
		String requestUuid = searchForm.getRequestUuid();
		this.setAjaxProcResultSuccess(requestUuid);
		return mv;
	}

}