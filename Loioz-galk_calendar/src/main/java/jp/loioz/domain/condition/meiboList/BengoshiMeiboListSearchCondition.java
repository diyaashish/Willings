package jp.loioz.domain.condition.meiboList;

import java.time.LocalDate;

import jp.loioz.domain.condition.SearchCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 名簿：弁護士名簿の検索条件オブジェクト
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BengoshiMeiboListSearchCondition extends SearchCondition {

	/** キーワード検索 */
	private String searchWord;
	
	/** 名簿ID */
	private String personId;

	/** 名前検索 */
	private String keyWords;

	/** 事務所名 */
	private String jimushoName;

	/** 住所 */
	private String address;

	/** 電話番号 */
	private String telNo;

	/** メールアドレス */
	private String mailAddress;

	/** 登録日From */
	private LocalDate createDateFrom;

	/** 登録日To */
	private LocalDate createDateTo;

	/** 特記事項 */
	private String remarks;

}
