package jp.loioz.app.user.dengon.controller;

import java.util.HashMap;
import java.util.List;
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
import jp.loioz.app.user.dengon.form.CustomerSearchRequest;
import jp.loioz.app.user.dengon.form.DengonAtesakiListPopForm;
import jp.loioz.app.user.dengon.form.DengonEditForm;
import jp.loioz.app.user.dengon.form.DengonEditForm.Account;
import jp.loioz.app.user.dengon.service.DengonEditService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.utility.StringUtils;

/**
 * 伝言編集画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0001})
@Controller
@RequestMapping(value = "user/dengon/edit")
public class DengonEditController extends DefaultController {

	/* サービスクラス */
	@Autowired
	private DengonEditService service;

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/dengon/dengonEditModal";

	/** 伝言作成モーダルと対応するviewのフラグメントパス */
	private static final String MY_VIEW_DENGON＿CREATE_MODAL_PATH = MY_VIEW_PATH + "::dengonCreateModal";

	/** 伝言作成モーダルと対応するform名 */
	private static final String DENGON＿CREATE_MODAL_FORM_NAME = "dengonEditForm";

	/** メッセージ作成：宛先ポッパー用Form名 */
	private static final String DENGON＿CREATE_MODAL_ATESAKI_POP_FORM_NAME = "dengonAtesakiListPopForm";

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 *
	 * @param dengonSeq
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView create(@RequestParam(name = "dengonSeq") Long dengonSeq) {

		DengonEditForm form = new DengonEditForm();
		if (dengonSeq != null) {
			form = service.selectDengonEdit(dengonSeq);
		}
		return getMyModelAndView(form, MY_VIEW_DENGON＿CREATE_MODAL_PATH, DENGON＿CREATE_MODAL_FORM_NAME);
	}

	/**
	 * 業務履歴画面からの伝言作成モーダル初期表示
	 *
	 * @param dengonSeq
	 * @param ankenId
	 * @param customerId
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/gyomuhistory", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView gyomuhistory(
			@RequestParam(name = "gyomuhistorySeq") Long gyomuhistorySeq) {

		DengonEditForm form = service.selectDengonEditFromGyoumuHistory(gyomuhistorySeq);
		return getMyModelAndView(form, MY_VIEW_DENGON＿CREATE_MODAL_PATH, DENGON＿CREATE_MODAL_FORM_NAME);
	}

	/**
	 * 返信用の情報を取得する
	 *
	 * @param dengonSeq
	 * @param msgHolder
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/reply", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView reply(
			@RequestParam(name = "dengonSeq") Long dengonSeq,
			@RequestParam(name = "isReplyAll") boolean isReplyAll) {

		DengonEditForm form = service.selectDengonReply(dengonSeq, isReplyAll);

		return getMyModelAndView(form, MY_VIEW_DENGON＿CREATE_MODAL_PATH, DENGON＿CREATE_MODAL_FORM_NAME);
	}

	/**
	 * 作成した伝言を送信する
	 *
	 * @param form
	 * @param result
	 * @param msgHolder
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/send", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> send(@Validated DengonEditForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>(); // 返却値
		// リロードフラグ
		boolean reloadFlg = false;
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		try {
			if (Objects.equals(form.getDto().getDengonSeq(), null)) {
				/* 新規登録処理 */
				reloadFlg = service.send(form);
			} else {
				/* 更新処理 */
				service.sendWhereDraft(form);
			}
		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得する
			String errorMsg = super.getMessage(ex.getMessageKey());
			response.put("succeeded", false);
			response.put("message", errorMsg);
			return response;
		}
		response.put("message", getMessage(MessageEnum.MSG_I00026, "送信しました"));
		response.put("succeeded", true);
		response.put("reload", reloadFlg);
		return response;
	}

	/**
	 * 作成した伝言を下書き保存する
	 *
	 * @param form
	 * @param result
	 * @param msgHolder
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/draft", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> draft(@Validated DengonEditForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>(); // 返却値

		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		try {
			if (Objects.isNull(form.getDto().getDengonSeq())) {
				/* 下書き・新規登録処理 */
				service.draftInsert(form);
			} else {
				/* 下書き・更新処理 */
				service.draftUpdate(form);
			}

		} catch (AppException ex) {
			// Exceptionが保持するエラーメッセージのキー情報を取得する
			String errorMsg = super.getMessage(ex.getMessageKey());
			response.put("succeeded", false);
			response.put("message", errorMsg);
			return response;
		}
		response.put("succeeded", true);
		response.put("message", getMessage(MessageEnum.MSG_I00027, "保存しました"));
		return response;
	}

	/**
	 * 宛先アカウント一覧を取得する
	 *
	 * @param word
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getReceiveAccountList", method = RequestMethod.POST)
	public Map<String, Object> getReceiveAccountList() {
		Map<String, Object> response = new HashMap<>();
		List<Account> selectAccountList = service.getEnabledAccount();
		selectAccountList.stream().forEach(e -> {
			e.setAccountName(StringUtils.removeChars(e.getAccountName(), CommonConstant.SPACE));
			e.setAccountNameKana(StringUtils.removeChars(e.getAccountNameKana(), CommonConstant.SPACE));
		});
		response.put("accountList", selectAccountList);
		return response;
	}

	/**
	 * 宛先アカウント選択ポップオーバーのテンプレートを取得する
	 *
	 * @return
	 */
	@RequestMapping(value = "/getAtesakiListPopTemplate", method = RequestMethod.POST)
	public ModelAndView getAtesakiListPopTemplate(@RequestParam(name = "ankenId", required = false) List<Long> ankenId) {

		DengonAtesakiListPopForm viewForm = new DengonAtesakiListPopForm();
		viewForm.setBushoList(service.getBushoList());
		viewForm.setAnkenIdToTantoAccountMap(service.getAnkenTantoAccountSeq(ankenId));
		return getMyModelAndView(viewForm, MY_VIEW_PATH + "::atesakiListPopTemplate", DENGON＿CREATE_MODAL_ATESAKI_POP_FORM_NAME);
	}

	/**
	 * 紐付け顧客の取得
	 * 
	 * @param searchText
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/searchCustomer", method = RequestMethod.POST)
	public Map<String, Object> searchCustomer(CustomerSearchRequest request) {
		Map<String, Object> response = new HashMap<>();
		response.put("customerList", service.getCustomerList(request.getSearchText(), request.getExclusionCustomerIdList()));
		response.put("uuid", request.getUuid());
		return response;
	}

}