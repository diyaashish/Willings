package jp.loioz.entity;

import java.math.BigDecimal;
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
 * 回収不能金詳細
 */
@Entity(listener = TUncollectibleDetailEntityListener.class)
@Table(name = "t_uncollectible_detail")
public class TUncollectibleDetailEntity extends DefaultEntity {

    /** 回収不能金詳細SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uncollectible_detail_seq")
    Long uncollectibleDetailSeq;

    /** 回収不能金SEQ */
    @Column(name = "uncollectible_seq")
    Long uncollectibleSeq;

    /** 回収不能金額 */
    @Column(name = "uncollectible_amount")
    BigDecimal uncollectibleAmount;

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
     * Returns the uncollectibleDetailSeq.
     * 
     * @return the uncollectibleDetailSeq
     */
    public Long getUncollectibleDetailSeq() {
        return uncollectibleDetailSeq;
    }

    /** 
     * Sets the uncollectibleDetailSeq.
     * 
     * @param uncollectibleDetailSeq the uncollectibleDetailSeq
     */
    public void setUncollectibleDetailSeq(Long uncollectibleDetailSeq) {
        this.uncollectibleDetailSeq = uncollectibleDetailSeq;
    }

    /** 
     * Returns the uncollectibleSeq.
     * 
     * @return the uncollectibleSeq
     */
    public Long getUncollectibleSeq() {
        return uncollectibleSeq;
    }

    /** 
     * Sets the uncollectibleSeq.
     * 
     * @param uncollectibleSeq the uncollectibleSeq
     */
    public void setUncollectibleSeq(Long uncollectibleSeq) {
        this.uncollectibleSeq = uncollectibleSeq;
    }

    /** 
     * Returns the uncollectibleAmount.
     * 
     * @return the uncollectibleAmount
     */
    public BigDecimal getUncollectibleAmount() {
        return uncollectibleAmount;
    }

    /** 
     * Sets the uncollectibleAmount.
     * 
     * @param uncollectibleAmount the uncollectibleAmount
     */
    public void setUncollectibleAmount(BigDecimal uncollectibleAmount) {
        this.uncollectibleAmount = uncollectibleAmount;
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