package jp.loioz.app.user.azukarikinManagement.controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.azukarikinManagement.form.AzukarikinSearchForm;
import jp.loioz.app.user.azukarikinManagement.form.AzukarikinViewForm;
import jp.loioz.app.user.azukarikinManagement.service.AzukarikinDocumentService;
import jp.loioz.app.user.azukarikinManagement.service.AzukarikinService;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;

@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@LoiozAdminControlCheck(items = {LoiozAdminControl.OLD_KAIKEI_OPEN})
@Controller
@RequestMapping(UrlConstant.AZUKARIKIN_MANAGEMENT_URL)
public class AzukarikinDocumentController extends DefaultController {

	/** 預り金・未収金一覧出力用サービスクラス */
	@Autowired
	private AzukarikinDocumentService azuykarikinDocService;

	/** 親画面のサービス */
	@Autowired
	private AzukarikinService azukarikinService;

	/**
	 * 預り金・未収金一覧 帳票出力
	 *
	 * @param session
	 * @param response
	 */
	@RequestMapping(value = "/excel/list", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void excelAzukarikinList(HttpSession session, HttpServletResponse response) {

		// 直前の検索条件を取得
		AzukarikinSearchForm searchForm = (AzukarikinSearchForm) session.getAttribute(AzukarikinListController.SEARCH_FORM_NAME);

		// 画面情報の作成
		AzukarikinViewForm viewForm = new AzukarikinViewForm();
		azukarikinService.setData(viewForm, searchForm);

		try {
			azuykarikinDocService.excelAzukarikinList(response, viewForm);
		} catch (Exception ex) {
			throw new RuntimeException(getMessage(MessageEnum.MSG_E00034.getMessageKey()), ex);
		}

	}

}
