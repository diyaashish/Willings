package jp.loioz.app.user.advisorContractPerson.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.validation.groups.Default;

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
import jp.loioz.app.user.advisorContractPerson.form.AdvisorContractEditForm;
import jp.loioz.app.user.advisorContractPerson.service.AdvisorContractEditService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.groups.Update;

/**
 * 顧問契約編集画面（モーダル）のコントローラークラス
 */
@Controller
@RequestMapping("user/advisorContractPerson")
public class AdvisorContractEditController extends DefaultController {

	/** コントローラと対応するviewパス **/
	public static final String MY_VIEW_PATH = "user/advisorContractPerson/advisorContractEditModal";
	
	/** 編集モーダルフラグメントのパス */
	private static final String AJAX_MODAL_PATH = MY_VIEW_PATH + "::select-modal";
	
	/** 売上計上先選択肢フラグメントのパス */
	private static final String AJAX_SALES_OWNER_FRAGMENT_PATH = MY_VIEW_PATH + "::lawyerOptionList";
	
	/** viewで使用するフォームオブジェクト名 **/
	public static final String EDIT_FORM_NAME = "editForm";
	
	/** 顧問契約編集画面（名簿）のサービスクラス */
	@Autowired
	private AdvisorContractEditService service;
	
	// =========================================================================
	// public メソッド
	// =========================================================================
	
	/**
	 * 登録モーダル表示
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView index(@RequestParam(name = "personId") Long personId) {
		
		// 登録用のフォームを取得
		AdvisorContractEditForm editForm = service.getCreateForm(personId);
		
		// 画面（モーダルコンテンツ）をリターン
		ModelAndView mv = getMyModelAndView(editForm, AJAX_MODAL_PATH, EDIT_FORM_NAME);
		super.setAjaxProcResultSuccess();
		
		return mv;
	}
	
	/**
	 * 更新モーダル表示
	 *
	 * @param viewForm
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam(name = "personId") Long personId,
							@RequestParam(name = "advisorContractSeq") Long advisorContractSeq) {

		AdvisorContractEditForm editForm = service.getEditForm(personId, advisorContractSeq);
		
		// 画面（モーダルコンテンツ）をリターン
		ModelAndView mv = getMyModelAndView(editForm, AJAX_MODAL_PATH, EDIT_FORM_NAME);
		super.setAjaxProcResultSuccess();
		
		return mv;
	}
	
	/**
	 * 売上計上先フラグメントを取得
	 * 
	 * @param personId
	 * @param advisorContractSeq
	 * @param contractType
	 * @return
	 */
	@RequestMapping(value = "/getSalesOwnerOptionsFragment", method = RequestMethod.GET)
	public ModelAndView getSalesOwnerOptionsFragment(@RequestParam(name = "personId") Long personId,
			@RequestParam(name = "advisorContractSeq") Long advisorContractSeq,
			@RequestParam("contractType") String contractType) {
		
		AdvisorContractEditForm editForm = service.getEditFormOnlySetDisplayData(personId, advisorContractSeq, contractType);
		
		// 画面（売上計上先フラグメント）をリターン
		ModelAndView mv = getMyModelAndView(editForm, AJAX_SALES_OWNER_FRAGMENT_PATH, EDIT_FORM_NAME);
		super.setAjaxProcResultSuccess();
		
		return mv;
	}
	
	/**
	 * 顧問契約情報の新規登録
	 * 
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	public Map<String, Object> regist(@Validated AdvisorContractEditForm form, BindingResult result) {
		
		Map<String, Object> response = new HashMap<>();
		
		// 担当者の相関チェック
		service.validateTanto(form.getSalesOwner(), result, "salesOwner", "売上計上先");
		service.validateTanto(form.getTantoLawyer(), result, "tantoLawyer", "担当弁護士");
		service.validateTanto(form.getTantoJimu(), result, "tantoJimu", "担当事務員");
		
		// 売上計上先の上限チェック
		service.validateSalesOwnerCount(form.getSalesOwner(), result, "salesOwner");
		
		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// 画面独自のフォームバリデーション
		if(this.inputFormValidated(form, response)) {
			//チェックエラー
			return response;
		}

		// DBアクセスバリデーションは無し
		
		try {
			// 登録処理
			service.regist(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00020, "契約情報"));
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}
	
	/**
	 * 顧問契約情報の更新
	 * 
	 * @param form
	 * @param result
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Map<String, Object> update(@Validated({Default.class, Update.class}) AdvisorContractEditForm form, BindingResult result) {
		
		Map<String, Object> response = new HashMap<>();
		
		// 担当者の相関チェック
		service.validateTanto(form.getSalesOwner(), result, "salesOwner", "売上計上先");
		service.validateTanto(form.getTantoLawyer(), result, "tantoLawyer", "担当弁護士");
		service.validateTanto(form.getTantoJimu(), result, "tantoJimu", "担当事務員");
		
		// 売上計上先の上限チェック
		service.validateSalesOwnerCount(form.getSalesOwner(), result, "salesOwner");
		
		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}
		
		// 画面独自のフォームバリデーション
		if(this.inputFormValidated(form, response)) {
			//チェックエラー
			return response;
		}

		// DBアクセスバリデーションは無し
		
		try {
			// 更新処理
			service.update(form);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "契約情報"));
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}
	
	/**
	 * 顧問契約情報の削除
	 * 
	 * @param advisorContractSeq
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Map<String, Object> delete(@RequestParam(name = "advisorContractSeq") Long advisorContractSeq) {
		
		Map<String, Object> response = new HashMap<>();
		
		// バリデーションチェックは特になし
		
		try {
			// 更新処理
			service.deleteContract(advisorContractSeq);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00028, "契約情報"));
			return response;

		} catch (AppException e) {
			// 保存に失敗した場合の処理
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	* 顧問契約情報の入力項目のバリデーションチェック
	* 
	* @param advisorContractSeq
	* @return
	*/
	private boolean inputFormValidated(AdvisorContractEditForm form, Map<String, Object> response) {
		
		LocalDate startDate = null;
		LocalDate endDate = null;
		// 開始、終了日が両方設定されている場合のみチェックするためLocalDateに変換
		if(StringUtils.isNotEmpty(form.getContractStartDate()) && StringUtils.isNotEmpty(form.getContractEndDate())) {
			startDate = DateUtils.parseToLocalDate(form.getContractStartDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			endDate = DateUtils.parseToLocalDate(form.getContractEndDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		}
		
		// 契約期間日付の前後整合性チェック
		if (!DateUtils.isCorrectDate(startDate, endDate)) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00041, "契約期間の終了日", "契約期間の開始日以降"));
			return true;
		}
		
		return false;
	}

}
