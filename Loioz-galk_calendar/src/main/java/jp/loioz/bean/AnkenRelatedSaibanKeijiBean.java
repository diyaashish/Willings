package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class AnkenRelatedSaibanKeijiBean {

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 分野ID */
	@Column(name = "bunya_id")
	private Long bunyaId;

	/** 裁判SEQ */
	@Column(name = "saiban_seq")
	private Long saibanSeq;

	/** 事件SEQ */
	@Column(name = "jiken_seq")
	private Long jikenSeq;

	/** 事件名 */
	@Column(name = "jiken_name")
	private String jikenName;

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

}
