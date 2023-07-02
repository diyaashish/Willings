package jp.loioz.app.user.fileTrashBox.form;

import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.validation.annotation.FileName;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.dto.TrashBoxFileListDto;
import lombok.Data;

/**
 * ファイル管理画面表示Form
 */
@Data
public class FileTrashBoxListViewForm implements PagerForm {

	/** 遷移元案件ID */
	private Long transitionAnkenId;

	/** 遷移元顧客ID */
	private Long transitionCustomerId;

	/** ファイル構成管理ID */
	private Long fileConfigurationManagementId;

	/** ファイル一覧情報 */
	private List<TrashBoxFileListDto> trashBoxFileList;

	/** ページ番号(初期遷移時) */
	private Integer page = DEFAULT_PAGE;

	/** 表示件数 */
	private PageSize pageSize = PageSize.TWENTY;

	/** 検索結果件数 */
	private Long count;

	/** ページ情報 */
	private Page<TrashBoxFileListDto> pageInfo;

	/** ファイル使用率の表示文言 */
	private String dispStrageInfo;

	/** ファイル名検索文言 */
	@MaxDigit(max = 255, message = "ファイル名検索は255文字以内で入力してください")
	@FileName(message = "ファイル名検索に次の文字は使用できません。 " + CommonConstant.FILENAME_PROHIBITION_CHARACTER)
	private String fileNameSearchString;

	/**
	 * 画面側返却用
	 */
	@Data
	public class JsonResponseData {

		/** 処理の成否 */
		private boolean isSuccess;

		/** エラーコード */
		private String errorCode;

		/** メッセージ */
		private String message;
	}

}
