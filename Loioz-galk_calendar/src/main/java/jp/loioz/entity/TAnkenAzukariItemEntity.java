package jp.loioz.entity;

import java.time.LocalDate;
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
 * 案件-預かり品
 */
@Entity(listener = TAnkenAzukariItemEntityListener.class)
@Table(name = "t_anken_azukari_item")
public class TAnkenAzukariItemEntity extends DefaultEntity {

	/** 預かり品SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "anken_item_seq")
    Long ankenItemSeq;

    /** 案件ID */
    @Column(name = "anken_id")
    Long ankenId;

    /** ステータス */
    @Column(name = "azukari_status")
    String azukariStatus;

    /** 品目 */
    @Column(name = "hinmoku")
    String hinmoku;

    /** 数量 */
    @Column(name = "azukari_count")
    String azukariCount;

    /** 保管場所 */
    @Column(name = "hokan_place")
    String hokanPlace;

    /** 預り日 */
    @Column(name = "azukari_date")
    LocalDate azukariDate;

    /** 預り元種別 */
    @Column(name = "azukari_from_type")
    String azukariFromType;

    /** 預かり元顧客ID */
    @Column(name = "azukari_from_customer_id")
    Long azukariFromCustomerId;

    /** 預かり元関与者SEQ */
    @Column(name = "azukari_from_kanyosha_seq")
    Long azukariFromKanyoshaSeq;

    /** 預り元 */
    @Column(name = "azukari_from")
    String azukariFrom;

    /** 返却期限 */
    @Column(name = "return_limit_date")
    LocalDate returnLimitDate;

    /** 返却日 */
    @Column(name = "return_date")
    LocalDate returnDate;

    /** 返却先種別 */
    @Column(name = "return_to_type")
    String returnToType;

    /** 返却先顧客ID */
    @Column(name = "return_to_customer_id")
    Long returnToCustomerId;

    /** 返却先関与者SEQ */
    @Column(name = "return_to_kanyosha_seq")
    Long returnToKanyoshaSeq;

    /** 返却先 */
    @Column(name = "return_to")
    String returnTo;

    /** 備考 */
    @Column(name = "remarks")
    String remarks;

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

    /** 
     * Returns the ankenItemSeq.
     * 
     * @return the ankenItemSeq
     */
    public Long getAnkenItemSeq() {
        return ankenItemSeq;
    }

    /** 
     * Sets the ankenItemSeq.
     * 
     * @param ankenItemSeq the ankenItemSeq
     */
    public void setAnkenItemSeq(Long ankenItemSeq) {
        this.ankenItemSeq = ankenItemSeq;
    }

    /** 
     * Returns the ankenId.
     * 
     * @return the ankenId
     */
    public Long getAnkenId() {
        return ankenId;
    }

    /** 
     * Sets the ankenId.
     * 
     * @param ankenId the ankenId
     */
    public void setAnkenId(Long ankenId) {
        this.ankenId = ankenId;
    }

    /** 
     * Returns the azukariStatus.
     * 
     * @return the azukariStatus
     */
    public String getAzukariStatus() {
        return azukariStatus;
    }

    /** 
     * Sets the azukariStatus.
     * 
     * @param azukariStatus the azukariStatus
     */
    public void setAzukariStatus(String azukariStatus) {
        this.azukariStatus = azukariStatus;
    }

    /** 
     * Returns the hinmoku.
     * 
     * @return the hinmoku
     */
    public String getHinmoku() {
        return hinmoku;
    }

    /** 
     * Sets the hinmoku.
     * 
     * @param hinmoku the hinmoku
     */
    public void setHinmoku(String hinmoku) {
        this.hinmoku = hinmoku;
    }

    /** 
     * Returns the azukariCount.
     * 
     * @return the azukariCount
     */
    public String getAzukariCount() {
        return azukariCount;
    }

    /** 
     * Sets the azukariCount.
     * 
     * @param azukariCount the azukariCount
     */
    public void setAzukariCount(String azukariCount) {
        this.azukariCount = azukariCount;
    }

    /** 
     * Returns the hokanPlace.
     * 
     * @return the hokanPlace
     */
    public String getHokanPlace() {
        return hokanPlace;
    }

    /** 
     * Sets the hokanPlace.
     * 
     * @param hokanPlace the hokanPlace
     */
    public void setHokanPlace(String hokanPlace) {
        this.hokanPlace = hokanPlace;
    }

    /** 
     * Returns the azukariDate.
     * 
     * @return the azukariDate
     */
    public LocalDate getAzukariDate() {
        return azukariDate;
    }

    /** 
     * Sets the azukariDate.
     * 
     * @param azukariDate the azukariDate
     */
    public void setAzukariDate(LocalDate azukariDate) {
        this.azukariDate = azukariDate;
    }

    /** 
     * Returns the azukariFromType.
     * 
     * @return the azukariFromType
     */
    public String getAzukariFromType() {
        return azukariFromType;
    }

    /** 
     * Sets the azukariFromType.
     * 
     * @param azukariFromType the azukariFromType
     */
    public void setAzukariFromType(String azukariFromType) {
        this.azukariFromType = azukariFromType;
    }

    /** 
     * Returns the azukariFromCustomerId.
     * 
     * @return the azukariFromCustomerId
     */
    public Long getAzukariFromCustomerId() {
        return azukariFromCustomerId;
    }

    /** 
     * Sets the azukariFromCustomerId.
     * 
     * @param azukariFromCustomerId the azukariFromCustomerId
     */
    public void setAzukariFromCustomerId(Long azukariFromCustomerId) {
        this.azukariFromCustomerId = azukariFromCustomerId;
    }

    /** 
     * Returns the azukariFromKanyoshaSeq.
     * 
     * @return the azukariFromKanyoshaSeq
     */
    public Long getAzukariFromKanyoshaSeq() {
        return azukariFromKanyoshaSeq;
    }

    /** 
     * Sets the azukariFromKanyoshaSeq.
     * 
     * @param azukariFromKanyoshaSeq the azukariFromKanyoshaSeq
     */
    public void setAzukariFromKanyoshaSeq(Long azukariFromKanyoshaSeq) {
        this.azukariFromKanyoshaSeq = azukariFromKanyoshaSeq;
    }

    /** 
     * Returns the azukariFrom.
     * 
     * @return the azukariFrom
     */
    public String getAzukariFrom() {
        return azukariFrom;
    }

    /** 
     * Sets the azukariFrom.
     * 
     * @param azukariFrom the azukariFrom
     */
    public void setAzukariFrom(String azukariFrom) {
        this.azukariFrom = azukariFrom;
    }

    /** 
     * Returns the returnLimitDate.
     * 
     * @return the returnLimitDate
     */
    public LocalDate getReturnLimitDate() {
        return returnLimitDate;
    }

    /** 
     * Sets the returnLimitDate.
     * 
     * @param returnLimitDate the returnLimitDate
     */
    public void setReturnLimitDate(LocalDate returnLimitDate) {
        this.returnLimitDate = returnLimitDate;
    }

    /** 
     * Returns the returnDate.
     * 
     * @return the returnDate
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /** 
     * Sets the returnDate.
     * 
     * @param returnDate the returnDate
     */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /** 
     * Returns the returnToType.
     * 
     * @return the returnToType
     */
    public String getReturnToType() {
        return returnToType;
    }

    /** 
     * Sets the returnToType.
     * 
     * @param returnToType the returnToType
     */
    public void setReturnToType(String returnToType) {
        this.returnToType = returnToType;
    }

    /** 
     * Returns the returnToCustomerId.
     * 
     * @return the returnToCustomerId
     */
    public Long getReturnToCustomerId() {
        return returnToCustomerId;
    }

    /** 
     * Sets the returnToCustomerId.
     * 
     * @param returnToCustomerId the returnToCustomerId
     */
    public void setReturnToCustomerId(Long returnToCustomerId) {
        this.returnToCustomerId = returnToCustomerId;
    }

    /** 
     * Returns the returnToKanyoshaSeq.
     * 
     * @return the returnToKanyoshaSeq
     */
    public Long getReturnToKanyoshaSeq() {
        return returnToKanyoshaSeq;
    }

    /** 
     * Sets the returnToKanyoshaSeq.
     * 
     * @param returnToKanyoshaSeq the returnToKanyoshaSeq
     */
    public void setReturnToKanyoshaSeq(Long returnToKanyoshaSeq) {
        this.returnToKanyoshaSeq = returnToKanyoshaSeq;
    }

    /** 
     * Returns the returnTo.
     * 
     * @return the returnTo
     */
    public String getReturnTo() {
        return returnTo;
    }

    /** 
     * Sets the returnTo.
     * 
     * @param returnTo the returnTo
     */
    public void setReturnTo(String returnTo) {
        this.returnTo = returnTo;
    }

    /** 
     * Returns the remarks.
     * 
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /** 
     * Sets the remarks.
     * 
     * @param remarks the remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
    
}