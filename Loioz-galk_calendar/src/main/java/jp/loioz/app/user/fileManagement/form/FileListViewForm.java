package jp.loioz.app.user.fileManagement.form;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.validation.annotation.FileName;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.dto.FileDetailDto;
import jp.loioz.dto.RootFolderInfoDto;
import lombok.Data;

/**
 * ファイル管理画面表示Form
 */
@Data
public class FileListViewForm implements PagerForm {

	/** 分野 */
	private Long bunya;

	/** 遷移元が案件か */
	private boolean isTransitionFromAnken;

	/** 検索フラグ */
	private boolean isSearch = false;

	/** 遷移元案件ID */
	private Long transitionAnkenId;

	/** 遷移元顧客ID */
	private Long transitionCustomerId;

	/** フォルダパス */
	private String folderPath;

	/** ルートフォルダ関連情報管理ID */
	private Long rootFolderRelatedInfoManagementId;

	/** ファイル構成管理ID */
	private Long fileConfigurationManagementId;

	/** ファイル詳細情報管理ID */
	private Long fileDetailInfoManagementId;

	/** ルートフォルダ情報 */
	private RootFolderInfoDto rootFolderInfo;

	/** サブフォルダ名リスト */
	private List<String> subFolderNameList;

	/** サブフォルダ名(現在階層) */
	private String currentLocationSubFolderName;

	/** 現在階層のファイル構成管理ID */
	private Long currentFileConfigurationManagementId;

	/** ファイル一覧情報 */
	private List<FileDetailDto> fileDetailDtoList;

	/** ページ番号(初期遷移時) */
	private Integer page = DEFAULT_PAGE;

	/** 表示件数 */
	private PageSize pageSize = PageSize.TWENTY;

	/** 検索結果件数 */
	private Long count;

	/** ページ情報 */
	private Page<FileDetailDto> pageInfo;

	/** 遷移元区分 */
	private String transitionSourceKubun;

	/** アップロードファイル */
	private List<MultipartFile> uploadFiles;

	/** ファイル使用率の表示文言 */
	private String dispStrageInfo;

	/** ファイル名検索文言 */
	@MaxDigit(max = 255, message = "ファイル名検索は255文字以内で入力してください")
	@FileName(message = "ファイル名検索に次の文字は使用できません。 " + CommonConstant.FILENAME_PROHIBITION_CHARACTER)
	private String fileNameSearchString;

}
