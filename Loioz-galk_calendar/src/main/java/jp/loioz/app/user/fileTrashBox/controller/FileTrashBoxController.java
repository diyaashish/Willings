package jp.loioz.app.user.fileTrashBox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.fileTrashBox.form.FileTrashBoxListViewForm;
import jp.loioz.app.user.fileTrashBox.form.FileTrashBoxListViewForm.JsonResponseData;
import jp.loioz.app.user.fileTrashBox.form.FileTrashBoxMentenanceViewForm;
import jp.loioz.app.user.fileTrashBox.service.FileTrashBoxService;
import jp.loioz.common.constant.CommonConstant.FileKubun;
import jp.loioz.common.constant.StatusCodeEnum;
import jp.loioz.common.exception.AppException;

/**
 * ゴミ箱(ファイル管理)画面 Controller
 */
@Controller
@RequestMapping(value = "user/fileTrashBox")
public class FileTrashBoxController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/fileTrashBox/fileTrashBox";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "fileTrashBoxMentenanceViewForm";

	/** ３点メニューのviewのパス */
	private static final String AJAX_MODAL_PATH_MENTENANCE = "user/fileTrashBox/fileTrashBoxEdit::mentenanceModal";

	/** サービスクラス */
	@Autowired
	private FileTrashBoxService fileTrashBoxService;

	/** フォームクラスの初期化 */
	@ModelAttribute(VIEW_FORM_NAME)
	FileTrashBoxListViewForm setUpForm() {
		return new FileTrashBoxListViewForm();
	}

	/**
	 * ファイル管理画面からの遷移
	 *
	 * @param fileTrashBoxListViewForm 遷移元情報
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView index(FileTrashBoxListViewForm fileTrashBoxListViewForm) {
		fileTrashBoxService.createViewForm(fileTrashBoxListViewForm);
		return getMyModelAndView(fileTrashBoxListViewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * ファイル名クイック検索
	 *
	 * @param fileTrashBoxListViewForm 遷移元情報
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/quickSearch", method = RequestMethod.GET)
	public ModelAndView quickSearch(FileTrashBoxListViewForm fileTrashBoxListViewForm) {
		fileTrashBoxService.setSearchResultToForm(fileTrashBoxListViewForm);
		return getMyModelAndView(fileTrashBoxListViewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 成功用のレスポンスデータを作成する
	 *
	 * @param errorCd エラーコード
	 * @param message メッセージ
	 * @return jsonデータ(成功用)
	 */
	private JsonResponseData createSuccessResponse(String errorCd, String message) {
		JsonResponseData successResponse = new FileTrashBoxListViewForm().new JsonResponseData();
		successResponse.setSuccess(true);
		successResponse.setErrorCode(errorCd);
		successResponse.setMessage(message);
		return successResponse;
	}

	/**
	 * 失敗用のレスポンスデータを作成する
	 *
	 * @param errorCode エラーコード
	 * @param errorMessage エラーメッセージ
	 * @return jsonデータ(失敗用)
	 */
	private JsonResponseData createErrorResponse(String errorCode, String errorMessage) {
		JsonResponseData errorResponse = new FileTrashBoxListViewForm().new JsonResponseData();
		errorResponse.setSuccess(false);
		errorResponse.setErrorCode(errorCode);
		errorResponse.setMessage(errorMessage);
		return errorResponse;
	}

	/**
	 * ファイル・フォルダを元の場所に戻す
	 *
	 * @param fileConfigurationManagementId 対象のファイル構成管理ID
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/undo", method = RequestMethod.POST)
	public Object undoItem(Long fileConfigurationManagementId) {

		// ファイル区分取得
		FileKubun fileKubun = fileTrashBoxService.getFileKubun(fileConfigurationManagementId);

		try {
			switch (fileKubun) {
			case IS_FILE:
				boolean existFile = fileTrashBoxService.existFile(fileConfigurationManagementId);
				if (existFile) {
					return createErrorResponse(
							StatusCodeEnum.ERROR_RESTORE_FILE_FAILED.getCodeKey(),
							"戻し先に同一フォルダ、ファイルが存在します。\r\nファイルを戻す場合は名前を変更をしてください。");
				} else {
					// ファイルを戻す
					fileTrashBoxService.undoFile(fileConfigurationManagementId);
				}
				break;

			case IS_FOLDER:
				// フォルダ名の重複チェック
				boolean existFolder = fileTrashBoxService.existFile(fileConfigurationManagementId);
				if (existFolder) {
					return createErrorResponse(
							StatusCodeEnum.ERROR_RESTORE_FILE_FAILED.getCodeKey(),
							"戻し先に同一フォルダ、ファイルが存在します。\r\nフォルダを戻す場合はフォルダ名、ファイル名を変更をしてください。");
				} else {
					// フォルダを戻す
					fileTrashBoxService.undoFolder(fileConfigurationManagementId);
				}
				break;

			case IS_ROOT_FOLDER:
				// シンボリックリンク(ルートフォルダ)は削除対象外のため、エラーとする
				throw new RuntimeException();
			}
		} catch (Exception e) {
			return createErrorResponse(
					StatusCodeEnum.ERROR_RESTORE_FILE_FAILED.getCodeKey(),
					FileKubun.IS_FILE.equals(fileKubun) ? "ファイルを元に戻せませんでした。" : "フォルダを元に戻せませんでした。");
		}

		return createSuccessResponse(
				null,
				FileKubun.IS_FILE.equals(fileKubun) ? "ファイルを元に戻しました。" : "フォルダを元に戻しました。");
	}

	/**
	 * ファイル名検索前の単項目チェック
	 *
	 * @param fileTrashBoxListViewForm フォーム情報
	 * @param result チェック結果
	 * @return チェック結果レスポンス情報
	 */
	@ResponseBody
	@RequestMapping(value = "/fileNameSearchPreCheck", method = RequestMethod.POST)
	public Object fileNameSearchPreCheck(@Validated FileTrashBoxListViewForm fileTrashBoxListViewForm, BindingResult result) {
		if (result.hasErrors()) {
			return createErrorResponse(StatusCodeEnum.ERROR_VALIDATION_RESULT_IS_ERROR.getCodeKey(),
					result.getFieldError().getDefaultMessage());
		}
		return createSuccessResponse(null, null);
	}

	/**
	 * ファイル・フォルダの完全削除
	 *
	 * @param fileConfigurationManagementId 削除対象のファイル構成管理ID
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Object deleteItem(Long fileConfigurationManagementId) {

		// ファイル区分取得
		FileKubun fileKubun = fileTrashBoxService.getFileKubun(fileConfigurationManagementId);

		try {
			switch (fileKubun) {
			case IS_FILE:
				fileTrashBoxService.deleteFile(fileConfigurationManagementId);
				break;

			case IS_FOLDER:
				fileTrashBoxService.deleteFolder(fileConfigurationManagementId);
				break;

			case IS_ROOT_FOLDER:
				// シンボリックリンク(ルートフォルダ)は削除対象外のため、エラーとする
				throw new RuntimeException();
			}
		} catch (Exception e) {
			return createErrorResponse(
					StatusCodeEnum.ERROR_FILE_DELETE_FAILED.getCodeKey(),
					FileKubun.IS_FILE.equals(fileKubun) ? "ファイルの削除に失敗しました。" : "フォルダの削除に失敗しました。");
		}
		return createSuccessResponse(
				null,
				FileKubun.IS_FILE.equals(fileKubun) ? "ファイルを削除しました。" : "フォルダを削除しました。");
	}

	/**
	 * 一括完全削除
	 *
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
	public Object deleteItem() {
		try {
			fileTrashBoxService.allFileAndFolderBatchDelete();
		} catch (AppException ae) {
			return createErrorResponse(
					StatusCodeEnum.ERROR_FILE_DELETE_FAILED.getCodeKey(),
					getMessage(ae.getMessageKey()));
		} catch (Exception e) {
			return createErrorResponse(
					StatusCodeEnum.ERROR_FILE_DELETE_FAILED.getCodeKey(),
					"一括完全削除処理に失敗しました。");
		}
		return createSuccessResponse(null, "全てのファイルを削除しました。");
	}

	/**
	 * メンテナンスメニューの表示
	 *
	 * @param FileListNameChangeForm 報酬情報の編集用フォーム
	 * @param msgHolder メッセージの保持クラス
	 * @return 画面情報
	 */
	@ResponseBody
	@RequestMapping(value = "/mentenance", method = RequestMethod.POST)
	public ModelAndView mentenance(Long fileConfigurationManagementId) throws Exception {
		FileTrashBoxMentenanceViewForm fileTrashBoxMentenanceViewForm = new FileTrashBoxMentenanceViewForm();
		fileTrashBoxMentenanceViewForm.setFileConfigurationManagementId(fileConfigurationManagementId);
		fileTrashBoxService.getFileInfoForMaintenance(fileTrashBoxMentenanceViewForm);
		return getMyModelAndView(fileTrashBoxMentenanceViewForm, AJAX_MODAL_PATH_MENTENANCE, VIEW_FORM_NAME);
	}
}
