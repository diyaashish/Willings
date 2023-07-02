package jp.loioz.app.common.word.dto.naibu;

import java.util.List;

import jp.loioz.dto.AnkenTantoAccountDto;
import jp.loioz.dto.AzukariItemListDto;
import jp.loioz.dto.AzukariItemUserDto;
import lombok.Data;

/**
 * 預り証のデータを保持するDtoクラス
 */
@Data
public class Wn0002WordDto {

	/** 案件ID */
	private String ankenId;

	/** 作成日 */
	private String createdAt;

	/** 宛先 */
	private String atesaki;

	/** 預かり証出力時のファイル名に用いるあて先名 */
	private String name;

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

	/** 品目 */
	private List<AzukariItemListDto> hinmokuList;

	/** 預かり証の宛先リスト */
	private List<AzukariItemUserDto> azukariUserList;

	/** 案件担当（弁護士） */
	private List<AnkenTantoAccountDto> ankenTantoAccountDtoList;

}
