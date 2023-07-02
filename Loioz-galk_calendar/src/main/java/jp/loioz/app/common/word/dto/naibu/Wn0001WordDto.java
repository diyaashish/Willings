package jp.loioz.app.common.word.dto.naibu;

import java.util.List;

import jp.loioz.dto.AzukariItemListDto;
import lombok.Data;

/**
 * 受領証のデータを保持するDtoクラス
 */
@Data
public class Wn0001WordDto {

	/** 案件ID */
	private String ankenId;

	/** 弁護士 */
	private List<String> lawyerNameList;

	/** 作成日 */
	private String createdAt;

	/** 住所１ */
	private String address1;

	/** 住所２ */
	private String address2;

	/** 氏名 */
	private String tenantName;

	/** 品目 */
	private List<AzukariItemListDto> hinmoku;

}
