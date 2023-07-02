package jp.loioz.app.user.fileManagementAllSearch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.app.common.controller.PageableController;
import jp.loioz.app.user.fileManagementAllSearch.form.FileAllSearchViewForm;
import jp.loioz.app.user.fileManagementAllSearch.service.FileAllSearchService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.PermissionException;
import jp.loioz.common.message.MessageHolder;

/**
 * ファイル管理全体検索画面 Controller
 */
@Controller
@RequestMapping(value = "user/fileManagementAllSearch")
@SessionAttributes(FileAllSearchController.VIEW_FORM_NAME)
public class FileAllSearchController extends DefaultController implements PageableController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/fileManagementAllSearch/fileAllSearch";

	/** 出力フォームオブジェクト名 */
	public static final String VIEW_FORM_NAME = "fileAllSearchViewForm";

	/** 全体ファイル管理検索Service */
	@Autowired
	private FileAllSearchService fileAllSearchService;

	@Autowired
	private CommonOAuthService commonOAuthService;

	/** フォームクラスの初期化 */
	@ModelAttribute(VIEW_FORM_NAME)
	FileAllSearchViewForm setUpForm() {
		return new FileAllSearchViewForm();
	}

	/**
	 * 初期表示
	 *
	 * @param fileManagementAllSearchForm 画面表示用フォーム
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(FileAllSearchViewForm fileAllSearchViewForm) {

		this.checkAccessPermission();

		fileAllSearchViewForm = setUpForm();
		return getMyModelAndView(fileAllSearchViewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 検索処理
	 *
	 * @param fileAllSearchViewForm 検索条件
	 * @param bindingResult validation結果
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView search(@Validated FileAllSearchViewForm fileAllSearchViewForm,
			BindingResult bindingResult, MessageHolder msgHolder) {

		this.checkAccessPermission();

		if (bindingResult.hasErrors()) {
			if (bindingResult.getErrorCount() > 1) {
				String errorMsg = super.getMessage(MessageEnum.MSG_E00001.getMessageKey());
				msgHolder.setErrorMsg(errorMsg);
			} else {
				msgHolder.setErrorMsg(bindingResult.getAllErrors().get(0).getDefaultMessage());
			}

			return this.getMyModelAndView(fileAllSearchViewForm, MY_VIEW_PATH, VIEW_FORM_NAME, msgHolder);
		}
		// 検索条件が全て空の場合エラーとする。
		if (fileAllSearchViewForm.getFileName().isEmpty() && fileAllSearchViewForm.getUploadUser().isEmpty()
				&& (fileAllSearchViewForm.getFileType() == null || fileAllSearchViewForm.getFileType().isEmpty())) {
			String errorMsg = super.getMessage(MessageEnum.MSG_E00081.getMessageKey());
			msgHolder.setErrorMsg(errorMsg);
			return this.getMyModelAndView(fileAllSearchViewForm, MY_VIEW_PATH, VIEW_FORM_NAME, msgHolder);
		}

		// 検索
		fileAllSearchService.setSearchResultToForm(fileAllSearchViewForm);
		return getMyModelAndView(fileAllSearchViewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * ページャー処理
	 *
	 * @param fileAllSearchViewForm
	 * @return
	 */
	@RequestMapping(value = "/pager", method = RequestMethod.GET)
	public ModelAndView pager(FileAllSearchViewForm fileAllSearchViewForm) {
		this.checkAccessPermission();
		fileAllSearchService.setSearchResultToForm(fileAllSearchViewForm);
		return getMyModelAndView(fileAllSearchViewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 外部ストレージサービスを利用している場合、PermissionError
	 */
	private void checkAccessPermission() {
		if (commonOAuthService.useExternalStorage()) {
			// 権限エラー画面にリダイレクトする
			throw new PermissionException();
		}
	}

}
