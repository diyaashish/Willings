package jp.loioz.entity;

import java.sql.Blob;
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
 * アカウント画像
 */
@Entity(listener = MAccountImgEntityListener.class)
@Table(name = "m_account_img")
public class MAccountImgEntity extends DefaultEntity {

    /** アカウント画像SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_img_seq")
    Long accountImgSeq;

    /** アカウントSEQ */
    @Column(name = "account_seq")
    Long accountSeq;

    /** 画像 */
    @Column(name = "img_contents")
    Blob imgContents;

    /** 画像タイプ */
    @Column(name = "img_type")
    String imgType;

    /** 拡張子 */
    @Column(name = "img_extension")
    String imgExtension;

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
     * Returns the accountImgSeq.
     * 
     * @return the accountImgSeq
     */
    public Long getAccountImgSeq() {
        return accountImgSeq;
    }

    /** 
     * Sets the accountImgSeq.
     * 
     * @param accountImgSeq the accountImgSeq
     */
    public void setAccountImgSeq(Long accountImgSeq) {
        this.accountImgSeq = accountImgSeq;
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
     * Returns the imgContents.
     * 
     * @return the imgContents
     */
    public Blob getImgContents() {
        return imgContents;
    }

    /** 
     * Sets the imgContents.
     * 
     * @param imgContents the imgContents
     */
    public void setImgContents(Blob imgContents) {
        this.imgContents = imgContents;
    }

    /** 
     * Returns the imgType.
     * 
     * @return the imgType
     */
    public String getImgType() {
        return imgType;
    }

    /** 
     * Sets the imgType.
     * 
     * @param imgType the imgType
     */
    public void setImgType(String imgType) {
        this.imgType = imgType;
    }

    /** 
     * Returns the imgExtension.
     * 
     * @return the imgExtension
     */
    public String getImgExtension() {
        return imgExtension;
    }

    /** 
     * Sets the imgExtension.
     * 
     * @param imgExtension the imgExtension
     */
    public void setImgExtension(String imgExtension) {
        this.imgExtension = imgExtension;
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