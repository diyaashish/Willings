package jp.loioz.app.user.seisansho.controller;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.seisansho.form.SeisanshoEditForm;
import jp.loioz.app.user.seisansho.form.SeisanshoViewForm;
import jp.loioz.app.user.seisansho.service.SeisanshoDocService;
import jp.loioz.app.user.seisansho.service.SeisanshoEditService;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dto.ExcelSeisanshoSetDto;

/**
 * 精算書出力のコントローラークラス
 *
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@LoiozAdminControlCheck(items = {LoiozAdminControl.OLD_KAIKEI_OPEN})
@Controller
@RequestMapping(UrlConstant.SEISANSHO_URL)
public class SeisanshoDocController extends DefaultController {

	/** 精算書作成サービスクラス */
	@Autowired
	private SeisanshoEditService editService;

	/** Excel出力用サービスクラス */
	@Autowired
	private SeisanshoDocService seisanshoDocService;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	/**
	 * 精算書のエクセル出力
	 *
	 * @param response
	 * @param seisanSeq
	 * @throws AppException
	 */
	@RequestMapping(value = "/outputSeisansho", method = RequestMethod.POST, produces = OutputConstant.BINARY_UTF8)
	public void outputSeisansho(HttpServletResponse response, @RequestParam(name = "seisanSeq", required = true) Long seisanSeq) throws AppException {

		// 精算記録情報取得
		SeisanshoEditForm editForm = editService.getSeisansho(seisanSeq);
		String seisanKubun = editForm.getSeisanshoCreateDto().getSeisanKubun();

		// 精算Seqに紐づく会計情報取得
		editForm.setKaikeiSeqList(editService.getKaikeikirokuByseisanSeq(seisanSeq));

		// 再計算処理
		SeisanshoViewForm reCalculationForm = editService.recalculation(editForm.getKaikeiSeqList());
		BigDecimal reSeisangakuTotal = reCalculationForm.getSeisangakuTotal();

		// 精算額0円かのフラグ
		boolean seisanZeroFlg = false;
		if (reSeisangakuTotal != null) {
			// 精算金額の判定

			if (reSeisangakuTotal.compareTo(BigDecimal.ZERO) > 0) {
				// 請求
				seisanZeroFlg = false;

			} else if (reSeisangakuTotal.compareTo(BigDecimal.ZERO) == 0) {
				// 0円精算
				seisanZeroFlg = true;

			} else {
				// 精算
				seisanZeroFlg = false;

			}

		}

		try {

			// 精算書（請求書）データ一式を取得
			ExcelSeisanshoSetDto kariseisanData = seisanshoDocService.createSeisanDataAll(editForm, seisanKubun, seisanZeroFlg);

			// エクセル出力
			seisanshoDocService.createExcelFile(response, kariseisanData, seisanKubun, seisanZeroFlg);

		} catch (Exception ex) {
			// 精算書（請求書）の出力失敗
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName, ex);
		}

	}

}
