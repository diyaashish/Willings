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
 * 問い合わせ-詳細
 */
@Entity(listener = TToiawaseDetailEntityListener.class)
@Table(name = "t_toiawase_detail")
public class TToiawaseDetailEntity extends DefaultEntity {

    /** 問い合わせ詳細SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toiawase_detail_seq")
    Long toiawaseDetailSeq;

    /** 問い合わせSEQ */
    @Column(name = "toiawase_seq")
    Long toiawaseSeq;

    /** 内容 */
    @Column(name = "body")
    String body;

    /** 登録元種別 */
    @Column(name = "regist_type")
    String registType;

    /** テナント既読フラグ */
    @Column(name = "tenant_read_flg")
    String tenantReadFlg;

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

    /** システム管理者_公開フラグ */
    @Column(name = "sys_open_flg")
    String sysOpenFlg;

    /** システム管理者_登録日時 */
    @Column(name = "sys_created_at")
    LocalDateTime sysCreatedAt;

    /** システム管理者_登録アカウントSEQ */
    @Column(name = "sys_created_by")
    Long sysCreatedBy;

    /** システム管理者_更新日時 */
    @Column(name = "sys_updated_at")
    LocalDateTime sysUpdatedAt;

    /** システム管理者_更新アカウントSEQ */
    @Column(name = "sys_updated_by")
    Long sysUpdatedBy;

    /** システム管理者_削除日時 */
    @Column(name = "sys_deleted_at")
    LocalDateTime sysDeletedAt;

    /** システム管理者_削除アカウントSEQ */
    @Column(name = "sys_deleted_by")
    Long sysDeletedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the toiawaseDetailSeq.
     * 
     * @return the toiawaseDetailSeq
     */
    public Long getToiawaseDetailSeq() {
        return toiawaseDetailSeq;
    }

    /** 
     * Sets the toiawaseDetailSeq.
     * 
     * @param toiawaseDetailSeq the toiawaseDetailSeq
     */
    public void setToiawaseDetailSeq(Long toiawaseDetailSeq) {
        this.toiawaseDetailSeq = toiawaseDetailSeq;
    }

    /** 
     * Returns the toiawaseSeq.
     * 
     * @return the toiawaseSeq
     */
    public Long getToiawaseSeq() {
        return toiawaseSeq;
    }

    /** 
     * Sets the toiawaseSeq.
     * 
     * @param toiawaseSeq the toiawaseSeq
     */
    public void setToiawaseSeq(Long toiawaseSeq) {
        this.toiawaseSeq = toiawaseSeq;
    }

    /** 
     * Returns the body.
     * 
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /** 
     * Sets the body.
     * 
     * @param body the body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /** 
     * Returns the registType.
     * 
     * @return the registType
     */
    public String getRegistType() {
        return registType;
    }

    /** 
     * Sets the registType.
     * 
     * @param registType the registType
     */
    public void setRegistType(String registType) {
        this.registType = registType;
    }

    /** 
     * Returns the tenantReadFlg.
     * 
     * @return the tenantReadFlg
     */
    public String getTenantReadFlg() {
        return tenantReadFlg;
    }

    /** 
     * Sets the tenantReadFlg.
     * 
     * @param tenantReadFlg the tenantReadFlg
     */
    public void setTenantReadFlg(String tenantReadFlg) {
        this.tenantReadFlg = tenantReadFlg;
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
     * Returns the sysOpenFlg.
     * 
     * @return the sysOpenFlg
     */
    public String getSysOpenFlg() {
        return sysOpenFlg;
    }

    /** 
     * Sets the sysOpenFlg.
     * 
     * @param sysOpenFlg the sysOpenFlg
     */
    public void setSysOpenFlg(String sysOpenFlg) {
        this.sysOpenFlg = sysOpenFlg;
    }

    /** 
     * Returns the sysCreatedAt.
     * 
     * @return the sysCreatedAt
     */
    public LocalDateTime getSysCreatedAt() {
        return sysCreatedAt;
    }

    /** 
     * Sets the sysCreatedAt.
     * 
     * @param sysCreatedAt the sysCreatedAt
     */
    public void setSysCreatedAt(LocalDateTime sysCreatedAt) {
        this.sysCreatedAt = sysCreatedAt;
    }

    /** 
     * Returns the sysCreatedBy.
     * 
     * @return the sysCreatedBy
     */
    public Long getSysCreatedBy() {
        return sysCreatedBy;
    }

    /** 
     * Sets the sysCreatedBy.
     * 
     * @param sysCreatedBy the sysCreatedBy
     */
    public void setSysCreatedBy(Long sysCreatedBy) {
        this.sysCreatedBy = sysCreatedBy;
    }

    /** 
     * Returns the sysUpdatedAt.
     * 
     * @return the sysUpdatedAt
     */
    public LocalDateTime getSysUpdatedAt() {
        return sysUpdatedAt;
    }

    /** 
     * Sets the sysUpdatedAt.
     * 
     * @param sysUpdatedAt the sysUpdatedAt
     */
    public void setSysUpdatedAt(LocalDateTime sysUpdatedAt) {
        this.sysUpdatedAt = sysUpdatedAt;
    }

    /** 
     * Returns the sysUpdatedBy.
     * 
     * @return the sysUpdatedBy
     */
    public Long getSysUpdatedBy() {
        return sysUpdatedBy;
    }

    /** 
     * Sets the sysUpdatedBy.
     * 
     * @param sysUpdatedBy the sysUpdatedBy
     */
    public void setSysUpdatedBy(Long sysUpdatedBy) {
        this.sysUpdatedBy = sysUpdatedBy;
    }

    /** 
     * Returns the sysDeletedAt.
     * 
     * @return the sysDeletedAt
     */
    public LocalDateTime getSysDeletedAt() {
        return sysDeletedAt;
    }

    /** 
     * Sets the sysDeletedAt.
     * 
     * @param sysDeletedAt the sysDeletedAt
     */
    public void setSysDeletedAt(LocalDateTime sysDeletedAt) {
        this.sysDeletedAt = sysDeletedAt;
    }

    /** 
     * Returns the sysDeletedBy.
     * 
     * @return the sysDeletedBy
     */
    public Long getSysDeletedBy() {
        return sysDeletedBy;
    }

    /** 
     * Sets the sysDeletedBy.
     * 
     * @param sysDeletedBy the sysDeletedBy
     */
    public void setSysDeletedBy(Long sysDeletedBy) {
        this.sysDeletedBy = sysDeletedBy;
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