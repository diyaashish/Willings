package jp.loioz.domain.condition.meiboList;

import java.time.LocalDate;

import jp.loioz.domain.condition.SearchCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 名簿一覧：顧客の検索条件オブジェクト
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAllMeiboListSearchCondition extends SearchCondition {

	/** キーワード検索 */
	private String searchWord;
	
	/** 名簿ID */
	private String personId;

	/** 名前検索 */
	private String keyWords;

	/** 住所 */
	private String address;

	/** 電話番号 */
	private String telNo;

	/** メールアドレス */
	private String mailAddress;

	/** 顧客登録日From */
	private LocalDate customerCreateDateFrom;

	/** 顧客登録日To */
	private LocalDate customerCreateDateTo;

	/** 特記事項 */
	private String remarks;

}
