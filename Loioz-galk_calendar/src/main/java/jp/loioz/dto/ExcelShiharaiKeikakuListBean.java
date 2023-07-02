package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class ExcelShiharaiKeikakuListBean {

	/** 入出金予定SEQ */
	@Column(name = "nyushukkin_yotei_seq")
	private Long nyushukkinYoteiSeq;

	/** 入出金予定日 */
	@Column(name = "nyushukkin_yotei_date")
	private LocalDate nyushukkinYoteiDate;

	/** 入出金予定額 */
	@Column(name = "nyushukkin_yotei_Gaku")
	private BigDecimal nyushukkinYoteiGaku;

	/** 精算額 */
	@Column(name = "seisan_gaku")
	private BigDecimal seisanGaku;

}
