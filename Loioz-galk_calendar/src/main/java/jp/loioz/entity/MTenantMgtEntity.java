package jp.loioz.entity;

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
 * テナント管理
 */
@Entity(listener = MTenantMgtEntityListener.class)
@Table(schema ="service_mgt", name = "m_tenant_mgt")
public class MTenantMgtEntity extends DefaultEntity {

    /** テナント連番 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tenant_seq")
    Long tenantSeq;

    /** サブドメイン名 */
    @Column(name = "sub_domain")
    String subDomain;

    /** 事務所名 */
    @Column(name = "tenant_name")
    String tenantName;

    /** 個人・法人区分 */
    @Column(name = "tenant_type")
    String tenantType;

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

    /** 作成日時 */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** 作成アカウントSEQ */
    @Column(name = "created_by")
    Long createdBy;

    /** 更新日時 */
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    /** 更新アカウントSEQ */
    @Column(name = "updated_by")
    Long updatedBy;

    /** 削除日時 */
    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    /** 削除アカウントSEQ */
    @Column(name = "deleted_by")
    Long deletedBy;

    /** システム管理者_作成日時 */
    @Column(name = "sys_created_at")
    LocalDateTime sysCreatedAt;

    /** システム管理者_作成アカウントSEQ */
    @Column(name = "sys_created_by")
    Long sysCreatedBy;

    /** システム管理者更新日時 */
    @Column(name = "sys_updated_at")
    LocalDateTime sysUpdatedAt;

    /** システム管理者更新アカウントSEQ */
    @Column(name = "sys_updated_by")
    Long sysUpdatedBy;

    /** システム管理者削除日時 */
    @Column(name = "sys_deleted_at")
    LocalDateTime sysDeletedAt;

    /** システム管理者_削除アカウントSEQ */
    @Column(name = "sys_deleted_by")
    Long sysDeletedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

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
     * Returns the subDomain.
     * 
     * @return the subDomain
     */
    public String getSubDomain() {
        return subDomain;
    }

    /** 
     * Sets the subDomain.
     * 
     * @param subDomain the subDomain
     */
    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    /** 
     * Returns the tenantName.
     * 
     * @return the tenantName
     */
    public String getTenantName() {
        return tenantName;
    }

    /** 
     * Sets the tenantName.
     * 
     * @param tenantName the tenantName
     */
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    /** 
     * Returns the tenantType.
     * 
     * @return the tenantType
     */
    public String getTenantType() {
        return tenantType;
    }

    /** 
     * Sets the tenantType.
     * 
     * @param tenantType the tenantType
     */
    public void setTenantType(String tenantType) {
        this.tenantType = tenantType;
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
     * Returns the deletedAt.
     * 
     * @return the deletedAt
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    /** 
     * Sets the deletedAt.
     * 
     * @param deletedAt the deletedAt
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    /** 
     * Returns the deletedBy.
     * 
     * @return the deletedBy
     */
    public Long getDeletedBy() {
        return deletedBy;
    }

    /** 
     * Sets the deletedBy.
     * 
     * @param deletedBy the deletedBy
     */
    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
    }

    /** 
     * Returns the sysCreatedAt.
     * 
     * @return the sysCreatedAt
     */
    public LocalDateTime getSysCreatedAt() {
        return sysCreatedAt;
    }

    /** 
     * Sets the sysCreatedAt.
     * 
     * @param sysCreatedAt the sysCreatedAt
     */
    public void setSysCreatedAt(LocalDateTime sysCreatedAt) {
        this.sysCreatedAt = sysCreatedAt;
    }

    /** 
     * Returns the sysCreatedBy.
     * 
     * @return the sysCreatedBy
     */
    public Long getSysCreatedBy() {
        return sysCreatedBy;
    }

    /** 
     * Sets the sysCreatedBy.
     * 
     * @param sysCreatedBy the sysCreatedBy
     */
    public void setSysCreatedBy(Long sysCreatedBy) {
        this.sysCreatedBy = sysCreatedBy;
    }

    /** 
     * Returns the sysUpdatedAt.
     * 
     * @return the sysUpdatedAt
     */
    public LocalDateTime getSysUpdatedAt() {
        return sysUpdatedAt;
    }

    /** 
     * Sets the sysUpdatedAt.
     * 
     * @param sysUpdatedAt the sysUpdatedAt
     */
    public void setSysUpdatedAt(LocalDateTime sysUpdatedAt) {
        this.sysUpdatedAt = sysUpdatedAt;
    }

    /** 
     * Returns the sysUpdatedBy.
     * 
     * @return the sysUpdatedBy
     */
    public Long getSysUpdatedBy() {
        return sysUpdatedBy;
    }

    /** 
     * Sets the sysUpdatedBy.
     * 
     * @param sysUpdatedBy the sysUpdatedBy
     */
    public void setSysUpdatedBy(Long sysUpdatedBy) {
        this.sysUpdatedBy = sysUpdatedBy;
    }

    /** 
     * Returns the sysDeletedAt.
     * 
     * @return the sysDeletedAt
     */
    public LocalDateTime getSysDeletedAt() {
        return sysDeletedAt;
    }

    /** 
     * Sets the sysDeletedAt.
     * 
     * @param sysDeletedAt the sysDeletedAt
     */
    public void setSysDeletedAt(LocalDateTime sysDeletedAt) {
        this.sysDeletedAt = sysDeletedAt;
    }

    /** 
     * Returns the sysDeletedBy.
     * 
     * @return the sysDeletedBy
     */
    public Long getSysDeletedBy() {
        return sysDeletedBy;
    }

    /** 
     * Sets the sysDeletedBy.
     * 
     * @param sysDeletedBy the sysDeletedBy
     */
    public void setSysDeletedBy(Long sysDeletedBy) {
        this.sysDeletedBy = sysDeletedBy;
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