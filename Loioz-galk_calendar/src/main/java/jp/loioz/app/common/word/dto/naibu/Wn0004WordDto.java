package jp.loioz.app.common.word.dto.naibu;

import java.util.List;

import jp.loioz.dto.AnkenTantoAccountDto;
import lombok.Data;

/**
 * 送付書（FAX用）のデータを保持するDtoクラス
 */
@Data
public class Wn0004WordDto {

	/** ID */
	private String id;

	/** 宛先1 */
	private String atesaki1;

	/** 宛先2 */
	private String atesaki2;

	/** FAX番号 */
	private String faxNo;

	/** 作成日 */
	private String createdAt;

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

	/** 案件担当（弁護士、事務） */
	private List<AnkenTantoAccountDto> ankenTantoAccountDtoList;

}