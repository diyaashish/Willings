package jp.loioz.app.user.meiboList.dto;

import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * 顧客名簿の一覧表示用Dto
 */
@Data
public class CustomerAllMeiboListDto {

	/** 名簿No */
	private Long personId;

	/** 顧客フラグ */
	private String customerFlg;

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

	/** 登録案件数 */
	private Long ankenTotalCnt;

	/** 進行中案件数 */
	private Long ankenProgressCnt;

	/** 登録日 */
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
