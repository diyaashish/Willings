package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 案件管理（民事・刑事弁護）画面：裁判情報（顧客部分）、<br>
 * 裁判管理（刑事弁護）画面：裁判一覧（顧客部分）用のDto
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class SaibanCustomerListDto {

	// -------------------------------------
	// t_saiban_customer
	// -------------------------------------
	/** 裁判SEQ */
	private Long saibanSeq;

	/** 筆頭フラグ */
	private String mainFlg;

	// -------------------------------------
	// t_person
	// -------------------------------------
	/** 顧客ID */
	private Long customerId;

	/** 顧客名 */
	private String customerName;
}
