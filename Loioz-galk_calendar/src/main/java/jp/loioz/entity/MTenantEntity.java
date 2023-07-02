package jp.loioz.entity;

import java.sql.Blob;
import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * テナント情報
 */
@Entity(listener = MTenantEntityListener.class)
@Table(name = "m_tenant")
public class MTenantEntity extends DefaultEntity {

    /** テナントSEQ */
    @Id
    @Column(name = "tenant_seq")
    Long tenantSeq;

    /** 事務所名 */
    @Column(name = "tenant_name")
    String tenantName;

    /** 事務所名（カナ） */
    @Column(name = "tenant_name_kana")
    String tenantNameKana;

    /** 個人・法人区分 */
    @Column(name = "tenant_type")
    String tenantType;

    /** 郵便番号 */
    @Column(name = "tenant_zip_cd")
    String tenantZipCd;

    /** 住所１ */
    @Column(name = "tenant_address1")
    String tenantAddress1;

    /** 住所２ */
    @Column(name = "tenant_address2")
    String tenantAddress2;

    /** 電話番号 */
    @Column(name = "tenant_tel_no")
    String tenantTelNo;

    /** FAX番号 */
    @Column(name = "tenant_fax_no")
    String tenantFaxNo;

    /** 適格請求書発行事業者登録番号 */
    @Column(name = "tenant_invoice_registration_no")
    String tenantInvoiceRegistrationNo;

    /** 事務所印画像 */
    @Column(name = "tenant_stamp_img")
    Blob tenantStampImg;

    /** 事務所印画像拡張子 */
    @Column(name = "tenant_stamp_img_extension")
    String tenantStampImgExtension;

    /** 代表姓 */
    @Column(name = "tenant_daihyo_name_sei")
    String tenantDaihyoNameSei;

    /** 代表姓（かな） */
    @Column(name = "tenant_daihyo_name_sei_kana")
    String tenantDaihyoNameSeiKana;

    /** 代表名 */
    @Column(name = "tenant_daihyo_name_mei")
    String tenantDaihyoNameMei;

    /** 代表名（かな） */
    @Column(name = "tenant_daihyo_name_mei_kana")
    String tenantDaihyoNameMeiKana;

    /** 代表者メールアドレス */
    @Column(name = "tenant_daihyo_mail_address")
    String tenantDaihyoMailAddress;

    /** 消費税の端数処理の方法 */
    @Column(name = "tax_hasu_type")
    String taxHasuType;

    /** 報酬の端数処理の方法 */
    @Column(name = "hoshu_hasu_type")
    String hoshuHasuType;

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

    /** 削除日時 */
    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    /** 削除アカウントSEQ */
    @Column(name = "deleted_by")
    Long deletedBy;

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
     * Returns the tenantNameKana.
     * 
     * @return the tenantNameKana
     */
    public String getTenantNameKana() {
        return tenantNameKana;
    }

    /** 
     * Sets the tenantNameKana.
     * 
     * @param tenantNameKana the tenantNameKana
     */
    public void setTenantNameKana(String tenantNameKana) {
        this.tenantNameKana = tenantNameKana;
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
     * Returns the tenantZipCd.
     * 
     * @return the tenantZipCd
     */
    public String getTenantZipCd() {
        return tenantZipCd;
    }

    /** 
     * Sets the tenantZipCd.
     * 
     * @param tenantZipCd the tenantZipCd
     */
    public void setTenantZipCd(String tenantZipCd) {
        this.tenantZipCd = tenantZipCd;
    }

    /** 
     * Returns the tenantAddress1.
     * 
     * @return the tenantAddress1
     */
    public String getTenantAddress1() {
        return tenantAddress1;
    }

    /** 
     * Sets the tenantAddress1.
     * 
     * @param tenantAddress1 the tenantAddress1
     */
    public void setTenantAddress1(String tenantAddress1) {
        this.tenantAddress1 = tenantAddress1;
    }

    /** 
     * Returns the tenantAddress2.
     * 
     * @return the tenantAddress2
     */
    public String getTenantAddress2() {
        return tenantAddress2;
    }

    /** 
     * Sets the tenantAddress2.
     * 
     * @param tenantAddress2 the tenantAddress2
     */
    public void setTenantAddress2(String tenantAddress2) {
        this.tenantAddress2 = tenantAddress2;
    }

    /** 
     * Returns the tenantTelNo.
     * 
     * @return the tenantTelNo
     */
    public String getTenantTelNo() {
        return tenantTelNo;
    }

    /** 
     * Sets the tenantTelNo.
     * 
     * @param tenantTelNo the tenantTelNo
     */
    public void setTenantTelNo(String tenantTelNo) {
        this.tenantTelNo = tenantTelNo;
    }

    /** 
     * Returns the tenantFaxNo.
     * 
     * @return the tenantFaxNo
     */
    public String getTenantFaxNo() {
        return tenantFaxNo;
    }

    /** 
     * Sets the tenantFaxNo.
     * 
     * @param tenantFaxNo the tenantFaxNo
     */
    public void setTenantFaxNo(String tenantFaxNo) {
        this.tenantFaxNo = tenantFaxNo;
    }

    /** 
     * Returns the tenantInvoiceRegistrationNo.
     * 
     * @return the tenantInvoiceRegistrationNo
     */
    public String getTenantInvoiceRegistrationNo() {
        return tenantInvoiceRegistrationNo;
    }

    /** 
     * Sets the tenantInvoiceRegistrationNo.
     * 
     * @param tenantInvoiceRegistrationNo the tenantInvoiceRegistrationNo
     */
    public void setTenantInvoiceRegistrationNo(String tenantInvoiceRegistrationNo) {
        this.tenantInvoiceRegistrationNo = tenantInvoiceRegistrationNo;
    }

    /** 
     * Returns the tenantStampImg.
     * 
     * @return the tenantStampImg
     */
    public Blob getTenantStampImg() {
        return tenantStampImg;
    }

    /** 
     * Sets the tenantStampImg.
     * 
     * @param tenantStampImg the tenantStampImg
     */
    public void setTenantStampImg(Blob tenantStampImg) {
        this.tenantStampImg = tenantStampImg;
    }

    /** 
     * Returns the tenantStampImgExtension.
     * 
     * @return the tenantStampImgExtension
     */
    public String getTenantStampImgExtension() {
        return tenantStampImgExtension;
    }

    /** 
     * Sets the tenantStampImgExtension.
     * 
     * @param tenantStampImgExtension the tenantStampImgExtension
     */
    public void setTenantStampImgExtension(String tenantStampImgExtension) {
        this.tenantStampImgExtension = tenantStampImgExtension;
    }

    /** 
     * Returns the tenantDaihyoNameSei.
     * 
     * @return the tenantDaihyoNameSei
     */
    public String getTenantDaihyoNameSei() {
        return tenantDaihyoNameSei;
    }

    /** 
     * Sets the tenantDaihyoNameSei.
     * 
     * @param tenantDaihyoNameSei the tenantDaihyoNameSei
     */
    public void setTenantDaihyoNameSei(String tenantDaihyoNameSei) {
        this.tenantDaihyoNameSei = tenantDaihyoNameSei;
    }

    /** 
     * Returns the tenantDaihyoNameSeiKana.
     * 
     * @return the tenantDaihyoNameSeiKana
     */
    public String getTenantDaihyoNameSeiKana() {
        return tenantDaihyoNameSeiKana;
    }

    /** 
     * Sets the tenantDaihyoNameSeiKana.
     * 
     * @param tenantDaihyoNameSeiKana the tenantDaihyoNameSeiKana
     */
    public void setTenantDaihyoNameSeiKana(String tenantDaihyoNameSeiKana) {
        this.tenantDaihyoNameSeiKana = tenantDaihyoNameSeiKana;
    }

    /** 
     * Returns the tenantDaihyoNameMei.
     * 
     * @return the tenantDaihyoNameMei
     */
    public String getTenantDaihyoNameMei() {
        return tenantDaihyoNameMei;
    }

    /** 
     * Sets the tenantDaihyoNameMei.
     * 
     * @param tenantDaihyoNameMei the tenantDaihyoNameMei
     */
    public void setTenantDaihyoNameMei(String tenantDaihyoNameMei) {
        this.tenantDaihyoNameMei = tenantDaihyoNameMei;
    }

    /** 
     * Returns the tenantDaihyoNameMeiKana.
     * 
     * @return the tenantDaihyoNameMeiKana
     */
    public String getTenantDaihyoNameMeiKana() {
        return tenantDaihyoNameMeiKana;
    }

    /** 
     * Sets the tenantDaihyoNameMeiKana.
     * 
     * @param tenantDaihyoNameMeiKana the tenantDaihyoNameMeiKana
     */
    public void setTenantDaihyoNameMeiKana(String tenantDaihyoNameMeiKana) {
        this.tenantDaihyoNameMeiKana = tenantDaihyoNameMeiKana;
    }

    /** 
     * Returns the tenantDaihyoMailAddress.
     * 
     * @return the tenantDaihyoMailAddress
     */
    public String getTenantDaihyoMailAddress() {
        return tenantDaihyoMailAddress;
    }

    /** 
     * Sets the tenantDaihyoMailAddress.
     * 
     * @param tenantDaihyoMailAddress the tenantDaihyoMailAddress
     */
    public void setTenantDaihyoMailAddress(String tenantDaihyoMailAddress) {
        this.tenantDaihyoMailAddress = tenantDaihyoMailAddress;
    }

    /** 
     * Returns the taxHasuType.
     * 
     * @return the taxHasuType
     */
    public String getTaxHasuType() {
        return taxHasuType;
    }

    /** 
     * Sets the taxHasuType.
     * 
     * @param taxHasuType the taxHasuType
     */
    public void setTaxHasuType(String taxHasuType) {
        this.taxHasuType = taxHasuType;
    }

    /** 
     * Returns the hoshuHasuType.
     * 
     * @return the hoshuHasuType
     */
    public String getHoshuHasuType() {
        return hoshuHasuType;
    }

    /** 
     * Sets the hoshuHasuType.
     * 
     * @param hoshuHasuType the hoshuHasuType
     */
    public void setHoshuHasuType(String hoshuHasuType) {
        this.hoshuHasuType = hoshuHasuType;
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