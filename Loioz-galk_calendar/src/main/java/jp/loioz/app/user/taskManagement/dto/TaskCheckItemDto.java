package jp.loioz.app.user.taskManagement.dto;

import java.time.LocalDateTime;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * タスク-チェックリスト項目
 */
@Data
public class TaskCheckItemDto {

	/** タスク-チェックリストSEQ */
	Long taskCheckItemSeq;
	
	/** タスクSEQ */
	Long taskSeq;
	
	/** チェックリスト項目名 */
	@Required
	@MaxDigit(max = 100)
	String itemName;
	
	/** 完了フラグ */
	Boolean completeFlg;
	
	/** チェックリスト表示順 */
	Long dispOrder;
	
	/** 登録日時 */
	LocalDateTime createdAt;
	
	/** 登録アカウントSEQ */
	Long createdBy;
	
	/** 更新日時 */
	LocalDateTime updatedAt;
	
	/** 更新アカウントSEQ */
	Long updatedBy;
	
	/** バージョンNo */
	Long versionNo;

}