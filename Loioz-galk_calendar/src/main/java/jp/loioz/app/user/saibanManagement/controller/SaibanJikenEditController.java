package jp.loioz.app.user.saibanManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.saibanManagement.form.SaibanJikenEditInputForm;
import jp.loioz.app.user.saibanManagement.service.SaibanJikenEditService;
import jp.loioz.common.constant.CommonConstant.EraEpoch;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.StringUtils;

/**
 * 裁判管理画面：事件情報の編集モーダルのコントローラークラス
 */
@Controller
@RequestMapping(value = "user/saibanMinjiManagement")
public class SaibanJikenEditController extends DefaultController {

	/** コントローラに対応するviewパス */
	private static final String MY_VIEW_PATH = "user/saibanManagement/saibanJikenEditModal::eidtSaibanJikenModalFragment";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	@Autowired
	private SaibanJikenEditService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 新規登録モーダルの表示
	 * 
	 * @param 事件SEQ
	 * @return 画面表示情報
	 */
	@RequestMapping(value = "/createSaibanJikenEditModal", method = RequestMethod.GET)
	public ModelAndView createSaibanJikenEditModal(@RequestParam(name = "jikenSeq") Long jikenSeq) {

		// editFormの作成
		SaibanJikenEditInputForm inputForm = service.createViewForm(jikenSeq);
		return getMyModelAndView(inputForm, MY_VIEW_PATH, VIEW_FORM_NAME);

	}

	/**
	 * 裁判事件情報の更新処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/updateSaibanJiken", method = RequestMethod.POST)
	public ModelAndView updateSaibanJiken(@Validated SaibanJikenEditInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;

		// 相関バリデート
		this.validateJikenNo(inputForm, result);

		if (result.hasErrors()) {
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			mv = getMyModelAndViewWithErrors(inputForm, MY_VIEW_PATH, VIEW_FORM_NAME, result);
			return mv;
		}

		try {
			// 更新処理
			service.updateSaibanJiken(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00002));
			return mv;

		} catch (AppException e) {
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			mv = getMyModelAndView(inputForm, MY_VIEW_PATH, VIEW_FORM_NAME);
			return mv;
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 事件番号入力チェック
	 *
	 * @param inputForm
	 * @param result
	 */
	private void validateJikenNo(SaibanJikenEditInputForm inputForm, BindingResult result) {

		// 事件番号必須チェック
		if (StringUtils.isExsistEmpty(inputForm.getJikenGengo(), inputForm.getJikenYear(), inputForm.getJikenMark(), inputForm.getJikenNo())) {

			result.rejectValue("jikenNo", null, getMessage(MessageEnum.VARIDATE_MSG_E00002));

		} else {

			// 元号と年数の整合性チェック
			Long compMotoYear = Long.parseLong(EraEpoch.of(inputForm.getJikenGengo()).getVal());
			Long heiseiYear = Long.parseLong(inputForm.getJikenYear());
			if (heiseiYear > compMotoYear) {
				result.rejectValue("jikenNo", null, getMessage(MessageEnum.VARIDATE_MSG_E00001));
			}

		}
	}

}
