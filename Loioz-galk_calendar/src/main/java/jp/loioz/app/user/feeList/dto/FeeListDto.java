package jp.loioz.app.user.feeList.dto;

import java.time.LocalDateTime;

import jp.loioz.common.utility.DateUtils;
import lombok.Data;

/**
 * 報酬一覧表示用Dto
 */
@Data
public class FeeListDto {

	/** 名簿ID */
	private Long personId;

	/** 名前 */
	private String name;

	/** 案件ID */
	private Long ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野ID */
	private String bunyaName;

	/** 顧客ステータスCD */
	private String ankenStatus;

	/** 顧客ステータス */
	private String ankenStatusName;

	/** 担当弁護士 */
	private String tantoLaywerName;

	/** 担当事務 */
	private String tantoJimuName;

	/** 報酬合計(税込) */
	private String feeTotalAmount;

	/** 未請求（合計） */
	private String feeUnclaimedTotalAmount;

	/** 最終更新日時 */
	private LocalDateTime lastEditAt;

	/**
	 * -区切りに編集した最終更新日時を取得します。
	 * 
	 * @return
	 */
	public String getLastEditAtFormat() {
		return DateUtils.parseToString(this.lastEditAt, DateUtils.HYPHEN_YYYY_MM_DD_HHMM);
	}

}
