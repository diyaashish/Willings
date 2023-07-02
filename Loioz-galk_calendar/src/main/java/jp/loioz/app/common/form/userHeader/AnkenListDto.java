package jp.loioz.app.common.form.userHeader;

import java.util.List;

import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.domain.value.AnkenId;
import lombok.Data;

/**
 * ヘッダー検索：案件一覧データ
 */
@Data
public class AnkenListDto {

	/** 案件ID */
	private AnkenId ankenId;

	/** 分野ID */
	private Long bunyaId;

	/** 分野名 */
	private String bunyaName;

	/** 案件名 */
	private String ankenName;

	/** 案件種別 */
	private AnkenType ankenType;

	/** 顧客名 */
	private List<String> customerNameList;

	/** 相手方 */
	private List<String> aitegataNameList;

	/** 被害者 */
	private List<String> higaishaNameList;

}
