package jp.loioz.app.common.excel.dto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.kaikeiManagement.form.ExcelHoshuMeisaiData;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

@Data
public class HoshuMeisaiExcelDto {

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

	/** 報酬明細一覧表示用データ */
	private List<ExcelHoshuMeisaiData> excelHoshuMeisaiData = Collections.emptyList();

	/** 報酬額合計 */
	private BigDecimal hoshuGakuTotal;

	/** 報酬額合計 Excelではカンマは入れない */
	private String dispHoshuGakuTotal;

	/** 税額金額合計 */
	private BigDecimal taxGakuTotal;

	/** 税額合計 Excelではカンマは入れない */
	private String dispTaxGakuTotal;

	/** 源泉金額合計 */
	private BigDecimal gensenGakuTotal;

	/** 源泉金額合計 Excelではカンマは入れない */
	private String dispGensenGakuTotal;

	/** 報酬計 */
	private BigDecimal hoshuTotal;

	/** 報酬計 Excelではカンマを入れない */
	private String dispHoshuTotal;

	/** 表示顧客 or 案件の判定 */
	public String getHeaderCellValue() {
		if (TransitionType.CUSTOMER.equals(transitonType)) {
			return customerName;
		} else if (TransitionType.ANKEN.equals(transitonType)) {
			return ankenName;
		} else {
			// ここには来ないはず
			return null;
		}
	}

	/** 表示顧客 or 案件の判定(ID) */
	public String getHeaderCellValueId() {
		if (TransitionType.CUSTOMER.equals(transitonType)) {
			return customerId.toString();
		} else if (TransitionType.ANKEN.equals(transitonType)) {
			return ankenId.toString();
		} else {
			// ここには来ないはず
			return null;
		}
	}
}
