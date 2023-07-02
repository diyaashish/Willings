package jp.loioz.bean;

import java.time.LocalDateTime;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 案件ダッシュボード：タスク情報取得用オブジェクト
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenDashTaskBean {

	/** タスクSEQ */
	private Long taskSeq;

	/** 案件ID */
	private Long ankenId;

	/** 件名 */
	private String title;

	/** 内容 */
	private String content;

	/** ステータス */
	private String taskStatus;

	/** 終日フラグ */
	private String allDayFlg;

	/** 期限 */
	private LocalDateTime limitDtTo;

	/** アカウントSEQ */
	private Long accountSeq;

	/** タスク委任フラグ */
	private String entrustFlg;

	/** コメント件数 */
	private Long commentCount;

	/** チェックアイテム件数 */
	private Long checkItemCount;

	/** 終了したチェックアイテム件数 */
	private Long completeCheckItemCount;
}
