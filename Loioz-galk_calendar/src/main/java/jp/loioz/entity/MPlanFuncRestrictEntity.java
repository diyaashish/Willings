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
 * プラン別機能制限テーブル
 */
@Entity(listener = MPlanFuncRestrictEntityListener.class)
@Table(name = "m_plan_func_restrict")
public class MPlanFuncRestrictEntity extends DefaultEntity {

    /** プラン別機能制限連番 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_func_restrict_seq")
    Long planFuncRestrictSeq;

    /** 機能制限ID */
    @Column(name = "func_restrict_id")
    String funcRestrictId;

    /** 機能種別 */
    @Column(name = "func_type")
    String funcType;

    /** 機能名 */
    @Column(name = "func_name")
    String funcName;

    /** スターター制限フラグ */
    @Column(name = "starter_restrict_flg")
    String starterRestrictFlg;

    /** スタンダード制限フラグ */
    @Column(name = "standard_restrict_flg")
    String standardRestrictFlg;

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
     * Returns the planFuncRestrictSeq.
     * 
     * @return the planFuncRestrictSeq
     */
    public Long getPlanFuncRestrictSeq() {
        return planFuncRestrictSeq;
    }

    /** 
     * Sets the planFuncRestrictSeq.
     * 
     * @param planFuncRestrictSeq the planFuncRestrictSeq
     */
    public void setPlanFuncRestrictSeq(Long planFuncRestrictSeq) {
        this.planFuncRestrictSeq = planFuncRestrictSeq;
    }

    /** 
     * Returns the funcRestrictId.
     * 
     * @return the funcRestrictId
     */
    public String getFuncRestrictId() {
        return funcRestrictId;
    }

    /** 
     * Sets the funcRestrictId.
     * 
     * @param funcRestrictId the funcRestrictId
     */
    public void setFuncRestrictId(String funcRestrictId) {
        this.funcRestrictId = funcRestrictId;
    }

    /** 
     * Returns the funcType.
     * 
     * @return the funcType
     */
    public String getFuncType() {
        return funcType;
    }

    /** 
     * Sets the funcType.
     * 
     * @param funcType the funcType
     */
    public void setFuncType(String funcType) {
        this.funcType = funcType;
    }

    /** 
     * Returns the funcName.
     * 
     * @return the funcName
     */
    public String getFuncName() {
        return funcName;
    }

    /** 
     * Sets the funcName.
     * 
     * @param funcName the funcName
     */
    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    /** 
     * Returns the starterRestrictFlg.
     * 
     * @return the starterRestrictFlg
     */
    public String getStarterRestrictFlg() {
        return starterRestrictFlg;
    }

    /** 
     * Sets the starterRestrictFlg.
     * 
     * @param starterRestrictFlg the starterRestrictFlg
     */
    public void setStarterRestrictFlg(String starterRestrictFlg) {
        this.starterRestrictFlg = starterRestrictFlg;
    }

    /** 
     * Returns the standardRestrictFlg.
     * 
     * @return the standardRestrictFlg
     */
    public String getStandardRestrictFlg() {
        return standardRestrictFlg;
    }

    /** 
     * Sets the standardRestrictFlg.
     * 
     * @param standardRestrictFlg the standardRestrictFlg
     */
    public void setStandardRestrictFlg(String standardRestrictFlg) {
        this.standardRestrictFlg = standardRestrictFlg;
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