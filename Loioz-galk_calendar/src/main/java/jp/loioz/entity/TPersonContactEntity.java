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
 * 名簿連絡先
 */
@Entity(listener = TPersonContactEntityListener.class)
@Table(name = "t_person_contact")
public class TPersonContactEntity extends DefaultEntity {

    /** 顧客連絡先SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_seq")
    Long contactSeq;

    /** 名簿ID */
    @Column(name = "person_id")
    Long personId;

    /** 電話番号 */
    @Column(name = "tel_no")
    String telNo;

    /** 電話番号区分 */
    @Column(name = "tel_no_type")
    String telNoType;

    /** 優先電話フラグ */
    @Column(name = "yusen_tel_flg")
    String yusenTelFlg;

    /** 電話備考 */
    @Column(name = "tel_remarks")
    String telRemarks;

    /** FAX番号 */
    @Column(name = "fax_no")
    String faxNo;

    /** FAX番号区分 */
    @Column(name = "fax_no_type")
    String faxNoType;

    /** 優先FAXフラグ */
    @Column(name = "yusen_fax_flg")
    String yusenFaxFlg;

    /** FAX備考 */
    @Column(name = "fax_remarks")
    String faxRemarks;

    /** メールアドレス */
    @Column(name = "mail_address")
    String mailAddress;

    /** メールアドレス区分 */
    @Column(name = "mail_address_type")
    String mailAddressType;

    /** 優先メールアドレスフラグ */
    @Column(name = "yusen_mail_address_flg")
    String yusenMailAddressFlg;

    /** メールアドレス備考 */
    @Column(name = "mail_address_remarks")
    String mailAddressRemarks;

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
     * Returns the contactSeq.
     * 
     * @return the contactSeq
     */
    public Long getContactSeq() {
        return contactSeq;
    }

    /** 
     * Sets the contactSeq.
     * 
     * @param contactSeq the contactSeq
     */
    public void setContactSeq(Long contactSeq) {
        this.contactSeq = contactSeq;
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
     * Returns the telNo.
     * 
     * @return the telNo
     */
    public String getTelNo() {
        return telNo;
    }

    /** 
     * Sets the telNo.
     * 
     * @param telNo the telNo
     */
    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    /** 
     * Returns the telNoType.
     * 
     * @return the telNoType
     */
    public String getTelNoType() {
        return telNoType;
    }

    /** 
     * Sets the telNoType.
     * 
     * @param telNoType the telNoType
     */
    public void setTelNoType(String telNoType) {
        this.telNoType = telNoType;
    }

    /** 
     * Returns the yusenTelFlg.
     * 
     * @return the yusenTelFlg
     */
    public String getYusenTelFlg() {
        return yusenTelFlg;
    }

    /** 
     * Sets the yusenTelFlg.
     * 
     * @param yusenTelFlg the yusenTelFlg
     */
    public void setYusenTelFlg(String yusenTelFlg) {
        this.yusenTelFlg = yusenTelFlg;
    }

    /** 
     * Returns the telRemarks.
     * 
     * @return the telRemarks
     */
    public String getTelRemarks() {
        return telRemarks;
    }

    /** 
     * Sets the telRemarks.
     * 
     * @param telRemarks the telRemarks
     */
    public void setTelRemarks(String telRemarks) {
        this.telRemarks = telRemarks;
    }

    /** 
     * Returns the faxNo.
     * 
     * @return the faxNo
     */
    public String getFaxNo() {
        return faxNo;
    }

    /** 
     * Sets the faxNo.
     * 
     * @param faxNo the faxNo
     */
    public void setFaxNo(String faxNo) {
        this.faxNo = faxNo;
    }

    /** 
     * Returns the faxNoType.
     * 
     * @return the faxNoType
     */
    public String getFaxNoType() {
        return faxNoType;
    }

    /** 
     * Sets the faxNoType.
     * 
     * @param faxNoType the faxNoType
     */
    public void setFaxNoType(String faxNoType) {
        this.faxNoType = faxNoType;
    }

    /** 
     * Returns the yusenFaxFlg.
     * 
     * @return the yusenFaxFlg
     */
    public String getYusenFaxFlg() {
        return yusenFaxFlg;
    }

    /** 
     * Sets the yusenFaxFlg.
     * 
     * @param yusenFaxFlg the yusenFaxFlg
     */
    public void setYusenFaxFlg(String yusenFaxFlg) {
        this.yusenFaxFlg = yusenFaxFlg;
    }

    /** 
     * Returns the faxRemarks.
     * 
     * @return the faxRemarks
     */
    public String getFaxRemarks() {
        return faxRemarks;
    }

    /** 
     * Sets the faxRemarks.
     * 
     * @param faxRemarks the faxRemarks
     */
    public void setFaxRemarks(String faxRemarks) {
        this.faxRemarks = faxRemarks;
    }

    /** 
     * Returns the mailAddress.
     * 
     * @return the mailAddress
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /** 
     * Sets the mailAddress.
     * 
     * @param mailAddress the mailAddress
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    /** 
     * Returns the mailAddressType.
     * 
     * @return the mailAddressType
     */
    public String getMailAddressType() {
        return mailAddressType;
    }

    /** 
     * Sets the mailAddressType.
     * 
     * @param mailAddressType the mailAddressType
     */
    public void setMailAddressType(String mailAddressType) {
        this.mailAddressType = mailAddressType;
    }

    /** 
     * Returns the yusenMailAddressFlg.
     * 
     * @return the yusenMailAddressFlg
     */
    public String getYusenMailAddressFlg() {
        return yusenMailAddressFlg;
    }

    /** 
     * Sets the yusenMailAddressFlg.
     * 
     * @param yusenMailAddressFlg the yusenMailAddressFlg
     */
    public void setYusenMailAddressFlg(String yusenMailAddressFlg) {
        this.yusenMailAddressFlg = yusenMailAddressFlg;
    }

    /** 
     * Returns the mailAddressRemarks.
     * 
     * @return the mailAddressRemarks
     */
    public String getMailAddressRemarks() {
        return mailAddressRemarks;
    }

    /** 
     * Sets the mailAddressRemarks.
     * 
     * @param mailAddressRemarks the mailAddressRemarks
     */
    public void setMailAddressRemarks(String mailAddressRemarks) {
        this.mailAddressRemarks = mailAddressRemarks;
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