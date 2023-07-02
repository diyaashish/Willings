package jp.loioz.app.common.mvc.accgDepositRecvSelect.form;

import java.util.List;

import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 預り金の入力用フォームクラス
 */
@Data
public class AccgInvoiceStatementDepositRecvSelectInputForm {

	/** 預り金SEQ */
	private List<Long> depositRecvSeqList;

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	/** 項目種別 */
	@Required
	private String depositType;

	/** 項目 */
	@Required
	private String depositItemName;

	/** 発生日 */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String depositDate;

	/** 金額 */
	@Required
	@Numeric
	@MaxNumericValue(max = 999999999)
	@MinNumericValue(min = -99999999)
	private String amountOfMoney;

	/** 事務所負担フラグ */
	private boolean tenantBearFlg;

	/** 摘要 */
	@MaxDigit(max = 10000)
	private String sumText;

	/** メモ */
	@MaxDigit(max = 10000)
	private String depositRecvMemo;

}
