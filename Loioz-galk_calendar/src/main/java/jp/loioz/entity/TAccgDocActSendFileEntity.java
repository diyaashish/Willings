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
 * 会計書類-対応-送付-ファイル
 */
@Entity(listener = TAccgDocActSendFileEntityListener.class)
@Table(name = "t_accg_doc_act_send_file")
public class TAccgDocActSendFileEntity extends DefaultEntity {

    /** 会計書類対応送付ファイルSEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accg_doc_act_send_file_seq")
    Long accgDocActSendFileSeq;

    /** 会計書類対応送付SEQ */
    @Column(name = "accg_doc_act_send_seq")
    Long accgDocActSendSeq;

    /** 会計書類ファイルSEQ */
    @Column(name = "accg_doc_file_seq")
    Long accgDocFileSeq;

    /** 最終ダウンロード時間 */
    @Column(name = "download_last_at")
    LocalDateTime downloadLastAt;

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
     * Returns the accgDocActSendFileSeq.
     * 
     * @return the accgDocActSendFileSeq
     */
    public Long getAccgDocActSendFileSeq() {
        return accgDocActSendFileSeq;
    }

    /** 
     * Sets the accgDocActSendFileSeq.
     * 
     * @param accgDocActSendFileSeq the accgDocActSendFileSeq
     */
    public void setAccgDocActSendFileSeq(Long accgDocActSendFileSeq) {
        this.accgDocActSendFileSeq = accgDocActSendFileSeq;
    }

    /** 
     * Returns the accgDocActSendSeq.
     * 
     * @return the accgDocActSendSeq
     */
    public Long getAccgDocActSendSeq() {
        return accgDocActSendSeq;
    }

    /** 
     * Sets the accgDocActSendSeq.
     * 
     * @param accgDocActSendSeq the accgDocActSendSeq
     */
    public void setAccgDocActSendSeq(Long accgDocActSendSeq) {
        this.accgDocActSendSeq = accgDocActSendSeq;
    }

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
     * Returns the downloadLastAt.
     * 
     * @return the downloadLastAt
     */
    public LocalDateTime getDownloadLastAt() {
        return downloadLastAt;
    }

    /** 
     * Sets the downloadLastAt.
     * 
     * @param downloadLastAt the downloadLastAt
     */
    public void setDownloadLastAt(LocalDateTime downloadLastAt) {
        this.downloadLastAt = downloadLastAt;
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