package jp.loioz.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * アクティビティ履歴の表示用データ保持クラス
 *
 */
@Data
public class ActivityHistoryDto {

	/** 項目名 */
	private String itemName;

	/** タスクステータス（タスクステータス変更時のみ使用） */
	private String taskStatus;

	/** チェック項目名（チェック項目の操作時のみ使用） */
	private String checkItemName;

	/** チェック項目更新区分（チェック項目の操作時のみ使用） */
	private String CheckItemUpdateKbn;

	/** アクション */
	private String action;

	/** 登録日 */
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
	private LocalDateTime createdAt;

	/** 登録アカウントSEQ */
	private Long createdBy;

	/** 登録者名 */
	private String accountName;

}
