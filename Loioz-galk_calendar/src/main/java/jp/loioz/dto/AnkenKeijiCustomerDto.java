package jp.loioz.dto;

import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenKeijiCustomerDto {

	// ********************************
	// DBと対となる情報
	// ********************************
	// t_anken_customer
	/** 案件ID */
	private Long ankenId;

	/** 顧客ID */
	private Long customerId;

	/** 受任日 */
	private LocalDate juninDate;

	/** 事件処理完了日 */
	private LocalDate jikenKanryoDate;

	/** 精算完了日 */
	private LocalDate kanryoDate;

	/** 完了フラグ */
	private String kanryoFlg;

	// t_keiji_anken_customer
	/** 保釈保証金 */
	private String hoshakuHoshokin;
}