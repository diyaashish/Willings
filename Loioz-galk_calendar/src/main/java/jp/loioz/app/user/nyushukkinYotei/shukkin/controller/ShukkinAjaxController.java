package jp.loioz.app.user.nyushukkinYotei.shukkin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.validation.accessDB.CommonKaikeiValidator;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ShukkinDetailViewForm;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ShukkinListSearchForm;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ajax.ShukkinSaveRequest;
import jp.loioz.app.user.nyushukkinYotei.shukkin.service.ShukkinDetailService;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.message.MessageHolder;

@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@LoiozAdminControlCheck(items = {LoiozAdminControl.OLD_KAIKEI_OPEN})
@Controller
@RequestMapping(UrlConstant.NYUSHUKKIN_YOTEI_SHUKKIN_URL)
@SessionAttributes(ShukkinListController.SEARCH_FORM_NAME)
public class ShukkinAjaxController extends DefaultController {

	/** コントローラーと対応するviewパス */
	public static final String MY_VIEW_PATH = ShukkinListController.MY_VIEW_PATH;

	/** コントローラーと対応する入金登録のAjaxPath */
	public static final String SHUKKIN_DETAIL_VIEW_AJAX_PATH = MY_VIEW_PATH + "::shukkinRegistForm";

	/** 入金予定データの詳細情報Formクラス */
	public static final String SHUKKIN_DETAIL_VIEW_NAME = "shukkinDetailViewForm";

	/** 入出金予定一覧（出金）で使用するserviceクラス **/
	@Autowired
	private ShukkinDetailService service;

	/** 会計系 共通入力チェック */
	@Autowired
	private CommonKaikeiValidator commonKaikeiValidator;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 出金予定詳細情報の取得
	 * 
	 * @param searchForm
	 * @param nyushukkinYoteiSeq
	 * @return
	 */
	@RequestMapping(value = "/shukkinDetail", method = RequestMethod.POST)
	public ModelAndView getShukkinDetail(
			@ModelAttribute(ShukkinListController.SEARCH_FORM_NAME) ShukkinListSearchForm searchForm) {

		// viewの作成
		ShukkinDetailViewForm form = service.createDetailForm();

		try {
			form = service.createDetailForm(searchForm.getNyushukkinYoteiSeq());
		} catch (AppException ex) {
			// エラーリダイレクト
			return getMyModelAndViewByModalError(MessageHolder.ofError(ex.getMessageKey()));
		}

		return getMyModelAndView(form, SHUKKIN_DETAIL_VIEW_AJAX_PATH, SHUKKIN_DETAIL_VIEW_NAME);
	}

	/**
	 * 1件の出金実績を登録します。
	 * 
	 * @param request
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/saveShukkinDetail", method = RequestMethod.POST)
	public Map<String, Object> saveShukkinDetail(@Validated ShukkinSaveRequest request, BindingResult result) {

		// Responseオブジェクト
		Map<String, Object> response = new HashMap<>();

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 相関入力値チェック
		if (!shukkinSaveRequestValidated(request, response)) {
			return response;
		}

		// DB相関チェック
		if (!accessDBValidated(request, response)) {
			return response;
		}

		try {
			// 登録処理
			service.shukkinSave(request);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "出金実績"));
			return response;

		} catch (AppException ex) {
			// エラーハンドリング
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 画面独自のバリデーションチェック（更新）
	 *
	 * @param form
	 * @param response
	 * @return
	 */
	private boolean shukkinSaveRequestValidated(ShukkinSaveRequest request, Map<String, Object> response) {

		// オブジェクト名を定義
		final String objName = request.getClass().getName();

		// エラー内容生成
		List<FieldError> errorList = new ArrayList<>();

		// *******************************************
		// ■金額フォーマットのチェック
		// *******************************************
		if (request.getParsedDecimalShukkinGaku() == null) {
			FieldError fieldError = new FieldError(objName, "nyushukkinGaku", getMessage(MessageEnum.MSG_E00030));
			errorList.add(fieldError);
		}

		boolean valid = true;
		// エラーの存在チェック
		if (errorList.size() > 0) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00061));
			response.put("errors", errorList);
			valid = false;
		}
		return valid;
	}

	/**
	 * 出金登録時のDB相関チェック
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean accessDBValidated(ShukkinSaveRequest request, Map<String, Object> response) {

		// DB整合性チェック
		Map<String, String> errorMsgMap = new HashMap<>();

		// エラーの判定と格納
		this.shukkinSaveAcessDBValdate(request, errorMsgMap);

		boolean valid = true;
		if (!errorMsgMap.isEmpty()) {
			response.put("succeeded", false);
			response.put("message", errorMsgMap.get("message"));
			valid = false;
		}
		return valid;
	}

	/**
	 * 入金登録時のDB相関チェック
	 * 
	 * @param request 入金登録リクエスト
	 * @param errorMsg (key : message) エラーがあった場合のメッセージを格納
	 * @return 検証結果
	 */
	private void shukkinSaveAcessDBValdate(ShukkinSaveRequest request, Map<String, String> errorMsg) {

		if (!commonKaikeiValidator.isValidNonMatchedPlanPrice(request.getNyushukkinYoteiSeq(), request.getParsedDecimalShukkinGaku())) {
			// 予定額 != 入金額
			errorMsg.put("message", getMessage(MessageEnum.MSG_E00002, "出金予定額", "出金額"));
			return;
		}
	}
}
