package jp.loioz.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
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
 * 利用プラン履歴テーブル
 */
@Entity(listener = TPlanHistoryEntityListener.class)
@Table(name = "t_plan_history")
public class TPlanHistoryEntity extends DefaultEntity {

    /** 利用プラン履歴連番 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_history_seq")
    Long planHistorySeq;

    /** テナントSEQ */
    @Column(name = "tenant_seq")
    Long tenantSeq;

    /** プラン契約ステータス */
    @Column(name = "plan_status")
    String planStatus;

    /** プランタイプ */
    @Column(name = "plan_type")
    String planType;

    /** ライセンス数 */
    @Column(name = "license_count")
    Long licenseCount;

    /** ストレージ容量 */
    @Column(name = "storage_capacity")
    Long storageCapacity;

    /** 利用期限 */
    @Column(name = "expired_at")
    LocalDateTime expiredAt;

    /** 自動課金番号 */
    @Column(name = "auto_charge_number")
    BigInteger autoChargeNumber;

    /** 当月請求金額 */
    @Column(name = "charge_this_month")
    BigDecimal chargeThisMonth;

    /** 翌月以降請求金額 */
    @Column(name = "charge_after_next_month")
    BigDecimal chargeAfterNextMonth;

    /** 履歴登録日 */
    @Column(name = "history_created_at")
    LocalDateTime historyCreatedAt;

    /** 履歴登録者ID */
    @Column(name = "history_created_by")
    Long historyCreatedBy;

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
     * Returns the planHistorySeq.
     * 
     * @return the planHistorySeq
     */
    public Long getPlanHistorySeq() {
        return planHistorySeq;
    }

    /** 
     * Sets the planHistorySeq.
     * 
     * @param planHistorySeq the planHistorySeq
     */
    public void setPlanHistorySeq(Long planHistorySeq) {
        this.planHistorySeq = planHistorySeq;
    }

    /** 
     * Returns the tenantSeq.
     * 
     * @return the tenantSeq
     */
    public Long getTenantSeq() {
        return tenantSeq;
    }

    /** 
     * Sets the tenantSeq.
     * 
     * @param tenantSeq the tenantSeq
     */
    public void setTenantSeq(Long tenantSeq) {
        this.tenantSeq = tenantSeq;
    }

    /** 
     * Returns the planStatus.
     * 
     * @return the planStatus
     */
    public String getPlanStatus() {
        return planStatus;
    }

    /** 
     * Sets the planStatus.
     * 
     * @param planStatus the planStatus
     */
    public void setPlanStatus(String planStatus) {
        this.planStatus = planStatus;
    }

    /** 
     * Returns the planType.
     * 
     * @return the planType
     */
    public String getPlanType() {
        return planType;
    }

    /** 
     * Sets the planType.
     * 
     * @param planType the planType
     */
    public void setPlanType(String planType) {
        this.planType = planType;
    }

    /** 
     * Returns the licenseCount.
     * 
     * @return the licenseCount
     */
    public Long getLicenseCount() {
        return licenseCount;
    }

    /** 
     * Sets the licenseCount.
     * 
     * @param licenseCount the licenseCount
     */
    public void setLicenseCount(Long licenseCount) {
        this.licenseCount = licenseCount;
    }

    /** 
     * Returns the storageCapacity.
     * 
     * @return the storageCapacity
     */
    public Long getStorageCapacity() {
        return storageCapacity;
    }

    /** 
     * Sets the storageCapacity.
     * 
     * @param storageCapacity the storageCapacity
     */
    public void setStorageCapacity(Long storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    /** 
     * Returns the expiredAt.
     * 
     * @return the expiredAt
     */
    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    /** 
     * Sets the expiredAt.
     * 
     * @param expiredAt the expiredAt
     */
    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    /** 
     * Returns the autoChargeNumber.
     * 
     * @return the autoChargeNumber
     */
    public BigInteger getAutoChargeNumber() {
        return autoChargeNumber;
    }

    /** 
     * Sets the autoChargeNumber.
     * 
     * @param autoChargeNumber the autoChargeNumber
     */
    public void setAutoChargeNumber(BigInteger autoChargeNumber) {
        this.autoChargeNumber = autoChargeNumber;
    }

    /** 
     * Returns the chargeThisMonth.
     * 
     * @return the chargeThisMonth
     */
    public BigDecimal getChargeThisMonth() {
        return chargeThisMonth;
    }

    /** 
     * Sets the chargeThisMonth.
     * 
     * @param chargeThisMonth the chargeThisMonth
     */
    public void setChargeThisMonth(BigDecimal chargeThisMonth) {
        this.chargeThisMonth = chargeThisMonth;
    }

    /** 
     * Returns the chargeAfterNextMonth.
     * 
     * @return the chargeAfterNextMonth
     */
    public BigDecimal getChargeAfterNextMonth() {
        return chargeAfterNextMonth;
    }

    /** 
     * Sets the chargeAfterNextMonth.
     * 
     * @param chargeAfterNextMonth the chargeAfterNextMonth
     */
    public void setChargeAfterNextMonth(BigDecimal chargeAfterNextMonth) {
        this.chargeAfterNextMonth = chargeAfterNextMonth;
    }

    /** 
     * Returns the historyCreatedAt.
     * 
     * @return the historyCreatedAt
     */
    public LocalDateTime getHistoryCreatedAt() {
        return historyCreatedAt;
    }

    /** 
     * Sets the historyCreatedAt.
     * 
     * @param historyCreatedAt the historyCreatedAt
     */
    public void setHistoryCreatedAt(LocalDateTime historyCreatedAt) {
        this.historyCreatedAt = historyCreatedAt;
    }

    /** 
     * Returns the historyCreatedBy.
     * 
     * @return the historyCreatedBy
     */
    public Long getHistoryCreatedBy() {
        return historyCreatedBy;
    }

    /** 
     * Sets the historyCreatedBy.
     * 
     * @param historyCreatedBy the historyCreatedBy
     */
    public void setHistoryCreatedBy(Long historyCreatedBy) {
        this.historyCreatedBy = historyCreatedBy;
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