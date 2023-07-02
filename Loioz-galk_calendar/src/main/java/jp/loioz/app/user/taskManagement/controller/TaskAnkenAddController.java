package jp.loioz.app.user.taskManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.taskManagement.form.TaskAnkenAddModalSearchForm;
import jp.loioz.app.user.taskManagement.form.TaskAnkenAddModalViewForm;
import jp.loioz.app.user.taskManagement.service.TaskAnkenAddService;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.SessionUtils;

/**
 * 案件タスク追加モーダルコントローラー
 */
@Controller
@RequestMapping("user/taskManagement")
public class TaskAnkenAddController extends DefaultController {

	/** 案件タスク追加モーダルのコントローラーパス */
	private static final String TASK_ANKEN_ADD_MODAL_PATH = "user/taskManagement/taskAnkenAddModal";
	/** 案件タスク追加モーダルのフラグメントパス */
	private static final String TASK_ANKEN_ADD_MODAL_FRAGMENT_PATH = TASK_ANKEN_ADD_MODAL_PATH + "::taskAnkenAddModalFragment";
	/** 案件タスク追加モーダルのフラグメント表示フォームオブジェクト名 */
	private static final String TASK_ANKEN_ADD_MODAL_VIEW_FORM_NAME = "taskAnkenAddModalViewForm";

	/** 案件タスク追加モーダル：タイトル */
	public static final String TASK_ANKEN_ADD_TITLE = "案件タスク追加";

	@Autowired
	private TaskAnkenAddService taskAnkenAddService;

	/**
 	 * 案件タスク追加モーダル表示<br>
	 * 検索条件フォームを初期化して、一覧に表示する案件データを取得。
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/openTaskAnkenAddModal", method = RequestMethod.GET)
	public ModelAndView index(TaskAnkenAddModalSearchForm searchForm) {

		ModelAndView mv = null;

		// 検索条件フォーム初期化
		taskAnkenAddService.initModalSearchForm(searchForm);

		// ログインユーザのアカウントタイプに応じて「自分の担当案件」検索条件フォームに「担当弁護士」、「担当事務」をセットする。
		if (AccountType.LAWYER.equals(SessionUtils.getAccountType())) {
			searchForm.setTantoLaywer(SessionUtils.getLoginAccountSeq());
		} else if (AccountType.JIMU.equals(SessionUtils.getAccountType())) {
			searchForm.setTantoJimu(SessionUtils.getLoginAccountSeq());
		}

		// 案件データ検索
		TaskAnkenAddModalViewForm viewForm = taskAnkenAddService.createModalViewForm(searchForm, SessionUtils.getLoginAccountSeq());
		mv = getMyModelAndView(viewForm, TASK_ANKEN_ADD_MODAL_FRAGMENT_PATH, TASK_ANKEN_ADD_MODAL_VIEW_FORM_NAME);

		if (mv == null) {
			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件タスク追加モーダル表示<br>
	 * パラメータで受け取った検索条件で、一覧に表示する案件データを取得。
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/searchTaskAnkenAddList", method = RequestMethod.GET)
	public ModelAndView searchTaskAnkenAddList(@Validated TaskAnkenAddModalSearchForm searchForm, BindingResult result) {

		ModelAndView mv = null;

		// 入力バリデーション
		if (result.hasErrors()) {
			// 検索条件にエラーがある場合は検索結果0件として表示する
			TaskAnkenAddModalViewForm viewForm = new TaskAnkenAddModalViewForm();
			mv = getMyModelAndView(viewForm, TASK_ANKEN_ADD_MODAL_FRAGMENT_PATH, TASK_ANKEN_ADD_MODAL_VIEW_FORM_NAME);
			super.setAjaxProcResultSuccess();
			return mv;
		}

		// 表示用データの設定
		taskAnkenAddService.setDisplayData(searchForm);

		// 案件データ検索
		TaskAnkenAddModalViewForm viewForm = taskAnkenAddService.createModalViewForm(searchForm, SessionUtils.getLoginAccountSeq());
		mv = getMyModelAndView(viewForm, TASK_ANKEN_ADD_MODAL_FRAGMENT_PATH, TASK_ANKEN_ADD_MODAL_VIEW_FORM_NAME);

		if (mv == null) {
			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 案件タスク登録処理
	 * 
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/registAnkenTaskAdd", method = RequestMethod.POST)
	public ModelAndView registAnkenTaskAdd(@RequestParam(name = "ankenId", required = true) Long ankenId) {

		try {
			// 案件タスク追加登録処理
			taskAnkenAddService.registAnkenTaskAdd(ankenId);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "案件タスク"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

}
