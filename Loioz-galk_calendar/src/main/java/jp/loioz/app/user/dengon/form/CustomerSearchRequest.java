package jp.loioz.app.user.dengon.form;

import java.util.List;

import lombok.Data;

@Data
public class CustomerSearchRequest {

	/** 検索文字列 */
	private String searchText;

	/** 除外顧客ID */
	private List<Long> exclusionCustomerIdList;

	/** AjaxRequestの識別用ID */
	private String uuid;

}
