package jp.loioz.app.common.excel.dto;

import java.util.Collections;
import java.util.List;

import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * 名簿一覧：法人顧客の出力用Dto
 */
@Data
public class CustomerHojinMeiboListExcelDto {

	/** 名簿一覧：法人顧客 */
	List<ExcelCustomerHojinMeiboListRowData> customerHojinMeiboListDtoList = Collections.emptyList();

	/**
	 * 名簿一覧：法人顧客の1行分のデータ
	 */
	@Data
	public static class ExcelCustomerHojinMeiboListRowData {

		/** 名簿No */
		private Long personId;

		/** 顧客フラグ */
		private String customerFlg;

		/** 顧客名 */
		private String customerName;

		/** 代表者名 */
		private String daihyoName;

		/** 代表者ポジション */
		private String daihyoPositionName;

		/** 担当者名 */
		private String tantoName;

		/** 担当者名かな */
		private String tantoNameKana;

		/** 郵便番号 */
		private String zipCode;

		/** 住所１ */
		private String address1;

		/** 住所２ */
		private String address2;

		/** 顧客登録日 */
		private String customerCreatedDate;

		/** 特記事項 */
		private String remarks;

		/** 電話番号 */
		private String telNo;

		/** FAX番号 */
		private String faxNo;

		/** メールアドレス */
		private String mailAddress;

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
