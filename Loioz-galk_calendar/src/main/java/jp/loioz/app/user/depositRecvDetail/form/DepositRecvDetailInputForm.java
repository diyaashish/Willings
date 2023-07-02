package jp.loioz.app.user.depositRecvDetail.form;

import jp.loioz.common.constant.AccgConstant;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 預り金明細画面の入力用フォームクラス
 */
@Data
public class DepositRecvDetailInputForm {

	/** 預り金SEQ */
	private Long depositRecvSeq;

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
	@MaxDigit(max = AccgConstant.MX_LEN_INVOICE_SUM_TEXT)
	private String sumText;

	/** メモ */
	@MaxDigit(max = AccgConstant.MX_LEN_MEMO)
	private String depositRecvMemo;

	/** 登録時間 */
	private String createdAtStr;

	/** 登録者名 */
	private String createdByName;

	/** 更新時間 */
	private String updatedAtStr;

	/** 更新者名 */
	private String updatedByName;

	/** 選択チェックボックスをチェックしていたかどうか（編集用） */
	private boolean isChecked;

	/** 紐づく請求書、精算書が発行されているかどうか */
	private boolean isIssued;

	/** 請求書、精算書が発行されていた場合に画面に表示するメッセージ */
	private String issuedMessage;
}
