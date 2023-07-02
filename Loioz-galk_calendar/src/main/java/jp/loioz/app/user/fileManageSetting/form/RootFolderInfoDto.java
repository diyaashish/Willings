package jp.loioz.app.user.fileManageSetting.form;

import lombok.Data;

@Data
public class RootFolderInfoDto {

	/** フォルダ名 */
	private String folderName;

	/** オーナー名 */
	private String ownerName;

	/** オーナーアカウントID(外部サービスのログインID) */
	private String ownerServiceAccountId;

	/** 作成日時 (連携開始日と同義) */
	private String createdAt;

}
