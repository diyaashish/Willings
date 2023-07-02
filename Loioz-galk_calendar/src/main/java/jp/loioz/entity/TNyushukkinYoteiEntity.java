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
 * 入出金予定
 */
@Entity(listener = TNyushukkinYoteiEntityListener.class)
@Table(name = "t_nyushukkin_yotei")
public class TNyushukkinYoteiEntity extends DefaultEntity {

	/** 入出金予定SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nyushukkin_yotei_seq")
    Long nyushukkinYoteiSeq;

    /** 顧客ID */
    @Column(name = "customer_id")
    Long customerId;

    /** 案件ID */
    @Column(name = "anken_id")
    Long ankenId;

    /** 入出金タイプ */
    @Column(name = "nyushukkin_type")
    String nyushukkinType;

    /** 入出金項目ID */
    @Column(name = "nyushukkin_komoku_id")
    Long nyushukkinKomokuId;

    /** 入金-支払者種別 */
    @Column(name = "nyukin_shiharaisha_type")
    String nyukinShiharaishaType;

    /** 入金-支払者顧客ID */
    @Column(name = "nyukin_customer_id")
    Long nyukinCustomerId;

    /** 入金-支払者関与者SEQ */
    @Column(name = "nyukin_kanyosha_seq")
    Long nyukinKanyoshaSeq;

    /** 入金-支払者 */
    @Column(name = "nyukin_shiharaisha")
    String nyukinShiharaisha;

    /** 入金-入金先口座SEQ */
    @Column(name = "nyukin_saki_koza_seq")
    Long nyukinSakiKozaSeq;

    /** 出金-出金先口座SEQ */
    @Column(name = "shukkin_saki_koza_seq")
    Long shukkinSakiKozaSeq;

    /** 出金-支払先種別 */
    @Column(name = "shukkin_shiharai_saki_type")
    String shukkinShiharaiSakiType;

    /** 出金-支払先顧客ID */
    @Column(name = "shukkin_customer_id")
    Long shukkinCustomerId;

    /** 出金-支払先関与者SEQ */
    @Column(name = "shukkin_kanyosha_seq")
    Long shukkinKanyoshaSeq;

    /** 出金-支払先 */
    @Column(name = "shukkin_shiharai_saki")
    String shukkinShiharaiSaki;

    /** 入出金予定日 */
    @Column(name = "nyushukkin_yotei_date")
    LocalDate nyushukkinYoteiDate;

    /** 入出金予定額 */
    @Column(name = "nyushukkin_yotei_gaku")
    BigDecimal nyushukkinYoteiGaku;

    /** 入出金日 */
    @Column(name = "nyushukkin_date")
    LocalDate nyushukkinDate;

    /** 入出金額 */
    @Column(name = "nyushukkin_gaku")
    BigDecimal nyushukkinGaku;

    /** 摘要 */
    @Column(name = "tekiyo")
    String tekiyo;

    /** 精算SEQ */
    @Column(name = "seisan_seq")
    Long seisanSeq;

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

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the nyushukkinYoteiSeq.
     * 
     * @return the nyushukkinYoteiSeq
     */
    public Long getNyushukkinYoteiSeq() {
        return nyushukkinYoteiSeq;
    }

    /** 
     * Sets the nyushukkinYoteiSeq.
     * 
     * @param nyushukkinYoteiSeq the nyushukkinYoteiSeq
     */
    public void setNyushukkinYoteiSeq(Long nyushukkinYoteiSeq) {
        this.nyushukkinYoteiSeq = nyushukkinYoteiSeq;
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
     * Returns the nyushukkinType.
     * 
     * @return the nyushukkinType
     */
    public String getNyushukkinType() {
        return nyushukkinType;
    }

    /** 
     * Sets the nyushukkinType.
     * 
     * @param nyushukkinType the nyushukkinType
     */
    public void setNyushukkinType(String nyushukkinType) {
        this.nyushukkinType = nyushukkinType;
    }

    /** 
     * Returns the nyushukkinKomokuId.
     * 
     * @return the nyushukkinKomokuId
     */
    public Long getNyushukkinKomokuId() {
        return nyushukkinKomokuId;
    }

    /** 
     * Sets the nyushukkinKomokuId.
     * 
     * @param nyushukkinKomokuId the nyushukkinKomokuId
     */
    public void setNyushukkinKomokuId(Long nyushukkinKomokuId) {
        this.nyushukkinKomokuId = nyushukkinKomokuId;
    }

    /** 
     * Returns the nyukinShiharaishaType.
     * 
     * @return the nyukinShiharaishaType
     */
    public String getNyukinShiharaishaType() {
        return nyukinShiharaishaType;
    }

    /** 
     * Sets the nyukinShiharaishaType.
     * 
     * @param nyukinShiharaishaType the nyukinShiharaishaType
     */
    public void setNyukinShiharaishaType(String nyukinShiharaishaType) {
        this.nyukinShiharaishaType = nyukinShiharaishaType;
    }

    /** 
     * Returns the nyukinCustomerId.
     * 
     * @return the nyukinCustomerId
     */
    public Long getNyukinCustomerId() {
        return nyukinCustomerId;
    }

    /** 
     * Sets the nyukinCustomerId.
     * 
     * @param nyukinCustomerId the nyukinCustomerId
     */
    public void setNyukinCustomerId(Long nyukinCustomerId) {
        this.nyukinCustomerId = nyukinCustomerId;
    }

    /** 
     * Returns the nyukinKanyoshaSeq.
     * 
     * @return the nyukinKanyoshaSeq
     */
    public Long getNyukinKanyoshaSeq() {
        return nyukinKanyoshaSeq;
    }

    /** 
     * Sets the nyukinKanyoshaSeq.
     * 
     * @param nyukinKanyoshaSeq the nyukinKanyoshaSeq
     */
    public void setNyukinKanyoshaSeq(Long nyukinKanyoshaSeq) {
        this.nyukinKanyoshaSeq = nyukinKanyoshaSeq;
    }

    /** 
     * Returns the nyukinShiharaisha.
     * 
     * @return the nyukinShiharaisha
     */
    public String getNyukinShiharaisha() {
        return nyukinShiharaisha;
    }

    /** 
     * Sets the nyukinShiharaisha.
     * 
     * @param nyukinShiharaisha the nyukinShiharaisha
     */
    public void setNyukinShiharaisha(String nyukinShiharaisha) {
        this.nyukinShiharaisha = nyukinShiharaisha;
    }

    /** 
     * Returns the nyukinSakiKozaSeq.
     * 
     * @return the nyukinSakiKozaSeq
     */
    public Long getNyukinSakiKozaSeq() {
        return nyukinSakiKozaSeq;
    }

    /** 
     * Sets the nyukinSakiKozaSeq.
     * 
     * @param nyukinSakiKozaSeq the nyukinSakiKozaSeq
     */
    public void setNyukinSakiKozaSeq(Long nyukinSakiKozaSeq) {
        this.nyukinSakiKozaSeq = nyukinSakiKozaSeq;
    }

    /** 
     * Returns the shukkinSakiKozaSeq.
     * 
     * @return the shukkinSakiKozaSeq
     */
    public Long getShukkinSakiKozaSeq() {
        return shukkinSakiKozaSeq;
    }

    /** 
     * Sets the shukkinSakiKozaSeq.
     * 
     * @param shukkinSakiKozaSeq the shukkinSakiKozaSeq
     */
    public void setShukkinSakiKozaSeq(Long shukkinSakiKozaSeq) {
        this.shukkinSakiKozaSeq = shukkinSakiKozaSeq;
    }

    /** 
     * Returns the shukkinShiharaiSakiType.
     * 
     * @return the shukkinShiharaiSakiType
     */
    public String getShukkinShiharaiSakiType() {
        return shukkinShiharaiSakiType;
    }

    /** 
     * Sets the shukkinShiharaiSakiType.
     * 
     * @param shukkinShiharaiSakiType the shukkinShiharaiSakiType
     */
    public void setShukkinShiharaiSakiType(String shukkinShiharaiSakiType) {
        this.shukkinShiharaiSakiType = shukkinShiharaiSakiType;
    }

    /** 
     * Returns the shukkinCustomerId.
     * 
     * @return the shukkinCustomerId
     */
    public Long getShukkinCustomerId() {
        return shukkinCustomerId;
    }

    /** 
     * Sets the shukkinCustomerId.
     * 
     * @param shukkinCustomerId the shukkinCustomerId
     */
    public void setShukkinCustomerId(Long shukkinCustomerId) {
        this.shukkinCustomerId = shukkinCustomerId;
    }

    /** 
     * Returns the shukkinKanyoshaSeq.
     * 
     * @return the shukkinKanyoshaSeq
     */
    public Long getShukkinKanyoshaSeq() {
        return shukkinKanyoshaSeq;
    }

    /** 
     * Sets the shukkinKanyoshaSeq.
     * 
     * @param shukkinKanyoshaSeq the shukkinKanyoshaSeq
     */
    public void setShukkinKanyoshaSeq(Long shukkinKanyoshaSeq) {
        this.shukkinKanyoshaSeq = shukkinKanyoshaSeq;
    }

    /** 
     * Returns the shukkinShiharaiSaki.
     * 
     * @return the shukkinShiharaiSaki
     */
    public String getShukkinShiharaiSaki() {
        return shukkinShiharaiSaki;
    }

    /** 
     * Sets the shukkinShiharaiSaki.
     * 
     * @param shukkinShiharaiSaki the shukkinShiharaiSaki
     */
    public void setShukkinShiharaiSaki(String shukkinShiharaiSaki) {
        this.shukkinShiharaiSaki = shukkinShiharaiSaki;
    }

    /** 
     * Returns the nyushukkinYoteiDate.
     * 
     * @return the nyushukkinYoteiDate
     */
    public LocalDate getNyushukkinYoteiDate() {
        return nyushukkinYoteiDate;
    }

    /** 
     * Sets the nyushukkinYoteiDate.
     * 
     * @param nyushukkinYoteiDate the nyushukkinYoteiDate
     */
    public void setNyushukkinYoteiDate(LocalDate nyushukkinYoteiDate) {
        this.nyushukkinYoteiDate = nyushukkinYoteiDate;
    }

    /** 
     * Returns the nyushukkinYoteiGaku.
     * 
     * @return the nyushukkinYoteiGaku
     */
    public BigDecimal getNyushukkinYoteiGaku() {
        return nyushukkinYoteiGaku;
    }

    /** 
     * Sets the nyushukkinYoteiGaku.
     * 
     * @param nyushukkinYoteiGaku the nyushukkinYoteiGaku
     */
    public void setNyushukkinYoteiGaku(BigDecimal nyushukkinYoteiGaku) {
        this.nyushukkinYoteiGaku = nyushukkinYoteiGaku;
    }

    /** 
     * Returns the nyushukkinDate.
     * 
     * @return the nyushukkinDate
     */
    public LocalDate getNyushukkinDate() {
        return nyushukkinDate;
    }

    /** 
     * Sets the nyushukkinDate.
     * 
     * @param nyushukkinDate the nyushukkinDate
     */
    public void setNyushukkinDate(LocalDate nyushukkinDate) {
        this.nyushukkinDate = nyushukkinDate;
    }

    /** 
     * Returns the nyushukkinGaku.
     * 
     * @return the nyushukkinGaku
     */
    public BigDecimal getNyushukkinGaku() {
        return nyushukkinGaku;
    }

    /** 
     * Sets the nyushukkinGaku.
     * 
     * @param nyushukkinGaku the nyushukkinGaku
     */
    public void setNyushukkinGaku(BigDecimal nyushukkinGaku) {
        this.nyushukkinGaku = nyushukkinGaku;
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