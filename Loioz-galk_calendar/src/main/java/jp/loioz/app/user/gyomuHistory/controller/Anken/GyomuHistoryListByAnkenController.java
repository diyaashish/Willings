package jp.loioz.app.user.gyomuHistory.controller.Anken;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.controller.PageableController;
import jp.loioz.app.user.gyomuHistory.annitations.HasGyomuHistoryByAnken;
import jp.loioz.app.user.gyomuHistory.form.Anken.GyomuHistoryListByAnkenSearchForm;
import jp.loioz.app.user.gyomuHistory.form.Anken.GyomuHistoryListByAnkenViewForm;
import jp.loioz.app.user.gyomuHistory.form.Common.ChangeImportantRequest;
import jp.loioz.app.user.gyomuHistory.service.Anken.GyomuHistoryListByAnkenService;
import jp.loioz.app.user.gyomuHistory.service.Common.CommonGyomuHistoryService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 業務履歴一覧画面 案件側
 * 
 * <pre>
 * 表示する内容、検索条件、業務履歴の登録ロジックなどが
 * 顧客側と異なるため顧客側と案件側でロジックを区別化
 * </pre>
 *
 */
@Controller
@HasGyomuHistoryByAnken
@RequestMapping("user/gyomuHistory/anken")
@SessionAttributes(GyomuHistoryListByAnkenController.SEARCH_FORM_NAME)
public class GyomuHistoryListByAnkenController extends DefaultController implements PageableController {

	/** コントローラと対応するviewパス **/
	public static final String MY_VIEW_PATH = "user/gyomuHistory/anken/gyomuHistoryListByAnken";

	/** 業務履歴一覧の1行 AjaxViewのパス */
	public static final String LIST_VIEW_AJAX_PATH = "user/gyomuHistory/anken/gyomuHistoryListByAnkenFragment::gyomuHistoryListRowByAnkenFragment";

	/** 検索条件のFormクラス */
	public static final String SEARCH_FORM_NAME = "gyomuHistoryListByAnkenSearchForm";

	/** viewで使用するフォームオブジェクト名 **/
	public static final String VIEW_FORM_NAME = "gyomuHistoryListByAnkenViewForm";

	/** 業務履歴 顧客側のサービスクラス */
	@Autowired
	private GyomuHistoryListByAnkenService ankenService;

	/** 業務履歴共通処理 */
	@Autowired
	private CommonGyomuHistoryService commonGyomuHistoryService;

	/** 検索条件のModelAttribute */
	@ModelAttribute(SEARCH_FORM_NAME)
	GyomuHistoryListByAnkenSearchForm setUpSearchForm() {
		return new GyomuHistoryListByAnkenSearchForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 案件側 一覧表示
	 * 
	 * @param searchForm
	 * @return 業務履歴一覧画面
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute(SEARCH_FORM_NAME) GyomuHistoryListByAnkenSearchForm searchForm) {

		// 初期化
		searchForm.initForm();

		// viewFormの作成
		GyomuHistoryListByAnkenViewForm viewForm = ankenService.createViewForm(searchForm);

		// 業務履歴ヘッダー情報の取得
		ankenService.setHeader(viewForm, searchForm);

		// 業務履歴一覧情報の取得
		ankenService.setGyomuHistoryList(viewForm, searchForm);

		// 画面情報をリターン
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 案件側 一覧表示<br>
	 * 
	 * 顧客軸の業務履歴から遷移する場合。
	 * 
	 * @param searchForm 業務履歴画面の検索条件フォーム
	 * @return
	 */
	@RequestMapping(value = "/fromCustomer", method = RequestMethod.GET)
	public ModelAndView fromCustomer(@ModelAttribute(SEARCH_FORM_NAME) GyomuHistoryListByAnkenSearchForm searchForm) {
		return ModelAndViewUtils.getRedirectModelAndView(this.getClass(), controller -> controller.index(null));
	}

	/**
	 * 検索(全体ボタン押下時)
	 * 
	 * @param searchForm
	 * @return 業務履歴一覧画面
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ModelAndView all(@ModelAttribute(SEARCH_FORM_NAME) GyomuHistoryListByAnkenSearchForm searchForm) {

		// 検索条件の設定
		searchForm.resetSearchCustomerId();
		searchForm.setDefaultPage();

		// viewFormの作成
		GyomuHistoryListByAnkenViewForm viewForm = ankenService.createViewForm(searchForm);

		// 業務履歴ヘッダー情報の取得
		ankenService.setHeader(viewForm, searchForm);

		// 業務履歴一覧情報の取得
		ankenService.setGyomuHistoryList(viewForm, searchForm);

		// 画面情報をリターン
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 検索
	 * 
	 * @param searchForm
	 * @return 業務履歴一覧画面
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView search(@ModelAttribute(SEARCH_FORM_NAME) GyomuHistoryListByAnkenSearchForm searchForm) {

		// 検索条件の設定
		searchForm.setDefaultPage();

		// viewFormの作成
		GyomuHistoryListByAnkenViewForm viewForm = ankenService.createViewForm(searchForm);

		// 業務履歴ヘッダー情報の取得
		ankenService.setHeader(viewForm, searchForm);

		// 業務履歴一覧情報の取得
		ankenService.setGyomuHistoryList(viewForm, searchForm);

		// 画面情報をリターン
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * ページング
	 * 
	 * @param searchForm
	 * @return 業務履歴一覧画面
	 */
	@RequestMapping(value = "/page", method = RequestMethod.GET)
	public ModelAndView page(@ModelAttribute(SEARCH_FORM_NAME) GyomuHistoryListByAnkenSearchForm searchForm) {

		// viewFormの作成
		GyomuHistoryListByAnkenViewForm viewForm = ankenService.createViewForm(searchForm);

		// 業務履歴ヘッダー情報の取得
		ankenService.setHeader(viewForm, searchForm);

		// 業務履歴一覧情報の取得
		ankenService.setGyomuHistoryList(viewForm, searchForm);

		// 画面情報をリターン
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 重要フラグ変更時の処理
	 * 
	 * @param requestData
	 * @param result
	 * @param msgHolder
	 * @return 更新画面情報
	 * 
	 * @throws AppException
	 */
	@ResponseBody
	@RequestMapping(value = "/changeImportantFlg", method = RequestMethod.POST)
	public Map<String, Object> changeImportantFlg(
			GyomuHistoryListByAnkenSearchForm searchForm,
			@Validated ChangeImportantRequest requestData,
			BindingResult result) {

		Map<String, Object> response = new HashMap<>(); // 返却値

		// バリデーションチェック
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00036));
			return response;
		}

		// 重要フラグを更新します。
		try {
			commonGyomuHistoryService.changeImportantFlg(requestData);
			// 返すメッセージを設定
			String msg = "重要を解除";
			if (CommonConstant.SystemFlg.FLG_ON.equalsByCode(CommonUtils.stringBoolanToFlg(requestData.getImportantFlg()))) {
				msg = "重要に設定";
			}
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00045, msg));
			return response;
		} catch (AppException e) {
			response.put("succeeded", false);
			response.put("message", getMessage(e.getErrorType()));
			return response;
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
