package jp.loioz.dto;

import java.time.LocalDate;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.constant.CommonConstant.AllowType;
import lombok.Data;

/**
 * 案件管理画面の顧客一覧情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenEditCustomerListDto {

	// 顧客情報

	/** 顧客ID */
	private Long customerId;

	/** 顧客名 */
	private String customerName;

	/** 電話連絡許可 */
	@Column(name = "contact_type")
	private AllowType allowTel;

	// 案件-顧客情報

	/** 初回面談日 */
	private LocalDate shokaiMendanDate;

	/** 初回面談予定SEQ */
	private Long shokaiMendanScheduleSeq;

	/** 完了日 */
	private LocalDate kanryoDate;

	/** 案件ステータス */
	private String ankenStatus;

	// 顧客連絡先

	/** 電話番号 */
	private String telNo;

	/** 優先電話番号 */
	private String yusenTelNo;

	/** メールアドレス */
	private String mailAddress;

	/** 優先メールアドレス */
	private String yusenMailAddress;

	/** 郵便番号 */
	private String zipCode;

	/** 住所1 */
	private String address1;

	/** 住所2 */
	private String address2;

}
