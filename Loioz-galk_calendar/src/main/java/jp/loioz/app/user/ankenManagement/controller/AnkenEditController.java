package jp.loioz.app.user.ankenManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.user.gyomuHistory.annitations.HasGyomuHistoryByAnken;
import jp.loioz.app.user.schedule.annotations.HasSchedule;
import jp.loioz.common.constant.CommonConstant.MessageLevel;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.StringUtils;

/**
 * 案件情報（民事・刑事）共通画面のコントローラークラス
 */
@Controller
@HasSchedule
@HasGyomuHistoryByAnken
@RequestMapping(value = "user/ankenManagement/edit/{ankenId}")
public class AnkenEditController extends DefaultController {

	/** サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/**
	 * 初期表示
	 *
	 * @param ankenId 案件ID
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable Long ankenId) {

		// 案件の分野が「刑事」かどうか
		boolean isAnkenKeiji = commonAnkenService.isKeiji(ankenId);

		if (isAnkenKeiji) {
			// 案件刑事コントローラーへ
			return ModelAndViewUtils.getRedirectModelAndView(AnkenEditKeijiController.class, controller -> controller.index(ankenId));
		} else {
			// 案件民事コントローラーへ
			return ModelAndViewUtils.getRedirectModelAndView(AnkenEditMinjiController.class, controller -> controller.index(ankenId));
		}
	}

	/**
	 * 初期表示
	 *
	 * @param ankenId
	 * @param customerId
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/{personId}/", method = RequestMethod.GET)
	public ModelAndView indexAndCustomerId(@PathVariable Long ankenId, @PathVariable Long personId) {

		// 案件の分野が「刑事」かどうか
		boolean isAnkenKeiji = commonAnkenService.isKeiji(ankenId);

		if (isAnkenKeiji) {
			// 案件刑事コントローラーへ
			return ModelAndViewUtils.getRedirectModelAndView(AnkenEditKeijiController.class, controller -> controller.index(ankenId));
		} else {
			// 案件民事コントローラーへ
			return ModelAndViewUtils.getRedirectModelAndView(AnkenEditMinjiController.class, controller -> controller.index(ankenId));
		}
	}

	/**
	 * 案件追加後の案件編集画面初期表示（案件の基本情報部分を入力フォームで表示する）。<br>
	 * 分野が刑事か民事か判定し、分野用の案件編集画面初期表示をおこなう。<br>
	 *
	 * @param ankenId
	 * @param redirectAttributes
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/indexAfterAnkenRegist", method = RequestMethod.GET)
	public ModelAndView indexAfterAnkenRegist(@PathVariable Long ankenId, RedirectAttributes redirectAttributes) {

		// 案件の分野が「刑事」かどうか
		boolean isAnkenKeiji = commonAnkenService.isKeiji(ankenId);

		if (isAnkenKeiji) {
			// 案件刑事コントローラーへ
			return ModelAndViewUtils.getRedirectModelAndView(AnkenEditKeijiController.class, controller -> controller.indexAfterAnkenRegist(ankenId, redirectAttributes));
		} else {
			// 案件民事コントローラーへ
			return ModelAndViewUtils.getRedirectModelAndView(AnkenEditMinjiController.class, controller -> controller.indexAfterAnkenRegist(ankenId, redirectAttributes));
		}
	}

	/**
	 * メッセージ表示をともなう初期表示画面へのリダイレクト処理
	 * 
	 * @param ankenIdId
	 * @param levelCd メッセージレベル
	 * @param message メッセージ
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "redirectIndexWithMessage", method = RequestMethod.GET)
	public ModelAndView redirectIndexWithMessage(@PathVariable Long ankenId,
			@RequestParam("level") String levelCd, @RequestParam("message") String message,
			RedirectAttributes redirectAttributes) {

		// 案件の分野が「刑事」かどうか
		boolean isAnkenKeiji = commonAnkenService.isKeiji(ankenId);

		if (isAnkenKeiji) {
			// 自画面のindexメソッドへのリダイレクトオブジェクトを生成
			String redirectPath = ModelAndViewUtils.getRedirectPath(AnkenEditKeijiController.class, controller -> controller.index(ankenId));
			RedirectViewBuilder redirectViewBuilder = new RedirectViewBuilder(redirectAttributes, redirectPath);
			// 案件刑事コントローラーへ
			MessageLevel level = MessageLevel.of(levelCd);
			return redirectViewBuilder.build(MessageHolder.ofLevel(level, StringUtils.lineBreakStr2Code(message)));
		} else {
			// 自画面のindexメソッドへのリダイレクトオブジェクトを生成
			String redirectPath = ModelAndViewUtils.getRedirectPath(AnkenEditMinjiController.class, controller -> controller.index(ankenId));
			RedirectViewBuilder redirectViewBuilder = new RedirectViewBuilder(redirectAttributes, redirectPath);
			// 案件民事コントローラーへ
			MessageLevel level = MessageLevel.of(levelCd);
			return redirectViewBuilder.build(MessageHolder.ofLevel(level, StringUtils.lineBreakStr2Code(message)));
		}

	}


}
