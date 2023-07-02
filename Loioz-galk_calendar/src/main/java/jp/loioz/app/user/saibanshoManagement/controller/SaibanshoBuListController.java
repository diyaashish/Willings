package jp.loioz.app.user.saibanshoManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.saibanshoManagement.form.SaibanshoBuListForm;
import jp.loioz.app.user.saibanshoManagement.service.SaibanshoBuListService;
import jp.loioz.common.aspect.annotation.DenyAccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountKengen;

@Controller
@RequestMapping("user/saibanshoBuManagement")
@DenyAccountKengen(value = AccountKengen.GENERAL)
public class SaibanshoBuListController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/saibanshoManagement/saibanshoBuList::keizokubuModal";

	/** viewのformで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 裁判所部のサービスクラス **/
	@Autowired
	private SaibanshoBuListService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 係属部一覧画面の取得
	 * 
	 * @param saibanId
	 * @return
	 */
	@RequestMapping(value = "/getSaibanshoBuList", method = RequestMethod.POST)
	public ModelAndView getSaibanshoBuList(@RequestParam(name = "saibanshoId") Long saibanshoId) {
		SaibanshoBuListForm viewForm = service.search(new SaibanshoBuListForm(), saibanshoId);
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

}