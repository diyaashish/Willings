package jp.loioz.entity;

import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import jp.loioz.entity.common.DefaultEntity;

/**
 * 精算書設定
 */
@Entity(listener = MStatementSettingEntityListener.class)
@Table(name = "m_statement_setting")
public class MStatementSettingEntity extends DefaultEntity {

	/** 精算書設定SEQ */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "statement_setting_seq")
	Long statementSettingSeq;

	/** タイトル */
	@Column(name = "default_title")
	String defaultTitle;

	/** 挿入文 */
	@Column(name = "default_sub_text")
	String defaultSubText;

	/** 件名-プレフィックス */
	@Column(name = "subject_prefix")
	String subjectPrefix;

	/** 件名-サフィックス */
	@Column(name = "subject_suffix")
	String subjectSuffix;

	/** 精算番号-接頭辞 */
	@Column(name = "statement_no_prefix")
	String statementNoPrefix;

	/** 精算番号-年フォーマット */
	@Column(name = "statement_no_y_fmt")
	String statementNoYFmt;

	/** 精算番号-月フォーマット */
	@Column(name = "statement_no_m_fmt")
	String statementNoMFmt;

	/** 精算番号-日フォーマット */
	@Column(name = "statement_no_d_fmt")
	String statementNoDFmt;

	/** 精算番号-区切り文字 */
	@Column(name = "statement_no_delimiter")
	String statementNoDelimiter;

	/** 精算番号-連番タイプ */
	@Column(name = "statement_no_numbering_type")
	String statementNoNumberingType;

	/** 精算番号-連番ゼロ埋めフラグ */
	@Column(name = "statement_no_zero_pad_flg")
	String statementNoZeroPadFlg;

	/** 精算番号-連番ゼロ埋め桁数 */
	@Column(name = "statement_no_zero_pad_digits")
	String statementNoZeroPadDigits;

	/** 取引日表示フラグ */
	@Column(name = "transaction_date_print_flg")
	String transactionDatePrintFlg;

	/** 返金期日表示フラグ */
	@Column(name = "refund_date_print_flg")
	String refundDatePrintFlg;

	/** 事務所印表示フラグ */
	@Column(name = "tenant_stamp_print_flg")
	String tenantStampPrintFlg;

	/** 備考 */
	@Column(name = "default_remarks")
	String defaultRemarks;

	/** 登録日時 */
	@Column(name = "created_at")
	LocalDateTime createdAt;

	/** 登録アカウントID */
	@Column(name = "created_by")
	Long createdBy;

	/** 更新日時 */
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

	/** 更新アカウントID */
	@Column(name = "updated_by")
	Long updatedBy;

	/** バージョンNo */
	@Version
	@Column(name = "version_no")
	Long versionNo;

	/**
	 * Returns the statementSettingSeq.
	 * 
	 * @return the statementSettingSeq
	 */
	public Long getStatementSettingSeq() {
		return statementSettingSeq;
	}

	/**
	 * Sets the statementSettingSeq.
	 * 
	 * @param statementSettingSeq the statementSettingSeq
	 */
	public void setStatementSettingSeq(Long statementSettingSeq) {
		this.statementSettingSeq = statementSettingSeq;
	}

	/**
	 * Returns the defaultTitle.
	 * 
	 * @return the defaultTitle
	 */
	public String getDefaultTitle() {
		return defaultTitle;
	}

	/**
	 * Sets the defaultTitle.
	 * 
	 * @param defaultTitle the defaultTitle
	 */
	public void setDefaultTitle(String defaultTitle) {
		this.defaultTitle = defaultTitle;
	}

	/**
	 * Returns the defaultSubText.
	 * 
	 * @return the defaultSubText
	 */
	public String getDefaultSubText() {
		return defaultSubText;
	}

	/**
	 * Sets the defaultSubText.
	 * 
	 * @param defaultSubText the defaultSubText
	 */
	public void setDefaultSubText(String defaultSubText) {
		this.defaultSubText = defaultSubText;
	}

	/**
	 * Returns the subjectPrefix.
	 * 
	 * @return the subjectPrefix
	 */
	public String getSubjectPrefix() {
		return subjectPrefix;
	}

	/**
	 * Sets the subjectPrefix.
	 * 
	 * @param subjectPrefix the subjectPrefix
	 */
	public void setSubjectPrefix(String subjectPrefix) {
		this.subjectPrefix = subjectPrefix;
	}

	/**
	 * Returns the subjectSuffix.
	 * 
	 * @return the subjectSuffix
	 */
	public String getSubjectSuffix() {
		return subjectSuffix;
	}

	/**
	 * Sets the subjectSuffix.
	 * 
	 * @param subjectSuffix the subjectSuffix
	 */
	public void setSubjectSuffix(String subjectSuffix) {
		this.subjectSuffix = subjectSuffix;
	}

	/**
	 * Returns the statementNoPrefix.
	 * 
	 * @return the statementNoPrefix
	 */
	public String getStatementNoPrefix() {
		return statementNoPrefix;
	}

	/**
	 * Sets the statementNoPrefix.
	 * 
	 * @param statementNoPrefix the statementNoPrefix
	 */
	public void setStatementNoPrefix(String statementNoPrefix) {
		this.statementNoPrefix = statementNoPrefix;
	}

	/**
	 * Returns the statementNoYFmt.
	 * 
	 * @return the statementNoYFmt
	 */
	public String getStatementNoYFmt() {
		return statementNoYFmt;
	}

	/**
	 * Sets the statementNoYFmt.
	 * 
	 * @param statementNoYFmt the statementNoYFmt
	 */
	public void setStatementNoYFmt(String statementNoYFmt) {
		this.statementNoYFmt = statementNoYFmt;
	}

	/**
	 * Returns the statementNoMFmt.
	 * 
	 * @return the statementNoMFmt
	 */
	public String getStatementNoMFmt() {
		return statementNoMFmt;
	}

	/**
	 * Sets the statementNoMFmt.
	 * 
	 * @param statementNoMFmt the statementNoMFmt
	 */
	public void setStatementNoMFmt(String statementNoMFmt) {
		this.statementNoMFmt = statementNoMFmt;
	}

	/**
	 * Returns the statementNoDFmt.
	 * 
	 * @return the statementNoDFmt
	 */
	public String getStatementNoDFmt() {
		return statementNoDFmt;
	}

	/**
	 * Sets the statementNoDFmt.
	 * 
	 * @param statementNoDFmt the statementNoDFmt
	 */
	public void setStatementNoDFmt(String statementNoDFmt) {
		this.statementNoDFmt = statementNoDFmt;
	}

	/**
	 * Returns the statementNoDelimiter.
	 * 
	 * @return the statementNoDelimiter
	 */
	public String getStatementNoDelimiter() {
		return statementNoDelimiter;
	}

	/**
	 * Sets the statementNoDelimiter.
	 * 
	 * @param statementNoDelimiter the statementNoDelimiter
	 */
	public void setStatementNoDelimiter(String statementNoDelimiter) {
		this.statementNoDelimiter = statementNoDelimiter;
	}

	/**
	 * Returns the statementNoNumberingType.
	 * 
	 * @return the statementNoNumberingType
	 */
	public String getStatementNoNumberingType() {
		return statementNoNumberingType;
	}

	/**
	 * Sets the statementNoNumberingType.
	 * 
	 * @param statementNoNumberingType the statementNoNumberingType
	 */
	public void setStatementNoNumberingType(String statementNoNumberingType) {
		this.statementNoNumberingType = statementNoNumberingType;
	}

	/**
	 * Returns the statementNoZeroPadFlg.
	 * 
	 * @return the statementNoZeroPadFlg
	 */
	public String getStatementNoZeroPadFlg() {
		return statementNoZeroPadFlg;
	}

	/**
	 * Sets the statementNoZeroPadFlg.
	 * 
	 * @param statementNoZeroPadFlg the statementNoZeroPadFlg
	 */
	public void setStatementNoZeroPadFlg(String statementNoZeroPadFlg) {
		this.statementNoZeroPadFlg = statementNoZeroPadFlg;
	}

	/**
	 * Returns the statementNoZeroPadDigits.
	 * 
	 * @return the statementNoZeroPadDigits
	 */
	public String getStatementNoZeroPadDigits() {
		return statementNoZeroPadDigits;
	}

	/**
	 * Sets the statementNoZeroPadDigits.
	 * 
	 * @param statementNoZeroPadDigits the statementNoZeroPadDigits
	 */
	public void setStatementNoZeroPadDigits(String statementNoZeroPadDigits) {
		this.statementNoZeroPadDigits = statementNoZeroPadDigits;
	}

	/**
	 * Returns the transactionDatePrintFlg.
	 * 
	 * @return the transactionDatePrintFlg
	 */
	public String getTransactionDatePrintFlg() {
		return transactionDatePrintFlg;
	}

	/**
	 * Sets the transactionDatePrintFlg.
	 * 
	 * @param transactionDatePrintFlg the transactionDatePrintFlg
	 */
	public void setTransactionDatePrintFlg(String transactionDatePrintFlg) {
		this.transactionDatePrintFlg = transactionDatePrintFlg;
	}

	/**
	 * Returns the refundDatePrintFlg.
	 * 
	 * @return the refundDatePrintFlg
	 */
	public String getRefundDatePrintFlg() {
		return refundDatePrintFlg;
	}

	/**
	 * Sets the refundDatePrintFlg.
	 * 
	 * @param refundDatePrintFlg the refundDatePrintFlg
	 */
	public void setRefundDatePrintFlg(String refundDatePrintFlg) {
		this.refundDatePrintFlg = refundDatePrintFlg;
	}

	/**
	 * Returns the tenantStampPrintFlg.
	 * 
	 * @return the tenantStampPrintFlg
	 */
	public String getTenantStampPrintFlg() {
		return tenantStampPrintFlg;
	}

	/**
	 * Sets the tenantStampPrintFlg.
	 * 
	 * @param tenantStampPrintFlg the tenantStampPrintFlg
	 */
	public void setTenantStampPrintFlg(String tenantStampPrintFlg) {
		this.tenantStampPrintFlg = tenantStampPrintFlg;
	}

	/**
	 * Returns the defaultRemarks.
	 * 
	 * @return the defaultRemarks
	 */
	public String getDefaultRemarks() {
		return defaultRemarks;
	}

	/**
	 * Sets the defaultRemarks.
	 * 
	 * @param defaultRemarks the defaultRemarks
	 */
	public void setDefaultRemarks(String defaultRemarks) {
		this.defaultRemarks = defaultRemarks;
	}

	/**
	 * Returns the createdAt.
	 * 
	 * @return the createdAt
	 */
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	/**
	 * Sets the createdAt.
	 * 
	 * @param createdAt the createdAt
	 */
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * Returns the createdBy.
	 * 
	 * @return the createdBy
	 */
	public Long getCreatedBy() {
		return createdBy;
	}

	/**
	 * Sets the createdBy.
	 * 
	 * @param createdBy the createdBy
	 */
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Returns the updatedAt.
	 * 
	 * @return the updatedAt
	 */
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * Sets the updatedAt.
	 * 
	 * @param updatedAt the updatedAt
	 */
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * Returns the updatedBy.
	 * 
	 * @return the updatedBy
	 */
	public Long getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * Sets the updatedBy.
	 * 
	 * @param updatedBy the updatedBy
	 */
	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * Returns the versionNo.
	 * 
	 * @return the versionNo
	 */
	public Long getVersionNo() {
		return versionNo;
	}

	/**
	 * Sets the versionNo.
	 * 
	 * @param versionNo the versionNo
	 */
	public void setVersionNo(Long versionNo) {
		this.versionNo = versionNo;
	}
}