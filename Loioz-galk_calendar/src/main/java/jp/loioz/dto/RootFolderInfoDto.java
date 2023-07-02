package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

@Entity
@Data
public class RootFolderInfoDto {

	/** ルートフォルダ関連情報管理ID */
	@Column(name = "root_folder_related_info_management_id")
	private Long rootFolderRelatedInfoManagementId;

	/** ファイル構成管理ID */
	@Column(name = "file_configuration_management_id")
	private Long fileConfigurationManagementId;

	/** 案件ID */
	@Column(name = "anken_id")
	private AnkenId ankenId;

	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 顧客ID */
	@Column(name = "customer_id")
	private CustomerId customerId;

	/** 顧客名 */
	@Column(name = "customer_name")
	private String customerName;

	/** ファイル名 */
	@Column(name = "file_name")
	private String fileName;

}
