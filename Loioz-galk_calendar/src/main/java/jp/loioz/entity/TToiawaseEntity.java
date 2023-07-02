package jp.loioz.entity;

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
 * 問い合わせ
 */
@Entity(listener = TToiawaseEntityListener.class)
@Table(name = "t_toiawase")
public class TToiawaseEntity extends DefaultEntity {

    /** 問い合わせSEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toiawase_seq")
    Long toiawaseSeq;

    /** 件名 */
    @Column(name = "subject")
    String subject;

    /** 問い合わせ種別 */
    @Column(name = "toiawase_type")
    String toiawaseType;

    /** 問い合わせステータス */
    @Column(name = "toiawase_status")
    String toiawaseStatus;

    /** 初回作成日時 */
    @Column(name = "shokai_created_at")
    LocalDateTime shokaiCreatedAt;

    /** 最終更新日時 */
    @Column(name = "last_update_at")
    LocalDateTime lastUpdateAt;

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

    /** システム管理者_登録日時 */
    @Column(name = "sys_created_at")
    LocalDateTime sysCreatedAt;

    /** システム管理者_登録アカウントSEQ */
    @Column(name = "sys_created_by")
    Long sysCreatedBy;

    /** システム管理者_更新日時 */
    @Column(name = "sys_updated_at")
    LocalDateTime sysUpdatedAt;

    /** システム管理者_更新アカウントSEQ */
    @Column(name = "sys_updated_by")
    Long sysUpdatedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the toiawaseSeq.
     * 
     * @return the toiawaseSeq
     */
    public Long getToiawaseSeq() {
        return toiawaseSeq;
    }

    /** 
     * Sets the toiawaseSeq.
     * 
     * @param toiawaseSeq the toiawaseSeq
     */
    public void setToiawaseSeq(Long toiawaseSeq) {
        this.toiawaseSeq = toiawaseSeq;
    }

    /** 
     * Returns the subject.
     * 
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /** 
     * Sets the subject.
     * 
     * @param subject the subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /** 
     * Returns the toiawaseType.
     * 
     * @return the toiawaseType
     */
    public String getToiawaseType() {
        return toiawaseType;
    }

    /** 
     * Sets the toiawaseType.
     * 
     * @param toiawaseType the toiawaseType
     */
    public void setToiawaseType(String toiawaseType) {
        this.toiawaseType = toiawaseType;
    }

    /** 
     * Returns the toiawaseStatus.
     * 
     * @return the toiawaseStatus
     */
    public String getToiawaseStatus() {
        return toiawaseStatus;
    }

    /** 
     * Sets the toiawaseStatus.
     * 
     * @param toiawaseStatus the toiawaseStatus
     */
    public void setToiawaseStatus(String toiawaseStatus) {
        this.toiawaseStatus = toiawaseStatus;
    }

    /** 
     * Returns the shokaiCreatedAt.
     * 
     * @return the shokaiCreatedAt
     */
    public LocalDateTime getShokaiCreatedAt() {
        return shokaiCreatedAt;
    }

    /** 
     * Sets the shokaiCreatedAt.
     * 
     * @param shokaiCreatedAt the shokaiCreatedAt
     */
    public void setShokaiCreatedAt(LocalDateTime shokaiCreatedAt) {
        this.shokaiCreatedAt = shokaiCreatedAt;
    }

    /** 
     * Returns the lastUpdateAt.
     * 
     * @return the lastUpdateAt
     */
    public LocalDateTime getLastUpdateAt() {
        return lastUpdateAt;
    }

    /** 
     * Sets the lastUpdateAt.
     * 
     * @param lastUpdateAt the lastUpdateAt
     */
    public void setLastUpdateAt(LocalDateTime lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
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