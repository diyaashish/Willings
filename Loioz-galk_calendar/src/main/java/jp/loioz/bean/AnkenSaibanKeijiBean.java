package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Data
@Entity
public class AnkenSaibanKeijiBean {

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 案件区分 */
	@Column(name = "anken_type")
	private String ankenType;

	/** 分野 */
	@Column(name = "bunya_id")
	private Long bunyaId;

	/** 私選・国選区分 */
	@Column(name = "lawyer_select_type")
	private String lawyerSelectType;

}
