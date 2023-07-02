package jp.loioz.app.user.ankenDashboard.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.dto.CustomerDto;
import lombok.Data;

/**
 * 案件ダッシュボード：タスク一覧用オブジェクト
 */
@Data
public class AnkenDashboardGyomuHistoryListDto {

	/** 業務履歴SEQ */
	private Long gyomuHistorySeq;

	/** 遷移元種別 */
	private TransitionType transitionType;

	/** 件名 */
	private String subject;

	/** 本文 */
	private String mainText;

	/** 登録者名 */
	private String createrName;

	/** 重要フラグ */
	private String importantFlg;

	/** 伝言送信済フラグ */
	private boolean isSentDengon;

	/** 固定 */
	private String koteiFlg;

	/** 対応日時 */
	private String supportedAt;

	/** 作成日時 */
	private String createdAt;

	/** 更新日時 */
	private String updatedAt;

	/** 案件ID */
	private Long ankenId;

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 裁判SEQに紐づく枝番 */
	private String saibanBranchNo;

	/** 事件名 */
	private String jikenName;

	/** 事件No */
	private CaseNumber caseNumber;

	/** 顧客情報 */
	private List<CustomerDto> customerList = Collections.emptyList();

	/** 表示用の対応日時 */
	public String getDispSupportedAt() {
		if (StringUtils.isEmpty(this.supportedAt)) {
			return "";
		}
		LocalDateTime localDateTime = LocalDateTime.parse(this.supportedAt, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
		return DateUtils.parseToString(localDateTime, DateUtils.DATE_M + "月" + DateUtils.DATE_D + "日") + " (" + localDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE) + ") "
				+ DateUtils.parseToString(localDateTime, DateUtils.TIME_FORMAT_HMM);
	}

	/** 表示用の作成日時 */
	public String getDispCreatedAt() {
		LocalDateTime localDateTime = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
		return DateUtils.parseToString(localDateTime, DateUtils.DATE_M + "月" + DateUtils.DATE_D + "日") + " (" + localDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE) + ") "
				+ DateUtils.parseToString(localDateTime, DateUtils.TIME_FORMAT_HMM);
	}

	/** 表示用の更新日時 */
	public String getDispUpdatedAt() {
		LocalDateTime localDateTime = LocalDateTime.parse(this.updatedAt, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
		return DateUtils.parseToString(localDateTime, DateUtils.DATE_M + "月" + DateUtils.DATE_D + "日") + " (" + localDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE) + ") "
				+ DateUtils.parseToString(localDateTime, DateUtils.TIME_FORMAT_HMM);
	}

}
