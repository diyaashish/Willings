package jp.loioz.app.user.saibanManagement.form.ajax;

import lombok.Builder;
import lombok.Getter;

/**
 * 係属部
 */
@Getter
@Builder
public class SaibanManagementKeizokuBu {

	/** 係属部名 */
	private String name;

	/** 電話番号 */
	private String telNo;

	/** FAX番号 */
	private String faxNo;

}