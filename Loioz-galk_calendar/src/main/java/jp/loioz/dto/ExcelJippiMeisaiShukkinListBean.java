package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class ExcelJippiMeisaiShukkinListBean {

	// 会計記録テーブル
	/** 発生日 */
	@Column(name = "hassei_date")
	private LocalDate hasseiDate;

	/** 出金額 */
	@Column(name = "shukkin_gaku")
	private BigDecimal shukkinGaku;

	/** 出金額 */
	@Column(name = "tax_gaku")
	private BigDecimal taxGaku;

	/** 消費税率 */
	@Column(name = "tax_rate")
	private String taxRate;

	/** 摘要 */
	@Column(name = "tekiyo")
	private String tekiyo;

	// 入出金項目テーブル
	/** 項目名 */
	@Column(name = "komoku_name")
	private String komokuName;

	/** 課税フラグ */
	@Column(name = "tax_flg")
	private String taxFlg;

}
