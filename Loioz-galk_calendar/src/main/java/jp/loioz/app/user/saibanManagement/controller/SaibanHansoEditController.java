package jp.loioz.app.user.saibanManagement.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.user.saibanManagement.form.SaibanHansoEditInputForm;
import jp.loioz.app.user.saibanManagement.service.SaibanHansoEditService;
import jp.loioz.common.constant.CommonConstant.EraEpoch;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.StringUtils;

/**
 * 裁判管理画面(民事)：反訴登録モーダルのコントローラークラス
 */
@Controller
@RequestMapping(value = "user/saibanMinjiManagement")
public class SaibanHansoEditController extends DefaultController {

	/** コントローラに対応するviewパス */
	private static final String MY_VIEW_PATH = "user/saibanManagement/saibanHansoEditModal";

	/** コントローラに対応するviewパス */
	private static final String AJAX_MODAL_PATH = MY_VIEW_PATH + "::editHansoModal";

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyInputForm";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** サービスクラス */
	@Autowired
	private SaibanHansoEditService service;

	@Autowired
	private Logger logger;

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}
	
	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 新規登録モーダルの表示
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @return 画面表示情報
	 */
	@RequestMapping(value = "/createHansoModal", method = RequestMethod.POST)
	public ModelAndView createMinjiHansoModal(@RequestParam(name = "saibanSeq") Long saibanSeq, @RequestParam(name = "ankenId") Long ankenId) {

		// editFormの作成
		SaibanHansoEditInputForm inputForm = service.createViewForm(saibanSeq, ankenId);
		return getMyModelAndView(inputForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 新規登録
	 * 
	 * @param inputForm
	 * @param result バリデーション結果
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/registHanso", method = RequestMethod.POST)
	public Map<String, Object> registHanso(@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) SaibanHansoEditInputForm inputForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 事件番号入力チェック
		validateJikenNo(inputForm, result);

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// DB整合姓チェック無し

		try {
			// 登録処理
			service.regist(inputForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "反訴裁判"));
			return response;

		} catch (Exception e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013.getMessageKey()));
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName, e);
			return response;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 事件番号入力チェック
	 * 
	 * @param editForm 反訴編集フォーム
	 * @param result バリデーション結果
	 */
	private void validateJikenNo(SaibanHansoEditInputForm inputForm, BindingResult result) {

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
