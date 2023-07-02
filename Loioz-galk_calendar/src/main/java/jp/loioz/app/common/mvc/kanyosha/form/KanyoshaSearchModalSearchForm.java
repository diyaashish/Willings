package jp.loioz.app.common.mvc.kanyosha.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.domain.condition.KanyoshaModalCustomerSearchCondition;
import lombok.Data;

/**
 * 関与者検索モーダルの検索用フォームオブジェクト
 */
@Data
public class KanyoshaSearchModalSearchForm {

	/** 案件ID */
	private Long ankenId;

	// 検索除外対象の判別に利用するプロパティ
	/** 裁判SEQ */
	private Long saibanSeq;

	/** 関与者種別Cd */
	private String KanyoshaTypeCd;

	/** 代理フラグ */
	private String dairiFlg;

	// 検索条件のキーとなるプロパティ
	/** 顧客種別（関与者種別） */
	private String customerType = CustomerType.KOJIN.getCd();

	/** キーワード */
	private String keywords;

	// 表示に関連するプロパティ
	/** 顧客種別の選択不可 */
	private List<String> disabledCustomerType = Collections.emptyList();

	/**
	 * 検索条件オブジェクトを作成
	 * 
	 * @return
	 */
	public KanyoshaModalCustomerSearchCondition toKanyoshaModalCustomerSearchCondition(List<Long> excludePersonIdList) {
		return KanyoshaModalCustomerSearchCondition.builder()
				.customerType(this.customerType)
				.excludePersonIdList(excludePersonIdList)
				.keywords(this.keywords)
				.build();
	}

}
