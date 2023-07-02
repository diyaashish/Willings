package jp.loioz.bean.meiboList;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class CustomerKojinMeiboListBean {

	/** レコード件数 */
	private Integer count;

	/** 名簿No */
	private Long personId;

	/** 顧客ID */
	private Long customerId;

	/** 顧客フラグ */
	private String customerFlg;

	/** 顧問フラグ */
	private String advisorFlg;

	/** 名前（姓） */
	private String customerNameSei;

	/** 名前（名） */
	private String customerNameMei;

	/** 名前（せい） */
	private String customerNameSeiKana;

	/** 名前（めい） */
	private String customerNameMeiKana;

	/** 性別種別 */
	private String genderType;

	/** 生年月日 */
	private LocalDate birthday;

	/** 死亡年月日 */
	private LocalDate deathDate;

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

	/** 登録案件数 */
	private Long ankenTotalCnt;

	/** 進行中案件数 */
	private Long ankenProgressCnt;

	/** 顧客登録日 */
	private LocalDateTime customerCreatedDate;

	/** 特記事項 */
	private String remarks;
}
