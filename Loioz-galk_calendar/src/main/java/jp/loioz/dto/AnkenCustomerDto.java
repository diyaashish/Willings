package jp.loioz.dto;

import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 案件-顧客EntityにpersonIdを追加したDto
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class AnkenCustomerDto {

	/** 案件ID */
	Long ankenId;

	/** 名簿ID */
	Long personId;

	/** 顧客ID */
	Long customerId;

	/** 初回面談日 */
	LocalDate shokaiMendanDate;

	/** 初回面談予定SEQ */
	Long shokaiMendanScheduleSeq;

	/** 受任日 */
	LocalDate juninDate;

	/** 事件処理完了日 */
	LocalDate jikenKanryoDate;

	/** 精算完了日 */
	LocalDate kanryoDate;

	/** 完了フラグ */
	String kanryoFlg;

	/** 案件ステータス */
	String ankenStatus;
}
