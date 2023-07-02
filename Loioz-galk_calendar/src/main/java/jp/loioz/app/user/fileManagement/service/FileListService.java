package jp.loioz.app.user.fileManagement.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.net.URLCodec;
import org.seasar.doma.jdbc.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3Object;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.common.service.CommonFileManagementService;
import jp.loioz.app.user.fileManagement.form.FileEditViewLimit;
import jp.loioz.app.user.fileManagement.form.FileListFolderCreateForm;
import jp.loioz.app.user.fileManagement.form.FileListNameChangeForm;
import jp.loioz.app.user.fileManagement.form.FileListUploadForm;
import jp.loioz.app.user.fileManagement.form.FileListViewForm;
import jp.loioz.app.user.fileManagement.form.FileMentenanceForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AxisKubun;
import jp.loioz.common.constant.CommonConstant.FileKubun;
import jp.loioz.common.constant.CommonConstant.FileManagementDisplayPriorityId;
import jp.loioz.common.constant.CommonConstant.FileType;
import jp.loioz.common.constant.CommonConstant.TrashBoxFlg;
import jp.loioz.common.constant.CommonConstant.ViewLimit;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.FileStorageConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.file.FileStorageService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.LoiozIOUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MTenantMgtDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TFileConfigurationManagementDao;
import jp.loioz.dao.TFileDetailInfoManagementDao;
import jp.loioz.dao.TFolderPermissionInfoManagementDao;
import jp.loioz.dao.TRootFolderRelatedInfoManagementDao;
import jp.loioz.domain.UriService;
import jp.loioz.domain.file.Directory;
import jp.loioz.dto.AnkenTantoDto;
import jp.loioz.dto.FileDetailDto;
import jp.loioz.dto.FileDownloadFileInfoDto;
import jp.loioz.dto.FileGeneralDto;
import jp.loioz.dto.FileUploadFileInfoDto;
import jp.loioz.dto.FolderNameListDto;
import jp.loioz.dto.RootFolderInfoDto;
import jp.loioz.entity.MTenantMgtEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TFileConfigurationManagementEntity;
import jp.loioz.entity.TFileDetailInfoManagementEntity;
import jp.loioz.entity.TFolderPermissionInfoManagementEntity;

/**
 * ファイル管理画面Service
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FileListService extends DefaultService {

	/**
	 * ファイル構成管理Dao
	 */
	@Autowired
	private TFileConfigurationManagementDao tFileConfigurationManagementDao;

	/**
	 * ファイル詳細情報管理Dao
	 */
	@Autowired
	private TFileDetailInfoManagementDao tFileDetailInfoManagementDao;

	/**
	 * フォルダ権限情報管理Dao
	 */
	@Autowired
	private TFolderPermissionInfoManagementDao tFolderPermissionInfoManagementDao;

	/**
	 * ルートフォルダ関連情報管理Dao
	 */
	@Autowired
	private TRootFolderRelatedInfoManagementDao tRootFolderRelatedInfoManagementDao;

	/**
	 * テナント管理用のDao
	 */
	@Autowired
	private MTenantMgtDao mTenantMgtDao;

	/**
	 * 案件情報用のDaoクラス
	 */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** URIサービスクラス */
	@Autowired
	private UriService uriService;

	/**
	 * ファイルアップロード共通部品
	 */
	@Autowired
	private FileStorageService fileStorageService;

	/**
	 * 案件共通部品
	 */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/**
	 * ファイル管理共通サービス
	 */
	@Autowired
	private CommonFileManagementService commonFileManagementService;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	/**
	 * ルートフォルダ直下判定パス
	 */
	private static final String ROOT_FOLDER_DIRECTLY_UNDER_PATH = "/";

	/**
	 * S3のアップロード先ディレクトリパス
	 */
	private static final String FILE_MANAGEMENT_S3_DIR_PATH = FileStorageConstant.S3FileManagementDir.TE_DO_FILE_MANAGEMENT.getPath();

	/** zip圧縮時のファイル読込みバイト数 */
	private static byte[] buf = new byte[1024];

	/**
	 * 案件フォルダ作成（まだフォルダが存在しない場合のみ作成処理を行う）
	 * 
	 * @param ankenId
	 */
	public void createAnkenFolderIfNotExist(Long ankenId) {
		commonFileManagementService.createAnkenFolderIfNotExist(ankenId);
	}

	/**
	 * 顧客フォルダ作成（まだフォルダが存在しない場合のみ作成処理を行う）
	 * 
	 * @param customerId
	 */
	public void createCustomerFolderIfNotExist(Long customerId) {
		commonFileManagementService.createCustomerFolderIfNotExist(customerId);
	}

	/**
	 * ファイル一覧情報をFormにセットする（案件軸）
	 *
	 * @param fileListViewForm 画面表示に使用するForm情報
	 * @param ankenId 案件ID
	 */
	public void setFileListToFormByAnkenId(FileListViewForm fileListViewForm, Long ankenId) {

		// 案件軸
		AxisKubun axisKubun = AxisKubun.ANKEN;
		// 遷移元が案件か
		fileListViewForm.setTransitionFromAnken(true);
		// 案件情報の取得
		TAnkenEntity tAnkenEntity = tAnkenDao.selectById(ankenId);
		// 分野情報の設定
		fileListViewForm.setBunya(tAnkenEntity.getBunyaId());

		// 案件IDからルートフォルダ情報を取得する
		RootFolderInfoDto rootFolderInfo = tFileDetailInfoManagementDao.selectRootFolderInfo(ankenId, axisKubun);
		if (rootFolderInfo == null) {
			throw new DataNotFoundException("案件のルートフォルダが存在しません。[ankenId=" + ankenId + "]");
		}
		fileListViewForm.setRootFolderInfo(rootFolderInfo);
		fileListViewForm.setCurrentFileConfigurationManagementId(rootFolderInfo.getFileConfigurationManagementId());

		// 案件配下のファイル一覧を取得
		List<FileDetailDto> fileList = tFileDetailInfoManagementDao.selectFileInfoExcludedSymbolicLinkInfo(ankenId, axisKubun);

		// 閲覧権限制御、閲覧制御ボタン表示制御
		List<FileDetailDto> addLimitFileList = new ArrayList<FileDetailDto>();
		addLimitFileList = checkViewLimitByAnkenId(fileList, ankenId);
		fileListViewForm.setFileDetailDtoList(addLimitFileList);

	}

	/**
	 * ファイル一覧情報をFormにセットする（顧客軸）
	 *
	 * @param fileListViewForm 画面表示に使用するForm情報
	 * @param customerId 顧客ID
	 */
	public void setFileListToFormByCustomerId(FileListViewForm fileListViewForm, Long customerId) {

		// 顧客軸
		AxisKubun axisKubun = AxisKubun.KOKYAKU;
		// 遷移元が案件か
		fileListViewForm.setTransitionFromAnken(false);
		// 顧客IDからルートフォルダ情報を取得する
		RootFolderInfoDto rootFolderInfo = tFileDetailInfoManagementDao.selectRootFolderInfo(customerId, axisKubun);
		if (rootFolderInfo == null) {
			throw new DataNotFoundException("顧客のルートフォルダが存在しません。[customerId=" + customerId + "]");
		}
		fileListViewForm.setRootFolderInfo(rootFolderInfo);
		fileListViewForm.setCurrentFileConfigurationManagementId(rootFolderInfo.getFileConfigurationManagementId());

		// 顧客配下のファイル一覧を取得
		List<FileDetailDto> fileList = tFileDetailInfoManagementDao.selectFileInfoExcludedSymbolicLinkInfo(customerId, axisKubun);

		fileListViewForm.setFileDetailDtoList(fileList);
	}

	/**
	 * ファイル構成管理IDを使って、サブフォルダ内のファイル一覧情報をFormにセットする。
	 * 
	 * <pre>
	 * サブフォルダではなく、
	 * ルートフォルダ（顧客、案件のルートのフォルダ）のファイル一覧を取得する場合は、
	 * 下記のメソッドを利用する。
	 * 
	 * 顧客のルートフォルダ：{@link #setFileListToFormByCustomerId(FileListViewForm, Long)}
	 * 案件のルートフォルダ：{@link #setFileListToFormByAnkenId(FileListViewForm, Long)}
	 * </pre>
	 * 
	 * @param fileListViewForm
	 * @param ankenId customerIdがnullの場合は必須
	 * @param customerId ankenIdがnullの場合は必須
	 * @param fileConfigurationManagementId サブフォルダの構成管理ID（必須）
	 */
	public void setFileListToFormByFileConfigurationManagementId(FileListViewForm fileListViewForm, Long ankenId, Long customerId, Long fileConfigurationManagementId) {

		// ルートフォルダ情報
		RootFolderInfoDto rootFolderInfo = null;

		if (ankenId != null) {
			// 案件軸のフォルダの場合

			// 案件情報の取得
			TAnkenEntity tAnkenEntity = tAnkenDao.selectById(ankenId);
			// 分野情報の設定
			fileListViewForm.setBunya(tAnkenEntity.getBunyaId());

			rootFolderInfo = tFileDetailInfoManagementDao.selectRootFolderInfo(ankenId, AxisKubun.ANKEN);
			if (rootFolderInfo == null) {
				throw new DataNotFoundException("案件のルートフォルダが存在しません。[ankenId=" + ankenId + "]");
			}
		} else if (customerId != null) {
			// 顧客軸のフォルダの場合

			rootFolderInfo = tFileDetailInfoManagementDao.selectRootFolderInfo(customerId, AxisKubun.KOKYAKU);
			if (rootFolderInfo == null) {
				throw new DataNotFoundException("顧客のルートフォルダが存在しません。[customerId=" + customerId + "]");
			}
		} else {
			throw new IllegalArgumentException("案件IDか顧客IDのどちらかは必須です。");
		}
		fileListViewForm.setRootFolderInfo(rootFolderInfo);

		if (fileConfigurationManagementId == null) {
			throw new IllegalArgumentException("ファイル構成管理IDは必須です。");
		}

		// 引数の構成管理IDがフォルダのものかチェック
		FileKubun fileKubun = getFileKubun(fileConfigurationManagementId);
		if (Objects.equals(FileKubun.IS_FILE, fileKubun)) {
			throw new RuntimeException("フォルダではなくファイルです。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}
		fileListViewForm.setCurrentFileConfigurationManagementId(fileConfigurationManagementId);

		// ファイル一覧取得
		List<FileDetailDto> fileList = tFileDetailInfoManagementDao
				.getFolderUnderFileListWithoutsymboliclink(fileConfigurationManagementId);

		// 閲覧権限制御、閲覧制御ボタン表示制御
		if (ankenId != null) {
			List<FileDetailDto> addLimitFileList = new ArrayList<FileDetailDto>();
			addLimitFileList = checkViewLimitByAnkenId(fileList, rootFolderInfo.getAnkenId().asLong());
			fileListViewForm.setFileDetailDtoList(addLimitFileList);
		} else {
			fileListViewForm.setFileDetailDtoList(fileList);
		}
	}

	/**
	 * シンボリックリンク内でのルートフォルダへの遷移処理
	 *
	 * @param fileListViewForm Form情報
	 * @throws AppException
	 */
	public void setFileListToFormWhenMovingToTheRootFolderInSymbolicLink(FileListViewForm fileListViewForm) throws AppException {

		// ファイル構成管理IDをFormにセット
		Long fileConfigurationManagementId = tFileConfigurationManagementDao
				.selectFileConfigurationManagementIdOfRootFolder(
						fileListViewForm.getRootFolderRelatedInfoManagementId());
		fileListViewForm.setFileConfigurationManagementId(fileConfigurationManagementId);

		// シンボリックリンクダブルクリック時と同じ処理を行う
		try {
			setFileListToFormMoveIntoFolder(fileListViewForm);
		} catch (AppException ex) {
			// 閲覧権限の不正アクセス検知
			throw new AppException(MessageEnum.MSG_E00077, ex);
		}
	}

	/**
	 * ファイル一覧情報をFormにセットする（サブフォルダやシンボリックリンクへの遷移時）
	 *
	 * @param fileListViewForm Form情報
	 * @throws AppException
	 */
	public void setFileListToFormMoveIntoFolder(FileListViewForm fileListViewForm) throws AppException {

		if (fileListViewForm.getTransitionAnkenId() != null) {
			// 案件情報の取得
			TAnkenEntity tAnkenEntity = tAnkenDao.selectById(fileListViewForm.getTransitionAnkenId());
			// 分野情報の設定
			fileListViewForm.setBunya(tAnkenEntity.getBunyaId());
		}
		// ファイル構成管理ID
		Long fileConfigurationManagementId = fileListViewForm.getFileConfigurationManagementId();

		// 遷移先の現在階層判別用に設定しておく
		fileListViewForm.setCurrentFileConfigurationManagementId(fileConfigurationManagementId);

		// 遷移先のフォルダ区分を判定
		FileKubun fileKubun = getFileKubun(fileConfigurationManagementId);

		List<FileDetailDto> fileList = new ArrayList<FileDetailDto>();
		if (Objects.equals(FileKubun.IS_FILE, fileKubun)) {
			throw new RuntimeException("遷移先がフォルダではなくファイルです。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}

		// ■画面上部のルートフォルダパス部分のパーツ作成
		// ファイル構成管理IDからルートフォルダ情報取得
		RootFolderInfoDto rootFolderInfo = tFileDetailInfoManagementDao
				.selectRootFolderInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (rootFolderInfo == null) {
			throw new DataNotFoundException(
					"ルートフォルダ情報の取得に失敗しました。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}
		fileListViewForm.setRootFolderInfo(rootFolderInfo);

		// ■画面上部のフォルダパス部分のパーツ作成
		// 遷移先のフォルダパスとフォルダ名取得
		TFileDetailInfoManagementEntity TransitionTargetFolderPathAndFolderNm = tFileDetailInfoManagementDao
				.getTransitionTargetFolderPathAndFolderName(fileConfigurationManagementId);
		if (TransitionTargetFolderPathAndFolderNm == null) {
			throw new DataNotFoundException("遷移先のフォルダパスの取得に失敗しました。[fileConfigurationManagementId="
					+ fileConfigurationManagementId + "]");
		}
		// 遷移先がサブフォルダ
		if (FileKubun.IS_FOLDER.equals(fileKubun)
				&& !StringUtils.isEmpty(TransitionTargetFolderPathAndFolderNm.getFileName())) {
			// '/'で区切ってフォルダパスボタンのパーツ元情報を設定する
			List<String> subFolderNameList = StringUtils.toArrayNoTrim(TransitionTargetFolderPathAndFolderNm.getFolderPath(),
					CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER);
			// ルートフォルダ直下以外のサブフォルダの場合
			if (subFolderNameList != null && subFolderNameList.size() > 1) {
				// 先頭の空要素を取り除いてFormにセット
				subFolderNameList = subFolderNameList
						.stream()
						.skip(1)
						.collect(Collectors.toList());
				fileListViewForm.setSubFolderNameList(subFolderNameList);
			}
			// サブフォルダ名(遷移先)
			fileListViewForm.setCurrentLocationSubFolderName(TransitionTargetFolderPathAndFolderNm.getFileName());

			// ルートフォルダのトップが案件フォルダの際に直接URLアクセスでの不正アクセスをチェックする。
			if (rootFolderInfo.getAnkenId() != null) {
				// 不正アクセスチェック
				Boolean irregularityAccess = CheckIrregularityAccess(TransitionTargetFolderPathAndFolderNm.getFileName(), subFolderNameList,
						rootFolderInfo.getAnkenId().asLong());
				if (irregularityAccess) {
					// 閲覧権限の不正アクセス検知
					throw new AppException(MessageEnum.MSG_E00077, null);
				}
			}
			// ■ファイル情報取得
			// ファイル一覧取得
			fileList = tFileDetailInfoManagementDao
					.getFolderUnderFileListWithoutsymboliclink(fileConfigurationManagementId);

		} else if (FileKubun.IS_ROOT_FOLDER.getCd().equals(fileKubun.getCd())) {
			// ルートフォルダ直下の場合は、シンボリックリンクの情報も含めて検索を行わない
			AxisKubun axisKubun = null;
			Long id = null;
			if (fileListViewForm.getRootFolderInfo().getAnkenId() != null) {
				axisKubun = AxisKubun.ANKEN;
				id = fileListViewForm.getRootFolderInfo().getAnkenId().asLong();
			} else {
				axisKubun = AxisKubun.KOKYAKU;
				id = fileListViewForm.getRootFolderInfo().getCustomerId().asLong();
			}

			// シンボリックリンクの情報は取得しない
			fileList = tFileDetailInfoManagementDao.selectFileInfoIncludedSymbolicLinkInfo(id, axisKubun);
		}

		// 閲覧権限制御、閲覧制御ボタン表示制御
		if (rootFolderInfo.getAnkenId() != null) {
			List<FileDetailDto> addLimitFileList = new ArrayList<FileDetailDto>();
			addLimitFileList = checkViewLimitByAnkenId(fileList, rootFolderInfo.getAnkenId().asLong());
			fileListViewForm.setFileDetailDtoList(addLimitFileList);
		} else {
			fileListViewForm.setFileDetailDtoList(fileList);
		}
	}

	/**
	 * ファイル一覧情報をFormにセットする（フォルダパスクリックによる上階層へのフォルダ遷移）
	 *
	 * @param fileListViewForm Form情報
	 * @throws AppException
	 */
	public void setFileListToFormWhenUpperLevelFolderBack(FileListViewForm fileListViewForm) throws AppException {

		// ルートフォルダ関連情報管理ID
		Long rootFolderRelatedInfoManagementId = fileListViewForm.getRootFolderRelatedInfoManagementId();

		// ■画面上部のルートフォルダパス部分のパーツ作成
		// ルートフォルダ関連情報管理IDからルートフォルダ情報の取得
		RootFolderInfoDto rootFolderInfo = getRootFolderInfoByRootFolderRelatedInfoManagementId(
				rootFolderRelatedInfoManagementId);
		fileListViewForm.setRootFolderInfo(rootFolderInfo);

		// ■画面上部のフォルダパス部分のパーツ作成
		// 遷移先のフォルダパス
		String folderPath = fileListViewForm.getFolderPath();
		// 各フォルダ名毎に区切る
		List<String> subFolderNameList = StringUtils.toArrayNoTrim(folderPath, CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER);

		// フォルダパスを区切った結果、ルートフォルダ扱いのパスになってしまった場合はシステムエラーにする
		if (subFolderNameList == null || subFolderNameList.size() < 2) {
			throw new RuntimeException(
					"フォルダパスの分解に失敗しました。[folderPath=" + folderPath + "]");
		}

		// 遷移先のフォルダ名を設定(遷移先においての現在地点)
		String transitionTargetFolderName = subFolderNameList.get(subFolderNameList.size() - 1);
		fileListViewForm.setCurrentLocationSubFolderName(transitionTargetFolderName);

		// 先頭の空要素を除いたリスト(フォルダパスの選択可能部分)をFormにセットする
		List<String> transitionTargetParentsSubFolderNameList = subFolderNameList.stream()
				.limit(subFolderNameList.size() - 1) // 遷移先のフォルダ要素を除く
				.skip(1) // 空要素を除く
				.collect(Collectors.toList());
		fileListViewForm.setSubFolderNameList(transitionTargetParentsSubFolderNameList);

		// 遷移先フォルダの親パス
		String parentFolderPath = CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER;
		// 遷移先の一つ上の階層フォルダがルート直下ではない場合、一つ上階層までのフォルダ名で親パスを組み立てる
		if (transitionTargetParentsSubFolderNameList.size() > 0) {
			parentFolderPath += String.join(CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER, transitionTargetParentsSubFolderNameList)
					+ CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER;
		}

		// フォルダ名とフォルダパスを用いて、遷移先のファイル構成管理IDを取得する
		Long fileConfigurationManagementId = tFileConfigurationManagementDao
				.getFileConfigurationManagementIdMatchInFolderNameInSomeFolderHierarchy(
						transitionTargetFolderName, null, parentFolderPath, rootFolderRelatedInfoManagementId,
						FileKubun.IS_FOLDER.getCd());
		if (fileConfigurationManagementId == null) {
			throw new DataNotFoundException("遷移先のファイル構成管理IDの取得に失敗しました。[rootFolderRelatedInfoManagementId="
					+ rootFolderRelatedInfoManagementId + "] [folderPath=" + folderPath + "]");
		}
		fileListViewForm.setCurrentFileConfigurationManagementId(fileConfigurationManagementId);

		// ルートフォルダのトップが案件フォルダの際に直接URLアクセスでの不正アクセスをチェックする。
		if (rootFolderInfo.getAnkenId() != null) {
			// 不正アクセスチェック
			Boolean irregularityAccess = CheckIrregularityAccess(transitionTargetFolderName, transitionTargetParentsSubFolderNameList,
					rootFolderInfo.getAnkenId().asLong());
			if (irregularityAccess) {
				// 閲覧権限の不正アクセス検知
				throw new AppException(MessageEnum.MSG_E00077, null);
			}
		}
		// ■ファイル情報取得
		// 遷移先のフォルダパスから画面に表示するファイル一覧を取得
		List<FileDetailDto> fileList = tFileDetailInfoManagementDao
				.getFileInfoByFolderPathAndRootFolderRelatedInfoManagementId(folderPath,
						rootFolderRelatedInfoManagementId);

		// 閲覧権限制御、閲覧制御ボタン表示制御
		if (rootFolderInfo.getAnkenId() != null) {
			List<FileDetailDto> addLimitFileList = new ArrayList<FileDetailDto>();
			addLimitFileList = checkViewLimitByAnkenId(fileList, rootFolderInfo.getAnkenId().asLong());
			fileListViewForm.setFileDetailDtoList(addLimitFileList);
		} else {
			fileListViewForm.setFileDetailDtoList(fileList);
		}
	}

	/**
	 * ファイル名検索によるファイル一覧情報をFormにセットする
	 *
	 * @param fileListViewForm フォーム情報
	 */
	public void setFileListToFormWhenFileNameSearch(FileListViewForm fileListViewForm) {

		if (fileListViewForm.getTransitionAnkenId() != null) {
			// 遷移元が案件か
			fileListViewForm.setTransitionFromAnken(true);
			// 案件情報の取得
			TAnkenEntity tAnkenEntity = tAnkenDao.selectById(fileListViewForm.getTransitionAnkenId());
			// 分野情報の設定
			fileListViewForm.setBunya(tAnkenEntity.getBunyaId());
		} else {
			// 遷移元が案件か
			fileListViewForm.setTransitionFromAnken(false);
		}
		// ルートフォルダ関連情報管理ID
		Long rootFolderRelatedInfoManagementId = fileListViewForm.getRootFolderRelatedInfoManagementId();

		// ■画面上部のルートフォルダパス部分のパーツ作成
		RootFolderInfoDto rootFolderInfo = getRootFolderInfoByRootFolderRelatedInfoManagementId(
				rootFolderRelatedInfoManagementId);
		fileListViewForm.setRootFolderInfo(rootFolderInfo);

		// ■画面上部のフォルダパス部分のパーツ作成(ルートフォルダ直下以外の場合のみ作成)
		if (!ROOT_FOLDER_DIRECTLY_UNDER_PATH.equals(fileListViewForm.getFolderPath())) {
			// 遷移先のフォルダパス
			String folderPath = fileListViewForm.getFolderPath();
			// 各フォルダ名毎に区切る
			List<String> subFolderNameList = StringUtils.toArrayNoTrim(folderPath, CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER);
			// 遷移先のフォルダ名を設定(遷移先においての現在地点)
			String transitionTargetFolderName = subFolderNameList.get(subFolderNameList.size() - 1);
			fileListViewForm.setCurrentLocationSubFolderName(transitionTargetFolderName);
			// 先頭の空要素を除いたリスト(フォルダパスの選択可能部分)をFormにセットする
			List<String> transitionTargetParentsSubFolderNameList = subFolderNameList.stream()
					.limit(subFolderNameList.size() - 1) // 現在階層を除く
					.skip(1) // 空要素を除く
					.collect(Collectors.toList());
			fileListViewForm.setSubFolderNameList(transitionTargetParentsSubFolderNameList);
		} else {
			// ルートフォルダの場合現在のファイル構成管理IDを設定
			fileListViewForm.setCurrentFileConfigurationManagementId(rootFolderInfo.getFileConfigurationManagementId());
		}

		// ■ファイル情報取得

		// Like検索用の文字列を設定
		String stringForLikeSearchFileName = "%" + fileListViewForm.getFileNameSearchString() + "%";
		String folderPathForLikeSearch = fileListViewForm.getFolderPath() + "%";
		List<FileDetailDto> fileList;
		List<String> targetSearchPathList = commonFileManagementService.createTargetSearchPathList(
				fileListViewForm.getCurrentLocationSubFolderName(), folderPathForLikeSearch, rootFolderRelatedInfoManagementId,
				fileListViewForm.getTransitionAnkenId());

		if (!ROOT_FOLDER_DIRECTLY_UNDER_PATH.equals(fileListViewForm.getFolderPath())) {
			fileList = tFileDetailInfoManagementDao.getFileInfoByLikeSearchFileName(stringForLikeSearchFileName,
					folderPathForLikeSearch, rootFolderRelatedInfoManagementId, targetSearchPathList);
		} else {
			targetSearchPathList = commonFileManagementService.createTargetSearchPathList(null, folderPathForLikeSearch,
					rootFolderRelatedInfoManagementId, fileListViewForm.getTransitionAnkenId());
			// ルートフォルダ直下の場合は、シンボリックリンクの情報も含めて検索を行う
			targetSearchPathList.add(ROOT_FOLDER_DIRECTLY_UNDER_PATH);
			AxisKubun axisKubun = null;
			Long id = null;
			if (fileListViewForm.getRootFolderInfo().getAnkenId() != null) {
				axisKubun = AxisKubun.ANKEN;
				id = fileListViewForm.getRootFolderInfo().getAnkenId().asLong();
			} else {
				axisKubun = AxisKubun.KOKYAKU;
				id = fileListViewForm.getRootFolderInfo().getCustomerId().asLong();
			}

			fileList = tFileDetailInfoManagementDao.getFileInfoByLikeSearchFileNameIncludedSymbolicLinkInfo(id,
					axisKubun, stringForLikeSearchFileName, targetSearchPathList);
		}

		// 閲覧権限制御、閲覧制御ボタン表示制御
		if (rootFolderInfo.getAnkenId() != null) {

			List<FileDetailDto> addLimitFileList = new ArrayList<FileDetailDto>();
			addLimitFileList = checkViewLimitByAnkenId(fileList, rootFolderInfo.getAnkenId().asLong());
			fileListViewForm.setFileDetailDtoList(addLimitFileList);
		} else {
			fileListViewForm.setFileDetailDtoList(fileList);

		}
		// 遷移先のフォルダパスから画面に表示するファイル一覧を取得
		fileListViewForm.setFileDetailDtoList(fileList);

	}

	/**
	 * ルートフォルダ関連情報管理IDからルートフォルダ情報を取得する
	 *
	 * @param rootFolderRelatedInfoManagementId 取得対象のルートフォルダ関連情報管理ID
	 * @return ルートフォルダ情報
	 */
	private RootFolderInfoDto getRootFolderInfoByRootFolderRelatedInfoManagementId(Long rootFolderRelatedInfoManagementId) {
		RootFolderInfoDto rootFolderInfo = tFileDetailInfoManagementDao
				.selectRootFolderInfoByRootFolderRelatedInfoManagementId(rootFolderRelatedInfoManagementId);
		if (rootFolderInfo == null) {
			throw new DataNotFoundException("ルートフォルダ情報の取得に失敗しました。[rootFolderRelatedInfoManagementId="
					+ rootFolderRelatedInfoManagementId + "]");
		}
		return rootFolderInfo;
	}

	/**
	 * 一回にアップロードする対象のファイルサイズが、最大容量を超過しないかをチェックする
	 *
	 * @param uploadFileSize アップロード対象のファイルサイズ
	 * @return true: アップロード最大容量を超過している、false: 超過していない
	 */
	public boolean isUploadMaxCapacityExcessForOnce(BigInteger uploadFileSize) {
		Boolean checkResult = false;
		if (CommonConstant.FILE_UPLOAD_MAX_STRAGE_BY_ONCE.compareTo(uploadFileSize) < 0) {
			checkResult = true;
		}
		return checkResult;
	}

	/**
	 * アップロード対象のファイルサイズが、アップロード最大容量を超過しないかをチェックする
	 *
	 * @param uploadFileSize アップロード対象のファイルサイズ
	 * @return true: アップロード最大容量を超過している、false: 超過していない
	 */
	public boolean isUploadMaxCapacityExcess(BigInteger uploadFileSize) {

		Boolean checkResult = false;
		Long tenantSeq = SessionUtils.getTenantSeq();

		// テナントのファイル容量定義を取得
		MTenantMgtEntity mTenantMgtEntity = mTenantMgtDao.selectBySeq(SessionUtils.getTenantSeq());
		if (mTenantMgtEntity == null) {
			throw new DataNotFoundException("アップロード最大容量の取得に失敗しました。[tenantSeq=" + tenantSeq + "]");
		}
		// テナントのファイル容量定義
		Long tenantStrageCapacity = mTenantMgtEntity.getStorageCapacity() * 1024 * 1024 * 1024;
		// 現在使用中のストレージ容量を取得
		Long usingFileStrage = tFileConfigurationManagementDao.getUsingFileStrage();
		if (usingFileStrage == null) {
			usingFileStrage = 0L;
		}
		// 現在使用中のストレージ容量 + アップロードされたファイル容量
		Long compareStrage = usingFileStrage + uploadFileSize.longValue();
		if (tenantStrageCapacity.compareTo(compareStrage) < 0) {
			checkResult = true;
		}

		return checkResult;
	}

	/**
	 * アップロード対象のファイル名の長さが、256バイト以上かチェックする
	 *
	 * @param uploadFileNameList アップロードファイル名リスト
	 * @return true: アップロードファイル名のサイズが256バイト以上になっている、false: アップロードファイル名のサイズが256バイト未満になっている
	 */
	public boolean isUploadMaxFileNameSize(List<String> uploadFileNameList) {
		Set<String> checkResult = new HashSet<String>();

		uploadFileNameList.forEach(uploadFileName -> {
			int fileNameLength;
			try {
				fileNameLength = uploadFileName.getBytes("UTF-8").length;
				if (fileNameLength >= 256) {
					checkResult.add(uploadFileName);
				}
			} catch (UnsupportedEncodingException e) {
			}
			;
		});

		if (checkResult.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * アップロード対象のファイル・フォルダが同階層内に存在するか
	 *
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @param fileName アップロードしたファイル名
	 * @param currentFolderPath アップロード時の現在階層
	 * @return true:同階層に同名のファイル・フォルダが存在する <br>
	 * false:同階層に同名のファイル・フォルダが存在しない
	 */
	public boolean existFile(Long rootFolderRelatedInfoManagementId, String fileName, String fileExtension, String currentFolderPath) {

		List<FileDetailDto> fileList = tFileDetailInfoManagementDao.getMatchInFileNameInSomeFolderHierarchy(
				fileName, fileExtension, currentFolderPath, rootFolderRelatedInfoManagementId);

		if (LoiozCollectionUtils.isEmpty(fileList)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * アップロード対象のフォルダが同階層内に存在するか
	 * 
	 * @param rootFolderRelatedInfoManagementId
	 * @param fileName
	 * @param fileExtension
	 * @param currentFolderPath
	 * @return
	 */
	public boolean existFolderWithSameName(Long rootFolderRelatedInfoManagementId, String fileName, String fileExtension, String currentFolderPath) {
		List<FileDetailDto> fileList = tFileDetailInfoManagementDao.getMatchInFileNameInSomeFolderHierarchy(
				fileName, fileExtension, currentFolderPath, rootFolderRelatedInfoManagementId);

		// フォルダのみのリスト（ファイル除外）
		fileList = fileList.stream().filter(file -> FileKubun.IS_FOLDER.equalsByCode(file.getFileKubun())).collect(Collectors.toList());

		// 拡張子付きのファイル名とフォルダ名で同じものがあればtrue
		for (FileDetailDto dto : fileList) {
			String fileNameWithExtension = dto.getFileName() + StringUtils.null2blank(dto.getFileExtension());
			if (fileNameWithExtension.equals(fileName + StringUtils.null2blank(fileExtension))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * アップロード対象のフォルダが同階層内に存在するか
	 * 
	 * @param rootFolderRelatedInfoManagementId
	 * @param fileName
	 * @param fileExtension
	 * @param currentFolderPath
	 * @return
	 */
	public boolean existFileWithSameName(Long rootFolderRelatedInfoManagementId, String fileName, String fileExtension, String currentFolderPath) {
		List<FileDetailDto> fileList = tFileDetailInfoManagementDao.getMatchInFileNameInSomeFolderHierarchy(
				fileName, fileExtension, currentFolderPath, rootFolderRelatedInfoManagementId);

		// ファイルのみのリスト（フォルダ除外）
		fileList = fileList.stream().filter(file -> FileKubun.IS_FILE.equalsByCode(file.getFileKubun())).collect(Collectors.toList());

		// 拡張子付きのファイル名とフォルダ名で同じものがあればtrue
		for (FileDetailDto dto : fileList) {
			String fileNameWithExtension = dto.getFileName() + StringUtils.null2blank(dto.getFileExtension());
			if (fileNameWithExtension.equals(fileName + StringUtils.null2blank(fileExtension))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ファイル管理IDのフォルダ情報（名前）が引数のフォルダ・ファイル名と一致しているかどうか
	 * 
	 * @param fileConfigurationManagementId ファイル管理ID
	 * @param fileName ファイル名
	 * @param fileExtension ファイル拡張子
	 * @return
	 */
	public boolean equalsFileName(Long fileConfigurationManagementId, String fileName, String fileExtension) {

		TFileDetailInfoManagementEntity tFileDetailInfoManagementEntity = tFileDetailInfoManagementDao.getTFileDetailInfoNotInTrashBox(fileConfigurationManagementId);

		String argsFileName = StringUtils.defaultString(fileName) + StringUtils.defaultString(fileExtension);
		String currentFileName = StringUtils.defaultString(tFileDetailInfoManagementEntity.getFileName()) + StringUtils.defaultString(tFileDetailInfoManagementEntity.getFileExtension());
		return Objects.equals(argsFileName, currentFileName);
	}

	/**
	 * アップロード処理
	 *
	 * @param uploadFileInfo
	 * @param fileListUploadForm
	 * @throws IOException
	 * @throws AppException
	 */
	public void executeUpload(FileUploadFileInfoDto uploadFileInfo, FileListUploadForm fileListUploadForm) throws IOException, AppException {

		// 登録値保持用
		FileGeneralDto generalDto = new FileGeneralDto();

		// フォルダ丸ごとのアップロードかどうかを判定
		boolean isFolderUpload = existsSubFolderByUploadFileFullPath(uploadFileInfo.getUploadFileFullPath());

		if (isFolderUpload) {

			// アップロード先のファイル構成管理IDを取得
			Long parentFileConfigurationManagementId = getParentFileDetailInfoManagementIdByCurrentFolderPath(
					fileListUploadForm);

			// フォルダのレコードを作成
			List<String> fileFullPathElementList = makeListByFullPath(uploadFileInfo.getUploadFileFullPath());
			String folderPathForCreateFolderRecord = fileListUploadForm.getCurrentFolderPath();
			List<String> folderNames = fileFullPathElementList.stream().limit(fileFullPathElementList.size() - 1).collect(Collectors.toList());
			for (int i = 0; i < folderNames.size(); i++) {
				String folderName = folderNames.get(i);

				// 既にそのフォルダのレコードを作成済みかを確認し、未作成時のみフォルダのレコードを作成
				List<FileDetailDto> fileList = tFileDetailInfoManagementDao.getMatchInFileNameInSomeFolderHierarchy(
						folderName,
						null,
						folderPathForCreateFolderRecord,
						fileListUploadForm.getRootFolderRelatedInfoManagementId());
				if (LoiozCollectionUtils.isEmpty(fileList)) {
					createSubFolderData(
							folderName,
							fileListUploadForm.getRootFolderRelatedInfoManagementId(),
							folderPathForCreateFolderRecord, isFolderUpload, generalDto,
							parentFileConfigurationManagementId);
				}
				// 後続処理のために、必要な情報を控えておく
				parentFileConfigurationManagementId = tFileConfigurationManagementDao.getTFileConfigureationIdByFileNameAndFolderPath(
						folderName,
						null,
						folderPathForCreateFolderRecord,
						fileListUploadForm.getRootFolderRelatedInfoManagementId());
				generalDto.setUploadedFolderFileConfigurationManagementId(parentFileConfigurationManagementId);
				folderPathForCreateFolderRecord += folderName + CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER;

				// アップロードフォルダが多段構成
				if (folderNames.size() > 1) {
					generalDto.setMultiLevelFolderStructure(true);
					generalDto.setUploadFolderPathElementForCreateFileDetailInfoOfFile(folderPathForCreateFolderRecord);
				} else {
					generalDto.setUploadFolderPathElementForCreateFileDetailInfoOfFile(folderName);
				}
			}
		}

		// アップロード対象のファイルが既に存在した場合に、該当のファイルとそのレコードを削除する
		String deleteObjectKey = "";
		if (fileListUploadForm.isFileDuplicate()) {
			// apiの削除処理はロールバックが出来ないので、ここではDBからの削除処理のみを行い、該当のオブジェクトキーを保持しておく
			deleteObjectKey = deleteFileIfFileIsExists(fileListUploadForm, uploadFileInfo, isFolderUpload);
		}

		// アップロードファイルの関連データを作成する
		String s3ObjectKey = createFileManagementRelatedData(
				fileListUploadForm,
				uploadFileInfo,
				isFolderUpload,
				generalDto);

		// ファイルをS3に送る
		uploadFileToAmazonS3(uploadFileInfo, s3ObjectKey);

		if (!StringUtils.isEmpty(deleteObjectKey)) {
			// 全ての処理が終了した場合のみ、S3からファイルオブジェクトを削除する。
			// S3オブジェクトの削除処理でエラーになった場合は、DBはロールバックされ、ファイルはアップロードされた状態（つまりS3にゴミファイルが存在する状態）になるが
			// システム上の影響がなく、ユーザー視点でも、アップロード → エラー → ファイルが上がっていない という見え方になるので問題ない。
			deleteS3File(deleteObjectKey);
		}

	}

	/**
	 * ファイルフルパスからリストを作成する
	 *
	 * @param uploadFileFullPath 作成元のファイルフルパス
	 * @return
	 */
	public List<String> makeListByFullPath(String uploadFileFullPath) {
		uploadFileFullPath = uploadFileFullPath.replace('\\', '/');
		return StringUtils.toArrayNoTrim(uploadFileFullPath, CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER);
	}

	/**
	 * フォルダ丸ごとのアップロードの判定
	 *
	 * @param uploadFileFullPathArray アップロードしたファイルのフルパス
	 * @return true:フォルダ丸ごとのアップロード<br>
	 * false : ファイルのみのアップロード
	 */
	public boolean existsSubFolderByUploadFileFullPath(String uploadFileFullPath) {
		if (StringUtils.isEmpty(uploadFileFullPath)) {
			return false;
		}
		List<String> subFolderNames = makeListByFullPath(uploadFileFullPath);
		return subFolderNames.size() > 0;
	}

	/**
	 * ファイルフルパスからフォルダ名を取得する
	 *
	 * @param uploadFileFullPath アップロードファイルのフルパス
	 * @return フォルダ名
	 */
	public String getFolderNameByUploadFileFullPath(String uploadFileFullPath) {
		// フルパスからフォルダ名の判定ができない場合
		if (!existsSubFolderByUploadFileFullPath(uploadFileFullPath)) {
			return CommonConstant.BLANK;
		}
		List<String> splitListFromOnePath = makeListByFullPath(uploadFileFullPath);
		return splitListFromOnePath.get(0);
	}

	/**
	 * 現在階層のパスに応じた、ファイル構成管理IDを取得する
	 *
	 * @param form アップロードファイル情報
	 * @return 親ファイル構成管理ID
	 */
	private Long getParentFileDetailInfoManagementIdByCurrentFolderPath(FileListUploadForm form) {
		Long id = ROOT_FOLDER_DIRECTLY_UNDER_PATH
				// ルート直下の場合
				.equals(form.getCurrentFolderPath()) ? getFileConfigurationManagementIdOfRootFolder(form.getRootFolderRelatedInfoManagementId())
						// サブフォルダ配下の場合
						: form.getCurrentFileConfigurationManagementId();
		return id;
	}

	/**
	 * ルートフォルダのファイル構成管理IDを取得する
	 *
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @return ファイル構成管理ID
	 */
	private Long getFileConfigurationManagementIdOfRootFolder(Long rootFolderRelatedInfoManagementId) {

		Long id = tFileConfigurationManagementDao.selectFileConfigurationManagementIdOfRootFolder(rootFolderRelatedInfoManagementId);

		if (id == null) {
			throw new DataNotFoundException("ファイル構成管理IDの取得に失敗しました。[rootFolderRelatedInfoManagementId="
					+ rootFolderRelatedInfoManagementId + "]");
		}
		return id;
	}

	/**
	 * サブフォルダのレコード作成処理
	 *
	 * @param subFolderName 作成対象のフォルダ名
	 * @param rootFolderRelatedInfoManagementId 親ルートフォルダ関連情報管理ID
	 * @param creationDestinationPath 作成場所となるパス
	 * @param isSetSysDateAndUplodadUser アップロード日時とアップロードユーザーの設定有無
	 * @param generalDto 値保持用DTO(オプションのため、未設定可)
	 * @param parentFileConfigurationManagementId アップロード時の親フォルダのファイル構成管理ID
	 */
	private void createSubFolderData(String subFolderName, Long rootFolderRelatedInfoManagementId,
			String creationDestinationPath, boolean isSetSysDateAndUplodadUser, FileGeneralDto generalDto,
			Long parentFileConfigurationManagementId) {

		// システム日時
		LocalDateTime sysDateTime = LocalDateTime.now();

		// ■ファイル詳細情報管理の登録■
		TFileDetailInfoManagementEntity detailInfoManagementEntity = new TFileDetailInfoManagementEntity();
		// ファイル名
		detailInfoManagementEntity.setFileName(subFolderName);
		// フォルダパス
		detailInfoManagementEntity.setFolderPath(creationDestinationPath);
		// ゴミ箱フラグ
		detailInfoManagementEntity.setTrashBoxFlg(TrashBoxFlg.IS_NOT_TRASH.getCd());
		// ファイル作成日時
		detailInfoManagementEntity.setFileCreatedDatetime(sysDateTime);

		if (isSetSysDateAndUplodadUser) {
			// アップロード日時
			detailInfoManagementEntity.setUploadDatetime(sysDateTime);
			// アップロードユーザー
			detailInfoManagementEntity.setUploadUser(SessionUtils.getLoginAccountName());
		}
		// 登録
		insertTFileDetailInfoManagementTBL(detailInfoManagementEntity);

		// ■ファイル構成管理の登録■
		TFileConfigurationManagementEntity configurationManagementEntity = new TFileConfigurationManagementEntity();
		// ルートフォルダ関連情報管理ID
		configurationManagementEntity.setRootFolderRelatedInfoManagementId(rootFolderRelatedInfoManagementId);
		// ファイル詳細情報管理ID
		configurationManagementEntity.setFileDetailInfoManagementId(detailInfoManagementEntity.getFileDetailInfoManagementId());
		// ファイル管理表示優先順位ID
		configurationManagementEntity.setFileManagementDisplayPriorityId(FileManagementDisplayPriorityId.NEW_FOLDER_GROUP.getCd());
		// 親ファイル構成管理ID
		configurationManagementEntity.setParentFileConfigurationManagementId(parentFileConfigurationManagementId);
		// ファイル区分
		configurationManagementEntity.setFileKubun(FileKubun.IS_FOLDER.getCd());

		// 登録
		insertTFileConfigurationManagementTBL(configurationManagementEntity);

		// アップロードファイルのレコード登録時用に値を保持
		if (generalDto != null) {
			generalDto.setUploadedFolderFileConfigurationManagementId(
					configurationManagementEntity.getFileConfigurationManagementId());
		}

		// ■フォルダ権限情報管理の登録■
		TFolderPermissionInfoManagementEntity folderPermissionInfoManagementEntity = new TFolderPermissionInfoManagementEntity();
		// ファイル構成管理ID
		folderPermissionInfoManagementEntity
				.setFileConfigurationManagementId(configurationManagementEntity.getFileConfigurationManagementId());
		// 閲覧制限
		folderPermissionInfoManagementEntity.setViewLimit(ViewLimit.NO_VIEW_LIMIT.getCd());

		int insertFolderPermissionInfoManagementCount = tFolderPermissionInfoManagementDao.insert(folderPermissionInfoManagementEntity);

		if (insertFolderPermissionInfoManagementCount != 1) {
			throw new DataNotFoundException("フォルダ権限情報の登録に失敗しました。[fileConfigurationManagementId="
					+ rootFolderRelatedInfoManagementId + "] [fileDetailInfoManagementId="
					+ configurationManagementEntity.getFileConfigurationManagementId() + "]");
		}
	}

	/**
	 * ファイル詳細情報管理登録処理
	 *
	 * @param entity 登録内容
	 */
	private void insertTFileDetailInfoManagementTBL(TFileDetailInfoManagementEntity entity) {
		int insertCount = tFileDetailInfoManagementDao.insert(entity);
		if (insertCount != 1) {
			throw new DataNotFoundException("ファイル詳細情報の登録に失敗しました。 [fileName=" + entity.getFileName() + "] [folderPath="
					+ entity.getFolderPath() + "]");
		}
	}

	/**
	 * ファイル構成管理登録処理
	 *
	 * @param entity 登録内容
	 */
	private void insertTFileConfigurationManagementTBL(TFileConfigurationManagementEntity entity) {
		int insertCount = tFileConfigurationManagementDao.insert(entity);
		if (insertCount != 1) {
			throw new DataNotFoundException("ファイル構成管理の登録に失敗しました。[rootFolderRelatedInfoManagementId="
					+ entity.getRootFolderRelatedInfoManagementId() + "] [fileDetailInfoManagementId="
					+ entity.getFileDetailInfoManagementId() + "]");
		}
	}

	/**
	 * ファイル管理関連のデータを作成する(ファイル用)
	 *
	 * @param fileListUploadForm Form情報
	 * @param uploadFileInfo アップロード処理対象のファイル情報
	 * @param isFolderUpload フォルダをまるごとアップロードしたか
	 * @param generalDto 登録値保持用DTO
	 * @return 登録に使用したS3オブジェクトキー
	 * @throws IOException
	 */
	private String createFileManagementRelatedData(
			FileListUploadForm fileListUploadForm,
			FileUploadFileInfoDto uploadFileInfo,
			boolean isFolderUpload, FileGeneralDto generalDto) throws IOException {

		// ファイル情報
		File fileInfo = this.multipartToFile(uploadFileInfo.getUploadFile());

		// S3のディレクトリ情報
		String dirPath = fileStorageService.getRepDirPath(FILE_MANAGEMENT_S3_DIR_PATH, SessionUtils.getTenantSeq(), uriService.getSubDomainName());
		Directory s3Dir = new Directory(dirPath);

		// S3オブジェクトキー
		String extension = fileStorageService.getFileExtension(uploadFileInfo.getUploadFile().getOriginalFilename());
		String s3ObjectKey = fileStorageService.generateS3ObjectKey(s3Dir, extension);

		// ■ファイル詳細情報管理の登録■
		TFileDetailInfoManagementEntity detailInfoManagementEntity = new TFileDetailInfoManagementEntity();
		// ファイル名
		detailInfoManagementEntity.setFileName(commonFileManagementService.getFileName(fileInfo.getName()));
		// ファイル拡張子
		String fileExtension = commonFileManagementService.getExtension(fileInfo.getName());
		detailInfoManagementEntity.setFileExtension(fileExtension);
		// ファイルタイプ
		detailInfoManagementEntity.setFileType(commonFileManagementService.getFileType(fileExtension));
		// フォルダパス
		detailInfoManagementEntity.setFolderPath(
				getFolderPathToInsertFileDetailInfoManagement(
						fileListUploadForm, isFolderUpload, generalDto));
		// ファイルサイズ
		detailInfoManagementEntity.setFileSize(uploadFileInfo.getUploadFileSize());
		// S3オブジェクトキー
		detailInfoManagementEntity.setS3ObjectKey(s3ObjectKey);
		// ゴミ箱フラグ
		detailInfoManagementEntity.setTrashBoxFlg(TrashBoxFlg.IS_NOT_TRASH.getCd());
		// ファイル作成日時
		detailInfoManagementEntity.setFileCreatedDatetime(
				DateUtils.parseToLocalDateTime(uploadFileInfo.getUploadFileCreateDateTimeString(),
						DateUtils.DATE_TIME_FORMAT_HYPHEN_DELIMITED));
		// アップロード日時
		detailInfoManagementEntity.setUploadDatetime(LocalDateTime.now());
		// アップロードユーザー
		detailInfoManagementEntity.setUploadUser(SessionUtils.getLoginAccountName());

		// 登録
		insertTFileDetailInfoManagementTBL(detailInfoManagementEntity);

		// ■ファイル構成管理■
		TFileConfigurationManagementEntity configurationManagementEntity = new TFileConfigurationManagementEntity();
		// ルートフォルダ関連情報管理ID
		configurationManagementEntity.setRootFolderRelatedInfoManagementId(fileListUploadForm.getRootFolderRelatedInfoManagementId());
		// ファイル詳細情報管理ID
		configurationManagementEntity.setFileDetailInfoManagementId(detailInfoManagementEntity.getFileDetailInfoManagementId());
		// ファイル管理表示優先順位ID
		configurationManagementEntity.setFileManagementDisplayPriorityId(FileManagementDisplayPriorityId.FILE_GROUP.getCd());
		// 親ファイル構成管理ID
		Long parentFileConfigurationManagementId = isFolderUpload ? generalDto.getUploadedFolderFileConfigurationManagementId() // フォルダまるごとアップロード時
				: getParentFileDetailInfoManagementIdByCurrentFolderPath(fileListUploadForm); // 単体のファイルアップロード時
		configurationManagementEntity.setParentFileConfigurationManagementId(parentFileConfigurationManagementId);
		// ファイル区分
		configurationManagementEntity.setFileKubun(FileKubun.IS_FILE.getCd());

		// 登録
		insertTFileConfigurationManagementTBL(configurationManagementEntity);

		return s3ObjectKey;
	}

	/**
	 * ファイル詳細情報管理テーブル登録用のフォルダパスを取得する
	 *
	 * @param fileListUploadForm アップロードフォーム情報
	 * @param isFolderUpload フォルダまるごとのアップロードか否か
	 * @param generalDto 登録値保持用DTO
	 * @return
	 */
	private String getFolderPathToInsertFileDetailInfoManagement(
			FileListUploadForm fileListUploadForm, boolean isFolderUpload,
			FileGeneralDto generalDto) {

		if (generalDto.isMultiLevelFolderStructure()) {
			return generalDto.getUploadFolderPathElementForCreateFileDetailInfoOfFile();
		}

		// 現在階層パス
		String currentFolderPath = fileListUploadForm.getCurrentFolderPath();

		// ファイル単体のアップロード時はアップロード時の現在階層を設定
		if (!isFolderUpload || StringUtils.isEmpty(generalDto.getUploadFolderPathElementForCreateFileDetailInfoOfFile())) {
			return fileListUploadForm.getCurrentFolderPath();
		}

		return currentFolderPath + generalDto.getUploadFolderPathElementForCreateFileDetailInfoOfFile()
				+ CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER;
	}

	/**
	 * MultipartFileをFile型に変換する
	 * 
	 * ※作成したFileクラスのInputStream要素を利用する場合は、{@link jp.loioz.common.service.file.FileStorageService#multipartToFile(MultipartFile) multipartToFile} を利用すること
	 *
	 * @param file MultipartFile情報
	 * @return convFile File型の情報(OriginalFilenameを取得できない場合はnullを返却)
	 */
	public File multipartToFile(MultipartFile file) throws IllegalStateException, IOException {
		if (StringUtils.isEmpty(file.getOriginalFilename())) {
			return null;
		}

		File convFile = fileStorageService.multipartToFile(file);

		// 一時ファイルの削除
		if (!convFile.delete()) {
			// 一時ファイルの削除に失敗した場合、アプリ停止時に削除するように設定
			convFile.deleteOnExit();
		}
		return convFile;
	}

	/**
	 * AmazonS3にファイルをアップロードする
	 *
	 * @param uploadFileInfo アップロードファイル情報
	 * @param s3ObjectKey S3オブジェクトキー
	 * @throws IOException
	 * @throws AppException
	 */
	private void uploadFileToAmazonS3(FileUploadFileInfoDto uploadFileInfo, String s3ObjectKey) throws AppException, IOException {
		fileStorageService.uploadPrivateSpecifyObjectKey(s3ObjectKey, uploadFileInfo.getUploadFile());
	}

	/**
	 * ファイル区分を取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return ファイル区分
	 */
	public FileKubun getFileKubun(Long fileConfigurationManagementId) {
		String fileKubun = tFileConfigurationManagementDao
				.getFileKubunByFileConfigurationManagementId(fileConfigurationManagementId);
		if (StringUtils.isEmpty(fileKubun)) {
			throw new DataNotFoundException("ファイル区分の取得に失敗しました。[fileConfigurationManagementId="
					+ fileConfigurationManagementId + "]");
		}
		return DefaultEnum.getEnum(FileKubun.class, fileKubun);
	}

	/**
	 * メンテナンスメニューに必要なメニューファイル情報を取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return ファイル詳細情報管理Dto
	 */
	public FileMentenanceForm getFileInfoForMentenceMenu(Long fileConfigurationManagementId) {
		FileDetailDto dto = tFileDetailInfoManagementDao.getFileInfoFormentenceMenu(fileConfigurationManagementId);
		if (dto == null) {
			throw new DataNotFoundException("ファイル区分の取得に失敗しました。[fileConfigurationManagementId="
					+ fileConfigurationManagementId + "]");
		}
		FileMentenanceForm form = new FileMentenanceForm();
		if (dto.getAnkenId() != null) {
			List<AnkenTantoDto> anlenTantoList = commonAnkenService.getAnkenTantoListByAnkenId(dto.getAnkenId().asLong());
			// ファイル区分がフォルダに対して閲覧制限を行う。
			if (FileKubun.IS_FOLDER.getCd().equals(dto.getFileKubun())) {
				// アカウントがオーナー、もしくはシステム管理者の場合は閲覧制限、権限変更ボタンの制限をかけない
				if (SessionUtils.isOwner() || SessionUtils.isSystemMng()) {
					dto.setAbleLimit(true);
				} else {
					boolean ablelimitFlg = false;
					// 閲覧制限ボタンの表示制御
					for (AnkenTantoDto ankenDto : anlenTantoList) {
						if (SessionUtils.getLoginAccountSeq().equals(ankenDto.getAccountSeq())) {
							// 担当にログインアカウントが存在した場合は閲覧制限ボタンを表示する
							ablelimitFlg = true;
						}
					}
					dto.setAbleLimit(ablelimitFlg);
				}
			}
		} else {
			dto.setAbleLimit(false);
		}
		form.setFileConfigurationManagementId(dto.getFileConfigurationManagementId());
		form.setFileName(dto.getFileName() + StringUtils.defaultString(dto.getFileExtension()));
		form.setFileKubun(dto.getFileKubun());
		form.setFileType(dto.getFileType());
		form.setAbleLimit(dto.isAbleLimit());
		return form;
	}

	/**
	 * AmazonS3からファイルをダウンロードする
	 *
	 * @param fileDetailInfoManagementId ダウンロード対象のファイル構成管理ID
	 * @param response レスポンス情報
	 * @throws Exception
	 */
	public void fileDownloadFromAmazonS3(Long fileConfigurationManagementId, HttpServletResponse response) throws Exception {

		// S3オブジェクトキーとファイル名を取得
		TFileDetailInfoManagementEntity tFileDetailInfoManagementEntity = getObcjectKeyAndFileName(
				fileConfigurationManagementId);

		if (tFileDetailInfoManagementEntity != null) {
			List<String> subFolderNameList = StringUtils.toArrayNoTrim(tFileDetailInfoManagementEntity.getFolderPath(),
					CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER);
			if (subFolderNameList != null && subFolderNameList.size() > 1) {
				// 先頭の空要素を取り除いてFormにセット
				subFolderNameList = subFolderNameList
						.stream()
						.skip(1)
						.collect(Collectors.toList());
				// サブフォルダがないということは直下のファイルなので閲覧制限チェックはしない。
				if (subFolderNameList != null && subFolderNameList.size() > 0) {
					List<FileDetailDto> subFolderList = tFileDetailInfoManagementDao
							.getFolderInfoBysubFolderNameList(subFolderNameList);
					// 不正アクセスチェック
					if (CheckIrregularityAccessForDownload(subFolderList)) {
						// 閲覧権限の不正アクセス検知
						throw new AppException(MessageEnum.MSG_E00077, null);
					}
				}
			}
		}
		// ファイルダウンロードの準備
		URLCodec codec = new URLCodec("UTF-8");
		String encodeFileName = codec.encode(
				tFileDetailInfoManagementEntity.getFileName() + StringUtils.defaultString(tFileDetailInfoManagementEntity.getFileExtension()));
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + URLEncoder.encode(encodeFileName, StandardCharsets.UTF_8.name()) + "\"");

		// S3からファイルをダウンロード
		try (S3Object s3Object = fileStorageService.fileDownload(tFileDetailInfoManagementEntity.getS3ObjectKey());) {
			byte[] byteArray = LoiozIOUtils.toByteArray(s3Object.getObjectContent());
			response.setHeader(CommonConstant.HEADER_NAME_OF_AJAX_PROC_RESULT, CommonConstant.HEADER_VALUE_OF_AJAX_PROC_RESULT_SUCCESS);
			response.getOutputStream().write(byteArray);
		}
	}

	/**
	 * ダウンロード対象のファイルがあるかチェックする。
	 *
	 * @param fileDetailInfoManagementId ダウンロード対象のファイル構成管理ID
	 * @param response レスポンス情報
	 * @return Boolean チェック結果
	 */
	public boolean downloadPrecheckForFile(Long fileConfigurationManagementId, HttpServletResponse response) throws Exception {
		// S3オブジェクトキーとファイル名を取得
		TFileDetailInfoManagementEntity tFileDetailInfoManagementEntity = getObcjectKeyAndFileName(
				fileConfigurationManagementId);

		if (tFileDetailInfoManagementEntity == null) {
			return false;
		}
		return true;
	}

	/**
	 * ダウンロード対象のフォルダの中にファイルがあるかチェックする。
	 *
	 * @param fileDetailInfoManagementId ダウンロード対象のファイル構成管理ID
	 * @param response レスポンス情報
	 * @return Boolean チェック結果
	 */
	public Boolean downloadPrecheckForFolder(Long fileConfigurationManagementId, HttpServletResponse response) throws Exception {

		// ダウンロード対象のファイル構成管理情報とファイル詳細情報を取得
		TFileConfigurationManagementEntity downloadTargetTFileConfigManageInfoEntity = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (downloadTargetTFileConfigManageInfoEntity == null) {
			return false;
		}
		RootFolderInfoDto rootFolderInfoDto = tRootFolderRelatedInfoManagementDao.getRootFolderRelatedInfoByRootFolderRelatedInfoManagementId(
				downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId());
		if (rootFolderInfoDto == null) {
			return false;
		}
		TFileDetailInfoManagementEntity downloadTargetTFileDetailInfoManagementEntity = tFileDetailInfoManagementDao
				.getTFileDetailInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (downloadTargetTFileDetailInfoManagementEntity == null) {
			return false;
		} else {
			// 閲覧制限不正アクセスチェック
			List<String> subFolderNameList = StringUtils.toArrayNoTrim(downloadTargetTFileDetailInfoManagementEntity.getFolderPath(),
					CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER);
			if (subFolderNameList != null && subFolderNameList.size() > 1) {
				// 先頭の空要素を取り除いてFormにセット
				subFolderNameList = subFolderNameList
						.stream()
						.skip(1)
						.collect(Collectors.toList());
				// サブフォルダがないということは直下のファイルなので閲覧制限チェックはしない。
				subFolderNameList.add(downloadTargetTFileDetailInfoManagementEntity.getFileName());
				if (subFolderNameList != null && subFolderNameList.size() > 0) {
					List<FileDetailDto> subFolderList = tFileDetailInfoManagementDao
							.getFolderInfoBysubFolderNameList(subFolderNameList);
					// 不正アクセスチェック
					if (CheckIrregularityAccessForDownload(subFolderList)) {
						return false;
					}

				}
			}
		}
		// LIKE検索用のフォルダパスを作成
		String folderPathForLikeSearch = createFolderPathForLikeSearch(
				downloadTargetTFileDetailInfoManagementEntity.getFileName(),
				downloadTargetTFileDetailInfoManagementEntity.getFolderPath());
		List<String> targetSearchPathList = new ArrayList<String>();
		if (rootFolderInfoDto.getAnkenId() != null) {
			targetSearchPathList = commonFileManagementService.createTargetSearchPathList(downloadTargetTFileDetailInfoManagementEntity.getFileName(),
					folderPathForLikeSearch, downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId(),
					rootFolderInfoDto.getAnkenId().asLong());
		}
		// LIKE検索
		List<FileDownloadFileInfoDto> downloadFileList = tFileDetailInfoManagementDao
				.getDownloadFileListByFolderPath(folderPathForLikeSearch,
						downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId(), targetSearchPathList);
		if (downloadFileList.size() < 1) {
			return false;
		}
		return true;
	}

	/**
	 * ダウンロード対象のルートフォルダの中にファイルがあるかチェックする。
	 *
	 * @param fileDetailInfoManagementId ダウンロード対象のファイル構成管理ID
	 * @param response レスポンス情報
	 * @return Boolean チェック結果
	 */
	public Boolean downloadPrecheckForRootFolder(Long fileConfigurationManagementId, HttpServletResponse response) throws Exception {

		// ダウンロード対象のファイル構成管理情報を取得
		TFileConfigurationManagementEntity downloadTargetTFileConfigManageInfoEntity = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (downloadTargetTFileConfigManageInfoEntity == null) {
			return false;
		}
		RootFolderInfoDto rootFolderInfoDto = tRootFolderRelatedInfoManagementDao.getRootFolderRelatedInfoByRootFolderRelatedInfoManagementId(
				downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId());
		if (rootFolderInfoDto == null) {
			return false;
		}
		TFileDetailInfoManagementEntity downloadTargetTFileDetailInfoManagementEntity = tFileDetailInfoManagementDao
				.getTFileDetailInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (downloadTargetTFileDetailInfoManagementEntity == null) {
			return false;
		}

		List<String> targetSearchPathList = new ArrayList<String>();
		if (rootFolderInfoDto.getAnkenId() != null) {
			String folderPathForLikeSearch = "/%";
			targetSearchPathList = commonFileManagementService.createTargetSearchPathList(null, folderPathForLikeSearch,
					downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId(), rootFolderInfoDto.getAnkenId().asLong());
		}
		// ルートフォルダ配下のファイル情報を取得
		List<FileDownloadFileInfoDto> downloadFileList = tFileDetailInfoManagementDao
				.getDownloadFileListByRootFolderRelatedInfoManagementId(
						downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId(), targetSearchPathList);
		if (downloadFileList.size() < 1) {
			return false;
		}
		return true;
	}

	/**
	 * ファイル構成管理IDからS3オブジェクトキーとファイル名を取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return S3オブジェクトキーとファイル名
	 */
	private TFileDetailInfoManagementEntity getObcjectKeyAndFileName(Long fileConfigurationManagementId) {

		TFileDetailInfoManagementEntity entity = tFileDetailInfoManagementDao
				.getS3ObjectKeyAndFileNameByFileConfigurationManagementId(fileConfigurationManagementId);
		if (entity == null
				|| StringUtils.isEmpty(entity.getS3ObjectKey())
				|| StringUtils.isEmpty(entity.getFileName())) {
			throw new DataNotFoundException("ダウンロード対象のファイル詳細情報の取得に失敗しました。[fileConfigurationManagementId="
					+ fileConfigurationManagementId + "]");
		}
		return entity;
	}

	/**
	 * AmazonS3からサブフォルダ配下のファイルをダウンロードする
	 *
	 * @param fileConfigurationManagementId ダウンロード対象のファイル構成管理ID
	 * @param response レスポンス情報
	 * @throws Exception
	 */
	public void folderDownloadFromAmazonS3(Long fileConfigurationManagementId, HttpServletResponse response) throws Exception {

		// ダウンロード対象のファイル構成管理情報とファイル詳細情報を取得
		TFileConfigurationManagementEntity downloadTargetTFileConfigManageInfoEntity = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (downloadTargetTFileConfigManageInfoEntity == null) {
			throw new DataNotFoundException("ダウンロード対象のファイル構成管理情報の取得に失敗しました。[fileConfigurationManagementId="
					+ fileConfigurationManagementId + "]");
		}
		RootFolderInfoDto rootFolderInfoDto = tRootFolderRelatedInfoManagementDao.getRootFolderRelatedInfoByRootFolderRelatedInfoManagementId(
				downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId());
		if (rootFolderInfoDto == null) {
			throw new DataNotFoundException("ダウンロード対象のルートフォルダ関連情報の取得に失敗しました。[rootFolderRelatedInfoManagementId="
					+ downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId() + "]");
		}
		TFileDetailInfoManagementEntity downloadTargetTFileDetailInfoManagementEntity = tFileDetailInfoManagementDao
				.getTFileDetailInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (downloadTargetTFileDetailInfoManagementEntity == null) {
			throw new DataNotFoundException("ダウンロード対象のファイル詳細情報の取得に失敗しました。[fileConfigurationManagementId="
					+ fileConfigurationManagementId + "]");
		} else {
			// 閲覧制限不正アクセスチェック
			List<String> subFolderNameList = StringUtils.toArrayNoTrim(downloadTargetTFileDetailInfoManagementEntity.getFolderPath(),
					CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER);
			if (subFolderNameList != null && subFolderNameList.size() > 1) {
				// 先頭の空要素を取り除いてFormにセット
				subFolderNameList = subFolderNameList
						.stream()
						.skip(1)
						.collect(Collectors.toList());
				// サブフォルダがないということは直下のファイルなので閲覧制限チェックはしない。
				subFolderNameList.add(downloadTargetTFileDetailInfoManagementEntity.getFileName());
				if (subFolderNameList != null && subFolderNameList.size() > 0) {
					List<FileDetailDto> subFolderList = tFileDetailInfoManagementDao
							.getFolderInfoBysubFolderNameList(subFolderNameList);
					// 不正アクセスチェック
					if (CheckIrregularityAccessForDownload(subFolderList)) {
						// 閲覧権限の不正アクセス検知
						throw new AppException(MessageEnum.MSG_E00077, null);
					}

				}
			}
		}
		// LIKE検索用のフォルダパスを作成
		String folderPathForLikeSearch = createFolderPathForLikeSearch(
				downloadTargetTFileDetailInfoManagementEntity.getFileName(),
				downloadTargetTFileDetailInfoManagementEntity.getFolderPath());
		List<String> targetSearchPathList = new ArrayList<String>();
		if (rootFolderInfoDto.getAnkenId() != null) {
			targetSearchPathList = commonFileManagementService.createTargetSearchPathList(downloadTargetTFileDetailInfoManagementEntity.getFileName(),
					folderPathForLikeSearch, downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId(),
					rootFolderInfoDto.getAnkenId().asLong());
		}
		// LIKE検索
		List<FileDownloadFileInfoDto> downloadFileList = tFileDetailInfoManagementDao
				.getDownloadFileListByFolderPath(folderPathForLikeSearch,
						downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId(), targetSearchPathList);

		for (FileDownloadFileInfoDto dto : downloadFileList) {
			// 現在のフォルダ位置移行のパスを取得する
			String currentFilepath = commonFileManagementService.getFilePathAfterCurrent(dto.getFolderPath(),
					downloadTargetTFileDetailInfoManagementEntity.getFolderPath());
			dto.setFolderPath(currentFilepath);
		}

		// ダウンロードするファイルに対してzipに格納するときのフォルダパスを作成
		createZipFolderPath(downloadFileList);

		// ファイルダウンロードの準備
		URLCodec codec = new URLCodec("UTF-8");
		String encodeFileName = codec.encode(downloadTargetTFileDetailInfoManagementEntity.getFileName() + ".zip");
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + URLEncoder.encode(encodeFileName, StandardCharsets.UTF_8.name()) + "\"");

		// zip圧縮
		zipCompression(response, downloadFileList);
	}

	/**
	 * S3からダウンロードしたファイルをZipに圧縮する
	 *
	 * @param response レスポンス情報
	 * @param fileList ダウンロードファイル一覧
	 * @param removeFolderPath 除去用フォルダパス
	 * @throws Exception
	 */
	private void zipCompression(
			HttpServletResponse response,
			List<FileDownloadFileInfoDto> downloadFileList) throws Exception {

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zip = new ZipOutputStream(baos, Charset.forName("MS932"));) {

			for (FileDownloadFileInfoDto fileInfo : downloadFileList) {

				String zipFolderPath = fileInfo.getZipFolderPath();
				if (!StringUtils.isEmpty(zipFolderPath) && zipFolderPath.length() > 1) {
					// 先頭の / を除外する
					zipFolderPath = zipFolderPath.substring(1);
				}
				ZipEntry entry = new ZipEntry(zipFolderPath);
				
				zip.putNextEntry(entry);

				// S3からファイルデータを取得し、データをzipに詰める
				if (FileKubun.IS_FILE.equalsByCode(fileInfo.getFileKubun())) {
					try (
							S3Object s3Object = fileStorageService.fileDownload(fileInfo.getS3ObjectKey());
							BufferedInputStream is = new BufferedInputStream(s3Object.getObjectContent());) {

						int len;
						for (;;) {
							len = is.read(buf);
							if (len < 0) {
								break;
							}
							zip.write(buf, 0, len);
						}
					}
				}

				zip.closeEntry();
			}

			// 明示的にZipOutputStreamを閉じる(try-with-resourcesのcloseだと、springが先にレスポンスを返してしまうのでzipファイルデータが欠損した状態になってしまう)
			zip.close();

			// レスポンスヘッダーへの書き込みは前段に記述する
			response.setHeader(CommonConstant.HEADER_NAME_OF_AJAX_PROC_RESULT, CommonConstant.HEADER_VALUE_OF_AJAX_PROC_RESULT_SUCCESS);
			response.getOutputStream().write(baos.toByteArray());
		}

	}

	/**
	 * フォルダ名とダウンロード対象のフォルダパスから、LIKE検索用(前方一致)のフォルダパスを作成する
	 *
	 * @param fileName ダウンロード対象のフォルダ名
	 * @param baseFolderPath 起点となるフォルダパス(ダウンロード対象のフォルダのファイル詳細情報に設定されているフォルダパス)
	 * @return LIKE検索用のフォルダパス
	 */
	private String createFolderPathForLikeSearch(String fileName, String baseFolderPath) {
		return baseFolderPath + fileName + CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER + "%";
	}

	/**
	 * ファイル名とフォルダパスからフルパスを作成する
	 *
	 * @param fileName 作成対象のファイル
	 * @param folderPath 作成対象のフォルダパス
	 * @return 対象のフルパス
	 */
	private String createFolderFullPath(String fileName, String folderPath) {
		return folderPath + fileName;
	}

	/**
	 * AmazonS3からルートフォルダ配下のファイルをダウンロードする
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @param response レスポンス情報
	 * @throws Exception
	 */
	public void rootFolderDownloadFromAmazonS3(Long fileConfigurationManagementId, HttpServletResponse response) throws Exception {

		String fileName = "";
		// ダウンロード対象のファイル構成管理情報を取得
		TFileConfigurationManagementEntity downloadTargetTFileConfigManageInfoEntity = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (downloadTargetTFileConfigManageInfoEntity == null) {
			throw new DataNotFoundException("ダウンロード対象のファイル構成管理情報の取得に失敗しました。[fileConfigurationManagementId="
					+ fileConfigurationManagementId + "]");
		}
		RootFolderInfoDto rootFolderInfoDto = tRootFolderRelatedInfoManagementDao.getRootFolderRelatedInfoByRootFolderRelatedInfoManagementId(
				downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId());
		if (rootFolderInfoDto == null) {
			throw new DataNotFoundException("ダウンロード対象のルートフォルダ関連情報の取得に失敗しました。[rootFolderRelatedInfoManagementId="
					+ downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId() + "]");
		}
		TFileDetailInfoManagementEntity downloadTargetTFileDetailInfoManagementEntity = tFileDetailInfoManagementDao
				.getTFileDetailInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (downloadTargetTFileDetailInfoManagementEntity == null) {
			throw new DataNotFoundException("ダウンロード対象のファイル詳細情報の取得に失敗しました。[fileConfigurationManagementId="
					+ fileConfigurationManagementId + "]");
		}

		List<String> targetSearchPathList = new ArrayList<String>();
		if (rootFolderInfoDto.getAnkenId() != null) {
			String folderPathForLikeSearch = "/%";
			targetSearchPathList = commonFileManagementService.createTargetSearchPathList(null, folderPathForLikeSearch,
					downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId(), rootFolderInfoDto.getAnkenId().asLong());
			// ルートフォルダからのダウンロードの場合は、直下のパスを加える。
			targetSearchPathList.add("/");
			fileName = rootFolderInfoDto.getAnkenId() + StringUtils.defaultString(rootFolderInfoDto.getAnkenName());
		} else {
			fileName = rootFolderInfoDto.getCustomerId() + rootFolderInfoDto.getCustomerName();
		}
		// ルートフォルダ配下のファイル情報を取得
		List<FileDownloadFileInfoDto> downloadFileList = tFileDetailInfoManagementDao
				.getDownloadFileListByRootFolderRelatedInfoManagementId(
						downloadTargetTFileConfigManageInfoEntity.getRootFolderRelatedInfoManagementId(), targetSearchPathList);

		// ダウンロードするファイルに対してzipに格納するときのフォルダパスを作成
		createZipFolderPath(downloadFileList);

		// ファイルダウンロードの準備
		URLCodec codec = new URLCodec("UTF-8");
		String encodeFileName = codec.encode(fileName + ".zip");
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + URLEncoder.encode(encodeFileName, StandardCharsets.UTF_8.name()) + "\"");

		// zip圧縮
		zipCompression(response, downloadFileList);

	}

	/**
	 * ファイルが既に存在する場合、ファイル(レコード)の削除を行う
	 *
	 * @param form フォーム情報
	 * @param uploadFileInfo アップロードしたファイルの情報
	 * @param isFolderUpload フォルダ丸ごとアップロード判定
	 * 
	 * @return 削除予定S3オブジェクトのキー
	 * 
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws AppException
	 */
	private String deleteFileIfFileIsExists(FileListUploadForm form, FileUploadFileInfoDto uploadFileInfo, boolean isFolderUpload)
			throws IllegalStateException, IOException, AppException {

		// ファイル情報
		File fileInfo = this.multipartToFile(uploadFileInfo.getUploadFile());
		// ファイル名
		String fileName = commonFileManagementService.getFileName(fileInfo.getName());
		// ファイル拡張子
		String fileExtension = commonFileManagementService.getExtension(fileInfo.getName());

		String currentFolderPath = form.getCurrentFolderPath();
		Long rootFolderRelatedInfoManagementId = form.getRootFolderRelatedInfoManagementId();

		if (isFolderUpload) {
			// フォルダ丸ごとアップロード時は、アップロードしたフォルダ名で検索を掛けに行くようにする
			currentFolderPath += uploadFileInfo.getUploadFileFullPath().replace(fileName + fileExtension, CommonConstant.BLANK);
		}

		// ファイルが存在しない場合は削除処理スキップ
		if (!existFile(rootFolderRelatedInfoManagementId, fileName, fileExtension, currentFolderPath)) {
			return "";
		}

		// 削除対象のファイル構成管理IDを取得
		Long fileConfigurationManagementId = tFileConfigurationManagementDao.getFileConfigurationManagementIdMatchInFolderNameInSomeFolderHierarchy(
				fileName, fileExtension, currentFolderPath, rootFolderRelatedInfoManagementId, CommonConstant.FileKubun.IS_FILE.getCd());

		// 既に削除済みか判定
		if (fileConfigurationManagementId == null) {
			throw new RuntimeException("削除対象のファイル構成管理IDがすでに存在しません。[fileName=" + fileName
					+ ", currentFolderPath=" + currentFolderPath + ", rootFolderRelatedInfoManagementId="
					+ rootFolderRelatedInfoManagementId + "]");
		}

		// AWS-S3のオブジェクトKEYを取得する
		TFileDetailInfoManagementEntity s3ObjectEntity = tFileDetailInfoManagementDao
				.getS3ObjectKeyAndFileNameByFileConfigurationManagementId(fileConfigurationManagementId);

		// 既に削除済みか判定
		if (s3ObjectEntity == null || StringUtils.isEmpty(s3ObjectEntity.getS3ObjectKey())) {
			throw new RuntimeException("削除対象のオブジェクトキーがすでに存在しません。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}

		// ■ファイル詳細情報管理の削除
		deleteTFileDetailInfoManagementInfo(fileConfigurationManagementId);

		// ■ファイル構成管理の削除
		deleteTFileConfigurationManagementInfo(fileConfigurationManagementId);

		// ■AmazonS3から削除するファイルオブジェクトキーを返却
		return s3ObjectEntity.getS3ObjectKey();
	}

	/**
	 * ファイル詳細情報の削除
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @throws AppException
	 */
	private void deleteTFileDetailInfoManagementInfo(Long fileConfigurationManagementId) throws AppException {

		// 削除対象の取得
		TFileDetailInfoManagementEntity deleteTargetEntity = tFileDetailInfoManagementDao
				.getTFileDetailInfoByFileConfigurationManagementId(fileConfigurationManagementId);

		if (deleteTargetEntity == null) {
			throw new RuntimeException("削除対象のファイル詳細情報はすでに存在しません。[fileConfigurationManagementId="
					+ fileConfigurationManagementId + "]");
		}

		int deleteCount = 0;
		try {
			// 削除
			deleteCount = tFileDetailInfoManagementDao.delete(deleteTargetEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
		if (deleteCount != 1) {
			throw new RuntimeException(
					"ファイル詳細情報の削除に失敗しました。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}

	}

	/**
	 * ファイル構成管理情報の削除
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @throws AppException
	 */
	private void deleteTFileConfigurationManagementInfo(Long fileConfigurationManagementId) throws AppException {

		// 削除対象の取得
		TFileConfigurationManagementEntity deleteTargetEntity = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (deleteTargetEntity == null) {
			throw new RuntimeException(
					"削除対象のファイル構成管理情報はすでに存在しません。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}

		try {
			// 削除
			int deleteCount = tFileConfigurationManagementDao.delete(deleteTargetEntity);

			if (deleteCount == 0) {
				throw new RuntimeException(
						"ファイル構成情報の削除に失敗しました。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
			}
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
	}

	/**
	 * AmazonS3から対象のファイルを削除する
	 *
	 * @param objectKey 削除対象のオブジェクトキー
	 * @throws AppException
	 */
	private void deleteS3File(String objectKey) throws AppException {
		List<String> deleteList = new ArrayList<String>();
		deleteList.add(objectKey);
		fileStorageService.deleteFile(deleteList);
	}

	/**
	 * 新規フォルダ作成
	 *
	 * @param fileManagementListFolderCreateForm フォーム情報
	 */
	public void createSubFolder(FileListFolderCreateForm fileManagementListFolderCreateForm) {

		// ファイル構成管理ID
		Long fileConfigurationManagementId = fileManagementListFolderCreateForm.isCurrentHierarchyIsSubFolder() ? fileManagementListFolderCreateForm
				.getFileConfigurationManagementId() // サブフォルダ配下に作成する場合はフォームのIDを使用
				: getFileConfigurationManagementIdOfRootFolder(fileManagementListFolderCreateForm.getRootFolderRelatedInfoManagementId()); // ルートフォルダ配下の場合はIDを取得

		// サブフォルダ関連のレコード作成
		createSubFolderData(fileManagementListFolderCreateForm.getFolderName(),
				fileManagementListFolderCreateForm.getRootFolderRelatedInfoManagementId(),
				fileManagementListFolderCreateForm.getCurrentFolderPath(), false, null, fileConfigurationManagementId);
	}

	/**
	 * 移動先選択モーダル画面に使用するフォルダ一覧情報を取得する
	 *
	 * @param transitionAnkenId 遷移元案件ID
	 * @param transitionCustomerId 遷移元顧客ID
	 * @param rootFolderRelatedInfoManagementId 取得対象のルートフォルダ関連情報管理ID
	 * @return
	 */
	public List<FolderNameListDto> getFolderList(
			Long transitionAnkenId, Long transitionCustomerId,
			Long rootFolderRelatedInfoManagementId) {

		List<FileDetailDto> folderList = tFileDetailInfoManagementDao.getFolderListIncludedSymbolicLink(rootFolderRelatedInfoManagementId);
		List<FileDetailDto> checkedFolderList = checkViewLimitForMoveFolder(folderList);

		List<FolderNameListDto> folderNameList = new ArrayList<FolderNameListDto>();
		for (FileDetailDto dto : checkedFolderList) {
			if (!dto.isDispLimit()) {
				FolderNameListDto folderNameListDto = new FolderNameListDto();
				folderNameListDto.setFileConfigurationManagementId(dto.getFileConfigurationManagementId());
				if (FileKubun.IS_ROOT_FOLDER.getCd().equals(dto.getFileKubun())) {
					if (dto.getAnkenId() != null) {
						folderNameListDto.setFileName(dto.getAnkenId() + " " + dto.getAnkenName());
					} else {
						folderNameListDto.setFileName(dto.getCustomerId() + " " + dto.getCustomerName());
					}
				} else {
					folderNameListDto.setFileName(dto.getFileName());
				}
				if (tFileDetailInfoManagementDao.getCountSubFolders(dto.getFileConfigurationManagementId()) > 0) {
					folderNameListDto.setExistsSubFolderFlg(true);
				} else {
					folderNameListDto.setExistsSubFolderFlg(false);
				}
				folderNameList.add(folderNameListDto);
			}
		}
		return folderNameList;
	}

	/**
	 * ログインユーザーのアカウント連番を取得する
	 *
	 * @return ログインユーザーのアカウント連番
	 */
	private Long getLoginAccountSeq() {
		Long seq = SessionUtils.getLoginAccountSeq();
		if (seq == null) {
			throw new DataNotFoundException("ログインユーザーのアカウント連番の取得に失敗しました");
		}
		return seq;
	}

	/**
	 * 該当フォルダの配下のフォルダ一覧情報を取得する
	 *
	 * @param fileConfigurationManagementId 取得対象のフォルダのファイル構成管理ID
	 * @return フォルダ一覧
	 */
	public List<FolderNameListDto> getFolderListUnderTargetFolder(Long fileConfigurationManagementId) {
		List<FileDetailDto> folderList = tFileDetailInfoManagementDao.getFolderListUnderFolder(fileConfigurationManagementId);
		if (folderList == null || folderList.size() == 0) {
			throw new DataNotFoundException("フォルダ一覧情報の取得に失敗しました。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}
		List<FileDetailDto> checkedFolderList = checkViewLimitForMoveFolder(folderList);

		List<FolderNameListDto> folderNameList = new ArrayList<FolderNameListDto>();
		for (FileDetailDto dto : checkedFolderList) {
			if (!dto.isDispLimit()) {
				FolderNameListDto folderNameListDto = new FolderNameListDto();
				folderNameListDto.setFileConfigurationManagementId(dto.getFileConfigurationManagementId());
				if (FileKubun.IS_ROOT_FOLDER.getCd().equals(dto.getFileKubun())) {
					if (dto.getAnkenId() != null) {
						folderNameListDto.setFileName(dto.getAnkenId() + " " + dto.getAnkenName());
					} else {
						folderNameListDto.setFileName(dto.getCustomerId() + " " + dto.getCustomerName());
					}
				} else {
					folderNameListDto.setFileName(dto.getFileName());
				}
				if (tFileDetailInfoManagementDao.getCountSubFolders(dto.getFileConfigurationManagementId()) > 0) {
					folderNameListDto.setExistsSubFolderFlg(true);
				} else {
					folderNameListDto.setExistsSubFolderFlg(false);
				}
				folderNameList.add(folderNameListDto);
			}
		}
		return folderNameList;
	}

	/**
	 * 移動対象のファイル・フォルダをサブフォルダへ論理的に移動させる
	 *
	 * @param moveToFileConfigurationManagementId 移動先のファイル構成管理ID
	 * @param targetFileConfigurationManagementId 移動対象ファイル・フォルダのファイル構成管理ID
	 * @throws AppException
	 */
	public void moveToSubFolder(Long moveToFileConfigurationManagementId, Long targetFileConfigurationManagementId) throws AppException {

		boolean destinationIsRootFolder = false;
		FileKubun fileKubunOfTarget = getFileKubun(targetFileConfigurationManagementId);

		switch (fileKubunOfTarget) {
		case IS_FILE:
			moveFileToFolder(moveToFileConfigurationManagementId, targetFileConfigurationManagementId, destinationIsRootFolder);
			break;
		case IS_FOLDER:
			moveSubFolderToFolder(moveToFileConfigurationManagementId, targetFileConfigurationManagementId, destinationIsRootFolder);
			break;
		default:
			// 移動対象がファイル・サブフォルダ以外は想定外のため、エラーとする
			throw new RuntimeException();
		}

	}

	/**
	 * 移動対象のファイル・フォルダをルートフォルダへ論理的に移動させる
	 *
	 * @param moveToFileConfigurationManagementId 移動先のファイル構成管理ID
	 * @param targetFileConfigurationManagementId 移動対象ファイル・フォルダのファイル構成管理ID
	 * @throws AppException
	 */
	public void moveToRootFolder(Long moveToFileConfigurationManagementId, Long targetFileConfigurationManagementId) throws AppException {

		boolean destinationIsRootFolder = true;
		FileKubun fileKubunOfTarget = getFileKubun(targetFileConfigurationManagementId);

		switch (fileKubunOfTarget) {
		case IS_FILE:
			moveFileToFolder(moveToFileConfigurationManagementId, targetFileConfigurationManagementId, destinationIsRootFolder);
			break;
		case IS_FOLDER:
			moveSubFolderToFolder(moveToFileConfigurationManagementId, targetFileConfigurationManagementId, destinationIsRootFolder);
			break;
		default:
			// 移動対象がファイル・サブフォルダ以外は想定外のため、エラーとする
			throw new RuntimeException();
		}
	}

	/**
	 * 移動対象のファイルを該当のフォルダ内に移動させる
	 *
	 * @param moveToFileConfigurationManagementId 移動先のファイル構成管理ID
	 * @param targetFileConfigurationManagementId 移動対象ファイルのファイル構成管理ID
	 * @param 移動先がルートフォルダか true:ルートフォルダ, false: サブフォルダ
	 * @throws AppException
	 */
	private void moveFileToFolder(Long moveToFileConfigurationManagementId, Long targetFileConfigurationManagementId,
			boolean destinationIsRootFolder) throws AppException {

		// 移動先のファイル詳細情報
		TFileDetailInfoManagementEntity destinationFileDetailInfoManagementEntity = tFileDetailInfoManagementDao
				.getTFileDetailInfoByFileConfigurationManagementId(moveToFileConfigurationManagementId);

		// 移動対象のファイル詳細情報
		TFileDetailInfoManagementEntity targetFileDetailInfoManagementEntity = tFileDetailInfoManagementDao
				.getTFileDetailInfoByFileConfigurationManagementId(targetFileConfigurationManagementId);

		// 遷移先に応じたフォルダパスに変更
		targetFileDetailInfoManagementEntity.setFolderPath(
				destinationIsRootFolder ? ROOT_FOLDER_DIRECTLY_UNDER_PATH : createFolderFullPath(
						destinationFileDetailInfoManagementEntity.getFileName(),
						destinationFileDetailInfoManagementEntity.getFolderPath())
						+ CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER);

		// 更新
		updateForTFileDetailInfo(targetFileDetailInfoManagementEntity);

		// 移動先のファイル構成管理情報
		TFileConfigurationManagementEntity destinationFileConfigurationManagementEntity = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoByFileConfigurationManagementId(
						moveToFileConfigurationManagementId);

		// 移動対象のファイル構成管理情報
		TFileConfigurationManagementEntity targetFileConfigurationManagementEntity = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoByFileConfigurationManagementId(
						targetFileConfigurationManagementId);

		// 移動対象の親フォルダの紐づけ先を、移動先のフォルダに変更
		targetFileConfigurationManagementEntity.setParentFileConfigurationManagementId(
				destinationFileConfigurationManagementEntity.getFileConfigurationManagementId());

		// 移動先がルートフォルダの場合はルートフォルダ関連情報管理IDも変更しておく
		if (destinationIsRootFolder) {
			targetFileConfigurationManagementEntity.setRootFolderRelatedInfoManagementId(
					destinationFileConfigurationManagementEntity.getRootFolderRelatedInfoManagementId());
		}

		// 更新
		updateForTFileConfigurationManagementInfo(targetFileConfigurationManagementEntity);
	}

	/**
	 * 移動対象のサブフォルダとその配下のファイルを、該当のサブフォルダ内に移動させる
	 *
	 * @param moveToFileConfigurationManagementId 移動先のファイル構成管理ID
	 * @param targetFileConfigurationManagementId 移動対象フォルダのファイル構成管理ID
	 * @param 移動先がルートフォルダか true:ルートフォルダ, false: サブフォルダ
	 * @throws AppException
	 */
	private void moveSubFolderToFolder(Long moveToFileConfigurationManagementId,
			Long targetFileConfigurationManagementId, boolean destinationIsRootFolder) throws AppException {

		// 移動先のファイル詳細情報
		TFileDetailInfoManagementEntity destinationFileDetailInfoManagementEntity = tFileDetailInfoManagementDao
				.getTFileDetailInfoByFileConfigurationManagementId(moveToFileConfigurationManagementId);

		// 移動対象のファイル詳細情報
		TFileDetailInfoManagementEntity targetFileDetailInfoManagementEntity = tFileDetailInfoManagementDao
				.getTFileDetailInfoByFileConfigurationManagementId(targetFileConfigurationManagementId);

		// 移動先のファイル構成管理情報
		TFileConfigurationManagementEntity destinationFileConfigurationManagementEntity = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoByFileConfigurationManagementId(
						moveToFileConfigurationManagementId);

		// 移動対象のファイル構成管理情報
		TFileConfigurationManagementEntity targetFileConfigurationManagementEntity = tFileConfigurationManagementDao
				.getTFileConfigurationManagementInfoByFileConfigurationManagementId(
						targetFileConfigurationManagementId);

		// ■ファイル詳細情報の更新
		// 更新対象リストに移動対象とその配下の情報を設定する
		String likeSearchPath = createFolderPathForLikeSearch(targetFileDetailInfoManagementEntity.getFileName(),
				targetFileDetailInfoManagementEntity.getFolderPath());
		List<TFileDetailInfoManagementEntity> updateTargetForTFileDetailInfo = tFileDetailInfoManagementDao
				.getFileInfoUnderTargetFolder(
						likeSearchPath, targetFileConfigurationManagementEntity.getRootFolderRelatedInfoManagementId(),
						TrashBoxFlg.IS_NOT_TRASH.getCd());
		updateTargetForTFileDetailInfo.add(targetFileDetailInfoManagementEntity);

		// フォルダパスを移動後のフォルダパスになるように変更する
		String replaceTargetFolderPath = targetFileDetailInfoManagementEntity.getFolderPath();
		// 遷移先に応じたフォルダパスを設定する
		String folderPathToUpdate = destinationIsRootFolder ? ROOT_FOLDER_DIRECTLY_UNDER_PATH : createFolderFullPath(
				destinationFileDetailInfoManagementEntity.getFileName(),
				destinationFileDetailInfoManagementEntity.getFolderPath())
				+ CommonConstant.FILE_MANAGEMENT_SPLIT_PATH_DELIMITER;
		updateTargetForTFileDetailInfo.forEach(target -> target
				.setFolderPath(target.getFolderPath().replaceFirst(replaceTargetFolderPath, folderPathToUpdate)));

		// 更新
		batchUpdateForTFileDetailInfo(updateTargetForTFileDetailInfo);

		// ■ ファイル構成管理情報の更新
		// 親フォルダの紐づけ先を移動先のフォルダに変更する
		targetFileConfigurationManagementEntity
				.setParentFileConfigurationManagementId(destinationFileConfigurationManagementEntity.getFileConfigurationManagementId());

		// 移動先がルートフォルダの場合はルートフォルダ関連情報管理IDも変更しておく
		if (destinationIsRootFolder) {
			targetFileConfigurationManagementEntity.setRootFolderRelatedInfoManagementId(
					destinationFileConfigurationManagementEntity.getRootFolderRelatedInfoManagementId());
		}

		// 更新
		updateForTFileConfigurationManagementInfo(targetFileConfigurationManagementEntity);
	}

	/**
	 * 該当ファイルをゴミ箱フラグを有効化する
	 *
	 * @param fileConfigurationManagementId ゴミ箱フラグ有効化対象のファイル構成管理ID
	 * @throws AppException
	 */
	public void activateTargetFileTrashBoxFlg(Long fileConfigurationManagementId) throws AppException {

		// 更新対象取得
		TFileDetailInfoManagementEntity updateTarget = tFileDetailInfoManagementDao
				.getTFileDetailInfoByFileConfigurationManagementId(fileConfigurationManagementId);

		if (updateTarget == null) {
			throw new DataNotFoundException(
					"更新対象のファイル詳細情報を取得できませんでした。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}

		// ゴミ箱フラグ
		updateTarget.setTrashBoxFlg(TrashBoxFlg.IS_TRASH.getCd());

		// ゴミ箱操作アカウント連番
		updateTarget.setDeletedOperationAccountSeq(getLoginAccountSeq());

		this.updateForTFileDetailInfo(updateTarget);

	}

	/**
	 * 該当フォルダのゴミ箱フラグを有効化する
	 *
	 * @param fileConfigurationManagementId ゴミ箱フラグ有効化対象フォルダのファイル構成管理ID
	 * @param rootFolderRelatedInfoManagementId 削除対象のルートフォルダ関連情報管理ID
	 * @throws AppException 業務エラー
	 */
	public void activateTargetFolderTrashBoxFlg(Long fileConfigurationManagementId, Long rootFolderRelatedInfoManagementId) throws AppException {

		// Like用の文字列を作成するために、更新対象(親フォルダ)の情報を取得
		TFileDetailInfoManagementEntity parentUpdateTarget = tFileDetailInfoManagementDao
				.getTFileDetailInfoByFileConfigurationManagementId(fileConfigurationManagementId);
		if (parentUpdateTarget == null) {
			throw new DataNotFoundException(
					"更新対象のファイル詳細情報を取得できませんでした。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}
		// like検索用文字列を作成
		String stringForLikeSearch = createFolderPathForLikeSearch(parentUpdateTarget.getFileName(),
				parentUpdateTarget.getFolderPath());

		// 選択対象配下の情報を更新対象として取得
		List<TFileDetailInfoManagementEntity> updateTargetList = tFileDetailInfoManagementDao
				.getFileInfoUnderTargetFolder(stringForLikeSearch, rootFolderRelatedInfoManagementId,
						TrashBoxFlg.IS_NOT_TRASH.getCd());
		// 親フォルダ情報を追加
		updateTargetList.add(parentUpdateTarget);

		updateTargetList.forEach(target -> {
			// ゴミ箱フラグ
			target.setTrashBoxFlg(TrashBoxFlg.IS_TRASH.getCd());
			// ゴミ箱操作アカウント連番
			target.setDeletedOperationAccountSeq(getLoginAccountSeq());
		});

		// 更新
		batchUpdateForTFileDetailInfo(updateTargetList);

	}

	/**
	 * ファイル詳細情報の複数件更新処理
	 *
	 * @param updateTargetList 更新対象のファイル詳細情報リスト
	 * @throws AppException
	 */
	private void batchUpdateForTFileDetailInfo(List<TFileDetailInfoManagementEntity> updateTargetList)
			throws AppException {

		// 更新件数
		int[] resultArray = null;

		try {
			// 更新
			resultArray = tFileDetailInfoManagementDao.batchUpdate(updateTargetList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00036, ex);
		}
		if (resultArray.length != updateTargetList.size()) {
			// 実際の更新件数と更新対象の件数が異なる場合はエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00036, null);
		}

	}

	/**
	 * ファイルの名称を変更する
	 *
	 * @param fileManagementListNameChangeForm 名称変更Form情報
	 * @throws AppException
	 */
	public void fileNameChange(FileListNameChangeForm fileManagementListNameChangeForm) throws AppException {

		Long fileConfigurationManagementId = fileManagementListNameChangeForm.getFileConfigurationManagementId();

		// ファイル詳細情報の取得
		TFileDetailInfoManagementEntity changeTarget = tFileDetailInfoManagementDao
				.getTFileDetailInfoNotInTrashBox(fileConfigurationManagementId);
		if (changeTarget == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 変更後の名称を設定
		changeTarget.setFileName(fileManagementListNameChangeForm.getRenameInputText());

		updateForTFileDetailInfo(changeTarget);

	}

	/**
	 * フォルダの名称を変更する
	 *
	 * @param fileManagementListNameChangeForm 名称変更フォーム情報
	 * @throws AppException
	 */
	public void folderNameChange(FileListNameChangeForm fileManagementListNameChangeForm) throws AppException {

		Long fileConfigurationManagementId = fileManagementListNameChangeForm.getFileConfigurationManagementId();

		// Like用の文字列を作成するために、更新対象(親フォルダ)の情報を取得
		TFileDetailInfoManagementEntity parentUpdateTarget = tFileDetailInfoManagementDao
				.getTFileDetailInfoNotInTrashBox(fileConfigurationManagementId);
		if (parentUpdateTarget == null) {
			throw new DataNotFoundException(
					"更新対象のファイル詳細情報を取得できませんでした。[fileConfigurationManagementId=" + fileConfigurationManagementId + "]");
		}
		// like検索用文字列を作成
		String stringForLikeSearch = createFolderPathForLikeSearch(parentUpdateTarget.getFileName(),
				parentUpdateTarget.getFolderPath());

		// 選択したフォルダ配下の更新対象を取得
		List<TFileDetailInfoManagementEntity> updateTargetList = tFileDetailInfoManagementDao
				.getTargetTFileDetailInfoUnderFolderContainTrashBoxFiles(stringForLikeSearch,
						fileManagementListNameChangeForm.getRootFolderRelatedInfoManagementId());
		// 子フォルダのフォルダパスに含まれている名称を変更
		updateTargetList.forEach(updateInfo -> updateInfo.setFolderPath(
				updateInfo.getFolderPath().replaceFirst(
						parentUpdateTarget.getFileName(),
						fileManagementListNameChangeForm.getRenameInputText())));
		// 親フォルダの名称を変更
		parentUpdateTarget.setFileName(fileManagementListNameChangeForm.getRenameInputText());
		// 親フォルダの情報をリストに追加
		updateTargetList.add(parentUpdateTarget);

		// 更新
		batchUpdateForTFileDetailInfo(updateTargetList);

	}

	/**
	 * 1件のファイル詳細情報を更新する
	 *
	 * @param updateTarget 更新内容が設定されたファイル詳細情報
	 * @throws AppException
	 */
	private void updateForTFileDetailInfo(TFileDetailInfoManagementEntity updateTarget) throws AppException {
		try {
			int updateCount = tFileDetailInfoManagementDao.update(updateTarget);
			if (updateCount != 1) {
				throw new RuntimeException(
						"ファイル詳細情報の更新に失敗しました。[fileDetailInfoManagementId=" + updateTarget.getFileDetailInfoManagementId() + "]");
			}
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00036, ex);
		}
	}

	/**
	 * 1件のファイル構成管理情報を更新する
	 *
	 * @param updateTarget 更新内容が設定されたファイル構成管理情報
	 * @throws AppException
	 */
	private void updateForTFileConfigurationManagementInfo(TFileConfigurationManagementEntity updateTarget) throws AppException {
		try {
			int updateCount = tFileConfigurationManagementDao.update(updateTarget);
			if (updateCount != 1) {
				throw new RuntimeException("ファイル構成管理情報の更新に失敗しました[fileConfigurationManagementId="
						+ updateTarget.getFileConfigurationManagementId() + "]");
			}
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00036, ex);
		}
	}

	/**
	 * ファイルプレビュー
	 *
	 * @param fileConfigurationManagementId プレビュー対象のファイル構成管理ID
	 * @param response レスポンス要素(プレビュー対象ファイルのバイナリ設定用)
	 * @throws Exception
	 */
	public void filePreview(Long fileConfigurationManagementId, HttpServletResponse response) throws Exception {

		// S3オブジェクトキーとファイル名を取得
		TFileDetailInfoManagementEntity tFileDetailInfoManagementEntity = getObcjectKeyAndFileName(
				fileConfigurationManagementId);

		URLCodec codec = new URLCodec("UTF-8");
		String encodeFileName = codec.encode(tFileDetailInfoManagementEntity.getFileName());

		String contentType = MediaType.TEXT_HTML_VALUE;
		if (FileType.PDF.getCd().equals(tFileDetailInfoManagementEntity.getFileType())) {
			contentType = MediaType.APPLICATION_PDF_VALUE;
		} else if (FileType.JPEG.getCd().equals(tFileDetailInfoManagementEntity.getFileType())) {
			contentType = MediaType.IMAGE_JPEG_VALUE;
		} else if (FileType.PNG.getCd().equals(tFileDetailInfoManagementEntity.getFileType())) {
			contentType = MediaType.IMAGE_PNG_VALUE;
		}
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "inline; filename=\"" + URLEncoder.encode(encodeFileName, StandardCharsets.UTF_8.name()) + "\"");

		// S3からファイルを取得
		try (S3Object s3Object = fileStorageService.fileDownload(tFileDetailInfoManagementEntity.getS3ObjectKey());) {
			byte[] byteArray = LoiozIOUtils.toByteArray(s3Object.getObjectContent());
			response.getOutputStream().write(byteArray);
		}
	}

	/**
	 * フォルダ権限を取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return ファイル区分
	 * @throws AppException
	 */
	public TFolderPermissionInfoManagementEntity getViewLimit(Long fileConfigurationManagementId) throws AppException {
		TFolderPermissionInfoManagementEntity entity = tFolderPermissionInfoManagementDao
				.selectByFileConfigurationManagementId(fileConfigurationManagementId);
		if (entity == null) {
			// 排他エラーチェックをします。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		return entity;
	}

	/**
	 * フォルダ権限を更新する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return ファイル区分
	 * @throws AppException
	 */
	public void updateViewLimit(FileEditViewLimit inputViewLimitForm) throws AppException {
		TFolderPermissionInfoManagementEntity entity = tFolderPermissionInfoManagementDao
				.selectByFolderPermissionInfoManagementId(inputViewLimitForm.getFolderPermissionInfoManagementId());
		if (entity == null) {
			// 排他エラーチェックをします。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 閲覧権限を設定する。
		entity.setViewLimit(inputViewLimitForm.getViewLimit());
		try {
			// 更新します。
			tFolderPermissionInfoManagementDao.update(entity);
		} catch (OptimisticLockException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	/**
	 * 案件軸の閲覧権限をチェックする
	 *
	 * @param fileDetailList
	 * @param ankenId
	 * @return
	 */
	private List<FileDetailDto> checkViewLimitByAnkenId(List<FileDetailDto> fileDetailList, Long ankenId) {

		// 案件担当を取得
		List<AnkenTantoDto> anlenTantoList = commonAnkenService.getAnkenTantoListByAnkenId(ankenId);
		List<FileDetailDto> addLimitFileDetailInfoList = new ArrayList<FileDetailDto>();

		for (FileDetailDto dto : fileDetailList) {
			// ファイル区分がフォルダに対して閲覧制限を行う。
			if (FileKubun.IS_FOLDER.equalsByCode(dto.getFileKubun())) {
				// ログインアカウントがオーナー、もしくはシステム管理者の場合は閲覧制限、権限変更ボタンの制限をかけない
				if (SessionUtils.isOwner() || SessionUtils.isSystemMng()) {
					dto.setDispLimit(false);
					dto.setAbleLimit(true);

				} else {
					// ログインアカウントが一般
					boolean viewlimitFlg = true;
					boolean ablelimitFlg = false;
					if (ViewLimit.VIEWING_LIMITED.equalsByCode(dto.getViewLimit().toString())) {
						for (AnkenTantoDto ankenDto : anlenTantoList) {
							if (SessionUtils.getLoginAccountSeq().equals(ankenDto.getAccountSeq())) {
								// 担当にログインアカウントが存在した場合は制限をかけない
								viewlimitFlg = false;
							}
						}
					} else {
						// 制限がかかってない場合
						viewlimitFlg = false;
					}
					// 閲覧制限ボタンの表示制御
					for (AnkenTantoDto ankenDto : anlenTantoList) {
						if (SessionUtils.getLoginAccountSeq().equals(ankenDto.getAccountSeq())) {
							// 担当にログインアカウントが存在した場合は閲覧制限ボタンを表示する
							ablelimitFlg = true;
						}
					}
					dto.setDispLimit(viewlimitFlg);
					dto.setAbleLimit(ablelimitFlg);
				}
			}
			addLimitFileDetailInfoList.add(dto);
		}
		return addLimitFileDetailInfoList;
	}

	/**
	 * 閲覧権限の不正アクセスをチェックする
	 *
	 * @param updateTarget 更新内容が設定されたファイル構成管理情報
	 * @throws AppException
	 */
	private Boolean CheckIrregularityAccess(String TargetFolderName, List<String> subFolderNameList, Long ankenId) {
		// チェック対象を遷移先のフォルダとその親のサブフォルダリストとする。
		List<String> searchFolderNameList = new ArrayList<String>();
		searchFolderNameList.add(TargetFolderName);
		searchFolderNameList.addAll(subFolderNameList);

		List<FileDetailDto> subFolderList = tFileDetailInfoManagementDao.getFolderInfoBysubFolderNameList(searchFolderNameList);
		List<AnkenTantoDto> ankenTantoList = commonAnkenService.getAnkenTantoListByAnkenId(ankenId);
		// 不正アクセス
		Boolean irregularityAccess = false;
		// アカウントがオーナー、もしくはシステム管理者の場合は閲覧制限、権限変更ボタンのチェックしない
		if (!SessionUtils.isOwner() && !SessionUtils.isSystemMng()) {
			for (FileDetailDto dto : subFolderList) {
				Boolean limitedFlg = false;
				// ファイル区分がフォルダに対して閲覧制限を行う。
				if (FileKubun.IS_FOLDER.getCd().equals(dto.getFileKubun())) {
					if (ViewLimit.VIEWING_LIMITED.getCd().equals(dto.getViewLimit().toString())) {
						for (AnkenTantoDto ankenDto : ankenTantoList) {
							if (SessionUtils.getLoginAccountSeq().equals(ankenDto.getAccountSeq())) {
								// 担当にログインアカウントが存在した場合は制限をかけない
								limitedFlg = true;
							}
						}
						// 制限がかかってない場合
						if (!limitedFlg) {
							irregularityAccess = true;
						}
					}
				}
			}
		}
		return irregularityAccess;
	}

	/**
	 * ダウンロード時の閲覧権限の不正アクセスをチェックする
	 *
	 * @param updateTarget 更新内容が設定されたファイル構成管理情報
	 * @throws AppException
	 */
	private Boolean CheckIrregularityAccessForDownload(List<FileDetailDto> subFolderList) {
		Boolean irregularityAccess = false;
		// アカウントがオーナー、もしくはシステム管理者の場合は閲覧制限、権限変更ボタンのチェックしない
		if (!SessionUtils.isOwner() && !SessionUtils.isSystemMng()) {
			for (FileDetailDto dto : subFolderList) {
				Boolean limitedFlg = false;
				// ファイル区分がフォルダに対して閲覧制限を行う。
				if (FileKubun.IS_FOLDER.getCd().equals(dto.getFileKubun())) {
					if (ViewLimit.VIEWING_LIMITED.getCd().equals(dto.getViewLimit().toString())) {
						List<AnkenTantoDto> ankenTantoList = commonAnkenService.getAnkenTantoListByAnkenId(dto.getAnkenId().asLong());
						for (AnkenTantoDto ankenDto : ankenTantoList) {
							if (SessionUtils.getLoginAccountSeq().equals(ankenDto.getAccountSeq())) {
								// 担当にログインアカウントが存在した場合は制限をかけない
								limitedFlg = true;
							}
						}
						// 制限がかかってない場合
						if (!limitedFlg) {
							irregularityAccess = true;
						}
					}
				}
			}
		}
		return irregularityAccess;
	}

	/**
	 * ファイル移動時の閲覧権限のチェックする
	 *
	 * @param updateTarget 更新内容が設定されたファイル構成管理情報
	 * @throws AppException
	 */
	private List<FileDetailDto> checkViewLimitForMoveFolder(List<FileDetailDto> fileDetailList) {

		List<FileDetailDto> addLimitFileDetailInfoList = new ArrayList<FileDetailDto>();
		for (FileDetailDto dto : fileDetailList) {
			// ファイル区分がフォルダに対して閲覧制限を行う。
			if (FileKubun.IS_FOLDER.getCd().equals(dto.getFileKubun())) {
				// アカウントがオーナー、もしくはシステム管理者の場合は閲覧制限、権限変更ボタンの制限をかけない
				if (SessionUtils.isOwner() || SessionUtils.isSystemMng()) {
					dto.setDispLimit(false);
				} else {
					boolean viewlimitFlg = true;
					if (ViewLimit.VIEWING_LIMITED.getCd().equals(dto.getViewLimit().toString())) {
						if (dto.getAnkenId() != null) {
							List<AnkenTantoDto> ankenTantoList = commonAnkenService.getAnkenTantoListByAnkenId(dto.getAnkenId().asLong());
							for (AnkenTantoDto ankenDto : ankenTantoList) {
								if (SessionUtils.getLoginAccountSeq().equals(ankenDto.getAccountSeq())) {
									// 担当にログインアカウントが存在した場合は制限をかけない
									viewlimitFlg = false;
								}
							}
						} else {
							viewlimitFlg = false;
						}
					} else {
						// 制限がかかってない場合
						viewlimitFlg = false;
					}
					dto.setDispLimit(viewlimitFlg);
				}
			}
			addLimitFileDetailInfoList.add(dto);
		}
		return addLimitFileDetailInfoList;
	}

	/**
	 * ZIPで圧縮するときのフォルダパスを作成します。
	 * パスが同じものがある場合、エラーとしてAppExceptionをthrowします。
	 * 
	 * @param downloadFileList ダウンロードファイルのリスト
	 * @throws AppException
	 */
	private void createZipFolderPath(List<FileDownloadFileInfoDto> downloadFileList) throws AppException {

		// 重複確認用のフォルダ名格納リスト
		List<String> zipFolderPathList = new ArrayList<>();

		// ダウンロードパス作成
		for (FileDownloadFileInfoDto fileInfo : downloadFileList) {
			String zipFolderPath = "";
			if (FileKubun.IS_FILE.getCd().equals(fileInfo.getFileKubun())) {
				zipFolderPath = fileInfo.getFolderPath() + fileInfo.getFileName() + StringUtils.defaultString(fileInfo.getFileExtension());
			} else if (FileKubun.IS_FOLDER.getCd().equals(fileInfo.getFileKubun())) {
				zipFolderPath = fileInfo.getFolderPath() + fileInfo.getFileName() + "/";
			} else if (FileKubun.IS_ROOT_FOLDER.getCd().equals(fileInfo.getFileKubun())) {
				zipFolderPath = fileInfo.getFileName() + "/";
			}
			// zipに圧縮するファイルを定義
			// ファイル名をMS932にエンコードできない文字はエクスクラメーションマークに置換する。
			if (!Charset.forName("MS932").newEncoder().canEncode(zipFolderPath)) {
				StringBuffer buffer = new StringBuffer();
				char[] zipFolderPathChar = zipFolderPath.toCharArray();
				for (char pathChar : zipFolderPathChar) {
					if (Charset.forName("MS932").newEncoder().canEncode(pathChar)) {
						buffer.append(pathChar);
					} else {
						buffer.append(CommonConstant.EXCLAMATIONMARK);
					}
				}
				zipFolderPath = buffer.toString();
			}
			// パスが重複しているか
			if (zipFolderPathList.contains(zipFolderPath)) {
				// パスが重複している場合は、エラーとする
				throw new AppException(MessageEnum.MSG_E00173, null);
			}
			zipFolderPathList.add(zipFolderPath);
			fileInfo.setZipFolderPath(zipFolderPath);
		}
	}
}
