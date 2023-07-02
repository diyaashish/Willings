package jp.loioz.app.user.saibanManagement.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Builder;
import lombok.Data;

/**
 * 裁判期日一覧のDto
 */
@Builder
@Data
public class SaibanKijitsuDto {

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 裁判期日SEQ */
	private Long saibanLimitSeq;

	/** 予定SEQ */
	private Long scheduleSeq;

	/** 回数 */
	private Long limitDateCount;

	/** 件名 */
	private String subject;

	/** 期日  開始日時*/
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
	private LocalDateTime limitAt;

	/** 期日 終了日 */
	private LocalDateTime dateTo;
	
	/** 期日 終了時間 */
	private LocalTime timeTo;
	
	/** 会議室ID */
	private String roomId;

	/** 場所（もしくは会議室名） */
	private String placeName;

	/** 出廷種別 */
	private String shutteiType;

	/** 手続き内容 */
	private String content;

	/** 期日結果 */
	private String result;

	/** 現在日時より過去の期日かどうか */
	public boolean isKakoKijitsu() {
		LocalDateTime kijitsuDateTime = this.dateTo.toLocalDate().atTime(this.timeTo);
		if (kijitsuDateTime.isBefore(LocalDateTime.now())) {
			return true;
		}
		return false;
	}

}
