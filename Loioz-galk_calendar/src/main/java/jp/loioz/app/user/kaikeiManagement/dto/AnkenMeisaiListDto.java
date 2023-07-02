package jp.loioz.app.user.kaikeiManagement.dto;

import java.math.BigDecimal;

import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 * 案件明細一覧用Dto
 */
@Data
public class AnkenMeisaiListDto {

	/** 顧客ID */
	private Long customerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID */
	private Long ankenId;

	/** 案件名 */
	private String ankenName;

	/** 入金計 */
	private BigDecimal nyukinTotal;

	/** 表示用入金計 */
	private String dispNyukinTotal;

	/** 報酬計 */
	private BigDecimal hoshuTotal;

	/** 表示用報酬計 */
	private String dispHoshuTotal;

	/** 出金計 */
	private BigDecimal shukkinTotal;

	/** 表示用出金計 */
	private String dispShukkinTotal;

	/** 合計 */
	private BigDecimal total;

	/** 表示用合計 */
	private String dispTotal;

	/** 案件ステータス */
	private AnkenStatus ankenStatus;

	/** 精算完了日 */
	private String seisanKanryoDate;

	/** 表示用顧客ID */
	public String getCustomerIdLabel() {
		return CustomerId.of(this.customerId).toString();
	}

	/** 表示用案件ID */
	public String getAnkenIdLabel() {
		return AnkenId.of(this.ankenId).toString();
	}

	/** 案件が完了かどうか */
	public boolean isComp() {
		return AnkenStatus.isComp(DefaultEnum.getCd(this.ankenStatus));
	}

	/** 精算完了かどうか */
	public boolean isSeisanComp() {
		return AnkenStatus.isSeisanComp(DefaultEnum.getCd(this.ankenStatus));
	}

}
