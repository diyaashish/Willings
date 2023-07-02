package jp.loioz.app.user.saibanManagement.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.saibanManagement.form.SaibanTsuikisoEditInputForm;
import jp.loioz.app.user.saibanManagement.service.SaibanTsuikisoEditService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.EraEpoch;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.utility.StringUtils;

/**
 * 裁判管理画面(刑事弁護)：追起訴(事件情報の登録)モーダルのコントローラークラス
 */
@Controller
@RequestMapping(value = "user/saibanKeijiManagement")
public class SaibanTsuikisoEditController extends DefaultController {

	/** コントローラに対応するviewパス */
	private static final String MY_VIEW_PATH = "user/saibanManagement/saibanTsuikisoEditModal";

	/** コントローラに対応するviewパス */
	private static final String AJAX_MODAL_PATH = MY_VIEW_PATH + "::editTsuikisoModal";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "editForm";

	/** サービスクラス */
	@Autowired
	private SaibanTsuikisoEditService service;

	/** フォームクラスの初期化 */
	@ModelAttribute(VIEW_FORM_NAME)
	SaibanTsuikisoEditInputForm setUpEditForm() {
		return new SaibanTsuikisoEditInputForm();
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
	@RequestMapping(value = "/createTsuikisoModal", method = RequestMethod.POST)
	public ModelAndView createKeijiTsuikisoModal(@RequestParam(name = "saibanSeq") Long saibanSeq) {

		// editFormの作成
		SaibanTsuikisoEditInputForm inputForm = service.createViewForm(saibanSeq);
		return getMyModelAndView(inputForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 更新モーダルの表示
	 * 
	 * @param editForm 追起訴(事件情報の登録)モーダルの編集用フォーム
	 * @return 画面表示情報
	 */
	@RequestMapping(value = "/editTsuikisoModal", method = RequestMethod.POST)
	public ModelAndView editKeijiTsuikisoModal(SaibanTsuikisoEditInputForm inputForm) {

		// 更新するデータのセット
		inputForm = service.setData(inputForm);
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
	@RequestMapping(value = "/registTsuikiso", method = RequestMethod.POST)
	public Map<String, Object> regist(@Validated @ModelAttribute(VIEW_FORM_NAME) SaibanTsuikisoEditInputForm inputForm, BindingResult result) {

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

		// DB整合姓チェック
		if (!this.accessDBValidated(inputForm, response)) {
			return response;
		}

		try {
			// 登録処理
			service.regist(inputForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "追起訴"));
			return response;

		} catch (Exception e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013.getMessageKey()));
			return response;
		}
	}

	/**
	 * 更新
	 * 
	 * @param inputForm
	 * @param result バリデーション結果
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/updateTsuikiso", method = RequestMethod.POST)
	public Map<String, Object> updateTsuikiso(@Validated @ModelAttribute(VIEW_FORM_NAME) SaibanTsuikisoEditInputForm inputForm,
			BindingResult result) {

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

		try {

			// 更新
			service.update(inputForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "追起訴"));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013.getMessageKey()));
			return response;
		}
	}

	/**
	 * 削除
	 * 
	 * @param inputForm
	 * @param result バリデーション結果
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteTsuikiso", method = RequestMethod.POST)
	public Map<String, Object> delete(@Validated @ModelAttribute(VIEW_FORM_NAME) SaibanTsuikisoEditInputForm inputForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 削除
			service.delete(inputForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00001));
			return response;

		} catch (Exception e) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013.getMessageKey()));
			return response;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 事件番号入力チェック
	 * 
	 * @param editForm 追起訴編集フォーム
	 * @param result バリデーション結果
	 */
	private void validateJikenNo(SaibanTsuikisoEditInputForm inputForm, BindingResult result) {

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

	/**
	 * DB整合性チェック
	 * 
	 * @param inputForm
	 * @param response
	 * @return
	 */
	private boolean accessDBValidated(SaibanTsuikisoEditInputForm inputForm, Map<String, Object> response) {

		// DB整合性チェック
		Map<String, String> errorMsgMap = new HashMap<>();

		// 裁判追起訴登録上限
		if (!service.isJikenAddLimitOverValid(inputForm.getSaibanSeq())) {
			errorMsgMap.put("message", getMessage(MessageEnum.MSG_E00067, String.valueOf(CommonConstant.JIKEN_INFO_ADD_LIMIT)));
		}

		boolean valid = true;
		if (!errorMsgMap.isEmpty()) {
			response.put("succeeded", false);
			response.put("message", errorMsgMap.get("message"));
			valid = false;
		}
		return valid;
	}

}
