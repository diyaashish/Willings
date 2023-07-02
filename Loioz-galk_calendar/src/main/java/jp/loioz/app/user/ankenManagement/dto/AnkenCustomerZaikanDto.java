package jp.loioz.app.user.ankenManagement.dto;

import java.time.LocalDate;

import jp.loioz.common.utility.DateUtils;
import lombok.Builder;
import lombok.Data;

/**
 * 案件顧客-在監情報一覧
 */
@Builder
@Data
public class AnkenCustomerZaikanDto {

	/** 勾留SEQ */
	private Long koryuSeq;

	/** 案件ID */
	private Long ankenId;

	/** 顧客ID */
	private Long customerId;

	/** 勾留日 */
	private LocalDate koryuDate;

	/** 保釈日 */
	private LocalDate hoshakuDate;

	/** 捜査機関ID */
	private Long sosakikanId;

	/** 勾留場所名 */
	private String koryuPlaceName;

	/** 捜査機関電話番号 */
	private String sosakikanTelNo;

	/** 備考 */
	private String remarks;

	/** String:勾留日 */
	public String getKoryuDateStr() {
		return DateUtils.parseToString(this.koryuDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
	}

	/** String: 保釈日 */
	public String getHoshakuDateStr() {
		return DateUtils.parseToString(this.hoshakuDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
	}

}
