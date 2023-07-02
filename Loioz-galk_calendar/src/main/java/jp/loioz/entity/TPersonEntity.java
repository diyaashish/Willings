package jp.loioz.entity;

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
 * 名簿
 */
@Entity(listener = TPersonEntityListener.class)
@Table(name = "t_person")
public class TPersonEntity extends DefaultEntity {

    /** 名簿ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    Long personId;

    /** 顧客ID */
    @Column(name = "customer_id")
    Long customerId;

    /** 顧客フラグ */
    @Column(name = "customer_flg")
    String customerFlg;

    /** 顧問フラグ */
    @Column(name = "advisor_flg")
    String advisorFlg;

    /** 個人・法人・弁護士区分 */
    @Column(name = "customer_type")
    String customerType;

    /** 顧客姓 */
    @Column(name = "customer_name_sei")
    String customerNameSei;

    /** 顧客姓かな */
    @Column(name = "customer_name_sei_kana")
    String customerNameSeiKana;

    /** 顧客名 */
    @Column(name = "customer_name_mei")
    String customerNameMei;

    /** 顧客名かな */
    @Column(name = "customer_name_mei_kana")
    String customerNameMeiKana;

    /** 郵便番号 */
    @Column(name = "zip_code")
    String zipCode;

    /** 地域 */
    @Column(name = "address1")
    String address1;

    /** 番地・建物名 */
    @Column(name = "address2")
    String address2;

    /** 住所備考 */
    @Column(name = "address_remarks")
    String addressRemarks;

    /** 電話連絡可否 */
    @Column(name = "contact_type")
    String contactType;

    /** 連絡先備考 */
    @Column(name = "contact_remarks")
    String contactRemarks;

    /** 郵送先住所区分 */
    @Column(name = "transfer_address_type")
    String transferAddressType;

    /** 郵送先郵便番号 */
    @Column(name = "transfer_zip_code")
    String transferZipCode;

    /** 郵送先地域 */
    @Column(name = "transfer_address1")
    String transferAddress1;

    /** 郵送先番地・建物名 */
    @Column(name = "transfer_address2")
    String transferAddress2;

    /** 宛名 */
    @Column(name = "transfer_name")
    String transferName;

    /** 郵送方法 */
    @Column(name = "transfer_type")
    String transferType;

    /** 郵送先備考 */
    @Column(name = "transfer_remarks")
    String transferRemarks;

    /** 顧客登録日 */
    @Column(name = "customer_created_date")
    LocalDate customerCreatedDate;

    /** 特記事項 */
    @Column(name = "remarks")
    String remarks;

    /** 相談経路 */
    @Column(name = "sodan_route")
    Long sodanRoute;

    /** 相談経路備考 */
    @Column(name = "sodan_remarks")
    String sodanRemarks;

    /** 追加情報 */
    @Column(name = "add_info")
    Long addInfo;

    /** 追加情報備考 */
    @Column(name = "add_info_remarks")
    String addInfoRemarks;

    /** 銀行名 */
    @Column(name = "ginko_name")
    String ginkoName;

    /** 支店名 */
    @Column(name = "shiten_name")
    String shitenName;

    /** 支店番号 */
    @Column(name = "shiten_no")
    String shitenNo;

    /** 口座種類 */
    @Column(name = "koza_type")
    String kozaType;

    /** 口座番号 */
    @Column(name = "koza_no")
    String kozaNo;

    /** 口座名義 */
    @Column(name = "koza_name")
    String kozaName;

    /** 口座名義カナ */
    @Column(name = "koza_name_kana")
    String kozaNameKana;

    /** 口座備考 */
    @Column(name = "koza_remarks")
    String kozaRemarks;

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
     * Returns the customerId.
     * 
     * @return the customerId
     */
    public Long getCustomerId() {
        return customerId;
    }

    /** 
     * Sets the customerId.
     * 
     * @param customerId the customerId
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /** 
     * Returns the customerFlg.
     * 
     * @return the customerFlg
     */
    public String getCustomerFlg() {
        return customerFlg;
    }

    /** 
     * Sets the customerFlg.
     * 
     * @param customerFlg the customerFlg
     */
    public void setCustomerFlg(String customerFlg) {
        this.customerFlg = customerFlg;
    }

    /** 
     * Returns the advisorFlg.
     * 
     * @return the advisorFlg
     */
    public String getAdvisorFlg() {
        return advisorFlg;
    }

    /** 
     * Sets the advisorFlg.
     * 
     * @param advisorFlg the advisorFlg
     */
    public void setAdvisorFlg(String advisorFlg) {
        this.advisorFlg = advisorFlg;
    }

    /** 
     * Returns the customerType.
     * 
     * @return the customerType
     */
    public String getCustomerType() {
        return customerType;
    }

    /** 
     * Sets the customerType.
     * 
     * @param customerType the customerType
     */
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    /** 
     * Returns the customerNameSei.
     * 
     * @return the customerNameSei
     */
    public String getCustomerNameSei() {
        return customerNameSei;
    }

    /** 
     * Sets the customerNameSei.
     * 
     * @param customerNameSei the customerNameSei
     */
    public void setCustomerNameSei(String customerNameSei) {
        this.customerNameSei = customerNameSei;
    }

    /** 
     * Returns the customerNameSeiKana.
     * 
     * @return the customerNameSeiKana
     */
    public String getCustomerNameSeiKana() {
        return customerNameSeiKana;
    }

    /** 
     * Sets the customerNameSeiKana.
     * 
     * @param customerNameSeiKana the customerNameSeiKana
     */
    public void setCustomerNameSeiKana(String customerNameSeiKana) {
        this.customerNameSeiKana = customerNameSeiKana;
    }

    /** 
     * Returns the customerNameMei.
     * 
     * @return the customerNameMei
     */
    public String getCustomerNameMei() {
        return customerNameMei;
    }

    /** 
     * Sets the customerNameMei.
     * 
     * @param customerNameMei the customerNameMei
     */
    public void setCustomerNameMei(String customerNameMei) {
        this.customerNameMei = customerNameMei;
    }

    /** 
     * Returns the customerNameMeiKana.
     * 
     * @return the customerNameMeiKana
     */
    public String getCustomerNameMeiKana() {
        return customerNameMeiKana;
    }

    /** 
     * Sets the customerNameMeiKana.
     * 
     * @param customerNameMeiKana the customerNameMeiKana
     */
    public void setCustomerNameMeiKana(String customerNameMeiKana) {
        this.customerNameMeiKana = customerNameMeiKana;
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
     * Returns the addressRemarks.
     * 
     * @return the addressRemarks
     */
    public String getAddressRemarks() {
        return addressRemarks;
    }

    /** 
     * Sets the addressRemarks.
     * 
     * @param addressRemarks the addressRemarks
     */
    public void setAddressRemarks(String addressRemarks) {
        this.addressRemarks = addressRemarks;
    }

    /** 
     * Returns the contactType.
     * 
     * @return the contactType
     */
    public String getContactType() {
        return contactType;
    }

    /** 
     * Sets the contactType.
     * 
     * @param contactType the contactType
     */
    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    /** 
     * Returns the contactRemarks.
     * 
     * @return the contactRemarks
     */
    public String getContactRemarks() {
        return contactRemarks;
    }

    /** 
     * Sets the contactRemarks.
     * 
     * @param contactRemarks the contactRemarks
     */
    public void setContactRemarks(String contactRemarks) {
        this.contactRemarks = contactRemarks;
    }

    /** 
     * Returns the transferAddressType.
     * 
     * @return the transferAddressType
     */
    public String getTransferAddressType() {
        return transferAddressType;
    }

    /** 
     * Sets the transferAddressType.
     * 
     * @param transferAddressType the transferAddressType
     */
    public void setTransferAddressType(String transferAddressType) {
        this.transferAddressType = transferAddressType;
    }

    /** 
     * Returns the transferZipCode.
     * 
     * @return the transferZipCode
     */
    public String getTransferZipCode() {
        return transferZipCode;
    }

    /** 
     * Sets the transferZipCode.
     * 
     * @param transferZipCode the transferZipCode
     */
    public void setTransferZipCode(String transferZipCode) {
        this.transferZipCode = transferZipCode;
    }

    /** 
     * Returns the transferAddress1.
     * 
     * @return the transferAddress1
     */
    public String getTransferAddress1() {
        return transferAddress1;
    }

    /** 
     * Sets the transferAddress1.
     * 
     * @param transferAddress1 the transferAddress1
     */
    public void setTransferAddress1(String transferAddress1) {
        this.transferAddress1 = transferAddress1;
    }

    /** 
     * Returns the transferAddress2.
     * 
     * @return the transferAddress2
     */
    public String getTransferAddress2() {
        return transferAddress2;
    }

    /** 
     * Sets the transferAddress2.
     * 
     * @param transferAddress2 the transferAddress2
     */
    public void setTransferAddress2(String transferAddress2) {
        this.transferAddress2 = transferAddress2;
    }

    /** 
     * Returns the transferName.
     * 
     * @return the transferName
     */
    public String getTransferName() {
        return transferName;
    }

    /** 
     * Sets the transferName.
     * 
     * @param transferName the transferName
     */
    public void setTransferName(String transferName) {
        this.transferName = transferName;
    }

    /** 
     * Returns the transferType.
     * 
     * @return the transferType
     */
    public String getTransferType() {
        return transferType;
    }

    /** 
     * Sets the transferType.
     * 
     * @param transferType the transferType
     */
    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    /** 
     * Returns the transferRemarks.
     * 
     * @return the transferRemarks
     */
    public String getTransferRemarks() {
        return transferRemarks;
    }

    /** 
     * Sets the transferRemarks.
     * 
     * @param transferRemarks the transferRemarks
     */
    public void setTransferRemarks(String transferRemarks) {
        this.transferRemarks = transferRemarks;
    }

    /** 
     * Returns the customerCreatedDate.
     * 
     * @return the customerCreatedDate
     */
    public LocalDate getCustomerCreatedDate() {
        return customerCreatedDate;
    }

    /** 
     * Sets the customerCreatedDate.
     * 
     * @param customerCreatedDate the customerCreatedDate
     */
    public void setCustomerCreatedDate(LocalDate customerCreatedDate) {
        this.customerCreatedDate = customerCreatedDate;
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
     * Returns the sodanRoute.
     * 
     * @return the sodanRoute
     */
    public Long getSodanRoute() {
        return sodanRoute;
    }

    /** 
     * Sets the sodanRoute.
     * 
     * @param sodanRoute the sodanRoute
     */
    public void setSodanRoute(Long sodanRoute) {
        this.sodanRoute = sodanRoute;
    }

    /** 
     * Returns the sodanRemarks.
     * 
     * @return the sodanRemarks
     */
    public String getSodanRemarks() {
        return sodanRemarks;
    }

    /** 
     * Sets the sodanRemarks.
     * 
     * @param sodanRemarks the sodanRemarks
     */
    public void setSodanRemarks(String sodanRemarks) {
        this.sodanRemarks = sodanRemarks;
    }

    /** 
     * Returns the addInfo.
     * 
     * @return the addInfo
     */
    public Long getAddInfo() {
        return addInfo;
    }

    /** 
     * Sets the addInfo.
     * 
     * @param addInfo the addInfo
     */
    public void setAddInfo(Long addInfo) {
        this.addInfo = addInfo;
    }

    /** 
     * Returns the addInfoRemarks.
     * 
     * @return the addInfoRemarks
     */
    public String getAddInfoRemarks() {
        return addInfoRemarks;
    }

    /** 
     * Sets the addInfoRemarks.
     * 
     * @param addInfoRemarks the addInfoRemarks
     */
    public void setAddInfoRemarks(String addInfoRemarks) {
        this.addInfoRemarks = addInfoRemarks;
    }

    /** 
     * Returns the ginkoName.
     * 
     * @return the ginkoName
     */
    public String getGinkoName() {
        return ginkoName;
    }

    /** 
     * Sets the ginkoName.
     * 
     * @param ginkoName the ginkoName
     */
    public void setGinkoName(String ginkoName) {
        this.ginkoName = ginkoName;
    }

    /** 
     * Returns the shitenName.
     * 
     * @return the shitenName
     */
    public String getShitenName() {
        return shitenName;
    }

    /** 
     * Sets the shitenName.
     * 
     * @param shitenName the shitenName
     */
    public void setShitenName(String shitenName) {
        this.shitenName = shitenName;
    }

    /** 
     * Returns the shitenNo.
     * 
     * @return the shitenNo
     */
    public String getShitenNo() {
        return shitenNo;
    }

    /** 
     * Sets the shitenNo.
     * 
     * @param shitenNo the shitenNo
     */
    public void setShitenNo(String shitenNo) {
        this.shitenNo = shitenNo;
    }

    /** 
     * Returns the kozaType.
     * 
     * @return the kozaType
     */
    public String getKozaType() {
        return kozaType;
    }

    /** 
     * Sets the kozaType.
     * 
     * @param kozaType the kozaType
     */
    public void setKozaType(String kozaType) {
        this.kozaType = kozaType;
    }

    /** 
     * Returns the kozaNo.
     * 
     * @return the kozaNo
     */
    public String getKozaNo() {
        return kozaNo;
    }

    /** 
     * Sets the kozaNo.
     * 
     * @param kozaNo the kozaNo
     */
    public void setKozaNo(String kozaNo) {
        this.kozaNo = kozaNo;
    }

    /** 
     * Returns the kozaName.
     * 
     * @return the kozaName
     */
    public String getKozaName() {
        return kozaName;
    }

    /** 
     * Sets the kozaName.
     * 
     * @param kozaName the kozaName
     */
    public void setKozaName(String kozaName) {
        this.kozaName = kozaName;
    }

    /** 
     * Returns the kozaNameKana.
     * 
     * @return the kozaNameKana
     */
    public String getKozaNameKana() {
        return kozaNameKana;
    }

    /** 
     * Sets the kozaNameKana.
     * 
     * @param kozaNameKana the kozaNameKana
     */
    public void setKozaNameKana(String kozaNameKana) {
        this.kozaNameKana = kozaNameKana;
    }

    /** 
     * Returns the kozaRemarks.
     * 
     * @return the kozaRemarks
     */
    public String getKozaRemarks() {
        return kozaRemarks;
    }

    /** 
     * Sets the kozaRemarks.
     * 
     * @param kozaRemarks the kozaRemarks
     */
    public void setKozaRemarks(String kozaRemarks) {
        this.kozaRemarks = kozaRemarks;
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