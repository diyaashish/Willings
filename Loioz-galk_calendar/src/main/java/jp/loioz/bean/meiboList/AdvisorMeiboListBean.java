package jp.loioz.bean.meiboList;

import java.time.LocalDateTime;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AdvisorMeiboListBean {

	/** レコード件数 */
	private Integer count;

	/** 名簿No */
	private Long personId;

	/** 顧客ID */
	private Long customerId;

	/** 顧客フラグ */
	private String customerFlg;

	/** 顧客タイプ */
	private String customerType;

	/** 名前（姓） */
	private String customerNameSei;

	/** 名前（名） */
	private String customerNameMei;

	/** 名前（せい） */
	private String customerNameSeiKana;

	/** 名前（めい） */
	private String customerNameMeiKana;

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

	/** 顧客登録日 */
	private LocalDateTime customerCreatedDate;

	/** 特記事項 */
	private String remarks;

	/** 顧客名（かな） */
	private String customerNameKana;
}
