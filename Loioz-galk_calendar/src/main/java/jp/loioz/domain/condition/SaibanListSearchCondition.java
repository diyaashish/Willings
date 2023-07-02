package jp.loioz.domain.condition;

import java.util.List;

import jp.loioz.common.constant.CommonConstant.AnkenListMenu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 裁判一覧画面検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaibanListSearchCondition extends SearchCondition {

	/** 選択中のメニュー */
	private AnkenListMenu selectedAnkenListMenu;

	/** 分野 */
	private String bunyaId;

	/** 検索ワード */
	private String searchWord;

	/** 裁判手続き */
	private List<String> saibanStatusList;

	/** 担当弁護士 */
	private Long tantoLaywerId;

	/** 担当事務 */
	private Long tantoJimuId;

	/** 申立日／起訴日From */
	private String saibanStartDateFrom;

	/**申立日／起訴日To */
	private String saibanStartDateTo;

	/** 終了日／判決日From */
	private String saibanEndDateFrom;

	/** 終了日／判決日To */
	private String saibanEndDateTo;

}
