package jp.loioz.app.common.excel.dto;

import java.util.Collections;
import java.util.List;

import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * 名簿一覧：弁護士の出力用Dto
 */
@Data
public class BengoshiMeiboListExcelDto {

	/** 名簿一覧：弁護士 */
	List<ExcelBengoshiMeiboListRowData> bengoshiMeiboListDtoList = Collections.emptyList();

	/**
	 * 名簿一覧；弁護士の1行分のデータ
	 */
	@Data
	public static class ExcelBengoshiMeiboListRowData {

		/** 名簿ID */
		private Long personId;

		/** 事務所名 */
		private String jimushoName;

		/** 部署・役職 */
		private String bushoYakushokuName;

		/** 名前 */
		private String bengoshiName;

		/** 名前（かな） */
		private String bengoshiNameKana;

		/** 郵便番号 */
		private String zipCode;

		/** 住所１ */
		private String address1;

		/** 住所２ */
		private String address2;

		/** 住所備考 */
		private String addressRemarks;

		/** 電話番号 */
		private String telNo;

		/** ファックス番号 */
		private String faxNo;

		/** メールアドレス */
		private String mailAddress;

		/** 登録日 */
		private String customerCreatedDate;

		/** 特記事項 */
		private String remarks;

		/**
		 * 住所が存在するか
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
