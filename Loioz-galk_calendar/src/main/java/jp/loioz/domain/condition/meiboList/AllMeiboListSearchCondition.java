package jp.loioz.domain.condition.meiboList;

import java.time.LocalDate;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.PersonAttributeCd;
import jp.loioz.domain.condition.SearchCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 名簿一覧：すべての検索条件オブジェクト
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllMeiboListSearchCondition extends SearchCondition {

	/** キーワード検索 */
	private String searchWord;
	
	/** 名簿ID */
	private String personId;

	/** 名前検索 */
	private String keyWords;

	/** 区分（名簿種別） */
	private CustomerType customerType;

	/** 名簿属性 */
	private PersonAttributeCd personAttributeCd;

	/** 住所 */
	private String address;

	/** 電話番号 */
	private String telNo;

	/** メールアドレス */
	private String mailAddress;
	
	/** 登録日From */
	private LocalDate customerCreateDateFrom;

	/** 登録日To */
	private LocalDate customerCreateDateTo;

}
