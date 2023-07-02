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
 * 業務履歴
 */
@Entity(listener = TGyomuHistoryEntityListener.class)
@Table(name = "t_gyomu_history")
public class TGyomuHistoryEntity extends DefaultEntity {

    /** 業務履歴SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gyomu_history_seq")
    Long gyomuHistorySeq;

    /** 遷移元種別 */
    @Column(name = "transition_type")
    String transitionType;

    /** 裁判SEQ */
    @Column(name = "saiban_seq")
    Long saibanSeq;

    /** 件名 */
    @Column(name = "subject")
    String subject;

    /** 本文 */
    @Column(name = "main_text")
    String mainText;

    /** 重要 */
    @Column(name = "important_flg")
    String importantFlg;

    /** 固定 */
    @Column(name = "kotei_flg")
    String koteiFlg;

    /** 対応日時 */
    @Column(name = "supported_at")
    LocalDateTime supportedAt;

    /** 最終編集日時 */
    @Column(name = "last_edit_at")
    LocalDateTime lastEditAt;

    /** 最終編集アカウントSEQ */
    @Column(name = "last_edit_by")
    Long lastEditBy;

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

    /** 伝言送信済みフラグ */
    @Column(name = "dengon_sent_flg")
    String dengonSentFlg;

    /** 
     * Returns the gyomuHistorySeq.
     * 
     * @return the gyomuHistorySeq
     */
    public Long getGyomuHistorySeq() {
        return gyomuHistorySeq;
    }

    /** 
     * Sets the gyomuHistorySeq.
     * 
     * @param gyomuHistorySeq the gyomuHistorySeq
     */
    public void setGyomuHistorySeq(Long gyomuHistorySeq) {
        this.gyomuHistorySeq = gyomuHistorySeq;
    }

    /** 
     * Returns the transitionType.
     * 
     * @return the transitionType
     */
    public String getTransitionType() {
        return transitionType;
    }

    /** 
     * Sets the transitionType.
     * 
     * @param transitionType the transitionType
     */
    public void setTransitionType(String transitionType) {
        this.transitionType = transitionType;
    }

    /** 
     * Returns the saibanSeq.
     * 
     * @return the saibanSeq
     */
    public Long getSaibanSeq() {
        return saibanSeq;
    }

    /** 
     * Sets the saibanSeq.
     * 
     * @param saibanSeq the saibanSeq
     */
    public void setSaibanSeq(Long saibanSeq) {
        this.saibanSeq = saibanSeq;
    }

    /** 
     * Returns the subject.
     * 
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /** 
     * Sets the subject.
     * 
     * @param subject the subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /** 
     * Returns the mainText.
     * 
     * @return the mainText
     */
    public String getMainText() {
        return mainText;
    }

    /** 
     * Sets the mainText.
     * 
     * @param mainText the mainText
     */
    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    /** 
     * Returns the importantFlg.
     * 
     * @return the importantFlg
     */
    public String getImportantFlg() {
        return importantFlg;
    }

    /** 
     * Sets the importantFlg.
     * 
     * @param importantFlg the importantFlg
     */
    public void setImportantFlg(String importantFlg) {
        this.importantFlg = importantFlg;
    }

    /** 
     * Returns the koteiFlg.
     * 
     * @return the koteiFlg
     */
    public String getKoteiFlg() {
        return koteiFlg;
    }

    /** 
     * Sets the koteiFlg.
     * 
     * @param koteiFlg the koteiFlg
     */
    public void setKoteiFlg(String koteiFlg) {
        this.koteiFlg = koteiFlg;
    }

    /** 
     * Returns the supportedAt.
     * 
     * @return the supportedAt
     */
    public LocalDateTime getSupportedAt() {
        return supportedAt;
    }

    /** 
     * Sets the supportedAt.
     * 
     * @param supportedAt the supportedAt
     */
    public void setSupportedAt(LocalDateTime supportedAt) {
        this.supportedAt = supportedAt;
    }

    /** 
     * Returns the lastEditAt.
     * 
     * @return the lastEditAt
     */
    public LocalDateTime getLastEditAt() {
        return lastEditAt;
    }

    /** 
     * Sets the lastEditAt.
     * 
     * @param lastEditAt the lastEditAt
     */
    public void setLastEditAt(LocalDateTime lastEditAt) {
        this.lastEditAt = lastEditAt;
    }

    /** 
     * Returns the lastEditBy.
     * 
     * @return the lastEditBy
     */
    public Long getLastEditBy() {
        return lastEditBy;
    }

    /** 
     * Sets the lastEditBy.
     * 
     * @param lastEditBy the lastEditBy
     */
    public void setLastEditBy(Long lastEditBy) {
        this.lastEditBy = lastEditBy;
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

    /** 
     * Returns the dengonSentFlg.
     * 
     * @return the dengonSentFlg
     */
    public String getDengonSentFlg() {
        return dengonSentFlg;
    }

    /** 
     * Sets the dengonSentFlg.
     * 
     * @param dengonSentFlg the dengonSentFlg
     */
    public void setDengonSentFlg(String dengonSentFlg) {
        this.dengonSentFlg = dengonSentFlg;
    }
}