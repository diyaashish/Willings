package jp.loioz.app.user.fileManagement.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.app.common.service.CommonFileManagementService;
import jp.loioz.app.user.fileManagement.form.FileEditViewLimit;
import jp.loioz.app.user.fileManagement.form.FileListFolderCreateForm;
import jp.loioz.app.user.fileManagement.form.FileListNameChangeForm;
import jp.loioz.app.user.fileManagement.form.FileListUploadForm;
import jp.loioz.app.user.fileManagement.form.FileListUploadForm.JsonResponseData;
import jp.loioz.app.user.fileManagement.form.FileListViewForm;
import jp.loioz.app.user.fileManagement.form.FileMentenanceForm;
import jp.loioz.app.user.fileManagement.form.FileUploadPreCheckForm;
import jp.loioz.app.user.fileManagement.service.FileListService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.FileKubun;
import jp.loioz.common.constant.CommonConstant.FileManagementTransitionSourceKubun;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.StatusCodeEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.PermissionException;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.dto.FileUploadFileInfoDto;
import jp.loioz.entity.TFolderPermissionInfoManagementEntity;

/**
 * ファイル管理画面 Controller
 */
@Controller
@RequestMapping(value = "user/fileManagement/list")
public class FileListController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/fileManagement/fileList";

	/** ファイル一覧エリアと対応するviewのフラグメントパス */
	private static final String MY_VIEW_FILE_LIST_PATH = MY_VIEW_PATH + "::fileListContents";

	/** 閲覧権限変更viewのパス */
	private static final String AJAX_MODAL_PATH_VIEWLIMIT = "user/fileManagement/fileEdit::viewLimitChangeModal";

	/** ３点メニューのviewのパス */
	private static final String AJAX_MODAL_PATH_MENTENANCE = "user/fileManagement/fileEdit::mentenanceModal";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "fileListViewForm";

	/** 閲覧権限変更出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME_VIEWLIMIT = "viewLimitForm";

	/** 閲覧権限変更出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME_MENTENANCE = "mentenanceViewForm";

	/** サービスクラス */
	@Autowired
	private FileListService fileListService;

	/** ファイル管理共通サービスクラス */
	@Autowired
	private CommonFileManagementService commonFileManagementService;

	@Autowired
	private CommonOAuthService commonOAuthService;

	/** フォームクラスの初期化 */
	@ModelAttribute(VIEW_FORM_NAME)
	FileListViewForm setUpForm() {
		return new FileListViewForm();
	}

	/**
	 * 案件軸・顧客軸の他画面からの遷移
	 *
	 * @param fileListViewForm
	 * @return 遷移先の画面
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(FileListViewForm fileListViewForm, MessageHolder msgHolder) {

		this.checkAccessPermission();

		Long transitionCustomerId = fileListViewForm.getTransitionCustomerId();
		Long transitionAnkenId = fileListViewForm.getTransitionAnkenId();

		if (transitionAnkenId == null && transitionCustomerId == null) {
			throw new RuntimeException("案件IDまたは顧客IDが設定されていません。");
		}

		// 軸のチェック
		if (transitionCustomerId != null && transitionAnkenId == null) {
			// 顧客軸

			fileListViewForm.setTransitionAnkenId(null);

			// 顧客フォルダが存在しない場合は作成する
			// ※フォルダが存在しないのは、データ移行や、顧客データのインポートなど、loiozの顧客登録画面以外からt_personが作成されたケース
			fileListService.createCustomerFolderIfNotExist(transitionCustomerId);

			// Formの値を設定
			fileListService.setFileListToFormByCustomerId(fileListViewForm, transitionCustomerId);

			// 遷移元区分を設定
			fileListViewForm
					.setTransitionSourceKubun(FileManagementTransitionSourceKubun.FROM_CUSTOMER_AXIS.getCd());

		} else if (transitionAnkenId != null) {
			// 案件軸

			fileListViewForm.setTransitionCustomerId(null);

			// 案件フォルダが存在しない場合は作成する
			// ※フォルダが存在しないのは、データ移行や、案件データのインポートなど、loiozの案件登録画面（案件登録モーダル）以外からt_ankenが作成されたケース
			fileListService.createAnkenFolderIfNotExist(transitionAnkenId);

			// Formの値を設定
			fileListService.setFileListToFormByAnkenId(fileListViewForm, transitionAnkenId);

			// 遷移元区分を設定
			fileListViewForm
					.setTransitionSourceKubun(FileManagementTransitionSourceKubun.FROM_ANKEN_AXIS.getCd());

		}

		// 契約中のストレージ容量、使用容量、使用率の文言を取得
		fileListViewForm.setDispStrageInfo(commonFileManagementService.getStrageInfo());
		return getMyModelAndView(fileListViewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * ファイル（フォルダ）リストを取得する
	 *
	 * @param transitionAnkenId
	 * @param transitionCustomerId
	 * @param fileConfigurationManagementId
	 * @return
	 */
	@RequestMapping(value = "/getFileList", method = RequestMethod.GET)
	public ModelAndView getFileList(
			@RequestParam(name = "transitionAnkenId") Long transitionAnkenId,
			@RequestParam(name = "transitionCustomerId") Long transitionCustomerId,
			@RequestParam(name = "fileConfigurationManagementId") Long fileConfigurationManagementId) {

		// 画面表示用の情報を取得&Formにセット
		FileListViewForm fileListViewForm = new FileListViewForm();

		if (fileConfigurationManagementId == null) {
			// 案件か顧客のルートフォルダ直下のファイル一覧の取得

			if (transitionAnkenId != null) {
				// 案件軸
				fileListService.setFileListToFormByAnkenId(fileListViewForm, transitionAnkenId);
				// 遷移元区分を設定
				fileListViewForm
						.setTransitionSourceKubun(FileManagementTransitionSourceKubun.FROM_ANKEN_AXIS.getCd());

			} else {
				// 顧客軸
				fileListService.setFileListToFormByCustomerId(fileListViewForm, transitionCustomerId);
				// 遷移元区分を設定
				fileListViewForm
						.setTransitionSourceKubun(FileManagementTransitionSourceKubun.FROM_CUSTOMER_AXIS.getCd());

			}
		} else {
			// 案件か顧客のルートフォルダ配下のサブフォルダ直下のファイル一覧の取得

			// 構成管理IDがある場合（サブフォルダ内のファイル一覧の取得の場合）
			fileListService.setFileListToFormByFileConfigurationManagementId(fileListViewForm, transitionAnkenId, transitionCustomerId, fileConfigurationManagementId);
			// 遷移元区分を設定
			fileListViewForm
					.setTransitionSourceKubun(FileManagementTransitionSourceKubun.FROM_DOUBLE_CLICK_FOLDER_ROW.getCd());
		}

		// 契約中のストレージ容量、使用容量、使用率の文言を取得
		fileListViewForm.setDispStrageInfo(commonFileManagementService.getStrageInfo());

		return getMyModelAndView(fileListViewForm, MY_VIEW_FILE_LIST_PATH, VIEW_FORM_NAME);
	}

	/**
	 * サブフォルダ（またはシンボリックリンク）内への遷移処理(ファイル一覧のフォルダダブルクリック時)
	 *
	 * @param fileListViewForm
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/folder", params = "fileConfigurationManagementId", method = RequestMethod.GET)
	public ModelAndView moveIntoFolder(FileListViewForm fileListViewForm, MessageHolder msgHolder) {

		this.checkAccessPermission();

		Long transitionCustomerId = fileListViewForm.getTransitionCustomerId();
		Long transitionAnkenId = fileListViewForm.getTransitionAnkenId();
		// 画面表示用の情報を取得&Formにセット
		try {
			fileListService.setFileListToFormMoveIntoFolder(fileListViewForm);
		} catch (AppException ex) {
			// エラーメッセージを表示します。
			msgHolder.setErrorMsg(super.getMessage(ex.getMessageKey()));
			return getMyModelAndViewByModalErrorHistoryBack(msgHolder);
		}
		// 軸のチェック
		if (transitionCustomerId != null && transitionAnkenId == null) {
			// 顧客軸
			fileListViewForm.setTransitionAnkenId(null);
			// 遷移元が案件か
			fileListViewForm.setTransitionFromAnken(false);
		} else if (transitionAnkenId != null) {
			// 案件軸
			fileListViewForm.setTransitionCustomerId(null);
			// 遷移元が案件か
			fileListViewForm.setTransitionFromAnken(true);

		}
		// 遷移元区分を設定
		fileListViewForm
				.setTransitionSourceKubun(FileManagementTransitionSourceKubun.FROM_DOUBLE_CLICK_FOLDER_ROW.getCd());
		// 契約中のストレージ容量、使用容量、使用率の文言を取得
		fileListViewForm.setDispStrageInfo(commonFileManagementService.getStrageInfo());

		return getMyModelAndView(fileListViewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 上の階層のフォルダへの遷移処理(フォルダパスクリック時)
	 *
	 * @param fileListViewForm
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/folder", params = "rootFolderRelatedInfoManagementId", method = RequestMethod.GET)
	public ModelAndView backToFolder(FileListViewForm fileListViewForm, MessageHolder msgHolder) {

		this.checkAccessPermission();

		// 画面表示用の情報を取得&Formにセット
		try {
			fileListService.setFileListToFormWhenUpperLevelFolderBack(fileListViewForm);
		} catch (AppException ex) {
			// エラーメッセージを表示します。
			msgHolder.setErrorMsg(super.getMessage(ex.getMessageKey()));
			return getMyModelAndViewByModalErrorHistoryBack(msgHolder);
		}
		// 遷移元区分を設定
		fileListViewForm
				.setTransitionSourceKubun(FileManagementTransitionSourceKubun.FROM_CLICK_FOLDER_PATH.getCd());
		// 契約中のストレージ容量、使用容量、使用率の文言を取得
		fileListViewForm.setDispStrageInfo(commonFileManagementService.getStrageInfo());

		return getMyModelAndView(fileListViewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * ファイルアップロード前のチェック処理
	 *
	 * @param fileListUploadForm jsonで受け取ったアップロードファイル情報
	 * @return チェック結果
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/uploadPrecheck", method = RequestMethod.POST)
	public Object uploadPrecheck(FileUploadPreCheckForm fileUploadPreCheckForm) throws Exception {

		// 1回にアップロードできる最大容量のチェック（複数ファイルの場合）
		if (fileListService.isUploadMaxCapacityExcessForOnce(fileUploadPreCheckForm.getUploadTotalSize())) {
			return createPrecheckErrorResponse(
					StatusCodeEnum.ERROR_UPLOAD_VOLUME_EXCEEDING.getCodeKey(),
					getMessage(MessageEnum.MSG_E00087.getMessageKey(), "500MB"),
					false, null);
		}
		// アップロード容量超過チェック
		if (fileListService.isUploadMaxCapacityExcess(fileUploadPreCheckForm.getUploadTotalSize())) {
			return createPrecheckErrorResponse(
					StatusCodeEnum.ERROR_UPLOAD_VOLUME_EXCEEDING.getCodeKey(),
					getMessage(MessageEnum.MSG_E00033.getMessageKey()),
					false, null);
		}

		// 1ファイル名が256バイト以上かどうかのチェック（Linux環境でtmpファイルを作成するときにファイル名が長すぎるとエラーになるため)
		if (fileListService.isUploadMaxFileNameSize(fileUploadPreCheckForm.getUploadFileNameList())) {
			return createPrecheckErrorResponse(
					StatusCodeEnum.ERROR_UPLOAD_MAX_FILE_NAME_SIZE.getCodeKey(),
					getMessage(MessageEnum.MSG_E00156.getMessageKey()),
					false, null);
		}

		// ■ファイル・フォルダの存在チェック START

		// フォルダ丸ごとのアップロードの場合かを判定
		boolean isFolderUpload = false;
		String folderName = CommonConstant.BLANK;
		for (String fileFullPath : fileUploadPreCheckForm.getUploadFileFullPathList()) {
			if (fileListService.existsSubFolderByUploadFileFullPath(fileFullPath)) {
				isFolderUpload = true;
				folderName = fileListService.getFolderNameByUploadFileFullPath(fileFullPath);
				break;
			}
		}

		// フォルダ丸ごとアップロードの場合
		if (isFolderUpload) {
			// アップロード済みフォルダとの重複チェック
			if (fileListService.existFolderWithSameName(
					fileUploadPreCheckForm.getRootFolderRelatedInfoManagementId(),
					folderName,
					null,
					fileUploadPreCheckForm.getCurrentFolderPath())) {
				// ファイル・フォルダの重複警告レスポンス作成
				return createFolderUploadOnlyPrecheckErrorResponse(
						StatusCodeEnum.WARN_FILE_DUPLICATE.getCodeKey(),
						fileUploadPreCheckForm, folderName, true);
			}

			// アップロード済みファイルとの重複チェック
			if (fileListService.existFileWithSameName(
					fileUploadPreCheckForm.getRootFolderRelatedInfoManagementId(),
					folderName,
					null,
					fileUploadPreCheckForm.getCurrentFolderPath())) {
				// ファイル・フォルダの重複警告レスポンス作成
				return createFolderUploadOnlyPrecheckErrorResponse(
						StatusCodeEnum.WARN_FILE_DUPLICATE.getCodeKey(),
						fileUploadPreCheckForm, folderName, false);
			}
		} else {
			// ファイルのみのアップロードの場合

			// アップロードするファイルの名前と重複したファイル、フォルダーリスト
			List<String> duplicateNameList = new ArrayList<String>();
			// アップロードするファイルの名前と重複したフォルダーリスト
			List<String> folderDuplicateNameList = new ArrayList<String>();

			// アップロード済みファイル、フォルダと重複チェック
			for (String fileName : fileUploadPreCheckForm.getUploadFileNameList()) {
				String fileNameExceptExtension = commonFileManagementService.getFileName(fileName);
				String extension = commonFileManagementService.getExtension(fileName);

				// アップロード済みフォルダとの重複
				if (fileListService.existFolderWithSameName(
						fileUploadPreCheckForm.getRootFolderRelatedInfoManagementId(),
						fileNameExceptExtension,
						extension,
						fileUploadPreCheckForm.getCurrentFolderPath())) {
					duplicateNameList.add(fileName);
					folderDuplicateNameList.add(fileName);
				}

				// アップロード済みファイルとの重複
				if (fileListService.existFileWithSameName(
						fileUploadPreCheckForm.getRootFolderRelatedInfoManagementId(),
						fileNameExceptExtension,
						extension,
						fileUploadPreCheckForm.getCurrentFolderPath())) {
					duplicateNameList.add(fileName);
				}
			}

			// 重複が1件以上ある場合
			if (!CollectionUtils.isEmpty(duplicateNameList)) {
				// ファイル・フォルダの重複警告レスポンス作成
				return createFileUploadOnlyPrecheckErrorResponse(
						StatusCodeEnum.WARN_FILE_DUPLICATE.getCodeKey(),
						fileUploadPreCheckForm, duplicateNameList, folderDuplicateNameList);
			}
		}
		// ■ファイル・フォルダの存在チェック END

		return createSuccessResponse(null, null);
	}

	/**
	 * ファイルアップロード
	 *
	 * @param fileListUploadForm Form情報
	 * @return アップロード結果
	 */
	@ResponseBody
	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
	public Object fileUpload(FileListUploadForm fileListUploadForm) throws Exception {

		// ファイルアップロード前のチェックでエラーの場合は、エラーを返却して終了
		if (!fileListUploadForm.isPreCheckResult() && !fileListUploadForm.isFileUploadContinue()) {
			return createErrorResponse(
					StatusCodeEnum.ERROR_UPLOAD_FAILED.getCodeKey(),
					getMessage(MessageEnum.MSG_E00032.getMessageKey()));
		}
		// アップロードファイルが設定されていない場合は、キャンセルされたものとみなして終了
		if (fileListUploadForm.getUploadFileInfo() == null) {
			return createSuccessResponse(null, null);
		}

		// ファイルのアップロードに失敗した、もしくはやらないものは配列に格納する
		List<String> failerUploadFiles = new ArrayList<>();

		// ファイルのみのアップロードでファイル上書きしないを選択した場合か判定
		if (fileListUploadForm.isFileUploadContinue()) {
			List<FileUploadFileInfoDto> exceptDupFileInfo = new ArrayList<FileUploadFileInfoDto>();
			Boolean existCheck = false;

			for (FileUploadFileInfoDto fileUploadFileInfoDto : fileListUploadForm.getUploadFileInfo()) {

				String fileNameExceptExtension = commonFileManagementService.getFileName(fileUploadFileInfoDto.getUploadFile().getOriginalFilename());
				String extension = commonFileManagementService.getExtension(fileUploadFileInfoDto.getUploadFile().getOriginalFilename());

				// 存在チェック
				existCheck = fileListService.existFile(
						fileListUploadForm.getRootFolderRelatedInfoManagementId(),
						fileNameExceptExtension,
						extension,
						fileListUploadForm.getCurrentFolderPath());

				if (!existCheck) {
					// ファイルが存在しないため、アップロード対象とする
					exceptDupFileInfo.add(fileUploadFileInfoDto);
				} else {
					// アップロードしないファイルはアップロード失敗扱いとする
					failerUploadFiles.add(fileUploadFileInfoDto.getUuid());
				}
			}
			// アップロード対象のみのリストで上書き
			fileListUploadForm.setUploadFileInfo(exceptDupFileInfo);
		}

		// １ファイルごとのトランザクションにするため、コントローラー側でループを回す。
		for (FileUploadFileInfoDto uploadFileInfo : fileListUploadForm.getUploadFileInfo()) {

			try {
				// アップロード処理
				fileListService.executeUpload(uploadFileInfo, fileListUploadForm);

			} catch (Exception e) {
				// エラーが発生した場合、失敗したファイルのUUIDをリストに保持する
				failerUploadFiles.add(uploadFileInfo.getUuid());
			}

		}

		if (!CollectionUtils.isEmpty(failerUploadFiles)) {
			JsonResponseData response = createErrorResponse(
					StatusCodeEnum.ERROR_UPLOAD_FAILED.getCodeKey(),
					getMessage(MessageEnum.MSG_E00032.getMessageKey()));

			// アップロードに失敗したファイルのUUIDリストをレスポンスに追加
			// この値を元に成功失敗をフロント側判定、表示する
			response.setFailerFileUuidList(failerUploadFiles);
			return response;
		}

		// Response返却(成功)
		return createSuccessResponse(null, null);
	}

	/**
	 * 成功用のレスポンスデータを作成する
	 *
	 * @param errorCd エラーコード
	 * @param message メッセージ
	 * @return jsonデータ(成功用)
	 */
	private JsonResponseData createSuccessResponse(String errorCd, String message) {
		JsonResponseData successResponse = new FileListUploadForm().new JsonResponseData();
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
		JsonResponseData errorResponse = new FileListUploadForm().new JsonResponseData();
		errorResponse.setSuccess(false);
		errorResponse.setErrorCode(errorCode);
		errorResponse.setMessage(errorMessage);
		return errorResponse;
	}

	/**
	 * アップロードプリチェックのレスポンスデータを作成する
	 *
	 * @param errorCode エラーコード
	 * @param errorMessage エラーメッセージ
	 * @return jsonデータ(失敗用)
	 */
	private JsonResponseData createPrecheckErrorResponse(String errorCode, String errorMessage, Boolean fileUploadContinue,
			List<String> dupIndexList) {
		JsonResponseData errorResponse = new FileListUploadForm().new JsonResponseData();
		errorResponse.setSuccess(false);
		errorResponse.setErrorCode(errorCode);
		errorResponse.setFileUploadContinue(fileUploadContinue);

		String fileList = CommonConstant.BLANK;
		if (dupIndexList != null) {
			fileList = dupIndexList.stream().collect(Collectors.joining(","));
		}
		errorResponse.setMessage(fileList + errorMessage);
		errorResponse.setDupFileList(dupIndexList);

		return errorResponse;
	}

	/**
	 * フォルダのアップロードプリチェックのレスポンスデータを作成する<br>
	 * 
	 * @param errorCode
	 * @param fileUploadPreCheckForm
	 * @param folderName
	 * @param isDuplicateFolder フォルダと名前が重複したか（ファイルと名前が重複した場合はfalse）
	 * @return
	 */
	private JsonResponseData createFolderUploadOnlyPrecheckErrorResponse(String errorCode, FileUploadPreCheckForm fileUploadPreCheckForm,
			String folderName, boolean isDuplicateFolder) {
		// レスポンスデータ作成
		JsonResponseData errorResponse = new FileListUploadForm().new JsonResponseData();

		// エラーの設定
		errorResponse.setSuccess(false);
		errorResponse.setErrorCode(errorCode);

		// エラーメッセージ
		String errorMessage = "";
		if (isDuplicateFolder) {
			// フォルダとの重複は、上書きの確認メッセージ
			errorMessage = getMessage(MessageEnum.MSG_W00002.getMessageKey(), folderName);
			errorResponse.setCanOverWrite(true);
		} else {
			// ファイルとの重複は、上書きできないメッセージ
			errorMessage = getMessage(MessageEnum.MSG_E00163.getMessageKey(), folderName);
			errorResponse.setCanOverWrite(false);
		}
		errorResponse.setMessage(errorMessage);

		// 上書きで実行する場合の継続フラグ設定
		errorResponse.setFileUploadContinue(false);

		return errorResponse;
	}

	/**
	 * アップロード（ファイルのみ）プリチェックのレスポンスデータを作成する<br>
	 * 
	 * @param errorCode
	 * @param fileUploadPreCheckForm
	 * @param fileDuplicateNameList
	 * @param folderDuplicateNameList
	 * @return
	 */
	private JsonResponseData createFileUploadOnlyPrecheckErrorResponse(String errorCode, FileUploadPreCheckForm fileUploadPreCheckForm,
			List<String> fileDuplicateNameList, List<String> folderDuplicateNameList) {
		// レスポンスデータ作成
		JsonResponseData errorResponse = new FileListUploadForm().new JsonResponseData();

		// エラーの設定
		errorResponse.setSuccess(false);
		errorResponse.setErrorCode(errorCode);

		// ファイル、フォルダーと名前が重複したファイル数
		int fileDuplicateNameCount = CollectionUtils.isEmpty(fileDuplicateNameList) ? 0 : fileDuplicateNameList.size();
		// フォルダーと名前が重複したファイル数
		int folderDuplicateNameCount = CollectionUtils.isEmpty(folderDuplicateNameList) ? 0 : folderDuplicateNameList.size();

		// エラーメッセージ
		String errorMessage = "";
		if (folderDuplicateNameCount > 0) {
			// フォルダ名の重複は、上書きできないメッセージ
			errorMessage = getMessage(MessageEnum.MSG_E00162.getMessageKey(), String.join(",", folderDuplicateNameList));
			errorResponse.setCanOverWrite(false);
		} else {
			// ファイル名の重複は、上書きの確認メッセージ
			errorMessage = getMessage(MessageEnum.MSG_W00002.getMessageKey(), String.join(",", fileDuplicateNameList));
			errorResponse.setCanOverWrite(true);
		}
		errorResponse.setMessage(errorMessage);

		// 上書きで実行する場合の継続フラグ設定
		if (fileDuplicateNameCount != fileDuplicateNameList.size()) {
			// 重複無し or 重複ファイル数よりアップロードファイル数が多ければ継続フラグはtrue（重複していないアップロードできるファイルがあるため）
			errorResponse.setFileUploadContinue(true);
		} else {
			// アップロードファイル数と重複したファイル数が同じ場合は継続フラグはfalse
			errorResponse.setFileUploadContinue(false);
		}
		return errorResponse;
	}

	/**
	 * ダウンロード事前チェックを行う
	 *
	 * @param fileConfigurationManagementId ダウンロード対象のファイル構成管理ID
	 */
	@ResponseBody
	@RequestMapping(value = "/downloadPreCheck", method = RequestMethod.POST)
	public Object downloadPreCheck(
			@RequestParam(name = "fileConfigurationManagementId", required = true) Long fileConfigurationManagementId,
			HttpServletResponse response) throws Exception {

		// ダウンロード対象のファイル区分を取得
		FileKubun fileKubunEnum = fileListService.getFileKubun(fileConfigurationManagementId);

		// ダウンロード処理
		Boolean checkResult = false;
		switch (fileKubunEnum) {
		case IS_FILE:
			checkResult = fileListService.downloadPrecheckForFile(fileConfigurationManagementId, response);
			if (checkResult) {
				return createSuccessResponse(null, null);
			} else {
				return createErrorResponse(StatusCodeEnum.ERROR_DOWNLOAD_CHECK_FAILED.getCodeKey(),
						getMessage(MessageEnum.MSG_E00084.getMessageKey()));
			}

		case IS_FOLDER:
			checkResult = fileListService.downloadPrecheckForFolder(fileConfigurationManagementId, response);
			if (checkResult) {
				return createSuccessResponse(null, null);
			} else {
				return createErrorResponse(StatusCodeEnum.ERROR_DOWNLOAD_CHECK_FAILED.getCodeKey(),
						getMessage(MessageEnum.MSG_E00084.getMessageKey()));
			}

		case IS_ROOT_FOLDER:
			checkResult = fileListService.downloadPrecheckForRootFolder(fileConfigurationManagementId, response);
			if (checkResult) {
				return createSuccessResponse(null, null);
			} else {
				return createErrorResponse(StatusCodeEnum.ERROR_DOWNLOAD_CHECK_FAILED.getCodeKey(),
						getMessage(MessageEnum.MSG_E00084.getMessageKey()));
			}
		}
		return createErrorResponse(StatusCodeEnum.ERROR_DOWNLOAD_CHECK_FAILED.getCodeKey(),
				getMessage(MessageEnum.MSG_E00084.getMessageKey()));

	}

	/**
	 * AmazonS3からファイルをダウンロードする
	 *
	 * @param fileConfigurationManagementId ダウンロード対象のファイル構成管理ID
	 */
	@ResponseBody
	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public void fileDownload(
			@RequestParam(name = "fileConfigurationManagementId", required = true) Long fileConfigurationManagementId,
			HttpServletResponse response) throws Exception {

		super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00034));

		try {
			// ダウンロード対象のファイル区分を取得
			FileKubun fileKubunEnum = fileListService.getFileKubun(fileConfigurationManagementId);

			// ダウンロード処理
			switch (fileKubunEnum) {
			case IS_FILE:
				fileListService.fileDownloadFromAmazonS3(fileConfigurationManagementId, response);
				break;

			case IS_FOLDER:
				fileListService.folderDownloadFromAmazonS3(fileConfigurationManagementId, response);
				break;

			case IS_ROOT_FOLDER:
				fileListService.rootFolderDownloadFromAmazonS3(fileConfigurationManagementId, response);
				break;
			}
		} catch (AppException ex) {
			// 想定するダウンロードエラー
			if (MessageEnum.MSG_E00173.equals(ex.getErrorType())) {
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00173));
			} else {
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00034));
			}
		} catch (Exception ex) {
			// 想定しないダウンロードエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00034));
		}
	}

	/**
	 * フォルダ新規作成
	 *
	 * @param form フォーム情報
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/folderCreate", method = RequestMethod.POST)
	public Object folderCreate(@Validated FileListFolderCreateForm form, BindingResult result) throws Exception {

		// メッセージの定型文
		final String messageFolderString = "「" + form.getFolderName() + "」";

		// バリデーションチェック
		if (result.hasErrors()) {
			return createErrorResponse(StatusCodeEnum.ERROR_VALIDATION_RESULT_IS_ERROR.getCodeKey(),
					result.getFieldError().getDefaultMessage());
		}

		// 同階層に同名のフォルダが存在するかを確認
		if (fileListService.existFile(
				form.getRootFolderRelatedInfoManagementId(),
				form.getFolderName(),
				null,
				form.getCurrentFolderPath())) {
			return createErrorResponse(StatusCodeEnum.ERROR_FOLDER_DUPLICATE.getCodeKey(),
					messageFolderString + "は既に存在します。\r\n別のフォルダ名を入力してください。");
		}

		try {
			fileListService.createSubFolder(form);
		} catch (Exception e) {
			return createErrorResponse(StatusCodeEnum.ERROR_FOLDER_CREATE_FAILED.getCodeKey(),
					getMessage(MessageEnum.MSG_E00035.getMessageKey(), messageFolderString));
		}

		return createSuccessResponse(null, messageFolderString + "を作成しました。");
	}

	/**
	 * 移動ボタン押下時の移動先一覧情報の取得
	 *
	 * @param form フォーム情報
	 * @returna 移動先のフォルダパス一覧情報
	 */
	@ResponseBody
	@RequestMapping(value = "/getFolderList", method = RequestMethod.GET)
	public Object getFolderList(Long transitionAnkenId, Long transitionCustomerId, Long rootFolderRelatedInfoManagementId) {
		if (transitionAnkenId == null && transitionCustomerId == null) {
			throw new RuntimeException("案件IDまたは顧客IDが設定されていません。");
		}
		return fileListService.getFolderList(transitionAnkenId, transitionCustomerId, rootFolderRelatedInfoManagementId);
	}

	/**
	 * 移動先選択モーダル画面の該当フォルダのサブフォルダ一覧の取得
	 *
	 * @param fileConfigurationManagementId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/openFolder", method = RequestMethod.GET)
	public Object openFolder(Long fileConfigurationManagementId) {
		return fileListService.getFolderListUnderTargetFolder(fileConfigurationManagementId);
	}

	/**
	 * ファイル・フォルダの移動
	 *
	 * @param moveToFileConfigurationManagementId 移動先フォルダのファイル構成管理ID
	 * @param targetFileConfigurationManagementId 移動対象となるファイルのファイル構成管理ID
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/moveToFolder", method = RequestMethod.POST)
	public Object moveToFolder(Long moveToFileConfigurationManagementId, Long targetFileConfigurationManagementId) {

		try {
			// 移動先のファイル区分を取得
			FileKubun fileKubunEnum = fileListService.getFileKubun(moveToFileConfigurationManagementId);

			switch (fileKubunEnum) {
			case IS_FOLDER:
				fileListService.moveToSubFolder(moveToFileConfigurationManagementId, targetFileConfigurationManagementId);
				break;
			case IS_ROOT_FOLDER:
				fileListService.moveToRootFolder(moveToFileConfigurationManagementId, targetFileConfigurationManagementId);
				break;
			case IS_FILE:
				// 移動先がファイルは想定外の動きであるため、エラーとする
				throw new RuntimeException();
			}
		} catch (Exception e) {
			return createErrorResponse(StatusCodeEnum.ERROR_MOVE_TO_FOLDER_FAILED.getCodeKey(),
					getMessage(MessageEnum.MSG_E00037.getMessageKey(), "フォルダへ"));
		}

		return createSuccessResponse(null, "選択したフォルダに移動しました");
	}

	/**
	 * 該当ファイルのゴミ箱移動
	 *
	 * @param fileConfigurationManagementId 移動対象のファイル構成管理ID
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/moveTrashBox", method = RequestMethod.POST)
	public Object moveItemToTrashBox(Long fileConfigurationManagementId, Long rootFolderRelatedInfoManagementId) throws Exception {

		try {
			// ファイル区分取得
			FileKubun fileKubunEnum = fileListService.getFileKubun(fileConfigurationManagementId);

			// 該当ファイルのゴミ箱フラグを有効化
			switch (fileKubunEnum) {
			case IS_FILE:
				fileListService.activateTargetFileTrashBoxFlg(fileConfigurationManagementId);
				break;
			case IS_FOLDER:
				fileListService.activateTargetFolderTrashBoxFlg(fileConfigurationManagementId,
						rootFolderRelatedInfoManagementId);
				break;
			case IS_ROOT_FOLDER:
				// シンボリックリンク(ルートフォルダ)は削除対象外のため、エラーとする
				throw new RuntimeException();
			}

		} catch (Exception e) {
			return createErrorResponse(
					StatusCodeEnum.ERROR_MOVE_TRASH_BOX_FAILED.getCodeKey(),
					"ゴミ箱への移動に失敗しました。");
		}

		return createSuccessResponse(null, "ゴミ箱に移動しました。");
	}

	/**
	 * ファイル・フォルダの名称変更処理
	 *
	 * @param form 名称変更フォーム情報
	 * @return 処理結果
	 */
	@ResponseBody
	@RequestMapping(value = "/nameChange", method = RequestMethod.POST)
	public Object nameChange(@Validated FileListNameChangeForm form, BindingResult result) {

		if (result.hasErrors()) {
			// バリデーションエラーが存在した場合 -> エラーメッセージを表示(複数のバリデーションに引っかかる場合は、いずれか一つのみ表示)
			List<FieldError> fieldErrors = result.getFieldErrors("renameInputText");
			String errorMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).findFirst().orElse(getMessage(MessageEnum.MSG_E00001));
			return createErrorResponse(StatusCodeEnum.ERROR_NAME_CHANGE_FAILED.getCodeKey(), errorMessage);
		}

		try {
			// ファイル区分取得
			FileKubun fileKubunEnum = fileListService.getFileKubun(form.getFileConfigurationManagementId());

			if (!fileListService.equalsFileName(form.getFileConfigurationManagementId(), form.getRenameInputText(), form.getFileExtension())) {
				// DB値の名称変更がある場合は、更新処理時にファイル・フォルダ名重複チェックをする

				// ファイル・フォルダ名の重複チェック
				if (fileListService.existFile(form.getRootFolderRelatedInfoManagementId(),
						form.getRenameInputText(), form.getFileExtension(), form.getCurrentFolderPath())) {
					return createErrorResponse(
							StatusCodeEnum.WARN_FILE_DUPLICATE.getCodeKey(),
							getMessage(MessageEnum.MSG_E00038.getMessageKey(), "名称"));
				}
			}

			switch (fileKubunEnum) {
			case IS_FILE:
				fileListService.fileNameChange(form);
				break;
			case IS_FOLDER:
				fileListService.folderNameChange(form);
				break;
			case IS_ROOT_FOLDER:
				// シンボリックリンク(ルートフォルダ)は変更対象外のため、エラーとする
				throw new RuntimeException();
			}
		} catch (AppException ex) {
			// エラーメッセージを表示します。
			return createErrorResponse(StatusCodeEnum.ERROR_NAME_CHANGE_FAILED.getCodeKey(), super.getMessage(ex.getMessageKey()));
		}

		return createSuccessResponse(null, getMessage(MessageEnum.MSG_I00023, "名称"));
	}

	/**
	 * ファイル名クイック検索単項目チェック用
	 *
	 * @param form チェック対象のフォーム情報
	 * @param result チェック結果
	 * @return チェック結果のレスポンス情報
	 */
	@ResponseBody
	@RequestMapping(value = "/fileNameSearchPreCheck", method = RequestMethod.POST)
	public Object fileNameSearchPreCheck(@Validated FileListViewForm form, BindingResult result) {
		if (result.hasErrors()) {
			return createErrorResponse(StatusCodeEnum.ERROR_VALIDATION_RESULT_IS_ERROR.getCodeKey(),
					result.getFieldError().getDefaultMessage());
		}
		return createSuccessResponse(null, null);
	}

	/**
	 * ファイル名検索
	 *
	 * @param fileListViewForm フォーム情報
	 * @return 検索結果
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView fileNameSearch(@Validated FileListViewForm fileListViewForm, BindingResult result) {

		// ファイル名検索を実施
		fileListService.setFileListToFormWhenFileNameSearch(fileListViewForm);

		// 遷移元区分を設定
		fileListViewForm
				.setTransitionSourceKubun(FileManagementTransitionSourceKubun.FROM_FILE_NAME_QUICK_SEARCH.getCd());
		// 契約中のストレージ容量、使用容量、使用率の文言を取得
		fileListViewForm.setDispStrageInfo(commonFileManagementService.getStrageInfo());
		fileListViewForm.setSearch(true);
		return getMyModelAndView(fileListViewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * ファイルプレビュー
	 *
	 * @param fileConfigurationManagementId プレビュー対象のファイル構成管理ID
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/preview", method = RequestMethod.POST)
	public void filePreview(Long fileConfigurationManagementId, HttpServletResponse response) throws Exception {

		// プレビュー対象のファイル区分を取得
		FileKubun fileKubun = fileListService.getFileKubun(fileConfigurationManagementId);

		switch (fileKubun) {
		case IS_FILE:
			fileListService.filePreview(fileConfigurationManagementId, response);
			break;
		case IS_FOLDER:
			// プレビュー機能はファイルのみ
			throw new RuntimeException();
		case IS_ROOT_FOLDER:
			// プレビュー機能はファイルのみ
			throw new RuntimeException();
		}
	}

	/**
	 * 権限変更モーダル表示
	 *
	 * @param fileConfigurationManagementId プレビュー対象のファイル構成管理ID
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/viewLimitChangeEdit", method = RequestMethod.POST)
	public ModelAndView viewLimitChangeEdit(FileEditViewLimit inputViewLimitForm, MessageHolder msgHolder) throws Exception {

		FileEditViewLimit viewLimitForm = new FileEditViewLimit();
		try {
			// プレビュー対象のファイル区分を取得
			TFolderPermissionInfoManagementEntity entity = fileListService
					.getViewLimit(inputViewLimitForm.getFileConfigurationManagementId());
			viewLimitForm.setFolderPermissionInfoManagementId(entity.getFolderPermissionInfoManagementId());
			viewLimitForm.setViewLimit(entity.getViewLimit());

		} catch (AppException ex) {
			// エラーメッセージを表示します。
			msgHolder.setErrorMsg(super.getMessage(ex.getMessageKey()));
			return getMyModelAndViewByModalError(msgHolder);

		}
		return getMyModelAndView(viewLimitForm, AJAX_MODAL_PATH_VIEWLIMIT, VIEW_FORM_NAME_VIEWLIMIT);
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
	public ModelAndView mentenance(FileListNameChangeForm form, MessageHolder msgHolder) throws Exception {

		FileMentenanceForm mentenanceViewForm = fileListService
				.getFileInfoForMentenceMenu(form.getFileConfigurationManagementId());
		return getMyModelAndView(mentenanceViewForm, AJAX_MODAL_PATH_MENTENANCE, VIEW_FORM_NAME_MENTENANCE);
	}

	/**
	 * フォルダの権限変更を行います。
	 *
	 * @param ankenMeisaiEditForm 報酬情報の編集用フォーム
	 * @param result バリデーション結果
	 * @param msgHolder メッセージの保持クラス
	 * @return 画面情報
	 */
	@ResponseBody
	@RequestMapping(value = "/updateViewLimit", method = RequestMethod.POST)
	public Map<String, Object> updateViewLimit(FileEditViewLimit inputViewLimitForm, BindingResult result) {

		Map<String, Object> response = new HashMap<>();

		// バリデーションエラーがある場合
		if (result.hasErrors()) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		try {
			// 更新処理
			fileListService.updateViewLimit(inputViewLimitForm);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00023, "閲覧権限"));
			return response;

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType()));
			return response;

		}

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
