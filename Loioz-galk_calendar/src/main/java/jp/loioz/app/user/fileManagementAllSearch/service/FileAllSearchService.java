package jp.loioz.app.user.fileManagementAllSearch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.doma.boot.Pageables;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.common.service.CommonFileManagementService;
import jp.loioz.app.user.fileManagementAllSearch.form.FileAllSearchViewForm;
import jp.loioz.common.constant.CommonConstant.FileKubun;
import jp.loioz.common.constant.CommonConstant.ViewLimit;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.TFileDetailInfoManagementDao;
import jp.loioz.domain.condition.FileSearchCondition;
import jp.loioz.dto.AnkenTantoDto;
import jp.loioz.dto.FileDetailDto;
import jp.loioz.dto.FileSearchResultDto;

/**
 * 全体ファイル管理検索Service
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FileAllSearchService {

	/** ファイル詳細情報管理DAO */
	@Autowired
	private TFileDetailInfoManagementDao tFileDetailInfoManagementDao;

	/** ファイル管理共通サービスクラス */
	@Autowired
	private CommonFileManagementService commonFileManagementService;

	/** 案件共通サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/**
	 * 検索結果をフォームにセットする
	 *
	 * @param fileManagementAllSearchForm 検索条件を含んだ、セット対象のフォーム
	 */
	public void setSearchResultToForm(FileAllSearchViewForm fileAllSearchViewForm) {

		FileSearchCondition searchCondition = fileAllSearchViewForm.convertToCondition();

		// ページャー設定
		Pageable pageable = fileAllSearchViewForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();
		List<FileDetailDto> folderList;
		List<FileDetailDto> viewLimitedFolderList;
		Map<String, String> limitFolderMap = new HashMap<String, String>();
		// 全ての案件配下のフォルダを取得する。
		folderList = tFileDetailInfoManagementDao.getFolderInfoAll();
		// 全ての案件配下の閲覧制限されたフォルダリストを取得する。
		viewLimitedFolderList = tFileDetailInfoManagementDao.getFolderViewLimitedInfoAll();
		// オーナもしくはシステム管理ユーザーは閲覧制限をかけない
		if (!SessionUtils.isOwner() && !SessionUtils.isSystemMng()) {
			// アカウントの閲覧制限されるフォルダ名リストを作成する。
			limitFolderMap = createLimitFolderMap(folderList, viewLimitedFolderList);
		}
		List<String> targetSearchPathList = new ArrayList<String>(limitFolderMap.values());
		// 検索
		List<FileSearchResultDto> searchResultList = tFileDetailInfoManagementDao
				.getFileDetailInfoListMatchedToSearchCondition(searchCondition, options, targetSearchPathList);

		List<FileSearchResultDto> addLimitFileDetailInfoList = new ArrayList<FileSearchResultDto>();
		// 閲覧権限制御、閲覧制御ボタン表示制御
		addLimitFileDetailInfoList = checkViewLimitByAnkenId(searchResultList);
		fileAllSearchViewForm.setSearchResultInfoList(addLimitFileDetailInfoList);

		Page<FileSearchResultDto> page = new PageImpl<>(
				fileAllSearchViewForm.getSearchResultInfoList(), fileAllSearchViewForm.toPageable(),
				options.getCount());
		fileAllSearchViewForm.setCount(page.getTotalElements());
		fileAllSearchViewForm.setPageInfo(page);
		fileAllSearchViewForm.setSearchResultTotalCount(page.getTotalElements());

		// 契約中のストレージ容量、使用容量、使用率の文言を取得
		fileAllSearchViewForm.setDispStrageInfo(commonFileManagementService.getStrageInfo());
	}

	/**
	 * ユーザーにおける閲覧制限されるフォルダーマップを取得する。
	 *
	 * @param fileManagementAllSearchForm 検索条件を含んだ、セット対象のフォーム
	 */
	public Map<String, String> createLimitFolderMap(List<FileDetailDto> folderList,
			List<FileDetailDto> viewLimitedFolderList) {

		List<FileDetailDto> limitFolderList = new ArrayList<FileDetailDto>();
		Map<String, String> limitFolderMap = new HashMap<String, String>();
		// 閲覧制限されたフォルダ一覧についてアカウントが担当になっていない場合にリストに入れる
		for (FileDetailDto dto : viewLimitedFolderList) {
			List<AnkenTantoDto> ankenTantoList = commonAnkenService.getAnkenTantoListByAnkenId(dto.getAnkenId().asLong());
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
		if (!limitFolderList.isEmpty()) {
			for (FileDetailDto limitDto : limitFolderList) {
				String targetPath = limitDto.getFolderPath() + limitDto.getFileName() + "/";
				for (FileDetailDto dto : folderList) {
					// 閲覧制限がかかっているフォルダパスの子はすべて検索対象から外す。
					// 制限がかかっていないフォルダパスをリストに追加する。
					if (dto.getFolderPath() != null) {
						if (dto.getFolderPath().startsWith(targetPath)) {
							limitFolderMap.put(dto.getFolderPath(), dto.getFolderPath());
						}
					}
				}
			}
		}
		return limitFolderMap;
	}

	/**
	 * 閲覧権限をチェックする
	 *
	 * @param searchResultList検索結果リスト
	 * @throws AppException
	 */
	private List<FileSearchResultDto> checkViewLimitByAnkenId(List<FileSearchResultDto> searchResultList) {
		List<FileSearchResultDto> addLimitFileDetailInfoList = new ArrayList<FileSearchResultDto>();
		for (FileSearchResultDto dto : searchResultList) {
			// ファイル区分がフォルダに対して閲覧制限を行う。
			if (FileKubun.IS_FOLDER.getCd().equals(dto.getFileKubun())) {
				// アカウントがオーナー、もしくはシステム管理者の場合は閲覧制限、権限変更ボタンの制限をかけない
				if (SessionUtils.isOwner() || SessionUtils.isSystemMng()) {
					dto.setDispLimit(false);
				} else {
					boolean viewlimitFlg = true;
					if (dto.getAnkenId() != null) {
						List<AnkenTantoDto> anlenTantoList = commonAnkenService.getAnkenTantoListByAnkenId(dto.getAnkenId().asLong());
						if (ViewLimit.VIEWING_LIMITED.getCd().equals(dto.getViewLimit().toString())) {
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
					} else {
						// 案件IDが存在しない(顧客が親のフォルダ)場合
						viewlimitFlg = false;
					}
					dto.setDispLimit(viewlimitFlg);
				}
			}
			addLimitFileDetailInfoList.add(dto);
		}
		return addLimitFileDetailInfoList;
	}
}
