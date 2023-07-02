package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.common.constant.CommonConstant.AxisKubun;
import jp.loioz.domain.condition.FileSearchCondition;
import jp.loioz.dto.FileDetailDto;
import jp.loioz.dto.FileDownloadFileInfoDto;
import jp.loioz.dto.FileSearchResultDto;
import jp.loioz.dto.RootFolderInfoDto;
import jp.loioz.dto.TrashBoxFileListDto;
import jp.loioz.entity.TFileDetailInfoManagementEntity;

@ConfigAutowireable
@Dao
public interface TFileDetailInfoManagementDao {

	/**
	 * ファイル詳細管理IDリストをキーに検索する
	 *
	 * @param fileDetailInfoManagementIdList
	 * @return ファイル構成管理情報リスト
	 */
	@Select
	List<TFileDetailInfoManagementEntity> selectByFileDetailInfoManagementIdList(List<Long> fileDetailInfoManagementIdList);

	/**
	 * 案件ID、または顧客IDからルートフォルダ情報を取得する
	 *
	 * @param id 遷移元画面の案件ID・顧客ID
	 * @param axisKubun 軸区分
	 * @return ルートフォルダ情報
	 */
	@Select
	RootFolderInfoDto selectRootFolderInfo(Long id, AxisKubun axisKubun);

	/**
	 * ファイル構成管理IDからルートフォルダ情報を取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return ルートフォルダ情報
	 */
	@Select
	RootFolderInfoDto selectRootFolderInfoByFileConfigurationManagementId(Long fileConfigurationManagementId);

	/**
	 * ルートフォルダ関連情報管理IDからルートフォルダ情報を取得する
	 *
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @return ルートフォルダ情報
	 */
	@Select
	RootFolderInfoDto selectRootFolderInfoByRootFolderRelatedInfoManagementId(Long rootFolderRelatedInfoManagementId);

	/**
	 * 遷移先のフォルダパスを取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return 遷移先のフォルダ名とフォルダパス
	 */
	@Select
	TFileDetailInfoManagementEntity getTransitionTargetFolderPathAndFolderName(Long fileConfigurationManagementId);

	/**
	 * ファイル管理画面に表示する、シンボリックリンクを除いたファイル情報を取得する<br>
	 *
	 * @param id 遷移元画面の案件IDまたは顧客ID
	 * @param axisKubun 軸区分
	 * @return ファイル管理画面に表示するシンボリックリンクを除いた情報
	 */
	@Select
	List<FileDetailDto> selectFileInfoExcludedSymbolicLinkInfo(Long id, AxisKubun axisKubun);

	/**
	 * ファイル管理画面に表示する、シンボリックリンクを含めたファイル情報を表示する<br>
	 *
	 * @param id 遷移元画面の案件IDまたは顧客ID
	 * @param symboliclinkFileConfigurationManagementIdList シンボリックリンクのファイル構成管理IDリスト
	 * @param axisKubun 軸区分
	 * @param options ページャー情報
	 * @return ファイル管理画面に表示するシンボリックリンクを含めた情報
	 */
	@Select
	List<FileDetailDto> selectFileInfoIncludedSymbolicLinkInfo(Long id, AxisKubun axisKubun);

	/**
	 * シンボリックリンクを含まない遷移先のファイル一覧を取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @param options ページャー情報
	 * @return ファイル管理画面に表示するファイル情報
	 */
	@Select
	List<FileDetailDto> getFolderUnderFileListWithoutsymboliclink(Long fileConfigurationManagementId);

	/**
	 * フォルダパスとルートフォルダ関連情報管理IDから遷移先のファイル一覧を取得する
	 *
	 * @param folderPath 遷移先のフォルダパス
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @param options ページャー情報
	 * @return ファイル管理画面に表示するファイル情報
	 */
	@Select
	List<FileDetailDto> getFileInfoByFolderPathAndRootFolderRelatedInfoManagementId(String folderPath,
			Long rootFolderRelatedInfoManagementId);

	/**
	 * 現在階層から下の階層に対して、ファイル名検索を行い、該当するファイル一覧を取得する
	 *
	 * @param fileName ファイル名(部分一致)
	 * @param folderPath フォルダパス(前方一致)
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @param options ページャー情報
	 * @return 検索結果のファイル情報
	 */
	@Select
	List<FileDetailDto> getFileInfoByLikeSearchFileName(String fileName, String folderPath,
			Long rootFolderRelatedInfoManagementId, List<String> targetSearchPathList);

	/**
	 * 現在階層から下の階層に対して、フォルダ名検索を行い、該当するフォルダ一覧を取得する
	 *
	 * @param folderPath フォルダパス(前方一致)
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @param options ページャー情報
	 * @return 検索結果のファイル情報
	 */
	@Select
	List<FileDetailDto> getFolderInfoByLikeSearchFileName(String folderName, String folderPath,
			Long rootFolderRelatedInfoManagementId);

	/**
	 * 全てのフォルダ一覧を取得する
	 *
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @return 検索結果のファイル情報
	 */
	@Select
	List<FileDetailDto> getFolderInfoAll();

	/**
	 * 閲覧制限がかけられた全てのフォルダ一覧を取得する
	 *
	 * @return 検索結果のファイル情報
	 */
	@Select
	List<FileDetailDto> getFolderViewLimitedInfoAll();

	/**
	 * サブフォルダののフォルダ一覧を取得する
	 *
	 * @return 検索結果のファイル情報
	 */
	@Select
	List<FileDetailDto> getFolderInfoBysubFolderNameList(List<String> subFolderList);

	/**
	 * シンボリックリンクも含めたファイル名検索を行い、該当するファイル一覧を取得する<br>
	 *
	 * @param id 遷移元画面の案件IDまたは顧客ID
	 * @param symboliclinkFileConfigurationManagementIdList シンボリックリンクのファイル構成管理IDリスト
	 * @param axisKubun 軸区分
	 * @param fileName ファイル名(部分一致)
	 * @param folderPath フォルダパス(前方一致)
	 * @param options ページャー情報
	 * @return ファイル管理画面に表示するシンボリックリンクを含めた情報
	 */
	@Select
	List<FileDetailDto> getFileInfoByLikeSearchFileNameIncludedSymbolicLinkInfo(Long id, AxisKubun axisKubun, String fileName,
			List<String> targetSearchPathList);

	/**
	 * 同フォルダ階層内でアップロード対象のファイル名・フォルダ名に一致するレコードを取得する
	 *
	 * @param fileName 作成対象のファイル名・フォルダ名
	 * @param fileExtension 拡張子
	 * @param folderPath 検索対象のフォルダパス(ルートフォルダ名以降のパス)
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @return 該当件数
	 */
	@Select
	List<FileDetailDto> getMatchInFileNameInSomeFolderHierarchy(String fileName, String fileExtension, String folderPath,
			Long rootFolderRelatedInfoManagementId);

	/**
	 * １件のファイル詳細情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TFileDetailInfoManagementEntity entity);

	/**
	 * ファイル構成管理IDからS3オブジェクトキーとファイル名を取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return S3オブジェクトキー
	 */
	@Select
	TFileDetailInfoManagementEntity getS3ObjectKeyAndFileNameByFileConfigurationManagementId(Long fileConfigurationManagementId);

	/**
	 * ファイル構成管理IDからファイル詳細情報を取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return ファイル詳細情報
	 */
	@Select
	TFileDetailInfoManagementEntity getTFileDetailInfoByFileConfigurationManagementId(Long fileConfigurationManagementId);

	/**
	 * ファイル構成管理IDから、ゴミ箱に入っている削除対象のファイル詳細情報を取得する
	 *
	 * @param fileConfigurationManagementId 削除対象のファイル構成管理ID
	 * @return 削除対象のファイル詳細情報
	 */
	@Select
	TFileDetailInfoManagementEntity getDeleteTFileDetailInfoByFileConfigurationManagementId(Long fileConfigurationManagementId);

	/**
	 * ファイル構成管理IDから、ゴミ箱に入っていないファイル詳細情報を取得する
	 *
	 * @param fileConfigurationManagementId 取得対象のファイル詳細情報
	 * @return ファイル詳細情報
	 */
	@Select
	TFileDetailInfoManagementEntity getTFileDetailInfoNotInTrashBox(Long fileConfigurationManagementId);

	/**
	 * 1件のファイル詳細情報を削除する
	 *
	 * @param tFileDetailInfoManagementEntity 削除対象
	 * @return 削除件数
	 */
	@Delete
	int delete(TFileDetailInfoManagementEntity tFileDetailInfoManagementEntity);

	/**
	 * フォルダパスに含まれるダウンロード対象のファイル一式を取得する
	 *
	 * @param folderPath ダウンロード対象のフォルダパス
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @return ダウンロードするファイル一式のファイル詳細情報
	 */
	@Select
	List<FileDownloadFileInfoDto> getDownloadFileListByFolderPath(String folderPath, Long rootFolderRelatedInfoManagementId,
			List<String> targetSearchPathList);

	/**
	 * ルートフォルダ関連情報管理IDに紐づくダウンロード対象のファイル一覧を取得する
	 *
	 * @param rootFolderRelatedInfoManagementId
	 * @return ダウンロードするファイル一式のファイル詳細情報
	 */
	@Select
	List<FileDownloadFileInfoDto> getDownloadFileListByRootFolderRelatedInfoManagementId(Long rootFolderRelatedInfoManagementId,
			List<String> targetSearchPathList);

	/**
	 * シンボリックリンクを含めた、ルートフォルダ直下のフォルダ一覧を取得する
	 *
	 * @param rootFolderRelatedInfoManagementId 取得対象のルートフォルダ関連情報管理ID
	 * @return シンボリックリンクを含めたフォルダ一覧情報
	 */
	@Select
	List<FileDetailDto> getFolderListIncludedSymbolicLink(Long rootFolderRelatedInfoManagementId);

	/**
	 * ファイル構成管理IDから該当のフォルダ配下のフォルダ一覧を取得する
	 *
	 * @param fileConfigurationManagementId 取得対象のフォルダのファイル構成管理ID
	 * @return フォルダ一覧情報
	 *
	 */
	@Select
	List<FileDetailDto> getFolderListUnderFolder(Long fileConfigurationManagementId);

	/**
	 * 1件のファイル詳細情報を更新する
	 *
	 * @param entity 更新データ
	 * @return 更新件数
	 */
	@Update
	int update(TFileDetailInfoManagementEntity entity);

	/**
	 * 対象フォルダ直下のファイル・フォルダのファイル詳細情報を取得する（１階層分のみ）
	 *
	 * @param currentFolderPath 選択対象のフォルダの配置フォルダパス
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @param trashBoxFlg ゴミ箱フラグ
	 * @param fileConfigurationManagementId 取得対象のフォルダのファイル構成管理ID
	 * @return 選択したフォルダとその直下にあるファイル/フォルダ一覧（１階層分）
	 */
	@Select
	List<TFileDetailInfoManagementEntity> getTargetTFileDetailInfoUnderFolder(String currentFolderPath, Long rootFolderRelatedInfoManagementId,
			String trashBoxFlg, Long fileConfigurationManagementId);

	/**
	 * 対象フォルダ配下のファイル・フォルダ（配下の全サブフォルダ、ファイル）のファイル詳細情報を取得する
	 *
	 * @param currentFolderPath 選択対象のフォルダの配置フォルダパス
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @param trashBoxFlg ゴミ箱フラグ
	 * @return 選択したフォルダとその配下にあるファイル/フォルダ一覧
	 */
	@Select
	List<TFileDetailInfoManagementEntity> getFileInfoUnderTargetFolder(String currentFolderPath,
			Long rootFolderRelatedInfoManagementId, String trashBoxFlg);

	/**
	 * 対象フォルダ配下のファイル・フォルダのファイル詳細情報を取得する(ゴミ箱内のファイル・フォルダも含む)
	 *
	 * @param currentFolderPath 選択対象のフォルダの配置フォルダパス
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @return 選択したフォルダとその配下にあるファイル/フォルダ一覧
	 */
	@Select
	List<TFileDetailInfoManagementEntity> getTargetTFileDetailInfoUnderFolderContainTrashBoxFiles(String currentFolderPath,
			Long rootFolderRelatedInfoManagementId);

	/**
	 * 複数件のファイル詳細情報を更新する
	 *
	 * @param updateTargetList 更新対象リスト
	 * @return 更新件数
	 */
	@BatchUpdate
	int[] batchUpdate(List<TFileDetailInfoManagementEntity> updateTargetList);

	/**
	 * ゴミ箱画面に表示するファイル一覧を取得する
	 *
	 * @param parentFileConfigurationManagementIdList 除外対象の親フォルダ
	 * @return ゴミ箱画面に表示するデータ
	 */
	@Select
	List<TrashBoxFileListDto> getTrashFileData(List<Long> parentFileConfigurationManagementIdList);

	/**
	 * フォルダ毎削除した対象を取得する
	 *
	 * @return フォルダ毎ゴミ箱に入れたファイル管理構成ID
	 */
	@Select
	List<Long> getTrashFileFolderData();

	/**
	 * ファイル名のLike検索に該当する、ゴミ箱画面に表示するファイル一覧を取得する
	 *
	 * @param isSystemManager ログインユーザーがシステム管理者か否か
	 * @param loginAccountSeq ログインユーザーのアカウント連番
	 * @param fileName ファイル名(部分一致)
	 * @param parentFileConfigurationManagementIdList 除外対象の親フォルダ
	 * @return
	 */
	@Select
	List<TrashBoxFileListDto> getTrashFileDataByFileNameLikeSearch(boolean isSystemManager, Long loginAccountSeq, String fileName,
			List<Long> parentFileConfigurationManagementIdList);

	/**
	 * ファイル名、フォルダパスに合致する、ゴミ箱内のファイル詳細情報を取得する
	 *
	 * @param fileName ファイル名
	 * @param folderPath フォルダパス
	 * @param rootFolderRelatedInfoManagementId ルートフォルダ関連情報管理ID
	 * @param parentFileConfigurationManagementId 親ファイル構成管理ID
	 * @return ゴミ箱内で合致したファイル詳細情報
	 */
	@Select
	TFileDetailInfoManagementEntity getTFileDetailInTrashBox(String fileName, String folderPath, Long rootFolderRelatedInfoManagementId,
			Long parentFileConfigurationManagementId);

	/**
	 * 複数件のファイル詳細情報を削除する
	 *
	 * @param entityList 削除対象のリスト
	 * @return 削除件数
	 */
	@BatchDelete
	int[] batchDelete(List<TFileDetailInfoManagementEntity> entityList);

	/**
	 * ゴミ箱内のすべてのファイル詳細情報を取得する
	 *
	 * @param isSystemManager ログインユーザーがシステム管理者か否か
	 * @param loginAccountSeq ログインユーザーのアカウント連番
	 * @return ゴミ箱内のすべてのファイル詳細情報(削除権限のあるもののみ)
	 */
	@Select
	List<TFileDetailInfoManagementEntity> getAllTFileDetailInfoInTrashBox(boolean isSystemManager, Long loginAccountSeq);

	/**
	 * 検索条件に該当するファイル詳細情報を取得する
	 *
	 * @param searchCondition 検索条件
	 * @param options ページャー情報
	 * @return 条件に合致するファイル詳細情報
	 */
	@Select
	List<FileSearchResultDto> getFileDetailInfoListMatchedToSearchCondition(FileSearchCondition searchCondition,
			SelectOptions options, List<String> targetSearchPathList);

	/**
	 * フォルダ配下にあるフォルダ数を取得する
	 *
	 * @param fileConfigurationManagementId フォルダのファイル構成管理ID
	 * @return ゴミ箱画面に表示するデータ
	 */
	@Select
	int getCountSubFolders(Long fileConfigurationManagementId);

	/**
	 * ファイル構成管理IDからメンテナンスメニューを表示する為の情報を取得する
	 *
	 * @param fileConfigurationManagementId ファイル構成管理ID
	 * @return ファイル区分
	 */
	@Select
	FileDetailDto getFileInfoFormentenceMenu(Long fileConfigurationManagementId);
}
