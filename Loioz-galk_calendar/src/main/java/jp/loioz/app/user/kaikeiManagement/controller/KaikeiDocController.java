package jp.loioz.app.user.kaikeiManagement.controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.kaikeiManagement.form.KaikeiListSearchForm;
import jp.loioz.app.user.kaikeiManagement.form.KaikeiListViewForm;
import jp.loioz.app.user.kaikeiManagement.service.KaikeiCommonService;
import jp.loioz.app.user.kaikeiManagement.service.KaikeiDocSevice;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.utility.StringUtils;

@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@LoiozAdminControlCheck(items = {LoiozAdminControl.OLD_KAIKEI_OPEN})
@Controller
@RequestMapping(UrlConstant.KAIKEI_MANAGEMENT_URL)
public class KaikeiDocController extends DefaultController {

	/** 会計管理系画面の共通サービス */
	@Autowired
	private KaikeiCommonService kaikeiCommonService;

	/** 会計記録の帳票出力用のサービスクラス */
	@Autowired
	private KaikeiDocSevice kaikeiDocSevice;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 入出金明細エクセル出力
	 *
	 * @param session
	 * @param response
	 */
	@RequestMapping(value = "/excel/nyushukkinMeisai", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void excelAnkenMeisai(@RequestParam(name = "transitionType") String transitionType,
			@RequestParam(name = "transitionAnkenId", required = false) Long transitionAnkenId,
			@RequestParam(name = "transitionCustomerId", required = false) Long transitionCustomerId,
			@RequestParam(name = "kaikeiKirokuHiddenFlg", required = false) String kaikeiKirokuHiddenFlg,
			HttpSession session, HttpServletResponse response) {

		// 遷移元の判定
		TransitionType transition = TransitionType.of(transitionType);

		// 顧客軸 : 1,案件軸 : 0
		String transitionFlg = SystemFlg.booleanToCode(TransitionType.CUSTOMER == transition);

		// 検索条件の取得
		KaikeiListSearchForm searchForm = (KaikeiListSearchForm) session.getAttribute(KaikeiListController.KAIKEI_SEARCH_FORM_NAME);

		// 出力データの取得
		if (StringUtils.isNotEmpty(kaikeiKirokuHiddenFlg)) {

			searchForm.setKaikeiKirokuHiddenFlg(kaikeiKirokuHiddenFlg);
		}

		KaikeiListViewForm viewForm = kaikeiCommonService.createViewForm(searchForm);

		try {
			// 出力する情報を取得(会計管理の表示と同じロジックを使用)
			viewForm.setTransitionCustomerId(transitionCustomerId);
			viewForm.setTransitionAnkenId(transitionAnkenId);
			viewForm.setTransitionFlg(transitionFlg);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			// 出力処理
			kaikeiDocSevice.excelNyushukkinMeisai(response, viewForm, transition);
		} catch (Exception ex) {
			throw new RuntimeException(getMessage(MessageEnum.MSG_E00034.getMessageKey()), ex);
		}
	}

	/**
	 * タイムチャージ出力を行う
	 *
	 * @param session
	 * @param response
	 */
	@RequestMapping(value = "/excel/timeCharge", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void excelTimeCharge(
			@RequestParam(name = "customerId") Long customerId,
			@RequestParam(name = "ankenId") Long ankenId,
			HttpServletResponse response) {

		try {
			kaikeiDocSevice.createTimeChargeExcelFile(response, customerId, ankenId);
		} catch (Exception ex) {
			throw new RuntimeException(getMessage(MessageEnum.MSG_E00034.getMessageKey()), ex);
		}
	}

	/**
	 * 報酬明細のエクセル出力
	 *
	 * @param transitionType 遷移元種別
	 * @param transitionAnkenId 遷移元案件ID
	 * @param transitionCustomerId 遷移元顧客ID
	 * @param hiddenFlg 表示非表示フラグ
	 * @param response HTTPResponse
	 */
	@RequestMapping(value = "/excel/hoshuMeisai", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputExcelHoshuMeisai(
			@RequestParam(name = "transitionType") String transitionType,
			@RequestParam(name = "transitionAnkenId", required = false) Long transitionAnkenId,
			@RequestParam(name = "transitionCustomerId", required = false) Long transitionCustomerId,
			@RequestParam(name = "selectedAnkenId", required = false) Long ankenId,
			@RequestParam(name = "selectedCustomerId", required = false) Long customerId,
			@RequestParam(name = "seisanCompDispFlg") boolean seisanCompDispFlg,
			HttpServletResponse response) {

		// 遷移元の判定
		TransitionType transition = TransitionType.of(transitionType);

		// 出力処理
		try {
			kaikeiDocSevice.excelHoshuMeisa(response, transition, transitionAnkenId, transitionCustomerId, seisanCompDispFlg, ankenId, customerId);
		} catch (AppException ex) {
			throw new RuntimeException(getMessage(MessageEnum.MSG_E00034.getMessageKey()), ex);
		}

	}

	/**
	 * 支払い計画のExcel出力
	 *
	 * @param seisanSeq
	 * @param response
	 */
	@RequestMapping(value = "/excel/shiharaiKeikaku", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputShiharaiKeikaku(
			@RequestParam(name = "seisanSeq") Long seisanSeq,
			HttpServletResponse response) {

		try {
			// 出力処理
			kaikeiDocSevice.excelShiharaiKeikaku(response, seisanSeq);
		} catch (AppException ex) {
			throw new RuntimeException(getMessage(MessageEnum.MSG_E00034.getMessageKey()), ex);
		}

	}

}
