package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class NyukinListBean {

	/** 入出金予定SEQ */
	@Column(name = "nyushukkin_yotei_seq")
	private Long nyushukkinYoteiSeq;

	/** 顧客ID */
	@Column(name = "customer_id")
	private Long customerId;

	/** 顧客名 */
	@Column(name = "customer_name")
	private String customerName;

	/** 案件ID */
	@Column(name = "anken_id")
	private Long ankenId;

	/** 案件名 */
	@Column(name = "anken_name")
	private String ankenName;

	/** 分野ID */
	@Column(name = "bunya_id")
	private Long bunyaId;

	/** 案件ステータス */
	@Column(name = "anken_status")
	private String ankenStatus;

	// 支払先口座情報
	/** 支払者アカウントSEQ */
	@Column(name = "shiharaisha_id")
	private Long shiharaishaId;

	/** 支払者名 顧客 */
	@Column(name = "shiharai_customer_name")
	private String shiharaiCustomerName;

	/** 支払者名 関与者 */
	@Column(name = "shiharai_kanyosha_name")
	private String shiharaiKanyoshaName;

	// 支払先情報
	/** 支払先銀行口座SEQ */
	@Column(name = "nyukin_saki_koza_seq")
	private Long nyukinSakiKozaSeq;

	/** 支払先テナントSEQ */
	@Column(name = "shiharai_saki_tenant_seq")
	private Long shiharaiSakiTenantSeq;

	/** 支払先アカウントSEQ */
	@Column(name = "shiharai_saki_account_seq")
	private Long shiharaiSakiAccountSeq;

	/** 支払先名 */
	@Column(name = "shiharai_saki_name")
	private String shiharaiSakiName;

	/** 入金予定日 */
	@Column(name = "nyushukkin_yotei_date")
	private LocalDate nyushukkinYoteiDate;

	/** 入金予定額 */
	@Column(name = "nyushukkin_yotei_gaku")
	private BigDecimal nyushukkinYoteiGaku;

	/** 入金日 */
	@Column(name = "nyushukkin_date")
	private LocalDate nyushukkinDate;

	/** 入金額 */
	@Column(name = "nyushukkin_gaku")
	private BigDecimal nyushukkinGaku;

	/** 精算SEQ */
	@Column(name = "seisan_seq")
	private Long seisanSeq;

	/** 精算ID */
	@Column(name = "seisan_id")
	private Long seisanId;

	/** 精算額 */
	@Column(name = "seisan_gaku")
	private BigDecimal seisanGaku;

	/** 入金済総額 */
	@Column(name = "nyukin_total")
	private BigDecimal nyukinTotal;

}
