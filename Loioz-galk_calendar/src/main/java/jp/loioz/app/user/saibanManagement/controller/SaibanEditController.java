package jp.loioz.app.user.saibanManagement.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonSaibanService;
import jp.loioz.bean.SaibanAnkenBean;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 裁判管理画面のコントローラークラス
 * 
 * <pre>
 * 裁判画面は、民事裁判画面と刑事裁判画面の2つの画面が存在するが、
 * アクセス時のURLは２つの裁判画面で共通となっており、内部のデータ状態で民事、刑事のどちらの裁判画面を表示するかが決定する。
 * 
 * このクラスは、民事裁判画面、刑事裁判画面を表示するための入り口となる共通のメソッドを提供する。
 * （メソッドは実際にどちらの裁判画面を表示するかのハンドリングをし、表示する裁判画面の表示処理へリダイレクトする。）
 * </pre>
 */
@Controller
@RequestMapping(value = "user/saibanManagement/{ankenId}")
public class SaibanEditController extends DefaultController {

	/** 共通裁判サービスクラス */
	@Autowired
	private CommonSaibanService commonSaibanService;

	/** 案件情報関連の共通サービス処理 */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 裁判管理（民事・刑事）の編集画面の表示
	 * 
	 * @param ankenId
	 * @param branchNumber
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{branchNumber}", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable Long ankenId, @PathVariable Long branchNumber,
			HttpServletRequest request) {

		// 裁判と案件を取得
		SaibanAnkenBean bean = commonSaibanService.getSaibanAnken(ankenId, branchNumber);
		Long saibanSeq = bean.getSaibanSeq();
		
		// 刑事分野の判定
		if (commonBunyaService.isKeiji(bean.getBunyaId())) {
			// 裁判刑事コントローラーへ
			return ModelAndViewUtils.getRedirectModelAndView(SaibanEditKeijiController.class, controller -> controller.index(saibanSeq, ankenId));
		} else {
			
			// saibanSeqが子裁判のものの場合、親裁判画面へ遷移させるため、親裁判の裁判SEQを取得する
			// （saibanSeqがもともと親裁判のものの場合は同じ値になる）
			Long parntSaibanSeq = commonSaibanService.getParentSaibanSeq(saibanSeq);
			
			// 裁判民事コントローラーへ
			return ModelAndViewUtils.getRedirectModelAndView(SaibanEditMinjiController.class, controller -> controller.index(parntSaibanSeq, ankenId));
		}

	}

	/**
	 * 裁判登録（民事・刑事）画面の表示
	 * 
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/new/", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable Long ankenId) {

		// 刑事案件かどうか
		boolean isKeiji = commonAnkenService.isKeiji(ankenId);

		if (isKeiji) {
			// 裁判登録（刑事）コントローラーへ
			return ModelAndViewUtils.getRedirectModelAndView(SaibanRegistKeijiController.class, controller -> controller.index(ankenId));	
		} else {
			// 裁判登録（民事）コントローラーへ
			return ModelAndViewUtils.getRedirectModelAndView(SaibanRegistMinjiController.class, controller -> controller.index(ankenId));
		}

	}

}
