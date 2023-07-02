package jp.loioz.app.common.excel.dto;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.kaikeiManagement.form.ExcelNyushukkinMeisaiData;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.dto.BunyaDto;
import lombok.Data;

@Data
public class NyushukkinMeisaiExcelDto {

	/** 遷移元が 顧客軸 or 案件軸 */
	private TransitionType transitonType;

	/** 表示顧客ID */
	private CustomerId customerId;

	/** 表示顧客名 */
	private String customerName;

	/** 表示案件ID */
	private AnkenId ankenId;

	/** 表示案件名 */
	private String ankenName;

	/** 表示案件分野 */
	private BunyaDto bunya;

	/** 報酬明細一覧表示用データ */
	private List<ExcelNyushukkinMeisaiData> excelNyushukkinMeisaiData = Collections.emptyList();

	/** 合計入金額 */
	private String totalNyukingaku;

	/** 合計出金額 */
	private String totalShukkingaku;

	/** 合計消費税 */
	private String totalTax;

	/** 合計源泉徴収 */
	private String totalGensen;

	/** 差引計 */
	private String sashihiki;

	/** 表示顧客 or 案件の判定 */
	public String getHeaderCellValue() {
		if (TransitionType.CUSTOMER.equals(transitonType)) {
			return String.format("%s%s%s", customerId.toString(), CommonConstant.HYPHEN, customerName);
		} else {
			return String.format("%s%s%s", ankenId.toString(), CommonConstant.HYPHEN, StringUtils.isEmpty(ankenName) ? "(案件名未入力)" : ankenName);
		}
	}
}
