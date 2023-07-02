package jp.loioz.dto;

import java.math.BigDecimal;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.SeisanId;
import lombok.Data;

@Data
public class SeisanKirokuDto {

	// ********************************
	// DBと対となる情報
	// ********************************
	// t_seisan_kiroku
	/** 精算SEQ */
	private Long seisanSeq;

	/** 精算ID */
	private Long seisanId;

	/** 案件ID */
	private AnkenId ankenId;

	/** 精算額 */
	private BigDecimal seisanGaku;

	/** 摘要 */
	private String tekiyo;

	/** 精算ステータス */
	private String seisanStatus;

	/** 登録日 */
	private String createdAt;

	// t_anken
	/** 案件名 */
	private String ankenName;

	/** 分野 */
	private String bunya;

	// t_person
	/** 顧客名 */
	private String customerName;

	/** 顧客ID */
	private CustomerId customerId;

	// ********************************
	// 画面表示用
	// ********************************
	/** 回収額 */
	private BigDecimal kaishuGaku;

	/** 回収額(表示用) */
	private String displayKaishuGaku;

	/** 精算額(表示用) */
	private String displaySeisanGaku;

	/** 精算ID(表示用) */
	private SeisanId displaySeisanId;

	/** 精算ステータス */
	private String dispSeisanStatus;

	/** 区分 */
	private String kubun;

	/** 請求書／精算書 */
	private String dispSeisanSeikyu;
}