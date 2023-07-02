package jp.loioz.app.common.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.CommonConstant.ExcelFileExtension;
import jp.loioz.common.constant.CommonConstant.FileKubun;
import jp.loioz.common.constant.CommonConstant.FileManagementDisplayPriorityId;
import jp.loioz.common.constant.CommonConstant.FileType;
import jp.loioz.common.constant.CommonConstant.JpgFileExtension;
import jp.loioz.common.constant.CommonConstant.PdfFileExtension;
import jp.loioz.common.constant.CommonConstant.PngFileExtension;
import jp.loioz.common.constant.CommonConstant.StorageUnit;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TextFileExtension;
import jp.loioz.common.constant.CommonConstant.ViewLimit;
import jp.loioz.common.constant.CommonConstant.WordFileExtension;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MTenantMgtDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TFileConfigurationManagementDao;
import jp.loioz.dao.TFileDetailInfoManagementDao;
import jp.loioz.dao.TFolderPermissionInfoManagementDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TRootFolderRelatedInfoManagementDao;
import jp.loioz.dto.AnkenTantoDto;
import jp.loioz.dto.FileDetailDto;
import jp.loioz.entity.MTenantMgtEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TFileConfigurationManagementEntity;
import jp.loioz.entity.TFileDetailInfoManagementEntity;
import jp.loioz.entity.TFolderPermissionInfoManagementEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TRootFolderRelatedInfoManagementEntity;

/**
 * ファイル管理関連の共通サービス処理
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonFileManagementService extends DefaultService {

	// ルートフォルダ関連情報管理テーブルのDaoクラス
	@Autowired
	private TRootFolderRelatedInfoManagementDao tRootFolderRelatedInfoManagementDao;

	// ファイル詳細情報管理テーブルのDaoクラス
	@Autowired
	private TFileDetailInfoManagementDao tFileDetailInfoManagementDao;

	// ファイル構成管理情報のDaoクラス
	@Autowired
	private TFileConfigurationManagementDao tFileConfigurationManagementDao;

	// フォルダ閲覧権限情報テーブルのDaoクラス
	@Autowired
	private TFolderPermissionInfoManagementDao tFolderPermissionInfoManagementDao;

	// テナントテーブルのDaoクラス
	@Autowired
	private MTenantMgtDao mTenantMgtDao;

	/**
	 * 名簿情報用のDaoクラス
	 */
	@Autowired
	private TPersonDao tPersonDao;
	
	/**
	 * 案件情報用のDaoクラス
	 */
	@Autowired
	private TAnkenDao tAnkenDao;
	
	/** アカウント情報 共通サービス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/**
	 * 顧客フォルダ作成（顧客登録時）
	 *
	 * @param customerId
	 * @param customerName
	 * @return
	 */
	public void createCustomerFolder(Long customerId, String customerName) {

		// 顧客のルートフォルダ関連情報登録
		Long customerRootFolderRelatedInfoManagementId = createRootFolderRelatedInfo(customerId, null);

		// ファイル詳細情報管理情報登録
		String customerFolderName = customerId.toString() + customerName;
		Long customerFileDetailInfoManagementId = createFileDetailInfo(customerFolderName);

		// ファイル構成管理情報登録
		Long customerFileConfigurationManagementId = createFileConfiguration(customerRootFolderRelatedInfoManagementId, customerFileDetailInfoManagementId, FileManagementDisplayPriorityId.CUSTOMER_ROOT_FOLDER_GROUP, FileKubun.IS_ROOT_FOLDER);

		// フォルダ権限情報管理情報登録
		createFolderPermissionInfo(customerFileConfigurationManagementId);
	}
	
	/**
	 * 顧客フォルダ作成（まだフォルダが存在しない場合のみ作成処理を行う）
	 * 
	 * @param customerId
	 */
	public void createCustomerFolderIfNotExist(Long customerId) {
		
		TPersonEntity personEntity = tPersonDao.selectById(customerId);
		// 顧客情報が存在しない場合
		if (personEntity == null) {
			throw new DataNotFoundException("名簿情報が存在しません。[customerId=" + customerId + "]");
		}
		
		// 顧客のRootフォルダの存在確認
		TRootFolderRelatedInfoManagementEntity tRootFolderRelatedInfoManagementEntity = tRootFolderRelatedInfoManagementDao
				.selectByCustomerId(customerId);
		if (tRootFolderRelatedInfoManagementEntity == null) {
			// Rootフォルダが存在しない場合のみ顧客フォルダの作成処理を実施
			
			// フォルダ名
			StringBuilder folderName = new StringBuilder();
			folderName.append(StringUtils.null2blank(personEntity.getCustomerNameSei()));
			folderName.append(StringUtils.null2blank(personEntity.getCustomerNameMei()));
			
			// ファイル管理の顧客フォルダを登録
			this.createCustomerFolder(customerId, folderName.toString());
		}
	}

	/**
	 * 案件フォルダ作成（案件登録時）
	 *
	 * @param customerId
	 * @param ankenId
	 * @param ankenName
	 * @return
	 */
	public void createAnkenFolder(Long ankenId, String ankenName) {

		// 顧客のルートフォルダ関連情報登録
		Long ankenRootFolderRelatedInfoManagementId = createRootFolderRelatedInfo(null, ankenId);

		// ファイル詳細情報管理情報登録
		String ankenFolderName = ankenId.toString() + ankenName;
		Long ankenFileDetailInfoManagementId = createFileDetailInfo(ankenFolderName);

		// ファイル構成管理情報登録
		Long ankenFileConfigurationManagementId = createFileConfiguration(ankenRootFolderRelatedInfoManagementId, ankenFileDetailInfoManagementId, FileManagementDisplayPriorityId.CUSTOMER_ROOT_FOLDER_GROUP, FileKubun.IS_ROOT_FOLDER);

		// フォルダ権限情報管理情報登録
		createFolderPermissionInfo(ankenFileConfigurationManagementId);
	}

	/**
	 * 案件フォルダ作成（まだフォルダが存在しない場合のみ作成処理を行う）
	 * 
	 * @param customerId
	 */
	public void createAnkenFolderIfNotExist(Long ankenId) {
		
		TAnkenEntity anken = tAnkenDao.selectById(ankenId);
		// 案件情報が存在しない場合
		if (anken == null) {
			throw new DataNotFoundException("案件情報が存在しません。[ankenId=" + ankenId + "]");
		}
		
		// 案件のRootフォルダの存在確認
		TRootFolderRelatedInfoManagementEntity tRootFolderRelatedInfoManagementEntity = tRootFolderRelatedInfoManagementDao
				.selectByAnkenId(ankenId);
		if (tRootFolderRelatedInfoManagementEntity == null) {
			// Rootフォルダが存在しない場合のみ案件フォルダの作成処理を実施
			
			// ファイル管理の案件フォルダを登録
			this.createAnkenFolder(ankenId, "");
		}
	}

	/**
	 * 契約中のストレージ容量、使用容量、使用率を取得
	 *
	 * @param customerId
	 * @param ankenId
	 * @param ankenName
	 * @return displayStrageInfo
	 */
	public String getStrageInfo() {

		// 契約中のストレージ容量を取得
		MTenantMgtEntity mTenantMgtEntity = mTenantMgtDao.selectBySeq(SessionUtils.getTenantSeq());
		Long strageCapacity = mTenantMgtEntity.getStorageCapacity();

		// 現在使用中のストレージ容量を取得
		Long usingFileStrage = tFileConfigurationManagementDao.getUsingFileStrage();

		// 現在使用中のストレージ容量の単位変換KB⇒GB
		double dispUsingFileStrage = 0;
		double dispUsingRate = 0;
		String dispScale = StorageUnit.GIGA.getUnitString();
		if (usingFileStrage != null) {
			// GB単位で計算
			dispUsingFileStrage = this.storageUnitChangeTo(StorageUnit.GIGA, usingFileStrage);
			dispUsingRate = Math.ceil(dispUsingFileStrage / strageCapacity * 100 * 100) / 100;
			if (dispUsingFileStrage < 1) {
				// GB単位で計算して1GB未満だったらMBで計算
				dispUsingFileStrage = this.storageUnitChangeTo(StorageUnit.MEGA, usingFileStrage);
				dispUsingRate = Math.ceil(dispUsingFileStrage / strageCapacity / 1024 * 100 * 100) / 100;
				if (dispUsingFileStrage == 0) {
					// MB単位で計算して0MBだったら0.01MBとする。
					dispUsingFileStrage = 0.01;
					dispUsingRate = 0.01;
				}
				dispScale = StorageUnit.MEGA.getUnitString();
			}
		} else {
			dispUsingFileStrage = 0;
			dispUsingRate = 0;
		}
		// 画面表示の文言を設定
		String dispStrageInfo = dispUsingFileStrage + dispScale + " (" + dispUsingRate + "%) " + " / " + strageCapacity + "GB を使用中";
		return dispStrageInfo;
	}

	/**
	 * 指定の単位に数値を変換する
	 * 
	 * @param unit
	 * @param beforeNum
	 * @return
	 */
	public double storageUnitChangeTo(StorageUnit unit, Long beforeNum) {
		if (beforeNum == null) {
			return 0;
		}

		double beforeNumDouble = (double) beforeNum;
		double result = 0;

		switch (unit) {
		case MEGA:
			result = Math.ceil(beforeNumDouble / 1024 / 1024 * 100) / 100;
			break;
		case GIGA:
			result = Math.ceil(beforeNumDouble / 1024 / 1024 / 1024 * 100) / 100;
			break;
		default:
		}

		return result;
	}

	/**
	 * ルートフォルダ関連情報登録
	 *
	 * @param customerId
	 * @param ankenId
	 * @param customerName
	 * @return Long rootFolderRelatedInfoManagementId
	 */
	private Long createRootFolderRelatedInfo(Long customerId, Long ankenId) {

		// 顧客のルートフォルダ関連情報登録
		TRootFolderRelatedInfoManagementEntity TFRIMentity = new TRootFolderRelatedInfoManagementEntity();
		if (customerId != null) {
			TFRIMentity.setCustomerId(customerId);
		} else {
			// 顧客のルートフォルダ登録
			TFRIMentity.setAnkenId(ankenId);
		}
		tRootFolderRelatedInfoManagementDao.insert(TFRIMentity);
		return TFRIMentity.getRootFolderRelatedInfoManagementId();
	}

	/**
	 * ファイル詳細情報管理情報登録
	 *
	 * @param fileName
	 * @return Long fileDetailInfoManagementId
	 */
	private Long createFileDetailInfo(String fileName) {

		TFileDetailInfoManagementEntity TFDIMentity = new TFileDetailInfoManagementEntity();
		TFDIMentity.setFileName(fileName);
		TFDIMentity.setTrashBoxFlg(SystemFlg.FLG_OFF.getCd());
		TFDIMentity.setFileCreatedDatetime(LocalDateTime.now());
		tFileDetailInfoManagementDao.insert(TFDIMentity);
		return TFDIMentity.getFileDetailInfoManagementId();
	}

	/**
	 * ファイル構成管理情報登録
	 *
	 * @param rootFolderRelatedInfoManagementId
	 * @param fileDetailInfoManagementId
	 * @param priorityId
	 * @param fileKubun
	 * @return Long fileDetailInfoManagementId
	 */
	private Long createFileConfiguration(Long rootFolderRelatedInfoManagementId, Long fileDetailInfoManagementId, FileManagementDisplayPriorityId priorityId, FileKubun fileKubun) {

		TFileConfigurationManagementEntity TFCMentity = new TFileConfigurationManagementEntity();
		TFCMentity.setRootFolderRelatedInfoManagementId(rootFolderRelatedInfoManagementId);
		TFCMentity.setFileDetailInfoManagementId(fileDetailInfoManagementId);
		TFCMentity.setFileManagementDisplayPriorityId(priorityId.getCd());
		TFCMentity.setFileKubun(fileKubun.getCd());
		tFileConfigurationManagementDao.insert(TFCMentity);
		return TFCMentity.getFileConfigurationManagementId();
	}

	/**
	 * フォルダ権限情報管理情報登録
	 *
	 * @param fileConfigurationManagementId
	 */
	private void createFolderPermissionInfo(Long fileConfigurationManagementId) {

		TFolderPermissionInfoManagementEntity TFPIMentity = new TFolderPermissionInfoManagementEntity();
		TFPIMentity.setFileConfigurationManagementId(fileConfigurationManagementId);
		TFPIMentity.setViewLimit(ViewLimit.NO_VIEW_LIMIT.getCd());
		tFolderPermissionInfoManagementDao.insert(TFPIMentity);
	}

	/**
	 * ルートフォルダ関連情報管理IDと検索キーから検索Pathを取得する
	 * 
	 * @param rootFolderRelatedInfoManagementId 取得対象のルートフォルダ関連情報管理ID
	 * @return 検索pathリスト
	 */
	public List<String> createTargetSearchPathList(String foldername, String folderPathForLikeSearch, Long rootFolderRelatedInfoManagementId, Long ankenId) {
		List<FileDetailDto> folderList;
		List<FileDetailDto> limitFolderList = new ArrayList<FileDetailDto>();
		folderList = tFileDetailInfoManagementDao.getFolderInfoByLikeSearchFileName(foldername, folderPathForLikeSearch, rootFolderRelatedInfoManagementId);
		// オーナもしくはシステム管理ユーザーは閲覧制限をかけない
		if (!SessionUtils.isOwner() && !SessionUtils.isSystemMng()) {
			// 顧客フォルダの場合は閲覧制限をかけない
			if (ankenId != null) {
				for (FileDetailDto dto : folderList) {
					List<AnkenTantoDto> ankenTantoList = commonAnkenService.getAnkenTantoListByAnkenId(ankenId);
					if (ViewLimit.VIEWING_LIMITED.getCd().equals(dto.getViewLimit().toString())) {
						boolean limitFolderFig = false;
						for (AnkenTantoDto ankenDto : ankenTantoList) {
							if (SessionUtils.getLoginAccountSeq().equals(ankenDto.getAccountSeq())) {
								// 担当にログインアカウントが存在した場合は制限をかけない
								limitFolderFig = true;
							}
						}
						if (!limitFolderFig) {
							// 制限がかかっていて、ユーザーが担当に含まれている場合制限フォルダリストに追加
							limitFolderList.add(dto);
						}
					}
				}
			}
		}
		Map<String, String> limitFolderMap = new HashMap<String, String>();
		if (limitFolderList.isEmpty()) {
			// 閲覧制限がない場合はすべてのフォルダを対象とする。
			folderList.forEach(dto -> {
				limitFolderMap.put(dto.getFolderPath() + dto.getFileName() + "/", dto.getFolderPath() + dto.getFileName() + "/");
			});
		} else {
			for (FileDetailDto dto : folderList) {
				// 閲覧制限がかかっているフォルダパスの子はすべて検索対象から外す。
				// 制限がかかっていないフォルダパスをリストに追加する。
				String ComparisonSource = dto.getFolderPath() + dto.getFileName() + "/";
				Boolean downloadLimitFlg = false;
				for (FileDetailDto limitDto : limitFolderList) {
					String targetPath = limitDto.getFolderPath() + limitDto.getFileName() + "/";
					if (ComparisonSource.startsWith(targetPath)) {
						downloadLimitFlg = true;
					}
				}
				if (!downloadLimitFlg) {
					limitFolderMap.put(dto.getFolderPath() + dto.getFileName() + "/", dto.getFolderPath() + dto.getFileName() + "/");
				}
			}
		}
		List<String> targetSearchPathList = new ArrayList<String>(limitFolderMap.values());
		return targetSearchPathList;
	}

	/**
	 * ファイル名を取得する
	 * 
	 * @return 拡張子を除いたファイル名
	 */
	public String getFileName(String fileName) {
		if (fileName == null) {
			return null;
		}
		int lastDotPosition = fileName.lastIndexOf(".");
		if (lastDotPosition != -1) {
			return fileName.substring(0, lastDotPosition);
		} else {
			return fileName;
		}
	}

	/**
	 * 拡張子を取得する
	 * 
	 * @return ファイル拡張子
	 */
	public String getExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		int lastDotPosition = fileName.lastIndexOf(".");
		if (lastDotPosition != -1) {
			return fileName.substring(lastDotPosition);
		} else {
			return null;
		}
	}

	/**
	 * ファイルタイプを取得する
	 * 
	 * @return ファイルタイプ
	 */
	public String getFileType(String extension) {
		if (extension == null) {
			return null;
		}
		Boolean isExceFilel = Arrays.stream(ExcelFileExtension.values())
				.anyMatch(data -> data.getVal().equals(extension));
		Boolean isWordFile = Arrays.stream(WordFileExtension.values())
				.anyMatch(data -> data.getVal().equals(extension));
		Boolean isPdfFile = Arrays.stream(PdfFileExtension.values())
				.anyMatch(data -> data.getVal().equals(extension));
		Boolean isJpgFile = Arrays.stream(JpgFileExtension.values())
				.anyMatch(data -> data.getVal().equals(extension));
		Boolean isPngFile = Arrays.stream(PngFileExtension.values())
				.anyMatch(data -> data.getVal().equals(extension));
		Boolean isTextFile = Arrays.stream(TextFileExtension.values())
				.anyMatch(data -> data.getVal().equals(extension));
		if (isExceFilel) {
			return FileType.EXCEL.getCd();
		} else if (isWordFile) {
			return FileType.WORD.getCd();
		} else if (isPdfFile) {
			return FileType.PDF.getCd();
		} else if (isJpgFile) {
			return FileType.JPEG.getCd();
		} else if (isPngFile) {
			return FileType.PNG.getCd();
		} else if (isTextFile) {
			return FileType.TEXT.getCd();
		} else {
			return FileType.OTHER.getCd();
		}
	}

	/**
	 * ファイルパスから現在のフォルダ位置以降のパスを取得する
	 * 
	 * @return 現在のフォルダ位置以降のパス
	 */
	public String getFilePathAfterCurrent(String filePath, String currentFilePath) {
		if (filePath == null || currentFilePath == null) {
			return null;
		}
		return filePath.replaceFirst(currentFilePath, "/");
	}
}
