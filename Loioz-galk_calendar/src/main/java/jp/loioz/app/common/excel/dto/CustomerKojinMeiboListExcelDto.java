package jp.loioz.app.common.excel.dto;

import java.util.Collections;
import java.util.List;

import jp.loioz.common.constant.CommonConstant.Gender;
import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * 名簿一覧：個人顧客の出力用Dto
 */
@Data
public class CustomerKojinMeiboListExcelDto {

	/** 名簿一覧：個人顧客 */
	List<ExcelCustomerKojinMeiboListRowData> customerKojinMeiboListDtoList = Collections.emptyList();

	/**
	 * 名簿一覧：個人顧客の1行分のデータ
	 */
	@Data
	public static class ExcelCustomerKojinMeiboListRowData {

		/** 名簿No */
		private Long personId;

		/** 顧客名 */
		private String customerName;

		/** 性別 */
		private Gender gender;

		/** 年齢 */
		private Integer age;

		/** 生年月日 */
		private String birthday;

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
		private String customerCreatedDate;

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
