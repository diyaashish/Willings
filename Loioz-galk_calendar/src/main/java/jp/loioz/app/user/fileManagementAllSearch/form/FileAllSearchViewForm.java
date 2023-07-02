package jp.loioz.app.user.fileManagementAllSearch.form;

import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.FileName;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.domain.condition.FileSearchCondition;
import jp.loioz.dto.FileSearchResultDto;
import lombok.Data;

/**
 * ファイル管理全体検索Form
 */
@Data
public class FileAllSearchViewForm implements PagerForm {

	/** ページ番号(初期遷移時) */
	private Integer page = DEFAULT_PAGE;

	/** 表示件数 */
	private PageSize pageSize = PageSize.TWENTY;

	/** 検索結果件数 */
	private Long count;

	/** ページ情報 */
	private Page<FileSearchResultDto> pageInfo;

	/** 詳細検索の開閉(初期遷移時はOFF) */
	private String detailSearchFlg = CommonConstant.SystemFlg.FLG_OFF.getCd();

	/** ファイル使用率の表示文言 */
	private String dispStrageInfo;

	/** ファイル名 */
	@MaxDigit(max = 255, message = "ファイル名は255文字以内で入力してください")
	@FileName(message = "ファイル名に次の文字は使用できません。 " + CommonConstant.FILENAME_PROHIBITION_CHARACTER)
	private String fileName;

	/** アップロードユーザー */
	@MaxDigit(max = 24, message = "アップロードユーザーは24文字以内で入力してください")
	private String uploadUser;

	/** ファイル種類 */
	private String fileType;

	/** 検索結果 */
	private List<FileSearchResultDto> searchResultInfoList;

	/** 検索該当総件数 */
	private Long searchResultTotalCount;

	/**
	 * 画面入力値を検索条件に変換する
	 *
	 * @return 検索条件
	 */
	public FileSearchCondition convertToCondition() {

		// ファイル名
		String fileNameCondition = CommonConstant.BLANK;
		if (!StringUtils.isEmpty(fileName)) {
			fileNameCondition = "%" + fileName + "%";
		}

		// アップロードユーザー
		String uploadUserCondition = CommonConstant.BLANK;
		if (!StringUtils.isEmpty(uploadUser)) {
			uploadUserCondition = "%" + uploadUser + "%";
		}

		// ファイル種類
		String fileTypeCondition = CommonConstant.BLANK;

		// ファイル区分(ルートフォルダorフォルダorファイル)
		String fileKubunCondition = CommonConstant.BLANK;

		if (!StringUtils.isEmpty(fileType)) {
			if (CommonConstant.FileType.FOLDER.getCd().equals(fileType)) {
				fileKubunCondition = CommonConstant.FileKubun.IS_FOLDER.getCd();
			} else if (CommonConstant.FileType.FILE.getCd().equals(fileType)) {
				fileKubunCondition = CommonConstant.FileKubun.IS_FILE.getCd();
			} else {
				fileTypeCondition = fileType;
			}
		}

		return FileSearchCondition.builder()
				.fileName(fileNameCondition)
				.uploadUser(uploadUserCondition)
				.fileKubun(fileKubunCondition)
				.fileType(fileTypeCondition)
				.build();
	}

}
