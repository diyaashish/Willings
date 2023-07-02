package jp.loioz.dto;

import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import jp.loioz.common.utility.DateUtils;
import lombok.Data;

/**
 * アップロードファイル一覧Dto
 */
@Entity
@Data
public class UploadFileListDto {

	private static final String DATE_STR_FORMAT = DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED;

	/** 独自情報ファイル連番 */
	@Column(name = "dokuji_info_file_seq")
	private Long dokujiInfoFileSeq;

	/** ファイル名 */
	@Column(name = "file_name")
	private String fileName;

	/** URL */
	@Column(name = "url")
	private String url;

	/** 作成日時 */
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	/** アカウント名 */
	@Column(name = "system_account_name")
	private String systemAccountName;

	/**
	 * 作成日時を文字列で取得する
	 */
	public String getCreatedAtStr() {

		return DateUtils.parseToString(this.getCreatedAt(), DATE_STR_FORMAT);
	}
}