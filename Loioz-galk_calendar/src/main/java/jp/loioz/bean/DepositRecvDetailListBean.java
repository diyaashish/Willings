package jp.loioz.bean;

import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class DepositRecvDetailListBean {

	/** 預り金SEQ */
	private Long depositRecvSeq;

	/** 名簿ID */
	private Long personId;

	/** 案件ID */
	private Long ankenId;

	/** 項目 */
	private String depositItemName;

	/** 発生日 */
	private LocalDate depositDate;

	/** 入出金完了フラグ */
	private String depositCompleteFlg;

	/** 入出金タイプ */
	private String depositType;

	/** 作成タイプ */
	private String createdType;

	/** 実費入金フラグ */
	private String expenseInvoiceFlg;

	/** 入金額 */
	private String depositAmount;

	/** 出金額 */
	private String withdrawalAmount;

	/** 使用先会計書類SEQ */
	private Long usingAccgDocSeq;

	/** 使用先請求書SEQ */
	private Long usingInvoiceSeq; 

	/** 使用先請求書番号 */
	private String usingInvoiceNo;

	/** 使用先請求書入金ステータス */
	private String usingInvoicePaymentStatus;

	/** 使用先精算書SEQ */
	private Long usingStatementSeq;

	/** 使用先精算書番号 */
	private String usingStatementNo;

	/** 使用先精算書返金ステータス */
	private String usingStatementRefundStatus;

	/** 作成元会計書類SEQ */
	private Long createdAccgDocSeq;

	/** 作成元請求書SEQ */
	private Long createdInvoiceSeq; 

	/** 作成元請求書番号 */
	private String createdInvoiceNo;

	/** 作成元請求書入金ステータス */
	private String createdInvoicePaymentStatus;

	/** 作成元精算書SEQ */
	private Long createdStatementSeq;

	/** 作成元精算書番号 */
	private String createdStatementNo;

	/** 作成元精算書返金ステータス */
	private String createdStatementRefundStatus;

	/** 事務所負担フラグ */
	private String tenantBearFlg;

	/** 摘要 */
	private String sumText;

	/** メモ */
	private String depositRecvMemo;

	/** 回収不能フラグ */
	private String uncollectibleFlg;
}
