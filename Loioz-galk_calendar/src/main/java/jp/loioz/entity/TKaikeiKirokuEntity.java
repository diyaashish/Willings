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
 * 会計記録
 */
@Entity(listener = TKaikeiKirokuEntityListener.class)
@Table(name = "t_kaikei_kiroku")
public class TKaikeiKirokuEntity extends DefaultEntity {

	/** 会計記録SEQ */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "kaikei_kiroku_seq")
	Long kaikeiKirokuSeq;

	/** 入出金予定SEQ */
	@Column(name = "nyushukkin_yotei_seq")
	Long nyushukkinYoteiSeq;

	/** 顧客ID */
	@Column(name = "customer_id")
	Long customerId;

	/** 案件ID */
	@Column(name = "anken_id")
	Long ankenId;

	/** 発生日 */
	@Column(name = "hassei_date")
	LocalDate hasseiDate;

	/** 報酬項目ID */
	@Column(name = "hoshu_komoku_id")
	String hoshuKomokuId;

	/** 入出金項目ID */
	@Column(name = "nyushukkin_komoku_id")
	Long nyushukkinKomokuId;

	/** 入出金タイプ */
	@Column(name = "nyushukkin_type")
	String nyushukkinType;

	/** 入金額 */
	@Column(name = "nyukin_gaku")
	BigDecimal nyukinGaku;

	/** 出金額 */
	@Column(name = "shukkin_gaku")
	BigDecimal shukkinGaku;

	/** 消費税金額 */
	@Column(name = "tax_gaku")
	BigDecimal taxGaku;

	/** タイムチャージ単価 */
	@Column(name = "time_charge_tanka")
	BigDecimal timeChargeTanka;

	/** タイムチャージ時間指定方法 */
	@Column(name = "time_charge_time_shitei")
	String timeChargeTimeShitei;

	/** タイムチャージ開始日時 */
	@Column(name = "time_charge_start_time")
	LocalDateTime timeChargeStartTime;

	/** タイムチャージ終了日時 */
	@Column(name = "time_charge_end_time")
	LocalDateTime timeChargeEndTime;

	/** タイムチャージ時間 */
	@Column(name = "time_charge_time")
	Integer timeChargeTime;

	/** 消費税率 */
	@Column(name = "tax_rate")
	String taxRate;

	/** 課税フラグ */
	@Column(name = "tax_flg")
	String taxFlg;

	/** 源泉徴収フラグ */
	@Column(name = "gensenchoshu_flg")
	String gensenchoshuFlg;

	/** 源泉徴収額 */
	@Column(name = "gensenchoshu_gaku")
	BigDecimal gensenchoshuGaku;

	/** 預かり金プール種別 */
	@Column(name = "pool_type")
	String poolType;

	/** 預り金プール元案件ID */
	@Column(name = "pool_moto_anken_id")
	Long poolMotoAnkenId;

	/** 預り金プール先案件ID */
	@Column(name = "pool_saki_anken_id")
	Long poolSakiAnkenId;

	/** 回収不能フラグ */
	@Column(name = "uncollectable_flg")
	String uncollectableFlg;

	/** 摘要 */
	@Column(name = "tekiyo")
	String tekiyo;

	/** 精算処理日 */
	@Column(name = "seisan_date")
	LocalDate seisanDate;

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
	 * Returns the kaikeiKirokuSeq.
	 * 
	 * @return the kaikeiKirokuSeq
	 */
	public Long getKaikeiKirokuSeq() {
		return kaikeiKirokuSeq;
	}

	/**
	 * Sets the kaikeiKirokuSeq.
	 * 
	 * @param kaikeiKirokuSeq the kaikeiKirokuSeq
	 */
	public void setKaikeiKirokuSeq(Long kaikeiKirokuSeq) {
		this.kaikeiKirokuSeq = kaikeiKirokuSeq;
	}

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
	 * Returns the hasseiDate.
	 * 
	 * @return the hasseiDate
	 */
	public LocalDate getHasseiDate() {
		return hasseiDate;
	}

	/**
	 * Sets the hasseiDate.
	 * 
	 * @param hasseiDate the hasseiDate
	 */
	public void setHasseiDate(LocalDate hasseiDate) {
		this.hasseiDate = hasseiDate;
	}

	/**
	 * Returns the hoshuKomokuId.
	 * 
	 * @return the hoshuKomokuId
	 */
	public String getHoshuKomokuId() {
		return hoshuKomokuId;
	}

	/**
	 * Sets the hoshuKomokuId.
	 * 
	 * @param hoshuKomokuId the hoshuKomokuId
	 */
	public void setHoshuKomokuId(String hoshuKomokuId) {
		this.hoshuKomokuId = hoshuKomokuId;
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
	 * Returns the nyukinGaku.
	 * 
	 * @return the nyukinGaku
	 */
	public BigDecimal getNyukinGaku() {
		return nyukinGaku;
	}

	/**
	 * Sets the nyukinGaku.
	 * 
	 * @param nyukinGaku the nyukinGaku
	 */
	public void setNyukinGaku(BigDecimal nyukinGaku) {
		this.nyukinGaku = nyukinGaku;
	}

	/**
	 * Returns the shukkinGaku.
	 * 
	 * @return the shukkinGaku
	 */
	public BigDecimal getShukkinGaku() {
		return shukkinGaku;
	}

	/**
	 * Sets the shukkinGaku.
	 * 
	 * @param shukkinGaku the shukkinGaku
	 */
	public void setShukkinGaku(BigDecimal shukkinGaku) {
		this.shukkinGaku = shukkinGaku;
	}

	/**
	 * Returns the taxGaku.
	 * 
	 * @return the taxGaku
	 */
	public BigDecimal getTaxGaku() {
		return taxGaku;
	}

	/**
	 * Sets the taxGaku.
	 * 
	 * @param taxGaku the taxGaku
	 */
	public void setTaxGaku(BigDecimal taxGaku) {
		this.taxGaku = taxGaku;
	}

	/**
	 * Returns the timeChargeTanka.
	 * 
	 * @return the timeChargeTanka
	 */
	public BigDecimal getTimeChargeTanka() {
		return timeChargeTanka;
	}

	/**
	 * Sets the timeChargeTanka.
	 * 
	 * @param timeChargeTanka the timeChargeTanka
	 */
	public void setTimeChargeTanka(BigDecimal timeChargeTanka) {
		this.timeChargeTanka = timeChargeTanka;
	}

	/**
	 * Returns the timeChargeTimeShitei.
	 * 
	 * @return the timeChargeTimeShitei
	 */
	public String getTimeChargeTimeShitei() {
		return timeChargeTimeShitei;
	}

	/**
	 * Sets the timeChargeTimeShitei.
	 * 
	 * @param timeChargeTimeShitei the timeChargeTimeShitei
	 */
	public void setTimeChargeTimeShitei(String timeChargeTimeShitei) {
		this.timeChargeTimeShitei = timeChargeTimeShitei;
	}

	/**
	 * Returns the timeChargeStartTime.
	 * 
	 * @return the timeChargeStartTime
	 */
	public LocalDateTime getTimeChargeStartTime() {
		return timeChargeStartTime;
	}

	/**
	 * Sets the timeChargeStartTime.
	 * 
	 * @param timeChargeStartTime the timeChargeStartTime
	 */
	public void setTimeChargeStartTime(LocalDateTime timeChargeStartTime) {
		this.timeChargeStartTime = timeChargeStartTime;
	}

	/**
	 * Returns the timeChargeEndTime.
	 * 
	 * @return the timeChargeEndTime
	 */
	public LocalDateTime getTimeChargeEndTime() {
		return timeChargeEndTime;
	}

	/**
	 * Sets the timeChargeEndTime.
	 * 
	 * @param timeChargeEndTime the timeChargeEndTime
	 */
	public void setTimeChargeEndTime(LocalDateTime timeChargeEndTime) {
		this.timeChargeEndTime = timeChargeEndTime;
	}

	/**
	 * Returns the timeChargeTime.
	 * 
	 * @return the timeChargeTime
	 */
	public Integer getTimeChargeTime() {
		return timeChargeTime;
	}

	/**
	 * Sets the timeChargeTime.
	 * 
	 * @param timeChargeTime the timeChargeTime
	 */
	public void setTimeChargeTime(Integer timeChargeTime) {
		this.timeChargeTime = timeChargeTime;
	}

	/**
	 * Returns the taxRate.
	 * 
	 * @return the taxRate
	 */
	public String getTaxRate() {
		return taxRate;
	}

	/**
	 * Sets the taxRate.
	 * 
	 * @param taxRate the taxRate
	 */
	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}

	/**
	 * Returns the taxFlg.
	 * 
	 * @return the taxFlg
	 */
	public String getTaxFlg() {
		return taxFlg;
	}

	/**
	 * Sets the taxFlg.
	 * 
	 * @param taxFlg the taxFlg
	 */
	public void setTaxFlg(String taxFlg) {
		this.taxFlg = taxFlg;
	}

	/**
	 * Returns the gensenchoshuFlg.
	 * 
	 * @return the gensenchoshuFlg
	 */
	public String getGensenchoshuFlg() {
		return gensenchoshuFlg;
	}

	/**
	 * Sets the gensenchoshuFlg.
	 * 
	 * @param gensenchoshuFlg the gensenchoshuFlg
	 */
	public void setGensenchoshuFlg(String gensenchoshuFlg) {
		this.gensenchoshuFlg = gensenchoshuFlg;
	}

	/**
	 * Returns the gensenchoshuGaku.
	 * 
	 * @return the gensenchoshuGaku
	 */
	public BigDecimal getGensenchoshuGaku() {
		return gensenchoshuGaku;
	}

	/**
	 * Sets the gensenchoshuGaku.
	 * 
	 * @param gensenchoshuGaku the gensenchoshuGaku
	 */
	public void setGensenchoshuGaku(BigDecimal gensenchoshuGaku) {
		this.gensenchoshuGaku = gensenchoshuGaku;
	}

	/**
	 * Returns the poolType.
	 * 
	 * @return the poolType
	 */
	public String getPoolType() {
		return poolType;
	}

	/**
	 * Sets the poolType.
	 * 
	 * @param poolType the poolType
	 */
	public void setPoolType(String poolType) {
		this.poolType = poolType;
	}

	/**
	 * Returns the poolMotoAnkenId.
	 * 
	 * @return the poolMotoAnkenId
	 */
	public Long getPoolMotoAnkenId() {
		return poolMotoAnkenId;
	}

	/**
	 * Sets the poolMotoAnkenId.
	 * 
	 * @param poolMotoAnkenId the poolMotoAnkenId
	 */
	public void setPoolMotoAnkenId(Long poolMotoAnkenId) {
		this.poolMotoAnkenId = poolMotoAnkenId;
	}

	/**
	 * Returns the poolSakiAnkenId.
	 * 
	 * @return the poolSakiAnkenId
	 */
	public Long getPoolSakiAnkenId() {
		return poolSakiAnkenId;
	}

	/**
	 * Sets the poolSakiAnkenId.
	 * 
	 * @param poolSakiAnkenId the poolSakiAnkenId
	 */
	public void setPoolSakiAnkenId(Long poolSakiAnkenId) {
		this.poolSakiAnkenId = poolSakiAnkenId;
	}

	/**
	 * Returns the uncollectableFlg.
	 * 
	 * @return the uncollectableFlg
	 */
	public String getUncollectableFlg() {
		return uncollectableFlg;
	}

	/**
	 * Sets the uncollectableFlg.
	 * 
	 * @param uncollectableFlg the uncollectableFlg
	 */
	public void setUncollectableFlg(String uncollectableFlg) {
		this.uncollectableFlg = uncollectableFlg;
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
	 * Returns the seisanDate.
	 * 
	 * @return the seisanDate
	 */
	public LocalDate getSeisanDate() {
		return seisanDate;
	}

	/**
	 * Sets the seisanDate.
	 * 
	 * @param seisanDate the seisanDate
	 */
	public void setSeisanDate(LocalDate seisanDate) {
		this.seisanDate = seisanDate;
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