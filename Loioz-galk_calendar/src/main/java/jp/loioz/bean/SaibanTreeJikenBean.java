package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Data
@Entity
public class SaibanTreeJikenBean {

	/** 事件ツリーSEQ */
	@Column(name = "saiban_tree_seq")
	private Long saibanTreeSeq;

	/** 裁判SEQ */
	@Column(name = "saiban_seq")
	private Long saibanSeq;

	/** 併合反訴種別 */
	@Column(name = "connect_type")
	private String connectType;

	/** 事件SEQ */
	@Column(name = "jiken_seq")
	private Long jikenSeq;

	/** 事件-元号 */
	@Column(name = "jiken_gengo")
	private String jikenGengo;

	/** 事件-年度 */
	@Column(name = "jiken_year")
	private String jikenYear;

	/** 事件-符号 */
	@Column(name = "jiken_mark")
	private String jikenMark;

	/** 事件-No */
	@Column(name = "jiken_no")
	private String jikenNo;

	/** 事件-事件名 */
	@Column(name = "jiken_name")
	private String jikenName;

}
