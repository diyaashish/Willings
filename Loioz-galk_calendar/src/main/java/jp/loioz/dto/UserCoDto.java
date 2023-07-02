package jp.loioz.dto;

import java.time.LocalDateTime;

import jp.loioz.common.utility.DateUtils;
import lombok.Data;

@Data
public class UserCoDto {

	public static final String EXPIRED_AT_STR_FORMAT = DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED;

	/** 企業連番 */
	Long companySeq;

	/** サブドメイン名 */
	String subDomain;

	/** 契約ステータス */
	String planStatus;

	/** 利用期限 */
	LocalDateTime expiredAt;

	/** 更新日時 */
	LocalDateTime updatedAt;

	/**
	 * 利用期限の日付文字列を取得
	 *
	 * @param time
	 */
	public String getServiceLimitDtStrLocalDate() {

		if (this.expiredAt == null) {
			return "";
		}

		return DateUtils.parseToString(this.expiredAt, EXPIRED_AT_STR_FORMAT);
	}

	/**
	 * 更新日の日付文字列を取得
	 *
	 * @param time
	 */
	public String getUpdatedAtStrLocalDate() {

		if (this.updatedAt == null) {
			return "";
		}

		return DateUtils.parseToString(this.updatedAt, EXPIRED_AT_STR_FORMAT);
	}
}
