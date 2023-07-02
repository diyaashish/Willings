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
 * 顧問契約担当
 */
@Entity(listener = TAdvisorContractTantoEntityListener.class)
@Table(name = "t_advisor_contract_tanto")
public class TAdvisorContractTantoEntity extends DefaultEntity {

    /** 顧問契約担当SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advisor_contract_tanto_seq")
    Long advisorContractTantoSeq;

    /** 顧問契約SEQ */
    @Column(name = "advisor_contract_seq")
    Long advisorContractSeq;

    /** アカウントSEQ */
    @Column(name = "account_seq")
    Long accountSeq;

    /** 担当種別 */
    @Column(name = "tanto_type")
    String tantoType;

    /** 担当種別枝番 */
    @Column(name = "tanto_type_branch_no")
    Long tantoTypeBranchNo;

    /** 主担当フラグ */
    @Column(name = "main_tanto_flg")
    String mainTantoFlg;

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
     * Returns the advisorContractTantoSeq.
     * 
     * @return the advisorContractTantoSeq
     */
    public Long getAdvisorContractTantoSeq() {
        return advisorContractTantoSeq;
    }

    /** 
     * Sets the advisorContractTantoSeq.
     * 
     * @param advisorContractTantoSeq the advisorContractTantoSeq
     */
    public void setAdvisorContractTantoSeq(Long advisorContractTantoSeq) {
        this.advisorContractTantoSeq = advisorContractTantoSeq;
    }

    /** 
     * Returns the advisorContractSeq.
     * 
     * @return the advisorContractSeq
     */
    public Long getAdvisorContractSeq() {
        return advisorContractSeq;
    }

    /** 
     * Sets the advisorContractSeq.
     * 
     * @param advisorContractSeq the advisorContractSeq
     */
    public void setAdvisorContractSeq(Long advisorContractSeq) {
        this.advisorContractSeq = advisorContractSeq;
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
     * Returns the tantoType.
     * 
     * @return the tantoType
     */
    public String getTantoType() {
        return tantoType;
    }

    /** 
     * Sets the tantoType.
     * 
     * @param tantoType the tantoType
     */
    public void setTantoType(String tantoType) {
        this.tantoType = tantoType;
    }

    /** 
     * Returns the tantoTypeBranchNo.
     * 
     * @return the tantoTypeBranchNo
     */
    public Long getTantoTypeBranchNo() {
        return tantoTypeBranchNo;
    }

    /** 
     * Sets the tantoTypeBranchNo.
     * 
     * @param tantoTypeBranchNo the tantoTypeBranchNo
     */
    public void setTantoTypeBranchNo(Long tantoTypeBranchNo) {
        this.tantoTypeBranchNo = tantoTypeBranchNo;
    }

    /** 
     * Returns the mainTantoFlg.
     * 
     * @return the mainTantoFlg
     */
    public String getMainTantoFlg() {
        return mainTantoFlg;
    }

    /** 
     * Sets the mainTantoFlg.
     * 
     * @param mainTantoFlg the mainTantoFlg
     */
    public void setMainTantoFlg(String mainTantoFlg) {
        this.mainTantoFlg = mainTantoFlg;
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