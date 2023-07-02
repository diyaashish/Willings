package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

/**
 * 部署一覧用のBean
 */
@Entity
@Data
public class BushoListUserBean {

	/** 部署ID */
	@Column(name = "busho_id")
	private Long bushoId;

	/** 部署名 */
	@Column(name = "busho_name")
	private String bushoName;

	/** 所属アカウント名 */
	@Column(name = "shozoku_account_name")
	private String shozokuAccountName;

	/** 表示順 */
	@Column(name = "disp_order")
	private Long dispOrder;

	/** バージョンNo */
	@Column(name = "version_no")
	private Long versionNo;

}