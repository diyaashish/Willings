package jp.loioz.bean;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Data
@Entity
public class SaibanLimitBean {

	/** 裁判SEQ */
	@Column(name = "saiban_seq")
	private Long saibanSeq;

	/** 裁判期日SEQ */
	@Column(name = "saiban_Limit_seq")
	private Long saibanLimitSeq;

	/** 予定SEQ */
	@Column(name = "schedule_seq")
	private Long scheduleSeq;

	/** 回数 */
	@Column(name = "limit_date_count")
	private Long limitDateCount;

	/** 件名 */
	@Column(name = "subject")
	private String subject;

	/** 期日 */
	@Column(name = "limit_at")
	private LocalDateTime limitAt;

	/** 期日 終了日 */
	@Column(name = "date_to")
	private LocalDateTime dataTo;

	/** 期日 終了時間 */
	@Column(name = "time_to")
	private LocalTime timeTo;
	
	/** 場所 */
	@Column(name = "place")
	private String place;

	/** 施設ID */
	@Column(name = "room_id")
	private String roomId;

	/** 施設名 */
	@Column(name = "room_name")
	private String roomName;

	/** 出廷種別 */
	@Column(name = "shuttei_type")
	private String shutteiType;

	/** 手続き内容 */
	@Column(name = "content")
	private String content;

	/** 期日結果 */
	@Column(name = "result")
	private String result;

	/** 登録日時 */
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	/** 登録者 */
	@Column(name = "created_by")
	private Long createdBy;

	/** 更新日時 */
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	/** 更新者 */
	@Column(name = "updated_by")
	private Long updatedBy;

	/** バージョンNo */
	@Column(name = "version_no")
	private Long versionNo;

}
