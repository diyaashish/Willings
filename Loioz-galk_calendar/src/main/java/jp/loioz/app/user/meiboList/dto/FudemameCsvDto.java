package jp.loioz.app.user.meiboList.dto;

import lombok.Data;

/**
 * 筆まめCSV出力用Dto
 */
@Data
public class FudemameCsvDto {

	/** フリガナ */
	private String nameKana;

	/** 氏名 */
	private String name;

	/** 旧姓 */
	private String oldName;

	/** 性別 */
	private String gender;

	/** 生年月日(yyyy/MM/dd) */
	private String birthday;

	/** 郵便番号 */
	private String zipCode;

	/** 住所１ */
	private String address1;

	/** 住所２ */
	private String address2;

	/** 電話番号 */
	private String telNo;

	/** Fax番号 */
	private String faxNo;

	/** メールアドレス */
	private String mailAddress;

	/** 会社 */
	private String workPlace;

	/** 部署名 */
	private String bushoName;

	/** 分類 */
	private String bunrui;

	/** メモ */
	private String memo;

}
