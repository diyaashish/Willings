package jp.loioz.app.common.form.accg;

import java.util.List;

import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.domain.value.PersonAttribute;
import lombok.Builder;
import lombok.Data;

/**
 * 「案件の会計管理」複数顧客の場合の選択用の表示フォーム
 */
@Data
public class AccgCasePersonForm {

	/** 案件ID */
	private Long ankenId;

	/** 名簿ID */
	private Long personId;

	/** 複数顧客の存在するか */
	private boolean isClients;

	/** 案件の顧客情報 */
	private List<Customer> relatedCustomer;

	/**
	 * 案件に紐づく顧客情報
	 *
	 */
	@Data
	@Builder
	public static class Customer {
		/** 名簿ID */
		private Long personId;

		/** 名簿名 */
		private String personName;

		/** 案件ステータス */
		private AnkenStatus ankenStatus;

		/** 顧客種別 */
		private CustomerType customerType;

		/** 属性 */
		private PersonAttribute personAttribute;
	}

}
