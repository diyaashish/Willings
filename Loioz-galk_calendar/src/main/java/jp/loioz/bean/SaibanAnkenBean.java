package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Data
@Entity
public class SaibanAnkenBean {

	/** 案件名 */
	@Column(name = "saiban_seq")
	private Long saibanSeq;

	/** 分野 */
	@Column(name = "bunya_id")
	private Long bunyaId;

}
