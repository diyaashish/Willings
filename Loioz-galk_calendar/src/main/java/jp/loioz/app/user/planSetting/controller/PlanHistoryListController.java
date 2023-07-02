package jp.loioz.app.user.planSetting.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.planSetting.dto.PlanSettingSessionInfoDto;
import jp.loioz.app.user.planSetting.exception.PlanSettingAuthException;
import jp.loioz.app.user.planSetting.form.PlanHistoryListForm;
import jp.loioz.app.user.planSetting.service.PlanHistoryListService;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.dataSource.SchemaContextHolder;
import jp.loioz.dto.PlanHistoryDto;

/**
 * プラン履歴画面のコントローラークラス
 */
@Controller
@RequestMapping(UrlConstant.PLANSETTING_URL)
public class PlanHistoryListController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/planSetting/planHistoryList";

	/** コントローラーに対するviewパス */
	private static final String AJAX_MODAL_PATH = MY_VIEW_PATH + "::select-modal";
	
	/** viewのformで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "planHistoryListForm";

	/** プラン設定画面のコントローラークラ **/
	@Autowired
	private PlanSettingController planSettingController;
	
	/** プラン履歴一覧画面のサービスクラス **/
	@Autowired
	private PlanHistoryListService service;
	
	// =========================================================================
	// Controller独自の例外ハンドラー
	// =========================================================================
	/**
	 * このController内（現状ではsetUpCommonDataメソッドのみ）でthrowされたPlanSettingAuthExceptionをキャッチしハンドリングする。<br>
	 * ajaxかhttpリクエストかを判定し、認証エラー画面を表示するための適切なレスポンスをresponseに設定する。<br>
	 * ※処理の内容はプラン画面と同じものとする
	 * 
	 * @param ex
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@ExceptionHandler(PlanSettingAuthException.class)
	public void handleCustomException(PlanSettingAuthException ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
		planSettingController.handleCustomException(ex, request, response);
	}
	
	// =========================================================================
	// ModelAttribute
	// =========================================================================
	
	/**
	 * プラン履歴画面用のコントローラ（本コントローラ）のメソッドにアクセスする前に、
	 * 不正アクセスチェックや権限チェックを行い、アクセス可能であればSession情報に相当するSessionDtoをmodelに設定する共通処理。<br>
	 * ※処理の内容はプラン画面と同じものとする
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@ModelAttribute
	PlanSettingSessionInfoDto setUpCommonData(HttpServletRequest request, Model model) {
		return planSettingController.setUpCommonData(request, model);
	}
	
	// =========================================================================
	// public メソッド
	// =========================================================================
	
	/**
	 * プラン履歴モーダルの表示
	 * 
	 * @param sessionDto
	 * @return
	 */
	@RequestMapping(value = "/planHistory/list", method = RequestMethod.GET)
	public ModelAndView index(PlanSettingSessionInfoDto sessionDto) {
		// viewFormの作成
		PlanHistoryListForm viewForm = this.setUpEditForm();
		
		Long tenantSeq = sessionDto.getTenantSeq();
		
		try {
			
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
		
			List<PlanHistoryDto> planHistoryList = service.getPlanHistoryList(sessionDto, viewForm);
			viewForm.setPlanHistoryList(planHistoryList);

		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}
		
		return getMyModelAndView(viewForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}
	
	// =========================================================================
	// private メソッド
	// =========================================================================
	
	/**
	 * フォームのセットアップ
	 * @return
	 */
	private PlanHistoryListForm setUpEditForm() {
		return new PlanHistoryListForm();
	}
}
