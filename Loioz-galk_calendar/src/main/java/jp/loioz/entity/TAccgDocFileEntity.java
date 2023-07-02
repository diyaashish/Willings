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
 * 会計書類ファイル
 */
@Entity(listener = TAccgDocFileEntityListener.class)
@Table(name = "t_accg_doc_file")
public class TAccgDocFileEntity extends DefaultEntity {

    /** 会計書類ファイルSEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accg_doc_file_seq")
    Long accgDocFileSeq;

    /** 会計書類SEQ */
    @Column(name = "accg_doc_seq")
    Long accgDocSeq;

    /** 会計書類ファイル種別 */
    @Column(name = "accg_doc_file_type")
    String accgDocFileType;

    /** 拡張子（PDF、PNG） */
    @Column(name = "file_extension")
    String fileExtension;

    /** 再作成実行待ちフラグ */
    @Column(name = "recreate_standby_flg")
    String recreateStandbyFlg;

    /** 登録日時 */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** 登録アカウントID */
    @Column(name = "created_by")
    Long createdBy;

    /** 更新日時 */
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    /** 更新アカウントID */
    @Column(name = "updated_by")
    Long updatedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the accgDocFileSeq.
     * 
     * @return the accgDocFileSeq
     */
    public Long getAccgDocFileSeq() {
        return accgDocFileSeq;
    }

    /** 
     * Sets the accgDocFileSeq.
     * 
     * @param accgDocFileSeq the accgDocFileSeq
     */
    public void setAccgDocFileSeq(Long accgDocFileSeq) {
        this.accgDocFileSeq = accgDocFileSeq;
    }

    /** 
     * Returns the accgDocSeq.
     * 
     * @return the accgDocSeq
     */
    public Long getAccgDocSeq() {
        return accgDocSeq;
    }

    /** 
     * Sets the accgDocSeq.
     * 
     * @param accgDocSeq the accgDocSeq
     */
    public void setAccgDocSeq(Long accgDocSeq) {
        this.accgDocSeq = accgDocSeq;
    }

    /** 
     * Returns the accgDocFileType.
     * 
     * @return the accgDocFileType
     */
    public String getAccgDocFileType() {
        return accgDocFileType;
    }

    /** 
     * Sets the accgDocFileType.
     * 
     * @param accgDocFileType the accgDocFileType
     */
    public void setAccgDocFileType(String accgDocFileType) {
        this.accgDocFileType = accgDocFileType;
    }

    /** 
     * Returns the fileExtension.
     * 
     * @return the fileExtension
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /** 
     * Sets the fileExtension.
     * 
     * @param fileExtension the fileExtension
     */
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    /** 
     * Returns the recreateStandbyFlg.
     * 
     * @return the recreateStandbyFlg
     */
    public String getRecreateStandbyFlg() {
        return recreateStandbyFlg;
    }

    /** 
     * Sets the recreateStandbyFlg.
     * 
     * @param recreateStandbyFlg the recreateStandbyFlg
     */
    public void setRecreateStandbyFlg(String recreateStandbyFlg) {
        this.recreateStandbyFlg = recreateStandbyFlg;
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