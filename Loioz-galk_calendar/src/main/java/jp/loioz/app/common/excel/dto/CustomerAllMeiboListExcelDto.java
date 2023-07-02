package jp.loioz.app.common.excel.dto;

import java.util.Collections;
import java.util.List;

import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * 名簿一覧：顧客の出力用Dto
 */
@Data
public class CustomerAllMeiboListExcelDto {

	/** 名簿一覧：顧客 */
	List<ExcelCustomerAllMeiboListRowData> customerAllMeiboListDtoList = Collections.emptyList();

	/**
	 * 名簿一覧：顧客の1行分のデータ
	 */
	@Data
	public static class ExcelCustomerAllMeiboListRowData {

		/** 名簿No */
		private Long personId;

		/** 顧客タイプ */
		private String customerType;

		/** 名前 */
		private String customerName;

		/** 郵便番号 */
		private String zipCode;

		/** 住所１ */
		private String address1;

		/** 住所２ */
		private String address2;

		/** 電話番号 */
		private String telNo;

		/** FAX番号 */
		private String faxNo;

		/** メールアドレス */
		private String mailAddress;

		/** 顧客登録日 */
		private String customerCreateDate;

		/** 特記事項 */
		private String remarks;

		/**
		 * 住所が存在するかどうか
		 * 
		 * @return
		 */
		public boolean existsAddress() {
			if (StringUtils.isEmpty(address1) && StringUtils.isEmpty(address2)) {
				return false;
			}
			return true;
		}
	}
}
