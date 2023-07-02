package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.Valid;

import jp.loioz.app.user.nyushukkinYotei.shukkin.enums.ShukkinSearchRifineType;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.SeisanId;
import lombok.Data;

@Data
@Valid
public class ShukkinListDto {

	/** 入出金予定SEQ */
	private Long nyushukkinYoteiSeq;

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野 */
	private BunyaDto bunya;

	/** Excel表示用担当弁護士 */
	private String dispTantoLawer;

	/** Excel表示用担当事務 */
	private String dispTantoJimu;

	/** 出金口座 */
	private String shiharaishaName;

	/** 支払先 */
	private String shiharaiSakiName;

	/** 出金予定日 */
	private LocalDate nyushukkinYoteiDate;

	/** 出金予定額（計算用） */
	private BigDecimal nyushukkinYoteiGaku;

	/** 出金予定額（表示用） */
	private String dispNyushukkinYoteiGaku;

	/** 精算額 */
	private BigDecimal seisanGaku;

	/** 精算額（計算用） */
	private String dispSeisanGaku;

	/** 精算SEQ */
	private Long seisanSeq;

	/** 精算ID */
	private SeisanId seisanId;

	/** 状態 */
	private ShukkinSearchRifineType shukkinSearchRifineType;

	/** 予定日がすぎているか判断 */
	private boolean isLimitOver;

	/** 処理済み */
	private boolean isCompleted;
}
