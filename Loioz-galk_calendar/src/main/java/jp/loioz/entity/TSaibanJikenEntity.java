package jp.loioz.entity;

import java.time.LocalDateTime;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import jp.loioz.entity.common.DefaultEntity;

/**
 * 
 */
@Entity(listener = TSaibanJikenEntityListener.class)
@Table(name = "t_saiban_jiken")
public class TSaibanJikenEntity extends DefaultEntity {

    /** 事件SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jiken_seq")
    Long jikenSeq;

    /** 裁判SEQ */
    @Column(name = "saiban_seq")
    Long saibanSeq;

    /** 事件番号元号 */
    @Column(name = "jiken_gengo")
    String jikenGengo;

    /** 事件番号年 */
    @Column(name = "jiken_year")
    String jikenYear;

    /** 事件番号符号 */
    @Column(name = "jiken_mark")
    String jikenMark;

    /** 事件番号 */
    @Column(name = "jiken_no")
    String jikenNo;

    /** 事件名 */
    @Column(name = "jiken_name")
    String jikenName;

    /** 登録日 */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** 登録アカウントSEQ */
    @Column(name = "created_by")
    Long createdBy;

    /** 更新日 */
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
     * Returns the jikenSeq.
     * 
     * @return the jikenSeq
     */
    public Long getJikenSeq() {
        return jikenSeq;
    }

    /** 
     * Sets the jikenSeq.
     * 
     * @param jikenSeq the jikenSeq
     */
    public void setJikenSeq(Long jikenSeq) {
        this.jikenSeq = jikenSeq;
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
     * Returns the jikenGengo.
     * 
     * @return the jikenGengo
     */
    public String getJikenGengo() {
        return jikenGengo;
    }

    /** 
     * Sets the jikenGengo.
     * 
     * @param jikenGengo the jikenGengo
     */
    public void setJikenGengo(String jikenGengo) {
        this.jikenGengo = jikenGengo;
    }

    /** 
     * Returns the jikenYear.
     * 
     * @return the jikenYear
     */
    public String getJikenYear() {
        return jikenYear;
    }

    /** 
     * Sets the jikenYear.
     * 
     * @param jikenYear the jikenYear
     */
    public void setJikenYear(String jikenYear) {
        this.jikenYear = jikenYear;
    }

    /** 
     * Returns the jikenMark.
     * 
     * @return the jikenMark
     */
    public String getJikenMark() {
        return jikenMark;
    }

    /** 
     * Sets the jikenMark.
     * 
     * @param jikenMark the jikenMark
     */
    public void setJikenMark(String jikenMark) {
        this.jikenMark = jikenMark;
    }

    /** 
     * Returns the jikenNo.
     * 
     * @return the jikenNo
     */
    public String getJikenNo() {
        return jikenNo;
    }

    /** 
     * Sets the jikenNo.
     * 
     * @param jikenNo the jikenNo
     */
    public void setJikenNo(String jikenNo) {
        this.jikenNo = jikenNo;
    }

    /** 
     * Returns the jikenName.
     * 
     * @return the jikenName
     */
    public String getJikenName() {
        return jikenName;
    }

    /** 
     * Sets the jikenName.
     * 
     * @param jikenName the jikenName
     */
    public void setJikenName(String jikenName) {
        this.jikenName = jikenName;
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