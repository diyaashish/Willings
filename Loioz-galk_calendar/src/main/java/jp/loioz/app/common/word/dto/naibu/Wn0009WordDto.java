package jp.loioz.app.common.word.dto.naibu;

import java.util.List;

import jp.loioz.dto.SaibanTantoAccountDto;
import lombok.Data;

/**
 * 公判期日請書のデータを保持するDtoクラス
 */
@Data
public class Wn0009WordDto {

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

	/** 当事者 被告 */
	private String hikoku;

	/** 裁判担当（弁護士、事務） */
	private List<SaibanTantoAccountDto> saibanTantoAccountDtoList;

	/** 事件番号 */
	private String jikenNo1;

	/** 事件名 */
	private String jikenName1;

	/** 被告人 (加工済データを格納すること) */
	private String hikokunin;

	/** 裁判所 */
	private String saibansho;

	/** 裁判所 係属部 */
	private String keizokuBu;

	/** 裁判所 係属係 */
	private String keizokuKakari;

	/** 弁護人 */
	private String bengonin;

	/** 公判回数 */
	private String limitCount;

	/** 公判日時 */
	private String limitDate;

}
