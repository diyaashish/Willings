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
 * 支払いカードテーブル
 */
@Entity(listener = TPaymentCardEntityListener.class)
@Table(name = "t_payment_card")
public class TPaymentCardEntity extends DefaultEntity {

    /** 支払いカード連番 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_card_seq")
    Long paymentCardSeq;

    /** カード番号 */
    @Column(name = "card_number_last_4")
    String cardNumberLast4;

    /** 有効期限（年） */
    @Column(name = "expired_year")
    String expiredYear;

    /** 有効期限（月） */
    @Column(name = "expired_month")
    String expiredMonth;

    /** 名義（姓） */
    @Column(name = "last_name")
    String lastName;

    /** 名義（名） */
    @Column(name = "first_name")
    String firstName;

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
     * Returns the paymentCardSeq.
     * 
     * @return the paymentCardSeq
     */
    public Long getPaymentCardSeq() {
        return paymentCardSeq;
    }

    /** 
     * Sets the paymentCardSeq.
     * 
     * @param paymentCardSeq the paymentCardSeq
     */
    public void setPaymentCardSeq(Long paymentCardSeq) {
        this.paymentCardSeq = paymentCardSeq;
    }

    /** 
     * Returns the cardNumberLast4.
     * 
     * @return the cardNumberLast4
     */
    public String getCardNumberLast4() {
        return cardNumberLast4;
    }

    /** 
     * Sets the cardNumberLast4.
     * 
     * @param cardNumberLast4 the cardNumberLast4
     */
    public void setCardNumberLast4(String cardNumberLast4) {
        this.cardNumberLast4 = cardNumberLast4;
    }

    /** 
     * Returns the expiredYear.
     * 
     * @return the expiredYear
     */
    public String getExpiredYear() {
        return expiredYear;
    }

    /** 
     * Sets the expiredYear.
     * 
     * @param expiredYear the expiredYear
     */
    public void setExpiredYear(String expiredYear) {
        this.expiredYear = expiredYear;
    }

    /** 
     * Returns the expiredMonth.
     * 
     * @return the expiredMonth
     */
    public String getExpiredMonth() {
        return expiredMonth;
    }

    /** 
     * Sets the expiredMonth.
     * 
     * @param expiredMonth the expiredMonth
     */
    public void setExpiredMonth(String expiredMonth) {
        this.expiredMonth = expiredMonth;
    }

    /** 
     * Returns the lastName.
     * 
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /** 
     * Sets the lastName.
     * 
     * @param lastName the lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** 
     * Returns the firstName.
     * 
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /** 
     * Sets the firstName.
     * 
     * @param firstName the firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
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