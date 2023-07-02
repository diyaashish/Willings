package jp.loioz.dto;

import java.time.LocalDateTime;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class InfoEditDto {

	/** お知らせ連番 */
	private Long infoSeq;

	/** 表示フラグ */
	@Required
	@MaxDigit(max=1, item="表示フラグ")
	private String infoOpenFlg;

	/** お知らせ区分 */
	@Required
	@MaxDigit(max=2, item="お知らせ区分")
	private String infoType;

	/** お知らせタイトル */
	@Required
	@MaxDigit(max = 30, item="お知らせタイトル")
	private String infoTitle;

	/** お知らせ本文 */
	@Required
	private String infoBody;

	/** 掲載開始日時 */
	private LocalDateTime infoStartAt;

	/** 掲載開始日時（日） */
	@LocalDatePattern
	private String infoStartDt;
	
	/** 掲載開始日時（時） */
	private String startHour;

	/** 掲載開始日時（分） */
	private String startMin;

	/** 掲載終了日時 */
	private LocalDateTime infoEndAt;

	/** 掲載終了日時 (日) */
	@LocalDatePattern
	private String infoEndDt;

	/** 掲載終了日時（時） */
	private String endHour;

	/** 掲載終了日時（分） */
	private String endMin;

	/** 作成日 */
	private String createdAt;

	/** 作成アカウントSEQ */
	private Long createdBy;

	/** 更新日時 */
	private String updatedAt;

	/** 更新アカウントSEQ */
	private Long updatedBy;

	/** 削除日 */
	private String deletedAt;

	/** 削除アカウントSEQ */
	private Long deletedBy;

	/** バージョンNo */
	private Long versionNo;

}
