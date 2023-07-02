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
 * 裁判ツリー
 */
@Entity(listener = TSaibanTreeEntityListener.class)
@Table(name = "t_saiban_tree")
public class TSaibanTreeEntity extends DefaultEntity {

    /** 裁判ツリーSEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saiban_tree_seq")
    Long saibanTreeSeq;

    /** 裁判SEQ */
    @Column(name = "saiban_seq")
    Long saibanSeq;

    /** 親裁判SEQ */
    @Column(name = "parent_seq")
    Long parentSeq;

    /** 併合反訴種別 */
    @Column(name = "connect_type")
    String connectType;

    /** 本訴フラグ */
    @Column(name = "honso_flg")
    String honsoFlg;

    /** 基本事件フラグ */
    @Column(name = "kihon_flg")
    String kihonFlg;

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
     * Returns the saibanTreeSeq.
     * 
     * @return the saibanTreeSeq
     */
    public Long getSaibanTreeSeq() {
        return saibanTreeSeq;
    }

    /** 
     * Sets the saibanTreeSeq.
     * 
     * @param saibanTreeSeq the saibanTreeSeq
     */
    public void setSaibanTreeSeq(Long saibanTreeSeq) {
        this.saibanTreeSeq = saibanTreeSeq;
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
     * Returns the parentSeq.
     * 
     * @return the parentSeq
     */
    public Long getParentSeq() {
        return parentSeq;
    }

    /** 
     * Sets the parentSeq.
     * 
     * @param parentSeq the parentSeq
     */
    public void setParentSeq(Long parentSeq) {
        this.parentSeq = parentSeq;
    }

    /** 
     * Returns the connectType.
     * 
     * @return the connectType
     */
    public String getConnectType() {
        return connectType;
    }

    /** 
     * Sets the connectType.
     * 
     * @param connectType the connectType
     */
    public void setConnectType(String connectType) {
        this.connectType = connectType;
    }

    /** 
     * Returns the honsoFlg.
     * 
     * @return the honsoFlg
     */
    public String getHonsoFlg() {
        return honsoFlg;
    }

    /** 
     * Sets the honsoFlg.
     * 
     * @param honsoFlg the honsoFlg
     */
    public void setHonsoFlg(String honsoFlg) {
        this.honsoFlg = honsoFlg;
    }

    /** 
     * Returns the kihonFlg.
     * 
     * @return the kihonFlg
     */
    public String getKihonFlg() {
        return kihonFlg;
    }

    /** 
     * Sets the kihonFlg.
     * 
     * @param kihonFlg the kihonFlg
     */
    public void setKihonFlg(String kihonFlg) {
        this.kihonFlg = kihonFlg;
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