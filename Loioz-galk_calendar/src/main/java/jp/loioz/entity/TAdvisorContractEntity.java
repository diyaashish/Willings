package jp.loioz.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * 顧問契約
 */
@Entity(listener = TAdvisorContractEntityListener.class)
@Table(name = "t_advisor_contract")
public class TAdvisorContractEntity extends DefaultEntity {

    /** 顧問契約SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advisor_contract_seq")
    Long advisorContractSeq;

    /** 名簿ID */
    @Column(name = "person_id")
    Long personId;

    /** 契約開始日 */
    @Column(name = "contract_start_date")
    LocalDate contractStartDate;

    /** 契約終了日 */
    @Column(name = "contract_end_date")
    LocalDate contractEndDate;

    /** 契約区分 */
    @Column(name = "contract_type")
    String contractType;

    /** 契約ステータス */
    @Column(name = "contract_status")
    String contractStatus;

    /** 顧問料金（月額） */
    @Column(name = "contract_month_charge")
    BigDecimal contractMonthCharge;

    /** 稼働時間（時間/月） */
    @Column(name = "contract_month_time")
    Long contractMonthTime;

    /** 契約内容 */
    @Column(name = "contract_content")
    String contractContent;

    /** メモ */
    @Column(name = "contract_memo")
    String contractMemo;

    /** 登録日時 */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** 登録アカウントSEQ */
    @Column(name = "created_by")
    Long createdBy;

    /** 更新日時 */
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    /** 更新アカウントSEQ */
    @Column(name = "updated_by")
    Long updatedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the advisorContractSeq.
     * 
     * @return the advisorContractSeq
     */
    public Long getAdvisorContractSeq() {
        return advisorContractSeq;
    }

    /** 
     * Sets the advisorContractSeq.
     * 
     * @param advisorContractSeq the advisorContractSeq
     */
    public void setAdvisorContractSeq(Long advisorContractSeq) {
        this.advisorContractSeq = advisorContractSeq;
    }

    /** 
     * Returns the personId.
     * 
     * @return the personId
     */
    public Long getPersonId() {
        return personId;
    }

    /** 
     * Sets the personId.
     * 
     * @param personId the personId
     */
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    /** 
     * Returns the contractStartDate.
     * 
     * @return the contractStartDate
     */
    public LocalDate getContractStartDate() {
        return contractStartDate;
    }

    /** 
     * Sets the contractStartDate.
     * 
     * @param contractStartDate the contractStartDate
     */
    public void setContractStartDate(LocalDate contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    /** 
     * Returns the contractEndDate.
     * 
     * @return the contractEndDate
     */
    public LocalDate getContractEndDate() {
        return contractEndDate;
    }

    /** 
     * Sets the contractEndDate.
     * 
     * @param contractEndDate the contractEndDate
     */
    public void setContractEndDate(LocalDate contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    /** 
     * Returns the contractType.
     * 
     * @return the contractType
     */
    public String getContractType() {
        return contractType;
    }

    /** 
     * Sets the contractType.
     * 
     * @param contractType the contractType
     */
    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    /** 
     * Returns the contractStatus.
     * 
     * @return the contractStatus
     */
    public String getContractStatus() {
        return contractStatus;
    }

    /** 
     * Sets the contractStatus.
     * 
     * @param contractStatus the contractStatus
     */
    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    /** 
     * Returns the contractMonthCharge.
     * 
     * @return the contractMonthCharge
     */
    public BigDecimal getContractMonthCharge() {
        return contractMonthCharge;
    }

    /** 
     * Sets the contractMonthCharge.
     * 
     * @param contractMonthCharge the contractMonthCharge
     */
    public void setContractMonthCharge(BigDecimal contractMonthCharge) {
        this.contractMonthCharge = contractMonthCharge;
    }

    /** 
     * Returns the contractMonthTime.
     * 
     * @return the contractMonthTime
     */
    public Long getContractMonthTime() {
        return contractMonthTime;
    }

    /** 
     * Sets the contractMonthTime.
     * 
     * @param contractMonthTime the contractMonthTime
     */
    public void setContractMonthTime(Long contractMonthTime) {
        this.contractMonthTime = contractMonthTime;
    }

    /** 
     * Returns the contractContent.
     * 
     * @return the contractContent
     */
    public String getContractContent() {
        return contractContent;
    }

    /** 
     * Sets the contractContent.
     * 
     * @param contractContent the contractContent
     */
    public void setContractContent(String contractContent) {
        this.contractContent = contractContent;
    }

    /** 
     * Returns the contractMemo.
     * 
     * @return the contractMemo
     */
    public String getContractMemo() {
        return contractMemo;
    }

    /** 
     * Sets the contractMemo.
     * 
     * @param contractMemo the contractMemo
     */
    public void setContractMemo(String contractMemo) {
        this.contractMemo = contractMemo;
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