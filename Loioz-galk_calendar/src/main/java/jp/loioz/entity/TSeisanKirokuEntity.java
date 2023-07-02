package jp.loioz.entity;

import java.math.BigDecimal;
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
 * 精算記録
 */
@Entity(listener = TSeisanKirokuEntityListener.class)
@Table(name = "t_seisan_kiroku")
public class TSeisanKirokuEntity extends DefaultEntity {

	/** 精算SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seisan_seq")
    Long seisanSeq;

    /** 精算ID */
    @Column(name = "seisan_id")
    Long seisanId;

    /** 顧客ID */
    @Column(name = "customer_id")
    Long customerId;

    /** 案件ID */
    @Column(name = "anken_id")
    Long ankenId;

    /** 精算ステータス */
    @Column(name = "seisan_status")
    String seisanStatus;

    /** 精算区分 */
    @Column(name = "seisan_kubun")
    String seisanKubun;

    /** 請求方法 */
    @Column(name = "seikyu_type")
    String seikyuType;

    /** 請求-月々の支払額 */
    @Column(name = "month_shiharai_gaku")
    BigDecimal monthShiharaiGaku;

    /** 請求-月々の支払日 */
    @Column(name = "month_shiharai_date")
    String monthShiharaiDate;

    /** 請求-支払開始日 */
    @Column(name = "shiharai_start_dt")
    LocalDate shiharaiStartDt;

    /** 請求-入金予定日 */
    @Column(name = "shiharai_dt")
    LocalDate shiharaiDt;

    /** 請求-端数月 */
    @Column(name = "hasu")
    String hasu;

    /** 請求-請求先種別 */
    @Column(name = "seikyu_shiharaisha_type")
    String seikyuShiharaishaType;

    /** 請求-請求先顧客ID */
    @Column(name = "seikyu_customer_id")
    Long seikyuCustomerId;

    /** 請求-請求先関与者SEQ */
    @Column(name = "seikyu_kanyosha_seq")
    Long seikyuKanyoshaSeq;

    /** 請求-口座SEQ */
    @Column(name = "seikyu_koza_seq")
    Long seikyuKozaSeq;

    /** 返金-支払い期日 */
    @Column(name = "shiharai_limit_dt")
    LocalDate shiharaiLimitDt;

    /** 返金-口座SEQ */
    @Column(name = "henkin_koza_seq")
    Long henkinKozaSeq;

    /** 返金-返金先種別 */
    @Column(name = "henkin_saki_type")
    String henkinSakiType;

    /** 返金-返金先顧客ID */
    @Column(name = "henkin_customer_id")
    Long henkinCustomerId;

    /** 返金-返金先関与者SEQ */
    @Column(name = "henkin_kanyosha_seq")
    Long henkinKanyoshaSeq;

    /** 精算額 */
    @Column(name = "seisan_gaku")
    BigDecimal seisanGaku;

    /** 摘要 */
    @Column(name = "tekiyo")
    String tekiyo;

    /** 預り金プールフラグ */
    @Column(name = "pool_flg")
    String poolFlg;

    /** 最終編集日時 */
    @Column(name = "last_edit_at")
    LocalDateTime lastEditAt;

    /** 最終編集アカウントSEQ */
    @Column(name = "last_edit_by")
    Long lastEditBy;

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

    /** 削除日 */
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
     * Returns the seisanSeq.
     * 
     * @return the seisanSeq
     */
    public Long getSeisanSeq() {
        return seisanSeq;
    }

    /** 
     * Sets the seisanSeq.
     * 
     * @param seisanSeq the seisanSeq
     */
    public void setSeisanSeq(Long seisanSeq) {
        this.seisanSeq = seisanSeq;
    }

    /** 
     * Returns the seisanId.
     * 
     * @return the seisanId
     */
    public Long getSeisanId() {
        return seisanId;
    }

    /** 
     * Sets the seisanId.
     * 
     * @param seisanId the seisanId
     */
    public void setSeisanId(Long seisanId) {
        this.seisanId = seisanId;
    }

    /** 
     * Returns the customerId.
     * 
     * @return the customerId
     */
    public Long getCustomerId() {
        return customerId;
    }

    /** 
     * Sets the customerId.
     * 
     * @param customerId the customerId
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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
     * Returns the seisanStatus.
     * 
     * @return the seisanStatus
     */
    public String getSeisanStatus() {
        return seisanStatus;
    }

    /** 
     * Sets the seisanStatus.
     * 
     * @param seisanStatus the seisanStatus
     */
    public void setSeisanStatus(String seisanStatus) {
        this.seisanStatus = seisanStatus;
    }

    /** 
     * Returns the seisanKubun.
     * 
     * @return the seisanKubun
     */
    public String getSeisanKubun() {
        return seisanKubun;
    }

    /** 
     * Sets the seisanKubun.
     * 
     * @param seisanKubun the seisanKubun
     */
    public void setSeisanKubun(String seisanKubun) {
        this.seisanKubun = seisanKubun;
    }

    /** 
     * Returns the seikyuType.
     * 
     * @return the seikyuType
     */
    public String getSeikyuType() {
        return seikyuType;
    }

    /** 
     * Sets the seikyuType.
     * 
     * @param seikyuType the seikyuType
     */
    public void setSeikyuType(String seikyuType) {
        this.seikyuType = seikyuType;
    }

    /** 
     * Returns the monthShiharaiGaku.
     * 
     * @return the monthShiharaiGaku
     */
    public BigDecimal getMonthShiharaiGaku() {
        return monthShiharaiGaku;
    }

    /** 
     * Sets the monthShiharaiGaku.
     * 
     * @param monthShiharaiGaku the monthShiharaiGaku
     */
    public void setMonthShiharaiGaku(BigDecimal monthShiharaiGaku) {
        this.monthShiharaiGaku = monthShiharaiGaku;
    }

    /** 
     * Returns the monthShiharaiDate.
     * 
     * @return the monthShiharaiDate
     */
    public String getMonthShiharaiDate() {
        return monthShiharaiDate;
    }

    /** 
     * Sets the monthShiharaiDate.
     * 
     * @param monthShiharaiDate the monthShiharaiDate
     */
    public void setMonthShiharaiDate(String monthShiharaiDate) {
        this.monthShiharaiDate = monthShiharaiDate;
    }

    /** 
     * Returns the shiharaiStartDt.
     * 
     * @return the shiharaiStartDt
     */
    public LocalDate getShiharaiStartDt() {
        return shiharaiStartDt;
    }

    /** 
     * Sets the shiharaiStartDt.
     * 
     * @param shiharaiStartDt the shiharaiStartDt
     */
    public void setShiharaiStartDt(LocalDate shiharaiStartDt) {
        this.shiharaiStartDt = shiharaiStartDt;
    }

    /** 
     * Returns the shiharaiDt.
     * 
     * @return the shiharaiDt
     */
    public LocalDate getShiharaiDt() {
        return shiharaiDt;
    }

    /** 
     * Sets the shiharaiDt.
     * 
     * @param shiharaiDt the shiharaiDt
     */
    public void setShiharaiDt(LocalDate shiharaiDt) {
        this.shiharaiDt = shiharaiDt;
    }

    /** 
     * Returns the hasu.
     * 
     * @return the hasu
     */
    public String getHasu() {
        return hasu;
    }

    /** 
     * Sets the hasu.
     * 
     * @param hasu the hasu
     */
    public void setHasu(String hasu) {
        this.hasu = hasu;
    }

    /** 
     * Returns the seikyuShiharaishaType.
     * 
     * @return the seikyuShiharaishaType
     */
    public String getSeikyuShiharaishaType() {
        return seikyuShiharaishaType;
    }

    /** 
     * Sets the seikyuShiharaishaType.
     * 
     * @param seikyuShiharaishaType the seikyuShiharaishaType
     */
    public void setSeikyuShiharaishaType(String seikyuShiharaishaType) {
        this.seikyuShiharaishaType = seikyuShiharaishaType;
    }

    /** 
     * Returns the seikyuCustomerId.
     * 
     * @return the seikyuCustomerId
     */
    public Long getSeikyuCustomerId() {
        return seikyuCustomerId;
    }

    /** 
     * Sets the seikyuCustomerId.
     * 
     * @param seikyuCustomerId the seikyuCustomerId
     */
    public void setSeikyuCustomerId(Long seikyuCustomerId) {
        this.seikyuCustomerId = seikyuCustomerId;
    }

    /** 
     * Returns the seikyuKanyoshaSeq.
     * 
     * @return the seikyuKanyoshaSeq
     */
    public Long getSeikyuKanyoshaSeq() {
        return seikyuKanyoshaSeq;
    }

    /** 
     * Sets the seikyuKanyoshaSeq.
     * 
     * @param seikyuKanyoshaSeq the seikyuKanyoshaSeq
     */
    public void setSeikyuKanyoshaSeq(Long seikyuKanyoshaSeq) {
        this.seikyuKanyoshaSeq = seikyuKanyoshaSeq;
    }

    /** 
     * Returns the seikyuKozaSeq.
     * 
     * @return the seikyuKozaSeq
     */
    public Long getSeikyuKozaSeq() {
        return seikyuKozaSeq;
    }

    /** 
     * Sets the seikyuKozaSeq.
     * 
     * @param seikyuKozaSeq the seikyuKozaSeq
     */
    public void setSeikyuKozaSeq(Long seikyuKozaSeq) {
        this.seikyuKozaSeq = seikyuKozaSeq;
    }

    /** 
     * Returns the shiharaiLimitDt.
     * 
     * @return the shiharaiLimitDt
     */
    public LocalDate getShiharaiLimitDt() {
        return shiharaiLimitDt;
    }

    /** 
     * Sets the shiharaiLimitDt.
     * 
     * @param shiharaiLimitDt the shiharaiLimitDt
     */
    public void setShiharaiLimitDt(LocalDate shiharaiLimitDt) {
        this.shiharaiLimitDt = shiharaiLimitDt;
    }

    /** 
     * Returns the henkinKozaSeq.
     * 
     * @return the henkinKozaSeq
     */
    public Long getHenkinKozaSeq() {
        return henkinKozaSeq;
    }

    /** 
     * Sets the henkinKozaSeq.
     * 
     * @param henkinKozaSeq the henkinKozaSeq
     */
    public void setHenkinKozaSeq(Long henkinKozaSeq) {
        this.henkinKozaSeq = henkinKozaSeq;
    }

    /** 
     * Returns the henkinSakiType.
     * 
     * @return the henkinSakiType
     */
    public String getHenkinSakiType() {
        return henkinSakiType;
    }

    /** 
     * Sets the henkinSakiType.
     * 
     * @param henkinSakiType the henkinSakiType
     */
    public void setHenkinSakiType(String henkinSakiType) {
        this.henkinSakiType = henkinSakiType;
    }

    /** 
     * Returns the henkinCustomerId.
     * 
     * @return the henkinCustomerId
     */
    public Long getHenkinCustomerId() {
        return henkinCustomerId;
    }

    /** 
     * Sets the henkinCustomerId.
     * 
     * @param henkinCustomerId the henkinCustomerId
     */
    public void setHenkinCustomerId(Long henkinCustomerId) {
        this.henkinCustomerId = henkinCustomerId;
    }

    /** 
     * Returns the henkinKanyoshaSeq.
     * 
     * @return the henkinKanyoshaSeq
     */
    public Long getHenkinKanyoshaSeq() {
        return henkinKanyoshaSeq;
    }

    /** 
     * Sets the henkinKanyoshaSeq.
     * 
     * @param henkinKanyoshaSeq the henkinKanyoshaSeq
     */
    public void setHenkinKanyoshaSeq(Long henkinKanyoshaSeq) {
        this.henkinKanyoshaSeq = henkinKanyoshaSeq;
    }

    /** 
     * Returns the seisanGaku.
     * 
     * @return the seisanGaku
     */
    public BigDecimal getSeisanGaku() {
        return seisanGaku;
    }

    /** 
     * Sets the seisanGaku.
     * 
     * @param seisanGaku the seisanGaku
     */
    public void setSeisanGaku(BigDecimal seisanGaku) {
        this.seisanGaku = seisanGaku;
    }

    /** 
     * Returns the tekiyo.
     * 
     * @return the tekiyo
     */
    public String getTekiyo() {
        return tekiyo;
    }

    /** 
     * Sets the tekiyo.
     * 
     * @param tekiyo the tekiyo
     */
    public void setTekiyo(String tekiyo) {
        this.tekiyo = tekiyo;
    }

    /** 
     * Returns the poolFlg.
     * 
     * @return the poolFlg
     */
    public String getPoolFlg() {
        return poolFlg;
    }

    /** 
     * Sets the poolFlg.
     * 
     * @param poolFlg the poolFlg
     */
    public void setPoolFlg(String poolFlg) {
        this.poolFlg = poolFlg;
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