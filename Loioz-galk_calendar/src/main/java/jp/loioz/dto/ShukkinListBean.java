package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class ShukkinListBean {

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

	// 支払者口座情報
	/** 支払者銀行口座SEQ */
	@Column(name = "shiharaisha_koza_seq")
	private Long shiharaishaKozaSeq;

	/** 支払者テナントSEQ */
	@Column(name = "shiharaisha_tenant_seq")
	private Long shiharaishaTenantSeq;

	/** 支払者アカウントSEQ */
	@Column(name = "shiharaisha_account_seq")
	private Long shiharaishaAccountSeq;

	/** 支払者名 */
	@Column(name = "shiharaisha_name")
	private String shiharaishaName;

	// 支払先口座情報
	/** 支払先アカウントSEQ */
	@Column(name = "shiharai_saki_id")
	private Long shiharaiSakiId;

	/** 支払者名 顧客 */
	@Column(name = "shiharai_saki_customer_name")
	private String shiharaiSakiCustomerName;

	/** 支払者名 関与者 */
	@Column(name = "shiharai_saki_kanyosha_name")
	private String shiharaiSakiKanyoshaName;

	/** 出金予定日 */
	@Column(name = "nyushukkin_yotei_date")
	private LocalDate nyushukkinYoteiDate;

	/** 出金予定額 */
	@Column(name = "nyushukkin_yotei_gaku")
	private BigDecimal nyushukkinYoteiGaku;

	/** 出金日 */
	@Column(name = "nyushukkin_date")
	private LocalDate nyushukkinDate;

	/** 出金額 */
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

}
