package jp.loioz.app.user.nyushukkinYotei.shukkin.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.service.chohyo.CommonChohyoService;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ShukkinListSearchForm;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ShukkinListViewForm;
import jp.loioz.app.user.nyushukkinYotei.shukkin.service.ShukkinDocService;
import jp.loioz.app.user.nyushukkinYotei.shukkin.service.ShukkinListService;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.dto.AnkenTantoAccountDto;
import jp.loioz.dto.ShukkinListDto;

@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@LoiozAdminControlCheck(items = {LoiozAdminControl.OLD_KAIKEI_OPEN})
@Controller
@RequestMapping(UrlConstant.NYUSHUKKIN_YOTEI_SHUKKIN_URL)
public class ShukkinDocController extends DefaultController {

	/** 出金一覧の帳票出力用サービスクラス */
	@Autowired
	private ShukkinListService listService;

	/** 出金一覧の帳票出力用サービスクラス */
	@Autowired
	private ShukkinDocService docService;

	/** 帳票用共通サービスクラス */
	@Autowired
	private CommonChohyoService commonChohyoService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 出金一覧のExcel出力
	 *
	 * @param session
	 * @param response
	 */
	@RequestMapping(value = "/excel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void excelShkkinList(HttpSession session, HttpServletResponse response) {

		// 直前の検索条件を取得
		ShukkinListSearchForm searchForm = (ShukkinListSearchForm) session.getAttribute(ShukkinListController.SEARCH_FORM_NAME);

		// 出力するデータの取得
		ShukkinListViewForm viewForm = new ShukkinListViewForm();
		listService.setData(searchForm, viewForm);
		for (ShukkinListDto dto : viewForm.getShukkinList()) {

			// 担当弁護士、担当事務情報の取得
			List<AnkenTantoAccountDto> dispAnkenTantoList = commonChohyoService.dispAnkenTantoBengoshiJimuAll(dto.getAnkenId().asLong());

			// 弁護士のみ抽出
			List<AnkenTantoAccountDto> ankenBengoshiList = commonChohyoService.dispAnkenTantoBengoshi(dispAnkenTantoList);
			dto.setDispTantoLawer(commonChohyoService.dispAnkenTantoToComma(ankenBengoshiList));

			// 事務のみ抽出
			List<AnkenTantoAccountDto> ankenJimuList = commonChohyoService.dispAnkenTantoJimu(dispAnkenTantoList);
			dto.setDispTantoJimu(commonChohyoService.dispAnkenTantoToComma(ankenJimuList));

		}
		// 出力処理
		docService.excelShukkinList(response, viewForm.getShukkinList(), searchForm);
	}

}
