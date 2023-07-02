package jp.loioz.bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class FeeListBean {

	/** レコード件数 */
	@Column(name = "count")
	private Integer count;

	/** 名簿ID */
	@Column(name = "person_id")
	private Long personId;

	/** 顧客姓 */
	@Column(name = "person_name_sei")
	private String personNameSei;

	/** 顧客名 */
	@Column(name = "person_name_mei")
	private String personNameMei;

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 分野ID */
	@Column(name = "bunya_id")
	private Long bunyaId;

	/** 顧客ステータス */
	@Column(name = "anken_status")
	private String ankenStatus;

	/** 担当弁護士 */
	@Column(name = "tanto_laywer_name")
	private String tantoLaywerName;

	/** 担当事務 */
	@Column(name = "tanto_jimu_name")
	private String tantoJimuName;

	/** 報酬合計（税込） */
	@Column(name = "fee_total_amount")
	private BigDecimal feeTotalAmount;

	/** 未請求 */
	@Column(name = "fee_unclaimed_total_amount")
	private BigDecimal feeUnclaimedTotalAmount;

	/** 最終更新日時 */
	@Column(name = "last_edit_at")
	private LocalDateTime lastEditAt;

}
