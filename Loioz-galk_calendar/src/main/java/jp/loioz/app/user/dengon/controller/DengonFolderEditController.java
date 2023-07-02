package jp.loioz.app.user.dengon.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.dengon.form.DengonFolderEditForm;
import jp.loioz.app.user.dengon.form.DengonMoveForm;
import jp.loioz.app.user.dengon.service.DengonFolderEditService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.utility.StringUtils;

/**
 * カスタムフォルダー編集画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0001})
@Controller
@RequestMapping(value = "user/dengon/folder")
public class DengonFolderEditController extends DefaultController {

	/* サービスクラス */
	@Autowired
	private DengonFolderEditService service;

	/** コントローラと対応するviewのパス */
	private static final String AJAX_MODAL_PATH_FOLDER_EDIT = "user/dengon/dengonFolderEditModal" + "::dengonFolderEdit";

	/** メッセージフォルダ移動と対応するviewのフラグメントパス */
	private static final String AJAX_MODAL_PATH_FOLDER_MOVE = "user/dengon/dengonFolderMoveModal" + "::dengonFolderMove";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/folderEditModalOpen", method = RequestMethod.POST)
	public ModelAndView indexFolderEdit(
			@RequestParam(name = "dengonFolderSeq", required = false) Long dengonFolderSeq,
			@RequestParam(name = "trashedFlg") String trashedFlg) {

		// viewFormの作成
		DengonFolderEditForm viewForm = service.selectFolderEdit(dengonFolderSeq);
		if (SystemFlg.FLG_ON.equalsByCode(trashedFlg)) {
			// 選択したフォルダがごみ箱のフォルダ
			viewForm.setCurrentTrashedFlg(true);
		}
		// 現在のフォルダSEQ
		viewForm.setCurrentDengonFolderSeq(dengonFolderSeq);

		return getMyModelAndView(viewForm, AJAX_MODAL_PATH_FOLDER_EDIT, VIEW_FORM_NAME);
	}

	/**
	 * 登録処理
	 *
	 * @param form
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> regist(@Validated DengonFolderEditForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// フォルダ数上限の判定
		if (Objects.isNull(form.getDto().getParentDengonFolderSeq())) {
			// 親フォルダの場合

			if (!service.creatableParentFolderCount()) {
				// フォルダの作成上限を超えた場合
				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00067, String.valueOf(CommonConstant.DENGON_FOLDER_ADD_LIMIT)));
				return response;
			}

		} else {
			// 子フォルダの場合

			if (!service.creatableSubFolderCount()) {
				// 子フォルダの作成上限を超えた場合
				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00067, String.valueOf(CommonConstant.DENGON_FOLDER_ADD_LIMIT)));
				return response;
			}
		}

		try {

			// 保存処理
			Long dengonFolderSeq = service.regist(form);
			response.put("succeeded", true);
			response.put("dengonFolderSeq", dengonFolderSeq);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "フォルダ"));

		} catch (AppException ex) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}

		return response;
	}

	/**
	 * 更新処理
	 *
	 * @param form
	 * @param result
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> update(@Validated DengonFolderEditForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		try {

			// 更新処理
			service.update(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "フォルダ"));

		} catch (AppException ex) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}

		return response;
	}

	/**
	 * カスタムフォルダーをごみ箱に入れる
	 *
	 * @param dengonFolderSeq
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/trashedFolder", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> trashedFolder(@RequestParam(name = "currentDengonFolderSeq") Long dengonFolderSeq) {

		Map<String, Object> response = new HashMap<>();

		try {

			// ごみ箱へ入れる処理
			service.trashedFolder(dengonFolderSeq);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00029, "ごみ箱に入れました"));

		} catch (AppException ex) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}

		return response;
	}

	/**
	 * ごみ箱のフォルダを戻す
	 *
	 * @param dengonFolderSeq
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> removeFolder(@RequestParam(name = "currentDengonFolderSeq") Long dengonFolderSeq) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 復元処理
			service.removeFolder(dengonFolderSeq);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00029, "受信BOX内に戻しました"));

		} catch (AppException ex) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}

		return response;
	}

	/**
	 * 削除処理
	 *
	 * @param dengonFolderSeq
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> delete(@RequestParam(name = "currentDengonFolderSeq") Long dengonFolderSeq) {

		Map<String, Object> response = new HashMap<>();

		try {

			// 削除処理
			service.delete(dengonFolderSeq);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00029, "削除しました"));

		} catch (AppException ex) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}

		return response;
	}

	/**
	 * 初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/folderMoveModalOpen", method = RequestMethod.POST)
	public ModelAndView indexFolderMove(@RequestParam(name = "dengonSeq") Long dengonSeq) {

		DengonMoveForm viewForm = new DengonMoveForm();
		viewForm = service.createDengonMoveForm(dengonSeq);
		return getMyModelAndView(viewForm, AJAX_MODAL_PATH_FOLDER_MOVE, VIEW_FORM_NAME);
	}

	/**
	 * メールをフォルダへ移動する
	 *
	 * @param dengonSeq
	 * @param dengonFolderSeq
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/moveMail", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> moveMail(
			@RequestParam(name = "receiveDengonSeqList", required = false) String receiveDengonSeqList,
			@RequestParam(name = "sendDengonSeqList", required = false) String sendDengonSeqList,
			@RequestParam(name = "dengonFolderSeq", required = false) Long dengonFolderSeq) {

		Map<String, Object> response = new HashMap<>();

		try {

			if (StringUtils.isNotEmpty(receiveDengonSeqList)) {
				// 受信メッセージの場合
				service.moveFolder(receiveDengonSeqList, dengonFolderSeq);
			}
			if (StringUtils.isNotEmpty(sendDengonSeqList)) {
				// 送信メッセージの場合
				service.moveSendBox(sendDengonSeqList);
			}

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00026, "移動しました"));

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));

		}

		return response;
	}

}