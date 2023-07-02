package jp.loioz.app.user.taskManagement.dto;

import java.time.LocalDate;

import jp.loioz.common.utility.DateUtils;
import jp.loioz.domain.value.AnkenId;
import lombok.Data;

/**
 * 案件タスク追加モーダルDto
 */
@Data
public class TaskAnkenAddModalDto {

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野名 */
	private String bunyaName;

	/** 担当弁護士 */
	private String tantoLaywerName;

	/** 担当事務 */
	private String tantoJimuName;

	/** 名簿ID */
	private Long personId;

	/** 顧客名 */
	private String customerName;

	/** 顧客数 */
	private Long numberOfCustomer;

	/** 相手方名 */
	private String aitegataName;

	/** 相手方数 */
	private Long numberOfAitegata;

	/** 登録日 */
	private LocalDate createdAt;

	/**
	 * 登録日
	 * @return
	 */
	public String getCreatedAtFormat() {
		return DateUtils.parseToString(this.createdAt, DateUtils.DATE_FORMAT_HYPHEN_DELIMITED);
	}
}
