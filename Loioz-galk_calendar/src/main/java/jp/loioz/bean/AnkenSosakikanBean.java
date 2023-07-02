package jp.loioz.bean;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Data
@Entity
public class AnkenSosakikanBean {

	/** 捜査機関SEQ */
	@Column(name = "sosakikan_seq")
	private Long sosakikanSeq;

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 捜査機関ID */
	@Column(name = "sosakikan_id")
	private Long sosakikanId;

	/** 捜査機関名 */
	@Column(name = "sosakikan_name")
	private String sosakikanName;

	/** 担当部 */
	@Column(name = "sosakikan_tanto_bu")
	private String sosakikanTantoBu;

	/** 案件ID */
	@Column(name = "sosakikan_tel_no")
	private String sosakikanTelNo;

	/** 案件ID */
	@Column(name = "sosakikan_extension_no")
	private String sosakikanExtensionNo;

	/** 案件ID */
	@Column(name = "sosakikan_fax_no")
	private String sosakikanFaxNo;

	/** 案件ID */
	@Column(name = "sosakikan_room_no")
	private String sosakikanRoomNo;

	/** 案件ID */
	@Column(name = "tantosha1_name")
	private String tantosha1Name;

	/** 案件ID */
	@Column(name = "tantosha2_name")
	private String tantosha2Name;

	/** 案件ID */
	@Column(name = "remarks")
	private String remarks;

}
