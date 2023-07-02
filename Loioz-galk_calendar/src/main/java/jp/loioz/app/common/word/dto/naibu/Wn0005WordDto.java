package jp.loioz.app.common.word.dto.naibu;

import java.util.List;

import jp.loioz.dto.AnkenTantoAccountDto;
import lombok.Data;

/**
 * 送付書（委任契約書）のデータを保持するDtoクラス
 */
@Data
public class Wn0005WordDto {

	/** ID */
	private String id;

	/** 郵便番号 */
	private String zipCode;

	/** 住所１ */
	private String address1;

	/** 住所２ */
	private String address2;

	/** 宛先1 */
	private String atesaki1;

	/** 宛先2 */
	private String atesaki2;

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