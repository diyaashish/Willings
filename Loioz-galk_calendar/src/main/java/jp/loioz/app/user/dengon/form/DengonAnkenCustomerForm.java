package jp.loioz.app.user.dengon.form;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 * 伝言編集画面のフォームクラス
 */
@Data
public class DengonAnkenCustomerForm {

	/** 顧客ID */
	CustomerId customerId;

	/** 顧客名 */
	String customerName;

	/** 案件ID */
	AnkenId ankenId;

	/** 案件名 */
	String ankenName;

	/** 分野 */
	Long bunyaId;

}