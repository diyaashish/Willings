package jp.loioz.app.user.saibanManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.saibanManagement.form.ajax.SaibanManagementKeizokuBu;
import jp.loioz.app.user.saibanManagement.form.ajax.SaibanManagementKensatsucho;
import jp.loioz.app.user.saibanManagement.service.SaibanEditAjaxService;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 裁判管理画面のAjax用コントローラークラス
 */
@Controller
@RequestMapping(value = "user/saibanManagement")
public class SaibanEditAjaxController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH_MINJI = "user/saibanManagement/saibanEditMinji";

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH_KEIJI = "user/saibanManagement/saibanEditKeiji";

	/** サービスクラス */
	@Autowired
	private SaibanEditAjaxService service;

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 係属部を取得する
	 *
	 * @param saibanshoId 裁判所ID
	 * @return 係属部
	 */
	@RequestMapping(value = "/getKeizokuBu", method = RequestMethod.POST)
	public ModelAndView getKeizokuBu(@RequestParam("saibanshoId") Long saibanshoId) {

		List<SaibanManagementKeizokuBu> keizokuBuList = service.getKeizokuBu(saibanshoId);

		return ModelAndViewUtils.getModelAndView(MY_VIEW_PATH_MINJI + "::keizokuBuSelect")
				.addObject("keizokuBuList", keizokuBuList);
	}

	/**
	 * 係属部を取得する<br>
	 *
	 * <pre>
	 * 刑事用です。
	 * </pre>
	 *
	 * @param saibanshoId 裁判所ID
	 * @return 係属部
	 */
	@RequestMapping(value = "/getKeizokuBuForKeiji", method = RequestMethod.POST)
	public ModelAndView getKeizokuBuForKeiji(@RequestParam("saibanshoId") Long saibanshoId) {

		List<SaibanManagementKeizokuBu> keizokuBuList = service.getKeizokuBu(saibanshoId);

		return ModelAndViewUtils.getModelAndView(MY_VIEW_PATH_KEIJI + "::keizokuBuSelect")
				.addObject("keizokuBuList", keizokuBuList);
	}

	/**
	 * 検察庁の電話番号、FAX番号を取得します。
	 *
	 * @param kensatsuchoId 検察庁ID(施設ID)
	 * @return 検察庁の電話番号、FAX番号
	 */
	@ResponseBody
	@RequestMapping(value = "/getKensatsuchoInfo", method = RequestMethod.POST)
	public String getKensatsuchoInfo(@RequestParam("kensatsuchoId") Long kensatsuchoId) {

		SaibanManagementKensatsucho kensatsuchoInfo = service.getKensatsuchoTelAndFaxNo(kensatsuchoId);

		// json形式で返却
		Gson gson = new Gson();
		return gson.toJson(kensatsuchoInfo);
	}

	/**
	 * 事件番号 + 事件名 を取得します。
	 *
	 * @param jikenGengo 事件元号
	 * @param jikenYear 事件年
	 * @param jikenMark 事件符号
	 * @param jikenNo 事件番号
	 * @param jikenName 事件名
	 * @return 事件番号 + 事件名 の文字列
	 */
	@ResponseBody
	@RequestMapping(value = "/getJikenNoAndName", method = RequestMethod.POST)
	public String getJikenNoAndName(@RequestParam(name = "jikenGengo", required = false) EraType jikenGengo,
			@RequestParam(name = "jikenYear", required = false) String jikenYear,
			@RequestParam(name = "jikenMark", required = false) String jikenMark,
			@RequestParam(name = "jikenNo", required = false) String jikenNo,
			@RequestParam(name = "jikenName", required = false) String jikenName) {

		String jikenNoAndName = jikenGengo.getVal() + jikenYear + "年 (" + jikenMark + ") 第" + jikenNo + "号　" + jikenName;

		return jikenNoAndName;
	}
}
