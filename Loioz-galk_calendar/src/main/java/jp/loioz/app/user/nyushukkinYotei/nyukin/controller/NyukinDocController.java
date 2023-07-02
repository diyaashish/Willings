package jp.loioz.app.user.nyushukkinYotei.nyukin.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.service.chohyo.CommonChohyoService;
import jp.loioz.app.user.nyushukkinYotei.nyukin.form.NyukinListSearchForm;
import jp.loioz.app.user.nyushukkinYotei.nyukin.form.NyukinListViewForm;
import jp.loioz.app.user.nyushukkinYotei.nyukin.service.NyukinDocService;
import jp.loioz.app.user.nyushukkinYotei.nyukin.service.NyukinListService;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.dto.AnkenTantoAccountDto;
import jp.loioz.dto.NyukinListDto;

/**
 * 入金一覧出力のコントローラー
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@LoiozAdminControlCheck(items = {LoiozAdminControl.OLD_KAIKEI_OPEN})
@Controller
@RequestMapping(UrlConstant.NYUSHUKKIN_YOTEI_NYUKIN_URL)
public class NyukinDocController extends DefaultController {

	/** 入金一覧出力用サービスクラス */
	@Autowired
	private NyukinListService listService;

	/** 入金一覧出力用サービスクラス */
	@Autowired
	private NyukinDocService docService;

	/** 帳票用共通サービスクラス */
	@Autowired
	private CommonChohyoService commonChohyoService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 入金一覧のExcel出力
	 *
	 * @param session
	 * @param response
	 */
	@RequestMapping(value = "/excel", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void excelNyukinList(HttpSession session, HttpServletResponse response) {

		// 直前の検索条件を取得
		NyukinListSearchForm searchForm = (NyukinListSearchForm) session.getAttribute(NyukinListController.SEARCH_FORM_NAME);

		NyukinListViewForm viewForm = new NyukinListViewForm();
		listService.setData(searchForm, viewForm);
		for (NyukinListDto dto : viewForm.getNyukinList()) {

			// 担当弁護士、担当事務情報の取得
			List<AnkenTantoAccountDto> dispAnkenTantoList = commonChohyoService.dispAnkenTantoBengoshiJimuAll(dto.getAnkenId().asLong());

			// 弁護士のみ抽出
			List<AnkenTantoAccountDto> ankenBengoshiList = commonChohyoService.dispAnkenTantoBengoshi(dispAnkenTantoList);
			dto.setDispTantoLawer(commonChohyoService.dispAnkenTantoToComma(ankenBengoshiList));

			// 事務のみ抽出
			List<AnkenTantoAccountDto> ankenJimuList = commonChohyoService.dispAnkenTantoJimu(dispAnkenTantoList);
			dto.setDispTantoJimu(commonChohyoService.dispAnkenTantoToComma(ankenJimuList));

		}
		// 出力用データの取得
		List<NyukinListDto> data = new ArrayList<NyukinListDto>(viewForm.getNyukinList());

		// 出力処理
		docService.excelNyukinList(response, data, searchForm);
	}
}
