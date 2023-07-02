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
 * お知らせ管理
 */
@Entity(listener = TInfoMgtEntityListener.class)
@Table(name = "t_info_mgt")
public class TInfoMgtEntity extends DefaultEntity {

    /** お知らせMTG連番 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "info_mgt_seq")
    Long infoMgtSeq;

    /** お知らせ区分 */
    @Column(name = "info_type")
    String infoType;

    /** 件名 */
    @Column(name = "info_subject")
    String infoSubject;

    /** 本文 */
    @Column(name = "info_body")
    String infoBody;

    /** 公開フラグ */
    @Column(name = "info_open_flg")
    String infoOpenFlg;

    /** 掲載開始日時 */
    @Column(name = "info_start_at")
    LocalDateTime infoStartAt;

    /** 掲載終了日時 */
    @Column(name = "info_end_at")
    LocalDateTime infoEndAt;

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
     * Returns the infoMgtSeq.
     * 
     * @return the infoMgtSeq
     */
    public Long getInfoMgtSeq() {
        return infoMgtSeq;
    }

    /** 
     * Sets the infoMgtSeq.
     * 
     * @param infoMgtSeq the infoMgtSeq
     */
    public void setInfoMgtSeq(Long infoMgtSeq) {
        this.infoMgtSeq = infoMgtSeq;
    }

    /** 
     * Returns the infoType.
     * 
     * @return the infoType
     */
    public String getInfoType() {
        return infoType;
    }

    /** 
     * Sets the infoType.
     * 
     * @param infoType the infoType
     */
    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    /** 
     * Returns the infoSubject.
     * 
     * @return the infoSubject
     */
    public String getInfoSubject() {
        return infoSubject;
    }

    /** 
     * Sets the infoSubject.
     * 
     * @param infoSubject the infoSubject
     */
    public void setInfoSubject(String infoSubject) {
        this.infoSubject = infoSubject;
    }

    /** 
     * Returns the infoBody.
     * 
     * @return the infoBody
     */
    public String getInfoBody() {
        return infoBody;
    }

    /** 
     * Sets the infoBody.
     * 
     * @param infoBody the infoBody
     */
    public void setInfoBody(String infoBody) {
        this.infoBody = infoBody;
    }

    /** 
     * Returns the infoOpenFlg.
     * 
     * @return the infoOpenFlg
     */
    public String getInfoOpenFlg() {
        return infoOpenFlg;
    }

    /** 
     * Sets the infoOpenFlg.
     * 
     * @param infoOpenFlg the infoOpenFlg
     */
    public void setInfoOpenFlg(String infoOpenFlg) {
        this.infoOpenFlg = infoOpenFlg;
    }

    /** 
     * Returns the infoStartAt.
     * 
     * @return the infoStartAt
     */
    public LocalDateTime getInfoStartAt() {
        return infoStartAt;
    }

    /** 
     * Sets the infoStartAt.
     * 
     * @param infoStartAt the infoStartAt
     */
    public void setInfoStartAt(LocalDateTime infoStartAt) {
        this.infoStartAt = infoStartAt;
    }

    /** 
     * Returns the infoEndAt.
     * 
     * @return the infoEndAt
     */
    public LocalDateTime getInfoEndAt() {
        return infoEndAt;
    }

    /** 
     * Sets the infoEndAt.
     * 
     * @param infoEndAt the infoEndAt
     */
    public void setInfoEndAt(LocalDateTime infoEndAt) {
        this.infoEndAt = infoEndAt;
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