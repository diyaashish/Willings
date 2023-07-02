package jp.loioz.app.common.word.dto.naibu;

import java.util.List;

import jp.loioz.dto.SaibanTantoAccountDto;
import lombok.Data;

/**
 * 口頭弁論期日請書のデータを保持するDtoクラス
 */
@Data
public class Wn0008WordDto {

	/** 宛先1 */
	private String atesaki1;

	/** 宛先2 */
	private String atesaki2;

	/** FAX番号 */
	private String faxNo;

	/** 事務所郵便番号 */
	private String tenantZipCode;

	/** 事務所住所1 */
	private String tenantAddress1;

	/** 事務所住所2 */
	private String tenantAddress2;

	/** 事務所名称 */
	private String tenantName;

	/** 事務所電話番号 */
	private String tenantTelNo;

	/** 事務所FAX番号 */
	private String tenantFaxNo;

	/** 裁判担当（弁護士、事務） */
	private List<SaibanTantoAccountDto> saibanTantoAccountDtoList;

	/** 事件番号（1） */
	private String jikenNo1;

	/** 事件名（1） */
	private String jikenName1;

	/** 当事者 原告側 */
	private String tojishaUp;

	/** 当事者 被告側 */
	private String tojishaMid;

	/** 当事者 第三者 */
	private String tojishaLow;

	/** 裁判所 */
	private String saibansho;

	/** 当事者表記 原告側の担代理人弁護士 */
	private String tojishaDairinin;

	/** 裁判期日回数 */
	private String limitCount;

	/** 裁判期日時 */
	private String limitDate;

}
