package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenSosakikanDto {

	// ********************************
	// DBと対となる情報
	// ********************************

	// t_anken_sosakikan
	/** 捜査機関SEQ */
	private Long sosakikanSeq;

	/** 捜査機関名 */
	private String sosakikanName;

	/** 担当部 */
	private String sosakikanTantoBu;

	/** 電話番号 */
	private String sosakikanTelNo;

	/** 内線番号 */
	private String sosakikanExtensionNo;

	/** FAX番号 */
	private String sosakikanFaxNo;

	/** 号室 */
	private String sosakikanRoomNo;

	/** 担当者①氏名 */
	private String tantosha1Name;

	/** 担当者①ふりがな */
	private String tantosha1NameKana;

	/** 担当者②氏名 */
	private String tantosha2Name;

	/** 担当者②ふりがな */
	private String tantosha2NameKana;

	/** 備考 */
	private String remarks;

	// m_sosakikan
	/** 施設ID */
	private Long sosakikanId;

	/** 捜査機関郵便番号 */
	private String sosakikanZip;

	/** 捜査機関住所1 */
	private String sosakikanAddress1;

	/** 捜査機関住所2 */
	private String sosakikanAddress2;

}