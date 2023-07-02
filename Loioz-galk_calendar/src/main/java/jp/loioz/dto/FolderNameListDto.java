package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class FolderNameListDto {
	
	/** ファイル構成管理ID */
	@Column(name = "file_configuration_management_id")
	private Long fileConfigurationManagementId;
	
	/** ファイル名 */
	@Column(name = "file_name")
	private String fileName;
	
	/** サブフォルダ存在フラグ */
	@Column(name = "exists_sub_folder_flg")
	private boolean existsSubFolderFlg;
	
}
