package jp.loioz.app.user.gyomuHistory.form.Common;

import java.time.LocalDateTime;

import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.LocalTimePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

@Data
public class CommonInputForm {

	/** 業務履歴SEQ */
	private Long gyomuHistorySeq;

	/** 遷移元種別 */
	private String transitionType;

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 対応日時(日付) */
	@Required
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String supportedDate;

	/** 対応日時(時間) */
	@Required
	@LocalTimePattern(format = DateUtils.TIME_FORMAT_HHMM)
	private String supportedTime;

	/** 重要フラグ */
	@Required
	private boolean isImportant;

	/** 固定フラグ */
	@Required
	private boolean isKotei;

	/** 件名 */
	@Required
	@MaxDigit(max = 30)
	private String subject;

	/** 内容 */
	@MaxDigit(max = 10000)
	private String mainText;

	/** 伝言送信済みフラグ */
	private boolean isSentDengon;

	/** 登録者 */
	private String createrName;

	/** 登録日 */
	private LocalDateTime createDate;

	/** 更新者 */
	private String updaterName;

	/** 更新日 */
	private LocalDateTime updateDate;

	/** バージョンNo */
	private Long versionNo;

	/** 対応日時(LocalTimeDate) */
	public LocalDateTime getSupportedAt() {
		// 時と分をLocalTimeに変換
		return LocalDateTime.of(DateUtils.parseToLocalDate(this.supportedDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED), DateUtils.parseToLocalTime(this.supportedTime, DateUtils.TIME_FORMAT_HHMM));
	}

}
