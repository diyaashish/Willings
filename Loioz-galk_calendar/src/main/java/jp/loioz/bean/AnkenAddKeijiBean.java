package jp.loioz.bean;

import java.time.LocalDate;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Data
@Entity
public class AnkenAddKeijiBean {

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 分野ID */
	@Column(name = "bunya_id")
	private Long bunyaId;

	/** 案件登録日 */
	@Column(name = "anken_created_date")
	private LocalDate ankenCreatedDate;

	/** 案件区分 */
	@Column(name = "anken_type")
	private String ankenType;

	/** 事案概要・方針 */
	@Column(name = "jian_summary")
	private String jianSummary;

	/** 私選・国選区分 */
	@Column(name = "lawyer_select_type")
	private String lawyerSelectType;

}
