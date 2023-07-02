package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Data
@Entity
public class AnkenTantoAccountBean {

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** アカウントSEQ */
	@Column(name = "account_seq")
	private Long accountSeq;

	/** アカウント名 */
	@Column(name = "account_name")
	private String accountName;

	/** アカウント名（姓） */
	@Column(name = "account_name_sei")
	private String accountNameSei;

	/** アカウント名（名） */
	@Column(name = "account_name_mei")
	private String accountNameMei;

	/** アカウント種別 */
	@Column(name = "account_type")
	private String accountType;

	/** 案件担当フラグ */
	@Column(name = "tanto_type")
	private String tantoType;

	/** 担当種別枝番 */
	@Column(name = "tanto_type_branch_no")
	private Long tantoTypeBranchNo;

	/** 案件主担当フラグ */
	@Column(name = "anken_main_tanto_flg")
	private String ankenMainTantoFlg;

}
