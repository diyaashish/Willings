package jp.loioz.app.user.feeDetail.form;

import java.util.List;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.constant.AccgConstant;
import jp.loioz.common.constant.CommonConstant.GensenChoshu;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.groups.FeeRegist;
import jp.loioz.common.validation.groups.TimeChargeCalculate;
import jp.loioz.common.validation.groups.TimeChargeRegist;
import lombok.Data;

/**
 * 報酬明細画面の入力用フォームクラス
 */
@Data
public class FeeDetailInputForm {

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	/** 項目 */
	@Required(groups = {FeeRegist.class, TimeChargeRegist.class})
	private String feeItemName;

	/** 発生日 */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED, groups = {FeeRegist.class, TimeChargeRegist.class})
	private String feeDate;

	/** 入金ステータス */
	private String feePaymentStatus;

	/** 報酬額 */
	@Required(groups = {FeeRegist.class, TimeChargeRegist.class})
	@Numeric(groups = {FeeRegist.class, TimeChargeRegist.class})
	@MaxNumericValue(max = 999999999, groups = {FeeRegist.class, TimeChargeRegist.class})
	@MinNumericValue(min = 1, groups = {FeeRegist.class, TimeChargeRegist.class})
	private String feeAmount;

	/** 課税フラグ */
	private String taxFlg;

	/** 消費税率 */
	private String taxRateType;

	/** 源泉徴収フラグ */
	@EnumType(value = GensenChoshu.class)
	private String withholdingFlg;

	/** タイムチャージフラグ */
	private boolean feeTimeChargeFlg;

	/** タイムチャージ単価 */
	@Required(groups = {TimeChargeCalculate.class, TimeChargeRegist.class})
	@Numeric(groups = {TimeChargeCalculate.class, TimeChargeRegist.class})
	@MaxNumericValue(max = 999999999, groups = {TimeChargeCalculate.class, TimeChargeRegist.class})
	@MinNumericValue(min = 1, groups = {TimeChargeCalculate.class, TimeChargeRegist.class})
	private String timeChargeTanka;

	/** タイムチャージ時間 */
	@Required(groups = {TimeChargeCalculate.class, TimeChargeRegist.class})
	@Numeric(groups = {TimeChargeCalculate.class, TimeChargeRegist.class})
	@MaxNumericValue(max = 99999, groups = {TimeChargeCalculate.class, TimeChargeRegist.class})
	@MinNumericValue(min = 1, groups = {TimeChargeCalculate.class, TimeChargeRegist.class})
	private String timeChargeTime;

	/** 摘要 */
	@MaxDigit(max = AccgConstant.MX_LEN_INVOICE_SUM_TEXT, groups = {FeeRegist.class, TimeChargeRegist.class})
	private String sumText;

	/** メモ */
	@MaxDigit(max = AccgConstant.MX_LEN_MEMO, groups = {FeeRegist.class, TimeChargeRegist.class})
	private String feeMemo;

	/** 報酬SEQ */
	private Long feeSeq;

	/** 消費税額 */
	private String taxAmount;

	/** 源泉徴収額 */
	private String withholdingAmount;

	/** 報酬入金ステータスのプルダウン情報 */
	private List<SelectOptionForm> feePaymentStatusList;

	/** 選択チェックボックスをチェックしていたかどうか（編集用） */
	private boolean isChecked;

	/** 請求書番号（編集用） */
	private String invoiceNo;

	/** 精算書番号（編集用） */
	private String statementNo;

	/**
	 * 請求書、精算書を作成しているか
	 * 
	 * @return
	 */
	public boolean createdInvoiceStatement() {
		if (StringUtils.isAllEmpty(this.invoiceNo, this.statementNo)) {
			return false;
		}
		return true;
	}

	/** 登録時間 */
	private String createdAtStr;

	/** 登録者名 */
	private String createdByName;

	/** 更新時間 */
	private String updatedAtStr;

	/** 更新者名 */
	private String updatedByName;

	/** 紐づく請求書、精算書が発行されているかどうか */
	private boolean isIssued;

	/** 紐づく請求書、精算書が発行されている場合に画面に表示するメッセージ */
	private String issuedMessage;
}
