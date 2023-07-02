package jp.loioz.app.user.saibanManagement.form.ajax;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 検察庁情報
 */
@Data
public class SaibanManagementKensatsucho {

	/** 検察庁情報 */
	private Kensatsucho kensatsucho;

	/**
	 * 検察庁情報
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Kensatsucho {

		/** 電話番号 */
		private String telNo;

		/** FAX番号 */
		private String faxNo;
	}
}