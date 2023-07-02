package jp.loioz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ExcelTimeChargeListDto {

	/** 日付 */
	private LocalDate hasseiDate;
	
	/** タイムチャージ開始日時 */
	private LocalDateTime timeChargeStartDateTime;
	
	/** タイムチャージ開始時間 */
	private String timeChargeStartTime;

	/** タイムチャージ終了時間 */
	private String timeChargeEndTime;

	/** 活動 */
	private String katsudo;

	/** 時間(分) */
	private int time;

	/** タイムチャージ単価 */
	private BigDecimal timeChargeTanka;

	/** タイムチャージ単価(表示用) */
	private String dispTimeChargeTanka;

	/** 出金額※小計 */
	private BigDecimal shukkinGaku;

	/** 出金額※小計(表示用) */
	private String dispShukkinGaku;

}
