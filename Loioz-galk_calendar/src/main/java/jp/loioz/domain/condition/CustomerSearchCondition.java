package jp.loioz.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 顧客情報の検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSearchCondition extends SearchCondition {

	// -----------------------------------------------
	// 顧客情報
	// -----------------------------------------------
	/** 顧客ID */
	private Long customerId;

	/** 顧客名 */
	private String customerName;

	/** 顧客名かな */
	private String customerNameKana;

	/** 遷移元顧客ID */
	private Long transitionCustomerId;

	/** 顧客の特記事項 */
	private String customerRemarks;

	/** 区分：依頼者 */
	private boolean isIraisha;

	/** 区分：相手方 */
	private boolean isAitegata;

	/** 区分：関与者 */
	private boolean isKanyosha;

	// -----------------------------------------------
	// 顧客連絡先
	// -----------------------------------------------
	/** 連絡先 */
	private CustomerContactSearchCondition contact;

	public CustomerContactSearchCondition getContact() {
		if (this.contact == null) {
			this.contact = new CustomerContactSearchCondition();
		}
		return this.contact;
	}

	/**
	 * 顧客連絡先の検索条件
	 */
	@Data
	@EqualsAndHashCode(callSuper = true)
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CustomerContactSearchCondition extends SearchCondition {

		/** 電話番号 */
		private String telNo;

		/** FAX番号 */
		private String faxNo;

		/** メールアドレス */
		private String mailAddress;
	}
}
