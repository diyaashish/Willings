package jp.loioz.bean;

import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class InvoiceDetailBean {

	/** 案件ID */
	private Long ankenId;

	/** 分野ID */
	private Long bunyaId;

	/** 分野名 */
	private String bunyaName;

	/** 案件タイプコード */
	private String ankenType;

	/** 案件名 */
	private String ankenName;

	/** 案件ステータス */
	private String ankenStatus;

	/** 名簿ID */
	private Long personId;

	/** 姓 */
	private String personNameSei;

	/** 名 */
	private String personNameMei;

	/** 顧客フラグ */
	private String customerFlg;

	/** 顧問フラグ */
	private String advisorFlg;

	/** 個人・法人・弁護士区分 */
	private String customerType;

	/** 発行ステータスコード */
	private String invoiceIssueStatus;

	/** 売上計上先SEQ */
	private Long salesAccountSeq;

	/** 売上計上先の姓 */
	private String salesAccountNameSei;

	/** 売上計上先の名 */
	private String salesAccountNameMei;

	/** 実費明細添付フラグ */
	private String depositDetailAttachFlg;

	/** 支払計画添付フラグ */
	private String paymentPlanAttachFlg;

	/** 請求方法 */
	private String invoiceType;

	/** メモ */
	private String invoiceMemo;

	/** 売上日 */
	private LocalDate salesDate;

	/** 請求書タイトル */
	private String invoiceTitle;

	/** 日付 */
	private LocalDate invoiceDate;

	/** 請求番号 */
	private String invoiceNo;

	/** 請求先名称 */
	private String invoiceToName;

	/** 請求先敬称 */
	private String invoiceToNameEnd;

	/** 請求先詳細 */
	private String invoiceToDetail;

	/** 請求元事務所名 */
	private String invoiceFromTenantName;

	/** 請求元詳細 */
	private String invoiceFromDetail;

	/** 印影フラグ */
	private String tenantStampPrintFlg;

	/** 挿入文 */
	private String invoiceSubText;

	/** 件名 */
	private String invoiceSubject;

	/** 支払期限 */
	private LocalDate dueDate;

	/** 支払期限印字フラグ */
	private String dueDatePrintFlg;

	/** 振込先 */
	private String tenantBankDetail;

	/** 備考 */
	private String invoiceRemarks;

	/** 請求先名簿ID */
	private Long billToPersonId;

	/** 請求先 姓 */
	private String billToPersonNameSei;

	/** 請求先 名 */
	private String billToPersonNameMei;
}
