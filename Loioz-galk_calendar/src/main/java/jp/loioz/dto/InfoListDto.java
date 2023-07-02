package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class InfoListDto {

	/** お知らせ連番 */
	private Long infoSeq;

	/** 表示フラグ */
	private String infoOpenFlg;

	/** お知らせ区分 */
	private String infoType;

	/** お知らせタイトル */
	private String infoTitle;

	/** お知らせ本文 */
	private String infoBody;

	/** 掲載開始日時 */
	private String infoStartAt;

	/** 掲載終了日時 */
	private String infoEndAt;

	/** 作成日時 */
	private String createdAt;

	/** 更新日時 */
	private String updatedAt;

	/** バージョンNo */
	private Long versionNo;

}
