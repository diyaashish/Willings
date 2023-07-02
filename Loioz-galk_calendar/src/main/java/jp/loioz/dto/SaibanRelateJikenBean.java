package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class SaibanRelateJikenBean {

	/** 裁判SEQ */
	@Column(name = "saiban_seq")
	private Long saibanSeq;

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 裁判枝番 */
	@Column(name = "saiban_branch_no")
	private Long saibanBranchNo;

	/** 事件番号元号 */
	@Column(name = "jiken_gengo")
	private String jikenGengo;

	/** 事件番号年 */
	@Column(name = "jiken_year")
	private String jikenYear;

	/** 事件番号符号 */
	@Column(name = "jiken_mark")
	private String jikenMark;

	/** 事件番号 */
	@Column(name = "jiken_no")
	private String jikenNo;

	/** 事件名 */
	@Column(name = "jiken_name")
	private String jikenName;

}
