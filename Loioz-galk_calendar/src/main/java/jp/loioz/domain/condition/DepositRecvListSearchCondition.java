package jp.loioz.domain.condition;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 預り金管理一覧画面検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositRecvListSearchCondition extends SearchCondition {

	/** 検索ワード */
	private String searchWord;

	/** 名簿ID */
	private String personId;

	/** 名前 */
	private String personName;

	/** 分野 */
	private String bunyaId;

	/** 案件名 */
	private String ankenName;

	/** 売上計上先 */
	private Long salesOwner;

	/** 担当弁護士 */
	private Long tantoLaywer;

	/** 担当事務 */
	private Long tantoJimu;

	/** 顧客ステータス */
	private List<String> ankenStatusList;

	/** 預り金残高合計 */
	private String totalDepositBalance;
}
