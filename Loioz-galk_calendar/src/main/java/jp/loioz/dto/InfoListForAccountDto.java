package jp.loioz.dto;

import java.time.LocalDateTime;

import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class InfoListForAccountDto {

	/** お知らせ連番 */
	private Long infoMgtSeq;

	/** お知らせ区分 */
	private String infoType;

	/** お知らせ区分(文字列) */
 	private String infoTypeStr;

	/** お知らせタイトル */
	private String infoSubject;

	/** お知らせ本文 */
	private String infoBody;

	/** 更新日 */
	private LocalDateTime updatedAt;

	/** 更新日(文字列) */
	private String updatedAtStr;

	/** アカウントSEQ */
	private Long accountSeq;

}
