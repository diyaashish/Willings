package jp.loioz.bean.meiboList;

import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 名簿一覧（すべて）の筆まめCSV用Bean
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class FudemameAllMeiboListBean {

	/** レコード件数 */
	private Integer count;

	/** 名簿ID */
	private Long personId;

	/** 顧客ID */
	private Long customerId;

	/** 顧客フラグ */
	private String customerFlg;

	/** 顧問フラグ */
	private String advisorFlg;

	/** 顧客タイプ */
	private String customerType;

	/** 名前（姓） */
	private String nameSei;

	/** 名前（名） */
	private String nameMei;

	/** 名前（せい） */
	private String nameSeiKana;

	/** 名前（めい） */
	private String nameMeiKana;

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

	/** 旧姓 */
	private String oldName;

	/** 性別 */
	private String genderType;

	/** 生年月日 */
	private LocalDate birthday;

	/** 勤務先・事務所 */
	private String workPlace;

	/** 部署名 */
	private String bushoName;

	/** 備考 */
	private String remarks;

}
