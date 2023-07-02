package jp.loioz.app.common.form.userHeader;

import java.util.List;

import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.BunyaType;
import jp.loioz.domain.value.AnkenId;
import lombok.Data;

/**
 * ヘッダー検索：裁判一覧データ
 */
@Data
public class SaibanListDto {

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 案件ID */
	private AnkenId ankenId;

	/** 裁判ブランチNo */
	private Long saibanBranchNo;

	/** 分野ID */
	private Long bunyaId;

	/** 分野名 */
	private String bunyaName;

	/** 分野種別 */
	private BunyaType bunyaType;

	/** 案件種別 */
	private AnkenType AnkenType;

	/** 事件名 */
	private String jikenName;

	/** 事件番号 */
	private String caseNo;

	/** 追起訴事件番号 */
	private List<String> subCaseNo;

	/** 当事者（顧客） */
	private List<String> customerNameList;

	/** 当事者・関与者 */
	private List<String> tojishaNameList;

	/** 相手方名 */
	private List<String> aitegataNameList;

	/** その他当事者名 */
	private List<String> kyodososhoninNameList;

	/** 共犯者名 */
	private List<String> kyohanshaNameList;

}
