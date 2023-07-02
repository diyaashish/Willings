package jp.loioz.app.common.controller;

import java.util.HashMap;
import java.util.Map;

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
import jp.loioz.app.common.form.GinkoKozaEditForm;
import jp.loioz.app.common.service.CommonGinkoKozaService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dto.KozaDto;

/**
 * 銀行口座モーダルの共通コントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "common")
public class GinkoKozaController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String VIEW_PATH_GINKO_KOZA = "common/ginkoKozaEdit::ginkoKozaEdit";

	/** コントローラと対応する銀行口座のview名 */
	private static final String GINKO_KOZA_VIEW_FORM_NAME = "ginkoKozaEditForm";

	/** アカウント編集画面用一覧画面のサービスクラス */
	@Autowired
	private CommonGinkoKozaService service;

	// =========================================================================
	// public メソッド
	// =========================================================================
	// ****************************************************************
	// 銀行口座モーダル
	// ****************************************************************
	/**
	 * 報酬登録初期表示
	 *
	 * @return 画面情報
	 */
	@RequestMapping(value = "/openGinkoKozaCreateModal", method = RequestMethod.POST)
	public ModelAndView openGinkoKozaCreateModal() {

		GinkoKozaEditForm ginkoKozaEditForm = service.createGinkoKozaEditForm();
		super.setAjaxProcResultSuccess();
		return getMyModelAndView(ginkoKozaEditForm, VIEW_PATH_GINKO_KOZA, GINKO_KOZA_VIEW_FORM_NAME);
	}

	/**
	 * 編集モーダル初期表示
	 *
	 * @param kozaSeq 口座SEQ
	 * @return 画面情報
	 */
	@RequestMapping(value = "/openGinkoKozaEditModal", method = RequestMethod.POST)
	public ModelAndView openGinkoKozaEditModal(@RequestParam(name = "kozaSeq") Long kozaSeq) {

		try {
			GinkoKozaEditForm ginkoKozaEditForm = service.createGinkoKozaEditForm(kozaSeq);

			super.setAjaxProcResultSuccess();
			return getMyModelAndView(ginkoKozaEditForm, VIEW_PATH_GINKO_KOZA, GINKO_KOZA_VIEW_FORM_NAME);

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * アカウント用口座新規作成
	 *
	 * @param form 共通口座編集画面用のフォームクラス
	 * @return 処理結果Map
	 */
	@RequestMapping(value = "/registAccountGinkokoza", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> registGinkokoza(@Validated GinkoKozaEditForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();
		KozaDto dto = form.getKozaDto();

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// DBアクセスチェック
		if (this.accessDBValidatedForAccountGinkokoza(SessionUtils.getLoginAccountSeq(), response)) {
			// 整合性エラーが発生した場合
			return response;
		}

		try {
			// 登録処理
			service.insertAccountKoza(dto);

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "口座"));
			return response;

		} catch (AppException ex) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 事務所用口座新規作成
	 *
	 * @param form 共通口座編集画面用のフォームクラス
	 * @return 処理結果Map
	 */
	@ResponseBody
	@RequestMapping(value = "/registOfficeGinkokoza", method = RequestMethod.POST)
	public Map<String, Object> registOfficeGinkokoza(@Validated GinkoKozaEditForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();
		KozaDto dto = form.getKozaDto();
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}
		// DBアクセスチェック
		if (this.accessDBValidatedForOfficeGinkokoza(SessionUtils.getTenantSeq(), response)) {
			// 整合性エラーが発生した場合
			return response;
		}
		try {
			// 登録処理
			service.insertOfficeKoza(dto);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "口座"));
			return response;

		} catch (AppException ex) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 口座更新
	 *
	 * @param form 共通口座編集画面用のフォームクラス
	 * @return 処理結果Map
	 */
	@ResponseBody
	@RequestMapping(value = "/updateGinkokoza", method = RequestMethod.POST)
	public Map<String, Object> updateGinkokoza(@Validated GinkoKozaEditForm form, BindingResult result) {

		Map<String, Object> response = new HashMap<>();
		KozaDto dto = form.getKozaDto();
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}
		try {
			// 更新処理
			service.updateKoza(dto);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00003));
			return response;

		} catch (AppException ex) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 口座利用再開
	 *
	 * @param form 共通口座編集画面用のフォームクラス
	 * @return 処理結果Map
	 */
	@ResponseBody
	@RequestMapping(value = "/reastartGinkokoza", method = RequestMethod.POST)
	public Map<String, Object> reastartGinkokoza(GinkoKozaEditForm form) {

		Map<String, Object> response = new HashMap<>();
		KozaDto dto = form.getKozaDto();

		try {
			// 更新処理
			service.restartKoza(dto);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00003));
			return response;

		} catch (AppException ex) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 口座論理削除
	 *
	 * @param form 共通口座編集画面用のフォームクラス
	 * @return 処理結果Map
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteGinkokoza", method = RequestMethod.POST)
	public Map<String, Object> deleteGinkokoza(GinkoKozaEditForm form) {

		Map<String, Object> response = new HashMap<>();
		Long ginkoAccountSeq = form.getKozaDto().getGinkoAccountSeq();

		try {
			// 削除処理
			service.deleteAccountGinkoKoza(ginkoAccountSeq);

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00001));
			return response;

		} catch (AppException ex) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	/**
	 * 事務所に紐づく銀行口座の登録有無を判定する
	 *
	 * @return 処理結果Map
	 */
	@ResponseBody
	@RequestMapping(value = "/isRegistOfficeKoza", method = RequestMethod.POST)
	public Map<String, Object> isRegistOfficeKoza() {
		Map<String, Object> response = new HashMap<>();

		/* ログインのアカウント連番を取得する */
		Long accountSeq = SessionUtils.getLoginAccountSeq();

		/* 事務所に紐づく銀行口座の登録有無を判定する */
		boolean isRegist = service.isRegistOfficeGinkoKoza(accountSeq);
		response.put("isRegist", isRegist);
		return response;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 事務所口座用のDBアクセスを伴うチェック
	 *
	 * @param tenantSeq
	 * @param response
	 * @return error
	 */
	private boolean accessDBValidatedForOfficeGinkokoza(Long tenantSeq, Map<String, Object> response) {
		boolean error = false;
		// 登録上限の重複チェック
		if (service.isOfficeGinkoKozaAdd(tenantSeq)) {
			// 登録上限を超えて登録はエラー
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00067, String.valueOf(CommonConstant.GINKO_KOZA_ADD_LIMIT_FOR_WEB)));
			error = true;
		}
		return error;
	}

	/**
	 * アカウント口座用のDBアクセスを伴うチェック
	 *
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean accessDBValidatedForAccountGinkokoza(Long accountSeq, Map<String, Object> response) {
		boolean error = false;
		// 登録上限の重複チェック
		if (service.isAccountGinkoKozaAdd(accountSeq)) {
			// 登録上限を超えて登録はエラー
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00067, String.valueOf(CommonConstant.GINKO_KOZA_ADD_LIMIT_FOR_WEB)));
			error = true;
		}
		return error;
	}
}
