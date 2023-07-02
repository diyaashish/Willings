package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

/**
 * 顧問契約の担当情報Bean
 */
@Data
@Entity
public class AdvisorContractTantoInfoBean {

	/** 顧問契約SEQ */
	@Column(name = "advisor_contract_seq")
	private Long advisorContractSeq;
	
	/** 担当タイプ */
	@Column(name = "tanto_type")
	private String tantoType;
	
	/** 担当タイプ枝番 */
	@Column(name = "tanto_type_branch_no")
	private String tantoTypeBranchNo;

	/** メイン担当フラグ */
	@Column(name = "main_tanto_flg")
	private String mainTantoFlg;

	/** 担当者名 */
	@Column(name = "tanto_name")
	private String tantoName;

}
