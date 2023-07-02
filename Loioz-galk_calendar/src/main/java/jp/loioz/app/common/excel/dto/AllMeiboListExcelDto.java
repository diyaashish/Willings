package jp.loioz.app.common.excel.dto;

import java.util.Collections;
import java.util.List;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.domain.value.PersonAttribute;
import lombok.Data;

/**
 * 名簿一覧：すべての出力用Dto
 */
@Data
public class AllMeiboListExcelDto {

	/** 名簿一覧：すべて */
	List<ExcelAllMeiboListRowData> allMeiboListDtoList = Collections.emptyList();

	/**
	 * 名簿一覧：すべての1行分のデータ
	 */
	@Data
	public static class ExcelAllMeiboListRowData {

		/** 名簿ID */
		private Long personId;

		/** 顧客フラグ */
		private String customerFlg;

		/** 名簿属性 */
		private PersonAttribute personAttribute;

		/** 案件顧客として登録されているかどうか */
		private boolean existsAnkenCustomer;

		/** 関与者として登録されているかどうか */
		private boolean existsKanyosha;

		/** 顧客区分 */
		private CustomerType customerType;

		/** 名前 */
		private String name;

		/** 郵便番号 */
		private String zipCode;

		/** 住所１ */
		private String address1;

		/** 住所２ */
		private String address2;

		/** 電話番号 */
		private String telNo;

		/** メールアドレス */
		private String mailAddress;

		/** 顧客登録日 */
		private String customerCreatedDate;

	}
}
