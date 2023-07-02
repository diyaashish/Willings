package jp.loioz.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 関与者情報の検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanyoshaSearchCondition extends SearchCondition {

	/** 氏名 */
	private String kanyoshaName;

	/** ふりがな */
	private String kanyoshaNameKana;

	/** 電話番号 */
	private String telNo;

	/** FAX番号 */
	private String faxNo;

	/** メールアドレス */
	private String mailAddress;
}
