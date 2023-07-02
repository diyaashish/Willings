package jp.loioz.dto;

import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenKoryuDto {

	// ********************************
	// DBと対となる情報
	// ********************************
	// t_anken_koryu
	/** 勾留SEQ */
	private Long koryuSeq;

	/** 勾留日 */
	private LocalDate koryuDate;

	/** 保釈日 */
	private LocalDate hoshakuDate;

	/** 勾留場所名 */
	private String koryuPlaceName;

	/** 備考 */
	private String remarks;

	// m_sosakikan
	/** 捜査機関ID */
	private Long sosakikanId;

	/** 捜査機関電話番号 */
	private String sosakikanTelNo;
}