package jp.loioz.entity;

import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * 法人名簿付帯情報
 */
@Entity(listener = TPersonAddHojinEntityListener.class)
@Table(name = "t_person_add_hojin")
public class TPersonAddHojinEntity extends DefaultEntity {

    /** 名簿ID */
    @Id
    @Column(name = "person_id")
    Long personId;

    /** 代表者 */
    @Column(name = "daihyo_name")
    String daihyoName;

    /** 代表者かな */
    @Column(name = "daihyo_name_kana")
    String daihyoNameKana;

    /** 代表者役職 */
    @Column(name = "daihyo_position_name")
    String daihyoPositionName;

    /** 担当者 */
    @Column(name = "tanto_name")
    String tantoName;

    /** 担当者かな */
    @Column(name = "tanto_name_kana")
    String tantoNameKana;

    /** 登記住所区分 */
    @Column(name = "toki_address_type")
    String tokiAddressType;

    /** 登記郵便番号 */
    @Column(name = "toki_zip_code")
    String tokiZipCode;

    /** 登記地域 */
    @Column(name = "toki_address1")
    String tokiAddress1;

    /** 登記番地・建物名 */
    @Column(name = "toki_address2")
    String tokiAddress2;

    /** 登記住所備考 */
    @Column(name = "toki_address_remarks")
    String tokiAddressRemarks;

    /** 旧商号 */
    @Column(name = "old_hojin_name")
    String oldHojinName;

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
     * Returns the daihyoName.
     * 
     * @return the daihyoName
     */
    public String getDaihyoName() {
        return daihyoName;
    }

    /** 
     * Sets the daihyoName.
     * 
     * @param daihyoName the daihyoName
     */
    public void setDaihyoName(String daihyoName) {
        this.daihyoName = daihyoName;
    }

    /** 
     * Returns the daihyoNameKana.
     * 
     * @return the daihyoNameKana
     */
    public String getDaihyoNameKana() {
        return daihyoNameKana;
    }

    /** 
     * Sets the daihyoNameKana.
     * 
     * @param daihyoNameKana the daihyoNameKana
     */
    public void setDaihyoNameKana(String daihyoNameKana) {
        this.daihyoNameKana = daihyoNameKana;
    }

    /** 
     * Returns the daihyoPositionName.
     * 
     * @return the daihyoPositionName
     */
    public String getDaihyoPositionName() {
        return daihyoPositionName;
    }

    /** 
     * Sets the daihyoPositionName.
     * 
     * @param daihyoPositionName the daihyoPositionName
     */
    public void setDaihyoPositionName(String daihyoPositionName) {
        this.daihyoPositionName = daihyoPositionName;
    }

    /** 
     * Returns the tantoName.
     * 
     * @return the tantoName
     */
    public String getTantoName() {
        return tantoName;
    }

    /** 
     * Sets the tantoName.
     * 
     * @param tantoName the tantoName
     */
    public void setTantoName(String tantoName) {
        this.tantoName = tantoName;
    }

    /** 
     * Returns the tantoNameKana.
     * 
     * @return the tantoNameKana
     */
    public String getTantoNameKana() {
        return tantoNameKana;
    }

    /** 
     * Sets the tantoNameKana.
     * 
     * @param tantoNameKana the tantoNameKana
     */
    public void setTantoNameKana(String tantoNameKana) {
        this.tantoNameKana = tantoNameKana;
    }

    /** 
     * Returns the tokiAddressType.
     * 
     * @return the tokiAddressType
     */
    public String getTokiAddressType() {
        return tokiAddressType;
    }

    /** 
     * Sets the tokiAddressType.
     * 
     * @param tokiAddressType the tokiAddressType
     */
    public void setTokiAddressType(String tokiAddressType) {
        this.tokiAddressType = tokiAddressType;
    }

    /** 
     * Returns the tokiZipCode.
     * 
     * @return the tokiZipCode
     */
    public String getTokiZipCode() {
        return tokiZipCode;
    }

    /** 
     * Sets the tokiZipCode.
     * 
     * @param tokiZipCode the tokiZipCode
     */
    public void setTokiZipCode(String tokiZipCode) {
        this.tokiZipCode = tokiZipCode;
    }

    /** 
     * Returns the tokiAddress1.
     * 
     * @return the tokiAddress1
     */
    public String getTokiAddress1() {
        return tokiAddress1;
    }

    /** 
     * Sets the tokiAddress1.
     * 
     * @param tokiAddress1 the tokiAddress1
     */
    public void setTokiAddress1(String tokiAddress1) {
        this.tokiAddress1 = tokiAddress1;
    }

    /** 
     * Returns the tokiAddress2.
     * 
     * @return the tokiAddress2
     */
    public String getTokiAddress2() {
        return tokiAddress2;
    }

    /** 
     * Sets the tokiAddress2.
     * 
     * @param tokiAddress2 the tokiAddress2
     */
    public void setTokiAddress2(String tokiAddress2) {
        this.tokiAddress2 = tokiAddress2;
    }

    /** 
     * Returns the tokiAddressRemarks.
     * 
     * @return the tokiAddressRemarks
     */
    public String getTokiAddressRemarks() {
        return tokiAddressRemarks;
    }

    /** 
     * Sets the tokiAddressRemarks.
     * 
     * @param tokiAddressRemarks the tokiAddressRemarks
     */
    public void setTokiAddressRemarks(String tokiAddressRemarks) {
        this.tokiAddressRemarks = tokiAddressRemarks;
    }

    /** 
     * Returns the oldHojinName.
     * 
     * @return the oldHojinName
     */
    public String getOldHojinName() {
        return oldHojinName;
    }

    /** 
     * Sets the oldHojinName.
     * 
     * @param oldHojinName the oldHojinName
     */
    public void setOldHojinName(String oldHojinName) {
        this.oldHojinName = oldHojinName;
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