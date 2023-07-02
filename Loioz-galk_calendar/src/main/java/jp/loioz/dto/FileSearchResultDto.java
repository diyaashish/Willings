package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 * 全体ファイル管理画面検索結果用Dto
 */
@Entity
@Data
public class FileSearchResultDto {

	/** ファイル構成管理ID */
	@Column(name = "file_configuration_management_id")
	private Long fileConfigurationManagementId;

	/** 親ファイル構成管理ID */
	@Column(name = "parent_file_configuration_management_id")
	private Long parentFileConfigurationManagementId;

	/** ルートフォルダ関連情報管理ID */
	@Column(name = "root_folder_related_info_management_id")
	private Long rootFolderRelatedInfoManagementId;

	/** ファイル区分 */
	@Column(name = "file_kubun")
	private String fileKubun;

	/** ファイル名 */
	@Column(name = "file_name")
	private String fileName;

	/** ファイル拡張子 */
	@Column(name = "file_extension")
	private String fileExtension;

	/** ファイルタイプ */
	@Column(name = "file_type")
	private String fileType;

	/** フォルダパス */
	@Column(name = "folder_path")
	private String folderPath;

	/** 閲覧制限 */
	@Column(name = "view_limit")
	private Long viewLimit;

	/** 案件ID */
	@Column(name = "anken_id")
	private AnkenId ankenId;

	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 顧客ID */
	@Column(name = "customer_id")
	private CustomerId customerId;

	/** 顧客ID */
	@Column(name = "customer_name")
	private String customerName;

	/** ルートフォルダ名 */
	@Column(name = "root_folder_name")
	private String rootFolderName;

	/** 配下のフォルダ表示制限を行うフラグ **/
	private boolean dispLimit = false;

}
