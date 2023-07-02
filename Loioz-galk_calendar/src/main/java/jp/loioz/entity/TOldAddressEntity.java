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
 * 旧住所
 */
@Entity(listener = TOldAddressEntityListener.class)
@Table(name = "t_old_address")
public class TOldAddressEntity extends DefaultEntity {

    /** 旧住所SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "old_address_seq")
    Long oldAddressSeq;

    /** 名簿ID */
    @Column(name = "person_id")
    Long personId;

    /** 郵便番号 */
    @Column(name = "zip_code")
    String zipCode;

    /** 地域 */
    @Column(name = "address1")
    String address1;

    /** 番地・建物名 */
    @Column(name = "address2")
    String address2;

    /** 備考 */
    @Column(name = "remarks")
    String remarks;

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
     * Returns the oldAddressSeq.
     * 
     * @return the oldAddressSeq
     */
    public Long getOldAddressSeq() {
        return oldAddressSeq;
    }

    /** 
     * Sets the oldAddressSeq.
     * 
     * @param oldAddressSeq the oldAddressSeq
     */
    public void setOldAddressSeq(Long oldAddressSeq) {
        this.oldAddressSeq = oldAddressSeq;
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
     * Returns the zipCode.
     * 
     * @return the zipCode
     */
    public String getZipCode() {
        return zipCode;
    }

    /** 
     * Sets the zipCode.
     * 
     * @param zipCode the zipCode
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /** 
     * Returns the address1.
     * 
     * @return the address1
     */
    public String getAddress1() {
        return address1;
    }

    /** 
     * Sets the address1.
     * 
     * @param address1 the address1
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /** 
     * Returns the address2.
     * 
     * @return the address2
     */
    public String getAddress2() {
        return address2;
    }

    /** 
     * Sets the address2.
     * 
     * @param address2 the address2
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /** 
     * Returns the remarks.
     * 
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /** 
     * Sets the remarks.
     * 
     * @param remarks the remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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