package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Data
@Entity
public class AnkenBean {

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 分野ID */
	@Column(name = "bunya_id")
	private Long bunyaId;

	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 案件種別 */
	@Column(name = "anken_type")
	private String ankenType;

	/** 案件ステータス */
	@Column(name = "anken_status")
	private String ankenStatus;

	/** ソート番号 */
	@Column(name = "orderby_column")
	private String orderbyColumn;
}
