package jp.loioz.domain.condition;

import java.util.List;

import jp.loioz.common.constant.CommonConstant.AnkenListMenu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 案件一覧画面検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnkenListSearchCondition extends SearchCondition {

	/** 選択中のメニュー */
	private AnkenListMenu selectedAnkenListMenu;

	/** 案件ステータスリスト */
	private List<String> ankenStatusList;

	/** 検索ワード */
	private String searchWord;

	/** 分野ID */
	private String bunyaId;

	/** 担当弁護士 */
	private Long tantoLaywer;

	/** 担当事務 */
	private Long tantoJimu;

	/** 案件登録日From */
	private String ankenCreateDateFrom;

	/** 案件登録日To */
	private String ankenCreateDateTo;

	/** 顧客登録日From */
	private String customerCreateDateFrom;

	/** 顧客登録日To */
	private String customerCreateDateTo;

	/** 受任日From */
	private String jyuninDateFrom;

	/** 受任日To */
	private String jyuninDateTo;
}
