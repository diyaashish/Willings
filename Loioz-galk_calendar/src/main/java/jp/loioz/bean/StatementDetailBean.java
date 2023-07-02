package jp.loioz.bean;

import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.PersonName;
import lombok.Data;

/**
 * 精算書詳細Bean
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class StatementDetailBean {

	/** 案件ID */
	private Long ankenId;

	/** 案件タイプコード */
	private String ankenType;

	/** 案件名 */
	private String ankenName;

	/** 分野ID */
	private Long bunyaId;

	/** 分野名 */
	private String bunyaName;

	/** 案件ステータス */
	private String ankenStatus;

	/** 名簿ID */
	private Long personId;

	/** 姓 */
	private String personNameSei;

	/** 名 */
	private String personNameMei;

	/** かな */
	private String personNameSeiKana;

	/** めい */
	private String personNameMeiKana;

	/** 顧客フラグ */
	private String customerFlg;

	/** 顧問フラグ */
	private String advisorFlg;

	/** 個人・法人・弁護士区分 */
	private String customerType;

	/** 精算書SEQ */
	private Long statementSeq;

	/** 売上計上先SEQ */
	private Long salesAccountSeq;

	/** 売上計上先の姓 */
	private String salesAccountNameSei;

	/** 売上計上先 せい */
	private String salesAccountNameSeiKana;

	/** 売上計上先 名 */
	private String salesAccountNameMei;

	/** 売上計上先 めい */
	private String salesAccountNameMeiKana;

	/** 発行ステータス */
	private String statementIssueStatus;

	/** 精算返金ステータス */
	private String statementRefundStatus;

	/** 実費明細添付フラグ */
	private String depositDetailAttachFlg;

	/** メモ */
	private String statementMemo;

	/** 売上日 */
	private LocalDate salesDate;

	/** 請求書タイトル */
	private String statementTitle;

	/** 日付 */
	private LocalDate statementDate;

	/** 請求番号 */
	private String statementNo;

	/** 精算先名称 */
	private String statementToName;

	/** 精算先敬称 */
	private String statementToNameEnd;

	/** 精算先詳細 */
	private String statementToDetail;

	/** 精算元事務所名 */
	private String statementFromTenantName;

	/** 精算元詳細 */
	private String statementFromDetail;

	/** 精算元印影フラグ */
	private String tenantStampPrintFlg;

	/** 挿入文 */
	private String statementSubText;

	/** 件名 */
	private String statementSubject;

	/** 返金日 */
	private LocalDate refundDate;

	/** 返却日印字フラグ */
	private String refundDatePrintFlg;

	/** 返金先 */
	private String refundBankDetail;

	/** 備考 */
	private String statementRemarks;

	/** 精算先名簿ID */
	private Long refundToPersonId;

	/** 精算先 姓 */
	private String refundToPersonNameSei;

	/** 精算先 名 */
	private String refundToPersonNameMei;

	/** 精算先 せい */
	private String refundToPersonNameSeiKana;

	/** 精算先 かな */
	private String refundToPersonNameMeiKana;

	/**
	 * 名簿者名
	 * 
	 * @return
	 */
	public PersonName getPersonName() {
		return new PersonName(
				this.personNameSei,
				this.personNameMei,
				this.personNameSeiKana,
				this.personNameMeiKana);
	}

	/**
	 * 売上計上先ユーザー名
	 * 
	 * @return
	 */
	public PersonName getSalesAccountPersonName() {
		return new PersonName(
				this.salesAccountNameSei,
				this.salesAccountNameMei,
				this.salesAccountNameSeiKana,
				this.salesAccountNameMeiKana);
	}

	/**
	 * 精算先名簿名
	 * 
	 * @return
	 */
	public PersonName getRefundPersonName() {
		return new PersonName(
				this.refundToPersonNameSei,
				this.refundToPersonNameMei,
				this.refundToPersonNameSeiKana,
				this.refundToPersonNameMeiKana);
	}

}
