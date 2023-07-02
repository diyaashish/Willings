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
 * 銀行口座
 */
@Entity(listener = TGinkoKozaEntityListener.class)
@Table(name = "t_ginko_koza")
public class TGinkoKozaEntity extends DefaultEntity {

    /** 銀行口座SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ginko_account_seq")
    Long ginkoAccountSeq;

    /** テナントSEQ */
    @Column(name = "tenant_seq")
    Long tenantSeq;

    /** アカウントSEQ */
    @Column(name = "account_seq")
    Long accountSeq;

    /** 口座連番 */
    @Column(name = "branch_no")
    Long branchNo;

    /** 表示名 */
    @Column(name = "label_name")
    String labelName;

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

    /** 口座名義かな */
    @Column(name = "koza_name_kana")
    String kozaNameKana;

    /** 既定利用フラグ */
    @Column(name = "default_use_flg")
    String defaultUseFlg;

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
     * Returns the ginkoAccountSeq.
     * 
     * @return the ginkoAccountSeq
     */
    public Long getGinkoAccountSeq() {
        return ginkoAccountSeq;
    }

    /** 
     * Sets the ginkoAccountSeq.
     * 
     * @param ginkoAccountSeq the ginkoAccountSeq
     */
    public void setGinkoAccountSeq(Long ginkoAccountSeq) {
        this.ginkoAccountSeq = ginkoAccountSeq;
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
     * Returns the accountSeq.
     * 
     * @return the accountSeq
     */
    public Long getAccountSeq() {
        return accountSeq;
    }

    /** 
     * Sets the accountSeq.
     * 
     * @param accountSeq the accountSeq
     */
    public void setAccountSeq(Long accountSeq) {
        this.accountSeq = accountSeq;
    }

    /** 
     * Returns the branchNo.
     * 
     * @return the branchNo
     */
    public Long getBranchNo() {
        return branchNo;
    }

    /** 
     * Sets the branchNo.
     * 
     * @param branchNo the branchNo
     */
    public void setBranchNo(Long branchNo) {
        this.branchNo = branchNo;
    }

    /** 
     * Returns the labelName.
     * 
     * @return the labelName
     */
    public String getLabelName() {
        return labelName;
    }

    /** 
     * Sets the labelName.
     * 
     * @param labelName the labelName
     */
    public void setLabelName(String labelName) {
        this.labelName = labelName;
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
     * Returns the defaultUseFlg.
     * 
     * @return the defaultUseFlg
     */
    public String getDefaultUseFlg() {
        return defaultUseFlg;
    }

    /** 
     * Sets the defaultUseFlg.
     * 
     * @param defaultUseFlg the defaultUseFlg
     */
    public void setDefaultUseFlg(String defaultUseFlg) {
        this.defaultUseFlg = defaultUseFlg;
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