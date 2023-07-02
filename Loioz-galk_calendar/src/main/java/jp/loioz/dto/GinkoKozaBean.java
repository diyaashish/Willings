package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class GinkoKozaBean {

	// t_ginko_koza
	/** 銀行口座SEQ */
	@Column(name = "ginko_account_seq")
	private Long ginkoAccountSeq;

	/** 表示名 */
	@Column(name = "label_name")
	private String labelName;

	/** 銀行名 */
	@Column(name = "ginko_name")
	private String ginkoName;

	/** 支店名 */
	@Column(name = "shiten_name")
	private String shitenName;

	/** 支店番号 */
	@Column(name = "shiten_no")
	private String shitenNo;
	
	/** 口座種類 */
	@Column(name = "koza_type")
	private String kozaType;
	
	/** 口座番号 */
	@Column(name = "koza_no")
	private String kozaNo;

	/** 口座名義 */
	@Column(name = "koza_name")
	private String kozaName;

	/** テナントSEQ */
	@Column(name = "tenant_seq")
	private Long tenantSeq;

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenid;

	/** アカウントSEQ */
	@Column(name = "account_seq")
	private Long accountSeq;

	// m_tenant
	/** テナント名 */
	@Column(name = "tenant_name")
	private String tenantName;

	// m_account
	/** アカウント名 */
	@Column(name = "account_name")
	private String accountName;
}
