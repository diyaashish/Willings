package jp.loioz.app.user.schedule.form;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.SaibanId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 予定
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleDetail extends ScheduleInputForm implements Cloneable {

	@Override
	public ScheduleDetail clone() throws CloneNotSupportedException {
		ScheduleDetail cloned = (ScheduleDetail) super.clone();
		return cloned;
	}

	/** 予定の一意のkey */
	private String scheduleSankasyaPk;

	/** 週内で表示期間（終日のみ） */
	private int weekViewCnt;

	/** 週表示：表示順（終日のみ） */
	private int weekViewOrder;

	/** 週内で表示（終日のみ） */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate weekViewDate;

	/** 場所(表示用) */
	private String placeDisp;

	/** 顧客ID(表示用) */
	@JsonSerialize(using = ToStringSerializer.class)
	private CustomerId customerIdDisp;

	/** 顧客名 */
	private String customerName;

	/** 案件ID(表示用) */
	@JsonSerialize(using = ToStringSerializer.class)
	private AnkenId ankenIdDisp;

	/** 案件名 */
	private String ankenName;

	/** 分野名 */
	private String bunyaName;
	
	/** 刑事分野かどうか */
	private boolean isKeiji = false;

	/** 裁判ID */
	@JsonSerialize(using = ToStringSerializer.class)
	private SaibanId saibanId;

	/** 裁判ID枝番 */
	private Long saibanBranchNumber;

	/** 事件番号 */
	@JsonSerialize(using = ToStringSerializer.class)
	private CaseNumber caseNumber;

	/** 事件名 */
	private String jikenName;

	/** 当事者タイトル（表示用） */
	private String tojishaNameTitleLabel;
	
	/** 当事者名（表示用） */
	private String tojishaNameLabel;

	/** 相手方タイトル（表示用） */
	private String aitegataNameTitleLabel;
	
	/** 相手方名（表示） */
	private String aitegataNameLabel;

	/** 登録者名 */
	private String createUserName;

	/** 登録日 */
	private LocalDateTime createdAt;

	/** 更新者名 */
	private String updateUserName;

	/** 更新日 */
	private LocalDateTime updatedAt;

	/** 公開対象外 */
	private boolean forbidden = false;

	/** 祝日かどうか */
	private boolean isHoliday = false;

	/** メモ表示用（URLはaタグリンクに変換して保持する） */
	private String viewMemo;
}