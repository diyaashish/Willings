package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jp.loioz.app.user.nyushukkinYotei.nyukin.enums.NyukinSearchRifineType;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.SeisanId;
import lombok.Data;

@Data
public class NyukinListDto {

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

	/** 分野名 */
	private BunyaDto bunya;

	/** Excel表示用担当弁護士 */
	private String dispTantoLawer;

	/** Excel表示用担当事務 */
	private String dispTantoJimu;

	/** 精算先 */
	private String shiharaishaName;

	/** 入金口座 */
	private String shiharaiSakiName;

	/** 入金予定日 */
	private LocalDate nyushukkinYoteiDate;

	/** 入金予定額 （計算用） */
	private BigDecimal nyushukkinYoteiGaku;

	/** 入金予定額 （表示用） */
	private String dispNyushukkinYoteiGaku;

	/** 残金 */
	private BigDecimal zankin;

	/** 残金（計算用） */
	private String dispZankin;

	/** 精算SEQ */
	private Long seisanSeq;

	/** 精算ID */
	private SeisanId seisanId;

	/** 状態 */
	private NyukinSearchRifineType nyukinSearchRifineType;

	/** 予定日がすぎているか判断 */
	private boolean isLimitOver;

	/** 処理済み */
	private boolean isCompleted;

}
