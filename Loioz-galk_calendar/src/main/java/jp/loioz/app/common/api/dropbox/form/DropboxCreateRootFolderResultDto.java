package jp.loioz.app.common.api.dropbox.form;

import lombok.Data;

/**
 * Dropboxルートフォルダ作成完了Dto
 */
@Data
public class DropboxCreateRootFolderResultDto {

	/** フォルダID */
	private String folderId;

	/** 共有フォルダID */
	private String shareFolderId;

}
